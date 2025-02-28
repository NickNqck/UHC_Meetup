package fr.nicknqck.roles.ds.demons.lune;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class GyutaroV2 extends DemonsRoles {

    private static boolean Passif = false;
    private DakiV2 daki;
    private ItemStack FauxItem = new ItemBuilder(Material.DIAMOND_SWORD).setName("§cFaux Démoniaques").setLore("§7Inflige Wither quand vous tapez un joueur").addEnchant(Enchantment.DAMAGE_ALL, 3).toItemStack();

    public GyutaroV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull DemonType getRank() {
        return DemonType.SUPERIEUR;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Gyutaro";
    }

    @Override
    public GameState.@NonNull Roles getRoles() {
        return GameState.Roles.Gyutaro;
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Demon;
    }

    @Override
    public void resetCooldown() {

    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    private static class RappelPower extends ItemPower {

        private final GyutaroV2 gyutaroV2;

        protected RappelPower(@NonNull GyutaroV2 role) {
            super("§cRappel", new Cooldown(60 * 10), new ItemBuilder(Material.NETHER_STAR), role, "§7Vous permez de vous téléporter à §cDaki §7si elle est présente dans un rayon de §c50 blocs§7 autour de vous et qu'elle possède moins de 7❤§7.");
            this.gyutaroV2 = role;
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            if (gyutaroV2.daki == null) {
                player.sendMessage("§cDaki§7 n'est pas dans la partie...");
                return false;
            }
            Player daki = Bukkit.getPlayer(gyutaroV2.daki.getPlayer());
            if (daki == null) {
                player.sendMessage("§cDaki n'est pas connecter");
                return false;
            }
            if (!player.getWorld().equals(daki.getWorld())) {
                player.sendMessage("§cDaki§7 ne peut pas être téléportée pour l'instant.");
                return false;
            }
            final double distance = player.getLocation().distance(daki.getLocation());
            if (distance <= 50.0) {
                Location loc = Loc.getRandomLocationAroundPlayer(player, 5);
                daki.teleport(loc);
                player.sendMessage("§7Vous venez de Téléportez §cDaki §7à vous.");
                daki.sendMessage("§cGyutaro §7vous téléportez sur lui.");
            }
            return true;
        }
    }
    private static class PassifCommandPower extends CommandPower {

        public PassifCommandPower(@NonNull RoleBase role) {
            super("/ds faux", "faux", new Cooldown(0), role, CommandType.DS, "§7Vous permez d'activer/désactiver votre passif. (Desactiver par défauts)");
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