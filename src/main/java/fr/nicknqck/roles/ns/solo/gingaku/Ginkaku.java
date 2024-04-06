package fr.nicknqck.roles.ns.solo.gingaku;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.StringUtils;

public class Ginkaku extends RoleBase{
	
	private final ItemStack KyubiItem = new ItemBuilder(Material.NETHER_STAR).setName("§6§lKyubi").setLore("§7Vous permet d'obtenir des effets").toItemStack();
	private int cdKyubi = 0;
	
	public Ginkaku(Player player, Roles roles, GameState gameState) {
		super(player, roles, gameState);
		owner.sendMessage(Desc());
	}
	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}
	@Override
	public String[] Desc() {
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§6Ginkaku",
				AllDesc.objectifsolo+"avec§6 Kinkaku",
				"",
				AllDesc.effet,
				"",
				AllDesc.point+"§9Résistance I§f proche de§6 Kinkaku§f et§e Speed I§f la "+AllDesc.nuit,
				"",
				AllDesc.items,
				"",
				AllDesc.point+"§6§lKyubi§f: Pendant§c 3 minutes§f vous offre des effets, cependant ils changent chaque minutes: ",
				AllDesc.tab+"§aPremière minute§f: Vous obtenez les effets§e Speed II§f ainsi que§c Force I§f.",
				AllDesc.tab+"§6Deuxième minute§f: Vous obtenez les effets§e Speed I§f ainsi que§c Force I§f.",
				AllDesc.tab+"§cTroisième minute§f: Vous obtenez l'effet§e Speed I§f.",
				"",
				AllDesc.point+"§6Corde d'or§f: En visant un joueur, le repousse en l'air, puis, lorsqu'il attérit, l'empêche de bouger pendant§c 5s§f."
				
		};
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(KyubiItem)) {
			if (cdKyubi <= 0) {
				owner.sendMessage("§7Activation de§6 Kyubi");
				cdKyubi = 60*18;
				new BukkitRunnable() {
					int time = 60;
					int state = 3;
					@Override
					public void run() {
						if (owner == null) {
							cancel();
							return;
						}
						if (gameState.getServerState() != ServerStates.InGame) {
							cancel();
							return;
						}
						if (state == 0) {
							owner.sendMessage("§7L'utilisation du chakra de§6 Kyubi§7 n'est ");
							cancel();
							return;
						}
						if (state == 3) {
							givePotionEffet(PotionEffectType.SPEED, 60, 2, true);
							givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
						}
						if (state == 2) {
							givePotionEffet(PotionEffectType.SPEED, 60, 1, true);
							givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
						}
						if (state == 1) {
							givePotionEffet(PotionEffectType.SPEED, 60, 1, true);
						}
						if (time == 0) {
							state--;
						}
						sendCustomActionBar(owner, "Temp avant prochain stade de§6 Kyubi§f:§c "+StringUtils.secondsTowardsBeautiful(time));
						time--;
					}
				}.runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
			} else {
				sendCooldown(owner, cdKyubi);
				return true;
			}
		}
		return super.ItemUse(item, gameState);
	}
	@Override
	public void Update(GameState gameState) {
		if (gameState.nightTime && cdKyubi < 60*10) {
			givePotionEffet(PotionEffectType.SPEED, 60, 1, true);
		}
		if (cdKyubi >= 0) {
			cdKyubi--;
			if (cdKyubi == 0) {
				owner.sendMessage("§7Vous pouvez à nouveau utiliser§6 Kyubi§7.");
			}
		}
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				KyubiItem	
		};
	}

	@Override
	public void resetCooldown() {
		
	}
}