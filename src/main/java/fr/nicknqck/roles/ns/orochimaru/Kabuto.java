package fr.nicknqck.roles.ns.orochimaru;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.HubListener;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.OrochimaruRoles;
import fr.nicknqck.utils.GlobalUtils;
import fr.nicknqck.utils.TripleMap;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Kabuto extends OrochimaruRoles implements Listener {
	private TextComponent desc;
	private int ninjutsuCD = 0;
	private final HashMap<Player, RoleBase> edoTensei = new HashMap<>();
	private final HashMap<Player, Location> killLoc = new HashMap<>();
	private boolean mortOrochimaru = false;
	private boolean karinDeath = false;
	private boolean kimimaroDeath = false;
	private boolean jugoDeath = false;
	private final ItemStack dashItem = new ItemBuilder(Material.NETHER_STAR).setName("§aDash").toItemStack();

	public Kabuto(UUID player) {
		super(player);
	}

	@Override
	public void RoleGiven(GameState gameState) {
		setChakraType(Chakras.SUITON);
		Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getPlugin(Main.class), () -> {
			if (!gameState.attributedRole.contains(Roles.Orochimaru)) {
				onOrochimaruDeath(false);
				owner.sendMessage("§5Orochimaru§7 n'étant pas dans la partie vous avez tout de même reçus les bonus dû à sa mort ! (§6/ns me§7)");
			}
		}, 20*5);
		givePotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
		getKnowedRoles().add(Orochimaru.class);
		AutomaticDesc automaticDesc = new AutomaticDesc(this);
		automaticDesc.addEffects(getEffects());
		automaticDesc.setItems(new TripleMap<>(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Soigne quelqu'un en fonction du clique:\n\n" +
				"§7     → Clique droit: En visant un joueur, celà permet de le§d soigner§7 de§c 2"+AllDesc.coeur+"\n\n" +
				"§7     → Clique gauche: Vous§d soigne§7 de§c 2"+AllDesc.coeur)}), "§aNinjutsu Médical", 60*3));
		automaticDesc.addParticularites(
				new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7A la mort de §5Karin§7 votre§a Ninjutsu Médical§d soignera§7 de§c 6"+AllDesc.coeur)}),
				new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7A la mort de§5 Jugo§7 vous obtiendrez un§a dash§7 qui vous propulsera§c 10blocs§7 en avant et infligera§c 2"+AllDesc.coeur+"§7 aux joueurs proche.")})
		);
		this.desc = automaticDesc.getText();
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
		Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
			if (!gameState.attributedRole.contains(Roles.Karin)) {
				onKarinDeath(false);
				owner.sendMessage("§5Karin§7 n'étend pas dans la partie, vous recevez donc les bonus dû à sa mort");
			}
			if (!gameState.attributedRole.contains(Roles.Kimimaro)) {
				onKimimaroDeath(false);
				owner.sendMessage("§5Kimimaro§7 n'étend pas dans la partie, vous recevez donc les bonus dû à sa mort");
			}
			if (!gameState.getAttributedRole().contains(Roles.Sasuke)) {
				onSasukeDeath(false);
				owner.sendMessage("§5Sasuke§7 n'étend pas dans la partie, vous recevez donc les bonus dû à sa mort");
			}
			if (!gameState.getAttributedRole().contains(Roles.Jugo)) {
				owner.sendMessage("§5Jugo§7 n'étend pas dans la partie, vous recevez donc les bonus dû à sa mort");
				onJugoDeath(false);
			}
		}, 20*10);
	}
	@Override
	public Roles getRoles() {
		return Roles.Kabuto;
	}
	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}
	@Override
	public ItemStack[] getItems() {
		List<ItemStack> toReturn = new ArrayList<>();
		toReturn.add(this.NinjutsuMedicalItem());
		if (mortOrochimaru) {
			toReturn.add(this.EdoTenseiItem());
		}
		if (jugoDeath) {
			toReturn.add(this.dashItem);
		}
		return toReturn.toArray(new ItemStack[0]);
	}
	@Override
	public String[] Desc() {
		return new String[] {/*
				AllDesc.bar,
				AllDesc.role+"§5Kabuto",
				AllDesc.objectifteam+"§5Orochimaru",
				"",
				AllDesc.effet,
				"",
				AllDesc.point+"§eSpeed 1§f permanent",
				"",
				AllDesc.items,
				"",
				AllDesc.point+"§aNinjutsu Médical§f: Effectue une action en fonction du clique:",
				"§7     →§f Clique droit: En visant un joueur, celà permet de le soigner de§c 2"+AllDesc.coeur,
				"§7     →§f Clique gauche: Vous soigne de§c 2"+AllDesc.coeur,
				"§4!§cLe cooldown des deux clique est partagé§4!",
				"",
				AllDesc.particularite,
				"",
				"A la mort de§5 Orochimaru§f vous obtenez l'item§5 Edo Tensei§f qui permet de tué une personne que vous avez précédemment tué",
				"",
				AllDesc.bar*/
		};
	}
	@Override
	public TextComponent getComponent() {
		return desc;
	}
	@Override
	public void Update(GameState gameState) {
		if (ninjutsuCD >= 0) {
			ninjutsuCD--;
			if (ninjutsuCD == 0) {
				owner.sendMessage("§7Vous pouvez à nouveau§d soigner§7 quelqu'un.");
			}
		}
	}
	@Override
	public void resetCooldown() {
		ninjutsuCD = 0;
		edoTensei.clear();
	}
	private ItemStack EdoTenseiItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§5Edo Tensei").setLore("§7Hérité de§5 Orochimaru§7 cette technique permet de réssusciter 1 personne que vous avez tuer").toItemStack();
	}
	private ItemStack NinjutsuMedicalItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§aNinjutsu Médical").setLore("§7Permet de vous soignez vous ou un autre joueur").toItemStack();
	}
	@Override
	public void OnAPlayerDie(Player player, GameState gameState, Entity killer) {
		if (edoTensei.containsKey(player)) {
			edoTensei.remove(player, getPlayerRoles(player));
		}
		if (getPlayerRoles(player) instanceof Orochimaru) {
			if (!mortOrochimaru) {
				onOrochimaruDeath(true);
			}
		}
		if (killer.getUniqueId() == owner.getUniqueId() && mortOrochimaru) {
			if (player.getLocation().getWorld().equals(Main.getInstance().getWorldManager().getGameWorld())) {
				killLoc.put(player, player.getLocation());
			} else {
				Location rLoc = new Location(Main.getInstance().getWorldManager().getGameWorld(), 0.0, 75, 0.0, player.getEyeLocation().getYaw(), player.getEyeLocation().getPitch());
				killLoc.put(player, rLoc);
			}
			((CraftPlayer) owner).getHandle().setAbsorptionHearts(((CraftPlayer) owner).getHandle().getAbsorptionHearts()+2.0f);
		}
	}
	private void onOrochimaruDeath(boolean msg) {
		owner.getInventory().removeItem(getItems());
		mortOrochimaru = true;
		giveItem(owner, true, getItems());
		if (msg) {
			owner.sendMessage("§7C'est terrible !§5 Orochimaru§7 est§c mort§7, en son homage vous récupérez sa plus puissante technique, l'§5Edo Tensei§7.");
		}
	}
	@Override
	public Intelligence getIntelligence() {
		return Intelligence.INTELLIGENT;
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (mortOrochimaru) {
			if (item.isSimilar(EdoTenseiItem())) {
				if (killLoc.isEmpty()) {
					owner.sendMessage("§7Il faut avoir tué au moins§n 1 joueurs§7 pour utiliser cette technique");
					return true;
				}
				if (!edoTensei.isEmpty()) {
					owner.sendMessage("§7Vous avez déjà§5 Edo Tensei");
					return true;
				}
				Inventory inv = Bukkit.createInventory(owner, 54, "§5Edo Tensei");
				for (int i = 0; i <= 8; i++) {
					if (i == 4) {
						inv.setItem(i, GUIItems.getSelectBackMenu());
					} else {
						inv.setItem(i, GUIItems.getOrangeStainedGlassPane());
					}
				}
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (killLoc.containsKey(p)) {
						if (owner.getLocation().distance(killLoc.get(p)) <= 50) {
							inv.addItem(new ItemBuilder(GlobalUtils.getPlayerHead(p.getUniqueId())).setName(p.getName()).setLore("§7Cliquez ici pour réssusciter ce joueur !").toItemStack());
						}
					}
				}
				owner.openInventory(inv);
				owner.updateInventory();
			}
		}
		return super.ItemUse(item, gameState);
	}
	@SuppressWarnings("deprecation")
	@Override
	public void onAllPlayerInventoryClick(InventoryClickEvent event, ItemStack item, Inventory inv, Player clicker) {
		if (clicker.getUniqueId() != owner.getUniqueId())return;
		if (inv.getTitle().equals("§5Edo Tensei")) {
			if (item == null) {
				return;
			}
			if (item.getType() == Material.AIR) {
				return;
			}
			if (item.hasItemMeta()) {
				if (item.getItemMeta().hasDisplayName()) {
					Player clicked = Bukkit.getPlayer(item.getItemMeta().getDisplayName());
					event.setCancelled(true);
					if (clicked != null) {
						edoTensei.put(clicked, getPlayerRoles(clicked));
						clicker.closeInventory();
						clicked.sendMessage("§7Vous avez été invoquée par l'§5Edo Tensei");
						clicker.sendMessage("§5Edo Tensei !");
						getPlayerRoles(clicked).setTeam(getPlayerRoles(clicker).getOriginTeam());
						HubListener.getInstance().giveStartInventory(clicked);
						gameState.RevivePlayer(clicked);
						setMaxHealth(getMaxHealth()-4.0);
						clicked.teleport(clicker);
						giveItem(clicked, false, getPlayerRoles(clicked).getItems());
						killLoc.remove(clicked);
						clicked.resetTitle();
						clicked.sendTitle("§5Edo Tensei !", "Vous êtes maintenant dans le camp "+getTeam(clicked).name());
						
					}
				}
			}
		}
	}
	@Override
	public String getName() {
		return "Kabuto";
	}
	@EventHandler
	private void onEndGame(EndGameEvent event) {
		HandlerList.unregisterAll(this);
	}
	@EventHandler
	private void onUHCDeath(UHCDeathEvent event) {
		if (event.getRole() != null) {
			if (event.getRole() instanceof Karin && !this.karinDeath) {
				onKarinDeath(true);
			}
			if (event.getRole() instanceof Kimimaro && !this.kimimaroDeath) {
				onKimimaroDeath(true);
			}
			if (event.getRole() instanceof Sasuke) {
				Sasuke role = (Sasuke) event.getRole();
				if (!role.isMortOrochimaru()) {
					onSasukeDeath(true);
				}
			}
			if (event.getRole() instanceof Jugo) {
				onJugoDeath(true);
			}
		}
	}
	@EventHandler
	private void onInteract(PlayerInteractEvent event) {
		if (event.getItem() != null) {
			Player player = event.getPlayer();
			if (player.getUniqueId() == getPlayer()) {
				if (event.getItem().isSimilar(NinjutsuMedicalItem())) {
					event.setCancelled(true);
					if (ninjutsuCD > 0) {
						sendCooldown(owner, ninjutsuCD);
						return;
					}
					if (event.getAction().name().contains("LEFT")) {
						if (!mortOrochimaru) {
							ninjutsuCD = 60*2;
						}else {
							ninjutsuCD = 60;
						}
						player.sendMessage("§dSoins !");
						Heal(owner, this.karinDeath ? 10 : 4);
					} else {
						Player target = getTargetPlayer(owner, 30);
						if (target == null) {
							owner.sendMessage("§cIl faut viser un joueur !");
							return;
						}
						Heal(target, this.karinDeath ? 10 : 4);
						player.sendMessage("§dSoins !");
						target.sendMessage("§5Kabuto§f vous à§d soigner§f !");
						if (!mortOrochimaru) {
							ninjutsuCD = 60*2;
						}else {
							ninjutsuCD = 60;
						}
					}
				}
				if (event.getItem().isSimilar(this.dashItem)) {
					player.setVelocity(player.getEyeLocation().getDirection().normalize().multiply(5));
					new BukkitRunnable() {
						private int tick = 19;
						@Override
						public void run() {
							tick--;
							for (Block block : getSurroundingBlocks(player)) {
								if (block.getType() != Material.BEDROCK && block.getType() != Material.BARRIER) {
									Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
										block.setType(Material.AIR);
									});
								}
							}
							if (tick == 0) {
								cancel();
							}
						}
					}.runTaskTimerAsynchronously(Main.getInstance(), 1, 0);
					event.setCancelled(true);
				}
			}
		}
	}
	private List<Block> getSurroundingBlocks(Player player) {
		List<Block> blocks = new ArrayList<>();
		Location playerLocation = player.getLocation();

		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				Block block = playerLocation.clone().add(x, 0, z).getBlock();
				Block block2 = playerLocation.clone().add(x, 1, z).getBlock();
				Block block3 = playerLocation.clone().add(x, 2, z).getBlock();
				blocks.add(block);
				blocks.add(block2);
				blocks.add(block3);
			}
		}

		return blocks;
	}
	private void onKarinDeath(boolean msg) {
		this.karinDeath = true;
		if (msg) {
			Player owner = Bukkit.getPlayer(getPlayer());
			if (owner != null) {
				owner.sendMessage("§5Karin§7 est malheuresement morte, celà vous a donnez des idées pour votre§a Ninjutsu Médical§7 il§d soignera§7 à partir de maintenant§c 5"+AllDesc.coeur+"§7 au lieu de§c 2§7.");
			}
		}
	}
	private void onKimimaroDeath(boolean msg) {
		Player owner = Bukkit.getPlayer(getPlayer());
		if (owner == null)return;
		this.kimimaroDeath = true;
		giveItem(owner, false, new ItemBuilder(Material.DIAMOND_SWORD).setName("§fManipulation des os").addEnchant(Enchantment.DAMAGE_ALL, 4).setUnbreakable(true).setDroppable(false).toItemStack());
		if (msg) {
			owner.sendMessage("§5Kimimaro§7 est mort, sa vie n'aura pas été inutile, vous récupérez sa capaciter de la §n§fManipulation des os");
		}
	}
	private void onSasukeDeath(boolean msg) {
		Player owner = Bukkit.getPlayer(getPlayer());
		setMaxHealth(getMaxHealth()+4.0);
		if (owner != null) {
			owner.setMaxHealth(getMaxHealth());
			owner.setHealth(owner.getHealth()+4.0);
			if (msg) {
				owner.sendMessage("§7Cette saleté de§5 Sasuke§7 est mort, malgré ceci sa vie n'a pas été inutile, sa grande vitalité vous à inspirez et vous a donnez§a +§c2"+AllDesc.coeur+"§7 permanent");
			}
		}
	}
	private void onJugoDeath(boolean msg) {
		this.jugoDeath = true;
		Player owner = Bukkit.getPlayer(getPlayer());
		if (owner != null) {
			giveItem(owner, false, this.dashItem);
			if (msg) {
				owner.sendMessage("§5Jugo§7 est malencontreusement mort, heureusement que vous aviez eu le temp de faire des expériences sur lui, vous avez§c récupérer§7 sa faculté de§a Dash");
			}
		}
	}
}