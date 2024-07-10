package fr.nicknqck.roles.ns.solo.zabuza_haku;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.nicknqck.roles.ns.builders.NSRoles;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ns.Intelligence;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.utils.CC;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.raytrace.RayTrace;

public class Haku extends NSRoles {
	private int maxCDHaku = 60*8;
	public Haku(Player player) {
		super(player);
		setChakraType(Chakras.SUITON);
		owner.sendMessage(Desc());
		setResi(20);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
			if (!gameState.attributedRole.contains(Roles.Zabuza)) {
				onZabuzaDeath(false);
				owner.sendMessage("§bZabuza§7 n'est pas dans la partie, vous récupérez donc le bonus dû à sa mort");
			}
		}, 20*10);
	}
	@Override
	public GameState.Roles getRoles() {
		return Roles.Haku;
	}
	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}
	@Override
    public String[] Desc() {
        KnowRole(owner, Roles.Zabuza, 20);
        return new String[] {
                AllDesc.bar,
                AllDesc.role+"§bHaku",
                AllDesc.objectifteam+"§bHaku et Zabuza",
                "",
                AllDesc.effet,
                "",
                AllDesc.point+"§bSpeed I §fet §9Résistance I §fpermanents",
                "",
                AllDesc.items,
                "",
                AllDesc.point+"§bHyôton §8: §fPermet de créer un §bdôme de glace §fpendant §c2 minutes §fdans lequel vous gagnez l'effet §cForce I§f ainsi que§a NoFall§f. §7(1x/8min)",
                "",
                AllDesc.commande,
                "",
                AllDesc.point+"§6/ns hyoton§f: Permet d'§cannuler §fvotre §bdôme de glace§f.",
                "",
                AllDesc.particularite,
                "",
                "Vous possédez l'identité de§b Zabuza§f, de plus, vous avez un§c chat privé§f avec ce dernier, pour communiquer il faudra utiliser le préfixe§c !§f devant vos message.",
                "A la§c mort§f de§b Zabuza§f le cooldown de votre§b Hyôton§f devient§c 3 minutes",
                "",
                AllDesc.chakra+getChakras().getShowedName(),
                AllDesc.bar
        };
    }

	@Override
	public TeamList getOriginTeam() {
		return TeamList.Zabuza_et_Haku;
	}

	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				BulleItem()
		};
	}
	private ItemStack BulleItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§bHyôton").setLore("§7Vous permet de crée une sphère de glace").toItemStack();
	}
	private int bulleHakuCD = 0;
	@Override
	public void Update(GameState gameState) {
		givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, false);
		givePotionEffet(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false);
		if (bulleHakuCD >= 0) {
			bulleHakuCD--;
			if (bulleHakuCD == 0) {
				owner.sendMessage(BulleItem().getItemMeta().getDisplayName()+"§7 est de nouveau utilisable");
			}
		}
		if (owner.getItemInHand().isSimilar(BulleItem())) {
			sendActionBarCooldown(owner, bulleHakuCD);
		}
	}
	private void onZabuzaDeath(boolean msg) {
		if (msg) {
			owner.sendMessage("§bZabuza§7 est mort, le cooldown de votre§b Bulle§7 diminue");
		}
		maxCDHaku = 60*3;
	}
	@Override
	public void resetCooldown() {
		bulleHakuCD = 0;
	}
	@SuppressWarnings("deprecation")
	@Override
	public void onAllPlayerChat(org.bukkit.event.player.PlayerChatEvent e, Player p) {
		if (p.getUniqueId() == owner.getUniqueId()) {
			if (e.getMessage().startsWith("!")) {
				String msg = e.getMessage();
				Player Zabuza = getPlayerFromRole(Roles.Zabuza);
				owner.sendMessage(CC.translate("&bHaku: "+msg.substring(1)));
				if (Zabuza != null) {
					Zabuza.sendMessage(CC.translate("&bHaku: "+msg.substring(1)));
				}
			}
		}
	}
	@Override
	public void onNsCommand(String[] args) {
		if (args[0].equalsIgnoreCase("hyoton")) {
			if (bulleHakuCD > 60*8+1) {
				bulleHakuCD = 60*8+1;
				Player player = Bukkit.getPlayer(getPlayer());
				if (player != null) {
					player.sendMessage("§7Vous forcez la fermeture de votre§b bulle");
				}
			}
		}
	}
	private List<Block> getBlocksInFrontOfPlayer(Player player, Location targetPosition) {
	    Location playerLocation = player.getLocation();
	    World world = playerLocation.getWorld();
	    List<Block> blocksInFront = new ArrayList<>();

	    Vector direction = targetPosition.toVector().subtract(playerLocation.toVector()).normalize();
	    double distance = playerLocation.distance(targetPosition);

	    for (double i = 0; i <= distance; i += 1) {
	        Location currentLocation = playerLocation.clone().add(direction.clone().multiply(i));
	        Block currentBlock = world.getBlockAt(currentLocation);
	        blocksInFront.add(currentBlock);
	    }

	    return blocksInFront;
	}
	private Location getHakuTargetLocation(Player player) {
        Vector origin = player.getEyeLocation().toVector();
        Vector direction = player.getEyeLocation().getDirection();

        RayTrace rayTrace = new RayTrace(origin, direction);
        Vector targetPosition = rayTrace.positionOfIntersection(50, 0.1);
        if (targetPosition != null) {
            return targetPosition.toLocation(player.getWorld());
        }
        return origin.clone().add(direction.clone().multiply((double) 50)).toLocation(player.getWorld());
    }
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(BulleItem())) {
			if (bulleHakuCD >= 1) {
				if (bulleHakuCD > 60*8) {
					Location precedentB = null;
					HashMap<Integer, Location> Integers = new HashMap<>();
					int i = 0;
					for (Block b : getBlocksInFrontOfPlayer(owner, getHakuTargetLocation(owner))) {
						i++;
						precedentB = b.getLocation();
						Integers.put(i, precedentB);
						if (b.getType() == Material.PACKED_ICE) {
							break;
						}
					}
					if (precedentB != null) {
						int e = Integers.size();
						int iv = e-2;
						Location test = Integers.get(iv).clone();
						if (test != null) {
							test.setPitch(owner.getLocation().getPitch());
							owner.teleport(test);
						}
					}
					return true;
				}
				sendCooldown(owner, bulleHakuCD);
				return true;
			}
			final HashMap<Block, Material> map = new HashMap<>();
            for(Location location : sphere(owner.getLocation())) {
                if(location.getBlock().getType() == Material.AIR || location.getBlock().getType() == Material.WATER || location.getBlock().getType() == Material.STATIONARY_WATER) {
                    map.put(location.getBlock(), location.getBlock().getType());
                    location.getBlock().setType(Material.PACKED_ICE);
                }
            }
            
            this.fMap.putAll(map);
            bulleHakuCD = maxCDHaku+120;
            setForce(20);
            setNoFall(true);
            new BukkitRunnable() {
				@Override
				public void run() {
					if (gameState.getServerState() != null && gameState.getServerState() != ServerStates.InGame) {
						cancel();
						return;
					}
					givePotionEffet(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, false);
					
					if (bulleHakuCD <= (maxCDHaku-120)) {
						owner.sendMessage("§7Votre bulle disparait");
						setForce(0);
						owner.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
						resetBulleHaku();
						cancel();
					}
					
				}
			}.runTaskTimer(Main.getInstance(), 0, 20);
		}
		return super.ItemUse(item, gameState);
	}
	private final HashMap<Block, Material> fMap = new HashMap<>();
	private void resetBulleHaku() {
		setNoFall(false);
		fMap.keySet().forEach(location -> location.setType(fMap.get(location)));
		fMap.clear();
	}
	@Override
	public boolean onPreDie(Entity damager, GameState gameState2) {
		resetBulleHaku();
		return super.onPreDie(damager, gameState2);
	}

	@Override
	public Intelligence getIntelligence() {
		return Intelligence.MOYENNE;
	}

	@Override
	public void onEndGame() {
		resetBulleHaku();
	}
	private Set<Location> sphere(Location location){
	       Set<Location> blocks = new HashSet<>();
	        World world = location.getWorld();
	        int X = location.getBlockX();
	        int Y = location.getBlockY();
	        int Z = location.getBlockZ();
	        int radiusSquared = 20 * 20;
        for (int x = X - 20; x <= X + 20; x++) {
            for (int y = Y - 20; y <= Y + 20; y++) {
                for (int z = Z - 20; z <= Z + 20; z++) {
                    if ((X - x) * (X - x) + (Y - y) * (Y - y) + (Z - z) * (Z - z) <= radiusSquared) {
                        Location block = new Location(world, x, y, z);
                        blocks.add(block);
                    }
                }
            }
        }
        return makeHollow(blocks);
    }
	private Set<Location> makeHollow(Set<Location> blocks){
	        Set<Location> edge = new HashSet<>();
        for (Location l : blocks) {
            World w = l.getWorld();
            int X = l.getBlockX();
            int Y = l.getBlockY();
            int Z = l.getBlockZ();
            Location front = new Location(w, X + 1, Y, Z);
            Location back = new Location(w, X - 1, Y, Z);
            Location left = new Location(w, X, Y, Z + 1);
            Location right = new Location(w, X, Y, Z - 1);
            Location top = new Location(w, X, Y + 1, Z);
            Location bottom = new Location(w, X, Y - 1, Z);
            if (!(blocks.contains(front) && blocks.contains(back) && blocks.contains(left) && blocks.contains(right) && blocks.contains(top) && blocks.contains(bottom))) {
                edge.add(l);
            }
        }
        return edge;
	}
	@Override
	public String getName() {
		return "§bHaku";
	}

	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		super.PlayerKilled(killer, victim, gameState);
		if (!gameState.hasRoleNull(victim)){
			if (gameState.getPlayerRoles().get(victim) instanceof Zabuza && maxCDHaku != 60*3) {
				onZabuzaDeath(true);
			}
		}
	}
}