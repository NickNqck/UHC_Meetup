package fr.nicknqck.roles.ds.slayers.pillier;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.demons.lune.Doma;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Shinobu extends PillierRoles {

	public Shinobu(Player player) {
		super(player);
        this.setCanuseblade(true);
	}
	@Override
	public Roles getRoles() {
		return Roles.Shinobu;
	}
	@Override
	public String[] Desc() {
		return AllDesc.Shinobu;
	}

    private int MedicamentCooldown = 0;
	private int InjectionCooldown = 0;
	private boolean Injection = false;
	private boolean killdoma = false;
	@Override
	public void resetCooldown() {
		InjectionCooldown = 0;
		MedicamentCooldown = 0;
	}
	public void GiveItems() {
		owner.getInventory().addItem(Items.getShinobuMedicament());
		owner.getInventory().addItem(Items.getShinobuInjection());
		owner.getInventory().addItem(Items.getLamedenichirin());
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				Items.getShinobuMedicament(),
				Items.getShinobuInjection()
		};
	}
	public void Update(GameState gameState) {
		if (owner.getItemInHand().isSimilar(Items.getShinobuMedicament())) {
			sendActionBarCooldown(owner, MedicamentCooldown);
		}
		if (owner.getItemInHand().isSimilar(Items.getShinobuInjection())) {
			sendActionBarCooldown(owner, InjectionCooldown);
		}
		if (gameState.nightTime) {
			owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*3, 0, false, false));
		if (killdoma) {
			owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 0, false, false));
		}
		}
		if (MedicamentCooldown >= 1) {
			MedicamentCooldown--;
		}
		if (InjectionCooldown >= 1) {
			InjectionCooldown--;
		}
		if (InjectionCooldown == 60*7-30) {
			Injection = false;
			owner.sendMessage("Vous n'empoisonnerez plus les joueurs que vous tapez");
		}
		if (owner.hasPotionEffect(PotionEffectType.POISON)) {
			owner.removePotionEffect(PotionEffectType.POISON);
		}
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if(item.isSimilar(Items.getShinobuMedicament())) {
			if (MedicamentCooldown<= 0) {
				if (owner.getHealth() <= this.getMaxHealth() - 4.0) {
					MedicamentCooldown = 60;
					owner.setHealth(owner.getHealth() + 4.0);
					owner.sendMessage("Votre médicament viens de vous heal 2 coeurs");
				} else {
					owner.sendMessage("Il te manque pas asser de vie pour utiliser cette item");
				}
			} else {
				int s = MedicamentCooldown%60;
				int m = MedicamentCooldown/60;
				owner.sendMessage(ChatColor.RED+"Vous ne pourrez utiliser a nouveau votre abilité que dans "+ChatColor.GOLD+m+" minutes"+ChatColor.RED+" et "+ChatColor.GOLD+s+" secondes");
				return true;
			}
				
		}
		if (item.isSimilar(Items.getShinobuInjection())) {
			if (InjectionCooldown <= 0) {
				Injection = true;
				InjectionCooldown = 60*4;
				owner.sendMessage("La prochaine personne que vous taperez obtiendra Poison 3 pendant 12 secondes");
			}
		}
		return super.ItemUse(item, gameState);
	}
	@Override
	public void ItemUseAgainst(ItemStack item, Player victim, GameState gameState) {
		if (item.isSimilar(Items.getdiamondsword())) {
			if (Injection) {
				if (!victim.hasPotionEffect(PotionEffectType.POISON)) {
					victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*12, 2, false, false));
					owner.sendMessage("Vous avez toucher le joueur: "+ChatColor.GOLD+ victim.getName());
					Injection = false;
				}
			}
		}
		super.ItemUseAgainst(item, victim, gameState);
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (victim != owner) {
			if (gameState.getInGamePlayers().contains(victim)) {
				if (gameState.getPlayerRoles().containsKey(victim)) {
					RoleBase r = gameState.getPlayerRoles().get(victim);
					if (killer == owner) {
						if (r instanceof Doma && !killdoma) {
							killdoma = true;
							owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 0, false, false));
							owner.sendMessage(ChatColor.GRAY+"Vous venez de tuez le joueur possédant le rôle de: "+ChatColor.GOLD+"Doma "+ChatColor.GRAY+"vous obtenez donc Speed 1 le jour");
							}
					}
			}
		}
	}
		if (victim == owner) {
				if (gameState.getInGamePlayers().contains(killer)) {
					if (gameState.getPlayerRoles().containsKey(killer)) {
						RoleBase r = gameState.getPlayerRoles().get(killer);
						if (killer != null) {
							if (r instanceof Doma) {
								killer.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*30, 0, false, false));
								killer.sendMessage("En tuant le joueur: "+ChatColor.GOLD+ owner.getName() +ChatColor.WHITE+" vous recevez poison 1 pendant 30 secondes");
							} else {
								killer.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*15, 0, false, false));
								killer.sendMessage("En tuant le joueur: "+ChatColor.GOLD+ owner.getName() +ChatColor.WHITE+" vous recevez poison 1 pendant 15 secondes");
							}
						}
				}
			}
		
		}
	super.PlayerKilled(killer, victim, gameState);
	}

	@Override
	public String getName() {
		return "Shinobu";
	}
}