package fr.nicknqck.roles.ds.demons.lune;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.ArrowTargetUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.betteritem.BetterItem;
import fr.nicknqck.utils.powers.LocPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class Nakime extends DemonsRoles {

	public Nakime(Player player) {
		super(player);
		if (!gameState.pregenNakime) {
			ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	        Bukkit.dispatchCommand(console, "nakime qF9JbNzW5R3s2ePk8mZr0HaS");
		}
        Main.getInstance().nakime = Bukkit.getWorld("nakime");
	}

	@Override
	public DemonType getRank() {
		return DemonType.LuneSuperieur;
	}

	@Override
	public Roles getRoles() {
		return Roles.Nakime;
	}
	@Override
	public String[] Desc() {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> KnowRole(owner, Roles.Muzan, 20), 20);
		return AllDesc.Nakime;
	}
	private int cooldown =0;
	private int cd = 0;
	@Override
	public void resetCooldown() {
		cooldown = 0;
		cd = 0;
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				BetterItem.of(new ItemBuilder(Material.MAGMA_CREAM).setName("§cCage").addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().setLore("§7"+StringID).toItemStack(), event -> {
					if (owner.getWorld().equals(Main.getInstance().nakime)) {
						if (cd <= 0) {
							for (Player p : Bukkit.getOnlinePlayers()) {
								if (p.getWorld().equals(Main.getInstance().nakime)) {
									if (!gameState.hasRoleNull(p)) {
										if (getPlayerRoles(p).getRoles() != Roles.Nakime) {
											LocPlayer loc = new LocPlayer();
											p.teleport(loc.getRandomPositionRespawn());
											p.sendMessage("§7Vous avez été téléporté de manière aléatoire par§c Nakime");
										}
									}
								}
							}
							cd = 60;
						}else {
							sendCooldown(owner, cd);
							return true;
						}
					}else if (owner.getWorld().equals(Main.getInstance().gameWorld)) {
						if (cooldown <= 0) {
							EnterinNakime();
                        }else {
							sendCooldown(owner, cooldown);
                        }
                        return true;
                    }
					return true;
				}).setDroppable(false).setDespawnable(false).setMovableOther(false).getItemStack()
		};
	}
	@Override
	public void onDSCommandSend(String[] args, GameState gameState) {
		if (args[0].equalsIgnoreCase("return")) {
			if (owner.getWorld().equals(Main.getInstance().nakime)) {
				ejectPlayerinCage();
				owner.sendMessage("Tout les joueurs étant dans votre cage on été éparpiller sur la map");
			}else {
				owner.sendMessage("§7Il faut être dans la§l cage de§c§l Nakime§7 pour faire cette commande !");
			}
		}
	}
	public void EnterinNakime() {
		cooldown =60*10;
		owner.sendMessage("§7Entrée dans la cage !");
		LocPlayer loc = new LocPlayer();
		for (Player p : Loc.getNearbyPlayersExcept(owner, 20)) {
			owner.sendMessage("§7§l"+p.getName()+"§7 est entrée dans votre§c cage§7 !");
			inCage.add(p);
			p.teleport(loc.getRandomPositionStart());
		}
		for (Player p : Loc.getNearbyPlayers(owner, 20)) {
			if (p != owner)return;
			if (Loc.getNearbyPlayers(owner, 20).size() == 1) {
				owner.sendMessage("§7Vous rejoignez votre§c cage de Nakime");
				inCage.add(owner);
				owner.teleport(loc.getRandomPositionStart());
			}
		}
		setResi(20);
		setNoFall(true);
	}
	@Override
	public void OnAPlayerDie(Player player, GameState gameState, Entity killer) {
		super.OnAPlayerDie(player, gameState, killer);
	}
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(getItems());
	}
	List<Player> inCage = new ArrayList<>();
	World actualworld;
	@Override
	public void Update(GameState gameState) {
		actualworld = owner.getWorld();
		if (actualworld.getName().equalsIgnoreCase("nakime")) {
			givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
			givePotionEffet(owner, PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true);
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.getLocation().getY() <= 122) {
					if (p.getWorld().equals(Bukkit.getWorld("nakime"))) {
						LocPlayer locPlayer = new LocPlayer();
						p.teleport(locPlayer.getRandomPositionRespawn());
						p.sendMessage("§7Vous avez été téléporter aléatoirement");
						getPlayerRoles(p).setInvincible(true);
						Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> getPlayerRoles(p).setInvincible(false), 40);
					}
				}
			}
		}else if (actualworld.getName().equalsIgnoreCase("world")) {
			givePotionEffet(owner, PotionEffectType.WEAKNESS, 60, 1, true);
		}
		if (cooldown == 0) {
			cooldown-=1;
			owner.sendMessage("§cCage§7 est maintenant utilisable !");
		}
		if (cooldown > 0) {
			cooldown-=1;
			if (cooldown >= 60*8) {
				int newcooldown = cooldown-60*8;
				if (Loc.getNearestPlayerforNakime(owner, 150, gameState) != null) {
					sendCustomActionBar(owner, "§bTemp restant dans la cage: "+cd(newcooldown)+"§a Slayer§b le plus proche: "+ArrowTargetUtils.calculateArrow(owner, Loc.getNearestPlayerforNakime(owner, 150, gameState).getLocation()));
				}else{
					sendCustomActionBar(owner, "§bTemp restant dans la cage: "+cd(newcooldown));
				}
			}
			if (cooldown == 60*8) {
				ejectPlayerinCage();
			}
		}
		if (cd > 0) {
			cd--;
		}
		if (cd == 0) {
			cd--;
			owner.sendMessage("§c§lTéléportation§c est à nouveau utilisable !");
		}
		super.Update(gameState);
	}
	public void ejectPlayerinCage() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getWorld().equals(Main.getInstance().nakime)) {
				GameListener.RandomTp(p, Main.getInstance().gameWorld);
				p.sendMessage("§7Vous avez été éjecté de la§c cage de Nakime");
				owner.sendMessage(p.getName()+"§7 est sortie de votre cage");
			}
		}
		setResi(0);
		setNoFall(false);
		cooldown = 60*8;
	}
	@Override
	public TeamList getOriginTeam() {
		return TeamList.Demon;
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (victim == owner) {
			ejectPlayerinCage();
		}
		super.PlayerKilled(killer, victim, gameState);
	}

	@Override
	public String getName() {
		return "Nakime";
	}
}