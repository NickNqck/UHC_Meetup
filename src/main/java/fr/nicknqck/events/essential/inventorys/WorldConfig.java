package fr.nicknqck.events.essential.inventorys;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
public class WorldConfig implements Listener {

    private final GameState gameState;
    @Setter
    private int goldBooster = 0;
    @Setter
    private int diamondBooster = 0;
    @Setter
    private int caveBooster = 0;

    public WorldConfig(final GameState gameState) {
        this.gameState = gameState;
    }

    @EventHandler
    private void onInventoryClick(final InventoryClickEvent event) {
        if (event.getClickedInventory() == null)return;
        if (!(event.getWhoClicked() instanceof Player))return;
        if (event.getCurrentItem() == null)return;
        final ItemStack item = event.getCurrentItem();
        final Player player = (Player) event.getWhoClicked();
        final InventoryAction action = event.getAction();
        if (event.getClickedInventory().getTitle().equalsIgnoreCase("§fConfiguration§7 ->§a Monde")) {
            if (item.getType().equals(Material.GOLD_ORE)) {
                openMineralConfig(player);
                event.setCancelled(true);
            } else if (item.getType().equals(Material.GRASS)) {
                player.closeInventory();
                Main.getInstance().getWorldListener().setEnable(true);
                if (!Main.getInstance().initGameWorld()) {
                    player.sendMessage("§cImpossible de supprimer le monde de jeu actuel, un joueur est peut être encore dedans ?");
                    event.setCancelled(true);
                    return;
                }
                event.getWhoClicked().sendMessage("§7Vous avez crée un nouveau monde.");
                if (gameState.hasPregen) {
                    gameState.hasPregen = false;
                }
                Main.getInstance().getWorldListener().setEnable(false);
                event.setCancelled(true);
            } else if (item.getType().equals(Material.EYE_OF_ENDER)) {
                player.closeInventory();
                player.performCommand("a preview");
                event.setCancelled(true);
            } else if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                player.openInventory(GUIItems.getAdminWatchGUI());
                Main.getInstance().getInventories().updateAdminInventory(player);
                event.setCancelled(true);
            }
        } else if (event.getClickedInventory().getTitle().equalsIgnoreCase("§fConfiguration§7 ->§6 Grottes")) {
            if (item.getType().equals(Material.GOLD_ORE)) {
                if (action.equals(InventoryAction.PICKUP_ALL)) {
                    setGoldBooster(Math.min(3, getGoldBooster()+1));
                } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                    setGoldBooster(Math.max(0, getGoldBooster()-1));
                }
            } else if (item.getType().equals(Material.DIAMOND_ORE)) {
                if (action.equals(InventoryAction.PICKUP_ALL)) {
                    setDiamondBooster(Math.min(3, getDiamondBooster()+1));
                } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                    setDiamondBooster(Math.max(0, getDiamondBooster()-1));
                }
            } else if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                openInitConfig(player);
                event.setCancelled(true);
                return;
            } else if (item.getType().equals(Material.STONE)) {
                if (action.equals(InventoryAction.PICKUP_ALL)) {
                    setCaveBooster(Math.min(600, getCaveBooster()+200));
                } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                    setCaveBooster(Math.max(0, getCaveBooster()-200));
                }
            }
            openMineralConfig(player);
            event.setCancelled(true);
        }

    }

    public void openInitConfig(Player player) {
        Inventory wConfig = Bukkit.createInventory(player, 54, "§fConfiguration§7 ->§a Monde");
        wConfig.setItem(11, new ItemBuilder(Material.GOLD_ORE).setName("§6Configuration des grottes").toItemStack());
        wConfig.setItem(13, new ItemBuilder(Material.EYE_OF_ENDER).setName("§fObserver le monde générer").toItemStack());
        wConfig.setItem(15, new ItemBuilder(Material.GRASS).setName("§aChanger de Monde").toItemStack());
        wConfig.setItem(wConfig.getSize()-1, GUIItems.getSelectBackMenu());
        player.openInventory(wConfig);
    }
    private void openMineralConfig(Player player) {
        Inventory inv = Bukkit.createInventory(player, 27, "§fConfiguration§7 ->§6 Grottes");
        inv.setItem(11, new ItemBuilder(Material.GOLD_ORE).setName("§fBoost des mineraix d'§6 or").setLore("","§7Boost actuel:§c x"+getGoldBooster()).toItemStack());
        inv.setItem(13, new ItemBuilder(Material.STONE).setName("§fBoost des§8 grottes").setLore("","§7Boost actuel: §c x"+getCaveBooster()).toItemStack());
        inv.setItem(15, new ItemBuilder(Material.DIAMOND_ORE).setName("§fBoost des mineraix de§b diamant").setLore("","§7Boost actual:§c x"+getDiamondBooster()).toItemStack());
        inv.setItem(inv.getSize()-1, GUIItems.getSelectBackMenu());
        player.openInventory(inv);
    }
}
