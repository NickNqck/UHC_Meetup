package fr.nicknqck.events.chat;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.utils.rank.ChatRank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class Chat implements Listener {

    private final GameState gameState;
	
	public Chat(GameState gameState) {
		this.gameState = gameState;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerTalk(org.bukkit.event.player.PlayerChatEvent e) {
		Player p = e.getPlayer();
		String msg = e.getMessage();
		String debut = "§7§l» §r";
		ChatRank rank = ChatRank.getPlayerGrade(p);
		if (rank.equals(ChatRank.Op)) {
			if (msg.startsWith("@")) {
				for (Player pl : Bukkit.getOnlinePlayers()) {
					if (pl.isOp()) {
						pl.sendMessage(ChatColor.translateAlternateColorCodes('&', "§9StaffChat ┃ " + p.getName() + " §f» " + e.getMessage().substring(1)));
					}
				}
				e.setCancelled(true);
				return;
			}
		}
		if (gameState.getServerState() == ServerStates.InLobby) {
			e.setFormat(ChatColor.translateAlternateColorCodes('&', debut+rank.getFullPrefix()+(p.getName().toLowerCase().contains("boulot") ? "(§f§lGoat§r"+rank.getColor()+") " : (p.getUniqueId().equals(UUID.fromString("a674c7e4-8cff-4eb5-bb54-5a9397eea4e3")) ? "(§f§lGoat§r"+rank.getColor()+") " : ""))+p.getName()+":§r "+msg));
		}else {
			if (gameState.getInSpecPlayers().contains(p)) {
				for (Player spec : gameState.getInSpecPlayers()) {
					spec.sendMessage("§7(§lSPEC§7) "+p.getDisplayName()+"§7:§f "+ChatColor.translateAlternateColorCodes('&', msg));
				}
				e.setCancelled(true);
            }else {
				for (UUID u : gameState.getInGamePlayers()) {
					Player ig = Bukkit.getPlayer(u);
					if (ig == null)continue;
					if (!gameState.hasRoleNull(u)) {
						if (gameState.getInGamePlayers().contains(p.getUniqueId())) {
							if (!gameState.hasRoleNull(p.getUniqueId())) {
								gameState.getGamePlayer().get(ig.getUniqueId()).getRole().onAllPlayerChat(e, p);
							}
						}
					}
				}
				e.setCancelled(true);
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerChat(AsyncPlayerChatEvent event) {
		ChatRank.updateRank(event.getPlayer());
		String message = event.getMessage();
		message = message.replace("&", "§");
		event.setMessage(message);
	}
}