package fr.nicknqck.roles.ds.demons;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.betteritem.BetterItem;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Furuto extends DemonInferieurRole {

	public Furuto(UUID player) {
		super(player);
	}
	@Override
	public TeamList getOriginTeam() {
		return TeamList.Demon;
	}
	@Override
	public DemonType getRank() {
		return DemonType.Demon;
	}

	@Override
	public Roles getRoles() {
		return Roles.Furuto;
	}
	@Override
	public String[] Desc() {
		return AllDesc.Furuto;
	}

	@Override
	public String getName() {
		return "Furuto";
	}

	private final List<Player> aP = new ArrayList<>();
	@Override
	public void Update(GameState gameState) {
		if (cooldown>=1)cooldown--;
		if (gameState.nightTime) {
			owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*3, 0, false, false), true);
		}
		for (UUID u : gameState.getInGamePlayers()) {
			Player p = Bukkit.getPlayer(u);
			if (p == null)continue;
			GamePlayer gamePlayer = gameState.getGamePlayer().get(p.getUniqueId());
			if (gamePlayer != null && owner.getWorld().equals(p.getWorld())) {
				if (p.getLocation().distance(owner.getLocation()) <= 20 && !aP.contains(p)) {
					aP.add(p);
				}else {
					aP.remove(p);
				}
			}
		}
		if (owner.getItemInHand().getItemMeta() != null) {
			if (owner.getItemInHand().getItemMeta().getDisplayName() != null) {
				if (owner.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("Flûte")) {
				sendActionBarCooldown(owner, cooldown);
			}
			}
		}
	}
	@Override
	public void RoleGiven(GameState gameState) {
		owner.getInventory().addItem(getItems());
		super.RoleGiven(gameState);
	}
	private int cooldown = 0;
	@Override
	    public ItemStack[] getItems() {
        return new ItemStack[]{BetterItem.of((new ItemBuilder(Material.NETHER_STAR)).setName("Flûte").toItemStack(), (event) -> {
            if (event.isLeftClick()) return false;
            else {
            	for (UUID u : gameState.getInGamePlayers()) {
            		if (cooldown >= 1) {
            			sendCooldown(event.getPlayer(), cooldown);
						return true;
            		}
					Player p = Bukkit.getPlayer(u);
					if (p == null)continue;
					GamePlayer gamePlayer = gameState.getGamePlayer().get(p.getUniqueId());
            		if (aP.contains(p)) {
            			int r = RandomUtils.getRandomInt(0, 4);
                			if (r == 0) {
                				if (gamePlayer.getRole().getOriginTeam() == TeamList.Demon) {
                					if (!gameState.nightTime&& !p.hasPotionEffect(PotionEffectType.SPEED)) {
                    					p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60, 0, false, false), true);
                    					p.sendMessage(getOriginTeam().getColor()+"Furuto§r vous à offert Speed 1");
                    					owner.sendMessage("Vous avez offert à§6 "+p.getName()+"§r"+" Speed 1 pendant§6 60§rs ");
                    				} else {
                    					if (!p.hasPotionEffect(PotionEffectType.SPEED)) {
                                    		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60, 1, false, false), true);
                        					p.sendMessage(getOriginTeam().getColor()+"Furuto§r vous à offert Speed 2");
                        					owner.sendMessage("Vous avez offert à§6 "+p.getName()+"§r"+" Speed 2 pendant§6 60§rs ");
                    					}   			
                    				}
                				} else {
                					if (p.hasPotionEffect(PotionEffectType.SLOW))return false;
                					if (!gameState.nightTime) {
                						p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*60, 0, false, false), true);
                						p.sendMessage(getOriginTeam().getColor()+"Furuto§r vous à offert Slowness 1");
                						owner.sendMessage("Vous avez offert à§6 "+p.getName()+"§r"+" Slowness 1 pendant§6 60§rs ");
                					} else {
                						p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*60, 1, false, false), true);
                						p.sendMessage(getOriginTeam().getColor()+"Furuto§r vous à offert Slowness 2");
                						owner.sendMessage("Vous avez offert à§6 "+p.getName()+"§r"+" Slowness 2 pendant§6 60§rs ");
                					}
                				}
                				cooldown = 60*3;
                               // owner.sendMessage("Vous venez d'activer votre§6 Flûte");
                			}
                			if (r == 1) {
                				if (gamePlayer.getRole().getOriginTeam() == TeamList.Demon) {
                					if (!gameState.nightTime) {
                    					if (!p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                    						p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*60, 0, false, false), true);
                        					p.sendMessage(getOriginTeam().getColor()+"Furuto§r vous à offert Force 1");
                        					owner.sendMessage("Vous avez offert à§6 "+p.getName()+"§r"+" Force 1 pendant§6 60§rs ");
                        					cooldown = 60*3;
                                           // owner.sendMessage("Vous venez d'activer votre§6 Flûte");
                    					}
                    				} else {
                    					if (!p.hasPotionEffect(PotionEffectType.SPEED)) {
                    						p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60, 0, false, false), true);
                        					p.sendMessage(getOriginTeam().getColor()+"Furuto§r vous à offert Speed 1");
                        					owner.sendMessage("Vous avez offert à§6 "+p.getName()+"§r"+" Speed 1 pendant§6 60§rs ");
                        					cooldown = 60*3;
                                           // owner.sendMessage("Vous venez d'activer votre§6 Flûte");
                    					}
                    				}
                				} else {
                					if (!gameState.nightTime) {
                    					if (!p.hasPotionEffect(PotionEffectType.WEAKNESS)) {
                    						p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*60, 0, false, false), true);
                        					p.sendMessage(getOriginTeam().getColor()+"Furuto§r vous à offert Weakness 1");
                        					owner.sendMessage("Vous avez offert à§6 "+p.getName()+"§r"+" Weakness 1 pendant§6 60§rs ");
                        					cooldown = 60*3;
                                           // owner.sendMessage("Vous venez d'activer votre§6 Flûte");
                    					}
                    				} else {
                    					if (!p.hasPotionEffect(PotionEffectType.SLOW)) {
                    						p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*60, 0, false, false), true);
                        					p.sendMessage(getOriginTeam().getColor()+"Furuto§r vous à offert Slowness 1");
                        					owner.sendMessage("Vous avez offert à§6 "+p.getName()+"§r"+" Slowness 1 pendant§6 60§rs ");
                        					cooldown = 60*3;
                                           // owner.sendMessage("Vous venez d'activer votre§6 Flûte");
                    					}
                    				}	
                				}                				
                			}
                			if (r == 2) {
                				if (gamePlayer.getRole().getOriginTeam() == TeamList.Demon) {
                					if (!gameState.nightTime) {
                    					if (!p.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                    						if (gamePlayer.getRole().getResi() != 20)gamePlayer.getRole().setResi(gamePlayer.getRole().getResi() + 20);
                    						p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60, 0, false, false), true);
                        					p.sendMessage(getOriginTeam().getColor()+"Furuto§r vous à offert Résistance 1");
                        					owner.sendMessage("Vous avez offert à§6 "+p.getName()+"§r"+" Résistance 1 pendant§6 60§rs ");
                    					}
                    				} else {
                    					if (!p.hasPotionEffect(PotionEffectType.JUMP)) {
                    						p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20*60, 1, false, false), true);
                        					p.sendMessage(getOriginTeam().getColor()+"Furuto§r vous à offert Speed 1");
                        					owner.sendMessage("Vous avez offert à§6 "+p.getName()+"§r"+" Jump Boost 2 pendant§6 60§rs ");
                    					}
                    				}	
                				} else {
                					if (!gameState.nightTime) {
                    					if (!p.hasPotionEffect(PotionEffectType.SLOW_DIGGING)) {
                    						p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20*60, 0, false, false), true);
                    						p.sendMessage(getOriginTeam().getColor()+"Furuto§r vous à offert Mining Fatigue 1");
                    						owner.sendMessage("Vous avez offrt à§6 "+p.getName()+"§r Mining Fatigue 1 pendant§6 60§rs");
                                        }
                    				} else {
                    					if (!p.hasPotionEffect(PotionEffectType.CONFUSION)) {
                    						p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*30, 0, false, false), true);
                    						p.sendMessage(getOriginTeam().getColor()+"Furuto§r vous à offert Nausee 1");
                    						owner.sendMessage("Vous avez offrt à§6 "+p.getName()+"§r Nausée 1 pendant§6 30§rs");
                                        }
                    				}
                				}
                				cooldown = 60*3;
                               // owner.sendMessage("Vous venez d'activer votre§6 Flûte");
                			}
                			if (r == 3) {
                				if (gameState.getPlayerRoles().get(p).getOriginTeam() == TeamList.Demon) {
                					if (!gameState.nightTime) {
                    					if (!p.hasPotionEffect(PotionEffectType.JUMP)) {
                    						p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20*60, 0, false, false), true);
                        					p.sendMessage(getOriginTeam().getColor()+"Furuto§r vous à offert Jump Boost 1");
                        					owner.sendMessage("Vous avez offert à§6 "+p.getName()+"§r"+" Jump Boost 1 pendant§6 60§rs ");
                        					cooldown = 60*3;
                                           // owner.sendMessage("Vous venez d'activer votre§6 Flûte");
                    					}
                    				} else {
                    					if (!p.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                    						if (gamePlayer.getRole().getResi() != 20)gamePlayer.getRole().setResi(gamePlayer.getRole().getResi() + 20);
                    						p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60, 0, false, false), true);
                        					p.sendMessage(getOriginTeam().getColor()+"Furuto§r vous à offert Résistance 1");
                        					owner.sendMessage("Vous avez offert à§6 "+p.getName()+"§r"+" Résistance 1 pendant§6 60§rs ");
                        					cooldown = 60*3;
                                           // owner.sendMessage("Vous venez d'activer votre§6 Flûte");
                    					}
                    				}
                				} else {
                				if (!gameState.nightTime) {
                					if (!p.hasPotionEffect(PotionEffectType.SLOW_DIGGING)) {
                						p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20*60, 0, false, false), true);
                						p.sendMessage(getOriginTeam().getColor()+"Furuto§r vous à offert Mining Fatigue 1");
                						owner.sendMessage("Vous avez offrt à§6 "+p.getName()+"§r Mining Fatigue 1 pendant§6 60§rs");
                						cooldown = 60*3;
                					}
                				} else {
                					if (!p.hasPotionEffect(PotionEffectType.CONFUSION)) {
                						p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*30, 0, false, false), true);
                						p.sendMessage(getOriginTeam().getColor()+"Furuto§r vous à offert Nausee 1");
                						owner.sendMessage("Vous avez offrt à§6 "+p.getName()+"§r Nausée 1 pendant§6 30§rs");
                						cooldown = 60*3;
                					}
                				}
                				}
                			}
                			 owner.sendMessage("Vous venez d'activer votre§6 Flûte");
            		}
            	}
            }
            return true;
        }).setDroppable(false).setMovableOther(false).getItemStack()};
    }
	@Override
	public void resetCooldown() {
		cooldown = 0;
	}

	@Override
	public void onDSCommandSend(String[] args, GameState gameState) {
		super.onDSCommandSend(args, gameState);
	}
}