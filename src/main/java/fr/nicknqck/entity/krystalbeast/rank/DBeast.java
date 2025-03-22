package fr.nicknqck.entity.krystalbeast.rank;

import fr.nicknqck.entity.krystalbeast.creator.BeastCreator;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class DBeast extends BeastCreator {

    private final List<ItemStack> defaultLoot;

    protected DBeast() {
        this.defaultLoot = new ArrayList<>();
        this.defaultLoot.add(new ItemBuilder(Material.GOLD_NUGGET).setAmount(8).toItemStack());
        this.defaultLoot.add(new ItemBuilder(Material.GOLD_INGOT).setAmount(3).toItemStack());
    }

    @Override
    public BeastRank getBeastRank() {
        return BeastRank.D;
    }
}