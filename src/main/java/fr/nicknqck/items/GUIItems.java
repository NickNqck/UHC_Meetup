package fr.nicknqck.items;

import java.util.Arrays;
import java.util.Collections;

import fr.nicknqck.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.nicknqck.GameState;
import fr.nicknqck.utils.itembuilder.ItemBuilder;

public abstract class GUIItems{
	
	public static ItemStack getEnderPearl() {
		ItemStack stack = new ItemStack(Material.ENDER_PEARL, Main.getInstance().getGameConfig().getStuffConfig().getPearl());
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Nombre d'Ender Pearl");
		meta.setLore(Arrays.asList("§r"+"Click To Change",
				ChatColor.GOLD+"Minimum: "+"0",
				ChatColor.GOLD+"Maximum: "+"1"));
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getGoldenCarrot() {
		ItemStack stack = new ItemStack(Material.GOLDEN_CARROT, 64);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Nombre de Golden Carrot");
		meta.setLore(Arrays.asList("§r"+"Non Modifiable"));
		stack.setItemMeta(meta);
		return stack;
	}

	public static ItemStack getFilForce() {	
		ItemStack stack = new ItemStack(Material.STRING, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Fil de Force");
		meta.setLore(Arrays.asList("§r"+"Vous obtiendrez: ",
				ChatColor.GOLD+"Force 1: "+"pendant 1m"));
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getFilSpeed() {
		ItemStack stack = new ItemStack(Material.STRING, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Fil de Speed");
		meta.setLore(Arrays.asList("§r"+"Vous obtiendrez: ",
				ChatColor.GOLD+"Speed 1: "+"pendant 1m"));
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getFilResi() {
		ItemStack stack = new ItemStack(Material.STRING, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Fil de Resistance");
		meta.setLore(Arrays.asList("§r"+"Vous obtiendrez: ",
				ChatColor.GOLD+"Resistance 1: "+"pendant 1m"/*,
				ChatColor.GOLD+"Cooldown: "+ChatColor.AQUA+Rui.getcdresi()+"s"*/));
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getFilRegen() {
		ItemStack stack = new ItemStack(Material.STRING, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Fil de Regeneration");
		meta.setLore(Arrays.asList("§r"+"Vous obtiendrez: ",
				ChatColor.GOLD+"Regeneration 1: "+"pendant 1m"/*,
				ChatColor.GOLD+"Cooldown: "+ChatColor.AQUA+Rui.getcdfilregen()+"s"*/));
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getPacte1() {
		ItemStack stack = new ItemStack(Material.APPLE, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"1");
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getPacte2() {
		ItemStack stack = new ItemStack(Material.GOLDEN_APPLE, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"2");
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getPacte3() {
		ItemStack stack = new ItemStack(Material.ARMOR_STAND, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"3");
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getJigoroPacte1() {
		ItemStack stack = new ItemStack(Material.NETHER_STAR, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Pacte 1");
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getJigoroPacte2() {
		ItemStack stack = new ItemStack(Material.INK_SACK, 1, (byte) 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Pacte 2");
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getJigoroPacte3() {
		ItemStack stack = new ItemStack(Material.INK_SACK, 1, (byte) 11);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Pacte 3");
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		return stack;
	}
	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	  public static ItemStack getx() {
		  ItemStack s = new ItemStack(Material.BARRIER, 1);
		  ItemMeta m = s.getItemMeta();
		  m.setDisplayName(ChatColor.RED+"§l✘");
		  s.setItemMeta(m);
		  return s;
	  }
	  public static ItemStack getdiamondsword() {
			ItemStack stack = new ItemStack(Material.DIAMOND_SWORD, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.addEnchant(Enchantment.DAMAGE_ALL, Main.getInstance().getGameConfig().getStuffConfig().getSharpness(), true);
			meta.setLore(Arrays.asList("§r"+"Click to Change",
					"Minimum: "+ChatColor.GOLD+"Sharpness 1",
					"§r"+"Maximal: "+ChatColor.GOLD+"Sharpness 5"));
			stack.setItemMeta(meta);
			return stack;
		}
	  public static ItemStack getblock() {
			ItemStack stack = new ItemStack(Material.BRICK, 1*(Main.getInstance().getGameConfig().getStuffConfig().getNmbblock()));
			ItemMeta meta = stack.getItemMeta();
			meta.setLore(Arrays.asList("§r"+"Click to Change",
					"Minimum: "+ChatColor.GOLD+"1 Stack",
					"§r"+"Maximal: "+ChatColor.GOLD+"4 Stack"));
			stack.setItemMeta(meta);
			return stack;
		}
	  public static ItemStack getbow() {
			ItemStack stack = new ItemStack(Material.BOW, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.addEnchant(Enchantment.ARROW_DAMAGE, Main.getInstance().getGameConfig().getStuffConfig().getPower(), true);
			meta.setLore(Arrays.asList("§r"+"Click to Change",
					"Minimum: "+ChatColor.GOLD+"Power 1",
					"§r"+"Maximum: "+ChatColor.GOLD+"Power 5"));
			stack.setItemMeta(meta);
			return stack;
		}
	  public static ItemStack geteau() {
			ItemStack stack = new ItemStack(Material.WATER_BUCKET, GameState.eau);
			ItemMeta meta = stack.getItemMeta();
			meta.setLore(Arrays.asList("§r"+"Click to Change",
					"Minimum: "+ChatColor.GOLD+"1 Sceau d'Eau",
					"§r"+"Maximum: "+ChatColor.GOLD+"4 Sceau d'Eau"));
			stack.setItemMeta(meta);
			return stack;
		}
	  public static ItemStack getlave() {
			ItemStack stack = new ItemStack(Material.LAVA_BUCKET, GameState.lave);
			ItemMeta meta = stack.getItemMeta();
			meta.setLore(Arrays.asList("§r"+"Click to Change",
					"Minimum: "+ChatColor.GOLD+"0 Sceau de lave",
					"§r"+"Maximum: "+ChatColor.GOLD+"4 Sceau de lave"));
			stack.setItemMeta(meta);
			return stack;
		}
	public static ItemStack getKyogaiSolo() {
			ItemStack stack = new ItemStack(Material.DIAMOND_SWORD, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD+"Choix: Solo");
			stack.setItemMeta(meta);
			return stack;
		}
		public static ItemStack getKyogaiDémon() {
			ItemStack stack = new ItemStack(Material.REDSTONE, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD+"Choix: Démon");
			stack.setItemMeta(meta);
			return stack;
		}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		public static ItemStack getTabRoleInfo(GameState gameState) {
			ItemStack stack = new ItemStack(Material.SIGN, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName("§r§fRoles dans le TAB");
			meta.setLore(Collections.singletonList(ChatColor.RESET+a(gameState)));
			meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			stack.setItemMeta(meta);
			return stack;
		}
		private static String a(final GameState gameState) {
			return gameState.roletab ? "Activer" : "Désactiver"; 
		}
		private static String b(final GameState gameState) {
			return gameState.hasPregen ? "Faite" : "Pas faite";
		}
		public static ItemStack getPregen(GameState gameState) {
			ItemStack stack = new ItemStack(Material.SIGN, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD+"Pregen");
			meta.setLore(Arrays.asList(ChatColor.WHITE+b(gameState)));
			meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			stack.setItemMeta(meta);
			return stack;
		}

		public static ItemStack getCrit(GameState gameState) {
			ItemStack stack = new ItemStack(Material.IRON_SWORD, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD+"Pourcentage de nerf des coups critique");
			meta.setLore(Arrays.asList("§r"+"(50% = non nerf) "+ChatColor.GOLD+ Main.getInstance().getGameConfig().getCritPercent()+"%"));
			stack.setItemMeta(meta);
			return stack;
		}
		public static ItemStack getStartGameButton() {
		ItemStack stack = new ItemStack(Material.STAINED_CLAY, 1, (byte) 5); // Green
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Commencer la partie");
		meta.setLore(Arrays.asList("§r"+"Demarre instantanément la partie"));
		stack.setItemMeta(meta);
		return stack;
	}
		public static ItemStack getCantStartGameButton() {
		ItemStack stack = new ItemStack(Material.STAINED_CLAY, 1, (byte) 14); // Red
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Commencer la partie");
		meta.setLore(Arrays.asList("§r"+"Demarre instantanément la partie"));
		stack.setItemMeta(meta);
		return stack;
	}
	
	public static ItemStack getSelectRoleButton() {
		ItemStack stack = new ItemStack(Material.BOOK, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("§fConfiguration§7 ->§6 Roles");
		meta.setLore(Arrays.asList("§7Choisissez les roles jouables dans cette partie"));
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getSelectScenarioButton() {
		ItemStack stack = new ItemStack(Material.BOOK_AND_QUILL, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("§fConfiguration§7 ->§6 Scénario");
		meta.setLore(Arrays.asList("§r"+"Choisissez les Scenarios de cette partie"));
		stack.setItemMeta(meta);
		return stack;
	}
public static ItemStack getSelectConfigButton() {
		ItemStack stack = new ItemStack(Material.WATCH, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("§fConfiguration§7 ->§6 Partie");
		meta.setLore(Collections.singletonList("§r"+"Configurez l'entièretée de la partie"));
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getSelectInvsButton() {
		ItemStack stack = new ItemStack(Material.CHEST, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("§fConfiguration§7 ->§6 Inventaire");
		meta.setLore(Collections.singletonList("§7Configurez l'inventaire"));
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getGreenStainedGlassPane() {
		ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+" ");
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getRedStainedGlassPane() {
		ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+" ");
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getPurpleStainedGlassPane() {
		return new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability((short)10).setName(" ").toItemStack();
	}
	public static ItemStack getOrangeStainedGlassPane() {
		ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+" ");
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getPinkStainedGlassPane() {
		return new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").setDurability((short)6).toItemStack();
	}
	public static ItemStack getSBluetainedGlassPane() {
		ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 11);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+" ");
		stack.setItemMeta(meta);
		return stack;
	}
	
	public static ItemStack getSelectBackMenu() {
		ItemStack stack = new ItemStack(Material.ARROW, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Retour");
		meta.setLore(Arrays.asList("§r"+"Permet de retourner dans le menu précédent"));
		meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
		  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getSelectSlayersButton() {
		ItemStack stack = new ItemStack(Material.IRON_SWORD, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Slayers");
		meta.setLore(Arrays.asList("§r"+"Choisissez les roles jouables dans cette partie"));
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getSelectSoldatButton() {
		ItemStack stack = new ItemStack(Material.IRON_SWORD, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Soldats");
		meta.setLore(Arrays.asList("§r"+"Choisissez les roles jouables dans cette partie"));
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		
		stack.setItemMeta(meta);
		return stack;
	}
	
	public static Inventory getSlayersSelectGUI() {
        return Bukkit.createInventory(null, 54, "DemonSlayer ->§a Slayers");
	}
	public static ItemStack getSelectDemonButton() {
		ItemStack stack = new ItemStack(Material.REDSTONE_BLOCK, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Démons");
		meta.setLore(Arrays.asList("§r"+"Choisissez les roles jouables dans cette partie"));
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getSelectAkatsukiButton() {
		return new ItemBuilder(Material.INK_SACK).setDurability((short) 1).setName("§cAkatsuki").toItemStack();
	}
	public static ItemStack getSelectOrochimaruButton() {
		return new ItemBuilder(Material.INK_SACK).setDurability((short)5).setName("§5Orochimaru").toItemStack();
	}
	public static ItemStack getSelectJubiButton() {
		return new ItemBuilder(Material.INK_SACK).setDurability((short)13).setName("§dJubi").toItemStack();
	}
	public static ItemStack getSelectBrumeButton() {
		return new ItemBuilder(Material.INK_SACK).setDurability(12).setName("§bZabuza et Haku").toItemStack();
	}
	public static ItemStack getSelectShinobiButton() {
		return new ItemBuilder(Material.INK_SACK).setDurability(10).setName("§aShinobi").toItemStack();
	}
	public static ItemStack getSelectKumogakureButton() {
		return new ItemBuilder(Material.INK_SACK).setDurability(14).setName("§6Kumogakure").toItemStack();
	}
	public static Inventory getSelectNSShinobiInventory() {
		return Bukkit.createInventory(null, 54, "§aNaruto§7 ->§a Shinobi");
	}
	public static Inventory getSelectNSBrumeInventory() {
		return Bukkit.createInventory(null, 54, "§eSolo§7 ->§b Zabuza et Haku");
	}
	public static ItemStack getSelectOverworldButton() {
		  return new ItemBuilder(Material.GRASS).setName("§aOverworld").toItemStack();
	}
	public static Inventory getSelectNSJubiInventory() {
		return Bukkit.createInventory(null, 54, "§eSolo§7 ->§d Jubi");
	}
	public static Inventory getSelectNSSoloInventory() {
		return Bukkit.createInventory(null, 54, "§aNaruto§7 ->§e Solo");
	}
	public static Inventory getSelectOrochimaruInventory() {
		return Bukkit.createInventory(null, 54, "§aNaruto§7 ->§5 Orochimaru");
	}
	public static Inventory getSelectAkatsukiInventory() {
		return Bukkit.createInventory(null, 54, "§aNaruto§7 ->§c Akatsuki");
	}
	public static ItemStack getSelectMahrButton() {
		ItemStack stack = new ItemStack(Material.FEATHER, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.BLUE+"Mahr");
		meta.setLore(Arrays.asList("§r"+"Choisissez les roles jouables dans cette partie"));
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getSelectTitanButton() {
		ItemStack stack = new ItemStack(Material.MOB_SPAWNER, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("§cTitans");
		meta.setLore(Arrays.asList("§r"+"Choisissez les roles jouables dans cette partie"));
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		return stack;
	}
	public static Inventory getDemonSelectGUI() {
        return Bukkit.createInventory(null, 54, "DemonSlayer -> §cDémons");
	}
	public static ItemStack getSelectSoloButton() {
		ItemStack stack = new ItemStack(Material.INK_SACK, 1, (byte) 14);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Solo");
		meta.setLore(Arrays.asList("§r"+"Choisissez les roles jouables dans cette partie"));
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		return stack;
	}
	public static Inventory getCutCleanConfigGUI() {
        return Bukkit.createInventory(null, 9, "§bCutClean");
	}
	public static Inventory getDSSoloSelectGUI() {
        return Bukkit.createInventory(null, 54, "DemonSlayer -> §eSolo");
	}
	public static Inventory getAOTSoloSelectGUI() {
		return Bukkit.createInventory(null, 54, "§fAOT§7 -> §eSolo");
	}
	public static Inventory getScenarioGUI() {
        return Bukkit.createInventory(null, 9*3, "§fConfiguration§7 -> §6scenarios");
	}
	public static Inventory getAdminWatchGUI() {
        return Bukkit.createInventory(null, 54, "§fConfiguration");
	}
	
	public static Inventory getConfigSelectGUI() {
        return Bukkit.createInventory(null, 27, "§fConfiguration de la partie");
	}

	public static Inventory getRoleSelectGUI() {
        return Bukkit.createInventory(null, 27, "§fConfiguration§7 ->§6 Roles");
	}
	public static Inventory getSelectInventoryGUI() {
        return Bukkit.createInventory(null, 54, "§fConfiguration§7 ->§6 Inventaire");
	}
	
	public static Inventory getEventSelectGUI() {
        return Bukkit.createInventory(null, 9, "§fConfiguration§7 -> §6Événements");
	}
	public static Inventory getMahrGui() {
        return Bukkit.createInventory(null, 54, "§fAOT§7 ->§9 Mahr");
		}
	public static Inventory getSecretTitansGui() {
        return Bukkit.createInventory(null, 54, "§fAOT§7 ->§c Titans");
	}
	public static Inventory getSecretSoldatGui() {
        return Bukkit.createInventory(null, 54, "§fAOT§7 ->§a Soldats");
	}
	public static Inventory getConfigurationAOT() {
		return Bukkit.createInventory(null, 54, "Configuration -> AOT");
	}
	public static ItemStack getSelectEventButton() {
		return new ItemBuilder(Material.FIREWORK)
				.setName("§fConfiguration§7 -> §6Événement")
				.setLore("§7Pour choisir quels événements seront présent dans la partie")
				.toItemStack();
	}
	public static ItemStack getSelectConfigAotButton() {
		return new ItemBuilder(Material.WATCH).setName("Configuration Avancée").toItemStack();
	}
	public static ItemStack getSelectDSButton() {
		return new ItemBuilder(Material.REDSTONE).setName("§6Demon Slayer").addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().toItemStack();
	}
	public static ItemStack getSelectMCButton() {
		  return new ItemBuilder(Material.GRASS).setName("§aMinecraft").addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().toItemStack();
	}
	public static Inventory getDemonSlayerInventory() {
		return Bukkit.createInventory(null, 27, "§fRoles§7 ->§6 DemonSlayer");
	}
	public static ItemStack getSelectAOTButton() {
		return new ItemBuilder(Material.FEATHER).setName("§6AOT").addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().toItemStack();
	}
	public static Inventory getSelectAOTInventory() {
		return Bukkit.createInventory(null, 27, "§fRoles§7 ->§6 AOT");
	}
	public static ItemStack getSelectNSButton() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§6NS").addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().toItemStack();
	}
	public static Inventory getSelectNSInventory() {
		return Bukkit.createInventory(null, 27, "§fRoles§7 ->§6 NS");
	}
	public static Inventory getSelectMCInventory() {
		  return Bukkit.createInventory(null, 27, "§fRoles§7 ->§a Minecraft");
	}

	public static ItemStack getSelectNetherButton() {
		  return new ItemBuilder(Material.NETHERRACK).setName("§cNether").toItemStack();
	}
}