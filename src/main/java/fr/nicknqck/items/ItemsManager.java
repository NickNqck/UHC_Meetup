package fr.nicknqck.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
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
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.scenarios.impl.AntiDrop;
import fr.nicknqck.scenarios.impl.Anti_Abso;

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
        jsp.addAll(Arrays.asList(i));
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
		if (gameState.hasRoleNull(player.getUniqueId()))return;
		final RoleBase role = gameState.getGamePlayer().get(player.getUniqueId()).getRole();
		role.onEat(item, gameState);
		for (UUID u : gameState.getInGamePlayers()) {
			Player ig = Bukkit.getPlayer(u);
			if (ig == null)continue;
			if (!gameState.hasRoleNull(ig.getUniqueId())) {
				gameState.getGamePlayer().get(u).getRole().onALLPlayerEat(event, item, player);
			}
		}
		if (item.getType() == Material.GOLDEN_APPLE) {
            player.updateInventory();
			if (Anti_Abso.isAntiabsoall()) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
					player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20*60*2, 0, false, false), true);
					player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*4, 1, false, false), true);
	        }, 1);	
			} else if (Anti_Abso.isAntiabsoinvi()) {
				if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
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
		if (!gameState.hasRoleNull(e.getPlayer().getUniqueId())) {
			e.setCancelled(gameState.getGamePlayer().get(e.getPlayer().getUniqueId()).getRole().onPickupItem(e.getItem()));
		}
		if (s.hasItemMeta()) {
			if (s.getItemMeta().hasLore() || jsp.contains(s)|| s.isSimilar(Items.getironpickaxe())|| s.isSimilar(Items.getironshovel())) {
				for (Bijus value : Bijus.values()) {
					if (s.getItemMeta().hasDisplayName()) {
						String name = s.getItemMeta().getDisplayName();
						if (name.equalsIgnoreCase("§dGyûki") || name.equalsIgnoreCase("§6Kyubi") || name.equalsIgnoreCase("§6Kyûbi")) {
							if (!gameState.hasRoleNull(e.getPlayer().getUniqueId())) {
								if (gameState.getGamePlayer().get(e.getPlayer().getUniqueId()).getRole().getOriginTeam().equals(TeamList.Jubi)) {
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
					return;
				}
			}
		}
		if (!gameState.hasRoleNull(player.getUniqueId())) {
			for (Power power : gameState.getGamePlayer().get(player.getUniqueId()).getRole().getPowers()) {
				if (power instanceof ItemPower) {
					if (event.getItemDrop().getItemStack().isSimilar(((ItemPower) power).getItem())) {
						((ItemPower) power).call(event);
					}
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