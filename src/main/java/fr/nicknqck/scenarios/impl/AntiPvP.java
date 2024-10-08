package fr.nicknqck.scenarios.impl;

import fr.nicknqck.scenarios.BasicScenarios;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class AntiPvP extends BasicScenarios {

	@Getter
	@Setter
	private static boolean antipvplobby = true;

	@Override
	public String getName() {
		return "Anti-PvP";
	}

	@Override
	public ItemStack getAffichedItem() {
		return new ItemBuilder(Material.STAINED_CLAY, 1 ,5).setName(getName()).setLore("§fL'"+getName()+"§f est actuellement: "+(isAntipvplobby() ? "§aActivé" : "§cDésactivé")).toItemStack();
	}

	@Override
	public void onClick(Player player) {

	}

	public static ItemStack getlobbypvp() {
		ItemStack stack = new ItemStack(Material.STAINED_CLAY, 1, (byte) 5);
		ItemMeta meta = stack.getItemMeta();
		meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setDisplayName(ChatColor.DARK_PURPLE+"Anti-PvP (Lobby)");
		meta.setLore(Collections.singletonList(ChatColor.GOLD + "Désactiver"));
		stack.setItemMeta(meta);
		return stack;
	}

	public static ItemStack getnotlobbypvp() {
		ItemStack stack = new ItemStack(Material.STAINED_CLAY, 1, (byte) 14);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_PURPLE+"Anti-PvP (Lobby)");
		meta.setLore(Collections.singletonList(ChatColor.GOLD + "Activer"));
		stack.setItemMeta(meta);
		return stack;
	}
}
