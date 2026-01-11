package fr.nicknqck.invs;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.fastinv.FastInv;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MDJ_DS_Config extends FastInv {

    public MDJ_DS_Config() {
        super(27, "§fConfiguration§7 ->§c Demon Slayer");
        setItems(getCorners(), new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        setItem(10, new ItemBuilder(Material.REDSTONE).setName("§fTemps avant l'§cAssassin").setLore(
                "§f10 secondes <§c "+ StringUtils.secondsTowardsBeautiful(Main.getInstance().getGameConfig().getTimingAssassin())+"§f > 5 minutes",
                "",
                "§fClique gauche:§a +10 secondes",
                "§fClique droit:§c -10 secondes"
        ).toItemStack(), event -> {
            if (event.isLeftClick()) {
                Main.getInstance().getGameConfig().setTimingAssassin(Math.min(60*5, Main.getInstance().getGameConfig().getTimingAssassin()+10));
            }
            if (event.isRightClick()) {
                Main.getInstance().getGameConfig().setTimingAssassin(Math.max(10, Main.getInstance().getGameConfig().getTimingAssassin()-10));
            }
            new MDJ_DS_Config().open((Player) event.getWhoClicked());
        });
        setItem(11, new ItemBuilder(Material.GHAST_TEAR).setName("§fTemps avant§c infection").setLore(
                "§f10 secondes <§c "+StringUtils.secondsTowardsBeautiful(Main.getInstance().getGameConfig().getInfectionTime())+"§f > 5 minutes",
                "",
                "§fClique gauche:§a +10 secondes",
                "§fClique droit:§c -10 secondes"
        ).toItemStack(), event -> {
            if (event.isLeftClick()) {
                Main.getInstance().getGameConfig().setInfectionTime(Math.min(60*5, Main.getInstance().getGameConfig().getInfectionTime()+10));
            }
            if (event.isRightClick()) {
                Main.getInstance().getGameConfig().setInfectionTime(Math.max(10, Main.getInstance().getGameConfig().getInfectionTime()-10));
            }
            new MDJ_DS_Config().open((Player) event.getWhoClicked());
        });
        setItem(12, new ItemBuilder(Material.NETHER_STAR).setName("§fDon de lame").setLore(
                "§7Lorsque ceci est§a activer§7 les joueurs le pouvant reçoive une§a Lame de Nichirin§7.",
                "",
                "§7Fonctionnalité actuellement: "+(Main.getInstance().getGameConfig().isGiveLame() ? "§aActiver" : "§cDésactiver")
        ).toItemStack(), event -> {
            Main.getInstance().getGameConfig().setGiveLame(!Main.getInstance().getGameConfig().isGiveLame());
            new MDJ_DS_Config().open((Player) event.getWhoClicked());
        });
        setItem(26, GUIItems.getSelectBackMenu(), event -> {
            if (GameState.getInstance().isAllMdjNull()) {
                new MDJConfigInventory().open((Player) event.getWhoClicked());
            } else {
                Main.getInstance().getInventories().updateRoleInventory((Player) event.getWhoClicked());
            }
        });
    }
}