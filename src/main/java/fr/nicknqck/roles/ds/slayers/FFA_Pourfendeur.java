package fr.nicknqck.roles.ds.slayers;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Objective;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

public class FFA_Pourfendeur extends RoleBase {
	enum Soufle {
		Soleil,
		Lune,
		Feu,
		Eau,
		Roche,
		Vent,
		Foudre,
		Univers,
		Amour,
		Brume,
		Serpent,
		Fleur
	}
	Soufle form = null;
	public FFA_Pourfendeur(Player player, Roles roles, GameState gameState) {
		super(player, roles, gameState);
		for (String desc : AllDesc.Pourfendeur)owner.sendMessage(desc);
		owner.sendMessage("Vue que nous somme en FFA vous devez vous-même choisir votre Soufle via la commmande§6 /ds role");
        setCanUseBlade(true);
        setLameIncassable(owner, true);
	}
	@Override
	public String[] Desc() {
		return AllDesc.Pourfendeur;
	}
	public boolean cheat = false;
	boolean Soleil = false;
	boolean Lune = false;
	boolean Feu = false;
	boolean Eau = false;
	boolean Roche = false;
	boolean Vent = false;
	boolean Foudre = false;
	boolean Amour = false;
	boolean Brume = false;
	public boolean Serpent = false;
	boolean Fleur = false;
	int cooldownsoleil = 0;
	int cooldowneau = 0;
	int cooldownvent = 0;
	int cooldownfoudre = 0;
	int cooldownbrume = 0;
	boolean usefoudre = false;
	boolean usesoleil = false;
	int nombredesoufle = 0;
	int kill = 1;
	boolean SoufleUnivers = false;
	boolean useuni = false;
	boolean msgsend = false;
	int serpenttimeusage = 60;
	public int serpentactualtime = 0;
	int cooldownserpent = 0;
	
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(Items.getLamedenichirin());
		super.GiveItems();
	}
	@Override
	public void OpenFormInventory(GameState gameState) {
		if (form == null || kill != 0) {
			Inventory inv = Bukkit.createInventory(owner, 54, "Choix de forme");
			inv.setItem(0, GUIItems.getOrangeStainedGlassPane());
			inv.setItem(1, GUIItems.getOrangeStainedGlassPane());
			inv.setItem(9, GUIItems.getOrangeStainedGlassPane());//haut gauche
			
			inv.setItem(7, GUIItems.getOrangeStainedGlassPane());
			inv.setItem(8, GUIItems.getOrangeStainedGlassPane());
			inv.setItem(17, GUIItems.getOrangeStainedGlassPane());//haut droite
			
			inv.setItem(45, GUIItems.getOrangeStainedGlassPane());
			inv.setItem(46, GUIItems.getOrangeStainedGlassPane());
			inv.setItem(36, GUIItems.getOrangeStainedGlassPane());//bas gauche
			
			inv.setItem(44, GUIItems.getOrangeStainedGlassPane());
			inv.setItem(52, GUIItems.getOrangeStainedGlassPane());
			inv.setItem(53, GUIItems.getOrangeStainedGlassPane());//bas droite
			inv.setItem(4, GUIItems.getSouffleSoleil());
			inv.setItem(14, GUIItems.getSouffleLune());
			inv.setItem(18, GUIItems.getSouffleFeu());
			inv.setItem(20, GUIItems.getSouffleEau());
			inv.setItem(22, GUIItems.getSouffleRoche());
			inv.setItem(24, GUIItems.getSouffleVent());
			inv.setItem(26, GUIItems.getSouffleFoudre());
			inv.setItem(27, GUIItems.getSouffleAmour());
			inv.setItem(33, GUIItems.getSouffleBrume());
			inv.setItem(30, GUIItems.getSouffleSerpent());
			inv.setItem(29, GUIItems.getSouffleFleur());
			owner.openInventory(inv);
			}
		if ((Soleil && Lune && Feu && Eau && Roche && Vent && Foudre && !SoufleUnivers) || kill == 7 || cheat) {
			Inventory inv = Bukkit.createInventory(owner, 27, "Choix de forme");
			inv.setItem(13, Items.getSoufleDeLunivers());
			owner.openInventory(inv);
		}
	//	super.OpenFormInventory(gameState);
	}
	@Override
	public void FormChoosen(ItemStack item, GameState gameState) {
		if (form == null || kill != 0) {
			if (item.isSimilar(GUIItems.getSouffleSoleil())) {
				if (!Soleil) {
					form = Soufle.Soleil;
					Soleil = true;
					owner.sendMessage("Ce soufle est temporairement désactivé, pour cause d'équilibrage");
					/*owner.sendMessage("Vous avez choisis le soufle du: "+ChatColor.GOLD+ form);
					owner.sendMessage("Vous avez maintenant accès à l'item: "+ChatColor.GOLD+"Soufle du Soleil: "+ChatColor.WHITE+"Pendant 2 minutes, quand vous tapez un joueur il perd son absorbtion");
					owner.getInventory().addItem(Items.getSoufleduSoleil());*/
					nombredesoufle++;
					kill--;
				} else {
					owner.sendMessage(ChatColor.RED+"Vous avez déjà maitriser ce soufle....");
				}
			}
			if (item.isSimilar(GUIItems.getSouffleLune())) {
				if (Lune == false) {
					form = Soufle.Lune;
					Lune = true;
					owner.sendMessage(ChatColor.WHITE+"Vous avez choisis le soufle de la: "+ChatColor.GOLD+ form);
					owner.sendMessage("Vous possédez maintenant: "+ChatColor.GOLD+"force 1 la nuit");
					this.setForce(20);
					nombredesoufle++;
					kill--;
				} else {
					owner.sendMessage(ChatColor.RED+"Vous avez déjà maitriser ce soufle....");
				}				
			}
			if (item.isSimilar(GUIItems.getSouffleFeu())) {
				if (Feu == false) {
					form = Soufle.Feu;
					Feu = true;
					owner.sendMessage(ChatColor.WHITE+"Vous avez choisis le soufle du: "+ChatColor.GOLD+ form);
					owner.sendMessage("Maintenant quand vous "+"taperez un joueur il sera automatiquement mit en feu");
					nombredesoufle++;
					kill--;
				} else {
					owner.sendMessage(ChatColor.RED+"Vous avez déjà maitriser ce soufle....");
				}				
			}
			if (item.isSimilar(GUIItems.getSouffleEau())) {
				if (!Eau) {
					form = Soufle.Eau;
					Eau = true;
					owner.sendMessage(ChatColor.WHITE+"Vous avez choisis le soufle de l' "+ChatColor.GOLD+ form);
					owner.getInventory().addItem(Items.getWaterBoots());
					owner.getInventory().addItem(Items.getSoufleDeLeau());
					owner.sendMessage(ChatColor.WHITE+"Vous venez d'obtenir des "+
					ChatColor.GOLD+"Botte en Diamant Depth Strider 1"+ChatColor.WHITE+" et l'item: "+ChatColor.GOLD+"Soufle de l'Eau: "+ChatColor.WHITE+"qui vous donne Speed 1 pendant 3 minutes");
					nombredesoufle++;
					kill--;
				}  else {
					owner.sendMessage(ChatColor.RED+"Vous avez déjà maitriser ce soufle....");
				}
				
			}
			if (item.isSimilar(GUIItems.getSouffleRoche())) {
				if (!Roche) {
					form = Soufle.Roche;
					Roche = true;
					owner.sendMessage(ChatColor.WHITE+"Vous avez choisis le soufle de la: "+ChatColor.GOLD+ form);
					owner.sendMessage(ChatColor.WHITE+"Vous obtenez maintenant résistance 1 le jour");
					nombredesoufle++;
					kill--;
				}  else {
					owner.sendMessage(ChatColor.RED+"Vous avez déjà maitriser ce soufle....");
				}				
			}
			if (item.isSimilar(GUIItems.getSouffleVent())) {
				if (Vent == false) {
					form = Soufle.Vent;
					Vent = true;
					owner.sendMessage(ChatColor.WHITE+"Vous avez choisis le soufle du: "+ChatColor.GOLD+ form);
					owner.getInventory().addItem(Items.getSoufleduVent());
					owner.sendMessage(ChatColor.WHITE+"Vous obtenez l'accès à l'item: "+
					ChatColor.GOLD+"Soufle du Vent: "+ChatColor.WHITE+"qui vous donne speed 2 pendant 2 minutes");
					nombredesoufle++;
					kill--;
				}  else {
					owner.sendMessage(ChatColor.RED+"Vous avez déjà maitriser ce soufle....");
				}			
			}
			if (item.isSimilar(GUIItems.getSouffleFoudre())) {
				if (!Foudre) {
					form = Soufle.Foudre;
					Foudre = true;
					owner.sendMessage(ChatColor.WHITE+"Vous avez choisis le soufle de la: "+ChatColor.GOLD+ form);
					owner.getInventory().addItem(Items.getSoufleFoudre5iememouvement());
					owner.sendMessage(ChatColor.WHITE+"Vous obtenez l'accès à l'item: "+
					ChatColor.GOLD+"Soufle de la Foudre: Eclair de Chaleur"+ChatColor.WHITE+" qui une fois activé fera que la prochaine personne que vous taperez ce verra infliger"+
					" 2 coeur via un éclair également elle ce verra obtenir l'effet Slowness 1 pendant 15 secondes");
					nombredesoufle++;
					kill--;
				}  else {
					owner.sendMessage(ChatColor.RED+"Vous avez déjà maitriser ce soufle....");
				}				
			}
			if (item.isSimilar(GUIItems.getSouffleAmour())) {
				if (!Amour) {
					form = Soufle.Amour;
					Amour = true;
					owner.sendMessage(ChatColor.WHITE+"Vous avez choisis le soufle de l' "+ChatColor.GOLD+ form);
					setMaxHealth(getMaxHealth() + 4.0);
					owner.setMaxHealth(getMaxHealth());
					owner.setHealth(owner.getHealth() + 4.0);
					owner.sendMessage("Vous venez de gagner 2§c❤§r permanent");
					kill--;
					nombredesoufle++;
				}
			}
			if (item.isSimilar(GUIItems.getSouffleBrume())) {
				if (!Brume) {
					form = Soufle.Brume;
					Brume = true;
					nombredesoufle++;
					kill--;
					owner.sendMessage(ChatColor.WHITE+"Vous avez choisis le soufle de la "+ChatColor.GOLD+ form);
					owner.getInventory().addItem(Items.getSoufleBrume());
				}
			}
			if (item.isSimilar(GUIItems.getSouffleSerpent())) {
				if (!Serpent) {
					form = Soufle.Serpent;
					Serpent = true;
					kill--;
					nombredesoufle++;
					owner.sendMessage("Vous avez choisis le souffle du§6 "+form.name());
					owner.getInventory().addItem(Items.getSouffleSerpent());
				}
			}
			if (item.isSimilar(GUIItems.getSouffleFleur())) {
				if (!Fleur) {
					form = Soufle.Fleur;
					Fleur = true;
					kill--;
					nombredesoufle++;
					owner.sendMessage("Vous avez choisis le souffle de la§6 "+form.name());
				}
			}
		}
		if (item.isSimilar(Items.getSoufleDeLunivers())) {
			if (!SoufleUnivers) {
				if (cheat) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.sendMessage(owner.getName()+" à utilisé le soufle qu'il à obtenue en trichant #pasbien");
					}
				}
				nombredesoufle = 1;			
				Soleil = false;
				Lune = false;
				Feu = false;
				Eau = false;
				Roche = false;
				Vent = false;
				Foudre = false;
				owner.getInventory().setBoots(Items.getdiamondboots());
				owner.getInventory().addItem(Items.getSoufleDeLunivers());
				owner.getInventory().remove(Items.getSoufleDeLeau());
				owner.getInventory().remove(Items.getSoufleduSoleil());
				owner.getInventory().remove(Items.getSoufleFoudre5iememouvement());
				owner.getInventory().remove(Items.getSoufleduVent());
				owner.getInventory().remove(Items.getWaterBoots());
				form = Soufle.Univers;
				owner.getInventory().addItem(Items.getgoldenapple());
				owner.getInventory().addItem(Items.getgoldenapple());
				owner.getInventory().addItem(Items.getgoldenapple());
				owner.getInventory().addItem(Items.getgoldenapple());
				owner.getInventory().addItem(Items.getgoldenapple());
				SoufleUnivers = true;
				setForce(20);
				if (kill == 7) {
					kill = 0;
				} else if (kill > 7) {
					kill-=7;
				}
				owner.sendMessage(ChatColor.GRAY+"Vous avez choisis de posséder le Soufle de l'Univers, vous obtenez donc un item vous permettant de gagner les effets: "+ChatColor.GOLD+"Résistance 1, Force 1, Speed 2, 5 coeur permanent en plus, 5 pommes en or");
				
			}  else {
				owner.sendMessage(ChatColor.RED+"Attend une seconde mais ta vrm farm 2 fois le soufle de l'univers la mais t un malade toi tient oublie sa c'est inutile");
			}
		}
		super.FormChoosen(item, gameState);
	}
	@Override
	public void Update(GameState gameState) {
		if (owner.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
			if (getResi() != 20)setResi(20);			
		} else {
			if (getResi() != 0) setResi(0);
		}
		if (Fleur) {
			if (owner.hasPotionEffect(PotionEffectType.POISON)) {
				owner.removePotionEffect(PotionEffectType.POISON);
			}
		}
		if (Soleil && Lune && Feu && Eau && Roche && Vent && Foudre && !msgsend ) {
			msgsend = true;
			owner.sendMessage(ChatColor.GRAY+"Vous venez de débloquer le Soufle de l'Univers, vous pouvez donc faire la commande /ds role pour valider le fait de l'obtenir cepandant si vous choisissez de l'obtenenir vous perdrez l'accès au autre Soufle, MAIS vous obtenez un item vous permettant de gagner les effets: "+ChatColor.GOLD+"Résistance 1, Force 1, Speed 2, 5 coeur permanent en plus, 5 pommes en or");
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
		if (cooldownbrume >= 1) {
			cooldownbrume--;
		}
		if (Serpent) {if (cooldownserpent >= 1)cooldownserpent--; if (serpentactualtime >= 0)serpentactualtime--;}
		if (cooldownsoleil == 60*3) {
			usesoleil = false;
			owner.sendMessage("Vous ne retirez plus l'absorbtion des joueurs que vous tapez");
		}
		if (Lune&& gameState.nightTime) {
			owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*3, 0, false, false));
		}
		if (Roche && !gameState.nightTime) {
			owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*3, 0, false, false));
		}
		if (useuni) {
			owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 0, false, false));
			owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1, false, false));
			owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 999999, 0, false, false));
		}
		if (owner.getItemInHand().isSimilar(Items.getSoufleduSoleil())) {
			if (cooldownsoleil > 0) {
					String message = "Cooldown: §6"+cooldownsoleil/60+"m"+cooldownsoleil%60+"s";
					PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte)2);
					((CraftPlayer) owner).getHandle().playerConnection.sendPacket(packet);
			} else {
					String message = owner.getItemInHand().getItemMeta().getDisplayName()+"§r Utilisable";
					PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte)2);
					((CraftPlayer) owner).getHandle().playerConnection.sendPacket(packet);
			}
		}
		if (owner.getItemInHand().isSimilar(Items.getSoufleduVent())) {
			if (cooldownvent > 0) {
					String message = "Cooldown: §6"+cooldownvent/60+"m"+cooldownvent%60+"s";
					PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte)2);
					((CraftPlayer) owner).getHandle().playerConnection.sendPacket(packet);
			} else {
					String message = owner.getItemInHand().getItemMeta().getDisplayName()+"§r Utilisable";
					PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte)2);
					((CraftPlayer) owner).getHandle().playerConnection.sendPacket(packet);
			}
		}
		if (owner.getItemInHand().isSimilar(Items.getSoufleDeLeau())) {
			if (cooldowneau > 0) {
					String message = "Cooldown: §6"+cooldowneau/60+"m"+cooldowneau%60+"s";
					PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte)2);
					((CraftPlayer) owner).getHandle().playerConnection.sendPacket(packet);
			} else {
					String message = owner.getItemInHand().getItemMeta().getDisplayName()+"§r Utilisable";
					PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte)2);
					((CraftPlayer) owner).getHandle().playerConnection.sendPacket(packet);
			}
		}
		if (owner.getItemInHand().isSimilar(Items.getSoufleFoudre5iememouvement())) {
			if (cooldownfoudre > 0) {
					String message = "Cooldown: §6"+cooldownfoudre/60+"m"+cooldownfoudre%60+"s";
					PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte)2);
					((CraftPlayer) owner).getHandle().playerConnection.sendPacket(packet);
			} else {
					String message = owner.getItemInHand().getItemMeta().getDisplayName()+"§r Utilisable";
					PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte)2);
					((CraftPlayer) owner).getHandle().playerConnection.sendPacket(packet);
			}
		}
		if (owner.getItemInHand().isSimilar(Items.getSoufleBrume())) {
			if (cooldownbrume > 0) {
					String message = "Cooldown: §6"+cooldownbrume/60+"m"+cooldownbrume%60+"s";
					PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte)2);
					((CraftPlayer) owner).getHandle().playerConnection.sendPacket(packet);
			} else {
					String message = owner.getItemInHand().getItemMeta().getDisplayName()+"§r Utilisable";
					PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte)2);
					((CraftPlayer) owner).getHandle().playerConnection.sendPacket(packet);
			}
		}
		if (owner.getItemInHand().isSimilar(Items.getSouffleSerpent())) {
			if (cooldownserpent > 0) {
					String message = "Cooldown: §6"+cooldownserpent/60+"m"+cooldownserpent%60+"s§r"+" Temp d'esquive:§6 "+serpentactualtime+"s";
					PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte)2);
					((CraftPlayer) owner).getHandle().playerConnection.sendPacket(packet);
			} else {
					String message = owner.getItemInHand().getItemMeta().getDisplayName()+"§r Utilisable";
					PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte)2);
					((CraftPlayer) owner).getHandle().playerConnection.sendPacket(packet);
			}
		}
		super.Update(gameState);
	}
	@Override
	public void ItemUseAgainst(ItemStack item, Player victim, GameState gameState) {
		if (Feu && victim != owner) {
			 int x = 8 * 20;
			 Random random = new Random();
			 int rint = random.nextInt(5);
				if (rint == 0) {
					 if (victim instanceof Player) {
				            victim.setFireTicks(x);
				        }
				}
		       
		}
		if (Foudre&& victim != owner) {
			if (usefoudre) {
				for(Player p : gameState.getInGamePlayers())
					if (p != owner && p == victim) {
						if (victim.getHealth() > 4.0) {
							victim.setHealth(victim.getHealth() - 4.0);
						} else {
							victim.setHealth(1.0);
						}			
						victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*15, 0, false, false));
							victim.damage(0.0);
					    	owner.sendMessage(ChatColor.GREEN+"Vous avez touchez : "+ ChatColor.GOLD + victim.getName() + "");
					    	victim.sendMessage(ChatColor.GREEN+"Vous avez été foudroyez par un simple "+ChatColor.GOLD+"Pourfendeur de Démon...");
					        victim.getWorld().strikeLightningEffect(victim.getLocation());
						}
				usefoudre = false;
			}
		}
		if (Soleil == true && usesoleil == true) {
			if (victim.hasPotionEffect(PotionEffectType.ABSORPTION)) {
				victim.removePotionEffect(PotionEffectType.ABSORPTION);
				victim.sendMessage(ChatColor.WHITE+"un simple "+ChatColor.GOLD+"Pourfendeur de Démon"+ChatColor.WHITE+" vous à retirer votre absorbtion");
				owner.sendMessage(ChatColor.WHITE+"Vous venez de retirer l'absorbtion de: "+ChatColor.GOLD+ChatColor.BOLD+ victim.getName());
			}
		}
		super.ItemUseAgainst(item, victim, gameState);
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(Items.getSoufleDeLunivers()) && SoufleUnivers ) {
		if (!useuni) {
			useuni = true;
			if (this.getMaxHealth() == 24.0) {
				this.setMaxHealth(34.0);
			}
			if (this.getMaxHealth() == 20.0) {
				this.setMaxHealth(30.0);
			}
			owner.sendMessage("Vous venez de d'activer le Soufle de l'Univers");
		} else if (useuni){
			if (this.getMaxHealth() == 34.0) {
				this.setMaxHealth(24.0);
			}
			if (this.getMaxHealth() == 30.0) {
				this.setMaxHealth(20.0);
			}
			owner.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
			owner.removePotionEffect(PotionEffectType.SPEED);
			owner.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
			owner.sendMessage("Vous venez de désactiver le Soufle de l'Univers");
			useuni = false;
			}
		}
		if (item.isSimilar(Items.getSoufleDeLeau()) && Eau) {
			if (cooldowneau <= 0) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*3, 0, false, false));
				cooldowneau = 60*6;
			} else {
				int s = cooldowneau%60;
				int m = cooldowneau/60;
				owner.sendMessage(ChatColor.RED+"Vous ne pourrez utiliser a nouveau votre abilité que dans "+ChatColor.GOLD+m+" minutes"+ChatColor.RED+" et "+ChatColor.GOLD+s+" secondes");
				return true;
			}
		}
		if (item.isSimilar(Items.getSoufleduVent()) && Vent) {
			if (cooldownvent <= 0) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*2, 1, false, false));
				cooldownvent = 60*4;
			} else {
				int s = cooldownvent%60;
				int m = cooldownvent/60;
				owner.sendMessage(ChatColor.RED+"Vous ne pourrez utiliser a nouveau votre abilité que dans "+ChatColor.GOLD+m+" minutes"+ChatColor.RED+" et "+ChatColor.GOLD+s+" secondes");
				return true;
			}
		}
		if (item.isSimilar(Items.getSoufleduSoleil()) && Soleil) {
			if (cooldownsoleil <= 0) {
				cooldownsoleil = 60*5;
				usesoleil = true;
			} else {
				int s = cooldownsoleil%60;
				int m = cooldownsoleil/60;
				owner.sendMessage(ChatColor.RED+"Vous ne pourrez utiliser a nouveau votre abilité que dans "+ChatColor.GOLD+m+" minutes"+ChatColor.RED+" et "+ChatColor.GOLD+s+" secondes");
				return true;
			}
		}
		if (item.isSimilar(Items.getSoufleFoudre5iememouvement()) && Foudre) {
			if (cooldownfoudre <= 0) {
				cooldownfoudre = 90;
				usefoudre = true;
				owner.sendMessage("Il faut maintenant tapée un joueur pour lui infliger un éclair");
			}  else {
				int s = cooldownfoudre%60;
				int m = cooldownfoudre/60;
				owner.sendMessage(ChatColor.RED+"Vous ne pourrez utiliser a nouveau votre abilité que dans "+ChatColor.GOLD+m+" minutes"+ChatColor.RED+" et "+ChatColor.GOLD+s+" secondes");
				return true;
			}
		}
		if (item.isSimilar(Items.getSoufleBrume()) && Brume) {
			if (cooldownbrume <= 0) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20*120, 0, false, false));
				cooldownbrume = 60*5;
				owner.sendMessage("Vous avez activé votre Soufle de la Brume");
			}  else {
				int s = cooldownbrume%60;
				int m = cooldownbrume/60;
				owner.sendMessage(ChatColor.RED+"Vous ne pourrez utiliser a nouveau votre abilité que dans "+ChatColor.GOLD+m+" minutes"+ChatColor.RED+" et "+ChatColor.GOLD+s+" secondes");
				return true;
			}
		}
		if (item.isSimilar(Items.getSouffleSerpent())) {
			if (Serpent) {
				if (cooldownserpent <= 0) {
					owner.sendMessage("Vous avez activé le Souffle du Serpent");
					cooldownserpent = 60*5; serpentactualtime = serpenttimeusage;
				}
			}
		}
		return super.ItemUse(item, gameState);
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (killer == owner && victim != owner) {
			kill++;
			if (form != null) {
			form = null;
			}
			owner.sendMessage("Vous venez de tué un joueur vous pouvez donc faire la commande§6 /ds role§r pour obtenir l'accès à un nouveau soufle");
			
		}
		
		super.PlayerKilled(killer, victim, gameState);
	}

	@Override
	public ItemStack[] getItems() {
		return new ItemStack[0];
	}
	@Override
	public void resetCooldown() {

	}
}