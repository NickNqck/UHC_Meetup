package fr.nicknqck.roles.ds.demons;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.slayers.*;
import fr.nicknqck.roles.ds.slayers.pillier.TomiokaV2;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class DemonMain extends DemonInferieurRole {

	public DemonMain(UUID player) {
		super(player);
		this.setResi(20);
	}
	@Override
	public @NonNull TeamList getOriginTeam() {
		return TeamList.Demon;
	}
	@Override
	public @NonNull Roles getRoles() {
		return Roles.DemonMain;
	}
	@Override
	public String getName() {
		return "Demon Main";
	}

	@Override
	public @NonNull DemonType getRank() {
		return DemonType.DEMON;
	}

	@Override
	public String[] Desc() {
		return AllDesc.DemonMain;
	}
	private int itemcooldown = 0;
	private boolean killurokodaki = false;
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(Items.getPouvoirSanginaire());
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				Items.getPouvoirSanginaire()
		};
	}
	@Override
	public void Update(GameState gameState) {
		if (owner.getItemInHand().isSimilar(Items.getPouvoirSanginaire())) {
			sendActionBarCooldown(owner, itemcooldown);
		}
		if (itemcooldown >= 1) {
			itemcooldown--;
		}
		if (gameState.nightTime) {
			owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*4, 0, false, false), true);
		} else {
			if (killurokodaki) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*4, 0, false, false), true);
			}
		}
		super.Update(gameState);
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(Items.getPouvoirSanginaire())) {
			if (itemcooldown <= 0) {
				for(UUID u : gameState.getInGamePlayers()) {
					Player p = Bukkit.getPlayer(u);
					if (p == null)continue;
					if (p!= owner) {
						  if(p.getLocation().distance(owner.getLocation()) <= 30) {
							  if (p.getHealth() > 3.0) {
								  p.setHealth(p.getHealth() - 3.0);
							  }else {
								  p.setHealth(0.5);
							  }
							  p.sendMessage("Vous avez touchez par le: "+ChatColor.GOLD+"Démon Main");
							  owner.sendMessage("Vos mains on touché le joueur: "+p.getName());
							  itemcooldown = 60*2;
						  }
					}
				}
			}  else {
				sendCooldown(owner, itemcooldown);
			}
		}
		return super.ItemUse(item, gameState);
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (killer == owner) {
			if (victim != owner){
				if (gameState.getInGamePlayers().contains(victim.getUniqueId())) {
					if (!gameState.hasRoleNull(victim.getUniqueId())) {
						final RoleBase role = gameState.getGamePlayer().get(victim.getUniqueId()).getRole();
						if (role instanceof UrokodakiV2) {
							killurokodaki = true;						
							owner.sendMessage(ChatColor.GRAY+"Vous venez de tuez "+ChatColor.GOLD+ role.getRoles() +ChatColor.GRAY+" vous obtenez donc "+ChatColor.GOLD+"force 1 le jour");
						}
						if (role instanceof Tanjiro || role instanceof SabitoV2 || role instanceof TomiokaV2 || role instanceof Makomo) {
							owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60*2, 0, false, false), true);							
							owner.sendMessage("Vous venez de tuez: "+victim.getName()+" qui était: "+role.getRoles()+" ce qui vous fait gagner résistance 1 pendant 2 minutes");
						}
					}
				}
			}
		}
		super.PlayerKilled(killer, victim, gameState);
	}

	@Override
	public void resetCooldown() {
		itemcooldown = 0;
	}
}