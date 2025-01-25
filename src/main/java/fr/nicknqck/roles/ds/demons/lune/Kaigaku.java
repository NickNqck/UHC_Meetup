package fr.nicknqck.roles.ds.demons.lune;

import fr.nicknqck.Main;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.roles.ds.demons.Muzan;
import fr.nicknqck.roles.ds.slayers.ZenItsuV2;
import fr.nicknqck.roles.ds.solos.JigoroV2;
import fr.nicknqck.utils.Loc;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;

import java.util.UUID;

public class Kaigaku extends DemonsRoles {

	private boolean killzen = false;
	private int cooldownquatriememouvement = 0;
	private int cooldowntroisiememouvement = 0;
	public Kaigaku(UUID player) {
		super(player);
		this.setCanuseblade(true);
		getKnowedRoles().add(ZenItsuV2.class);
		getKnowedRoles().add(Muzan.class);
		setLameIncassable(owner, true);
	}

	@Override
	public Soufle getSoufle() {
		return Soufle.FOUDRE;
	}

	@Override
	public @NonNull DemonType getRank() {
		return DemonType.SUPERIEUR;
	}
	@Override
	public @NonNull Roles getRoles() {
		return Roles.Kaigaku;
	}
	@Override
	public void resetCooldown() {
		cooldownquatriememouvement = 0;
		cooldowntroisiememouvement = 0;
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				Items.getSoufleFoudre3iememouvement(),
				Items.getSoufleFoudre4iememouvement()
		};
	}
	@Override
	public TeamList getOriginTeam() {
		return TeamList.Demon;
	}
	@Override
	public String[] Desc() {
		return AllDesc.Kaigaku;
	}
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(Items.getSoufleFoudre3iememouvement());
		owner.getInventory().addItem(Items.getSoufleFoudre4iememouvement());
		owner.getInventory().addItem(Items.getLamedenichirin());
		super.GiveItems();
	}
	@Override
	public String getName() {
		return "Kaigaku";
	}
	@Override
	public void Update(GameState gameState) {
		if (owner.getItemInHand().isSimilar(Items.getSoufleFoudre4iememouvement())) {
			sendActionBarCooldown(owner, cooldownquatriememouvement);
		}
		if (owner.getItemInHand().isSimilar(Items.getSoufleFoudre3iememouvement())) {
			sendActionBarCooldown(owner, cooldowntroisiememouvement);
		}
		if (cooldownquatriememouvement >= 1) {
			cooldownquatriememouvement--;
		}	
		if (cooldowntroisiememouvement >= 1) {
			cooldowntroisiememouvement--;
		}
	}

	@Override
	public void RoleGiven(GameState gameState) {
		givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.NIGHT);
	}

	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (killer == owner) {
			if (victim != owner){
				if (gameState.getInGamePlayers().contains(victim.getUniqueId())) {
					if (!gameState.hasRoleNull(victim.getUniqueId())) {
						final RoleBase role = gameState.getGamePlayer().get(victim.getUniqueId()).getRole();
						if (role instanceof ZenItsuV2) {
							killzen = true;						
							owner.sendMessage(ChatColor.GRAY+"Vous venez de tuez "+ChatColor.GOLD+role.getRoles()+" "+ChatColor.GRAY+"vous obtenez donc sa capacité qui est que quand vous mettez un coup avec votre épée il y à 1 chance sur 10 que la cible ce prenne "+ChatColor.RED+"1 coeur de dégat"+ChatColor.GRAY+" via un éclair");
						
						}
					}
				}
			}
			if (gameState.JigoroV2Pacte2) {
				if (gameState.getInGamePlayers().contains(victim.getUniqueId()) && gameState.getInGamePlayers().contains(killer.getUniqueId())) {
					if (!gameState.hasRoleNull(victim.getUniqueId()) || !gameState.hasRoleNull(killer.getUniqueId())) {
						if (killer == owner) {
							String msg = "Vous avez reçus 1 demi"+AllDesc.coeur+" permanent car§6 Jigoro§r ou§6 Kaigaku à fait un kill";
							owner.sendMessage(msg);
							setMaxHealth(getMaxHealth()+1.0);
							owner.updateInventory();
							for (UUID u : gameState.getInGamePlayers()) {
								Player p = Bukkit.getPlayer(u);
								if (p == null)continue;
								if (!gameState.hasRoleNull(u)) {
									if (gameState.getGamePlayer().get(u).getRole() instanceof JigoroV2) {
										final GamePlayer jigoro = gameState.getGamePlayer().get(u);
										jigoro.sendMessage(msg);
										jigoro.getRole().setMaxHealth(jigoro.getRole().getMaxHealth()+1.0);
									}
								}
							}
						}
					}
				}
			}
		}
		super.PlayerKilled(killer, victim, gameState);
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(Items.getSoufleFoudre3iememouvement())) {
			if (!isPowerEnabled()) {
				owner.sendMessage(ChatColor.RED+"Votre pouvoir est désactivé.");
				return false;
			}
			if (cooldowntroisiememouvement <= 0) {
				cooldowntroisiememouvement = 5*60;
				owner.sendMessage(ChatColor.GREEN+"Exécution du"+ChatColor.GOLD+" Troisème mouvement du soufle de la foudre.");
				for(Player p : Loc.getNearbyPlayersExcept(owner, 30)) {
					if (!gameState.hasRoleNull(p.getUniqueId())) {
						if (p.getHealth() > 4.0) {
							p.setHealth(p.getHealth() - 4.0);
						} else {
							p.setHealth(0.5);
						}
						owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*(60*3), 0, false, false));
						owner.sendMessage("§aVos éclair ont toucher: " + p.getName());
						p.sendMessage("§aVous avez été touchez le Troisième mouvement du soufle de la foudre de:§c Kaigaku");
						gameState.spawnLightningBolt(p.getWorld(), p.getLocation());
					}
				}
				if (killzen) {
					cooldownquatriememouvement-=60;
				}
			}  else {
				sendCooldown(owner, cooldowntroisiememouvement);
			}
		}
		if (item.isSimilar(Items.getSoufleFoudre4iememouvement())) {
			if (!isPowerEnabled()) {
				owner.sendMessage(ChatColor.RED+"Votre pouvoir est désactivé.");
				return false;
			}
			if (cooldownquatriememouvement <= 0) {
				Player target = getTargetPlayer(owner, 25);
					if (target != null) {
						if (owner.canSee(target)) {
							Location loc = target.getLocation();
							if (Main.isDebug()){
								System.out.println(target.getEyeLocation());
							}
							loc.setX(loc.getX()+Math.cos(Math.toRadians(-target.getEyeLocation().getYaw()+90)));
							loc.setZ(loc.getZ()+Math.sin(Math.toRadians(target.getEyeLocation().getYaw()-90)));
							loc.setPitch(0);
							if (Main.isDebug()) {
								System.out.println(loc);
							}
							owner.teleport(loc);
							target.getWorld().strikeLightning(target.getLocation());
							if (target.getHealth() > 4.0) {
								target.setHealth(target.getHealth() - 4.0);
							} else {
								target.setHealth(0.5);
							}
							owner.sendMessage(ChatColor.GREEN+"Exécution du"+ChatColor.GOLD+" Quatrième mouvement du soufle de la foudre");
							cooldownquatriememouvement = 60*3;
							target.sendMessage(ChatColor.WHITE+"Vous avez été touché par un soufle de la foudre");
							owner.teleport(loc);
							if (killzen) {
								cooldownquatriememouvement-=60;
							}
							return true;
						}	
					} else {
						owner.sendMessage("§cIl faut viser un joueur !");
						return true;
					}
				} else {
					sendCooldown(owner, cooldownquatriememouvement);
					return true;
				}
		}
		return super.ItemUse(item, gameState);
	}
}