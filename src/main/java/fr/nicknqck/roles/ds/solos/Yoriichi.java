package fr.nicknqck.roles.ds.solos;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.DemonsSlayersRoles;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.PacketDisplay;
import fr.nicknqck.utils.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Yoriichi extends DemonsSlayersRoles {

	public Yoriichi(Player player) {
		super(player);
		owner.sendMessage(Desc());
		this.setForce(20);
		this.setCanuseblade(true);
		this.setResi(20);
		setLameIncassable(owner, true);
	}
	@Override
	public Roles getRoles() {
		return Roles.Yoriichi;
	}

	@Override
	public TeamList getOriginTeam() {
		return TeamList.Solo;
	}

	@Override
	public String[] Desc() {
		return AllDesc.Yoriichi;
	}

	private boolean killkoku = false;
	private boolean activersoufle = false;
	private final Map<UUID, PacketDisplay> inEye = new HashMap<>();
	@Override
	public void onTick() {
		for (Player p : Loc.getNearbyPlayers(owner, 30)){
			if (!inEye.containsKey(p.getUniqueId())) {
				if (!p.isSneaking()) {
					PacketDisplay display = new PacketDisplay(p.getLocation(), WorldUtils.getBeautyHealth(p) + " ❤");

		            display.display(owner);

		            inEye.put(p.getUniqueId(), display);
				}
			} else {
				PacketDisplay packetDisplay = inEye.get(p.getUniqueId());
				if (p.isSneaking()) {
					packetDisplay.setCustomNameVisible(false, owner);
				} else {
					packetDisplay.setCustomNameVisible(true, owner);
				}
				DecimalFormat df = new DecimalFormat("0");
                packetDisplay.rename(df.format(p.getHealth())+ AllDesc.Coeur(" §c")+"§7 |§f "+df.format(((CraftPlayer) p).getHandle().getAbsorptionHearts())+AllDesc.Coeur(" §e"), owner);
                if (packetDisplay != null) {
                	packetDisplay.teleport(p.getLocation(), owner);
                }
			}
		}
	}
	@Override
	public void OnAPlayerDie(Player player, GameState gameState, Entity killer) {
		if (player.getUniqueId() == owner.getUniqueId()) {
			for (UUID u : inEye.keySet()) {
				inEye.get(u).destroy(owner);
			}
			inEye.clear();
		}
	}
	@Override
	public void onEndGame() {
		for (UUID u : inEye.keySet()) {
			inEye.get(u).destroy(owner);
		}
		inEye.clear();
	}
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(Items.getLamedenichirin());
		giveItem(owner, false, getItems());
	}
	@Override
	public void Update(GameState gameState) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!Loc.getNearbyPlayersExcept(owner, 30).contains(p)) {
				if (inEye.containsKey(p.getUniqueId())){
					inEye.get(p.getUniqueId()).destroy(owner);
				}
			}
		}
		owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
		owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false));
		if (killkoku) {
			if (!gameState.nightTime) {			
				owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
				
			} else {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
			}
		} else {
			if (!gameState.nightTime) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 0, false, false));
			} else {
				if (owner.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
					owner.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
				}
			}
		}
		for (Player p : Loc.getNearbyPlayersExcept(owner, 15)) {
			if (p.getWorld() != owner.getWorld())return;
			if (p.getLocation().distance(owner.getLocation()) <= 15) {
					if (p != owner) {
						DecimalFormat df = new DecimalFormat("0.0");
						String message = "§cVie de "+p.getName()+" §6" + (df.format(p.getHealth()/2))+"♥";
						sendCustomActionBar(owner, message);
					}				
			}
				
		}
		super.Update(gameState);
	}
	@Override
	public void resetCooldown() {
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(Items.getSoufleduSoleil())) {
			if (!activersoufle) {				
				owner.sendMessage("Activation du Soufle du Soleil");
				activersoufle = true;
			} else if (activersoufle) {
				owner.sendMessage("Désactivation du Soufle du Soleil");
				activersoufle = false;
			}
		}
		return super.ItemUse(item, gameState);
	}
	@Override
	public void ItemUseAgainst(ItemStack item, Player victim, GameState gameState) {
		if (item.isSimilar(Items.getdiamondsword())) {
			if (activersoufle) {
				if (victim.hasPotionEffect(PotionEffectType.ABSORPTION)) {
					victim.removePotionEffect(PotionEffectType.ABSORPTION);
					float abso = ((CraftPlayer) victim).getHandle().getAbsorptionHearts();
					if (victim.getHealth() > abso) {
						victim.setHealth(victim.getHealth() - abso);
					} else {
						victim.setHealth(1.0);
					}
					victim.sendMessage("§6Yoriichi"+"§f vous à retirer votre absorbtion");
					owner.sendMessage("Vous venez de retirer l'absorbtion de:§6§l "+ victim.getName());
				}
			}
		}
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (killer == owner) {
			if (victim != owner) {
				if (gameState.getInGamePlayers().contains(victim)) {
					if (gameState.getPlayerRoles().containsKey(victim)) {
						RoleBase role = gameState.getPlayerRoles().get(victim);
						if (role.getRoles() == Roles.Kokushibo && !killkoku) {
							killkoku = true;
							owner.sendMessage(ChatColor.GRAY+"Vous venez de tuée: "+ victim.getName()+" il possédait le rôle de: "+ChatColor.GOLD+role.getRoles()+ChatColor.GRAY+" maintenant la nuit vous posséderez l'effet: "+AllDesc.Resi+" 1");
						}
					}
				}
			}
		}
		super.PlayerKilled(killer, victim, gameState);
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
			Items.getSoufleduSoleil()	
		};
	}

	@Override
	public String getName() {
		return "§eYoriichi";
	}
}