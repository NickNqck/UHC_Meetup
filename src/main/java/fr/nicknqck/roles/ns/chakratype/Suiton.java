package fr.nicknqck.roles.ns.chakratype;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fr.nicknqck.roles.ns.Chakra;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.ns.Chakras;

public class Suiton implements Chakra {

	@Override
	public void onPlayerDamageAnEntity(EntityDamageByEntityEvent event, Entity entity) {}

	@Override
	public Chakras getChakres() {
		return Chakras.SUITON;
	}

	@Override
	public List<UUID> getList() {
		return Suiton;
	}
	private List<UUID> Suiton = new ArrayList<>();
	@Override
	public void onSecond(GameState gameState) {}

	@Override
	public void onEntityDamage(EntityDamageEvent event, Player player) {}

	@Override
	public void onPlayerMoove(PlayerMoveEvent e, Player p, Location from, Location to) {
		if (getList().contains(p.getUniqueId())) {
			if (p.getInventory().getBoots() != null) {
				ItemStack boots = p.getInventory().getBoots();
				if (boots.hasItemMeta()) {
					if (to.getBlock().getType().name().contains("WATER")) {
						ItemMeta meta = boots.getItemMeta();
						if (!meta.hasEnchant(Enchantment.DEPTH_STRIDER)) {
							meta.addEnchant(Enchantment.DEPTH_STRIDER, 1, false);
						}
						boots.setItemMeta(meta);
						p.getInventory().setBoots(boots);
					}else {
						ItemMeta meta = boots.getItemMeta();
						if (meta.getEnchantLevel(Enchantment.DEPTH_STRIDER) == 1) {
							meta.removeEnchant(Enchantment.DEPTH_STRIDER);
							boots.setItemMeta(meta);
							p.getInventory().setBoots(boots);
						}
					}
				}
			}
		}
	}

}
