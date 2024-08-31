package fr.nicknqck.events.essential.inventorys;

import fr.nicknqck.Border;
import fr.nicknqck.GameState;
import fr.nicknqck.HubListener;
import fr.nicknqck.Main;
import fr.nicknqck.events.Events;
import fr.nicknqck.events.chat.Chat;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.items.Items;
import fr.nicknqck.pregen.PregenerationTask;
import fr.nicknqck.scenarios.Scenarios;
import fr.nicknqck.scenarios.impl.AntiPvP;
import fr.nicknqck.scenarios.impl.CutClean;
import fr.nicknqck.scenarios.impl.DiamondLimit;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.rank.ChatRank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class HubConfig implements Listener {

    private final GameState gameState;

    public HubConfig(GameState gameState) {
        this.gameState = gameState;
        EventUtils.registerEvents(this);
    }
    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player))return;
        if (gameState.getServerState() != GameState.ServerStates.InLobby) return;
        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getClickedInventory();
        InventoryAction action = event.getAction();
        if (inv != null && event.getCurrentItem() != null) {
            ItemStack item = event.getCurrentItem();
            if (!item.hasItemMeta())return;
            switch (inv.getTitle()) {
                case "§fConfiguration":
                    if (item.isSimilar(GUIItems.getStartGameButton()) && ChatRank.isHost(player)) {
                        HubListener.getInstance().StartGame(player);
                    } else if (item.isSimilar(GUIItems.getSelectRoleButton())  &&ChatRank.isHost(player)) {
                        player.openInventory(GUIItems.getRoleSelectGUI());
                        Main.getInstance().getInventories().updateRoleInventory(player); //Ouvre le menu role
                    }  else if (item.isSimilar(GUIItems.getSelectScenarioButton())  && ChatRank.isHost(player) ) {
                        player.openInventory(GUIItems.getScenarioGUI());
                        Main.getInstance().getInventories().updateScenarioInventory(player); //Ouvre le menu des scenarios
                    } else if (item.isSimilar(GUIItems.getSelectConfigButton())  && ChatRank.isHost(player)) {
                        player.openInventory(GUIItems.getConfigSelectGUI());
                        Main.getInstance().getInventories().updateConfigInventory(player); //Ouvre le menu permettant de configurer l'essentiel de la partie
                    } else if (item.isSimilar(GUIItems.getSelectInvsButton())  && ChatRank.isHost(player)) {
                        player.openInventory(GUIItems.getSelectInventoryGUI());
                        Main.getInstance().getInventories().updateSelectInventory(player); //Ouvre le menu pour config l'inventaire
                    } else if (item.isSimilar(AntiPvP.getlobbypvp())||item.isSimilar(AntiPvP.getnotlobbypvp())  && ChatRank.isHost(player)) {
                        if (AntiPvP.isAntipvplobby()) {
                            AntiPvP.setAntipvplobby(false);
                            player.sendMessage("Vous venez d'activer le PvP dans le Lobby");
                            Bukkit.broadcastMessage("Un administrateur à activer le PvP dans le Lobby");
                        } else {
                            AntiPvP.setAntipvplobby(true);
                            player.sendMessage("Vous venez de désactiver le PvP dans le lobby");
                            Bukkit.broadcastMessage("Un administrateur à desactiver le PvP dans le Lobby");
                        }
                    }  else if (item.isSimilar(GUIItems.getCrit(gameState))  && ChatRank.isHost(player)) {
                        if (action.equals(InventoryAction.PICKUP_ALL)) {
                            if (gameState.critP < 50) {
                                gameState.critP+=1;
                            }
                        } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                            if (gameState.getCritP() > 0) {
                                gameState.critP-=1;
                            }
                        }
                    } else if (item.isSimilar(Chat.getColoritem())) {
                        if (action.equals(InventoryAction.PICKUP_ALL)) {
                            ChatColor c = Chat.getopColor();
                            if (c == ChatColor.RED) Chat.setopColor(ChatColor.GOLD);
                            if (c == ChatColor.GOLD) Chat.setopColor(ChatColor.YELLOW);
                            if (c == ChatColor.YELLOW) Chat.setopColor(ChatColor.DARK_GREEN);
                            if (c == ChatColor.DARK_GREEN) Chat.setopColor(ChatColor.GREEN);
                            if (c == ChatColor.GREEN) Chat.setopColor(ChatColor.AQUA);
                            if (c == ChatColor.AQUA) Chat.setopColor(ChatColor.DARK_AQUA);
                            if (c == ChatColor.DARK_AQUA) Chat.setopColor(ChatColor.DARK_BLUE);
                            if (c == ChatColor.DARK_BLUE) Chat.setopColor(ChatColor.BLUE);
                            if (c == ChatColor.BLUE) Chat.setopColor(ChatColor.LIGHT_PURPLE);
                            if (c == ChatColor.LIGHT_PURPLE) Chat.setopColor(ChatColor.DARK_PURPLE);
                            if (c == ChatColor.DARK_PURPLE) Chat.setopColor(ChatColor.WHITE);
                            if (c == ChatColor.WHITE) Chat.setopColor(ChatColor.GRAY);
                            if (c == ChatColor.GRAY) Chat.setopColor(ChatColor.DARK_GRAY);
                            if (c == ChatColor.DARK_GRAY) Chat.setopColor(ChatColor.BLACK);
                            if (c == ChatColor.BLACK) Chat.setopColor(ChatColor.DARK_RED);
                            if (c == ChatColor.DARK_RED) Chat.setopColor(ChatColor.RED);
                        } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                            ChatColor c = Chat.getopColor();
                            if (c == ChatColor.RED) Chat.setopColor(ChatColor.DARK_RED);
                            if (c == ChatColor.DARK_RED) Chat.setopColor(ChatColor.BLACK);
                            if (c == ChatColor.BLACK) Chat.setopColor(ChatColor.DARK_GRAY);
                            if (c == ChatColor.DARK_GRAY) Chat.setopColor(ChatColor.GRAY);
                            if (c == ChatColor.GRAY) Chat.setopColor(ChatColor.WHITE);
                            if (c == ChatColor.WHITE)Chat.setopColor(ChatColor.DARK_PURPLE);
                            if (c == ChatColor.DARK_PURPLE) Chat.setopColor(ChatColor.LIGHT_PURPLE);
                            if (c == ChatColor.LIGHT_PURPLE) Chat.setopColor(ChatColor.BLUE);
                            if (c == ChatColor.BLUE) Chat.setopColor(ChatColor.DARK_BLUE);
                            if (c == ChatColor.DARK_BLUE) Chat.setopColor(ChatColor.DARK_AQUA);
                            if (c == ChatColor.DARK_AQUA) Chat.setopColor(ChatColor.AQUA);
                            if (c == ChatColor.AQUA) Chat.setopColor(ChatColor.GREEN);
                            if (c == ChatColor.GREEN) Chat.setopColor(ChatColor.DARK_GREEN);
                            if (c == ChatColor.DARK_GREEN) Chat.setopColor(ChatColor.YELLOW);
                            if (c == ChatColor.YELLOW) Chat.setopColor(ChatColor.GOLD);
                            if (c == ChatColor.GOLD) Chat.setopColor(ChatColor.RED);
                        }
                    } else if (item.isSimilar(GUIItems.getPregen(gameState))) {
                        if (!gameState.hasPregen){
                            new PregenerationTask(Main.getInstance().getWorldManager().getGameWorld(), Border.getMaxBorderSize());
                            gameState.hasPregen = true;
                        }
                    }else if (item.isSimilar(GUIItems.getSelectEventButton())) {
                        player.openInventory(GUIItems.getEventSelectGUI());
                        Main.getInstance().getInventories().updateEventInventory(player);
                    } else if (item.getType().equals(Material.GRASS)) {
                        event.getWhoClicked().closeInventory();
                        Main.getInstance().initGameWorld();
                        event.getWhoClicked().sendMessage("§7Vous avez réinitialiser le monde de jeu.");
                        if (gameState.hasPregen) {
                            gameState.hasPregen = false;
                        }
                    }
                    if (item.isSimilar(GUIItems.getx())) player.closeInventory();
                    event.setCancelled(true);
                    break;
                case "Séléction du mode de jeu":
                    if (item.getType() != Material.AIR) {
                        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                            player.openInventory(GUIItems.getRoleSelectGUI());
                            Main.getInstance().getInventories().updateRoleInventory(player);
                        }
                        for (GameState.MDJ mdj : GameState.MDJ.values()) {
                            if (item.isSimilar(mdj.getItem())) {
                                if (gameState.getMdj().equals(mdj)) {
                                    gameState.setMdj(GameState.MDJ.Aucun);
                                    gameState.updateGameCanLaunch();
                                }else {
                                    gameState.setMdj(GameState.MDJ.Aucun);
                                    gameState.setMdj(mdj);
                                }
                            }
                        }
                    }
                    for (UUID u : gameState.getInLobbyPlayers()) {
                        Player p = Bukkit.getPlayer(u);
                        if (p == null)continue;
                        Main.getInstance().getInventories().updateSelectMDJ(p);
                    }
                    event.setCancelled(true);
                    break;
                case "§fConfiguration§7 -> §6Événements":
                    if (item.getType() != Material.AIR) {
                        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                            player.openInventory(GUIItems.getAdminWatchGUI());
                            Main.getInstance().getInventories().updateAdminInventory(player);
                        } else {
                            for (Events e : Events.values()) {
                                if (item.getItemMeta().getDisplayName().equals(e.getName())) {
                                    if (e.equals(Events.DemonKingTanjiro)) {
                                        if (action == InventoryAction.PICKUP_ALL) {
                                            gameState.DKminTime += 60;
                                        }
                                        if (action == InventoryAction.PICKUP_HALF) {
                                            if (gameState.DKminTime > 60) {
                                                gameState.DKminTime -= 60;
                                            }
                                        }
                                        if (action == InventoryAction.DROP_ONE_SLOT) {
                                            if (gameState.DKTProba == 0) {
                                                gameState.DKTProba = 101;
                                            }
                                            if (gameState.DKTProba > 0) {
                                                gameState.DKTProba--;
                                            }
                                        }
                                        if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                                            if (gameState.DKTProba == 100) {
                                                gameState.DKTProba = -1;
                                            }
                                            if (gameState.DKTProba < 100) {
                                                gameState.DKTProba++;
                                            }
                                        }
                                    }
                                    if (e.equals(Events.AkazaVSKyojuro)) {
                                        if (action == InventoryAction.PICKUP_ALL) {
                                            gameState.AkazaVsKyojuroTime += 60;
                                        }
                                        if (action == InventoryAction.PICKUP_HALF) {
                                            if (gameState.AkazaVsKyojuroTime > 60) {
                                                gameState.AkazaVsKyojuroTime -= 60;
                                            }
                                        }
                                        if (action == InventoryAction.DROP_ONE_SLOT) {
                                            if (gameState.AkazaVSKyojuroProba == 0) {
                                                gameState.AkazaVSKyojuroProba = 101;
                                            }
                                            if (gameState.AkazaVSKyojuroProba > 0) {
                                                gameState.AkazaVSKyojuroProba--;
                                            }
                                        }
                                        if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                                            if (gameState.AkazaVSKyojuroProba == 100) {
                                                gameState.AkazaVSKyojuroProba = -1;
                                            }
                                            if (gameState.AkazaVSKyojuroProba < 100) {
                                                gameState.AkazaVSKyojuroProba++;
                                            }
                                        }
                                    }
                                    if (e.equals(Events.Alliance)) {
                                        if (action == InventoryAction.PICKUP_ALL) {
                                            gameState.AllianceTime += 60;
                                        }
                                        if (action == InventoryAction.PICKUP_HALF) {
                                            if (gameState.AllianceTime > 60) {
                                                gameState.AllianceTime -= 60;
                                            }
                                        }
                                        if (action == InventoryAction.DROP_ONE_SLOT) {
                                            if (gameState.AllianceProba == 0) {
                                                gameState.AllianceProba = 101;
                                            }
                                            if (gameState.AllianceProba > 0) {
                                                gameState.AllianceProba--;
                                            }
                                        }
                                        if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                                            if (gameState.AllianceProba == 100) {
                                                gameState.AllianceProba = -1;
                                            }
                                            if (gameState.AllianceProba < 100) {
                                                gameState.AllianceProba++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    for (UUID u : gameState.getInLobbyPlayers()) {
                        Player p = Bukkit.getPlayer(u);
                        if (p == null)continue;
                        Main.getInstance().getInventories().updateEventInventory(p);
                    }
                    event.setCancelled(true);
                    break;
                case "§fConfiguration§7 -> §6scenarios":
                    for (Scenarios scenario : Scenarios.values()){
                        if (item.isSimilar(scenario.getScenarios().getAffichedItem())){
                            scenario.getScenarios().setAction(action);
                            scenario.getScenarios().onClick(player);
                        }
                    }
                    player.updateInventory();
                    if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                        for (UUID u : gameState.getInLobbyPlayers()) {
                            Player p = Bukkit.getPlayer(u);
                            if (p == null)continue;
                            if (p == event.getWhoClicked()) {
                                if (ChatRank.isHost(player))p.openInventory(GUIItems.getAdminWatchGUI());
                                if (!p.isOp() && p.getOpenInventory() != null && p.getInventory() != null) p.closeInventory();
                            }
                        }
                    }
                    if (item.isSimilar(DiamondLimit.ChangeDiamond())) {
                        if (action.equals(InventoryAction.PICKUP_ALL)) {
                            if (DiamondLimit.getmaxdiams() != 32) {
                                DiamondLimit.setMaxDiams(DiamondLimit.getmaxdiams()+1);
                            } else {
                                player.sendMessage(DiamondLimit.DM()+"Vous avez déjà atteint le nombre maximum de la "+ChatColor.GOLD+"DiamondLimit");
                            }
                        } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                            if (DiamondLimit.getmaxdiams() != 1) {
                                DiamondLimit.setMaxDiams(DiamondLimit.getmaxdiams() - 1);
                            } else {
                                player.sendMessage(DiamondLimit.DM()+"Vous avez déjà atteint le nombre minimum de la "+ChatColor.GOLD+"DiamondLimit");
                            }
                        }

                    }
                    event.setCancelled(true);
                    break;
                case "§bCutClean":
                    if (action.equals(InventoryAction.PICKUP_ALL)||action.equals(InventoryAction.PICKUP_HALF)) {
                        InventoryAction g = InventoryAction.PICKUP_ALL;
                        String name = item.getItemMeta().getDisplayName();
                        if (name.startsWith("Point")) {
                            if (item.isSimilar(CutClean.getXpCharbon(gameState))) {
                                if (action == g) {
                                    gameState.xpcharbon++;
                                } else {
                                    gameState.xpcharbon--;
                                }
                            }
                            if (item.isSimilar(CutClean.getXpFer(gameState))) {
                                if (action == g) {
                                    gameState.xpfer++;
                                } else {
                                    gameState.xpfer--;
                                }
                            }
                            if (item.isSimilar(CutClean.getXpOr(gameState))) {
                                if (action == g) {
                                    gameState.xpor++;
                                } else {
                                    gameState.xpor--;
                                }
                            }
                            if (item.isSimilar(CutClean.getXpDiams(gameState))) {
                                if (action == g) {
                                    gameState.xpdiams++;
                                } else {
                                    gameState.xpdiams--;
                                }
                            }
                            if (gameState.xpcharbon<1)gameState.xpcharbon++;
                            if (gameState.xpfer<1)gameState.xpfer++;
                            if (gameState.xpor<1)gameState.xpor++;
                            if (gameState.xpdiams<1)gameState.xpdiams++;
                        } else {
                            if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                                for (UUID u : gameState.getInLobbyPlayers()) {
                                    Player p = Bukkit.getPlayer(u);
                                    if (p == null)continue;
                                    if (p == event.getWhoClicked()) {
                                        if (ChatRank.isHost(player))p.openInventory(GUIItems.getScenarioGUI()); Main.getInstance().getInventories().updateScenarioInventory(player);
                                        if (!p.isOp() && p.getOpenInventory() != null && p.getInventory() != null) p.closeInventory();
                                    }
                                }
                            }
                        }
                    }
                    player.updateInventory();
                    Main.getInstance().getInventories().updateCutCleanInventory(player);
                    event.setCancelled(true);
                    break;
                case "Configuration de la partie":
                    if (item.getType() != Material.AIR) {
                        String name = item.getItemMeta().getDisplayName();
                        if (item.getType().equals(Material.WATER_BUCKET)) {
                            if (action.equals(InventoryAction.PICKUP_ALL)) {
                                if (Main.getInstance().getGameConfig().getWaterEmptyTiming() != 60) {
                                    Main.getInstance().getGameConfig().setWaterEmptyTiming(Main.getInstance().getGameConfig().getWaterEmptyTiming()+1);
                                }else {
                                    player.sendMessage("Timing maximal atteint !");
                                }
                            }else {
                                if (Main.getInstance().getGameConfig().getWaterEmptyTiming() != 0) {
                                    Main.getInstance().getGameConfig().setWaterEmptyTiming(Main.getInstance().getGameConfig().getWaterEmptyTiming()-1);
                                }else {
                                    player.sendMessage("Timing minimal atteint !");
                                }
                            }
                        }
                        if (item.getType().equals(Material.LAVA_BUCKET)) {
                            if (action.equals(InventoryAction.PICKUP_ALL)) {
                                if (Main.getInstance().getGameConfig().getLavaEmptyTiming() != 60) {
                                    Main.getInstance().getGameConfig().setLavaEmptyTiming(Main.getInstance().getGameConfig().getLavaEmptyTiming()+1);
                                }else {
                                    player.sendMessage("Timing maximal atteint !");
                                }
                            }else {
                                if (Main.getInstance().getGameConfig().getLavaEmptyTiming() != 0) {
                                    Main.getInstance().getGameConfig().setLavaEmptyTiming(Main.getInstance().getGameConfig().getLavaEmptyTiming()-1);
                                }else {
                                    player.sendMessage("Timing minimal atteint !");
                                }
                            }
                        }
                        if (name.contains("Taille de la bordure max")) {
                            if (action.equals(InventoryAction.PICKUP_ALL)) {
                                Border.setMaxBorderSize(Border.getMaxBorderSize()+50);
                            } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                Border.setMaxBorderSize(Border.getMaxBorderSize()-50);
                            }
                        }
                        if (name.contains("Taille de la bordure minimum")) {
                            if (action.equals(InventoryAction.PICKUP_ALL)) {
                                Border.setMinBorderSize(Math.min(Border.getMinBorderSize()+50, 2400));
                            } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                Border.setMinBorderSize(Border.getMinBorderSize()-50);
                            }
                        }
                        if (name.contains("Vitesse de la bordure")) {
                            if (action.equals(InventoryAction.PICKUP_ALL)) {
                                Border.setBorderSpeed(Math.min(Border.getBorderSpeed()+1, 10));
                            } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                Border.setBorderSpeed(Math.max(Border.getBorderSpeed()-1, 1));
                            }
                        }
                        if (name.contains("Temp avant activation du PVP")) {
                            if (action.equals(InventoryAction.PICKUP_ALL)) {
                                gameState.pvpTimer += 60;
                            } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                gameState.pvpTimer -= 60;
                            }
                        }
                        if (name.contains("Temp avant annonce des roles")) {
                            if (action.equals(InventoryAction.PICKUP_ALL)) {
                                gameState.roleTimer+=60;
                            } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                gameState.roleTimer-=60;
                            }
                        }
                        if (name.contains("Temp avant réduction de la bordure")) {
                            if (action.equals(InventoryAction.PICKUP_ALL)) {
                                Border.setTempReduction(Math.min(Border.getTempReduction()+60, 60*60));
                            } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                Border.setTempReduction(Math.max(Border.getTempReduction()-60, 0));
                            }
                        }
                        if (item.getType() == Material.REDSTONE) {
                            if (action.equals(InventoryAction.PICKUP_ALL)) {
                                if (gameState.TimingAssassin < 60*5) {
                                    gameState.TimingAssassin+=10;
                                }
                            } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                if (gameState.TimingAssassin > 10) {
                                    gameState.TimingAssassin-=10;
                                }
                            }
                        }
                        if (item.getType().equals(Material.TNT)) {
                            gameState.setTNTGrief(!gameState.isTNTGrief());
                        }
                        Border.setMaxBorderSize(Math.max(50, Math.min(Border.getMaxBorderSize(), 2400)));
                        Border.setMinBorderSize(Math.max(50, Math.min(Border.getMinBorderSize(), Border.getMaxBorderSize())));
                        gameState.pvpTimer = Math.max(0, Math.min(gameState.pvpTimer, 40*60));
                        gameState.roleTimer = Math.max(0, Math.min(gameState.roleTimer, 40*60));
                        if (name.contains("Durée du jour (et de la nuit)")) {
                            if (ChatRank.isHost(player)) {
                                if (action.equals(InventoryAction.PICKUP_ALL)) {
                                    gameState.timeday+=10;
                                    player.updateInventory();
                                    Main.getInstance().getInventories().updateConfigInventory(player);
                                } else {
                                    if (action.equals(InventoryAction.PICKUP_HALF)) {
                                        gameState.timeday-=10;
                                        player.updateInventory();
                                        Main.getInstance().getInventories().updateConfigInventory(player);
                                    }
                                }
                            }
                        }

                        if (item.isSimilar(GUIItems.getTabRoleInfo(gameState))) {
                            if (ChatRank.isHost(player)) {
                                if (!gameState.roletab) {
                                    player.sendMessage("Les roles seront maintenant afficher dans le tab");
                                    gameState.roletab = true;
                                } else {
                                    player.sendMessage("Les roles ne seront plus afficher dans le tab");
                                    gameState.roletab = false;
                                }
                                player.updateInventory();
                                Main.getInstance().getInventories().updateConfigInventory(player);
                            }
                        }
                        if (item.isSimilar(Items.geteclairmort())) {
                            if (ChatRank.isHost(player)) {
                                if (!gameState.morteclair) {
                                    player.sendMessage("Éclair à la mort est désormais§6 activé");
                                    gameState.morteclair = true;
                                } else {
                                    player.sendMessage("Éclair à la mort est désormais§6 désactivé");
                                    gameState.morteclair = false;
                                }
                            }
                        }
                        if (item.getItemMeta().getDisplayName().equals("§fBijus")) {
                            if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                                player.closeInventory();
                                player.openInventory(Bukkit.createInventory(player, 9*4, "Configuration ->§6 Bijus"));
                                Main.getInstance().getInventories().openConfigBijusInventory(player);
                            } else {
                                gameState.BijusEnable = !gameState.BijusEnable;
                            }
                        }
                        if (item.getItemMeta().getDisplayName().equals("§cInfection")) {
                            if (action.equals(InventoryAction.PICKUP_ALL)) {
                                if (gameState.timewaitingbeinfected < 60*20) {
                                    gameState.timewaitingbeinfected+=5;
                                }
                            }else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                if (gameState.timewaitingbeinfected > 5) {
                                    gameState.timewaitingbeinfected-=5;
                                }
                            }
                        }
                    }
                    for (UUID u : gameState.getInLobbyPlayers()) {
                        Player p = Bukkit.getPlayer(u);
                        if (p == null)continue;
                        Main.getInstance().getInventories().updateConfigInventory(p);
                    }
                    if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                        if (ChatRank.isHost(player)) player.openInventory(GUIItems.getAdminWatchGUI());
                    }
                    event.setCancelled(true);
                    break;
            }
        }
    }
}