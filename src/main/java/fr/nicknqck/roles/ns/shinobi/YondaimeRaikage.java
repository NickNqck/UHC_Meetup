package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.StringUtils;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class YondaimeRaikage extends ShinobiRoles {

	public YondaimeRaikage(UUID player) {
		super(player);
		setChakraType(Chakras.RAITON);
	}

	@Override
	public @NonNull Intelligence getIntelligence() {
		return Intelligence.INTELLIGENT;
	}

	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}
	@Override
	public @NonNull Roles getRoles() {
		return Roles.Raikage;
	}
	@Override
	public String[] Desc() {
		KnowRole(owner, Roles.KillerBee, 9);
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§aYondaime Raikage",
				AllDesc.objectifteam+"§aShinobi",
				"",
				AllDesc.effet,
				"",
				AllDesc.point+"§eSpeed I§f permanent",
				"",
				AllDesc.items,
				"",
				AllDesc.point+"§eArmure Raiton§f: Vous possédez une barre de temp de§c 5 minutes§f qui est§a activable§f/§cdésactivable§f vous donnant les effets§e Speed II§f et§6 Résistance I§florsqu'elle est active, vous pouvez rechargez votre barre de temp en§c tuant§f un joueur (§a+1min§f).",
				"",
				AllDesc.particularite,
				"",
				"Vous possédez l'identité de§a Killer Bee",
				AllDesc.chakra+getChakras().getShowedName(),
				AllDesc.bar
				
		};
	}
	private ItemStack ArmureItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§eArmure Raiton").setLore("§7Vous permet d'obtenir l'effet Résistance 1 et Speed 2").toItemStack();
	}
	private int timeLeft = 60*5;
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				ArmureItem()
		};
	}
	@Override
	public void Update(GameState gameState) {
		OLDgivePotionEffet(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false);
		if (owner.getItemInHand().isSimilar(ArmureItem())) {
			sendCustomActionBar(owner, "§eArmure Raiton:§c "+StringUtils.secondsTowardsBeautifulinScoreboard(timeLeft));
		}
	}
	@Override
	public void resetCooldown() {
		timeLeft = 60*5;
		armureActived = false;
	}
	private boolean armureActived = false;
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (killer.getUniqueId() != owner.getUniqueId())return;
		timeLeft+=60;
		owner.sendMessage("§7Vous avez gagnez§c 60s§7 d'utilisation de votre§e Armure Raiton§7, il vous reste maintenant§c "+StringUtils.secondsTowardsBeautifulinScoreboard(timeLeft)+"§7 de temp d'utilisation");
    }
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(ArmureItem())) {
			if (timeLeft > 0) {
				if (!armureActived) {
					armureActived = true;
					owner.sendMessage("§7Activation de votre§e Armure Raiton");
					new BukkitRunnable() {
						@Override
						public void run() {
							if (timeLeft <=0 || !armureActived || !gameState.getInGamePlayers().contains(owner.getUniqueId())) {
								if (timeLeft <= 0) {
									owner.sendMessage("§7Désactivation de votre§e Armure Raiton");
									owner.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
									OLDgivePotionEffet(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, true);
								}
								armureActived = false;
								cancel();
								return;
							}
							timeLeft--;
							sendCustomActionBar(owner, "§eArmure Raiton:§c "+StringUtils.secondsTowardsBeautifulinScoreboard(timeLeft));
							OLDgivePotionEffet(PotionEffectType.SPEED, 60, 2, true);
							OLDgivePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true);
						}
					}.runTaskTimer(Main.getInstance(), 0, 20);
                } else {
					armureActived = false;
					owner.sendMessage("§7Désactivation de votre§e Armure Raiton");
					owner.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
					OLDgivePotionEffet(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, true);
                }
            } else {
				owner.sendMessage("§7Vous n'avez pas asser de chakra pour utiliser cette technique.");
            }
            return true;
        }
		return super.ItemUse(item, gameState);
	}

	@Override
	public String getName() {
		return "Yondaime Raikage";
	}
}