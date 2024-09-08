package fr.nicknqck.events.essential.inventorys.rconfig.aot;

import fr.nicknqck.GameState;
import fr.nicknqck.HubListener;
import fr.nicknqck.Main;
import fr.nicknqck.events.essential.inventorys.ConfiguratorRole;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.utils.rank.ChatRank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class AOTSolo extends ConfiguratorRole {

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        InventoryAction action = event.getAction();
        ItemStack item = event.getCurrentItem();
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
            if (item.isSimilar(GUIItems.getSelectSoldatButton())) {
                player.openInventory(GUIItems.getSecretSoldatGui());
                Main.getInstance().getInventories().updateSoldatInventory(player);
                return;
            }
            if (!item.isSimilar(GUIItems.getOrangeStainedGlassPane()) && !item.isSimilar(GUIItems.getCantStartGameButton()) && !item.isSimilar(GUIItems.getStartGameButton())) {
                String name = item.getItemMeta().getDisplayName();
                if (action.equals(InventoryAction.PICKUP_ALL)) {
                    addRoles(name);
                } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                    removeRoles(name);
                }
            }
            if (item.isSimilar(GUIItems.getStartGameButton()) && gameState.gameCanLaunch)
                HubListener.getInstance().StartGame(player);

        } else {
            for (UUID u : gameState.getInLobbyPlayers()) {
                Player p = Bukkit.getPlayer(u);
                if (p == null) continue;
                if (p == event.getWhoClicked()) {
                    if (ChatRank.isHost(player)) {
                        p.openInventory(GUIItems.getSelectAOTInventory());
                        Main.getInstance().getInventories().updateAOTInventory(player);
                    }
                    if (!p.isOp() && p.getOpenInventory() != null && p.getInventory() != null)
                        p.closeInventory();
                }
            }
        }
        for (UUID u : gameState.getInLobbyPlayers()) {
            Player p = Bukkit.getPlayer(u);
            if (p == null) continue;
            Main.getInstance().getInventories().updateDSSoloInventory(p);
        }
        event.setCancelled(true);
    }

    @Override
    public String getInvName() {
        return "§fAOT§7 -> §eSolo";
    }
}
