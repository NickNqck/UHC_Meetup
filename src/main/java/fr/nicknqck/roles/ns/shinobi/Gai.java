package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.DoubleCircleEffect;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Gai extends ShinobiRoles {

	public Gai(UUID player) {
		super(player);
		setChakraType(getRandomChakras());
		setCanBeHokage(true);
	}

	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
		super.GiveItems();
	}

	@Override
	public Intelligence getIntelligence() {
		return Intelligence.PEUINTELLIGENT;
	}

	@Override
	public GameState.Roles getRoles() {
		return Roles.Gai;
	}
	@Override
	public String[] Desc() {
		KnowRole(owner, Roles.RockLee, 5);
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§aGaï Maito",
				AllDesc.objectifteam+"§aShinobi",
				"",
				AllDesc.items,
				"§aTroisième porte :§r vous octroie l'effet "+AllDesc.Speed+" 1 pendant 1 minute 30 mais vous perdrez 1 "+AllDesc.coeur,
				"§aSixième porte : §rvous octroie les effets "+AllDesc.Force+" 1 ainsi que "+AllDesc.Speed+" 1 pendant 2 minute mais vous perdrez 1"+AllDesc.coeur+" permanent",
				"§chuitième porte : §rvous octroie "+AllDesc.Speed+" 2, "+AllDesc.Force+" 1, "+AllDesc.Resi+" 1, "+AllDesc.fireResi+" 1 ainsi que 5"+AllDesc.coeur+" Supplémentaire et vous obtiendrez le §8Gaï de la nuit §rcependant vous tomberez à 5 "+AllDesc.coeur+" permanent et vous aurez "+AllDesc.weak+" 1 pendant 15 min",
				"§8Gaï de la nuit : §rvous octroie "+AllDesc.Speed+" 3 ainsi que "+AllDesc.Force+" 3 pendant 10 secondes mais vous viendrez à mourir après ces 10 secondes",
				"",
				AllDesc.particularite,
				"",
				"Vous connaissez le joueur possédant le rôle RockLee",
				"Vous regénérez 1 coeur permanent toute les 6 min",
				"",
				AllDesc.chakra+getChakras().getShowedName(),
				AllDesc.bar,
		};
	}

	@Override
	public ItemStack[] getItems() {
		return new ItemStack[]{
		TroisPortesItem(),
		SixPortesItem(),
		HuitPortesItem(),
		
		};
	}

	@Override
	public void resetCooldown() {
		cdSixPortes = 0;
		cdTroisPortes = 0;
		
	}
	private ItemStack TroisPortesItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§aTroisième Porte").setLore("§7Vous permez d'obtenir "+AllDesc.Speed+" 1 pendant 1 minute 30").toItemStack();
	}
	private ItemStack SixPortesItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§aSixième Porte").setLore("§7Vous permez d'obtenir "+AllDesc.Speed+" 1 ainsi que "+AllDesc.Force+" 1 pendant 2 minutes").toItemStack();
	}
	private ItemStack HuitPortesItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§cHuitième Porte").setLore("§cVous permez d'ouvrir la Huitième porte").toItemStack();
	}
	private ItemStack GaiDeLaNuitItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§8Gaï de la nuit").setLore("§8Vous permez de libéré votre plein potentiel").toItemStack();
	}
	private int cdTroisPortes = 0;
	private int cdSixPortes = 0;
	
	
	@Override
	public void Update(GameState gameState) {
		if (cdTroisPortes >= 0) {
			cdTroisPortes --;
			if(cdTroisPortes == 0) {
				owner.sendMessage("Vous pouvez de nouveaux utiliser votre §aTroisième Porte");
			}
		}
		if (cdSixPortes >= 0) {
			cdSixPortes --;
			if(cdSixPortes== 0) {
				owner.sendMessage("Vous pouvez de nouveaux utiliser votre §aSixième Porte");
			}
		}
		
		super.Update(gameState);
	}
	
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if(item.isSimilar(TroisPortesItem())) {
			if (cdTroisPortes <= 0) {
				owner.sendMessage("Vous venez d'ouvrir la §aTroisième Porte");
				givePotionEffet(PotionEffectType.SPEED, 20*90, 1, true);
				owner.damage(1.0, owner);
				cdTroisPortes = 60*3;
            } else {
				sendCooldown(owner, cdTroisPortes);
            }
            return true;
        }
		if (item.isSimilar(SixPortesItem())) {
			if (cdSixPortes <= 0) {
				owner.sendMessage("Vous venez d'ouvrir la §aSixième Porte");
				givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 20*120, 1, true);
				givePotionEffet(PotionEffectType.SPEED, 20*120, 1, true);
				setMaxHealth(getMaxHealth()-2);
				cdSixPortes = 60*6;
				new BukkitRunnable() {
					int intVie = 60*6;
					@Override
					public void run() {
						if (gameState.getInGamePlayers().contains(owner)) {
						intVie --;
						} else {
							cancel();
						}
						if (intVie == 0) {
							setMaxHealth(getMaxHealth()+2);
							owner.setMaxHealth(getMaxHealth());
							owner.sendMessage("Vous venez de récupérer 1"+AllDesc.coeur);
							cancel();
						}
						
						if (intVie <= 120) {
							new DoubleCircleEffect(20*3, EnumParticle.VILLAGER_HAPPY).start(owner);
						}
					}
				}.runTaskTimer(Main.getInstance(), 0, 20);
            } else {
				sendCooldown(owner, cdSixPortes);
            }
            return true;
        }
		if (item.isSimilar(HuitPortesItem())) {
				givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 20*180, 1, true);
				givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, 20*180, 1, true);
				givePotionEffet(PotionEffectType.SPEED, 20*180, 2, true);
				givePotionEffet(PotionEffectType.FIRE_RESISTANCE, 20*180, 1, true);
				giveHealedHeartatInt(5);
				owner.sendMessage("§cVous venez d'ouvrir la Huitième Porte");
				owner.getInventory().removeItem(getItems());
				owner.getInventory().addItem(GaiDeLaNuitItem());
				
				new BukkitRunnable() {
					int i = 0;
					@Override
					public void run() {
						if (gameState.getServerState() == ServerStates.InGame) {
						i++;
						} else {
							cancel();
						}
						if (i == 185) {
							
							givePotionEffet(PotionEffectType.WEAKNESS, 20*(60*15), 1, true);
							setMaxHealth(10.0);
							owner.setMaxHealth(getMaxHealth());
							cancel();
						}
						if (i <= 180) {
							new DoubleCircleEffect(20*3, EnumParticle.REDSTONE).start(owner);
						}
					}
				}.runTaskTimer(Main.getInstance(), 0, 20);
				return true;
			
		}
		if (item.isSimilar(GaiDeLaNuitItem())) {
			if (owner.hasPotionEffect(PotionEffectType.WEAKNESS)) {
				owner.removePotionEffect(PotionEffectType.WEAKNESS);
			}
				givePotionEffet(PotionEffectType.SPEED, 20*10, 3, true);
				givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 20*10, 3, true);
				owner.sendMessage("§8Vous utilisez le meilleur de vous même");
				new BukkitRunnable() {
					int in = 0;
					@Override
					public void run() {
						if (gameState.getServerState() == ServerStates.InGame) {
							in ++;
						} else {
							cancel();
						}
						if (in == 10) {
							GameListener.getInstance().DeathHandler(owner, owner, 50000.0, gameState);
							cancel();
						}
					
					}
				}.runTaskTimer(Main.getInstance(), 0, 20);
				return true;
		}
		return super.ItemUse(item, gameState);
	}


	@Override
	public String getName() {
		return "Gaï";
	}
}
