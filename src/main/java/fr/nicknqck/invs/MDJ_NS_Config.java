package fr.nicknqck.invs;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.EChakras;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.fastinv.PaginatedFastInv;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MDJ_NS_Config extends PaginatedFastInv {

    public MDJ_NS_Config() {
        super(27, "§fConfiguration§7 ->§a Naruto");
        setItems(getCorners(), new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        final List<Integer> integerList = new ArrayList<>();
        integerList.add(10);
        integerList.add(11);
        integerList.add(12);
        integerList.add(13);
        integerList.add(14);
        integerList.add(15);
        integerList.add(16);
        setContentSlots(integerList);
        previousPageItem(3, p -> new ItemBuilder(Material.PAPER).setName("§fPage " + p + "/" + lastPage()).toItemStack());
        nextPageItem(5, p -> new ItemBuilder(Material.PAPER).setName("§fPage " + p + "/" + lastPage()).toItemStack());
        addContent(new ItemBuilder(Material.NETHER_STAR).setName("§dBijus").setLore(
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
        addContent(new ItemBuilder(Material.APPLE)
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
                Main.getInstance().getGameConfig().getNarutoConfig().setEdoHealthRemove(Math.max(1.0,  oldValue-1.0));
                Main.getInstance().sendMessageToHosts(Main.getInstance().getNAME()+" §c"+event.getWhoClicked().getName()+"§7 a modifier la valeur: \"§fCoût de l'§5Edo Tensei§7\",§c "+(oldValue/2)+AllDesc.coeur+"§7 -> "+(Main.getInstance().getGameConfig().getNarutoConfig().getEdoHealthRemove()/2)+AllDesc.coeur);
            }
            new MDJ_NS_Config().open((Player) event.getWhoClicked());
        });
        addContent(new ItemBuilder(Material.EYE_OF_ENDER)
                .setName("§dObito§f peut récupérer le§c Sharingan§f de§a Kakashi")
                .setLore(
                        "§fValeur actuel:§c "+ (Main.getInstance().getGameConfig().getNarutoConfig().isObitoCanGetKakashiEye() ? "§aActivé" : "§cDésactivé"),
                        "",
                        "§7Si§a activer§7 et qu'§dObito§7 obtient le§c Sharingan§7 de§a Kakashi§7, alors,",
                        "§7tout ses§c cooldowns§7 seront§c divisé§7 par§c deux§7."
                )
                .toItemStack(), event -> {
            Main.getInstance().getGameConfig().getNarutoConfig().setObitoCanGetKakashiEye(!Main.getInstance().getGameConfig().getNarutoConfig().isObitoCanGetKakashiEye());
            Main.getInstance().sendMessageToHosts(Main.getInstance().getNAME()+"§c "+event.getWhoClicked().getName()+"§7 a modifié la valeur de "+"§dObito§f peut récupérer le§c Sharingan§f de§a Kakashi"+"§7 sur "+(Main.getInstance().getGameConfig().getNarutoConfig().isObitoCanGetKakashiEye() ? "§aActivé" : "§cDésactivé"));
            new MDJ_NS_Config().open((Player) event.getWhoClicked());
                }
        );
        addContent(new ItemBuilder(Material.INK_SACK).setDurability(EChakras.KATON.getColorCode()).setName(EChakras.KATON.getShowedName()).setLore(
                "§fValeur actuel:§c "+Main.getInstance().getGameConfig().getNarutoConfig().getKatonPercent()+"%",
                "",
                "§fClique gauche:§a +1%",
                "",
                "§fClique droit:§c -1%"
        ).toItemStack(), event -> {
            if (event.isLeftClick()) {
                Main.getInstance().getGameConfig().getNarutoConfig().setKatonPercent(Math.min(100, Main.getInstance().getGameConfig().getNarutoConfig().getKatonPercent()+1));
            }
            if (event.isRightClick()) {
                Main.getInstance().getGameConfig().getNarutoConfig().setKatonPercent(Math.max(0, Main.getInstance().getGameConfig().getNarutoConfig().getKatonPercent()-1));
            }
            Main.getInstance().sendMessageToHosts(Main.getInstance().getNAME()+"§c "+event.getWhoClicked().getName()+"§7 a modifié la valeur de \"§fChance d'activation du "+EChakras.KATON.getShowedName()+"§7\" sur§c "+Main.getInstance().getGameConfig().getNarutoConfig().getKatonPercent()+"%");
            new MDJ_NS_Config().open((Player) event.getWhoClicked());
        });
        addContent(new ItemBuilder(Material.INK_SACK).setDurability(EChakras.RAITON.getColorCode()).setName(EChakras.RAITON.getShowedName()).setLore(
                "§fValeur actuel:§c "+Main.getInstance().getGameConfig().getNarutoConfig().getRaitonPercent()+"%",
                "",
                "§fClique gauche:§a +1%",
                "",
                "§fClique droit:§c -1%"
        ).toItemStack(), event -> {
            if (event.isLeftClick()) {
                Main.getInstance().getGameConfig().getNarutoConfig().setRaitonPercent(Math.min(100, Main.getInstance().getGameConfig().getNarutoConfig().getRaitonPercent()+1));
            }
            if (event.isRightClick()) {
                Main.getInstance().getGameConfig().getNarutoConfig().setRaitonPercent(Math.max(0, Main.getInstance().getGameConfig().getNarutoConfig().getRaitonPercent()-1));
            }
            Main.getInstance().sendMessageToHosts(Main.getInstance().getNAME()+"§c "+event.getWhoClicked().getName()+"§7 a modifié la valeur de \"§fChance d'activation du "+EChakras.RAITON.getShowedName()+"§7\" sur§c "+Main.getInstance().getGameConfig().getNarutoConfig().getRaitonPercent()+"%");
            new MDJ_NS_Config().open((Player) event.getWhoClicked());
        });
        addContent(new ItemBuilder(Material.INK_SACK).setDurability(EChakras.DOTON.getColorCode()).setName(EChakras.DOTON.getShowedName()).setLore(
                "§fValeur actuel:§c "+Main.getInstance().getGameConfig().getNarutoConfig().getDotonPercent()+"%",
                "",
                "§fClique gauche:§a +1%",
                "",
                "§fClique droit:§c -1%"
        ).toItemStack(), event -> {
            if (event.isLeftClick()) {
                Main.getInstance().getGameConfig().getNarutoConfig().setDotonPercent(Math.min(100, Main.getInstance().getGameConfig().getNarutoConfig().getDotonPercent()+1));
            }
            if (event.isRightClick()) {
                Main.getInstance().getGameConfig().getNarutoConfig().setDotonPercent(Math.max(0, Main.getInstance().getGameConfig().getNarutoConfig().getDotonPercent()-1));
            }
            Main.getInstance().sendMessageToHosts(Main.getInstance().getNAME()+"§c "+event.getWhoClicked().getName()+"§7 a modifié la valeur de \"§fChance d'activation du "+EChakras.DOTON.getShowedName()+"§7\" sur§c "+Main.getInstance().getGameConfig().getNarutoConfig().getDotonPercent()+"%");
            new MDJ_NS_Config().open((Player) event.getWhoClicked());
        });
        addContent(new ItemBuilder(Material.EYE_OF_ENDER).setName("§aIzanamai§f peut infecter les rôles§e Solo").setLore(
                "§fValeur actuel: "+(Main.getInstance().getGameConfig().getNarutoConfig().isIzanamiCanInfectSolo() ? "§aOui" : "§cNon"),
                "",
                "§fCliquez pour modifier ce paramètre."
        ).toItemStack(), event -> {
            Main.getInstance().getGameConfig().getNarutoConfig().setIzanamiCanInfectSolo(!Main.getInstance().getGameConfig().getNarutoConfig().isIzanamiCanInfectSolo());
            Main.getInstance().sendMessageToHosts(Main.getInstance().getNAME()+"§c "+event.getWhoClicked().getName()+"§7 a définie le paramètre \"§aIzanami§f peut infecter les rôles§e Solo§7\" sur "+(Main.getInstance().getGameConfig().getNarutoConfig().isIzanamiCanInfectSolo() ? "§aOui" : "§cNon"));
            new MDJ_NS_Config().open((Player) event.getWhoClicked());
        });
        addContent(new ItemBuilder(Material.ENDER_PEARL).setName("§e§lShisui§f ignore la réglementation sur l'§aIzanami§f des rôles§e Solos").setLore(
                "§fValeur actuel: "+(Main.getInstance().getGameConfig().getNarutoConfig().isShisuiByPassIzanamiLimitation() ? "§aOui" : "§cNon"),
                "",
                "§fCliquez pour modifier ce paramètre."
        ).toItemStack(), event -> {
            Main.getInstance().getGameConfig().getNarutoConfig().setShisuiByPassIzanamiLimitation(!Main.getInstance().getGameConfig().getNarutoConfig().isShisuiByPassIzanamiLimitation());
            Main.getInstance().sendMessageToHosts(Main.getInstance().getNAME()+"§c "+event.getWhoClicked().getName()+"§7 a définie le paramètre \"§e§lShisui§f ignore la réglementation sur l'§aIzanami§f des rôles§e Solos§7\" sur "+(Main.getInstance().getGameConfig().getNarutoConfig().isShisuiByPassIzanamiLimitation() ? "§aOui" : "§cNon"));
            final MDJ_NS_Config c = new MDJ_NS_Config();
            c.open((Player) event.getWhoClicked());
            c.openPage(2);
        });
        addContent(new ItemBuilder(Material.FERMENTED_SPIDER_EYE).setName("§aKotoAmatsukami§7 de§e§l Shisui§7 peut§a infecter§7 les§e rôles Solitaire").setLore(
                "§fValeur actuel: "+(Main.getInstance().getGameConfig().getNarutoConfig().isShisuiKotoAmatsukamiInfectSolo() ? "§aOui" : "§cNon"),
                "",
                "§fCliquez pour modifier ce paramètre."
        ).toItemStack(), event -> {
            Main.getInstance().getGameConfig().getNarutoConfig().setShisuiKotoAmatsukamiInfectSolo(!Main.getInstance().getGameConfig().getNarutoConfig().isShisuiKotoAmatsukamiInfectSolo());
            Main.getInstance().sendMessageToHosts(Main.getInstance().getNAME()+"§c "+event.getWhoClicked().getName()+"§7 a définie le paramètre \"§aKotoAmatsukami§7 de§e§l Shisui§7 peut§a infecter§7 les§e rôles Solitaire§7\" sur "+(Main.getInstance().getGameConfig().getNarutoConfig().isShisuiKotoAmatsukamiInfectSolo() ? "§aOui" : "§cNon"));
            final MDJ_NS_Config c = new MDJ_NS_Config();
            c.open((Player) event.getWhoClicked());
            c.openPage(2);
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