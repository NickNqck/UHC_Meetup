package fr.nicknqck.roles.aot.titanrouge;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.aot.soldats.Soldat;
import fr.nicknqck.roles.aot.titans.TitanListener;
import fr.nicknqck.roles.aot.titans.Titans;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.RandomUtils;

public class TitanBestial extends RoleBase{

	public TitanBestial(Player player, Roles roles) {
		super(player, roles);
		owner.sendMessage(Desc());
		canShift = true;
		gameState.GiveRodTridi(owner);
		TitanListener.getInstance().setBestial(owner.getUniqueId());
		giveItem(owner, true, Titans.Bestial.getTitan().Items());
	}
	@Override
	public String[] Desc() {
		gameState.sendTitansList(owner);
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"Titan Bestial",
				"",
				AllDesc.items,
				"",
				AllDesc.point+"§6§lTransformation§r: Vous transforme en Titan Bestial ce qui vous donne "+AllDesc.Force+" 1, "+AllDesc.Resi+" 1 ainsi que 3"+AllDesc.coeur+" permanent pendant le reste de la transformation, également vous donnerez également l'effet "+AllDesc.Force+" 1 au§c titan rouge§r (transformé) étant à moins de 20blocs de vous",
				"",
				AllDesc.commande,
				"",
				AllDesc.point+"§6/aot cri§f: Transforme tout les Soldats simple (sauf ceux du Bataillon d'exploration) ayant passé au moins 5mins proche de vous (20blocs) (aléatoirement) en Grand Titan ou Petit Titan",
				AllDesc.bar,
				"§c§lATTENTION§f vous possédez la liste des§c Titans rouge§f mais vous n'apparaissez pas dedans"
		};
	}
	private java.util.List<Player> canBeTransformed = new ArrayList<>();
	private boolean cri = false;
	@Override
	public void onAotCommands(String arg, String[] args, GameState gameState) {
		if (args[0].equalsIgnoreCase("cri")) {
			if (!cri) {
				owner.sendMessage("§7Votre cri s'apprête à retentir...");
				for (Player p : canBeTransformed) {
					if (getPlayerRoles(p).type.equals(Roles.Soldat)) {
						Soldat soldat = (Soldat) getPlayerRoles(p);
						if (soldat.form == Soldat.kit.Brigade || soldat.form.equals(Soldat.kit.Garnison)) {
							p.sendMessage("§7Vous sentez l'alcool vous montez à la tête...");
							givePotionEffet(p, PotionEffectType.CONFUSION, 20*15, 2, true);
							Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
								int r = RandomUtils.getRandomInt(0, 1);
								if (r < 1) {
									givePotionEffet(p, PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, true);
									soldat.setResi(20);
									TransfoEclairxMessage(p);
								}else {
									givePotionEffet(p, PotionEffectType.SPEED, Integer.MAX_VALUE, 1, true);
									TransfoEclairxMessage(p);
								}							
								soldat.setTeam(TeamList.Titan);
								GameListener.detectWin(gameState);
								owner.sendMessage("§c"+p.getName()+"§7 à bien été transformé en Titan...");
								owner.sendMessage("§7Voic les coordonnées de§c "+p.getName()+" x:§c "+p.getLocation().getBlockX()+"§f y:§c "+p.getLocation().getBlockY()+"§f z:§c "+p.getLocation().getBlockZ());
							}, 20*15);
						}
					}
				}
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
					GameListener.SendToEveryone("");
					GameListener.SendToEveryone("§c§lLe Titan Bestial vient de crier !!!");
					GameListener.SendToEveryone("");
					for (Player p : gameState.getOnlinePlayers()) {
						p.playSound(p.getLocation(), "aotmtp.bestialcri", 8, 1);
					}
				}, 20*15);
				cri = true;
				for (Player p : getIGPlayers()) {
					if (!gameState.hasRoleNull(p)) {
						if (getPlayerRoles(p).getTeam().equals(TeamList.Titan)) {
							Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
								DecimalFormat df = new DecimalFormat("0");
								String x = df.format(owner.getLocation().getX());
								String y = df.format(owner.getLocation().getY());
								String z = df.format(owner.getLocation().getZ());
								p.sendMessage("§7Vous avez entendu le cri du§c Titan Bestial§7 au loin, voici ses coordonnées §cx§f: "+x+" y§f: "+y+" z§f: "+z);
							}, 20*15);
						}
					}
				}	
			}else {
				owner.sendMessage("§7Vous avez déjà crié !");
			}
		}
		super.onAotCommands(arg, args, gameState);
	}
	private HashMap<UUID, Integer> timePassed = new HashMap<>();
	@Override
	public void Update(GameState gameState) {
		if (actualTridiCooldown > 0) {
			actualTridiCooldown--;
			if (owner.getItemInHand().isSimilar(gameState.EquipementTridi())) {
				DecimalFormat df = new DecimalFormat("0.0");
			//	sendCustomActionBar(owner, aqua+"Gaz:§c "+df.format(gazAmount)+"%"+aqua+" Cooldown:§6 "+actualTridiCooldown+"s");
				sendCustomActionBar(owner, "Gaz restant§8»"+gameState.sendGazBar(gazAmount, 2)+"§7("+aqua+df.format(gazAmount)+"%§7), Cooldown:§b "+cd(actualTridiCooldown));
			}
		}else if (actualTridiCooldown == 0){
			owner.sendMessage("§7§l"+gameState.EquipementTridi().getItemMeta().getDisplayName()+"§7 utilisable !");
			actualTridiCooldown--;
		}
		if (actualTridiCooldown <= 0) {
			if (owner.getItemInHand().isSimilar(gameState.EquipementTridi())) {
				DecimalFormat df = new DecimalFormat("0.0");
				sendCustomActionBar(owner, aqua+"Gaz:§c "+df.format(gazAmount)+"% "+"§7§lArc Tridimentionnel§r:§6 Utilisable");
			}
		}
		if (owner.getWorld().equals(Main.getInstance().gameWorld)){
			for (Player p : Loc.getNearbyPlayersExcept(owner, 30)) {
				if (!gameState.hasRoleNull(p)) {
					if (getPlayerRoles(p).type == Roles.Soldat) {
						if (!canBeTransformed.contains(p)) {
							if (timePassed.containsKey(p.getUniqueId())) {
								int time = timePassed.get(p.getUniqueId());
								if (time != 60*5) {
									timePassed.remove(p.getUniqueId(), time);
									timePassed.put(p.getUniqueId(), time+1);
								}else {
									canBeTransformed.add(p);
									owner.sendMessage("Vous êtes resté asser longtemp proche de "+p.getName()+" pour qu'il sois transformable par votre cri");
								}
							}else {
								timePassed.put(p.getUniqueId(), 1);
							}
						}
					}
				}
			}
		}		
		if (isTransformedinTitan) {
		if (owner.getWorld().equals(Main.getInstance().gameWorld)) {
			for (Player p : gameState.getNearbyPlayers(owner, 20)) {
				if (!gameState.hasRoleNull(p)) {
					RoleBase role = gameState.getPlayerRoles().get(p);
					if (role.isTransformedinTitan) {
						if (role.type.equals(Roles.PetitTitan) || role.type.equals(Roles.GrandTitan)) {
								role.setForce(20);
								role.givePotionEffet(role.owner, PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
							}
						}
					}
				}
			}
		}
	}
	@Override
	public ItemStack[] getItems() {
		if (Titans.Bestial.getTitan().getOwner() != null && Titans.Bestial.getTitan().getOwner() == owner.getUniqueId()) {
			return Titans.Bestial.getTitan().Items();
		}
		return new ItemStack[0];
	}
	@Override
	public void resetCooldown() {
		
	}

	@Override
	public String getName() {
		return "§cTitan Bestial";
	}
}