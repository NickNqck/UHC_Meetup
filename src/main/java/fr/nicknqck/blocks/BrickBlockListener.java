package fr.nicknqck.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;

public class BrickBlockListener implements Listener {

    private final Map<Block, Long> blockMap = new HashMap<>();
    private final List<Block> blocksToDestroy = new ArrayList<>();
//toute ce qui concerne cette class a été crée par chatgpt
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
    	if (GameState.getInstance().getServerState() != ServerStates.InLobby) {
    		 Block block = event.getBlock();        
    	        blockMap.put(block, System.currentTimeMillis());
    	        blocksToDestroy.add(block);
    	        Main.getInstance().getServer().getScheduler().runTaskLater(Main.getInstance(), () -> {
    	        	if (blocksToDestroy.contains(block)) {
    	        		block.setType(Material.AIR);
    	                blocksToDestroy.remove(block);
    	               }
    	            }, 20 * 60);
    	}
    }
    
    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        if (event.getItemStack().getType() == Material.WATER_BUCKET || event.getItemStack().getType() == Material.LAVA_BUCKET) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    event.getBlockClicked().setType(Material.AIR);
                    
                }
            }.runTaskLater(Main.getInstance(), 1200L); // 1 minute = 60 seconds = 1200 ticks (1 tick = 1/20 second)
        }
    }
}