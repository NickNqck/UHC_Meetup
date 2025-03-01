package fr.nicknqck.roles.ds.solos.jigorov2;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.events.custom.roles.ds.JigoroV2ChoosePacteEvent;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.DemonsSlayersRoles;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.roles.ds.demons.lune.KaigakuV2;
import fr.nicknqck.roles.ds.slayers.ZenItsuV2;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.UUID;

public class JigoroV2 extends DemonsSlayersRoles {

	public JigoroV2(UUID player) {
		super(player);
	}

	@Override
	public Soufle getSoufle() {
		return Soufle.FOUDRE;
	}

	@Override
	public void RoleGiven(GameState gameState) {
		super.RoleGiven(gameState);
		setCanuseblade(true);
		addKnowedRole(ZenItsuV2.class);
		addKnowedRole(KaigakuV2.class);
		setLameIncassable(owner, true);
		givePotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
		addPower(new SpeedTroisPower(this), true);
		addPower(new ChoosePactePower(this));
	}

	@Override
	public TeamList getOriginTeam() {
		return TeamList.Solo;
	}

	@Override
	public @NonNull Roles getRoles() {
		return Roles.JigoroV2;
	}

	@Override
	public String[] Desc() {
		return AllDesc.JigoroV2;
	}

	@Override
	public String getName() {
		return "Jigoro§7 (§6V2§7)";
	}

	@Override
	public ItemStack[] getItems() {
		return new ItemStack[0];
	}

	@Override
	public void resetCooldown() {}

	private static class SpeedTroisPower extends ItemPower {

		protected SpeedTroisPower(@NonNull RoleBase role) {
			super("Vitesse", new Cooldown(60 * 10), new ItemBuilder(Material.NETHER_STAR).setName("§6Vitesse"), role);
		}

