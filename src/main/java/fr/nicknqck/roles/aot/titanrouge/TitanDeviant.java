package fr.nicknqck.roles.aot.titanrouge;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.aot.builders.TitansRoles;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.Loc;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class TitanDeviant extends TitansRoles {
	public TitanDeviant(Player player) {
		super(player);
		gameState.GiveRodTridi(owner);
	}
	@Override
	public Roles getRoles() {
		return Roles.TitanDeviant;
	}
	@Override
	public String[] Desc() {
		Main.getInstance().getGetterList().getTitanRougeList(owner);
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"Titan Déviant",
				"",
				AllDesc.capacite+"Vous êtes déjà transformé, mais pour possèdez des effets il vous faurdra être à côté de titans",
				"",
				AllDesc.point+"§31 Titan :§r vous posséderez "+AllDesc.Speed+" 1",
				AllDesc.point+"§32 Titans :§r vous posséderez "+AllDesc.Speed+" 1 ainsi que "+AllDesc.Resi+" 1",
				"",
				AllDesc.bar,
		};
	}
	@Override
	public void Update(GameState gameState) {
		for (Player p: Loc.getNearbyPlayers(owner, 20)) {
			if (gameState.TitansRouge.contains(p)) {
				List<Player> isTitanAroundDeviant = new ArrayList<>();
				isTitanAroundDeviant.add(p);
                isTransformedinTitan = true;
                givePotionEffet(owner, PotionEffectType.SPEED, 40, 1, true);

            }
		}
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[0];
	}
	@Override
	public void resetCooldown() {
		
	}

	@Override
	public String getName() {
		return "§cTitan Deviant";
	}
}