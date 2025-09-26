package fr.nicknqck.roles.ds.demons.lune;

import fr.nicknqck.enums.Roles;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.demons.MuzanV2;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.particles.WingsEffect;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

import java.util.UUID;

public class Hantengu extends DemonsRoles {
	private int killforce = 0;
	public Hantengu(UUID player) {
		super(player);
		getKnowedRoles().add(MuzanV2.class);
	}

	@Override
	public @NonNull DemonType getRank() {
		return DemonType.SUPERIEUR;
	}

	@Override
	public @NonNull Roles getRoles() {
		return Roles.Hantengu;
	}
	private enum Form{
		Zohakuten,
		Sekido,
		Urogi,
		Karaku,
		Hantengu
	}
	@Override
	public @NonNull TeamList getOriginTeam() {
		return TeamList.Demon;
	}
	@Override
		public String[] Desc() {
		getKnowedRoles().add(MuzanV2.class);
			return AllDesc.Hantengu;
		}
	
	Form form = Form.Hantengu;
	private int Zohakuten = 0;
	private int Sekido = 0;
	private int Urogi = 0;
	private int Karaku = 0;
	private int powerKaraku = 0;
	private boolean powerUrogi = false;
	private int flytime = 5;
	private int zohatime = 0;
	private int sekitime = 0;
	private int urotime = 0;
	private int karakutime = 0;
	@Override
	public void resetCooldown() {
		Zohakuten = 0;
		Sekido = 0;
		Urogi = 0;
		Karaku = 0;
		powerKaraku = 0;
		powerUrogi = false;
		flytime = 5;
		zohatime = 0;
		sekitime = 0;
		urotime = 0;
		karakutime = 0;
	}

	@Override
	public String getName() {
		return "Hantengu";
	}

