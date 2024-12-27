package fr.nicknqck.roles.ds.solos;

import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.demons.Muzan;
import fr.nicknqck.roles.ds.slayers.Tanjiro;
import lombok.NonNull;
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
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.scenarios.impl.FFA;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.RandomUtils;
import net.md_5.bungee.api.ChatColor;

import java.util.UUID;

public class Kyogai extends DemonsRoles {

	public Kyogai(UUID player) {
		super(player);
	}

	@Override
	public void RoleGiven(GameState gameState) {
		super.RoleGiven(gameState);
		owner.sendMessage("Pour choisir votre camp il faudra faire la commande: "+ChatColor.GOLD+"/ds role");
	}

	@Override
	public @NonNull DemonType getRank() {
		return DemonType.DEMON;
	}

	@Override
	public Roles getRoles() {
		return Roles.Kyogai;
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
	@Override
	public TeamList getOriginTeam() {
		return TeamList.Solo;
	}
	@Override
	public String getName() {
		return "Kyogai";
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
		if (item.isSimilar(GUIItems.getKyogaiDémon())) {
			if (!FFA.getFFA()) {
				for (UUID u : gameState.getInGamePlayers()) {
					Player e = Bukkit.getPlayer(u);
					if (e == null)continue;
					if (gameState.getPlayerRoles().get(e) != null) {
						if (!gameState.hasRoleNull(e.getUniqueId())) {
							for (UUID as : gameState.getInGamePlayers()) {
								Player s = Bukkit.getPlayer(as);
								if (s == null)continue;
								if (!gameState.hasRoleNull(s.getUniqueId())) {
										if (gameState.getAvailableRoles().containsKey(Roles.Muzan)) {
											if (gameState.getPlayerRoles().get(e) instanceof Muzan) {
												s.sendMessage("Le joueur §6"+s.getName()+"§r est§6 "+gameState.getPlayerRoles().get(e).getRoles().name());
											}
										} else {
											s.sendMessage("Aucun joueur ne possède le rôle§6 Muzan");
										}
								}
							}
						}
					}
				}
				setTeam(TeamList.Demon);
				owner.sendMessage("La commande§6 /ds me§r à été mis-à-jour !");
			} else {
				setTeam(TeamList.Solo);
				owner.sendMessage("Malgré votre choix vue que le mode FFA est activé vous devez tout de même gagner en temp que rôle solitaire");
			}
			System.out.println(owner.getName()+" = "+ getOriginTeam()+", OldTeam = "+getOriginTeam());
			System.out.println(owner.getName()+" = "+getRoles().name());
			owner.getInventory().addItem(Items.getTambour());
			camp = Camp.Démon;
		}
		if (item.isSimilar(GUIItems.getKyogaiSolo())) {
			setTeam(TeamList.Solo);
			owner.sendMessage("La commande§6 /ds me§r à été mis-à-jour !");
			System.out.println(owner.getName()+" = "+ getOriginTeam());
			System.out.println(owner.getName()+" = "+getRoles().name());
			owner.getInventory().addItem(Items.getTambour());
			owner.getInventory().addItem(Items.getPercussionRapide());
			camp = Camp.Solo;
			owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false));
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
			if (getOriginTeam() != TeamList.Demon && !FFA.getFFA())setTeam(TeamList.Demon);
			if (cooldowntambour >= 1) cooldowntambour--;
			if (killtanjiro) if (cooldownpercu >= 1) cooldownpercu--;
			if (gameState.nightTime) owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*3, 0, false, false), true);
			if (cooldownpercu == 60*10 && killtanjiro && usepercu) {
				usepercu = false; owner.sendMessage("Vous ne pouvez plus retourner de joueur en tapant ce dit joueur");
			}
			
		}
		if (camp == Camp.Solo) {
			if (getOriginTeam() != TeamList.Solo)setTeam(TeamList.Solo);
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
						for (UUID u : gameState.getInGamePlayers()) {
							Player target = Bukkit.getPlayer(u);
							if (target == null)continue;
						if (u != getPlayer()) {
							  if(target.getLocation().distance(owner.getLocation()) <= 30) {
                                  Location location = target.getLocation().clone();
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
				for (UUID u : gameState.getInGamePlayers()) {
					Player p = Bukkit.getPlayer(u);
					if (p == null)continue;
					if (u != getPlayer()) {
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
				for (UUID u : gameState.getInGamePlayers()) {
					Player p = Bukkit.getPlayer(u);
					if (p == null)continue;
					if (u != getPlayer()) {
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
		if (gameState.getInGamePlayers().contains(victim.getUniqueId())) {
			if (gameState.getPlayerRoles().containsKey(victim)) {
				RoleBase role = gameState.getPlayerRoles().get(victim);
		if (camp == Camp.Solo) {		
			if (role instanceof Muzan && !killmuzan) {
				killmuzan = true;
				setMaxHealth(getMaxHealth() + 4.0);
				owner.sendMessage(ChatColor.GRAY+"Vous venez de tuez "+ChatColor.GOLD+role.getRoles()+ChatColor.GRAY+" vous obtenez donc "+ChatColor.GOLD+"2 coeur permanent"+ChatColor.GRAY+" supplémentaire, également l'objet "+ChatColor.GOLD+"Tambour"+ChatColor.GRAY+" à été améliorer car maintenant il retournera tout les joueurs (sauf vous) étant dans une zone de 30 blocs au tours de vous");
					}
				} else if (camp == Camp.Démon) {
					if (role instanceof Tanjiro && !killtanjiro) {
						killtanjiro = true;
						owner.sendMessage(ChatColor.GRAY+"Vous venez de tuez "+ChatColor.GOLD+role.getRoles()+ChatColor.GRAY+" vous obtenez donc l'item "+ChatColor.GOLD+"Percussion Rapide"+ChatColor.GRAY+" vous obtiendrez l'item §6Percussion Rapide"+ChatColor.GRAY+" vous activerez un passif d'une durée de 10 secondes qui vous permettra de faire que quand vous tapez un joueur il y aura 1 chance sur 5 qu'elle sois retourner");
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