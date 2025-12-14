package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.HShinobiRoles;
import fr.nicknqck.roles.ns.power.YameruPower;
import fr.nicknqck.utils.GlobalUtils;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.fastinv.FastInv;
import fr.nicknqck.utils.fastinv.InventoryScheme;
import fr.nicknqck.utils.fastinv.PaginatedFastInv;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.KamuiUtils;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class KakashiV2 extends HShinobiRoles {

    public KakashiV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.GENIE;
    }

    @Override
    public String getName() {
        return "Kakashi";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Kakashi;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new KamuiPower(this), true);
        addPower(new SharinganPower(this), true);
        addPower(new YameruPower(this));
        setChakraType(Chakras.RAITON);
        super.RoleGiven(gameState);
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
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
                    "§7La personne cibler ce verra téléporter dans la§c dimension Kamui§7 pendant§c 5 minutes§7. (1x/12m)");
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
                super("Kamui§7 (§dArimasu§7)", new Cooldown(60*10), role);
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                KamuiUtils.start(player.getLocation(), KamuiUtils.Users.kakashi, player, true);
                new KamuiPower.Arimasu.ArimasuRunnable(getRole().getGameState(), this);
                return true;
            }
            private static class ArimasuRunnable extends BukkitRunnable {

                private final GameState gameState;
                private final KamuiPower.Arimasu arimasu;
                private int timeLeft = 60*3;

                private ArimasuRunnable(GameState gameState, KamuiPower.Arimasu arimasu) {
                    this.gameState = gameState;
                    this.arimasu = arimasu;
                    arimasu.getRole().getGamePlayer().getActionBarManager().addToActionBar("kakashi.arimasu", "§bTemp restant dans le§d Kamui§b: §c3 minutes");
                    runTaskTimerAsynchronously(arimasu.getPlugin(), 0, 20);
                }

                @Override
                public void run() {
                    if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                        cancel();
                        return;
                    }
                    arimasu.getRole().getGamePlayer().getActionBarManager().updateActionBar("kakashi.arimasu", "§bTemp restant dans le§d Kamui: "+StringUtils.secondsTowardsBeautiful(this.timeLeft));

                    final Player owner = Bukkit.getPlayer(this.arimasu.getRole().getPlayer());
                    if (owner != null) {
                        if (this.timeLeft <= 0 || !owner.getWorld().getName().equalsIgnoreCase("Kamui")) {
                            Bukkit.getScheduler().runTask(this.arimasu.getPlugin(), () -> KamuiUtils.end(owner));
                            this.arimasu.getRole().getGamePlayer().getActionBarManager().removeInActionBar("kakashi.arimasu");
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
                super("Kamui§7 (§dSonohaka§7)§r", new Cooldown(60*12), role);
                this.kamuiPower = kamuiPower;
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                if (map.isEmpty()) {
                    @NonNull final Inventory inv = Bukkit.createInventory(player, 54, "§7(§c!§7)§d Sonohaka");
                    for (int i = 0; i <= 8; i++) {
                        inv.setItem(i, GUIItems.getPurpleStainedGlassPane());
                    }
                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    @NonNull final List<Player> playerList = new ArrayList<>(Loc.getNearbyPlayersExcept(player, 30));
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
                                Map<String, Object> map = new HashMap<>();
                                map.put("((Player)event.getWhoClicked())", event.getClick());
                                if (checkUse((Player) event.getWhoClicked(), map)){
                                    KamuiUtils.start(target.getLocation(), KamuiUtils.Users.cibleKakashi, (target), true);
                                }
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
    private static class SharinganPower extends ItemPower {

        private static final InventoryScheme SCHEME = new InventoryScheme()
                .mask(" 1111111 ")
                .mask(" 1111111 ")
                .bindPagination('1');
        private final HashMap<String, List<PotionEffectType>> Copied = new HashMap<>();
        private boolean coping = false;
        private int actualPoint = 0;
        private Player targetOfCopy = null;

        public SharinganPower(@NonNull RoleBase role) {
            super("§cSharingan§r", null, new ItemBuilder(Material.NETHER_STAR).setName("§cSharingan"), role,
                    "§7Ouvre un menu ayant plusieurs choix de pouvoir: ",
                    "§7     →§a Copie§7: Ouvre un menu permettant de sélectionner un joueur,",
                    "§7en le sélectionnant cela crée une§c bar§7 de§a 1600 points§7,",
                    "§7vous augmentez ces dernier en étant plus ou moins proche de la cible:",
                    "",
                    "§8 -§f 5 blocs:§a + 20 points",
                    "§8 -§f 10 blocs:§6 + 10 points",
                    "§8 -§f 20 blocs:§c + 5 points",
                    "",
                    "§7Une fois les§a 1600§7 points atteint vous obtenez les mêmes effets que la cible (seulement les effets permanent)",
                    "",
                    "§7     →§a Technique§7: Ouvre un menu ayant à l'intérieur la liste des joueurs a qui vous avez copié les effets,",
                    "§7en cliquant sur la tête d'un de ces joueurs",
                    "§7vous obtiendrez a nouveau les effets permanent de la cible (vous retire vos effet actuel).",
                    "");
            new SharinganRunnable(this).runTaskTimerAsynchronously(getPlugin(), 100, 20);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            openInv(player);
            return true;
        }
        private void openInv(@NonNull final Player player) {
            @NonNull final FastInv fastInv = new FastInv(27, "§cSharingan");
            fastInv.setItems(fastInv.getCorners(), new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").setDurability(7).toItemStack());
            fastInv.setItem(12, new ItemBuilder(Material.PAPER).setName("§aTechnique").toItemStack(),
                    event -> {
                if (event.getWhoClicked() instanceof Player) {
                    openTechniqueInventory((Player) event.getWhoClicked());
                }
                    });
            if (!coping) {
                fastInv.setItem(14, new ItemBuilder(Material.EYE_OF_ENDER)
                        .setName("§aCopie")
                        .toItemStack(), event -> {
                    @NonNull final FastInv inv = new FastInv(54, "§cSharingan§7 ->§a Copie");
                    inv.setItems(inv.getBorders(), new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
                    for (@NonNull final GamePlayer gamePlayer : Loc.getNearbyGamePlayers(event.getWhoClicked().getLocation(), 30)) {
                        if (gamePlayer.getRole() == null)continue;
                        if (!gamePlayer.isAlive())continue;
                        if (!gamePlayer.isOnline())continue;
                        inv.addItem(new ItemBuilder(GlobalUtils.getPlayerHead(gamePlayer.getUuid())).setName("§a"+gamePlayer.getPlayerName()).toItemStack(),
                                event1 -> {
                            String name = event1.getCurrentItem().getItemMeta().getDisplayName().substring(0, event1.getCurrentItem().getItemMeta().getDisplayName().length()-2);
                            final Player target = Bukkit.getPlayer(name);
                            if (target != null){
                                coping = true;
                                targetOfCopy = target;
                                actualPoint = 0;
                            }
                            event1.getWhoClicked().closeInventory();
                                });
                    }
                });
            } else {
                fastInv.setItem(14, new ItemBuilder(Material.BARRIER)
                        .setName("§cAnnuler")
                        .setLore(targetOfCopy == null ? "§cLa cible n'est pas connecter" : "§cLa cible est§4 "+targetOfCopy.getName()+"§c.")
                        .toItemStack());
            }
            fastInv.open(player);
        }
        private void openTechniqueInventory(@NonNull final Player player) {
            @NonNull final PaginatedFastInv paginatedFastInv = new PaginatedFastInv(27, "§cSharingan§7 ->§a Technique");
            paginatedFastInv.setItems(paginatedFastInv.getCorners(), new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").setDurability(7).toItemStack());
            paginatedFastInv.previousPageItem(20, p -> new ItemBuilder(Material.ARROW).setName("Page "+p+"/"+paginatedFastInv.lastPage()).toItemStack());
            paginatedFastInv.nextPageItem(24, p -> new ItemBuilder(Material.ARROW).setName("Page "+p+"/"+paginatedFastInv.lastPage()).toItemStack());
            for (String string : Copied.keySet()) {
                paginatedFastInv.addContent(new ItemBuilder(Material.PAPER).setName(string).toItemStack());
            }
            paginatedFastInv.open(player);
            SCHEME.apply(paginatedFastInv);
        }
        private static class SharinganRunnable extends BukkitRunnable {

            private final SharinganPower sharinganPower;

            private SharinganRunnable(SharinganPower sharinganPower) {
                this.sharinganPower = sharinganPower;
            }

            @Override
            public void run() {
                if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (!sharinganPower.getRole().getGamePlayer().isAlive() || !sharinganPower.getRole().getGamePlayer().isOnline() || this.sharinganPower.targetOfCopy == null) {
                    sharinganPower.getRole().getGamePlayer().getActionBarManager().removeInActionBar("kakashi.copy");
                    return;
                }
                final Player owner = Bukkit.getPlayer(sharinganPower.getRole().getPlayer());
                if (owner == null)return;
                if (this.sharinganPower.actualPoint >= 1600) {
                    this.sharinganPower.coping = false;
                    this.sharinganPower.Copied.put(this.sharinganPower.targetOfCopy.getName(), this.sharinganPower.getRole().getPermanentPotionEffects(this.sharinganPower.targetOfCopy));
                    this.sharinganPower.getRole().getGamePlayer().sendMessage("§7La copie est terminer.");
                    this.sharinganPower.targetOfCopy = null;
                    return;
                }
                sharinganPower.getRole().getGamePlayer().getActionBarManager().updateActionBar("kakashi.copy", "§aPoints:§c "+this.sharinganPower.actualPoint+"/§6"+1600);
                if (Loc.getNearbyPlayersExcept(owner, 5).contains(this.sharinganPower.targetOfCopy)) {
                    this.sharinganPower.actualPoint+=20;
                } else if (Loc.getNearbyPlayersExcept(owner, 10).contains(this.sharinganPower.targetOfCopy)) {
                    this.sharinganPower.actualPoint+=10;
                } else if (Loc.getNearbyPlayersExcept(owner, 20).contains(this.sharinganPower.targetOfCopy)) {
                    this.sharinganPower.actualPoint+=5;
                } else if (Loc.getNearbyPlayersExcept(owner, 40).contains(this.sharinganPower.targetOfCopy)) {
                    this.sharinganPower.actualPoint+=1;
                }
            }
        }
    }
}