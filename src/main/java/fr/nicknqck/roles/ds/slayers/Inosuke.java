package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.roles.ds.demons.lune.Doma;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;

public class Inosuke extends RoleBase{

	public Inosuke(Player player) {
		super(player);
		for (String desc : AllDesc.Inosuke) owner.sendMessage(desc);
		this.setForce(20);
		this.setCanUseBlade(true);
		this.setResi(20);
	}
	@Override
	public Roles getRoles() {
		return Roles.Inosuke;
	}
	@Override
	public String[] Desc() {return AllDesc.Inosuke;}
	
	private int perforationcooldown = 0;
	private int mutilationcooldown = 0;
	private int tailladecooldown = 0;
	private boolean killdoma = false;
	private boolean perfoactiver = false;
	private boolean useblade = false;
	@Override
	public void resetCooldown() {
		perforationcooldown = 0;
		mutilationcooldown = 0;
		tailladecooldown = 0;
	}
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(Items.getSoufledelaBêtePerforation());
		owner.getInventory().addItem(Items.getSoufledelaBêteMutilationFurieuse());
		owner.getInventory().addItem(Items.getSoufledelaBêteTailladeOndulanceDivine());
		owner.getInventory().addItem(Items.getLamedenichirin());
		super.GiveItems();
	}
	@Override
	public void Update(GameState gameState) {
		if (owner.getItemInHand().isSimilar(Items.getSoufledelaBêtePerforation())) {
			sendActionBarCooldown(owner, perforationcooldown);
		}
		if (owner.getItemInHand().isSimilar(Items.getSoufledelaBêteMutilationFurieuse())) {
			sendActionBarCooldown(owner, mutilationcooldown);
		}
		if (owner.getItemInHand().isSimilar(Items.getSoufledelaBêteTailladeOndulanceDivine())) {
			sendActionBarCooldown(owner, tailladecooldown);
		}
		if (perforationcooldown >= 1) {perforationcooldown--;}
		if (mutilationcooldown >= 1) {mutilationcooldown--;}
		if (tailladecooldown >= 1) {tailladecooldown--;}
		if (gameState.nightTime) {
			givePotionEffet(owner, PotionEffectType.DAMAGE_RESISTANCE, 20*3, 1, true);
		}
		super.Update(gameState);
	}
	public int actualuse = 0;
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(Items.getLamedenichirin()) && !useblade) {
			owner.getInventory().addItem(Items.getLamedenichirin());
			useblade = true;
		}
		if (item.isSimilar(Items.getSoufledelaBêtePerforation())) {
			if (perforationcooldown <= 0) {
				owner.sendMessage(ChatColor.WHITE+"Activation de votre: "+ChatColor.GOLD+"Premier Croc: Perforation, "+ChatColor.WHITE+"veuiller tapée un joueur pour l'utilisation");
				perfoactiver = true;
				perforationcooldown = 60*3;
			} else {
				sendCooldown(owner, perforationcooldown);
			}
		}
		if (item.isSimilar(Items.getSoufledelaBêteMutilationFurieuse())) {
			if (mutilationcooldown <= 0) {
				for(Player p : gameState.getInGamePlayers()) {
					if (p!= owner) {
						  if(p.getLocation().distance(owner.getLocation()) <= 30) {
							  p.setHealth(p.getHealth() - 3.0);
							  p.sendMessage(ChatColor.WHITE+"Vous avez touchez par le: "+ChatColor.GOLD+"Cinquième Croc: Mutilation Furieuse");
							  owner.sendMessage(ChatColor.WHITE+"Activation de votre: "+ChatColor.GOLD+"Cinquième Croc: Mutillation Furieuse");
							  mutilationcooldown = 60*5;
						  }
					}
				}
			} else {
				sendCooldown(owner, mutilationcooldown);
			}
		}
		if (item.isSimilar(Items.getSoufledelaBêteTailladeOndulanceDivine())) {
			if (tailladecooldown <= 0) {
				owner.sendMessage(ChatColor.WHITE+"Activation de votre: "+ChatColor.GOLD+"Neuvième Croc: Taillade Ondulante Divine");
				owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*120, 0, false, false));
				if (killdoma == true) {
				} else {
					owner.setHealth(owner.getHealth() - 4.0);
				}
				tailladecooldown = 60*5;
			} else {
				sendCooldown(owner, tailladecooldown);
			}
		}
		return super.ItemUse(item, gameState);
	}
	@Override
	public void ItemUseAgainst(ItemStack item, Player victim, GameState gameState) {
		if (item.isSimilar(Items.getdiamondsword())){
			if (perfoactiver && victim != owner) {
				victim.setHealth(victim.getHealth() - 1.0);
				owner.sendMessage(ChatColor.WHITE+"Utilisation de votre: "+ChatColor.GOLD+"Premier Croc: Perforation");
				perfoactiver = false;
			}
		}
		super.ItemUseAgainst(item, victim, gameState);
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (killer == owner) {
			if (victim != owner){
				if (gameState.getInGamePlayers().contains(victim)) {
					if (gameState.getPlayerRoles().containsKey(victim)) {
						RoleBase role = gameState.getPlayerRoles().get(victim);
						if (role instanceof Doma) {
							killdoma = true;						
							owner.sendMessage(ChatColor.GRAY+"Vous venez de tuez "+ChatColor.GOLD+ role.getRoles() +ChatColor.GRAY+" vous ne prendrez plus de mallus avec votre: "+ChatColor.GOLD+"Taillade Ondulante Divine");
						
						}
					}
				}
			}
		}
		super.PlayerKilled(killer, victim, gameState);
	}
	@Override
	public void onDSCommandSend(String[] args, GameState gameState) {
		if (args[0].equalsIgnoreCase("aura")) {
			if (args.length == 2) {
				if (args[1] != null) {
				Player cible = Bukkit.getPlayer(args[1]);
					if (actualuse < 2) {
					owner.sendMessage("§7§o§m--------------------");										
					listPotionEffects(cible, owner);
					owner.sendMessage("\n(Vie maximal) "+cible.getMaxHealth());
					actualuse++;
					owner.sendMessage("Il ne vous reste que§a "+(2-actualuse)+"§f utilisation de votre§6 /ds aura");
					} else {
					owner.sendMessage("Vous avez atteind le maximum d'utilisation de cette commande (2)");
					}
				}	
			} else {
			owner.sendMessage("Veuiller indiquer le pseudo d'un joueur");	
			}
		}
	}

	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				Items.getSoufledelaBêteMutilationFurieuse(),
				Items.getSoufledelaBêtePerforation(),
				Items.getSoufledelaBêteTailladeOndulanceDivine()
		};
	}

	@Override
	public String getName() {
		return "§aInosuke";
	}
}