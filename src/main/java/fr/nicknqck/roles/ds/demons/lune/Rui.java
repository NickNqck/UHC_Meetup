package fr.nicknqck.roles.ds.demons.lune;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.demons.Muzan;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Rui extends DemonsRoles {

	public Rui(Player player) {
		super(player);
		owner.sendMessage(Desc());
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			for (Player p : gameState.getInGamePlayers()) {
				if (getPlayerRoles(p) instanceof Muzan) {
					owner.sendMessage("La personne possédant le rôle de§c Muzan§r est:§c "+p.getName());
				}
			}
		}, 20);
	}
	@Override
	public TeamList getOriginTeam() {
		return TeamList.Demon;
	}
	@Override
	public DemonType getRank() {
		return DemonType.LuneInferieur;
	}

	@Override
	public Roles getRoles() {
		return Roles.Rui;
	}
	@Override
	public String[] Desc() {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			for (Player p : gameState.getInGamePlayers()) {
				if (getPlayerRoles(p) instanceof Muzan) {
					owner.sendMessage("La personne possédant le rôle de§c Muzan§r est:§c "+p.getName());
				}
			}
		}, 20);
		return AllDesc.Rui;
	}
	
	@Override
	public void GiveItems() {
		Inventory inv = owner.getInventory();
		inv.addItem(Items.getFils());
		inv.addItem(Items.getPouvoirSanginaire());
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				Items.getFils(),
				Items.getPouvoirSanginaire()
		};
	}
	private int cdfilforce = 0;
	private int cdfilspeed = 0;
	private int cdfilresi = 0;
	private int cdfilregen = 0;
	private int cdpouvoirsanginaire = 0;
	private int cdfil = 0;
	private int filuse = 0;
	private int usefilforce = 0;
	private int usefilspeed= 0;
	private int usefilresi =0;
	private int usefilregen=0;
	
	@Override
	public void resetCooldown() {
		cdfil = 0;
		cdfilforce = 0;
		cdfilregen = 0;
		cdfilresi = 0;
		cdfilspeed = 0;
		allcdfil = 0;
		cdpouvoirsanginaire = 0;
		filuse = 0;
		usefilforce = 0;
		usefilregen = 0;
		usefilresi = 0;
		usefilspeed = 0;
	}
	
	private int allcdfil = 0;

	@Override
	public void Update(GameState gameState) {
		if (owner.getItemInHand().isSimilar(Items.getPouvoirSanginaire())) {
			if (allcdfil > 0) {
					String message = "Cooldown: §6"+(allcdfil/60)+"m"+(allcdfil%60)+"s";
					PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte)2);
					((CraftPlayer) owner).getHandle().playerConnection.sendPacket(packet);
			} else {
					String message = owner.getItemInHand().getItemMeta().getDisplayName()+"§r Utilisable";
					PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte)2);
					((CraftPlayer) owner).getHandle().playerConnection.sendPacket(packet);
			}
		}
		allcdfil = cdfilforce+cdfilspeed+cdfilresi+cdfilregen;
		if (cdfilspeed>=1)cdfilspeed--;
		if (cdfilforce>=1)cdfilforce--;
		if(cdfilresi>=1)cdfilresi--;
		if(cdfilregen>=1)cdfilregen--;
		if (cdfil>=1)cdfil--;
		if (cdpouvoirsanginaire>=1)cdpouvoirsanginaire--;
		if (usefilforce>=1)usefilforce--;
		if(usefilspeed>=1)usefilspeed--;
		if(usefilresi>=1)usefilresi--;
		if(usefilregen>=1)usefilregen--;
		
		int endfil =60*2;
		if(cdfilspeed == endfil) {
			filuse--;
			owner.sendMessage("Vous venez de perdre vos effet du au fil de speed");
		}
		if(cdfilforce == endfil) {
			filuse--;
			owner.sendMessage("Vous venez de perdre vos effet du au fil de force");
		}
		if(cdfilresi == endfil) {
			filuse--;
			owner.sendMessage("Vous venez de perdre vos effet du au fil de résistance");
		}
		if(cdfilregen == endfil+30) {
			filuse--;
			owner.sendMessage("Vous venez de perdre vos effet du au fil de régénération");
		}
		super.Update(gameState);
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(Items.getFils())) {
			Inventory inv = Bukkit.createInventory(owner, 9, "Choix de fils");
			inv.setItem(1, GUIItems.getFilForce());
			inv.setItem(3, GUIItems.getFilSpeed());
			inv.setItem(5, GUIItems.getFilResi());
			inv.setItem(7, GUIItems.getFilRegen());
			owner.openInventory(inv);
		}
		if(item.isSimilar(Items.getPouvoirSanginaire())) {
			if (filuse <=0) {
				if (allcdfil <=0) {
					if (cdpouvoirsanginaire <=0) {
						owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*60, 0, false, false));
						owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60, 0, false, false));
						owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60, 0, false, false));
						owner.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*30, 0, false, false));
						setForce(20);
						setResi(20);
						int cd = 60*3;
						cdfilforce =cd;
						cdfilspeed=cd;
						cdfilresi=cd;
						cdfilregen=cd;
						filuse+=4;
						cdpouvoirsanginaire = 20*(60*8);
					} else {
						int s = cdpouvoirsanginaire%60;
						int m = cdpouvoirsanginaire/60;
						owner.sendMessage(ChatColor.RED+"Vous ne pourrez utiliser a nouveau votre abilité que dans "+ChatColor.GOLD+m+" minutes"+ChatColor.RED+" et "+ChatColor.GOLD+s+" secondes");
						return false;
					}
				} else {
					int s = allcdfil%60;
					int m = allcdfil/60;
					owner.sendMessage(ChatColor.RED+"Vous ne pourrez pas utiliser votre: "+ChatColor.GOLD+"Pouvoir Sanginaire"+ChatColor.WHITE+" pendant encore, "+ChatColor.GOLD+m+" minutes"+ChatColor.RED+" et "+ChatColor.GOLD+s+" secondes");
					return false;
				}
			} else {
				owner.sendMessage("Vous avez atteint le nombre maximum de fil utilisable en même temp");
				return false;
			}
		}
		return super.ItemUse(item, gameState);
	}
	@Override
	public void FormChoosen(ItemStack item, GameState gameState) {
		if (item.isSimilar(GUIItems.getFilForce())) {
			if (filuse <=1) {
				if (cdfilforce <=0) {
					owner.sendMessage("Activation du fil de Force");
					owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60*20, 0, false, false));
					setForce(20);
					filuse++;
					cdfilforce=60*3;
					usefilforce=60;
					owner.closeInventory();
				} else {
					int s = cdfilforce%60;
					int m = cdfilforce/60;
					owner.sendMessage(ChatColor.RED+"Vous ne pourrez utiliser a nouveau votre abilité que dans "+ChatColor.GOLD+m+" minutes"+ChatColor.RED+" et "+ChatColor.GOLD+s+" secondes");
					owner.closeInventory();
				}
			} else {
				owner.sendMessage("Vous avez atteint le nombre maximum de fil utilisable en même temp");
				owner.closeInventory();
			}
		}
		if (item.isSimilar(GUIItems.getFilRegen())) {
			if (filuse <=1) {
				if (cdfilregen <=0) {
					owner.sendMessage("Activation du fil de Régénération");
					owner.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*30, 0, false, false));
					filuse++;
					cdfilregen=60*3;
					usefilregen=30;
					owner.closeInventory();
				} else {
					int s = cdfilregen%60;
					int m = cdfilregen/60;
					owner.sendMessage(ChatColor.RED+"Vous ne pourrez utiliser a nouveau votre abilité que dans "+ChatColor.GOLD+m+" minutes"+ChatColor.RED+" et "+ChatColor.GOLD+s+" secondes");
					owner.closeInventory();
				}
			} else {
				owner.sendMessage("Vous avez atteint le nombre maximum de fil utilisable en même temp");
				owner.closeInventory();
			}
		}
		if (item.isSimilar(GUIItems.getFilResi())) {
			if (filuse <=1) {
				if (cdfilresi <=0) {
					owner.sendMessage("Activation du fil de Résistance");
					owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60*20, 0, false, false));
					setResi(20);
					filuse++;
					cdfilresi=60*3;
					usefilresi=60;
					owner.closeInventory();
				} else {
					int s = cdfilresi%60;
					int m = cdfilresi/60;
					owner.sendMessage(ChatColor.RED+"Vous ne pourrez utiliser a nouveau votre abilité que dans "+ChatColor.GOLD+m+" minutes"+ChatColor.RED+" et "+ChatColor.GOLD+s+" secondes");
					owner.closeInventory();
				}
			} else {
				owner.sendMessage("Vous avez atteint le nombre maximum de fil utilisable en même temp");
				owner.closeInventory();
			}
		}
		if (item.isSimilar(GUIItems.getFilSpeed())) {
			if (filuse <=1) {
				if (cdfilspeed <=0) {
					owner.sendMessage("Activation du fil de Speed");
					owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60*20, 0, false, false));
					filuse++;
					cdfilspeed=60*3;
					usefilspeed=60;
					owner.closeInventory();
				} else {
					int s = cdfilspeed%60;
					int m = cdfilspeed/60;
					owner.sendMessage(ChatColor.RED+"Vous ne pourrez utiliser a nouveau votre abilité que dans "+ChatColor.GOLD+m+" minutes"+ChatColor.RED+" et "+ChatColor.GOLD+s+" secondes");
					owner.closeInventory();
				}
			} else {
				owner.sendMessage("Vous avez atteint le nombre maximum de fil utilisable en même temp");
				owner.closeInventory();
			}
		}
		owner.updateInventory();
		super.FormChoosen(item, gameState);
	}

	@Override
	public String getName() {
		return "§cRui";
	}
}