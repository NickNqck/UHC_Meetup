package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class KillerBee extends ShinobiRoles {

	public KillerBee(UUID player) {
		super(player);
		setChakraType(Chakras.RAITON);
	}

	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}

	@Override
	public GameState.Roles getRoles() {
		return Roles.KillerBee;
	}
	@Override
	public @NonNull Intelligence getIntelligence() {
		return Intelligence.MOYENNE;
	}

	@Override
	public String[] Desc() {
		KnowRole(owner, Roles.Raikage, 9);
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§aKiller Bee",
				AllDesc.objectifteam+"§aShinobi",
				"",
				AllDesc.effet,
				"",
				AllDesc.point+"§cForce I§f permanent",
				"",
				AllDesc.items,
				"",
				AllDesc.point+"§dGyûki§f: Pendant§c 5 minutes§f vous offre l'effet§e Speed I§f ainsi que§c 2"+AllDesc.coeur+"§f.§7 (1x/10m)",
				"",
				AllDesc.point+"§aTentacule§f: Sous forme de§l cane à pêche§r§f, cette objet vous permet de vous déplacez comme via un §cgrappin§f,§c cette item n'est utilisable que lorsque que§d Gyûki§c est activé§f.§7 (1x/30s)",
				"",
				AllDesc.point+"§eLame Raiton§f: Épée en fer §lTranchant IV§r§f",
				"",
				AllDesc.particularite,
				"",
				"Vous possédez l'identité du§a Yondaime Raikage",
				"Lorsque vous frappez un joueur avec votre§e Lame Raiton§f, vous aurez§c 5%§f de chance d'§cinfliger§f un§e éclair§f faisant§c 1"+AllDesc.coeur+" (en plus de§e Raiton§f).",
				"",
				AllDesc.chakra+getChakras().getShowedName(),
				AllDesc.bar
		};
	}
	private ItemStack GyukiItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§dGyûki").setLore("§7Vous permet d'obtenir la puissance de votre§d Biju").toItemStack();
	}
	private ItemStack TentaculeItem() {
		return new ItemBuilder(Material.FISHING_ROD).setName("§aTentacule").setLore("§7Si vous êtes sous l'utilisation de§d Gyûki§7 vous permet de l'utiliser comme un grappin.").setUnbreakable(true).addEnchant(Enchantment.ARROW_FIRE, 1).hideAllAttributes().toItemStack();
	}
	private ItemStack LameItem() {
		return new ItemBuilder(Material.IRON_SWORD).setName("§eLame Raiton").setLore("§7Juste de puissance lame remplit de§e Raiton").addEnchant(Enchantment.DAMAGE_ALL, 4).setUnbreakable(true).toItemStack();
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				GyukiItem(),
				TentaculeItem(),
				LameItem()
		};	
	}
	private int cdGyuki = 0;
	private int cdTentacule = 0;
	@Override
	public void resetCooldown() {
		cdGyuki = 0;
		cdTentacule = 0;
	}
	@Override
	public void Update(GameState gameState) {
		OLDgivePotionEffet(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, false);
		if (cdGyuki >= 0) {
			cdGyuki--;
			if (cdGyuki == 0) {
				owner.sendMessage("§7Vous pouvez à nouveau utiliser§d Gyûki");
			}
		}
		if (cdTentacule >= 0) {
			cdTentacule--;
			if (cdTentacule == 0) {
				owner.sendMessage("§7Vous pouvez à nouveau utiliser vos§a Tentacules");
			}
		}
	}
	@Override
	public void onALLPlayerDamageByEntityAfterPatch(EntityDamageByEntityEvent event, Player victim, Player damager) {
		if (damager.getUniqueId().equals(owner.getUniqueId())) {
			if (damager.getItemInHand().isSimilar(LameItem())) {
				if (RandomUtils.getOwnRandomProbability(5)) {
					gameState.spawnLightningBolt(victim.getWorld(), victim.getLocation());
					damage(victim, 2.0, 1, owner, true);
				}
			}
		}
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(GyukiItem())) {
			if (cdGyuki > 0) {
				sendCooldown(owner, cdGyuki);
				return true;
			}
			owner.sendMessage("§7Vous êtes maintenant sous l'effet de§d Gyûki§7.");
			OLDgivePotionEffet(PotionEffectType.SPEED, 20*300, 1, true);//5 minutes
			giveHealedHeartatInt(2.0);
			cdGyuki = 60*15;
			new BukkitRunnable() {
				int i = 60*5;
				@Override
				public void run() {
					i--;
					if (i <= 0 || !gameState.getInGamePlayers().contains(owner)) {
						setMaxHealth(getMaxHealth()-4.0);
						owner.sendMessage("§7Vous n'êtes plus sous l'effet de§d Gyûki§7.");
						cancel();
						return;
					}
					sendCustomActionBar(owner, "§bTemp d'utilisation restant: "+StringUtils.secondsTowardsBeautiful(i));
				}
			}.runTaskTimer(Main.getInstance(), 0, 20);
			return true;
		}
		return super.ItemUse(item, gameState);
	}

	public boolean isCanTentacule() {
		if (cdTentacule > 0) {
			sendCooldown(owner, cdTentacule);
			return false;
		}
		if (cdGyuki <= 60*10) {
			owner.sendMessage("§cIl faut être sous l'effet de§d Gyûki§7.");
			return false;
		}
		return true;
	}

	public void onTentaculeEnd(double distanceSquared) {
		cdTentacule = 30;
		owner.sendMessage("§7Pouvoir de la §aTentacule§7.");
	}

	@Override
	public String getName() {
		return "Killer Bee";
	}
}