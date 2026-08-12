package fr.nicknqck.roles.valo.agents;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.EffectWhen;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.death.FinalDeathEvent;
import fr.nicknqck.interfaces.IRoles;
import fr.nicknqck.interfaces.ITeam;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.fastinv.PaginatedFastInv;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.util.*;

public final class Sage extends ValoBase {

    private SageItem sageItem = null;
    private SageTeam sageTeam = null;
    private SageUltime sageUltime = null;

    public Sage(UUID player) {
        super(player);
    }

    @Override
    public @NonNull ValoItemPower getValoItemPower() {
        if (this.sageItem == null) {
            this.sageItem = new SageItem(this);
        }
        return this.sageItem;
    }

    @Override
    public @NonNull ItemPower getUltime() {
        if (this.sageUltime == null) {
            this.sageUltime = new SageUltime(this);
        }
        return this.sageUltime;
    }

    @Override
    public String getName() {
        return "Sage";
    }

    @Override
    public @NonNull IRoles<?> getRoles() {
        return Roles.Sage;
    }

    @Override
    public @NonNull ITeam getOriginTeam() {
        if (this.sageTeam == null) {
            this.sageTeam = new SageTeam();
        }
        return this.sageTeam;
    }

    private static final class SageItem extends ValoItemPower {

        private final SlowPower slowPower;
        private final HealPower healPower;

        public SageItem(@NonNull RoleBase role) {
            super("§aSage", new ItemBuilder(Material.NETHER_STAR).setName("§aSage"), role,
                    "§7Effectue différentes actions en fonction du clique:",
                    "",
                    AllDesc.point+"§fClique gauche§7: Envoie une§f boule de neige§7 qui à l'impacte, créé une§c zone§7 de§c 10 blocs§7 pendant§c 10 secondes§7,",
                    "§7les joueurs à l'intérieur ont l'effet§c Slowness III§7. (vous aussi) (2x/partie) (1x/10s)",
                    "",
                    AllDesc.point+"§fClique droit§7: Vous§a soigne complètement§7. (1x/45s)"
            );
            this.slowPower = new SlowPower(role);
            this.healPower = new HealPower(role);
        }

        @Nonnull
        @Override
        public Power getLeftClickPower() {
            return this.slowPower;
        }

        @Nonnull
        @Override
        public Power getRightClickPower() {
            return this.healPower;
        }

        private static final class HealPower extends Power {

            public HealPower(@NonNull RoleBase role) {
                super("§aHeal", new Cooldown(45), role);
                setShowInDesc(false);
                role.addPower(this);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                player.setHealth(player.getMaxHealth());
                player.sendMessage("§7Vous avez été§a soigner§7 par§a Sage§7.");
                return true;
            }
        }
        private static final class SlowPower extends Power implements Listener {

            public SlowPower(@NonNull RoleBase role) {
                super("§bSlow", new Cooldown(10), role);
                EventUtils.registerRoleEvent(this);
                setShowInDesc(false);
                role.addPower(this);
                setMaxUse(2);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                final Snowball snowball = player.launchProjectile(Snowball.class);
                snowball.setCustomNameVisible(true);
                snowball.setCustomName(getName() + " §c" + getUse()+1+"§7/§a"+getMaxUse());
                snowball.setBounce(false);
                snowball.setVelocity(player.getLocation().getDirection().normalize().multiply(5));
                player.playSound(player.getEyeLocation(), Sound.DIG_SNOW, 8, 1);
                snowball.setMetadata("sage.slow"+getCooldown().getUniqueId(), new FixedMetadataValue(getPlugin(), snowball.getUniqueId()));
                player.sendMessage("§7Vous avez lancer une "+getName());
                return true;
            }
            @EventHandler(priority = EventPriority.NORMAL)
            public void onHit(@NonNull final ProjectileHitEvent event) {
                if (!(event.getEntity() instanceof Snowball))return;
                final Snowball snowball = (Snowball) event.getEntity();
                if (!snowball.hasMetadata("sage.slow"+getCooldown().getUniqueId()))return;
                new SlowRunnable(this, event.getEntity().getLocation());

            }
            private static final class SlowRunnable extends BukkitRunnable {

                private final SlowPower power;
                private int timeLeftZone = 10;
                private final Location center;

                private SlowRunnable(SlowPower power, Location center) {
                    this.power = power;
                    this.center = center;
                    runTaskTimerAsynchronously(Main.getInstance(), 1, 20);
                }

