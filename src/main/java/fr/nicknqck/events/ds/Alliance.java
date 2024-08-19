package fr.nicknqck.events.ds;

import fr.nicknqck.roles.builder.EffectWhen;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.events.EventBase;
import fr.nicknqck.events.Events;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.slayers.pillier.Kyojuro;
import fr.nicknqck.roles.ds.solos.Shinjuro;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.packets.NMSPacket;
import fr.nicknqck.utils.StringUtils;

import java.util.UUID;

public class Alliance extends EventBase{
	private Kyojuro kyojuro;
	private Shinjuro shinjuro;
	@Override
	public boolean PlayEvent(int gameTime) {
		if (!isActivated() && gameTime == getMinTime()) {
			if (gameState.attributedRole.contains(Roles.Kyojuro) && !gameState.DeadRole.contains(Roles.Kyojuro)) {
				if (gameState.attributedRole.contains(Roles.Shinjuro) && !gameState.DeadRole.contains(Roles.Shinjuro)) {
					Bukkit.broadcastMessage("L'évènement aléatoire "+Events.Alliance.getName()+"§r s'est activé, à partir de maintenant§e Shinjuro§r et§a Kyojuro§r gagne ensemble.§6 /ds alliance§r pour plus d'information...");
					for (UUID u : gameState.getInGamePlayers()) {
						Player p = Bukkit.getPlayer(u);
						if (p == null)continue;
						if (!gameState.hasRoleNull(p)) {
							RoleBase role = gameState.getPlayerRoles().get(p);
							if (role instanceof Kyojuro || role instanceof Shinjuro) {
								role.setTeam(TeamList.Alliance);
								if (role instanceof Kyojuro) {
									Kyojuro k = (Kyojuro) role;
									k.owner.sendMessage("Vous gagnez maintenant avec "+TeamList.Alliance.getColor()+gameState.getOwner(Roles.Shinjuro).getName());
									k.owner.sendMessage("Vous avez convaincue votre père d'arrêter l'alcool, temp que vous serez en vie il aura "+AllDesc.Force+" 1 proche de vous, de plus vous gagnez §c2"+AllDesc.coeur);
									k.giveHealedHeartatInt(2);
									this.kyojuro = k;
									k.setAlliance(true);
								}
								if (role instanceof Shinjuro) {
									Shinjuro s = (Shinjuro) role;
									s.owner.sendMessage("Vous gagnez maintenant avec "+TeamList.Alliance.getColor()+gameState.getOwner(Roles.Kyojuro).getName());
									s.owner.sendMessage("Votre fils vous à convaincue d'arrêter l'alcool, temp qu'il sera en vie vous obtiendrez "+AllDesc.Force+" 1 proche de lui, de plus vous aurez un traqueur vers lui.");
									s.owner.getInventory().removeItem(Items.getSake());
									s.setSakeCooldown(-1);
									this.shinjuro = s;
								}
							}
						}
					}
					return true;
				}
			}
		}
		return false;
	}
	@Override
	public void OnPlayerKilled(Player player, Player victim, GameState gameState) {
		if (this.kyojuro != null && this.shinjuro != null) {
			if (victim.getUniqueId().equals(kyojuro.getPlayer())) {
				this.shinjuro.givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
			} else if (victim.getUniqueId().equals(shinjuro.getPlayer())) {
				this.kyojuro.givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
			}
		}
	}
	@Override
	public void setupEvent() {
		setMinTime(GameState.getInstance().AllianceTime);
		System.out.println("Alliance will be start at "+StringUtils.secondsTowardsBeautiful(getMinTime()));
	}
	@Override
	public Events getEvents() {
		return Events.Alliance;
	}
	@Override
	public int getProba() {
		return GameState.getInstance().AllianceProba;
	}
	@Override
	public void onItemInteract(PlayerInteractEvent event, ItemStack item, Player player) {}
	@Override
	public void onPlayerKilled(Entity damager, Player player, GameState gameState) {}
	@Override
	public void onSecond() {
		if (shinjuro != null && kyojuro != null) {
			Player kOwner = Bukkit.getPlayer(kyojuro.getPlayer());
			Player sOwner = Bukkit.getPlayer(shinjuro.getPlayer());
			if (kOwner == null|| sOwner == null)return;
			if (Loc.getNearbyPlayers(kOwner, 20).contains(sOwner)) {
				sOwner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), true);
				kOwner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), true);
			}
			NMSPacket.sendActionBar(sOwner, Loc.getDirectionMate(sOwner, kOwner, true));
			NMSPacket.sendActionBar(kOwner, Loc.getDirectionMate(kOwner, sOwner, true));
		}
	}
	@Override
	public void resetCooldown() {
		this.kyojuro = null;
		this.shinjuro = null;
	}
	@Override
	public void onPlayerDamagedByPlayer(EntityDamageByEntityEvent event, Player player, Entity damageur) {}
	@Override
	public boolean onSubDSCommand(Player sender, String[] args) {
		if (args[0].equalsIgnoreCase("alliance")) {
			sender.sendMessage(new String[] {
					AllDesc.bar,
					"§7Évènement:§r \""+getEvents().getName()+"\"",
					"",
					"§7Dans cette évènement aléatoire le rôle de§e Shinjuro§7 et de§a Kyojuro§7 rentre en§6 alliance§7 pour vaincre tout les autres joueurs de la partie,",
					"",
					"§aKyojuro§7 gagne l'effet§c Force 1§7 proche de§e Shinjuro§7 (20blocs), également il gagne§c 2"+AllDesc.coeur+"§7 permanent",
					"",
					"§eShinjuro§7 gagne l'effet§c Force 1§7 proche de§a Kyojuro§7 (20blocs), également il gagne un traqueur jusqu'à la fin de la partie pointant vers§a Kyojuro",
					"",
					"§7A la mort de l'un l'autre obtient l'effet§c Force I§7 permanent",
					"",
					AllDesc.bar
			});
			return true;
		}
		return false;
	}
}