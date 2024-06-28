package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import fr.nicknqck.roles.ds.demons.DemonMain;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Makomo extends SlayerRoles {

	public Makomo(Player player) {
		super(player);
		for (String desc : AllDesc.Makomo) owner.sendMessage(desc);
		setCanuseblade(true);
	}
	@Override
	public Roles getRoles() {
		return Roles.Makomo;
	}
	@Override
	public String[] Desc() {
		return AllDesc.Makomo;
	}
	private int souflecooldown = 0;
	private boolean fuse = false;
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(Items.getSoufleDeLeau());
		owner.getInventory().addItem(Items.getLamedenichirin());
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				Items.getSoufleDeLeau()
		};
	}
	@Override
	public void resetCooldown() {
		souflecooldown = 0;
	}
	@Override
	public void Update(GameState gameState) {
		if (owner.getItemInHand().isSimilar(Items.getSoufleDeLeau())) {
			if (souflecooldown > 0) {
					String message = "Cooldown: §6"+souflecooldown/60+"m"+souflecooldown%60+"s";
					PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte)2);
					((CraftPlayer) owner).getHandle().playerConnection.sendPacket(packet);
			} else {
					String message = owner.getItemInHand().getItemMeta().getDisplayName()+"§r Utilisable";
					PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte)2);
					((CraftPlayer) owner).getHandle().playerConnection.sendPacket(packet);
			}
		}
		if (souflecooldown >= 1) {
			souflecooldown--;
		}
		if (souflecooldown > 60*5) {
			owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*4, 0, false, false));
		} else {
			owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
		}
		super.Update(gameState);
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(Items.getSoufleDeLeau())) {
			if (souflecooldown <= 0) {
				souflecooldown = 60*7;
				System.out.println("Force Makomo [1] "+ this.getForce());
				if (!fuse) {
					this.addforce(20);
				}
				System.out.println("Force Makomo [2] "+ this.getForce());
				fuse = true;
				owner.sendMessage("Vous venez d'activer votre Soufle de L'eau ce qui vous donne"+ChatColor.GOLD+" Force 1 pendant 2 minutes");
				org.bukkit.Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
					if (fuse) {
						addforce(-20);
					}
		        }, 20*120);
			} else {
				sendCooldown(owner, souflecooldown);
				return true;
			}
		}
		return super.ItemUse(item, gameState);
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (killer == owner) {
			if (victim != owner) {
				if (gameState.getInGamePlayers().contains(victim)) {
					if (gameState.getPlayerRoles().containsKey(victim)) {
						RoleBase role = gameState.getPlayerRoles().get(victim);
						if (role instanceof DemonMain) {
							System.out.println("Speed Makomo [1] "+ owner.getWalkSpeed());
							owner.sendMessage(ChatColor.GRAY+"Vous venez de tuée: "+ victim.getName()+" il possédait le rôle de: "+ChatColor.GOLD+ role.getRoles() +ChatColor.GRAY+" vous obtenez donc 10% de Speed");
							addSpeedAtInt(owner, 10);
							System.out.println("Speed Makomo [2] "+owner.getWalkSpeed());
						}
					}
				}
			}
		}
		super.PlayerKilled(killer, victim, gameState);
	}

	@Override
	public String getName() {
		return "§aMakomo";
	}
}