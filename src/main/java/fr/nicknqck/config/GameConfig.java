package fr.nicknqck.config;

import fr.nicknqck.enums.MDJ;
import fr.nicknqck.invs.MDJ_AOT_Config;
import fr.nicknqck.invs.MDJ_DS_Config;
import fr.nicknqck.invs.MDJ_NS_Config;
import fr.nicknqck.utils.fastinv.FastInv;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

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
    private final NarutoConfig narutoConfig;
    private boolean laveTitans = true;
    private boolean stuffUnbreak = true;
    private boolean pvpEnable = false;
    private int maxTimeDay = 60*3;
    private int critPercent = 20;
    private boolean giveLame = false;
    private int timingAssassin = 30;
    private int infectionTime = 60;
    private int groupe = 5;
    private int tridiCooldown = 16;
    private int forcePercent = 30;
    private int resiPercent = 20;
    private StunType stunType = StunType.TELEPORT;
    private boolean mortEclair = true;
    private boolean tntGrief = false;
    private final LinkedHashMap<MDJ, Class<? extends FastInv>> configurablesMdj;
    private boolean rodTridimenssionel = false;

    public GameConfig() {
        instance = this;
        this.itemOnKill = new ArrayList<>();
        itemOnKill.add(new ItemBuilder(Material.GOLDEN_APPLE).setAmount(2).toItemStack());
        this.stuffConfig = new StuffConfig();
        this.narutoConfig = new NarutoConfig();
        this.configurablesMdj = new LinkedHashMap<>();
        configurablesMdj.put(MDJ.DS, MDJ_DS_Config.class);
        configurablesMdj.put(MDJ.AOT, MDJ_AOT_Config.class);
        configurablesMdj.put(MDJ.NS, MDJ_NS_Config.class);
    }

    @Getter
    @Setter
    public final static class StuffConfig {

        private int protectionBoost = 2;
        private int protectionLeggings = 3;
        private int protectionChestplate = 2;
        private int protectionHelmet = 2;
        private int nmbArrow = 16;
        private int sharpness = 3;
        private int nmbblock = 1;
        private int power = 2;
        private int pearl = 0;
        private int eau = 2;
        private int lave = 2;

        private int nmbGap = 20;
        private int minGap = 12;

    }
    @Getter
    @Setter
    public final static class NarutoConfig {

        private double edoHealthRemove = 4.0;
        private int minTimeSpawnBiju = 90;
        private int maxTimeSpawnBiju = 160;

    }
    public enum StunType {
        TELEPORT("Téléportation", "§a"),
        STUCK("Anti-Déplacement", "§c");

        @Getter
        private final String name;
        @Getter
        private final String color;

        StunType(String name, String color) {
            this.name = name;
            this.color = color;
        }
    }
}