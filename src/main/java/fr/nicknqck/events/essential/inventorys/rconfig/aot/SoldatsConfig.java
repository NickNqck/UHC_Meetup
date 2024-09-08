package fr.nicknqck.events.essential.inventorys.rconfig.aot;

import fr.nicknqck.GameState;
import fr.nicknqck.HubListener;
import fr.nicknqck.Main;
import fr.nicknqck.events.essential.inventorys.ConfiguratorRole;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.utils.rank.ChatRank;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class SoldatsConfig extends ConfiguratorRole {

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        InventoryAction action = event.getAction();
        GameState gameState = GameState.getInstance();
        if (item.isSimilar(GUIItems.getx())) {
            event.setCancelled(true);
            return;
        }
        if (!ChatRank.isHost(player)) {
            event.setCancelled(true);
            return;
        }
        if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
            event.setCancelled(true);
            if (item.isSimilar(GUIItems.getSelectMahrButton())) {
                player.openInventory(GUIItems.getMahrGui());
                Main.getInstance().getInventories().updateMahrInventory(player);
                return;
            }
            if (item.isSimilar(GUIItems.getSelectTitanButton())) {
                player.openInventory(GUIItems.getSecretTitansGui());
                Main.getInstance().getInventories().updateSecretTitansInventory(player);
                return;
            }
            if (item.isSimilar(GUIItems.getSelectSoloButton())) {
                player.openInventory(GUIItems.getAOTSoloSelectGUI());
                Main.getInstance().getInventories().updateAOTSoloInventory(player);
                return;
            }
            if (item.isSimilar(GUIItems.getSelectSoldatButton())) {
                player.openInventory(GUIItems.getSecretSoldatGui());
                Main.getInstance().getInventories().updateSoldatInventory(player);
                return;
            }
            if (item.isSimilar(GUIItems.getSelectConfigAotButton())) {
                player.openInventory(GUIItems.getConfigurationAOT());
                Main.getInstance().getInventories().updateAOTConfiguration(player);
                return;
            }
            if (!item.isSimilar(GUIItems.getGreenStainedGlassPane()) && !item.isSimilar(GUIItems.getCantStartGameButton()) && !item.isSimilar(GUIItems.getStartGameButton())) {
                String name = item.getItemMeta().getDisplayName();
                if (action.equals(InventoryAction.PICKUP_ALL)) {
                    addRoles(name);
                } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                    removeRoles(name);
                }
            }
            if (item.isSimilar(GUIItems.getStartGameButton()) && gameState.gameCanLaunch) HubListener.getInstance().StartGame(player);
        } else {
            if (ChatRank.isHost(player))player.openInventory(GUIItems.getSelectAOTInventory());
            Main.getInstance().getInventories().updateAOTInventory(player);
        }
        event.setCancelled(true);
    }

    @Override
    public String getInvName() {
        return "§fAOT§7 ->§a Soldats";
    }
}
