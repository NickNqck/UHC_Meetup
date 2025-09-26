package fr.nicknqck.entity.krystalbeast;

import fr.nicknqck.entity.krystalbeast.rank.BeastRank;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.UUID;

public interface IBeast {

    Entity getBeast();
    Location getOriginSpawn();
    int getTimingProc();
    ItemBuilder getItemBuilder();
    BeastRank getBeastRank();
    boolean spawn();
    boolean checkCanSpawn();
    String getName();
    UUID getUniqueId();
    boolean hasSpawn();
    void setHasSpawn(boolean b);

}
