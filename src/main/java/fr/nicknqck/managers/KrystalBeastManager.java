package fr.nicknqck.managers;

import fr.nicknqck.entity.krystalbeast.Beast;
import fr.nicknqck.entity.krystalbeast.beast.Lijen;
import fr.nicknqck.utils.event.EventUtils;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class KrystalBeastManager implements Listener {

    private final Map<Class<? extends Beast>, Integer> beastMap;

    public KrystalBeastManager() {
        this.beastMap = new HashMap<>();
        initOriginBeastList();
        EventUtils.registerEvents(this);
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
    @EventHandler
    private void onInventoryClick(@NonNull final InventoryClickEvent event) {
        if (event.getCurrentItem() == null)return;
        if (event.getInventory() == null)return;
        if (event.getInventory().getTitle() == null)return;
        if (event.getInventory().getTitle().isEmpty())return;
        if (!event.getInventory().getTitle().equals("§fConfiguration§7 ->§d KrystalBeast"))return;
        final ItemStack item = event.getCurrentItem();
    }
}
