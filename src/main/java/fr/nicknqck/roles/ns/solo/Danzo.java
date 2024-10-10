package fr.nicknqck.roles.ns.solo;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.ns.builders.NSRoles;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.UchiwaRoles;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.powers.Power;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class Danzo extends NSRoles {

	private int futonCD = 0;
	private int izanagiItemCD = 0;
	private int sceauCD = 0;
	private int coupToScelled = 0;
	private boolean SceauActived = false;
	private final List<UUID> cantHaveAbso = new ArrayList<>();
	@Getter
	private boolean killHokage = false;
	private final HashMap<Player, SceauAction> inSceau = new HashMap<>();

	public Danzo(UUID player) {
		super(player);
		setChakraType(Chakras.FUTON);
	}
	@Override
	public void RoleGiven(GameState gameState) {
		giveHealedHeartatInt(2);
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
			int nmbUchiwa = 0;
			for (Player p : Bukkit.getOnlinePlayers()){
				if (isUchiwa(p)){
					nmbUchiwa++;
				}
			}
			if (nmbUchiwa == 0){
				owner.sendMessage("§7Il n'y a pas de§c Uchiwa§7 dans la partie, vous obtenez donc directement l'effet§c Résistance I permanent");
				killUchiwa = true;
			}
		}, 20*10);
		givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1, 0), EffectWhen.PERMANENT);
		givePotionEffect(new PotionEffect(PotionEffectType.SPEED,1 , 0), EffectWhen.PERMANENT);
		addPower(new UchiwaFinders(this));
	}
	@Override
	public TeamList getOriginTeam() {
		return TeamList.Solo;
	}
	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}
	@Override
	public GameState.Roles getRoles() {
		return Roles.Danzo;
	}
	@Override
	public String[] Desc() {
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§eDanzo",
				AllDesc.objectifsolo+"§e Seul",
				"",
				AllDesc.effet,
				"",
				"Vous possédez les effets§e Speed 1§f et§c Force 1§f permanent",
				"",
				AllDesc.items,
				"",
				AllDesc.point+"§aFûton§f: Vous propulse en avant",
				"",
				AllDesc.point+IzanagiItem().getItemMeta().getDisplayName()+": En visant un joueur, vous permet de vous téléportez dans les§c 10 blocs§f autours d'un joueur, également, vous gagnerez§e 2 pommes d'or§f, de plus, vous §dvous régénéreriez entièrement§f, cependant, vous perdrez§c 1/2"+AllDesc.coeur+"§c permanent",
				"",
				AllDesc.point+"Sceau: En visant un joueur, vous permet de lui imposer un sceau ayant un effet aléatoire la cible obtiendra sois l'effet§1 Wither II§f pendant§c 12s§f, sois, l'incapacité d'obtenir l'effet§e Absorbtion§f via des§e pommes d'or§f pendant§c 8 secondes§f.",
				"§c⚠ Pour activer le §nSceau§c il faudra infliger 15 coups à la cible ⚠",
				"",
				AllDesc.commande,
				"",
				"§6/ns izanagi§f: Permet (1x par partie) de vous remettre full vie et de vous donnez§e 5 pommes en or§f, en contrepartie, vous perdez§c 1"+AllDesc.coeur+"§f permanent",
				"",
				AllDesc.particularite,
				"",
				"Si vous parvenez à tuer un membre du clan§4§l Uchiwa§f vous obtiendrez l'effet "+AllDesc.Resi+"§9 1 permanent",
				"Vous infligez§c +10%§f de dégat au§4§l Uchiwa",
				"Toute les§c 2 minutes§7 vous obtenez l'information si un§4§l Uchiwa§7 est présent autours de vous ou non (§c15 blocs§7), s'il y en a un vous obtiendrez son rang (§6/ns uchirang§7)",
				"",
				AllDesc.chakra+getChakras().getShowedName(),
				AllDesc.bar
		};
	}
	@Override
	public void Update(GameState gameState) {
		if (killUchiwa) {
			givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, false);
		}
		if (futonCD >= 0) {
			futonCD--;
			if (futonCD == 0) {
				owner.sendMessage("§aFûton§7 est à nouveau utilisable !");
			}
		}
		if (izanagiItemCD >= 0) {
			izanagiItemCD--;
			if (izanagiItemCD == 0) {
				owner.sendMessage("§cIzanagi§f (§cOffensif§f)§7 est à nouveau utilisable !");
			}
		}
		if (sceauCD >= 0) {
			sceauCD--;
			if (sceauCD == 0) {
				owner.sendMessage("Sceau§7 est à nouveau utilisable !");
			}
		}
	}
	private ItemStack FutonItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§aFûton").setLore("§7Vous permet de vous propulsez").toItemStack();
	}
	private ItemStack IzanagiItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§cIzanagi§f (§cOffensif§f)").setLore("§7Vous permet de vous téléportez autours de la cible").toItemStack();
	}
	public ItemStack SceauItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("Sceau").setLore("§7Permet de sceller un joueur.").toItemStack();
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (killer.getUniqueId() == owner.getUniqueId()) {
			if (gameState.getHokage() != null) {
				if (gameState.getHokage().Hokage != null) {
					if (gameState.getHokage().Hokage.equals(victim.getUniqueId())) {
						killer.sendMessage("§7Lors de la prochaine élection de l'§cHokage§7 vous serez obligatoirement élu");
						this.killHokage = true;
					}
				}
			}
			if (owner.getMaxHealth() < 24.0) {
				setMaxHealth(owner.getMaxHealth()+1.0);
				owner.setMaxHealth(getMaxHealth());
				owner.sendMessage("§7Tuer un joueur vous a rendu §c1/2"+AllDesc.coeur+"§7 permanent");
			}
			if (isUchiwa(victim)) {
				owner.sendMessage("§7Vous venez de tuer un de ces démons du clan §4§lUchiwa !");
				if (!killUchiwa) {
					killUchiwa = true;
					owner.sendMessage("§7Vous obtenez l'effet §9Résistance 1§7 de manière permanente");
				}
			}
		}
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				FutonItem(),
				IzanagiItem(),
				SceauItem()
		};
	}
	private boolean izanagi = false;
	private boolean killUchiwa = false;
	@Override
	public void onNsCommand(String[] args) {
		if (args[0].equalsIgnoreCase("izanagi")) {
			if (!izanagi) {
				setMaxHealth(getMaxHealth()-2.0);
				owner.setMaxHealth(getMaxHealth());
				owner.setHealth(getMaxHealth());
				giveItem(owner, false, new ItemStack(Material.GOLDEN_APPLE, 5));
				izanagi = true;
			}
		}
	}
	private boolean isUchiwa(Player p){
		if (!gameState.hasRoleNull(p)) {
            return gameState.getPlayerRoles().get(p) instanceof UchiwaRoles;
		}
		return false;
	}

	@Override
	public String getName() {
		return "Danzo";
	}

	private enum SceauAction {
		Wither(),
		AntiAbso()
	}

	@Override
	public void resetCooldown() {
		izanagiItemCD = 0;
		sceauCD = 0;
		futonCD = 0;
	}
	@Override
	public void onALLPlayerDamageByEntity(EntityDamageByEntityEvent event, Player victim, Entity entity) {
		if (entity.getUniqueId().equals(owner.getUniqueId())){
			if (isUchiwa(victim)){
				event.setDamage(event.getDamage()*1.1);
			}
		}
		if (inSceau.containsKey(victim)) {
			if (entity.getUniqueId() == owner.getUniqueId()) {
				if (coupToScelled == 15 && !SceauActived) {
					owner.sendMessage("§7Votre Sceau c'est activé");
					victim.sendMessage("§7Vous subissez les effets d'un sceau inconnu...");
					if (inSceau.get(victim) == SceauAction.Wither) {
						SceauActived = true;
						givePotionEffet(victim, PotionEffectType.WITHER, 20*12, 2, false);
						Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
							owner.sendMessage("§7Votre sceau ne fais plus effet sur "+victim.getDisplayName());
							victim.sendMessage("§7Les effets du sceau se dissipent");
							inSceau.remove(victim, inSceau.get(victim));
							coupToScelled = 0;
							SceauActived = false;
						}, 20*12);
					}
					if (inSceau.get(victim).equals(SceauAction.AntiAbso)){
						SceauActived = true;
						cantHaveAbso.add(victim.getUniqueId());
						Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
							victim.sendMessage("§7Les effets du sceau se dissipent");
							owner.sendMessage("§7Votre sceau ne fais plus effet sur "+victim.getDisplayName());
							inSceau.remove(victim, inSceau.get(victim));
							coupToScelled = 0;
							SceauActived = false;
							cantHaveAbso.remove(victim.getUniqueId());
						}, 20*8);
					}
				} else {
					if (inSceau.size() == 1 && !SceauActived) {
						coupToScelled++;
					}
				}
			}
		}
	}

	@Override
	public void onALLPlayerEat(PlayerItemConsumeEvent e, ItemStack item, Player eater) {
		super.onALLPlayerEat(e, item, eater);
		if (cantHaveAbso.contains(eater.getUniqueId())){
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
				eater.removePotionEffect(PotionEffectType.ABSORPTION);
				eater.sendMessage("§cUn sceau vous empêche d'obtenir l'effet§e Absorbtion");
			}, 1);
		}
	}

	@Override
	public @NonNull Intelligence getIntelligence() {
		return Intelligence.INTELLIGENT;
	}

	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(SceauItem())) {
			if (sceauCD <= 0) {
				Player target = getTargetPlayer(owner, 30);
				if (target == null) {
					owner.sendMessage("§cIl faut viser un joueur !");
					return true;
				}
				owner.sendMessage("§7Vous placez un sceau sur §e§l§n"+target.getName());
				sceauCD = 60*5;
				if (RandomUtils.getOwnRandomProbability(50)) {
					inSceau.put(target, SceauAction.Wither);
				} else {
					inSceau.put(target, SceauAction.AntiAbso);
				}
				owner.sendMessage("§7Dans§c 15 coups§f "+target.getName()+"§7 subirat les effets de votre§c Sceau");
            } else {
				sendCooldown(owner, sceauCD);
            }
            return true;
        }
		if (item.isSimilar(IzanagiItem())) {
			if (izanagiItemCD <= 0) {
				Player target = getTargetPlayer(owner, 30);
				if (target == null) {
					owner.sendMessage("§cMerci de viser un joueur !");
					return true;
				}
				Location loc = Loc.getRandomLocationAroundPlayer(target, 10);
                owner.sendMessage("§cIzanagi !");
				owner.teleport(loc);
				izanagiItemCD = 60*3;
				setMaxHealth(getMaxHealth()-1.0);
				owner.setMaxHealth(getMaxHealth());
				owner.setHealth(owner.getMaxHealth());
				giveItem(owner, false, new ItemStack(Material.GOLDEN_APPLE, 2));
				owner.updateInventory();
            } else {
				sendCooldown(owner, izanagiItemCD);
            }
            return true;
        }
		if (item.isSimilar(FutonItem())) {
			if (futonCD <= 0) {
				Vector dir = owner.getLocation().getDirection();
				owner.setVelocity(dir.multiply(3));
				futonCD = 90;
				owner.sendMessage("§7Vous avez utilisé votre "+FutonItem().getItemMeta().getDisplayName());
            } else {
				sendCooldown(owner, futonCD);
            }
            return true;
        }
		return super.ItemUse(item, gameState);
	}

	@Getter
    private static class UchiwaFinders extends Power {

		private final FindersRunnable findersRunnable;

		public UchiwaFinders(Danzo role) {
			super("Chercheur d'Uchiwa", null, role);
			this.findersRunnable = new FindersRunnable(role, role.getGamePlayer());
		}

		@Override
		public boolean onUse(Player player, Map<String, Object> args) {
			return false;
		}
		@Getter
		private static class FindersRunnable extends BukkitRunnable {

			private final Danzo danzo;
			private final GamePlayer gamePlayer;

			public FindersRunnable(Danzo role, GamePlayer gamePlayer) {
				this.danzo = role;
				this.gamePlayer = gamePlayer;
				runTaskTimerAsynchronously(Main.getInstance(), 0, 20*120);
			}

			@Override
			public void run() {
				if (!danzo.getGameState().getServerState().equals(GameState.ServerStates.InGame)) {
					cancel();
					return;
				}
				Player owner = Bukkit.getPlayer(gamePlayer.getUuid());
				if (!gamePlayer.isAlive() || owner == null) {
					return;
				}

				List<Player> players = Loc.getNearbyPlayers(gamePlayer.getLastLocation(), 15);
				if (players.isEmpty()) {
					owner.sendMessage("§7Aucun membre de ce maudit clan des§4§l Uchiwas§7 n'est présent autours de vous (§c15 blocs§7)");
					return;
				}
				int nmbUchiwa = 0;
				for (Player player : players) {
					if (!danzo.getGameState().getGamePlayer().containsKey(player.getUniqueId())) continue;
					GamePlayer gm = danzo.getGameState().getGamePlayer().get(player.getUniqueId());
					if (!gm.isAlive())continue;
					if (gm.getRole() == null)continue;
					if (gm.getRole() instanceof UchiwaRoles) {
						nmbUchiwa++;
						owner.sendMessage("§7Il y a au moins un§4§l Uchiwa§7 autours de vous, son aura vous fait donné l'impression qu'il est§c "+((UchiwaRoles) gm.getRole()).getUchiwaType().getName());
						break;
					}
				}
				if (nmbUchiwa == 0) {
					owner.sendMessage("§7Aucun membre de ce maudit clan des§4§l Uchiwas§7 n'est présent autours de vous (§c15 blocs§7)");
				}
			}
		}
	}
}