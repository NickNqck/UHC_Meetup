package fr.nicknqck.roles.aot.builders.titans;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import fr.nicknqck.events.custom.EndGameEvent;

public class TitanListener implements Listener{

	private static TitanListener instance;
//	private boolean endGame = false;
	
	private UUID Colossal;
	private UUID Bestial;
	private UUID Cuirasse;
	private UUID WarHammer;
	private UUID Machoire;
	private UUID Charette;
	private UUID Assaillant;
	
	private int ColossalCooldown = 0;
	private int CuirasseCooldown = 0;
	private int WarHammerCooldown = 0;
	private int MachoireCooldown = 0;
	private int AssaillantCooldown = 0;
	private int BestialCooldown = 0;
	
	
	public void setColossal(UUID t) {
		Colossal = t;
	}
	public UUID getColossal() {
		return Colossal;
	}
	
	public void resetCooldown() {
		setColossal(null);
		setCuirasse(null);
		setWarHammer(null);
		setCharette(null);
		setMachoire(null);
		setBestial(null);
		setAssaillant(null);
		setAssaillantCooldown(0);
		setBestialCooldown(0);
		setColossalCooldown(0);
		setCuirasseCooldown(0);
		setWarHammerCooldown(0);
		setMachoireCooldown(0);
		for (Titans t : Titans.values()) {
			t.getTitan().setTransformedinTitan(false);
			t.getTitan().resetCooldown();
			t.getTitan().getListforSteal().clear();
		}
	}
	public int getColossalCooldown() {
		return ColossalCooldown;
	}
	public void setColossalCooldown(int i) {
		this.ColossalCooldown = i;
	}
	
	public void setCuirasse(UUID t) {
		Cuirasse = t;
	}
	public UUID getCuirasse() {
		return Cuirasse;
	}
	
	public int getCuirasseCooldown() {
		return CuirasseCooldown;
	}
	public void setCuirasseCooldown(int i) {
		this.CuirasseCooldown = i;
	}
	
	public void setWarHammer(UUID p) {
		WarHammer = p;
	}
	public UUID getWarHammer() {
		return WarHammer;
	}
	
	public void setWarHammerCooldown(int i) {
		WarHammerCooldown = i;
	}
	public int getWarHammerCooldown() {
		return WarHammerCooldown;
	}
	
	public UUID getMachoire() {
		return Machoire;
	}
	public void setMachoire(UUID t) {
		Machoire = t;
	}
	
	public int getMachoireCooldown() {
		return MachoireCooldown;
	}
	public void setMachoireCooldown(int i) {
		MachoireCooldown = i;
	}
	
	public UUID getCharette() {
		return Charette;
	}
	public void setCharette(UUID p) {
		Charette = p;
	}
	
	public UUID getAssaillant() {
		return Assaillant;
	}
	public void setAssaillant(UUID p) {
		Assaillant = p;
	}
	
	public int getAssaillantCooldown() {
		return AssaillantCooldown;
	}
	public void setAssaillantCooldown(int i) {
		AssaillantCooldown = i;
	}
	
	public UUID getBestial() {
		return Bestial;
	}
	public void setBestial(UUID p) {
		Bestial = p;
	}
	
	public int getBestialCooldown() {
		return BestialCooldown;
	}
	public void setBestialCooldown(int i) {
		BestialCooldown = i;
	}
	
	public static TitanListener getInstance() {return instance;}
	public TitanListener() {
		instance = this;
	}
	@EventHandler
	private void onEndGame(EndGameEvent e) {
		for (Titans t : Titans.values()) {
			t.getTitan().resetCooldown();
		}
		System.out.println("ended Titans");
	}
	
