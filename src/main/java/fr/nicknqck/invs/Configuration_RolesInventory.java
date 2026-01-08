package fr.nicknqck.invs;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.MDJ;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.utils.fastinv.FastInv;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.rank.ChatRank;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class Configuration_RolesInventory extends FastInv {

    public Configuration_RolesInventory(final Player player) {
        super(9*3, "§7(§c!§7) §fConfiguration§7 ->§6 Roles");

        if (GameState.getInstance().isAllMdjNull()) {
            setItem(13, new ItemBuilder(Material.SIGN).setName("§7Aucun mode de jeux activé !").toItemStack());
        } else {
            for (@NonNull final MDJ mdj : MDJ.values()) {
                if (!mdj.equals(GameState.getInstance().getMdj()))continue;
                setItem(13, mdj.getItem(), event -> {
                    if (mdj.getConsumer() != null){
                        mdj.getConsumer().accept(player);
                    } else {
                        event.getWhoClicked().sendMessage("§cAucun action n'a été définie.");
                    }
                });
                break;
            }
        }
        if (ChatRank.isHost(player)) {
            setItem(25, new ItemBuilder(Material.BOOKSHELF).setName("Configuration du mode de jeu").toItemStack(), event -> {
                Inventory inventaire = Bukkit.createInventory(player, 9, "Séléction du mode de jeu");
                player.openInventory(inventaire);
                Main.getInstance().getInventories().updateSelectMDJ(player);
            });
            if (GameState.getInstance().isAllMdjNull()) {
                setItem(18, new ItemBuilder(Material.CAULDRON_ITEM).setName("§fAppuyer pour configurer d'autres modes de jeux").toItemStack(), inventoryClickEvent -> new MDJConfigInventory().open(player));
            } else {
                final MDJ mdj = GameState.getInstance().getMdj();
                if (mdj != null) {
                    if (Main.getInstance().getGameConfig().getConfigurablesMdj().containsKey(mdj)) {
                        setItem(18, new ItemBuilder(mdj.getItem()).setName("§fAppuyer pour configurer le mode "+mdj.getItem().getItemMeta().getDisplayName()).setLore().toItemStack(), event -> {
                            try {
                                final FastInv inv = Main.getInstance().getGameConfig().getConfigurablesMdj().get(mdj).newInstance();
                                inv.open((Player) event.getWhoClicked());
                            } catch (InstantiationException | IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                }
            }
        }
        setItem(26, GUIItems.getSelectBackMenu(), event -> {
            if (ChatRank.isHost(player)) {
                player.openInventory(GUIItems.getAdminWatchGUI());
                Main.getInstance().getInventories().updateAdminInventory(player);
            }
            if (!player.isOp() && player.getOpenInventory() != null && player.getInventory() != null) player.closeInventory();
        });
    }
}
