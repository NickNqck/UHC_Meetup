package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Shikamaru extends RoleBase {
    private final ItemStack stunItem = new ItemBuilder(Material.NETHER_STAR).setName("§aStun").setLore("§7Vous permet d'empêcher de bouger un joueur").toItemStack();
    private int cdStun = 0;
    public Shikamaru(Player player, GameState.Roles roles, GameState gameState) {
        super(player, roles, gameState);
        owner.sendMessage(Desc());
    }

    @Override
    public String[] Desc() {
        return new String[]{
                AllDesc.bar,
                AllDesc.role+"§aShikamaru",
                AllDesc.objectifteam+"§aShinobi",
                "",
                AllDesc.items,
                "",
                AllDesc.point+"§aStun§f: Ouvre un menu affichant tout les joueurs étant à moins de§c 25 blocs§f de vous, en sélectionnant un joueur, vous et le joueur viser ne pourrez plus bouger pendant§c 10 secondes§f.§7 (1x/5min)",
                "§c(Vous et le joueur viser pourrez prendre des dégats pendant le stun)"
        };
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
                stunItem
        };
    }

    @Override
    public void resetCooldown() {
        cdStun = 0;
    }

    @Override
    public boolean ItemUse(ItemStack item, GameState gameState) {
        if (item.isSimilar(stunItem)){

        }
        return super.ItemUse(item, gameState);
    }
}
