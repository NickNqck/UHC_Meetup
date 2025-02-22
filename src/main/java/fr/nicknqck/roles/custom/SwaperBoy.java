package fr.nicknqck.roles.custom;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SwaperBoy extends CustomRolesBase{

    public SwaperBoy(UUID player) {
        super(player);
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "The Swapper";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.TheSwapper;
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Solo;
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
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new SwapPower(this));
    }
    private static class SwapPower extends Power implements Listener {

        public SwapPower(@NonNull RoleBase role) {
            super("Super Swap", null, role);
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> map) {
            return true;
        }
        @EventHandler
        private void UHCPlayerBattleEvent(final UHCPlayerBattleEvent event) {
            if (!event.getDamager().getUuid().equals(getRole().getPlayer()))return;
            if (!checkUse((Player) event.getOriginEvent().getDamager(), new HashMap<>()))return;
            if (!((Player)event.getOriginEvent().getDamager()).getItemInHand().getType().name().contains("SWORD"))return;
            if (!event.isPatch())return;
            if (Main.RANDOM.nextInt(20) != 1) return;
            final int random = Main.RANDOM.nextInt(9);
            final Player victim = (Player) event.getOriginEvent().getEntity();
            for (int i = 0; i <= 8; i++) {
                final ItemStack stack = victim.getInventory().getItem(i);
                if (stack == null)continue;
                if (stack.getType().equals(Material.AIR))continue;
                if (i == random) {
                    final ItemStack oldStack = victim.getItemInHand();
                    victim.setItemInHand(stack);
                    victim.getInventory().setItem(i, oldStack);
                    break;
                }
            }
        }
        @EventHandler
        private void EntityDamageByEntityEvent(final EntityDamageByEntityEvent event) {
            if (event.getDamager() instanceof Arrow) {
                final Arrow arrow = (Arrow) event.getDamager();
                if (arrow.getShooter() instanceof Player) {
                    final Player shooter = (Player) arrow.getShooter();
                    if (shooter.getUniqueId().equals(getRole().getPlayer())) {
                        final int random = Main.RANDOM.nextInt(101);
                        if (random <= 25) {
                            final Location loc1 = event.getEntity().getLocation();
                            final Location loc2 = shooter.getLocation();
                            event.getEntity().teleport(loc2);
                            shooter.teleport(loc1);
                            event.getEntity().sendMessage("§cVous avez été swap par le§6 "+getRole().getName());
                        }
                    }
                }
            }
        }
        @EventHandler
        private void TeleportEvent(final PlayerTeleportEvent event) {
            if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) {
                if (event.getPlayer().getUniqueId().equals(getRole().getPlayer())) {
                    final int random = Main.RANDOM.nextInt(101);
                    if (random <= 76) {
                        final Location to = event.getTo();
                        GamePlayer tp = getRole().getGamePlayer();
                        Location oldTpLoc = event.getPlayer().getLocation();
                        for (final GamePlayer gamePlayer : getRole().getGameState().getGamePlayer().values()) {
                            if (!gamePlayer.isAlive())continue;
                            if (gamePlayer.getDiscRunnable() != null){
                                if (!gamePlayer.getDiscRunnable().isOnline())return;
                            }
                            if (gamePlayer.getLastLocation().getWorld().equals(to.getWorld())) {
                                if (to.distance(gamePlayer.getLastLocation()) < tp.getLastLocation().distance(to)) {
                                    tp = gamePlayer;
                                    oldTpLoc = gamePlayer.getLastLocation();
                                }
                            }
                        }
                        final Player player = Bukkit.getPlayer(tp.getUuid());
                        if (player != null) {
                            player.teleport(to, PlayerTeleportEvent.TeleportCause.PLUGIN);
                            event.getPlayer().teleport(oldTpLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);
                        }
                    }
                }
            }
        }
    }
}
