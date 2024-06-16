package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.RandomUtils;

public class Pourfendeur extends RoleBase {
	@Override
	public String getName() {
		return "§aPourfendeur Simple";
	}

	public enum Soufle {
		Soleil,
		Lune,
		Feu,
		Eau,
		Roche,
		Vent,
		Foudre;
	}
	Soufle form = null;
	ChatColor color = null;
	public Pourfendeur(Player player) {
		super(player);
		for (String desc : AllDesc.Pourfendeur) owner.sendMessage(desc);
        this.setCanUseBlade(true);
        int rint = Main.RANDOM.nextInt(5);
        if (rint == 0) {
        form = Soufle.Eau;	
        Eau1 = true;
        color = ChatColor.AQUA;
        owner.getInventory().addItem(Items.getWaterBoots());
		owner.getInventory().addItem(Items.getSoufleDeLeau());
		owner.sendMessage(ChatColor.WHITE+"Vous venez d'obtenir des "+
		ChatColor.GOLD+"Botte en Diamant Depth Strider 1"+ChatColor.WHITE+" et l'item: "+ChatColor.GOLD+"Soufle de l'Eau: "+ChatColor.WHITE+"qui vous donne Speed 1 pendant 3 minutes");
		this.nombredesoufle+=1;
        } else if (rint == 1) {
        	form = Soufle.Feu;
        	Feu = true;
        	color = ChatColor.RED;
        	this.nombredesoufle+=1;
        	owner.sendMessage("Maintenant quand vous "+"taperez un joueur il y aura 1 chance/5 qu'il sois mit en feu");
        } else if (rint == 2) {
        	this.nombredesoufle+=1;
        	form = Soufle.Foudre;
        	color = ChatColor.GOLD;
        	Foudre = true;
        	owner.getInventory().addItem(Items.getSoufleFoudre5iememouvement());
			owner.sendMessage(ChatColor.WHITE+"Vous obtenez l'accès à l'item: "+
			ChatColor.GOLD+"Soufle de la Foudre: Eclair de Chaleur"+ChatColor.WHITE+" qui une fois activé fera que la prochaine personne que vous taperez ce verra infliger"+
			" 2 coeur via un éclair également elle ce verra obtenir l'effet Slowness 1 pendant 15 secondes");
        } else if (rint == 3) {
        	this.nombredesoufle+=1;
        	form = Soufle.Roche;
        	color = ChatColor.GRAY;
        	Roche = true;
			 this.setResi(20);
			owner.sendMessage(ChatColor.WHITE+"Vous avez choisis le soufle de la: "+ChatColor.GOLD+ form);
			owner.sendMessage(ChatColor.WHITE+"Vous obtenez maintenant résistance 1 le jour");
        } else if (rint == 4) {
        	this.nombredesoufle+=1;
        	form = Soufle.Vent;
        	Vent = true;
        	color = ChatColor.GREEN;
        	owner.getInventory().addItem(Items.getSoufleduVent());
        	owner.sendMessage(ChatColor.WHITE+"Vous obtenez l'accès à l'item: "+
					ChatColor.GOLD+"Soufle du Vent: "+ChatColor.WHITE+"qui vous donne speed 2 pendant 2 minutes");
        }
        owner.sendMessage("Vous avez obtenue le Soufle: "+color+ form.name());
	}
	@Override
	public Roles getRoles() {
		return Roles.Slayer;
	}
	@Override
	public String[] Desc() {
		return AllDesc.Pourfendeur;
	}
	private boolean Soleil = false;
	private boolean Lune = false;
	private boolean Feu = false;
	private boolean Eau1 = false;
	private boolean Eau2 = false;
	private boolean Eau3 = false;
	private boolean Roche = false;
	private boolean Vent = false;
	private boolean Foudre = false;
	private int cooldownsoleil = 0;
	private int cooldowneau = 0;
	private int cooldownvent = 0;
	private int cooldownfoudre = 0;
	private boolean usefoudre = false;
	private boolean usesoleil = false;
	private int nombredesoufle = 0;
	private int kill = 0;
	private boolean msgsend2 = false;
	@SuppressWarnings("unused")
	private boolean hasanybasicsoufle = false;
	@Override
	public void resetCooldown() {
		cooldowneau = 0;
		cooldownfoudre = 0;
		cooldownsoleil = 0;
		cooldownvent = 0;
	}
	@Override
 	public void GiveItems() {
		owner.getInventory().addItem(Items.getLamedenichirin());
		super.GiveItems();
	}
	@Override
	public void Update(GameState gameState) {
		if (Feu && Eau1 && Roche && Vent && Foudre && !msgsend2 ) {	
			String msg = ("Vous venez de débloquer l'accès au Soufle du Soleil et au Soufle de la Lune");
			owner.sendMessage(msg);
			System.out.println(msg);
			hasanybasicsoufle = true;
			msgsend2 = true;
		}
		
		if (cooldownsoleil >= 1) {
			cooldownsoleil--;
		}
		if (cooldowneau >= 1) {
			cooldowneau--;
		}
		if (cooldownvent >= 1) {
			cooldownvent--;
		}
		if (cooldownfoudre >= 1) {
			cooldownfoudre--;
		}
		if (cooldownsoleil == 60*3) {
			usesoleil = false;
			owner.sendMessage(ChatColor.WHITE+"Vous ne retirez plus l'absorbtion des joueurs que vous tapez");
		}
		if (Lune && gameState.nightTime) {
			owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*3, 0, false, false));
		}
		if (Roche && !gameState.nightTime) {
			owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*3, 0, false, false));
		}
		if (owner.getItemInHand().isSimilar(Items.getSoufleduSoleil())) {
			sendActionBarCooldown(owner, cooldownsoleil);
		}
		if (owner.getItemInHand().isSimilar(Items.getSoufleduVent())) {
			sendActionBarCooldown(owner, cooldownvent);
		}
		if (owner.getItemInHand().isSimilar(Items.getSoufleDeLeau())) {
			sendActionBarCooldown(owner, cooldowneau);
		}
		if (owner.getItemInHand().isSimilar(Items.getSoufleFoudre5iememouvement())) {
			sendActionBarCooldown(owner, cooldownfoudre);
		}
		super.Update(gameState);
	}
	@Override
	public void ItemUseAgainst(ItemStack item, Player victim, GameState gameState) {
		if (Feu && victim != owner) {
			 int x = 10 * 20;
				if (RandomUtils.getRandomProbability(5)) {
					 if (victim instanceof Player) {
				            victim.setFireTicks(x);
				        }
				}		       
		}
		if (Foudre && victim != owner) {
			if (usefoudre) {
				Heal(victim, -4);
				victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*15, 0, false, false));
				owner.sendMessage(ChatColor.GREEN+"Vous avez touchez : "+ ChatColor.GOLD + victim.getName());
				victim.sendMessage(ChatColor.GREEN+"Vous avez été foudroyez par un simple "+ChatColor.GOLD+"Pourfendeur de Démon...");
				victim.getWorld().strikeLightningEffect(victim.getLocation());
				usefoudre = false;
				cooldownfoudre = 60;
			}
		}
		if (Soleil && usesoleil) {
			if (victim.hasPotionEffect(PotionEffectType.ABSORPTION)) {
				victim.removePotionEffect(PotionEffectType.ABSORPTION);
				victim.sendMessage("Un simple "+ChatColor.GOLD+"Pourfendeur de Démon"+ChatColor.WHITE+" vous à retirer votre absorbtion");
				owner.sendMessage(ChatColor.WHITE+"Vous venez de retirer l'absorbtion de: "+ChatColor.GOLD+ChatColor.BOLD+ victim.getName());
			}
		}
		super.ItemUseAgainst(item, victim, gameState);
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(Items.getSoufleDeLeau()) && Eau1 == true) {
			if (cooldowneau <= 0) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*3, 0, false, false));
				cooldowneau = 60*6;
			} else {
				sendCooldown(owner, cooldowneau);
			}
		}
		if (item.isSimilar(Items.getSoufleduVent()) && Vent == true) {
			if (cooldownvent <= 0) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*2, 1, false, false));
				cooldownvent = 60*4;
			} else {
				sendCooldown(owner, cooldownvent);
			}
		}
		if (item.isSimilar(Items.getSoufleduSoleil()) && Soleil == true) {
			if (cooldownsoleil <= 0) {
				cooldownsoleil = 60*5;
				usesoleil = true;
			} else {
				sendCooldown(owner, cooldownsoleil);
			}
		}
		if (item.isSimilar(Items.getSoufleFoudre5iememouvement()) && Foudre == true) {
			if (cooldownfoudre <= 0) {
				cooldownfoudre = 90;
				usefoudre = true;
			}  else {
				sendCooldown(owner, cooldownfoudre);
			}
		}		
		return super.ItemUse(item, gameState);
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (killer == owner && victim != owner) {
			kill++;
			boolean modif = false;
			do {
				int rint = RandomUtils.getRandomInt(0, 5);
				if (rint == 0) {
					 if (!Eau1 && !Eau2 && !Eau3) {
						   form = Soufle.Eau;	
					        Eau1 = true;
					        color = ChatColor.AQUA;
					        giveItem(owner, false, Items.getWaterBoots(), Items.getSoufleDeLeau());
							owner.sendMessage(ChatColor.WHITE+"Vous venez d'obtenir des "+
							ChatColor.GOLD+"Botte en Diamant Depth Strider 1"+ChatColor.WHITE+" et l'item: "+ChatColor.GOLD+"Soufle de l'Eau: "
									 +ChatColor.WHITE+"qui vous donne Speed 1 pendant 3 minutes");	 
					 } else if (Eau1 && !Eau2 && !Eau3){
						 if (form != Soufle.Eau) {
							form = Soufle.Eau; 
						 }
					        Eau2 = true;
					        color = ChatColor.AQUA;
					        giveItem(owner, false, getItems());
					        owner.getInventory().addItem(Items.getWaterBoots2());
							owner.sendMessage(ChatColor.WHITE+"Vous venez d'obtenir des "+
							ChatColor.GOLD+"Botte en Diamant Depth Strider 2");
					 } else if (Eau1 && Eau2 && !Eau3) {
						 if (form != Soufle.Eau) {
							 form = Soufle.Eau;
						 }
						 Eau3 = true;
						 color = ChatColor.AQUA;
						 owner.getInventory().addItem(Items.getWaterBoots2());
						 owner.sendMessage("Vous venez d'obtenir des "+ChatColor.GOLD+"Botte en diamant Depth Strider 3");
					 }
					 modif = true;
				        } else if (rint == 1) {
				        	if (!Feu) {
				        		if (form != Soufle.Feu) form = Soufle.Feu;
					        	Feu = true;
					        	color = ChatColor.RED;
					        	owner.sendMessage("Maintenant quand vous "+"taperez un joueur il y aura 1 chance/5 qu'il sois mit en feu");
					        	modif = true;
				        	}
				        } else if (rint == 2) {
				        	if (!Foudre) {
				        	 	if (form != Soufle.Foudre) form = Soufle.Foudre;
					        	color = ChatColor.GOLD;
					        	Foudre = true;
					        	owner.getInventory().addItem(Items.getSoufleFoudre5iememouvement());
								owner.sendMessage(ChatColor.WHITE+"Vous obtenez l'accès à l'item: "+
								ChatColor.GOLD+"Soufle de la Foudre: Eclair de Chaleur"+ChatColor.WHITE+" qui une fois activé fera que la prochaine personne que vous taperez ce verra infliger"+
								" 2 coeur via un éclair également elle ce verra obtenir l'effet Slowness 1 pendant 15 secondes");
								modif = true;
				        	}			       
				        } else if (rint == 3) {
				        	if (!Roche) {
				        		if (form != Soufle.Roche) form = Soufle.Roche;
					        	color = ChatColor.GRAY;
					        	Roche = true;
								 this.setResi(20);
								owner.sendMessage(ChatColor.WHITE+"Vous avez choisis le soufle de la: "+ChatColor.GOLD+ form);
								owner.sendMessage(ChatColor.WHITE+"Vous obtenez maintenant résistance 1 le jour");
								modif = true;
				        	}			      
				        } else if (rint == 4) {
				        	if (!Vent) {
					        if (form != Soufle.Vent) form = Soufle.Vent;
					        	Vent = true;
					        	color = ChatColor.GREEN;
					        	owner.getInventory().addItem(Items.getSoufleduVent());
					        	owner.sendMessage(ChatColor.WHITE+"Vous obtenez l'accès à l'item: "+
										ChatColor.GOLD+"Soufle du Vent: "+ChatColor.WHITE+"qui vous donne speed 2 pendant 2 minutes");
					        	modif = true;
				        	}
				        }
				        owner.sendMessage("Vous avez obtenue le Soufle: "+color+ form.name());
			} while (modif == false);
		}
		super.PlayerKilled(killer, victim, gameState);
	}

	@Override
	public ItemStack[] getItems() {
		return new ItemStack[0];
	}
}