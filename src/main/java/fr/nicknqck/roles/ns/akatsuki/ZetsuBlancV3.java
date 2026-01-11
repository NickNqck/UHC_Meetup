package fr.nicknqck.roles.ns.akatsuki;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.FinalDeathEvent;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.AkatsukiRoles;
import fr.nicknqck.utils.GlobalUtils;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.fastinv.InventoryScheme;
import fr.nicknqck.utils.fastinv.PaginatedFastInv;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.packets.ArmorStandUtils;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ZetsuBlancV3 extends AkatsukiRoles {

    public ZetsuBlancV3(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.PEUINTELLIGENT;
    }

    @Override
    public String getName() {
        return "Zetsu Blanc§7 (§6V3§7)";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.ZetsuBlanc;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        new SporeManager(this);
        setChakraType(Chakras.DOTON);
        addKnowedRole(ZetsuNoir.class);
        getGamePlayer().startChatWith("§cZetsu Blanc:", "!", ZetsuNoir.class, ZetsuBlancV3.class);
        super.RoleGiven(gameState);
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

    private static class SporeManager implements Listener {

        private final ZetsuBlancV3 role;
        private final SporeRunnable sporeRunnable;
        private int amountSpore = 2;
        private int maxAmountSpore = 3;
        private final Map<Integer, ItemStack> getContents = new HashMap<>();
        private ItemStack[] getArmors = new ItemStack[0];

        private SporeManager(ZetsuBlancV3 role) {
            this.role = role;
            this.sporeRunnable = new SporeRunnable(this);
            role.addPower(new SporeItem(role, this), true);
            EventUtils.registerRoleEvent(this);
        }

        @EventHandler
        private void onPlayerMove(final PlayerMoveEvent event) {
            final Player owner = Bukkit.getPlayer(this.role.getPlayer());
            if (owner == null)return;
            if (this.sporeRunnable.healthPlayers.containsKey(event.getPlayer().getUniqueId())) {
                this.sporeRunnable.healthPlayers.get(event.getPlayer().getUniqueId()).rename("§bPourcentage:§c "+this.sporeRunnable.healthPlayers.get(event.getPlayer().getUniqueId()).percent+"%", owner);
                final Location to = new Location(event.getTo().getWorld(), event.getTo().getX(), event.getTo().getY()+0.5, event.getTo().getZ());
                this.sporeRunnable.healthPlayers.get(event.getPlayer().getUniqueId()).teleport(to, owner);
            }
        }
        @EventHandler()
        private void onDeath(@NonNull final FinalDeathEvent event) {
            if (!this.sporeRunnable.healthPlayers.containsKey(event.getPlayer().getUniqueId()))return;
            int gap = GlobalUtils.getItemAmount(event.getPlayer(), Material.GOLDEN_APPLE);
            final SporeRunnable.CustomArmorStand customArmorStand = this.sporeRunnable.healthPlayers.get(event.getPlayer().getUniqueId());
            if (customArmorStand.percent >= 75){
                gap = gap/2;
            } else {
                gap = 0;
            }
            double distance = role.getGamePlayer().getLastLocation().distance(event.getPlayer().getLocation());
            while (distance >= 100) {
                distance-=100;
                gap--;
            }
            if (gap < 2) {
                gap = 2;
            }
            role.giveItem(event.getPlayer(), false, new ItemStack(Material.GOLDEN_APPLE, gap));
            role.getGamePlayer().sendMessage("§7Suite à la mort de§b "+event.getPlayer().getName()+"§7 vous avez reçus§e "+gap+" pommes d'or");
            customArmorStand.delete(Bukkit.getPlayer(role.getPlayer()));
            this.sporeRunnable.healthPlayers.remove(event.getPlayer().getUniqueId());
        }
        @EventHandler(priority = EventPriority.HIGH)
        private void onDeath2(@NonNull final UHCDeathEvent event) {
            if (event.getRole() == null)return;
            if (!event.getRole().getPlayer().equals(role.getPlayer()))return;
            final List<GamePlayer> list = getListOf100Percent();
            if (list.isEmpty()) {
                event.getRole().getGamePlayer().sendMessage("§cPas de réapparition pour vous.");
            } else {
                GamePlayer gamePlayer = null;
                double distance = 0.0;
                for (GamePlayer player : list) {
                    if (gamePlayer == null) {
                        gamePlayer = player;
                        distance = player.getLastLocation().distance(event.getPlayer().getLocation());
                        continue;
                    }
                    final double d = player.getLastLocation().distance(event.getPlayer().getLocation());
                    if (d > distance) {
                        gamePlayer = player;
                        distance = d;
                    }
                }
                event.setCancelled(true);
                role.getGamePlayer().setLastArmorContent(event.getPlayer().getInventory().getArmorContents());
                role.getGamePlayer().setLastInventoryContent(event.getPlayer().getInventory().getContents());
                this.getArmors = event.getPlayer().getInventory().getArmorContents();
                int i = 0;
                for (ItemStack stack : event.getPlayer().getInventory().getContents()) {
                    if (stack != null && stack.getType() != Material.AIR){
                       // System.out.println("Amount: "+stack.getAmount() + ", hasItemMeta "+stack.hasItemMeta()+", Type: "+stack.getType());
                        getContents.put(i, stack);
                    }
                    i++;
                }
                final Location location = Loc.getRandomLocationAroundPlayer(event.getPlayer(), 20);
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    final Player player = Bukkit.getPlayer(role.getPlayer());
                    if (player == null)return;
                    if (player.isDead()){
                        player.spigot().respawn();
                    }
                    if (location.getWorld().getName().equalsIgnoreCase("arena")) {
                        player.teleport(location);
                    } else {
                        player.teleport(fr.nicknqck.GameListener.generateRandomLocation(Main.getInstance().getWorldManager().getGameWorld()));
                    }
                    player.getInventory().setContents(role.getGamePlayer().getLastInventoryContent());
                    player.getInventory().setArmorContents(this.getArmors);
                    getContents.keySet().stream().filter(z -> getContents.get(z).getAmount() > 0).filter(z -> getContents.get(z).getAmount() <= 64).forEach(z -> player.getInventory().setItem(z, getContents.get(z)));
                }, 20*5);
            }
        }
        @EventHandler
        private void onDeath3(@NonNull final FinalDeathEvent event) {
            if (event.getRole() == null)return;
            if (event.getRole().getClass().getName().toLowerCase().contains("zetsublanc")) {
                this.maxAmountSpore++;
                role.getGamePlayer().sendMessage("§7Votre nombre de§a spore§7 maximum est maintenant de§c "+maxAmountSpore);
            }
        }
        private List<GamePlayer> getListOf100Percent() {
            @NonNull final List<GamePlayer> list = new ArrayList<>();
            if (!this.sporeRunnable.healthPlayers.isEmpty()) {
                for (@NonNull final SporeRunnable.CustomArmorStand value : this.sporeRunnable.healthPlayers.values()) {
                    if (value.percent < 100)continue;
                    final GamePlayer gamePlayer = GamePlayer.of(value.uuid);
                    if (!gamePlayer.isAlive())continue;
                    if (!gamePlayer.isOnline())continue;
                    if (gamePlayer.getRole() == null)continue;
                    list.add(gamePlayer);
                }
            }
            return list;
        }
        private static class SporeItem extends ItemPower {

            private final SporeManager sporeManager;

            public SporeItem(@NonNull RoleBase role, SporeManager sporeManager) {
                super("§aSpores§r", null, new ItemBuilder(Material.NETHER_STAR).setName("§aSpores"), role,
                        "§7Ouvre un menu contenant la tête des joueurs proches",
                        "",
                        "§7En cliquant sur l'une d'elle vous lui attacherez un§a Spore§7.",
                        "",
                        "§7En restant proche des joueurs ayant des§a spores§7, vous augmenterez leurs \"§bPourcentage§7\".",
                        "",
                        "§8 -§7 Si un joueur meurs avec un§b pourcentage§7 supérieur ou égal à§c 75%§7, vous récupérerez la§c moitié§7 de ses§e pommes d'or§7,",
                        "§7s'il avait un§b pourcentage§7 inférieur alors vous récupérerez§e 2 pommes d'or§7.",
                        "§8 -§7 A chacune de vos morts, vous réapparaitrez proche du joueur le plus proche ayant un§b pourcentage§7 égal à§c 100%§7.",
                        "§8 -§7 Lorsqu'un autre§c Zetsu Blanc§7 meurt, votre nombre maximal de§a spores§7 augmente.",
                        "§8 -§7 Vous récupérerez un§e spore§7 toute les§c 5 minutes§7."
                );
                this.sporeManager = sporeManager;
                this.sporeManager.sporeRunnable.runTaskTimerAsynchronously(getPlugin(), 1, 20);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                if (getInteractType().equals(InteractType.INTERACT)) {
                    if (sporeManager.amountSpore > 0) {
                        openInv(player);
                        player.sendMessage("§7Vous vous préparez à libérez vos§a spores§7...");
                        return true;
                    } else {
                        player.sendMessage("§cVous n'avez plus de§a spore§c disponible...");
                        return false;
                    }
                }
                return false;
            }
            private void openInv(@NonNull final Player player) {
                Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> new SporeInventory(player, this).open(player));
            }
            private static class SporeInventory extends PaginatedFastInv {

                private static final InventoryScheme SCHEME = new InventoryScheme()
                        .mask("         ")
                        .mask(" 1111111 ")
                        .bindPagination('1');

                public SporeInventory(@NonNull final Player player, @NonNull final SporeItem sporeItem) {
                    super(9*3, "§aSpores");
                    previousPageItem(20, p -> new ItemBuilder(Material.ARROW).setName("§fPage " + p + "/" + lastPage()).toItemStack());
                    nextPageItem(24, p -> new ItemBuilder(Material.ARROW).setName("§fPage " + p + "/" + lastPage()).toItemStack());

                    for (@NonNull final GamePlayer gamePlayer : Loc.getNearbyGamePlayers(player.getLocation(), 30)) {
                        if (gamePlayer.getRole() == null) continue;
                        if (!gamePlayer.isAlive()) continue;
                        if (!gamePlayer.isOnline())continue;
                        addContent(
                                new ItemBuilder(GlobalUtils.getAsyncPlayerHead(gamePlayer.getUuid()))
                                        .setName("§a"+gamePlayer.getPlayerName())
                                        .toItemStack(),
                                event -> {
                                    if (sporeItem.sporeManager.amountSpore > 0 && event.getWhoClicked() instanceof Player) {
                                        if (sporeItem.sporeManager.sporeRunnable.addSporeTo(gamePlayer, (Player) event.getWhoClicked())) {
                                            event.getWhoClicked().sendMessage("§c"+gamePlayer.getPlayerName()+"§a a reçus un spore.");
                                        } else {
                                            event.getWhoClicked().sendMessage("§cImpossible de donner de§a spore§c a§b "+gamePlayer.getPlayerName());
                                        }
                                    } else {
                                        event.getWhoClicked().sendMessage("§cVous n'avez pas assez de§a spore");
                                    }
                                    event.getWhoClicked().closeInventory();
                                }
                        );
                    }

                    setItem(26, new ItemBuilder(Material.BARRIER).setName("§cFermer").toItemStack(),
                            e -> e.getWhoClicked().closeInventory());
                    setItems(getCorners(), new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());

                    SCHEME.apply(this);
                }
            }
        }
        private static class SporeRunnable extends BukkitRunnable {

            private final SporeManager sporeManager;
            private final Map<UUID, CustomArmorStand> healthPlayers;
            private int timeLeft = 0;

            private SporeRunnable(SporeManager sporeManager) {
                this.sporeManager = sporeManager;
                this.healthPlayers = new HashMap<>();
            }

            @Override
            public void run() {
                if (!GameState.inGame()) {
                    cancel();
                    return;
                }
                this.sporeManager.role.getGamePlayer().getActionBarManager().updateActionBar("zetsublanc.spore", "§aSpore(s):§c "+this.sporeManager.amountSpore+"§7/§6"+this.sporeManager.maxAmountSpore);
                if (!healthPlayers.isEmpty()) {
                    final Player owner = Bukkit.getPlayer(this.sporeManager.role.getPlayer());
                    if (owner != null) {
                        final List<Player> list = new ArrayList<>(Loc.getNearbyPlayersExcept(owner, 30));
                        for (@NonNull final Player player : list) {
                            if (!healthPlayers.containsKey(player.getUniqueId()))continue;
                            final CustomArmorStand customArmorStand = healthPlayers.get(player.getUniqueId());
                            if (customArmorStand.percent >= 100)continue;
                            if (!customArmorStand.addPercent()) {
                                owner.sendMessage("§7Votre§a spore§7 qui était collé à§c "+player.getName()+"§7 a expiré.");
                            }
                        }
                    }
                }
                if (this.timeLeft <= 0) {
                    if (this.sporeManager.amountSpore < this.sporeManager.maxAmountSpore) {
                        this.timeLeft = 60*5;
                        this.sporeManager.role.getGamePlayer().sendMessage("§7Vous avez gagner un§a spore§7.");
                        this.sporeManager.amountSpore++;
                    }
                    return;
                }
                this.timeLeft--;
            }
            public boolean addSporeTo(final GamePlayer gamePlayer, final Player owner) {
                if (!this.healthPlayers.containsKey(gamePlayer.getUuid())) {
                    final CustomArmorStand customArmorStand = new CustomArmorStand(gamePlayer.getLastLocation(), "§bPourcentage:§c 0%", gamePlayer.getUuid());
                    customArmorStand.display(owner);
                    this.healthPlayers.put(gamePlayer.getUuid(), customArmorStand);
                    this.sporeManager.amountSpore--;
                    return true;
                }
                if (this.healthPlayers.get(gamePlayer.getUuid()).getPercent() >= 100) {
                    return false;
                }
                this.healthPlayers.get(gamePlayer.getUuid()).reset();
                return true;
            }
            @Getter
            private static class CustomArmorStand extends ArmorStandUtils {

                private final UUID uuid;
                @Setter
                private int percent = 0;
                private int amountAdded = 0;
                private int amountSpore = 0;

                public CustomArmorStand(Location loc, String text, UUID uuid) {
                    super(loc, text);
                    this.uuid = uuid;
                    amountSpore++;
                }
                public boolean addPercent() {
                    if (amountSpore <=0)return false;
                    if (amountAdded < (25 * amountSpore)) {
                        percent++;
                        amountAdded++;
                        return true;
                    } else {
                        amountSpore = 0;
                        return false;
                    }
                }
                public void reset() {
                    this.amountAdded = 0;
                    amountSpore++;
                }
            }
        }

    }
}