package fr.nicknqck.enums;

import fr.nicknqck.GameState;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

@Getter
public enum MDJ {

    Aucun(new ItemBuilder(Material.WOOL).setName("Aucun").toItemStack()),
    DS(new ItemBuilder(Material.REDSTONE).setName("§6Demon Slayer").toItemStack()),
    AOT(new ItemBuilder(Material.FEATHER).setName("§6AOT").toItemStack()),
    NS(new ItemBuilder(Material.NETHER_STAR).setName("§aNaruto").toItemStack()),
    //	MC(new ItemBuilder(Material.GRASS).setName("§aMinecraft").toItemStack()),
    KRYSTAL(new ItemBuilder(Material.EMERALD_ORE).setName("§dKrystal UHC").toItemStack());

    private final ItemStack item;
    MDJ(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        ItemStack itemC = item.clone();
        ItemMeta iMeta = item.getItemMeta();
        if (GameState.getInstance().getMdj() == this){
            iMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, false);
            iMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            iMeta.setLore(Collections.singletonList("§r§aActivé"));
        } else {
            iMeta.setLore(Collections.singletonList("§r§cDésactivé"));
        }
        itemC.setItemMeta(iMeta);
        return itemC;
    }

}
