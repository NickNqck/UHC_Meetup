package fr.nicknqck.items;

import java.util.Arrays;
import java.util.Collections;

import fr.nicknqck.roles.desc.AllDesc;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.nicknqck.Main;
import fr.nicknqck.entity.bijus.Bijus;
import fr.nicknqck.utils.itembuilder.ItemBuilder;

public abstract class Items {
	
	public static ItemStack getAdminWatch() {
		ItemStack stack = new ItemStack(Material.WATCH, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Parametres de Jeu");
		meta.setLore(Arrays.asList(ChatColor.DARK_PURPLE+"Cet item sert a configurer l'entièretée de la partie.", ChatColor.DARK_RED+"Seul les Administrateurs peuvent utiliser cet item!"));
		stack.setItemMeta(meta);
		return stack;
	}
	  public static ItemStack Matatabi() {
	        ItemStack stack = new ItemStack(Material.NETHER_STAR, 1);
	        ItemMeta meta = stack.getItemMeta();
	        meta.setDisplayName("§6Matatabi");
	        stack.setItemMeta(meta);
	        return stack;
	  }
	  public static ItemStack Saiken() {
		  return new ItemBuilder(Material.NETHER_STAR)
				  .setName("§5Saiken")
				  .toItemStack();
	  }
	  public static ItemStack Isobu() {
	        ItemStack stack = new ItemStack(Material.NETHER_STAR, 1);
	        ItemMeta meta = stack.getItemMeta();
	        meta.setDisplayName("§eIsobu");
	        stack.setItemMeta(meta);
	        return stack;
	  }
	  public static ItemStack Kokuo() {
	        ItemStack stack = new ItemStack(Material.NETHER_STAR, 1);
	        ItemMeta meta = stack.getItemMeta();
	        meta.setDisplayName(Bijus.Kokuo.getBiju().getName());
	        stack.setItemMeta(meta);
	        return stack;
	  }
	  public static ItemStack SonGoku() {
		  return new ItemBuilder(Material.NETHER_STAR)
				  .setName(Bijus.SonGoku.getBiju().getName())
				  .toItemStack();
	  }
	  public static ItemStack Chomei() {
		  return new ItemBuilder(Material.NETHER_STAR)
				  .setName(Bijus.Chomei.getBiju().getName())
				  .toItemStack();
	  }
	  public static ItemStack ArcTridi() {
		  ItemStack stack = new ItemStack(Material.BOW, 1);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setDisplayName(ChatColor.AQUA+"Arc Tridimentionel");
		  meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
		  meta.spigot().setUnbreakable(true);
		  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		  stack.setItemMeta(meta);
		  return stack;
	  }
	  public static ItemStack getTransformation() {
          ItemStack stack = new ItemStack(Material.NETHER_STAR, 1);
          ItemMeta meta = stack.getItemMeta();
          meta.setDisplayName(ChatColor.GOLD+"Transformation");
          meta.addEnchant(Enchantment.ARROW_DAMAGE, 6, true);
          meta.spigot().setUnbreakable(true);
          meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
          meta.setLore(Arrays.asList("§fUtilisation: "+ChatColor.GOLD+"Click Droit"));
          stack.setItemMeta(meta);
          return stack;
      }
	
	  public static ItemStack getLamedenichirin() {
		  ItemStack stack = new ItemStack(Material.NETHER_STAR);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setDisplayName(ChatColor.GOLD+"Lame de Nichirin");
		  meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
		  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		  meta.setLore(Arrays.asList(ChatColor.WHITE+"Lame§a verte§r:§a NoFall",
				  ChatColor.WHITE+"Lame§d Rose§r:§d 2"+ AllDesc.coeur,
				  "§fLame§7 grise§r:§7 10% de Résistance",
				  "§fLame§8 noir§r:§8 10% de Force",
				  "§fLame§6 orange§r:§6 Fire Résistance",
				  "§fLame§b bleu§r:§b 10% de Speed"));
		  stack.setItemMeta(meta);
		  return stack;
	  }
	  public static ItemStack getLamedenichirincoeur() {
		  ItemStack stack = new ItemStack(Material.NETHER_STAR);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setDisplayName(ChatColor.WHITE+"Lame de Nichirin (§dRose§f)");
		  meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
		  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		  stack.setItemMeta(meta);
		  return stack;
	  }
	  public static ItemStack getLamedenichirinnofall() {
		  ItemStack stack = new ItemStack(Material.NETHER_STAR);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setDisplayName(ChatColor.WHITE+"Lame de Nichirin (§aVerte§f)");
		  meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
		  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

		  stack.setItemMeta(meta);
		  return stack;
	  }
	  public static ItemStack getLamedenichirinfireresi() {
		  ItemStack stack = new ItemStack(Material.NETHER_STAR);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setDisplayName(ChatColor.WHITE+"Lame de Nichirin (§6Orange§f)");
		  meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
		  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

		  stack.setItemMeta(meta);
		  return stack;
	  }
	  public static ItemStack getLamedenichirinforce() {
		  ItemStack stack = new ItemStack(Material.NETHER_STAR);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setDisplayName(ChatColor.WHITE+"Lame de Nichirin (§8Noir§f)");
		  meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
		  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

		  stack.setItemMeta(meta);
		  return stack;
	  }
	  public static ItemStack getLamedenichirinresi() {
		  ItemStack stack = new ItemStack(Material.NETHER_STAR);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setDisplayName(ChatColor.WHITE+"Lame de Nichirin (§7Grise§f)");
		  meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
		  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

		  stack.setItemMeta(meta);
		  return stack;
	  }
	  public static ItemStack getLamedenichirinspeed() {
		  ItemStack stack = new ItemStack(Material.NETHER_STAR);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setDisplayName(ChatColor.WHITE+"Lame de Nichirin (§eJaune§f)");
		  meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
		  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		  stack.setItemMeta(meta);
		  return stack;
	  }
	  public static ItemStack getSoufleFoudre4iememouvement() {
		  ItemStack stack = new ItemStack(Material.NETHER_STAR);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setDisplayName(ChatColor.GOLD+"4ième mouvement du Soufle de la Foudre");
		  meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
		  meta.spigot().setUnbreakable(true);
		  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		  meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
		  stack.setItemMeta(meta);
		  return stack;
	  }
	  public static ItemStack getPouvoirSanginaire() {
		  ItemStack stack = new ItemStack(Material.NETHER_STAR);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setDisplayName(ChatColor.GOLD+"Pouvoir Sanginaire");
		  meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
		  meta.spigot().setUnbreakable(true);
		  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		  meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
		  stack.setItemMeta(meta);
		  return stack;
	  }
		public static ItemStack getkokushibosword() {
			ItemStack stack = new ItemStack(Material.DIAMOND_SWORD, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD+"Epée de la nuit");
			meta.addEnchant(Enchantment.DAMAGE_ALL, 4, true);
			meta.spigot().setUnbreakable(true);
			stack.setItemMeta(meta);
			return stack;
		}
		public static ItemStack getSoufleDeLeau() {
			ItemStack stack = new ItemStack(Material.NETHER_STAR, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD+"Soufle De L'Eau");
			meta.addEnchant(Enchantment.ARROW_DAMAGE, 6, true);
			meta.spigot().setUnbreakable(true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
			stack.setItemMeta(meta);
			return stack;
		}
		public static ItemStack getSoufleDuFeu() {
			ItemStack stack = new ItemStack(Material.NETHER_STAR, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD+"Soufle Du Feu");
			meta.addEnchant(Enchantment.ARROW_DAMAGE, 6, true);
			meta.spigot().setUnbreakable(true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
			stack.setItemMeta(meta);
			return stack;
		}
		public static ItemStack getSake() {
			ItemStack stack = new ItemStack(Material.NETHER_STAR, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD+"Sake");
			meta.addEnchant(Enchantment.ARROW_DAMAGE, 6, true);
			meta.spigot().setUnbreakable(true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
			stack.setItemMeta(meta);
			return stack;
		}
		public static ItemStack getGyokkoPlastron() {
			ItemStack stack = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true);
			meta.addEnchant(Enchantment.THORNS, 3, true);
			meta.setDisplayName(ChatColor.GOLD+"Plastron de Gyokko");
			meta.spigot().setUnbreakable(true);
			stack.setItemMeta(meta);
			return stack;
		}
		public static ItemStack getGyokkoBoots() {
			ItemStack stack = new ItemStack(Material.DIAMOND_BOOTS, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true);
			meta.addEnchant(Enchantment.DEPTH_STRIDER, 2, true);
			meta.setDisplayName(ChatColor.GOLD+"Botte de Gyokko");
			meta.spigot().setUnbreakable(true);
			stack.setItemMeta(meta);
			return stack;
		}
		public static ItemStack getSoufleDeLaBrume() {
			ItemStack stack = new ItemStack(Material.NETHER_STAR, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD+"Soufle De La Brume");
			meta.addEnchant(Enchantment.ARROW_DAMAGE, 6, true);
			meta.spigot().setUnbreakable(true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
			stack.setItemMeta(meta);
			return stack;
		}
		public static ItemStack getSoufleDeLaRoche() {
			ItemStack stack = new ItemStack(Material.NETHER_STAR, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD+"Soufle De La Roche");
			meta.addEnchant(Enchantment.ARROW_DAMAGE, 6, true);
			meta.spigot().setUnbreakable(true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
			stack.setItemMeta(meta);
			return stack;
		}
		public static ItemStack getObi() {
			ItemStack stack = new ItemStack(Material.NETHER_STAR, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD+"Obi");
			meta.addEnchant(Enchantment.ARROW_DAMAGE, 6, true);
			meta.spigot().setUnbreakable(true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
			stack.setItemMeta(meta);
			return stack;
		}
		public static ItemStack getTroisièmeOeil() {
			ItemStack stack = new ItemStack(Material.NETHER_STAR, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD+"Troisième Oeil");
			meta.addEnchant(Enchantment.ARROW_DAMAGE, 6, true);
			meta.spigot().setUnbreakable(true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
			stack.setItemMeta(meta);
			return stack;
		}
		public static ItemStack getFaucille() {
			ItemStack stack = new ItemStack(Material.NETHER_STAR, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD+"Faucille");
			meta.addEnchant(Enchantment.ARROW_DAMAGE, 6, true);
			meta.spigot().setUnbreakable(true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
			stack.setItemMeta(meta);
			return stack;
		}
		public static ItemStack getSoufledelaBêtePerforation() {
			ItemStack stack = new ItemStack(Material.NETHER_STAR, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD+"Perforation");
			meta.addEnchant(Enchantment.ARROW_DAMAGE, 6, true);
			meta.spigot().setUnbreakable(true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
			stack.setItemMeta(meta);
			return stack;
		}
		public static ItemStack getSoufledelaBêteMutilationFurieuse() {
			ItemStack stack = new ItemStack(Material.NETHER_STAR, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD+"Mutilation Furieuse");
			meta.addEnchant(Enchantment.ARROW_DAMAGE, 6, true);
			meta.spigot().setUnbreakable(true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
			stack.setItemMeta(meta);
			return stack;
		}
		public static ItemStack getSoufledelaBêteTailladeOndulanceDivine() {
			ItemStack stack = new ItemStack(Material.NETHER_STAR, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD+"Taillade Ondulante Divine");
			meta.addEnchant(Enchantment.ARROW_DAMAGE, 6, true);
			meta.spigot().setUnbreakable(true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
			stack.setItemMeta(meta);
			return stack;
		}
		public static ItemStack getDomaEpouventaille() {
			ItemStack stack = new ItemStack(Material.DIAMOND_SWORD, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD+"Epouventaille de Glace");
			meta.addEnchant(Enchantment.DAMAGE_ALL, 3, true);
			meta.spigot().setUnbreakable(true);
			stack.setItemMeta(meta);
			return stack;
		}
		  public static ItemStack getDomaZonedeGlace() {
			  ItemStack stack = new ItemStack(Material.NETHER_STAR);
			  ItemMeta meta = stack.getItemMeta();
			  meta.setDisplayName(ChatColor.GOLD+"Pouvoir Sanginaire, Zone de Glace");
			  meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
			  meta.spigot().setUnbreakable(true);
			  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			  meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
			  stack.setItemMeta(meta);
			  return stack;
		  }
		  public static ItemStack getDomaStatutdeGlace() {
			  ItemStack stack = new ItemStack(Material.NETHER_STAR);
			  ItemMeta meta = stack.getItemMeta();
			  meta.setDisplayName(ChatColor.GOLD+"Pouvoir Sanginaire, Statut de Glace");
			  meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
			  meta.spigot().setUnbreakable(true);
			  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			  meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
			  stack.setItemMeta(meta);
			  return stack;
		  }
		  public static ItemStack getHanagoromoPourpre() {
			  ItemStack stack = new ItemStack(Material.NETHER_STAR);
			  ItemMeta meta = stack.getItemMeta();
			  meta.setDisplayName(ChatColor.GOLD+"Hanagoromo Pourpre");
			  meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
			  meta.spigot().setUnbreakable(true);
			  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			  meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
			  stack.setItemMeta(meta);
			  return stack;
		  }
		  public static ItemStack getTourbillondePêche() {
			  ItemStack stack = new ItemStack(Material.NETHER_STAR);
			  ItemMeta meta = stack.getItemMeta();
			  meta.setDisplayName(ChatColor.GOLD+"Tourbillon de Pêche");
			  meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
			  meta.spigot().setUnbreakable(true);
			  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			  meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
			  stack.setItemMeta(meta);
			  return stack;
		  }
		  public static ItemStack getSoufleduSoleil() {
				ItemStack stack = new ItemStack(Material.NETHER_STAR, 1);
				ItemMeta meta = stack.getItemMeta();
				meta.setDisplayName(ChatColor.GOLD+"Soufle du Soleil");
				meta.addEnchant(Enchantment.ARROW_DAMAGE, 6, true);
				meta.spigot().setUnbreakable(true);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
				stack.setItemMeta(meta);
				return stack;
			}
		  public static ItemStack getHantenguSekido() {
			  ItemStack stack = new ItemStack(Material.NETHER_STAR);
			  ItemMeta meta = stack.getItemMeta();
			  meta.setDisplayName(ChatColor.GOLD+"Sekido");
			  meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
			  meta.spigot().setUnbreakable(true);
			  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		//	  meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
			  stack.setItemMeta(meta);
			  return stack;
		  }
		  public static ItemStack getHantenguZohakuten() {
			  ItemStack stack = new ItemStack(Material.NETHER_STAR);
			  ItemMeta meta = stack.getItemMeta();
			  meta.setDisplayName(ChatColor.GOLD+"Zohakuten");
			  meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
			  meta.spigot().setUnbreakable(true);
			  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		//	  meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
			  stack.setItemMeta(meta);
			  return stack;
		  }
		  public static ItemStack getHantenguUrogi() {
			  ItemStack stack = new ItemStack(Material.NETHER_STAR);
			  ItemMeta meta = stack.getItemMeta();
			  meta.setDisplayName(ChatColor.GOLD+"Urogi");
			  meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
			  meta.spigot().setUnbreakable(true);
			  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		//	  meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
			  stack.setItemMeta(meta);
			  return stack;
		  }
		  public static ItemStack getHantenguUrogiFly() {
			  ItemStack stack = new ItemStack(Material.NETHER_STAR);
			  ItemMeta meta = stack.getItemMeta();
			  meta.setDisplayName(ChatColor.GOLD+"Pouvoir Sanginaire (Urogi), Fly");
			  meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
			  meta.spigot().setUnbreakable(true);
			  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			  stack.setItemMeta(meta);
			  return stack;
		  }
		  public static ItemStack getHantenguUrogiCri() {
			  ItemStack stack = new ItemStack(Material.NETHER_STAR);
			  ItemMeta meta = stack.getItemMeta();
			  meta.setDisplayName(ChatColor.GOLD+"Cri Sonique");
			  meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
			  meta.spigot().setUnbreakable(true);
			  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			  stack.setItemMeta(meta);
			  return stack;
		  }
		  public static ItemStack getHantenguKaraku() {
			  ItemStack stack = new ItemStack(Material.NETHER_STAR);
			  ItemMeta meta = stack.getItemMeta();
			  meta.setDisplayName(ChatColor.GOLD+"Karaku");
			  meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
			  meta.spigot().setUnbreakable(true);
			  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);			 
			  stack.setItemMeta(meta);
			  return stack;
		  }
		  public static ItemStack getHantenguKarakuVent() {
			  ItemStack stack = new ItemStack(Material.NETHER_STAR);
			  ItemMeta meta = stack.getItemMeta();
			  meta.setDisplayName(ChatColor.GOLD+"Pouvoir Sanginaire (Karaku), Vent");
			  meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
			  meta.spigot().setUnbreakable(true);
			  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			 // meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
			  stack.setItemMeta(meta);
			  return stack;
		  }
		  public static ItemStack getHantenguSekidoKakkhara() {
			  ItemStack stack = new ItemStack(Material.NETHER_STAR);
			  ItemMeta meta = stack.getItemMeta();
			  meta.setDisplayName(ChatColor.GOLD+"Pouvoir Sanginaire (Sekido), Kakkhara");
			  meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
			  meta.spigot().setUnbreakable(true);
			  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			 // meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
			  stack.setItemMeta(meta);
			  return stack;
		  }
		  public static ItemStack getHantenguUrami() {
			  ItemStack stack = new ItemStack(Material.NETHER_STAR);
			  ItemMeta meta = stack.getItemMeta();
			  meta.setDisplayName(ChatColor.GOLD+"Urami");
			  meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
			  meta.spigot().setUnbreakable(true);
			  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			 // meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
			  stack.setItemMeta(meta);
			  return stack;
		  }
		  public static ItemStack getHantenguAizetsu() {
			  ItemStack stack = new ItemStack(Material.NETHER_STAR);
			  ItemMeta meta = stack.getItemMeta();
			  meta.setDisplayName(ChatColor.GOLD+"Aizetsu");
			  meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
			  meta.spigot().setUnbreakable(true);
			  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			 // meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
			  stack.setItemMeta(meta);
			  return stack;
		  }
		  public static ItemStack getHantenguAizetsuEpee() {
			  ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
			  ItemMeta meta = stack.getItemMeta();
			  meta.setDisplayName(ChatColor.GOLD+"Yari");
			  meta.addEnchant(Enchantment.DAMAGE_ALL, 3, true);
			  meta.spigot().setUnbreakable(true);
			  stack.setItemMeta(meta);
			  return stack;
		  }
		  public static ItemStack getTambour() {
			  ItemStack stack = new ItemStack(Material.NETHER_STAR);
			  ItemMeta meta = stack.getItemMeta();
			  meta.setDisplayName(ChatColor.GOLD+"Tambour");
			  meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
			  meta.spigot().setUnbreakable(true);
			  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			  meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
			  stack.setItemMeta(meta);
			  return stack;
		  }
		  public static ItemStack getPercussionRapide() {
			  ItemStack stack = new ItemStack(Material.NETHER_STAR);
			  ItemMeta meta = stack.getItemMeta();
			  meta.setDisplayName(ChatColor.GOLD+"Percussion Rapide");
			  meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
			  meta.spigot().setUnbreakable(true);
			  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			  meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
			  stack.setItemMeta(meta);
			  return stack;
		  }
		  public static ItemStack getFils() {
			  ItemStack stack = new ItemStack(Material.NETHER_STAR);
			  ItemMeta meta = stack.getItemMeta();
			  meta.setDisplayName(ChatColor.GOLD+"Fils");
			  meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
			  meta.spigot().setUnbreakable(true);
			  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			  meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit (ouvre un menu)"+ChatColor.DARK_PURPLE+"."));
			  stack.setItemMeta(meta);
			  return stack;
		  }
			public static ItemStack getCharm() {
				ItemStack stack = new ItemStack(Material.RED_ROSE, 1, (byte) 2);
				ItemMeta meta = stack.getItemMeta();
				meta.setDisplayName(ChatColor.GOLD+"Charm");
				meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
				meta.spigot().setUnbreakable(true);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
				stack.setItemMeta(meta);
				return stack;
			}
			public static ItemStack getSusamaruBow() {
				ItemStack stack = new ItemStack(Material.BOW, 1);
				ItemMeta meta = stack.getItemMeta();
				meta.addEnchant(Enchantment.ARROW_DAMAGE, 6, true);
				meta.spigot().setUnbreakable(true);
				meta.setDisplayName("§6Ballon ");
				stack.setItemMeta(meta);
				return stack;
			}
			public static ItemStack getMaterialisationEmotion() {
				ItemStack stack = new ItemStack(Material.NETHER_STAR, 1);
				ItemMeta meta = stack.getItemMeta();
				meta.setDisplayName(ChatColor.GOLD+"Matérialisation des émotions");
				meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
				stack.setItemMeta(meta);
				return stack;
			}
			public static ItemStack getInfection() {
				return new ItemBuilder(Material.NETHER_STAR)
						.setName("§cInfection")
						.setLore("§fOuvre un menu dans lequel il y aura tout les joueurs étant à moins de 30blocs de vous,",
								"§f (si vous séléctionnez quelqu'un qui est déjà§c démon§f cela fonctionnera quand même)")
						.toItemStack();
			}
			public static ItemStack getKumoEmprisonnement() {
				ItemStack stack = new ItemStack(Material.STRING, 1);
				ItemMeta meta = stack.getItemMeta();
				meta.setDisplayName(ChatColor.GOLD+"Emprisonnement dans la toile");
				meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				stack.setItemMeta(meta);
				return stack;
			}
			public static ItemStack getKumoPrison() {
				ItemStack stack = new ItemStack(Material.WEB, 1);
				ItemMeta meta = stack.getItemMeta();
				meta.setDisplayName(ChatColor.GOLD+"Prison de toile");
				meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				stack.setItemMeta(meta);
				return stack;
			}
			public static ItemStack getsugar() {
		         ItemStack stack = new ItemStack(Material.SUGAR, 1);
		         ItemMeta meta = stack.getItemMeta();
		         meta.setDisplayName(ChatColor.GOLD+"Sucre");
		         meta.addEnchant(Enchantment.ARROW_DAMAGE, 6, true);
		         meta.spigot().setUnbreakable(true);
		         meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		         meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
		         stack.setItemMeta(meta);
		         return stack;
		     }
			public static ItemStack getalcool() {
		         ItemStack stack = new ItemStack(Material.GLASS_BOTTLE, 1);
		         ItemMeta meta = stack.getItemMeta();
		         meta.setDisplayName(ChatColor.GOLD+"Alcool");
		         meta.addEnchant(Enchantment.ARROW_DAMAGE, 6, true);
		         meta.spigot().setUnbreakable(true);
		         meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		         meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
		         stack.setItemMeta(meta);
		         return stack;
		     }
		     public static ItemStack getcarabine() {
		         ItemStack stack = new ItemStack(Material.STICK, 1);
		         ItemMeta meta = stack.getItemMeta();
		         meta.setDisplayName(ChatColor.GOLD+"Carabine");
		         meta.addEnchant(Enchantment.DEPTH_STRIDER, 6, true);
		         meta.spigot().setUnbreakable(true);
		         meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		         meta.setLore(Arrays.asList("§rUtilisation: "+ChatColor.GOLD+"Click Droit"+ChatColor.DARK_PURPLE+"."));
		         stack.setItemMeta(meta);
		         return stack;
		     }
		     public static ItemStack geteclairmort() {
		 		ItemStack stack = new ItemStack(Material.INK_SACK, 1, (byte) 11);
		 		ItemMeta meta = stack.getItemMeta();
		 		meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, true);
		 		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		 		meta.setDisplayName("§r§fÉclair à la mort: ");
		 		meta.setLore(Collections.singletonList("§r§fÉclair à la mort: "+(Main.getInstance().getGameConfig().isMortEclair() ? "§aActiver" : "§cDésactiver")));
		 		stack.setItemMeta(meta);
		 		return stack;
		 	}
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		  //Début Casque
		  public static ItemStack getdiamondhelmet() {
				ItemStack stack = new ItemStack(Material.DIAMOND_HELMET, 1);
				ItemMeta meta = stack.getItemMeta();
				meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, Main.getInstance().getGameConfig().getStuffConfig().getProtectionHelmet(), true);
				meta.spigot().setUnbreakable(Main.getInstance().getGameConfig().isStuffUnbreak());
				stack.setItemMeta(meta);
				return stack;
			}
		  //Fin Casque
		  //Début Plastron
		  public static ItemStack getdiamondchestplate() {
				ItemStack stack = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
				ItemMeta meta = stack.getItemMeta();
				meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, Main.getInstance().getGameConfig().getStuffConfig().getProtectionChestplate(), true);
				meta.spigot().setUnbreakable(Main.getInstance().getGameConfig().isStuffUnbreak());
				stack.setItemMeta(meta);
				return stack;
			}
	//Fin Plastron
	//Début Leggings
		  public static ItemStack getironleggings() {
				ItemStack stack = new ItemStack(Material.IRON_LEGGINGS, 1);
				ItemMeta meta = stack.getItemMeta();
				meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, Main.getInstance().getGameConfig().getStuffConfig().getProtectionLeggings(), true);
				meta.spigot().setUnbreakable(true);
				stack.setItemMeta(meta);
				return stack;
			}
	
	//Fin Leggings
	//Début Botte
		  public static ItemStack getdiamondboots() {
				ItemStack stack = new ItemStack(Material.DIAMOND_BOOTS, 1);
				ItemMeta meta = stack.getItemMeta();
				meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, Main.getInstance().getGameConfig().getStuffConfig().getProtectionBoost(), true);
				meta.spigot().setUnbreakable(Main.getInstance().getGameConfig().isStuffUnbreak());
				stack.setItemMeta(meta);
				return stack;
			}
	
	//Fin Botte
	public static ItemStack getdiamondsword() {
		ItemStack stack = new ItemStack(Material.DIAMOND_SWORD, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.addEnchant(Enchantment.DAMAGE_ALL, Main.getInstance().getGameConfig().getStuffConfig().getSharpness(), true);
		meta.spigot().setUnbreakable(Main.getInstance().getGameConfig().isStuffUnbreak());
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getironpickaxe() {
		ItemStack stack = new ItemStack(Material.IRON_PICKAXE, 1);
		ItemMeta meta = stack.getItemMeta();
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getironshovel() {
		ItemStack stack = new ItemStack(Material.IRON_SPADE, 1);
		ItemMeta meta = stack.getItemMeta();
		stack.setItemMeta(meta);
		return stack;
	}
	public static ItemStack getbow() {
		ItemStack stack = new ItemStack(Material.BOW, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.addEnchant(Enchantment.ARROW_DAMAGE, Main.getInstance().getGameConfig().getStuffConfig().getPower(), true);
		meta.spigot().setUnbreakable(Main.getInstance().getGameConfig().isStuffUnbreak());
		stack.setItemMeta(meta);
		return stack;
	}
}