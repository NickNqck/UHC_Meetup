package fr.nicknqck.roles.ds.solos;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.DemonsSlayersRoles;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.betteritem.BetterItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class KyogaiV2 extends DemonsSlayersRoles implements Listener {

	public KyogaiV2(UUID player) {
		super(player);
	}

	@Override
	public Soufle getSoufle() {
		return Soufle.AUCUN;
	}

	@Override
	public Roles getRoles() {
		return Roles.KyogaiV2;
	}
	@Override
	public void RoleGiven(GameState gameState) {
		giveHealedHeartatInt(owner, 3);
		owner.getInventory().addItem(getItems());
		new onTick(this).runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
		this.givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.NIGHT);
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
	}
	@Override
	public TeamList getOriginTeam() {
		return TeamList.Solo;
	}
	@Override
	public String[] Desc() {
		return new String[] {
				AllDesc.bar,
				AllDesc.role+getName(),
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
				AllDesc.point+"si vous tappez un joueur qui regarde dans la même direction que vous vous aurez alors 15% de chance de lui infliger 1"+AllDesc.coeur+" de dégat supplémentaire",
				"",
				AllDesc.point+"Si vous êtes frappez par un joueur qui regarde dans la même direction que vous, vous aurez §c20%§f de§c chance§f d'obtenir§c 30 secondes§f de "+AllDesc.Speed,
				"",
				AllDesc.bar
		};
	}

	@Override
	public String getName() {
		return "Kyogai§7 (§6V2§7)";
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
	private ItemStack Tambour() {
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
		if (cooldownShoulder == 0)owner.sendMessage("§cTambour§7 model§l Back§7 est à nouveau utilisable !");
		if (cooldownLeg >= 0)cooldownLeg-=1;
		if (cooldownLeg == 0)owner.sendMessage("§cTambour§7 model§l Leg§7 est à nouveau utilisable !");
		if (cooldownHead >= 0)cooldownHead-=1;
		if (cooldownHead == 0)owner.sendMessage("§cTambour§7 model§l Head§7 est à nouveau utilisable !");
		if (cooldownTP >= 0)cooldownTP--;
		if (cooldownTP == 0)owner.sendMessage("§cTambour§7 model§l TP§7 est à nouveau utilisable !");
	}
	@EventHandler
	private void onUHCBattle(UHCPlayerBattleEvent event) {
		if (!event.isPatch())return;
		if (event.getDamager().getUuid().equals(getPlayer())) {//donc le owner de KyogaiV2 est le mec qui tape
			Player owner = Bukkit.getPlayer(event.getDamager().getUuid());
			Player victim = Bukkit.getPlayer(event.getVictim().getUuid());
			if (owner != null && victim != null) {//Les deux joueurs sont encore connecter
				if (Loc.getPlayerFacing(owner).equals(Loc.getPlayerFacing(victim))) {//si ils regardent la même direction
					if (RandomUtils.getRandomProbability(15)){
						Heal(victim, -2);
						owner.sendMessage("§7§l"+victim.getName()+"§7 à subit 1"+AllDesc.coeur+"§7 de dégat supplémentaire");
						owner.sendMessage("§eKyogai§7 vous à fait subir 1"+AllDesc.coeur+"§7 de dégat supplémentaire");
					}
				}
			}
		} else if (event.getVictim().getUuid().equals(getPlayer())) {
			Player owner = Bukkit.getPlayer(event.getVictim().getUuid());
			Player damager = Bukkit.getPlayer(event.getDamager().getUuid());
			if (owner != null && damager != null) {//Les deux joueurs sont encore connecter
				if (Loc.getPlayerFacing(owner).equals(Loc.getPlayerFacing(damager))) {//si ils regardent la même direction
					if (RandomUtils.getRandomProbability(20)) {//donc 20% de chance de donner speed 1 a kyogai
						givePotionEffet(owner, PotionEffectType.SPEED, 20*30, 1, true);
						owner.sendMessage("§7Vous avez gagné l'effet "+AllDesc.Speed+"§b 1§7 suite à là douleurs");
					}
				}
			}
		}
	}
	@EventHandler
	private void onEndGame(EndGameEvent event) {
		HandlerList.unregisterAll(this);
	}
	private static class onTick extends BukkitRunnable {
		private final KyogaiV2 kyogaiV2;
		private onTick(KyogaiV2 kyogai) {
			this.kyogaiV2 = kyogai;
		}
		@Override
		public void run() {
			if (!kyogaiV2.getGameState().getServerState().equals(GameState.ServerStates.InGame) || !kyogaiV2.getGamePlayer().isAlive()) {
				cancel();
				return;
			}
			Player owner = Bukkit.getPlayer(kyogaiV2.getPlayer());
			if (owner != null) {
				if (owner.getItemInHand().isSimilar(kyogaiV2.Tambour())) {
					switch (kyogaiV2.model) {
						case Head:
							kyogaiV2.sendCustomActionBar(owner, "§7(§c"+kyogaiV2.model.name()+"§7)"+kyogaiV2.getItemNameInHand(owner)+" "+ StringUtils.secondsTowardsBeautiful(kyogaiV2.cooldownHead));
							break;
						case Leg:
							kyogaiV2.sendCustomActionBar(owner, "§7(§c"+kyogaiV2.model.name()+"§7)"+kyogaiV2.getItemNameInHand(owner)+" "+StringUtils.secondsTowardsBeautiful(kyogaiV2.cooldownLeg));
							break;
						case Back:
							kyogaiV2.sendCustomActionBar(owner, "§7(§c"+kyogaiV2.model.name()+"§7)"+kyogaiV2.getItemNameInHand(owner)+" "+StringUtils.secondsTowardsBeautiful(kyogaiV2.cooldownShoulder));
							break;
						default:
							break;
					}
				}
			}
		}
	}
}