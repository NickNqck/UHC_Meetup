package fr.nicknqck.roles.ns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import fr.nicknqck.roles.ns.builders.NSRoles;
import fr.nicknqck.roles.ns.solo.Danzo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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
                        for (UUID u : gameState.getInGamePlayers()) {
							Player p = Bukkit.getPlayer(u);
							if (p == null)continue;
							if (gameState.hasRoleNull(u))continue;
							if (gameState.getPlayerRoles().get(p).getClass().equals(Danzo.class)) {
								if (!u.equals(fH.getUniqueId())) {
									p.sendMessage("§7Voici le rôle de l'Hokage: §a"+gameState.getPlayerRoles().get(fH).getName()+"§f (§cAttention vous êtes le seul joueur à avoir cette information§f)");
								}
							}
						}
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
        List<UUID> canBeHokage = new ArrayList<>(gameState.getInGamePlayers());
		Collections.shuffle(canBeHokage);
		Danzo danzo = null;
		for (UUID u : canBeHokage) {
			Player p = Bukkit.getPlayer(u);
			if (p == null)continue;
			System.out.println(p.getDisplayName()+" can be Hokage ?");
			if (!gameState.hasRoleNull(u) && gameState.getPlayerRoles().get(p) instanceof NSRoles) {
				if (gameState.getPlayerRoles().get(p) instanceof Danzo){
					danzo = (Danzo) gameState.getPlayerRoles().get(p);
					System.out.println("p = "+p.getName()+" est Danzo");
				}
				if (((NSRoles) gameState.getPlayerRoles().get(p)).isCanBeHokage()) {
					System.out.println(p.getDisplayName()+" has been choosed, role: "+gameState.getPlayerRoles().get(p).getName());
					return p;
				}
			}
		}
		if (danzo != null){
			if (danzo.isKillHokage()) {
				Player d = Bukkit.getPlayer(danzo.getPlayer());
				if (d != null) {
					return d;
				}
			}
		}
		System.out.println("Searched Hokage returned NULL");
		return null;
	}
	public void onDeath(Player player, Entity entityKiller, GameState gameState) {
		if (Hokage == null) {
			return;
		}
		if (player.getUniqueId().equals(Hokage)) {
			if (!gameState.hasRoleNull(player.getUniqueId())){
				if (gameState.getGamePlayer().containsKey(entityKiller.getUniqueId())) {
					if (gameState.getGamePlayer().get(entityKiller.getUniqueId()).getRole() != null) {
						if (gameState.getGamePlayer().get(entityKiller.getUniqueId()).getRole() instanceof Danzo) {
							((Danzo) gameState.getGamePlayer().get(entityKiller.getUniqueId()).getRole()).setKillHokage(true);
							entityKiller.sendMessage("§7Lors de la prochaine élection de l'§cHokage§7 vous serez obligatoirement élu");
						}
					}
				}
				this.Hokage = null;
				gameState.getPlayerRoles().get(player).addBonusforce(-10);
				gameState.getPlayerRoles().get(player).addBonusResi(-10);
			}
			run();
		}
	}
}