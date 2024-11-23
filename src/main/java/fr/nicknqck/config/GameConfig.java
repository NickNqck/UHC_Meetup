package fr.nicknqck.config;

import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;


@Setter
public class GameConfig {
    @Getter
    private static GameConfig instance;
    @Getter
    private int WaterEmptyTiming = 30;
    @Getter
    private int LavaEmptyTiming = 30;
    @Getter
    @Setter
    private boolean minage = false;
    @Getter
    private final List<ItemStack> itemOnKill;
    @Getter
    private final StuffConfig stuffConfig;
    public GameConfig() {
        instance = this;
        this.itemOnKill = new ArrayList<>();
        itemOnKill.add(new ItemBuilder(Material.GOLDEN_APPLE).setAmount(2).toItemStack());
        this.stuffConfig = new StuffConfig();
    }
    private static class StuffConfig {



    }
}