	public void onSecond() {
			if (getColossalCooldown() == 60*5) {
				Titans.Colossal.getTitan().Transfo();
			}
			if (getColossalCooldown() > 0) {
				setColossalCooldown(getColossalCooldown()-1);
			}
			if (getColossalCooldown() == 0) {
				if (getColossal() != null) {
					Player player = Bukkit.getPlayer(getColossal());
					if (player == null)return;
					player.sendMessage("§7Vous pouvez à nouveau vous transformé en Titan "+Titans.Colossal.getTitan().getName());
					setColossalCooldown(-5);
				}
			}
			if (getCuirasseCooldown() >0) {
				setCuirasseCooldown(getCuirasseCooldown()-1);
			}
			if (getCuirasseCooldown() == 60*4) {
				Titans.Cuirasse.getTitan().Transfo();
			}
			if (getCuirasseCooldown() == 0) {
				if (getCuirasse() != null) {
					Player player = Bukkit.getPlayer(getCuirasse());
					if (player != null) {
						player.sendMessage("§7Vous pouvez à nouveau vous transformé en Titan "+Titans.Cuirasse.getTitan().getName());
						setCuirasseCooldown(-5);
					}
				}
			}
			if (getWarHammerCooldown()== 60*4) {
				Titans.WarHammer.getTitan().Transfo();
			}
			if (getWarHammerCooldown() > 0) {
				setWarHammerCooldown(getWarHammerCooldown()-1);
			}
			if (getWarHammerCooldown() == 0) {
				if (getWarHammer() != null) {
					Player player = Bukkit.getPlayer(getWarHammer());
					if (player != null) {
						player.sendMessage("§7Vous pouvez à nouveau vous transformez en Titan "+Titans.WarHammer.getTitan().getName());
					}
					setWarHammerCooldown(-5);
				}
			}
			if (getMachoireCooldown() == 0) {
				if (getMachoire() != null) {
					Player p = Bukkit.getPlayer(getMachoire());
					if (p != null) {
						p.sendMessage("§7Vous pouvez à nouveau vous transformez en Titan "+Titans.Machoire.getTitan().getName());
					}
					setMachoireCooldown(-5);
				}
			}
			if (getMachoireCooldown()>0) {
				setMachoireCooldown(getMachoireCooldown()-1);
			}
			if (getMachoireCooldown() == 60*3) {
				Titans.Machoire.getTitan().Transfo();
			}
			if (getAssaillantCooldown() > 0) {
				setAssaillantCooldown(getAssaillantCooldown()-1);
				if (getAssaillantCooldown() == 60*4) {
					Titans.Assaillant.getTitan().Transfo();
				}
			}
			if (getAssaillantCooldown() == 0) {
				if (getAssaillant() != null) {
					Player p = Bukkit.getPlayer(getAssaillant());
					if (p != null) {
						p.sendMessage("§7Vous pouvez à nouveau vous transformez en Titan "+Titans.Assaillant.getTitan().getName());
					}
					setAssaillantCooldown(-5);
				}
			}
			if (getBestialCooldown() > 0) {
				setBestialCooldown(getBestialCooldown()-1);
				if (getBestialCooldown() == 60*4) {
					Titans.Bestial.getTitan().Transfo();
				}
			}
			if (getBestialCooldown() == 0) {
				if (getBestial() != null) {
					Player p = Bukkit.getPlayer(getBestial());
					if (p != null) {
						p.sendMessage("§7Vous pouvez à nouveau vous transformez en Titan "+Titans.Bestial.getTitan().getName());
					}
					setBestialCooldown(-5);
				}
			}
			for (Titans t : Titans.values()) {
				t.getTitan().onSecond();
				if (t.getTitan().getOwner() == null) {
					t.getTitan().resetCooldown();
					if (t.getTitan().isTransformedinTitan()) {
						t.getTitan().setTransformedinTitan(false);
					}
				}
			}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRecup(PlayerPickupItemEvent e) {
		if (e.getItem() != null) {
			for (Titans t : Titans.values()) {
				t.getTitan().onPickup(e, e.getPlayer());
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (e.getItem() == null)return;
		if (e.getItem().getType() == Material.AIR)return;
		for (Titans t : Titans.values()) {
			t.getTitan().onInteract(e, e.getPlayer());
		}
	}
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		for (Titans t : Titans.values()) {
			t.getTitan().onBlockBreak(e, e.getPlayer());
		}
	}
	public void onStartGame() {
		System.out.println("Started Titans");
	}
}