                @Override
                public void run() {
                    if (!GameState.inGame()) {
                        cancel();
                        return;
                    }
                    if (this.timeLeftZone <= 0) {
                        this.power.getRole().getGamePlayer().getActionBarManager().removeInActionBar("sage.slow"+this.power.getCooldown().getUniqueId()+center.getZ());
                        cancel();
                        return;
                    }
                    this.power.getRole().getGamePlayer().getActionBarManager().updateActionBar("sage.slow"+this.power.getCooldown().getUniqueId()+center.getZ(), this.power.getName()+"§7 (§cTemps restant§7):§c "+ StringUtils.secondsTowardsBeautiful(this.timeLeftZone));
                    MathUtil.sendCircleParticle(EnumParticle.SNOWBALL, center, 12.0, 24);
                    this.power.getPlugin().getServer().getScheduler().runTask(this.power.getPlugin(), () -> {
                        for (final GamePlayer nearbyGamePlayer : Loc.getNearbyGamePlayers(this.center, 12.0)) {
                            if (nearbyGamePlayer.getRole() == null)continue;
                            nearbyGamePlayer.getRole().givePotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 2, false, false), EffectWhen.NOW);
                        }
                    });
                    this.timeLeftZone--;
                }
            }
        }
    }
    @Getter
    private static final class SageTeam implements ITeam {

        private final java.util.List<Player> list;
        private final String Color;
        private final String name;
        private final String mdj;
        private final boolean solo;

        private SageTeam() {
            this.list = new ArrayList<>();
            Color = "§b";
            this.name = "§bSage";
            this.mdj = "valo";
            this.solo = false;
        }

        @Override
        public List<Player> getList() {
            return this.list;
        }

        @Override
        public String getColor() {
            return this.Color;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getMdj() {
            return this.mdj;
        }

        @Override
        public boolean isSolo() {
            return this.solo;
        }

        @Override
        public void addPlayer(Player player) {
            getList().add(player);
        }

        @Override
        public String name() {
            return "Sage";
        }
    }
    private static final class SageUltime extends ItemPower implements Listener {

        private final Map<GamePlayer, Location> deathMap;
        private int points = 1;

        public SageUltime(@NonNull RoleBase role) {
            super("§a§lRésurrection", null, new ItemBuilder(Material.NETHER_STAR).setName("§a§lRésurrection"), role,
                    "§7Ouvre un menu, à l'intérieur est présent tout les joueurs§c mort§7 dans un rayon de§c 50 blocs§7,",
                    "§7en cliquant sur l'un d'eux vous le§a ramènerez à la vie§7.",
                    "",
                    "§7Par défaut vous avez§c 1 point§7, en tuant un joueur vous en gagnez§c 1§7.",
                    "§aRamener à la vie§7 vous coutera§c tout vos points§7."
            );
            this.deathMap = new HashMap<>();
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (this.points < 1) {
                    player.sendMessage("§cVous n'avez pas assez de point pour utiliser votre "+getName());
                    return false;
                }
                final PaginatedFastInv inv = new PaginatedFastInv(27, getName());
                inv.setItems(inv.getCorners(), new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").toItemStack());
                final List<Integer> list = new ArrayList<>();
                list.add(10);
                list.add(11);
                list.add(12);
                list.add(14);
                list.add(15);
                list.add(16);
                inv.setContentSlots(list);
                inv.previousPageItem(3, p -> new ItemBuilder(Material.ARROW).setName("§fPage " + p + "/" + inv.lastPage()).toItemStack());

                inv.nextPageItem(5, p -> new ItemBuilder(Material.ARROW).setName("§fPage " + p + "/" + inv.lastPage()).toItemStack());
                for (final GamePlayer gamePlayer : this.deathMap.keySet()) {
                    if (gamePlayer.getPlayer() == null)continue;
                    if (gamePlayer.getRole() == null)continue;
                    if (gamePlayer.isAlive())continue;
                    if (this.deathMap.get(gamePlayer).distance(player.getLocation()) > 50.0)continue;
                    inv.addContent(new ItemBuilder(gamePlayer.getHeadItem()).setName("§a"+gamePlayer.getPlayerName()).toItemStack(), event -> {
                        event.getWhoClicked().closeInventory();
                        revive(GameState.getInstance(), gamePlayer.getPlayer(), (Player) event.getWhoClicked());
                    });
                }
                inv.open(player);
                return true;
            }
            return false;
        }
        @EventHandler(priority = EventPriority.NORMAL)
        public void onDeath(final FinalDeathEvent event) {
            final Location location = event.getPlayer().getLocation();
            if (!location.getWorld().getName().equalsIgnoreCase("arena")) {
                location.setWorld(Main.getInstance().getWorldManager().getGameWorld());
                location.setX(0.0);
                location.setZ(0.0);
                location.setY(location.getWorld().getHighestBlockYAt(location.getBlockX(), location.getBlockZ()));
            }
            GamePlayer gamePlayer = event.getRole().getGamePlayer();
            deathMap.put(gamePlayer, location);
            if (event.getEntityKiller() != null) {
                final GamePlayer owner = GamePlayer.of(event.getEntityKiller().getUniqueId());
                if (owner != null) {
                    this.points++;
                    owner.sendMessage("§7Vous avez gagner un§a point§7, vous êtes maintenant à§c "+this.points);
                }
            }
        }
        private void revive(final GameState gameState, final Player clicked, final Player owner) {
            this.points = 0;
            RoleBase role = gameState.getGamePlayer().get(clicked.getUniqueId()).getRole();
            owner.closeInventory();
            clicked.sendMessage("§7Vous avez été invoquée par l'§5Edo Tensei");
            owner.sendMessage("§5Edo Tensei !");
            role.setTeam(this.getRole().getTeam());
            role.setMaxHealth(20.0);
            clicked.getInventory().setContents(role.getGamePlayer().getLastInventoryContent());
            clicked.getInventory().setArmorContents(role.getGamePlayer().getLastArmorContent());
            gameState.RevivePlayer(clicked);
            this.getRole().setMaxHealth(this.getRole().getMaxHealth()-Main.getInstance().getGameConfig().getNarutoConfig().getEdoHealthRemove());
            owner.setMaxHealth(this.getRole().getMaxHealth());
            clicked.teleport(owner);
            final List<Power> copyPower = new ArrayList<>(role.getPowers());
            if (!copyPower.isEmpty()) {
                for (Power power : copyPower) {
                    if (power instanceof ItemPower) {
                        clicked.getInventory().removeItem(((ItemPower) power).getItem());
                    }
                    role.removePower(power);
                }
            }
            role.GiveItems();
            role.RoleGiven(this.getRole().gameState);
            clicked.resetTitle();
            clicked.sendTitle("§5Edo Tensei !", "Vous êtes maintenant dans le camp "+this.getRole().getTeam().getName());
        }
    }
}