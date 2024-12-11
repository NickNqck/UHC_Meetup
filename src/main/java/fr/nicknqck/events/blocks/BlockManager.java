package fr.nicknqck.events.blocks;

import java.util.Arrays;

import fr.nicknqck.roles.aot.builders.AotRoles;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.items.Items;
import fr.nicknqck.scenarios.impl.CutClean;
import fr.nicknqck.scenarios.impl.DiamondLimit;
import fr.nicknqck.utils.packets.NMSPacket;

public class BlockManager implements Listener{
	
	GameState gameState;
	
	public BlockManager(GameState gameState) {
		this.gameState = gameState;
	}
	
	 @EventHandler
	 public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		Block block = event.getBlockClicked().getRelative(event.getBlockFace());
	        if (event.getBucket() == Material.WATER_BUCKET) {
				if (Main.getInstance().getGameConfig().getWaterEmptyTiming() <= 0)return;
	            new BukkitRunnable() {
	                @Override
	                public void run() {
	                    if (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER) {
	                        block.setType(Material.AIR);
	                        cancel();
	                    }
	                }
	            }.runTaskLater(Main.getInstance(), 20L *Main.getInstance().getGameConfig().getWaterEmptyTiming());
	        }
	        if (!Main.getInstance().getGameConfig().isLaveTitans()) {
	        	if (!gameState.hasRoleNull(event.getPlayer())) {
	        		if (gameState.getPlayerRoles().get(event.getPlayer()) instanceof AotRoles && ((AotRoles) gameState.getPlayerRoles().get(event.getPlayer())).isTransformedinTitan) {
	        			if (event.getBucket() == Material.LAVA_BUCKET) {
	            			event.getPlayer().sendMessage("§7Impossible de poser de la§c§l lave§7 lorsque vous êtes§l transformé en titan§7 !");
		        			event.setCancelled(true);
		        			return;
	        			}
	        		}
	        	}
	        }
	        if (event.getBucket() == Material.LAVA_BUCKET) {
				if (Main.getInstance().getGameConfig().getLavaEmptyTiming() <= 0)return;
	        	new BukkitRunnable() {
	        		public void run() {
	        				block.setType(Material.AIR);
	        				cancel();
	        		}
                }.runTaskLater(Main.getInstance(), 20L *Main.getInstance().getGameConfig().getLavaEmptyTiming());
	        }
	    }
	@EventHandler
	public void OnBlockPlaced(org.bukkit.event.block.BlockPlaceEvent event) {
		for (ItemStack is : Arrays.asList(
				Items.getSoufleDeLaBrume(),
				Items.getSoufleDeLaRoche(),
				Items.getAdminWatch(),
				Items.getKumoEmprisonnement(),
				Items.getKumoPrison(),
				Items.getCharm())){//variable for
			if (event.getItemInHand().isSimilar(is)) event.setCancelled(true);
			event.getPlayer().updateInventory();
		}
		if (gameState.getServerState() == ServerStates.InGame) {
			if (gameState.getPlayerRoles().containsKey(event.getPlayer())) {
				for (ItemStack ne : gameState.getPlayerRoles().get(event.getPlayer()).getItems()) {
					if (event.getItemInHand().isSimilar(ne)) {
						event.setCancelled(true);
						event.getPlayer().updateInventory();
					}
				}
			}
			if (!gameState.hasRoleNull(event.getPlayer())) {
				for (Power power : gameState.getGamePlayer().get(event.getPlayer().getUniqueId()).getRole().getPowers()) {
					if (power instanceof ItemPower) {
						if (event.getItemInHand().isSimilar(((ItemPower) power).getItem()))  {
							((ItemPower) power).call(event);
						}
					}
				}
			}
		}
	}
	@EventHandler
	public void BlockBreak(org.bukkit.event.block.BlockBreakEvent e) {
		Player player = e.getPlayer();
		Block block = e.getBlock();
		Material type = block.getType();
		Location loc = block.getLocation();
		World world = block.getWorld();
		if (player != null) {
            if (block.getType() == Material.PACKED_ICE) {
                e.setCancelled(true);
                return;
            }
            if (Bukkit.getWorld("Gamabunta") != null) {
                if (block.getWorld().equals(Bukkit.getWorld("Gamabunta"))) {
                    if (type != Material.BRICK && type != Material.COBBLESTONE && type != Material.OBSIDIAN) {
                        e.setCancelled(true);
                        return;
                    }
                }
            }
            if (Bukkit.getWorld("Kamui") != null) {
                if (block.getWorld().getName().equals("Kamui")) {
                    if (type != Material.BRICK && type != Material.COBBLESTONE && type != Material.OBSIDIAN) {
                        e.setCancelled(true);
                        return;
                    }
                }
            }
            if (CutClean.isCutClean()) {
                CutClean(type, loc, world, block, gameState);
            }
            if (type == Material.STONE) {
                block.setType(Material.AIR);
                GameListener.dropItem(loc, new ItemStack(Material.COBBLESTONE));
            }
            if (type == Material.DIAMOND_ORE) {
                ExperienceOrb expOrb = (ExperienceOrb) world.spawnEntity(loc, EntityType.EXPERIENCE_ORB);
                expOrb.setExperience(xpdiams+gameState.xpdiams);
                block.setType(Material.AIR);
                if (DiamondLimit.isLimit()) {
                    if (DiamondLimit.getdiamsmined() >= DiamondLimit.getmaxdiams()) {
                        GameListener.dropItem(loc, new ItemStack(Material.GOLD_INGOT, 1));
                    } else {
                        GameListener.dropItem(loc, new ItemStack(Material.DIAMOND, 1));
                        DiamondLimit.setdiamsmined(DiamondLimit.getdiamsmined() + 1);
                        NMSPacket.sendActionBar(player, ChatColor.AQUA+"Diamond Limite: "+DiamondLimit.getdiamsmined()+"/"+DiamondLimit.getmaxdiams());
                    }
                }
            }
			if (!gameState.hasRoleNull(player)) {
				for (Power power : gameState.getGamePlayer().get(player.getUniqueId()).getRole().getPowers()) {
					if (power instanceof ItemPower) {
						for (ItemStack itemStack : e.getBlock().getDrops()) {
							if (itemStack.isSimilar(((ItemPower) power).getItem())) {
								((ItemPower) power).call(e);
							}
						}
					}
				}
			}
        }
		System.out.println(e.isCancelled());
	}
	@EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntityType() == EntityType.MINECART_TNT || event.getEntityType().equals(EntityType.PRIMED_TNT)) {
        	if (!gameState.isTNTGrief()) {
        		event.blockList().clear();
        	}
        }
    }
	public static int xpfer =(int)1.25;
	public static int xpor =(int)1.5;
	public static int xpcharbon = 1;
	public static int xpdiams = 2;
	private void CutClean(Material type, Location loc, World world, Block block, GameState gameState) {
		if (type != Material.IRON_ORE && type != Material.GOLD_ORE && type != Material.COAL_ORE)return;
		ExperienceOrb expOrb = (ExperienceOrb) world.spawnEntity(loc, EntityType.EXPERIENCE_ORB);
		int xp;
		if (type == Material.IRON_ORE) {
			xp = xpfer;
            expOrb.setExperience(xp +gameState.xpfer);
			block.setType(Material.AIR);
			GameListener.dropItem(loc, new ItemStack(Material.IRON_INGOT, 1));
		}
		if (type == Material.GOLD_ORE) {
			xp = xpor;
            expOrb.setExperience(xp +gameState.xpor);
			block.setType(Material.AIR);
			GameListener.dropItem(loc, new ItemStack(Material.GOLD_INGOT, 1));
		}
		if (type == Material.COAL_ORE) {
			xp = xpcharbon;
            expOrb.setExperience(xp +gameState.xpcharbon);
			block.setType(Material.AIR);
			GameListener.dropItem(loc, new ItemStack(Material.TORCH, 4));
		}
	}
}