	@Override
		public void GiveItems() {
			owner.getInventory().addItem(Items.getHantenguZohakuten());
			owner.getInventory().addItem(Items.getHantenguSekido());
			owner.getInventory().addItem(Items.getHantenguUrogi());
			owner.getInventory().addItem(Items.getHantenguKaraku());
			owner.getInventory().addItem(Items.getHantenguKarakuVent());
			owner.getInventory().addItem(Items.getHantenguUrogiFly());
			super.GiveItems();
		}
	@Override
		public ItemStack[] getItems() {
			return new ItemStack[] {
					Items.getHantenguZohakuten(),
					Items.getHantenguSekido(),
					Items.getHantenguUrogi(),
					Items.getHantenguKaraku(),
					Items.getHantenguKarakuVent(),
					Items.getHantenguUrogiFly()
			};
		}
	@Override
		public void Update(GameState gameState) {
		if (owner.getItemInHand().isSimilar(Items.getHantenguZohakuten())) {
			sendActionBarCooldown(owner, zohatime);
		}
		if (owner.getItemInHand().isSimilar(Items.getHantenguKaraku())) {
			sendActionBarCooldown(owner, Karaku);
		}
		if (owner.getItemInHand().isSimilar(Items.getHantenguSekido())) {
			if (Sekido > 0) {
					String message = "Temp de transformation restant:§6 "+StringUtils.secondsTowardsBeautiful(sekitime)+"§r Cooldown: §6"+StringUtils.secondsTowardsBeautiful(Sekido);
					PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte)2);
					((CraftPlayer) owner).getHandle().playerConnection.sendPacket(packet);
			} else {
					String message = owner.getItemInHand().getItemMeta().getDisplayName()+"§r Utilisable";
					PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte)2);
					((CraftPlayer) owner).getHandle().playerConnection.sendPacket(packet);
			}
		}
		if (owner.getItemInHand().isSimilar(Items.getHantenguUrogi())) {
			sendActionBarCooldown(owner, Urogi);
		}
		if (owner.getItemInHand().isSimilar(Items.getHantenguKarakuVent())) {
			sendActionBarCooldown(owner, powerKaraku);
		}
		if (owner.getItemInHand().isSimilar(Items.getHantenguUrogiFly())) {
			if (powerUrogi) {
				if (flytime > 0) {
					String message = "Pouvoir utilisable";
					PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte)2);
					((CraftPlayer) owner).getHandle().playerConnection.sendPacket(packet);
				}
			} else {
				if (flytime > 0) {
					String message = "Temp restant: §6"+flytime%60+"s";
					PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte)2);
					((CraftPlayer) owner).getHandle().playerConnection.sendPacket(packet);
				} else {
					String message = "Pouvoir déjà utilisé";
					PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte)2);
					((CraftPlayer) owner).getHandle().playerConnection.sendPacket(packet);
				}
			}
		}
		if (powerKaraku >= 1) {powerKaraku--;}
			if (Zohakuten == 60*15-1 || Sekido == 60*10-1 || Urogi == 60*10-1 || Karaku == 60*10-1) {
				form = Form.Hantengu;
			}
			
			if (Zohakuten >= 1) {
				Zohakuten--;
			} if (Sekido >= 1) {
				Sekido--;
			} if (Urogi >= 1) {
				Urogi--;
			} if (Karaku >= 1) {
				Karaku--;
			}
			if (zohatime >= 1)zohatime--;
			if (sekitime>= 1)sekitime--;
			if (urotime>=1)urotime--;
			if (karakutime>=1)karakutime--;
			if (form == Form.Zohakuten) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*5, 0, false, false));
				owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*5, 0, false, false));
			}
			if (form == Form.Sekido) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*5, 0, false, false));
			}
			if (form == Form.Karaku) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*5, 0, false, false));
			}
			if (form == Form.Urogi) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*5, 0, false, false));
				owner.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20*5, 3, false, false));
				if (flytime >= 1 && !powerUrogi) {
					owner.setAllowFlight(true);
					owner.setFlying(true);
					flytime--;
					owner.sendMessage("Il vous reste "+ChatColor.GOLD+flytime+"§rs pour voler");
				} else if (flytime == 0 && !powerUrogi){
					owner.setAllowFlight(false);
					owner.setFlying(false);
				}
				this.setNoFall(true);
			} else {
				this.setNoFall(false);
			}
			if (form == Form.Urogi) {
				setNoFall(true);
			} else {
				if (form != Form.Hantengu) {
					if (isHasNoFall()) setNoFall(false);
				}
			}
			if (form == Form.Hantengu) {
				if (Sekido > 60*10) {
					form = Form.Sekido;
				}
				if (Urogi > 60*10) {
					form = Form.Urogi;
				}
				if (Karaku > 60*10) {
					form = Form.Karaku;
				}
				if (Zohakuten > 60*15) {
					form = Form.Zohakuten;
				}
				if (!gameState.nightTime) {
					owner.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*4, 0, false, false));
				}
				if (form == Form.Hantengu) {
					PlayerInventory inv = owner.getInventory();
					if (inv.getBoots() == null && inv.getLeggings() == null && inv.getChestplate() == null && inv.getHelmet() == null) {
						owner.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 0, false, false));
						owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 1, false, false));
						this.setResi(40);
						this.setNoFall(true);
					} else {
						this.setResi(0);
						owner.removePotionEffect(PotionEffectType.INVISIBILITY);
						owner.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
						this.setNoFall(false);
					}
				}
				
			}
			if (gameState.nightTime) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*4, 0, false, false)); 
			}
			if (Urogi <= 60*10)urotime=0;
			if (Zohakuten<=60*15)zohatime=0;
			if (Sekido<=60*10)sekitime=0;
			if (Karaku<=60*10)karakutime=0;
			super.Update(gameState);
		}
	@Override
		public boolean ItemUse(ItemStack item, GameState gameState) {
		String name = item.getItemMeta().getDisplayName();
		if (item.isSimilar(Items.getHantenguUrogiFly())) {
			if (form == Form.Urogi) {
				if (powerUrogi) {
					if (flytime == 6) {
                        powerUrogi = false;
						new WingsEffect(20*flytime+1, EnumParticle.CRIT).start(owner);
						owner.sendMessage("Vous avez maintenant: "+flytime+"s pour voler");
					}
				}else {
					owner.sendMessage("Vous avez déjà utiliser: "+ChatColor.GOLD+name);
				}
			} else {
				owner.sendMessage("Vous devez êtres en clone Urogi pour utiliser"+ChatColor.GOLD+" (actuellement vous êtes le clone nommé "+form.name()+" )");
			}
		}
		if (item.isSimilar(Items.getHantenguUrogi())) {
			if (Urogi <= 0) {
				if (form == Form.Hantengu) {
					owner.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
					owner.removePotionEffect(PotionEffectType.INVISIBILITY);
					form = Form.Urogi;
					Urogi = 60*15;
				}
				if (form == Form.Karaku) {
					Karaku = 60*10;
				}
				if (form == Form.Zohakuten) {
					Zohakuten = 60*15;
				}
				if (form == Form.Sekido) {
					Sekido = 60*10;
				}
				form = Form.Urogi;
				Urogi = 60*15;
				urotime = 60*5;
				powerUrogi = true;
				flytime = 6;
				owner.sendMessage("Vous venez de vous transformez en: "+ChatColor.GOLD+name);
			}else {
				int s = Urogi%60;
				int m = Urogi/60;
				owner.sendMessage(ChatColor.RED+"Vous ne pourrez utiliser a nouveau votre abilité que dans "+ChatColor.GOLD+m+" minutes"+ChatColor.RED+" et "+ChatColor.GOLD+s+" secondes");
				return true;
			}
		}
		if (item.isSimilar(Items.getHantenguSekido())) {
			if (Sekido <= 0) {
				if (form == Form.Hantengu) {
					owner.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
					owner.removePotionEffect(PotionEffectType.INVISIBILITY);
					form = Form.Sekido;
					Sekido = 60*15;
				}
				if (form == Form.Karaku) {
					Karaku = 60*10;
				}
				if (form == Form.Zohakuten) {
					Zohakuten = 60*15;
				}
				if (form == Form.Urogi) {
					Urogi = 60*10;
				}
				form = Form.Sekido;
				Sekido = 60*15;
				sekitime = 60*5;
				owner.sendMessage("Vous venez de vous transformez en: "+ChatColor.GOLD+name);
			}else {
				int s = Sekido%60;
				int m = Sekido/60;
				owner.sendMessage(ChatColor.RED+"Vous ne pourrez utiliser a nouveau votre abilité que dans "+ChatColor.GOLD+m+" minutes"+ChatColor.RED+" et "+ChatColor.GOLD+s+" secondes");
				return true;
			}
		}
			if(item.isSimilar(Items.getHantenguZohakuten())) {
				if (Zohakuten <= 0) {
					if (form == Form.Hantengu) {
						owner.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
						owner.removePotionEffect(PotionEffectType.INVISIBILITY);
						form = Form.Zohakuten;
						Zohakuten = 60*20;
					}
					if (form == Form.Karaku) {
						Karaku = 60*10;
					}
					if (form == Form.Sekido) {
						Sekido = 60*10;
					}
					if (form == Form.Urogi) {
						Urogi = 60*10;
					}
					this.setResi(20);
					form = Form.Zohakuten;
					Zohakuten = 60*20;
					zohatime = 60*5;
					owner.sendMessage("Vous venez de vous transformez en: "+ChatColor.GOLD+name);
				} else {
					int s = Zohakuten%60;
					int m = Zohakuten/60;
					owner.sendMessage(ChatColor.RED+"Vous ne pourrez utiliser a nouveau votre abilité que dans "+ChatColor.GOLD+m+" minutes"+ChatColor.RED+" et "+ChatColor.GOLD+s+" secondes");
					return true;
				}
			}
			if (item.isSimilar(Items.getHantenguKaraku())) {
					if (Karaku <= 0) {
						if (form == Form.Hantengu) {
							owner.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
							owner.removePotionEffect(PotionEffectType.INVISIBILITY);
							form = Form.Karaku;
							Karaku = 60*15;
						}
						if (form == Form.Zohakuten) {
							Zohakuten = 60*15;
						}
						if (form == Form.Sekido) {
							Sekido = 60*10;
						}
						if (form == Form.Urogi) {
							Urogi = 60*10;
						}
						form = Form.Karaku;
						Karaku = 60*15;
						karakutime=60*5;
						powerKaraku = 1;
						owner.sendMessage("Vous venez de vous transformez en: "+ChatColor.GOLD+name);
					} else {
						int ks = Karaku%60;
						int km = Karaku/60;
						owner.sendMessage(ChatColor.RED+"Vous ne pourrez utiliser a nouveau votre abilité que dans "+ChatColor.GOLD+km+" minutes"+ChatColor.RED+" et "+ChatColor.GOLD+ks+" secondes");
						return true;
					}
			}
			if (item.isSimilar(Items.getHantenguKarakuVent())) {
				if (powerKaraku <= 0) {
					if (form == Form.Karaku) {
						for (UUID u : gameState.getInGamePlayers()) {
							Player p = Bukkit.getPlayer(u);
							if (p == null)continue;
							if (p != owner) {
										if (p.getLocation().distance(owner.getLocation()) <= 30) {
										Location ploc1 = p.getLocation();
										Location spawn1 = new Location(Bukkit.getWorld("World"), ploc1.getX(), ploc1.getY() + 30, ploc1.getZ());
										Location loc = p.getLocation();
										System.out.println(p.getEyeLocation());
										loc.setX(loc.getX()+Math.cos(Math.toRadians(-p.getEyeLocation().getYaw()+90)));
										loc.setZ(loc.getZ()+Math.sin(Math.toRadians(p.getEyeLocation().getYaw()-90)));
										loc.setPitch(0);
										System.out.println(loc);
										System.out.println(spawn1);
										p.teleport(spawn1);
										p.sendMessage("Vous venez d'être téléporter par Karaku");
										powerKaraku = 60;
										}								
							}
						}						
					} else {
						owner.sendMessage("Vous devez êtres transformé en Karaku pour utilisé ce"+ChatColor.GOLD+" Pouvoir Sanginaire");
					}
				} else {
					int s = powerKaraku%60;
					int m = powerKaraku/60;
					owner.sendMessage(ChatColor.RED+"Vous ne pourrez utiliser a nouveau votre abilité que dans "+ChatColor.GOLD+m+" minutes"+ChatColor.RED+" et "+ChatColor.GOLD+s+" secondes");
					return true;
				}
			}
			return super.ItemUse(item, gameState);
		}
	@Override
		public void ItemUseAgainst(ItemStack item, Player victim, GameState gameState) {
			boolean diamond = item.isSimilar(Items.getdiamondsword());
			if (diamond) {
				if (form == Form.Zohakuten) {
					if (victim != owner) {
						 if(!victim.hasPotionEffect(PotionEffectType.CONFUSION)) {
							 victim.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*15, 1, false, false));
							 owner.sendMessage("Vous venez d'infliger Nausée 2 pendant 15s à: "+ChatColor.GOLD+victim.getName());
						 }
					}
				}
			}
			super.ItemUseAgainst(item, victim, gameState);
		}
	@Override
		public void PlayerKilled(Player killer, Player victim, GameState gameState) {
			if (killer == owner && victim != owner && form == Form.Sekido) {
				if (gameState.getInGamePlayers().contains(victim.getUniqueId())) {
					if (!gameState.hasRoleNull(victim.getUniqueId())) {
						final RoleBase role = gameState.getGamePlayer().get(victim.getUniqueId()).getRole();
							this.killforce+=5;
							owner.sendMessage(ChatColor.GRAY+"Vous venez de tuée: "+ victim.getName()+" il possédait le rôle de: "+ChatColor.GOLD+ role.getRoles() +ChatColor.GRAY+" vous obtenez donc 5% de Force");
							addBonusforce(killforce);
					}
				}
			}
			super.PlayerKilled(killer, victim, gameState);
		}
}