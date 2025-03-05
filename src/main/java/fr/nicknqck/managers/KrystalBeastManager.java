package fr.nicknqck.managers;

import fr.nicknqck.entity.krystalbeast.Beast;
import fr.nicknqck.entity.krystalbeast.beast.Lijen;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

public class KrystalBeastManager {

    private final Map<Class<? extends Beast>, Integer> beastMap;

    public KrystalBeastManager() {
        this.beastMap = new HashMap<>();
        initOriginBeastList();
    }

    public void initOriginBeastList() {
        addPlayableBeast(Lijen.class);
    }

    public void addPlayableBeast(@NonNull final Class<? extends Beast> beastClass) {
        if (isAlreadyPlayable(beastClass)) {
            int i = this.beastMap.get(beastClass);
            this.beastMap.remove(beastClass, i);
            i++;
            this.beastMap.put(beastClass, i);
        } else {
            this.beastMap.put(beastClass, 0);
        }
    }
    public boolean removePlayableBeast(@NonNull final Class<? extends Beast> beastClass) {
        int amount = getAmountReady(beastClass);
        if (amount <= 0)return false;
        if (this.isAlreadyPlayable(beastClass)) {
            this.beastMap.remove(beastClass, amount);
            amount--;
            this.beastMap.put(beastClass, amount);
            return true;
        }
        return false;
    }
    public boolean isAlreadyPlayable(@NonNull final Class<? extends Beast> beastClass) {
        return this.beastMap.containsKey(beastClass);
    }
    public int getAmountReady(@NonNull final Class<? extends Beast> beastClass) {
        if (isAlreadyPlayable(beastClass)) {
            return beastMap.get(beastClass);
        }
        return 0;
    }
}
