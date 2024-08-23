package fr.nicknqck.roles.ds.demons.lune;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.demons.Muzan;
import fr.nicknqck.roles.ds.slayers.Tanjiro;
import fr.nicknqck.scenarios.impl.FFA;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.betteritem.BetterItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class Kokushibo extends DemonsRoles {
	private int itemcooldown = 0;
	private int regencooldown;
	public boolean solo;
	public Kokushibo(UUID player) {
		super(player);
		regencooldown = 15;
		this.setCanuseblade(true);
		orginalMaxHealth = owner.getMaxHealth();
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> getKnowedRoles().add(Muzan.class), 20);
		setLameIncassable(owner, true);
		solo = false;
		killtanjiro = false;
	}
	@Override
	public TeamList getOriginTeam() {
		return TeamList.Demon;
	}
	@Override
	public DemonType getRank() {
		return DemonType.LuneSuperieur;
	}

	@Override
	public Roles getRoles() {
		return Roles.Kokushibo;
	}
	@Override
	public String[] Desc() {
		if (!gameState.demonKingTanjiro) {
			return AllDesc.Kokushibo;
		}else {
			if (getOriginTeam().equals(TeamList.Solo)) {
				return AllDesc.KokushiboSolo;
			}else {
				if (getOriginTeam() == TeamList.Demon) {
					return new String[] {
							AllDesc.bar,
							"§lRôle: §r§cKokushibo",
							"",
							ChatColor.BOLD+"Capacité: ",
							"",
							(ChatColor.DARK_GRAY+" • " +ChatColor.WHITE+"Vous possédez la régénération naturel à hauteur de 1 demi"+AllDesc.coeur+" toute les 15 secondes"),
							"",
							ChatColor.BOLD+"Effet: ",
							"",
							(ChatColor.DARK_GRAY+" • " +AllDesc.Speed+" 1 permanent, "+AllDesc.Force+" 1 la "+AllDesc.nuit),
							"",
							ChatColor.BOLD+"Items: ",
							"",
							(ChatColor.DARK_GRAY+" • " +ChatColor.GOLD+ChatColor.BOLD+"Pouvoir Sanginaire: "+ChatColor.WHITE+"Vous donne une épée en diamant nommé "+ChatColor.RED+"Epée de la nuit§r qui est Sharpness 4, et qui remplace instantanément le "+AllDesc.jour+" par la "+AllDesc.nuit),
							"",
							AllDesc.point+"§lBoule d'énergie§r: Prépare une explosion faisant 1"+AllDesc.coeur+" de dégat d'explosion (en plus de votre coup)","",
							(ChatColor.WHITE+""+ChatColor.BOLD+"Amélioration: "),
							"",
							(ChatColor.DARK_GRAY+" • " +ChatColor.WHITE+"Si vous parvenez à tuez un joueur vous obtiendrez 1/2"+AllDesc.coeur+" permanent"),
				            "",
							AllDesc.bar
					};
				}else {
					return new String[] {
							AllDesc.bar,
							AllDesc.role+"§aKokushibo",
							"",
							AllDesc.effet,
							"",
							AllDesc.point+AllDesc.Speed+"§b 1§f le "+AllDesc.jour+" ainsi que "+AllDesc.Force+"§c 1§f la "+AllDesc.nuit,
							"",
							AllDesc.bar
					};
				}
			}
		}
	}
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(Items.getPouvoirSanginaire());
		owner.getInventory().addItem(Items.getLamedenichirin());
	}
	private int cd = -1;
	@Override
	public void resetCooldown() {
		cd = 0;
	}
	@Override
	public ItemStack[] getItems() {
		if (killtanjiro) {
			if (getOriginTeam().equals(TeamList.Demon)) {
				return new ItemStack[] {
						BetterItem.of(new ItemBuilder(Material.NETHER_STAR).addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().setName("§f§lBoule d'énergie").setLore("§7» Crée une explosion sur le prochaine adversaire que vous taperez","§7"+StringID).toItemStack(), event -> {
							if (cd <= 0) {
								cd = 120;
								owner.sendMessage("§7Vous avez accumulé asser d'énergie, votre§l Boule d'énergie§7 est paré pour le combat...");
							}else {
								sendCooldown(owner, cd);
							}
							return true;
						}).setDespawnable(true).setDroppable(false).setMovableOther(false).getItemStack()
				};
			} else {
				return new ItemStack[] {
						BetterItem.of(new ItemBuilder(Material.NETHER_STAR).addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().setName("§f§lBoule d'énergie").setLore("§7» Crée une explosion sur le prochaine adversaire que vous taperez","§7"+StringID).toItemStack(), event -> {
							if (cd <= 0) {
								cd = 100;
								owner.sendMessage("§7Vous avez accumulé asser d'énergie, votre§l Boule d'énergie§7 est paré pour le combat...");
							}else {
								sendCooldown(owner, cd);
							}
							return true;
						}).setDespawnable(true).setDroppable(false).setMovableOther(false).getItemStack()
				};
			}
		}
		return new ItemStack[] {
			Items.getPouvoirSanginaire()	
		};
	}
	@Override
	public void Update(GameState gameState) {
		if (owner.getItemInHand().isSimilar(Items.getPouvoirSanginaire())) {
			sendActionBarCooldown(owner, itemcooldown);
		}
		if (!FFA.getFFA()) {
			if (!solo) {
				if (getOriginTeam().equals(TeamList.Demon)) {
					if (!gameState.nightTime) {
						givePotionEffet(owner, PotionEffectType.SPEED, 60, 1, true);
						} else {
						givePotionEffet(owner, PotionEffectType.SPEED, 60, 1, true);
						givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
					}
				}else if (getOriginTeam().equals(TeamList.Slayer)) {
					if (gameState.nightTime) {
						givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 100, 1, true);
					}else {
						givePotionEffet(owner, PotionEffectType.SPEED, 100, 1, true);
					}
				}
			}else {
				givePotionEffet(owner, PotionEffectType.SPEED, 60, 1, true);
				givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
				if (!owner.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE) && getResi() != 0) {
					setResi(0);
				}
			}
		}else {
			if (!gameState.nightTime) {
				givePotionEffet(owner, PotionEffectType.SPEED, 60, 1, true);
				} else {
				givePotionEffet(owner, PotionEffectType.SPEED, 60, 1, true);
				givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
			}
		}
		if (itemcooldown >= 1) {
			itemcooldown--;
		}
		if (regencooldown == 0) {
			Heal(owner, 1);
			regencooldown = 15;
		}
		if (regencooldown>=1) regencooldown-=1;
		super.Update(gameState);
	}
