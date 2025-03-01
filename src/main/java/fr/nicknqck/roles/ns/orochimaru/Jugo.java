package fr.nicknqck.roles.ns.orochimaru;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.OrochimaruRoles;
import fr.nicknqck.roles.ns.orochimaru.edotensei.Orochimaru;
import fr.nicknqck.utils.TripleMap;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.RandomUtils;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Jugo extends OrochimaruRoles {

	private boolean kimimaroDeath = false;
	private int marqueCD = 0;
	private boolean orochimaruDeath = false;
	private TextComponent desc;
	public Jugo(UUID player) {
		super(player);
	}

	@Override
	public void RoleGiven(GameState gameState) {
		super.RoleGiven(gameState);
		setChakraType(getRandomChakras());
		Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
			if (!gameState.getAttributedRole().contains(Roles.Kimimaro)) {
				onKimimaroDeath(false);
				owner.sendMessage("§5Kimimaro§7 n'étant pas dans la composition de la partie vous avez reçus tout de même le bonus dû à sa mort (/§6ns me§7)");
			}
			if (!gameState.getAttributedRole().contains(Roles.Orochimaru)) {
				onOrochimaruDeath(false);
				owner.sendMessage("§5Orochimaru§7 n'étant pas dans la composition de la partie vous avez reçus tout de même le bonus dû à sa mort (/§6ns me§7)");
			}
		}, 20*5);
		givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
		AutomaticDesc automaticDesc = new AutomaticDesc(this);
		automaticDesc.setItems(new TripleMap<>(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous offre§c 3 minutes§7 d'effet en fonction de votre§c chance§7:\n\n" +
				"§7     →§a70%§7: Vous obtenez l'effet§e Speed 1\n" +
				"§7     →§c30%§7: Vous obtenez les effets§e Speed 1§7 et§9 Résistance 1§7 mais vous devennez un rôle§e Solo§f pendant§c 2m30s")}), "§5Marque maudite", 60*5));
		automaticDesc.addParticularites(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous possédez l'identité de§5 Kimimaro")}),
				new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7A la mort de§5 Kimimaro§7 vous obtenez l'identité d'§5Orochimaru")}),
				new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7A la mort d'§5Orochimaru§7 vous obtenez l'identité de§5 Karin§7 et de§5 Suigetsu")}),
				new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Votre nature de chakra§c aléatoire§7, cette partie vous avez la nature de chakra: "+getChakras().getShowedName())}));
		this.desc = automaticDesc.getText();
		getKnowedRoles().add(Kimimaro.class);
	}

	@Override
	public @NonNull Roles getRoles() {
		return Roles.Jugo;
	}
	@Override
	public @NonNull Intelligence getIntelligence() {
		return Intelligence.PEUINTELLIGENT;
	}
	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}
	@Override
	public String[] Desc() {
		return new String[0];
	}

	@Override
	public TextComponent getComponent() {
		return this.desc;
	}

	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				MarqueMauditeItem()
		};
	}
	private ItemStack MarqueMauditeItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§5Marque maudite").setLore("§7Vous permet de devenir plus puissant pendant un court instant").toItemStack();
	}
	private void onKimimaroDeath(boolean msg) {
		kimimaroDeath = true;
		if (msg) {
			owner.sendMessage("§5Kimimaro§7 est mort, vous obtenez donc l'identité de son maitre§5 Orochimaru§7, (§6/ns me§7)");
		}
		getKnowedRoles().add(Orochimaru.class);
	}
	private void onOrochimaruDeath(boolean msg) {
		orochimaruDeath = true;
		if (msg) {
			owner.sendMessage("§7Maitre§5 Orochimaru§7 est mort, vous obtenez donc l'identité de vos nouveau amis,§5 Karin§f et§5 Suigetsu§7, (§6/ns me§7)");
		}
		getKnowedRoles().add(Karin.class);
		getKnowedRoles().add(Suigetsu.class);
	}
	@Override
	public void OnAPlayerDie(Player player, GameState gameState, Entity killer) {
		if (!gameState.hasRoleNull(player.getUniqueId())) {
			if (getListPlayerFromRole(Roles.Kimimaro).contains(player) &&!kimimaroDeath) {
				onKimimaroDeath(true);
			}
			if (getListPlayerFromRole(Roles.Orochimaru).contains(player) && !orochimaruDeath) {
				onOrochimaruDeath(true);
			}
		}
	}
	@Override
	public void Update(GameState gameState) {
		if (marqueCD >= 0) {
			marqueCD--;
			if (marqueCD == 0) {
				owner.sendMessage("§7Vous pouvez a nouveau utiliser votre§5 Marque maudite");
			}
		}
	}
	@SuppressWarnings("deprecation")
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(MarqueMauditeItem())) {
			if (marqueCD <= 0) {
				marqueCD = 60*8;
				if (RandomUtils.getOwnRandomProbability(70)) {
					owner.sendMessage("§7Vous obtenez l'effet§e Speed 1");
					OLDgivePotionEffet(PotionEffectType.SPEED, 20*60*3, 1, true);
                } else {
					owner.sendMessage("§7Vous obtenez l'effet§e Speed 1§7 et l'effet§9 Résistance 1");
					OLDgivePotionEffet(PotionEffectType.SPEED, 20*60*3, 1, true);
					OLDgivePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, 20*60*3, 1, true);
					setResi(20);
					TeamList oldTeam = getOriginTeam();
					setTeam(TeamList.Solo);
					owner.resetTitle();
					owner.sendTitle("Vous gagnez maintenant§e Seul", "Pendant 3 minutes");
					new BukkitRunnable() {
						int i =0;
						@Override
						public void run() {
							i++;
							if (gameState.getServerState() != GameState.ServerStates.InGame) {
								cancel();
							}
							if (owner != null && !gameState.hasRoleNull(owner.getUniqueId()) && i >= 60*3) {
								setTeam(oldTeam);
							}
						}
					}.runTaskTimer(Main.getInstance(), 0, 20);
                }
            }else {
				sendCooldown(owner, marqueCD);
            }
            return true;
        }
		return super.ItemUse(item, gameState);
	}
	@Override
	public void resetCooldown() {
		marqueCD = 0;
	}

	@Override
	public String getName() {
		return "Jugo";
	}
}