package fr.nicknqck.roles.aot.builders.titans;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.aot.solo.Gabi;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.*;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Dash;
import lombok.Getter;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Bestial extends Titan{

	public enum Animal {
		Taureau("§4", 0, getInstance().DashItem()),
		Singe("§c", 1, getInstance().Items()),
		Crocodile("§b", 2, getInstance().Items()),
		Oiseau("§a", 3, getInstance().FlyItem(), getInstance().AileItem()),
		Okapi("§2", 4, getInstance().LangueItem());
		@Getter
		private final String color;
		private final int number;
		private final ItemStack[] item;
		Animal(String color, int i, ItemStack... items) {
			this.color = color;
			this.number = i;
			this.item = items;
		}

		public ItemStack[] getItems(){
			return item;
		}
	}
	public Animal form = null;
	@Override
	public void onInteract(PlayerInteractEvent e, Player player) {
		if (getOwner() != null) {
			if (player.getUniqueId() == getOwner()) {
				if (e.getItem() != null) {
					if (e.getItem().isSimilar(DashItem())) {
						if (form == Animal.Taureau) {
							if (isTransformedinTitan()) {
								if (DashCooldown <= 0) {
									DashCooldown = 60;
							//		new Dash(getOwner(), 30, true, 4.0).playSound("aotmtp.bestialcri").start(3);
									new Dash(player).setDistance(30).setDegatBoolean(true).setDegatDouble(2.0).start(2, 2.0, 0.2);
								} else {
									sendCooldown(player, DashCooldown);
								}
							}
						}
					}
					if (e.getItem().isSimilar(TransfoItem())) {
						Transfo();
					}
					if (e.getItem().isSimilar(FlyItem())) {
						if (form == Animal.Oiseau) {
							if (isTransformedinTitan()) {
								if (FlyCooldown <= 0) {
									if (getListener().getBestialCooldown() > 60*5) {
										getListener().setBestialCooldown(getListener().getBestialCooldown()-60);
										player.sendMessage("§7Activation du fly");
										player.setAllowFlight(true);
										player.setFlying(true);
										FlyCooldown = 90;
									} else {
										player.sendMessage("§7Il ne vous reste pas assez de temp de transformation...");
									}
								} else {
									sendCooldown(player, FlyCooldown);
								}
							} else {
								player.sendMessage("§cIl faut être transformé en Titan !");
							}
						}
					}
					if (e.getItem().isSimilar(AileItem())) {
						if (form == Animal.Oiseau) {
							if (isTransformedinTitan()) {
								if (AileCooldown <= 0) {
									for (Player p : Loc.getNearbyPlayersExcept(player, 30)) {
										Vector direction = p.getLocation().getDirection();
							            direction.setY(0.8);
							            direction.setX(1);
							            direction.setZ(1);
							            p.setVelocity(direction.multiply(4));
										p.sendMessage("§7Vous avez été éjecté !");
									}
									AileCooldown = 60;
								} else {
									sendCooldown(player, AileCooldown);
								}
							}
						}
					}
					if (e.getItem().isSimilar(LangueItem())) {
						if (form == Animal.Okapi) {
							if (isTransformedinTitan()) {
								if (LangueCooldown <= 0) {
									Player target = getPlayerRole(player.getUniqueId()).getTargetPlayer(player, 30);
									if (target != null) {
										target.teleport(player);
										player.sendMessage("§7Téléportation de "+form.getColor()+"§l"+target.getName()+"§7 à votre position");
										target.sendMessage("§7Le Titan "+form.getColor()+form.name()+"§7 vous à téléporté à sa position");
										LangueCooldown = 60;
									}
								} else {
									sendCooldown(player, LangueCooldown);
								}
							}
						}
					}
				}
			}
		}
	}
	private int FlyCooldown = 0;
	private int LangueCooldown = 0;
	@Getter
	private static Bestial instance;

	public Bestial() {
		instance = this;
	}
	@Override
	public void onSecond() {
		if (getOwner() != null) {
			Player player = Bukkit.getPlayer(getOwner());
			if (form != null && player != null) {
				if (isTransformedinTitan()) {
					if (form == Animal.Crocodile) {
						getPlayerRole(getOwner()).givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true);
						if (getPlayerRole(getOwner()).getResi() < 20) {
							getPlayerRole(getOwner()).setResi(20);
						}
						if (player.getLocation().getBlock().getType().name().contains("WATER")) {
							getPlayerRole(getOwner()).givePotionEffet(PotionEffectType.WATER_BREATHING, 60, 1, true);
							getPlayerRole(getOwner()).givePotionEffet(PotionEffectType.SPEED, 60, 2, true);
						}else {
							getPlayerRole(getOwner()).givePotionEffet(PotionEffectType.SPEED, 60, 1, true);
						}
					}
					if (form == Animal.Oiseau) {
						getPlayerRole(getOwner()).givePotionEffet(PotionEffectType.SPEED, 60, 1, true);
					}
					if (form == Animal.Singe) {
						getPlayerRole(getOwner()).givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
						getPlayerRole(getOwner()).givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true);
						if (getPlayerRole(getOwner()).getForce() < 20) {
							getPlayerRole(getOwner()).setForce(20);
						}
						if (getPlayerRole(getOwner()).getResi() < 20) {
							getPlayerRole(getOwner()).setResi(20);
						}
					}
					if (form == Animal.Okapi) {
						getPlayerRole(getOwner()).givePotionEffet(PotionEffectType.SPEED, 60, 1, true);
						getPlayerRole(getOwner()).givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
						if (getPlayerRole(getOwner()).getForce() < 20) {
							getPlayerRole(getOwner()).setForce(20);
						}
					}
					if (form == Animal.Taureau) {
						getPlayerRole(getOwner()).givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
						if (getPlayerRole(getOwner()).getForce() < 20) {
							getPlayerRole(getOwner()).setForce(20);
						}
					}
					int cd = getListener().getBestialCooldown()-(60*4);
					getPlayerRole(player.getUniqueId()).sendCustomActionBar(player, "§bTemp restant de transformation: "+StringUtils.secondsTowardsBeautiful(cd));
				}
				if (player.getItemInHand().isSimilar(TransfoItem())) {
					getPlayerRole(getOwner()).sendActionBarCooldown(player, getListener().getBestialCooldown());
				}
				if (form == Animal.Oiseau) {
					if (FlyCooldown > 0) {
						FlyCooldown-=1;
						if (FlyCooldown >= 80) {
							int cd = FlyCooldown-80;
							getPlayerRole(getOwner()).sendCustomActionBar(player, "§bTemp restant de Fly: "+cd+"s");
						}
						if (FlyCooldown == 80) {
							player.setAllowFlight(false);
							player.setFlying(false);
							player.sendMessage("§7Vous ne voler plus...");
						}
					}
					if (AileCooldown > 0) {
						AileCooldown --;
						if (player.getItemInHand().isSimilar(AileItem())) {
							getPlayerRole(getOwner()).sendActionBarCooldown(player, AileCooldown);
						}
					}
					if (AileCooldown == 0) {
						player.sendMessage(AileItem().getItemMeta().getDisplayName()+"§7 est à nouveau utilisable !");
						AileCooldown--;
					}
					if (FlyCooldown == 0) {
						player.sendMessage(FlyItem().getItemMeta().getDisplayName()+"§7 est à nouveau utilisable !");
						FlyCooldown--;
					}
					if (player.getItemInHand().isSimilar(FlyItem())) {
						getPlayerRole(getOwner()).sendActionBarCooldown(player, FlyCooldown);
					}
				}
				if (form == Animal.Okapi) {
					if (LangueCooldown > 0) {
						LangueCooldown--;
					}
					if (LangueCooldown == 0) {
						player.sendMessage(LangueItem().getItemMeta().getDisplayName()+"§7 est à nouveau utilisable !");
						LangueCooldown--;
					}
					if (player.getItemInHand().isSimilar(LangueItem())) {
						getPlayerRole(getOwner()).sendActionBarCooldown(player, LangueCooldown);
					}
				}
				if (form == Animal.Taureau) {
					if (DashCooldown > 0) {
						DashCooldown--;
					}
					if (DashCooldown == 0) {
						player.sendMessage(DashItem().getItemMeta().getDisplayName()+"§7 est à nouveau utilisable !");
						DashCooldown--;
					}
					if (player.getItemInHand().isSimilar(DashItem())) {
						getPlayerRole(getOwner()).sendActionBarCooldown(player, DashCooldown);
					}
				}
			}
		}
	}
	private int DashCooldown = 0;
	private int AileCooldown = 0;
	@Override
	public void onBlockBreak(BlockBreakEvent e, Player player) {}
	@Override
	public void Transfo() {
		Player player = Bukkit.getPlayer(getOwner());
		if (player == null)return;
		if (form != null) {
				if (!isTransformedinTitan()) {
					if (getListener().getBestialCooldown() <= 0) {
						setTransformedinTitan(true);
						getPlayerRole(getOwner()).isTransformedinTitan = true;
						TransfoMessage(player, true);
						player.sendMessage("§7Transformation en Titan "+getName()+"§7 ("+form.getColor()+form.name()+"§7)");
						getListener().setBestialCooldown(60*8);
						if (form == Animal.Oiseau) {
							PotionUtils.effetGiveNofall(player);
						}
						for (ItemStack stack : form.getItems()) {
							if (!stack.isSimilar(TransfoItem())) {
								getPlayerRole(getOwner()).giveItem(player, true, stack);
							}
						}
						if (form == Animal.Singe) {
							getPlayerRole(getOwner()).giveHealedHeartatInt(3);
						}
					}else {
						sendCooldown(player, getListener().getBestialCooldown());
					}
				} else {
					if (getListener().getBestialCooldown() > 60*7+30) {
						return;
					}
					setTransformedinTitan(false);
					getPlayerRole(getOwner()).isTransformedinTitan = false;
					TransfoMessage(player, false);
					player.sendMessage("§7Transformation en§l humain");
					getListener().setBestialCooldown(60*4);
					ItemStack boots = player.getInventory().getBoots();
					if (boots != null) {
						ItemMeta bMeta = boots.getItemMeta();
						if (bMeta.getEnchants().containsKey(Enchantment.DEPTH_STRIDER)) {
							bMeta.removeEnchant(Enchantment.DEPTH_STRIDER);
							boots.setItemMeta(bMeta);
						}
					}
					if (player.getInventory().contains(FlyItem())) {
						player.getInventory().remove(FlyItem());
					}
					if (player.getInventory().contains(LangueItem())) {
						player.getInventory().remove(LangueItem());
					}
					PotionUtils.effetRemoveNofall(player);
					if (form == Animal.Singe) {
						getPlayerRole(getOwner()).setMaxHealth(player.getMaxHealth()-6.0);
						player.setMaxHealth(getPlayerRole(getOwner()).getMaxHealth());
						if (getPlayerRole(getOwner()).getResi() > 0) {
							getPlayerRole(getOwner()).setResi(0);
						}
						if (getPlayerRole(getOwner()).getForce() > 0) {
							getPlayerRole(getOwner()).setForce(0);
						}
					}
					if (form == Animal.Crocodile) {
						if (getPlayerRole(getOwner()).getResi() > 0) {
							getPlayerRole(getOwner()).setResi(0);
						}
					}
					if (form == Animal.Taureau) {
						if (getPlayerRole(getOwner()).getForce() > 0) {
							getPlayerRole(getOwner()).setForce(0);
						}
					}
					if (form == Animal.Okapi) {
						if (getPlayerRole(getOwner()).getForce() > 0) {
							getPlayerRole(getOwner()).setForce(0);
						}
					}
				}
		} else {
			int animalCount = Animal.values().length-1;
			int rdm = getRandomInt(0, animalCount);
			for (Animal a : Animal.values()) {
				if (rdm == a.number) {
					this.form = a;
					player.sendMessage("§7Vous avez obtenue la forme "+a.getColor()+a.name()+"§7 du Titan "+getName());
					return;
				}
			}
		}
	}
	public int getRandomInt(int min, int max) {
		int toReturn = -1;
		do {
			toReturn = RandomUtils.getRandomInt(min, max);
		} while (toReturn == -1 || toReturn < min || toReturn > max);
		return toReturn;
	}
	@Override
	public void onAPlayerDie(Player player, Entity killer) {
		if (getOwner() != null) {
			if (player.getUniqueId() == getOwner()) {
				for (Player p : Loc.getNearbyPlayersExcept(player, 30)) {
					if (!getState().hasRoleNull(p)) {
						if (getPlayerRole(p.getUniqueId()).isCanVoleTitan() && canStealTitan(p)) {
							canSteal.add(p);
							p.sendMessage("§7Vous pouvez maintenant volé le Titan "+getName()+"§7 avec la commande§l /aot steal");
						}
					}
				}
				getListener().setBestial(null);
				resetCooldown();
			}
		}
	}
	private ItemStack TransfoItem() {
		return new ItemBuilder(Material.FEATHER).setName("§6§lTransformation").setLore("§7» Vous transforme en§c Titan ou vous fait redevenir humain").addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().toItemStack();
	}
	@Override
	public void resetCooldown() {
		FlyCooldown = 0;
		LangueCooldown = 0;
		form = null;
		AileCooldown = 0;
		getListener().setBestialCooldown(0);
	}
	@Override
	public ItemStack[] Items() {
		return new ItemStack[] {
				TransfoItem()
		};
	}
	@Override
	public void onSteal(Player sender, String[] args) {
		if (canSteal.contains(sender)) {
			if (getOwner() == null) {
				if (getPlayerRole(sender.getUniqueId()).isCanVoleTitan()) {
					if (getPlayerRole(sender.getUniqueId()) instanceof Gabi) {
						getPlayerRole(sender.getUniqueId()).setMaxHealth(20.0);
						sender.setMaxHealth(getPlayerRole(sender.getUniqueId()).getMaxHealth());
						sender.updateInventory();
					}
					getListener().setBestial(sender.getUniqueId());
					canSteal.clear();
					sender.sendMessage("§7Vous venez de voler le Titan "+getName()+"§7§l /aot titan§7 pour plus d'information");
					getPlayerRole(getOwner()).giveItem(sender, true, Items());
					getPlayerRole(sender.getUniqueId()).setCanVoleTitan(false);
				}
			}
		}
	}
	@Override
	public void onAotTitan(Player player, String[] args) {
		if (getOwner() != null) {
			if (player.getUniqueId() == getOwner()) {
				if (form == null) {
					player.sendMessage(new String[] {
							AllDesc.bar,
							"§7Titan: "+getName(),
							"",
							"§7Vous ne possédez aucune forme précise, pour en obtenir une vous pouvez utilisé votre item de transformation en Titan (celà ne vous transformera pas)",
							AllDesc.bar
					});
				}else {
					player.sendMessage(AllDesc.bar);
					player.sendMessage("§7Titan: "+getName()+"§7 ("+form.color+form.name()+"§7)");
					player.sendMessage("");
					player.sendMessage("§7Votre transformation vous offres: ");
					if (form == Animal.Crocodile) {
						player.sendMessage(new String[] {
								"§7 - L'effet "+AllDesc.Speed+"§e 1§7 (hors de l'§beau§7) et "+AllDesc.Resi+"§9 1§7 permanent",
								"§7 - Dans l'§beau§7, vous obtiendrez l'enchantement§b§l Depth Strider 3§7, ainsi que "+AllDesc.Speed+"§e 2§7 et§3 Water Breathing"
						});
					}
					if (form == Animal.Oiseau) {
						player.sendMessage(new String[] {
								"§7 - L'effet "+AllDesc.Speed+"§e 1§7 ainsi que§a No Fall§7.",
								"§7Vous aurez accès à l'item "+FlyItem().getItemMeta().getDisplayName()+"§7, ce qui vous permettra de voler pendant 10s en vous faisant perdre 1 minute de transformation"
						});
					}
					if (form == Animal.Singe) {
						player.sendMessage(new String[] {
								"§7 - Les effets "+AllDesc.Force+"§c 1§7, "+AllDesc.Resi+"§6 1§7 ainsi que§c 3"+AllDesc.coeur+"§7 supplémentaire",
						});
					}
					if (form == Animal.Okapi) {
						player.sendMessage(new String[] {
								"§7 - Les effets "+AllDesc.Speed+"§e 1§7, "+AllDesc.Force+"§c 1§7.",
								"§7Vous aurez accès à l'item "+LangueItem().getItemMeta().getDisplayName()+"§7 qui téléportera le joueur visé à votre position."
						});
					}
					if (form == Animal.Taureau) {
						player.sendMessage(new String[] {
								"§7 - L'effet "+AllDesc.Force+" 1",
								"§7Vous aurez accès à l'item "+DashItem().getItemMeta().getDisplayName()+"§7 qui effectura un dash dans la direction que vous visé en infligant§c 2"+AllDesc.coeur+"§7 au joueur proche"
						});
					}
					player.sendMessage(AllDesc.bar);
					
				}
			}
		}
	}
	private ItemStack FlyItem() {
		return new ItemBuilder(Material.FEATHER).setName("§f§lFly").setLore("§7Vous permet de fly pendant 10s").toItemStack();
	}
	private ItemStack LangueItem() {
		return new ItemBuilder(Material.ROTTEN_FLESH).setName("§c§lLangue").setLore("§fVous permet d'attirer un joueur sur vous").toItemStack();
	}
	private ItemStack DashItem() {
		return new ItemBuilder(Material.FEATHER).setName("§4§lDash").setLore("§fVous permet d'effectuer un dash en avant").toItemStack();
	}
	private ItemStack AileItem() {
		return new ItemBuilder(Material.QUARTZ).setName("§fAiles").setLore("§7Vous permet de faire voltiger les joueurs autours de vous").toItemStack();
	}
	@Override
	public UUID getOwner() {
		return getListener().getBestial();
	}
	@Override
	public void onGetDescription(Player player) {
		if (getOwner() != null) {
			if (getOwner() == player.getUniqueId()) {
				if (form != null) {
					getPlayerRole(player.getUniqueId()).sendMessageAfterXseconde(player, "§7Vous possédez le Titan "+getName()+"§7 ("+form.color+form.name()+"§7)", 1);
				} else {
					getPlayerRole(player.getUniqueId()).sendMessageAfterXseconde(player, "§7Vous possédez le Titan "+getName()+"§7 (Incomplet)", 1);
				}
			}
		}
	}
	@Override
	public String getName() {
		return "§cBestial";
	}

	@Override
	public void onSubCommand(Player player, String[] args) {
		if (args[0].equalsIgnoreCase("oiseau")) {
			form = Animal.Oiseau;
		}
	}

	@Override
	public void onPickup(PlayerPickupItemEvent e, Player player) {}
	List<Player> canSteal = new ArrayList<>();
	@Override
	public List<Player> getListforSteal() {
		return canSteal;
	}
	@Override
	public void PlayerKilled(Player player, Entity damager) {}
	public void onTick(GameState gameState) {
		if (form != null) {
			if (form.equals(Animal.Crocodile)) {
				Player player = Bukkit.getPlayer(getOwner());
				if (player == null)return;
				if (getOwner() != null) {
					if (isTransformedinTitan()) {
						if (player.getLocation().getBlock().getType().name().contains("WATER")) {
							if (player.getInventory().getBoots() != null) {
								ItemStack boots = player.getInventory().getBoots();
								if (boots.hasItemMeta()) {
									ItemMeta meta = boots.getItemMeta();
									if (!meta.hasEnchant(Enchantment.DEPTH_STRIDER)) {
										meta.addEnchant(Enchantment.DEPTH_STRIDER, 3, true);
										boots.setItemMeta(meta);
									}
								}
							}
						} else {
							if (player.getInventory().getBoots() != null) {
								ItemStack boots = player.getInventory().getBoots();
								if (boots.hasItemMeta()) {
									ItemMeta meta = boots.getItemMeta();
									if (meta.hasEnchants()) {
										if (meta.hasEnchant(Enchantment.DEPTH_STRIDER)) {
											meta.removeEnchant(Enchantment.DEPTH_STRIDER);
											boots.setItemMeta(meta);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	@Override
	public void onPlayerAttackAnotherPlayer(Player damager, Player victim, EntityDamageByEntityEvent event) {}
	}
