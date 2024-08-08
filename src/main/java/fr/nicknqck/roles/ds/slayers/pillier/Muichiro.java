package fr.nicknqck.roles.ds.slayers.pillier;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.demons.lune.Gyokko;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.particles.MathUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class Muichiro extends PillierRoles {

	public Muichiro(UUID player) {
		super(player);
		this.setCanuseblade(true);
	}
	@Override
	public Roles getRoles() {
		return Roles.Muichiro;
	}
	@Override
	public String[] Desc() {
		return AllDesc.Muichiro;
	}
	
	private boolean killgyokko = false;
	private int souflecooldown = 0;
	private boolean zone = false;
	private int marquecooldown = 0;
	private int dsbrumeuse =0;
	private int dsbrumecd = 0;
	@Override
	public void resetCooldown() {
		marquecooldown = 0;
		dsbrumecd = 0;
		dsbrumeuse = 0;
	}
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(Items.getLamedenichirin());
		owner.getInventory().addItem(Items.getSoufleDeLaBrume());
	}
	@Override
	public ItemStack[] getItems() {
		if (killgyokko) {
			return new ItemStack[] {
				Items.getSoufleDeLaBrume(),
				Items.getSlayerMark()
			};
		}
		return new ItemStack[] {
				Items.getSoufleDeLaBrume()
		};
	}
	@Override
	public void onDSCommandSend(String[] args, GameState gameState) {
		if (args[0].equalsIgnoreCase("brume")) {
			if (args.length == 2) {
				if (args[1] != null) {
					Player target = Bukkit.getPlayer(args[1]);
					if (target != null) {
						if (gameState.getInGamePlayers().contains(target) && gameState.getInGamePlayers().contains(owner)){
							if (!gameState.hasRoleNull(target)) {
										if (dsbrumeuse < 3) {
											if (dsbrumecd <= 0) {
												dsbrumeuse+=1;
												dsbrumecd = 180;
												givePotionEffet(target, PotionEffectType.BLINDNESS, 20*30, 5, true);
												target.sendMessage("Vous avez été touché par la brume de§6 Muichiro");
												owner.sendMessage("Votre brume à bien toucher "+target.getName());
												owner.sendMessage("Il ne vous reste que "+(3-dsbrumeuse)+" utilisation du§6 /ds brume");
											} else {
												sendCooldown(owner, dsbrumecd);
											}
								} else {
									owner.sendMessage("Vous n'avez plus d'utilisation du§6 /ds brume");
									
								}
							}else {
								owner.sendMessage("Vous ne pouvez visée qu'une personne ayant un rôle");
								
							}
						} else {
							owner.sendMessage("Il faut que vous et le joueur visée soyez en jeux");
							
						}
					} else {
						owner.sendMessage("Veuiller cibler un joueur éxistant");
						
					}
				}else {
					owner.sendMessage("Veuiller cibler un joueur éxistant");
				}
			}else {
				owner.sendMessage("Veuiller cibler un joueur éxistant");
			}
		}
	}
	private Location zoneloc = null;
	@Override
	public void Update(GameState gameState) {
		if (owner.getItemInHand().isSimilar(Items.getSoufleDeLaBrume())) {
			sendActionBarCooldown(owner, souflecooldown);
		}
		if (owner.getItemInHand().isSimilar(Items.getSlayerMark())) {
			sendActionBarCooldown(owner, marquecooldown);
		}
		if (gameState.nightTime) {
			givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 20*3, 1, true);
		}
		if (souflecooldown >= 1) {souflecooldown--;}
		if (dsbrumecd >= 1)dsbrumecd--;
		if (dsbrumecd == 0) {
			owner.sendMessage("§7Vous pouvez à nouveau utilisé votre§l /ds brume <joueur>");
			dsbrumecd-=5;
		}
		if (zone) {
			MathUtil.sendCircleParticle(EnumParticle.CLOUD, zoneloc, 15, 50);
			for(Player p : gameState.getInGamePlayers()) {
				if (p != owner && p.getWorld().equals(owner.getWorld())) {
					  if(p.getLocation().distance(zoneloc) <= 15) {
						  givePotionEffet(p, PotionEffectType.BLINDNESS, 20*3, Integer.MAX_VALUE, true);
					    }
					  if (p.getLocation().distance(zoneloc) <= 30) {
					    	p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*3, 0, false, false), true);
					    }
				}	
			}			
		}
		if (souflecooldown == 60*10) {
			zone = false;
			zoneloc = null;
			owner.sendMessage("Votre Zone de Brume c'est arrêter...");
		}
		if (marquecooldown <= 1) {marquecooldown--;}
		super.Update(gameState);
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(Items.getSoufleDeLaBrume())) {
			if (souflecooldown <= 0 ) {
				zone = true;
				owner.sendMessage("Vous venez d'activer votre Zone de Brume pour une durée de: "+ChatColor.GOLD+"15 secondes");
				souflecooldown = 60*10+16;
				zoneloc = owner.getLocation();
				}  else {
				sendCooldown(owner, souflecooldown);
			}
		}
		if (item.isSimilar(Items.getSlayerMark())) {
			if (item.isSimilar(Items.getSlayerMark())) {
				if (killgyokko) {
					if (marquecooldown <= 0) {
					int rint = RandomUtils.getRandomInt(0, 1);
					if (rint == 0) {
						owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*3, 0, false, false));
						owner.sendMessage("Votre Marque des Pourfendeurs vous à donnée "+AllDesc.Speed+" 1 pendant 3 minutes");
						marquecooldown = 60*7;
					} else if (rint == 1) {
						owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60*3, 0, false, false));
						owner.sendMessage("Votre Marque des Pourfendeurs vous à donné "+AllDesc.Resi+" 1 pendant 3 minutes");
						marquecooldown = 60*7;
					}
					if (rint != 0 && rint != 1) {
						owner.sendMessage("Vous n'avez pas réussis à éveiller votre marque, cependant vous pouvez réessayer");
					}
			}  else {
				sendCooldown(owner, marquecooldown);
			}
				}
			}
		}
		return super.ItemUse(item, gameState);
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (victim == owner) {
			victim.getInventory().remove(Items.getLamedenichirin());
			victim.getInventory().remove(Items.getSoufleDeLaBrume());
		}
		if (killer == owner) {
			if (victim != owner) {
				if (gameState.getInGamePlayers().contains(victim)) {
					if (gameState.getPlayerRoles().containsKey(victim)) {
						RoleBase role = gameState.getPlayerRoles().get(victim);
						if (role instanceof Gyokko) {
							killgyokko = true;
							this.addresi(20);
							owner.sendMessage(ChatColor.GRAY+"Vous venez de tuée: "+ victim.getName()+" il possédait le rôle de: "+ChatColor.GOLD+ role.getRoles() +ChatColor.GRAY+" vous obtenez donc la marque des pourfendeurs de démon qui vous donnera aléatoirement résistance 1 ou speed 1 pendant 3 minutes");
						}
					}
				}
			}
		}
		super.PlayerKilled(killer, victim, gameState);
	}

	@Override
	public String getName() {
		return "Muichiro";
	}
}