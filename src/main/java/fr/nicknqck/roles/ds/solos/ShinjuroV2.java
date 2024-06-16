package fr.nicknqck.roles.ds.solos;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.betteritem.BetterItem;

public class ShinjuroV2 extends RoleBase{

	public ShinjuroV2(Player player) {
		super(player);
		owner.sendMessage(Desc());
		setCanUseBlade(true);
		setLameIncassable(owner, true);
		setLameFr(true);
	}
	@Override
	public Roles getRoles() {
		return Roles.ShinjuroV2;
	}
	@Override
	public String[] Desc() {
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§6ShinjuroV2",
				AllDesc.objectifsolo+"§6Seul",
				"",
				"§lPassif: ",
				"",
				AllDesc.point+"Lorsque vous êtes en§c feu§f vous vous régénèrerez à auteurs de 1/2"+AllDesc.coeur+" toute les 10 secondes",
				"",
				AllDesc.effet,
				"",
				AllDesc.point+AllDesc.Speed+"§e 1§f et "+AllDesc.fireResi+"§6 1§f permanent",
				"",
				AllDesc.items,
				"",
				AllDesc.point+"§6Sake§f: Permet via un clique droit de gagner entre 0 et 30% d'ivresse, vous recevrez des bonus/malus pour chaque stade d'ivresse",
				"§aStade 1§f: Entre 20% et 50% d'ivresse, vous donne "+AllDesc.Force+"§c 1.",
				"§cStade 2§f: Entre 50% et 80% d'ivresse, vous perdez l'effet "+AllDesc.Force+", cependant vos coup seront quoi qu'il arrive en§6 feu",
				"§6Stade 3§f: Entre 80% et 100% d'ivresse, vous recevez l'effet "+AllDesc.Resi+", cependant il y aura 50% de chance qu'en buvant votre§6 Sake§f vous gagnerez l'effet "+AllDesc.nausee+"§2 1§f pendant 1 minutes.",
				"§fToute les 6 secondes vous perdez 1% d'ivresse.",
				"",
				AllDesc.commande,
				"",
				AllDesc.point+"§6/ds flamme§f: Celà vous permet d'activer votre passif vous permettant que pour chaque coup d'épée il y est 25% de chance que la cible sois§6 enflammée",
				"",
				AllDesc.amelioration,
				"",
				AllDesc.point+"Si vous parvennez à tuer un joueur possédant le rôle de§a Kyojuro§f, vous gagnerez plusieurs avantage: ",
				"1: Votre "+AllDesc.regen+" dans la§6 lave§f deviendra 0.5"+AllDesc.coeur+" toutes les 5 secondes",
				"2: Le cooldown de§6 Sake§f deviendra 10 secondes au lieu de 15",
				"3: Le§6 /ds flamme§f augmentera son pourcentage de change de mettre en§c feu§f la cible de 25%",
				"",
				AllDesc.bar
		};
	}
	private boolean flamme = false;
	private int ivresse = 0;
	private int cooldownsake = 0;
	private boolean killkyojuro = false;
	private int timingivresse = 0;
	private int regencooldown = 10;
	@Override
	public void resetCooldown() {
		cooldownsake = 0;
		regencooldown = 0;
	}

	@Override
	public String getName() {
		return "§eShinjuro§7 (§6V2§7)";
	}

	private enum Stade {
		Stade0,
		Stade1,
		Stade2,
		Stade3;
	}
	private Stade stade = Stade.Stade0;
	@Override
	public void Update(GameState gameState) {
		givePotionEffet(owner, PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false);
		givePotionEffet(owner, PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1, false);
		if (owner.getItemInHand().isSimilar(SakeVideItem()) || owner.getItemInHand().isSimilar(SakeRemplieItem())) {
			sendActionBarCooldown(owner, cooldownsake);
		}else {
			sendCustomActionBar(owner, "§bIvresse: "+gameState.sendGazBar(ivresse, 2)+"§f (§b"+(ivresse)+"%§f)");
		}
		if (cooldownsake >= 1) {
			cooldownsake--;
		}else if (cooldownsake == 0) {
			cooldownsake--;
			owner.sendMessage("§7Votre§6 Sake§7 est à nouveau utilisable !");
		}
		if (regencooldown >= 1) {
			   regencooldown--;
		}
		if (timingivresse == 0) {
			if (ivresse >= 1.0) {
				ivresse-=1.0;
				timingivresse = 6;
			}else {
				ivresse =0;
				timingivresse = 6;
			}
		} else if (timingivresse > 0) {
			timingivresse--;
		}
		if (ivresse <= 100) {
			if (ivresse < 20.0) {
				stade = Stade.Stade0;
			}
			if (ivresse >= 20.0 && ivresse < 50.0) {
				stade = Stade.Stade1;
			}
			if (ivresse >= 50.0 && ivresse < 80.0) {
				stade = Stade.Stade2;
			}
			if (ivresse >= 80.0 && ivresse <= 100.0) {
				stade = Stade.Stade3;
			}
		}else {
			ivresse = 100;
		}
		switch (stade) {
		case Stade0:
			break;
		case Stade1:
			givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
			break;
		case Stade2:
			break;
		case Stade3:
			givePotionEffet(owner, PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true);
			break;
		default:
			break;
		}
	}
	@Override
	public void onDSCommandSend(String[] args, GameState gameState) {
		if (args[0].equalsIgnoreCase("flamme")) {
			if (flamme) {
				flamme = false;
				owner.sendMessage("§7Désactivation du§6 /ds flamme");
			}else {
				flamme = true;
				owner.sendMessage("§7Activation du§6 /ds flamme");
			}
		}
	}
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(getItems());
		owner.getInventory().addItem(Items.getLamedenichirin());
	}
	@Override
	public void onALLPlayerInteract(PlayerInteractEvent event, Player player) {
	    if (player.getUniqueId().equals(owner.getUniqueId())) {
	        if (event.hasItem() && event.getItem().isSimilar(SakeVideItem())) {
	            Block clickedBlock = event.getClickedBlock();
	            if (clickedBlock != null && owner.getWorld().getHighestBlockAt(new Location(clickedBlock.getWorld(), clickedBlock.getX(), clickedBlock.getY()+1, clickedBlock.getZ())).getType().name().contains("WATER")) {
	                owner.getInventory().removeItem(SakeVideItem());
	                owner.updateInventory();
	                owner.sendMessage("§7Vous avez rempli votre fiole de §6Sake");
	                giveItem(owner, false, SakeRemplieItem());
	                event.setCancelled(true);
	                return;
	            }
	        }
	    }
	}
	private ItemStack SakeVideItem() {
		return new ItemBuilder(Material.GLASS_BOTTLE).setName("§6Sake").setLore("§7Vous permet de remplir votre bouteille d'alcool","§7"+StringID).setDurability(0).toItemStack();
	}
	private ItemStack SakeRemplieItem() {
		return new ItemBuilder(Material.POTION).setName("§6Sake").setDurability(0).setLore("§7Vous permet de boire votre bouteille d'alcool","§7"+StringID).toItemStack();
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				BetterItem.of(SakeRemplieItem(), event -> {
					if (event.isRightClick()){
						if (cooldownsake <= 0) {
							int rdm = RandomUtils.getRandomInt(1, 30);
							ivresse += rdm;
							if (ivresse >= 80) {
								if (RandomUtils.getOwnRandomProbability(30)) {
									givePotionEffet(owner, PotionEffectType.CONFUSION, 20*60, 1, true);
									owner.sendMessage("§7L'alcool vous monte à la tête...");
								}
							}
							owner.getInventory().removeItem(event.getItemStack());
							owner.updateInventory();
							giveItem(owner, false, SakeVideItem());
							owner.sendMessage("§7Vous venez de gagnée§b "+(rdm)+"%§7 d'ivresse");
							owner.updateInventory();
							if (killkyojuro) {
								cooldownsake = 10;
							}else {
								cooldownsake = 15;
							}
						}else {
							sendCooldown(owner, cooldownsake);
						}
					}
					return true;
				}).setDroppable(false).setDespawnable(true).setMovableOther(false).setPosable(false).setLeftClick(false).getItemStack()
		};
	}
	@Override
	public void onTick() {
		Material m = owner.getPlayer().getLocation().getBlock().getType();
		 Location y1 = new Location(owner.getWorld(), owner.getLocation().getX(), owner.getLocation().getY()+1, owner.getLocation().getZ());
		 Material a = y1.getBlock().getType();
		    if (m == Material.LAVA || m == Material.STATIONARY_LAVA || a == Material.LAVA || a == Material.STATIONARY_LAVA || owner.getFireTicks() > 0) { 
		   if (owner.getHealth() != getMaxHealth()) {
			   if (regencooldown == 0) {
				   double max = this.getMaxHealth();
				   double ahealth = owner.getHealth();
				   double dif = max-ahealth;
				  if (!(dif <= 1.0)) {
					 Heal(owner, 1);
					 owner.sendMessage("§7Vous venez de gagné§c 1/2"+AllDesc.coeur+"§7 suite à votre temp passé au chaud");
				  } else {
					  owner.setHealth(max);
				  }
				  if (!killkyojuro) {
					  regencooldown = 10;
				  }else {
					  regencooldown = 5;
				  }
			   }
		   }
		    } else {
		    	if (!killkyojuro) {
		    		if (regencooldown != 10) regencooldown = 10;
		    	}else {
		    		if (regencooldown != 5) regencooldown = 5;
		    	}
		    }
	}
	@Override
	public void neoItemUseAgainst(ItemStack itemInHand, Player player, GameState gameState, Player damager) {
		if (damager == owner) {
			if (itemInHand != null && itemInHand.getType() != Material.AIR) {
				if (flamme) {
					if (stade == Stade.Stade2) {
						player.setFireTicks(player.getFireTicks()+100);
					}else {
						if (killkyojuro) {
							if (RandomUtils.getRandomProbability(50)) {
								player.setFireTicks(player.getFireTicks()+100);
							}
						}else {
							if (RandomUtils.getRandomProbability(25)) {
								player.setFireTicks(player.getFireTicks()+100);
							}
						}
					}					
				}
			}
		}
		super.neoItemUseAgainst(itemInHand, player, gameState, damager);
	}
	@Override
	public void onALLPlayerDamage(EntityDamageEvent e, Player victim) {
		if (e.getCause().equals(DamageCause.PROJECTILE)) {
			if (victim.getLastDamageCause() == null)return;
			if (victim.getLastDamageCause().getEntity() instanceof Player) {
				if (victim.getLastDamageCause().getEntity() == owner) {
					if (flamme) {
						if (stade == Stade.Stade2) {
							victim.setFireTicks(victim.getFireTicks()+100);
						}else {
							if (!killkyojuro) {
								if (RandomUtils.getRandomProbability(25)) {
									victim.setFireTicks(victim.getFireTicks()+100);
								}
							}else {
								if (RandomUtils.getRandomProbability(50)) {
									victim.setFireTicks(victim.getFireTicks()+100);
								}
							}
						}
					}
				}
			}
		}
		super.onALLPlayerDamage(e, victim);
	}
}