package fr.nicknqck.roles.ns.solo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.Cuboid;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.packets.NMSPacket;
import fr.nicknqck.utils.WorldUtils;
import fr.nicknqck.utils.particles.TapisSableEffect;
import net.minecraft.server.v1_8_R3.EnumParticle;

public class Gaara extends RoleBase {

    public Gaara(Player player, Roles roles) {
		super(player, roles);
		owner.sendMessage(Desc());
		setNoFall(true);
		resetCooldown();
	}

    private int fixCooldown = 0;
    private int shukakuCooldown = 0;
    private boolean usingShukaku = false;
    private boolean usingArmure = false;
    private Manipulation manipulation = Manipulation.AUCUN;
    private boolean tookDamage = false;
    
    
    @Override
    public void resetCooldown() {
    	fixCooldown = 0;
        shukakuCooldown = 0;
    }
    int timingsable = 15;
    @Override
    public void Update(GameState gameState) {
    	if(fixCooldown > 0) fixCooldown--;
    	sendCustomActionBar(owner, "§eSable: "+sablenmb);
        if(shukakuCooldown > 0) {
        	if (usingShukaku) {
        		int newcd = shukakuCooldown-(60*15);
        		sendCustomActionBar(owner, "§eSable: "+sablenmb+aqua+" Temp restant sous§e Shukaku§r: "+cd(newcd));
        	}
        	shukakuCooldown--;
        }
        if (sablenmb < 300) {
        	sablenmb++;
        }
        if (shukakuCooldown == 60*15) {
        	usingShukaku = false;
        	owner.sendMessage("§7Désactivation de§e Shukaku");
        }
    	super.Update(gameState);
    }
    public void removeItem(Player player, Material material, int remove) {
        if (player.getInventory().getItem(player.getInventory().first(material)).getAmount() <= remove) {
            player.getInventory().removeItem(player.getInventory().getItem(player.getInventory().first(material)));
            return;
        }
        player.getInventory().getItem(player.getInventory().first(material)).setAmount(player.getInventory().getItem(player.getInventory().first(material)).getAmount() - remove);
        if (remove > 64) {
            player.getInventory().getItem(player.getInventory().first(material)).setAmount(player.getInventory().getItem(player.getInventory().first(material)).getAmount() - (remove - 64));
        }
    }
    public int getItemAmount(Player player, Material material) {
        int toReturn = 0;
        for (ItemStack content : player.getInventory().getContents()) {
            if (content != null && content.getType() == material) {
                toReturn += content.getAmount();
            }
        }
        return toReturn;
    }
    @Override
    public String[] Desc() {
    	return new String[]
    			{
    					"§7§m--------------------------------------\n" +
    			                "§e §f\n" +
    			                "§7▎ Rôle: §eGaara\n" +
    			                "§7▎ Objectif: §rGagner§e Seul\n" +
    			                "§e §f\n" +
    			                "§7§l▎ Items :\n" +
    			                "§e §f\n" +
    			                "§7• \"§eManipulation du sable§7\", via un clique droit active le pouvoir choisis lors de l'utilisation de l'item \"§eAttaque§7\\ ou §eDéfense§7\"\\.\n" +
    			                "§e §f\n" +
    			                "§7• Il dispose d’un item “§eShukaku§7”, celui-ci, lui permet, pendant 5 minutes, de réduire le nombre de sables qu’il doit utiliser, il utilise donc 2 fois moins de sable que d’habitude, en utilisant l’une de ses techniques. Son pouvoir possède un délai de 20 minutes. \n" +
    			                "§e §f\n" +
    			                "§7§l▎ Particularités :\n" +
    			                "§e §f\n" +
    			                "§7• Il dispose de 128 blocs de sables.\n" +
    			                "§e §f\n" +
    			                "§7§l▎ Manipulation du sable :\n" +
    			                "§e §f\n" +
    			                "§7• lorsque §eGaara clique sur l’item §rAttaque§7, celui-ci le dirige vers un nouveau menu, lui montrant plusieurs options, ayant toutes des avantages différents.\n" +
    			                "      \n" +
    			                "§7→ §lTsunami de Sable§7 : Il permet de créer une vague de sable qui déferle face à §eGaara§7, si elle vient à touché un joueur, le joueur sera propulsé violemment en arrière, et reçoit §c2"+AllDesc.coeur+"§7 de dégâts. Ce pouvoir nécessite§e 30 blocs de sables§7. \n" +
    			                "§e §f\n" +
    			                "§7→ §lLance§7 : Il permet de donner à §eGaara§7 une épée en diamant Tranchant 4, cependant celle-ci possède seulement 25 points de durabilités. Ce pouvoir nécessite§e 64 blocs de sables§7. \n" +
    			                "§e §f\n" +
    			                "§7Lorsqu’il est dans le menu de son item et qu’il clique sur l’option §rDéfense§7, celui-ci le dirige vers un nouveau menu, lui montrant plusieurs options, ayant toutes des avantages différents.\n" +
    			                "§e §f\n" +
    			                "§7→ §lArmure de Sable§7 : Il permet de donner à §eGaara§7 l’effet §9Résistance 1§7. Ce pouvoir nécessite 32 blocs de sables, cependant chaque coup qu’il reçoit lui coûte 5 blocs de sables, s’il n’a plus ou pas suffisamment de blocs de sables, son pouvoir ne fait plus effet. \n" +
    			                "§e §f\n" +
    			                "§7→ §lSuspension du Désert§7 : Il permet à §eGaara§7 de voler pendant 20 secondes, lorsqu’il vole des particules oranges apparaissent sous ses pieds, elles forment une plateforme. S’il vient à infliger ou recevoir un dégâts, son pouvoir se désactive instantanément. Ce pouvoir nécessite§e 96 blocs de sables§7.\n" +
    			                "§e §f\n" +
    			                "§7Après avoir sélectionné l’un de ces pouvoirs, pour utiliser le pouvoir choisit, il doit faire un clique gauche avec son item et le pouvoir s’utilisera, les pouvoirs cités ci-dessus peuvent être utilisés simultanément.\n" +
    			                "§e §f\n" +
    			                AllDesc.commande,
    			                "",
    			                AllDesc.point+"§6§l/ns convert:§7 Convertie vos stack de§e sable§7 en point de§e sable§7, ce qui vous permettra d'utiliser vos capacité",
    			                "",
    			                "§7§m--------------------------------------"
    			};
    }
    @Override
    public void GiveItems() {
    	giveItem(owner, false, getItems());
        owner.getInventory().addItem(new ItemBuilder(Material.INK_SACK).setDyeColor(DyeColor.BLUE).addEnchant(Enchantment.ARROW_DAMAGE, 1).hideEnchantAttributes().setName(("§eShukaku")).setLore("§7"+StringID).toItemStack());
        sablenmb = 128;
    	super.GiveItems();
    }    
    @Override
    public void neoAttackedByPlayer(Player attacker, GameState gameState) {
    	tookDamage = true;
    	if(usingArmure) {
            if(sablenmb < 5) {
                owner.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                usingArmure = false;
                owner.sendMessage("§7Vous n'avez plus asser de sable pour maintenir votre armure, ce qui vous à fais perdre votre effet "+AllDesc.Resi);
                setResi(0);
            } else {
            	sablenmb-=5;
                owner.sendMessage("§7Vous venez de perdre§e 5 sables§7, il ne vous en reste que "+sablenmb);
            }
        }
    	super.neoAttackedByPlayer(attacker, gameState);
    }
    @Override
    public void FormChoosen(ItemStack item, GameState gameState) {
    	if (selectedmeni == 1) {
    		Material type = item.getType();
    		 if (type.equals(Material.WATER_BUCKET)){
         		owner.sendMessage(("§fVous avez sélectionné le pouvoir §aTsunami de Sable§f."));
                 manipulation = Manipulation.TSUNAMI;
                 owner.closeInventory();
         	}
    		 if (type.equals(Material.DIAMOND_SWORD)) {
    			 owner.sendMessage(("§fVous avez sélectionné le pouvoir §aLance§f."));
                 manipulation = Manipulation.LANCE;
                 owner.closeInventory();
    		 }
    	}
    	if (selectedmeni == 2) {
    		Material type = item.getType();
    		if (type.equals(Material.DIAMOND_CHESTPLATE)) {
    			owner.sendMessage(("§fVous avez sélectionné le pouvoir §aArmure de Sable§f."));
                manipulation = Manipulation.ARMURE;
                owner.closeInventory();
    		}
    		if (type.equals(Material.FEATHER)) {
    			  owner.sendMessage(("§fVous avez sélectionné le pouvoir §aSuspension du Désert§f."));
                  manipulation = Manipulation.SUSPENSION;
                  owner.closeInventory();
    		}
    	}
    	super.FormChoosen(item, gameState);
    }
    private void openDefense(GameState gameState) {
    	selectedmeni = 2;
    	Inventory inv = Bukkit.createInventory(owner, 9, "Défense");
        inv.setItem(3, new ItemBuilder(Material.DIAMOND_CHESTPLATE).setName("§6Armure de Sable").setLore(
                "§7Il permet de donner à Gaara l’effet Résistance",
                "§71. Ce pouvoir nécessite 32 blocs de sables,",
                "§7cependant chaque coup qu’il reçoit lui coûte",
                "§75 blocs de sables, s’il n’a plus ou pas",
                "§7suffisamment de blocs de sables, son pouvoir",
                "§7ne fait plus effet.",
                "",
                "§0"+StringID
        ).toItemStack());
        inv.setItem(5, new ItemBuilder(Material.FEATHER).setName("§6Suspension du Désert").setLore(
                "§7Il permet à Gaara de voler pendant 20",
                "§7secondes, lorsqu’il vole des particules",
                "§7oranges apparaissent sous ses pieds, elles",
                "§7forment une plateforme. S’il vient à",
                "§7infliger ou recevoir un dégâts, son pouvoir",
                "§7se désactive instantanément. Ce pouvoir",
                "§7nécessite 96 blocs de sables.",
                "",
                "§0"+StringID
        ).toItemStack());

        owner.openInventory(inv);
		
	}
    private void openAttaque(GameState gameState) {
     selectedmeni = 1;
	 Inventory inv = Bukkit.createInventory(owner, 9, "Attaque");
     inv.setItem(3, new ItemBuilder(Material.WATER_BUCKET).setName("§6Tsunami de Sable").setLore(
             "§7Il permet de créer une vague de sable qui",
             "§7déferle face à Gaara, si elle vient à",
             "§7touché un joueur, le joueur sera propulsé",
             "§7violemment en arrière, et reçoit 2"+AllDesc.coeur,
             "§7de dégâts. Ce pouvoir nécessite 30 blocs",
             "§7de sables.",
             "",
             "§0"+StringID
     ).toItemStack());
     inv.setItem(5, new ItemBuilder(Material.DIAMOND_SWORD).setName("§6Lance").setLore(
             "§7Il permet de donner à Gaara une épée en",
             "§7diamant Tranchant 4, cependant celleci",
             "§7possède seulement 25 points de durabilités.",
             "§7Ce pouvoir nécessite 64 blocs de sables.",
             "",
             "§0"+StringID
     ).toItemStack());

     owner.openInventory(inv);
		
	}
    int selectedmeni = 0;
    @Override
    public void onNsCommand(String[] args) {
    	if (args[0].equalsIgnoreCase("convert")) {
    		sablenmb += getItemAmount(owner, Material.SAND);
    		owner.sendMessage("Vous venez de gagner§e "+getItemAmount(owner, Material.SAND)+" sable");
    		owner.getInventory().remove(Material.SAND);
    	}
    	super.onNsCommand(args);
    }
	@Override
	public void onLeftClick(PlayerInteractEvent event, GameState gameState) {
		if (event.getItem() != null) {
			if (event.getItem().hasItemMeta()) {
				if (event.getItem().getItemMeta().hasDisplayName()) {
					String name = event.getItem().getItemMeta().getDisplayName();
					if (name.equalsIgnoreCase("§eManipulation du sable") || name.equalsIgnoreCase(Attaque().getItemMeta().getDisplayName()) || name.equals(Defense().getItemMeta().getDisplayName())){
						if (manipulation != null) {
							runPower(owner);
                        }else {
							owner.sendMessage("Veuiller séléctionner un pouvoir avant d'utiliser cette item !");
                        }
                        return;
                    }
				}
			}
		}
		super.onLeftClick(event, gameState);
	}    
    @Override
    public boolean ItemUse(ItemStack item, GameState gameState) {
    	if (item == null)return false;
    	if (!item.hasItemMeta()) return false;
    	if (!item.getItemMeta().hasDisplayName())return false;
    if (item.getType().equals(Material.NETHER_STAR)) {
    	String name = item.getItemMeta().getDisplayName();
    	if (item.isSimilar(Attaque())) {
    		openAttaque(gameState);
    	}
    	if (item.isSimilar(Defense())) {
    		openDefense(gameState);
    	}
    	if (name.equalsIgnoreCase("§eManipulation du sable")) {
    		if(manipulation != Manipulation.AUCUN) {
                runPower(owner);
                return true;
            }
    	}
    }
    if (item.getItemMeta().getDisplayName().equalsIgnoreCase("§eShukaku")) {
		if(shukakuCooldown > 0) {
			sendCooldown(owner, shukakuCooldown);
            return true;
        }
        owner.sendMessage(("Vous avez utilisé votre §aShukaku§f."));
        usingShukaku = true;
        givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 20*60*5, 1, true);
        setForce(20);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
        	usingShukaku = false;
        }, 5*20*60);
        shukakuCooldown = 20*60;
	}
    	return super.ItemUse(item, gameState);
    }    
    ItemStack Defense() {
		  ItemStack stack = new ItemStack(Material.NETHER_STAR, 1);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setDisplayName(ChatColor.YELLOW+"Défense");
		  meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
		  meta.spigot().setUnbreakable(true);
		  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		  meta.setLore(Arrays.asList("§7"+StringID));
		  stack.setItemMeta(meta);
		  return stack;
	  }
    ItemStack Attaque() {
		  ItemStack stack = new ItemStack(Material.NETHER_STAR, 1);
		  ItemMeta meta = stack.getItemMeta();
		  meta.setDisplayName(ChatColor.YELLOW+"Attaque");
		  meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
		  meta.spigot().setUnbreakable(true);
		  meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		  meta.setLore(Arrays.asList("§7"+StringID));
		  stack.setItemMeta(meta);
		  return stack;
	  }
    public void useTsunami(Player player) {
        Location initialLocation = player.getLocation().clone();
        initialLocation.setPitch(0.0f);

        Vector direction = initialLocation.getDirection();

        List<List<Location>> shape = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            List<Location> line = new ArrayList<>();

            Vector front = direction.clone().multiply(i);

            line.add(initialLocation.clone().add(front));
            for (int j = 0; j <= 2; j++) {
                Vector right = getRightHeadDirection(player).multiply(j), left = getLeftHeadDirection(player).multiply(j);


                line.add(initialLocation.clone().add(front.clone().add(right)));
                line.add(initialLocation.clone().add(front.clone().add(left)));
            }
            shape.add(line);
        }

        player.updateInventory();
        new Wave(initialLocation.toVector(), shape);
    }
    private  Vector getRightHeadDirection(Player player) {
        Vector direction = player.getLocation().getDirection().normalize();
        return new Vector(-direction.getZ(), 0.0, direction.getX()).normalize();
    }
    private  Vector getLeftHeadDirection(Player player) {
        Vector direction = player.getLocation().getDirection().normalize();
        return new Vector(direction.getZ(), 0.0, -direction.getX()).normalize();
    }
    private static class Wave extends BukkitRunnable {

        private final Vector origin;
        private final List<List<Location>> shape;
        private int index;

        public Wave(Vector origin, List<List<Location>> shape) {
            this.origin = origin;
            this.shape = shape;
            this.start();
        }

        private void start() {
            super.runTaskTimer(Main.getInstance(), 0, 2);
        }

        @SuppressWarnings("deprecation")
		@Override
        public void run() {
            if(index >= shape.size()){
                cancel();
                return;
            }
            for (Location loc : shape.get(index)) {
                FallingBlock fb = loc.getWorld().spawnFallingBlock(loc, Material.SAND, (byte) 0);
                fb.setDropItem(false);
                fb.setHurtEntities(true);
                fb.setVelocity(new Vector(0, .3, 0));
                for (Player players : Bukkit.getOnlinePlayers()) {
                    if(WorldUtils.getDistanceBetweenTwoLocations(players.getLocation(), loc) <= 1){
                        Vector fromPlayerToTarget = players.getLocation().toVector().clone().subtract(origin);
                        fromPlayerToTarget.multiply(4); //6
                        fromPlayerToTarget.setY(1); // 2
                        players.setVelocity(fromPlayerToTarget);
                        players.damage(2D*2D);
                    }
                }
            }
            index++;
        }
    }
    public void useSarcophage(Player player) {
        Player target = getTargetPlayer(player, 10);
        if(target != null){
            Location min = target.getLocation().clone().subtract(2, 1, 2), max = target.getLocation().clone().add(2, 5, 2);

            Cuboid sarcophage = new Cuboid(min, max);
            sarcophage.getBlocks().forEach(block -> block.setType(Material.SAND));
        }
        player.updateInventory();
    }
    public  void useLance(Player player) {
        ItemBuilder itemBuilder = new ItemBuilder(Material.DIAMOND_SWORD);
        itemBuilder.addEnchant(Enchantment.DAMAGE_ALL, 4);
        itemBuilder.setDurability((short) (Material.DIAMOND_SWORD.getMaxDurability() - 25));
        player.getInventory().addItem(itemBuilder.toItemStack());
        player.updateInventory();
    }
    public  List<Block> getBlocks(Location center, int radius, boolean hollow, boolean sphere) {
        List<Location> locs = circle(center, radius, radius, hollow, sphere, 0);
        List<Block> blocks = new ArrayList<>();

        for (Location loc : locs) {
            blocks.add(loc.getBlock());
        }

        return blocks;
    }
    public  List<Location> circle(final Location loc,final int radius,final int height,final boolean hollow,final boolean sphere,final int plusY) {
        List<Location> circleblocks = new ArrayList<>();
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();

        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int z = cz - radius; z <= cz + radius; z++) {
                for (int y = (sphere ? cy - radius : cy); y < (sphere ? cy
                        + radius : cy + height); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z)
                            + (sphere ? (cy - y) * (cy - y) : 0);

                    if (dist < radius * radius
                            && !(hollow && dist < (radius - 1) * (radius - 1))) {
                        Location l = new Location(loc.getWorld(), x, y + plusY,
                                z);
                        circleblocks.add(l);
                    }
                }
            }
        }

        return circleblocks;
    }
    public void useArmure(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, false, false));
        setResi(40);
        usingArmure = true;
        player.updateInventory();
    }
    public  void useSuspension(Player player) {
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setFlySpeed(0.1F);
        player.sendMessage(("Vous pouvez désormais voler !"));
        tookDamage = false;
        player.updateInventory();
        new TapisSableEffect(20*20, EnumParticle.REDSTONE, 255, 183, 0).start(player);

        new BukkitRunnable() {

            int timer = 20*20;

            @Override
            public void run() {
                if(tookDamage) {
                    player.sendMessage(("§cVotre pouvoir a été annulé."));
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    cancel();
                    return;
                }
                if(player.getGameMode() != GameMode.SPECTATOR) {
                    NMSPacket.sendActionBar(player, "§7Vous pouvez voler pendant encore §a" + timer/20 + " §fsecondes");

                    if(timer == 0){
                        player.setFlying(false);
                        player.setAllowFlight(false);
                        cancel();
                    }
                    timer--;
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }
    public enum Manipulation {
    	TSUNAMI,
    	AUCUN,
    	LANCE,
    	ARMURE,
    	SUSPENSION;
    }
    int sablenmb = 128;
    public void runPower(Player player) {
    	if (manipulation.equals(Manipulation.TSUNAMI)) {
    		if (!usingShukaku) {
    			if (sablenmb < 30) {
                    player.sendMessage(("§cIl vous faut un total de 30 sables pour utiliser ce pouvoir."));
                    return;
                }
    			sablenmb -= 30;
    			owner.sendMessage("§7Vous venez de perdre§e 30 sables§7, il ne vous en reste que "+sablenmb);
    		}else {
    			if (sablenmb < 15) {
                    player.sendMessage(("§cIl vous faut un total de 15 sables pour utiliser ce pouvoir."));
                    return;
                }
    			sablenmb -= 15;
    			owner.sendMessage("§7Vous venez de perdre§e 15 sables§7, il ne vous en reste que "+sablenmb);
    		}
            useTsunami(player);
    	}
    	if (manipulation.equals(Manipulation.LANCE)) {
    		if (!usingShukaku) {
    			if (sablenmb < 64) {
                    player.sendMessage(("§cIl vous faut un total de 64 sables pour utiliser ce pouvoir."));
                    return;
                }
    			sablenmb -= 64;
    			owner.sendMessage("§7Vous venez de perdre§e 64 sables§7, il ne vous en reste que "+sablenmb);
    		}else {
    			if (sablenmb < 32) {
                    player.sendMessage(("§cIl vous faut un total de 32 sables pour utiliser ce pouvoir."));
                    return;
                }
    			sablenmb -= 32;
    			owner.sendMessage("§7Vous venez de perdre§e 32 sables§7, il ne vous en reste que "+sablenmb);
    		}
            useLance(player);
    	}
    	if (manipulation.equals(Manipulation.ARMURE)) {
    		if (!usingArmure) {
    			if (!usingShukaku) {
        			if (sablenmb < 32) {
                        player.sendMessage(("§cIl vous faut un total de 32 sables pour utiliser ce pouvoir."));
                        return;
                    }
        			sablenmb -= 32;
        			owner.sendMessage("§7Vous venez de perdre§e 32 sables§7, il ne vous en reste que "+sablenmb);
        		}else {
        			if (sablenmb < 16) {
                        player.sendMessage(("§cIl vous faut un total de 16 sables pour utiliser ce pouvoir."));
                        return;
                    }
        			sablenmb -= 16;
        			owner.sendMessage("§7Vous venez de perdre§e 16 sables§7, il ne vous en reste que "+sablenmb);
        		}
                useArmure(player);
    		}else {
    			owner.sendMessage("Vous êtes déjà sous l'effet de votre§e Armure de Sable");
    		}    		
    	}
    	if (manipulation.equals(Manipulation.SUSPENSION)) {
    		if (!usingShukaku) {
    			if (sablenmb < 96) {
                    player.sendMessage(("§cIl vous faut un total de 96 sables pour utiliser ce pouvoir."));
                    return;
                }
    			sablenmb -= 96;
    			owner.sendMessage("§7Vous venez de perdre§e 96 sables§7, il ne vous en reste que "+sablenmb);
    		}else {
    			if (sablenmb < 48) {
                    player.sendMessage(("§cIl vous faut un total de 48 sables pour utiliser ce pouvoir."));
                    return;
                }
    			sablenmb -= 48;
    			owner.sendMessage("§7Vous venez de perdre§e 48 sables§7, il ne vous en reste que "+sablenmb);
    		}
            useSuspension(player);
    	}
    }
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				Attaque(),
				Defense()
		};
	}
}