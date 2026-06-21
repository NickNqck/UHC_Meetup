package fr.nicknqck.managers;

import fr.nicknqck.enums.EChakras;
import fr.nicknqck.events.custom.GameEndEvent;
import fr.nicknqck.interfaces.IChakraV2;
import fr.nicknqck.roles.ns.chakratype.*;
import fr.nicknqck.utils.event.EventUtils;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.List;

public class ChakraManager implements Listener {

    @Getter
    private final List<IChakraV2> loadedChakra;

    public  ChakraManager() {
        EventUtils.registerEvents(this);
        this.loadedChakra = new ArrayList<>();
        this.loadedChakra.add(new FutonV2());
        this.loadedChakra.add(new SuitonV2());
        this.loadedChakra.add(new Katon());
        this.loadedChakra.add(new Raiton());
        this.loadedChakra.add(new Doton());
    }

    public IChakraV2 getChakra(@NonNull EChakras eChakras) {
        for (IChakraV2 iChakraV2 : loadedChakra) {
            if (iChakraV2.getChakraType().equals(eChakras)) {
                return iChakraV2;
            }
        }
        return null;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onEndGame(@NonNull final GameEndEvent event) {
        for (IChakraV2 iChakraV2 : loadedChakra) {
            iChakraV2.getMap().clear();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onCommand(@NonNull final PlayerCommandPreprocessEvent event) {
        String[] args = event.getMessage().split(" ");
        if (args[0].equalsIgnoreCase("/ns") && args.length == 2) {
            for (IChakraV2 iChakraV2 : loadedChakra) {
                if (iChakraV2.getArg0().equalsIgnoreCase(args[1])) {
                    iChakraV2.setActivateFor(event.getPlayer().getUniqueId(), !iChakraV2.isActivate(event.getPlayer().getUniqueId()));
                    if (iChakraV2.isActivate(event.getPlayer().getUniqueId())) {
                        event.getPlayer().sendMessage("§7Vous avez§a activer§7 votre "+iChakraV2.getChakraType().getShowedName());
                    } else {
                        event.getPlayer().sendMessage("§7Vous avez§c désactiver§7 votre "+iChakraV2.getChakraType().getShowedName());
                    }
                    break;
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    private void onTabComplete(@NonNull final PlayerChatTabCompleteEvent event) {
        final String[] args = event.getChatMessage().split(" ");

        // On ne complète que pour "/ns <arg>" en cours de frappe du 2e argument
        final String lastToken = event.getLastToken();
        event.getPlayer().sendMessage(lastToken);
        event.getPlayer().sendMessage("l="+args.length);
        if (args.length != 2) return;
        if (!args[0].equalsIgnoreCase("/ns")) return;

        for (final IChakraV2 iChakraV2 : loadedChakra) {
            if (iChakraV2.getArg0().toLowerCase().startsWith(lastToken.toLowerCase())) {
                event.getTabCompletions().add(iChakraV2.getArg0());
            }
        }
    }
}