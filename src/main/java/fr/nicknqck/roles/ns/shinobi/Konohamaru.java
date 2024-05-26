package fr.nicknqck.roles.ns.shinobi;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.particles.MathUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;

public class Konohamaru extends RoleBase{

	public Konohamaru(Player player, Roles roles) {
		super(player, roles);
		setChakraType(Chakras.KATON);
		giveItem(owner, false, getItems());
		owner.sendMessage(Desc());
		
	}

	@Override
	public String[] Desc() {
		if (KnowNaruto) {
			KnowRole(owner, Roles.Naruto, 1);
		}
		return new String[] {
				 AllDesc.bar,
				 AllDesc.role+"§aKonohamaru",
				 AllDesc.objectifteam+"§aShinobi",
				 "",
				 AllDesc.items,
				 "",
				 AllDesc.point+"§aRasengan §f: En frappant un joueur avec l'item §aen main§f, vous créez une §cexplosion §flui infligeant §c2"+AllDesc.coeur+". §7(1x/3min)",
				 "",
				 AllDesc.point+"§aNuées Ardentes §f: Vous permez d'infliger l'effet "+AllDesc.blind+" aux joueurs à§c 20 blocs§f de vous",
				 "",
				 AllDesc.particularite,
				 "",
				 "Vous connaîtrez l'§eidentité §fde §aNaruto§f en restant 2 minutes à côté de ce dernier",
				 "",
				 AllDesc.chakra+getChakras().getShowedName(),
				 AllDesc.bar
		};
	}

	@Override
	public ItemStack[] getItems() {
		return new ItemStack[]{
			RasenganItem(),
			NueesArdentesItem()
		};
	}

	@Override
	public void resetCooldown() {
		cdNueesArdentes = 0;
		cdRasengan = 0;
		
	}
	private ItemStack RasenganItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§aRasengan").setLore("§7Vous permez de rassembler votre Chakra en un point").toItemStack();
	}
	private ItemStack NueesArdentesItem() {
		return new ItemBuilder(Material.SULPHUR).setName("§aNuées Ardentes").setLore("§7 Vous permez de mettre Blindness aux Joueurs autour de vous").toItemStack();
	}
	private int cdRasengan = 0;
	private int cdNueesArdentes = 0;
	@Override
	public void onALLPlayerDamageByEntity(EntityDamageByEntityEvent event, Player victim, Entity entity) {
		if (entity.getUniqueId() == owner.getUniqueId()) {
			if (owner.getItemInHand().isSimilar(RasenganItem())) {
				if (cdRasengan <= 0) {
					MathUtil.sendParticle(EnumParticle.EXPLOSION_LARGE, victim.getLocation());
					Heal(victim, -4);
					victim.damage(0.0);
					Location loc = victim.getLocation();
					loc.add(new Vector(0.0, 1.8, 0.0));
					victim.teleport(loc);
					cdRasengan = 60*3;
				} else {
					sendCooldown(owner, cdRasengan);
				}
			}
		}
		super.onALLPlayerDamageByEntity(event, victim, entity);
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(RasenganItem())) {
			if (cdRasengan <= 0) {
				owner.sendMessage("§7Il faut frapper un joueur pour créer une explosion.");
				return true;
			} else {
				sendCooldown(owner, cdRasengan);
				return true;
			}
		}
		if (item.isSimilar(NueesArdentesItem())) {
			if (cdNueesArdentes <= 0) {
				owner.sendMessage("§aNuées Ardentes!");
				for (Player p : Loc.getNearbyPlayersExcept(owner, 20)) {
					if (owner.canSee(p)) {
						givePotionEffet(p, PotionEffectType.BLINDNESS, 20*20, 1, true);
						cdNueesArdentes = 60*3;
						p.sendMessage("Vous venez d'être touche par la §8Nuées Ardentes §fde §aKonohamaru");
						owner.sendMessage("§7§l"+p.getName()+"§7 à été touchée");
					}
				}
				return true;	
				} else {
					sendCooldown(owner, cdNueesArdentes);
					return true;
				}
			}
		return super.ItemUse(item, gameState);
	}
	private Map<UUID, Integer> timePassedNearby = new HashMap<>();
	private boolean KnowNaruto = false;
	@Override
	public void Update(GameState gameState) {
		if (cdRasengan >= 0) {
			cdRasengan--;
			if (cdRasengan == 0) {
				owner.sendMessage("§7Vous pouvez à nouveau utiliser votre§a Rasengan");
			}
		}
		if (cdNueesArdentes >= 0) {
			cdNueesArdentes --;
		}
		if (cdNueesArdentes == 0) {
			owner.sendMessage("§7Vous pouvez à nouveau utiliser votre §aNuees Ardentes");
		}
		Player p = getPlayerFromRole(Roles.Naruto);
		if (p != null) {
			if (timePassedNearby.containsKey(p.getUniqueId())) {
				int i = timePassedNearby.get(p.getUniqueId());
				timePassedNearby.remove(p, i);
				timePassedNearby.put(p.getUniqueId(), i+1);
					if (timePassedNearby.get(p.getUniqueId()).intValue() == 60*2) {
						owner.sendMessage("§a"+p.getDisplayName()+"§f est §aNaruto");
						KnowNaruto = true;
					}
			}else {
				timePassedNearby.put(p.getUniqueId(), 1);
			}
		}
	}
}