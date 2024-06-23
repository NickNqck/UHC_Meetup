package fr.nicknqck.roles.ds;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.ds.builders.DemonsSlayersRoles;
import fr.nicknqck.roles.ds.builders.Lames;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import fr.nicknqck.roles.ds.demons.lune.Kaigaku;
import fr.nicknqck.roles.ds.demons.lune.Kokushibo;
import fr.nicknqck.utils.RandomUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

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
					if (role instanceof SlayerRoles || role instanceof Kokushibo || role instanceof Kaigaku) {
						if (role.getLames() != null) {
							p.sendMessage("§cVous avez déjà une lame de nichirin");
						} else {
							Lames toGett = null;
							while (toGett == null){
								for (Lames lames : Lames.values()){
									if (RandomUtils.getOwnRandomProbability(15)){
										toGett = lames;
										break;
									}
								}
							}
							toGett.getConsumer().accept(e);
							giveLame(role, toGett);
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
	public static void giveLame(DemonsSlayersRoles role, Lames lames){
		lames.getUsers().put(role.getUuidOwner(), 40);
		role.setLames(lames);
		role.owner.sendMessage("§7Vous avez obtenue la lame de "+lames.name());
	}
}