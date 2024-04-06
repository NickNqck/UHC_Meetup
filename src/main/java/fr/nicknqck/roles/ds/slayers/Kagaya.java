package fr.nicknqck.roles.ds.slayers;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.PacketDisplay;
import fr.nicknqck.utils.WorldUtils;

public class Kagaya extends RoleBase {

	public Kagaya(Player player, Roles roles, GameState gameState) {
		super(player, roles, gameState);
		owner.sendMessage(Desc());
	}
	@Override
	public String[] Desc() {
		return AllDesc.Kagaya;
	}
	enum Pacte{
		Pacte1,
		Pacte2,
		Pacte3;
	}
	Pacte pacte;
	boolean pacte1 = false;
	boolean pacte2 = false;
	public boolean pacte3 = false;
	public Player pillier= null;
	public boolean access = false;
	private Map<UUID, PacketDisplay> seeHealth = new HashMap<>();
	@Override
	public void resetCooldown() {
	}
	@Override
	public void OpenFormInventory(GameState gameState) {
		if (pacte == null) {
			Inventory inv = Bukkit.createInventory(owner, 9, "Choix de forme");
			inv.setItem(2, GUIItems.getPacte1());
			inv.setItem(4, GUIItems.getPacte2());
			inv.setItem(6, GUIItems.getPacte3());
			owner.openInventory(inv);
		} else {owner.sendMessage("Vous avez déjà choisis votre Pacte ("+pacte.name()+")");}
		super.OpenFormInventory(gameState);
	}
	@Override
	public void FormChoosen(ItemStack item, GameState gameState) {
		if (pacte == null) {
			if (item.isSimilar(GUIItems.getPacte1())) {
				pacte = Pacte.Pacte1;
				pacte1 = true;
				owner.sendMessage("Vous venez de choisir le pacte: "+pacte.name());
			}
			if (item.isSimilar(GUIItems.getPacte2())) {
				pacte = Pacte.Pacte2;
				pacte2 = true;
				owner.sendMessage("Vous venez de choisir le pacte: "+pacte.name());
				setMaxHealth(getMaxHealth()+4.0);
			}
			if (item.isSimilar(GUIItems.getPacte3())) {
				pacte = Pacte.Pacte3;
				pacte3 = true;				
				owner.sendMessage("Vous venez de choisir le pacte: "+pacte.name());
				if (gameState.getPillier().size() > 0) {					
						owner.sendMessage("Pour obtenir votre pillier faite la commande:§6 /ds getPillier");
						access = true;
				} else {
					owner.sendMessage("Désolé mais il n'y à aucun pillier dans la partie donc vous avez la possibilité de choisir un autre pacte");
					pacte3 = false;
					pacte = null;
					}
			}
		}
		super.FormChoosen(item, gameState);
	}
	@Override
	public void onTick() {
		if (pacte1) {
			for (Player p : Loc.getNearbyPlayersExcept(owner, 30)){
				if (!seeHealth.containsKey(p.getUniqueId())) {
					if (!p.isSneaking()) {
						PacketDisplay display = new PacketDisplay(p.getLocation(), WorldUtils.getBeautyHealth(p) + " ❤");

			            display.display(owner);

			            seeHealth.put(p.getUniqueId(), display);
					}
				} else {
					PacketDisplay packetDisplay = seeHealth.get(p.getUniqueId());
					if (!packetDisplay.isCustomNameVisible()) {
	                    packetDisplay.setCustomNameVisible(true, owner);
	                }
					DecimalFormat df = new DecimalFormat("0");
	                packetDisplay.rename(df.format(p.getHealth())+ AllDesc.Coeur(" §c"), owner);
	                if (p.isSneaking()) {
	                	packetDisplay.setCustomNameVisible(false, owner);
	                }
	                packetDisplay.teleport(p.getLocation(), owner);
				}
			}
		}
	}
	@Override
	public void OnAPlayerDie(Player player, GameState gameState, Entity killer) {
		if (player.getUniqueId() == owner.getUniqueId()) {
			seeHealth.clear();
		}
	}
	@Override
	public void onEndGame() {
		seeHealth.clear();
	}
	@Override
	public void Update(GameState gameState) {
		if (pacte1) {
			if (gameState.nightTime) {
				if (getResi() < 20)setResi(20);
				givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true);
			} else {
				if (getResi()>0)setResi(0);
				givePotionEffet(PotionEffectType.WEAKNESS, 60, 1, true);
			}
		}
		if (pacte2) {
			if (!gameState.nightTime) {
				if (getResi() < 20)setResi(20);
				givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true);
			} else {if (getResi()>0) setResi(0);}
		}
		if (pacte3) {
			for (RoleBase r : gameState.getPlayerRoles().values()) {
				if (r != null && r.owner != null) {
					if (r.owner != owner) {
						if (pillier != null) {
							if (r.owner == pillier) {
								if (gameState.getInGamePlayers().contains(pillier)) {
									if (pillier.getGameMode() != GameMode.SPECTATOR) {
										if (pillier.getWorld().equals(owner.getWorld())) {
											if (pillier.getLocation().distance(owner.getLocation()) <= 30) {
												owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*5, 0, false, false));
												givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true);
											}
										}										
									}
								}
							}
						}
					}
				}
			}
		}
		super.Update(gameState);
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[0];
	}
}