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

import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.Loc;

public class Machoire extends Titan{

	@Override
	public void onInteract(PlayerInteractEvent e, Player player) {
		if (e.getPlayer() != player)return;
		if (e.getItem() != null) {
			if (getListener().getMachoire() == null)return;
			if (e.getItem().isSimilar(TransfoItem())) {
				if (player.getUniqueId() == getOwner()) {
					Transfo();
				}
			}
			if (e.getItem().isSimilar(getJump())) {
				if (isTransformedinTitan()) {
					if (JumpCooldown <= 0) {
						org.bukkit.util.Vector direction = player.getLocation().getDirection();
			            direction.setY(0.8);
			            player.setVelocity(direction.multiply(1.3));
			            JumpCooldown = 20;
			            player.sendMessage("§7C'est l'heure du Saut !");
					}else {
						sendCooldown(player, JumpCooldown);
					}
				}
			}
		}
	}

	@Override
	public void onSecond() {
		if (getListener().getMachoire() != null) {
			if (JumpCooldown > 0) {
				JumpCooldown--;
			}
			if (JumpCooldown == 0) {
				Player p = Bukkit.getPlayer(getOwner());
				if (p != null) {
					p.sendMessage("§7Votre§f§l Saut§7 est à nouveau utilisable !");
				}
				JumpCooldown-=5;
			}
			if (isTransformedinTitan()) {
				getPlayerRole(getOwner()).givePotionEffet(PotionEffectType.SPEED, 60, 1, isTransformedinTitan());
				getPlayerRole(getOwner()).givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 60, 1, isTransformedinTitan());
			}
		}
	}

	@Override
	public void onBlockBreak(BlockBreakEvent e, Player player) {}

	@Override
	public void Transfo() {
		Player p = Bukkit.getPlayer(getOwner());
		if (p == null)return;
		if (!isTransformedinTitan()) {
			if (getListener().getMachoire() != null) {
				if (getListener().getMachoireCooldown() <=0) {
					setTransformedinTitan(true);
					getPlayerRole(getListener().getMachoire()).isTransformedinTitan = true;
					getListener().setMachoireCooldown(60*6);
					TransfoMessage(getListener().getMachoire(), true);
					p.sendMessage("§7Transformation en Titan§9 Machoire");
					getPlayerRole(getOwner()).setNoFall(true);
				}else {
					sendCooldown(p, getListener().getMachoireCooldown());
				}
			}
		} else {
			if (getListener().getMachoire() != null) {
				setTransformedinTitan(false);
				getPlayerRole(getListener().getMachoire()).isTransformedinTitan = false;
				getListener().setMachoireCooldown(60*3);
				TransfoMessage(getListener().getMachoire(), false);
				p.sendMessage("§7Transformation en§l humain");
				getPlayerRole(getOwner()).setNoFall(false);
			}
		}
	}

	@Override
	public void onAPlayerDie(Player player, Entity killer) {
		if (getOwner() != null) {
			if (player.getUniqueId() == getOwner()) {
				for (Player p : Loc.getNearbyPlayersExcept(player, 30, player)) {
					if (getState().hasRoleNull(p))return;
					if (getPlayerRole(p).isCanVoleTitan()&&canStealTitan(p)) {
						canSteal.add(p);
						p.sendMessage("§7Vous pouvez maintenant volé le Titan "+getName()+"§6 /aot steal§7 pour le récupérer");
						getListener().setMachoire(null);
					}
				}
			}
		}
	}

	@Override
	public void resetCooldown() {
		JumpCooldown = 0;
		getListener().setMachoireCooldown(0);
	}
	private ItemStack TransfoItem() {
		return new ItemBuilder(Material.FEATHER).setName("§6§lTransformation").setLore("§7» Vous transforme en§c Titan ou vous fait redevenir humain").toItemStack();
	}
	@Override
	public ItemStack[] Items() {
		return new ItemStack[] {
				TransfoItem(),
				getJump()
		};
	}
	private int JumpCooldown = 0;
	private ItemStack getJump() {
		return new ItemBuilder(Material.SLIME_BALL).setName("§fJump").setLore("§7Permet d'effectuer un Saut en avant").addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().toItemStack();
	}
	private List<Player> canSteal = new ArrayList<>();
	@Override
	public void onSteal(Player sender, String[] args) {
		if (canSteal.contains(sender)) {
			if (getOwner() == null) {
				if (getPlayerRole(sender).isCanVoleTitan()) {
					getPlayerRole(sender).setCanVoleTitan(false);
					getListener().setMachoire(sender.getUniqueId());
					sender.sendMessage("§7Vous venez de volé le Titan "+getName());
					getPlayerRole(sender).giveItem(sender, true, Items());
					canSteal.clear();
					resetCooldown();
				}
			}
		}
	}

	@Override
	public void onAotTitan(Player player, String[] args) {
		if (getListener().getMachoire() == player.getUniqueId()) {
			player.sendMessage(new String[] {
					AllDesc.bar,
					"§7Titan:§9 Machoire",
					"",
					"§7Votre transformation vous offrira "+AllDesc.Speed+"§7 1 ainsi que "+AllDesc.Force+"§7 1.",
					"",
					"§7Une fois transformé vous aurez accès au "+getJump().getItemMeta().getDisplayName()+"§7, cette item vous permettra d'effectuer un Saut en avant d'environ 20blocs.",
					"",
					AllDesc.bar
			});
		}
	}

	@Override
	public UUID getOwner() {
		return getListener().getMachoire();
	}

	@Override
	public void onGetDescription(Player player) {
		if (getOwner() != null) {
			if (player.getUniqueId() == getOwner()) {
				getPlayerRole(player).sendMessageAfterXseconde(player, "§7Vous possédez le Titan "+getName(), 1);
			}
		}
	}

	@Override
	public String getName() {
		return "§9Machoire";
	}

	@Override
	public void onSubCommand(Player player, String[] args) {}

	@Override
	public void onPickup(PlayerPickupItemEvent e, Player player) {}
	@Override
	public List<Player> getListforSteal() {
		return canSteal;
	}

	@Override
	public void PlayerKilled(Player player, Entity damager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayerAttackAnotherPlayer(Player damager, Player victim, EntityDamageByEntityEvent event) {
		// TODO Auto-generated method stub
		
	}
}
