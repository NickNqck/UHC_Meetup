package fr.nicknqck.invs;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.interfaces.IMDJ;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.utils.fastinv.PaginatedFastInv;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SelectModeDeJeuInventory extends PaginatedFastInv {

    public SelectModeDeJeuInventory() {
        super(27, "§fSéléction du mode de jeu");
        setItems(getCorners(), new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        final List<Integer> list = new ArrayList<>();
        for (int i = 10; i <= 16; i++) {
            list.add(i);
        }
        setContentSlots(list);
        setItem(4, GUIItems.getSelectBackMenu(), event -> {
            event.getWhoClicked().openInventory(GUIItems.getRoleSelectGUI());
            Main.getInstance().getInventories().updateRoleInventory((Player) event.getWhoClicked());
        });
        previousPageItem(3, new ItemBuilder(Material.WOOD_BUTTON)
                .setName("§7◄ Page précédente").toItemStack());
        nextPageItem(5, new ItemBuilder(Material.WOOD_BUTTON)
                .setName("§7Page suivante ►").toItemStack());
        for (IMDJ imdj : Main.getInstance().getGameConfig().getPlayableMdj()) {
            addContent(imdj.getItem(), event -> {
                GameState.getInstance().setMdj(imdj);
                new SelectModeDeJeuInventory().open((Player) event.getWhoClicked());
                Main.getInstance().sendMessageToHosts("§c"+event.getWhoClicked().getName()+"§7 a définie le§c mode de jeu§7 sur§c "+imdj.getItem().getItemMeta().getDisplayName());
            });
        }
    }
}
