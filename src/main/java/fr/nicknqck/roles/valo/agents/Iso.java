package fr.nicknqck.roles.valo.agents;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Iso extends RoleBase {
    public Iso(Player player, GameState.Roles roles, GameState gameState) {
        super(player, roles, gameState);
    }

    @Override
    public String[] Desc() {
        return new String[]{
                AllDesc.bar,
                AllDesc.role+"§5Iso",
                AllDesc.objectifsolo+"§5Solo",
                "",
                AllDesc.items,
                "",
                AllDesc.point+"§dSape§f: vous permet de tirer un laser de particule, les joueurs touchés par ce laser prendront§c +25%§f de dégat de votre pars.§7 (1x/5min)",
                "",
                AllDesc.point+"§dProtection couplé§f: A l'activation crée un timer de§c 15 secondes§f durant lesquelles tout les coups que vous infligerez au un joueur seront comptabilisé, après ce temp la vous pourrez esquiver un nombre de coup équivalant à ceux que vous aviez accumulé.§7 (1x/7min)",
                "",
                AllDesc.point+"§dContingence§f: Vous permet de tirer un laser de particule, les joueurs touchés ne pourront pas vous tapez pendant les§c 10 secondes§f qui suive.§7 (1x/7m)",
                "",
                AllDesc.point+""
        };
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public void resetCooldown() {

    }
}
