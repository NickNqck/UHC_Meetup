package fr.nicknqck.events.custom.biju;

import com.avaje.ebean.validation.NotNull;
import fr.nicknqck.entity.bijuv2.BijuBase;
import lombok.Getter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.Inventory;

public class InventoryClickBijuEvent extends BijuEvent implements Cancellable {

    @Getter
    private final boolean isInventoryItem;
    @NotNull
    @Getter
    private final Inventory inventory;
    @NotNull
    @Getter
    private final HumanEntity whoClicked;
    private boolean cancelled = false;

    public InventoryClickBijuEvent(BijuBase biju, boolean isInventoryItem, Inventory inventory, HumanEntity whoClicked) {
        super(biju);
        this.isInventoryItem = isInventoryItem;
        this.inventory = inventory;
        this.whoClicked = whoClicked;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
