package fr.nicknqck.roles.ds.solos;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.scenarios.impl.FFA;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.RandomUtils;
import net.md_5.bungee.api.ChatColor;

public class Kyogai extends RoleBase{

	public Kyogai(Player player, Roles roles) {
		super(player, roles);
		owner.sendMessage(AllDesc.Kyogai);
		owner.sendMessage("Pour choisir votre camp il faudra faire la commande: "+ChatColor.GOLD+"/ds role");
	}
	
	@Override
	public String[] Desc() {
		if (camp == Camp.Démon) {
			return AllDesc.KyogaiDemon;
		}
		if (camp == Camp.Solo) {
			return AllDesc.KyogaiSolo;
		}
		return AllDesc.Kyogai;
	}
	
	private enum Camp{
		Solo,
		Démon
	}
	 private Camp camp = null;
	 private int cooldowntambour = 0;
	 private int cooldownpercu = 0;
	 private boolean killmuzan = false;
	 private boolean killtanjiro = false;
	 private boolean usepercu = false;
	 @Override
	public void resetCooldown() {
		 cooldownpercu = 0;
		 cooldowntambour = 0;
	}
	 
	 @Override
	public void OpenFormInventory(GameState gameState) {
		 if (camp != null) return;
			Inventory inv = Bukkit.createInventory(owner, 9, "Choix de forme");
			inv.setItem(3, GUIItems.getKyogaiDémon());
			inv.setItem(5, GUIItems.getKyogaiSolo());
			owner.openInventory(inv);
			owner.updateInventory();
	}
	@Override
	public void FormChoosen(ItemStack item, GameState gameState) {
		setOldTeamList(TeamList.Demon);
		if (item.isSimilar(GUIItems.getKyogaiDémon())) {
			if (!FFA.getFFA()) {
				for (Player e : gameState.getInGamePlayers()) {
					if (gameState.getPlayerRoles().get(e) != null) {
						if (gameState.getPlayerRoles().get(e).type != null) {
							for (Player s : gameState.getInGamePlayers()) {
								s = owner;
								if (gameState.getPlayerRoles().get(s) != null) {
									if (gameState.getPlayerRoles().get(s).type != null) {
										if (gameState.getAvailableRoles().containsKey(Roles.Muzan)) {
											if (gameState.getLuneSupPlayers().contains(s)) {
												if (gameState.getPlayerRoles().get(e).type == Roles.Muzan) {
													s.sendMessage("Le joueur "+ChatColor.GOLD+s.getName()+"§r est "+ChatColor.GOLD+gameState.getPlayerRoles().get(e).type.name());
													setTeam(TeamList.Demon);
													owner.sendMessage("La commande§6 /ds me§r à été mis-à-jour !");
												}
											}
										} else {
											s.sendMessage("Aucun joueur ne possède le rôle§6 Muzan");
										}										
									}
								}
							}
						}
					}
				}
			} else {
				setTeam(TeamList.Solo);
				owner.sendMessage("Malgré votre choix vue que le mode FFA est activé vous devez tout de même gagner en temp que rôle solitaire");
			}
			System.out.println(owner.getName()+" = "+getTeam());
			System.out.println(owner.getName()+" = "+type.name());
			owner.getInventory().addItem(Items.getTambour());
			camp = Camp.Démon;
			setForce(20);
		}
		if (item.isSimilar(GUIItems.getKyogaiSolo())) {
			setTeam(TeamList.Solo);
			owner.sendMessage("La commande§6 /ds me§r à été mis-à-jour !");
			System.out.println(owner.getName()+" = "+getTeam());
			System.out.println(owner.getName()+" = "+type.name());
			owner.getInventory().addItem(Items.getTambour());
			owner.getInventory().addItem(Items.getPercussionRapide());
			camp = Camp.Solo;
			owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false));
			setForce(20);
		}
		super.FormChoosen(item, gameState);
	}
	@Override
	public void Update(GameState gameState) {
		if (owner.getItemInHand().isSimilar(Items.getTambour())) {
			sendActionBarCooldown(owner, cooldowntambour);
		}
		if (owner.getItemInHand().isSimilar(Items.getPercussionRapide())) {
			sendActionBarCooldown(owner, cooldownpercu);
		}
		if (camp == Camp.Démon) {
			if (getTeam() != TeamList.Demon && !FFA.getFFA())setTeam(TeamList.Demon);
			if (cooldowntambour >= 1) cooldowntambour--;
			if (killtanjiro) if (cooldownpercu >= 1) cooldownpercu--;
			if (gameState.nightTime) owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*3, 0, false, false), true);
			if (cooldownpercu == 60*10 && killtanjiro && usepercu) {
				usepercu = false; owner.sendMessage("Vous ne pouvez plus retourner de joueur en tapant ce dit joueur");
			}
			
		}
		if (camp == Camp.Solo) {
			if (getTeam() != TeamList.Solo)setTeam(TeamList.Solo);
			if (cooldowntambour >= 1) cooldowntambour--;
			if (cooldownpercu >= 1) cooldownpercu--;
			if (gameState.nightTime) owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*3, 0, false, false), true);

			if (cooldownpercu == 60*10 && usepercu) {
				usepercu = false; owner.sendMessage("Vous ne pouvez plus retourner de joueur en tapant ce dit joueur");
			}
		}
		super.Update(gameState);
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (camp == Camp.Démon) {	
				if (item.isSimilar(Items.getTambour())) {
					if (cooldowntambour <= 0) {
							Player t = getRightClicked(30, 1);
							if (t == null) {
								owner.sendMessage("§cVeuiller viser un joueur");
							} else {
								 Loc.inverserDirectionJoueur(t);
							     t.sendMessage("Kyogai vous à retourner");
								 owner.sendMessage("Vous avez retourné "+t.getName());
								 cooldowntambour = 30;
							}										
					}else {
						sendCooldown(owner, cooldowntambour);
					}		
			}
				if (item.isSimilar(Items.getPercussionRapide())) {
					if (cooldownpercu <= 0 && killtanjiro) {
						usepercu = true;
						cooldownpercu = 60*10+15;
						owner.sendMessage("Vous pouvez maintenant pendant §6 15s §r retourner un joueur en le tapant (avec §6 une chance sur 5§r)");
					} else {
					sendCooldown(owner, cooldownpercu);
					}
				}
		} else if (camp == Camp.Solo) {
			if (item.isSimilar(Items.getTambour())) {
				if (cooldowntambour <= 0) {
					if (!killmuzan) {
						Player t = getRightClicked(30, 1);
						if (t == null) {
							owner.sendMessage("§cVeuiller viser un joueur");
						} else {
							 Loc.inverserDirectionJoueur(t);
						     t.sendMessage("§6Kyogai§f vous à retourner");
							 owner.sendMessage("Vous avez retourné "+t.getName());
							 cooldowntambour = 30;
						}
					} else {
						for (Player target : gameState.getInGamePlayers()) {
						if (target != owner && target != null) {
							  if(target.getLocation().distance(owner.getLocation()) <= 30) {
								  	 Player player = (Player) target;
									 Location location = player.getLocation().clone();
								     location.setYaw(-location.getYaw());
								     location.setPitch(-location.getPitch());
								     target.teleport(location);
								     target.sendMessage("Kyogai vous à retourner");
									 owner.sendMessage("Vous avez retourné "+target.getName());
									 cooldowntambour = 30;
							    }
							}
						}
					}
										
				}else {
					sendCooldown(owner, cooldowntambour);
				}
			}
			if (item.isSimilar(Items.getPercussionRapide())) {
				if (cooldownpercu <= 0) {
					usepercu = true;
					cooldownpercu = 60*10+15;
					owner.sendMessage("Vous pouvez maintenant pendant §6 15s §r retourner un joueur en le tapant (avec §6 une chance sur 5§r)");
				} else {
					sendCooldown(owner, cooldownpercu);
				}
			}
		}
		return super.ItemUse(item, gameState);
	}
	public void ItemUseAgainst(ItemStack item, Player victim, GameState gameState) {
	if (!usepercu) return;
	if (camp == Camp.Solo) {
		Material type = item.getType();
		if (type == Material.DIAMOND_SWORD || type == Material.IRON_SWORD || type == Material.GOLD_SWORD || type == Material.STONE_SWORD || type == Material.WOOD_SWORD) {
			int izi = RandomUtils.getRandomInt(0, 5);
			if (izi == 0) {
				double min = 30;
				Player target = null;
				for (Player p : gameState.getInGamePlayers()) {
					if (p != owner) {
						double dist = Math.abs(p.getLocation().distance(owner.getLocation()));
						if (dist < min) {
							target = p;
							min = dist;
						}
					}
				}
				if (target != null) {
					 Loc.inverserDirectionJoueur(target);
				     target.sendMessage("§6Kyogai§f vous à retourner");
					 owner.sendMessage("Vous avez retourné "+target.getName());
					 
				}
			}
		}
	} else if (camp == Camp.Démon) {
		Material type = item.getType();
		if (type == Material.DIAMOND_SWORD || type == Material.IRON_SWORD || type == Material.GOLD_SWORD || type == Material.STONE_SWORD || type == Material.WOOD_SWORD) {
			int izi = RandomUtils.getRandomInt(0, 5);
			if (izi == 0) {
				double min = 30;
				Player target = null;
				for (Player p : gameState.getInGamePlayers()) {
					if (p != owner) {
						double dist = Math.abs(p.getLocation().distance(owner.getLocation()));
						if (dist < min) {
							target = p;
							min = dist;//
						}
					}
				}
				if (target != null) {
					 Loc.inverserDirectionJoueur(target);
				     target.sendMessage("§cKyogai vous à retourner");
					 owner.sendMessage("Vous avez retourné "+target.getName());
					 
				}
			}
		}
	}
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (killer != owner)return;
		if (victim == owner)return;
		if (gameState.getInGamePlayers().contains(victim)) {
			if (gameState.getPlayerRoles().containsKey(victim)) {
				RoleBase role = gameState.getPlayerRoles().get(victim);
		if (camp == Camp.Solo) {		
			if (role.type == Roles.Muzan && !killmuzan) {
				killmuzan = true;
				setMaxHealth(getMaxHealth() + 4.0);
				owner.sendMessage(ChatColor.GRAY+"Vous venez de tuez "+ChatColor.GOLD+role.type+ChatColor.GRAY+" vous obtenez donc "+ChatColor.GOLD+"2 coeur permanent"+ChatColor.GRAY+" supplémentaire, également l'objet "+ChatColor.GOLD+"Tambour"+ChatColor.GRAY+" à été améliorer car maintenant il retournera tout les joueurs (sauf vous) étant dans une zone de 30 blocs au tours de vous");
					}
				} else if (camp == Camp.Démon) {
					if (role.type == Roles.Tanjiro && !killtanjiro) {
						killtanjiro = false;
						owner.sendMessage(ChatColor.GRAY+"Vous venez de tuez "+ChatColor.GOLD+role.type+ChatColor.GRAY+" vous obtenez donc l'item "+ChatColor.GOLD+"Percussion Rapide"+ChatColor.GRAY+" vous obtiendrez l'item §6Percussion Rapide"+ChatColor.GRAY+" vous activerez un passif d'une durée de 10 secondes qui vous permettra de faire que quand vous tapez un joueur il y aura 1 chance sur 5 qu'elle sois retourner");
					}
				}
			}
		}
		super.PlayerKilled(killer, victim, gameState);
	}

	@Override
	public ItemStack[] getItems() {
		if (camp != null) {
			if (camp == Camp.Solo) {
				return new ItemStack[] {
					Items.getTambour(),
					Items.getPercussionRapide()
				};
			}
			if (camp == Camp.Démon) {
				if (killtanjiro) {
					return new ItemStack[] {
						Items.getTambour(),
						Items.getPercussionRapide()
					};
				}else {
					return new ItemStack[] {
						Items.getTambour()	
					};
				}
			}
		}
		return new ItemStack[0];
	}
}