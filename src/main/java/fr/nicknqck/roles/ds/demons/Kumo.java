package fr.nicknqck.roles.ds.demons;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.betteritem.BetterItem;

public class Kumo extends RoleBase{

	public Kumo(Player player, Roles roles, GameState gameState) {
		super(player, roles, gameState);
		owner.sendMessage(AllDesc.Kumo);
	}
	@Override
	public ItemStack[] getItems() {
        return new ItemStack[]{BetterItem.of(Items.getKumoPrison(), (event) -> {
        	if (cdtoile <= 0) {
				HashMap<Block, Material> map = new HashMap<>();
				for(Location location : sphere(owner.getLocation(), 8, true)) {
					Material type = location.getBlock().getType();
	                if(type == Material.AIR || type == Material.WATER || type == Material.STATIONARY_WATER || type == Material.LEAVES || type == Material.LEAVES_2) {
	                    map.put(location.getBlock(), location.getBlock().getType());
	                    location.getBlock().setType(Material.WEB);
	                }
	            }
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
	                map.keySet().forEach(location -> location.setType(map.get(location)));
	                prison = false;
	            }, 20*60);
				cdtoile = 60*5;
				prison = true;
				owner.sendMessage("Activation de la§l Prison");
			}else {
				sendCooldown(owner, cdtoile);
			}
            return true;
        }).setDroppable(false).setMovableOther(false).setPosable(false).getItemStack(),
        		BetterItem.of(Items.getKumoEmprisonnement(), event -> {
        			if (cdprison <= 0) {
        				Player player = event.getRightClicked(30, 10);
        				if (player != null) {
        					HashMap<Block, Material> map = new HashMap<>();
        					for(Location location : sphere(player.getLocation(), 2, true)) {
        						Material type = location.getBlock().getType();
        		                if(type == Material.AIR || type == Material.WATER || type == Material.STATIONARY_WATER || type == Material.LEAVES || type == Material.LEAVES_2) {
        		                    map.put(location.getBlock(), location.getBlock().getType());
        		                    location.getBlock().setType(Material.WEB);
        		                }
        		            }
        					cdprison = 60*5;
        					owner.sendMessage("Vous venez d'enfermer "+player.getName());
        					player.sendMessage("Vous avez été enfermé par§c Kumo");
        					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
        		                map.keySet().forEach(location -> location.setType(map.get(location)));
        		            }, 20*60);
        				} else {
        					owner.sendMessage("Veuiller viser un joueur !");
        				}				
        			}else {
        				sendCooldown(owner, cdprison);
        			}
        			return true;
        		}).setPosable(false).setMovableOther(false).getItemStack()
        };
    }
	
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(Items.getKumoEmprisonnement());
		owner.getInventory().addItem(Items.getKumoPrison());
		super.GiveItems();
	}
	private int cdprison = 0;//pour l'emprisonnement
	boolean prison =false;
	private int cdtoile = 0;//pour la prison
	@Override
	public void resetCooldown() {
		cdprison = 0;
		cdtoile = 0;
	}
	@Override
	public void Update(GameState gameState) {
		if (cdprison >= 1) {cdprison--;}
		if (cdtoile >=1)cdtoile--;
		if (gameState.nightTime) {
			givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
		}else {
			if (!prison) {
				givePotionEffet(owner, PotionEffectType.WEAKNESS, 60, 1, true);
			}
		}
		if (owner.getItemInHand().isSimilar(Items.getKumoEmprisonnement())) {
			sendActionBarCooldown(owner, cdprison);
		}
		if (owner.getItemInHand().isSimilar(Items.getKumoPrison()))sendActionBarCooldown(owner, cdtoile);
		super.Update(gameState);
	}
	public Set<Location> sphere(Location location, int radius, boolean hollow){
	        Set<Location> blocks = new HashSet<Location>();
	        World world = location.getWorld();
	        int X = location.getBlockX();
	        int Y = location.getBlockY();
	        int Z = location.getBlockZ();
	        int radiusSquared = radius * radius;

	        if(hollow){
	            for (int x = X - radius; x <= X + radius; x++) {
	                for (int y = Y - radius; y <= Y + radius; y++) {
	                    for (int z = Z - radius; z <= Z + radius; z++) {
	                        if ((X - x) * (X - x) + (Y - y) * (Y - y) + (Z - z) * (Z - z) <= radiusSquared) {
	                            Location block = new Location(world, x, y, z);
	                            blocks.add(block);
	                        }
	                    }
	                }
	            }
	            return makeHollow(blocks, true);
	        } else {
	            for (int x = X - radius; x <= X + radius; x++) {
	                for (int y = Y - radius; y <= Y + radius; y++) {
	                    for (int z = Z - radius; z <= Z + radius; z++) {
	                        if ((X - x) * (X - x) + (Y - y) * (Y - y) + (Z - z) * (Z - z) <= radiusSquared) {
	                            Location block = new Location(world, x, y, z);
	                            blocks.add(block);
	                        }
	                    }
	                }
	            }
	            return blocks;
	        }
	    }
	private Set<Location> makeHollow(Set<Location> blocks, boolean sphere){
	        Set<Location> edge = new HashSet<Location>();
	        if(!sphere){
	            for(Location l : blocks){
	                World w = l.getWorld();
	                int X = l.getBlockX();
	                int Y = l.getBlockY();
	                int Z = l.getBlockZ();
	                Location front = new Location(w, X + 1, Y, Z);
	                Location back = new Location(w, X - 1, Y, Z);
	                Location left = new Location(w, X, Y, Z + 1);
	                Location right = new Location(w, X, Y, Z - 1);
	                if(!(blocks.contains(front) && blocks.contains(back) && blocks.contains(left) && blocks.contains(right))){
	                    edge.add(l);
	                }
	            }
	        } else {
	            for(Location l : blocks){
	                World w = l.getWorld();
	                int X = l.getBlockX();
	                int Y = l.getBlockY();
	                int Z = l.getBlockZ();
	                Location front = new Location(w, X + 1, Y, Z);
	                Location back = new Location(w, X - 1, Y, Z);
	                Location left = new Location(w, X, Y, Z + 1);
	                Location right = new Location(w, X, Y, Z - 1);
	                Location top = new Location(w, X, Y + 1, Z);
	                Location bottom = new Location(w, X, Y - 1, Z);
	                if(!(blocks.contains(front) && blocks.contains(back) && blocks.contains(left) && blocks.contains(right) && blocks.contains(top) && blocks.contains(bottom))){
	                    edge.add(l);
	                }
	            }
	        }
	        return edge;
	    }
	@Override
	public String[] Desc() {
		return AllDesc.Kumo;
	}
}