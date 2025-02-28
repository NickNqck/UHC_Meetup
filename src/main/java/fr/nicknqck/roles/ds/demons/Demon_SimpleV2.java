package fr.nicknqck.roles.ds.demons;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.utils.StringUtils;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class Demon_SimpleV2 extends DemonInferieurRole {

	
	public Demon_SimpleV2(UUID player) {
		super(player);
	}
	@Override
	public TeamList getOriginTeam() {
		return TeamList.Demon;
	}
	@Override
	public @NonNull DemonType getRank() {
		return DemonType.DEMON;
	}

	@Override
	public @NonNull Roles getRoles() {
		return Roles.DemonSimpleV2;
	}
	@Override
	public String[] Desc() {
		return AllDesc.Demon_SimpleV2;
	}

	@Override
	public String getName() {
		return "Demon§7 (§6V2§7)";
	}

	@Override
	public ItemStack[] getItems() {
		return new ItemStack[0];
	}
	  @Override
	public void Update(GameState gameState) {
		  if (gameState.nightTime) {
			  owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*4, 0, false, false), true);
		  } else {
			  if (killnumber == 0) {
				  owner.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*4, 0, false, false), true);
			  }
		  } 
		  if (Transfocooldown >= 1) {Transfocooldown -= 1;}
		super.Update(gameState);
	}
	private int killnumber = 0;
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (killer == owner) {
			if (victim != owner) {
				killnumber+=1; 
				owner.sendMessage("Vous venez d'obtenir un kill suplémentaire vous êtes désormais à: "+ killnumber);
				if (killnumber == 1) {
					owner.removePotionEffect(PotionEffectType.WEAKNESS);
					owner.sendMessage("Vous perdez désormais votre Weakness de jour");
				}
				if (killnumber == 2) {
					addSpeedAtInt(owner, 10); 
					owner.sendMessage("Vous obtenez désormais 10% de speed");
				}
				if (killnumber == 3) {
					addBonusforce(10);
					owner.sendMessage("Vous obtenez désormais 10% de force");
				}
				if (killnumber == 4) {
					addBonusResi(10);
					owner.sendMessage("Vous obtenez désormais 10% de résitance");
				}
				if (killnumber == 6) {
					owner.getInventory().addItem(Items.getTransformation());
					owner.sendMessage("Vous obtenez désormais l'item Transformation");
				}
			}
		}
		super.PlayerKilled(killer, victim, gameState);
	}   
	private int Transfocooldown = 0; 
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(Items.getTransformation()))
			if (Transfocooldown <= 0) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*180, 0, false, false));
				Transfocooldown = 60*10;
				setResi(20);
			} else {
				owner.sendMessage(ChatColor.RED + "Vous ne pourrez utiliser a nouveau votre Tranformation que dans "+ StringUtils.secondsTowardsBeautiful(Transfocooldown));
			}
		return super.ItemUse(item, gameState);
	}
	@Override
	public void resetCooldown() {
	}
}