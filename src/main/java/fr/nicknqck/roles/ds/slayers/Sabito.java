package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import fr.nicknqck.roles.ds.demons.DemonMain;
import fr.nicknqck.roles.ds.slayers.pillier.Tomioka;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class Sabito extends SlayerRoles {
	private final TextComponent automaticDesc;
	public Sabito(UUID player) {
		super(player);
		this.setCanuseblade(true);
		this.setResi(20);
		AutomaticDesc automaticDesc = new AutomaticDesc(this).addEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 0), EffectWhen.NIGHT)
				.addItem(this.SoufleComponent(), 60*5)
				.addParticularites(
						new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Si vous tuez le joueur possédant le rôle de§c Demon Main§7 le cooldown de votre \"§6Soufle de l'eau§7\" sera réduit de§c 30 secondes§7.")}),
						new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7A la mort de§a Tomioka§7 votre cooldown \"§6Soufle de l'eau§7\" sera réduit de§c 30 secondes§7, également, ce dernier vous donnerea§b Speed 2§7.")})
				);
		this.automaticDesc = automaticDesc.getText();
		owner.spigot().sendMessage(this.automaticDesc);
	}
	@Override
	public Roles getRoles() {
		return Roles.Sabito;
	}

	@Override
	public TextComponent getComponent() {
		return this.automaticDesc;
	}

	private int souflecooldown = 0;
	private boolean killdemon = false;
	private boolean dietomioka = false;
	@Override
	public void resetCooldown() {
		souflecooldown = 0;
	}
	@Override
	public String[] Desc() {
		return AllDesc.Sabito;
	}
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(Items.getLamedenichirin());
		owner.getInventory().addItem(Items.getSoufleDeLeau());
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				Items.getSoufleDeLeau()
		};
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
		if (gameState.nightTime) {
			owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*3, 0, false, false));
		}
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(Items.getSoufleDeLeau())) {
			if (souflecooldown <= 0) {
				if (dietomioka) {
					owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*2, 1, false, false));
					System.out.println("give speed 2");
				} else {
					owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*2, 0, false, false));
					System.out.println("give speed 1");
				}
				if (killdemon) {
					owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 0, 20*180, false, false));
				}
				if (dietomioka && killdemon) {
					souflecooldown = 60*4;
				} else if (dietomioka) {
					souflecooldown = 60*4+30;
				} else if (killdemon) {
					souflecooldown = 60*4+30;
				} else {
					souflecooldown = 60*5;
				}
			}  else {
				int s = souflecooldown%60;
				int m = souflecooldown/60;
				owner.sendMessage(ChatColor.RED+"Vous ne pourrez utiliser a nouveau votre abilité que dans "+ChatColor.GOLD+m+" minutes"+ChatColor.RED+" et "+ChatColor.GOLD+s+" secondes");
				return true;
			}
		}
		return super.ItemUse(item, gameState);
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (victim != owner) {
			if (gameState.getInGamePlayers().contains(victim.getUniqueId())) {
				if (gameState.getPlayerRoles().containsKey(victim)) {
					RoleBase r = gameState.getPlayerRoles().get(victim);
					if (r instanceof Tomioka && !dietomioka) {
						dietomioka = true;
						owner.sendMessage(ChatColor.GOLD+""+ r.getRoles()+ChatColor.GRAY+" est mort vous gagnez donc en utilisant votre Soufle de L'eau Speed 2 pendant 2 minutes au lieu de Speed 1 pendant 2 minutes, également le cooldown est réduit de 30 secondes");
						}
					if (killer.getUniqueId() == getPlayer()) {
						if (r instanceof DemonMain && !killdemon) {
							killdemon = true;
							owner.sendMessage(ChatColor.GRAY+"Vous venez de tuez le joueur possédant le rôle de: "+ChatColor.GOLD+"Demon Main "+ChatColor.GRAY+"vous obtenez donc force 1 en utilisant votre Soufle de L'eau également son cooldown est réduit de 30 secondes");
						}
					}
			}
		}
	}
		super.PlayerKilled(killer, victim, gameState);
	}

	@Override
	public String getName() {
		return "Sabito";
	}
	private TextComponent SoufleComponent() {
		TextComponent text = new TextComponent("§7\"§cSoufle de l'eau§7\"");
		text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous donne l'effet§b Speed "+(!dietomioka ? "I" : "II")+"§7.")}));
		return text;
	}
 }