package fr.nicknqck.invs;

import fr.nicknqck.Border;
import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.StunType;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.items.Items;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.fastinv.PaginatedFastInv;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.rank.ChatRank;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public final class ConfigurationGameInventory extends PaginatedFastInv {

    public ConfigurationGameInventory() {
        super(9*5, "§fConfiguration de la partie");
        final List<Integer> list = getList();
        nextPageItem(6, new ItemBuilder(Material.PAPER).setName("§fPage suivante").toItemStack());
        previousPageItem(4, new ItemBuilder(Material.PAPER).setName("§fPage précédente").toItemStack());
        setItem(5, GUIItems.getSelectBackMenu(), event -> {
            event.getWhoClicked().closeInventory();
            event.getWhoClicked().openInventory(Bukkit.createInventory(null, 54, "§7(§c!§7)§f Configuration"));
            Main.getInstance().getInventories().updateAdminInventory((Player) event.getWhoClicked());
        });
        setContentSlots(list);
        setItems(getCorners(), new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        addContent(new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(5).setName("§fTaille de la bordure maximum").setLore(
                "§f[50b < "+ Border.getMaxBorderSize()+" > 2400b",
                "§fClique gauche: §a+50b",
                "§fClique droit: §c-50b"
        ).toItemStack(), event -> {
            if (event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                Border.setMaxBorderSize(Border.getMaxBorderSize()+50);
            } else if (event.getAction().equals(InventoryAction.PICKUP_HALF)) {
                Border.setMaxBorderSize(Border.getMaxBorderSize()-50);
            }
            refreshCurrentPage();
        });
        addContent(new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§fTaille de la bordure minimum").setDurability(14).setLore(
                "§r§f[50b < "+Border.getMinBorderSize()+"b > "+Border.getMaxBorderSize()+"b]",
                "§r§fClique gauche:§a +50b",
                "§r§fClique droit: §c-50b"
        ).toItemStack(), event -> {
            if (event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                Border.setMinBorderSize(Math.min(Border.getMinBorderSize()+50, 2400));
            } else if (event.getAction().equals(InventoryAction.PICKUP_HALF)) {
                Border.setMinBorderSize(Border.getMinBorderSize()-50);
            }
            refreshCurrentPage();
        });
        addContent(new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(0).setName("§r§fTemp avant réduction de la bordure").setLore(
                "§r§f[0 minute < "+Border.getTempReduction()/60+" minutes > 60 minutes]",
                "§r§fClique gauche: §a+1 minutes",
                "§r§fClique droit: §c-1 minutes"
        ).toItemStack(), event -> {
            if (event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                Border.setTempReduction(Math.min(Border.getTempReduction()+60, 60*60));
            } else if (event.getAction().equals(InventoryAction.PICKUP_HALF)) {
                Border.setTempReduction(Math.max(Border.getTempReduction()-60, 0));
            }
            refreshCurrentPage();
        });
        addContent(new ItemBuilder(Material.STAINED_GLASS_PANE).setAmount(1).setDurability(3).setName("§r§fVitesse de la bordure")
                .setLore("§r§f[1b/s < "+Border.getBorderSpeed()+"§r§fb/s > 10b/s",
                        "§r§fClique gauche: §a+1b/s",
                        "§r§fClique droit: §c-1b/s").toItemStack(), event -> {
            if (event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                Border.setBorderSpeed(Math.min(Border.getBorderSpeed()+1, 10));
            } else if (event.getAction().equals(InventoryAction.PICKUP_HALF)) {
                Border.setBorderSpeed(Math.max(Border.getBorderSpeed()-1, 1));
            }
            refreshCurrentPage();
        });
        addContent(new ItemBuilder(Material.IRON_SWORD).setName("§r§fTemp avant activation du PVP").setLore(
                "§r§f[0 minute < "+ GameState.getInstance().getPvPTimer()/60+" minutes > 40 minutes]",
                "§r§fClique gauche: §a+1 minutes",
                "§r§fClique droit: §c-1 minutes"
        ).toItemStack(), event -> {
            if (event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                GameState.getInstance().pvpTimer += 60;
            } else if (event.getAction().equals(InventoryAction.PICKUP_HALF)) {
                GameState.getInstance().pvpTimer -= 60;
            }
            refreshCurrentPage();
        });
        addContent(new ItemBuilder(Material.SKULL_ITEM).setName("§r§fTemp avant annonce des roles").setLore(
                "§r§f[0 minute < "+GameState.getInstance().getRoleTimer()/60+" minutes > 40 minutes]",
                "§r§fClique gauche: §a+1 minutes",
                "§r§fClique droit: §c-1 minutes"
        ).toItemStack(), event -> {
            if (event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                GameState.getInstance().roleTimer+=60;
            } else if (event.getAction().equals(InventoryAction.PICKUP_HALF)) {
                GameState.getInstance().roleTimer-=60;
            }
            refreshCurrentPage();
        });
        addContent(new ItemBuilder(Material.WATCH).setName("§r§fDurée du jour (et de la nuit)").setLore(
                "§r§fDurée actuel:§6 "+ StringUtils.secondsTowardsBeautiful(Main.getInstance().getGameConfig().getMaxTimeDay()),
                "§r§fClique gauche: §a+10 secondes",
                "§r§fClique droit: §c-10 secondes"
        ).toItemStack(), event -> {
            if (ChatRank.isHost(event.getWhoClicked())) {
                if (event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                    Main.getInstance().getGameConfig().setMaxTimeDay(Main.getInstance().getGameConfig().getMaxTimeDay()+10);
                    refreshCurrentPage();
                } else {
                    if (event.getAction().equals(InventoryAction.PICKUP_HALF)) {
                        Main.getInstance().getGameConfig().setMaxTimeDay(Main.getInstance().getGameConfig().getMaxTimeDay()-10);
                        refreshCurrentPage();
                    }
                }
            }
            refreshCurrentPage();
        });
        addContent(GUIItems.getTabRoleInfo(GameState.getInstance()), event -> {
            if (ChatRank.isHost(event.getWhoClicked())) {
                if (!GameState.getInstance().roletab) {
                    event.getWhoClicked().sendMessage("Les roles seront maintenant afficher dans le tab");
                    GameState.getInstance().roletab = true;
                } else {
                    event.getWhoClicked().sendMessage("Les roles ne seront plus afficher dans le tab");
                    GameState.getInstance().roletab = false;
                }
                refreshCurrentPage();
            }
        });
        addContent(Items.geteclairmort(), event -> {
            if (ChatRank.isHost(event.getWhoClicked())) {
                if (!Main.getInstance().getGameConfig().isMortEclair()) {
                    event.getWhoClicked().sendMessage("Éclair à la mort est désormais§6 activé");
                    Main.getInstance().getGameConfig().setMortEclair(true);
                } else {
                    event.getWhoClicked().sendMessage("Éclair à la mort est désormais§6 désactivé");
                    Main.getInstance().getGameConfig().setMortEclair(false);
                }
            }
            refreshCurrentPage();
        });
        addContent(new ItemBuilder(Material.WATER_BUCKET).setName("§r§fTemp avant despawn de l'§bEau").setLore(
                "§r§f[0 secondes < "+StringUtils.secondsTowardsBeautiful(Main.getInstance().getGameConfig().getWaterEmptyTiming())+" > 1 minutes",
                "§r§fClique gauche: §a+1 secondes",
                "§r§fClique droit: §c-1 secondes",
                "§r§f(0 secondes =§c désactiver"
        ).toItemStack(), event -> {
            if (event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                if (Main.getInstance().getGameConfig().getWaterEmptyTiming() != 60) {
                    Main.getInstance().getGameConfig().setWaterEmptyTiming(Main.getInstance().getGameConfig().getWaterEmptyTiming()+1);
                }else {
                    event.getWhoClicked().sendMessage("Timing maximal atteint !");
                }
            }else {
                if (Main.getInstance().getGameConfig().getWaterEmptyTiming() != 0) {
                    Main.getInstance().getGameConfig().setWaterEmptyTiming(Main.getInstance().getGameConfig().getWaterEmptyTiming()-1);
                }else {
                    event.getWhoClicked().sendMessage("Timing minimal atteint !");
                }
            }
            refreshCurrentPage();
        });
        addContent(new ItemBuilder(Material.LAVA_BUCKET).setName("§r§fTemp avant despawn de la§6 Lave").setLore(
                "§r§f[0 seconde < "+StringUtils.secondsTowardsBeautiful(Main.getInstance().getGameConfig().getLavaEmptyTiming())+" > 1 minutes",
                "§r§fClique gauche: §a+1 seconde",
                "§r§fClique droit: §c-1 seconde",
                "§r§f(0 secondes =§c désactiver"
        ).toItemStack(), event -> {
            if (event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                if (Main.getInstance().getGameConfig().getLavaEmptyTiming() != 60) {
                    Main.getInstance().getGameConfig().setLavaEmptyTiming(Main.getInstance().getGameConfig().getLavaEmptyTiming()+1);
                }else {
                    event.getWhoClicked().sendMessage("Timing maximal atteint !");
                }
            }else {
                if (Main.getInstance().getGameConfig().getLavaEmptyTiming() != 0) {
                    Main.getInstance().getGameConfig().setLavaEmptyTiming(Main.getInstance().getGameConfig().getLavaEmptyTiming()-1);
                }else {
                    event.getWhoClicked().sendMessage("Timing minimal atteint !");
                }
            }
            refreshCurrentPage();
        });
        addContent(new ItemBuilder(Material.TNT).setName("§fGrief du terrain par les§c TNT").setLore(Main.getInstance().getGameConfig().isTntGrief() ? "§aActivé" : "§cDésactivé").toItemStack(), event -> {
            if (Main.getInstance().getGameConfig().isTntGrief()) {
                Main.getInstance().getGameConfig().setTntGrief(false);
                Main.getInstance().sendMessageToHosts("§7[§6UHC-Meetup§7] "+ChatRank.getPlayerGrade((Player) event.getWhoClicked()).getPrefix()+event.getWhoClicked().getName()+"§7 a définie la règle \"§cGrief par les tnt§7\" sur§c désactiver§7.");
            } else {
                Main.getInstance().getGameConfig().setTntGrief(true);
                Main.getInstance().sendMessageToHosts("§7[§6UHC-Meetup§7] "+ChatRank.getPlayerGrade((Player) event.getWhoClicked()).getPrefix()+event.getWhoClicked().getName()+"§7 a définie la règle \"§cGrief par les tnt§7\" sur§a activer§7.");
            }
            refreshCurrentPage();
        });
        addContent(new ItemBuilder(Material.DIAMOND_SWORD).setName("§fPourcentage de force").setLore(
                "§c"+Main.getInstance().getGameConfig().getForcePercent()+"%",
                "",
                "§fVanilla: §c130%",
                "§aMinimum:§c 10%"
        ).toItemStack(), event -> {
            if (event.isLeftClick()) {
                Main.getInstance().getGameConfig().setForcePercent(Math.max(10, Main.getInstance().getGameConfig().getForcePercent()+5));
            } else {
                Main.getInstance().getGameConfig().setForcePercent(Math.max(10, Main.getInstance().getGameConfig().getForcePercent()-5));
            }
            refreshCurrentPage();
        });
        addContent(new ItemBuilder(Material.IRON_CHESTPLATE).setName("§fPourcentage de Résistance").setLore(
                "§9Résistance I§f: "+Main.getInstance().getGameConfig().getResiPercent()+"%",
                "§9Résistance II§f: "+(Main.getInstance().getGameConfig().getResiPercent()*2)+"%",
                "",
                "§fVanilla: §c20%",
                "§aMinimum:§c 10%"
        ).toItemStack(), event -> {
            if (event.isLeftClick()) {
                Main.getInstance().getGameConfig().setResiPercent(Math.max(10, Main.getInstance().getGameConfig().getResiPercent()+5));
            } else {
                Main.getInstance().getGameConfig().setResiPercent(Math.max(10, Main.getInstance().getGameConfig().getResiPercent()-5));
            }
            refreshCurrentPage();
        });
        addContent(new ItemBuilder(Material.TRIPWIRE_HOOK).setName("§fTypes de stun").setLore(
                "",
                (Main.getInstance().getGameConfig().getStunType().equals(StunType.TELEPORT) ?
                        "§8 -§r "+ StunType.TELEPORT.getColor()+"§l"+ StunType.TELEPORT.getName()
                        :
                        "§8 -§r "+ StunType.TELEPORT.getColor() + StunType.TELEPORT.getName()),
                (Main.getInstance().getGameConfig().getStunType().equals(StunType.STUCK) ?
                        "§8 -§r"+ StunType.STUCK.getColor()+" §l"+ StunType.STUCK.getName()
                        :
                        "§8 -§r "+ StunType.STUCK.getColor() + StunType.STUCK.getName())
        ).toItemStack(), event -> {
            if (Main.getInstance().getGameConfig().getStunType().equals(StunType.TELEPORT)) {
                Main.getInstance().getGameConfig().setStunType(StunType.STUCK);
            } else {
                Main.getInstance().getGameConfig().setStunType(StunType.TELEPORT);
            }
            refreshCurrentPage();
        });
        addContent(new ItemBuilder(Material.IRON_SWORD).setName("§fLes joueurs§a stun§f peuvent subir des dégâts").setLore(
                "§fValeur actuel: "+(Main.getInstance().getGameConfig().isPlayerStunCanTakeDamage() ? "§aActiver" : "§cDésactiver")
        ).toItemStack(), event -> {
            Main.getInstance().getGameConfig().setPlayerStunCanTakeDamage(!Main.getInstance().getGameConfig().isPlayerStunCanTakeDamage());
            Main.getInstance().sendMessageToHosts("§c"+event.getWhoClicked().getName()+"§7 a définie la règle \""+event.getCurrentItem().getItemMeta().getDisplayName()+"§7\" sur "+(Main.getInstance().getGameConfig().isPlayerStunCanTakeDamage() ? "§aActiver" : "§cDésactiver"));
            refreshCurrentPage();
        });
    }

    @Nonnull
    private List<Integer> getList() {
        final List<Integer> list = new ArrayList<>();
        list.add(10);
        list.add(11);
        list.add(12);
        list.add(13);
        list.add(14);
        list.add(15);
        list.add(16);

        list.add(19);
        list.add(20);
        list.add(21);
        list.add(22);
        list.add(23);
        list.add(24);
        list.add(25);

        list.add(28);
        list.add(29);
        list.add(30);
        list.add(31);
        list.add(32);
        list.add(33);
        list.add(34);
        return list;
    }
}