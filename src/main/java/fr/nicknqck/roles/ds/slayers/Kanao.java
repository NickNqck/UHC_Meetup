package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import fr.nicknqck.roles.ds.demons.lune.Doma;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Kanao extends SlayerRoles {

	public Kanao(Player player) {
		super(player);
		setNoFall(true);
		this.setCanuseblade(true);
		invuse = 3;
	}
	@Override
	public Roles getRoles() {
		return Roles.Kanao;
	}
	@Override
	public String[] Desc() {return AllDesc.Kanao;}
	
	private int hanacooldown = 0;
	private int tourbicooldown = 0;
	private boolean killdoma = false;
	@Override
	public void resetCooldown() {
		hanacooldown = 0;
		tourbicooldown = 0;
	}
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(Items.getLamedenichirin());
		owner.getInventory().addItem(Items.getHanagoromoPourpre());
		owner.getInventory().addItem(Items.getTourbillondePêche());
		super.GiveItems();
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				Items.getHanagoromoPourpre(),
				Items.getTourbillondePêche()
		};
	}
	public int invuse = 3;
	@Override
	public void Update(GameState gameState) {
		if (owner.getItemInHand().isSimilar(Items.getHanagoromoPourpre())) {
			sendActionBarCooldown(owner, hanacooldown);
		}
		if (owner.getItemInHand().isSimilar(Items.getTourbillondePêche())) {
			sendActionBarCooldown(owner, tourbicooldown);
			}
		if (hanacooldown >= 1) {
			hanacooldown--;
		}
		if (tourbicooldown >= 1) {
			tourbicooldown--;
		}
		super.Update(gameState);
	}
	
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(Items.getHanagoromoPourpre())) {
			if (hanacooldown <= 0) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*90, 0, false, false));
				hanacooldown = 60*4;
			}   else {
				sendCooldown(owner, hanacooldown);
			}
		}
		if (item.isSimilar(Items.getTourbillondePêche())) {
			if (tourbicooldown <= 0) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*3, 0, false, false));
				owner.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20*30, 1, false, false));
				tourbicooldown = 60*10;
			} else {
				sendCooldown(owner, tourbicooldown);
			}
		}
		return super.ItemUse(item, gameState);
	}
	@Override
	public void onDSCommandSend(String[] args, GameState gameState) {
		if (args.length == 2) {
			if (args[1] != null) {
				Player target = Bukkit.getPlayer(args[1]);
				if (invuse > 0) {
					   // Créer un nouvel inventaire avec la taille d'un double coffre (54 emplacements)
			        Inventory doubleChest = Bukkit.createInventory(owner, 54, "Inventaire de " + target.getName());

			        // Copier le contenu de l'inventaire du joueur cible dans l'inventaire double coffre

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
					owner.sendMessage("Il ne vous reste que "+(invuse)+" utilisation(s) du§6 /ds inv");
					} else {
					owner.sendMessage("Vous avez atteind le maximum d'utilisation de cette commande (3)");
				}
			}	
		} else {
			owner.sendMessage("Veuiller indiquer le pseudo d'un joueur");
		}
		super.onDSCommandSend(args, gameState);
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (victim == owner) {
			victim.getInventory().remove(Material.NETHER_STAR);
		} else {
			if (gameState.getInGamePlayers().contains(victim)) {
				if (gameState.getPlayerRoles().containsKey(victim)) {
					RoleBase r = gameState.getPlayerRoles().get(victim);
					if (killer == owner) {
						if (r instanceof Doma && !killdoma) {
							killdoma = true;
							owner.sendMessage(ChatColor.GRAY+"Vous venez de tuez le joueur possédant le rôle de: "+ChatColor.GOLD+r.getRoles()+" "+ChatColor.GRAY+"vous obtenez donc l'item de poison de shinobu");
						}
					}
				}
			}
		}
		super.PlayerKilled(killer, victim, gameState);
	}

	@Override
	public String getName() {
		return "Kanao";
	}
}