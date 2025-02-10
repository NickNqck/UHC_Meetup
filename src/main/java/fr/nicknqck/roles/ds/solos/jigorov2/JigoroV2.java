package fr.nicknqck.roles.ds.solos.jigorov2;

import com.avaje.ebean.validation.NotNull;
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
import fr.nicknqck.roles.ds.demons.lune.Kaigaku;
import fr.nicknqck.roles.ds.slayers.ZenItsuV2;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.UUID;

public class JigoroV2 extends DemonsSlayersRoles {

	@NotNull
	private JigoroV2ChoosePacteEvent.Pacte pacte;

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
		pacte = JigoroV2ChoosePacteEvent.Pacte.NON_CHOISIS;
		setCanuseblade(true);
		addKnowedRole(ZenItsuV2.class);
		addKnowedRole(Kaigaku.class);
		setLameIncassable(owner, true);
		givePotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
		addPower(new SpeedTroisPower(this), true);
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
	@Override
	public void OpenFormInventory(GameState gameState) {
		if (pacte == JigoroV2ChoosePacteEvent.Pacte.NON_CHOISIS) {
			Inventory inv = Bukkit.createInventory(owner, 9, "Choix de forme");
			inv.setItem(2, GUIItems.getJigoroPacte1());
			inv.setItem(4, GUIItems.getJigoroPacte2());
			inv.setItem(6, GUIItems.getJigoroPacte3());
			owner.openInventory(inv);
		} else {
			owner.sendMessage("Vous devez regretter votre Pacte pour vouloir le changer...");
		}
		super.OpenFormInventory(gameState);
	}
	@Override
	public void FormChoosen(ItemStack item, GameState gameState) {
		if (pacte == JigoroV2ChoosePacteEvent.Pacte.NON_CHOISIS) {
			JigoroV2ChoosePacteEvent choosePacteEvent = new JigoroV2ChoosePacteEvent(JigoroV2ChoosePacteEvent.Pacte.NON_CHOISIS, owner);
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
			if (!choosePacteEvent.isCancelled()) {
				this.pacte = choosePacteEvent.getPacte();
				switch (choosePacteEvent.getPacte()) {
					case SOLO:
						owner.sendMessage("§7Vous avez choisis le pacte§6 1");
						final JigoroV2PSolo jigoro = new JigoroV2PSolo(getPlayer(), getGamePlayer());
						jigoro.getEffects().putAll(getEffects());
						jigoro.getPowers().addAll(getPowers());
						jigoro.getGamePlayer().sendMessage("La commande§6 /ds me§r à été mis-à-jour !");
						break;
					case KAIGAKU:
						owner.sendMessage("§7Vous avez choisis le pacte§c 2");
						for (final UUID u : gameState.getInGamePlayers()) {//p = les gens en jeux
							final Player p = Bukkit.getPlayer(u);
							if (p == null)continue;
							if (!gameState.hasRoleNull(u)) { //vérifie que p a un role
								if (gameState.getGamePlayer().get(p.getUniqueId()).getRole() instanceof Kaigaku) {//si p est kaigaku
									final Kaigaku kaigaku = (Kaigaku) gameState.getGamePlayer().get(p.getUniqueId()).getRole();
									final JigoroV2PKaigaku jigorok = new JigoroV2PKaigaku(getPlayer(), kaigaku, getGamePlayer());
									jigorok.getEffects().putAll(getEffects());
									jigorok.getPowers().addAll(getPowers());
									jigorok.getGamePlayer().sendMessage(p.getName()+" est§c Kaigaku");
									kaigaku.setTeam(TeamList.Jigoro);
									jigorok.setTeam(TeamList.Jigoro);
									kaigaku.getGamePlayer().sendMessage("§7Le joueur§6 "+jigorok.getGamePlayer().getPlayerName()+"§7 est§6 "+jigorok.getName());
									kaigaku.getKnowedRoles().add(jigorok.getClass());
									kaigaku.givePotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
									kaigaku.getGamePlayer().sendMessage("Vous avez rejoint la team§6 Jigoro ");
									kaigaku.getGamePlayer().sendMessage("Votre pacte avec votre Sensei Jigoro vous à offert l'effet Speed 1 permanent");
									gameState.JigoroV2Pacte2 = true;
									jigorok.getGamePlayer().sendMessage("La commande§6 /ds me§r à été mis-à-jour !");
									break;
								}
							}
						}
						break;
					case ZENITSU:
						getGamePlayer().sendMessage("§7Vous avez choisis le pacte§6 3");
						gameState.JigoroV2Pacte3 = true;
						givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
						for (final UUID u : gameState.getInGamePlayers()) {//p = les gens en jeux
							final Player p = Bukkit.getPlayer(u);
							if (p == null)continue;
							if (!gameState.hasRoleNull(u)) {//vérifie que p a un role
								if (gameState.getGamePlayer().get(u).getRole() instanceof ZenItsuV2) {//si p est ZenItsu
									final ZenItsuV2 zenItsu = (ZenItsuV2) gameState.getGamePlayer().get(u).getRole();
									final JigoroV2PZenItsu jigoroz = new JigoroV2PZenItsu(getPlayer(), zenItsu, getGamePlayer());
									jigoroz.getEffects().putAll(getEffects());
									jigoroz.getPowers().addAll(getPowers());
									jigoroz.getGamePlayer().sendMessage("§a"+p.getName()+"§f est§a ZenItsu");
									zenItsu.setTeam(TeamList.Jigoro);
									jigoroz.setTeam(TeamList.Jigoro);
									zenItsu.getGamePlayer().sendMessage("Le joueur§6 "+jigoroz.getGamePlayer().getPlayerName()+"§r est§6 Jigoro");
									zenItsu.getGamePlayer().sendMessage("Vous avez rejoint la team§6 Jigoro ");
									zenItsu.givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
									jigoroz.getGamePlayer().sendMessage("La commande§6 /ds me§r à été mis-à-jour !");
								}
							}
						}
						break;
                    default:
						break;
				}
			} else {
				owner.sendMessage(choosePacteEvent.getMessage());
			}
		}
		super.FormChoosen(item, gameState);
	}
	private static class SpeedTroisPower extends ItemPower {

		protected SpeedTroisPower(@NonNull RoleBase role) {
			super("Vitesse", new Cooldown(60*10), new ItemBuilder(Material.NETHER_STAR).setName("§6Vitesse"), role);
		}

		@Override
		public boolean onUse(Player player, Map<String, Object> map) {
			if (getInteractType().equals(InteractType.INTERACT)) {
				getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60, 2, false, false), EffectWhen.NOW);
				return true;
			}
			return false;
		}
	}
}