package fr.nicknqck.invs;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.fastinv.FastInv;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MDJ_NS_Config extends FastInv {

    public MDJ_NS_Config() {
        super(27, "§fConfiguration§7 ->§a Naruto");
        setItems(getCorners(), new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());

        setItem(10, new ItemBuilder(Material.NETHER_STAR).setName("§dBijus").setLore(
                "§fLa valeur est actuellement définie sur: "+(Main.getInstance().getBijuManager().isBijuEnable() ? "§aActiver" : "§cDésactiver")
        ).toItemStack(), event -> {
            if (event.isShiftClick()) {
                event.getWhoClicked().closeInventory();
                event.getWhoClicked().openInventory(Bukkit.createInventory(event.getWhoClicked(), 9*4, "Configuration ->§6 Bijus"));
                Main.getInstance().getInventories().openConfigBijusInventory((Player) event.getWhoClicked());
            } else {
                if (Main.getInstance().getBijuManager().isBijuEnable()) {
                    Main.getInstance().getBijuManager().setBijuEnable(false);
                    Main.getInstance().sendMessageToHosts(Main.getInstance().getNAME()+" §c"+event.getWhoClicked().getName()+"§7 a définie l'apparition de tout les§d Bijus§7 sur:§c Désactiver");
                } else {
                    Main.getInstance().getBijuManager().setBijuEnable(true);
                    Main.getInstance().sendMessageToHosts(Main.getInstance().getNAME()+" §c"+event.getWhoClicked().getName()+"§7 a définie l'apparition de tout les§d Bijus§7 sur:§a Activer");
                }
                new MDJ_NS_Config().open((Player) event.getWhoClicked());
            }
        });
        setItem(11, new ItemBuilder(Material.APPLE)
                .setName("§fCoût d'utilisation de l'§5Edo Tensei")
                .setLore(
                        "§fValeur actuel:§c "+(Main.getInstance().getGameConfig().getNarutoConfig().getEdoHealthRemove()/2)+ AllDesc.coeur,
                        "",
                        "§7Définie le coût que la personne utilisant le pouvoir",
                        "§7de l'§5Edo Tensei§7 devra payer pour l'utiliser."
                )
                .toItemStack(), event -> {
            final double oldValue = Main.getInstance().getGameConfig().getNarutoConfig().getEdoHealthRemove();
            if (event.isLeftClick()) {
                Main.getInstance().getGameConfig().getNarutoConfig().setEdoHealthRemove(Math.min(10.0, oldValue+1.0));
                Main.getInstance().sendMessageToHosts(Main.getInstance().getNAME()+" §c"+event.getWhoClicked().getName()+"§7 a modifier la valeur: \"§fCoût de l'§5Edo Tensei§7\",§c "+(oldValue/2)+AllDesc.coeur+"§7 -> "+(Main.getInstance().getGameConfig().getNarutoConfig().getEdoHealthRemove()/2)+AllDesc.coeur);
            }
            if (event.isRightClick()) {
                Main.getInstance().getGameConfig().getNarutoConfig().setEdoHealthRemove(Math.max(1.0,  oldValue+1.0));
                Main.getInstance().sendMessageToHosts(Main.getInstance().getNAME()+" §c"+event.getWhoClicked().getName()+"§7 a modifier la valeur: \"§fCoût de l'§5Edo Tensei§7\",§c "+(oldValue/2)+AllDesc.coeur+"§7 -> "+(Main.getInstance().getGameConfig().getNarutoConfig().getEdoHealthRemove()/2)+AllDesc.coeur);
            }
            new MDJ_NS_Config().open((Player) event.getWhoClicked());
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