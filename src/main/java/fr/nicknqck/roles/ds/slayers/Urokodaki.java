package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.roles.ds.slayers.pillier.TomiokaV2;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;
import java.util.UUID;

public class Urokodaki extends SlayerRoles {
	private TextComponent automaticDesc;
	public Urokodaki(UUID player) {
		super(player);
	}

	@Override
	public Soufle getSoufle() {
		return Soufle.EAU;
	}

	@Override
	public Roles getRoles() {
		return Roles.Urokodaki;
	}
	private int souflecooldown = 0;
	@Override
	public void resetCooldown() {
		souflecooldown = 0;
	}

	@Override
	public void RoleGiven(GameState gameState) {
		this.setCanuseblade(true);
		AutomaticDesc automaticDesc = new AutomaticDesc(this);
		automaticDesc.addCustomWhenEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20, 0, false, false), "dans l'§beau").addItem(
				this.SoufleComponent(), 60*5
		);
		this.automaticDesc = automaticDesc.getText();
	}

	@Override
	public TextComponent getComponent() {
		return this.automaticDesc;
	}

	@Override
	public void GiveItems() {
		owner.getInventory().addItem(Items.getLamedenichirin());
		owner.getInventory().addItem(Items.getSoufleDeLeau());
		owner.getInventory().addItem(Items.getUrokodakiBoots());
		super.GiveItems();
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				Items.getSoufleDeLeau(),
				Items.getUrokodakiBoots()
		};
	}
	@Override
	public String[] Desc() {
		return AllDesc.Urokodaki;
	}
	@Override
	public void Update(GameState gameState) {
		if (owner.getItemInHand().isSimilar(Items.getSoufleDeLeau())) {
			sendActionBarCooldown(owner, souflecooldown);
		}
		if (souflecooldown >= 1) {
			souflecooldown--;
		}
		Material m = owner.getPlayer().getLocation().getBlock().getType();
	    if (m == Material.STATIONARY_WATER || m == Material.WATER) {
	    	owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*4, 0, false, false));
	    }
		super.Update(gameState);
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(Items.getSoufleDeLeau())) {
			if (souflecooldown <= 0) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*2, 0, false, false));
				owner.sendMessage("Activation de votre: "+ChatColor.GOLD+owner.getItemInHand().getItemMeta().getDisplayName());
				souflecooldown = 60*5;
			} else {
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
					RoleBase role = gameState.getPlayerRoles().get(victim);
					if (role instanceof TomiokaV2 || role instanceof Tanjiro || role instanceof Makomo || role instanceof Sabito) {
						Random random = new Random();
						int rint = random.nextInt(2);
						System.out.println("Speed Urokodaki 1"+ owner.getWalkSpeed());
						if (rint == 0) {
							this.addBonusforce(5);
							owner.sendMessage(ChatColor.GRAY+ victim.getName()+" il possédait le rôle de: "+ChatColor.GOLD+ role.getRoles() +ChatColor.GRAY+" vous obtenez donc 5% de Force");
						}
						if (rint == 1) {
						addSpeedAtInt(owner, 5);
						owner.sendMessage(ChatColor.GRAY+ victim.getName()+" il possédait le rôle de: "+ChatColor.GOLD+ role.getRoles() +ChatColor.GRAY+" vous obtenez donc 5% de Speed");
						}
						System.out.println("Speed Urokodaki 2"+ owner.getWalkSpeed());
					}
				}
			}
		}
		super.PlayerKilled(killer, victim, gameState);
	}
	@Override
	public String getName() {
		return "Urokodaki";
	}
	private TextComponent SoufleComponent() {
		TextComponent text = new TextComponent("§7\"§cSoufle de l'eau§7\"");
		text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous donne l'effet§b Speed I§7. (Cooldown: 1x/5m)")}));
		return text;
	}
}