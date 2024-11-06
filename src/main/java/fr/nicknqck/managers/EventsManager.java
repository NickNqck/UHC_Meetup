package fr.nicknqck.managers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.StartGameEvent;
import fr.nicknqck.events.ds.AkazaVSKyojuroV2;
import fr.nicknqck.events.ds.AllianceV2;
import fr.nicknqck.events.ds.Event;
import fr.nicknqck.events.ds.dkt.DemonKingEvent;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.Getter;
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

    public EventsManager() {
        eventsList.add(new AllianceV2());
        eventsList.add(new AkazaVSKyojuroV2());
        eventsList.add(new DemonKingEvent());
    }

    @EventHandler
    private void onStartGame(StartGameEvent event) {
        for (final Event events : eventsList) {
            new EventsRunnables(events, event.getGameState()).runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
        }
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
                        gameEvent.setPercent(Math.max(0, gameEvent.getPercent()) - 1);
                    } else if (action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                        openInv(player, gameEvent.getName(), gameEvent.getMinTimeProc(), gameEvent.getMaxTimeProc());
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
                    if (item.getItemMeta().getDisplayName().equals("§bTemp minimum")) {
                        if (action.equals(InventoryAction.PICKUP_ALL)) {
                            gameEvent.setMinTimeProc(Math.min(gameEvent.getMaxTimeProc(), gameEvent.getMinTimeProc()+60));
                        } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                            gameEvent.setMinTimeProc(Math.max(120, gameEvent.getMinTimeProc())-60);
                        }
                    } else if (item.getItemMeta().getDisplayName().equals("§bTemp maximum")) {
                        if (action.equals(InventoryAction.PICKUP_ALL)) {
                            gameEvent.setMaxTimeProc(gameEvent.getMaxTimeProc()+60);
                        } else if (action.equals(InventoryAction.PICKUP_HALF)){
                            gameEvent.setMaxTimeProc(Math.max(gameEvent.getMinTimeProc()+60, gameEvent.getMaxTimeProc()-60));
                        }
                    }
                    openInv(player, gameEvent.getName(), gameEvent.getMinTimeProc(), gameEvent.getMaxTimeProc());
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }
    private void openInv(final Player player, final String name, final int minTime, final int maxTime) {
        final Inventory gameInv = Bukkit.createInventory(player, 27, "§f"+name);
        gameInv.setItem(12, new ItemBuilder(Material.WATCH).setName("§bTemp minimum").setLore(
                "",
                "§fClique gauche: §a+1 minute",
                "",
                "§fClique droit: §c-1 minute",
                "",
                "§bTemp minimum actuel: §c"+ StringUtils.secondsTowardsBeautiful(minTime)
        ).toItemStack());
        gameInv.setItem(14, new ItemBuilder(Material.WATCH).setName("§bTemp maximum").setLore(
                "",
                "§fClique gauche: §a+1 minute",
                "",
                "§fClique droit: §c-1 minute",
                "",
                "§bTemp maximum actuel: §c"+ StringUtils.secondsTowardsBeautiful(maxTime)
        ).toItemStack());
        gameInv.setItem(gameInv.getSize()-1, GUIItems.getSelectBackMenu());
        player.openInventory(gameInv);
    }
    private Event getEvent(final String name) {
        for (final Event event : eventsList) {
            if (name.equals(event.getName())) {
                return event;
            }
        }
        return eventsList.getFirst();
    }
    private static class EventsRunnables extends BukkitRunnable {

        private final GameState gameState;
        private final Event event;
        private int actualTime;

        public EventsRunnables(Event events, GameState gameState) {
            this.gameState = gameState;
            this.event = events;
            this.actualTime = 0;
        }

        @Override
        public void run() {
            if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            actualTime++;
            if (this.actualTime >= event.getMinTimeProc()) {
                if (Main.RANDOM.nextInt(100) <= 5) {
                    this.event.onProc(gameState);
                    cancel();
                    return;
                }
            }
            if (this.actualTime == event.getMaxTimeProc()) {
                this.event.onProc(gameState);
                cancel();
            }
        }
    }
}