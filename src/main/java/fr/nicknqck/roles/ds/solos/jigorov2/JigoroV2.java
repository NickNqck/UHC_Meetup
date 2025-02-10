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
import fr.nicknqck.roles.ds.demons.lune.Kaigaku;
import fr.nicknqck.roles.ds.slayers.ZenItsuV2;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.UUID;

public class JigoroV2 extends DemonsSlayersRoles implements Listener {

	private boolean killzen = false;
	private boolean killkai = false;
	private boolean killtwo = false;

	public JigoroV2(UUID player) {
		super(player);
        pacte = JigoroV2ChoosePacteEvent.Pacte.NON_CHOISIS;
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
		EventUtils.registerRoleEvent(this);
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
		if (pacte == JigoroV2ChoosePacteEvent.Pacte.SOLO) {
			return AllDesc.JigoroV2Pacte1;
		}
		return AllDesc.JigoroV2;
	}

	@Override
	public String getName() {
		return "Jigoro§7 (§6V2§7)";
	}

	@NonNull
	private JigoroV2ChoosePacteEvent.Pacte pacte;

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
						owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false));
						owner.sendMessage("La commande§6 /ds me§r à été mis-à-jour !");
						break;
					case KAIGAKU:
						owner.sendMessage("§7Vous avez choisis le pacte§c 2");
						for (final UUID u : gameState.getInGamePlayers()) {//p = les gens en jeux
							final Player p = Bukkit.getPlayer(u);
							if (p == null)continue;
							if (!gameState.hasRoleNull(u)) { //vérifie que p a un role
								if (gameState.getGamePlayer().get(p.getUniqueId()).getRole() instanceof Kaigaku) {//si p est kaigaku
									final Kaigaku kaigaku = (Kaigaku) gameState.getGamePlayer().get(p.getUniqueId()).getRole();
									final JigoroV2PKaigaku jigoro = new JigoroV2PKaigaku(getPlayer(), kaigaku, getGamePlayer());
									jigoro.getEffects().putAll(getEffects());
									jigoro.getPowers().addAll(getPowers());
									jigoro.getGamePlayer().sendMessage(p.getName()+" est§c Kaigaku");
									kaigaku.setTeam(TeamList.Jigoro);
									jigoro.setTeam(TeamList.Jigoro);
									kaigaku.getGamePlayer().sendMessage("§7Le joueur§6 "+jigoro.getGamePlayer().getPlayerName()+"§7 est§6 "+jigoro.getName());
									kaigaku.getKnowedRoles().add(jigoro.getClass());
									kaigaku.givePotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
									kaigaku.getGamePlayer().sendMessage("Vous avez rejoint la team§6 Jigoro ");
									kaigaku.getGamePlayer().sendMessage("Votre pacte avec votre Sensei Jigoro vous à offert l'effet Speed 1 permanent");
									gameState.JigoroV2Pacte2 = true;
									jigoro.getGamePlayer().sendMessage("La commande§6 /ds me§r à été mis-à-jour !");
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
									final JigoroV2PZenItsu jigoro = new JigoroV2PZenItsu(getPlayer(), zenItsu, getGamePlayer());
									jigoro.getEffects().putAll(getEffects());
									jigoro.getPowers().addAll(getPowers());
									jigoro.getGamePlayer().sendMessage("§a"+p.getName()+"§f est§a ZenItsu");
									zenItsu.setTeam(TeamList.Jigoro);
									jigoro.setTeam(TeamList.Jigoro);
									zenItsu.getGamePlayer().sendMessage("Le joueur§6 "+owner.getName()+"§r est§6 Jigoro");
									zenItsu.getGamePlayer().sendMessage("Vous avez rejoint la team§6 Jigoro ");
									zenItsu.givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
									getGamePlayer().sendMessage("La commande§6 /ds me§r à été mis-à-jour !");
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
	@Override
	public void Update(GameState gameState) {
		if (pacte == JigoroV2ChoosePacteEvent.Pacte.SOLO) {
			if (killzen && !killtwo) {
				if (!gameState.nightTime) {
					owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*3, 0, false, false));
				}
			}
			if (killkai && !killtwo) {
				if (gameState.nightTime) {
					owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*3, 0, false, false));
				}
			}
			if (killzen && killkai && !killtwo) {
				killtwo = true;
				owner.sendMessage("On dirait que vous avez réussit à tuer vos deux disciple, vous êtes vraiment un être cruel !");
			}
			if (killtwo) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 0, false, false));
			}
		}
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (pacte == JigoroV2ChoosePacteEvent.Pacte.SOLO) {
			if (killer.getUniqueId() == getPlayer()) {
				if (victim.getUniqueId() != getPlayer()){
					if (gameState.getInGamePlayers().contains(victim.getUniqueId())) {
						if (!gameState.hasRoleNull(victim.getUniqueId())) {
							RoleBase role = gameState.getGamePlayer().get(victim.getUniqueId()).getRole();
							if (role instanceof ZenItsuV2) {
								if (!killzen) {
									addSpeedAtInt(owner, 10);
									owner.sendMessage(ChatColor.GRAY+"Vous venez de tuez "+ChatColor.GOLD+"Zen'Itsu "+ChatColor.GRAY+"vous obtenez donc "+ChatColor.RED+"résistance 1 le jour"+ChatColor.GRAY+", ainsi que "+ChatColor.GOLD+"10% de Speed");
									killzen = true;
								}					
							}
							if (role instanceof Kaigaku) {
								if (!killkai) {
									killkai = true;
									owner.sendMessage(ChatColor.GRAY+"Vous venez de tuez "+ChatColor.GOLD+"Kaigaku "+ChatColor.GRAY+"vous obtenez donc "+ChatColor.RED+"résistance 1 la nuit"+ChatColor.GRAY+", ainsi que "+ChatColor.GOLD+"10% de Speed");
									addSpeedAtInt(owner, 10);
								}
							}
						}
					}
				}
			}	
		}
		super.PlayerKilled(killer, victim, gameState);
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