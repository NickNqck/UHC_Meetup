package fr.nicknqck.roles.ns.solo;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.EffectGiveEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.events.custom.time.OnSecond;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.NSSoloRoles;
import fr.nicknqck.utils.*;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.TapisSableEffect;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GaaraV2 extends NSSoloRoles implements Listener{

    private int reserve = 128;
    private int divisor = 1;

    public GaaraV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.MOYENNE;
    }

    @Override
    public String getName() {
        return "Gaara";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Gaara;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Solo;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        setChakraType(Chakras.FUTON);
        addPower(new DefensePower(this), true);
        addPower(new AttackPower(this), true);
        addPower(new ShukakuPower(this), true);
        givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 0, false, false), EffectWhen.PERMANENT);
        EventUtils.registerRoleEvent(this);
        getGamePlayer().getActionBarManager().addToActionBar("gaara.sablecount", "§eSable(s): "+this.reserve);
        super.RoleGiven(gameState);
    }
    @EventHandler
    private void onSecond(final OnSecond onSecond) {
        this.reserve++;
        getGamePlayer().getActionBarManager().updateActionBar("gaara.sablecount", "§eSable(s): "+this.reserve);
    }
    @EventHandler
    private void onKill(final UHCPlayerKillEvent event) {
        if (event.getKiller().getUniqueId().equals(getPlayer())) {
            event.getKiller().sendMessage("§7Vous avez gagner§e 128 sables§7.");
            this.reserve+=128;
        }
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .addCustomLine("§7Vous commencez la partie avec§e 128 sables§7, toute les secondes vous en gagner§c 1§7.")
                .addCustomLine("§7En tuant un joueur, vous récupérez§e 128 sables§7.")
                .getText();
    }
    private static class ShukakuPower extends ItemPower implements Listener {

        private final GaaraV2 gaaraV2;
        private int timeLeft = -1;

        public ShukakuPower(@NonNull GaaraV2 role) {
            super("Shukaku", new Cooldown(60*20), new ItemBuilder(Material.INK_SACK).setDurability(11).setName("§eShukaku"), role,
                    "§7Pendant§c 5 minutes§7, vous donne l'effet§c Force I§7, également, vos technique utiliseront§c 2x moins§7 de§e sable§7. (1x/20m)");
            this.gaaraV2 = role;
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*60*5, 0, false, false), true);
                this.gaaraV2.divisor = 2;
                player.sendMessage("§7Vous activez§e Shukaku§7.");
                this.timeLeft = 60*5;
                this.gaaraV2.getGamePlayer().getActionBarManager().addToActionBar("gaara.shukaku", "§eShukaku:§c "+StringUtils.secondsTowardsBeautiful(this.timeLeft));
                return true;
            }
            return false;
        }
        @EventHandler
        private void onSecond(OnSecond onSecond) {
            if (this.timeLeft > 0) {
                this.gaaraV2.getGamePlayer().getActionBarManager().updateActionBar("gaara.shukaku", "§eShukaku:§c "+StringUtils.secondsTowardsBeautiful(this.timeLeft));
                this.timeLeft--;
            } else if (this.timeLeft == 0) {
                this.gaaraV2.getGamePlayer().getActionBarManager().removeInActionBar("gaara.shukaku");
                this.gaaraV2.getGamePlayer().sendMessage("§7Vous n'êtes plus sous l'effet de§e Shukaku§7.");
                this.timeLeft--;
            }
        }
    }
    private static class DefensePower extends ItemPower implements Listener {

        private final GaaraV2 gaaraV2;
        private final SuspensionPower suspensionPower;
        private final ArmurePower armurePower;
        private boolean tookDamage = false;
        private Defense defense = null;
        private boolean armure = false;

        public DefensePower(@NonNull GaaraV2 role) {
            super("Defense", new Cooldown(1), new ItemBuilder(Material.NETHER_STAR).setName("§eDéfense"), role,
                    "§7Ouvre un menu permettant d'équiper l'un des pouvoirs suivant: ",
                    "",
                    "§8 -§e Suspension du désert§7: Vous permet de vous§a envolez§7 pendant§c 20 secondes§7, ",
                            "§7si vous subissez des dégâts pendant le§a vol§7 vous arrêterez de voler. (Coût:§e 96 sables§7)",
                    "",
                    "§8 -§e Armure de sable§7: Vous permet d'activer un passif de§9 Résistance II§7 vous coûtant§c 5 sables§7 à chaque coût reçus. (Coût:§e 25 sables§7)",
                    "",
                    "§7Pour ouvrir le menu il vous faudra faire§c clique droit§7 et pour activer le pouvoir sélectionner il faudra faire un§a clique gauche");
            this.gaaraV2 = role;
            this.suspensionPower = new SuspensionPower(role, this);
            this.armurePower = new ArmurePower(this);
            EventUtils.registerRoleEvent(this);
            setShowCdInDesc(false);
            setSendCooldown(false);
            getShowCdRunnable().setCustomText(true);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                if (event.getAction().name().contains("RIGHT")){
                    openDefenseInventory(player);
                } else {
                    if (defense == null) {
                        player.sendMessage("§cIl faut d'abord équiper une§e Défense§c.");
                        return false;
                    }
                    if (defense.equals(Defense.SUSPENSION)) {
                        if (this.gaaraV2.reserve < 96) {
                            player.sendMessage("§cIl vous faut§6 96§e sable§c minimum pour utiliser cette technique !");
                            return false;
                        }
                        this.gaaraV2.reserve-=96/this.gaaraV2.divisor;
                        if (this.suspensionPower.checkUse(player, map)) {
                            return true;
                        }
                    } else if (defense.equals(Defense.ARMURE)) {
                        if (this.gaaraV2.reserve < 25) {
                            player.sendMessage("§cIl vous faut§6 25§e sable§c minimum pour utiliser cette technique");
                            return false;
                        }
                        if (this.armurePower.checkUse(player, map)) {
                            return true;
                        }
                    }
                }
                return true;
            }
            return false;
        }
        private void openDefenseInventory(HumanEntity humanEntity) {
            final Inventory inv = Bukkit.createInventory(humanEntity, 27, "§eDéfense");
            inv.setItem(12, new ItemBuilder(Material.FEATHER).setName("§eSuspension du désert").toItemStack());
            inv.setItem(14, new ItemBuilder(Material.IRON_CHESTPLATE).setName("§eArmure de Sable").toItemStack());
            humanEntity.openInventory(inv);
        }
        @EventHandler
        private void onInventoryClick(final InventoryClickEvent event) {
            if (event.getClickedInventory() == null)return;
            if (event.getClickedInventory().getTitle() == null)return;
            if (event.getClickedInventory().getTitle().isEmpty())return;
            if (event.getCurrentItem() == null)return;
            if (!event.getCurrentItem().hasItemMeta())return;
            if (!event.getCurrentItem().getItemMeta().hasDisplayName())return;
            if (!(event.getWhoClicked() instanceof Player))return;
            if (event.getClickedInventory().getTitle().equals("§eDéfense")) {
                final String name = event.getCurrentItem().getItemMeta().getDisplayName();
                event.setCancelled(true);
                if (name.equalsIgnoreCase("§eSuspension du désert")) {
                    this.defense = Defense.SUSPENSION;
                    event.getWhoClicked().closeInventory();
                } else if (name.equalsIgnoreCase("§eArmure de Sable")) {
                    this.defense = Defense.ARMURE;
                    event.getWhoClicked().closeInventory();
                }
            }
        }
        private enum Defense {
            SUSPENSION,
            ARMURE
        }
        @EventHandler
        private void onSecond(OnSecond onSecond) {
            if (this.armure) {
                if (this.gaaraV2.reserve < 5) {
                    this.gaaraV2.getGamePlayer().sendMessage("§cVotre§e Armure de Sable§c doit arrêter de fonctionner suite à votre manque de§e sable§c.");
                    this.armure = false;
                    return;
                }
                final Player player = Bukkit.getPlayer(getRole().getPlayer());
                if (player != null) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 1, false, false), true);
                }
            }
        }
        @EventHandler
        private void onDamage(EntityDamageEvent event) {
            if (event.getEntity().getUniqueId().equals(getRole().getPlayer())) {
                this.tookDamage = true;
            }
        }
        @EventHandler
        private void onDamageByEntity(EntityDamageByEntityEvent event) {
            if (event.getEntity().getUniqueId().equals(getRole().getPlayer()) && this.armure && event.getDamager() instanceof Player) {
                this.gaaraV2.reserve = Math.max(this.gaaraV2.reserve-5, 0);
                event.getEntity().sendMessage("§cVous avez perdu§e 5 sables§c.");
            }
        }
        @EventHandler
        private void potionEffectGiveEvent(final EffectGiveEvent event) {
            if (event.getRole().getPlayer().equals(getRole().getPlayer()) && event.getRole() instanceof GaaraV2 && event.getPotionEffect().getType().equals(PotionEffectType.DAMAGE_RESISTANCE)) {
                if (this.armure)event.setCancelled(true);
            }
        }

        @Override
        public void tryUpdateActionBar() {
            if (this.defense == null) {
                getShowCdRunnable().setCustomTexte("§cAucun pouvoir n'a été sélectionné");
            } else {
                if (this.defense.equals(Defense.ARMURE)) {
                    getShowCdRunnable().setCustomTexte("§eArmure de sable§7 est actuellement "+(this.armure ? "§aactivé" : "§cdésactivé"));
                } else if (this.defense.equals(Defense.SUSPENSION)) {
                    getShowCdRunnable().setCustomTexte("§eSuspension du désert§7 est équiper");
                }
            }
        }

        private static class SuspensionPower extends Power {

            private final DefensePower defensePower;

            public SuspensionPower(@NonNull GaaraV2 role, DefensePower defensePower) {
                super("§eSuspension du désert§7", null, role);
                this.defensePower = defensePower;
                setShowInDesc(false);
                role.addPower(this);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                this.defensePower.tookDamage = false;
                player.setAllowFlight(true);
                player.setFlying(true);
                player.setFlySpeed(0.1F);
                player.sendMessage("§7Vous pouvez désormais voler !");
                getRole().getGamePlayer().getActionBarManager().addToActionBar("gaara.suspension", "§7Vous pouvez voler pendant encore §c"+ StringUtils.secondsTowardsBeautiful(20));
                new SuspensionRunnable(this, player);
                return true;
            }
            private static class SuspensionRunnable extends BukkitRunnable {

                private final SuspensionPower suspensionPower;
                private final Player player;
                private final TapisSableEffect tapisSableEffect;
                private int timer = 20*20;

                private SuspensionRunnable(SuspensionPower suspensionPower, Player player) {
                    this.suspensionPower = suspensionPower;
                    this.player = player;
                    this.tapisSableEffect = new TapisSableEffect(20*20, EnumParticle.REDSTONE, 255, 183, 0);
                    runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
                    this.tapisSableEffect.start(player);
                }

                @Override
                public void run() {
                    if(player.getGameMode() != GameMode.SPECTATOR) {
                        this.suspensionPower.getRole().getGamePlayer().getActionBarManager().updateActionBar("gaara.suspension", "§7Vous pouvez voler pendant encore §c" + StringUtils.secondsTowardsBeautiful(timer/20));
                        if (timer == 0 || this.suspensionPower.defensePower.tookDamage){
                            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                                player.setFlying(false);
                                player.setAllowFlight(false);
                                this.tapisSableEffect.cancel();
                            });
                            this.suspensionPower.getRole().getGamePlayer().getActionBarManager().removeInActionBar("gaara.suspension");
                            player.sendMessage("§7Vous ne pouvez plus voler");
                            cancel();
                        }
                        timer--;
                    } else {
                        cancel();
                    }
                }
            }
        }
        private static class ArmurePower extends Power {

            private final DefensePower defensePower;

            public ArmurePower(@NonNull DefensePower defensePower) {
                super("Armure de sable", null, defensePower.getRole());
                this.defensePower = defensePower;
                setShowInDesc(false);
                defensePower.getRole().addPower(this);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                this.defensePower.armure = !this.defensePower.armure;
                if (this.defensePower.armure){
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 1, false, false), true);
                    player.sendMessage("§7Vous avez§a activé§7 votre§e "+getName());
                    this.defensePower.gaaraV2.reserve-=25/this.defensePower.gaaraV2.divisor;
                } else {
                    player.sendMessage("§7Vous avez§c désactivé§7 votre§e "+getName());
                    this.defensePower.gaaraV2.reserve+=25;
                }
                return true;
            }
        }
    }
    private static class AttackPower extends ItemPower implements Listener {

        private final GaaraV2 gaaraV2;
        private Attaque attaque = null;
        private final TsunamiPower tsunamiPower;
        private final SarcophagePower sarcophagePower;
        private final LancePower lancePower;

        public AttackPower(@NonNull GaaraV2 role) {
            super("Attaque", new Cooldown(1), new ItemBuilder(Material.NETHER_STAR).setName("§eAttaque"), role,
                    "§7Ouvre un menu permettant d'équiper l'un des pouvoirs suivant: ",
                    "",
                    "§8 -§e Tsunami de sable§7: Crée une§e vague de sable§7 devant vous propulsant les joueurs touchés et leurs infligeant§c 1❤§7 de§c dégâts§7. (Coût:§e 30 sables§7)",
                    "",
                    "§8 -§e Sarcophage de sable§7: Crée un§e sarcophage§7 composé de§e sable§7 sur le joueur visé. (Coût:§e 45 sables§7)",
                    "",
                    "§8 -§cLance§7: Fabrique une épée en diamant enchantée§c tranchant 4§7 ayant§c 30 durabilité§7. (Coût§7:§e 64 sables§7)",
                    "",
                    "§7Pour ouvrir le menu il vous faudra faire§c clique droit§7 et pour activer le pouvoir sélectionner il faudra faire un§a clique gauche");
            this.gaaraV2 = role;
            this.tsunamiPower = new TsunamiPower(this);
            this.sarcophagePower = new SarcophagePower(this);
            this.lancePower = new LancePower(this);
            setShowCdInDesc(false);
            setSendCooldown(false);
            EventUtils.registerRoleEvent(this);
            getShowCdRunnable().setCustomText(true);
        }

        private enum Attaque {
            TSUNAMI,
            SARCOPHAGE,
            LANCE
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                if (event.getAction().name().contains("RIGHT")) {
                    openAttaqueInventory(player);
                    return true;
                } else {
                    if (this.attaque == null) {
                        player.sendMessage("§cIl faut d'abord équiper une§e Attaque§c.");
                        return true;
                    }
                    if (this.attaque.equals(Attaque.TSUNAMI)) {
                        if (this.gaaraV2.reserve < 30) {
                            player.sendMessage("§cIl vous faut§6 30§e sable§c minimum pour utiliser cette technique !");
                            return false;
                        }
                        this.gaaraV2.reserve-=30/this.gaaraV2.divisor;
                        return this.tsunamiPower.checkUse(player, map);
                    } else if (this.attaque.equals(Attaque.SARCOPHAGE)) {
                        if (this.gaaraV2.reserve < 45) {
                            player.sendMessage("§cIl vous faut§6 45§e sable§c minimum pour utiliser cette technique !");
                            return false;
                        }
                        return this.sarcophagePower.checkUse(player, map);

                    } else if (this.attaque.equals(Attaque.LANCE)) {
                        if (this.gaaraV2.reserve < 64) {
                            player.sendMessage("§cIl vous faut§6 64§e sable§c minimum pour utiliser cette technique !");
                            return false;
                        }
                        this.gaaraV2.reserve-=64/this.gaaraV2.divisor;
                        return this.lancePower.checkUse(player, map);
                    }
                }
            }
            return false;
        }
        @EventHandler
        private void onInventoryClick(final InventoryClickEvent event) {
            if (event.getClickedInventory() == null)return;
            if (event.getClickedInventory().getTitle() == null)return;
            if (event.getClickedInventory().getTitle().isEmpty())return;
            if (event.getCurrentItem() == null)return;
            if (!event.getCurrentItem().hasItemMeta())return;
            if (!event.getCurrentItem().getItemMeta().hasDisplayName())return;
            if (!(event.getWhoClicked() instanceof Player))return;
            if (event.getClickedInventory().getTitle().equals("§eAttaque")) {
                final String name = event.getCurrentItem().getItemMeta().getDisplayName();
                event.setCancelled(true);
                switch (name) {
                    case "§eTsunami":
                        this.attaque = Attaque.TSUNAMI;
                        event.getWhoClicked().sendMessage("§7Vous avez équiper le pouvoir§e Tsunami§7.");
                        break;
                    case "§eSarcophage de sable":
                        this.attaque = Attaque.SARCOPHAGE;
                        event.getWhoClicked().sendMessage("§7Vous avez équiper le pouvoir§e Sarcophage de sable§7.");
                        break;
                    case "§cLance":
                        this.attaque = Attaque.LANCE;
                        event.getWhoClicked().sendMessage("§7Vous avez équiper le pouvoir§c Lance§7.");
                        break;
                }
                event.getWhoClicked().closeInventory();
            }
        }
        private void openAttaqueInventory(final Player player) {
            final Inventory inv = Bukkit.createInventory(player, 27, "§eAttaque");
            inv.setItem(11, new ItemBuilder(Material.FEATHER).setName("§eTsunami").toItemStack());
            inv.setItem(13, new ItemBuilder(Material.SANDSTONE).setName("§eSarcophage de sable").toItemStack());
            inv.setItem(15, new ItemBuilder(Material.DIAMOND_SWORD).setName("§cLance").toItemStack());
            player.openInventory(inv);
        }

        @Override
        public void tryUpdateActionBar() {
            if (this.attaque == null) {
                getShowCdRunnable().setCustomTexte("§cAucun pouvoir n'a été sélectionné");
            } else {
                if (this.attaque.equals(Attaque.LANCE)) {
                    getShowCdRunnable().setCustomTexte("§cLance§7 est actuellement équiper");
                } else if (this.attaque.equals(Attaque.TSUNAMI)) {
                    getShowCdRunnable().setCustomTexte("§eTsunami de sable§7 est actuellement équiper");
                } else if (this.attaque.equals(Attaque.SARCOPHAGE)) {
                    getShowCdRunnable().setCustomTexte("§eSarcophage de sable§7 est actuellement équiper");
                }
            }
        }

        private static class TsunamiPower extends Power {

            public TsunamiPower(AttackPower attackPower) {
                super("Tsunami", null, attackPower.gaaraV2);
                setShowInDesc(false);
                attackPower.gaaraV2.addPower(this);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                useTsunami(player);
                return true;
            }
            private void useTsunami(Player player) {
                Location initialLocation = player.getLocation().clone();
                initialLocation.setPitch(0.0f);

                Vector direction = initialLocation.getDirection();

                List<List<Location>> shape = new ArrayList<>();

                for (int i = 1; i <= 10; i++) {
                    List<Location> line = new ArrayList<>();

                    Vector front = direction.clone().multiply(i);

                    line.add(initialLocation.clone().add(front));
                    for (int j = 0; j <= 2; j++) {
                        Vector right = Loc.getRightHeadDirection(player).multiply(j), left = Loc.getLeftHeadDirection(player).multiply(j);

                        line.add(initialLocation.clone().add(front.clone().add(right)));
                        line.add(initialLocation.clone().add(front.clone().add(left)));
                    }
                    shape.add(line);
                }

                player.updateInventory();
                new Wave(initialLocation.toVector(), shape);
            }
            private static class Wave extends BukkitRunnable {

                private final Vector origin;
                private final List<List<Location>> shape;
                private int index;

                public Wave(Vector origin, List<List<Location>> shape) {
                    this.origin = origin;
                    this.shape = shape;
                    this.start();
                }

                private void start() {
                    super.runTaskTimer(Main.getInstance(), 0, 2);
                }

                @SuppressWarnings("deprecation")
                @Override
                public void run() {
                    if(index >= shape.size()){
                        cancel();
                        return;
                    }
                    for (Location loc : shape.get(index)) {
                        FallingBlock fb = loc.getWorld().spawnFallingBlock(loc, Material.SAND, (byte) 0);
                        fb.setDropItem(false);
                        fb.setHurtEntities(true);
                        fb.setVelocity(new Vector(0, .3, 0));
                        for (Player players : Bukkit.getOnlinePlayers()) {
                            if(WorldUtils.getDistanceBetweenTwoLocations(players.getLocation(), loc) <= 1){
                                Vector fromPlayerToTarget = players.getLocation().toVector().clone().subtract(origin);
                                fromPlayerToTarget.multiply(4); //6
                                fromPlayerToTarget.setY(1); // 2
                                players.setVelocity(fromPlayerToTarget);
                                players.damage(2D*2D);
                            }
                        }
                    }
                    index++;
                }
            }
        }
        private static class SarcophagePower extends Power {

            private final AttackPower attackPower;

            public SarcophagePower(@NonNull AttackPower role) {
                super("Sarcophage de sable", null, role.gaaraV2);
                this.attackPower = role;
                setShowInDesc(false);
                role.gaaraV2.addPower(this);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                useSarcophage(player);
                return true;
            }
            private void useSarcophage(Player player) {
                Player target = RayTrace.getTargetPlayer(player, 10, null);
                if(target != null){
                    Location min = target.getLocation().clone().subtract(2, 1, 2), max = target.getLocation().clone().add(2, 5, 2);

                    Cuboid sarcophage = new Cuboid(min, max);
                    sarcophage.getBlocks().forEach(block -> block.setType(Material.SAND));
                    this.attackPower.gaaraV2.reserve-=45/this.attackPower.gaaraV2.divisor;
                } else {
                    player.sendMessage("§cIl faut viser un joueur !");
                }
            }
        }
        private static class LancePower extends Power {

            private final AttackPower attackPower;

            public LancePower(final AttackPower attackPower) {
                super("Lance", null, attackPower.gaaraV2);
                this.attackPower = attackPower;
                setShowInDesc(false);
                attackPower.gaaraV2.addPower(this);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                this.attackPower.gaaraV2.giveItem(player, true,
                        new ItemBuilder(Material.DIAMOND_SWORD)
                                .addEnchant(Enchantment.DAMAGE_ALL, 4)
                                .setName("§cLance")
                                .setDurability(Material.DIAMOND_SWORD.getMaxDurability()-30)
                                .toItemStack());
                return true;
            }
        }
    }
}