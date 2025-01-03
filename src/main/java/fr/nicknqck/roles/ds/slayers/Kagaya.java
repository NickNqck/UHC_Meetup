package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.roles.ds.slayers.pillier.PilierRoles;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.packets.PacketDisplay;
import fr.nicknqck.utils.TripleMap;
import fr.nicknqck.utils.WorldUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.*;

public class Kagaya extends SlayerRoles {
	private TextComponent desc;
	public Kagaya(UUID player) {
		super(player);
	}

	@Override
	public Soufle getSoufle() {
		return Soufle.AUCUN;
	}

	@Override
	public Roles getRoles() {
		return Roles.Kagaya;
	}
	@Override
	public String[] Desc() {
		return AllDesc.Kagaya;
	}
	@Override
	public String getName() {
		return "Kagaya";
	}

	@Override
	public void RoleGiven(GameState gameState) {
		AutomaticDesc desc = new AutomaticDesc(this);
		desc.setCommands(new TripleMap<>(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous permet de choisir entre les 3 pactes disponible\n\n"+
		"§c§nPacte n°1:§r§7 Vous offre la particularité de voir la§c vie§r§7 des joueurs au dessus de leurs têtes, également vous obtenez §8Weakness I§r§7 le §eJour§r§7 et §9Resistance I§r§7 la§1 Nuit§r\n\n"+
		"§c§nPacte n°2:§r§7 Vous octroie §c2♥§r§7 permanent supplémentaire ainsi que §9Resistance I§r§7 le§e Jour§r\n\n"+
		"§c§nPacte n°3:§r§7 Vous obtenez l'identité d'un §aPilier§r§7 aléatoire parmis ceux encore en §cvie§r§7 ainsi que §9Resistance I§r§7 à moins de §c30 blocs§r§7 de ce dernier")}), "§c/ds role", -500));
		this.desc = desc.getText();
		new onTick(this).runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
	}

	private enum Pacte{
		Pacte1,
		Pacte2,
		Pacte3
	}
	private Pacte pacte;
	private boolean pacte1 = false;
	private boolean pacte2 = false;
	public boolean pacte3 = false;
	public PilierRoles pillier= null;
	private final Map<UUID, PacketDisplay> seeHealth = new HashMap<>();
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
				List<PilierRoles> pilierRoles = new ArrayList<>();
				for (UUID uuid : gameState.getInGamePlayers()) {
					Player player = Bukkit.getPlayer(uuid);
					if (player == null)continue;
					if (gameState.hasRoleNull(player))continue;
					if (gameState.getPlayerRoles().get(player) instanceof PilierRoles) {
						pilierRoles.add((PilierRoles) gameState.getPlayerRoles().get(player));
					}
				}
				if (!pilierRoles.isEmpty()) {
					Collections.shuffle(pilierRoles, Main.RANDOM);
					PilierRoles pilier = pilierRoles.get(0);
					if (pilier == null) {
						System.err.println("Kagaya pilier is null, HOW ?!");
						return;
					}
					owner.sendMessage("§7Votre pilier est§a "+pilier.getName()+"§7 (§a"+pilier.owner.getName()+"§7)");
					this.pillier = pilier;
				} else {
					owner.sendMessage("Malheuresement, il n'y à aucun pillier dans la partie donc vous avez la possibilité de choisir un autre pacte");
					pacte3 = false;
					pacte = null;
				}
			}
		}
		super.FormChoosen(item, gameState);
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


	public void updateHealthAboveHead(Player player) {
		double health = player.getHealth();
		double maxHealth = player.getMaxHealth();
		String healthDisplay = "§c❤ " + String.format("%.1f", health) + "/" + String.format("%.0f", maxHealth);
		player.setPlayerListName("§a"+player.getName() + " " + healthDisplay);
		player.setCustomName(healthDisplay);
		player.setCustomNameVisible(true);  // Affiche le nom personnalisé au-dessus du joueur
	}


	@Override
	public void Update(GameState gameState) {
		if (pacte1) {
			if (gameState.nightTime){
				givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, 3,1,true);
			} else {
				givePotionEffet(PotionEffectType.WEAKNESS, 3,1,true);
			}
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player != owner) {
					updateHealthAboveHead(player);
				}
			}
		}
		if (pacte2) {
			if (!gameState.nightTime) {
				if (getResi() < 20)setResi(20);
				givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true);
			} else {if (getResi()>0) setResi(0);}
		}
		if (pacte3) {
			for (Player p : Loc.getNearbyPlayers(owner, 30)) {
				if (p == pillier){
					givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, 3, 1,true);
				}
			}
		}
		super.Update(gameState);
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[0];
	}
	private static class onTick extends BukkitRunnable {
		private final Kagaya kagaya;
		private onTick(Kagaya kagaya) {
			this.kagaya = kagaya;
		}
		@Override
		public void run() {
			if (!kagaya.getGamePlayer().isAlive() || !kagaya.getGameState().getServerState().equals(GameState.ServerStates.InGame)) {
				cancel();
				return;
			}
			if (kagaya.pacte1) {
				Player owner = Bukkit.getPlayer(kagaya.getPlayer());
				if (owner == null)return;
				for (Player p : Loc.getNearbyPlayersExcept(owner, 30)){
					if (!kagaya.seeHealth.containsKey(p.getUniqueId())) {
						if (!p.isSneaking()) {
							PacketDisplay display = new PacketDisplay(p.getLocation(), WorldUtils.getBeautyHealth(p) + " ❤");

							display.display(owner);

							kagaya.seeHealth.put(p.getUniqueId(), display);
						}
					} else {
						PacketDisplay packetDisplay = kagaya.seeHealth.get(p.getUniqueId());
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
	}

	@Override
	public TextComponent getComponent() {
		return this.desc;
	}
}