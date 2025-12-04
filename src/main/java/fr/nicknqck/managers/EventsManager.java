package fr.nicknqck.managers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.GameEndEvent;
import fr.nicknqck.events.custom.RoleGiveEvent;
import fr.nicknqck.events.custom.roles.TeamChangeEvent;
import fr.nicknqck.events.ds.AkazaVSKyojuroV2;
import fr.nicknqck.events.ds.AllianceV2;
import fr.nicknqck.events.ds.Event;
import fr.nicknqck.events.ds.dkt.DemonKingEvent;
import fr.nicknqck.events.ns.EveilTenseiGan;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

@Getter
public class EventsManager implements Listener {

    private final List<Event> eventsList = new ArrayList<>();
    private boolean tryed = false;

    public EventsManager() {
        eventsList.add(new AllianceV2());
        eventsList.add(new AkazaVSKyojuroV2());
        eventsList.add(new DemonKingEvent());
        eventsList.add(new EveilTenseiGan());
    }

    @EventHandler
    private void onGiveRole(final RoleGiveEvent event) {
        if (tryed)return;
        for (final Event events : eventsList) {
            if (events.isEnable()){
                if (events.getPercent() <= 0)continue;
                int random = Main.RANDOM.nextInt(101);
                if (events.getPercent() >= 100 || random <= events.getPercent()){
                    new EventsRunnables(events, event.getGameState()).runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
                } else {
                    System.out.println("the Event ("+events.getName()+") don't gonna proc with Percent: \""+events.getPercent()+"\" and random: \""+random+"\"");
                }
            }
        }
        tryed = true;
    }
    @EventHandler
    private void onEndGame(final GameEndEvent event) {
        this.tryed = false;
    }
    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player))return;
        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getClickedInventory();
        InventoryAction action = event.getAction();
        if (inv != null && event.getCurrentItem() != null) {
            ItemStack item = event.getCurrentItem();
            if (!item.hasItemMeta())return;
            if (!item.getItemMeta().hasDisplayName())return;
            if (inv.getTitle().equals("§fConfiguration§7 -> §6Événements")) {
                if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                    player.openInventory(GUIItems.getAdminWatchGUI());
                    Main.getInstance().getInventories().updateAdminInventory(player);
                } else {
                    final Event gameEvent = getEvent(item.getItemMeta().getDisplayName());
                    if (action.equals(InventoryAction.PICKUP_ALL)) {
                        gameEvent.setPercent(Math.min(100, gameEvent.getPercent() + 1));
                    } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                        gameEvent.setPercent(Math.max(1, gameEvent.getPercent()) - 1);
                    } else if (action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                        openInv(player, gameEvent.getName(), gameEvent);
                    }
                    Main.getInstance().getInventories().updateEventInventory(player);
                }
                event.setCancelled(true);
                return;
            }
            for (final Event gameEvent : this.eventsList) {
                if (inv.getTitle().equals("§f"+gameEvent.getName())) {
                    if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                        player.openInventory(GUIItems.getEventSelectGUI());
                        Main.getInstance().getInventories().updateEventInventory(player);
                        event.setCancelled(true);
                        return;
                    }
                    final String name = item.getItemMeta().getDisplayName();
                    if (name.equals("§bTemp minimum")) {
                        if (action.equals(InventoryAction.PICKUP_ALL)) {
                            gameEvent.setMinTimeProc(Math.min(gameEvent.getMaxTimeProc(), gameEvent.getMinTimeProc()+60));
                        } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                            gameEvent.setMinTimeProc(Math.max(120, gameEvent.getMinTimeProc())-60);
                        }
                    }
                    if (name.equals("§bTemp maximum")) {
                        if (action.equals(InventoryAction.PICKUP_ALL)) {
                            gameEvent.setMaxTimeProc(gameEvent.getMaxTimeProc()+60);
                        } else if (action.equals(InventoryAction.PICKUP_HALF)){
                            gameEvent.setMaxTimeProc(Math.max(gameEvent.getMinTimeProc()+60, gameEvent.getMaxTimeProc()-60));
                        }
                    }
                    if (item.getType().equals(Material.STAINED_CLAY)) {
                        gameEvent.setEnable(!gameEvent.isEnable());
                    }
                    if (name.equals("§bPourcentage (§cdiminution§b)")) {
                        if (action.equals(InventoryAction.PICKUP_ALL)) {//Clique gauche
                            gameEvent.setPercent(Math.max(0, gameEvent.getPercent()-5));
                        } else if (action.equals(InventoryAction.PICKUP_HALF)){
                            gameEvent.setPercent(Math.max(0, gameEvent.getPercent()-1));
                        }
                    }
                    if (name.equals("§bPourcentage (§aaugmentation§b)")) {
                        if (action.equals(InventoryAction.PICKUP_ALL)) {
                            gameEvent.setPercent(Math.min(100, gameEvent.getPercent()+5));
                        } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                            gameEvent.setPercent(Math.min(100, gameEvent.getPercent()+1));
                        }
                    }
                    openInv(player, gameEvent.getName(), gameEvent);
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }
    private void openInv(final Player player, final String name, final Event event) {
        final Inventory gameInv = Bukkit.createInventory(player, 27, "§f"+name);
        final int minTime = event.getMinTimeProc();
        final int maxTime = event.getMaxTimeProc();
        final boolean enable = event.isEnable();
        gameInv.setItem(9, new ItemBuilder(Material.WATCH).setName("§bTemp minimum").setLore(
                "",
                "§fClique gauche: §a+1 minute",
                "",
                "§fClique droit: §c-1 minute",
                "",
                "§bTemp minimum actuel: §c"+ StringUtils.secondsTowardsBeautiful(minTime)
        ).toItemStack());
        gameInv.setItem(5, new ItemBuilder(Material.STAINED_CLAY)
                .setDurability((enable ? 5 : 14))
                .setName((enable ? "§aActivé" : "§cDésactivé"))
                .toItemStack());
        gameInv.setItem(11, new ItemBuilder(Material.WATCH).setName("§bTemp maximum").setLore(
                "",
                "§fClique gauche: §a+1 minute",
                "",
                "§fClique droit: §c-1 minute",
                "",
                "§bTemp maximum actuel: §c"+ StringUtils.secondsTowardsBeautiful(maxTime)
        ).toItemStack());
        gameInv.setItem(3, new ItemBuilder(Material.SIGN).setName("§fDescriptions de l'Event").setLore(event.getExplications()).toItemStack());
        gameInv.setItem(15, new ItemBuilder(Material.WATCH).setName("§bPourcentage (§cdiminution§b)").setLore(
                "§fPourcentage actuel: §c"+event.getPercent()+"%",
                "",
                "§fClique gauche:§c -5%",
                "§fClique droit:§c -1%").toItemStack());
        gameInv.setItem(17, new ItemBuilder(Material.WATCH).setName("§bPourcentage (§aaugmentation§b)").setLore(
                "§fPourcentage actuel: §a"+event.getPercent()+"%",
                "",
                "§fClique gauche:§a +5%",
                "§fClique droit:§a +1%"
        ).toItemStack());
        gameInv.setItem(4, GUIItems.getSelectBackMenu());
        player.openInventory(gameInv);
    }
    private Event getEvent(final String name) {
        for (final Event event : eventsList) {
            if (name.equals(event.getName()) || name.equals(event.getMenuItem().getItemMeta().getDisplayName())) {
                return event;
            }
        }
        return eventsList.get(0);
    }
    @EventHandler
    private void onChangeTeam(@NonNull final TeamChangeEvent event) {
        if (GameState.getInstance() == null)return;
        if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame))return;
        if (event.getNewTeam() == null)return;
        if (event.getNewTeam().equals(TeamList.Shisui))return;
        if (event.getOldTeam() == null)return;
        if (event.getOldTeam().equals(TeamList.Jubi) ||
                event.getOldTeam().equals(TeamList.Jigoro) ||
                event.getOldTeam().equals(TeamList.Shisui) ||
                event.getOldTeam().equals(TeamList.Sasuke)) {
            event.setCancelled(true);
        }
    }
    private static class EventsRunnables extends BukkitRunnable {

        private final GameState gameState;
        private final Event event;
        private int actualTime;
        private final int timeGonnaProc;

        public EventsRunnables(Event events, GameState gameState) {
            int timeGonnaProc1;
            this.gameState = gameState;
            this.event = events;
            this.actualTime = 0;
            timeGonnaProc1 = 0;
            while (timeGonnaProc1 == 0 || timeGonnaProc1 > events.getMaxTimeProc() || timeGonnaProc1 < events.getMinTimeProc()) {
                timeGonnaProc1 = RandomUtils.getRandomDeviationValue(Main.RANDOM.nextInt(), events.getMinTimeProc(), events.getMaxTimeProc());
            }
            this.timeGonnaProc = timeGonnaProc1;
            System.out.println(events.getName()+" gonna proc at "+timeGonnaProc1+" ("+StringUtils.secondsTowardsBeautiful(timeGonnaProc1)+")");
            if (events.onGameStart(gameState)) {
                System.out.println("Method onGameStart has been successfully return true");
            }
        }

        @Override
        public void run() {
            if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            actualTime++;
            if (this.actualTime >= this.timeGonnaProc) {
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    if (this.event.canProc(this.gameState)) {
                        this.event.onProc(gameState);
                        cancel();
                    }
                });
            }
        }
    }
}