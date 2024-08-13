package fr.nicknqck.roles.ds.demons.lune;

import java.util.Random;
import java.util.UUID;

import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;

public class Gyutaro extends DemonsRoles {

	public Gyutaro(UUID player) {
		super(player);
		setCanRespawn(true);
		this.setResi(20);
		}

	@Override
	public DemonType getRank() {
		return DemonType.LuneSuperieur;
	}

	@Override
	public Roles getRoles() {
		return Roles.Gyutaro;
	}
	private boolean killtengen = false;
	private boolean diedaki = false;
	private int faucillecooldown = 0;	
	private int itemcooldown = 0;
	private int troisiemeoeilcooldown = 0;
	private int healthtime = 0;
	@Override
	public void resetCooldown() {
		faucillecooldown = 0;
		itemcooldown = 0;
		troisiemeoeilcooldown = 0;
	}
	@Override
	public TeamList getOriginTeam() {
		return TeamList.Demon;
	}
	@Override
	public String[] Desc() {
		KnowRole(owner, Roles.Muzan, 1);
		KnowRole(owner, Roles.Daki, 1);
		return AllDesc.Gyutaro;
	}
	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}
	@Override
	public ItemStack[] getItems() {
		if (diedaki) {
			return new ItemStack[] {
				Items.getFaucille(),
				Items.getPouvoirSanginaire(),
				Items.getTroisièmeOeil()
			};
		}
		return new ItemStack[] {
				Items.getFaucille(),
				Items.getPouvoirSanginaire()
		};
	}

	@Override
	public String getName() {
		return "Gyutaro";
	}

	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (victim == owner) {			
			if (isCanRespawn()) {
				GameListener.RandomTp(owner);
				owner.sendMessage("Vous venez d'être TP aléatoirement");
				setMaxHealth(getMaxHealth() - 2.0); 
				owner.setHealth(getMaxHealth());
				owner.sendMessage("Vous venez de réssucité vous perdez donc 1 coeur permanent ce qui vous fait tomber à: "+ChatColor.GOLD + (getMaxHealth() / 2) + " coeur");
			} else {
				victim.getInventory().remove(Material.NETHER_STAR);
			}
		}
		if (victim != owner) {
			if (gameState.getInGamePlayers().contains(victim)) {
				if (gameState.getPlayerRoles().containsKey(victim)) {
					RoleBase r = gameState.getPlayerRoles().get(victim);
					if (r instanceof Daki && !diedaki) {
						diedaki = true;
						giveItem(owner, false, Items.getTroisièmeOeil());
						owner.sendMessage(ChatColor.GOLD+""+ r.getRoles()+ChatColor.GRAY+" est morte vous récupérez donc votre troisième oeil qui vous donnera speed 1 en l'activant");
						}
					if (killer == owner) {
						if (r.getRoles() == Roles.Tengen && !killtengen) {
							killtengen = true;
							owner.sendMessage(ChatColor.GRAY+"Vous venez de tuez le joueur possédant le rôle de: "+ChatColor.GOLD+"Tengen "+ChatColor.GRAY+"vous obtenez donc force 1 le jour");
							}
						if (r.getRoles() == Roles.Tengen || r.getRoles() == Roles.Tanjiro || r.getRoles() == Roles.Inosuke || r.getRoles() == Roles.ZenItsu || r.getRoles() == Roles.Nezuko) {
							if (owner.getMaxHealth() <= 18.0) {
								owner.sendMessage(ChatColor.WHITE+"Vous venez de tué le joueur: "+ChatColor.GOLD + victim.getName() + ChatColor.WHITE+", il possédait le rôle de: "+ChatColor.GOLD+ r.getRoles()+ ChatColor.WHITE+" vous augmenter donc vos point de vie à: "+ChatColor.GOLD+ (getMaxHealth() / 2) +" coeur");
							}
						}
					}
			}
		}
	}
		super.PlayerKilled(killer, victim, gameState);
	}
	@Override
	public void Update(GameState gameState) {
		if (owner.getItemInHand().isSimilar(Items.getPouvoirSanginaire())) {
			sendActionBarCooldown(owner, itemcooldown);
		}
		if (owner.getItemInHand().isSimilar(Items.getFaucille())) {
			sendActionBarCooldown(owner, faucillecooldown);
		}
		if (owner.getItemInHand().isSimilar(Items.getTroisièmeOeil())) {
			sendActionBarCooldown(owner, troisiemeoeilcooldown);
		}
		if (gameState.nightTime) {
			owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*3, 0, false, false), true);
		} if (killtengen && !gameState.nightTime) {
			owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*3, 0, false, false), true);
		}
		for (RoleBase r : gameState.getPlayerRoles().values()) {
			if (!gameState.getInGamePlayers().contains(r.owner)) continue;
			if (r instanceof Daki && owner.getWorld().equals(r.owner.getWorld())) {
				if (r.owner.getLocation().distance(owner.getLocation()) <= 30)
					owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*2, 0, false, false), true);
			}
		}
		if (getMaxHealth() >= 16.0) {
			setCanRespawn(true);
		}else {
			setCanRespawn(false);
		}
		if (faucillecooldown >= 1) {faucillecooldown--;}
		if (itemcooldown >= 1) {itemcooldown--;}
		
		if (troisiemeoeilcooldown >= 1) {troisiemeoeilcooldown--;}
		if (healthtime >= 1) {healthtime--;}
		if (healthtime == 60*3) {setMaxHealth(getMaxHealth() - 2.0);}
		super.Update(gameState);
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(Items.getTroisièmeOeil())) {
			if (diedaki) {
				if (troisiemeoeilcooldown <= 0) {
					owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60, 0, false, false));
					owner.sendMessage(ChatColor.WHITE+"Vous venez d'activer votre pouvoir vous donne l'effet speed 1 pendant 1 minutes");
					troisiemeoeilcooldown = 60*3;
				}  else {
					sendCooldown(owner, troisiemeoeilcooldown);
				}
			} else {
				owner.sendMessage(ChatColor.WHITE+"Daki n'est pas morte vous ne pouvez donc pas utiliser cette objet (nan sérieux comment tu la eu y'a un anti drop");
			}
		}
		if (item.isSimilar(Items.getFaucille())) {
			if (faucillecooldown <= 0) {
				double min = 25;
				Player target = null;
				for (Player p : gameState.getInGamePlayers()) {
					if (owner.canSee(p) && p != owner) {
						double dist = Math.abs(p.getLocation().distance(owner.getLocation()));
						if (dist < min) {
							target = p;
							min = dist;
						}
					}
				}
				if (target != null) {
					if (owner.canSee(target)) {
					target.sendMessage("Vous avez été toucher par les Faucille de Gyutaro");
					target.setHealth(target.getMaxHealth() - 4.0);
					}
					owner.sendMessage("Lancement de vos Faucille sur: " + target.getName());
					setMaxHealth(getMaxHealth() + 2.0);
					healthtime = 60*5;
					faucillecooldown = healthtime;
				} else {
					owner.sendMessage("Veuiller viser un joueur");
				}
			} else {
				sendCooldown(owner, faucillecooldown);
			}
		}
		if (item.isSimilar(Items.getPouvoirSanginaire())) {
			if (itemcooldown <= 0) {
				owner.sendMessage("Exécution de votre: "+ChatColor.GOLD+" Pouvoir Sanginaire");
				for(Player p : gameState.getInGamePlayers()) {
					Player player = p;
					if (player != owner) {
						if(p.getLocation().distance(owner.getLocation()) <= 30) {
							if (gameState.getPlayerRoles().get(player).getRoles() != Roles.Daki ) {
								Random random = new Random();
								int rint = random.nextInt(2);
								if (rint == 0) {
									player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*30, 0, false, false));
									owner.sendMessage(ChatColor.WHITE+"Le joueur: "+ChatColor.GOLD+ player.getName() + ChatColor.WHITE+" à reçue Poison 1 pendant 30 secondes");
								}
								if (rint == 1) {
									player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*15, 1, false, false));
									owner.sendMessage(ChatColor.WHITE+"Le joueur: "+ChatColor.GOLD+ player.getName() + ChatColor.WHITE+" à reçue Poison 2 pendant 15 secondes");
								}
								if (rint == 2) {
									player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*30, 0, false, false));
									owner.sendMessage(ChatColor.GRAY+"Le joueur: "+ChatColor.GOLD+ player.getName() + ChatColor.GRAY+" à reçue Poison 1 pendant 30 secondes");
								}
								p.sendMessage("Vous avez été toucher par le Pouvoir Sanginaire de Gyutaro");
							}
						}
						itemcooldown = 60*2;
						return true;
						}
				}
			}  else {
				sendCooldown(owner, itemcooldown);
			}
		}
		return super.ItemUse(item, gameState);
	}
}