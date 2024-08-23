package fr.nicknqck.items;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.builders.DemonsSlayersRoles;
import fr.nicknqck.roles.ds.slayers.Nezuko;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
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

import javax.annotation.Nonnull;
import java.util.UUID;

public class InfectItem implements Listener{
	@Getter
	private static InfectItem instance;
	private final GameState gameState;
	private int ActualTime;
	private boolean infect;
	private Player clicker;
	private Player p;
	private DemonsSlayersRoles toInfected;
	@Setter
	@Getter
	private DemonsSlayersRoles infecteur;
	public InfectItem(GameState state) {
		this.gameState = state;
		ActualTime = 0;
		instance = this;
		infect = false;
		clicker = null;
		p = null;
		toInfected = null;
		infecteur = null;
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
							if (gameState.getInGamePlayers().contains(p.getUniqueId())) {
								GamePlayer gamePlayer = gameState.getGamePlayer().get(p.getUniqueId());
								if (gamePlayer.getRole() instanceof DemonsSlayersRoles && gamePlayer.getRole().getOriginTeam() != TeamList.Demon && !(gamePlayer.getRole() instanceof Nezuko)) {
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
	@EventHandler
	public void InventoryClick(InventoryClickEvent e) {
		if (e.getWhoClicked() != null) {
			if (e.getWhoClicked().getOpenInventory() != null) {
				if (e.getAction() == null)return;
				if (e.getCurrentItem() == null)return;
				if (e.getClickedInventory() == null)return;
				if (e.getWhoClicked().getOpenInventory() == null)return;
				if (e.getWhoClicked().getOpenInventory().getTitle() == null) return;
				if (e.getWhoClicked().getOpenInventory().getTitle().equalsIgnoreCase("Infection")) {
					if (!e.getWhoClicked().getUniqueId().equals(getInfecteur().getPlayer()))return;
					if (!e.getCurrentItem().getType().equals(Material.SKULL_ITEM))return;
					for (UUID u : gameState.getInGamePlayers()) {
						Player p = Bukkit.getPlayer(u);
						if (p == null)continue;
						if (e.getCurrentItem().getItemMeta().getDisplayName().equals(p.getName())) {
							@Nonnull
							Player clicker = (Player)e.getWhoClicked();
							Player toInfected = Bukkit.getPlayer(e.getCurrentItem().getItemMeta().getDisplayName());
							if (toInfected == null) {
								clicker.sendMessage("§c"+e.getCurrentItem().getItemMeta().getDisplayName()+"§f n'est pas connecter");
								e.setCancelled(true);
								return;
							}
							ActualTime = 0;
							infect = false;
							this.clicker = null;
							this.p = null;
							clicker.sendMessage("Vous avez lancé l'infection sur le joueur: "+e.getCurrentItem().getItemMeta().getDisplayName());
							infect = true;
							clicker.sendMessage("§6"+ StringUtils.secondsTowardsBeautiful(gameState.timewaitingbeinfected)+"§r avant infection");
							e.setCancelled(true);
							clicker.getInventory().remove(Items.getInfection());
							this.clicker = clicker;
							this.p = p;
							GamePlayer gamePlayer = gameState.getGamePlayer().get(toInfected.getUniqueId());
							if (gamePlayer != null && gamePlayer.getRole() instanceof DemonsSlayersRoles) {
								this.toInfected = (DemonsSlayersRoles) gamePlayer.getRole();
							}
						}
					}
				}
			}
		}
	}
	@SuppressWarnings("deprecation")
	public void onSecond() {
		if (p == null)return;
		if (clicker == null)return;
		if (infect) {
			ActualTime+=1;
			if (ActualTime == gameState.timewaitingbeinfected) {
				if (gameState.getServerState() == ServerStates.InGame) {
					if (gameState.infecteur != null) {
						if (gameState.infecteur.getUniqueId() == clicker.getUniqueId() && toInfected != null) {
							if (gameState.getInSpecPlayers().contains(p)) {
								for (UUID u : gameState.getInGamePlayers()) {
									Player p = Bukkit.getPlayer(u);
									if (p == null)continue;
									if (!gameState.hasRoleNull(p)) {
										if (gameState.getPlayerRoles().get(p) instanceof DemonsRoles) {
											p.sendMessage("§cL'infection a échoué");
										}
									}
								}
								return;
							}
							Player p = Bukkit.getPlayer(toInfected.getPlayer());
							if (p == null) {
								System.out.println("Error | The Player "+toInfected.getPlayer()+" isn't online, it can't be infected");
								ActualTime-=5;
								return;
							}
							p.resetTitle();
							gameState.infected = p;
							p.sendTitle("§cVous avez été infecté", "Vous gagnez maintenant avec les Démons");
							p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*10, 0, false, false));
							
							gameState.infecteur.sendMessage(p.getName()+" à été infecté");
							for (UUID u : gameState.getInGamePlayers()) {
								Player z = Bukkit.getPlayer(u);
								if (z == null)continue;
								if (gameState.getPlayerRoles().get(z).getOriginTeam() == TeamList.Demon) {
									z.sendMessage("§4Un joueur à été infecté et à rejoins le camp des§c Démons");
								}
							}
							gameState.infected = p;
							clicker.sendMessage(p.getName()+" à été infecté");
							if (gameState.getPlayerRoles().get(p).getTeam() != TeamList.Slayer) {
								p.sendMessage("Vous avez été infecté mais comme vous n'étiez pas du camp§a Slayer§r vous n'avez pas pus être infecté, vous restez donc dans votre camp d'origine");
							}
							if (gameState.getPlayerRoles().get(p).getTeam() == TeamList.Slayer) {
								gameState.getPlayerRoles().get(p).setTeam(TeamList.Demon);
							}
							p.resetTitle();
							p.sendMessage("Voici l'identité de votre§c infecteur§f:§c§l "+gameState.infecteur.getName());
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