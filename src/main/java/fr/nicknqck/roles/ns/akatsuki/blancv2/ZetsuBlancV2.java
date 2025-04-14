package fr.nicknqck.roles.ns.akatsuki.blancv2;

import fr.nicknqck.GameState;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.AkatsukiRoles;
import fr.nicknqck.utils.TripleMap;
import fr.nicknqck.utils.event.EventUtils;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ZetsuBlancV2 extends AkatsukiRoles implements Listener {

    private TextComponent desc;


    public ZetsuBlancV2(UUID player) {
        super(player);
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.PEUINTELLIGENT;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        setChakraType(getRandomChakras());
        AutomaticDesc desc = new AutomaticDesc(this);
        desc.setItems(
          new TripleMap<>(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Lorsque vous §ctuez§7 un joueur vous gagnez l'un de ses effet permanent, les autres effets qu'il possédait son stocké dans cette item et sont récupérable constemment.")}), "§fBanque", 5)
        );
        desc.addParticularites(
          new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous possédez la nature de chakra: "+getChakras().getShowedName())}),
          new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Lorsque vous envoyez un message avec comme préfixe§c !§7 vous pourrez parler avec tout les autres§c "+getName()+"§7.")})
        );
        this.desc = desc.getText();
        EventUtils.registerEvents(this);
    }

    @Override
    public String getName() {
        return "Zetsu Blanc §7(§6V2§7)";
    }

    @Override
    public GameState.@NonNull Roles getRoles() {
        return GameState.Roles.ZetsuBlancV2;
    }

    @Override
    public void resetCooldown() {

    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public TextComponent getComponent() {
        return this.desc;
    }

    @EventHandler
    private void onEndGame(EndGameEvent event) {
        EventUtils.unregisterEvents(this);
    }
    @EventHandler
    private void onAllPlayerChat(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();
        if (p.getUniqueId().equals(getPlayer()) && event.getMessage().startsWith("!")) {
                if (event.getMessage().length() == 1) {
                    return;
                }
                String message = event.getMessage().substring(1);
                for (final GamePlayer gamePlayer : gameState.getGamePlayer().values()) {
                    if (gamePlayer.isAlive()) {
                        if (gamePlayer.getRole() != null) {
                            if (gamePlayer.getRole() instanceof ZetsuBlancV2) {
                                Player zetsu = Bukkit.getPlayer(gamePlayer.getUuid());
                                if (zetsu != null) {
                                    zetsu.sendMessage("§cZetsu Blanc V2 "+message);
                                }
                            }
                        }
                    }
                }
        }
    }

}
