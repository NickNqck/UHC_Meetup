package fr.nicknqck.events.custom.beast;

import fr.nicknqck.entity.krystalbeast.Beast;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

@Getter
public class BeastDeathEvent extends BeastEvent {

    private final List<ItemStack> drops;
    private final Player killer;

    public BeastDeathEvent(@NonNull Beast beast,@NonNull List<ItemStack> drops,@Nullable Player killer) {
        super(beast);
        this.drops = drops;
        this.killer = killer;
    }
}
