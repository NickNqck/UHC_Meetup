package fr.nicknqck.config;

import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GameConfig {
    @Getter
    private static GameConfig instance;
    private int WaterEmptyTiming = 30;
    private int LavaEmptyTiming = 30;
    private boolean minage = false;
    private final List<ItemStack> itemOnKill;
    private final StuffConfig stuffConfig;
    private boolean laveTitans = true;
    private boolean BijusEnable = false;
    private boolean stuffUnbreak = true;
    private boolean pvpEnable = false;
    private int maxTimeDay = 60*5;
    private int critPercent = 20;
    private boolean giveLame = false;
    private int timingAssassin = 30;
    private int infectionTime = 60;
    private int groupe = 5;
    private int tridiCooldown = 16;

    public GameConfig() {
        instance = this;
        this.itemOnKill = new ArrayList<>();
        itemOnKill.add(new ItemBuilder(Material.GOLDEN_APPLE).setAmount(2).toItemStack());
        this.stuffConfig = new StuffConfig();
    }

    @Getter
    @Setter
    public final static class StuffConfig {

        private int protectionBoost = 2;
        private int protectionLeggings = 3;
        private int protectionChestplate = 2;
        private int protectionHelmet = 2;
        private int nmbArrow = 24;

        private int nmbGap = 12;
        private int minGap = 12;

    }
}
