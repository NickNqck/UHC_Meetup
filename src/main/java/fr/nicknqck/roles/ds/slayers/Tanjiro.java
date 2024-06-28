package fr.nicknqck.roles.ds.slayers;

import java.text.DecimalFormat;

import fr.nicknqck.roles.ds.builders.SlayerRoles;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.WorldUtils;
import fr.nicknqck.utils.betteritem.BetterItem;
import fr.nicknqck.utils.particles.MathUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;

public class Tanjiro extends SlayerRoles {
int itemcooldown = 0;
boolean killassa = false;
boolean dance = false;
	public Tanjiro(Player player) {
		super(player);
		for (String desc : AllDesc.Tanjiro) owner.sendMessage(desc);
		this.setCanuseblade(true);
		this.setLameFr(true);
	}

    @Override
	public Roles getRoles() {
		return Roles.Tanjiro;
	}
	public int actualuse = 0;
	@Override
		public void RoleGiven(GameState gameState) {
			gameState.aroundTanjiro.clear();
			super.RoleGiven(gameState);
		}
	@Override
		public String[] Desc() {return AllDesc.Tanjiro;}
	@Override
	public void GiveItems() {
			owner.getInventory().addItem(Items.getDSTanjiroDance());
			owner.getInventory().addItem(Items.getLamedenichirin());
		}
	@Override
	public void Update(GameState gameState) {
		if (!gameState.demonKingTanjiro) {
			if (killassa) {
				givePotionEffet(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, false);
			}
			if (!gameState.nightTime) {
				givePotionEffet(owner, PotionEffectType.SPEED, 80, 1, true);
			}
			if (itemcooldown >= 1) {
				itemcooldown--;
			}
			if(itemcooldown == 60*7) {
				dance = false;
				owner.sendMessage("Vous ne mettez plus en feu les joueurs que vous tapez");
			}
			if (itemcooldown >= 60*5) {
				MathUtil.sendCircleParticle(EnumParticle.FLAME, owner.getLocation(), 1, 30);
				int newcd = itemcooldown-(60*5);
				sendCustomActionBar(owner, aqua+"Temp restant de§6§l Dance du dieu du Feu§r: "+cd(newcd));
			}
			if (itemcooldown == 60*5) {
				owner.sendMessage("Votre "+ChatColor.GOLD+"Dance du Dieu du Feu"+ChatColor.WHITE+" a été désactiver");
				if (!killassa) {
					setMaxHealth(owner.getMaxHealth() - 4.0);
					owner.sendMessage("Vous subissez les contre coup de votre "+ChatColor.GOLD+"Dance du Dieu du Feu "+ChatColor.WHITE+"vous vennez de perdre "+ChatColor.RED+"2"+AllDesc.coeur+"permanent");
				}else {
					setMaxHealth(owner.getMaxHealth() - 2.0);
					owner.sendMessage("Vous subissez les contre coup de votre "+ChatColor.GOLD+"Dance du Dieu du Feu "+ChatColor.WHITE+"vous vennez de perdre "+ChatColor.RED+"1"+AllDesc.coeur+" permanent");
				}
			}
			for (Player p : gameState.getInGamePlayers()) {
				if (gameState.getPlayerRoles().containsKey(p)) {
					if (getPlayerRoles(p) instanceof Nezuko || getPlayerRoles(p).getOldTeam() == TeamList.Demon) {
						if (p.getWorld().equals(owner.getWorld())) {
							if (p.getLocation().distance(owner.getLocation()) <= 30) {
								if (!gameState.aroundTanjiro.contains(p)) {
									gameState.aroundTanjiro.add(p);
								}
							} else {
                                gameState.aroundTanjiro.remove(p);
							}	
						} else {
                            gameState.aroundTanjiro.remove(p);
						}	
					}
				}				
			}
			if (owner.getItemInHand().isSimilar(Items.getDSTanjiroDance())) {
				sendActionBarCooldown(owner, itemcooldown);}	
		} else {
			givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, false);
			givePotionEffet(owner, PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, false);
			givePotionEffet(owner, PotionEffectType.SPEED, Integer.MAX_VALUE,1, false);
			setNoFall(true);
			if (cd >= 1) {
				cd-=1;
			}else if (cd == 0) {
				owner.sendMessage("§7§lBoule d'énergie§7 est à nouveau§l Utilisable");
				cd--;
			}
		} 
	}
	@Override
		public void onDSCommandSend(String[] args, GameState gameState) {
		if (args[0].equalsIgnoreCase("sEnTir")) {
			if (actualuse < 3) {
				if (args.length == 2) {
					Player target = Bukkit.getPlayer(args[1]);
					if (target == null) {
						owner.sendMessage("§cVeuiller ciblé un joueur éxistant !");
					} else {
						if (!gameState.hasRoleNull(target)) {
							actualuse++;
							if (getPlayerRoles(target).getOldTeam() == TeamList.Demon || getPlayerRoles(target) instanceof Nezuko) {
								owner.sendMessage("§7§l"+target.getName()+"§7 sens le§c§l §nDémon");
							} else {
								owner.sendMessage("§7§l"+target.getName()+"§7 ne sens pas spécialement le§c Démon");
							}
						}
					}
				} else {
					if (gameState.aroundTanjiro.isEmpty()) {
						owner.sendMessage("§cIl n'y à aucun démon autours de vous");
						actualuse++;
						owner.sendMessage("Il ne vous reste que "+(3 - actualuse)+" utilisation du§6 /ds sentir");
						} else {
							actualuse++;
							int size = gameState.aroundTanjiro.size();
							gameState.aroundTanjiro.forEach(e -> owner.sendMessage("Il y à§c "+size+" §r de démon dans les 30blocs autours de vous (Nezuko est peut-être compter dedans)"));
							owner.sendMessage("Il ne vous reste que "+(3 - actualuse)+" utilisation du§6 /ds sentir");
						}
				}
			} else {
			owner.sendMessage("Vous ne pouvez plus sniffer les joueurs autours de vous");
			}
		}
		}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (!gameState.demonKingTanjiro) {
			if (killer == owner) {
				if (victim != owner){
					if (gameState.getInGamePlayers().contains(victim)) {
						if (gameState.getPlayerRoles().containsKey(victim)) {
							RoleBase role = gameState.getPlayerRoles().get(victim);
							if (gameState.Assassin != null) {
								if (gameState.Assassin.equals(role.owner)) {
									setForce(20);
									killassa = true;
									owner.sendMessage("§7Vous venez de venger votre famille en tuant§c "+role.owner.getName()+"§7, ce qui vous offre l'effet§c Force 1§7 permanent, de plus");
									givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 800, 1, true);
								}
							}
						}
					}
				}
			}	
		}		
			super.PlayerKilled(killer, victim, gameState);
		}
		boolean giveresi = false;
	@Override
		public boolean ItemUse(ItemStack item, GameState gameState) {
			if (item.isSimilar(Items.getDSTanjiroDance())) {
				if (itemcooldown <= 0) {
					MathUtil.sendCircleParticle(EnumParticle.FLAME, owner.getLocation(), 1, 15);
					owner.sendMessage(ChatColor.WHITE+"Vous venez d'activer votre: "+ChatColor.GOLD+"Dance du Dieu du Feu");
					givePotionEffet(owner, PotionEffectType.DAMAGE_RESISTANCE, 20*60*3, 1, true);
					givePotionEffet(owner, PotionEffectType.FIRE_RESISTANCE, 20*60*3, 1, true);
					dance = true;
					if (!giveresi) {
						giveresi = true;
						addresi(20);
					}
					itemcooldown = 60*8;
					for (Player p : gameState.getInGamePlayers()) {
						if (gameState.getPlayerRoles().containsKey(p)) {
							if (gameState.getPlayerRoles().get(p) instanceof Nezuko) {
								gameState.getPlayerRoles().get(p).owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*3, 1, false, false));
								gameState.getPlayerRoles().get(p).owner.sendMessage("Votre frère§a Tanjiro§r (§a"+owner.getName()+"§r) à activer sa§6 Danse du Dieu du Feu§r");
								DecimalFormat df = new DecimalFormat("0");
								gameState.getPlayerRoles().get(p).owner.sendMessage("Voici les coordonnées de votre frère: x:"+df.format(owner.getLocation().getX())+" y:"+df.format(owner.getLocation().getY())+" z:"+df.format(owner.getLocation().getZ()));
							}
						}
					}
				} else {
					sendCooldown(owner, itemcooldown);
				}
			}
			return super.ItemUse(item, gameState);
		}
	   	@Override
		public void ItemUseAgainst(ItemStack item, Player victim, GameState gameState) {
		if (item.getType() != Material.DIAMOND_SWORD)return;
		if (dance) {
			if (victim != owner) {
			   if (victim != null) {
			      victim.setFireTicks(200);
			   }
			}
			
		}
		if (gameState.demonKingTanjiro) {
			if (a) {
                if (victim != null) {
                    WorldUtils.createBeautyExplosion(victim.getLocation(), 2);
                }
                if (owner != null) {
                    if (victim != null) {
                        owner.sendMessage("Vous avez touchez: "+ victim.getName()+" avec votre§l Boule d'énergie");
                    }
                }
                if (victim != null) {
                    victim.sendMessage("Vous avez été toucher par la§l Boulot d'énergie§r de§c Tanjiro");
                }
                Heal(victim, -2.0);
				a = false;
			}
		}
			super.ItemUseAgainst(item, victim, gameState);
		}
	   	int cd = 0;
		public boolean a = false;
		public ItemStack[] getItems() {
			return new ItemStack[] {
					BetterItem.of(new ItemBuilder(Material.NETHER_STAR).addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().setName("§f§lBoule d'énergie").setLore("§7» Crée une explosion sur le prochaine adversaire que vous taperez","§7"+StringID).toItemStack(), event -> {
						if (cd <= 0) {
							a = true;
							cd = 120;
							owner.sendMessage("§7Vous avez accumulé asser d'énergie, votre§l Boule d'énergie§7 est paré pour le combat...");
						}else {
							sendCooldown(owner, cd);
						}
						return true;
					}).setDespawnable(true).setDroppable(false).setMovableOther(false).getItemStack()
			};
		}
	@Override
		public void resetCooldown() {
		cd = 0;
		actualuse = 0;
		}

	@Override
	public String getName() {
		return "§aTanjiro";
	}
}