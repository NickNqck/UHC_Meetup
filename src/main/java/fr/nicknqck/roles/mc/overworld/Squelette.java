package fr.nicknqck.roles.mc.overworld;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.mc.builders.OverWorldRoles;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.Loc;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Squelette extends OverWorldRoles {

    private final ItemStack KorosuItem = new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 4).setLore("§71 flèche sur deux que vous tirez ira directement sur votre cible").setName("§aKorosu shi no yumi").toItemStack();
    private final ItemStack BontoutouItem = new ItemBuilder(Material.BONE).addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().setName("§aBon TouTou").setLore("§7Vous permez d'apprivoiser un loup").toItemStack();
    private int KorosuCount = 0;
    private boolean ZombieSound = false;

    public Squelette(UUID player) {
        super(player);
    }

    @Override
    public String[] Desc() {
        return new String[]{
                AllDesc.bar,
                AllDesc.role + "§aSquelette",
                AllDesc.objectifteam + "§aOverWorld",
                "",
                AllDesc.items,
                "",
                AllDesc.point + "§aKorosu shi no yumi :§r Un arc §1power 4",
                "",
                AllDesc.particularite,
                "",
                "1 flèche sur deux que vous tirerez avec §aKorosu shi no yumi§r touchera directement le joueur le plus proche",
                "La prémière fois que vous croiserez le joueur possédant le rôle zombie vous entendrez un bruit de zombie",
                "",
                AllDesc.bar,

        };
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.Squelette;
    }

    @Override
    public String getName() {
        return "Squelette";
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
                KorosuItem,
                BontoutouItem
        };
    }

    @Override
    public void resetCooldown() {

    }

    @Override
    public void PlayerKilled(Player killer, Player victim, GameState gameState) {
        if (gameState.getInGamePlayers().contains(killer)) {
            if (victim.getUniqueId() == owner.getUniqueId()) {
                giveItem(killer, false, BontoutouItem);
                killer.sendMessage("Vous venez de tuez §a" + owner.getName() + " §rqui était §aSquelette §r,vous obtenez donc un os §aBon TouTou §rqui vous permez d'apprivoiser un loup");
            }
        }
        super.PlayerKilled(killer, victim, gameState);
    }


    @Override
    public void onProjectileLaunch(Projectile entity, Player shooter) {
        if (shooter.getUniqueId().equals(owner.getUniqueId())) {
            if (entity instanceof Arrow) {
                if (KorosuCount == 0) KorosuCount++;
                else {
                    Player target = null;
                    for (Player onlinePlayer : shooter.getWorld().getPlayers()) {
                        if (!onlinePlayer.getGameMode().equals(GameMode.SURVIVAL)) continue;
                        if (target == null) target = onlinePlayer;
                        double distance = shooter.getLocation().distance(target.getLocation());
                        if (distance > shooter.getLocation().distance(onlinePlayer.getLocation()))
                            target = onlinePlayer;
                    }
                    if(target == null) return;
                    entity.setVelocity(target.getLocation().toVector().subtract(entity.getLocation().toVector()));
                    KorosuCount = 0;
                }
            }
        }
        super.onProjectileLaunch(entity, shooter);
    }

    @Override
    public void onAllPlayerMoove(PlayerMoveEvent e, Player moover) {
        if (moover.getUniqueId() == owner.getUniqueId()) {
            if (!ZombieSound) {
                for (Player p : Loc.getNearbyPlayersExcept(owner, 15)) {
                    if (getPlayerRoles(p) instanceof Zombie) {
                        playSound(owner, "entity.zombie.ambient");
                        ZombieSound = true;
                    }
                }
            }
        }
        super.onAllPlayerMoove(e, moover);
    }
}
