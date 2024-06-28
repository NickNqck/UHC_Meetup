package fr.nicknqck.roles.ds.solos;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.DemonsSlayersRoles;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.betteritem.BetterItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class KyogaiV2 extends DemonsSlayersRoles {

	public KyogaiV2(Player player) {
		super(player);
		owner.sendMessage(Desc());
		owner.getInventory().addItem(getItems());
		addforce(20);
	}
	@Override
	public Roles getRoles() {
		return Roles.KyogaiV2;
	}
	@Override
	public void RoleGiven(GameState gameState) {
		giveHealedHeartatInt(owner, 3);
	}
	@Override
	public TeamList getOriginTeam() {
		return TeamList.Solo;
	}
	@Override
	public String[] Desc() {
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"Kyogai",
				AllDesc.objectifsolo+"§e Seul",
				"",
				AllDesc.effet,
				"",
				AllDesc.point+AllDesc.Force+"§c 1§f la "+AllDesc.nuit+" ainsi que 13"+AllDesc.coeur+" permanent",
				"",
				AllDesc.items,
				"",
				AllDesc.point+"§6Tambour§f: Si vous êtes accroupie, échange votre place avec le joueur visé, sinon fais quelque chose en fonction du mode de votre§6 Tambour",
				"",
				"§cHead§f: Force le joueur visé à regardé le ciel",
				"§cBack§f: Tourne le joueur à 180°",
				"§cLeg§f: Force le joueur visé à regarder le sol",
				"",
				AllDesc.particularite,
				"",
				AllDesc.point+"si vous tappez un joueur qui regarde dans la même direction que vous vous aurez alors 5% de chance de lui infliger 1"+AllDesc.coeur+" de dégat supplémentaire",
				"",
				"si vous êtes frappez par un joueuer qui regarde dans la même direction que vous vous aurez 20% de chance d'obtenir 30s de "+AllDesc.Speed,
				"",
				AllDesc.bar
		};
	}

	@Override
	public String getName() {
		return "§cKyogai§7 (§6V2§7)";
	}

	public enum Model {
		Back,
		Leg,
		Head
    }
	private Model model = Model.Back;
	private int cooldownShoulder = 0;
	private int cooldownLeg = 0;
	private int cooldownHead = 0;
	private int cooldownTP = 0;
	@Override
	public void resetCooldown() {
		cooldownHead = 0;
		cooldownLeg = 0;
		cooldownShoulder = 0;
		cooldownTP = 0;
	}
	ItemStack Tambour() {
		return BetterItem.of(new ItemBuilder(Material.STICK).addEnchant(Enchantment.ARROW_DAMAGE, 1).setUnbreakable(true).hideAllAttributes().setName("§cTambour").setLore("§7Permet de retourner la personne visée","§7"+StringID).toItemStack(), event -> {
			if (event.isRightClick()) {
				if (owner.isSneaking()) {
					if (cooldownTP <= 0) {
						Player target = event.getRightClicked(50);
						if (target != null) {
							Location tLoc = target.getLocation().clone();
							Location oLoc = owner.getLocation().clone();
							owner.teleport(tLoc);
							target.teleport(oLoc);
							owner.sendMessage("§7Vous échangez votre place avec§l "+target.getName());
							target.sendMessage("§eKyogaiV2§7 à échanger sa place avec vous !");
							cooldownTP = 90;
                        }else {
							owner.sendMessage("§cVeuillez visée quelqu'un");
                        }
                    } else {
						sendCooldown(owner, cooldownTP);
                    }
                    return true;
                }else {
					if (model == Model.Back) {
						if (cooldownShoulder <= 0) {
							Player target = event.getRightClicked(20);
							if (target != null) {
								Loc.inverserDirectionJoueur(target);
								cooldownShoulder = 30;
								owner.sendMessage("Vous avez retourné: "+target.getName());
							}else {
								owner.sendMessage("§cVeuillez visée quelqu'un");
							}
						}else {
							sendCooldown(owner, cooldownShoulder);
						}
						return true;
					} else if (model == Model.Leg){
						if (cooldownLeg <=0) {
							Player target = event.getRightClicked(20);
							if (target != null) {
								target.teleport(new Location(target.getWorld(), target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ(), target.getEyeLocation().getYaw(), 90));
								cooldownLeg = 30;
								owner.sendMessage("§7§l "+target.getName()+"§7 vise maintenant ses pied");
							} else {
								owner.sendMessage("§cVeuillez visée quelqu'un");
							}
						}else {
							sendCooldown(owner, cooldownLeg);
						}
						return true;
					} else if (model == Model.Head) {
						if (cooldownHead <= 0) {
							Player target = event.getRightClicked(20);
							if (target != null) {
								target.teleport(new Location(target.getWorld(), target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ(), target.getEyeLocation().getYaw(), -90));
								cooldownHead = 30;
								owner.sendMessage("§7§l "+target.getName()+"§7 vise maintenant les cieux");
							} else {
								owner.sendMessage("§cVeuillez visée quelqu'un");
							}
						} else {
							sendCooldown(owner, cooldownHead);
						}
						return true;
					}
				}
			}else {
				if (model == Model.Back) {
					model = Model.Leg;
					owner.sendMessage("§7Votre§c Tambour§7 est maintenant en mode§6 "+model.name());
				}else if (model == Model.Leg){
					model = Model.Head;
					owner.sendMessage("§7Votre§c Tambour§7 est maintenant en mode§6 "+model.name());
				} else if (model == Model.Head) {
					model = Model.Back;
					owner.sendMessage("§7Votre§c Tambour§7 est maintenant en mode§6 "+model.name());
				}
				return true;
			}
			return false;
		}).setDespawnable(true).setDroppable(false).setMovableOther(false).getItemStack();
	}
	@Override
	public ItemStack[] getItems() {
	return new ItemStack[] {
			Tambour()
	};
	}
	@Override
	public void Update(GameState gameState) {
		if (cooldownShoulder >=0)cooldownShoulder-=1;
		if (cooldownShoulder == 0)owner.sendMessage("§cTambour§7 model§l Shoulder§7 est à nouveau utilisable !");
		if (cooldownLeg >= 0)cooldownLeg-=1;
		if (cooldownLeg == 0)owner.sendMessage("§cTambour§7 model§l Leg§7 est à nouveau utilisable !");
		if (cooldownHead >= 0)cooldownHead-=1;
		if (cooldownHead == 0)owner.sendMessage("§cTambour§7 model§l Head§7 est à nouveau utilisable !");
		if (gameState.nightTime) {
			givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
		}
		if (cooldownTP >= 0)cooldownTP--;
		if (cooldownTP == 0)owner.sendMessage("§cTambour§7 model§l TP§7 est à nouveau utilisable !");
	}
	@Override
	public void onNight(GameState gameState) {
		addforce(20);
		super.onNight(gameState);
	}
	@Override
	public void onDay(GameState gameState) {
		addforce(-20);
		super.onDay(gameState);
	}
	@Override
	public void onTick() {
		if (owner.getItemInHand().isSimilar(Tambour())) {
			switch (model) {
			case Head:
				sendCustomActionBar(owner, "§7(§c"+model.name()+"§7)"+getItemNameInHand(owner)+" "+cd(cooldownHead));
				break;
			case Leg:
				sendCustomActionBar(owner, "§7(§c"+model.name()+"§7)"+getItemNameInHand(owner)+" "+cd(cooldownLeg));
				break;
			case Back:
				sendCustomActionBar(owner, "§7(§c"+model.name()+"§7)"+getItemNameInHand(owner)+" "+cd(cooldownShoulder));
				break;
			default:
				break;
			}
		}
	}
	@Override
	public void neoItemUseAgainst(ItemStack itemInHand, Player player, GameState gameState, Player damager) {
		if (damager.equals(owner)) {
			if (player.equals(owner))return;
			if (Loc.getCardinalDirection(damager).equals(Loc.getCardinalDirection(player))) {
				if (RandomUtils.getRandomProbability(5)){
					Heal(player, -2);
					damager.sendMessage("§7§l"+player.getName()+"§7 à subit 1"+AllDesc.coeur+"§7 de dégat supplémentaire");
					player.sendMessage("§eKyogai§7 vous à fait subir 1"+AllDesc.coeur+"§7 de dégat supplémentaire");
				}
			}
		}
	}
	@Override
	public void neoAttackedByPlayer(Player attacker, GameState gameState) {
		if (attacker != owner) {
			if (Loc.getCardinalDirection(attacker).equals(Loc.getCardinalDirection(owner))) {
				if (RandomUtils.getRandomProbability(20)) {
					givePotionEffet(owner, PotionEffectType.SPEED, 20*30, 1, true);
					owner.sendMessage("§7Vous avez gagné l'effet "+AllDesc.Speed+"§b 1§7 suivre à là douleurs");
				}
			}
		}
	}
}