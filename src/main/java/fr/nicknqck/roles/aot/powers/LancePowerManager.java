package fr.nicknqck.roles.aot.powers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.GameEndEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.titans.TitanBase;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.Cooldown;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentHashMap;

public class LancePowerManager {

    private static final ConcurrentHashMap<GamePlayer, Lance> lancesMap = new ConcurrentHashMap<>();
    @Getter
    private static final ItemStack lanceItem = new ItemBuilder(Material.SNOW_BALL)
            .setName("§aLance explosive")
            .addEnchant(Enchantment.ARROW_FIRE, 1)
            .hideEnchantAttributes()
            .setLore(
                    "§7Lorsque vous lancez une§a Lance explosive§7 sur un joueur, il subirat des dégâts",
                    "",
                    "§7Si une personne touché est§c transformer en titan§7, elle subira§c 1,5"+AllDesc.coeur+"§7, sinon ce sera§c 1"+AllDesc.coeur+"§7.",
                    "",
                    "§7Si personne n'est touché, alors tout les joueurs étant à moins de§c 3 blocs§7 subiront les effets de la§c lance explosive§7."
            )
            .toItemStack();

    public static synchronized boolean giveLance(@NonNull final GamePlayer player) {
        final Lance lance;
        if (lancesMap.containsKey(player)) {
            lance = lancesMap.get(player);
        } else {
            lance = new Lance(player);
            lancesMap.put(player, lance);
        }
        return lance.tryGiveLance();
    }
    public static synchronized boolean giveLance(@NonNull final GamePlayer player, final int amount) {
        final Lance lance;
        if (lancesMap.containsKey(player)) {
            lance = lancesMap.get(player);
        } else {
            lance = new Lance(player);
            lancesMap.put(player, lance);
        }
        int gived = 0;
        for (int i = 0; i < amount; i++) {
            if (lance.tryGiveLance()) {
                gived++;
            }
        }
        return gived == amount;
    }
    private static final class Lance implements Listener {

        private final GamePlayer gamePlayer;
        @Getter
        private int amountLance = 0;
        @Getter
        private final Cooldown cooldown = new Cooldown(8);

