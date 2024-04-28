package fr.nicknqck.roles.aot.titans;

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

import fr.nicknqck.GameState;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.particles.MathUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;

public class Colossal extends Titan{

	@Override
	public void onInteract(PlayerInteractEvent e, Player player) {
		if (e.getItem() != null) {
			ItemStack item = e.getItem();
			if (item.isSimilar(TransfoItem())) {
				if (getListener().getColossal() == null)return;
				if (getListener().getColossal() == e.getPlayer().getUniqueId()) {
					if (player.getUniqueId() == getOwner()) {
						Transfo();
					}
				}
			}
			if (item.isSimilar(Cercle())) {
				if (isTransformedinTitan()) {
					if (cerclecooldown <= 0) {
						if (getListener().getColossal() == null)return;
						if (player.getUniqueId() == getOwner()) {
							if (getPlayerRole(getListener().getColossal()).getResi() == 40) {
								getPlayerRole(getListener().getColossal()).setResi(20);
								cerclecooldown = 100+15;
								getPlayerRole(getListener().getColossal()).owner.sendMessage("Activation du "+Cercle().getItemMeta().getDisplayName());
								return;
							}
							if (getPlayerRole(getListener().getColossal()).getResi() == 20) {
								getPlayerRole(getListener().getColossal()).setResi(0);
								cerclecooldown = 100+15;
								getPlayerRole(getListener().getColossal()).owner.sendMessage("Activation du "+Cercle().getItemMeta().getDisplayName());
								return;
							}
							if (getPlayerRole(getListener().getColossal()).getResi() == 0) {
								getPlayerRole(getListener().getColossal()).owner.sendMessage("Vous ne pouvez plus utilisé votre "+Cercle().getItemMeta().getDisplayName());
                            }
						}
					}else {
						sendCooldown(player, cerclecooldown);
                    }
				}else {
					player.sendMessage("§7Il faut être transformé en§c Titan§7 !");
                }
			}
		}
	}
	@Override
	public void Transfo() {
		Player player = Bukkit.getPlayer(getOwner());
		if (player == null)return;
		if (!isTransformedinTitan()) {
			if (getListener().getColossalCooldown() <= 0) {
				if (getListener().getColossal() == null)return;
				setTransformedinTitan(true);
				getPlayerRole(getListener().getColossal()).isTransformedinTitan = true;
				getPlayerRole(getOwner()).givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, 90, 2, true);
				getPlayerRole(getListener().getColossal()).setResi(40);
				getListener().setColossalCooldown(60*8);
				TransfoMessage(getListener().getColossal(), true);
				player.sendMessage("§7Transformation en Titan§9 Colossal§7 !");
			}else {
				if (getListener().getColossal() == null)return;
				sendCooldown(player, getListener().getColossalCooldown());
			}
		}else {
			if (getListener().getColossal() == null)return;
			setTransformedinTitan(false);
			getPlayerRole(getListener().getColossal()).isTransformedinTitan = false;
			getPlayerRole(getListener().getColossal()).setResi(0);
			getListener().setColossalCooldown(60*5);
			TransfoMessage(getListener().getColossal(), false);
			player.sendMessage("§7Transformation en §lhumain§7 !");
		}
	}
	@Override
	public void onSecond() {
		if (cerclecooldown > 0) {
			cerclecooldown--;
		}else if (cerclecooldown == 0) {
			cerclecooldown-=5;
			if (getListener().getColossal() == null)return;
			Player player = Bukkit.getPlayer(getOwner());
			if (player == null)return;
			player.sendMessage(Cercle().getItemMeta().getDisplayName()+"§7 est à nouveau utilisable !");
		}
		Player player = Bukkit.getPlayer(getOwner());
		if (player == null)return;
		if (isTransformedinTitan()) {
			if (getListener().getColossalCooldown() >= 60*5) {
				if (cerclecooldown >= 100) {
					if (getListener().getColossal() == null)return;
					MathUtil.sendCircleParticle(EnumParticle.FLAME, player.getLocation(), 8, 24);
					for (Player p : GameState.getInstance().getInGamePlayers()) {
						if (p.getLocation().distance(player.getLocation()) <= 8) {
							p.setFireTicks(p.getFireTicks()+60);
						}
					}
					if (cerclecooldown == 100) {
						player.sendMessage(Cercle().getItemMeta().getDisplayName()+"§7 est maintenant désactivée !");
					}
				}
				if (getPlayerRole(getListener().getColossal()).getResi() >= 40) {
					getPlayerRole(getListener().getColossal()).givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, 60, 2, true);
				}else if (getPlayerRole(getListener().getColossal()).getResi() == 20) {
					getPlayerRole(getListener().getColossal()).givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true);
				}
				getPlayerRole(getListener().getColossal()).givePotionEffet(PotionEffectType.FIRE_RESISTANCE, 60, 1, true);
				int cooldown = getListener().getColossalCooldown();
				int newcd = cooldown-(60*5);
				ItemStack item = player.getItemInHand();
				if (item.isSimilar(Cercle())) {
					if (cerclecooldown <= 0) {
						sendCustomActionBar(player, "§bTemp de Transformation§r: "+StringUtils.secondsTowardsBeautiful(newcd)+" "+getItemNameInHand(player)+" Utilisable");
					}else {
						sendCustomActionBar(player, "§bTemp de Transformation§r: "+StringUtils.secondsTowardsBeautiful(newcd)+" Cooldown: "+cd(cerclecooldown));
					}
				}else{
					sendCustomActionBar(player, "§bTemp de Transformation§r: "+StringUtils.secondsTowardsBeautiful(newcd));
				}
			}
		}else {
			if (getListener().getColossal() == null)return;
			if (player.getItemInHand().isSimilar(TransfoItem())) {
				sendActionBarCooldown(player, getListener().getColossalCooldown());
			}
			if (player.getItemInHand().isSimilar(Cercle())) {
				sendActionBarCooldown(player, cerclecooldown);
			}
		}
	}
	@Override
	public ItemStack[] Items() {
		return new ItemStack[] {
				Cercle(),
				TransfoItem()
		};
	}
	private ItemStack TransfoItem() {
		return new ItemBuilder(Material.FEATHER).setName("§6§lTransformation").setLore("§7» Vous transforme en§c Titan ou vous fait redevenir humain").toItemStack();
	}
	public ItemStack Cercle() {
		return new ItemBuilder(Material.MAGMA_CREAM).setName("§cCercle").setLore("§7Crée un§c Cercle§7 autours de vous qui§6 enflamme§7 tout les joueurs proche de vous").addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().toItemStack();
	}
	public int cerclecooldown = 0;
	@Override
	public void resetCooldown() {
		cerclecooldown = 0;
		getListener().setColossalCooldown(0);
	}
	@Override
	public void onBlockBreak(BlockBreakEvent e, Player player) {}
	@Override
	public void onAPlayerDie(Player player, Entity killer) {
		if (getListener().getColossal() != null) {
			if (getListener().getColossal().equals(player.getUniqueId())) {
				for (Player p : Loc.getNearbyPlayersExcept(player, 30, player)) {
					if (!GameState.getInstance().hasRoleNull(p)) {
						if (getPlayerRole(p).isCanVoleTitan() && canStealTitan(p)) {
							p.sendMessage("§7Vous pouvez maintenant volé le Titan§9 Colossal§7 avec la commande§l /aot steal");
							canVoleColossal.add(p);
						}
					}
				}
				getListener().setColossal(null);
			}
		}
	}
	@Override
	public UUID getOwner() {
		return getListener().getColossal();
	}
	private final List<Player> canVoleColossal = new ArrayList<>();
	@Override
	public void onSteal(Player sender, String[] args) {
		if (getListener().getColossal() == null) {
			if (canVoleColossal.contains(sender)) {
				if (getState().hasRoleNull(sender)) {
					canVoleColossal.remove(sender);
					sender.sendMessage("§7Impossible de volé ce Titan");
					return;
				}
				if (getPlayerRole(sender.getUniqueId()).isCanVoleTitan()) {
					getListener().setColossal(sender.getUniqueId());
					sender.sendMessage("§7Vous avez volé le Titan§9 Colossal");
					getPlayerRole(sender.getUniqueId()).giveItem(sender, false, Items());
					canVoleColossal.clear();
					resetCooldown();
				}
			}
		}
	}
	@Override
	public void onAotTitan(Player player, String[] args) {
		if (getListener().getColossal() == null)return;
		if (getListener().getColossal().equals(player.getUniqueId())) {
			player.sendMessage(new String[] {
					AllDesc.bar,
					"§7Titan:§9 Colossal",
					"",
					"§7Votre transformation vous offre "+AllDesc.Resi+"§7 2 et "+AllDesc.fireResi+"§7 1 pendant 3m (Temp de transformation), après ce temp vous aurez 5m à attendre avant la prochaine transformation.",
					"",
					Cercle().getItemMeta().getDisplayName()+"§7: Vous permet pendant 15s de crée un cercle de§6 flamme§7 qui brûlera toute personne étant à moins de 8blocs de vous, cependant celà vous fera perdre 1 niveau de "+AllDesc.Resi,
					"",
					AllDesc.bar
					});	
		}
	}
	@Override
	public String getName() {
		return "§9Colossal";
	}
	@Override
	public void onGetDescription(Player player) {
		if (getOwner() == null)return;
		if (getOwner() == player.getUniqueId()) {
			getPlayerRole(player).sendMessageAfterXseconde(player, "§7Vous possédez le Titan "+getName(), 1);
		}
	}
	@Override
	public void onSubCommand(Player player, String[] args) {}
	@Override
	public void onPickup(PlayerPickupItemEvent e, Player player) {}
	@Override
	public List<Player> getListforSteal() {
		return canVoleColossal;
	}
	@Override
	public void PlayerKilled(Player player, Entity damager) {}
	@Override
	public void onPlayerAttackAnotherPlayer(Player damager, Player victim, EntityDamageByEntityEvent event) {}
}