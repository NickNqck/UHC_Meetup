package fr.nicknqck.roles.ns.solo.jubi;

import com.avaje.ebean.validation.NotNull;
import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.builders.IUchiwa;
import fr.nicknqck.roles.ns.builders.JubiRoles;
import fr.nicknqck.roles.ns.power.Genjutsu;
import fr.nicknqck.roles.ns.power.Izanagi;
import fr.nicknqck.roles.ns.power.YameruPower;
import fr.nicknqck.utils.GlobalUtils;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.*;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ObitoV2 extends JubiRoles {

    public ObitoV2(UUID player) {
        super(player);
    }

    @Override
    public String getName() {
        return "Obito";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Obito;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        if (!gameState.getAttributedRole().contains(GameState.Roles.Kakashi)) {
            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            Bukkit.dispatchCommand(console, "nakime Gh6Iu2YjZl8A9Bv3Tn0Pq5Rm");
        }
        givePotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        addPower(new KamuiPower(this), true);
        addPower(new NinjutsuSpatioTemporel(this), true);
        addPower(new Genjutsu(this), true);
        addPower(new YameruPower(this));
        addPower(new ObtainSusanoPower(this));
        addPower(new Izanagi(this));
        setChakraType(Chakras.KATON);
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .getText();
    }
    private static class ObtainSusanoPower extends CommandPower implements Listener {

        private final Map<String, Location> deathLocations;


        public ObtainSusanoPower(@NonNull RoleBase role) {
            super("/ns obtain", "obtain", null, role, CommandType.NS, "§7Lorsqu'un§4 Uchiwa§7 meurt vous obtiendrez ses coordonnées, une fois que vous y serez, en faisant cette commande vous obtiendrez le§c§l Susanô§7 ");
            this.deathLocations = new HashMap<>();
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            @NonNull final String[] args = (String[]) map.get("args");
            if (this.deathLocations.isEmpty()) {
                player.sendMessage("§7Aucun§4§l Uchiwa§7 est mort...");
                return false;
            }
            if (args.length < 2) {
                for (@NonNull final Location location : this.deathLocations.values()) {
                    if (player.getLocation().distance(location) <= 5) {
                        this.getRole().addPower(new SusanoPower(this.getRole()), true);
                        this.getRole().getPowers().remove(this);
                        return true;
                    }
                }
            } else {
                if (args[1].equalsIgnoreCase("list")) {
                    for (@NonNull final String string : this.deathLocations.keySet()) {
                        player.sendMessage(new String[] {
                                string+"§7 est mort en: ",
                                "",
                                "§cx: "+this.deathLocations.get(string).getBlockX(),
                                "",
                                "§cy: "+this.deathLocations.get(string).getBlockY(),
                                "",
                                "§cz: "+this.deathLocations.get(string).getBlockZ()
                        });
                    }
                    return false;
                }
            }
            return false;
        }
        @EventHandler
        private void GamePlayerDeathEvent(@NonNull final UHCDeathEvent event) {
            if (event.getPlayer().getUniqueId().equals(getRole().getPlayer()))return;
            if (!event.getPlayer().getWorld().getName().equals("arena"))return;
            if (!this.getRole().getPowers().contains(this))return;
            if (event.getRole() instanceof IUchiwa) {
                getRole().getGamePlayer().sendMessage("§cUn§4 Uchiwa§c est mort ! C'est sûrement l'occasion pour vous de récupérez un§4 Sharingan§c, avec un peux de chance vous pourriez obtenir un Susanô...",
                        " ",
                        "§cx: "+event.getPlayer().getLocation().getBlockX(),
                        " ",
                        "§cz: "+event.getPlayer().getLocation().getBlockZ());
                this.deathLocations.put(event.getRole().getTeamColor()+event.getRole().getName(), event.getPlayer().getLocation());
            }
        }
        private static class SusanoPower extends ItemPower {

            protected SusanoPower(@NonNull RoleBase role) {
                super("Susano (Obito)", new Cooldown(60*20), new ItemBuilder(Material.NETHER_STAR).setName("§c§lSusanô"), role,
                        "§7Vous permet d'obtenir l'effet§c Résistance I§7 pendant§c 5 minutes§7. (1x/20m)");
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                if (getInteractType().equals(InteractType.INTERACT)) {
                    new SusanoRunnable(this.getRole().getGameState(), this.getRole().getGamePlayer());
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
                    this.gamePlayer.getActionBarManager().addToActionBar("obito.susano", "§bTemp restant du§c§l Susanô§b: "+StringUtils.secondsTowardsBeautiful(this.timeLeft));
                    runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
                }

                @Override
                public void run() {
                    if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                        cancel();
                        return;
                    }
                    if (this.timeLeft <= 0) {
                        this.gamePlayer.getActionBarManager().removeInActionBar("obito.susano");
                        this.gamePlayer.sendMessage("§cVotre§l Susanô§c s'arrête");
                        cancel();
                        return;
                    }
                    this.timeLeft--;
                    this.gamePlayer.getActionBarManager().updateActionBar("obito.susano", "§bTemp restant du§c§l Susanô§b: "+StringUtils.secondsTowardsBeautiful(this.timeLeft));
                }
            }
        }
    }
    private static class KamuiPower extends ItemPower implements Listener {

        private final Arimasu arimasu;
        private final Sonohaka sonohaka;

        protected KamuiPower(@NonNull RoleBase role) {
            super("Kamui", null, new ItemBuilder(Material.NETHER_STAR).setName("§dKamui"), role,
                    "§7Vous ouvre un menu vous permettant d'accéder à§c deux pouvoirs§7:",
                    "",
                    "§dArimasu§7: Vous permet de rentrer dans la§c dimension Kamui§7 pendant une durée maximal de§c 5 minutes§7. (1x/10m)",
                    "",
                    "§dSonohaka§7: Vous permet d'ouvrir un autre menu vous permettant de cibler un joueur,",
                    "§7La personne cibler ce verra téléporter dans la§c dimension Kamui§7 pendant§c 5 minutes§7. (1x/10m)");
            this.arimasu = new Arimasu(role);
            this.sonohaka = new Sonohaka(role, this);
            role.addPower(arimasu);
            role.addPower(sonohaka);
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                openMenu(player);
                return true;
            }
            return false;
        }
        @EventHandler
        private void onInventoryClick(@NonNull final InventoryClickEvent event) {
            if (event.getInventory() == null)return;
            if (event.getInventory().getTitle() == null)return;
            if (!(event.getWhoClicked() instanceof Player))return;
            if (!event.getWhoClicked().getUniqueId().equals(this.getRole().getPlayer()))return;
            if (event.getInventory().getTitle().equalsIgnoreCase("§7(§c!§7)§d Kamui")) {
                if (event.getCurrentItem() == null)return;
                if (event.getCurrentItem().getItemMeta() == null)return;
                if (event.getCurrentItem().getItemMeta().getDisplayName() == null)return;
                if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§dArimasu")) {
                    event.setCancelled(true);
                    this.arimasu.checkUse((Player) event.getWhoClicked(), new HashMap<>());
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§dSonohaka")) {
                    event.setCancelled(true);
                    this.sonohaka.checkUse((Player) event.getWhoClicked(), new HashMap<>());
                }
            }
        }
        private void openMenu(@NonNull final Player player) {
            @NonNull final Inventory inv = Bukkit.createInventory(player, 27, "§7(§c!§7)§d Kamui");
            inv.setItem(12, new ItemBuilder(Material.EYE_OF_ENDER).setName("§dArimasu").setLore("§7Cooldown "+ StringUtils.secondsTowardsBeautiful(this.arimasu.getCooldown().getCooldownRemaining()),
                    "§7Permet de vous téléportez dans le Kamui").toItemStack());
            inv.setItem(14, new ItemBuilder(Material.ENDER_PEARL).setName("§dSonohaka").setLore("§7Cooldown "+StringUtils.secondsTowardsBeautiful(this.sonohaka.getCooldown().getCooldownRemaining()),
                    "§7Permet de téléporter un joueur dans le Kamui").toItemStack());
            player.openInventory(inv);
        }
        private static class Arimasu extends Power {

            public Arimasu(@NonNull RoleBase role) {
                super("Kamui§7 (§dArimasu§7)", new Cooldown(60*15), role);
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                KamuiUtils.start(player.getLocation(), KamuiUtils.Users.obito, player, true);
                new ArimasuRunnable(getRole().getGameState(), this);
                return true;
            }
            private static class ArimasuRunnable extends BukkitRunnable {

                private final GameState gameState;
                private final Arimasu arimasu;
                private int timeLeft = 60*3;

                private ArimasuRunnable(GameState gameState, Arimasu arimasu) {
                    this.gameState = gameState;
                    this.arimasu = arimasu;
                    arimasu.getRole().getGamePlayer().getActionBarManager().addToActionBar("obito.arimasu", "§bTemp restant dans le§d Kamui§b: §c3 minutes");
                    runTaskTimerAsynchronously(arimasu.getPlugin(), 0, 20);
                }

                @Override
                public void run() {
                    if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                        cancel();
                        return;
                    }
                    arimasu.getRole().getGamePlayer().getActionBarManager().updateActionBar("obito.arimasu", "§bTemp restant dans le§d Kamui: "+StringUtils.secondsTowardsBeautiful(this.timeLeft));

                    final Player owner = Bukkit.getPlayer(this.arimasu.getRole().getPlayer());
                    if (owner != null) {
                        if (this.timeLeft <= 0 || !owner.getWorld().getName().equalsIgnoreCase("Kamui")) {
                            Bukkit.getScheduler().runTask(this.arimasu.getPlugin(), () -> KamuiUtils.end(owner));
                            this.arimasu.getRole().getGamePlayer().getActionBarManager().removeInActionBar("obito.arimasu");
                            cancel();
                            return;
                        }
                    }
                    this.timeLeft--;
                }
            }
        }
        private static class Sonohaka extends Power implements Listener {

            private final KamuiPower kamuiPower;

            public Sonohaka(@NonNull RoleBase role, KamuiPower kamuiPower) {
                super("Kamui§7 (§dSonohaka§7)§r", new Cooldown(60*15), role);
                this.kamuiPower = kamuiPower;
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                if (map.isEmpty()) {
                    @NonNull final Inventory inv = Bukkit.createInventory(player, 54, "§7(§c!§7)§d Sonohaka");
                    for (int i = 0; i < 8; i++) {
                        inv.setItem(i, GUIItems.getPurpleStainedGlassPane());
                    }
                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    @NonNull final List<Player> playerList = new ArrayList<>(Loc.getNearbyPlayers(player, 30));
                    for (@NonNull Player p : playerList) {
                        @NonNull final ItemStack item = GlobalUtils.getPlayerHead(p.getName());
                        inv.addItem(new ItemBuilder(item)
                                .setName("§b"+p.getName())
                                .setLore("§7Cliquez ici pour envoyer§c "+p.getDisplayName()+"§7 dans le§5 Kamui")
                                .toItemStack());
                    }
                    player.openInventory(inv);
                    EventUtils.registerRoleEvent(this);
                } else {
                    return true;
                }
                return false;
            }
            @EventHandler
            private void onInventoryClick(@NonNull final InventoryClickEvent event) {
                if (event.getInventory() == null)return;
                if (event.getInventory().getTitle() == null)return;
                if (!(event.getWhoClicked() instanceof Player))return;
                if (!event.getWhoClicked().getUniqueId().equals(this.getRole().getPlayer()))return;
                if (event.getInventory().getTitle().equals("§7(§c!§7)§d Sonohaka")) {
                    if (event.getCurrentItem() == null)return;
                    if (event.getCurrentItem().getItemMeta() == null)return;
                    if (event.getCurrentItem().getItemMeta().getDisplayName() == null)return;
                    if (event.getCurrentItem().isSimilar(GUIItems.getSelectBackMenu())) {
                        event.setCancelled(true);
                        this.kamuiPower.openMenu((Player) event.getWhoClicked());
                    } else {
                        event.setCancelled(true);
                        String name = event.getCurrentItem().getItemMeta().getDisplayName();
                        if (name.length() > 2) {
                            name = name.substring(2);
                            @NonNull final Player target = Bukkit.getPlayer(name);
                            if (target != null) {
                                event.getWhoClicked().closeInventory();
                                KamuiUtils.start(target.getLocation(), KamuiUtils.Users.cibleObito, ((Player)event.getWhoClicked()), true);
                            }
                        }
                    }
                }
            }
            @EventHandler
            private void InventoryCloseEvent(@NonNull final InventoryCloseEvent event) {
                if (event.getInventory() == null)return;
                if (!event.getPlayer().getUniqueId().equals(getRole().getPlayer()))return;
                if (event.getInventory().getTitle().equals("§7(§c!§7)§d Sonohaka")) {
                    EventUtils.unregisterEvents(this);
                }
            }
        }
    }
    private static class NinjutsuSpatioTemporel extends ItemPower {

        @NotNull
        private final NinjutsuRunnable ninjutsuRunnable;

        protected NinjutsuSpatioTemporel(@NonNull RoleBase role) {
            super("Ninjutsu Spatio-Temporel", new Cooldown(60*8), new ItemBuilder(Material.NETHER_STAR).setName("§dNinjutsu Spatio-Temporel"), role,
                    "§7Vous permet de vous rendre§c invisible§7 pendant une durée de§c 60 secondes§7.",
                    "",
                    "§7(Vous pouvez retirer votre invisibilité en réapuyant sur cette item)");
            this.ninjutsuRunnable = new NinjutsuRunnable(role.getGameState(), this);
            setWorkWhenInCooldown(true);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (!getCooldown().isInCooldown()) {
                    this.ninjutsuRunnable.start(player);
                    return true;
                } else {
                    if (getCooldown().getCooldownRemaining() > 60*9) {
                        if (getCooldown().getCooldownRemaining() < (60*10)-5) {
                            this.ninjutsuRunnable.stop(player);
                        } else {
                            player.sendMessage("§cIl faut attendre avant de pouvoir annuler votre Ninjutsu.");
                        }
                    } else {
                        player.sendMessage("§cVous êtes en cooldown:§b "+StringUtils.secondsTowardsBeautiful(getCooldown().getCooldownRemaining()));
                    }
                    return false;
                }
            }
            return false;
        }
        private static class NinjutsuRunnable extends BukkitRunnable {

            private final GameState gameState;
            private final NinjutsuSpatioTemporel ninjutsu;
            private int timeLeft;

            private NinjutsuRunnable(GameState gameState, NinjutsuSpatioTemporel ninjutsu) {
                this.gameState = gameState;
                this.ninjutsu = ninjutsu;
                this.timeLeft = 60;
            }

            @Override
            public void run() {
                if (!this.gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                this.timeLeft--;
                ninjutsu.getRole().getGamePlayer().getActionBarManager().updateActionBar("ninjutsu.runnable", "§bTemp restant§c invisible§b:§c "+StringUtils.secondsTowardsBeautiful(this.timeLeft));
                if (this.timeLeft <= 0) {
                    final Player player = Bukkit.getPlayer(this.ninjutsu.getRole().getPlayer());
                    if (player != null) {
                        this.stop(player);
                    }
                }
            }

            public void start(@NonNull Player player) {
                ninjutsu.getRole().getGamePlayer().getActionBarManager().addToActionBar("ninjutsu.runnable", "§bTemp restant§c invisible§b:§c "+StringUtils.secondsTowardsBeautiful(this.timeLeft));
                this.ninjutsu.getRole().givePotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20*60, 0, false, false), EffectWhen.NOW);
                player.setSleepingIgnored(true);
                for (@NonNull final UUID uuid : gameState.getInGamePlayers()) {
                    @NonNull final Player target = Bukkit.getPlayer(uuid);
                    if (target == null)continue;
                    if (target.getUniqueId().equals(player.getUniqueId()))continue;
                    target.hidePlayer(player);
                }
                this.timeLeft = 60;
                runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
            }
            public void stop(@NonNull final Player player) {
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> player.removePotionEffect(PotionEffectType.INVISIBILITY));
                for (@NonNull final Player target : Bukkit.getOnlinePlayers()) {
                    if (!target.canSee(player)) {
                        target.showPlayer(player);
                    }
                }
                this.ninjutsu.getRole().getGamePlayer().getActionBarManager().removeInActionBar("ninjutsu.runnable");
                cancel();
            }
        }
    }
}