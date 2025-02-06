package fr.nicknqck.roles.aot.builders.titans;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;

public class Assaillant extends Titan {

	@Override
	public void onInteract(PlayerInteractEvent e, Player player) {
		if (getOwner() != null) {
			if (player.getUniqueId() == getOwner()) {
				if (e.getItem().isSimilar(TransfoItem())) {
					Transfo();
				}
				if (e.getItem().isSimilar(ShutDown())) {
					if (ShutDownCooldown <=0) {
						for (Player p : Loc.getNearbyPlayersExcept(player, 50)) {
							getState().shutdown.add(p);
							player.sendMessage("§6§l"+p.getName()+"§7 est tomber sous l'emprise de votre§l ShutDown");
							p.sendMessage("§7Vous êtes maintenant sous l'effet du "+ShutDown().getItemMeta().getDisplayName()+"§7 du Titan "+getName());
							ShutDownCooldown = 60*10;
							p.setAllowFlight(true);
							p.setFlying(true);
							Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
								player.sendMessage("§6§l"+p.getName()+"§7 n'est plus sous l'emprise de votre§l ShutDown");
								getState().shutdown.remove(p);
								p.setAllowFlight(false);
								p.setFlying(false);
							}, 100);
						}
					}
				}
			}
		}
	}

	@Override
	public void onSecond() {
		Player player = Bukkit.getPlayer(getOwner());
		if (player != null) {
			if (ShutDownCooldown == 0) {
				player.sendMessage(ShutDown().getItemMeta().getDisplayName()+"§7 set à nouveau utilisable !");
				ShutDownCooldown--;
			}
			if (ShutDownCooldown >= 1) {
				ShutDownCooldown--;
			}
			if (isTransformedinTitan()) {
				if (getPlayerRole(getOwner()).getRoles() != Roles.Eren) {
					getPlayerRole(getOwner()).OLDgivePotionEffet(PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
					getPlayerRole(getOwner()).OLDgivePotionEffet(PotionEffectType.SPEED, 60, 1, true);
					getPlayerRole(getOwner()).OLDgivePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true);
				}
				int cd = getListener().getAssaillantCooldown()-(60*4);
				getPlayerRole(getOwner()).sendCustomActionBar(player,"§bTemp restant de transformation: "+StringUtils.secondsTowardsBeautiful(cd));
			}
			if (player.getItemInHand().isSimilar(TransfoItem())) {
				getPlayerRole(getOwner()).sendActionBarCooldown(player, getListener().getAssaillantCooldown());
			}
			
		}
	}
	@Override
	public void onBlockBreak(BlockBreakEvent e, Player player) {}

	@Override
	public void Transfo() {
		Player player = Bukkit.getPlayer(getOwner());
		if (player == null)return;
		if (!isTransformedinTitan()) {
			if (getListener().getAssaillantCooldown() <= 0) {
				setTransformedinTitan(true);
				TransfoMessage(player, true);
				player.sendMessage("§7Transformation en "+getName());
				getListener().setAssaillantCooldown(60*8);
				getPlayerRole(getOwner()).isTransformedinTitan = true;
				getPlayerRole(getOwner()).setResi(30);
				getPlayerRole(getOwner()).addBonusforce(10.0);
			} else {
				sendCooldown(player, getListener().getAssaillantCooldown());
			}
		} else {
			if (getListener().getAssaillantCooldown() < (60*8)-5) {
				setTransformedinTitan(false);
				TransfoMessage(player, false);
				player.sendMessage("§7Transformation en§l humain");
				getListener().setAssaillantCooldown(60*4);
				getPlayerRole(getOwner()).isTransformedinTitan = false;
				getPlayerRole(getOwner()).setResi(0);
				getPlayerRole(getOwner()).addBonusforce(-10.0);

			}else {
				player.sendMessage("§7Veuiller attendre un peux avant de vous détransformez");
			}
		}
	}
	@Override
	public void onAPlayerDie(Player player, Entity killer) {
		if (killer != null) {
			if (getOwner() != null) {
				if (player.getUniqueId() == getOwner()) {
					for (Player p : Loc.getNearbyPlayersExcept(player, 30)) {
						if (!getState().hasRoleNull(p.getUniqueId())) {
							if (getPlayerRole(p.getUniqueId()).isCanVoleTitan() && canStealTitan(p)) {
								canSteal.add(p);
								p.sendMessage("§7Vous pouvez maintenant volé le Titan§9 "+"§7 avec la commande§l /aot steal");
							}
						}
					}
					getListener().setAssaillant(null);
					for (ItemStack stack : Items()) {
						player.getInventory().removeItem(stack);
					}
				}
			}
		}
	}
	@Override
	public void resetCooldown() {
		ShutDownCooldown = 0;
		getListener().setAssaillantCooldown(0);
	}
	int ShutDownCooldown = 0;
	@Override
	public ItemStack[] Items() {
		return new ItemStack[] {
				TransfoItem(),
				ShutDown()
		};
	}
	private ItemStack TransfoItem() {
		return new ItemBuilder(Material.ROTTEN_FLESH).setName("§6§lTransformation").setLore("§7» Vous transforme en§c Titan ou vous fait redevenir humain").addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().toItemStack();
	}
	private ItemStack ShutDown() {
		return new ItemBuilder(Material.RECORD_11).setName("§7§lShutDown").addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().setLore("§fPermet d'empêcher§c tout les joueurs§f étant à moins de§6 50blocs§f de vous pendant 4s").toItemStack();
	}
	@Override
	public void onSteal(Player sender, String[] args) {
		if (canSteal.contains(sender)) {
			if (getOwner() == null) {
				if (getPlayerRole(sender.getUniqueId()).isCanVoleTitan()) {
					resetCooldown();
					getPlayerRole(sender.getUniqueId()).setCanVoleTitan(false);
					sender.sendMessage("§7Vous avez volé le Titan "+getName());
					getListener().setAssaillant(sender.getUniqueId());
					getPlayerRole(sender.getUniqueId()).giveItem(sender, true, Items());
					canSteal.clear();
				}
			}
		}
	}
	@Override
	public void onAotTitan(Player player, String[] args) {
		if (getListener().getAssaillant() == null)return;
		if (player.getUniqueId() != getListener().getAssaillant())return;
		player.sendMessage(new String[] {
				AllDesc.bar,
				"§7Titan:§6 Assaillant",
				"",
				"§7Votre transformation vous donne les effets: "+AllDesc.Speed+"§e 1§7, "+AllDesc.Force+"§c 1§7 ainsi que "+AllDesc.Resi+"§3 1",
				"",
				"§7Vous possédez l'item "+ShutDown().getItemMeta().getDisplayName()+"§7 qui vous permettra d'empêcher tout joueur étant à moins de 50blocs de bouger pendant 4s",
				"",
				AllDesc.bar
		});
	}
	@Override
	public UUID getOwner() {
		return getListener().getAssaillant();
	}
	@Override
	public void onGetDescription(Player player) {
		if (getOwner() == null)return;
		if (player.getUniqueId().equals(getOwner())) {
			getPlayerRole(getOwner()).sendMessageAfterXseconde(player, "§7Vous possédez le Titan "+getName(), 1);
		}
	}
	@Override
	public String getName() {
		return "§6Assaillant";
	}
	@Override
	public void onSubCommand(Player player, String[] args) {}
	@Override
	public void onPickup(PlayerPickupItemEvent e, Player player) {}
	List<Player> canSteal = new ArrayList<>();
	@Override
	public List<Player> getListforSteal() {
		return canSteal;
	}
	@Override
	public void PlayerKilled(Player player, Entity damager) {}
	@Override
	public void onPlayerAttackAnotherPlayer(Player damager, Player victim, EntityDamageByEntityEvent event) {}
}