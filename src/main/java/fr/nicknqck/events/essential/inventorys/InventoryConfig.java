package fr.nicknqck.events.essential.inventorys;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.utils.rank.ChatRank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class InventoryConfig implements Listener {
    private final GameState gameState;
    public InventoryConfig(GameState gameState) {
        this.gameState = gameState;
        new HubConfig(gameState);
    }
    @EventHandler
    private void onInventoryClick(InventoryClickEvent event){
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().getTitle() == null)return;
        if (event.getClickedInventory().getTitle().equals("§fConfiguration§7 ->§6 Inventaire") && event.getWhoClicked() instanceof Player){
            ItemStack item = event.getCurrentItem();
            Player player = (Player) event.getWhoClicked();
            if (item == null)return;
            if (!item.hasItemMeta())return;
            InventoryAction action = event.getAction();
            event.setCancelled(true);
            if (item.getType() == Material.DIAMOND_HELMET) {
                if (action.equals(InventoryAction.PICKUP_ALL)) {
                    Main.getInstance().getGameConfig().getStuffConfig().setProtectionChestplate(Math.min(4, Main.getInstance().getGameConfig().getStuffConfig().getProtectionChestplate()+1));
                }else {
                    Main.getInstance().getGameConfig().getStuffConfig().setProtectionChestplate(Math.max(1, Main.getInstance().getGameConfig().getStuffConfig().getProtectionChestplate()-1));
                }
            }
            if (item.getType() == Material.DIAMOND_CHESTPLATE) {
                if (action.equals(InventoryAction.PICKUP_ALL)) {
                    Main.getInstance().getGameConfig().getStuffConfig().setProtectionChestplate(Math.min(4, Main.getInstance().getGameConfig().getStuffConfig().getProtectionChestplate()+1));
                }else {
                    Main.getInstance().getGameConfig().getStuffConfig().setProtectionChestplate(Math.max(1, Main.getInstance().getGameConfig().getStuffConfig().getProtectionChestplate()-1));
                }
            }
            if (item.getType() == Material.IRON_LEGGINGS) {
                if (action.equals(InventoryAction.PICKUP_ALL)) {
                    Main.getInstance().getGameConfig().getStuffConfig().setProtectionLeggings(Math.min(4, Main.getInstance().getGameConfig().getStuffConfig().getProtectionLeggings()+1));
                }else {
                    Main.getInstance().getGameConfig().getStuffConfig().setProtectionLeggings(Math.max(1, Main.getInstance().getGameConfig().getStuffConfig().getProtectionLeggings()-1));
                }
            }
            if (item.getType() == Material.DIAMOND_BOOTS) {
                if (action.equals(InventoryAction.PICKUP_ALL)) {
                    Main.getInstance().getGameConfig().getStuffConfig().setProtectionBoost(Math.min(4, Main.getInstance().getGameConfig().getStuffConfig().getProtectionBoost()+1));
                } else {
                    Main.getInstance().getGameConfig().getStuffConfig().setProtectionBoost(Math.max(1, Main.getInstance().getGameConfig().getStuffConfig().getProtectionBoost()-1));
                }
            }
            if (item.getItemMeta().hasDisplayName()) {
                if (item.getItemMeta().getDisplayName().equalsIgnoreCase("§r§fNombre de pomme d'§eor")) {
                    if (action.equals(InventoryAction.PICKUP_ALL)) {
                        if (Main.getInstance().getGameConfig().getStuffConfig().getNmbGap() != 64) {
                            Main.getInstance().getGameConfig().getStuffConfig().setNmbGap(Main.getInstance().getGameConfig().getStuffConfig().getNmbGap()+1);
                        } else {
                            player.sendMessage("Vous avez déjà atteint le nombre maximum de Pomme en Or ("+ ChatColor.GOLD+"64"+ChatColor.RESET+")");
                        }
                    } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                        if (Main.getInstance().getGameConfig().getStuffConfig().getNmbGap() != Main.getInstance().getGameConfig().getStuffConfig().getMinGap()) {
                            Main.getInstance().getGameConfig().getStuffConfig().setNmbGap(Main.getInstance().getGameConfig().getStuffConfig().getNmbGap()-1);
                        } else {
                            player.sendMessage("Vous avez déjà atteint le nombre minimum de Pomme en Or ("+ChatColor.GOLD+"12"+ChatColor.RESET+")");
                        }
                    }
                }
            }
            if (item.isSimilar(GUIItems.getdiamondsword())) {
                if (action.equals(InventoryAction.PICKUP_ALL)) {
                    if (Main.getInstance().getGameConfig().getStuffConfig().getSharpness() != 5) {
                        Main.getInstance().getGameConfig().getStuffConfig().setSharpness(Main.getInstance().getGameConfig().getStuffConfig().getSharpness()+1);
                    } else {
                        player.sendMessage("Vous avez déjà atteint la limite de sharpness maximal");
                    }
                } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                    if (Main.getInstance().getGameConfig().getStuffConfig().getSharpness() != 1) {
                        Main.getInstance().getGameConfig().getStuffConfig().setSharpness(Main.getInstance().getGameConfig().getStuffConfig().getSharpness()-1);
                    } else {
                        player.sendMessage("Vous avez déjà atteint la limite de sharpness minimal");
                    }
                }
            }
            if (item.isSimilar(GUIItems.getblock())) {
                if (action.equals(InventoryAction.PICKUP_ALL)) {
                    if (Main.getInstance().getGameConfig().getStuffConfig().getNmbblock() != 4) {
                        Main.getInstance().getGameConfig().getStuffConfig().setNmbblock(Main.getInstance().getGameConfig().getStuffConfig().getNmbblock()+1);
                    } else {
                        player.sendMessage("Vous avez déjà atteint la limite de block");
                    }
                } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                    if (Main.getInstance().getGameConfig().getStuffConfig().getNmbblock() != 1) {
                        Main.getInstance().getGameConfig().getStuffConfig().setNmbblock(Main.getInstance().getGameConfig().getStuffConfig().getNmbblock()-1);
                    } else {
                        player.sendMessage("Vous avez déjà atteint la limite de block");
                    }
                }
            }
            if (item.isSimilar(GUIItems.getbow())) {
                if (action.equals(InventoryAction.PICKUP_ALL)) {
                    if (Main.getInstance().getGameConfig().getStuffConfig().getPower() != 5) {
                        Main.getInstance().getGameConfig().getStuffConfig().setPower(Main.getInstance().getGameConfig().getStuffConfig().getPower()+1);
                    } else {
                        player.sendMessage("Vous avez déjà atteint la limite de power");
                    }
                } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                    if (Main.getInstance().getGameConfig().getStuffConfig().getPower() != 1) {
                        Main.getInstance().getGameConfig().getStuffConfig().setPower(Main.getInstance().getGameConfig().getStuffConfig().getPower()-1);
                    } else {
                        player.sendMessage("Vous avez déjà atteint la limite minimal de power");
                    }
                }

            }
            if (item.isSimilar(GUIItems.getEnderPearl())) {
                if (action.equals(InventoryAction.PICKUP_ALL)) {
                    if (GameState.pearl == 0) {
                        GameState.pearl++;
                    } else {
                        player.sendMessage("Vous avez déjà atteint la limite maximum d'ender pearl");
                    }
                } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                    if (GameState.pearl == 1) {
                        GameState.pearl--;
                    } else {
                        player.sendMessage("Vous avez déjà atteint la limite minimum d'ender pearl");
                    }
                }
            }
            if (item.isSimilar(GUIItems.geteau())) {
                if (action.equals(InventoryAction.PICKUP_ALL)) {
                    if (GameState.eau != 4) {
                        GameState.eau++;
                    } else {
                        player.sendMessage("Vous avez déjà atteint la limite de sceau d'eau");
                    }
                } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                    if (GameState.eau != 1) {
                        GameState.eau--;
                    } else {
                        player.sendMessage("Vous avez déjà atteint la limite minimal de sceau d'eau");
                    }
                }

            }
            if (item.isSimilar(GUIItems.getlave())) {
                if (action.equals(InventoryAction.PICKUP_ALL)) {
                    if (GameState.lave != 4) {
                        GameState.lave++;
                    } else {
                        player.sendMessage("Vous avez déjà atteint la limite de sceau de lave");
                    }
                } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                    if (GameState.lave != 1) {
                        GameState.lave--;
                    } else {
                        player.sendMessage("Vous avez déjà atteint la limite minimal de sceau de lave");
                    }
                }
            }
            if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                for (UUID u : gameState.getInLobbyPlayers()) {
                    Player p = Bukkit.getPlayer(u);
                    if (p == null)continue;
                    if (p == event.getWhoClicked()) {
                        if (ChatRank.isHost(player))p.openInventory(GUIItems.getAdminWatchGUI());
                        if (!p.isOp() && p.getOpenInventory() != null && p.getInventory() != null) p.closeInventory();
                    }
                }
            } else {
                if (item.getType() == Material.ARROW) {
                    if (action.equals(InventoryAction.PICKUP_ALL)) {
                        Main.getInstance().getGameConfig().getStuffConfig().setNmbArrow(Math.min(64, Main.getInstance().getGameConfig().getStuffConfig().getNmbArrow()+1));
                    } else {
                        Main.getInstance().getGameConfig().getStuffConfig().setNmbArrow(Math.max(1, Main.getInstance().getGameConfig().getStuffConfig().getNmbArrow()-1));
                    }
                }
            }
        }
    }

}