package fr.nicknqck.roles.ns.akatsuki;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Nagato extends RoleBase {
    private final ItemStack ShuradoItem = new ItemBuilder(Material.DIAMOND_SWORD).addEnchant(Enchantment.DAMAGE_ALL, 4).setName("§7Shuradô").setLore("§7").toItemStack();
    public Nagato(Player player, GameState.Roles roles, GameState gameState) {
        super(player, roles, gameState);
        setChakraType(Chakras.SUITON);
        player.sendMessage(Desc());
    }
// /ns jigokudo <joueur> S'il est proche donne le rôle de la cible a Nagato et la première fois qu'ils se battent après sa on inflige 3c de base à la cible
    @Override
    public void GiveItems() {
        super.GiveItems();
        super.giveItem(owner, false, getItems());
    }

    @Override
    public String[] Desc() {
        return new String[]{
                AllDesc.bar,
                AllDesc.role+"§cNagato",
                AllDesc.objectifteam+"§cAkatsuki",
                "",
                AllDesc.items,
                "",
                AllDesc.point+"§7Shuradô§f: Juste une épée en diamant§7 Tranchant IV§f."
        };
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
                ShuradoItem
        };
    }

    @Override
    public void resetCooldown() {

    }
}
