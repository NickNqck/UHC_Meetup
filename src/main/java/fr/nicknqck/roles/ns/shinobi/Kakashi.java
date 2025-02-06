package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
import fr.nicknqck.utils.GlobalUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.powers.KamuiUtils;
import fr.nicknqck.utils.powers.KamuiUtils.Users;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Kakashi extends ShinobiRoles {

	public Kakashi(UUID player) {
		super(player);
	}

	@Override
	public void RoleGiven(GameState gameState) {
		super.RoleGiven(gameState);
		setChakraType(Chakras.RAITON);
		if (!gameState.attributedRole.contains(Roles.Obito)) {
			ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
			Bukkit.dispatchCommand(console, "nakime Gh6Iu2YjZl8A9Bv3Tn0Pq5Rm");
		}
		setCanBeHokage(true);
	}

	@Override
	public @NonNull Intelligence getIntelligence() {
		return Intelligence.GENIE;
	}

	@Override
	public GameState.Roles getRoles() {
		return Roles.Kakashi;
	}
	@Override
	public String[] Desc() {
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§aKakashi",
				AllDesc.objectifteam+"§aShinobi",
				"",
				AllDesc.items,
				"",
				AllDesc.point+SharinganItem().getItemMeta().getDisplayName()+"§f: Ouvre un menu ayant plusieurs choix de pouvoir: ",
				"§7     →§a Copie§f: Ouvre un menu permettant de séléctionner un joueur, en le séléctionnant cela crée une§c bar§f de§a 2400 points§f, vous augmentez ces dernier en étant plus ou moins proche de la cible:",
				"§8 -§f 5 blocs§a + 20 points",
				"§8 -§f 10 blocs§6 + 10 points",
				"§8 -§f 20 blocs§c + 5 points",
				"Une fois les§a 2400§f points atteint vous obtenez les mêmes effet que la cible (seulement les effets permanent)",
				"",
				"§7     →§a Technique§f: Ouvre un menu ayant à l'intérieur la liste des joueurs a qui vous avez copié les effets, en cliquant sur la tête d'un de ces joueurs vous obtiendrez a nouveau les effets permanent de la cible (vous retire vos effet actuel).",
				"",
				AllDesc.point+KamuiItem().getItemMeta().getDisplayName()+"§f:  Vous offre le choix entre:",
				"§7     →§d Arimasu§f: Vous permet de vous téléportez dans la dimension \"§dKamui§f\"",
				"§7     →§d Sonohaka§f: En choisissant un joueur, vous permet de le téléportez dans la dimension \"§dKamui§f\"",
				"",
				AllDesc.commande,
				"",
				AllDesc.point+"§6/ns yameru <joueur>§f: Permet en ciblant un joueur, de s'il est dans le§d Kamui§f de le téléporter dans le monde normal",
				"",
				AllDesc.particularite,
				"",
				AllDesc.chakra+getChakras().getShowedName(),
				AllDesc.bar
				
				
		};
	}
	private ItemStack KamuiItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§dKamui").setLore("§7Permet de vous téléportez ou de téléporter un joueur dans le Kamui").toItemStack();
	}
 	private ItemStack SharinganItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§cSharingan").setLore("§7Ouvre un menu").toItemStack();
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				SharinganItem(),
				KamuiItem()
		};
	}
	private int cdSonohoka = 0;
    private int cdArimasu = 0;
    private Inventory KamuiInventory() {
		Inventory inv = Bukkit.createInventory(owner, 9, "§cKamui");
		inv.setItem(3, new ItemBuilder(Material.EYE_OF_ENDER).setName("§dArimasu").setLore("§7Cooldown "+StringUtils.secondsTowardsBeautiful(cdArimasu),
				"§7Permet de vous téléportez dans le Kamui").toItemStack());
		inv.setItem(7, new ItemBuilder(Material.ENDER_PEARL).setName("§dSonohaka").setLore("§7Cooldown "+StringUtils.secondsTowardsBeautiful(cdSonohoka),
				"§7Permet de téléporter un joueur dans le Kamui").toItemStack());
		return inv;
	}
    private void openSonohakaInventory(){
		Inventory inv = Bukkit.createInventory(owner, 54, "§cKamui§7 ->§d Sonohaka");
		for (int i = 0; i <= 8; i++) {
			if (i == 4) {
				inv.setItem(i, GUIItems.getSelectBackMenu());
			} else {
				inv.setItem(i, GUIItems.getOrangeStainedGlassPane());
			}
		}
		for (Player p : Loc.getNearbyPlayersExcept(owner, 30)) {
			inv.addItem(new ItemBuilder(Material.SKULL_ITEM).setDurability(((short)3)).setName(p.getDisplayName()).toItemStack());
		}
		owner.openInventory(inv);
	}
	@Override
	public void onNsCommand(String[] args) {
		if (args[0].equalsIgnoreCase("yameru")) {
			if (args.length == 2) {
				Player target = Bukkit.getPlayer(args[1]);
				if (target == null) {
					owner.sendMessage("§CVeuiller cibler un joueur éxistant !");
                }else {
					KamuiUtils.end(target);
				}
			}
		}
	}
	@Override
	public void onAllPlayerInventoryClick(InventoryClickEvent event, ItemStack item, Inventory inv, Player clicker) {
		if (clicker.getUniqueId() == owner.getUniqueId()) {
			if (inv != null && item != null && item.getType() != Material.AIR) {
				if (inv.getTitle().equals("§cKamui§7 ->§d Sonohaka")) {
					if (item.getType() != Material.AIR) {
						if (item.isSimilar(GUIItems.getSelectBackMenu())) {
							owner.openInventory(KamuiInventory());
							event.setCancelled(true);
						}
						if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
							Player clicked = Bukkit.getPlayer(item.getItemMeta().getDisplayName());
							if (clicked != null) {
								clicker.closeInventory();
								if (Loc.getNearbyPlayersExcept(owner, 30).contains(clicked)) {
									clicked.closeInventory();
									clicked.sendMessage("§7Vous avez été téléportez dans le§d Kamui !");
									KamuiUtils.start(clicked.getLocation(), Users.cibleKakashi, clicked, true);
									new BukkitRunnable() {
										int i = 0;
										@Override
										public void run() {
											i++;
											if (i == 60*3) {
												KamuiUtils.end(clicked);
											}else {
												sendCustomActionBar(clicked, "§bTemp restant:§c "+StringUtils.secondsTowardsBeautiful((60*3)-i));
											}
											if (clicked.getWorld() != Bukkit.getWorld("Kamui")) {
												cancel();
                                            }
										}
									}.runTaskTimer(Main.getInstance(), 0, 20);
									cdSonohoka = 60*15;
									event.setCancelled(true);
								}
							}
						}
					}
				}
				if (inv.getTitle().equalsIgnoreCase("§cKamui")) {
					if (item.getType() == Material.AIR)return;
					if (Bukkit.getWorld("Kamui") == null) {
						clicker.sendMessage("§7La map Kamui n'existe pas");
						clicker.closeInventory();
						event.setCancelled(true);
						return;
					}
					if (event.getSlot() == 3) {
						owner.closeInventory();
						event.setCancelled(true);
						if (cdArimasu <= 0) {
								cdArimasu = 60*10;
								KamuiUtils.start(owner.getLocation(), Users.kakashi, owner, true);
								new BukkitRunnable() {
									int i = 0;
									@Override
									public void run() {
										if (Bukkit.getWorld("Kamui") == null) {
											cancel();
											return;
										}
										if (gameState.getServerState() != ServerStates.InGame) {
											cancel();
											return;
										}
										i++;
										if (!Objects.equals(owner.getWorld().getName(), "Kamui")) {
											cancel();
											return;
										}
										if (i == 60*3) {
											owner.sendMessage("§7Vous sortez du§d Kamui§7 !");
											KamuiUtils.end(owner);
											cancel();
                                        } else {
											sendCustomActionBar(owner, "§bTemp restant:§c "+StringUtils.secondsTowardsBeautiful((60*3)-i));
										}
									}
								}.runTaskTimer(Main.getInstance(), 0, 20);
                        } else {
							sendCooldown(clicker, cdArimasu);
                        }
                        return;
                    }
					if (event.getSlot() == 7) {
						event.setCancelled(true);
						owner.closeInventory();
						if (cdSonohoka <= 0) {
								openSonohakaInventory();
                        } else {
							sendCooldown(clicker, cdSonohoka);
                        }
					}
				}
			}
		}
	}
	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}
	private Inventory SharinganInventory() {
		Inventory inv = Bukkit.createInventory(owner, 9, "§cSharingan");
		inv.setItem(1, new ItemBuilder(Material.PAPER).setName("§aTechnique").toItemStack());
		if (!Coping) {
			inv.setItem(7, new ItemBuilder(Material.EYE_OF_ENDER).setName("§aCopie").toItemStack());
		} else {
			inv.setItem(7, new ItemBuilder(Material.BARRIER).setName("§cAnnuler").toItemStack());
		}
		return inv;
	}
	@Override
	public void Update(GameState gameState) {
		if (cdArimasu >= 0) {
			cdArimasu -= 1;
		}
		if (cdArimasu == 0) {
			owner.sendMessage("§cVous pouvez de nouveau vous téléportez dans le kamui");
		}
		if (cdSonohoka >= 0) {
			cdSonohoka -= 1;
		}
		if (cdSonohoka == 0) {
			owner.sendMessage("§cVous pouvez de nouveau téléporter quelqu'un dans le kamui");
		}
		if (inCopy != null) {
			if (Loc.getNearbyPlayers(owner, 5).contains(inCopy)) {
				actualPoint += 20;
			} else {
				if (Loc.getNearbyPlayers(owner, 10).contains(inCopy)) {
					actualPoint += 10;
				} else {
					if (Loc.getNearbyPlayers(owner, 20).contains(inCopy)) {
						actualPoint += 5;
					}
				}
			}
			sendCustomActionBar(owner, "Points: ["+actualPoint+"/1600]"+gameState.sendIntBar(actualPoint, 1600, 16));
			if (actualPoint >= 1600) {
				owner.sendMessage("§7La copie de "+inCopy.getName()+" est maintenant terminé");
				Copied.put(inCopy.getName(), getPermanentPotionEffects(inCopy));
				inCopy = null;
				Coping = false;
				actualPoint = 0;
			}
		}
	}
	private Inventory TechniqueInventory() {
		Inventory inv = Bukkit.createInventory(owner, 54, "§aTechnique");
		for (String p : Copied.keySet()) {
			inv.addItem(new ItemBuilder(Material.PAPER).setName(p).toItemStack());
		}
		return inv;
	}
	@Override
	public void neoFormChoosen(ItemStack item, Inventory inv, int slot, GameState gameState) {
		if (inv.getTitle().equals("§cSharingan")) {
			if (slot == 1) {
				owner.openInventory(TechniqueInventory());
				owner.updateInventory();
			}
			if (slot == 7) {
				owner.openInventory(CopieInventory());
				owner.updateInventory();
			}
		}
		if (inv.getTitle().equals("§aCopie")) {
			if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
				Player target = Bukkit.getPlayer(item.getItemMeta().getDisplayName());
				if (target != null) {
					inCopy = target;
					owner.closeInventory();
					owner.sendMessage("§7Vous copier maintenant les effets de "+target.getDisplayName());
					actualPoint = 0;
					Coping = true;
				}
			}
		}
		if (inv.getTitle().equals("§aTechnique")) {
			if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
				String target = item.getItemMeta().getDisplayName();
				owner.getActivePotionEffects().stream().filter(e -> getPermanentPotionEffects(owner).contains(e.getType())).forEach(e -> owner.removePotionEffect(e.getType()));
				setResi(0);
				if (Copied.containsKey(target)) {
					for (PotionEffectType po : Copied.get(target)) {
						OLDgivePotionEffet(po, Integer.MAX_VALUE, 1, true);
					}
					new BukkitRunnable() {
						
						@Override
						public void run() {
							if (owner.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)){
								setResi(20);
							}
							cancel();
						}
					}.runTaskTimer(Main.getInstance(), 0, 55);
				}
			}
		}
	}
	private Player inCopy = null;
	private int actualPoint = 0;
	private boolean Coping = false;
	private final HashMap<String, List<PotionEffectType>> Copied = new HashMap<>();
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(SharinganItem())) {
			owner.openInventory(SharinganInventory());
			owner.updateInventory();
			return true;
		}
		if (item.isSimilar(KamuiItem())) {
			owner.openInventory(KamuiInventory());
			return true;
		}
		return super.ItemUse(item, gameState);
	}
	private Inventory CopieInventory() {
		Inventory inv = Bukkit.createInventory(owner, 54, "§aCopie");
		for (Player p : Loc.getNearbyPlayersExcept(owner, 20)) {
			if (!Copied.containsKey(p.getName())) {
				inv.addItem(new ItemBuilder(GlobalUtils.getPlayerHead(p.getName())).setName(p.getName()).toItemStack());
			}
		}
		return inv;
	}
	@Override
	public void resetCooldown() {
		cdArimasu =0;
		cdSonohoka = 0;
	}

	@Override
	public String getName() {
		return "Kakashi";
	}
}