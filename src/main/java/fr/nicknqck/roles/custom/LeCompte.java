package fr.nicknqck.roles.custom;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LeCompte extends CustomRolesBase {
    private final TextComponent automaticDesc;
    public LeCompte(Player player) {
        super(player);
        AutomaticDesc desc = new AutomaticDesc(this);
        desc.addCommand(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("\n§7Vous permet d'obtenir petit-à-petit des informations sur le joueur cibler dans cette ordre là:\n\n"
        +AllDesc.point+"§a1 minutes§7: Vous obtenez le camp du joueur\n"+
        AllDesc.point+"§a2 minutes§7: Vous obtenez le rôle du joueur\n" +
        AllDesc.point+"§a3 minutes§7: Vous obtenez le nombre de§e pomme d'or§7 de la cible, de plus, vous en obtiendrez§c 5§7 en le tuant")}),"§c/c inspection <joueur>", 60*10);
        this.automaticDesc = desc.getText();
    }

    @Override
    public String getName() {
        return "Le Compte";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.LeCompte;
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Solo;
    }

    @Override
    public void resetCooldown() {

    }

    @Override
    public String[] Desc() {
        return new String[] {

        };
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[] {

        };
    }

}
