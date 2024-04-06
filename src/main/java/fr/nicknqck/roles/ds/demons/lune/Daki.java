package fr.nicknqck.roles.ds.demons.lune;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Objective;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.TeamList;
import fr.nicknqck.roles.desc.AllDesc;

public class Daki extends RoleBase{

	public Daki(Player player, Roles roles, GameState gameState) {
		super(player, roles, gameState);
		for (String desc : AllDesc.Daki) owner.sendMessage(desc);
		this.setForce(20);
		setCanRespawn(true);
		this.setResi(20);
		gameState.addLuneSupPlayers(owner);
		if (!gameState.lunesup.contains(owner))gameState.lunesup.add(owner);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			for (Player p : getIGPlayers()) {
				if (getPlayerRoles(p).type == Roles.Muzan) {
					owner.sendMessage("La personne possédant le rôle de§c Muzan§r est:§c "+p.getName());
				}
				if (getPlayerRoles(p).type == Roles.Gyutaro) {
					owner.sendMessage("La personne possédant le rôle de§c Gyutaro§r est:§c "+p.getName());
				}
			}
		}, 20);
	}
	
	@Override
	public String[] Desc() {
		KnowRole(owner, Roles.Daki, 5);
		KnowRole(owner, Roles.Muzan, 5);
		return AllDesc.Daki;
	}
	private boolean diegyutaro = false;
	private boolean dietizt = false;
	private int obicooldown = 0;
	private int troisiemeoeilcooldown = 0;
	@Override
	public void resetCooldown() {
		obicooldown = 0;
		troisiemeoeilcooldown = 0;
	}
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(getItems());
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				Items.getObi(),
				Items.getTroisièmeOeil()
		};
	}
	int obitime = 0;
	@Override
	public void Update(GameState gameState) {
		if (owner.getItemInHand().isSimilar(Items.getObi())) {
			sendActionBarCooldown(owner, obicooldown);
		}
		if (owner.getItemInHand().isSimilar(Items.getTroisièmeOeil())) {
			sendActionBarCooldown(owner, troisiemeoeilcooldown);
		}
		if (obitime > 0) {
			obitime-=1;
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (gameState.getInObiPlayers().contains(p)) {
					p.sendMessage("Temp avant liberté:§6 "+obitime+"§rs");
				}
			}
		}
		if (obitime == 0 && obicooldown > 0) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (gameState.getInObiPlayers().contains(p)) {
					p.sendMessage("Temp avant liberté:§6 "+obitime+"§rs");
					gameState.delInObiPlayers(p);
					p.sendMessage("Vous n'êtes plus sous l'emprise des Obis de Daki");
					if (p.getAllowFlight())p.setAllowFlight(false);
				}
			}
		}
		if (gameState.nightTime) {
			owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*3, 0, false, false), true);
		}
		for (RoleBase r : gameState.getPlayerRoles().values()) {
			if (!gameState.getInGamePlayers().contains(r.owner))continue;
			if (r.type == Roles.Gyutaro && r.owner.getWorld().equals(owner.getWorld())) {
				if (r.owner.getLocation().distance(owner.getLocation()) <= 30)
					owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*3, 0, false, false), true);
			}
			if (dietizt == false) {
			if (r.type == Roles.Tanjiro || r.type == Roles.Tengen || r.type == Roles.Inosuke || r.type == Roles.ZenItsu || r.type == Roles.Nezuko) {
				if (r.owner.getLocation().distance(owner.getLocation()) <= 15) {
					owner.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*2, 0, false, false), true);
					}
				}
			}
		}
		if (diegyutaro) {
			owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
		}
		if (obicooldown >= 1) {obicooldown--;}
		if (troisiemeoeilcooldown >= 1) {troisiemeoeilcooldown--;}
		super.Update(gameState);
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {		
		if (item.isSimilar(Items.getTroisièmeOeil())) {
			if (troisiemeoeilcooldown <= 0) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60, 0, false, false), true);
				owner.sendMessage(ChatColor.WHITE+"Vous venez d'activer le pouvoir de votre frère Gyutaro qui vous donne l'effet speed 1 pendant 1 minutes");
				troisiemeoeilcooldown = 60*3;
			}  else {
				int s = troisiemeoeilcooldown%60;
				int m = troisiemeoeilcooldown/60;
				owner.sendMessage(ChatColor.RED+"Vous ne pourrez utiliser a nouveau votre abilité que dans "+ChatColor.GOLD+m+" minutes"+ChatColor.RED+" et "+ChatColor.GOLD+s+" secondes");
				return true;
			}
		}
		if (item.isSimilar(Items.getObi())) {
			if (obicooldown <= 0) {
				for (Player pl : gameState.getInGamePlayers()) {
					RoleBase p = gameState.getPlayerRoles().get(pl);
					if (pl != owner) {
						if (p.type != Roles.Tanjiro && p.getTeam() != TeamList.Demon && p.type != Roles.Inosuke && p.type != Roles.Tengen && p.type != Roles.ZenItsu && p.type != Roles.Daki && p.type != Roles.Gyutaro) {
							Player player = p.owner;
							if (player.getLocation().distance(owner.getLocation()) <= 30) {
								if (!gameState.getInObiPlayers().contains(player)) {
									gameState.addInObiPlayers(player);
									player.sendMessage("Vous avez été toucher par les Obis de Daki vous ne pouvez donc plus bouger pendant 8s");
									owner.sendMessage(ChatColor.WHITE+"Vous venez de lancer vos Obis sur: "+player.getName());
									obicooldown = 60*5;
									obitime = 8;
								}//getobi
							} //location
						} else {
							if (p.getTeam() == TeamList.Demon) {
								pl.sendMessage("§6Daki§r à utilisé ses Obis mais vous y êtes visiblement autorisé car vous êtes dans le camp des§6 "+p.getTeam().name());
								return true;
							} else {
								pl.sendMessage("§6Daki§r à utilisé ses Obis mais vous y êtes visiblement autorisé car vous possédez le rôle:§6 "+p.type.name());
								return true;
							}
						}
					}
				}		
			}  else {
				int s = obicooldown%60;
				int m = obicooldown/60;
				owner.sendMessage(ChatColor.RED+"Vous ne pourrez utiliser a nouveau votre abilité que dans "+ChatColor.GOLD+m+" minutes"+ChatColor.RED+" et "+ChatColor.GOLD+s+" secondes");
				return true;
			}
		}
		return super.ItemUse(item, gameState);
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (victim == owner) {
			if (isCanRespawn()) {
				owner.sendMessage("Vous venez de vous faire tuez pour la première fois vous venez donc de réssucité");
				
				GameListener.RandomTp(owner, gameState);
				owner.sendMessage("Vous venez d'être TP aléatoirement");
				owner.setHealth(getMaxHealth());
				setCanRespawn(false);
			} else {
				gameState.getInObiPlayers().clear();
			owner.getInventory().removeItem(Items.getObi());
			owner.getInventory().removeItem(Items.getTroisièmeOeil());
				}
			}
		if (victim != owner && killer == owner){
			if (gameState.getInGamePlayers().contains(victim)) {
				if (gameState.getPlayerRoles().containsKey(victim)) {
					RoleBase r = gameState.getPlayerRoles().get(victim);
					if (r.type == Roles.Gyutaro) {
						diegyutaro = true;
						owner.sendMessage(ChatColor.GOLD+"Gyutaro "+ChatColor.GRAY+"est mort définitivement ce qui viens de vous octroyez l'effet: "+ChatColor.RED+"résistance 1 permanent");
					}
					if (r.type == Roles.Tanjiro || r.type == Roles.Tengen || r.type == Roles.Inosuke || r.type == Roles.ZenItsu || r.type == Roles.Nezuko) {
						if (dietizt == false) {
							dietizt = true;
							owner.sendMessage(ChatColor.WHITE+"Suite à la mort de: "+ChatColor.GOLD+ victim.getName() + ChatColor.WHITE+" qui possédait le rôle: "+ChatColor.GOLD+ r.type + ChatColor.WHITE+" vous perdez donc le fait d'avoir weakness 1 proche de certain rôle");
						}
					}
				}
			}
		}
		super.PlayerKilled(killer, victim, gameState);
	}

	private String a() {
		return diegyutaro ? "Mort" : "Vivant"; 
	}
	@Override
	public int UpdateScoreboard(Objective objective, int i) {
    	objective.getScore("Gyutaro " + this.a()).setScore(i);
		return super.UpdateScoreboard(objective, i);
	}

}