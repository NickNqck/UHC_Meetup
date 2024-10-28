package fr.nicknqck.roles.ds.demons.lune;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class GyutaroV2 extends DemonsRoles {

    public static boolean Passif = false;

    public GyutaroV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull DemonType getRank() {
        return null;
    }

    @Override
    public String[] Desc() {
        return new String[0];
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
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    private static class RappelPower extends ItemPower {
        protected RappelPower(@NonNull RoleBase role) {
            super("§cRappel", new Cooldown(60*10), new ItemBuilder(Material.NETHER_STAR), role, "§7Vous permez de vous téléporter à §cDaki §7si elle est présente dans un rayon de 50 blocs autour de vous et qu'elle possède moins de 7 "+ AllDesc.coeur+"§7.");
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {

            return true;
        }
    }
    private static class PassifPower extends CommandPower {
        public PassifPower(@NonNull RoleBase role) {
            super("/ds faux", "faux", new Cooldown(0), role, CommandType.DS, "§7Vous permez d'activer/désactiver votre passif.");
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            if (!Passif) {
                Passif = true;
                player.sendMessage("§7Vous venez d'activer votre passif.");
            } else {
                Passif = false;
                player.sendMessage("§7Vous venez de désactiver votre passif.");
            }
            return false;
        }
    }
}
