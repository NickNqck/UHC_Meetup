package fr.nicknqck.events.essential.inventorys;

import fr.nicknqck.GameState;
import fr.nicknqck.HubListener;
import fr.nicknqck.Main;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.rank.ChatRank;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class HubInventory implements Listener {

    private final GameState gameState;
    @Getter
    private static HubInventory instance;

    public HubInventory(GameState gameState) {
        this.gameState = gameState;
        instance = this;
        EventUtils.registerEvents(new InventoryConfig(gameState));
    }
    @EventHandler
    private void OnInventoryClicked(InventoryClickEvent event) {
        if (gameState.getServerState() != GameState.ServerStates.InLobby) return;
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            Inventory inv = event.getClickedInventory();
            if (inv != null && event.getCurrentItem() != null) {
                if (event.getCurrentItem().isSimilar(GUIItems.getStartGameButton()) && gameState.gameCanLaunch) HubListener.getInstance().StartGame(player);
                final ItemStack item = event.getCurrentItem();
                final boolean mahr = item.isSimilar(GUIItems.getSelectMahrButton());
                final boolean sl = item.isSimilar(GUIItems.getSelectSlayersButton());
                final boolean d = item.isSimilar(GUIItems.getSelectDemonButton());
                final boolean solo = item.isSimilar(GUIItems.getSelectSoloButton());
                final boolean titans = item.isSimilar(GUIItems.getSelectTitanButton());
                final boolean soldat = item.isSimilar(GUIItems.getSelectSoldatButton());
                final boolean demon = item.isSimilar(GUIItems.getSelectDSButton());
                final boolean aot = item.isSimilar(GUIItems.getSelectAOTButton());
                final boolean ns = item.isSimilar(GUIItems.getSelectNSButton());
                final boolean akatsuki = item.isSimilar(GUIItems.getSelectAkatsukiButton());
                final boolean orochimaru = item.isSimilar(GUIItems.getSelectOrochimaruButton());
                final boolean shinobi = item.isSimilar(GUIItems.getSelectShinobiButton());
                if (!item.hasItemMeta())return;
                switch(inv.getTitle()) {
                    case "§fConfiguration§7 ->§6 Roles":
                        if (item.getType() != Material.AIR) {
                            if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
                                if (demon) {
                                    player.openInventory(GUIItems.getDemonSlayerInventory());
                                    Main.getInstance().getInventories().updateDSInventory(player);
                                }
                                if (aot) {
                                    player.openInventory(GUIItems.getSelectAOTInventory());
                                    Main.getInstance().getInventories().updateAOTInventory(player);
                                }
                                if (ns) {
                                    player.openInventory(GUIItems.getSelectNSInventory());
                                    Main.getInstance().getInventories().updateNSInventory(player);
                                }
                                if (item.isSimilar(GUIItems.getSelectMCButton())) {
                                    player.openInventory(GUIItems.getSelectMCInventory());
                                    Main.getInstance().getInventories().updateMCInventory(player);
                                }
                                if (item.getType() == Material.BOOKSHELF) {
                                    Inventory inventaire = Bukkit.createInventory(player, 9, "Séléction du mode de jeu");
                                    player.openInventory(inventaire);
                                    Main.getInstance().getInventories().updateSelectMDJ(player);
                                }
                            } else {
                                if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                                    if (ChatRank.isHost(player))player.openInventory(GUIItems.getAdminWatchGUI());
                                    if (!player.isOp() && player.getOpenInventory() != null && player.getInventory() != null) player.closeInventory();
                                }
                            }
                        }
                        for (UUID u : gameState.getInLobbyPlayers()) {
                            Player p = Bukkit.getPlayer(u);
                            if (p == null)continue;
                            Main.getInstance().getInventories().updateRoleInventory(p);
                        }
                        event.setCancelled(true);
                        break;
                    case "§fRoles§7 ->§6 DemonSlayer":
                        if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
                            if (sl || d || solo) {
                                if (sl) {
                                    player.openInventory(GUIItems.getSlayersSelectGUI());
                                    Main.getInstance().getInventories().updateSlayerInventory(player);
                                }
                                if (d) {
                                    player.openInventory(GUIItems.getDemonSelectGUI());
                                    Main.getInstance().getInventories().updateDemonInventory(player);
                                }
                                if (solo) {
                                    player.openInventory(GUIItems.getDSSoloSelectGUI());
                                    Main.getInstance().getInventories().updateDSSoloInventory(player);
                                }
                            }
                        } else {
                            player.openInventory(GUIItems.getRoleSelectGUI());
                            Main.getInstance().getInventories().updateRoleInventory(player);
                        }
                        event.setCancelled(true);
                        break;
                    case "§fRoles§7 ->§6 AOT":
                        if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
                            if (solo || mahr || titans || soldat) {
                                if (solo) {
                                    player.openInventory(GUIItems.getAOTSoloSelectGUI());
                                    Main.getInstance().getInventories().updateAOTSoloInventory(player);
                                }
                                if (mahr) {
                                    player.openInventory(GUIItems.getMahrGui());
                                    Main.getInstance().getInventories().updateMahrInventory(player);
                                }
                                if (titans) {
                                    player.openInventory(GUIItems.getSecretTitansGui());
                                    Main.getInstance().getInventories().updateSecretTitansInventory(player);
                                }
                                if (soldat) {
                                    player.openInventory(GUIItems.getSecretSoldatGui());
                                    Main.getInstance().getInventories().updateSoldatInventory(player);
                                }
                            }
                        }else {
                            player.openInventory(GUIItems.getRoleSelectGUI());
                            Main.getInstance().getInventories().updateRoleInventory(player);
                        }
                        event.setCancelled(true);
                        break;
                    case "§fRoles§7 ->§6 NS":
                        if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
                            if (item.isSimilar(GUIItems.getStartGameButton())) {
                                if (gameState.gameCanLaunch) {
                                    HubListener.getInstance().StartGame(player);
                                }
                            } else {
                                if (solo) {
                                    player.openInventory(GUIItems.getSelectNSSoloInventory());
                                    Main.getInstance().getInventories().updateNSSoloInventory(player);
                                }
                                if (akatsuki) {
                                    player.openInventory(GUIItems.getSelectAkatsukiInventory());
                                    Main.getInstance().getInventories().updateNSAkatsukiInventory(player);
                                }
                                if (orochimaru) {
                                    player.openInventory(GUIItems.getSelectOrochimaruInventory());
                                    Main.getInstance().getInventories().updateNSOrochimaruInventory(player);
                                }
                                if (shinobi) {
                                    player.openInventory(GUIItems.getSelectNSShinobiInventory());
                                    Main.getInstance().getInventories().updateNSShinobiInventory(player);
                                }
                            }
                        }else {
                            player.openInventory(GUIItems.getRoleSelectGUI());
                            Main.getInstance().getInventories().updateRoleInventory(player);
                        }
                        event.setCancelled(true);
                        break;
                    case "§fRoles§7 ->§a Minecraft":
                        if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
                            if (item.isSimilar(GUIItems.getStartGameButton())) {
                                if (gameState.gameCanLaunch) {
                                    HubListener.getInstance().StartGame(player);
                                }
                            } else {
                                if (item.isSimilar(GUIItems.getSelectOverworldButton())) {
                                    player.openInventory(Bukkit.createInventory(player, 54, "§aMinecraft§7 ->§a Overworld"));
                                    Main.getInstance().getInventories().updateOverworldInventory(player);
                                }
                                if (item.isSimilar(GUIItems.getSelectNetherButton())) {
                                    player.openInventory(Bukkit.createInventory(player, 54, "§aMinecraft§7 ->§c Nether"));
                                    Main.getInstance().getInventories().updateNetherInventory(player);
                                }
                            }
                        } else {
                            player.openInventory(GUIItems.getRoleSelectGUI());
                            Main.getInstance().getInventories().updateRoleInventory(player);
                        }
                        event.setCancelled(true);
                        break;
                    default:
                        break;
                }
                for (UUID u : gameState.getInLobbyPlayers()) {
                    Player p = Bukkit.getPlayer(u);
                    if (p == null)continue;
                    Main.getInstance().getInventories().menuUpdater(p);
                }
            }
        }
    }

}