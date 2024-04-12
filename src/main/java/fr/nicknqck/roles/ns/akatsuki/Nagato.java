package fr.nicknqck.roles.ns.akatsuki;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.Loc;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Nagato extends RoleBase {
    private final ItemStack ShuradoItem = new ItemBuilder(Material.DIAMOND_SWORD).addEnchant(Enchantment.DAMAGE_ALL, 4).setName("§7Shuradô").setLore("§7").toItemStack();
    private int useJikogudo = 0;
    public Nagato(Player player, GameState.Roles roles, GameState gameState) {
        super(player, roles, gameState);
        setChakraType(Chakras.SUITON);
        player.sendMessage(Desc());
    }

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
    public void onNsCommand(String[] args) {
        super.onNsCommand(args);
        if (args[0].equalsIgnoreCase("jigokudo")){
            if (args.length == 2){
                if (useJikogudo > 2){
                    owner.sendMessage("§cVous avez utiliser le nombre maximum d'utilisation de Jigokudo (2)");
                    return;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null){
                    if (Loc.getNearbyPlayersExcept(owner, 15).contains(target)){
                        if (!gameState.hasRoleNull(target)){
                            owner.sendMessage(getTeamColor(target)+"§f possède le rôle: "+getPlayerRoles(target).type.getItem().getItemMeta().getDisplayName());
                            useJikogudo++;
                        }
                    }
                }
            }
        }
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
