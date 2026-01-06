package fr.nicknqck.invs;

import fr.nicknqck.Main;
import fr.nicknqck.enums.MDJ;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.utils.fastinv.FastInv;
import fr.nicknqck.utils.fastinv.InventoryScheme;
import fr.nicknqck.utils.fastinv.PaginatedFastInv;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MDJConfigInventory extends PaginatedFastInv {

    private static final InventoryScheme SCHEME = new InventoryScheme()
            .mask("         ")
            .mask(" 1111111 ")
            .bindPagination('1');

    public MDJConfigInventory() {
        super(9*3, "§fConfiguration§7 ->§a Modes de jeux");
        previousPageItem(20, p -> new ItemBuilder(Material.ARROW).setName("§fPage " + p + "/" + lastPage()).toItemStack());
        nextPageItem(24, p -> new ItemBuilder(Material.ARROW).setName("§fPage " + p + "/" + lastPage()).toItemStack());

        if (!Main.getInstance().getGameConfig().getConfigurablesMdj().isEmpty()) {
            for (@NonNull final MDJ mdj : Main.getInstance().getGameConfig().getConfigurablesMdj().keySet()) {
                addContent(new ItemBuilder(mdj.getItem()).setLore().toItemStack(), event -> {
                    try {
                        final FastInv inv = Main.getInstance().getGameConfig().getConfigurablesMdj().get(mdj).newInstance();
                        inv.open((Player) event.getWhoClicked());
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
        setItem(26, GUIItems.getSelectBackMenu(), event -> Main.getInstance().getInventories().updateRoleInventory((Player) event.getWhoClicked()));
        SCHEME.apply(this);
    }
}