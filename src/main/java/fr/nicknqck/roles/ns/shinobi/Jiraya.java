package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.particles.MathUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Jiraya extends ShinobiRoles {

	public Jiraya(Player player) {
		super(player);
		setChakraType(Chakras.KATON);
		ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		Bukkit.dispatchCommand(console, "nakime Gamabunta8vzqzZvv189Zbxc:!");
		setCanBeHokage(true);
	}
	@Override
	public GameState.Roles getRoles() {
		return Roles.Jiraya;
	}
	@Override
	public String[] Desc() {
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§aJiraya",
				AllDesc.objectifteam+"§aShinobi",
				"",
				AllDesc.items,
				"",
				AllDesc.point+FukasakuEtShima().getItemMeta().getDisplayName()+"§f: Vous permet d'obtenir les effets§c Force I§f et§9 Résistance I§f ainsi que§c 2"+AllDesc.coeur+" pendant§c 3 minutes§f§7 (1x/5min)",
				"",
				AllDesc.point+RasenganItem().getItemMeta().getDisplayName()+"§f: Crée une §nexplosion§f infligeant§c 3"+AllDesc.coeur+"§f de dégat au joueur frapper§.§7 (1x/5m)",
				"",
				AllDesc.point+CrapaudItem().getItemMeta().getDisplayName()+"§f: Téléporte tout les joueurs autours de vous dans les§c 25 blocs§f",
				"",
				AllDesc.particularite,
				"",
				AllDesc.chakra+getChakras().getShowedName(),
				AllDesc.bar
		};
	}
	private ItemStack FukasakuEtShima() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§aFukasaku et Shima").setLore("§7Permet d'accumuler de l'énergie§a Senjutsu§7 en bougeant").toItemStack();
	}
	private ItemStack RasenganItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§aRasengan Géant").setLore("§7Permet d'infliger§c 3"+AllDesc.coeur+"§7 au joueur taper").toItemStack();
	}
	private ItemStack CrapaudItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§aGamabunta").setLore("§7Permet de téléporter tout les joueurs proche dans l'estomac d'un crapaud").toItemStack();
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				FukasakuEtShima(),
				RasenganItem(),
				CrapaudItem()
		};
	}
	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}
	private int cdSenjutsu = 0;
	private int cdRasengan = 0;
	private int cdGamabunta = 0;
	@Override
	public void resetCooldown() {
		cdSenjutsu = 0;
		cdRasengan = 0;
		cdGamabunta = 0;
	}
	@Override
	public void onNsCommand(String[] args) {
		if (args[0].equalsIgnoreCase("gamabunta")) {
			if (cdGamabunta >= 60*8) {
				cdGamabunta = 60*8+1;
				owner.sendMessage("§7Vous avez forcé la sortie du ventre de§c Gamabunta§7.");
			}
		}
	}
	private void returnGamabuntaPlayers() {
		if(Bukkit.getWorld("Gamabunta") != null) {
			if (owner.getWorld().equals(Bukkit.getWorld("Gamabunta"))) {
				for (Player p : owner.getWorld().getPlayers()) {
					GameListener.RandomTp(p, Main.getInstance().gameWorld);
				}
			}
		}
	}
	@Override
	public void Update(GameState gameState) {
		if (cdGamabunta >=0) {
			cdGamabunta--;
			if (cdGamabunta == 60*8) {
				returnGamabuntaPlayers();
			}
			if (cdGamabunta == 0) {
				owner.sendMessage("§7Vous pouvez à nouveau téléporter des joueurs dans l'estomac de Gamabunta");
			}
		}
		if (cdRasengan >= 0) {
			cdRasengan--;
			if (cdRasengan == 0) {
				owner.sendMessage("§aRasengan Géant§7 est à nouveau utilisable");
			}
		}
		if (cdSenjutsu >= 0) {
			cdSenjutsu--;
			if (cdSenjutsu == 300) {
				setResi(0);
				owner.sendMessage("§7L'invocation de§a Fukasaku et Shima§7 est terminé");
				setMaxHealth(getMaxHealth()-4.0);
				owner.setMaxHealth(getMaxHealth());
			}
			if (cdSenjutsu == 0) {
				owner.sendMessage("§7Vous pouvez à nouveau invoquer§a Fukasaku et Shima");
			}
		}
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(CrapaudItem())) {
			if (cdGamabunta <= 0) {
				if (!Loc.getNearbyPlayersExcept(owner, 25).isEmpty()) {
					World g = Bukkit.getWorld("Gamabunta");
					if (g != null) {
						fr.nicknqck.utils.GamabuntaLoc gLoc = new fr.nicknqck.utils.GamabuntaLoc();
						owner.sendMessage("§aDans le ventre du crapeaud !");
						for (Player p : Loc.getNearbyPlayers(owner, 25)) {
							p.teleport(new Location(g, 0.0, 5, 0.0));
							owner.sendMessage("§7§l"+p.getDisplayName()+"§7 est rentré dans le ventre de§a Gamabunta");
							p.teleport(gLoc.getRandomPositionStart());
						}
						owner.teleport(gLoc.getJirayaSpawn());
						cdGamabunta = 60*10;
						new BukkitRunnable() {
							
							@Override
							public void run() {
								if (cdGamabunta <= 60*8 || !gameState.getInGamePlayers().contains(owner)) {
									cancel();
								}
								int truc = cdGamabunta-(60*8);
								for (Player p : Loc.getNearbyPlayers(owner, 150)) {
									sendCustomActionBar(p, "§bTemp restant dans§c Gamabunta§b:§c "+cd(truc));
								}
							}
						}.runTaskTimer(Main.getInstance(), 0, 20);
						return true;
					}
				} else {
					owner.sendMessage("§cIl n'y a pas asser de joueur autours de vous !");
					return true;
				}
			} else {
				sendCooldown(owner, cdGamabunta);
				return true;
			}
		}
		if (item.isSimilar(FukasakuEtShima())) {
			if (cdSenjutsu <= 0) {
				owner.sendMessage("§a§lFukasaku et Shima§r§a vous donne de leur énergie naturelle");
				giveHealedHeartatInt(2);
				givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 20*180, 1, false);
				givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, 20*180, 1, false);
				setResi(20);
				cdSenjutsu = 480;
            } else {
				sendCooldown(owner, cdSenjutsu);
            }
            return true;
        }
		if (item.isSimilar(RasenganItem())) {
			if (cdRasengan <= 0) {
				owner.sendMessage("§7Il faut frapper un joueur pour crée une explosion.");
            } else {
				sendCooldown(owner, cdRasengan);
            }
            return true;
        }
		return super.ItemUse(item, gameState);
	}
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
					cdRasengan = 60*5;
				} else {
					sendCooldown(owner, cdRasengan);
				}
			}
		}
	}

	@Override
	public Intelligence getIntelligence() {
		return Intelligence.INTELLIGENT;
	}

	@Override
	public void OnAPlayerDie(Player player, GameState gameState, Entity killer) {
		if (Bukkit.getWorld("Gamabunta")!=null) {
			if (player.getWorld().equals(Bukkit.getWorld("Gamabunta"))) {
				GameListener.RandomTp(player, Main.getInstance().gameWorld);
				if (player.getUniqueId() == owner.getUniqueId()) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						if (p.getWorld().equals(Bukkit.getWorld("Gamabunta"))) {
							GameListener.RandomTp(p, Main.getInstance().gameWorld);
						}
					}
					cdGamabunta = 60*8;
				}
			}
		}
	}

	@Override
	public String getName() {
		return "§aJiraya";
	}
}