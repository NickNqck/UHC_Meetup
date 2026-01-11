package fr.nicknqck.invs;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.fastinv.FastInv;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MDJ_AOT_Config extends FastInv {

    public MDJ_AOT_Config() {
        super(27, "§fConfiguration§7 ->§c Attack On Titan");
        setItems(getCorners(), new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());

        setItem(10, new ItemBuilder(Material.WATCH).setName("§fCooldown de l'équipement tridimensionnel").setLore(
                "§fCooldown actuel:§b "+ StringUtils.secondsTowardsBeautiful(Main.getInstance().getGameConfig().getTridiCooldown()),
                "",
                "§fClique gauche:§a + 1 seconde",
                "§fClique droit:§c - 1 seconde"
        ).toItemStack(), event -> {
            if (event.isLeftClick()) {
                Main.getInstance().getGameConfig().setTridiCooldown(Math.min(64, Main.getInstance().getGameConfig().getTridiCooldown() + 1));
            }
            if (event.isRightClick()) {
                Main.getInstance().getGameConfig().setTridiCooldown(Math.max(5,  Main.getInstance().getGameConfig().getTridiCooldown() - 1));
            }
            new MDJ_AOT_Config().open((Player) event.getWhoClicked());
        });

        if (Main.getInstance().getGameConfig().isRodTridimenssionel()) {
            setItem(11, new ItemBuilder(Material.FISHING_ROD)
                    .setName("§fEquipement Tridimentionnel")
                    .toItemStack(), event -> {
                Main.getInstance().getGameConfig().setRodTridimenssionel(false);
                Main.getInstance().sendMessageToHosts(Main.getInstance().getNAME()+" §c"+event.getWhoClicked().getName()+"§7 a définie l'équipement tridimentionnel sur: \"§fArc§7\".");
                new MDJ_AOT_Config().open((Player) event.getWhoClicked());
            });
        } else {
            setItem(11, new ItemBuilder(Material.BOW)
                    .setName("§fEquipement Tridimentionnel")
                    .toItemStack(), event -> {
                Main.getInstance().getGameConfig().setRodTridimenssionel(true);
                Main.getInstance().sendMessageToHosts(Main.getInstance().getNAME()+" §c"+event.getWhoClicked().getName()+"§7 a définie l'équipement tridimentionnel sur: \"§fCânne à pêche§7\".");
                new MDJ_AOT_Config().open((Player) event.getWhoClicked());
            });
        }
        setItem(12, new ItemBuilder(Material.LAVA_BUCKET)
                .setName("Capacité d'utiliser de la§6 lave§f pour les§c titans§f (§ctransformer§f)")
                .setLore(Main.getInstance().getGameConfig().isLaveTitans() ?
                        "§fLes titans§a peuvent§f utiliser de la§6 lave§f une fois§c transformer§f."
                        :
                        "§fLes titans§c ne§f peuvent§c pas§f utiliser de la§6 lave§f une fois§c transformer§f.")
                .toItemStack(), event -> {
            if (!Main.getInstance().getGameConfig().isLaveTitans()) {
                Main.getInstance().getGameConfig().setLaveTitans(true);
                Main.getInstance().sendMessageToHosts(Main.getInstance().getNAME()+" §c"+event.getWhoClicked().getName()+"§f a définie la capacité des§c titans§f (§ctransformer§f) d'utiliser de la§6 lave§f sur:§a Activer");
            } else {
                Main.getInstance().getGameConfig().setLaveTitans(false);
                Main.getInstance().sendMessageToHosts(Main.getInstance().getNAME()+" §c"+event.getWhoClicked().getName()+"§f a définie la capacité des§c titans§f (§ctransformer§f) d'utiliser de la§6 lave§f sur:§c Désactiver");
            }
            new MDJ_AOT_Config().open((Player) event.getWhoClicked());
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
