package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.roles.builder.TeamList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;

public class Hotaru extends RoleBase{

	public Hotaru(Player player) {
		super(player);
		owner.sendMessage(AllDesc.Hotaru);
		setLameIncassable(owner, true);
		lame = null;
	}
	@Override
	public TeamList getTeam() {
		return TeamList.Slayer;
	}
	@Override
	public Roles getRoles() {
		return Roles.Hotaru;
	}
	@Override
	public String[] Desc() {return AllDesc.Hotaru;}
	public int actualdslameuse = 0;
	public boolean hasdsunbreak = false;
	public int actualdsrepair = 0;

	@Override
	public String getName() {
		return "§aHotaru";
	}

	private enum Lame {
		Force,
		Resi,
		Speed,
		Coeur,
		FireResi,
		NoFall;
	}
	private Lame lame;
	@Override
	public void resetCooldown() {
		actualdslameuse = 0;
		hasdsunbreak = false;
		actualdsrepair = 0;
	}
	@Override
	public void FormChoosen(ItemStack item, GameState gameState) {
		if (item == null)return;
		if (owner.getInventory().contains(Items.getLamedenichirincoeur())){
			owner.sendMessage("Vous avez perdu la lame de "+lame.name());
			lame = null;
			owner.getInventory().remove(Items.getLamedenichirincoeur());
			setMaxHealth(getMaxHealth()-4.0);
		}
		if (owner.getInventory().contains(Items.getLamedenichirinfireresi())) {
			owner.sendMessage("Vous avez perdu la lame de "+lame.name());
			lame = null;
			owner.getInventory().remove(Items.getLamedenichirinfireresi());
			owner.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
		}
		if (owner.getInventory().contains(Items.getLamedenichirinforce())) {
			owner.sendMessage("Vous avez perdu la lame de "+lame.name());
			lame = null;
			owner.getInventory().remove(Items.getLamedenichirinforce());
			addBonusforce(-10);
		}
		if (owner.getInventory().contains(Items.getLamedenichirinnofall())) {
			owner.sendMessage("Vous avez perdu la lame de "+lame.name());
			lame = null;
			owner.getInventory().remove(Items.getLamedenichirinnofall());
			setNoFall(false);
		}
		if (owner.getInventory().contains(Items.getLamedenichirinresi())) {
			owner.sendMessage("Vous avez perdu la lame de "+lame.name());
			lame = null;
			owner.getInventory().remove(Items.getLamedenichirinresi());
			addBonusResi(-10);
		}
		if (owner.getInventory().contains(Items.getLamedenichirinspeed())) {
			owner.sendMessage("Vous avez perdu la lame de "+lame.name());
			lame = null;
			owner.getInventory().remove(Items.getLamedenichirinspeed());
			addSpeedAtInt(owner, -10);
		}
		if (item.isSimilar(Items.getLamedenichirincoeur())) {
			setMaxHealth(getMaxHealth()+4.0);
			owner.closeInventory();
			owner.sendMessage("Vous avez obtenue la lame§d Rose");
			lame = Lame.Coeur;
			owner.getInventory().addItem(Items.getLamedenichirincoeur());
		}
		if (item.isSimilar(Items.getLamedenichirinfireresi())) {
			owner.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
			owner.closeInventory();
			owner.sendMessage("Vous avez obtenue la lame de§6 Fire Résistance");
			lame = Lame.FireResi;
			owner.getInventory().addItem(Items.getLamedenichirinfireresi());
		}
		if (item.isSimilar(Items.getLamedenichirinforce())) {
			owner.closeInventory();
			owner.sendMessage("Vous avez obtenue la lame de§c Force");
			lame = Lame.Force;
			addBonusforce(10);
			owner.getInventory().addItem(item);
		}
		if (item.isSimilar(Items.getLamedenichirinnofall())) {
			owner.closeInventory();
			owner.sendMessage("Vous avez obtenue la lame de§a NoFall");
			lame = Lame.NoFall;
			setNoFall(true);
			owner.getInventory().addItem(item);
		}
		if (item.isSimilar(Items.getLamedenichirinresi())) {
			owner.closeInventory();
			owner.sendMessage("Vous avez obtenue la lame de§7 Résistance");
			lame = Lame.Resi;
			addBonusResi(10);
			owner.getInventory().addItem(item);
		}
		if (item.isSimilar(Items.getLamedenichirinspeed())) {
			owner.closeInventory();
			owner.sendMessage("Vous avez obtenue la lame de§b Speed");
			lame = Lame.Speed;
			addSpeedAtInt(owner, 10);
			owner.getInventory().addItem(item);
		}
		super.FormChoosen(item, gameState);
	}
	@Override
	public void OpenFormInventory(GameState gameState) {
		Inventory inv = Bukkit.createInventory(owner, 27, "Choix de la lame");
		inv.setItem(9, Items.getLamedenichirincoeur());
		inv.setItem(4, Items.getLamedenichirinfireresi());
		inv.setItem(13, Items.getLamedenichirinforce());
		inv.setItem(15, Items.getLamedenichirinnofall());
		inv.setItem(17, Items.getLamedenichirinresi());
		inv.setItem(11, Items.getLamedenichirinspeed());
		owner.openInventory(inv);
		super.OpenFormInventory(gameState);
	}
	@Override
	public void onDSCommandSend(String[] args, GameState gameState) {
		if (args[0].equalsIgnoreCase("lame")) {
			if (args.length == 2) {
				if (args[1] != null) {
				Player cible = Bukkit.getPlayer(args[1]);
					if (actualdslameuse <5) {
						if (cible != null) {
							if (!gameState.getInGamePlayers().contains(cible)) {
							owner.sendMessage("Vous ne pouvez pas obtenir d'information sur un joueur qui n'est pas en jeu");
							}
							if (cible.getInventory().contains(Items.getLamedenichirincoeur())) {
							owner.sendMessage("Ce joueur possède une lame de§d coeur");
							}
							if (cible.getInventory().contains(Items.getLamedenichirinfireresi())) {
							owner.sendMessage("Ce joueur possède une lame de§6 fire resistance");
							}
							if (cible.getInventory().contains(Items.getLamedenichirinforce())) {
							owner.sendMessage("Ce joueur possède une lame de§c force");
							}
							if (cible.getInventory().contains(Items.getLamedenichirinnofall())) {
							owner.sendMessage("Ce joueur possède une lame de§a nofall");
							}
							if (cible.getInventory().contains(Items.getLamedenichirinresi())) {
							owner.sendMessage("Ce joueur possède une lame de§7 Resistance");
							}
							if (cible.getInventory().contains(Items.getLamedenichirinspeed())) {
							owner.sendMessage("Ce joueur possède une lame de§e speed");
							}
							if (!gameState.hasRoleNull(cible)) {
								if (!gameState.getPlayerRoles().get(cible).hasblade) {
								owner.sendMessage("Ce joueurs ne possède pas de lame");
								}
							}
							actualdslameuse+=1;
							owner.sendMessage("Il ne vous reste que "+(5-actualdslameuse));
						}
					}
				}
			} else {
			owner.sendMessage("Veuiller indiquer le pseudo d'un joueur");
			}
		}
		if (args[0].equalsIgnoreCase("unbreak")) {
			if (args.length == 2) {
				if (args[1] != null) {
				Player cible = Bukkit.getPlayer(args[1]);
					if (!hasdsunbreak) {
						if (cible != null) {
							if (!gameState.getInGamePlayers().contains(cible)) {
							owner.sendMessage("Impossible de rendre la lame d'un mort incassable !");
							}
						if (!gameState.getPlayerRoles().get(cible).HisUnbreak()) {
							gameState.getPlayerRoles().get(cible).setLameIncassable(cible, true);
							gameState.getPlayerRoles().get(cible).sendMessageAfterXseconde(cible, "§aHotaru§f à rendu votre Lame de Nichirin incassable", 1);	
							sendMessageAfterXseconde(owner, "Vous avez rendu la lame de "+cible.getName()+" incassable", 1);
							hasdsunbreak = true;
							} else {
							gameState.getPlayerRoles().get(cible).sendMessageAfterXseconde(cible, "§aHotaru§f à rendu votre Lame de Nichirin incassable", 1);
							sendMessageAfterXseconde(owner, "Vous avez rendu la lame de "+cible.getName()+" incassable", 1);
							hasdsunbreak = true;
							}
						} else {
						owner.sendMessage("Veuiller indiquer le pseudo d'un joueur");
						}
					} else {
					owner.sendMessage("Vous avez déjà mis une lame incassable");
					}
				} else {
				owner.sendMessage("Veuiller indiquer le pseudo d'un joueur");
				}
			} else {
			owner.sendMessage("Veuiller indiquer le pseudo d'un joueur");
			}
		}
		if (args[0].equalsIgnoreCase("repair")) {
			if (args.length == 2) {
				if (args[1] != null) {
					Player cible = Bukkit.getPlayer(args[1]);
					if (actualdsrepair < 5) {
						if (cible != null) {
							if (!gameState.getInGamePlayers().contains(cible)) {
								owner.sendMessage("Impossible de rendre la lame d'un mort incassable !");
							}
							gameState.getPlayerRoles().get(cible).actualduralame+=40;
							gameState.getPlayerRoles().get(cible).sendMessageAfterXseconde(cible, "§aHotaru§f à augmenter la durabilité de votre lame elle monte maintenant jusqu'à: "+gameState.getPlayerRoles().get(cible).actualduralame, 1);
							owner.sendMessage("Vous avez augmenté la durabilité de la lame de "+cible.getName()+" de 40");
							actualdsrepair+=1;
							owner.sendMessage("Il ne vous reste que: "+(5-actualdsrepair));
						} else {
						owner.sendMessage("Veuiller indiquer le pseudo d'un joueur");
						}
					} else {
					owner.sendMessage("Vous avez déjà mis une lame incassable");
					}
				} else {
				owner.sendMessage("Veuiller indiquer le pseudo d'un joueur");
				}
			} else {
			owner.sendMessage("Veuiller indiquer le pseudo d'un joueur");
			}
			
			
		}
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				
		};
	}
}