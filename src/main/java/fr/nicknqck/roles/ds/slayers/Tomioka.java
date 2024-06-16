package fr.nicknqck.roles.ds.slayers;

import java.util.HashMap;

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
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.Cuboid;

public class Tomioka extends RoleBase{

	public Tomioka(Player player) {
		super(player);
	for (String desc : AllDesc.Tomioka) owner.sendMessage(desc);
		this.setForce(20);
		this.setCanUseBlade(true);
		gameState.addPillier(owner);
	}
	@Override
	public Roles getRoles() {
		return Roles.Tomioka;
	}
	@Override
	public String[] Desc() {
		return AllDesc.Tomioka;
	}
	private int itemcooldown = 0;
	@Override
	public void resetCooldown() {
		itemcooldown = 0;
	}
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(Items.getLamedenichirin());
		owner.getInventory().setBoots(Items.getTomiokaBoots());
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				Items.getTomiokaBoots()
		};
	}
	@Override
	public void Update(GameState gameState) {
		if (owner.getItemInHand().isSimilar(Items.getSoufleDeLeau())) {
			sendActionBarCooldown(owner, itemcooldown);
		}
		if (itemcooldown >= 1) {
			itemcooldown--;
		}
		if (itemcooldown > 60*4+20) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
				SpawnWater(5);
			}, 1);
		}
		givePotionEffet(owner, PotionEffectType.SPEED, 100, 1, true);
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(Items.getSoufleDeLeau())) {
			if (itemcooldown <= 0) {
				owner.sendMessage("Vous venez d'activer votre Soufle de L'eau");
				itemcooldown = 60*5;
				SpawnWater(5);
			} else {
				sendCooldown(owner, itemcooldown);
			}
		}
		return super.ItemUse(item, gameState);
	}
	@SuppressWarnings("deprecation")
	private void SpawnWater(int size) {
		HashMap<Block, Location> BL = new HashMap<>();
		World w = owner.getWorld();
		Location l1 = new Location(w, owner.getLocation().getBlockX()+size, owner.getLocation().getBlockY(), owner.getLocation().getBlockZ()+size);
		Location l2 = new Location(w, owner.getLocation().getBlockX()-size, owner.getLocation().getBlockY(), owner.getLocation().getBlockZ()-size);
		Cuboid cube = new Cuboid(l1, l2);
		for (int x = cube.getLowerX(); x < cube.getUpperX(); x++) {
			for (int z = cube.getLowerZ(); z < cube.getUpperZ(); z++) {
				Block block = w.getBlockAt(x, w.getHighestBlockYAt(x, z), z);
				if (block.getTypeId() != 9 && w.getBlockAt(x, w.getHighestBlockYAt(x, z)-1, z).getType() != Material.STATIONARY_WATER && w.getBlockAt(x, w.getHighestBlockYAt(x, z)-1, z).getType() != Material.WATER) {
					w.getBlockAt(x, w.getHighestBlockYAt(x, z), z).setTypeId(9, false);
					BL.put(block, block.getLocation().clone());
				}
			}
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			BL.keySet().stream().filter(e -> e.getTypeId() == 9 || e.getType().equals(Material.WATER) || e.getType().equals(Material.STATIONARY_WATER)).forEachOrdered(e -> e.setType(Material.AIR));
		}, 20);
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (victim == owner) {
			victim.getInventory().remove(Items.getSoufleDeLeau());
			victim.getInventory().remove(Items.getTomiokaBoots());
		}
		super.PlayerKilled(killer, victim, gameState);
	}

	@Override
	public String getName() {
		return "§aTomioka";
	}
}