package fr.nicknqck.roles.aot.titanrouge;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.aot.builders.TitansRoles;
import fr.nicknqck.roles.desc.AllDesc;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Jelena extends TitansRoles {

	public Jelena(Player player) {
		super(player);
		gameState.GiveRodTridi(owner);
	}
	@Override
	public Roles getRoles() {
		return Roles.Jelena;
	}
	@Override
	public String[] Desc() {
		Main.getInstance().getGetterList().getTitanRougeList(owner);
		KnowRole(owner, Roles.TitanBestial, 20);
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"Jelena",
				"",
				AllDesc.commande,
				"",
				AllDesc.point+"§6/aot inv <§ljoueur§r§6>§f: Permet de visualisé l'inventaire d'un joueur désignée, cette commande n'est utilisable que 2x dans la partie et ne possède pas de cooldown,Les joueurs espionnés recevront un message comme si c'était §aArmin qui avait utilisé la commande.",
				"",
				AllDesc.point+"Si vous espionnez Eren vous obtindrez l'inventaire d'un soldat aléatoire",
				"",
				AllDesc.bar,
		};
	}

	@Override
	public String getName() {
		return "Jelena";
	}

	private int invuse = 2;
	@Override
	public void onAotCommands(String arg, String[] args, GameState gameState) {
		if (args.length == 2) {
			if (args[1] != null) {
				Player target = Bukkit.getPlayer(args[1]);
				if (!gameState.hasRoleNull(target)) {
					if (invuse > 0) {
						   // Créer un nouvel inventaire avec la taille d'un double coffre (54 emplacements)
				        Inventory doubleChest = Bukkit.createInventory(owner, 54, "Inventaire de " + target.getName());

				        // Copier le contenu de l'inventaire du joueur cible dans l'inventaire double coffre
				        for (int i = 0; i < target.getInventory().getSize(); i++) {
				            ItemStack item = target.getInventory().getItem(i);
				            if (getPlayerRoles(target).getRoles() != Roles.Eren) {
				            	if (item != null && item.getType() != Material.AIR) {
					            	doubleChest.setItem(i, item.clone());
					            }
				            }else {
				            	if (item != null && item.getType() != Material.AIR) {
				            		if (item.hasItemMeta()) {
				            			if (item.getItemMeta().hasLore()) {
				            				System.out.println(item.getItemMeta().getDisplayName()+" n'a pas ete scan par Armin");
				            			}else {
				            				doubleChest.setItem(i, item.clone());
				            			}
				            		}else {
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
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[0];
	}
	@Override
	public void resetCooldown() {
		invuse = 2;
	}
}