		@Override
		public boolean onUse(Player player, Map<String, Object> map) {
			if (getInteractType().equals(InteractType.INTERACT)) {
				getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 60, 2, false, false), EffectWhen.NOW);
				return true;
			}
			return false;
		}
	}

	private static class ChoosePactePower extends CommandPower implements Listener {

		private boolean pacte;

		public ChoosePactePower(@NonNull RoleBase role) {
			super("/ds pacte", "pacte", null, role, CommandType.DS);
			setMaxUse(1);
			this.pacte = false;
		}

		@Override
		public boolean onUse(Player player, Map<String, Object> map) {
			final Inventory inv = Bukkit.createInventory(player, 27, "§fChoix de pacte§e Jigoro§7 (§6V2§7)");
			inv.setItem(11, GUIItems.getJigoroPacte1());
			inv.setItem(13, GUIItems.getJigoroPacte2());
			inv.setItem(15, GUIItems.getJigoroPacte3());
			player.openInventory(inv);
			EventUtils.registerEvents(this);
			return true;
		}

		@EventHandler
		private void InventoryCloseEvent(final InventoryCloseEvent event) {
			if (!event.getPlayer().getUniqueId().equals(getRole().getPlayer())) return;
			if (event.getInventory().getTitle() == null) return;
			if (event.getInventory().getTitle().isEmpty()) return;
			if (event.getInventory().getTitle().equals("§fChoix de pacte§e Jigoro§7 (§6V2§7)")) {
				if (!this.pacte) {
					setMaxUse(getMaxUse() + 1);
					event.getPlayer().sendMessage("Vous n'avez pas encore utiliser votre§c pacte");
				}
				EventUtils.unregisterEvents(this);
			}
		}

		@EventHandler
		private void InventoryClickEvent(final InventoryClickEvent event) {
			if (!event.getWhoClicked().getUniqueId().equals(getRole().getPlayer())) return;
			if (event.getCurrentItem() == null) return;
			if (event.getCurrentItem().getType().equals(Material.AIR)) return;
			if (event.getInventory().getTitle() == null) return;
			if (event.getInventory().getTitle().isEmpty()) return;
			if (event.getInventory().getTitle().equals("§fChoix de pacte§e Jigoro§7 (§6V2§7)")) {
				final ItemStack item = event.getCurrentItem();
				final JigoroV2ChoosePacteEvent choosePacteEvent = new JigoroV2ChoosePacteEvent(JigoroV2ChoosePacteEvent.Pacte.NON_CHOISIS, (Player) event.getWhoClicked());
				if (item.isSimilar(GUIItems.getJigoroPacte1())) {
					choosePacteEvent.setPacte(JigoroV2ChoosePacteEvent.Pacte.SOLO);
				}
				if (item.isSimilar(GUIItems.getJigoroPacte2())) {
					choosePacteEvent.setPacte(JigoroV2ChoosePacteEvent.Pacte.KAIGAKU);
				}
				if (item.isSimilar(GUIItems.getJigoroPacte3())) {
					choosePacteEvent.setPacte(JigoroV2ChoosePacteEvent.Pacte.ZENITSU);
				}
				Bukkit.getPluginManager().callEvent(choosePacteEvent);
			}
		}

		@EventHandler
		private void JigoroV2ChoosePacteEvent(final JigoroV2ChoosePacteEvent event) {
			if (event.isCancelled()) {
				event.getJigoro().sendMessage(event.getMessage());
				return;
			}
			if (!event.getJigoro().getUniqueId().equals(getRole().getPlayer())) return;
			final Player owner = event.getJigoro();
			final GameState gameState = getRole().getGameState();
			switch (event.getPacte()) {
				case SOLO:
					owner.sendMessage("§7Vous avez choisis le pacte§6 1");
					final JigoroV2PSolo jigoro = new JigoroV2PSolo(getRole().getPlayer(), getRole().getGamePlayer());
					jigoro.getEffects().putAll(getRole().getEffects());
					jigoro.getPowers().addAll(getRole().getPowers());
					break;
				case KAIGAKU:
					final KaigakuV2 kaigaku = findKaigaku(gameState);
					if (kaigaku == null) {
						setMaxUse(getMaxUse()+1);
						owner.sendMessage("§cKaigaku§f n'est pas présent dans la partie");
						owner.closeInventory();
						return;
					}
					owner.sendMessage("§7Vous avez choisis le pacte§c 2");
					final JigoroV2PKaigaku jigorok = new JigoroV2PKaigaku(getRole().getPlayer(), kaigaku, getRole().getGamePlayer());
					gameState.getGamePlayer().get(getRole().getPlayer()).setRole(jigorok);
					jigorok.getEffects().putAll(getRole().getEffects());
					jigorok.getPowers().addAll(getRole().getPowers());
					kaigaku.setTeam(TeamList.Jigoro);
					kaigaku.getGamePlayer().sendMessage("§7Le joueur§6 " + jigorok.getGamePlayer().getPlayerName() + "§7 est§6 " + jigorok.getName());
					kaigaku.getKnowedRoles().add(jigorok.getClass());
					kaigaku.givePotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
					kaigaku.getGamePlayer().sendMessage("Vous avez rejoint la team§6 Jigoro ");
					kaigaku.getGamePlayer().sendMessage("Votre pacte avec votre Sensei Jigoro vous à offert l'effet Speed 1 permanent");
					gameState.JigoroV2Pacte2 = true;
					break;
				case ZENITSU:
					getRole().getGamePlayer().sendMessage("§7Vous avez choisis le pacte§6 3");
					gameState.JigoroV2Pacte3 = true;
					getRole().givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
					final ZenItsuV2 zenItsu = findZenItsu(gameState);
					if (zenItsu == null) {
						setMaxUse(getMaxUse()+1);
						owner.sendMessage("§aZen'Itsu§f n'est pas présent dans la partie");
						owner.closeInventory();
						return;
					}
					final JigoroV2PZenItsu jigoroz = new JigoroV2PZenItsu(getRole().getPlayer(), zenItsu, getRole().getGamePlayer());
					gameState.getGamePlayer().get(getRole().getPlayer()).setRole(jigoroz);
					jigoroz.getEffects().putAll(getRole().getEffects());
					jigoroz.getPowers().addAll(getRole().getPowers());
					zenItsu.setTeam(TeamList.Jigoro);
					zenItsu.getGamePlayer().sendMessage("Le joueur§6 " + jigoroz.getGamePlayer().getPlayerName() + "§r est§6 Jigoro");
					zenItsu.getGamePlayer().sendMessage("Vous avez rejoint la team§6 Jigoro ");
					zenItsu.givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
					break;
			}
			this.pacte = true;
			owner.closeInventory();
		}
		private KaigakuV2 findKaigaku(@NonNull final GameState gameState) {
			for (final UUID u : gameState.getInGamePlayers()) {//p = les gens en jeux
				final Player p = Bukkit.getPlayer(u);
				if (p == null) continue;
				if (!gameState.hasRoleNull(u)) { //vérifie que p a un role
					if (gameState.getGamePlayer().get(p.getUniqueId()).getRole() instanceof KaigakuV2) {//si p est kaigaku
                        return (KaigakuV2) gameState.getGamePlayer().get(p.getUniqueId()).getRole();
					}
				}
			}
			return null;
		}
		private ZenItsuV2 findZenItsu(@NonNull final GameState gameState) {
			for (final UUID u : gameState.getInGamePlayers()) {//p = les gens en jeux
				final Player p = Bukkit.getPlayer(u);
				if (p == null) continue;
				if (!gameState.hasRoleNull(u)) {//vérifie que p a un role
					if (gameState.getGamePlayer().get(u).getRole() instanceof ZenItsuV2) {//si p est ZenItsu
                        return (ZenItsuV2) gameState.getGamePlayer().get(u).getRole();
					}
				}
			}
			return null;
		}
	}
}