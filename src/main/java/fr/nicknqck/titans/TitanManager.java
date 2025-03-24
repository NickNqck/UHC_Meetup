package fr.nicknqck.titans;

import fr.nicknqck.utils.event.EventUtils;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TitanManager implements Listener {

    private final Map<UUID, TitanBase> titansMap;

    public TitanManager() {
        this.titansMap = new HashMap<>();
        EventUtils.registerEvents(this);
    }

    public void addTitan(final UUID uuid, final TitanBase titan){
        this.titansMap.put(uuid, titan);
    }

}