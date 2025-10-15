package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.roles.ds.slayers.pillier.TomiokaV2;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;

public class UrokodakiV3 extends SlayerRoles {

    public UrokodakiV3(UUID player) {
        super(player);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.EAU;
    }

    @Override
    public String getName() {
        return "Urokodaki";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Urokodaki;
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createAutomaticDesc(this)
                .addCustomLine("§7Vous possédez l'effet§c Force I§7 dans l'§beau")
                .getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        new ForceWaterRunnable(this);
        ItemStack Book = new ItemBuilder(Material.ENCHANTED_BOOK).addStoredEnchantment(Enchantment.DEPTH_STRIDER, 2).toItemStack();
        giveItem(owner, false, Book);
        if (!Main.getInstance().getGameConfig().isMinage()) {
            owner.setLevel(owner.getLevel()+4);
        }
        addPower(new ImpacteItem(this), true);
        addPower(new DSWATER(this));
        super.RoleGiven(gameState);
    }
    private static class ImpacteItem extends ItemPower implements Listener {

        private boolean using = false;

        public ImpacteItem(@NonNull RoleBase role) {
            super("Impacte de la Cascade", new Cooldown(60*7), new ItemBuilder(Material.NETHER_STAR).setName("§bImpacte de la cascade"), role,
                    "§7En visant un joueur, vous propulse dans sa direction puis, lui fait perdre§c 2❤ non permanents§7 et",
                    "§7lui donne pendant§c 10 secondes§7 l'effet§c Slowness II§7.");
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final Player target = RayTrace.getTargetPlayer(player, 20, null);
                if (target == null) {
                    player.sendMessage("§cIl faut viser un joueur");
                    return false;
                }
                this.using = true;
                propelPlayerTo(player, target);
                return true;
            }
            return false;
        }
        public void propelPlayerTo(final Player from, final Player to) {
            if (from == null || to == null) return;

            // Si différent monde, téléport instantané final
            if (!from.getWorld().equals(to.getWorld())) {
                from.teleport(to.getLocation());
                from.setVelocity(new Vector(0, 0, 0));
                return;
            }

            // Impulsion verticale initiale immédiate
            from.setVelocity(new Vector(0, 1.35, 0));
            from.setNoDamageTicks(120);

            // Démarre la boucle de propulsion au tick suivant pour laisser l'impulsion initiale agir
            new BukkitRunnable() {
                final int maxTicks = 100; // sécurité (~10s)
                final double stopDistance = 2.5;
                final double baseSpeed = 1.6;
                final double verticalAggression = 0.8;
                int tick = 0;

                @Override
                public void run() {
                    if (!from.isOnline() || !to.isOnline()) {
                        cleanupAndCancel();
                        return;
                    }
                    if (!from.getWorld().equals(to.getWorld())) {
                        finalizeTeleport();
                        cancel();
                        return;
                    }

                    Location locFrom = from.getLocation();
                    Location locTo = to.getLocation().clone();
                    Vector diff = locTo.toVector().subtract(locFrom.toVector());
                    double distance = diff.length();

                    if (distance <= stopDistance || tick >= maxTicks) {
                        finalizeTeleport();
                        cancel();
                        return;
                    }

                    Vector dir = diff.normalize();
                    Vector horiz = new Vector(dir.getX(), 0, dir.getZ());
                    if (horiz.length() != 0) horiz = horiz.normalize().multiply(baseSpeed);

                    double yDiff = diff.getY();
                    double vy = yDiff * verticalAggression;
                    if (vy > 1.5) vy = 1.5;
                    if (vy < -1.5) vy = -1.5;

                    Vector finalVel = new Vector(horiz.getX(), vy, horiz.getZ());
                    from.setVelocity(finalVel);
                    MathUtil.sendCircleParticle(EnumParticle.WATER_SPLASH, from.getLocation(), 1, 18);
                    tick++;
                }

                private void finalizeTeleport() {
                    from.setNoDamageTicks(Math.min(from.getNoDamageTicks(), 15));
                    from.setVelocity(new Vector(0, 0, 0));
                    Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> using = false, 20);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
                        to.setHealth(Math.max(to.getHealth()-4, 1));
                        final GamePlayer gamePlayer = GamePlayer.of(to.getUniqueId());
                        boolean give = false;
                        if (gamePlayer != null) {
                            if (gamePlayer.getRole() != null) {
                                give = true;
                                gamePlayer.getRole().givePotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*200, 1, false, false), EffectWhen.NOW);
                            }
                        }
                        if (!give){
                            to.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1, false, false), true);
                        }
                        getRole().getGamePlayer().sendMessage("§c"+to.getDisplayName()+"§7 à subit votre§b Impacte de la cascade§7.");
                        to.sendMessage("§7Vous avez été toucher par l'§bImpacte de la cascade§7.");
                    }, 20);
                }

                private void cleanupAndCancel() {
                    from.setVelocity(new Vector(0, 0, 0));
                    cancel();
                }
            }.runTaskTimer(plugin, 5L, 1L); // commence au tick suivant

        }
        @EventHandler
        private void onDamage(final EntityDamageEvent event) {
            if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL))return;
            if (!event.getEntity().getUniqueId().equals(getRole().getPlayer()))return;
            if (!using)return;
            event.setDamage(0.0);
            event.setCancelled(true);
        }
    }
    private static class DSWATER extends CommandPower {

        public DSWATER(@NonNull RoleBase role) {
            super("/ds water <joueur>", "water", null, role, CommandType.DS,
                    "§7Si la personne visée est l'un de vos élève, vous obtiendrez l'effet§e Speed I§7 de manière§c permanente§7 et",
                    "§9Résistance I§7 proche de la§c cible§7.",
                    "",
                    "§7Vos élèves sont:§a Tanjiro§7,§a Tomioka§7,§a Sabito§7 et§a Makomo§7.");
            setMaxUse(1);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            final String[] args = (String[]) map.get("args");
            if (args.length == 2) {
                final Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    final GamePlayer gamePlayer = GameState.getInstance().getGamePlayer().get(target.getUniqueId());
                    if (gamePlayer != null) {
                        if (gamePlayer.getRole() != null) {
                            if (gamePlayer.getRole() instanceof Tanjiro ||
                                    gamePlayer.getRole() instanceof TomiokaV2 ||
                                    gamePlayer.getRole() instanceof SabitoV2 ||
                                    gamePlayer.getRole() instanceof Makomo) {
                                getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
                                new ResistanceRunnable(this, gamePlayer).runTaskTimerAsynchronously(getPlugin(), 20, 20);
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }
        private static class ResistanceRunnable extends BukkitRunnable {

            private final DSWATER dswater;
            private final GamePlayer gamePlayer;

            private ResistanceRunnable(DSWATER dswater, GamePlayer gamePlayer) {
                this.dswater = dswater;
                this.gamePlayer = gamePlayer;
            }

            @Override
            public void run() {
                if (!dswater.getRole().getGameState().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                final Player owner = Bukkit.getPlayer(dswater.getRole().getPlayer());
                if (owner == null)return;
                if (!gamePlayer.isOnline())return;
                if (!gamePlayer.isAlive())return;
                for (Player player : owner.getWorld().getPlayers()) {
                    if (player.getUniqueId().equals(owner.getUniqueId()))continue;
                    if (player.getLocation().distance(owner.getLocation()) > 25)continue;
                    if (!player.getUniqueId().equals(gamePlayer.getUuid()))continue;
                    Bukkit.getScheduler().runTask(this.dswater.getPlugin(), () -> this.dswater.getRole().givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0, false
                    , false), EffectWhen.NOW));
                }
            }
        }
    }
    private static class ForceWaterRunnable extends BukkitRunnable {

        private final UrokodakiV3 urokodaki;

        private ForceWaterRunnable(UrokodakiV3 urokodaki) {
            this.urokodaki = urokodaki;
            runTaskTimerAsynchronously(Main.getInstance(), 20, 1);
        }

        @Override
        public void run() {
            if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (!urokodaki.getGamePlayer().isAlive() || !urokodaki.getGamePlayer().isOnline()) {
                return;
            }
            final Player player = Bukkit.getPlayer(this.urokodaki.getPlayer());
            if (player == null)return;
            if (player.getLocation().getBlock().getType().name().contains("WATER") || player.getEyeLocation().getBlock().getType().name().contains("WATER")) {
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> this.urokodaki.givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.NOW));
            }
        }
    }
}