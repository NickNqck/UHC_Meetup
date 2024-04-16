package fr.nicknqck.items;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.ItemBuilder;

public abstract class GUIItems{
	
	public static ItemStack getEnderPearl() {
		ItemStack stack = new ItemStack(Material.ENDER_PEARL, GameState.pearl);
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
	
	public static ItemStack getSouffleSoleil() {
		ItemStack stack = new ItemStack(Material.DOUBLE_PLANT, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Souffle du Soleil");
		meta.setLore(Arrays.asList("§r"+"Vous obtiendrez l'accès à l'objet: ",
				ChatColor.GOLD+"Souffle du Soleil: "+"Pendant 2 minutes, quand vous tapez un joueur il perd son absorbtion"));
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getSouffleLune() {
		ItemStack stack = new ItemStack(Material.BED, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Souffle de la Lune");
		meta.setLore(Arrays.asList("§r"+"Vous obtiendrez force 1 la nuit"));
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getSouffleRoche() {
		ItemStack stack = new ItemStack(Material.STONE, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Souffle de la Roche");
		meta.setLore(Arrays.asList("§r"+"Vous obtiendrez Résistance 1 le jour"));
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getSouffleEau() {
		ItemStack stack = new ItemStack(Material.WATER_BUCKET, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Souffle de l'Eau");
		meta.setLore(Arrays.asList("§r"+"Vous obtiendrez:",
				ChatColor.GOLD+"Botte en Diamant Depth Strider 1",
				ChatColor.GOLD+"Souffle de l'Eau:§7 Vous donne Speed 1 pendant 3 minutes"));
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getSouffleFeu() {
		ItemStack stack = new ItemStack(Material.FLINT_AND_STEEL, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Souffle du Feu");
		meta.setLore(Arrays.asList("§r"+"Quand vous taperez un joueur il y aura une chance sur 5 qu'il soit mis automatiquement mit en feu"));
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getSouffleFoudre() {
		ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, (byte) 4);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Souffle de la Foudre");
		meta.setLore(Arrays.asList("§7"+"Vous obtiendrez: ",
				ChatColor.GOLD+"Souffle de la Foudre: Eclair de Chaleur"+"§7 la prochaine personne que vous taperez ce verra infliger",
				"§7 2"+AllDesc.coeur+" via un§e éclair§7 également elle ce verra obtenir l'effet "+AllDesc.slow+"§7 1 pendant 15 secondes"));
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getSouffleVent() {
		ItemStack stack = new ItemStack(Material.FEATHER, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Souffle du Vent");
		meta.setLore(Arrays.asList("§r"+"Vous obtiendrez: ",
				ChatColor.GOLD+"Souffle du Vent:§7 Vous donne "+AllDesc.Speed+"§7 2 pendant 2 minutes"));
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getSouffleAmour() {
		ItemStack stack = new ItemStack(Material.APPLE, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Souffle de l'Amour");
		meta.setLore(Arrays.asList("§7Vous obtiendrez: ",
				ChatColor.GOLD+"2§c❤§7 permanent supplémentaire"));
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getSouffleBrume() {
		ItemStack stack = new ItemStack(Material.POTION, 1, (byte) 8238);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Souffle de la Brume");
		meta.setLore(Arrays.asList("§7Vous obtiendrez: ",
				ChatColor.GOLD+"Souffle de la Brume:§7 vous donne invisibilité 1 pendant 2mins"));
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		return stack;
	}
	public final static ItemStack getSouffleSerpent() {
		ItemStack stack = new ItemStack(Material.BARRIER, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Souffle du Serpent");
		meta.setLore(Arrays.asList("Vous obtiendrez: ",
				ChatColor.GOLD+"Souffle du Serpent:§7 Pendant 1 minutes vous aurez 1chance/5 d'esquiver les coups"));
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
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
	public final static ItemStack getSouffleFleur() {
		ItemStack stack = new ItemStack(Material.RED_ROSE, 1, (byte) 2);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Souffle de la Fleur");
		meta.setLore(Arrays.asList("§rVous obtiendrez une immunité au Poison"
				));
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
	
	  public static ItemStack getdiamondhelmet() {
			ItemStack stack = new ItemStack(Material.DIAMOND_HELMET, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, GameState.pc, true);
			meta.setLore(Arrays.asList("§r"+"Click to Change"));
			stack.setItemMeta(meta);
			return stack;
		}

	  public static ItemStack getdiamondchestplate() {
			ItemStack stack = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, GameState.pch, true);
			meta.setLore(Arrays.asList("§r"+"Click to Change"));
			stack.setItemMeta(meta);
			return stack;
		}
	  public static ItemStack getironleggings() {
		  ItemStack stack = new ItemStack(Material.IRON_LEGGINGS, 1);
		  ItemMeta meta = stack.getItemMeta();
		  meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, GameState.pl, true);
		  meta.setLore(Arrays.asList("§r"+"Click to Change"));
		  stack.setItemMeta(meta);
		  return stack;
	  }
	  public static ItemStack getdiamondboots() {
		  ItemStack stack = new ItemStack(Material.DIAMOND_BOOTS, 1);
		  ItemMeta meta = stack.getItemMeta();
		  meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, GameState.pb, true);
		  meta.setLore(Arrays.asList("§r"+"Click to Change"));
		  stack.setItemMeta(meta);
		  return stack;
	  }
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
			meta.addEnchant(Enchantment.DAMAGE_ALL, GameState.sharpness, true);
			meta.setLore(Arrays.asList("§r"+"Click to Change",
					"Minimum: "+ChatColor.GOLD+"Sharpness 1",
					"§r"+"Maximal: "+ChatColor.GOLD+"Sharpness 5"));
			stack.setItemMeta(meta);
			return stack;
		}
	  public static ItemStack getblock() {
			ItemStack stack = new ItemStack(Material.BRICK, 1*(GameState.nmbblock));
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
			meta.addEnchant(Enchantment.ARROW_DAMAGE, GameState.power, true);
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
		public static ItemStack getRoleInfo() {
			ItemStack stack = new ItemStack(Material.BOOK, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD+"Afficher le Role Info");
			meta.setLore(Arrays.asList("(Actuellement) Activer"));
			meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, false);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			stack.setItemMeta(meta);
			return stack;
		}
		public static ItemStack getdisRoleInfo() {
			ItemStack stack = new ItemStack(Material.BOOK, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD+"Afficher le Role Info");
			meta.setLore(Arrays.asList("(Actuellement) Désactiver"));
			meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, false);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			stack.setItemMeta(meta);
			return stack;
		}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		public static ItemStack getTabRoleInfo(GameState gameState) {
			ItemStack stack = new ItemStack(Material.SIGN, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD+"Role dans le TAB");
			meta.setLore(Arrays.asList(ChatColor.RESET+a(gameState)));
			meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			stack.setItemMeta(meta);
			return stack;
		}
		private static String a(final GameState gameState) {
			return gameState.roletab ? "Activer" : "Désactiver"; 
		}
		private final static String b(final GameState gameState) {
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
		
		public static ItemStack XpBoostFer() {
			ItemStack stack = new ItemStack(Material.IRON_INGOT, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD+"Fer");
			meta.setLore(Arrays.asList("§r"+"Choisissez les roles jouables dans cette partie"));			
			stack.setItemMeta(meta);
			return stack;
		}
		public static ItemStack getCrit(GameState gameState) {
			ItemStack stack = new ItemStack(Material.IRON_SWORD, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD+"Pourcentage de nerf des coups critique");
			meta.setLore(Arrays.asList("§r"+"(50% = non nerf) "+ChatColor.GOLD+ gameState.critP+"%"));
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
		meta.setLore(Arrays.asList("§r"+"Configurez l'entièretée de la partie"));
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getSelectInvsButton() {
		ItemStack stack = new ItemStack(Material.CHEST, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("§fConfiguration§7 ->§6 Inventaire");
		meta.setLore(Arrays.asList("§7Configurez l'inventaire"));
		stack.setItemMeta(meta);
		return stack;
	}

	public static ItemStack getBlackStainedGlassPane() {
		ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+" ");
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
		Inventory inv = Bukkit.createInventory(null, 54, "DemonSlayer ->§a Slayers");
		return inv;
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
		Inventory inv = Bukkit.createInventory(null, 54, "DemonSlayer -> §cDémons");
		return inv;
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
		Inventory inv = Bukkit.createInventory(null, 9, "§bCutClean");
		return inv;
	}
	public static Inventory getDSSoloSelectGUI() {
		Inventory inv = Bukkit.createInventory(null, 54, "DemonSlayer -> §eSolo");
		return inv;
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
        return Bukkit.createInventory(null, 27, "Configuration de la partie");
	}

	public static Inventory getRoleSelectGUI() {
        return Bukkit.createInventory(null, 27, "§fConfiguration§7 ->§6 Roles");
	}
	public static Inventory getSelectInventoryGUI() {
        return Bukkit.createInventory(null, 54, "§fConfiguration§7 ->§6 Inventaire");
	}
	
	public static Inventory getEventSelectGUI() {
		Inventory inv = Bukkit.createInventory(null, 9, "§fConfiguration§7 -> §6Événements");
		return inv;
	}
	public static Inventory getMahrGui() {
		Inventory inv = Bukkit.createInventory(null, 54, "§fAOT§7 ->§9 Mahr");
		return inv;
		}
	public static Inventory getSecretTitansGui() {
		Inventory inv = Bukkit.createInventory(null, 54, "§fAOT§7 ->§c Titans");
		return inv;
	}
	public static Inventory getSecretSoldatGui() {
		Inventory inv = Bukkit.createInventory(null, 54, "§fAOT§7 ->§a Soldats");
		return inv;
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
}