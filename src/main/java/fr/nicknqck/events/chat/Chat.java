package fr.nicknqck.events.chat;

import java.util.Collections;

import fr.nicknqck.utils.rank.ChatRank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.utils.CC;

public class Chat implements Listener{
	private static ChatColor opcolor = ChatColor.RED;
    private final GameState gameState;
	
	public Chat(GameState gameState) {
		this.gameState = gameState;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerTalk(org.bukkit.event.player.PlayerChatEvent e) {
		Player p = e.getPlayer();
		String msg = e.getMessage();
		String debut = ChatColor.GRAY+"§l» §r";
		ChatRank rank = ChatRank.getPlayerGrade(p);
		if (rank.equals(ChatRank.Op)) {
			if (msg.startsWith("@")) {
				for (Player pl : Bukkit.getOnlinePlayers()) {
					if (pl.isOp()) {
						pl.sendMessage(CC.translate("§9StaffChat ┃ " + p.getName() + " §f» " + e.getMessage().substring(1)));
					}
				}
				e.setCancelled(true);
				return;
			}
		}
		if (gameState.getServerState() == ServerStates.InLobby) {
			e.setFormat(CC.translate(debut+rank.getFullPrefix()+p.getName()+":§r "+msg));
		}else {
			if (gameState.getInSpecPlayers().contains(p)) {
				for (Player spec : gameState.getInSpecPlayers()) {
					spec.sendMessage("§7(§lSPEC§7) "+p.getDisplayName()+"§7:§f "+CC.translate(msg));
				}
				e.setCancelled(true);
            }else {
				for (Player ig : gameState.getInGamePlayers()) {
					if (!gameState.hasRoleNull(ig)) {
						if (gameState.getInGamePlayers().contains(p)) {
							if (!gameState.hasRoleNull(p)) {
								gameState.getPlayerRoles().get(ig).onAllPlayerChat(e, p);
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
	public static ChatColor getopColor() {return opcolor;}
	public static void setopColor(ChatColor c) {opcolor = c;}
	public static ItemStack getColoritem() {
		ItemStack stack = new ItemStack(Material.FEATHER, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(getopColor()+"Une phrase au hasard");
		meta.setLore(Collections.singletonList("Clique pour changer la couleur des gens qui sont op sur le serveur"));
		stack.setItemMeta(meta);
		return stack;
	}
}