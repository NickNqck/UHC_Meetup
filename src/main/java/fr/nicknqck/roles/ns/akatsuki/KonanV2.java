package fr.nicknqck.roles.ns.akatsuki;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.ChiefAkatsukiRoles;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.fastinv.FastInv;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.particles.WingsEffect;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class KonanV2 extends ChiefAkatsukiRoles {

    public KonanV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.INTELLIGENT;
    }

    @Override
    public String getName() {
        return "Konan";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Konan;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        setNoFall(true);
        setChakraType(Chakras.SUITON);
        addPower(new VolDeCombat(this), true);
        addPower(new DanceDuShikigami(this), true);
        super.RoleGiven(gameState);
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }
    private static class DanceDuShikigami extends ItemPower {

        private final FuiteAnticipePower fuiteAnticipePower;
        private final LancePower lancePower;
        private final ParcheminsExplosifs parcheminsExplosifs;

        public DanceDuShikigami(@NonNull RoleBase role) {
            super("§cDance du Shikigami§r", new Cooldown(1), new ItemBuilder(Material.NETHER_STAR).setName("§cDance du Shikigami"), role,
                    "§7Ouvre un menu vous offrant§c plusieurs choix§7:",
                    "",
                    "§8 -§c Fuite anticiper§7: Permet de poser un§c point de téléportation§7 puis de s'y§c re-téléporter§7 en rappuyant dans le menu.",
                    "§7§o(1x/8m)",
                    "",
                    "§8 -§c Lance§7: Pendant§c 60 secondes§7, vous offre§c 20%§7 de§c force§7 (§c+2%§7 par§c 1/2❤§7 manquant).",
                    "§7§o(1x/5m)",
                    "",
                    "§8 -§c Parchemins explosifs§7: Pose un§c parchemin§7 à votre position, si vous faite§f shift + clique§7 celà fera exploser§c tout§7",
                    "§7les§c parchemins explosifs§7 que vous aviez précédemment poser, infligeant donc§c 1,5❤§7 au joueurs toucher.",
                    "§7§o(1x/3m)"
            );
            setWorkWhenInCooldown(true);
            setSendCooldown(false);
            this.fuiteAnticipePower = new FuiteAnticipePower(role);
            this.lancePower = new LancePower(role);
            this.parcheminsExplosifs = new ParcheminsExplosifs(role);
            role.addPower(fuiteAnticipePower);
            role.addPower(lancePower);
            role.addPower(parcheminsExplosifs);
            getShowCdRunnable().setCustomText(true);
            setShowCdInDesc(false);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                @NonNull final FastInv inv = new FastInv(27, "§cDance du Shikigami");
                inv.setItems(inv.getCorners(), new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").setDurability(7).toItemStack());
                inv.setItem(11, new ItemBuilder(Material.ENDER_PEARL).setName(this.fuiteAnticipePower.getName()).setLore(this.fuiteAnticipePower.getDescriptions()).toItemStack(),
                        event -> this.fuiteAnticipePower.checkUse((Player) event.getWhoClicked(), new HashMap<>()));
                inv.setItem(13, new ItemBuilder(Material.DIAMOND_SWORD).setName(this.lancePower.getName()).setLore(this.lancePower.getDescriptions()).toItemStack(),
                        event -> this.lancePower.checkUse((Player) event.getWhoClicked(), new HashMap<>()));
                inv.setItem(15, new ItemBuilder(Material.PAPER).setName(this.parcheminsExplosifs.getName()).setLore(this.parcheminsExplosifs.getDescriptions()).toItemStack(),
                        event -> {
                    if (event.isShiftClick()) {
                        player.sendMessage("try");
                        final Map<String, Object> uwu = new HashMap<>();
                        uwu.put("activate", "activate");
                        this.parcheminsExplosifs.checkUse((Player) event.getWhoClicked(), uwu);
                        return;
                    }
                    player.sendMessage("ok");
                    this.parcheminsExplosifs.checkUse((Player) event.getWhoClicked(), new HashMap<>());
                        });
                inv.open(player);
                return true;
            }
            return false;
        }

        @Override
        public void tryUpdateActionBar() {
            getShowCdRunnable().setCustomText(true);
            getShowCdRunnable().setCustomTexte(
                    this.fuiteAnticipePower.getName() + (!this.fuiteAnticipePower.getCooldown().isInCooldown() ? "§7 est§c utilisable" : "§7 est en§b cooldown§7:§c "+StringUtils.secondsTowardsBeautiful(this.fuiteAnticipePower.getCooldown().getCooldownRemaining()))
                    + "§7 | "
                    + this.lancePower.getName() + (!this.lancePower.getCooldown().isInCooldown() ? "§7 est§c utilisable" : "§7 est en§b cooldown§7:§c "+StringUtils.secondsTowardsBeautiful(this.lancePower.getCooldown().getCooldownRemaining()))
                    + "§7 | "
                    + this.parcheminsExplosifs.getName() + (!this.parcheminsExplosifs.getCooldown().isInCooldown() ? "§7 est§c utilisable" : "§7 est en§b cooldown§7:§c "+StringUtils.secondsTowardsBeautiful(this.parcheminsExplosifs.getCooldown().getCooldownRemaining()))
            );
        }

        private static class FuiteAnticipePower extends Power {

            private Location location;

            public FuiteAnticipePower(@NonNull RoleBase role) {
                super("§cFuite anticipé§r", new Cooldown(60*8), role,
                        "§7Vous permet de placer un§c point de téléportation§7.",
                        "",
                        "§7Une fois poser, vous pourrez§c réutiliser§7 ce§c pouvoir§7 pour vous§a téléporter§7 a l'endroit définie.",
                        "§7§o(Une fois que vous vous§a téléportez§7§o, le§c point§7§o§c supprimer§7§o)",
                        "",
                        "§bCooldown§7:§c 8 minutes");
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                player.closeInventory();
                if (location == null) {
                    player.sendMessage("§7Vous avez§c enregistré§7 votre position pour§c plus tard§7.");
                    location = player.getLocation();
                    return false;
                }
                player.sendMessage("§7Vous vous§c téléportez§7 a la position§c enregistrer§7.");
                player.teleport(location);
                location = null;
                return true;
            }
        }
        private static class LancePower extends Power implements Listener {

            private boolean active = false;

            public LancePower(@NonNull RoleBase role) {
                super("§cLance§r", new Cooldown(60*5), role,
                        "§7Pendant§c 60 secondes§7 vos coups infligeront§c 20%§7 (§c+2%§7 par§c 1/2❤ en moins§7).",
                        "",
                        "§bCooldown§7:§c 5 minutes");
                EventUtils.registerRoleEvent(this);
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                new LanceRunnable(this).runTaskTimerAsynchronously(getPlugin(), 0, 20);
                player.sendMessage("§7Pendant les§c 60 prochaines secondes§7 vous aurez un§c bonus de force§7.");
                player.closeInventory();
                return true;
            }
            @EventHandler
            private void onDamage(EntityDamageByEntityEvent event) {
                if (!(event.getDamager() instanceof Player)) return;
                Player player = (Player) event.getDamager();

                // Vérifier la metadata
                if (!active) return;
                if (!player.getUniqueId().equals(getRole().getPlayer()))return;
                double baseDamage = event.getDamage();

                // PV actuels
                double finalDamage = getFinalDamage(player, baseDamage);

                event.setDamage(finalDamage);
            }

            private static double getFinalDamage(Player player, double baseDamage) {
                double health = player.getHealth();
                double maxHealth = player.getMaxHealth(); // 20.0 la plupart du temps

                // Cœurs manquants → chaque cœur = 2 HP → demi-cœur = 1 HP
                double missingHP = maxHealth - health;

                // Chaque demi-cœur (1 HP) → +2%
                double bonusPerHalfHeart = 0.02 * missingHP;

                // Bonus fixe 20% → 0.20
                double fixedBonus = 0.20;

                double finalMultiplier = 1.0 + fixedBonus + bonusPerHalfHeart;
                return baseDamage * finalMultiplier;
            }
            private static class LanceRunnable extends BukkitRunnable {

                private final LancePower lancePower;
                private int timeLeft = 60;

                private LanceRunnable(LancePower lancePower) {
                    this.lancePower = lancePower;
                }

                @Override
                public void run() {
                    if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                        cancel();
                        return;
                    }
                    if (this.timeLeft <= 0) {
                        this.lancePower.getRole().getGamePlayer().getActionBarManager().removeInActionBar("konan.lance");
                        lancePower.active = false;
                        this.lancePower.getRole().getGamePlayer().sendMessage("§7Vous n'êtes plus sous l'effet de la§c Lance§7.");
                        cancel();
                        return;
                    }
                    this.lancePower.getRole().getGamePlayer().getActionBarManager().updateActionBar("konan.lance", "§bTemps restant (§cLance§b):§c "+ StringUtils.secondsTowardsBeautiful(timeLeft));
                    this.timeLeft--;
                }
            }
        }
        private static class ParcheminsExplosifs extends Power {

            private final List<Location> locations = new ArrayList<>();

            public ParcheminsExplosifs(@NonNull RoleBase role) {
                super("§cParchemins explosif§r", new Cooldown(60*3), role,
                        "§7Vous permet de poser un§c parchemin§7 la ou vous êtes§7. (1x/3m)",
                        "",
                        "§7Dans le menu, en faisant un§f shift + clique§7, déclenche des explosions partout la ou vous aviez poser",
                        "§7des§c parchemins explosif§7, les joueurs touchés perdront§c 1,5❤§7 par§c explosion§7.",
                        "",
                        "§7§o(Aucun joueur ne peut mourir de vos explosions).",
                        "",
                        "§bCooldown§7:§c 3 minutes");
                setShowInDesc(false);
                setWorkWhenInCooldown(true);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                player.closeInventory();
                if (map.containsKey("activate")) {
                    if (locations.isEmpty()) {
                        player.sendMessage("§cVous devez d'abord poser des parchemins !");
                        return false;
                    }
                    int target = 0;
                    for (@NonNull final Location location : locations) {
                        player.sendMessage(location.toString());
                        MathUtil.sendCircleParticle(EnumParticle.EXPLOSION_LARGE, location, 1, 16);
                        for (@NonNull final Entity entity : location.getWorld().getEntities()) {
                            if (!(entity instanceof Player))continue;
                            if (entity.getLocation().distance(location) > 2.0)continue;
                            ((Player) entity).setHealth(Math.max(1.0, ((Player) entity).getHealth()-3.0));
                            MathUtil.sendParticle(EnumParticle.EXPLOSION_HUGE, entity.getLocation());
                            target++;
                            entity.sendMessage("§7Vous avez été toucher par un§c parchemin explosif§7.");
                        }
                    }
                    this.locations.clear();
                    player.sendMessage("§7Vous avez touché§c "+target+" cible(s)");
                    return true;
                }
                if (getCooldown().isInCooldown()) {
                    sendCooldown(player);
                    return false;
                }
                if (add(player.getLocation())) {
                    locations.add(player.getLocation());
                    player.sendMessage("§7Vous avez posé un§c Parchemin explosif§7 à votre position.");
                    return true;
                } else {
                    player.sendMessage("§cImpossible de mettre un parchemin aussi proche d'un autre.");
                    return false;
                }
            }
            private boolean add(@NonNull final Location location) {
                if (!this.locations.isEmpty()) {
                    for (@NonNull final Location loc : this.locations) {
                        if (!loc.getWorld().equals(location.getWorld()))continue;
                        if (loc.distance(location) < 2.5) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
    }
    private static class VolDeCombat extends ItemPower {

        public VolDeCombat(@NonNull RoleBase role) {
            super("§aVol de combat§r", new Cooldown(60*6), new ItemBuilder(Material.NETHER_STAR).setName("§aVol de combat"), role,
                    "§7Active un§a fly§7 d'une durée de§c 8 secondes§7.");

        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                player.setAllowFlight(true);
                player.setFlying(true);
                new WingsEffect(20*8, EnumParticle.FLAME).start(player);
                new BukkitRunnable() {
                    int i = 0;
                    @Override
                    public void run() {
                        i++;
                        if (i == 8) {
                            player.setFlying(false);
                            player.setAllowFlight(false);
                            cancel();
                        }
                    }
                }.runTaskTimer(Main.getInstance(), 0, 20);
                return true;
            }
            return false;
        }
    }
}