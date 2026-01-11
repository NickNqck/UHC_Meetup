package fr.nicknqck.roles.krystal;

import fr.nicknqck.enums.MDJ;
import lombok.NonNull;
import org.bukkit.entity.Player;

public interface IMDJConfig {

    @NonNull
    MDJ getMDJ();
    void openConfigInventory(@NonNull final Player player);


}