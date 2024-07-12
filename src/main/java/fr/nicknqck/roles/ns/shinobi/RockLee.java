package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.particles.DoubleCircleEffect;
import net.minecraft.server.v1_8_R3.EnumParticle;

public class RockLee extends ShinobiRoles {

	public RockLee(Player player) {
		super(player);
		setChakraType(getRandomChakras());
		owner.sendMessage(Desc());
		giveItem(owner, false, getItems());
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), () ->{
			if (!gameState.attributedRole.contains(Roles.Gai)) {
				giveItem(owner, false, HuitPortesItem());
			} 
		}, cdDrunkenFist);
	}

	@Override
	public Intelligence getIntelligence() {
		return Intelligence.PEUINTELLIGENT;
	}

	@Override
	public Roles getRoles() {
		return Roles.RockLee;
	}
	@Override
	public String[] Desc() {
		KnowRole(owner, Roles.Gai, 5);
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§aRock Lee",
				AllDesc.objectifteam+"§aShinobi",
				"",
				AllDesc.items,
				"",
				"§aTroisième porte :§r vous octroie l'effet "+AllDesc.Speed+" 1 pendant 1 minute 30 mais vous perdrez 1 "+AllDesc.coeur,
				"§aSixième porte : §rvous octroie les effets "+AllDesc.Force+" 1 ainsi que "+AllDesc.Speed+" 1 pendant 2 minute mais vous perdrez 1"+AllDesc.coeur+" permanent",
				"§aAlcool : §rVous donne "+AllDesc.nausee+" 1 ainsi que "+AllDesc.Speed+" 1 pendant 1 minute cependant vous aurez "+AllDesc.blind+" 1 pendant les 15 premières secondes",
				"§cHuitième porte : §rvous octroie "+AllDesc.Speed+" 2, "+AllDesc.Force+" 1, "+AllDesc.Resi+" 1, "+AllDesc.fireResi+" 1 ainsi que 5"+AllDesc.coeur+" Supplémentaire et vous obtiendrez cependant vous tomberez à 5 "+AllDesc.coeur+" permanent et vous aurez "+AllDesc.weak+" 1 pendant 15 min",
				"",
				AllDesc.particularite,
				"",
				"Vous régénerez 1 coeur permanent toute les 6 minutes",
				"Vous connaissez le joueur possédant le rôle de §aGaï Maito",
				"Si Gaï Maito viens à mourir vous obtiendrez la §cHuitième porte",
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
				DrunkenFistItem()
				};
	}

	@Override
	public void resetCooldown() {
		cdTroisPortes = 0;
		cdSixPortes = 0;
		cdDrunkenFist = 0;
		DrunkenFist = false;
		
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
	private ItemStack DrunkenFistItem() {
		return new ItemBuilder(Material.GLASS_BOTTLE).setName("§aAlcool").setLore("§7Vous fait devenir bourré").toItemStack();
	}
	private int cdTroisPortes = 0;
	private int cdSixPortes = 0;
	private int cdDrunkenFist = 0;
	private boolean DrunkenFist = false;
	
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
		if (cdDrunkenFist >= 0) {
			cdDrunkenFist --;
			if (cdDrunkenFist == 0) {
				owner.sendMessage("Vous pouvez de nouveaus utiliser votre Alccol");
			}
		}
		super.Update(gameState);
	}
	
	@Override
	public void OnAPlayerDie(Player player, GameState gameState, Entity killer) {
		if (gameState.getInGamePlayers().contains(owner)) {
			if (getListPlayerFromRole(Roles.Gai).contains(player)) {
				owner.sendMessage("§aGaï Maito §rviens de mourir vous obtenez désormais la §cHuitième porte");
				giveItem(owner, false, HuitPortesItem());
			}
		}
	}
	
	@Override
	public void onALLPlayerDamageByEntity(EntityDamageByEntityEvent event, Player victim, Entity entity) {
		if (victim.getUniqueId() == owner.getUniqueId() && entity instanceof Player) {
			if (DrunkenFist) {
				if (gameState.getInGamePlayers().contains(entity)) {
					if (RandomUtils.getOwnRandomProbability(20)) {
						owner.sendMessage("Vous venez d'esquiver un coup");
						event.setDamage(0.0);
					}
				}
			}
		}
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
				setMaxHealth(getMaxHealth()-2.0);
				owner.setMaxHealth(getMaxHealth());
				cdSixPortes = 60*6;
				new BukkitRunnable() {
					int intVie = 0;
					@Override
					public void run() {
						if (gameState.getInGamePlayers().contains(owner)) {
						intVie ++;
						} else {
							cancel();
						}
						if (intVie == 60*6) {
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
				owner.getInventory().removeItem(HuitPortesItem());
				owner.getInventory().addItem(DrunkenFistItem()); 
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
		if (item.isSimilar(DrunkenFistItem())) {
			if (cdDrunkenFist <= 0) {
				givePotionEffet(PotionEffectType.CONFUSION, 20*60, 1, true);
				givePotionEffet(PotionEffectType.SPEED, 20*60, 1, true);
				DrunkenFist = true;
				cdDrunkenFist = 60*4;
				
				new BukkitRunnable() {
					int intbourre = 0;
					@Override
					public void run() {
						if (gameState.getServerState() == ServerStates.InGame) {
							intbourre ++;
						} else {
							cancel();
						}
						if (intbourre == 60) {
							DrunkenFist = false;
							cancel();
						}
						
					}
				};
            } else {
				sendCooldown(owner, cdDrunkenFist);
            }
            return true;
        }
		return super.ItemUse(item, gameState);
	}


	@Override
	public String getName() {
		return "§aRock Lee";
	}
}
