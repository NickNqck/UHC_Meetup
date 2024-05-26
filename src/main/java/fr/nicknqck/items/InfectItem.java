package fr.nicknqck.items;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.utils.Loc;

public class InfectItem implements Listener{
	GameState gameState;
	public InfectItem(GameState state) {
		this.gameState = state;
		ActualTime = 0;
		instance = this;
		infect = false;
		clicker = null;
		p = null;
	}
	static InfectItem instance;
	public static InfectItem getInstance() {
		return instance;
	}
	@EventHandler
	public void CliqueDroit(PlayerInteractEvent e) {
		if (e.getItem() == null)return;
		if (e.getItem().getItemMeta() == null)return;
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.getItem().isSimilar(Items.getInfection())) {
				if (gameState.infected == null && gameState.infecteur != null ) {
					Inventory inv = Bukkit.createInventory(e.getPlayer(), 27, "Infection");
					Player a = e.getPlayer();					
						for (Player p : Loc.getNearbyPlayers(a, 30)) {
									if (!gameState.hasRoleNull(p)) {
										if (gameState.getInGamePlayers().contains(p)) {
											if (gameState.getPlayerRoles().get(p).getOldTeam() != TeamList.Demon && !gameState.getPlayerRoles().get(p).getPlayerRoles(p).type.equals(Roles.Nezuko)) {
												ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte)3);
												SkullMeta meta = (SkullMeta) skull.getItemMeta();
												meta.setOwner(p.getDisplayName());
												meta.setDisplayName(p.getDisplayName());
												skull.setItemMeta(meta);
												inv.addItem(skull);
												a.openInventory(inv);
											}											
										}
									}
						}
					a.openInventory(inv);
				}
			}
		}
	}
	public static ItemStack getItem() {return Items.getInfection();}
	private int ActualTime = 0;
	private boolean infect = false;
	@EventHandler
	public void InventoryClick(InventoryClickEvent e) {
		if (e.getWhoClicked() != null) {
			if (e.getWhoClicked().getOpenInventory() != null) {
				if (e.getAction() == null)return;
				if (e.getWhoClicked().getOpenInventory().getTitle().equalsIgnoreCase("Infection")) {
					for (Player p : gameState.getInGamePlayers()) {
						if (e.getCurrentItem().getType() == Material.SKULL_ITEM) {
							if (e.getCurrentItem().getItemMeta().getDisplayName().equals(p.getName())) {
								ActualTime = 0;
								infect = false;
								clicker = null;
								this.p = null;
								Player clicker = (Player)e.getWhoClicked();
								clicker.sendMessage("Vous avez lancé l'infection sur le joueur: "+e.getCurrentItem().getItemMeta().getDisplayName());
								infect = true;
								e.getWhoClicked().sendMessage(""+ChatColor.GOLD+gameState.timewaitingbeinfected+"§rs avant infection");
								e.setCancelled(true);
								e.getWhoClicked().getInventory().remove(Items.getInfection());
								this.clicker = clicker;
								this.p = p;
							}
						}
					}
				}
			}
		}
	}
	Player clicker;
	Player p;
	@SuppressWarnings("deprecation")
	public void onSecond() {
		if (p == null)return;
		if (clicker == null)return;
		if (infect) {
			ActualTime+=1;
			if (ActualTime == gameState.timewaitingbeinfected) {
				if (gameState.getServerState() == ServerStates.InGame) {
					if (gameState.infecteur != null) {
						if (gameState.infecteur == clicker) {
							gameState.infected = p;
							p.sendTitle("§cVous avez été infecté", "Vous gagnez maintenant avec les Démons");
							p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*10, 0, false, false));
							
							gameState.infecteur.sendMessage(p.getName()+" à été infecté");
							for (Player z : gameState.getInGamePlayers()) {
								if (gameState.getPlayerRoles().get(z).getTeam() == TeamList.Demon) {
									z.sendMessage("§4Un joueur à été infecté et à rejoins le camp des§c Démons");
								}
							}
							gameState.infected = p;
							clicker.sendMessage(p.getName()+" à été infecté");
							if (gameState.getPlayerRoles().get(p).getTeam() != TeamList.Slayer) {
								p.sendMessage("Vous avez été infecté mais comme vous n'étiez pas du camp§a Slayer§r vous n'avez pas pus être infecté, vous restez donc dnas votre camp d'origine");
							}
							if (gameState.getPlayerRoles().get(p).getTeam() == TeamList.Slayer) {
								gameState.getPlayerRoles().get(p).setTeam(TeamList.Demon);
								p.sendMessage("Voici l'identité de votre§c infecteur§f:§c§l "+gameState.infecteur.getName());
							}
							p.sendTitle("§cVous avez été infecté", "Vous gagnez maintenant avec les Démons");
						}
					}
				} else {
					gameState.infecteur = null;
					gameState.infected = null;
				}
			}
		}
	}
}