package fr.nicknqck.entity.krystalbeast.configurable;

import lombok.NonNull;
import org.bukkit.entity.HumanEntity;

public interface IConfigurable {

    void openBeastInventory(@NonNull final HumanEntity human);
    @NonNull String getInventoryName();

}
