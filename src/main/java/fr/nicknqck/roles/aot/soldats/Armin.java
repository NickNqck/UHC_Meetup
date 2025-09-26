package fr.nicknqck.roles.aot.soldats;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.roles.aot.builders.SoldatsRoles;
import fr.nicknqck.roles.desc.AllDesc;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Armin extends SoldatsRoles {

	public Armin(UUID player) {
		super(player);
	}

    @Override
	public @NonNull Roles getRoles() {
		return Roles.Armin;
	}
	@Override
	public String[] Desc() {
			return new String[] {
					AllDesc.bar,
					AllDesc.role+"Armin",
					AllDesc.commande,
					"",
					AllDesc.point+"§6/aot inv <§ljoueur§r§6>§f: Permet de visualisé l'inventaire d'un joueur désignée, cette commande n'est utilisable que 2x dans la partie et ne possède pas de cooldown",
					AllDesc.point
					
			};
	}

	@Override
	public String getName() {
		return "Armin";
	}

	@Override
	public void Update(GameState gameState) {
		if (cooldown > 0) cooldown--;
	}
	private int cooldown = -1;
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[0];
	}
	private int invuse = 2;
	@Override
	public void onAotCommands(String arg, String[] args, GameState gameState) {
		if (args.length == 2) {
			if (args[1] != null) {
				Player target = Bukkit.getPlayer(args[1]);
				if (target == null) {
					getGamePlayer().sendMessage("§b"+args[1]+"§c n'existe pas !");
					return;
				}
				if (!gameState.hasRoleNull(target.getUniqueId())) {
					if (invuse > 0) {
				        Inventory doubleChest = Bukkit.createInventory(owner, 54, "Inventaire de " + target.getName());
				        for (int i = 0; i < target.getInventory().getSize(); i++) {
				        	   ItemStack item = target.getInventory().getItem(i);
					            if (gameState.getGamePlayer().get(target.getUniqueId()).getRole().getRoles() != Roles.Eren) {
					            	if (item != null && item.getType() != Material.AIR) {
						            	doubleChest.setItem(i, item.clone());
						            }
					            }else {
					            	if (item != null && item.getType() != Material.AIR) {
					            		if (item.hasItemMeta()) {
                                            doubleChest.setItem(i, item.clone());
                                        }
					            }
					        }
				        }
				        // Copier l'armure du joueur cible dans l'inventaire double coffre
				        ItemStack[] armorContents = target.getInventory().getArmorContents();
				        for (int i = 0; i < armorContents.length; i++) {
				            ItemStack armorPiece = armorContents[i];
				            if (armorPiece != null && armorPiece.getType() != Material.AIR) {
				                doubleChest.setItem(36 + i, armorPiece.clone());
				            }
				        }

				        // Ouvrir l'inventaire double coffre pour le joueur
				        owner.openInventory(doubleChest);

						owner.sendMessage("§aOuverture de l'inventaire de:§r "+target.getName());
						invuse-=1;
						owner.sendMessage("Il ne vous reste que "+(invuse)+" utilisation(s) du§6 /aot inv");
						} else {
						owner.sendMessage("Vous avez atteind le maximum d'utilisation de cette commande (2)");
					}
				}else {
					owner.sendMessage("La cible n'a pas de rôle !");
				}
			}	
		} else {
			owner.sendMessage("Veuiller indiquer le pseudo d'un joueur");	
		}
		super.onAotCommands(arg, args, gameState);
	}
	@Override
	public void resetCooldown() {
		cooldown = 0;
		invuse = 2;
	}
}