package fr.nicknqck.roles.mc.solo;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class Warden extends RoleBase {

    private final ItemStack sword = new ItemBuilder(Material.DIAMOND_SWORD).setUnbreakable(true).setName("").toItemStack();

    public Warden(Player player, GameState.Roles roles, GameState gameState) {
        super(player, roles, gameState);
        giveHealedHeartatInt(5);
        addBonusResi(10.0);
    }

    @Override
    public String[] Desc() {
        return new String[]{
                AllDesc.bar,
                AllDesc.role+"§9Warden",
                AllDesc.objectifsolo+"§e Seul",
                "",
                AllDesc.effet,
                "",
                AllDesc.point+"§cForce I§f,§6 Résistance au feu I§f et§c 5"+AllDesc.coeur+" permanent supplémentaire",
                "",
                AllDesc.items,
                "",

        };
    }

    @Override
    public void Update(GameState gameState) {
        givePotionEffet(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, false);
        givePotionEffet(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1, false);
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
                sword
        };
    }

    @Override
    public void resetCooldown() {

    }
}
