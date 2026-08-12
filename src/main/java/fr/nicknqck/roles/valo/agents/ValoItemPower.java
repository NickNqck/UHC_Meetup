package fr.nicknqck.roles.valo.agents;

import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import javax.annotation.Nonnull;
import java.util.Map;

public abstract class ValoItemPower extends ItemPower {

    public ValoItemPower(@NonNull String name, ItemBuilder item, @NonNull RoleBase role, String... description) {
        super(name, new Cooldown(1), item, role, description);
        getShowCdRunnable().setCustomText(true);
    }

    @Override
    public boolean isSendCooldown() {
        return false;
    }

    @Override
    public boolean isShowCdInDesc() {
        return false;
    }

    @Override
    public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
        if (getInteractType().equals(InteractType.INTERACT)) {
            final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
            if (event.getAction().name().contains("RIGHT")) {
                return this.getRightClickPower().checkUse(player, map);
            } else if (event.getAction().name().contains("LEFT")) {
                return this.getLeftClickPower().checkUse(player, map);
            }
        }
        return false;
    }

    @Nonnull
    public abstract Power getLeftClickPower();

    @Nonnull
    public abstract Power getRightClickPower();

    @Override
    public void tryUpdateActionBar() {
        getShowCdRunnable().setCustomTexte(this.getLeftClickPower().getName()+": "+
                (this.getLeftClickPower().getCooldown() != null ?
                        this.getLeftClickPower().getCooldown().isInCooldown() ?
                        "§c"+ StringUtils.secondsTowardsBeautiful(this.getLeftClickPower().getCooldown().getCooldownRemaining()) :
                        "§aUtilisable" :
                        "§aUtilisable"
                ) + "§7 | " +
                (this.getRightClickPower().getName()+": "+(
                        this.getRightClickPower().getCooldown() != null ?
                                this.getRightClickPower().getCooldown().isInCooldown() ?
                                "§c"+StringUtils.secondsTowardsBeautiful(this.getRightClickPower().getCooldown().getCooldownRemaining())
                                :
                                "§aUtilisable" :
                                "§aUtilisable"
                        ))
        );
    }
}