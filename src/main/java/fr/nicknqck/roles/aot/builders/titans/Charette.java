package fr.nicknqck.roles.aot.builders.titans;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.items.Items;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.Loc;

public class Charette extends Titan {

	
	@Override
	public void onInteract(PlayerInteractEvent e, Player player) {}

	@Override
	public void onSecond() {
		Player player = Bukkit.getPlayer(getOwner());
		if (player != null) {
			Inventory inv = player.getInventory();
			getPlayerRole(getOwner()).isTransformedinTitan = isTransformedinTitan();
			if (countOccupiedSlots(inv) <= 9 ){
				getPlayerRole(getOwner()).OLDgivePotionEffet(PotionEffectType.SPEED, 60, 2, true);
				getPlayerRole(getOwner()).OLDgivePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true);
				getPlayerRole(getOwner()).setResi(20);
				setTransformedinTitan(true);
			}
			if (countOccupiedSlots(inv) > 9 && countOccupiedSlots(inv) <= 18) {
				getPlayerRole(getOwner()).OLDgivePotionEffet(PotionEffectType.SPEED, 60, 1, true);
				getPlayerRole(getOwner()).OLDgivePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true);
				getPlayerRole(getOwner()).setResi(20);
				setTransformedinTitan(true);
			}
			if (countOccupiedSlots(inv) > 18 && countOccupiedSlots(inv) <= 27) {
				getPlayerRole(getOwner()).OLDgivePotionEffet(PotionEffectType.SPEED, 60, 1, true);
				getPlayerRole(getOwner()).setResi(0);
				setTransformedinTitan(true);
			}
			if (countOccupiedSlots(inv) > 27 && countOccupiedSlots(inv) <= 36) {
				getPlayerRole(getOwner()).OLDgivePotionEffet(PotionEffectType.WEAKNESS, 60, 1, true);
				setTransformedinTitan(false);
				getPlayerRole(getOwner()).setResi(0);
			}
		}
	}
		public int countOccupiedSlots(Inventory inventory) {
		    int count = 0;
		    for (ItemStack item : inventory.getContents()) {
		        if (item != null && item.getType() != Material.ARROW && !item.isSimilar(Items.ArcTridi())) {
		            count++;
		        }
		    }
		    return count;
		}
	@Override
	public void onBlockBreak(BlockBreakEvent e, Player player) {}

	@Override
	public void Transfo() {}

	@Override
	public void onAPlayerDie(Player player, Entity killer) {
	  if (getListener().getCharette() !=null) {
		  if (getListener().getCharette().equals(player.getUniqueId())) {
			  for (Player p : Loc.getNearbyPlayersExcept(player, 30, player)) {
				  if (!getState().hasRoleNull(p.getUniqueId())) {
					  if (getPlayerRole(p.getUniqueId()).isCanVoleTitan() && canStealTitan(p)) {
						  p.sendMessage("§7Vous pouvez mainteant volé le Titan§9 Charette§7 avec la commande§6 /aot steal");
						  canVoleCharette.add(p);
					  }
				  }
			  }
			getListener().setCharette(null);
			resetCooldown();
		  }
	  }
	}

	@Override
	public void resetCooldown() {
		imun.clear();
		pickup = false;
	}

	@Override
	public ItemStack[] Items() {
		return new ItemStack[]{
		};
	}
	private List<Player> canVoleCharette = new ArrayList<>();
	@Override
	public void onSteal(Player sender, String[] args) {
		if (getListener().getCharette() == null) {
			if (canVoleCharette.contains(sender)) {
				if (getPlayerRole(sender.getUniqueId()).isCanVoleTitan()) {
					getListener().setCharette(sender.getUniqueId());
					sender.sendMessage("§7Vous avez volé le Titan§9 Charette");
					getPlayerRole(sender.getUniqueId()).setCanVoleTitan(false);
					canVoleCharette.clear();
				}
			}
		}
		System.out.println("end");
	}
    
	@Override
	public void onAotTitan(Player player, String[] args) {
		if (getListener().getCharette() == null)return;
		      if (getListener().getCharette().equals(player.getUniqueId())) {
		    	  player.sendMessage(new String[] {
		    		         AllDesc.bar,
		    		         "§7Titan:§9 Charette",
		    		         "Vous obtenez certain effet en fonction de votre nombre de slot remplis dans votre inventaire",
		    		         "",
		    		         "1-9,vous donne "+AllDesc.Speed+" 2 ainsi que "+AllDesc.Resi+" 1",
		    		         "10-18, vous donne "+AllDesc.Speed+" 1 ainsi que "+AllDesc.Resi+" 1",
		    		         "19-27, vous donne "+AllDesc.Speed+" 1",
		    		         "28-36, vous donne "+AllDesc.weak+" 1",
		    		         "Vous possédez une régénération naturelle de 1/2"+AllDesc.coeur+" toute les 30s",
		    		         "/aot pickup - Vous empêche de récupéré tout item étant au sol",
		    	   });
		    	  
		      }
	}
	boolean pickup = false;
	@Override
	public void onSubCommand(Player player, String[] args) {
		if (args[0].equalsIgnoreCase("pickup")) {
			if (player.getUniqueId() != getOwner())return;
			if (pickup) {
				player.sendMessage("Vous venez de désactivé votre /aot pickup");
				pickup = false;
			}else {
				player.sendMessage("Vous venez d'activé votre /aot pickup");
				pickup = true;
			}
		}
	}
	List<EntityType> imun = new ArrayList<>();
	@Override
	public void onPickup(PlayerPickupItemEvent e, Player player) {
		if (e.getPlayer() == player) {
			if (getOwner() != null) {
				if (player.getUniqueId() != getOwner())return;
				if (pickup) {
					if (!imun.contains(e.getItem().getType())) {
						e.setCancelled(true);
					}
				}
			}
		}
	}
	
	
	@Override
	public UUID getOwner() {
		return getListener().getCharette();
	}

	@Override
	public void onGetDescription(Player player) {
		if (getOwner() != null) {
			if (getOwner() == player.getUniqueId()) {
				player.sendMessage("§7Vous possédez le Titan "+getName());
				return;
			}
		}
	}

	@Override
	public String getName() {
		return "§9Charette";
	}

	@Override
	public List<Player> getListforSteal() {
		return canVoleCharette;
	}

	@Override
	public void PlayerKilled(Player player, Entity damager) {}

	@Override
	public void onPlayerAttackAnotherPlayer(Player damager, Player victim, EntityDamageByEntityEvent event) {
		// TODO Auto-generated method stub
		
	}
	
}