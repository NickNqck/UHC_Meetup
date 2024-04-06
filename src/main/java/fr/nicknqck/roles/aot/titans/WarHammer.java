package fr.nicknqck.roles.aot.titans;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
import fr.nicknqck.utils.particles.MathUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;

public class WarHammer extends Titan{

	@Override
	public void onInteract(PlayerInteractEvent e, Player player) {
		if (e.getPlayer() == player) {
			if (e.getItem() == null)return;
			if (getListener().getWarHammer() == null)return;
			if (getListener().getWarHammer() == player.getUniqueId()) {
				if (e.getItem().isSimilar(TransfoItem())) {
					Transfo();
					return;
				}
			}
		}
	}

	@Override
	public void onSecond() {
		if (getOwner() == null)return;
		if (CoconLocation == null)return;
		if (TransfoLocation == null)return;
		Player p = Bukkit.getPlayer(getOwner());
		if (p == null)return;
		if (CoconLocation.getWorld() == p.getWorld()) {
			MathUtil.sendParticleLine(TransfoLocation, CoconLocation, EnumParticle.PORTAL, (int) CoconLocation.distance(TransfoLocation));
			MathUtil.sendParticleLine(TransfoLocation, p.getLocation(), EnumParticle.CLOUD, (int) TransfoLocation.distance(p.getLocation()));
		}
		getPlayerRole(getOwner()).givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
	}

	@Override
	public void onBlockBreak(BlockBreakEvent e, Player player) {
		if (!isTransformedinTitan())return;
		if (e.getBlock() == null)return;
		if (e.getBlock().getType() != Material.IRON_BLOCK)return;
		if (CoconLocation == null)return;
		if (getListener().getWarHammer() == null)return;
		if (e.getBlock().getLocation().distance(CoconLocation) < 1) {
			Transfo();
			getPlayerRole(getListener().getWarHammer()).setMaxHealth(getPlayerRole(getListener().getWarHammer()).getMaxHealth()-2);
			player.sendMessage("§7Vous venez de casser le Cocon du§9 WarHammer");
			Player p = Bukkit.getPlayer(getOwner());
			if (p != null) {
				p.sendMessage("§7§l"+player.getDisplayName()+"§7 viens de cassé votre Cocon");
			}
		}
	}
	private ItemStack TransfoItem() {
		return new ItemBuilder(Material.FEATHER).setName("§6§lTransformation").setLore("§7» Vous transforme en§c Titan ou vous fait redevenir humain").toItemStack();
	}
	@Override
	public void Transfo() {
		Player war = Bukkit.getPlayer(getOwner());
		if (war == null)return;
		if (!isTransformedinTitan()) {
			if (getListener().getWarHammerCooldown() <= 0) {
				setTransformedinTitan(true);
				TransfoMessage(getListener().getWarHammer(), true);
				war.sendMessage("§7Transformation en Titan§9 WarHammer");
				getPlayerRole(getListener().getWarHammer()).isTransformedinTitan = true;
				getListener().setWarHammerCooldown(60*8);
				getPlayerRole(getListener().getWarHammer()).giveHealedHeartatInt(3);
				this.TransfoLocation = war.getLocation();
				this.CoconLocation = new Location(TransfoLocation.getWorld(), TransfoLocation.getBlockX(), TransfoLocation.getBlockY()-6, TransfoLocation.getBlockZ());
				this.CoconLocation.getBlock().setType(Material.IRON_BLOCK);
				System.out.println(getPlayerRole(getOwner()).isCanRespawn());
				getPlayerRole(getOwner()).setCanRespawn(true);
				getPlayerRole(getOwner()).setForce(30);
				System.out.println(getPlayerRole(getOwner()).isCanRespawn());
			}else {
				if (getListener().getWarHammer() == null)return;
				sendCooldown(war, getListener().getWarHammerCooldown());
			}
		} else {
			System.out.println(getPlayerRole(getOwner()).isCanRespawn());
			getListener().setWarHammerCooldown(60*4);
			TransfoMessage(getListener().getWarHammer(), false);
			setTransformedinTitan(false);
			getPlayerRole(getListener().getWarHammer()).isTransformedinTitan = false;
			getPlayerRole(getListener().getWarHammer()).setMaxHealth(getPlayerRole(getListener().getWarHammer()).getMaxHealth()-6);
			getPlayerRole(getOwner()).setForce(0);
			TransfoLocation = null;
			if (CoconLocation != null) {
				CoconLocation.getWorld().getBlockAt(CoconLocation).setType(Material.STONE);
			}
			CoconLocation = null;
		}
	}

