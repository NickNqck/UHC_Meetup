package fr.nicknqck.roles.ns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import fr.nicknqck.roles.ns.solo.Danzo;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.desc.AllDesc;

public class Hokage {
	private final GameState gameState;
	private final int tWait;
	private boolean stopPlease = false;
	public UUID Hokage;
	private Player forceHokage = null;

	public Hokage(int timeW, GameState ga) {
		this.gameState = ga;
		this.tWait = timeW;
	}
	
	public void run() {
		System.out.println("Starting Hokage");
		new BukkitRunnable() {
			private int aT = 0;
			@Override
			public void run() {
				if (stopPlease ||!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
					System.out.println("Stoping Hokage System");
					stopPlease = false;
					cancel();
					return;
				}
				aT++;
				if (tWait == aT) {
					System.out.println("Searching Hokage");
					Player fH = null;
					int e = 0;
					if (forceHokage != null) {
						fH = forceHokage;
						System.out.println(fH.getDisplayName()+" will be Hokage obvsiously");
					}
					while (fH == null && e < 3) {
						fH = searchHokage();
						e++;
						System.out.println("Searched an Hokage "+e+"/3");
					}
					GameListener.SendToEveryone(AllDesc.bar);
					if (fH != null) {
						GameListener.SendToEveryone("§bLe conseil viens d'élire un nouvelle§e Hokage§b, le village est");
						GameListener.SendToEveryoneWithHoverMessage("§b maintenant sous le commandement de ", "§e"+fH.getDisplayName(), "§7La personne désigné comme étant l'§cHokage§7 obtient§c 10%§7 de§c Force§7 et de§9 Résistance", "§e.");
						Hokage = fH.getUniqueId();
						gameState.getPlayerRoles().get(fH).addBonusforce(10);
						gameState.getPlayerRoles().get(fH).addBonusResi(10);
						Player finalFH = fH;
						Player finalFH1 = fH;
						gameState.getInGamePlayers().stream().filter(p -> !gameState.hasRoleNull(p)).filter(p -> gameState.getPlayerRoles().get(p).getClass().equals(Danzo.class)).filter(p -> !p.getUniqueId().equals(finalFH.getUniqueId())).forEach(p -> p.sendMessage("§7Voici le rôle de l'Hokage: "+gameState.getPlayerRoles().get(finalFH1).type.getItem().getItemMeta().getDisplayName()+"§f (§cAttention vous êtes le seul joueur à avoir cette information§f)"));
						forceHokage = null;
					} else {
						GameListener.SendToEveryone("§7Aucun joueur n'a le niveau pour devenir§c hokage§7...");
					}
					GameListener.SendToEveryone(AllDesc.bar);
					cancel();
					System.out.println("Hokage end");
				}
			}
		}.runTaskTimer(Main.getInstance(), 0, 20);
	}
	public void stop() {
		System.out.println("stoping Hokage System");
		stopPlease = true;
	}
	private Player searchHokage() {
		System.out.println("Research Hokage");
        List<Player> canBeHokage = new ArrayList<>(gameState.getInGamePlayers());
		Collections.shuffle(canBeHokage);
		Player danzo = null;
		for (Player p : canBeHokage) {
			System.out.println(p.getDisplayName()+" can be Hokage ?");
			if (!gameState.hasRoleNull(p)) {
				if (gameState.getPlayerRoles().get(p) instanceof Danzo){
					danzo = p;
				}
				if (gameState.getPlayerRoles().get(p).isCanBeHokage()) {
					System.out.println(p.getDisplayName()+" has been choosed, role: "+gameState.getPlayerRoles().get(p).type.getItem().getItemMeta().getDisplayName());
					return p;
				}
			}
		}
		if (danzo != null){
			return danzo;
		}
		System.out.println("Searched Hokage returned NULL");
		return null;
	}
	public void onDeath(Player player, Entity damager, GameState gameState) {
		if (Hokage == null) {
			return;
		}
		if (player.getUniqueId().equals(Hokage)) {
			Player killer = null;
			if (damager instanceof Player) {
				killer = (Player)damager;
			}
			if (damager instanceof Projectile) {
				Projectile prok = (Projectile)damager;
                if (prok.getShooter() instanceof Player) {
					killer = (Player) prok.getShooter();
				}
			}
			if (killer != null) {
				if (!gameState.hasRoleNull(killer)) {
					if (gameState.getPlayerRoles().get(killer).type == GameState.Roles.Danzo) {
						if (this.Hokage != null && player.getUniqueId().equals(this.Hokage)){
							killer.sendMessage("§7Lors de la prochaine élection de l'§cHokage§7 vous serez obligatoirement élu");
							forceHokage = killer;
						}
					}
				}
			}
			if (!gameState.hasRoleNull(player)){
				this.Hokage = null;
				gameState.getPlayerRoles().get(player).addBonusforce(-10);
				gameState.getPlayerRoles().get(player).addBonusResi(-10);
			}
			run();
		}
	}
}