package fr.nicknqck.utils.powers;

import fr.nicknqck.interfaces.Buyable;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CommonBuyablePower implements Buyable {
    @Override
    public @NonNull Class<? extends RoleBase> getAssossiatedClass() {
        return RoleBase.class;
    }

    @Override
    public @NonNull ItemStack getMenuItem() {
        return new ItemBuilder(Material.IRON_INGOT)
                .setName("§fChance§d cristalline")
                .setLore(
                        "§7Une fois acheté, vous aurez§c 15% de chance§7 de ne pas perdre",
                        "§7de§a durabilité§7 lorsque vous utilisez un objet",
                        "",
                        "§fCoût:§d "+getCrystalCost()+" cristaux"
                )
                .toItemStack();
    }

    @Override
    public @NonNull Power createPower(@NonNull RoleBase role) {
        return new ChanceCristalline(role);
    }

    @Override
    public int getCrystalCost() {
        return 3;
    }

    private static final class ChanceCristalline extends Power implements Listener {

        public ChanceCristalline(@NonNull RoleBase role) {
            super("§fChance§d cristalline", null, role,
                    "§7Vous aurez§c 15% de chance§7 de ne pas perdre",
                    "§7de§a durabilité§7 lorsque vous utilisez un objet"
            );
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (map.isEmpty()) {
                return RandomUtils.getOwnRandomProbability(15.0);
            }
            return false;
        }
        @EventHandler
        public void onItemDamage(PlayerItemDamageEvent event) {
            final Player player = event.getPlayer();
            if (!player.getUniqueId().equals(getRole().getPlayer()))return;
            // 15% de chance de ne pas perdre de durabilité
            if (checkUse(player, new HashMap<>())) {
                event.setCancelled(true);
            }
        }
    }
}