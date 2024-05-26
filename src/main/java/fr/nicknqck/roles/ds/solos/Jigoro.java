package fr.nicknqck.roles.ds.solos;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.particles.MathUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;

public class Jigoro extends RoleBase{
	private boolean Zoneactiver2 = false;
	private boolean killzen = false;
	private boolean killkai = false;
	private int cooldowndegat = 0;
	private int cooldownzone = 0;
	private int cooldownpremiermouvement = 0;
	private int cooldownquatriememouvement = 0;
	private boolean killtwo = false;
	enum Status {
		Min1,
		Min2,
		Min3
	}
	private Status status = null;
	public Jigoro(Player player, Roles roles) {
		super(player, roles);
		for (String desc : AllDesc.Jigoro) owner.sendMessage(desc);
		this.setCanUseBlade(true);
		this.setResi(20);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			for (Player p : getIGPlayers()) {
				if (getPlayerRoles(p).type == Roles.ZenItsu) {
					owner.sendMessage("La personne possédant le rôle de§a ZenItsu§r est:§a "+p.getName());
				}
				if (getPlayerRoles(p).type == Roles.Kaigaku) {
					owner.sendMessage("La personne possédant le rôle de§c Kaigaku§r est:§c "+p.getName());
				}
			}
		}, 20);
		setLameIncassable(owner, true);
	}
	@Override
	public void resetCooldown() {
		cooldowndegat = 0;
		cooldownpremiermouvement = 0;
		cooldownquatriememouvement = 0;
		cooldownzone = 0;
	}
	boolean giveforce = false;
	@Override
	public String[] Desc() {
		KnowRole(owner, Roles.Kaigaku, 1);
		KnowRole(owner, Roles.ZenItsu, 1);
		return AllDesc.Jigoro;
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				Items.getJoueurZoneDeFoudre()
		};
	}
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(Items.getLamedenichirin());
		owner.getInventory().addItem(Items.getJoueurZoneDeFoudre());
		super.GiveItems();
	}
	@Override
	public void Update(GameState gameState) {
		if (owner.getItemInHand().isSimilar(Items.getJoueurZoneDeFoudre())) {
			sendActionBarCooldown(owner, cooldownzone);
		}
		givePotionEffet(owner, PotionEffectType.DAMAGE_RESISTANCE, 80, 1, true);
		if (!owner.hasPotionEffect(PotionEffectType.SPEED)) {
			givePotionEffet(owner, PotionEffectType.SPEED, Integer.MAX_VALUE, 1, true);
		}
		if (killzen && !killtwo) {
			if (!gameState.nightTime) {
				givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 80, 1, true);
			}
		}
		if (killkai && !killtwo) {
			if (gameState.nightTime) {
				givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 80, 1, true);
			}
		}
		if (killzen && killkai && !killtwo) {
			killtwo = true;
			owner.sendMessage("On dirait que vous avez réussit à tuer vos deux disciple, vous êtes vraiment un être cruel !");
		}
		if (killtwo) {
			owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 999999, 0, false, false));
		}
		if (cooldownpremiermouvement >= 1) {
			cooldownpremiermouvement--;
		}
		if (cooldownquatriememouvement >= 1) {
			cooldownquatriememouvement--;
		}
		if (cooldownzone >= 1) {
			cooldownzone--;
		}
		if (cooldowndegat >= 1) {
			cooldowndegat--;
		}
		if (status == Status.Min1) {
			owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*3, 2, false, false), true);
		}
		
		if (cooldownpremiermouvement == 60*4 && status == Status.Min1) {
			status = Status.Min2;
		}
		if (status == Status.Min2 && cooldownpremiermouvement <= 60) {
			status = null;
		}
		if (Zoneactiver2) {
			MathUtil.sendCircleParticle(EnumParticle.VILLAGER_ANGRY, owner.getLocation(), 10, 15);
			for (Player p : Loc.getNearbyPlayersExcept(owner, 15)) {
				if (!gameState.hasRoleNull(p)) {
					if (gameState.getInGamePlayers().contains(p)) {
						if (cooldowndegat <= 0 && p != owner) {
							if (p.getHealth() > 4.0) {
							p.setHealth(p.getHealth() - 4.0);
							} else {
							p.setHealth(0.5);
							}
							 owner.sendMessage("Vous avez foudroyé: "+ ChatColor.GOLD + p.getName());
							 p.sendMessage("§6Jigoro§7 vous à fait perdre 2"+AllDesc.coeur+"§7 suite à votre§e Foudroyage");
							 p.getWorld().strikeLightningEffect(p.getLocation());
							 Location loc = p.getLocation();
							 gameState.spawnLightningBolt(loc.getWorld(), loc);
							 Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
								 cooldowndegat = 4;
							 }, 1);
					    }
					}
				}
			}	
		if (cooldownzone == 60*10) {
			Zoneactiver2 = false;
			owner.sendMessage("§7Votre zone c'est désactiver");
		}
		}
		super.Update(gameState);
		if (owner.getItemInHand().isSimilar(Items.getJoueurZenItsuSpeed())) {
			sendActionBarCooldown(owner, cooldownpremiermouvement);
		}
		if (owner.getItemInHand().isSimilar(Items.getSoufleFoudre4iememouvement())) {
			sendActionBarCooldown(owner, cooldownquatriememouvement);
		}
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (killer == owner) {
			if (victim != owner){
				if (gameState.getInGamePlayers().contains(victim)) {
					if (gameState.getPlayerRoles().containsKey(victim)) {
						RoleBase role = gameState.getPlayerRoles().get(victim);
						if (role.type == Roles.ZenItsu && !killzen) {
							giveItem(owner, false, Items.getJoueurZenItsuSpeed());
							owner.sendMessage(ChatColor.GRAY+"Vous venez de tuez "+ChatColor.GOLD+"Zen'Itsu "+ChatColor.GRAY+"vous obtenez donc "+ChatColor.RED+"force 1 le jour"+ChatColor.GRAY+", ainsi que l'accès au: "+ChatColor.GOLD+"Premier Mouvement du Soufle de la Foudre"+ChatColor.GRAY+" qui vous donnera Speed 3 pendant 1 minutes");
							killzen = true;
							if (!giveforce) {
								addforce(20);
							}
						}
						if (role.type == Roles.Kaigaku && !killkai) {
							killkai = true;
							giveItem(owner, false, Items.getSoufleFoudre4iememouvement());
							owner.sendMessage(ChatColor.GRAY+"Vous venez de tuez "+ChatColor.GOLD+"Kaigaku "+ChatColor.GRAY+"vous obtenez donc "+ChatColor.RED+"force 1 la nuit"+ChatColor.GRAY+", ainsi que l'accès au: "+ChatColor.GOLD+"Quatrième Mouvement du Soufle de la Foudre"+ChatColor.GRAY+" qui vous téléportera à la personne la plus proche que vous pouvez voir dans un rayon maximum de 30 blocs");
							if (!giveforce) {
								addforce(20);
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
		if (item.isSimilar(Items.getJoueurZoneDeFoudre())) {
			if (cooldownzone <= 0) {
				Zoneactiver2 = true;
				cooldownzone = 60*10+15;
				owner.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*15, 0, false, false));
				owner.sendMessage("Activation de votre Zone");
			}  else {
				sendCooldown(owner, cooldownzone);
			}
		}
		if (item.isSimilar(Items.getSoufleFoudre4iememouvement())) {
			if (!isPowerEnabled()) {
				owner.sendMessage(ChatColor.RED+"Votre pouvoir est désactivé.");
				return false;
			}
			if (cooldownquatriememouvement <= 0) {				
				Player target = getRightClicked(30, 1);
				if (target == null) {
					owner.sendMessage("§cVeuiller viser un joueur");
				} else {
					Location loc = target.getLocation();
					System.out.println(target.getEyeLocation());
					loc.setX(loc.getX()+Math.cos(Math.toRadians(-target.getEyeLocation().getYaw()+90)));
					loc.setZ(loc.getZ()+Math.sin(Math.toRadians(target.getEyeLocation().getYaw()-90)));
					loc.setPitch(0);
					System.out.println(loc);
					owner.teleport(loc);
					gameState.spawnLightningBolt(target.getWorld(), loc);
					if (target.getHealth() > 4.0) {
						target.setHealth(target.getHealth() - 4.0);
					} else {
						target.setHealth(0.5);
					}
					owner.sendMessage("Exécution du "+owner.getItemInHand().getItemMeta().getDisplayName());
					cooldownquatriememouvement = 60*3;
				}
				} else {
					sendCooldown(owner, cooldownquatriememouvement);
				}
		}
		if (item.isSimilar(Items.getJoueurZenItsuSpeed())) {
			if (!isPowerEnabled()) {
				owner.sendMessage(ChatColor.RED+"Votre pouvoir est désactivé.");
				return false;
			}
			if (cooldownpremiermouvement <= 0) {
					owner.sendMessage("Vous venez d'utiliser votre Speed");
					Location ploc1 = owner.getLocation();
					gameState.spawnLightningBolt(owner.getWorld(), ploc1);
					cooldownpremiermouvement = 60*5;
					owner.removePotionEffect(PotionEffectType.SPEED); owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60, 2, false, false));
			} else {
				sendCooldown(owner, cooldownpremiermouvement);
			}
		}
		return super.ItemUse(item, gameState);
	}
}