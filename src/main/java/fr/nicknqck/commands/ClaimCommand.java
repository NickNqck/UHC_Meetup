package fr.nicknqck.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.builder.RoleBase;

public class ClaimCommand implements CommandExecutor {
	private final GameState gameState;
	public ClaimCommand(GameState gameState) {
		this.gameState = gameState;
	}

	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if (s instanceof Player) {
			Player sender = (Player)s;
			if (!gameState.hasRoleNull(sender.getUniqueId())) {
				RoleBase role = gameState.getGamePlayer().get(sender.getUniqueId()).getRole();
				if (!role.toClaim.isEmpty()) {
					if (role.toClaim.size() <= 9) {
						org.bukkit.inventory.Inventory inv = Bukkit.createInventory(sender, 9, "§c/claim");
						for (ItemStack item : role.toClaim) {
							inv.addItem(item);
						}
						sender.openInventory(inv);
						sender.updateInventory();
					} else {
						for (ItemStack item : role.toClaim) {
							if (gameState.countEmptySlots(sender) > 0) {
								if (item.hasItemMeta()) {
									if (item.getItemMeta().hasDisplayName()) {
										role.giveItem(sender, true, item);
										role.toClaim.remove(item);
										sender.sendMessage("§7Vous avez reçus§r "+item.getItemMeta().getDisplayName());
									}else {//else du hasDisplayName
										role.giveItem(sender, true, item);
										role.toClaim.remove(item);
										sender.sendMessage("§7Vous avez reçus§r "+item.getType().name());
									}
								}else {//else du hasItemMeta
									role.giveItem(sender, true, item);
									role.toClaim.remove(item);
									sender.sendMessage("§7Vous avez reçus§r "+item.getType().name());
								}
							}else {//else du countEmptySlots
								if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
									sender.sendMessage("§cVous n'avez pas reçus§r "+item.getItemMeta().getDisplayName());
								}else {
									sender.sendMessage("§cVous n'avez pas reçus§r "+item.getType().name());
								}
							}
						}
						return true;
					}
				}else {
					sender.sendMessage("Vous n'avez rien à récupérée");
					return true;
				}
			}
		}
		return false;
	}

}
