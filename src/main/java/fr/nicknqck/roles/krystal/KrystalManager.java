package fr.nicknqck.roles.krystal;

import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.events.custom.roles.krystal.KrystalGiveEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.event.EventUtils;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class KrystalManager implements Listener {

    public static final String prequel = "§7[§dKrystal-UHC§7]";

    public KrystalManager() {
        EventUtils.registerEvents(this);
    }

    @EventHandler
    private void onMine(@NonNull final BlockBreakEvent event) {
        if (event.getBlock().getType().equals(Material.EMERALD_ORE)) {
            final GamePlayer gamePlayer = GamePlayer.of(event.getPlayer().getUniqueId());
            if (gamePlayer != null) {
                if (gamePlayer.getRole() != null && gamePlayer.isAlive() && gamePlayer.isOnline()) {
                    if (gamePlayer.getRole() instanceof KrystalBase) {
                        if (event.getBlock().hasMetadata("pur")) {
                            int amount = giveKrystals((KrystalBase) gamePlayer.getRole(), RandomUtils.getRandomInt(2, 4));
                            gamePlayer.sendMessage(prequel+"§a Vous avez gagner§c "+amount+" krystaux");
                        } else {
                            int amount = giveKrystals((KrystalBase) gamePlayer.getRole(), 1);
                            gamePlayer.sendMessage(prequel+"§a Vous avez gagner§c "+amount+" krystaux");
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    private void onKill(final UHCPlayerKillEvent event) {
        if (event.getGamePlayerKiller() == null)return;
        if (event.getGamePlayerKiller().getRole() == null)return;
        if (event.getGamePlayerKiller().getRole() instanceof KrystalBase) {
            if (event.getGameState().hasRoleNull(event.getVictim().getUniqueId()))return;
            final GamePlayer gameVictim = event.getGameState().getGamePlayer().get(event.getVictim().getUniqueId());
            if (gameVictim.getRole() instanceof KrystalBase) {
                int toAdd = ((KrystalBase) gameVictim.getRole()).getKrystalAmount()/3;
                if (toAdd <= 0) {
                    return;
                }
                toAdd = giveKrystals((KrystalBase) event.getGamePlayerKiller().getRole(), toAdd);
                event.getGamePlayerKiller().sendMessage(prequel+"§c "+event.getVictim().getDisplayName()+"§b avait quelque krystaux sur lui, vous n'avez pus en récupérer que§c "+toAdd+"§b.");
                ((KrystalBase) gameVictim.getRole()).setKrystalAmount(((KrystalBase) gameVictim.getRole()).getKrystalAmount()-toAdd);
            }
        }
    }
    public int giveKrystals(@NonNull final KrystalBase role, int amount) {
        final KrystalGiveEvent krystalGiveEvent = new KrystalGiveEvent(role, amount);
        if (!krystalGiveEvent.isCancelled()) {
            int i = krystalGiveEvent.getAmount();
            role.setKrystalAmount(role.getKrystalAmount()+i);
            return i;
        }
        return 0;
    }
}