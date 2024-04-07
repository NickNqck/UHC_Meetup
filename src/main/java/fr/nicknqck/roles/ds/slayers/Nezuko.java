package fr.nicknqck.roles.ds.slayers;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.particles.DoubleCircleEffect;
import net.minecraft.server.v1_8_R3.EnumParticle;

public class Nezuko extends RoleBase{
	int itemcooldown = 0;
	int regencooldown = 0;
	boolean PouvoirSanginaireNez = false;
	boolean firezone = false;
	boolean nuit = false;
	boolean jour = false;
	Random random = new Random();
	public Nezuko(Player player, Roles roles, GameState gameState) {
		super(player, roles, gameState);
		for (String desc : AllDesc.Nezuko) owner.sendMessage(desc);
		regencooldown = 20;
		this.setForce(20);
		this.setResi(20);
		gameState.lunesup.add(owner);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			for (Player p : getIGPlayers()) {
				if (getPlayerRoles(p).type == Roles.Tanjiro) {
					owner.sendMessage("La personne possédant le rôle de §aTanjiro§r est:§a "+p.getName());
				}
			}
		}, 20);
	}
	@Override
	public String[] Desc() {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			for (Player p : getIGPlayers()) {
				if (getPlayerRoles(p).type == Roles.Tanjiro) {
					owner.sendMessage("La personne possédant le rôle de §aTanjiro§r est:§a "+p.getName());
				}
			}
		}, 20);
		return AllDesc.Nezuko;
	}
	@Override
	public void resetCooldown() {
		itemcooldown = 0;
	}
	@Override
	public void Update(GameState gameState) {
		if (itemcooldown >= 1) {
			itemcooldown--;
		}
		if (itemcooldown == 60*5) {
			PouvoirSanginaireNez = false;
			owner.sendMessage(ChatColor.GREEN+"Désactivation de votre pouvoir sanginaire");
		}
		if (gameState.nightTime) {owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*3, 0, false, false));}
		if ((owner.getHealth() != getMaxHealth())) {
			if (gameState.nightTime) {
				if (regencooldown >= 1){
					regencooldown--;
				}
				if (regencooldown == 0) {
					regencooldown = 20;
					if (owner.getHealth() != this.getMaxHealth()) {
						if (owner.getHealth() <= this.getMaxHealth() - 0.5) {
							owner.setHealth(owner.getHealth() + 0.5);
						} else {
							owner.setHealth(this.getMaxHealth());
						}			
					}
					
				}
			}			
		}
		
		if (gameState.nightTime) {
			nuit = true;
			jour = false;
		} else {
			nuit = false;
			jour = true;
		}
		if (firezone) {
			new DoubleCircleEffect(20*3, EnumParticle.REDSTONE).start(owner);
			new DoubleCircleEffect(20*3, EnumParticle.FLAME).start(owner);
			}
		if (itemcooldown <= 60*10-20) {
			firezone = false;
		}
		for (RoleBase r : gameState.getPlayerRoles().values()) {
			if (!gameState.getInGamePlayers().contains(r.owner)) continue;
			if (r.type == Roles.Tanjiro && r.owner.getWorld().equals(owner.getWorld())) {
				if (r.owner.getLocation().distance(owner.getLocation()) <= 30)
					owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*2, 0, false, false));
			}
		}
		if (owner.getItemInHand().isSimilar(Items.getPouvoirSanginaire())) {
			sendActionBarCooldown(owner, itemcooldown);
		}
		super.Update(gameState);
		}	
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
	public void ItemUseAgainst(ItemStack item, Player victim, GameState gameState) {
		if (victim != owner) {
			if (item != null) {
				if (firezone) {
					if (victim != null) {
						victim.setFireTicks(20*5);
						owner.sendMessage("Vous avez mis en§6 feu§r le joueur:§a "+victim.getName());
					}
				}
			}
		}
		super.ItemUseAgainst(item, victim, gameState);
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(Items.getPouvoirSanginaire())) {
			if (itemcooldown <= 0) {
				firezone = true;
				itemcooldown = 60*10;
				owner.removePotionEffect(PotionEffectType.REGENERATION);
				owner.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*5, 4, false, false));
			} else {
				sendCooldown(owner, itemcooldown);
			}
		}
		return super.ItemUse(item, gameState);
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (victim == owner) {victim.getInventory().remove(Items.getPouvoirSanginaire());}
		super.PlayerKilled(killer, victim, gameState);
	}
}