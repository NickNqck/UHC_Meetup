package fr.nicknqck.roles.ds.demons.lune;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.demons.MuzanV2;
import fr.nicknqck.roles.ds.slayers.pillier.PilierRoles;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.packets.NMSPacket;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Akaza extends DemonsRoles implements Listener {

	private int regencooldown = 0;
	private TextComponent desc;
	private AkazaPilierRunnable runnable;
	private int coupInfliged = 0;
	private int coupToInflig = 50;
	public Akaza(UUID player) {
		super(player);
	}

	@Override
	public void RoleGiven(GameState gameState) {
		getKnowedRoles().add(MuzanV2.class);
		AutomaticDesc desc = new AutomaticDesc(this);
		desc.addEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
		desc.addParticularites(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous possédez une§c régénération§7 naturel à hauteur de§c 1/2"+ AllDesc.coeur+"§7 toute les§c 20 secondes§7.")}),
				new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Toute les§c 5 minutes§7 vous êtes informer du nombre de§c pilier§7 que vous avez croiser ces§c 5 dernières minutes§7.")}),
				new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Tout les§c 50 coups§7 infliger vous gagnerez§c 30 secondes§7 de l'effet§c Résistance I§7.")}),
				new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7A chaque mort d'une§c lune supérieur§7 les coups requis pour obtenir l'effet§c Résistance I§7 réduise de§c 5§7.")}));
		this.desc = desc.getText();
		givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
		this.runnable = new AkazaPilierRunnable(this);
		this.runnable.runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
		EventUtils.registerEvents(this);
		Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
			int amount = 5;
			for (GamePlayer gamePlayer : gameState.getGamePlayer().values()) {
                if (gamePlayer.getRole() instanceof DemonsRoles) {
					DemonsRoles role = (DemonsRoles) gamePlayer.getRole();
					if (role.getPlayer().equals(getPlayer()))continue;
					if (role.getRank().equals(DemonType.SUPERIEUR)) {
						amount--;
					}
                }
            }
			coupToInflig -= (amount*5);
		}, 20);
	}

	@Override
	public @NonNull DemonType getRank() {
		return DemonType.SUPERIEUR;
	}
	@Override
	public @NonNull TeamList getOriginTeam() {
		return TeamList.Demon;
	}
	@Override
	public @NonNull Roles getRoles() {
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
	private void onUHCBattle(UHCPlayerBattleEvent event) {
		if (event.isPatch()) {
			if (!event.getOriginEvent().isCancelled() && event.getOriginEvent().getDamager() instanceof Player) {
				if (event.getDamager().getUuid().equals(getPlayer())) {
					this.coupInfliged++;
					NMSPacket.sendActionBar((Player) event.getOriginEvent().getDamager(), "§7Coup infliger:§c "+coupInfliged+"§7/§6"+coupToInflig);
				}
			}
		}
	}
	@EventHandler
	private void onEndGame(EndGameEvent event) {
		this.runnable.cancel();
		HandlerList.unregisterAll(this);
	}
	@EventHandler
	private void onUHCDeath(UHCDeathEvent event) {
		if (!event.isCancelled()) {
			if (!event.getGameState().hasRoleNull(event.getPlayer().getUniqueId())) {
				if (event.getGameState().getGamePlayer().get(event.getPlayer().getUniqueId()).getRole() instanceof DemonsRoles) {
					DemonsRoles role = (DemonsRoles) event.getGameState().getGamePlayer().get(event.getPlayer().getUniqueId()).getRole();
					if (role.getRank().equals(DemonType.SUPERIEUR)) {
						this.coupToInflig-=5;
					}
				}
			}
		}
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
			if (!akaza.getGamePlayer().isAlive() || !GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
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
				if (!akaza.getGameState().hasRoleNull(around.getUniqueId())) {
					RoleBase role = akaza.getGameState().getGamePlayer().get(around.getUniqueId()).getRole();
					if (role instanceof PilierRoles) {
						PilierRoles pillierRoles = (PilierRoles) role;
						if (pillierRoles.getGamePlayer().isAlive()) {
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
			}
			timeRemaining--;
			NMSPacket.sendActionBar(owner, "§7Coup infliger:§c "+akaza.coupInfliged+"§7/§6"+akaza.coupToInflig);
			if (akaza.coupInfliged >= akaza.coupToInflig) {
				Bukkit.getScheduler().runTask(Main.getInstance(), () -> owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*30, 0, false, false), true));
				owner.sendMessage("§7Vous avez obtenue l'effet§c Résistance I§7 pendant§c 30 secondes§7.");
				akaza.coupInfliged = 0;
			}
		}
	}
}