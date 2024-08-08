package fr.nicknqck.roles.ds.demons.lune;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.demons.Muzan;
import fr.nicknqck.roles.ds.slayers.pillier.PillierRoles;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Akaza extends DemonsRoles {

	private int regencooldown = 0;
	private final TextComponent desc;
	private final AkazaPilierRunnable runnable;

	public Akaza(UUID player) {
		super(player);
		givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
		getKnowedRoles().add(Muzan.class);
		AutomaticDesc desc = new AutomaticDesc(this);
		desc.addEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
		desc.addParticularites(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous possédez une§c régénération§7 naturel à hauteur de§c 1/2"+ AllDesc.coeur+"§7 toute les§c 20 secondes§7.")}),
				new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Toute les§c 5 minutes§7 vous êtes informer du nombre de§c pilier§7 que vous avez croiser ces§c 5 dernières minutes§7.")}),
				new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("")}));
		this.desc = desc.getText();
		this.runnable = new AkazaPilierRunnable(this);
		this.runnable.runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
	}
	@Override
	public DemonType getRank() {
		return DemonType.LuneSuperieur;
	}
	@Override
	public TeamList getOriginTeam() {
		return TeamList.Demon;
	}
	@Override
	public Roles getRoles() {
		return Roles.Akaza;
	}
	@Override
	public String[] Desc() {
		return new String[0];
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[0];
	}
	@Override
	public TextComponent getComponent() {
		return desc;
	}
	@Override
	public void Update(GameState gameState) {
		if (regencooldown == 0) {
			regencooldown = 20;
			if (owner.getHealth() != this.getMaxHealth()) {
				if (owner.getHealth() <= (this.getMaxHealth() - 1.0)) {
					owner.setHealth(owner.getHealth() + 1.0);
				} else {
					owner.setHealth(this.getMaxHealth());
				}			
			}
		}
		if (regencooldown >= 1) {regencooldown--;}
	}
	@Override
	public String getName() {
		return "Akaza";
	}
	@Override
	public void resetCooldown() {
		regencooldown = 0;
	}
	@EventHandler
	private void onEndGame(EndGameEvent event) {
		this.runnable.cancel();
		HandlerList.unregisterAll(this);
	}
	private static class AkazaPilierRunnable extends BukkitRunnable {

		private final Akaza akaza;
		private int timeRemaining = 60*5;
		private final Map<String, Integer> timeCroised = new HashMap<>();
		private AkazaPilierRunnable(Akaza akaza) {
			this.akaza = akaza;
		}
		@Override
		public void run() {
			if (!akaza.getGamePlayer().isAlive()) {
				cancel();
				return;
			}
			Player owner = Bukkit.getPlayer(akaza.getPlayer());
			if (owner == null)return;
			if (timeRemaining == 0) {
				if (!timeCroised.isEmpty()) {
					for (String string : timeCroised.keySet()) {
						owner.sendMessage("§7Vous avez croiser un joueur possédant le rôle de§a "+string+"§7 pendant §c"+ StringUtils.secondsTowardsBeautiful(timeCroised.get(string))+"§7.");
					}
					timeCroised.clear();
				} else {
					owner.sendMessage("§7Vous n'avez croiser aucun§a pilier§7 durant ces§c 5 dernières minutes§7.");
				}
			}
			for (Player around : Loc.getNearbyPlayersExcept(owner, 10)) {
				if (!akaza.getGameState().hasRoleNull(around)) {
					RoleBase role = akaza.getGameState().getPlayerRoles().get(around);
					if (role instanceof PillierRoles) {
						PillierRoles pillierRoles = (PillierRoles) role;
						if (timeCroised.containsKey(pillierRoles.getName())) {
							int i = timeCroised.get(pillierRoles.getName());
							timeCroised.remove(pillierRoles.getName(), i);
							timeCroised.put(pillierRoles.getName(), i+1);
						} else {
							timeCroised.put(pillierRoles.getName(), 1);
						}
					}
				}
			}
			timeRemaining--;
		}
	}
}