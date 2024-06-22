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
import fr.nicknqck.utils.StringUtils;

public class Cuirasse extends Titan{

	@Override
	public void onInteract(PlayerInteractEvent e, Player player) {
		if (e.getItem() == null)return;
		if (getOwner() == null)return;
		if (player.getUniqueId() != getOwner())return;
		Player p = Bukkit.getPlayer(getOwner());
		if (p == null)return;
		if (e.getItem().isSimilar(TransfoItem())) {
			if (getListener().getCuirasse() == null)return;
			if (getListener().getCuirasse() == e.getPlayer().getUniqueId()) {
				Transfo();
			}
		}
		if (e.getItem().isSimilar(CuirasseItem())) {
			if (getListener().getCuirasse() == null)return;
			if (!isTransformedinTitan()) {
				p.sendMessage("§cIl faut être transformé en§l Titan !");
				return;
			}
			if (getListener().getCuirasse() == e.getPlayer().getUniqueId()) {
				if (killCount > 0) {
					if (form == Form.Entiere) {
						form = Form.Partielle;
						player.sendMessage("§7Votre cuirasse est maintenant§l§n "+form.name());
						getPlayerRole(player).setResi(0);
						getPlayerRole(player).setForce(20);
						getPlayerRole(player).setMaxHealth(player.getMaxHealth()-4.0);
						killCount--;
						player.sendMessage("§7Il vous reste maintenant§l "+killCount+"§7 changement de cuirasse possible.");
						return;
					}
					if (form == Form.Partielle) {
						form = Form.Entiere;
						player.sendMessage("§7Votre cuirasse est maintenant§l§n "+form.name());
						getPlayerRole(player).setResi(20);
						getPlayerRole(player).setForce(0);
						getPlayerRole(player).giveHealedHeartatInt(2);
						killCount--;
						player.sendMessage("§7Il vous reste maintenant§l "+killCount+"§7 changement de cuirasse possible.");
						return;
					}
				}
			}
		}
	}
	private ItemStack TransfoItem() {
		return new ItemBuilder(Material.FEATHER).setName("§6§lTransformation").setLore("§7» Vous transforme en§c Titan ou vous fait redevenir humain").toItemStack();
	}
	private int killCount = 1;
	@Override
	public void onSecond() {
		if (getOwner() != null) {
			Player p = Bukkit.getPlayer(getOwner());
			if (isTransformedinTitan() && p != null) {
				if (getListener().getCuirasseCooldown() >= 60*4) {
					if (form == Form.Entiere) {
						getState().getPlayerRoles().get(p).givePotionEffet(PotionEffectType.SPEED, 60, 1, true);
						getState().getPlayerRoles().get(p).givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true);	
					} else if (form ==Form.Partielle) {
						getPlayerRole(getListener().getCuirasse()).givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
						getPlayerRole(getListener().getCuirasse()).givePotionEffet(PotionEffectType.SPEED, 60, 2, true);
					}
					int cooldown = getListener().getCuirasseCooldown();
					int newcd = cooldown-(60*4);
					if (p.getItemInHand().isSimilar(TransfoItem())) {{
						sendCustomActionBar(p, "§bTemp de Transformation§r: "+StringUtils.secondsTowardsBeautiful(newcd));
					}
					}
				}
			}
		}
	}
	@Override
	public void onBlockBreak(BlockBreakEvent e, Player player) {}
	@Override
	public void Transfo() {
		if (getListener().getCuirasse() == null)return;
		if (getOwner() == null)return;
		Player p = Bukkit.getPlayer(getOwner());
		if (p == null)return;
		if (!isTransformedinTitan()) {
			if (getListener().getCuirasseCooldown() <= 0) {
				setTransformedinTitan(true);
				getState().getPlayerRoles().get(p).isTransformedinTitan  = true;
				TransfoMessage(getListener().getCuirasse(), true);
				p.sendMessage("§7Transformation en Titan§9 Cuirassé");
				getListener().setCuirasseCooldown(60*8);
				if (form == Form.Entiere) {
					getPlayerRole(getListener().getCuirasse()).setResi(20);
					getPlayerRole(getListener().getCuirasse()).giveHealedHeartatInt(p, 2);
				} else if (form == Form.Partielle) {
					getPlayerRole(getListener().getCuirasse()).setForce(20);
				}
			}else {
				sendCooldown(p, getListener().getCuirasseCooldown());
			}
		} else {
			if (getListener().getCuirasseCooldown() <= 60*8-5) {
				setTransformedinTitan(false);
				getPlayerRole(getListener().getCuirasse()).isTransformedinTitan = false;
				TransfoMessage(getListener().getCuirasse(), false);
				p.sendMessage("§7Transformation en§l humain");
				getListener().setCuirasseCooldown(60*4);
				if (form == Form.Entiere) {
					getPlayerRole(getListener().getCuirasse()).setMaxHealth(getPlayerRole(getListener().getCuirasse()).owner.getMaxHealth()-4);
				}
			} else {
				p.sendMessage("§7Veuiller attendre avant de vous transformer en§l humain");
			}
		}
	}
	private enum Form {
		Entiere,
		Partielle
	}
	private Form form = Form.Entiere;
	@Override
	public void onAPlayerDie(Player player, Entity killer) {
		if (getOwner() != null) {
			if (getListener().getCuirasse() == player.getUniqueId()) {
				for (Player p : Loc.getNearbyPlayersExcept(player, 30, player)) {
					if (getState().hasRoleNull(p))return;
					if (getPlayerRole(p).isCanVoleTitan()&&canStealTitan(p)) {
						canSteal.add(p);
						p.sendMessage("§7Vous pouvez maintenant volé le Titan§9 Cuirassé§7 avec la commande§l /aot steal");
					}
				}
				getListener().setCuirasse(null);
			}
		}
	}

	@Override
	public void resetCooldown() {
		getListener().setCuirasseCooldown(0);
		form = Form.Entiere;
		killCount = 1;
	}
	
	@Override
	public ItemStack[] Items() {
		return new ItemStack[] {
				TransfoItem(),
				CuirasseItem()
		};
	}
	public ItemStack CuirasseItem() {
		return new ItemBuilder(Material.QUARTZ).setName("§f§lCuirasse").setLore("§7Permet de mettre/enlever votre cuirasse").addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().toItemStack();
	}
	private List<Player> canSteal = new ArrayList<>();
	@Override
	public void onSteal(Player sender, String[] args) {
		if (canSteal.contains(sender)) {
			if (getListener().getCuirasse() == null) {
				if (getPlayerRole(sender).isCanVoleTitan()) {
					resetCooldown();
					getPlayerRole(sender).setCanVoleTitan(false);
					sender.sendMessage("§7Vous avez volé le Titan§9 Cuirassé");
					getListener().setCuirasse(sender.getUniqueId());
					getPlayerRole(sender).giveItem(sender, false, Items());
					canSteal.clear();
				}
			}
		}
	}
	@Override
	public UUID getOwner() {
		return getListener().getCuirasse();
	}
	@Override
	public void onAotTitan(Player player, String[] args) {
		if (getListener().getCuirasse() == null)return;
		if (player.getUniqueId() != getListener().getCuirasse())return;
		player.sendMessage(new String[] {
				AllDesc.bar,
				"§7Titan:§9 Cuirassé",
				"",
				"§7Votre transformation vous donne des effets en fonction de l'épaisseur de votre cuirasse: ",
				"",
				"§7 - Entière: Vous donne les effets "+AllDesc.Speed+"§7 1, "+AllDesc.Resi+"§7 1 ainsi que 2"+AllDesc.coeur+"§7 supplémentaire,",
				"",
				"§7 - Partielle: Vous donne les effets "+AllDesc.Speed+"§7 2, "+AllDesc.Force+"§7 1",
				"",
				"§7Pour chaque changement de cuirasse il vous faudra avoir fais déjà un kill (à partir du moment ou vous recevez ce Titan)",
				"",
				AllDesc.bar
		});
	}
	@Override
	public String getName() {
		return "§9Cuirassé";
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
		return canSteal;
	}
	@Override
	public void PlayerKilled(Player player, Entity damager) {
		if (getOwner() != null) {
			if (damager.getUniqueId() == getOwner()) {
				System.out.println("killCount 0."+killCount);
				killCount+=1;
				System.out.println("killCount 0."+killCount);
				player.sendMessage("§7Vous pouvez à nouveau changer de cuirasse");
			}
		}
	}
	@Override
	public void onPlayerAttackAnotherPlayer(Player damager, Player victim, EntityDamageByEntityEvent event) {}
}
