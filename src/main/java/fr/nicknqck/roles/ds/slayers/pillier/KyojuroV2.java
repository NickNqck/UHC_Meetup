package fr.nicknqck.roles.ds.slayers.pillier;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.builders.Lames;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import lombok.Setter;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;

public class KyojuroV2 extends PilierRoles {

    private TextComponent desc;
    @Setter
    private boolean alliance = false;

    public KyojuroV2(UUID player) {
        super(player);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.FLAMME;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Kyojuro";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Kyojuro;
    }

    @Override
    public void resetCooldown() {
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 60, 0, false, false), EffectWhen.PERMANENT);
        getCantHave().add(Lames.FireResistance);
        addPower(new FlammePower(this));
        addPower(new DashPower(this), true);
        AutomaticDesc desc = new AutomaticDesc(this).addEffects(getEffects()).setPowers(getPowers());
        this.desc = desc.getText();
    }

    @Override
    public TextComponent getComponent() {
        return this.desc;
    }
    private static class FlammePower extends CommandPower implements Listener {

        private boolean end;

        public FlammePower(@NonNull KyojuroV2 role) {
            super("§6/ds flamme", "flamme", new Cooldown(60*10), role, CommandType.DS, "§7Vous permet d'§6enflammer§7 les joueurs que vous frappez et ceux sur lesquelles vous tirez,","§7En frappant un joueur vous aurez§c 35%§7 de§c chance§7 de recevoir§c 5 secondes§7 de l'effet§c Force I§7.");
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> map) {
            if (getCooldown().isInCooldown() && getCooldown().getCooldownRemaining() >= 60*7) {
                return false;
            }
            end = false;
            EventUtils.registerEvents(this);
            player.sendMessage("§7Vous activez votre§c Soufle de la Flamme");
            Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> {
                if (end)return;
                EventUtils.unregisterEvents(this);
                player.sendMessage("§7Votre§c Soufle de la Flamme§7 se désactive");
            }, 20*60*3);
            return true;
        }
        @EventHandler
        private void onDamage(EntityDamageByEntityEvent event) {
            if (event.getDamager().getWorld().getName().equals("enmuv2_duel"))return;
            if (event.getDamager() instanceof Player) {
                if (event.getDamager().getUniqueId().equals(getRole().getPlayer())){
                    event.getEntity().setFireTicks(event.getEntity().getFireTicks()+160);
                    if (Main.RANDOM.nextInt(100) <= 35 || ((KyojuroV2)getRole()).alliance) {
                        ((Player) event.getDamager()).addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0, false, false), true);
                    }
                }
            } else if (event.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) event.getDamager();
                if (projectile.getShooter() instanceof Player && ((Player) projectile.getShooter()).getUniqueId().equals(getRole().getPlayer())) {
                    Player shooter = (Player) projectile.getShooter();
                    event.getEntity().setFireTicks(event.getEntity().getFireTicks()+160);
                    if (Main.RANDOM.nextInt(100) <= 35 || ((KyojuroV2)getRole()).alliance) {
                        shooter.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0, false, false), true);
                    }
                }
            }
        }
        @EventHandler
        private void onEndGame(EndGameEvent event) {
            EventUtils.unregisterEvents(this);
            end = true;
        }
    }
    private static class DashPower extends ItemPower implements Listener {

        private final DashRunnable runnable;

        protected DashPower(RoleBase role) {
            super("§cDash de Flamme", new Cooldown(60*10), new ItemBuilder(Material.BLAZE_ROD).setName("§cDash de Flamme"), role, "§7Vous permet d'effectuer un§c dash§6 enflammant§7 et détruisant tout les blocs autours de vous");
            this.runnable = new DashRunnable(this);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                runnable.time = 0;
                player.setAllowFlight(true);
                runnable.runTaskTimerAsynchronously(getPlugin(), 0, 1);
                EventUtils.registerRoleEvent(this);
                return true;
            }
            return false;
        }
        @EventHandler
        private void onToggleFly(PlayerToggleFlightEvent event) {
            if (event.getPlayer().getUniqueId().equals(getRole().getPlayer())) {
                if (this.runnable.time > 0 && this.runnable.time < 21) {
                    event.setCancelled(true);
                }
            }
        }
        private static class DashRunnable extends BukkitRunnable {

            private final GameState gameState;
            private final DashPower power;
            private final GamePlayer gamePlayer;
            private int time;


            private DashRunnable(DashPower power) {
                this.power = power;
                this.gameState = power.getRole().getGameState();
                this.gamePlayer = power.getRole().getGamePlayer();
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (!gamePlayer.isAlive()) {
                    cancel();
                    return;
                }
                if (time == 20) {
                    time++;
                    Player player = Bukkit.getPlayer(gamePlayer.getUuid());
                    if (player != null) {
                        Bukkit.getScheduler().runTask(power.getPlugin(), () -> {
                            player.sendMessage("§7Votre§c dash§7 se termine.");
                            player.setAllowFlight(false);
                        });
                    }
                    cancel();
                    return;
                }
                time++;
                Player player = Bukkit.getPlayer(gamePlayer.getUuid());
                if (player != null) {
                    Vector v = player.getEyeLocation().getDirection();
                    v.setY(0.1);
                    player.setVelocity(v.multiply(1));
                    Bukkit.getScheduler().runTask(power.getPlugin(), () -> {
                        for (final Location location : MathUtil.getCircle(player.getLocation(), 4.0)) {
                            final Block block = location.getWorld().getBlockAt(location);
                            if (block != null) {
                                if (block.getType().equals(Material.AIR) && !block.getWorld().getBlockAt(block.getLocation().add(0, -1, 0)).getType().equals(Material.AIR)) {
                                    block.setType(Material.FIRE);
                                    MathUtil.sendParticle(EnumParticle.EXPLOSION_NORMAL, block.getLocation());
                                }
                            }
                            for (final Player around : Loc.getNearbyPlayers(location, 1.0)) {
                                around.setFireTicks(180);
                            }
                        }
                    });
                    player.playSound(player.getLocation(), Sound.EXPLODE, 1, 8);
                }
            }
        }
    }
}