        private Lance(GamePlayer gamePlayer) {
            this.gamePlayer = gamePlayer;
            EventUtils.registerRoleEvent(this);
            new LanceRunnable(this).runTaskTimerAsynchronously(Main.getInstance(), 20, 20);
        }
        private boolean tryGiveLance() {
            if (this.amountLance < Main.getInstance().getGameConfig().getAotConfig().getAmountLanceMax()) {
                this.amountLance++;
                gamePlayer.addItems(LancePowerManager.lanceItem);
                return true;
            }
            return false;
        }
        @EventHandler
        private void onEndGame(final GameEndEvent event) {
            LancePowerManager.lancesMap.clear();
        }
        @EventHandler
        private void onShoot(@NonNull final ProjectileLaunchEvent event) {
            if (event.getEntity() instanceof Snowball) {
                final Snowball snowball = (Snowball) event.getEntity();
                if (snowball.getShooter() instanceof Player) {
                    final Player player = (Player) snowball.getShooter();
                    if (player.getUniqueId().equals(gamePlayer.getUuid())) {
                        if (cooldown.isInCooldown()) {
                            player.getInventory().addItem(LancePowerManager.lanceItem);
                            player.sendMessage("§cVous êtes en cooldown:§b "+ StringUtils.secondsTowardsBeautiful(getCooldown().getCooldownRemaining()));
                            event.setCancelled(true);
                            return;
                        }
                        if (gamePlayer.isAlive() && gamePlayer.isOnline()) {
                            snowball.setMetadata("lance_explosive", new FixedMetadataValue(Main.getInstance(), "lance_explosive"));
                        } else {
                            player.sendMessage("§cVous êtes en cooldown:§b "+ StringUtils.secondsTowardsBeautiful(getCooldown().getCooldownRemaining()));
                            event.setCancelled(true);
                            player.getInventory().addItem(LancePowerManager.lanceItem);
                        }
                    }
                }
            }
        }
        @EventHandler
        private void onHit(final EntityDamageByEntityEvent event) {
            if (event.getDamager() instanceof Snowball) {
                final Snowball snowball = (Snowball) event.getDamager();
                if (snowball.getShooter() instanceof Player) {
                    final Player player = (Player) snowball.getShooter();
                    if (player.getUniqueId().equals(gamePlayer.getUuid())) {
                        if (snowball.hasMetadata("lance_explosive")) {
                            snowball.removeMetadata("lance_explosive", Main.getInstance());
                            if (event.getEntity() instanceof Player) {
                                createExplosion(event.getEntity().getLocation(), (Player) event.getEntity());
                            } else {
                                createExplosion(event.getEntity().getLocation(), null);
                            }
                        }
                    }
                }
            }
        }
        @EventHandler
        private void onHit2(@NonNull final ProjectileHitEvent event) {
            if (event.getEntity() instanceof Snowball) {
                final Snowball snowball = (Snowball) event.getEntity();
                if (snowball.getShooter() instanceof Player) {
                    final Player player = (Player) snowball.getShooter();
                    if (player.getUniqueId().equals(gamePlayer.getUuid())) {
                        if (snowball.hasMetadata("lance_explosive")) {
                            snowball.removeMetadata("lance_explosive", Main.getInstance());
                            createExplosion(event.getEntity().getLocation(), null);
                        }
                    }
                }
            }
        }
        private void createExplosion(@NonNull final Location location, @Nullable final Player player) {
            MathUtil.sendParticle(EnumParticle.EXPLOSION_LARGE, location);
            this.amountLance--;
            this.cooldown.use();
            if (player != null) {
                player.setVelocity(new Vector(0.0, 1.5, 0.0).add(location.getDirection().multiply(2)));
                player.damage(0.0);
                if (Main.getInstance().getTitanManager().hasTitan(player.getUniqueId())) {
                    final TitanBase titan = Main.getInstance().getTitanManager().getTitan(player.getUniqueId());
                    if (titan.isTransformed()) {
                        player.setHealth(Math.max(1.0, player.getHealth()-3.0));
                        player.sendMessage(Main.getInstance().getNAME()+"§a Vous avez été toucher par une§a§l Lance Explosive§r§a, vous avez perdu§c 1,5"+ AllDesc.coeur);
                    }
                    return;
                }
                player.setHealth(Math.max(1.0, player.getHealth()-2.0));
                player.sendMessage(Main.getInstance().getNAME()+"§a Vous avez été toucher par une§a§l Lance Explosive§r§a, vous avez perdu§c 1"+ AllDesc.coeur);
            } else {
                for (@NonNull final Player target : Loc.getNearbyPlayers(location, 3)) {
                    if (target.getUniqueId().equals(gamePlayer.getUuid()))continue;
                    target.setVelocity(new Vector(0.0, 1.5, 0.0).add(location.getDirection().multiply(2)));
                    target.damage(0.0);
                    if (Main.getInstance().getTitanManager().hasTitan(target.getUniqueId())) {
                        final TitanBase titan = Main.getInstance().getTitanManager().getTitan(target.getUniqueId());
                        if (titan.isTransformed()) {
                            target.setHealth(Math.max(1.0, target.getHealth()-2.0));
                            target.sendMessage(Main.getInstance().getNAME()+"§a Vous avez été toucher par une§a§l Lance Explosive§r§a, vous avez perdu§c 1"+ AllDesc.coeur);
                        }
                        continue;
                    }
                    target.setHealth(Math.max(1.0, target.getHealth()-1.0));
                    target.sendMessage(Main.getInstance().getNAME()+"§a Vous avez été toucher par une§a§l Lance Explosive§r§a, vous avez perdu§c 0,5"+ AllDesc.coeur);
                }
            }
        }
        private static final class LanceRunnable extends BukkitRunnable {

            private final Lance lance;

            private LanceRunnable(Lance lance) {
                this.lance = lance;
            }

            @Override
            public void run() {
                if (!GameState.inGame()) {
                    cancel();
                    return;
                }
                if (!lance.gamePlayer.isOnline() || !lance.gamePlayer.isAlive()) {
                    return;
                }
                final Player player = Bukkit.getPlayer(lance.gamePlayer.getUuid());
                if (player == null)return;
                if (player.getItemInHand() == null)return;
                if (player.getItemInHand().isSimilar(LancePowerManager.lanceItem)) {
                    this.lance.gamePlayer.getActionBarManager().updateActionBar("aot.lance", this.lance.cooldown.isInCooldown() ?
                            "§bCooldown: §c"+ StringUtils.secondsTowardsBeautiful(this.lance.cooldown.getCooldownRemaining()) :
                            LancePowerManager.lanceItem.getItemMeta().getDisplayName()+" est§c utilisable");
                } else {
                    this.lance.gamePlayer.getActionBarManager().removeInActionBar("aot.lance");
                }
            }
        }
    }
}