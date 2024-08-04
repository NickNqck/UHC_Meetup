package fr.nicknqck.roles.ds.slayers.pillier;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.demons.lune.Doma;
import fr.nicknqck.roles.ds.demons.lune.Kokushibo;
import fr.nicknqck.utils.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Sanemi extends PillierRoles {

	public Sanemi(Player player) {
		super(player);
		setCanuseblade(true);
	}
	@Override
	public Roles getRoles() {
		return Roles.Sanemi;
	}
	@Override
	public void RoleGiven(GameState gameState) {
		givePotionEffet(owner, PotionEffectType.SPEED, Integer.MAX_VALUE, 1, true);
	}
	@Override
	public String[] Desc() {
		return AllDesc.Sanemi;
	}
	private boolean giveforce = false;
	private boolean giveresi = false;
	private boolean killkoku = false;
	private int marquecooldown = 0;
	@Override
	public void resetCooldown() {
		dstpcooldown = 0;
		marquecooldown = 0;
	}
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(Items.getLamedenichirin());
	}
	@Override
	public void Update(GameState gameState) {
		if (owner.getItemInHand().isSimilar(Items.getSlayerMark())) {
			sendActionBarCooldown(owner, marquecooldown);
		}
		if (killkoku) {
			if (marquecooldown >= 1) {
				marquecooldown--;
			}
			if (marquecooldown == 60*5) {
				owner.sendMessage("Les effets de votre marque s'estompe...");
				if (giveforce) {
					giveforce = false;
				}
				if (giveresi) {
					addresi(-20);
					giveresi = false;
				}
			}
		}
		if (dstpcooldown >= 1) {
			dstpcooldown--;
		}else if (dstpcooldown == 0) {
			owner.sendMessage("§6/ds tp <joueur>§7 est à nouveau utilisable !");
			dstpcooldown--;
		}
		super.Update(gameState);
	}
	@Override
	public void onDSCommandSend(String[] args, GameState gameState) {
		if (args[0].equalsIgnoreCase("tp")) {
			if (args.length == 2 && args[1] != null) {
				Player target = Bukkit.getPlayer(args[1]);
				if (target != null) {
					if (dstpcooldown <= 0 && dstpuse < 2) {
						if (target.getWorld().equals(Bukkit.getWorld("world"))) {
							Location loc = GameListener.generateRandomLocation(gameState, target.getWorld());
							target.teleport(loc);
							target.sendMessage("§7Vous avez été téléporté par§a Sanemi");
							dstpuse++;
							dstpcooldown+=60*5;
							owner.sendMessage("§7§l"+target.getName()+"§7 a été téléporté en§f x:§c "+loc.getBlockX()+"§7,§f y:§c "+loc.getBlockY()+"§7,§f z:§c "+loc.getBlockZ());
							owner.sendMessage("§7Il vous reste§6 "+(2 - dstpuse )+"§7 utilisation du§6 /ds tp <joueur>");
						}else {
							owner.sendMessage("§7Impossible de trouver le joueur visée !");
						}
					}else {//else du dstpcooldown && dstpuse
						if (dstpcooldown > 0) {
							sendCooldown(owner, dstpcooldown);
						}
						if (dstpuse >= 2) {
							owner.sendMessage("§cVous avez atteind le nombre maximum d'utilisation de cette commande !");
						}
					}
				} else {
					if (args[1].equalsIgnoreCase("all")) {
						if (!usetpAll) {
							for (Player p : gameState.getInGamePlayers()) {
								if (p != null && p != owner) {
									if (!gameState.hasRoleNull(p)) {
										if (!(getPlayerRoles(p) instanceof Doma)) {
											if (owner.getWorld().equals(Main.getInstance().gameWorld)) {
												if (owner.getWorld().equals(p.getWorld())) {
													Location loc = GameListener.generateRandomLocation(gameState, owner.getWorld());
													p.teleport(loc);
													p.sendMessage("§7Vous avez été téléporté par§a Sanemi");
													usetpAll = true;
												}
											}else {
												owner.sendMessage("§cPouvoir inutilisable actuellement !");
											}
										}
									}
								}
							}
						}else {
							owner.sendMessage("§cVous avez atteint le nombre maximum d'utilisation de cette commande !");
						}						
					}
				}
			}else {
				owner.sendMessage("§cVeuiller ciblé un joueur !");
			}
		}
		super.onDSCommandSend(args, gameState);
	}
	boolean usetpAll = false;
	int dstpcooldown = 0;
	int dstpuse = 0;
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(Items.getSlayerMark())) {
			if (killkoku) {
				if (marquecooldown <= 0) {
					int rdm = RandomUtils.getRandomInt(0, 1);
					if (rdm < 0.5) {
						owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*120, 0, false, false));
						if (!giveforce) {
							giveforce = true;
						}
						owner.sendMessage("Votre marque des§a Pourfendeurs de démons§r vous à donnez l'effet§c force 1§r pendant 2 minutes");
                    } else {
						owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*120, 0, false, false));
						if (!giveresi) {
							addresi(20);
							giveresi = true;
						}
						owner.sendMessage("Votre marque des§a Pourfendeurs de démons§r vous à donnez l'effet§3 résistance 1§r pendant 2 minutes");
                    }
                    marquecooldown = 60*7;
                }
			}
		}
		return super.ItemUse(item, gameState);
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (killer == owner) {
			if (victim != owner){
				if (gameState.getInGamePlayers().contains(victim)) {
					if (gameState.getPlayerRoles().containsKey(victim)) {
						RoleBase role = gameState.getPlayerRoles().get(victim);
						if (role instanceof Kokushibo && !killkoku) {
							killkoku = true;
							owner.sendMessage(ChatColor.GRAY+"Vous venez de tuez "+ChatColor.GOLD+"Kokushibo "+ChatColor.GRAY+"vous obtenez donc la marque des pourfendeurs ce qui vous donnera force 1 pendant 2 minutes");
							owner.getInventory().addItem(Items.getSlayerMark());
		super.PlayerKilled(killer, victim, gameState);
	}
}
				}
			}
		}
	}
	@Override
	public void onDay(GameState gameState) {
		owner.removePotionEffect(PotionEffectType.SPEED);
		owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
		super.onDay(gameState);
	}
	@Override
	public void onNight(GameState gameState) {
		owner.removePotionEffect(PotionEffectType.SPEED);
		owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false));
		super.onNight(gameState);
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[0];
	}

	@Override
	public String getName() {
		return "Sanemi";
	}
}