public boolean killtanjiro;
	@Override
	public void OpenFormInventory(GameState gameState) {
		if (!killtanjiro) {
			Inventory inv = Bukkit.createInventory(owner, 9, "§cChoix de§l Kokushibo");
			inv.setItem(2, new ItemBuilder(Material.INK_SACK).setDyeColor(DyeColor.PURPLE).setName("§aSlayer").setLore(
					"§7En faisant ce choix là vous entrerez dans le camp des§a Slayers",
					"§7 vous perdrez vos avantage de rôle solo (lame incassable, les 15"+AllDesc.coeur+"§7 et les effets surpuissant)",
					"§7A la place vous aurez: "+AllDesc.Speed+"§b 1§7 le "+AllDesc.jour+"§7 et "+AllDesc.Force+"§c 1§7 la "+AllDesc.nuit).toItemStack());
			inv.setItem(4, new ItemBuilder(Material.INK_SACK).setDyeColor(DyeColor.ORANGE).setName("§cDémons").setLore(
					"§7En faisant ce choix là vous reviendrez dans le camp des§c Démons",
					"§7Vous retomberer au point de vie d'avant votre transformation en rôle§e Solo",
					"§7Également vos effet deviendront: "+AllDesc.Speed+"§b 1§7 permanant ainsi que "+AllDesc.Force+"§c 1§7 la "+AllDesc.nuit,
					"§7Pour finir vous gagnerez la§f§l Boule d'énergie§7 de§c Demon King Tanjiro").toItemStack());
			inv.setItem(6, new ItemBuilder(Material.INK_SACK).setDyeColor(DyeColor.BLUE).setName("§eSolo").setLore(
	/*Ligne 1*/		"§7En faisant ce choix là vous déciderez de rester§e Solo",
	/*Ligne 2*/		"§7Vous n'y gagnerez que la§f§l Boule d'énergie§7 de§c Demon King Tanjiro").toItemStack());
			owner.openInventory(inv);
		}else {
			owner.sendMessage("§7Commande inutilisable !");
		}
		super.OpenFormInventory(gameState);
	}
	@Override
	public void FormChoosen(ItemStack item, GameState gameState) {
		if (!killtanjiro) {
			owner.sendMessage("§7Action impossible !");
			return;
		}
		if (item == null)return;
		if (item.getType() == Material.AIR)return;
		if (!item.hasItemMeta())return;
		if (!item.getItemMeta().hasDisplayName())return;
		if (!item.getItemMeta().hasLore())return;
		String itemName = item.getItemMeta().getDisplayName();
		if (itemName.equals("§aSlayer")) {
			setLameIncassable(owner, false);
			setMaxHealth(getMaxHealth()-10.0);
			solo = false;
			setTeam(TeamList.Slayer);
			owner.sendMessage("§7Vous avez choisis de passer dans le camp des§a Slayers§7, cependant attention seul vous êtes au courant de votre changement de camp");
		}
		if (itemName.equals("§cDémons")) {
			setMaxHealth(this.orginalMaxHealth);
			solo = false;
			setTeam(TeamList.Demon);
			giveItem(owner, false, getItems());
		}
		if (itemName.equals("§eSolo")){
			giveItem(owner, true, getItems());
		}
		super.FormChoosen(item, gameState);
	}
	public double orginalMaxHealth;

	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (killer == owner) {
			if (victim != owner) {
				if (!solo) {
					if (getMaxHealth() < 30.0) {
						owner.sendMessage("Vous venez de tuez: "+ChatColor.BOLD+victim.getName()+"§r vous gagnez donc§c 1 demi-"+AllDesc.coeur+" permanent");
						giveHalfHeartatInt(owner, 1);
					}else {
						owner.sendMessage("Vous venez de tuez: "+ChatColor.BOLD+victim.getName()+"§r vous venez donc de gagner 2"+AllDesc.coeur+" d'absorbtion");
						((CraftPlayer) owner).getHandle().setAbsorptionHearts(((CraftPlayer) owner).getHandle().getAbsorptionHearts()+4);
					}
				}else {
					owner.sendMessage("Vous venez de tuez:§l "+victim.getName()+"§r vous gagnez donc "+AllDesc.Resi+" 1 pendant 3minutes");
					givePotionEffet(owner, PotionEffectType.DAMAGE_RESISTANCE, 20*60*3, 1, true);
					setResi(20);
					if (gameState.getGamePlayer().get(victim.getUniqueId()).getRole() instanceof Tanjiro) {
						owner.sendMessage("§7Vous avez réussis à vaincre cette imposteur de§a Tanjiro§7, vous avez maintenant un choix qui s'offre à vous... (§l/ds role§7)");
						killtanjiro = true;
					}
				}
			}
		}
		super.PlayerKilled(killer, victim, gameState);
	}
	@Override
	public void onDay(GameState gameState) {
		if (getOriginTeam() != TeamList.Slayer) {
			if (owner.getInventory().contains(Items.getkokushibosword())) {
				owner.getInventory().remove(Items.getkokushibosword());
				owner.sendMessage("Vous avez perdu votre§b Épée de la nuit");
			}
		}
		super.onDay(gameState);
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(Items.getPouvoirSanginaire())){
			if (itemcooldown <= 0) {
				owner.sendMessage("Activation de votre:"+ChatColor.BOLD+" Pouvoir Sanginaire");
				gameState.nightTime = true;
				gameState.t = gameState.timeday;
				GameListener.SendToEveryone("§6Kokushibo §rà mis la §9nuit");
				owner.getInventory().addItem(Items.getkokushibosword());
				itemcooldown = gameState.timeday*2;
				for (UUID u : gameState.getInGamePlayers()) {
					Player p = Bukkit.getPlayer(u);
					if (p == null)continue;
					if (gameState.getPlayerRoles().containsKey(p)) {
						gameState.getPlayerRoles().get(p).onNight(gameState);
					}
				}
			} else {
				sendCooldown(owner, itemcooldown);
			}
		}
		return super.ItemUse(item, gameState);
	}

	@Override
	public String getName() {
		return "Kokushibo";
	}
}