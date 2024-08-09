package fr.nicknqck.roles.mc.overworld;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class AraigneeVenimeuse extends RoleBase {

    private final ItemStack ToileItem = new ItemBuilder(Material.WEB).setName("§aToile d'araignée").setLore("§7Vous permez de poser une toile d'araigée sous un joueur").addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).hideAllAttributes().toItemStack();
    private boolean poison = false;

    public AraigneeVenimeuse(UUID player) {
        super(player);
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
            ToileItem,
        };
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public GameState.Roles getRoles() {
        return null;
    }

    @Override
    public TeamList getOriginTeam() {
        return null;
    }

    @Override
    public void resetCooldown() {

    }

    @Override
    public void onALLPlayerDamageByEntity(EntityDamageByEntityEvent event, Player victim, Entity entity) {
        if (entity.getUniqueId() == owner.getUniqueId()){
        /*    if(owner.getItemInHand()){
                if (poison){
                    givePotionEffet(victim, PotionEffectType.POISON, 2,1,true);
                }
            }*/
        }
        super.onALLPlayerDamageByEntity(event, victim, entity);
    }
}