	@Override
	public void onAPlayerDie(Player player, Entity killer) {
		if (player.getUniqueId() == getListener().getWarHammer()) {
			if (isTransformedinTitan()) {
				if (getPlayerRole(getOwner()).isCanRespawn()) {
					player.teleport(TransfoLocation);
					Transfo();
					getPlayerRole(player).setMaxHealth(getPlayerRole(player).getMaxHealth()-2);
					player.sendMessage("§7Votre mort à conduit à la perte d'1"+AllDesc.coeur+"§7 permanent");
					getPlayerRole(player).setCanRespawn(false);
					return;
				}
			} else {
				for (Player p : Loc.getNearbyPlayersExcept(player, 30, player)) {
					if (!getState().hasRoleNull(p)) {
						if (getPlayerRole(p).isCanVoleTitan() && canStealTitan(p)) {
							canSteal.add(p);
							p.sendMessage("§7Vous pouvez maintenant volé le Titan§9 WarHammer§7 avec la commande§6 /aot steal");
						}
					}
				}
				getListener().setWarHammer(null);
			}
		}
	}
	Location TransfoLocation;
	Location CoconLocation;
	@Override
	public void resetCooldown() {
		TransfoLocation = null;
		if (CoconLocation != null) {
			CoconLocation.getWorld().getBlockAt(CoconLocation).setType(Material.STONE);
		}
		CoconLocation = null;
		getListener().setWarHammerCooldown(0);
	}
	@Override
	public UUID getOwner() {
		return getListener().getWarHammer();
	}
	@Override
	public ItemStack[] Items() {
		return new ItemStack[] {
				TransfoItem()
		};
	}
	private List<Player> canSteal = new ArrayList<>();
	@Override
	public void onSteal(Player sender, String[] args) {
		if (canSteal.contains(sender)) {
			if (getOwner() == null) {
				if (getPlayerRole(sender).isCanVoleTitan()) {
					getListener().setWarHammer(sender.getUniqueId());
					getPlayerRole(sender).giveItem(sender, false, Items());
					canSteal.clear();
					resetCooldown();
				}
			}
		}
	}
	@Override
	public String getName() {
		return "§9WarHammer";
	}
	@Override
	public void onGetDescription(Player player) {
		if (getOwner() == null)return;
		if (getOwner() == player.getUniqueId()) {
			getPlayerRole(player).sendMessageAfterXseconde(player, "§7Vous possédez le Titan "+getName(), 1);
		}
	}
	@Override
	public void onAotTitan(Player player, String[] args) {
		if (getOwner() != null) {
			if (player.getUniqueId() == getOwner()) {
				player.sendMessage(new String[] {
						AllDesc.bar,
						"§7Titans:§9 WarHammer",
						"",
						"§7Votre transformation vous offre l'effet "+AllDesc.Force+"§7 1.5 ainsi que 3"+AllDesc.coeur+"§7, également, un fil de particule vous liera à l'endroit ou vous vous êtes transformé.",
						"",
						"Lors de votre transformation en titan un bloc de fer apparaitra 6 blocs en dessous de votre position, si quelqu'un parvient à le casser vous ne serrez plus transformé et vous perdrez 1"+AllDesc.coeur+"§7 permanent",
						"",
						AllDesc.bar
				});
			}
		}
	}

	@Override
	public void onSubCommand(Player player, String[] args) {}

	@Override
	public void onPickup(PlayerPickupItemEvent e, Player player) {}
	@Override
	public List<Player> getListforSteal() {return canSteal;}

	@Override
	public void PlayerKilled(Player player, Entity damager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayerAttackAnotherPlayer(Player damager, Player victim, EntityDamageByEntityEvent event) {
		// TODO Auto-generated method stub
		
	}
}