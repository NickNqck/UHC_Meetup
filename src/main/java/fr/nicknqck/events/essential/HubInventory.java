package fr.nicknqck.events.essential;

import fr.nicknqck.Border;
import fr.nicknqck.GameState;
import fr.nicknqck.HubListener;
import fr.nicknqck.Main;
import fr.nicknqck.bijus.Bijus;
import fr.nicknqck.events.chat.Chat;
import fr.nicknqck.events.Events;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.items.Items;
import fr.nicknqck.pregen.PregenerationTask;
import fr.nicknqck.scenarios.Scenarios;
import fr.nicknqck.scenarios.impl.AntiPvP;
import fr.nicknqck.scenarios.impl.CutClean;
import fr.nicknqck.scenarios.impl.DiamondLimit;
import lombok.Getter;
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

public class HubInventory implements Listener {

    private final GameState gameState;
    @Getter
    private static HubInventory instance;

    public HubInventory(GameState gameState) {
        this.gameState = gameState;
        instance = this;
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryConfig(gameState), Main.getInstance());
    }
    @EventHandler
    private void OnInventoryClicked(InventoryClickEvent event) {
        if (gameState.getServerState() != GameState.ServerStates.InLobby) return;
        if (event.getWhoClicked() instanceof Player) {
            Player player = Bukkit.getPlayer(event.getWhoClicked().getName());
            Inventory inv = event.getClickedInventory();
            InventoryAction action = event.getAction();
            if (inv != null && event.getCurrentItem() != null) {
                if (event.getCurrentItem().isSimilar(GUIItems.getStartGameButton()) && gameState.gameCanLaunch) HubListener.getInstance().StartGame(player);
                final ItemStack item = event.getCurrentItem();
                final boolean mahr = item.isSimilar(GUIItems.getSelectMahrButton());
                final boolean sl = item.isSimilar(GUIItems.getSelectSlayersButton());
                final boolean d = item.isSimilar(GUIItems.getSelectDemonButton());
                final boolean solo = item.isSimilar(GUIItems.getSelectSoloButton());
                final boolean titans = item.isSimilar(GUIItems.getSelectTitanButton());
                final boolean soldat = item.isSimilar(GUIItems.getSelectSoldatButton());
                final boolean aotconfig = item.isSimilar(GUIItems.getSelectConfigAotButton());
                final boolean demon = item.isSimilar(GUIItems.getSelectDSButton());
                final boolean aot = item.isSimilar(GUIItems.getSelectAOTButton());
                final boolean ns = item.isSimilar(GUIItems.getSelectNSButton());
                final boolean akatsuki = item.isSimilar(GUIItems.getSelectAkatsukiButton());
                final boolean orochimaru = item.isSimilar(GUIItems.getSelectOrochimaruButton());
                final boolean brume = item.isSimilar(GUIItems.getSelectBrumeButton());
                final boolean shinobi = item.isSimilar(GUIItems.getSelectShinobiButton());
                final boolean kumo = item.isSimilar(GUIItems.getSelectKumogakureButton());
                if (!item.hasItemMeta())return;
                switch(inv.getTitle()) {
                    case "§fConfiguration":
                        if (item.isSimilar(GUIItems.getStartGameButton()) && (player.isOp() || gameState.getHost().contains(player.getUniqueId()) )) {
                            HubListener.getInstance().StartGame(player);
                        } else if (item.isSimilar(GUIItems.getSelectRoleButton())  && (player.isOp() || gameState.getHost().contains(player.getUniqueId()))) {
                            player.openInventory(GUIItems.getRoleSelectGUI());
                            Main.getInstance().getInventories().updateRoleInventory(player); //Ouvre le menu role
                        }  else if (item.isSimilar(GUIItems.getSelectScenarioButton())  && (player.isOp() || gameState.getHost().contains(player.getUniqueId()) ) ) {
                            player.openInventory(GUIItems.getScenarioGUI());
                            Main.getInstance().getInventories().updateScenarioInventory(player); //Ouvre le menu des scenarios
                        } else if (item.isSimilar(GUIItems.getSelectConfigButton())  && (player.isOp() || gameState.getHost().contains(player.getUniqueId()) )) {
                            player.openInventory(GUIItems.getConfigSelectGUI());
                            Main.getInstance().getInventories().updateConfigInventory(player); //Ouvre le menu permettant de configurer l'essentiel de la partie
                        } else if (item.isSimilar(GUIItems.getSelectInvsButton())  && (player.isOp() || gameState.getHost().contains(player.getUniqueId()) )) {
                            player.openInventory(GUIItems.getSelectInventoryGUI());
                            Main.getInstance().getInventories().updateSelectInventory(player); //Ouvre le menu pour config l'inventaire
                        } else if (item.isSimilar(AntiPvP.getlobbypvp())||item.isSimilar(AntiPvP.getnotlobbypvp())  && (player.isOp() || gameState.getHost().contains(player.getUniqueId()) )) {
                            if (AntiPvP.isAntipvplobby()) {
                                AntiPvP.setAntipvplobby(false);
                                player.sendMessage("Vous venez d'activer le PvP dans le Lobby");
                                Bukkit.broadcastMessage("Un administrateur à activer le PvP dans le Lobby");
                            } else {
                                AntiPvP.setAntipvplobby(true);
                                player.sendMessage("Vous venez de désactiver le PvP dans le lobby");
                                Bukkit.broadcastMessage("Un administrateur à desactiver le PvP dans le Lobby");
                            }
                        }  else if (item.isSimilar(GUIItems.getCrit(gameState))  && (player.isOp() || gameState.getHost().contains(player.getUniqueId()) )) {
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
                                new PregenerationTask(Main.getInstance().gameWorld, Border.getMaxBorderSize());
                                gameState.hasPregen = true;
                            }
                        }else if (item.isSimilar(GUIItems.getSelectEventButton())) {
                            player.openInventory(GUIItems.getEventSelectGUI());
                            Main.getInstance().getInventories().updateEventInventory(player);
                        }
                        if (item.isSimilar(GUIItems.getx())) player.closeInventory();
                        event.setCancelled(true);
                        break;
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
                                if (item.getType() == Material.BOOKSHELF) {
                                    Inventory inventaire = Bukkit.createInventory(player, 9, "Séléction du mode de jeu");
                                    player.openInventory(inventaire);
                                    Main.getInstance().getInventories().updateSelectMDJ(player);
                                }
                            } else {
                                if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                                    if ((player.isOp() || gameState.getHost().contains(player.getUniqueId()) ))player.openInventory(GUIItems.getAdminWatchGUI());
                                    if (!player.isOp() && player.getOpenInventory() != null && player.getInventory() != null) player.closeInventory();
                                }
                            }
                        }
                        for (Player p : gameState.getInLobbyPlayers()) {
                            Main.getInstance().getInventories().updateRoleInventory(p);
                        }
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
                        for (Player p : gameState.getInLobbyPlayers()) {
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
                        for (Player p : gameState.getInLobbyPlayers()) {
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
                            for (Player p : gameState.getInLobbyPlayers()) {
                                if (p == event.getWhoClicked()) {
                                    if ((player.isOp() || gameState.getHost().contains(player.getUniqueId()) ))p.openInventory(GUIItems.getAdminWatchGUI());
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
                                    for (Player p : gameState.getInLobbyPlayers()) {
                                        if (p == event.getWhoClicked()) {
                                            if ((player.isOp() || gameState.getHost().contains(player.getUniqueId()) ))p.openInventory(GUIItems.getScenarioGUI()); Main.getInstance().getInventories().updateScenarioInventory(player);
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
                    case "DemonSlayer ->§a Slayers":
                        if (item.getItemMeta() == null)return;
                        if (item.getItemMeta().getDisplayName() == null)return;
                        if (item.getType() == Material.AIR)return;
                        if (item.isSimilar(GUIItems.getx())) {
                            event.setCancelled(true);
                            return;
                        }
                        if (!event.getWhoClicked().isOp() && !gameState.getHost().contains(player.getUniqueId())) {
                            event.setCancelled(true);
                            return;
                        }
                        if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
                            if (!item.isSimilar(GUIItems.getGreenStainedGlassPane()) && !item.isSimilar(GUIItems.getSelectSoloButton()) && !item.isSimilar(GUIItems.getSelectDemonButton()) && !item.isSimilar(GUIItems.getCantStartGameButton()) && !item.isSimilar(GUIItems.getStartGameButton())) {
                                String name = item.getItemMeta().getDisplayName();
                                GameState.Roles role = GameState.Roles.valueOf(name);
                                if (action.equals(InventoryAction.PICKUP_ALL)) {
                                    gameState.addInAvailableRoles(role, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(role)+1));
                                } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                    gameState.addInAvailableRoles(role, Math.max(0, gameState.getAvailableRoles().get(role)-1));
                                }
                            }
                            if (item.isSimilar(GUIItems.getStartGameButton()) && gameState.gameCanLaunch) HubListener.getInstance().StartGame(player);
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

                        } else {
                            player.openInventory(GUIItems.getDemonSlayerInventory());
                            Main.getInstance().getInventories().updateDSInventory(player);
                        }
                        for (Player p : gameState.getInLobbyPlayers()) {
                            Main.getInstance().getInventories().updateSlayerInventory(p);
                        }
                        event.setCancelled(true);
                        break;
                    case "DemonSlayer -> §cDémons":
                        if (item.getItemMeta() == null)return;
                        if (item.getItemMeta().getDisplayName() == null)return;
                        if (item.getType() == Material.AIR)return;
                        if (item.isSimilar(GUIItems.getx())) {
                            event.setCancelled(true);
                            return;
                        }
                        if (!event.getWhoClicked().isOp() && !gameState.getHost().contains(player.getUniqueId())) {
                            event.setCancelled(true);
                            return;
                        }
                        if (!player.isOp() && !gameState.getHost().contains(player.getUniqueId()))return;
                        if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
                            if (!item.isSimilar(GUIItems.getRedStainedGlassPane()) && !item.isSimilar(GUIItems.getSelectSoloButton()) && !item.isSimilar(GUIItems.getSelectDemonButton()) && !item.isSimilar(GUIItems.getSelectSlayersButton())&& !item.isSimilar(GUIItems.getCantStartGameButton()) && !item.isSimilar(GUIItems.getStartGameButton())) {
                                String name = item.getItemMeta().getDisplayName();
                                GameState.Roles role = GameState.Roles.valueOf(name);
                                if (action.equals(InventoryAction.PICKUP_ALL)) {
                                    gameState.addInAvailableRoles(role, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(role)+1));
                                } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                    gameState.addInAvailableRoles(role, Math.max(0, gameState.getAvailableRoles().get(role)-1));
                                }
                            }
                            if (item.isSimilar(GUIItems.getStartGameButton()) && gameState.gameCanLaunch) HubListener.getInstance().StartGame(player);
                        } else {
                            player.openInventory(GUIItems.getDemonSlayerInventory());
                            Main.getInstance().getInventories().updateDSInventory(player);
                        }
                        for (Player p : gameState.getInLobbyPlayers()) {
                            Main.getInstance().getInventories().updateDemonInventory(p);
                        }
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

                        event.setCancelled(true);
                        break;
                    case "DemonSlayer -> §eSolo":
                        if (item.getItemMeta() == null)return;
                        if (item.getItemMeta().getDisplayName() == null)return;
                        if (item.getType() == Material.AIR)return;
                        if (item.isSimilar(GUIItems.getx())) {
                            event.setCancelled(true);
                            return;
                        }
                        if (!event.getWhoClicked().isOp() && !gameState.getHost().contains(player.getUniqueId())) {
                            event.setCancelled(true);
                            return;
                        }
                        if (!player.isOp() && !gameState.getHost().contains(player.getUniqueId()))return;
                        if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
                            if (!item.isSimilar(GUIItems.getOrangeStainedGlassPane()) && !solo && !d && !sl && !mahr && !titans && !soldat && !item.isSimilar(GUIItems.getCantStartGameButton()) && !item.isSimilar(GUIItems.getStartGameButton())) {
                                String name = item.getItemMeta().getDisplayName();
                                GameState.Roles role = GameState.Roles.valueOf(name);
                                if (action.equals(InventoryAction.PICKUP_ALL)) {
                                    gameState.addInAvailableRoles(role, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(role)+1));
                                } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                    gameState.addInAvailableRoles(role, Math.max(0, gameState.getAvailableRoles().get(role)-1));
                                }
                            }
                            if (item.isSimilar(GUIItems.getStartGameButton()) && gameState.gameCanLaunch) HubListener.getInstance().StartGame(player);

                        } else {
                            for (Player p : gameState.getInLobbyPlayers()) {
                                if (p == event.getWhoClicked()) {
                                    if ((player.isOp() || gameState.getHost().contains(player.getUniqueId()))) {
                                        p.openInventory(GUIItems.getDemonSlayerInventory());
                                        Main.getInstance().getInventories().updateDSInventory(player);
                                    }
                                    if (!p.isOp() && p.getOpenInventory() != null && p.getInventory() != null) p.closeInventory();
                                }
                            }
                        }
                        for (Player p : gameState.getInLobbyPlayers()) {
                            Main.getInstance().getInventories().updateDSSoloInventory(p);
                        }
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
                    case "Configuration de la partie":
                        if (item.getType() != Material.AIR) {
                            String name = item.getItemMeta().getDisplayName();
                            if (item.getType().equals(Material.WATER_BUCKET)) {
                                if (action.equals(InventoryAction.PICKUP_ALL)) {
                                    if (gameState.WaterEmptyTiming != 60) {
                                        gameState.WaterEmptyTiming+=1;
                                    }else {
                                        player.sendMessage("Timing maximal atteint !");
                                    }
                                }else {
                                    if (gameState.WaterEmptyTiming != 0) {
                                        gameState.WaterEmptyTiming-=1;
                                    }else {
                                        player.sendMessage("Timing minimal atteint !");
                                    }
                                }
                            }
                            if (item.getType().equals(Material.LAVA_BUCKET)) {
                                if (action.equals(InventoryAction.PICKUP_ALL)) {
                                    if (gameState.LavaEmptyTiming != 60) {
                                        gameState.LavaEmptyTiming+=1;
                                    }else {
                                        player.sendMessage("Timing maximal atteint !");
                                    }
                                }else {
                                    if (gameState.LavaEmptyTiming != 0) {
                                        gameState.LavaEmptyTiming-=1;
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
                                if (player.isOp() || gameState.getHost().contains(player.getUniqueId())) {
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
                                if (player.isOp() || gameState.getHost().contains(player.getUniqueId())) {
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
                                if (player.isOp() || gameState.getHost().contains(player.getUniqueId())) {
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
                        for (Player p : gameState.getInLobbyPlayers()) {
                            Main.getInstance().getInventories().updateConfigInventory(p);
                        }
                        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                            if ((player.isOp() || gameState.getHost().contains(player.getUniqueId())))player.openInventory(GUIItems.getAdminWatchGUI());
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
                        if (!event.getWhoClicked().isOp() && !gameState.getHost().contains(player.getUniqueId())) {
                            event.setCancelled(true);
                            return;
                        }
                        if (!player.isOp() && !gameState.getHost().contains(player.getUniqueId()))return;
                        if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
                            if (!item.isSimilar(GUIItems.getSBluetainedGlassPane()) && !solo && !d && !sl && !mahr && !titans && !soldat && !aotconfig &&!item.isSimilar(GUIItems.getCantStartGameButton()) && !item.isSimilar(GUIItems.getStartGameButton())) {
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
                            if ((player.isOp() || gameState.getHost().contains(player.getUniqueId()) ))player.openInventory(GUIItems.getSelectAOTInventory());Main.getInstance().getInventories().updateAOTInventory(player);
                        }
                        for (Player p : gameState.getInLobbyPlayers()) {
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
                    case "§fAOT§7 ->§c Titans":
                        if (item.getItemMeta() == null)return;
                        if (item.getItemMeta().getDisplayName() == null)return;
                        if (item.getType() == Material.AIR)return;
                        if (item.isSimilar(GUIItems.getx())) {
                            event.setCancelled(true);
                            return;
                        }
                        if (!event.getWhoClicked().isOp() && !gameState.getHost().contains(player.getUniqueId())) {
                            event.setCancelled(true);
                            return;
                        }
                        if (!player.isOp() && !gameState.getHost().contains(player.getUniqueId()))return;
                        if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
                            if (!item.isSimilar(GUIItems.getRedStainedGlassPane()) && !solo && !d && !sl && !mahr && !titans && !soldat && !aotconfig &&!item.isSimilar(GUIItems.getCantStartGameButton()) && !item.isSimilar(GUIItems.getStartGameButton())) {
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
                            if ((player.isOp() || gameState.getHost().contains(player.getUniqueId()) ))player.openInventory(GUIItems.getSelectAOTInventory());Main.getInstance().getInventories().updateAOTInventory(player);
                        }
                        for (Player p : gameState.getInLobbyPlayers()) {
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
                    case "§fAOT§7 ->§a Soldats":
                        if (item.getItemMeta() == null)return;
                        if (item.getItemMeta().getDisplayName() == null)return;
                        if (item.getType() == Material.AIR)return;
                        if (item.isSimilar(GUIItems.getx())) {
                            event.setCancelled(true);
                            return;
                        }
                        if (!event.getWhoClicked().isOp() && !gameState.getHost().contains(player.getUniqueId())) {
                            event.setCancelled(true);
                            return;
                        }
                        if (!player.isOp() && !gameState.getHost().contains(player.getUniqueId()))return;
                        if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
                            if (!item.isSimilar(GUIItems.getGreenStainedGlassPane()) && !solo && !d && !sl && !mahr && !titans && !soldat && !aotconfig &&!item.isSimilar(GUIItems.getCantStartGameButton()) && !item.isSimilar(GUIItems.getStartGameButton())) {
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
                            if ((player.isOp() || gameState.getHost().contains(player.getUniqueId()) ))player.openInventory(GUIItems.getSelectAOTInventory());Main.getInstance().getInventories().updateAOTInventory(player);
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
                        }else {
                            player.openInventory(GUIItems.getRoleSelectGUI());
                            Main.getInstance().getInventories().updateRoleInventory(player);
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
                        if (!event.getWhoClicked().isOp() && !gameState.getHost().contains(player.getUniqueId())) {
                            event.setCancelled(true);
                            return;
                        }
                        if (!player.isOp() && !gameState.getHost().contains(player.getUniqueId()))return;
                        if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
                            if (!item.isSimilar(GUIItems.getOrangeStainedGlassPane()) && !solo && !d && !sl && !mahr && !titans && !soldat && !item.isSimilar(GUIItems.getCantStartGameButton()) && !item.isSimilar(GUIItems.getStartGameButton())) {
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
                            for (Player p : gameState.getInLobbyPlayers()) {
                                if (p == event.getWhoClicked()) {
                                    if ((player.isOp() || gameState.getHost().contains(player.getUniqueId()))){
                                        p.openInventory(GUIItems.getSelectAOTInventory());
                                        Main.getInstance().getInventories().updateAOTInventory(player);
                                    }
                                    if (!p.isOp() && p.getOpenInventory() != null && p.getInventory() != null) p.closeInventory();
                                }
                            }
                        }
                        for (Player p : gameState.getInLobbyPlayers()) {
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
                    case "Configuration ->§6 Bijus":
                        if (!item.hasItemMeta())return;
                        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                            player.openInventory(GUIItems.getConfigSelectGUI());
                            Main.getInstance().getInventories().updateConfigInventory(player);
                            event.setCancelled(true);
                            return;
                        }
                        for (Bijus bijus : Bijus.values()) {
                            if (item.isSimilar(bijus.getBiju().getItemInMenu())) {
                                bijus.setEnable(!bijus.isEnable());
                            }
                        }

                        if (item.getItemMeta().hasDisplayName()){
                            if (item.getItemMeta().getDisplayName().equalsIgnoreCase("§r§fCoordonnée minimal de spawn des bijus")){
                                if (action == InventoryAction.PICKUP_ALL){
                                    Border.setMinBijuSpawn(Math.min(Border.getMinBijuSpawn()+50, Border.getMaxBijuSpawn()-50));
                                } else {
                                    Border.setMinBijuSpawn(Math.max(Border.getMinBijuSpawn()-50, Border.getMinBorderSize()+50));
                                }
                            }
                            if (item.getItemMeta().getDisplayName().equalsIgnoreCase("§r§fCoordonnée maximal de spawn des bijus")){
                                if (action == InventoryAction.PICKUP_ALL){
                                    Border.setMaxBijuSpawn(Math.min(Border.getMaxBijuSpawn()+50, Border.getMaxBorderSize()-50));
                                } else {
                                    Border.setMaxBijuSpawn(Math.max(Border.getMaxBijuSpawn()-50, Border.getMinBijuSpawn()+50));
                                }
                            }
                        }
                        Main.getInstance().getInventories().openConfigBijusInventory(player);
                        event.setCancelled(true);
                        break;
                    case "§aNaruto§7 ->§c Akatsuki":
                        if (!item.hasItemMeta())return;
                        if (!item.getItemMeta().hasDisplayName())return;
                        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                            player.openInventory(GUIItems.getSelectNSInventory());
                            Main.getInstance().getInventories().updateNSInventory(player);
                        } else {
                            if (gameState.getHost().contains(player.getUniqueId()) || player.isOp()) {
                                for (GameState.Roles roles : GameState.Roles.values()) {
                                    if (item.getItemMeta().getDisplayName().contains(roles.getItem().getItemMeta().getDisplayName())) {
                                        if (action.equals(InventoryAction.PICKUP_ALL)) {
                                            gameState.addInAvailableRoles(roles, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(roles)+1));
                                        } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                            gameState.addInAvailableRoles(roles, Math.max(0, gameState.getAvailableRoles().get(roles)-1));
                                        }
                                    }
                                }
                            }
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
                    case "§aNaruto§7 ->§5 Orochimaru":
                        if (!item.getItemMeta().hasDisplayName())return;
                        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                            player.openInventory(GUIItems.getRoleSelectGUI());
                            Main.getInstance().getInventories().updateRoleInventory(player);
                        } else {
                            if (gameState.getHost().contains(player.getUniqueId()) || player.isOp()) {
                                for (GameState.Roles roles : GameState.Roles.values()) {
                                    if (item.getItemMeta().getDisplayName().contains(roles.getItem().getItemMeta().getDisplayName())) {
                                        if (action.equals(InventoryAction.PICKUP_ALL)) {
                                            gameState.addInAvailableRoles(roles, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(roles)+1));
                                        } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                            gameState.addInAvailableRoles(roles, Math.max(0, gameState.getAvailableRoles().get(roles)-1));
                                        }
                                    }
                                }
                            }
                        }
                        event.setCancelled(true);
                        break;
                    case "§aNaruto§7 ->§e Solo":
                        if (!item.hasItemMeta())return;
                        if (!item.getItemMeta().hasDisplayName())return;
                        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                            player.openInventory(GUIItems.getRoleSelectGUI());
                            Main.getInstance().getInventories().updateRoleInventory(player);
                        } else {
                            if (item.isSimilar(GUIItems.getSelectJubiButton())) {
                                player.openInventory(GUIItems.getSelectNSJubiInventory());
                                Main.getInstance().getInventories().updateNSJubiInventory(player);
                                event.setCancelled(true);
                                return;
                            }
                            if (brume) {
                                player.openInventory(GUIItems.getSelectNSBrumeInventory());
                                Main.getInstance().getInventories().updateNSBrumeInventory(player);
                                event.setCancelled(true);
                                return;
                            }
                            if (kumo){
                                player.openInventory(Bukkit.createInventory(player, 54, "§eSolo§7 ->§6 Kumogakure"));
                                Main.getInstance().getInventories().updateNSKumogakure(player);
                                event.setCancelled(true);
                                return;
                            }
                            if (gameState.getHost().contains(player.getUniqueId()) || player.isOp()) {
                                for (GameState.Roles roles : GameState.Roles.values()) {
                                    if (item.getItemMeta().getDisplayName().contains(roles.getItem().getItemMeta().getDisplayName())) {
                                        if (action.equals(InventoryAction.PICKUP_ALL)) {
                                            gameState.addInAvailableRoles(roles, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(roles)+1));
                                        } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                            gameState.addInAvailableRoles(roles, Math.max(0, gameState.getAvailableRoles().get(roles)-1));
                                        }
                                    }
                                }
                            }
                        }
                        event.setCancelled(true);
                        break;
                    case "§eSolo§7 ->§d Jubi":
                        if (!item.hasItemMeta())return;
                        if (!item.getItemMeta().hasDisplayName())return;
                        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                            player.openInventory(GUIItems.getSelectNSSoloInventory());
                            Main.getInstance().getInventories().updateNSSoloInventory(player);
                        } else {
                            if (gameState.getHost().contains(player.getUniqueId()) || player.isOp()) {
                                for (GameState.Roles roles : GameState.Roles.values()) {
                                    if (item.getItemMeta().getDisplayName().contains(roles.getItem().getItemMeta().getDisplayName())) {
                                        if (action.equals(InventoryAction.PICKUP_ALL)) {
                                            gameState.addInAvailableRoles(roles, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(roles)+1));
                                        } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                            gameState.addInAvailableRoles(roles, Math.max(0, gameState.getAvailableRoles().get(roles)-1));
                                        }
                                    }
                                }
                            }
                        }
                        event.setCancelled(true);
                        break;
                    case "§eSolo§7 ->§b Zabuza et Haku":
                        if (!item.getItemMeta().hasDisplayName())return;
                        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                            player.openInventory(GUIItems.getSelectNSSoloInventory());
                            Main.getInstance().getInventories().updateNSSoloInventory(player);
                        } else {
                            if (gameState.getHost().contains(player.getUniqueId()) || player.isOp()) {
                                for (GameState.Roles roles : GameState.Roles.values()) {
                                    if (item.getItemMeta().getDisplayName().contains(roles.getItem().getItemMeta().getDisplayName())) {
                                        if (action.equals(InventoryAction.PICKUP_ALL)) {
                                            gameState.addInAvailableRoles(roles, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(roles)+1));
                                        } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                            gameState.addInAvailableRoles(roles, Math.max(0, gameState.getAvailableRoles().get(roles)-1));
                                        }
                                    }
                                }
                            }
                        }
                        event.setCancelled(true);
                        break;
                    case "§aNaruto§7 ->§a Shinobi":
                        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                            player.openInventory(GUIItems.getRoleSelectGUI());
                            Main.getInstance().getInventories().updateRoleInventory(player);
                        } else {
                            if (gameState.getHost().contains(player.getUniqueId()) || player.isOp()) {
                                for (GameState.Roles roles : GameState.Roles.values()) {
                                    if (item.getItemMeta().getDisplayName().contains(roles.getItem().getItemMeta().getDisplayName())) {
                                        if (action.equals(InventoryAction.PICKUP_ALL)) {
                                            gameState.addInAvailableRoles(roles, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(roles)+1));
                                        } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                            gameState.addInAvailableRoles(roles, Math.max(0, gameState.getAvailableRoles().get(roles)-1));
                                        }
                                    }
                                }
                            }
                        }
                        event.setCancelled(true);
                        break;
                    case "§eSolo§7 ->§6 Kumogakure":
                        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                            player.openInventory(GUIItems.getSelectNSSoloInventory());
                            Main.getInstance().getInventories().updateNSSoloInventory(player);
                        } else {
                            if (gameState.getHost().contains(player.getUniqueId()) || player.isOp()) {
                                for (GameState.Roles roles : GameState.Roles.values()) {
                                    if (item.getItemMeta().getDisplayName().contains(roles.getItem().getItemMeta().getDisplayName())) {
                                        if (action.equals(InventoryAction.PICKUP_ALL)) {
                                            gameState.addInAvailableRoles(roles, Math.min(gameState.getInLobbyPlayers().size(), gameState.getAvailableRoles().get(roles)+1));
                                        } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                            gameState.addInAvailableRoles(roles, Math.max(0, gameState.getAvailableRoles().get(roles)-1));
                                        }
                                    }
                                }
                            }
                        }
                        event.setCancelled(true);
                        break;
                    default:
                        break;
                }
                for (Player p : gameState.getInLobbyPlayers()) {
                    Main.getInstance().getInventories().menuUpdater(p);
                }
            }
        }
    }

}