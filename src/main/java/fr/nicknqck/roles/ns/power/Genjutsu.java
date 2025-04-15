package fr.nicknqck.roles.ns.power;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.builders.NSRoles;
import fr.nicknqck.utils.GlobalUtils;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Genjutsu extends ItemPower implements Listener {

    private final TsukuyomiPower tsukuyomi;
    private final AttaquePower attaque;
    private final IzanamiPower izanami;

    public Genjutsu(@NonNull RoleBase role) {
        super("Genjutsu", null, new ItemBuilder(Material.NETHER_STAR)
                .setName("§cGenjutsu"), role,
                "§7Vous permet d'ouvrir un menu vous permettant d'accéder à§c trois pouvoirs§7:",
                "",
                "§8 -§c Tsukuyomi§7: Vous permet de stun pendant§c 8 secondes§7 tout les joueurs autours de vous (§c30 blocs§7). (1x/10m)",
                "",
                "§8 -§c Attaque§7: Vous permet d'ouvrir un menu vous permettant de sélectionner un joueur, celà vous téléportera derrière ce joueur. (1x/5m)",
                "",
                "§8 - "+role.getTeamColor()+"Izanami§7: Vous permet d'ouvrir un menu vous permettant de sélectionner un joueur,",
                "§7Celà vous donnera des missions à faire pour que la cible rejoigne votre camp. (1x/partie)");
        this.tsukuyomi = new TsukuyomiPower(role);
        this.attaque = new AttaquePower(role);
        this.izanami = new IzanamiPower(role);
        EventUtils.registerRoleEvent(this);
        role.addPower(this.tsukuyomi);
        role.addPower(this.attaque);
        role.addPower(this.izanami);
    }

    @Override
    public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
        if (getInteractType().equals(InteractType.INTERACT)) {
            @NonNull final Inventory inv = Bukkit.createInventory(player, 27, "§7(§c!§7)§c Genjutsu");
            inv.setItem(11, new ItemBuilder(Material.ARMOR_STAND).setName("§cTsukuyomi").toItemStack());
            inv.setItem(13, new ItemBuilder(Material.FERMENTED_SPIDER_EYE).setName("§cAttaque").toItemStack());
            inv.setItem(15, new ItemBuilder(Material.NETHER_STAR)
                    .setName(getRole().getTeamColor()+"Izanami")
                    .setLore(findListIzami()).toItemStack());
            player.openInventory(inv);
            return true;
        }
        return false;
    }
    private List<String> findListIzami() {
        @NonNull final List<String> toReturn = new ArrayList<>();
        if (this.izanami.izanami == null) {
            return toReturn;
        }
        toReturn.addAll(this.izanami.izanami.findListUserLore());
        toReturn.add(this.izanami.izanami.findVictimLore());
        return toReturn;
    }
    @EventHandler
    private void InventoryClickEvent(@NonNull final InventoryClickEvent event) {
        if (event.getInventory() == null)return;
        if (event.getCurrentItem() == null)return;
        if (event.getInventory().getTitle() == null)return;
        if (!event.getInventory().getTitle().equals("§7(§c!§7)§c Genjutsu"))return;
        if (!event.getWhoClicked().getUniqueId().equals(getRole().getPlayer()))return;
        if (event.getCurrentItem().getItemMeta() == null)return;
        if (event.getCurrentItem().getItemMeta().getDisplayName() == null)return;
        if (!(event.getWhoClicked() instanceof Player))return;
        if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§cTsukuyomi")) {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
            this.tsukuyomi.checkUse((Player) event.getWhoClicked(), new HashMap<>());
        } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§cAttaque")) {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
            this.attaque.checkUse((Player) event.getWhoClicked(), new HashMap<>());
        } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(getRole().getTeamColor()+"Izanami")) {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
            this.izanami.checkUse((Player) event.getWhoClicked(), new HashMap<>());
        }
    }
    private static class TsukuyomiPower extends Power {

        public TsukuyomiPower(@NonNull RoleBase role) {
            super("Tsukuyomi", new Cooldown(60*10), role);
            setShowInDesc(false);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            @NonNull final List<Player> playerList = Loc.getNearbyPlayersExcept(player, 30);
            if (playerList.isEmpty()) {
                player.sendMessage("§cIl n'y a aucun joueur autours de vous, impossible de lancé le§4 Tsukuyomi§c.");
                return false;
            }
            for (@NonNull final Player target : playerList) {
                if (!(player).canSee(target))continue;
                if (getRole().getGameState().hasRoleNull(target.getUniqueId()))continue;
                @NonNull final GamePlayer gamePlayer = getRole().getGameState().getGamePlayer().get(target.getUniqueId());
                gamePlayer.stun(20*8, true, true);
            }
            return true;
        }
    }
    private static class AttaquePower extends Power implements Listener{

        public AttaquePower(@NonNull RoleBase role) {
            super("Attaque", new Cooldown(60*5), role);
            setShowInDesc(false);
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (map.isEmpty()) {
                @NonNull final Inventory inv = Bukkit.createInventory(player, 54, "§7(§c!§7)§c Attaque");
                for (@NonNull final Player target : Loc.getNearbyPlayersExcept(player, 30)) {
                    inv.addItem(new ItemBuilder(GlobalUtils.getPlayerHead(player.getName())).setName("§c"+target.getName()).toItemStack());
                }
                player.openInventory(inv);
                return false;
            } else {
                return true;
            }
        }
        @EventHandler
        private void InventoryClickEvent(@NonNull final InventoryClickEvent event) {
            if (event.getInventory() == null)return;
            if (event.getCurrentItem() == null)return;
            if (event.getInventory().getTitle() == null)return;
            if (!event.getInventory().getTitle().equals("§7(§c!§7)§c Attaque"))return;
            if (!event.getWhoClicked().getUniqueId().equals(getRole().getPlayer()))return;
            if (event.getCurrentItem().getItemMeta() == null)return;
            if (event.getCurrentItem().getItemMeta().getDisplayName() == null)return;
            if (!(event.getWhoClicked() instanceof Player))return;
            if (!event.getCurrentItem().getType().equals(Material.SKULL_ITEM))return;
            event.setCancelled(true);
            String name = event.getCurrentItem().getItemMeta().getDisplayName();
            if (name.length() > 2) {
                name = name.substring(2);
                @NonNull final Player target = Bukkit.getPlayer(name);
                if (target != null) {
                    event.getWhoClicked().closeInventory();
                    @NonNull final Map<String, Object> map = new HashMap<>();
                    //Il faut juste que la map ne soit pas vide
                    map.put("rien", "RIEN DU TOUT");
                    if (checkUse((Player) event.getWhoClicked(), map)) {
                        event.getWhoClicked().sendMessage("§cAttaque !");
                        Loc.teleportBehindPlayer((Player) event.getWhoClicked(), target);
                        target.sendMessage("§7Vous ressentez une présence derrière vous...");
                    }
                }
            }
        }
    }
    private static class IzanamiPower extends Power implements Listener{

        private IzanamiV2 izanami;

        public IzanamiPower(@NonNull RoleBase role) {
            super("Izanami", null, role);
            setShowInDesc(false);
            EventUtils.registerRoleEvent(this);
            setMaxUse(1);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (map.isEmpty()) {
                @NonNull final Inventory inv = Bukkit.createInventory(player, 54, "§7(§c!§7)§c Izanami");
                for (@NonNull final Player target : Loc.getNearbyPlayersExcept(player, 30)) {
                    inv.addItem(new ItemBuilder(GlobalUtils.getPlayerHead(player.getName())).setName("§c"+target.getName()).toItemStack());
                }
                player.openInventory(inv);
                return false;
            } else {
                return true;
            }
        }
        @EventHandler
        private void InventoryClickEvent(@NonNull final InventoryClickEvent event) {
            if (event.getInventory() == null)return;
            if (event.getCurrentItem() == null)return;
            if (event.getInventory().getTitle() == null)return;
            if (!event.getInventory().getTitle().equals("§7(§c!§7)§c Izanami"))return;
            if (!event.getWhoClicked().getUniqueId().equals(getRole().getPlayer()))return;
            if (event.getCurrentItem().getItemMeta() == null)return;
            if (event.getCurrentItem().getItemMeta().getDisplayName() == null)return;
            if (!(event.getWhoClicked() instanceof Player))return;
            if (!event.getCurrentItem().getType().equals(Material.SKULL_ITEM))return;
            event.setCancelled(true);
            String name = event.getCurrentItem().getItemMeta().getDisplayName();
            if (name.length() > 2) {
                name = name.substring(2);
                @NonNull final Player target = Bukkit.getPlayer(name);
                if (target != null) {
                    event.getWhoClicked().closeInventory();
                    @NonNull final Map<String, Object> map = new HashMap<>();
                    //Il faut juste que la map ne soit pas vide
                    map.put("rien", "RIEN DU TOUT");
                    if (getRole().getGameState().hasRoleNull(target.getUniqueId())) {
                        return;
                    }
                    final GamePlayer gamePlayer = getRole().getGameState().getGamePlayer().get(target.getUniqueId());
                    if (!gamePlayer.isAlive()) {
                        event.getWhoClicked().sendMessage("§cImpossible, ce joueur est MORT.");
                        return;
                    }
                    if (checkUse((Player) event.getWhoClicked(), map)) {
                        @NonNull final IzanamiV2 izanami = new IzanamiV2(getRole().getGamePlayer(), gamePlayer);
                        izanami.start(getRole().getTeamColor());
                        this.izanami = izanami;
                        event.getWhoClicked().sendMessage(this.izanami.getStringsMission());
                        new IzanamiRunnable(getRole().getGameState(), this, getRole().getGameState().getGamePlayer().get(target.getUniqueId()));
                    }
                }
            }
        }
        private static class IzanamiRunnable extends BukkitRunnable {

            private final GameState gameState;
            private final IzanamiPower power;
            private final GamePlayer gameTarget;

            private IzanamiRunnable(GameState gameState, IzanamiPower power, GamePlayer gameTarget) {
                this.gameState = gameState;
                this.power = power;
                this.gameTarget = gameTarget;
                runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (power.izanami == null) {
                    return;
                }
                if (power.izanami.isAllTrue()) {
                    @NonNull final Player owner = Bukkit.getPlayer(this.power.getRole().getPlayer());
                    if (owner != null) {
                        if (power.izanami.onSuccessfullInfection((NSRoles) power.getRole(), this.gameTarget.getRole())) {
                            cancel();
                        }
                    }
                }
            }
        }
    }
}