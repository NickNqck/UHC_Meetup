package fr.nicknqck.invs;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.scenarios.impl.AntiPvP;
import fr.nicknqck.utils.fastinv.FastInv;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import org.bukkit.Material;

public class FirstConfigurationInventory extends FastInv {

    public FirstConfigurationInventory() {
        super(54, "§7(§c!§7)§f Configuration");
        setItems(getCorners(), GameState.getInstance().gameCanLaunch ? new ItemBuilder(Material.STAINED_GLASS_PANE)
                .setName(" ")
                .setDurability(5)
                .toItemStack()
                :
                new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDurability(14)
                        .setName(" ")
                        .toItemStack());
        setItem(10, GUIItems.getSelectRoleButton());
        if (Main.getInstance().isGoodServer()) {
            setItem(12, new ItemBuilder(Material.DIAMOND_PICKAXE).setName("§cMinage").setLore("§7État: "+(Main.getInstance().getGameConfig().isMinage() ? "§aActivé" : "§cDésactiver")).toItemStack());
        }
        setItem(13, GUIItems.getPregen(GameState.getInstance()));
        setItem(16, new ItemBuilder(Material.GRASS).setName("§aChanger le monde de jeu").toItemStack());
        setItem(19, GUIItems.getSelectConfigButton());
        setItem(22, GameState.getInstance().gameCanLaunch ? GUIItems.getStartGameButton() : GUIItems.getCantStartGameButton());
        setItem(28, GUIItems.getSelectInvsButton());
        setItem(31, GUIItems.getSelectScenarioButton());
        setItem(37, GUIItems.getSelectEventButton());
        if (AntiPvP.isAntipvplobby()) {
            setItem(40, AntiPvP.getlobbypvp());
        } else {
            setItem(40, AntiPvP.getnotlobbypvp());
        }
        setItem(43, GUIItems.getCrit(GameState.getInstance()));
        setItem(53, GUIItems.getx());
    }
}