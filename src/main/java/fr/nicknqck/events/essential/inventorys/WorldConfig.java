package fr.nicknqck.events.essential.inventorys;

import fr.nicknqck.GameState;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
public class WorldConfig implements Listener {

    private final GameState gameState;
    @Setter
    private int goldBooster = 1;
    @Setter
    private int diamondBooster = 2;

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
        if (event.getClickedInventory().getTitle().equalsIgnoreCase("§fConfiguration§7 ->§a Monde")) {
            if (item.getType().equals(Material.GOLD_ORE)) {
                openMineralConfig(player);
                event.setCancelled(true);
            }
        }
    }

    public void openInitConfig(Player player) {
        Inventory wConfig = Bukkit.createInventory(player, 54, "§fConfiguration§7 ->§a Monde");
        wConfig.setItem(11, new ItemBuilder(Material.GOLD_ORE).setName("§6Configuration des mineraix").toItemStack());
        player.openInventory(wConfig);
    }
    private void openMineralConfig(Player player) {
        Inventory inv = Bukkit.createInventory(player, 27, "§fConfiguration§7 ->§6 Mineraix");
        inv.setItem(12, new ItemBuilder(Material.GOLD_ORE).setName("§fBoost des mineraix d'§6 or").setLore().toItemStack());
        player.openInventory(inv);
    }
}
