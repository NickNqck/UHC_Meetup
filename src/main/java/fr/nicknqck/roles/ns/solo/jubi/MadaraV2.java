package fr.nicknqck.roles.ns.solo.jubi;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.builders.JubiRoles;
import fr.nicknqck.roles.ns.power.Izanagi;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.PotionUtils;
import fr.nicknqck.utils.PropulserUtils;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MadaraV2 extends JubiRoles {

    public MadaraV2(UUID player) {
        super(player);
    }

    @Override
    public String getName() {
        return "Madara";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Madara;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        super.RoleGiven(gameState);
        givePotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        addPower(new MadaraItem(this), true);
        addPower(new ChibakuTensei(this), true);
        addPower(new SusanoPower(this), true);
        addPower(new Izanagi(this));
        addKnowedRole(ObitoV2.class);
        getGamePlayer().startChatWith("!", "§dMadara: ", ObitoV2.class);
        setChakraType(Chakras.KATON);
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

    private static class MadaraItem extends ItemPower {

        private final PotionEffect speed;
        private final PotionEffect force;
        private boolean have;

        public MadaraItem(@NonNull RoleBase role) {
            super("Madara", null, new ItemBuilder(Material.NETHER_STAR).setName("§dMadara"), role,
                    "§aActive§7/§cDésactive§7 vos effets de§e Speed II§7 et§c Force I permanents.");
            this.speed = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false);
            this.force = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false);
            this.have = false;
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (!have) {
                    player.sendMessage("§aVous commencez à obtenir votre puissance§c maximale§a.");
                    this.have = true;
                    getRole().givePotionEffect(this.force, EffectWhen.PERMANENT);
                    getRole().givePotionEffect(this.speed, EffectWhen.PERMANENT);
                } else {
                    player.sendMessage("§cVous perdez de votre puissance...");
                    this.have = false;
                    getRole().getEffects().remove(this.speed, EffectWhen.PERMANENT);
                    getRole().getEffects().remove(this.force, EffectWhen.PERMANENT);
                    player.removePotionEffect(PotionEffectType.SPEED);
                    player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                }
                return true;
            }
            return false;
        }
    }
    private static class ChibakuTensei extends ItemPower implements Listener {

        private final BenshoTenin benshoTenin;
        private final ShinraTensei shinraTensei;
        private final Meteorite meteorite;

        public ChibakuTensei(@NonNull RoleBase role) {
            super("Chibaku Tensei", new Cooldown(5), new ItemBuilder(Material.NETHER_STAR).setName("§cChibaku Tensei"), role,
                    "§7Vous permet d'ouvrir un menu, ce dernier vous permettra d'accéder au§c 3 pouvoirs§7 suivant: ",
                    "",
                    "§8 - §cBenshô Ten'in§7: Ouvre un menu permettant de sélectionner un§c joueur§7, une fois fait ce dernier sera téléporter à votre position. (1x/5m)",
                    "",
                    "§8 -§c Shinra Tensei§7: Vous permet de repousser toute les entités autours de vous. (1x/5m)",
                    "",
                    "§8 -§c Météorite§7: Au bout de§c 10 secondes§7 après avoir déclencher le pouvoir,",
                    "§7toute les personnes étant à moins de§c 50 blocs§7 du point d'impacte tomberont à§c 1/2❤§7 (sauf vous). (1x/partie)");
            setShowCdInDesc(false);
            EventUtils.registerRoleEvent(this);

            this.benshoTenin = new BenshoTenin(role, this);
            this.shinraTensei = new ShinraTensei(role);
            this.meteorite = new Meteorite(role);


            role.addPower(this.benshoTenin);
            role.addPower(this.shinraTensei);
            role.addPower(this.meteorite);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (map.containsKey("rien")) {
                if (map.get("rien") instanceof String) {
                    if (map.get("rien").equals("§cBenshô Ten'in")) {
                        return this.benshoTenin.checkUse(player, new HashMap<>());
                    } else if (map.get("rien").equals("§cShinra Tensei")) {
                        return this.shinraTensei.checkUse(player, map);
                    } else if (map.get("rien").equals("§cMétéorite")) {
                        return this.meteorite.checkUse(player, map);
                    }
                }
            }
            if (getInteractType().equals(InteractType.INTERACT)) {
                openInventory(player);
                return false;
            }
            return false;
        }

        @EventHandler
        private void onInventoryClick(InventoryClickEvent event) {
            if (event.getClickedInventory() == null)return;
            if (!(event.getWhoClicked() instanceof Player))return;
            if (!event.getClickedInventory().getTitle().equals("§cChibaku Tensei"))return;
            event.setCancelled(true);
            if (event.getCurrentItem() == null)return;
            if (!event.getCurrentItem().hasItemMeta())return;
            if (!event.getCurrentItem().getItemMeta().hasDisplayName())return;
            final ItemStack item = event.getCurrentItem();
            final String name = item.getItemMeta().getDisplayName();
            final Map<String, Object> rien = new HashMap<>();
            switch (name) {
                case "§cBenshô Ten'in":
                case "§cShinra Tensei":
                case "§cMétéorite":
                    rien.put("rien", name);
                    checkUse((Player) event.getWhoClicked(), rien);
                    break;
            }
        }

        private void openInventory(final Player player) {
            final Inventory inv = Bukkit.createInventory(player, 27, "§cChibaku Tensei");
            inv.setItem(11, new ItemBuilder(Material.LEASH).setName("§cBenshô Ten'in").setLore(this.benshoTenin.getCooldown().isInCooldown() ? "§7Cooldown:§c "+ StringUtils.secondsTowardsBeautiful(this.benshoTenin.getCooldown().getCooldownRemaining()) : "§cPouvoir utilisable !").toItemStack());
            inv.setItem(13, new ItemBuilder(Material.FEATHER).setName("§cShinra Tensei").setLore(
                    this.shinraTensei.getCooldown().isInCooldown() ? "§7Cooldown: "+StringUtils.secondsTowardsBeautiful(this.shinraTensei.getCooldown().getCooldownRemaining()) : "§cPouvoir utilisable !"
            ).toItemStack());
            inv.setItem(15, new ItemBuilder(Material.STONE).setName("§cMétéorite").setLore(
                    this.meteorite.getUse() == 0 ? "§cPouvoir utilisable !" : "§cVous avez déjà utiliser ce pouvoir"
            ).toItemStack());
            player.openInventory(inv);
        }

        private static class BenshoTenin extends Power implements Listener {

            private final ChibakuTensei chibakuTensei;

            public BenshoTenin(@NonNull RoleBase role, ChibakuTensei chibakuTensei) {
                super("Benshô Ten'in", new Cooldown(60*5), role);
                this.chibakuTensei = chibakuTensei;
                setShowInDesc(false);
                EventUtils.registerRoleEvent(this);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                if (map.isEmpty()) {
                    final Inventory inv = Bukkit.createInventory(player, 54, "§cBenshô Ten'in");
                    for (int i = 0; i <= 8; i++) {
                        if (i == 4) {
                            inv.setItem(i, GUIItems.getSelectBackMenu());
                        } else {
                            inv.setItem(i, GUIItems.getOrangeStainedGlassPane());
                        }
                    }
                    for (final Player p : Loc.getNearbyPlayersExcept(player, 30)) {
                        inv.addItem(new ItemBuilder(Material.SKULL_ITEM).setDurability(3).setSkullOwner(p.getName()).setName(p.getDisplayName()).toItemStack());
                    }
                    player.openInventory(inv);
                } else return map.containsKey("rien");
                return false;
            }
            @EventHandler
            private void onInventoryClick(final InventoryClickEvent event) {
                if (event.getClickedInventory() == null)return;
                if (!event.getClickedInventory().getTitle().equals("§cBenshô Ten'in"))return;
                event.setCancelled(true);
                if (!(event.getWhoClicked() instanceof Player))return;
                if (event.getCurrentItem() == null)return;
                if (!event.getCurrentItem().hasItemMeta())return;
                if (!event.getCurrentItem().getItemMeta().hasDisplayName())return;
                if (event.getCurrentItem().isSimilar(GUIItems.getSelectBackMenu())) {
                    this.chibakuTensei.openInventory((Player) event.getWhoClicked());
                    return;
                }
                if (!event.getCurrentItem().getType().equals(Material.SKULL_ITEM))return;
                final Player target = Bukkit.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName());
                if (target == null)return;
                final Map<String, Object> map = new HashMap<>();
                map.put("rien", "rien");
                if (!this.checkUse(target, map))return;
                target.teleport(event.getWhoClicked());
                event.getWhoClicked().sendMessage("§7Vous avez téléporter§c "+target.getName()+"§7 à votre position");
                target.sendMessage("§7Vous êtes sous l'effet du§c Benshô Ten'in§7.");
            }
        }
        private static class ShinraTensei extends Power {

            public ShinraTensei(@NonNull RoleBase role) {
                super("Shinra Tensei", new Cooldown(60*5), role);
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                player.closeInventory();
                player.sendMessage("§cShinra Tensei !");
                PropulserUtils pu = new PropulserUtils(player, 20).soundToPlay("nsmtp.shinratensei");
                for (final UUID uuid : pu.getPropulsedUUID()) {
                    PotionUtils.addTempNoFall(uuid, 1);
                }
                pu.applyPropulsion();
                return true;
            }
        }
        private static class Meteorite extends Power {

            public Meteorite(@NonNull RoleBase role) {
                super("Meteorite", null, role);
                setShowInDesc(false);
                setMaxUse(1);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                player.closeInventory();
                player.sendMessage("§7Votre§c météorite§7 arrivera à terre dans§c 10 secondes§7.");
                new MeteoriteRunnable(player, this);
                Loc.getNearbyPlayers(player, 50)
                        .stream()
                        .filter(p -> GameState.getInstance().getInGamePlayers().contains(p.getUniqueId()))
                        .filter(p -> !GameState.getInstance().hasRoleNull(p.getUniqueId()))
                        .forEach(e -> e.playSound(e.getEyeLocation(), "mob.wither.death", 8f, 1.0f));
                return true;
            }
            private static class MeteoriteRunnable extends BukkitRunnable {

                private final Meteorite meteorite;
                private final UUID uuid;
                private final Location initLocation;
                private int timeLeft;

                private MeteoriteRunnable(Player player, Meteorite meteorite) {
                    this.meteorite = meteorite;
                    this.uuid = player.getUniqueId();
                    this.initLocation = player.getLocation();
                    this.timeLeft = 10;
                    runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
                }

                @Override
                public void run() {
                    if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                        cancel();
                        return;
                    }
                    if (this.timeLeft == 5) {
                        final Player owner = Bukkit.getPlayer(this.uuid);
                        if (owner != null){
                            owner.sendMessage("§7Votre météorite attérira dans 5s");
                        }
                        for (final GamePlayer gamePlayer : Loc.getNearbyGamePlayers(this.initLocation, 50)) {
                            final Player p = Bukkit.getPlayer(gamePlayer.getUuid());
                            if (p == null)continue;
                            this.meteorite.getRole().playSound(p, "mob.wither.death");
                            if (!p.getUniqueId().equals(this.uuid)){
                                p.sendMessage("§cAttention !!! Vous allez bientôt être pris dans la météorite de§d Madara§c !!!");
                            }
                        }
                    }
                    if (this.timeLeft <= 0) {
                        this.meteorite.getRole().getGamePlayer().getActionBarManager().removeInActionBar("madara.meteorite");
                        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                            PotionUtils.addTempNoFall(this.meteorite.getRole().getPlayer(), 2);
                            final MathUtil mathUtil = new MathUtil();
                            for(final Location loc : mathUtil.sphere(this.initLocation, 30, false)) {
                                loc.getBlock().setType(Material.AIR);
                                for (final Player p : Loc.getNearbyPlayers(loc, 0.9)) {
                                    if (p.getUniqueId() != this.uuid) {
                                        p.damage(0.0);
                                        if (p.getHealth() - 10.0 <= 0) {
                                            p.setHealth(1.0);
                                        } else {
                                            p.setHealth(p.getHealth()-10.0);
                                        }
                                        p.sendMessage("§7Vous avez subit les dégâts de la§c météorite§7 de§d Madara§7.");
                                    }
                                }
                            }
                            mathUtil.spawnFallingBlocks(new Location(this.initLocation.getWorld(), this.initLocation.getX(), this.initLocation.getY()+10, this.initLocation.getZ()), Material.STONE, 8, false, false, 60);
                        });
                        cancel();
                        return;
                    }
                    this.meteorite.getRole().getGamePlayer().getActionBarManager().addToActionBar("madara.meteorite", "§bTemp avant déclanchement de la§c Météorite§b: "+StringUtils.secondsTowardsBeautiful(this.timeLeft));
                    this.timeLeft--;
                }
            }
        }
    }
    private static class SusanoPower extends ItemPower {

        protected SusanoPower(@NonNull RoleBase role) {
            super("Susano (Madara)", new Cooldown(60*20), new ItemBuilder(Material.NETHER_STAR).setName("§c§lSusanô"), role,
                    "§7Vous permet d'obtenir l'effet§c Résistance I§7 pendant§c 5 minutes§7. (1x/20m)");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                new SusanoPower.SusanoRunnable(this.getRole().getGameState(), this.getRole().getGamePlayer());
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60*5, 0, false, false), EffectWhen.NOW);
                player.sendMessage("§cActivation du§l Susanô§c.");
                return true;
            }
            return false;
        }
        private static class SusanoRunnable extends BukkitRunnable {

            private final GameState gameState;
            private final GamePlayer gamePlayer;
            private int timeLeft = 60*5;

            private SusanoRunnable(GameState gameState, GamePlayer gamePlayer) {
                this.gameState = gameState;
                this.gamePlayer = gamePlayer;
                this.gamePlayer.getActionBarManager().addToActionBar("madara.susano", "§bTemp restant du§c§l Susanô§b: "+StringUtils.secondsTowardsBeautiful(this.timeLeft));
                runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (this.timeLeft <= 0) {
                    this.gamePlayer.getActionBarManager().removeInActionBar("madara.susano");
                    this.gamePlayer.sendMessage("§cVotre§l Susanô§c s'arrête");
                    cancel();
                    return;
                }
                this.timeLeft--;
                this.gamePlayer.getActionBarManager().updateActionBar("madara.susano", "§bTemp restant du§c§l Susanô§b: "+StringUtils.secondsTowardsBeautiful(this.timeLeft));
            }
        }
    }
}