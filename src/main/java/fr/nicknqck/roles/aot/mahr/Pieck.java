package fr.nicknqck.roles.aot.mahr;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.aot.builders.MahrRoles;
import fr.nicknqck.roles.aot.builders.titans.Titans;
import fr.nicknqck.roles.desc.AllDesc;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Pieck extends MahrRoles {

	public Pieck(UUID player) {
		super(player);
		gameState.Shifter.add(owner);
		if (owner.getName().equalsIgnoreCase("BoulotPieck")) {
			sendMessageAfterXseconde(owner, "On dirait que tu Boulot ton propre role, y'a de quoi rire LOL (:jadorerire)", 5);
		}
		gameState.GiveRodTridi(owner);
		Titans.Charette.getTitan().getListener().setCharette(owner.getUniqueId());
	}
	@Override
	public Roles getRoles() {
		return Roles.Pieck;
	}
	@Override
	public void RoleGiven(GameState gameState) {
	canShift = true;
		super.RoleGiven(gameState);
	}

	@Override
	public String getName() {
		return "Pieck";
	}

	@Override
	public String[] Desc() {
		Main.getInstance().getGetterList().getMahrList(owner);
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"Pieck",
				"",
				AllDesc.capacite,
				"",
				"Vous obtenez certain effet en fonction de votre nombre de slot remplis dans votre inventaire",
				"",
				AllDesc.point+"1-9, vous donne "+AllDesc.Speed+" 2 ainsi que "+AllDesc.Resi+" 1",
				"",
				AllDesc.point+"10-18, vous donne "+AllDesc.Speed+" 1 ainsi que "+AllDesc.Resi+" 1",
				"",
				AllDesc.point+"19-27, vous donne "+AllDesc.Speed+" 1",
				"",
				AllDesc.point+"28-36, vous donne "+AllDesc.weak+" 1",
				"",
				AllDesc.point+"Vous possédez une régénération naturelle de 1/2"+AllDesc.coeur+" toute les 30s",
				"",
				AllDesc.commande+"/aot pickup - Vous empêche de récupéré tout item étant au sol",
				"",
				AllDesc.bar
		};
	}
	int regencd = 0;
	boolean pickup = false;
	@Override
	public boolean onPickupItem(Item item) {return pickup;}
	@Override
	public void Update(GameState gameState) {//Update s'actualise toute les secondes
		if (regencd > 0) {
			regencd--;
		}else {
			double min = getMaxHealth()-1.0;
			if (owner.getHealth() < min) {
				owner.setHealth(owner.getHealth()+1.0);
			}else {
				owner.setHealth(getMaxHealth());
			}
			regencd = 30;
		}
		super.Update(gameState);
	}
	@Override
	public ItemStack[] getItems() {
		if (Titans.Charette.getTitan().getOwner() != null && Titans.Charette.getTitan().getOwner() == owner.getUniqueId()) {
			return Titans.Charette.getTitan().Items();
		}
		return new ItemStack[0];
	}
	@Override
	public void resetCooldown() {
		regencd = 0;
	}
}