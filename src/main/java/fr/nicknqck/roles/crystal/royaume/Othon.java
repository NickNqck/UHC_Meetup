package fr.nicknqck.roles.crystal.royaume;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.CrystalFaction;
import fr.nicknqck.enums.CrystalRoles;
import fr.nicknqck.events.custom.CrystalUHCReputationObtainEvent;
import fr.nicknqck.events.custom.ForcePatchEvent;
import fr.nicknqck.interfaces.IGotReputation;
import fr.nicknqck.interfaces.IRoles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Othon extends RoyaumeBase {

    public Othon(UUID player) {
        super(player);
    }

    @Override
    public void onRoleGive(@NonNull GameState gameState) {
        addPower(new EpeeDeNoblePower(this), true);
        addPower(new MenteauDeNoblePower(this));
    }

    @Override
    public @NonNull CrystalFaction getCrystalFaction() {
        return CrystalFaction.ROYAL;
    }

    @Override
    public String getName() {
        return "Othon";
    }

    @Override
    public @NonNull IRoles<?> getRoles() {
        return CrystalRoles.Othon;
    }

    @Override
    public @NonNull TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

    private static final class EpeeDeNoblePower extends ItemPower implements Listener {

        public EpeeDeNoblePower(@NonNull RoleBase role) {
            super("§aÉpée de noble§r", null, new ItemBuilder(Material.DIAMOND_SWORD).setName("§aÉpée de noble").addEnchant(Enchantment.DAMAGE_ALL, 4), role,
                    "§7Tant que vous avez cette§a épée§7 en§a main§7, votre§c réputation§7 sera perçu comme étant§c plus haute§7."
            );
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            return true;
        }
        @EventHandler
        private void onReputation(@NonNull final CrystalUHCReputationObtainEvent event) {
            if (!event.getCrystalRole().getPlayer().equals(getRole().getPlayer()))return;
            final Player player = event.getCrystalRole().getGamePlayer().getPlayer();
            if (player == null)return;
            if (player.getItemInHand() == null)return;
            if (player.getItemInHand().isSimilar(getItem())) {
                event.setReputation(event.getReputation()+1);
            }
        }
    }
    private static final class MenteauDeNoblePower extends Power implements Listener{

        public MenteauDeNoblePower(@NonNull RoleBase role) {
            super("§aManteau de Noble", null, role,
                    "§7Tant que votre§c réputation§7 est au dessus de§c 6§7, vous serez immunisé aux dégâts de§c force§7."
            );
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            return false;
        }
        @EventHandler(priority = EventPriority.HIGHEST)
        private void onDamage(@NonNull final ForcePatchEvent event) {
            if (!event.getGameVictim().getUuid().equals(getRole().getPlayer()))return;
            if (!checkUse(event.getGameVictim().getPlayer(), new HashMap<>()))return;
            if (!(getRole() instanceof IGotReputation))return;
            event.setForcePercentToUse(1.0);
        }
    }

}