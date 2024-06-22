package fr.nicknqck.roles.ns.akatsuki;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.AkatsukiRoles;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class Hidan extends AkatsukiRoles {

	public Hidan(Player player) {
		super(player);
		setChakraType(getRandomChakras());
		owner.sendMessage(Desc());
	}
	@Override
	public Roles getRoles() {
		return Roles.Hidan;
	}
	@Override
	public Intelligence getIntelligence() {
		return Intelligence.PEUINTELLIGENT;
	}

	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}
	@Override
	public String[] Desc() {
		KnowRole(owner, Roles.Kakuzu, 15);
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§cHidan",
				AllDesc.objectifteam+"§cAkatsuki",
				"",
				AllDesc.items,
				"",
				AllDesc.point+"§cFaux Tri-Lame§f: Épée en diamant Tranchant IV",
				"",
				AllDesc.point+"§cRituel de Jashin§f: Après avoir§c frapper§f un seul et même joueur§c 10 fois§f avec la§c Faux Tri-Lame§f vous pourrez créer une zone de§c 3x3§f qui, lorsque vous êtes§c frapper à l'intérieur de celui-ci, la victime subira les§c dégâts§f à votre place",
				"",
				AllDesc.particularite,
				"",
				AllDesc.chakra+getChakras().getShowedName(),
				AllDesc.bar
		};
	}
	private ItemStack FauxItem() {
		return new ItemBuilder(Material.DIAMOND_SWORD).setName("§cFaux Tri-Lame").setUnbreakable(true).addEnchant(Enchantment.DAMAGE_ALL, 4).setLore("§7Juste une§c Faux§7 permettant d'effectuer le§c Rituel de Jashin§7.").toItemStack();
	}
	private ItemStack RituelItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§cRituel de Jashin").setLore("§7Vous permet de déclancher le§c Rituel de Jashin§7.").toItemStack();
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				FauxItem(),
				RituelItem()
		};
	}
	@Override
	public void resetCooldown() {
		Fautiste = null;
		canFautiser = false;
		FauxTaped.clear();
		cdJashin = 0;
	}
	private final HashMap<Player, Integer> FauxTaped = new HashMap<>();
	private Player Fautiste = null;
	private boolean canFautiser = false;
	@Override
	public void Update(GameState gameState) {
		if (cdJashin >=0) {
			cdJashin--;
			if (cdJashin == 0) {
				owner.sendMessage("§7Vous pouvez à nouveau faire un§c rituel");
			}
		}
	}
	@Override
	public void onALLPlayerDamage(EntityDamageEvent e, Player victim) {
		if (e.getEntity().getUniqueId().equals(owner.getUniqueId())) {
			if (jLoc != null && Fautiste != null) {
				if (owner.getLocation().distance(jLoc) <= 2.0) {
					if (Fautiste.isOnline()) {
						damage(Fautiste, e.getDamage()/6, 1, owner, true);
						owner.sendMessage("§c"+Fautiste.getDisplayName()+"§7 à subit les effets du§c Rituel de Jashin");
						e.setDamage(0.0);
						owner.setVelocity(new Vector(0f, 0f, 0f));
					}
				}
			}	
		}
	}
	@Override
	public void onALLPlayerDamageByEntityAfterPatch(EntityDamageByEntityEvent event, Player victim, Player damager) {
		if (damager.getUniqueId().equals(owner.getUniqueId())) {
			if (owner.getItemInHand().isSimilar(FauxItem())) {
				if (cdJashin > 0) {
					return;
				}
				if (FauxTaped.containsKey(victim)) {
					int i = FauxTaped.get(victim);
					if (i >= 9 && !canFautiser) {
						canFautiser = true;
						Fautiste = victim;
						owner.sendMessage("§c"+victim.getDisplayName()+"§7 peut maintenant subir le§c Rituel de Jashin");
                    } else {
						i++;
						FauxTaped.clear();
						FauxTaped.put(victim, i);
						sendCustomActionBar(owner, "§7Coups contre "+victim.getDisplayName()+": "+i+"/§c10");
                    }
				} else {
					if (jLoc == null) {
						if (!FauxTaped.isEmpty()) {
							owner.sendMessage("§7Fin du§c Rituel§7 contre "+FauxTaped.keySet().stream().findFirst().get().getDisplayName());
						}
						FauxTaped.clear();
						Fautiste = null;
						canFautiser = false;
						FauxTaped.put(victim, 1);
						owner.sendMessage("§7Début du§c Rituel§7 contre§c "+victim.getDisplayName());
                    }
				}
			}
		}
	}
	private int cdJashin = 0;
	private Location jLoc = null;
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(RituelItem())) {
			if (cdJashin >=1) {
				sendCooldown(owner, cdJashin);
				return true;
			}
			if (canFautiser && Fautiste != null) {
					this.jLoc = owner.getLocation().clone();
					cdJashin = 190;
					owner.sendMessage("§7Démarrage du véritable§c Rituel de Jashin");
					new BukkitRunnable() {
						int i = 200;
						@Override
						public void run() {
							if (!getIGPlayers().contains(owner)) {
								cancel();
							}
							if (i == 0) {
								owner.sendMessage("§7Le§c Rituel de Jashin§7 est terminé.");
								jLoc = null;
								canFautiser = false;
								Fautiste = null;
								cancel();
								return;
							}
							MathUtil.sendCircleParticle(EnumParticle.REDSTONE, jLoc, 2, 16);
							i--;
						}
					}.runTaskTimer(Main.getInstance(), 0, 1);
            } else {
				owner.sendMessage("§7Vous n'avez pas asser de§c sang§7 pour faire un§c rituel§7.");
				if (!canFautiser) {
					owner.sendMessage("§7Vous n'avez pas l'autorisation de Fautiser");
				}
				if (Fautiste == null) {
					owner.sendMessage("§7Aucun cible trouver");
				}
            }
            return true;
        }
		return super.ItemUse(item, gameState);
	}

	@Override
	public String getName() {
		return "§cHidan";
	}
}