package fr.nicknqck.roles.valo.agents;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Iso extends RoleBase {
    public Iso(Player player, GameState.Roles roles, GameState gameState) {
        super(player, roles, gameState);
        owner.sendMessage(Desc());
        giveItem(owner, false, getItems());
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
                AllDesc.point+"§dProtection couplé§f: A l'activation crée un timer de§c 15 secondes§f durant lesquelles tout les coups que vous infligerez au un joueur seront comptabilisé, après ce temp la vous pourrez esquiver un nombre de coup équivalant à ceux que vous aviez accumulé.§7 (1x/7min)",
                "",
                AllDesc.point+"§5Barrière§f: Vous permet de tier un laser de particule, les joueurs touchés auront un effet différent en fonction du clique utiliser: ",
                AllDesc.tab+"§cClique gauche§f: Les joueurs touchés par le laser subiront§c +25%§f de dégat de votre pars pendant§c 1 minute§f.§7 (1x/5m)",
                AllDesc.tab+"§aClique droit§f: Les joueurs touchés par le laser ne pourront plus vous tapez durant les§c 10 secondes§f qui suive.§7 (1x/7m)",
                "",
                AllDesc.point+"Ultime: duel: En visant un joueur, vous permet de vous téléportez avec lui dans un§c 1v1§f dans une arène, le gagnant remporte le nombre de pomme d'or que possédait le perdant (avant la téléportation).§7 (1x/10min)",
                "",
                AllDesc.bar
        };
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{

        };
    }

    @Override
    public void resetCooldown() {

    }
}
