package fr.nicknqck.roles.aot.solo;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.aot.builders.TitansRoles;
import fr.nicknqck.roles.aot.titanrouge.Sieg;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.betteritem.BetterItem;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class TitanUltime extends TitansRoles {

	public TitanUltime(UUID player) {
		super(player);
	}
	@Override
	public @NonNull Roles getRoles() {
		return Roles.TitanUltime;
	}

	@Override
	public @NonNull TeamList getOriginTeam() {
		return TeamList.Solo;
	}

	@Override
	public String[] Desc() {
		Main.getInstance().getGetterList().getTitanRougeList(owner);
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"Titan Ultime",
				"",
				AllDesc.point+"Vous êtes un traître chez les titans",
				"",
				AllDesc.items,
				"",
				AllDesc.point+"Vous possèdez une plume enchantée (transformation) qui à son activation vous donnera l'effet "+AllDesc.Speed+" 1 ou "+AllDesc.Resi+" 1",
				"",
				AllDesc.point+"Si vous venez à tuer un Titan vous obtiendrez votre transformation ultime, qui à son activation vous octroie les effets "+AllDesc.Speed+" 3,"+AllDesc.Force+" 1 ansi que 3 "+AllDesc.coeur+" supplémentaire",
				"",
				AllDesc.point+"Vous pourrez utiliser votre transformation Ultime uniquement si vous avez utilisez votre transformation au par avant",
				"",
				AllDesc.bar
		};
	}
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(getItems());
	}
	boolean killtitan = false;
	@Override
	public ItemStack[] getItems() {
		if (!killtitan) {
		return new ItemStack[] {
				BetterItem.of(new ItemBuilder(Material.FEATHER).setName("§6§lTransformation").setLore("§fTransformation en Titan (§cAttention cette transformation est§l PERMANENTE§f)","§7 "+StringID).addEnchant(Enchantment.ARROW_DAMAGE,1).hideAllAttributes().toItemStack(),event  -> {
					int rint = RandomUtils.getRandomInt(0, 1);
					if (rint == 0) {
						OLDgivePotionEffet(owner, PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, true);
						isTransformedinTitan = true;
						setResi(20);
						owner.getInventory().remove(owner.getItemInHand());
						TransfoEclairxMessage(owner);
					} else {
						if (rint == 1) {
							OLDgivePotionEffet(owner, PotionEffectType.SPEED, Integer.MAX_VALUE, 1, true);
							isTransformedinTitan = true;
							owner.getInventory().remove(owner.getItemInHand());
							TransfoEclairxMessage(owner);
						}
					}
					return true;
				}).setDespawnable(false).setDroppable(false).getItemStack(),
				
		};
		} else {
				return new ItemStack[] {
				BetterItem.of(new ItemBuilder(Material.NETHER_STAR).setName("Transformation Ultime").setLore("Vous transforme en titan ultime","§7 "+StringID).toItemStack(), event ->{
					owner.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
					OLDgivePotionEffet(owner, PotionEffectType.SPEED, Integer.MAX_VALUE, 3, true);
					OLDgivePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, true);
					giveHealedHeartatInt(owner, 3);
					owner.getInventory().remove(owner.getItemInHand());
					GameListener.SendToEveryone("");
					GameListener.SendToEveryone("§6§lLE TITAN ULTIME VIENS DE CE TRANSFORMER");
					GameListener.SendToEveryone("");
					owner.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.playSound(p.getLocation(), "aotmtp.ultimetransfo", 10, 20);
					}
					return true;
				}).setDespawnable(false).setDroppable(false).getItemStack(),
				};
		}
	}
	private boolean invvide = false;

	@Override
	public String getName() {
		return "Titan Ultime";
	}

	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (killer == owner) {
			if (victim != null) {
				if (killtitan)return;
				GamePlayer gamePlayer = gameState.getGamePlayer().get(victim.getUniqueId());
				if (gamePlayer.getRole().getOriginTeam() == TeamList.Titan) {
					killtitan = true;
					if (countEmptySlots(owner) > 0 ) {
						owner.getInventory().addItem(getItems());
						owner.sendMessage("Vous avez obtenue votre§6§l Transformation Ultime");
					}else {
						owner.sendMessage("Votre inventaire est remplie vous devez donc faire§6§l /aot claim§f pour obtenir votre§6§l Transformation Ultime");
						invvide = true;
					}
				}
			}
		}
		super.PlayerKilled(killer, victim, gameState);
	}
	@Override
	public void onAotCommands(String arg, String[] args, GameState gameState) {
		if (args[0].equalsIgnoreCase("claim")) {
			if (invvide && killtitan) {
				if (countEmptySlots(owner) > 0 ) {
					owner.getInventory().addItem(getItems());
				}else {
					owner.sendMessage("Votre inventaire est remplie veuiller le vidée pour obtenir la§6§l Transformation Ultime");
				}
			}
		}
	}
	@Override
	public void Update(GameState gameState) {
		if (!killtitan) {
			OLDgivePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
		}
		super.Update(gameState);
	}

	@Override
	public void RoleGiven(GameState gameState) {
		addKnowedRole(Sieg.class);
		gameState.TitansRouge.add(owner);
		addBonusforce(10.0);
	}
}