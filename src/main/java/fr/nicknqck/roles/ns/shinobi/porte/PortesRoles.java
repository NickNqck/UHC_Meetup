package fr.nicknqck.roles.ns.shinobi.porte;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.DoubleCircleEffect;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public abstract class PortesRoles extends ShinobiRoles implements Listener {

    public final ItemStack troisPorteItem = new ItemBuilder(Material.NETHER_STAR).setName("§aTroisième Porte").setUnbreakable(true).setDroppable(false).toItemStack();
    public final ItemStack sixPorteItem = new ItemBuilder(Material.NETHER_STAR).setName("§aSixième Porte").setUnbreakable(true).setDroppable(false).toItemStack();
    public final ItemStack huitPorteItem = new ItemBuilder(Material.NETHER_STAR).setName("§aHuitième Porte").setUnbreakable(true).setDroppable(false).toItemStack();
    private int cdTrois, cdSix;
    private boolean huitUsed = false;

    public PortesRoles(UUID player) {
        super(player);
        EventUtils.registerEvents(this);
        new PortePowersListener(this);

    }
    @EventHandler
    private void onEndGame(EndGameEvent event) {
        EventUtils.unregisterEvents(this);
    }
    @Override
    public void resetCooldown() {
        cdTrois = 0;
        cdSix = 0;
        huitUsed = false;
    }
    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.PEUINTELLIGENT;
    }
    private static class PortePowersListener implements Listener {

        private final PortesRoles role;

        private PortePowersListener(PortesRoles role) {
            this.role = role;
            EventUtils.registerEvents(this);
            new PorteCooldownRunnable(role).runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
        }
        @EventHandler
        private void onEndGame(EndGameEvent event) {
            EventUtils.unregisterEvents(this);
        }
        @EventHandler
        private void onInteract(PlayerInteractEvent event) {
            if (!event.hasItem()) return;
            if (event.isCancelled()) return;
            if (!event.getPlayer().getUniqueId().equals(role.getPlayer())) return;
            ItemStack item = event.getItem();
            if (item.isSimilar(role.troisPorteItem)) {
                event.setCancelled(true);
                if (role.cdTrois > 0) {
                    role.sendCooldown(event.getPlayer(), role.cdTrois);
                    return;
                }
                if (role.huitUsed) {
                    event.getPlayer().sendMessage("§cVous avez déjà utiliser toute votre espérence de vie");
                    return;
                }
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 60 + 30, 0, false, false), true);
                if (event.getPlayer().getHealth() - 2.0 <= 0.0) {
                    event.getPlayer().setHealth(1.0);
                } else {
                    event.getPlayer().setHealth(event.getPlayer().getHealth() - 2.0);
                }
                role.cdTrois = 90;
            }
            if (item.isSimilar(role.sixPorteItem)) {
                event.setCancelled(true);
                if (role.cdSix > 0) {
                    role.sendCooldown(role.owner, role.cdSix);
                    return;
                }
                if (role.huitUsed) {
                    event.getPlayer().sendMessage("§cVous avez déjà utiliser toute votre espérence de vie");
                    return;
                }
                if (role.getMaxHealth() - 2.0 <= 0) {
                    event.getPlayer().sendMessage("§cVous ne pouvez plus utiliser cette technique !");
                    return;
                }
                role.cdSix = 180;
                role.setMaxHealth(role.getMaxHealth()-2.0);
                role.owner.setMaxHealth(role.getMaxHealth());
                role.owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 60 *3, 0, false, false), true);
                role.owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 60 * 3, 0, false, false), true);
                new DoubleCircleEffect(20*60*3, EnumParticle.VILLAGER_HAPPY).start(role.owner);
            }
            if (item.isSimilar(role.huitPorteItem)) {
                event.setCancelled(true);
                if (role.huitUsed) {
                    event.getPlayer().sendMessage("§cVous avez déjà utiliser toute votre espérence de vie");
                    return;
                }
                new DoubleCircleEffect(20*60*5, EnumParticle.REDSTONE).start(role.owner);
                role.owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*5, 1, false, false), true);
                role. owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60*5, 1, false, false), true);
                role.owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*60*5, 1, false, false), true);
                role.owner.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20*60*5, 1, false, false), true);
                role.setMaxHealth(30.0);
                role.owner.setMaxHealth(role.getMaxHealth());
                role.owner.setHealth(role.owner.getMaxHealth());
                role.cdSix = -1;
                role.cdTrois = -1;
                role.huitUsed = true;
                Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
                    if (Bukkit.getPlayer(role.getPlayer()) != null) {
                        Player owner = Bukkit.getPlayer(role.getPlayer());
                        if (role.gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                            if (!role.gameState.hasRoleNull(owner)) {
                                if (role.gameState.getGamePlayer().get(owner.getUniqueId()).getRole() instanceof RockLeeV2) {
                                    if (role.gameState.getGamePlayer().get(owner.getUniqueId()).getRole().StringID.equals(role.StringID)) {//donc c'est définitivement la même partie que quand il a activer
                                        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                                            role.setMaxHealth(10.0);
                                            owner.setMaxHealth(role.getMaxHealth());
                                            owner.setHealth(owner.getHealth());
                                        });
                                    }
                                }
                            }
                        }
                    }
                }, 20*60*5);
            }
        }
        private static class PorteCooldownRunnable extends BukkitRunnable {

            private final PortesRoles role;

            private PorteCooldownRunnable(PortesRoles role) {
                this.role = role;
            }

            @Override
            public void run() {
                if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (role.cdTrois >= 0) {
                    role.cdTrois--;
                    if (role.cdTrois == 0) {
                        role.owner.sendMessage("§7Vous pouvez à nouveau utiliser la§a Troisième Porte");
                    }
                }
                if (role.cdSix >= 0) {
                    role.cdSix--;
                    if (role.cdSix == 0) {
                        role.owner.sendMessage("§7Vous pouvez à nouveau utiliser la§a Sixième Porte");
                    }
                }
            }
        }
    }
}