package fr.nicknqck.events.essential.inventorys.rconfig;

import fr.nicknqck.GameState;
import fr.nicknqck.HubListener;
import fr.nicknqck.Main;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.rank.ChatRank;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class AotRolesConfig implements Listener {

    private final GameState gameState;

    public AotRolesConfig(GameState gameState) {
        EventUtils.registerEvents(this);
        this.gameState = gameState;
        EventUtils.registerEvents(new NSRolesConfig(gameState));
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        if (gameState.getServerState() != GameState.ServerStates.InLobby) return;
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            Inventory inv = event.getClickedInventory();
            InventoryAction action = event.getAction();
            if (inv != null && event.getCurrentItem() != null) {
                ItemStack item = event.getCurrentItem();
                if (!item.hasItemMeta())return;
                final boolean solo = item.isSimilar(GUIItems.getSelectSoloButton());
                final boolean titans = item.isSimilar(GUIItems.getSelectTitanButton());
                final boolean soldat = item.isSimilar(GUIItems.getSelectSoldatButton());
                final boolean aotconfig = item.isSimilar(GUIItems.getSelectConfigAotButton());
                final boolean mahr = item.isSimilar(GUIItems.getSelectMahrButton());
                switch (inv.getTitle()) {
                    case "§fAOT§7 ->§a Soldats":
                        if (item.getItemMeta() == null)return;
                        if (item.getItemMeta().getDisplayName() == null)return;
                        if (item.getType() == Material.AIR)return;
                        if (item.isSimilar(GUIItems.getx())) {
                            event.setCancelled(true);
                            return;
                        }
                        if (!ChatRank.isHost(player)) {
                            event.setCancelled(true);
                            return;
                        }
                        if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
                            if (!item.isSimilar(GUIItems.getGreenStainedGlassPane()) && !solo && !mahr && !titans && !soldat && !aotconfig &&!item.isSimilar(GUIItems.getCantStartGameButton()) && !item.isSimilar(GUIItems.getStartGameButton())) {
                                String name = item.getItemMeta().getDisplayName();
                                GameState.Roles role = GameState.Roles.valueOf(name);
                                if (action.equals(InventoryAction.PICKUP_ALL)) {
                                    gameState.addInAvailableRoles(role, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(role)+1));
                                } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                    gameState.addInAvailableRoles(role, Math.max(0, gameState.getAvailableRoles().get(role)-1));
                                }else {
                                    event.setCancelled(true);
                                }
                            }
                            if (item.isSimilar(GUIItems.getStartGameButton()) && gameState.gameCanLaunch) HubListener.getInstance().StartGame(player);

                        } else {
                            if (ChatRank.isHost(player))player.openInventory(GUIItems.getSelectAOTInventory());
                            Main.getInstance().getInventories().updateAOTInventory(player);
                        }
                        if (item.isSimilar(GUIItems.getSelectMahrButton())) {
                            player.openInventory(GUIItems.getMahrGui());
                            Main.getInstance().getInventories().updateMahrInventory(player);
                        }
                        if (item.isSimilar(GUIItems.getSelectTitanButton())) {
                            player.openInventory(GUIItems.getSecretTitansGui());
                            Main.getInstance().getInventories().updateSecretTitansInventory(player);
                        }
                        if (item.isSimilar(GUIItems.getSelectSoloButton())) {
                            player.openInventory(GUIItems.getAOTSoloSelectGUI());
                            Main.getInstance().getInventories().updateAOTSoloInventory(player);
                        }
                        if (item.isSimilar(GUIItems.getSelectSoldatButton())) {
                            player.openInventory(GUIItems.getSecretSoldatGui());
                            Main.getInstance().getInventories().updateSoldatInventory(player);
                        }
                        if (item.isSimilar(GUIItems.getSelectConfigAotButton())) {
                            player.openInventory(GUIItems.getConfigurationAOT());
                            Main.getInstance().getInventories().updateAOTConfiguration(player);
                        }
                        event.setCancelled(true);
                        break;
                    case "§fAOT§7 ->§c Titans":
                        if (item.getItemMeta() == null)return;
                        if (item.getItemMeta().getDisplayName() == null)return;
                        if (item.getType() == Material.AIR)return;
                        if (item.isSimilar(GUIItems.getx())) {
                            event.setCancelled(true);
                            return;
                        }
                        if (!ChatRank.isHost(player)) {
                            event.setCancelled(true);
                            return;
                        }
                        if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
                            if (!item.isSimilar(GUIItems.getRedStainedGlassPane()) && !solo && !mahr && !titans && !soldat && !aotconfig &&!item.isSimilar(GUIItems.getCantStartGameButton()) && !item.isSimilar(GUIItems.getStartGameButton())) {
                                for (GameState.Roles roles : GameState.Roles.values()) {
                                    if (item.getItemMeta().getDisplayName().contains(roles.getItem().getItemMeta().getDisplayName())) {
                                        if (action.equals(InventoryAction.PICKUP_ALL)) {
                                            gameState.addInAvailableRoles(roles, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(roles)+1));
                                            break;
                                        } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                            gameState.addInAvailableRoles(roles, Math.max(0, gameState.getAvailableRoles().get(roles)-1));
                                            break;
                                        }
                                    }
                                }
                            }
                            if (item.isSimilar(GUIItems.getStartGameButton()) && gameState.gameCanLaunch) HubListener.getInstance().StartGame(player);

                        } else {
                            if (ChatRank.isHost(player))player.openInventory(GUIItems.getSelectAOTInventory());Main.getInstance().getInventories().updateAOTInventory(player);
                        }
                        for (UUID u : gameState.getInLobbyPlayers()) {
                            Player p = Bukkit.getPlayer(u);
                            if (p == null)continue;
                            Main.getInstance().getInventories().updateDSSoloInventory(p);
                        }
                        if (mahr) {
                            player.openInventory(GUIItems.getMahrGui());
                            Main.getInstance().getInventories().updateMahrInventory(player);
                        }
                        if (titans) {
                            player.openInventory(GUIItems.getSecretTitansGui());
                            Main.getInstance().getInventories().updateSecretTitansInventory(player);
                        }
                        if (solo) {
                            player.openInventory(GUIItems.getAOTSoloSelectGUI());
                            Main.getInstance().getInventories().updateAOTSoloInventory(player);
                        }
                        if (soldat) {
                            player.openInventory(GUIItems.getSecretSoldatGui());
                            Main.getInstance().getInventories().updateSoldatInventory(player);
                        }
                        if (aotconfig) {
                            player.openInventory(GUIItems.getConfigurationAOT());
                            Main.getInstance().getInventories().updateAOTConfiguration(player);
                        }
                        event.setCancelled(true);
                        break;
                    case "§fAOT§7 ->§9 Mahr":
                        if (item.getItemMeta() == null)return;
                        if (item.getItemMeta().getDisplayName() == null)return;
                        if (item.getType() == Material.AIR)return;
                        if (item.isSimilar(GUIItems.getx())) {
                            event.setCancelled(true);
                            return;
                        }
                        if (!ChatRank.isHost(player)) {
                            event.setCancelled(true);
                            return;
                        }
                        if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
                            if (!item.isSimilar(GUIItems.getSBluetainedGlassPane()) && !solo && !mahr && !titans && !soldat && !aotconfig &&!item.isSimilar(GUIItems.getCantStartGameButton()) && !item.isSimilar(GUIItems.getStartGameButton())) {
                                String name = item.getItemMeta().getDisplayName();
                                GameState.Roles role = GameState.Roles.valueOf(name);
                                if (action.equals(InventoryAction.PICKUP_ALL)) {
                                    gameState.addInAvailableRoles(role, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(role)+1));
                                } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                    gameState.addInAvailableRoles(role, Math.max(0, gameState.getAvailableRoles().get(role)-1));
                                }else {
                                    event.setCancelled(true);
                                }
                            }
                            if (item.isSimilar(GUIItems.getStartGameButton()) && gameState.gameCanLaunch) HubListener.getInstance().StartGame(player);

                        } else {
                            if (ChatRank.isHost(player))player.openInventory(GUIItems.getSelectAOTInventory());Main.getInstance().getInventories().updateAOTInventory(player);
                        }
                        for (UUID u : gameState.getInLobbyPlayers()) {
                            Player p = Bukkit.getPlayer(u);
                            if (p == null)continue;
                            Main.getInstance().getInventories().updateAOTSoloInventory(p);
                        }
                        if (mahr) {
                            player.openInventory(GUIItems.getMahrGui());
                            Main.getInstance().getInventories().updateMahrInventory(player);
                        }
                        if (titans) {
                            player.openInventory(GUIItems.getSecretTitansGui());
                            Main.getInstance().getInventories().updateSecretTitansInventory(player);
                        }
                        if (solo) {
                            player.openInventory(GUIItems.getAOTSoloSelectGUI());
                            Main.getInstance().getInventories().updateAOTSoloInventory(player);
                        }
                        if (soldat) {
                            player.openInventory(GUIItems.getSecretSoldatGui());
                            Main.getInstance().getInventories().updateSoldatInventory(player);
                        }
                        if (aotconfig) {
                            player.openInventory(GUIItems.getConfigurationAOT());
                            Main.getInstance().getInventories().updateAOTConfiguration(player);
                        }
                        event.setCancelled(true);
                        break;
                    case "§fAOT§7 -> §eSolo":
                        if (item.getItemMeta() == null)return;
                        if (item.getItemMeta().getDisplayName() == null)return;
                        if (item.getType() == Material.AIR)return;
                        if (item.isSimilar(GUIItems.getx())) {
                            event.setCancelled(true);
                            return;
                        }
                        if (!ChatRank.isHost(player)) {
                            event.setCancelled(true);
                            return;
                        }
                        if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
                            if (!item.isSimilar(GUIItems.getOrangeStainedGlassPane()) && !solo && !mahr && !titans && !soldat && !item.isSimilar(GUIItems.getCantStartGameButton()) && !item.isSimilar(GUIItems.getStartGameButton())) {
                                for (GameState.Roles roles : GameState.Roles.values()) {
                                    if (item.getItemMeta().getDisplayName().equalsIgnoreCase(roles.getItem().getItemMeta().getDisplayName())) {
                                        if (action.equals(InventoryAction.PICKUP_ALL)) {
                                            gameState.addInAvailableRoles(roles, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(roles)+1));
                                        } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                            gameState.addInAvailableRoles(roles, Math.max(0, gameState.getAvailableRoles().get(roles)-1));
                                        }
                                    }
                                }
                            }
                            if (item.isSimilar(GUIItems.getStartGameButton()) && gameState.gameCanLaunch) HubListener.getInstance().StartGame(player);

                        } else {
                            for (UUID u : gameState.getInLobbyPlayers()) {
                                Player p = Bukkit.getPlayer(u);
                                if (p == null)continue;
                                if (p == event.getWhoClicked()) {
                                    if (ChatRank.isHost(player)){
                                        p.openInventory(GUIItems.getSelectAOTInventory());
                                        Main.getInstance().getInventories().updateAOTInventory(player);
                                    }
                                    if (!p.isOp() && p.getOpenInventory() != null && p.getInventory() != null) p.closeInventory();
                                }
                            }
                        }
                        for (UUID u : gameState.getInLobbyPlayers()) {
                            Player p = Bukkit.getPlayer(u);
                            if (p == null)continue;
                            Main.getInstance().getInventories().updateDSSoloInventory(p);
                        }
                        if (solo) {
                            player.openInventory(GUIItems.getDSSoloSelectGUI());
                            Main.getInstance().getInventories().updateDSSoloInventory(player);
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
                        event.setCancelled(true);
                        break;
                    case "Configuration -> AOT":
                        if (!item.hasItemMeta())return;
                        if (!item.getItemMeta().hasDisplayName())return;
                        if (item.getType() == Material.AIR)return;
                        String name = item.getItemMeta().getDisplayName();
                        if (name.equals("§rCooldown Equipement Tridimentionnel")) {
                            if (action.equals(InventoryAction.PICKUP_ALL)) {
                                gameState.TridiCooldown+=1;
                            }else {
                                if (gameState.TridiCooldown > 0) {
                                    gameState.TridiCooldown-=1;
                                }
                            }
                        }
                        if (name.equals("§rEquipement Tridimentionnel")){
                            gameState.rod = !gameState.rod;
                        }
                        if (name.equals("§r§6Lave§f pour les titans (transformé)")) {
                            gameState.LaveTitans = !gameState.LaveTitans;
                        }
                        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                            player.openInventory(GUIItems.getMahrGui());
                            Main.getInstance().getInventories().updateMahrInventory(player);
                        }
                        Main.getInstance().getInventories().updateAOTConfiguration(player);
                        event.setCancelled(true);
                        break;
                }
            }
        }
    }

}