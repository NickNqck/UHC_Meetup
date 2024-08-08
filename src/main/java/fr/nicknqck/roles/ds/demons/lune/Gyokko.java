package fr.nicknqck.roles.ds.demons.lune;

import java.util.Random;
import java.util.UUID;

import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.slayers.pillier.Muichiro;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.items.BulleGyokko;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.StringUtils;

public class Gyokko extends DemonsRoles {

	public Gyokko(UUID player) {
		super(player);
		this.setResi(20);
	}
	@Override
	public Roles getRoles() {
		return Roles.Gyokko;
	}

	@Override
	public DemonType getRank() {
		return DemonType.LuneSuperieur;
	}

	@Override
	public String[] Desc() {
		KnowRole(owner, Roles.Muzan, 1);
		return AllDesc.Gyokko;
	}
	@Override
	public TeamList getOriginTeam() {
		return TeamList.Demon;
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				Items.getPouvoirSanginaire(),
				Items.getFormeDémoniaque(),
				BulleGyokko.getBulleGyokko()
		};
	}

	@Override
	public String getName() {
		return "Gyokko";
	}

	private int pouvoircooldown = 0;
	private int formecooldown = 0;
	private boolean formedemoniaque = false;
	private boolean killmuichiro = false;
	public int bullecooldown = 0;
	@Override
	public void resetCooldown() {
		bullecooldown = 0;
		pouvoircooldown = 0;
	}
	Random random = new Random();
	@Override
	public void Update(GameState gameState) {
		if (owner.getItemInHand().isSimilar(Items.getPouvoirSanginaire())) {
			sendActionBarCooldown(owner, pouvoircooldown);
		}
		if (owner.getItemInHand().isSimilar(Items.getFormeDémoniaque())) {
			sendCustomActionBar(owner, "Temp avant perte de "+AllDesc.coeur+": "+StringUtils.secondsTowardsBeautiful(formecooldown));
		}
		if (owner.getItemInHand().isSimilar(BulleGyokko.getBulleGyokko())) {
			sendActionBarCooldown(owner, bullecooldown);
		}
		if (bullecooldown > 0)bullecooldown-=1;
		if (gameState.nightTime) {owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*3, 0, false, false), true);}
		if (pouvoircooldown >= 1) {pouvoircooldown--;}
		if (formedemoniaque) {
			if (formecooldown == 0) {
				formecooldown = 60;
				setMaxHealth(getMaxHealth() - 2.0);
				owner.sendMessage("Vous venez de perdre 1"+AllDesc.coeur+" permanent suite à votre Forme Démoniaque, ce qui vous fait donc tomber à: "+ChatColor.GOLD+ (getMaxHealth() / 2) +" Coeur");
			}
			if (formecooldown >= 1) {
				formecooldown--;
			}
		}
		if (killmuichiro) {
			if (!gameState.nightTime) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*3, 0, false, false), true);
			}
		}
	if (owner.getMaxHealth() == 2.0 && formedemoniaque) {
		owner.sendMessage("Désactivation de votre Forme Démoniaque suite à votre faible santé");
		formedemoniaque = false;
	}
		
		super.Update(gameState);
	}
	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}

	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(Items.getFormeDémoniaque())) {
			if (!formedemoniaque) {
				if (formecooldown == 0) {
					formecooldown = 60;
				}
				formedemoniaque = true;
				owner.sendMessage("Vous venez d'activer votre Forme Démoniaque");
				owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
				owner.getInventory().setChestplate(Items.getGyokkoPlastron());
			} else {
				formedemoniaque = false;
				owner.sendMessage(ChatColor.WHITE+"Vous venez de désactivé votre Forme Démoniaque");
				owner.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
				owner.getInventory().setChestplate(Items.getdiamondchestplate());
			}				
		}
		if (item.isSimilar(Items.getPouvoirSanginaire())) {
			if (pouvoircooldown <= 0) {
				setInvincible(true);
				Location ploc = owner.getLocation();
				Location pot = new Location(Main.getInstance().gameWorld, ploc.getX() + random.nextInt(15), ploc.getY(), ploc.getZ() + random.nextInt(15));
				System.out.println(pot);
				owner.teleport(pot);
				setInvincible(false);
				pouvoircooldown = 120;
			}  else {
				sendCooldown(owner, pouvoircooldown);
			}
		}
		return super.ItemUse(item, gameState);
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (victim == owner) {
			owner.getInventory().remove(Items.getGyokkoPlastron());
			owner.getInventory().remove(Items.getGyokkoBoots());
		}
		if (killer == owner) {
			if (victim != owner){
				if (gameState.getInGamePlayers().contains(victim)) {
					if (gameState.getPlayerRoles().containsKey(victim)) {
						RoleBase role = gameState.getPlayerRoles().get(victim);
						if (role instanceof Muichiro) {
							killmuichiro = true;
							giveItem(owner, false, Items.getGyokkoBoots());
							owner.sendMessage(ChatColor.GRAY+"Vous venez de tuez "+ChatColor.GOLD+ role.getRoles() +ChatColor.GRAY+"vous obtenez donc "+ChatColor.RED+"force 1 le jour"+ChatColor.GRAY+", ainsi que des bottes en diamant enchantée avec: "+ChatColor.GOLD+"Depht Strider 2");
						}
					}
				}
			}
		}
		
		super.PlayerKilled(killer, victim, gameState);
	}
}