package fr.nicknqck.entity.krystalbeast.creator;

import  fr.nicknqck.Border;
import fr.nicknqck.Main;
import fr.nicknqck.entity.krystalbeast.Beast;
import fr.nicknqck.entity.krystalbeast.configurable.IConfigurable;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;

public abstract class BeastCreator extends Beast implements IBeastCreator, IConfigurable {

    @Override
    public Location getRandomLocation() {
        Location location = null;
        int essaie = 0;
        while (location == null && essaie <= 1000) {
            essaie++;
            location = new Location(Main.getInstance().getWorldManager().getGameWorld(),
                    Main.RANDOM.nextInt(Border.getMaxBorderSize()),
                    60,
                    Main.RANDOM.nextInt(Border.getMaxBorderSize()));
            if (location.getBlockX() >= (Border.getMaxBorderSize()-10)) {
                location = null;
                continue;
            }
            if (location.getBlockX() <= (-Border.getMaxBorderSize()+10)) {
                location = null;
                continue;
            }
            if (location.getBlockZ() >= (Border.getMaxBorderSize()-10)) {
                location = null;
                continue;
            }
            if (location.getBlockZ() <= (-Border.getMinBorderSize()+10)) {
                location = null;
            }
        }
        if (location == null) {//location par défaut x:100 z:100
            location = new Location(Main.getInstance().getWorldManager().getGameWorld(),
                    100,
                    0,
                    100);
        }
        location.setY(location.getWorld().getHighestBlockYAt(location)+1);
        return location;
    }

    @Override
    public void openBeastInventory(@NonNull HumanEntity human) {
        human.closeInventory();
        final String name = getName();
        final Inventory inv = Bukkit.createInventory(human, 27, "§dKrystalBeast§7 -> "+name);
        inv.setItem(12, new ItemBuilder(Material.WATCH).setName("§bTemp maximal").setLore(
                "",
                "§fClique gauche:§a +15s",
                "§fClique droit:§c -15s",
                "",
                "§bTemp actuel: "+ StringUtils.secondsTowardsBeautiful(getMaxTiming())
        ).toItemStack());
        inv.setItem(14, new ItemBuilder(Material.WATCH).setName("§bTemp minimal").setLore(
                "",
                "§fClique gauche:§a +15s",
                "§fClique droit:§c -15s",
                "",
                "§bTemp actuel: "+StringUtils.secondsTowardsBeautiful(getMinTiming())
        ).toItemStack());
        inv.setItem(26, GUIItems.getSelectBackMenu());
        human.openInventory(inv);
    }

    @Override
    public @NonNull String getInventoryName() {
        return "§dKrystalBeast§7 -> "+getName();
    }
}