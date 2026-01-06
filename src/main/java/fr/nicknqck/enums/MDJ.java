package fr.nicknqck.enums;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.function.Consumer;

@Getter
public enum MDJ {

    Aucun(new ItemBuilder(Material.WOOL).setName("Aucun").toItemStack(),null ),
    DS(new ItemBuilder(Material.REDSTONE).setName("§6Demon Slayer").toItemStack(), event ->{
        event.openInventory(GUIItems.getDemonSlayerInventory());
        Main.getInstance().getInventories().updateDSInventory(event);
    }),
    AOT(new ItemBuilder(Material.FEATHER).setName("§6AOT").toItemStack(), event -> {
        event.openInventory(GUIItems.getSelectAOTInventory());
        Main.getInstance().getInventories().updateAOTInventory(event);
    }),
    NS(new ItemBuilder(Material.NETHER_STAR).setName("§aNaruto").toItemStack(), event -> {
        event.openInventory(GUIItems.getSelectNSInventory());
        Main.getInstance().getInventories().updateNSInventory(event);
    }),
    //	MC(new ItemBuilder(Material.GRASS).setName("§aMinecraft").toItemStack()),
    KRYSTAL(new ItemBuilder(Material.EMERALD_ORE).setName("§dKrystal UHC").toItemStack(), null);

    private final ItemStack item;
    private final Consumer<Player> consumer;

    MDJ(ItemStack item, Consumer<Player> consumer) {
        this.item = item;
        this.consumer = consumer;
    }

    public ItemStack getItem() {
        ItemStack itemC = item.clone();
        ItemMeta iMeta = item.getItemMeta();
        if (GameState.getInstance().getMdj() == this){
            iMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, false);
            iMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            iMeta.setLore(Collections.singletonList("§aActivé"));
        } else {
            iMeta.setLore(Collections.singletonList("§cDésactivé"));
        }
        itemC.setItemMeta(iMeta);
        return itemC;
    }

}
