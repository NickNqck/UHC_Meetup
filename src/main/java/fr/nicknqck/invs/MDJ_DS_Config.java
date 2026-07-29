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
                "§f10 secondes <§c "+ StringUtils.secondsTowardsBeautiful(Main.getInstance().getGameConfig().getDemonSlayerConfig().getTimingAssassin())+"§f > 5 minutes",
                "",
                "§fClique gauche:§a +10 secondes",
                "§fClique droit:§c -10 secondes"
        ).toItemStack(), event -> {
            if (event.isLeftClick()) {
                Main.getInstance().getGameConfig().getDemonSlayerConfig().setTimingAssassin(Math.min(60*5, Main.getInstance().getGameConfig().getDemonSlayerConfig().getTimingAssassin()+10));
            }
            if (event.isRightClick()) {
                Main.getInstance().getGameConfig().getDemonSlayerConfig().setTimingAssassin(Math.max(10, Main.getInstance().getGameConfig().getDemonSlayerConfig().getTimingAssassin()-10));
            }
            new MDJ_DS_Config().open((Player) event.getWhoClicked());
        });
        setItem(11, new ItemBuilder(Material.GHAST_TEAR).setName("§fTemps avant§c infection").setLore(
                "§f10 secondes <§c "+StringUtils.secondsTowardsBeautiful(Main.getInstance().getGameConfig().getDemonSlayerConfig().getInfectionTime())+"§f > 5 minutes",
                "",
                "§fClique gauche:§a +10 secondes",
                "§fClique droit:§c -10 secondes"
        ).toItemStack(), event -> {
            if (event.isLeftClick()) {
                Main.getInstance().getGameConfig().getDemonSlayerConfig().setInfectionTime(Math.min(60*5, Main.getInstance().getGameConfig().getDemonSlayerConfig().getInfectionTime()+10));
            }
            if (event.isRightClick()) {
                Main.getInstance().getGameConfig().getDemonSlayerConfig().setInfectionTime(Math.max(10, Main.getInstance().getGameConfig().getDemonSlayerConfig().getInfectionTime()-10));
            }
            new MDJ_DS_Config().open((Player) event.getWhoClicked());
        });
        setItem(12, new ItemBuilder(Material.NETHER_STAR).setName("§fDon de lame").setLore(
                "§7Lorsque ceci est§a activer§7 les joueurs le pouvant reçoive une§a Lame de Nichirin§7.",
                "",
                "§7Fonctionnalité actuellement: "+(Main.getInstance().getGameConfig().getDemonSlayerConfig().isGiveLame() ? "§aActiver" : "§cDésactiver")
        ).toItemStack(), event -> {
            Main.getInstance().getGameConfig().getDemonSlayerConfig().setGiveLame(!Main.getInstance().getGameConfig().getDemonSlayerConfig().isGiveLame());
            new MDJ_DS_Config().open((Player) event.getWhoClicked());
        });
        setItem(13, new ItemBuilder(Material.REDSTONE_ORE).setName("§cMuzan§f peut s'auto donner l'§cInfection")
                .setLore(
                        "§7Si§a activer§7, alors§c Muzan§7 pourra éxécuter la commande§6 /ds give§7 sur lui-même",
                        "",
                        "§7Fonctionnalité actuellement: "+(Main.getInstance().getGameConfig().getDemonSlayerConfig().isMuzanAutoGive() ? "§aActiver" : "§cDésactiver")
                )
                .toItemStack(), event -> {
            Main.getInstance().getGameConfig().getDemonSlayerConfig().setMuzanAutoGive(!Main.getInstance().getGameConfig().getDemonSlayerConfig().isMuzanAutoGive());
            Main.getInstance().sendMessageToHosts(Main.getInstance().getNAME()+"§c "+event.getWhoClicked().getName()+"§7 a définie le paramètre \"§cMuzan§f peut s'auto donner l'§cInfection§7\" sur "+(Main.getInstance().getGameConfig().getDemonSlayerConfig().isMuzanAutoGive() ? "§aActiver" : "§cDésactiver"));
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