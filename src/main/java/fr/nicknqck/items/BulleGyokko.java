package fr.nicknqck.items;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.roles.ds.demons.lune.Gyokko;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class BulleGyokko implements Listener{
	private final GameState gameState;
	public BulleGyokko(GameState gameState) {
		this.gameState = gameState;
	}
	public static ItemStack getBulleGyokko() {
		ItemStack stack = new ItemStack(Material.NETHER_STAR, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA+"Bulle");
		meta.addEnchant(Enchantment.ARROW_INFINITE, 3, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		return stack;
	}
    public  List<Block> getBlocks(Location center, int radius, boolean hollow, boolean sphere) {
        List<Location> locs = circle(center, radius, radius, hollow, sphere, 0);
        List<Block> blocks = new ArrayList<>();

        for (Location loc : locs) {
            blocks.add(loc.getBlock());
        }

        return blocks;
    }
    public  List<Location> circle(final Location loc,final int radius,final int height,final boolean hollow,final boolean sphere,final int plusY) {
        List<Location> circleblocks = new ArrayList<>();
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();

        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int z = cz - radius; z <= cz + radius; z++) {
                for (int y = (sphere ? cy - radius : cy); y < (sphere ? cy
                        + radius : cy + height); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z)
                            + (sphere ? (cy - y) * (cy - y) : 0);

                    if (dist < radius * radius
                            && !(hollow && dist < (radius - 1) * (radius - 1))) {
                        Location l = new Location(loc.getWorld(), x, y + plusY,
                                z);
                        circleblocks.add(l);
                    }
                }
            }
        }

        return circleblocks;
    }
	@EventHandler
	public void onItemInteract(PlayerInteractEvent e) {
		if (gameState.getServerState() != ServerStates.InGame)return;
		Player player = e.getPlayer();
		if (!gameState.hasRoleNull(player.getUniqueId())) {
			if (gameState.getGamePlayer().get(player.getUniqueId()).getRole() instanceof Gyokko) {
				Gyokko gyokko = (Gyokko)gameState.getGamePlayer().get(player.getUniqueId()).getRole();
				player = gyokko.owner;
				if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if (!player.getItemInHand().isSimilar(getBulleGyokko()))return;
					if (gyokko.bullecooldown <= 0) {
						Location loc = gyokko.owner.getLocation();
						HashMap<Block, Material> iron = new HashMap<>();
						HashMap<Block, Material> water = new HashMap<>();
						gyokko.givePotionEffet(gyokko.owner, PotionEffectType.WATER_BREATHING, 20*65, 1, true);
						for (Block block : getBlocks(loc, 5, true, true)) {//true = bulle vide, true = sphere ? oui non
							iron.put(block, block.getType());
							block.setType(Material.IRON_BLOCK);
						}
						for (Block waters : getBlocks(loc, 4, false, true)) {
							water.put(waters, waters.getType());
							waters.setType(Material.WATER);
						}
						Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> iron.keySet().forEach(ez -> ez.setType(iron.get(ez))), 20*60);
						Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> water.keySet().forEach(ez -> ez.setType(water.get(ez))), 1190);//1190 = 20*59+10
                        gyokko.bullecooldown = 60*7;
					}else {
						gyokko.sendCooldown(gyokko.owner, gyokko.bullecooldown);
					}
					
				}
			}
		}
	}
}