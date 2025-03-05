package fr.nicknqck.entity.krystalbeast;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Setter
public abstract class Beast implements IBeast{

    @Getter
    private int minTiming;
    @Getter
    private int maxTiming;
    private final UUID uuid;
    private final int timingProc;

    protected Beast() {
        this.uuid = UUID.randomUUID();
        this.timingProc = Main.RANDOM.nextInt((getMaxTiming() - getMinTiming() +1)+getMinTiming());
    }

    public ItemStack getItemStack() {
        return getItemBuilder().setLore(getLore()).toItemStack();
    }

    @Override
    public boolean checkCanSpawn() {
        if (getOriginSpawn() == null) {
            return false;
        }
        if (getName() == null) {
            return false;
        }
        if (getName().isEmpty()) {
            return false;
        }
        if (GameState.getInstance().getInGameTime() != getTimingProc()) {
            return false;
        }
        return spawn();
    }

    @Override
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public final int getTimingProc() {
        return this.timingProc;
    }

    private String[] getLore() {
        return new String[] {
                "",
                "§fTemp minimal d'apparition:§c "+StringUtils.secondsTowardsBeautiful(getMinTiming()),
                "§fTemp maximal d'apparition:§c "+StringUtils.secondsTowardsBeautiful(getMaxTiming()),
        };
    }

}