package fr.nicknqck.roles.ns.akatsuki;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.interfaces.IRoles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.enums.EChakras;
import fr.nicknqck.enums.Intelligence;
import fr.nicknqck.roles.ns.builders.AkatsukiRoles;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.fastinv.FastInv;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DeidaraV2 extends AkatsukiRoles {

    public DeidaraV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.MOYENNE;
    }

    @Override
    public EChakras[] getChakrasCanHave() {
        return new EChakras[] {
                EChakras.DOTON,
                EChakras.RAITON
        };
    }

    @Override
    public String getName() {
        return "Deidara";
    }

    @Override
    public @NonNull IRoles<?> getRoles() {
        return Roles.Deidara;
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new BakutonItem(this), true);
        super.RoleGiven(gameState);
    }

    private static final class BakutonItem extends ItemPower {

        private final Cooldown cd1 = new Cooldown(20);
        private final Cooldown cd2 = new Cooldown(60*8);
        private final Cooldown cd3 = new Cooldown(60*10);
        private final Cooldown cd4 = new Cooldown(60*8+15);
        private final ArtUltimePower artUltimePower;

        @Getter
        private enum Mode {
            C1(new ItemBuilder(Material.SULPHUR).setName("§cC1").toItemStack()),
            C2(new ItemBuilder(Material.FEATHER).setName("§cC2").toItemStack()),
            C3(new ItemBuilder(Material.TNT).setName("§cC3").toItemStack()),
            C4(new ItemBuilder(Material.POTION).setDurability(16420).setName("§cC4").toItemStack()),
            ARTULTIME(new ItemBuilder(Material.STONE).setName("§cArt Ultime").toItemStack());
            final ItemStack item;
            Mode(ItemStack e) {
                this.item = e;
            }
        }
        private Mode mode;

        public BakutonItem(@NonNull RoleBase role) {
            super("§6Bakuton§r", new Cooldown(1), new ItemBuilder(Material.NETHER_STAR).setName("§6Bakuton"), role,
                    "§7Effectue plusieurs actions selon le§c clique effectué§7:",
                    "",
                    "§fClique droit§7: Vous permet d'ouvrir un menu vous permettant de choisir une technique parmi§a celle-ci§f:",
                    "",
                    "§7     →§cC1§7: Avec ce mode d'§aactiver§7, vos flèches créeront une explosion qui à l'impacte, inflige§c 1❤ de dégât au joueurs proche.§7 (1x/20s)",
                    "",
                    "§7     →§cC2§7: La prochaine flèche que vous tirerez avec l'§cArc Explosif§7 créera une explosion a l'impacte de la même puissance que le§c C1§7, de plus,",
                    "§7il vous donnera un§a fly§7 de§c 10s§7, qui, chaque§c seconde§7, lâchera une§c TNT§7 en dessous de vous.§7 (1x/8m)",
                    "",
                    "§7     →§cC3§7: Fais apparaître une§c pluie de TNT§7 d'une taille de§c 10x10§7 qui apparaitront§c 15 blocs§7 au dessus du sol.§7 (1x/10m)",
                    "",
                    "§7     →§cC4§7: Pendant§c 15s§7 tout les joueurs à moins de 25 blocs de la zone d'§catterissage§7 de la §c flèche§7 perdront§c 1/2"+AllDesc.coeur+"/§cs§7.§7 (1x/8m)",
                    "",
                    "§7     →§cArt Ultime§7:§c Après avoir sélectionné§7 ce§c mode§7, en faisant un§c Shift + clique gauche avec le§6 Bakûton§7 vous empêche de bouger pendant§c 10s§7 puis,",
                    "§7crée une explosion énorme tuant tout joueurs dans la zone de cette dernière (dont vous).§7 (1x/partie)",
                    "",
                    "§fClique gauche§7: Vous permet de changer§c rapidement§7 de§c mode§7 (ne vous permet d'accédez à l'§cArt Ultime§7)."
            );
            this.mode = Mode.C1;
            setSendCooldown(false);
            getShowCdRunnable().setCustomText(true);
            getRole().addPower(new ArcExplosif(role, this), true);
            this.artUltimePower = new ArtUltimePower(role);
            getRole().addPower(this.artUltimePower);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (map.containsKey("event") && map.get("event") instanceof PlayerInteractEvent) {
                    final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                    if (event.getAction().name().contains("LEFT")) {
                        if (this.mode.equals(Mode.C1)) {
                            this.mode = Mode.C2;
                            player.sendMessage("§7Vous utilisez maintenant la technique \"§cC2§7\".");
                            return true;
                        }
                        if (this.mode.equals(Mode.C2)) {
                            this.mode = Mode.C3;
                            player.sendMessage("§7Vous utilisez maintenant la technique \"§cC3§7\".");
                            return true;
                        }
                        if (this.mode.equals(Mode.C3)) {
                            this.mode = Mode.C4;
                            player.sendMessage("§7Vous utilisez maintenant la technique \"§cC4§7\".");
                            return true;
                        }
                        if (this.mode.equals(Mode.C4)) {
                            this.mode = Mode.C1;
                            player.sendMessage("§7Vous utilisez maintenant la technique \"§cC1§7\".");
                            return true;
                        }
                        if (this.mode.equals(Mode.ARTULTIME)) {
                            if (player.isSneaking()) {
                                return this.artUltimePower.checkUse(player, map);
                            } else {
                                player.sendMessage("§7Pour vous§c sacrifiez§7 il faudra faire un§c Shift + Clique gauche§7.");
                            }
                        }
                    } else {
                        final FastInv fastInv = new FastInv(27, "§fTechniques§6 Bakuton");
                        fastInv.setItems(fastInv.getCorners(), new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).toItemStack());
                        int i = 10;
                        for (Mode modes : Mode.values()) {
                            if (modes.equals(Mode.ARTULTIME)) {
                                fastInv.setItem(22, modes.getItem(), event1 -> {
                                    event1.getWhoClicked().closeInventory();
                                    this.mode = modes;
                                    player.sendMessage("§7Vous utilisez maintenant la technique \""+this.mode.getItem().getItemMeta().getDisplayName()+"§7\".");
                                    event1.setCancelled(true);
                                });
                                continue;
                            }
                            fastInv.setItem(i, modes.getItem(), event1 -> {
                                event1.getWhoClicked().closeInventory();
                                this.mode = modes;
                                player.sendMessage("§7Vous utilisez maintenant la technique \""+this.mode.getItem().getItemMeta().getDisplayName()+"§7\".");
                                event1.setCancelled(true);
                            });
                            i+=2;
                        }
                        fastInv.open(player);
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void tryUpdateActionBar() {
            getShowCdRunnable().setCustomTexte("§fTechnique§6 Bakuton§f actuel§7: "+this.mode.getItem().getItemMeta().getDisplayName() + "§7 | " +
                    (!this.cd1.isInCooldown() ? "§cC1§7:§a ✔" : "§cC1§7:§c "+ StringUtils.secondsTowardsBeautiful(this.cd1.getCooldownRemaining()))
                    + "§7 | " +
                    (!this.cd2.isInCooldown() ? "§cC2§7:§a ✔" : "§cC2§7:§c "+StringUtils.secondsTowardsBeautiful(this.cd2.getCooldownRemaining()))
                    + "§7 | " +
                    (!this.cd3.isInCooldown() ? "§cC3§7:§a ✔" : "§cC3§7:§c "+StringUtils.secondsTowardsBeautiful(this.cd3.getCooldownRemaining()))
                    + "§7 | " +
                    (!this.cd4.isInCooldown() ? "§cC4§7:§a ✔" : "§cC4§7:§c "+StringUtils.secondsTowardsBeautiful(this.cd4.getCooldownRemaining()))
                    );
        }
        private static final class ArtUltimePower extends Power {

            public ArtUltimePower(@NonNull RoleBase role) {
                super("§cArt Ultime§r", null, role);
                setShowInDesc(false);
                setMaxUse(1);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                player.sendMessage("§7L'§cArt Ultime§7 ce déclanche des maintenants");
                player.setAllowFlight(true);
                player.setFlying(true);
                getRole().getGamePlayer().stun(20*10, false);
                Main.getInstance().debug("Début de l'Art Ultime de Deidara ("+player.getName()+")");
                new BukkitRunnable() {
                    int i = 10;
                    final Location loc = player.getLocation().clone();
                    @Override
                    public void run() {
                        if (!GameState.inGame()) {
                            cancel();
                        }
                        if (i == 10) {
                            for (Player p : Loc.getNearbyPlayers(loc, 50)) {
                                p.playSound(p.getLocation(), Sound.WITHER_DEATH, 8, 8);
                            }
                        }
                        if (i == 5) {
                            for (Player p : Loc.getNearbyPlayers(loc, 50)) {
                                p.playSound(p.getLocation(), Sound.WITHER_DEATH, 8, 8);
                            }
                        }
                        if (i == 0) {
                            Location Center = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
                            for(Location loc : new MathUtil().sphere(Center, 35, false)) {
                                loc.getBlock().setType(Material.AIR);
                                for (Player p : Loc.getNearbyPlayers(loc, 0.85)) {
                                    Main.getInstance().getDeathManager().KillHandler(p, player);
                                }
                            }
                            GameListener.SendToEveryone("§4§lL'art est explosion !");
                            cancel();
                            return;
                        }
                        i--;
                    }
                }.runTaskTimer(Main.getInstance(), 0, 20);
                return false;
            }
        }
        private static final class ArcExplosif extends ItemPower implements Listener {

            private final BakutonItem bakutonItem;

            public ArcExplosif(@NonNull RoleBase role, BakutonItem bakutonItem) {
                super("§cArc Explosif§r", new Cooldown(1), new ItemBuilder(Material.BOW).setName("§cArc Explosif").addEnchant(Enchantment.ARROW_DAMAGE, Main.getInstance().getGameConfig().getStuffConfig().getPower()+1), role);
                this.bakutonItem = bakutonItem;
                setSendCooldown(false);
                setWorkWhenInCooldown(true);
                getShowCdRunnable().setCustomText(true);
                EventUtils.registerRoleEvent(this);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                return true;
            }

            @Override
            public void tryUpdateActionBar() {
                getShowCdRunnable().setCustomTexte("§fTechnique§6 Bakuton§f actuel§7: "+this.bakutonItem.mode.getItem().getItemMeta().getDisplayName() + "§7 | " +
                        (!this.bakutonItem.cd1.isInCooldown() ? "§cC1§7:§a ✔" : "§cC1§7:§c "+ StringUtils.secondsTowardsBeautiful(this.bakutonItem.cd1.getCooldownRemaining()))
                        + "§7 | " +
                        (!this.bakutonItem.cd2.isInCooldown() ? "§cC2§7:§a ✔" : "§cC2§7:§c "+StringUtils.secondsTowardsBeautiful(this.bakutonItem.cd2.getCooldownRemaining()))
                        + "§7 | " +
                        (!this.bakutonItem.cd3.isInCooldown() ? "§cC3§7:§a ✔" : "§cC3§7:§c "+StringUtils.secondsTowardsBeautiful(this.bakutonItem.cd3.getCooldownRemaining()))
                        + "§7 | " +
                        (!this.bakutonItem.cd4.isInCooldown() ? "§cC4§7:§a ✔" : "§cC4§7:§c "+StringUtils.secondsTowardsBeautiful(this.bakutonItem.cd4.getCooldownRemaining()))
                );
            }
            @EventHandler
            private void onProjectileLaunch(@NonNull final ProjectileLaunchEvent event) {
                if (event.getEntity().getShooter() instanceof Player && ((Player) event.getEntity().getShooter()).getUniqueId().equals(getRole().getPlayer()) && event.getEntity() instanceof Arrow) {
                    if (((Player) event.getEntity().getShooter()).getItemInHand().equals(getItem())) {
                        event.getEntity().setMetadata(bakutonItem.mode.name(), new FixedMetadataValue(getPlugin(), bakutonItem.mode));
                    }
                }
            }
            @EventHandler
            private void onProjectileHit(@NonNull final ProjectileHitEvent event) {
                if (event.getEntity() instanceof Arrow && event.getEntity().getShooter() instanceof Player && ((Player) event.getEntity().getShooter()).getUniqueId().equals(getRole().getPlayer())) {
                    if (!checkUse((Player) event.getEntity().getShooter(), new HashMap<>()))return;
                    if (event.getEntity().hasMetadata("C1") && !this.bakutonItem.cd1.isInCooldown()) {
                        for (@NonNull final Player p : Loc.getNearbyPlayers(event.getEntity(), 5)) {
                            if (!p.getUniqueId().equals(getRole().getPlayer())) {
                                MathUtil.sendParticle(EnumParticle.EXPLOSION_LARGE, p.getLocation());
                                if (p.getHealth() - 2 <= 0.0) {
                                    p.damage(800.0, (Entity) event.getEntity().getShooter());
                                } else {
                                    p.damage(0.0, (Entity) event.getEntity().getShooter());
                                    p.setHealth(p.getHealth()-2.0);
                                }
                                p.sendMessage("§7Vous avez été touché par le§c C1§7 de§c Deidara");
                                getRole().getGamePlayer().sendMessage(p.getDisplayName()+"§7 à subit les dégats de votre§c C1");
                            }
                            p.playSound(p.getLocation(), Sound.EXPLODE, 1, 8);
                        }
                        MathUtil.sendParticle(EnumParticle.EXPLOSION_NORMAL, event.getEntity().getLocation());
                        event.getEntity().removeMetadata("C1", Main.getInstance());
                        this.bakutonItem.cd1.use();
                    } else if (event.getEntity().hasMetadata("C2") && !this.bakutonItem.cd2.isInCooldown()) {
                        final TNTPrimed tnt = (TNTPrimed) event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.PRIMED_TNT);
                        tnt.setFuseTicks(50);
                        tnt.setMetadata("DeidaraC2"+getRole().StringID, new FixedMetadataValue(Main.getInstance(), event.getEntity().getShooter()));
                        ((Player) event.getEntity().getShooter()).setAllowFlight(true);
                        ((Player) event.getEntity().getShooter()).setFlying(true);
                        new BukkitRunnable() {
                            int i = 12;
                            @Override
                            public void run() {
                                i--;
                                if (!GameState.inGame() || !GameState.getInstance().getInGamePlayers().contains(getRole().getPlayer()) || i ==0) {
                                    ((Player) event.getEntity().getShooter()).setFlying(false);
                                    ((Player) event.getEntity().getShooter()).setAllowFlight(false);
                                    ((Player)event.getEntity().getShooter()).setFallDistance(0.0f);
                                    cancel();
                                    return;
                                }
                                TNTPrimed t = (TNTPrimed) event.getEntity().getWorld().spawnEntity(((Player)event.getEntity().getShooter()).getLocation(), EntityType.PRIMED_TNT);
                                t.setFuseTicks(100);
                                t.setMetadata("DeidaraC2"+getRole().StringID, new FixedMetadataValue(Main.getInstance(), event.getEntity().getShooter()));
                            }
                        }.runTaskTimer(Main.getInstance(), 0, 20);
                        event.getEntity().removeMetadata("C2", Main.getInstance());
                        this.bakutonItem.cd2.use();
                    } else if (event.getEntity().hasMetadata("C3") && !this.bakutonItem.cd3.isInCooldown()) {
                        event.getEntity().removeMetadata("C3", Main.getInstance());
                        Location goodY = new Location(event.getEntity().getWorld(), event.getEntity().getLocation().getX(), event.getEntity().getWorld().getHighestBlockYAt(event.getEntity().getLocation())+15, event.getEntity().getLocation().getZ());
                        int sizeExplo = 10;
                        Location quarterB = new Location(goodY.getWorld(), goodY.getX()-sizeExplo, goodY.getY(), goodY.getZ()-sizeExplo);
                        Location quarterD = new Location(goodY.getWorld(), goodY.getX()+sizeExplo, goodY.getY(), goodY.getZ()+sizeExplo);
                        for (int x = quarterB.getBlockX(); x <= quarterD.getBlockX(); x+=3) {
                            for (int z = quarterB.getBlockZ(); z <= quarterD.getBlockZ(); z+=3) {
                                Location loc = new Location(goodY.getWorld(), x, goodY.getY(), z);
                                TNTPrimed tnt = (TNTPrimed) loc.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
                                tnt.setFuseTicks(90);
                                tnt.setCustomNameVisible(true);
                                tnt.setCustomName("§cC3 de Deidara");
                            }
                        }
                        this.bakutonItem.cd3.use();
                    } else if (event.getEntity().hasMetadata("C4") && !this.bakutonItem.cd4.isInCooldown()) {
                        event.getEntity().removeMetadata("C4", Main.getInstance());
                        new BukkitRunnable() {
                            int i = 15;
                            @SuppressWarnings("deprecation")
                            @Override
                            public void run() {
                                if (i <= 0) {
                                    getRole().getGamePlayer().sendMessage("§7Votre zone ne fais plus d'effet");
                                    cancel();
                                }
                                for (Player p : Loc.getNearbyPlayersExcept(event.getEntity(), 25, (Player) event.getEntity().getShooter())) {
                                    p.damage(0.0);
                                    if (p.getHealth() - 1.0 <= 0.0) {
                                        p.damage(800.0, (Entity) event.getEntity().getShooter());
                                    } else {
                                        p.damage(0.0, (Entity) event.getEntity().getShooter());
                                        p.setHealth(p.getHealth()-1.0);
                                    }
                                    p.resetTitle();
                                    p.sendTitle("§7Vous subissez les effets du§c C4", "§7de §cDeidara");
                                    getRole().getGamePlayer().sendMessage("§c"+p.getDisplayName()+"§7 à subit les dégats de votre§c C4");
                                }
                                i--;
                                MathUtil.sendCircleParticle(EnumParticle.BARRIER, event.getEntity().getLocation(), 25, 100);
                            }
                        }.runTaskTimer(Main.getInstance(), 0, 20);
                        this.bakutonItem.cd4.use();
                    }
                }
            }
            @EventHandler
            private void onDamage(@NonNull final EntityDamageByEntityEvent event) {
                if (event.getDamager() instanceof TNTPrimed) {
                    TNTPrimed tnt = (TNTPrimed) event.getDamager();
                    if (tnt.hasMetadata("DeidaraC2"+getRole().StringID)) {
                        if (event.getEntity().getUniqueId() != event.getDamager().getUniqueId()) {
                            event.setDamage(0);
                            if (event.getEntity() instanceof Player) {
                                final Player victim = (Player) event.getEntity();
                                if (victim.getHealth() - 4 <= 0.0) {
                                    victim.damage(800.0, event.getDamager());
                                } else {
                                    victim.damage(0.0, event.getDamager());
                                    victim.setHealth(victim.getHealth()-4.0);
                                }
                            }
                        }
                    }
                    if (event.getDamager().getUniqueId().equals(event.getDamager().getUniqueId())) {
                        event.setDamage(0.0);
                    }
                }
            }
        }
    }
}