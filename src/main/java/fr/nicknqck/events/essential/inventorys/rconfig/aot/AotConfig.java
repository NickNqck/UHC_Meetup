package fr.nicknqck.events.essential.inventorys.rconfig.aot;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.essential.inventorys.ConfiguratorRole;
import fr.nicknqck.items.GUIItems;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class AotConfig extends ConfiguratorRole {

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        InventoryAction action = event.getAction();
        GameState gameState = GameState.getInstance();
        String name = item.getItemMeta().getDisplayName();
        if (name.equals("§rCooldown Equipement Tridimentionnel")) {
            if (action.equals(InventoryAction.PICKUP_ALL)) {
                gameState.TridiCooldown+=1;
            }else {
                if (gameState.TridiCooldown > 0) {
                    gameState.TridiCooldown-=1;
                }
            }
        }
        if (name.equals("§rEquipement Tridimentionnel")){
            gameState.rod = !gameState.rod;
        }
        if (name.equals("§r§6Lave§f pour les titans (transformé)")) {
            gameState.LaveTitans = !gameState.LaveTitans;
        }
        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
            player.openInventory(GUIItems.getMahrGui());
            Main.getInstance().getInventories().updateMahrInventory(player);
        }
        Main.getInstance().getInventories().updateAOTConfiguration(player);
        event.setCancelled(true);
    }

    @Override
    public String getInvName() {
        return "Configuration -> AOT";
    }
}
