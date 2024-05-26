package fr.nicknqck.roles.mc.overworld;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;

public class Squelette extends RoleBase {

    private final ItemStack KorosuItem = new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 4).setLore("§71 flèche sur deux que vous tirez ira directement sur votre cible").setName("§aKorosu shi no yumi").toItemStack();
    private final ItemStack BontoutouItem = new ItemBuilder(Material.BONE).addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().setName("§aBon TouTou").setLore("§7Vous permez d'apprivoiser un loup").toItemStack();
    private int KorosuCount = 0;
    public Squelette(Player player, GameState.Roles roles) {
        super(player, roles);
    }

    @Override
    public String[] Desc() {
        return new String[]{
                AllDesc.bar,
                AllDesc.role+"§aSquelette",
                AllDesc.objectifteam+"§aOverWorld",
                "",
                AllDesc.items,
                "",
                AllDesc.point+"§aKorosu shi no yumi :§r Un arc §1power 4",
                "",
                AllDesc.particularite,
                "",
                "1 flèche sur deux que vous tirerez avec §aKorosu shi no yumi§r touchera directement le joueur le plus proche",
                "",
                AllDesc.bar,

        };
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
        if (getIGPlayers().contains(killer)){
            if (victim.getUniqueId() == owner.getUniqueId()){
                giveItem(killer, false, BontoutouItem);
                killer.sendMessage("Vous venez de tuez §a"+owner.getName()+" §rqui était §aSquelette §r,vous obtenez donc un os §aBon TouTou §rqui vous permez d'apprivoiser un loup");
            }
        }
        super.PlayerKilled(killer, victim, gameState);
    }



    @Override
    public void onProjectileLaunch(Projectile entity, Player shooter) {
        if (shooter.getUniqueId().equals(owner.getUniqueId())){
            if (entity instanceof Arrow){
                if (KorosuCount == 0){
                    KorosuCount++;
                }
            }
        }
        super.onProjectileLaunch(entity, shooter);
    }
}
