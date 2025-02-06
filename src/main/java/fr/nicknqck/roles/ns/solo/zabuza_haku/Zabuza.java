package fr.nicknqck.roles.ns.solo.zabuza_haku;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.events.custom.roles.PowerActivateEvent;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.NSRoles;
import fr.nicknqck.utils.AttackUtils;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Zabuza extends NSRoles implements Listener {

	private boolean HakuDeath = false;
	private InvisibilitePower invisibilitePower;

	public Zabuza(UUID player) {
		super(player);
	}
	@Override
	public void RoleGiven(GameState gameState) {
		setChakraType(Chakras.SUITON);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
			if (!gameState.attributedRole.contains(Roles.Haku)) {
				onHakuDeath(false);
				owner.sendMessage("§bHaku§7 n'est pas dans la partie, vous récupérez donc le bonus dû à sa mort");
			}
		}, 20*10);
		EventUtils.registerEvents(this);
		this.invisibilitePower = new InvisibilitePower(this);
		addPower(this.invisibilitePower, true);
	}

	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}

	@Override
	public GameState.Roles getRoles() {
		return Roles.Zabuza;
	}
	@Override
    public String[] Desc() {
        KnowRole(owner, Roles.Haku, 5);
        return new String[] {
                AllDesc.bar,
                AllDesc.role+"§bZabuza",
                AllDesc.objectifteam+"§bZabuza et Haku",
                "",
                AllDesc.effet,
                "",
                AllDesc.point+"§bSpeed I §fet §cForce I §fpermanents.",
                "",
                AllDesc.items,
                "",
                AllDesc.point+ KubikiribochoItem().getItemMeta().getDisplayName()+" §f: Épée en diamant§7 Tranchant IV",
                "",
                AllDesc.point+"§aInvisibilité§f: Vous devenez §ainvisible §fen portant votre armure jusqu'à votre prochain coup (§c5 minutes maximum§f). De plus, lorsque vous êtes §ainvisible§f, vous laissez apparaître des §7particules blanches §fsur vos pas que seuls vous et §bHaku §fpeuvent voir.§7 (1x/5m)",
                "",
                AllDesc.particularite,
                "",
                AllDesc.point+"En frappant un joueur avec §bKubikiribôchô§f, vous aurez §c5% §fde vous§d régenerez§f§c 2"+AllDesc.coeur+".",
                "",
                "Vous connaissez l'§aidentité §fd'§bHaku §fet obtenez l'effet §bSpeed II§f, pendant §c5 minutes§f, à sa §cmort§f.",
                AllDesc.point+"Vous possédez un §cchat privé §favec §bHaku§f. Vous pouvez §acommuniquer §favec ce dernier en ajoutant le préfixe §c!§f devant vos messages.",
                "",
                AllDesc.chakra+getChakras().getShowedName(),
                AllDesc.bar
        };
    }
	@Override
	public TeamList getOriginTeam() {
		return TeamList.Zabuza_et_Haku;
	}
	@SuppressWarnings("deprecation")
	@Override
	public void onAllPlayerChat(org.bukkit.event.player.PlayerChatEvent e, Player p) {
		if (p.getUniqueId() == owner.getUniqueId()) {
			if (e.getMessage().startsWith("!")) {
				String msg = e.getMessage();
				Player Haku = getPlayerFromRole(Roles.Haku);
				owner.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bZabuza: "+msg.substring(1)));
				if (Haku != null) {
					Haku.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bZabuza: "+msg.substring(1)));
				}
			}
		}
	}
	@Override
	public @NonNull Intelligence getIntelligence() {
		return Intelligence.INTELLIGENT;
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				KubikiribochoItem()
		};
	}
	@Override
	public void resetCooldown() {
	}
	private ItemStack KubikiribochoItem() {
		return new ItemBuilder(Material.DIAMOND_SWORD).setName("§bKubikiribôchô").setLore("§7La légéndaire épée de §bZabuza").addEnchant(Enchantment.DAMAGE_ALL, 4).setUnbreakable(true).hideEnchantAttributes().toItemStack();
	}
	@Override
	public void onALLPlayerDamageByEntity(EntityDamageByEntityEvent event, Player victim, Entity entity) {
		if (entity.getUniqueId() == owner.getUniqueId()) {
			if (gameState.getInGamePlayers().contains(victim.getUniqueId())) {
				if (owner.getItemInHand().isSimilar(KubikiribochoItem())) {
					if (this.invisibilitePower.invisible) return;
					if (RandomUtils.getOwnRandomProbability(5)) {
						Heal(owner, 4.0);
						owner.sendMessage("Vous venez de vous §drégénerez§r de§c 2"+AllDesc.coeur);
					}
				}
			}
		}
	}
	@Override
	public void Update(GameState gameState) {
		OLDgivePotionEffet(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false);
		OLDgivePotionEffet(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, false);
	}
	@Override
	public void OnAPlayerDie(Player player, GameState gameState, Entity killer) {
		if (gameState.getGamePlayer().get(player.getUniqueId()).getRole() instanceof Haku && !HakuDeath) {
			onHakuDeath(true);
		}
	}
	private void onHakuDeath(boolean msg) {
		HakuDeath = true;
		if (msg) {
			owner.sendMessage("§bHaku§7 est mort, pour vous vengez vous obtenez§c 10 minutes§f de§e Speed 2");
		}
		OLDgivePotionEffet(PotionEffectType.SPEED, 20*60*10, 2, true);
	}

	@Override
	public String getName() {
		return "Zabuza";
	}
	@EventHandler
	private void onEndGame(EndGameEvent event) {
		EventUtils.unregisterEvents(this);
	}
	@EventHandler
	private void onPowerUse(PowerActivateEvent event) {
		if (event.isCancel())return;
		if (event.getPower().getCooldown() != null) {
			if (event.getPower().isCooldownResetSended())return;
		}
		String name = event.getPower().getName();
		Player owner = Bukkit.getPlayer(getPlayer());
		if (owner == null)return;
		owner.sendMessage("§aLe pouvoir \"§r"+name+"§a\" a été utiliser par§c "+event.getPlayer().getName());
	}
	private static class InvisibilitePower extends ItemPower {

		private InvisibiliteRunnable runnable;
		private final Zabuza zabuza;
		private final HashMap<Integer, ItemStack> armorContents = new HashMap<>();
		private boolean invisible = false;
		private final Cooldown cooldown;

		protected InvisibilitePower(Zabuza role) {
			super("Invisibilité", null, new ItemBuilder(Material.NETHER_STAR).setName("§aInvisibilité").setLore("§7Vous permez de devenir invisible"), role);
			this.zabuza = role;
			this.cooldown = new Cooldown(60*5);
		}

		@Override
		public boolean onUse(Player player, Map<String, Object> args) {
			if (getInteractType().equals(InteractType.INTERACT)) {
				if (cooldown.isInCooldown()) {
					zabuza.sendCooldown(player, cooldown.getCooldownRemaining());
					return false;
				}
				if (this.invisible) {
					this.runnable.timeLeft = 0;
					return false;
				}
				player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20*60*5, 0, false, false), true);
				if (player.getInventory().getHelmet() != null) {
					armorContents.put(1, player.getInventory().getHelmet());
					player.getInventory().setHelmet(null);
				}
				if (player.getInventory().getChestplate() != null) {
					armorContents.put(2, player.getInventory().getChestplate());
					player.getInventory().setChestplate(null);
				}
				if (player.getInventory().getLeggings() != null) {
					armorContents.put(3, player.getInventory().getLeggings());
					player.getInventory().setLeggings(null);
				}
				if (player.getInventory().getBoots() != null) {
					armorContents.put(4, player.getInventory().getBoots());
					player.getInventory().setBoots(null);
				}
				player.sendMessage("§aVous êtes maintenant invisible.");
				AttackUtils.CantAttack.add(player.getUniqueId());
				AttackUtils.CantReceveAttack.add(player.getUniqueId());
				runnable = new InvisibiliteRunnable(this);
				return true;
			}
			return false;
		}

		private void removeInvisibility() {
			Player owner = Bukkit.getPlayer(zabuza.getPlayer());
			if (owner == null)return;
			AttackUtils.CantAttack.remove(owner.getUniqueId());
			AttackUtils.CantReceveAttack.remove(owner.getUniqueId());
			owner.sendMessage("§cVous n'êtes plus invisible.");
			owner.removePotionEffect(PotionEffectType.INVISIBILITY);
			this.runnable.timeLeft = 0;
			if (armorContents.get(1) != null) {
				owner.getInventory().setHelmet(armorContents.get(1));
			}
			if (armorContents.get(2) != null) {
				owner.getInventory().setChestplate(armorContents.get(2));
			}
			if (armorContents.get(3) != null) {
				owner.getInventory().setLeggings(armorContents.get(3));
			}
			if (armorContents.get(4) != null) {
				owner.getInventory().setBoots(armorContents.get(4));
			}
			this.runnable.cancel();
			this.runnable = null;
			cooldown.use();
		}

		private static class InvisibiliteRunnable extends BukkitRunnable {

			private final Zabuza zabuza;
			private int timeLeft = 60*5*20;
			private final GameState gameState;
			private final InvisibilitePower power;
			public InvisibiliteRunnable(InvisibilitePower power) {
				this.zabuza = power.zabuza;
				this.gameState = this.zabuza.getGameState();
				this.power = power;
				runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
				this.power.invisible = true;
			}

			@Override
			public void run() {
				if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
					cancel();
					return;
				}
				if (timeLeft <= 0) {
					this.power.removeInvisibility();
					this.power.invisible = false;
					return;
				}
				timeLeft--;
				int toshow = timeLeft/20;
				zabuza.sendCustomActionBar(zabuza.owner, "§bTemp d'invisibilité:§c "+(StringUtils.secondsTowardsBeautiful((60*5)-toshow)));
				for (Player p : zabuza.getGamePlayer().getLastLocation().getWorld().getPlayers()) {
					if (zabuza.getListPlayerFromRole(Roles.Haku).contains(p) || zabuza.getListPlayerFromRole(Roles.Zabuza).contains(p)) {
						MathUtil.sendParticleTo(p, EnumParticle.CLOUD, zabuza.owner.getLocation().clone());
					}
				}
			}
		}
	}
}