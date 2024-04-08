package fr.nicknqck.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.bijus.Bijus;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.TeamList;
import fr.nicknqck.scenarios.AntiDrop;
import fr.nicknqck.scenarios.Anti_Abso;

public class ItemsManager implements Listener {
 	public static final ItemStack adminWatch = Items.getAdminWatch();
	GameState gameState;
	public ItemsManager(GameState gameState) {
		this.gameState = gameState;
		jsp.add(Items.getdiamondboots());
		jsp.add(Items.getdiamondchestplate());
		jsp.add(Items.getdiamondhelmet());
		jsp.add(Items.getdiamondsword());
		jsp.add(Items.getironleggings());
		jsp.add(Items.getbow());
		instance = this;
	}
	public void clearJspList() {
		jsp.clear();
	}
	public void addItemToJspList(ItemStack... i) {
		for (ItemStack e : i) {
			jsp.add(e);
		}
	}
	public static ItemsManager instance;
	public static void ClearInventory(Player player) {
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.updateInventory();
	}
	
	public static void GiveHubItems(Player player) {
		ClearInventory(player);
		player.getInventory().setItem(0, Items.getAdminWatch());
		player.updateInventory();
		player.getInventory().setHeldItemSlot(0);
	}

	public List<ItemStack> jsp = new ArrayList<>();
	
	@EventHandler
	public void OnItemConsumed(PlayerItemConsumeEvent event) {
		ItemStack item = event.getItem();
		Player player = event.getPlayer();
		if (!gameState.getPlayerRoles().containsKey(player))return;
		gameState.getPlayerRoles().get(player).onEat(item, gameState);
		for (Player ig : gameState.getInGamePlayers()) {
			if (!gameState.hasRoleNull(ig)) {
				gameState.getPlayerRoles().get(ig).onALLPlayerEat(event, item, player);
			}
		}
		if (item.getType() == Material.GOLDEN_APPLE) {
			RoleBase role = gameState.getPlayerRoles().get(player);
			if (role == null)return;
			player.updateInventory();
			if (Anti_Abso.isAntiabsoall()) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {	
					player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20*60*2, 0, false, false), true);
					player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*4, 1, false, false), true);
	        }, 1);	
			} else if (Anti_Abso.isAntiabsoinvi()) {
				if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
						player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20*60*2, 0, false, false), true);
						player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*4, 1, false, false), true);
					}, 1);
				}
			}
		}
	}
	@EventHandler
	public void PlayerRecupItemEvent(PlayerPickupItemEvent e) {
		ItemStack s = e.getItem().getItemStack();
		if (gameState.getPlayerRoles().containsKey(e.getPlayer())) {
			e.setCancelled(gameState.getPlayerRoles().get(e.getPlayer()).onPickupItem(e.getItem()));
		}
		for (Player p : gameState.getInGamePlayers()) {
			if (!gameState.hasRoleNull(p)) {
				gameState.getPlayerRoles().get(p).onALLPlayerRecupItem(e, s);
			}
		}
		if (s.hasItemMeta()) {
			if (s.getItemMeta().hasLore() || jsp.contains(s)|| s.isSimilar(Items.getironpickaxe())|| s.isSimilar(Items.getironshovel())) {
				for (Bijus value : Bijus.values()) {
					if (s.getItemMeta().hasDisplayName()) {
						if (s.getItemMeta().getDisplayName().equalsIgnoreCase("§dGyûki") || s.getItemMeta().getDisplayName().equalsIgnoreCase("§6Kyubi")) {
							if (!gameState.hasRoleNull(e.getPlayer())) {
								if (gameState.getPlayerRoles().get(e.getPlayer()).getTeam().equals(TeamList.Jubi)) {
									return;
								}
							}
						}
						if (s.getItemMeta().getDisplayName().equals(value.getBiju().getItem().getItemMeta().getDisplayName())) {
							return;
						}
						
					}
				}
				e.setCancelled(true);
			}
		}
		if (s.isSimilar(gameState.EquipementTridi())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void OnItemDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		Material items = event.getItemDrop().getItemStack().getType();
		ItemMeta meta = event.getItemDrop().getItemStack().getItemMeta();
		if (AntiDrop.getAntiDrop() && gameState.getServerState() == ServerStates.InGame) {
			if (event.getItemDrop().getItemStack().hasItemMeta()) {
				if (meta.hasLore() || jsp.contains(event.getItemDrop().getItemStack())|| event.getItemDrop().getItemStack().isSimilar(gameState.EquipementTridi())) {
					event.setCancelled(true);
				}
			}
			for (Player p : gameState.getInGamePlayers()) {
				if (!gameState.hasRoleNull(p)) {
					gameState.getPlayerRoles().get(p).onALLPlayerDropItem(event, player, event.getItemDrop().getItemStack());
				}
			}
		}
		if (gameState.getServerState() == ServerStates.InLobby) {
			if (items != Material.AIR) {
				event.setCancelled(true);
				player.updateInventory();
			}
		}
	}
}