package fr.nicknqck.roles.aot.soldats;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.Loc;

public class Jean extends RoleBase{

	public Jean(Player player, Roles roles, GameState gameState) {
		super(player, roles, gameState);
		owner.sendMessage(Desc());
		gameState.GiveRodTridi(owner);
	}
	@Override
	public String[] Desc() {
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"Jean",
				"",
				AllDesc.commande,
				"",
				AllDesc.point+"§6§l/aot fuse§f: Envoie une fusée dont la couleur change en fonction de s'il y à des ennemies dans les 25blocs autours de vous ou pas s'il n'y en à pas du tout la couleur sera§a verte§f sinon ce sera§c rouge§f (§6tout joueur n'étant pas dans le camp§a Soldat§6 à l'exeption de§l Eren, Gabi et Jelena§6 seront considéré comme étant des ennemies)",
				AllDesc.bar
		};
	}
	private int actualuse = 0;
	private void createFirework(Player player, Color color) {
        Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        FireworkEffect.Builder builder = FireworkEffect.builder();

        builder.withColor(color);
        builder.with(FireworkEffect.Type.BALL);

        meta.addEffect(builder.build());
        meta.setPower(1);
        firework.setFireworkMeta(meta);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                firework.detonate();
            }
        }.runTaskLater(Main.getInstance(), 20L); // 20L équivaut à une seconde
    }
	@Override
	public void onAotCommands(String arg, String[] args, GameState gameState) {
		if (args[0].equalsIgnoreCase("fuse")) {
			if (actualuse <3) {
				List<Player> mechant = new ArrayList<>();
				List<Player> inZone = new ArrayList<>();
				for (Player p : Loc.getNearbyPlayers(owner, 25)) {//Pour chaque joueur étant à moins de 25 blocs du l'owner du rôle
					if (!gameState.hasRoleNull(p)) {//si ce même joueur possède un rôle
						if (getPlayerRoles(p).type != Roles.Gabi && getPlayerRoles(p).type != Roles.Eren && getPlayerRoles(p).type != Roles.Jelena) {//S'il n'est pas Gabi ou Eren ou Jelena
							if (getPlayerRoles(p).getTeam() != TeamList.Soldat) {//S'il n'est pas dans la team Soldat
								mechant.add(p);//alors il est ajouté en temp que méchant
							}
						}
						inZone.add(p);
					}
				}
				actualuse+=1;
				owner.sendMessage("§7Votre fusée sera lancée au yeux de tous dans§c 15s");
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
					if (mechant.size() > 0) {
						createFirework(owner, Color.RED);
						setResi(20);
						givePotionEffet(owner, PotionEffectType.DAMAGE_RESISTANCE, 20*(60*5), 1, true);
						owner.sendMessage("§7Vous avez gagnez l'effet "+AllDesc.Resi+" pendant§6 5minutes");
					}else {
						createFirework(owner, Color.GREEN);
					}
					owner.sendMessage("§7Voici la liste des joueurs étant dans le rayon de la fusée quand vous avez fait la commande: ");
					inZone.forEach(e -> owner.sendMessage("§7 -§l "+e.getName()));
				}, 20*15);//20(tick)*15(seconde)
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
					if (mechant.size() > 0) {
						setResi(0);
						owner.sendMessage("§7Vous venez de perdre votre effet de "+AllDesc.Resi);
					}
				}, 20*(60*5));//20(tick)*60*5(seconde)	
			}else {
				
			}
		}
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[0];
	}
	@Override
	public void resetCooldown() {
		actualuse = 0;
	}
}