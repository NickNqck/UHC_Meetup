package fr.nicknqck.roles.ds.demons.lune;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.demons.Muzan;
import fr.nicknqck.utils.RandomUtils;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class Doma extends DemonsRoles {

	public Doma(UUID player) {
		super(player);
		this.setResi(20);
		getKnowedRoles().add(Muzan.class);
	}
	@Override
	public TeamList getOriginTeam() {
		return TeamList.Demon;
	}
	@Override
	public @NonNull DemonType getRank() {
		return DemonType.SUPERIEUR;
	}

	@Override
	public Roles getRoles() {
		return Roles.Doma;
	}
	@Override
	public String[] Desc() {
		return AllDesc.Doma;
	}
	
	private boolean zonedeglace = false;
	private boolean statutdeglace = false;
	private int Zonecooldown = 0;
	private int Statutcooldown = 0;
	private boolean killshinobu = false;
	@Override
	public void resetCooldown() {
		Zonecooldown = 0;
		Statutcooldown = 0;
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				Items.getDomaEpouventaille(),
				Items.getDomaStatutdeGlace(),
				Items.getDomaZonedeGlace()
		};
	}
	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}
	@Override
	public void Update(GameState gameState) {
		if (owner.getItemInHand().isSimilar(Items.getDomaZonedeGlace())) {
			sendActionBarCooldown(owner, Zonecooldown);
		}
		if (owner.getItemInHand().isSimilar(Items.getDomaStatutdeGlace())) {
			sendActionBarCooldown(owner, Statutcooldown);
		}
		if (Zonecooldown >= 1) {Zonecooldown--;}
		if (Statutcooldown >= 1) {Statutcooldown--;}
		if (gameState.nightTime) {
			owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*3, 0, false, false), true);
		}
		if (!gameState.nightTime && killshinobu) {
			owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*3, 0, false, false), true);
		}
		if (zonedeglace) {
		for (UUID u : gameState.getInGamePlayers()) {
			Player p = Bukkit.getPlayer(u);
			if (p == null)continue;
			if (p != owner && p.getWorld().equals(owner.getWorld())) {
				if(p.getLocation().distance(owner.getLocation()) <= 5) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*3, 2, false, false), true);
					
				}
				if(p.getLocation().distance(owner.getLocation()) <= 30) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*3, 0, false, false), true);
				}
			}
		}
	}
		if (Zonecooldown == 60*10) {
			zonedeglace = false;
			owner.sendMessage("Votre Pouvoir Sanginaire c'est arrêté");
		}
		super.Update(gameState);
	}

	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(Items.getDomaZonedeGlace())) {
			if (Zonecooldown <= 0) {
				owner.sendMessage(ChatColor.WHITE+"Activation de votre Pouvoir Sanginaire");
				Zonecooldown = 60*10+25;
				zonedeglace = true;
			}   else {
				sendCooldown(owner, Zonecooldown);
			}
		}
		if (item.isSimilar(Items.getDomaStatutdeGlace())) {
			if (Statutcooldown <= 0) {
				owner.sendMessage(ChatColor.WHITE+"Activation de votre Pouvoir Sanginaire");
				statutdeglace = true;
				Statutcooldown = 60*10;
			}   else {
				sendCooldown(owner, Statutcooldown);
			}
		}
		return super.ItemUse(item, gameState);
	}

	@Override
	public String getName() {
		return "Doma";
	}

	@Override
	public void ItemUseAgainst(ItemStack item, Player victim, GameState gameState) {
		if (item.isSimilar(Items.getDomaEpouventaille()) && statutdeglace) {
			int rint = RandomUtils.getRandomInt(0, 5);
			if (rint == 0) {
				if (owner.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
					owner.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
					owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*10, 0, false, false), true);
				} else {
					owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*10, 0, false, false), true);
				}
			}
			
		}
		super.ItemUseAgainst(item, victim, gameState);
	}
	
}