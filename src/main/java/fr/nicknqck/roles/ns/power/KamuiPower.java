package fr.nicknqck.roles.ns.power;

import fr.nicknqck.GameState;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.GlobalUtils;
import fr.nicknqck.utils.KamuiDimension;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class KamuiPower extends ItemPower implements Listener{

    @Getter
    private final KamuiDimension kamuiDimension;
    private final Arimasu arimasu;
    private final Sonohaka sonohaka;

    public KamuiPower(@NonNull RoleBase role) {
        super("Kamui", null, new ItemBuilder(Material.NETHER_STAR).setName("§dKamui"), role,
                "§7Vous ouvre un menu vous permettant d'accéder à§c deux pouvoirs§7:",
                "",
                "§dArimasu§7: Vous permet de rentrer dans la§c dimension Kamui§7 pendant une durée maximal de§c 5 minutes§7. (1x/5m)",
                "",
                "§dSonohaka§7: Vous permet d'ouvrir un autre menu vous permettant de cibler un joueur,",
                "§7La personne cibler ce verra téléporter dans la§c dimension Kamui§7 pendant§c 5 minutes§7. (1x/10m)"
        );
        if (getPlugin().getRoleWorldManager().getSubRoleWorldMap().containsKey("Kamui") && getPlugin().getRoleWorldManager().getSubRoleWorldMap().get("Kamui") instanceof KamuiDimension) {
            this.kamuiDimension = (KamuiDimension) getPlugin().getRoleWorldManager().getSubRoleWorldMap().get("Kamui");
        } else {
            this.kamuiDimension = new KamuiDimension();
        }
        this.arimasu = new Arimasu(role, this);
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

        private final KamuiPower kamuiPower;

        public Arimasu(@NonNull RoleBase role, @NonNull KamuiPower kamuiPower) {
            super("Kamui§7 (§dArimasu§7)", new Cooldown(60*8), role);
            this.kamuiPower = kamuiPower;
            setShowInDesc(false);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            final List<Location> locationList = new ArrayList<>(this.kamuiPower.kamuiDimension.getPossibleTeleportLocations());
            Collections.shuffle(locationList);
            this.kamuiPower.kamuiDimension.getBeforeTpMap().put(player.getUniqueId(), player.getLocation());
            player.teleport(locationList.get(0));
            player.sendMessage("§7Vous vous êtes aspirer dans le§d Kamui§7.");
            new ArimasuRunnable(getRole().getGameState(), this);
            return true;
        }
        private static final class ArimasuRunnable extends BukkitRunnable {

            private final GameState gameState;
            private final Arimasu arimasu;
            private int timeLeft = 60*5;

            private ArimasuRunnable(GameState gameState, Arimasu arimasu) {
                this.gameState = gameState;
                this.arimasu = arimasu;
                arimasu.getRole().getGamePlayer().getActionBarManager().addToActionBar("power.kamui.arimasu", "§bTemp restant dans le§d Kamui§b: §c3 minutes");
                runTaskTimerAsynchronously(arimasu.getPlugin(), 0, 20);
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                arimasu.getRole().getGamePlayer().getActionBarManager().updateActionBar("power.kamui.arimasu", "§bTemp restant dans le§d Kamui§b:§c "+StringUtils.secondsTowardsBeautiful(this.timeLeft));

                final Player owner = Bukkit.getPlayer(this.arimasu.getRole().getPlayer());
                if (owner != null) {
                    if (this.timeLeft <= 0 || !owner.getWorld().getName().equalsIgnoreCase("Kamui")) {
                        Bukkit.getScheduler().runTask(this.arimasu.getPlugin(), () -> {
                            if (this.arimasu.kamuiPower.kamuiDimension.getBeforeTpMap().containsKey(owner.getUniqueId())) {
                                owner.teleport(this.arimasu.kamuiPower.kamuiDimension.getBeforeTpMap().get(owner.getUniqueId()));
                            }
                        });
                        this.arimasu.getRole().getGamePlayer().getActionBarManager().removeInActionBar("power.kamui.arimasu");
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
            super("Kamui§7 (§dSonohaka§7)§r", new Cooldown(60*10), role);
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
                    final GamePlayer gamePlayer = GamePlayer.of(p.getUniqueId());
                    if (gamePlayer == null)continue;
                    if (!gamePlayer.check())continue;
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
                            final GamePlayer gamePlayer = GamePlayer.of(target.getUniqueId());
                            if (gamePlayer == null || !gamePlayer.check()) {
                                event.getWhoClicked().sendMessage("§cUne erreur c'est produite, impossible de toucher§b "+target.getName()+"§c !");
                                return;
                            }
                            Map<String, Object> map = new HashMap<>();
                            map.put("((Player)event.getWhoClicked())", event.getClick());
                            if (checkUse((Player) event.getWhoClicked(), map)){
                                final List<Location> locationList = new ArrayList<>(this.kamuiPower.kamuiDimension.getPossibleTeleportLocations());
                                Collections.shuffle(locationList);
                                this.kamuiPower.kamuiDimension.getBeforeTpMap().put(target.getUniqueId(), target.getLocation());
                                target.teleport(locationList.get(0));
                                target.sendMessage("§7Vous avez été aspirer dans le§d Kamui§7.");
                                new SonoHakaRunnable(this, gamePlayer).runTaskTimerAsynchronously(getPlugin(), 0, 20);
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
        private static final class SonoHakaRunnable extends BukkitRunnable {

            private final Sonohaka sonohaka;
            private final GamePlayer gamePlayer;
            private int timeLeft = 60*5;

            private SonoHakaRunnable(Sonohaka sonohaka, GamePlayer gamePlayer) {
                this.sonohaka = sonohaka;
                this.gamePlayer = gamePlayer;
            }

            @Override
            public void run() {
                if (!GameState.inGame()) {
                    cancel();
                    return;
                }
                this.gamePlayer.getActionBarManager().updateActionBar("power.kamui.sonohaka", "§bTemps restant (§cSonohaka§b):§c "+StringUtils.secondsTowardsBeautiful(this.timeLeft));
                final Player target = Bukkit.getPlayer(this.gamePlayer.getUuid());
                if (target == null)return;
                if (this.timeLeft <= 0 || !target.getWorld().getName().equalsIgnoreCase("kamui")) {
                    this.gamePlayer.getActionBarManager().removeInActionBar("power.kamui.sonohaka");
                    Bukkit.getScheduler().runTask(this.sonohaka.getPlugin(), () -> {
                        if (this.sonohaka.kamuiPower.kamuiDimension.getBeforeTpMap().containsKey(target.getUniqueId())) {
                            target.teleport(this.sonohaka.kamuiPower.kamuiDimension.getBeforeTpMap().get(target.getUniqueId()));
                            this.sonohaka.getRole().getGamePlayer().sendMessage("§c"+target.getName()+"§7 s'est échapper du§d Kamui§7.");
                            target.sendMessage("§7Vous vous êtes échapper du§d Kamui§7.");
                        }
                    });
                    cancel();
                    return;
                }
                this.timeLeft--;
            }
        }
    }
}