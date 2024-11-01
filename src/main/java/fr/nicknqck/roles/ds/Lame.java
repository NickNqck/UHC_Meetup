package fr.nicknqck.roles.ds;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.ds.builders.DemonsSlayersRoles;
import fr.nicknqck.roles.ds.builders.Lames;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lame implements Listener{

	@EventHandler
	public void ItemUse(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack item = e.getItem();
		if (item == null)return;
		if (p == null)return;
		if (item.isSimilar(Items.getLamedenichirin())) {
			GameState gameState = GameState.getInstance();
			if(gameState.getServerState() != ServerStates.InGame)return;
			if (!gameState.hasRoleNull(p)) {
				if (gameState.getPlayerRoles().get(p) instanceof DemonsSlayersRoles) {
					DemonsSlayersRoles role = (DemonsSlayersRoles) gameState.getPlayerRoles().get(p);
					if (role.isCanuseblade()) {
						if (role.getLames() != null) {
							p.sendMessage("§cVous avez déjà une lame de nichirin");
						} else {
							Lames toGett;
							List<Lames> lames = new ArrayList<>();
                            Collections.addAll(lames, Lames.values());
							lames.removeAll(role.getCantHave());
							Collections.shuffle(lames, Main.RANDOM);
							toGett = lames.get(0);
							toGett.getConsumer().accept(e);
							giveLame(role, toGett);
							e.getPlayer().setItemInHand(new ItemStack(Material.AIR));
						}
					}
				} else {
					p.sendMessage("Vous n'avez pas accès à la lame de Nichirin");
				}
			} else {
				p.sendMessage("Il faut avoir un rôle pour obtenir une lame");
			}
		}
	}
	@EventHandler
	private void onBattle(UHCPlayerBattleEvent event){
		if (event.getDamager().getRole() != null && event.getDamager().getRole() instanceof DemonsSlayersRoles) {
			DemonsSlayersRoles role = (DemonsSlayersRoles) event.getDamager().getRole();
			if (role.isHasblade()) {
				int i = role.getLames().getUsers().get(role.getPlayer());
				role.getLames().getUsers().remove(role.getPlayer(), i);
				if (Main.RANDOM.nextInt(100) < 10) {
					i-=1;
				}
				role.getLames().getUsers().put(role.getPlayer(), i);
			}
		}
	}
	private void giveLame(DemonsSlayersRoles role, Lames lames){
		lames.getUsers().put(role.getPlayer(), 40);
		role.setLames(lames);
		role.owner.sendMessage("§7Vous avez obtenue la lame de "+lames.getName());
		role.owner.setHealth(role.owner.getMaxHealth());
	}
}