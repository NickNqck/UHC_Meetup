package fr.nicknqck.roles.ns.orochimaru;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.OrochimaruRoles;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.NonNull;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Kimimaro extends OrochimaruRoles {

	public Kimimaro(UUID player) {
		super(player);
		setChakraType(getRandomChakras());
	}

	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}

	@Override
	public @NonNull Intelligence getIntelligence() {
		return Intelligence.MOYENNE;
	}
	@Override
	public @NonNull Roles getRoles() {
		return Roles.Kimimaro;
	}

	public String[] Desc(){
		KnowRole(owner, Roles.Orochimaru, 1);
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§5Kimimaro",
				AllDesc.objectifteam+"§5Orochimaru",
				"",
				AllDesc.effet,
				"",
				AllDesc.point+"Vous possèdez l'effet "+AllDesc.Force+"§c 1§f permanent",
				"",
				AllDesc.items,
				"",
				AllDesc.point+"Vous possèdez l'item §5Marque Maudite §rQui vous octroie l'effet "+AllDesc.Resi+" 1 pendant 3 minutes mais vous perdrez votre force et obtiendrez faiblesse 1 pendant 2 minutes",
				"",
				AllDesc.point+"Vous possèdez un Os nommé §5Ken§f qui inflige les dégâts d'une épée en diamant tranchant 4 avec lequel vous pouvez infligé 10 coups avec 2 minutes de cooldown",
				"",
				AllDesc.particularite,
				"",
				"Vous connaissez l'identité d'§5Orochimaru",
				"",
				"Vous possédez la nature de Chakra "+getChakras().getShowedName(),
				AllDesc.bar
		};
	}
		@Override
		public ItemStack[] getItems() {
			return new ItemStack[] {
					MarqueMauditeItem(),
					KenItem()
			};
	}
	private ItemStack MarqueMauditeItem(){
		return new ItemBuilder(Material.NETHER_STAR).setName("§5Marque Maudite").setLore("§7Vous recevez l'effet Résistance 1 pendant 3 minute mais vous receverez faiblesse 1 pendant 2min").toItemStack();
	}
	private ItemStack KenItem() {
		return new ItemBuilder(Material.BONE).setName("§5Ken").setLore("§7Mets les dégâts equivalent l'équivalent d'une épée en diamant Tranchant 4").addEnchant(Enchantment.DAMAGE_ALL, 4).hideAllAttributes().toItemStack();
	}
	@Override
	public void resetCooldown() {
		Marquecd = 0;
		Kencd = 0;
	}
	private int Marquecd = 0;
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(MarqueMauditeItem())) {
			if (Marquecd <= 0){
			OLDgivePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, 20*60*3, 1, false);
			setResi(20);
			new BukkitRunnable() {
				int l = 0;
				@Override
				public void run() {
					l++;
					if (gameState.getServerState() != ServerStates.InGame) {
						cancel();
						return;
					}
					if (owner.getGameMode() != GameMode.SURVIVAL) {
						cancel();
						return;
					}
					if (l == 60*3) {
						owner.sendMessage("Vous perdez votre force et oobtenez faiblesse 1 pendant 2 minutes");
						OLDgivePotionEffet(PotionEffectType.WEAKNESS, 20*60*2, 1, true);
						owner.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
						owner.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
						Marquecd = 60*10;
						setResi(0);
					} else {
						if (l == 60*5) {
							owner.sendMessage("Vous regagnez votre§c force");
							cancel();
						}
					}
					
				}
				
			}.runTaskTimer(Main.getInstance(), 0, 20);
            } else {
			sendCooldown(owner, Marquecd);
            }
            return true;
        }
		return super.ItemUse(item, gameState);
	}
	private int Kencoup =0;
	private int Kencd = 0;
	@Override
	public void onALLPlayerDamageByEntity(EntityDamageByEntityEvent event, Player victim, Entity entity) {
	    if (entity.getUniqueId() == owner.getUniqueId()) {
	    	if (owner.getItemInHand().isSimilar(KenItem())) {
	    		if (Kencoup <= 10) {
	    				if (Kencd > 0) {
	    					sendCooldown(owner, Kencd);
	    					return;
	    				}
	    			event.setDamage(33);
	    			Kencoup++;
	    			if (Kencoup == 10) {
	    				owner.sendMessage("Vous venez d'utiliser vos 10 coups avec votre OS Ken veuillez attendre la fin de votre cooldown pour le réutilisez ");
	    				Kencd = 120;
	    			}
	    		}
	    	}
	    }
	}
	@Override
	public void Update(GameState gameState) {
		if (Marquecd >= 0) {
			Marquecd--;
			if (Marquecd == 0) {
				owner.sendMessage(MarqueMauditeItem().getItemMeta().getDisplayName()+"§7 est maintenant utilisable");
			}
		}
		if (Kencd >= 0) {
			Kencd--;
			if (Kencd == 0) {
				owner.sendMessage("Vous pouvez réutilisez votre §5Ken");
				Kencoup = 0;
			}
		}
		if (!owner.hasPotionEffect(PotionEffectType.WEAKNESS)) {
			OLDgivePotionEffet(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, false);
		}
	}

	@Override
	public String getName() {
		return "Kimimaro";
	}
}
