package fr.nicknqck.roles.ns.solo.zabuza_haku;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.utils.AttackUtils;
import fr.nicknqck.utils.CC;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.particles.MathUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;

public class Zabuza extends RoleBase{

	public Zabuza(Player player, Roles roles) {
		super(player, roles);
		giveItem(owner, false, getItems());
		setChakraType(Chakras.SUITON);
		owner.sendMessage(Desc());
	}
	@Override
	public void RoleGiven(GameState gameState) {
		setForce(20);
	}

	@Override
    public String[] Desc() {
        KnowRole(owner, Roles.Haku, 5);
        return new String[] {
                AllDesc.bar,
                AllDesc.role+"§bZabuza",
                AllDesc.objectifteam+"§bZabuza et Haku",
                "",
                AllDesc.effet,
                "",
                AllDesc.point+"§bSpeed I §fet §cForce I §fpermanents.",
                "",
                AllDesc.items,
                "",
                AllDesc.point+KubikiribôchôItem().getItemMeta().getDisplayName()+" §f: Épée en diamant§7 Tranchant IV",
                "",
                AllDesc.point+KirigakureNoJutsuItem().getItemMeta().getDisplayName()+"§f: Vous devenez §ainvisible §fen portant votre armure jusqu'à votre prochain coup (§c5 minutes maximum§f). De plus, lorsque vous êtes §ainvisible§f, vous laissez apparaître des §7particules blanches §fsur vos pas que seuls vous et §bHaku §fpeuvent voir.§7 (1x/5m)",
                "",
                AllDesc.particularite,
                "",
                AllDesc.point+"En frappant un joueur avec §bKubikiribôchô§f, vous aurez §c5% §fde vous§d régenerez§f§c 2"+AllDesc.coeur+".",
                "",
                "Vous connaissez l'§aidentité §fd'§bHaku §fet obtenez l'effet §bSpeed II§f, pendant §c5 minutes§f, à sa §cmort§f.",
                AllDesc.point+"Vous possédez un §cchat privé §favec §bHaku§f. Vous pouvez §acommuniquer §favec ce dernier en ajoutant le préfixe §c!§f devant vos messages.",
                "",
                AllDesc.chakra+getChakras().getShowedName(),
                AllDesc.bar
        };
    }
	@SuppressWarnings("deprecation")
	@Override
	public void onAllPlayerChat(org.bukkit.event.player.PlayerChatEvent e, Player p) {
		if (p.getUniqueId() == owner.getUniqueId()) {
			if (e.getMessage().startsWith("!")) {
				String msg = e.getMessage();
				Player Haku = getPlayerFromRole(Roles.Haku);
				owner.sendMessage(CC.translate("&bZabuza: "+msg.substring(1)));
				if (Haku != null) {
					Haku.sendMessage(CC.translate("&bZabuza: "+msg.substring(1)));
				}
			}
		}
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				KubikiribôchôItem(),
				KirigakureNoJutsuItem()
		};
	}

	@Override
	public void resetCooldown() {
		cdKirigakureNoJutsu = 0;
	}
	
	private ItemStack KubikiribôchôItem () {
		return new ItemBuilder(Material.DIAMOND_SWORD).setName("§bKubikiribôchô").setLore("§7La légéndaire épée de §bZabuza").addEnchant(Enchantment.DAMAGE_ALL, 4).setUnbreakable(true).hideEnchantAttributes().toItemStack();
	}
	private ItemStack KirigakureNoJutsuItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§aInvisibilité").setLore("§7Vous permez de devenir invisible").toItemStack();
	}
	@Override
	public void onALLPlayerDamageByEntity(EntityDamageByEntityEvent event, Player victim, Entity entity) {
		if (entity.getUniqueId() == owner.getUniqueId()) {
			if (getIGPlayers().contains(victim)) {
				if (Invisible) {
					removeInvisibility(timeInv);
				}
				if (owner.getItemInHand().isSimilar(KubikiribôchôItem())) {
						if (RandomUtils.getOwnRandomProbability(5)) {
							Heal(owner, 4.0);
							owner.sendMessage("Vous venez de vous §drégénerez§r de§c 2"+AllDesc.coeur);
						}
				}
			}
		}
	}
	@Override
	public void Update(GameState gameState) {
		givePotionEffet(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false);
		givePotionEffet(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, false);
		if (cdKirigakureNoJutsu >= 0) {
			cdKirigakureNoJutsu--;
			if (cdKirigakureNoJutsu == 0) {
				owner.sendMessage("§7Vous pouvez a nouveau vous mettre invisible");
			}
		}
		if (Invisible) {
			timeInv++;
			sendCustomActionBar(owner, "§bTemp d'invisibilité:§c "+(StringUtils.secondsTowardsBeautiful((60*5)-timeInv)));
			if (timeInv == 60*5) {
				removeInvisibility(timeInv);
			}
		}
	}
	@Override
	public void onTick() {
		if (Invisible) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (getListPlayerFromRole(Roles.Haku).contains(p) || getListPlayerFromRole(Roles.Zabuza).contains(p)) {
					MathUtil.sendParticleTo(p, EnumParticle.CLOUD, owner.getLocation().clone());
				}
			}
		}
	}
	private boolean HakuDeath = false;
	@Override
	public void OnAPlayerDie(Player player, GameState gameState, Entity killer) {
		if (getPlayerRoles(player).type == Roles.Haku && !HakuDeath) {
			onHakuDeath(true);
		}
	}
	private void onHakuDeath(boolean msg) {
		HakuDeath = true;
		if (msg) {
			owner.sendMessage("§bHaku§7 est mort, pour vous vengez vous obtenez§c 10 minutes§f de§e Speed 2");
		}
		givePotionEffet(PotionEffectType.SPEED, 20*60*10, 2, true);
	}
	private int cdKirigakureNoJutsu = 0;
	private boolean Invisible = false;
	private HashMap<Integer, ItemStack> armorContents = new HashMap<>();
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(KirigakureNoJutsuItem())) {
			if (cdKirigakureNoJutsu <= 0) {
				if (!Invisible) {
					givePotionEffet(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true);
					if (owner.getInventory().getHelmet() != null) {
						armorContents.put(1, owner.getInventory().getHelmet());
						owner.getInventory().setHelmet(null);
					}
					if (owner.getInventory().getChestplate() != null) {
						armorContents.put(2, owner.getInventory().getChestplate());
						owner.getInventory().setChestplate(null);
					}
					if (owner.getInventory().getLeggings() != null) {
						armorContents.put(3, owner.getInventory().getLeggings());
						owner.getInventory().setLeggings(null);
					}
					if (owner.getInventory().getBoots() != null) {
						armorContents.put(4, owner.getInventory().getBoots());
						owner.getInventory().setBoots(null);
					}
					Invisible = true;
					owner.sendMessage("§aVous êtes maintenant invisible.");
					AttackUtils.CantAttack.add(owner.getUniqueId());
					AttackUtils.CantReceveAttack.add(owner.getUniqueId());
					return true;
				} else {
					removeInvisibility(timeInv);
					return true;
				}
			} else {
				sendCooldown(owner, cdKirigakureNoJutsu);
				return true;
			}
		}
		return super.ItemUse(item, gameState);
	}
	private int timeInv = 0;
	private void removeInvisibility(int invitime) {
		Invisible = false;
		AttackUtils.CantAttack.remove(owner.getUniqueId());
		AttackUtils.CantReceveAttack.remove(owner.getUniqueId());
		owner.sendMessage("§cVous n'êtes plus invisible.");
		owner.removePotionEffect(PotionEffectType.INVISIBILITY);
		cdKirigakureNoJutsu = 60*5;
		timeInv = 0;
		if (armorContents.get(1) != null) {
			owner.getInventory().setHelmet(armorContents.get(1));
		}
		if (armorContents.get(2) != null) {
			owner.getInventory().setChestplate(armorContents.get(2));
		}
		if (armorContents.get(3) != null) {
			owner.getInventory().setLeggings(armorContents.get(3));
		}
		if (armorContents.get(4) != null) {
			owner.getInventory().setBoots(armorContents.get(4));
		}
	}
}