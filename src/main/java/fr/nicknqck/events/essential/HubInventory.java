package fr.nicknqck.events.essential;

import fr.nicknqck.Border;
import fr.nicknqck.GameState;
import fr.nicknqck.HubListener;
import fr.nicknqck.Main;
import fr.nicknqck.bijus.Bijus;
import fr.nicknqck.chat.Chat;
import fr.nicknqck.events.Events;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.items.Items;
import fr.nicknqck.pregen.PregenerationTask;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.scenarios.Scenarios;
import fr.nicknqck.scenarios.impl.AntiPvP;
import fr.nicknqck.scenarios.impl.CutClean;
import fr.nicknqck.scenarios.impl.DiamondLimit;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.StringUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class HubInventory implements Listener {

    private final GameState gameState;
    @Getter
    private static HubInventory instance;

    public HubInventory(GameState gameState) {
        this.gameState = gameState;
        instance = this;
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
                            updateRoleInventory(player); //Ouvre le menu role
                        }  else if (item.isSimilar(GUIItems.getSelectScenarioButton())  && (player.isOp() || gameState.getHost().contains(player.getUniqueId()) ) ) {
                            player.openInventory(GUIItems.getScenarioGUI());
                            updateScenarioInventory(player); //Ouvre le menu des scenarios
                        } else if (item.isSimilar(GUIItems.getSelectConfigButton())  && (player.isOp() || gameState.getHost().contains(player.getUniqueId()) )) {
                            player.openInventory(GUIItems.getConfigSelectGUI());
                            updateConfigInventory(player); //Ouvre le menu permettant de configurer l'essentiel de la partie
                        } else if (item.isSimilar(GUIItems.getSelectInvsButton())  && (player.isOp() || gameState.getHost().contains(player.getUniqueId()) )) {
                            player.openInventory(GUIItems.getSelectInventoryGUI());
                            updateSelectInventory(player); //Ouvre le menu pour config l'inventaire
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
                            updateEventInventory(player);
                        }
                        if (item.isSimilar(GUIItems.getx())) player.closeInventory();
                        event.setCancelled(true);
                        break;
                    case "§fConfiguration§7 ->§6 Roles":
                        if (item.getType() != Material.AIR) {
                            if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
                                if (demon) {
                                    player.openInventory(GUIItems.getDemonSlayerInventory());
                                    updateDSInventory(player);
                                }
                                if (aot) {
                                    player.openInventory(GUIItems.getSelectAOTInventory());
                                    updateAOTInventory(player);
                                }
                                if (ns) {
                                    player.openInventory(GUIItems.getSelectNSInventory());
                                    updateNSInventory(player);
                                }
                                if (item.getType() == Material.BOOKSHELF) {
                                    Inventory inventaire = Bukkit.createInventory(player, 9, "Séléction du mode de jeu");
                                    player.openInventory(inventaire);
                                    updateSelectMDJ(player);
                                }
                            } else {
                                if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                                    if ((player.isOp() || gameState.getHost().contains(player.getUniqueId()) ))player.openInventory(GUIItems.getAdminWatchGUI());
                                    if (!player.isOp() && player.getOpenInventory() != null && player.getInventory() != null) player.closeInventory();
                                }
                            }
                        }
                        for (Player p : gameState.getInLobbyPlayers()) {
                            updateRoleInventory(p);
                        }
                        event.setCancelled(true);
                        break;
                    case "Séléction du mode de jeu":
                        if (item.getType() != Material.AIR) {
                            if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                                player.openInventory(GUIItems.getRoleSelectGUI());
                                updateRoleInventory(player);
                            }
                            for (GameState.MDJ mdj : GameState.MDJ.values()) {
                                if (item.isSimilar(mdj.getItem())) {
                                    if (gameState.getMdj().equals(mdj)) {
                                        gameState.setAllMDJDesac();
                                        gameState.updateGameCanLaunch();
                                    }else {
                                        gameState.setAllMDJDesac();
                                        gameState.setMdj(mdj);
                                    }
                                }
                            }
                        }
                        for (Player p : gameState.getInLobbyPlayers()) {
                            updateSelectMDJ(p);
                        }
                        event.setCancelled(true);
                        break;
                    case "§fConfiguration§7 -> §6Événements":
                        if (item.getType() != Material.AIR) {
                            if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                                player.openInventory(GUIItems.getAdminWatchGUI());
                                updateAdminInventory(player);
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
                            updateEventInventory(p);
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
                                            if ((player.isOp() || gameState.getHost().contains(player.getUniqueId()) ))p.openInventory(GUIItems.getScenarioGUI()); updateScenarioInventory(player);
                                            if (!p.isOp() && p.getOpenInventory() != null && p.getInventory() != null) p.closeInventory();
                                        }
                                    }
                                }
                            }
                        }
                        player.updateInventory();
                        updateCutCleanInventory(player);
                        event.setCancelled(true);
                        break;
                    case "§fConfiguration§7 ->§6 Inventaire":
                        if (item.getType() == Material.AIR) return;
                        if (item.getType() == Material.DIAMOND_HELMET) {
                            if (action.equals(InventoryAction.PICKUP_ALL)) {
                                if (GameState.pc < 4) {
                                    GameState.pc++;
                                }
                            }else {
                                if (GameState.pc > 0) {
                                    GameState.pc--;
                                }
                            }
                        }
                        if (item.getType() == Material.DIAMOND_CHESTPLATE) {
                            if (action.equals(InventoryAction.PICKUP_ALL)) {
                                if (GameState.pch < 4) {
                                    GameState.pch++;
                                }
                            }else {
                                if (GameState.pch > 0) {
                                    GameState.pch--;
                                }
                            }
                        }
                        if (item.getType() == Material.IRON_LEGGINGS) {
                            if (action.equals(InventoryAction.PICKUP_ALL)) {
                                if (GameState.pl < 4) {
                                    GameState.pl++;
                                }
                            }else {
                                if (GameState.pl > 0) {
                                    GameState.pl--;
                                }
                            }
                        }
                        if (item.getType() == Material.DIAMOND_BOOTS) {
                            if (action.equals(InventoryAction.PICKUP_ALL)) {
                                if (GameState.pb < 4) {
                                    GameState.pb++;
                                }
                            } else {
                                if (GameState.pb > 0) {
                                    GameState.pb--;
                                }
                            }
                        }
                        if (item.hasItemMeta()) {
                            if (item.getItemMeta().hasDisplayName()) {
                                if (item.getItemMeta().getDisplayName().equalsIgnoreCase("§r§fNombre de pomme d'§eor")) {
                                    if (action.equals(InventoryAction.PICKUP_ALL)) {
                                        if (gameState.getNmbGap() != 64) {
                                            gameState.setNmbGap(gameState.getNmbGap()+1);
                                        } else {
                                            player.sendMessage("Vous avez déjà atteint le nombre maximum de Pomme en Or ("+ChatColor.GOLD+"64"+ChatColor.RESET+")");
                                        }
                                    } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                        if (gameState.getNmbGap() != gameState.minnmbGap) {
                                            gameState.setNmbGap(gameState.getNmbGap()-1);
                                        } else {
                                            player.sendMessage("Vous avez déjà atteint le nombre minimum de Pomme en Or ("+ChatColor.GOLD+"12"+ChatColor.RESET+")");
                                        }
                                    }
                                }
                            }
                        }
                        if (item.isSimilar(GUIItems.getdiamondsword())) {
                            if (action.equals(InventoryAction.PICKUP_ALL)) {
                                if (GameState.sharpness != 5) {
                                    GameState.sharpness++;
                                } else {
                                    player.sendMessage("Vous avez déjà atteint la limite de sharpness maximal");
                                }
                            } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                if (GameState.sharpness != 1) {
                                    GameState.sharpness--;
                                } else {
                                    player.sendMessage("Vous avez déjà atteint la limite de sharpness minimal");
                                }
                            }
                        }
                        if (item.isSimilar(GUIItems.getblock())) {
                            if (action.equals(InventoryAction.PICKUP_ALL)) {
                                if (GameState.nmbblock != 4) {
                                    GameState.nmbblock++;
                                } else {
                                    player.sendMessage("Vous avez déjà atteint la limite de block");
                                }
                            } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                if (GameState.nmbblock != 1) {
                                    GameState.nmbblock--;
                                } else {
                                    player.sendMessage("Vous avez déjà atteint la limite de block");
                                }
                            }
                        }
                        if (item.isSimilar(GUIItems.getbow())) {
                            if (action.equals(InventoryAction.PICKUP_ALL)) {
                                if (GameState.power != 5) {
                                    GameState.power++;
                                } else {
                                    player.sendMessage("Vous avez déjà atteint la limite de power");
                                }
                            } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                if (GameState.power != 1) {
                                    GameState.power--;
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
                            for (Player p : gameState.getInLobbyPlayers()) {
                                if (p == event.getWhoClicked()) {
                                    if ((player.isOp() || gameState.getHost().contains(player.getUniqueId()) ))p.openInventory(GUIItems.getAdminWatchGUI());
                                    if (!p.isOp() && p.getOpenInventory() != null && p.getInventory() != null) p.closeInventory();
                                }
                            }
                        } else {
                            if (item.getType() == Material.ARROW) {
                                if (action.equals(InventoryAction.PICKUP_ALL)) {
                                    if (gameState.nmbArrow < 64) {
                                        gameState.nmbArrow++;
                                    }
                                } else {
                                    if (gameState.nmbArrow > 0) {
                                        gameState.nmbArrow--;
                                    }
                                }
                            }
                        }
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
                                updateSlayerInventory(player);
                            }
                            if (d) {
                                player.openInventory(GUIItems.getDemonSelectGUI());
                                updateDemonInventory(player);
                            }
                            if (solo) {
                                player.openInventory(GUIItems.getDSSoloSelectGUI());
                                updateDSSoloInventory(player);
                            }

                        } else {
                            for (Player p : gameState.getInLobbyPlayers()) {
                                if (p == event.getWhoClicked()) {
                                    if ((player.isOp() || gameState.getHost().contains(player.getUniqueId()) ))p.openInventory(GUIItems.getRoleSelectGUI());updateRoleInventory(player);
                                    if (!p.isOp() && p.getOpenInventory() != null && p.getInventory() != null) p.closeInventory();
                                }
                            }
                        }
                        for (Player p : gameState.getInLobbyPlayers()) {
                            updateSlayerInventory(p);
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
                            for (Player p : gameState.getInLobbyPlayers()) {
                                if (p == event.getWhoClicked()) {
                                    if ((player.isOp() || gameState.getHost().contains(player.getUniqueId()) ))p.openInventory(GUIItems.getRoleSelectGUI());updateRoleInventory(player);
                                    if (!p.isOp() && p.getOpenInventory() != null && p.getInventory() != null) p.closeInventory();
                                }
                            }
                        }
                        for (Player p : gameState.getInLobbyPlayers()) {
                            updateDemonInventory(p);
                        }
                        if (sl) {
                            player.openInventory(GUIItems.getSlayersSelectGUI());
                            updateSlayerInventory(player);
                        }
                        if (d) {
                            player.openInventory(GUIItems.getDemonSelectGUI());
                            updateDemonInventory(player);
                        }
                        if (solo) {
                            player.openInventory(GUIItems.getDSSoloSelectGUI());
                            updateDSSoloInventory(player);
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
                                    if ((player.isOp() || gameState.getHost().contains(player.getUniqueId()) ))p.openInventory(GUIItems.getRoleSelectGUI());updateRoleInventory(player);
                                    if (!p.isOp() && p.getOpenInventory() != null && p.getInventory() != null) p.closeInventory();
                                }
                            }
                        }
                        for (Player p : gameState.getInLobbyPlayers()) {
                            updateDSSoloInventory(p);
                        }
                        if (sl) {
                            player.openInventory(GUIItems.getSlayersSelectGUI());
                            updateSlayerInventory(player);
                        }
                        if (d) {
                            player.openInventory(GUIItems.getDemonSelectGUI());
                            updateDemonInventory(player);
                        }
                        if (solo) {
                            player.openInventory(GUIItems.getDSSoloSelectGUI());
                            updateDSSoloInventory(player);
                        }
                        if (mahr) {
                            player.openInventory(GUIItems.getMahrGui());
                            updateMahrInventory(player);
                        }
                        if (titans) {
                            player.openInventory(GUIItems.getSecretTitansGui());
                            updateSecretTitansInventory(player);
                        }
                        if (soldat) {
                            player.openInventory(GUIItems.getSecretSoldatGui());
                            updateSoldatInventory(player);
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
                                    if (gameState.WaterEmptyTiming != 10) {
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
                                    if (gameState.LavaEmptyTiming != 10) {
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
                                    Border.setBorderSpeed(Math.min(Border.getBorderSpeed()+1f, 10f));
                                } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                    Border.setBorderSpeed(Math.max(Border.getBorderSpeed()-1f, 1f));
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
                                        updateConfigInventory(player);
                                    } else {
                                        if (action.equals(InventoryAction.PICKUP_HALF)) {
                                            gameState.timeday-=10;
                                            player.updateInventory();
                                            updateConfigInventory(player);
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
                                    updateConfigInventory(player);
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
                                    player.openInventory(Bukkit.createInventory(player, 9, "Configuration ->§6 Bijus"));
                                    openConfigBijusInventory(player);
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
                            updateConfigInventory(p);
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
                            if ((player.isOp() || gameState.getHost().contains(player.getUniqueId()) ))player.openInventory(GUIItems.getSelectAOTInventory());updateAOTInventory(player);
                        }
                        for (Player p : gameState.getInLobbyPlayers()) {
                            updateAOTSoloInventory(p);
                        }
                        if (mahr) {
                            player.openInventory(GUIItems.getMahrGui());
                            updateMahrInventory(player);
                        }
                        if (titans) {
                            player.openInventory(GUIItems.getSecretTitansGui());
                            updateSecretTitansInventory(player);
                        }
                        if (solo) {
                            player.openInventory(GUIItems.getAOTSoloSelectGUI());
                            updateAOTSoloInventory(player);
                        }
                        if (soldat) {
                            player.openInventory(GUIItems.getSecretSoldatGui());
                            updateSoldatInventory(player);
                        }
                        if (aotconfig) {
                            player.openInventory(GUIItems.getConfigurationAOT());
                            updateAOTConfiguration(player);
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
                            if ((player.isOp() || gameState.getHost().contains(player.getUniqueId()) ))player.openInventory(GUIItems.getSelectAOTInventory());updateAOTInventory(player);
                        }
                        for (Player p : gameState.getInLobbyPlayers()) {
                            updateDSSoloInventory(p);
                        }
                        if (mahr) {
                            player.openInventory(GUIItems.getMahrGui());
                            updateMahrInventory(player);
                        }
                        if (titans) {
                            player.openInventory(GUIItems.getSecretTitansGui());
                            updateSecretTitansInventory(player);
                        }
                        if (solo) {
                            player.openInventory(GUIItems.getAOTSoloSelectGUI());
                            updateAOTSoloInventory(player);
                        }
                        if (soldat) {
                            player.openInventory(GUIItems.getSecretSoldatGui());
                            updateSoldatInventory(player);
                        }
                        if (aotconfig) {
                            player.openInventory(GUIItems.getConfigurationAOT());
                            updateAOTConfiguration(player);
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
                            if ((player.isOp() || gameState.getHost().contains(player.getUniqueId()) ))player.openInventory(GUIItems.getSelectAOTInventory());updateAOTInventory(player);
                        }
                        if (mahr) {
                            player.openInventory(GUIItems.getMahrGui());
                            updateMahrInventory(player);
                        }
                        if (titans) {
                            player.openInventory(GUIItems.getSecretTitansGui());
                            updateSecretTitansInventory(player);
                        }
                        if (solo) {
                            player.openInventory(GUIItems.getAOTSoloSelectGUI());
                            updateAOTSoloInventory(player);
                        }
                        if (soldat) {
                            player.openInventory(GUIItems.getSecretSoldatGui());
                            updateSoldatInventory(player);
                        }
                        if (aotconfig) {
                            player.openInventory(GUIItems.getConfigurationAOT());
                            updateAOTConfiguration(player);
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
                            updateMahrInventory(player);
                        }
                        updateAOTConfiguration(player);
                        event.setCancelled(true);
                        break;
                    case "§fRoles§7 ->§6 DemonSlayer":
                        if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
                            if (sl || d || solo) {
                                if (sl) {
                                    player.openInventory(GUIItems.getSlayersSelectGUI());
                                    updateSlayerInventory(player);
                                }
                                if (d) {
                                    player.openInventory(GUIItems.getDemonSelectGUI());
                                    updateDemonInventory(player);
                                }
                                if (solo) {
                                    player.openInventory(GUIItems.getDSSoloSelectGUI());
                                    updateDSSoloInventory(player);
                                }
                            }
                        }else {
                            player.openInventory(GUIItems.getRoleSelectGUI());
                            updateRoleInventory(player);
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
                                    if ((player.isOp() || gameState.getHost().contains(player.getUniqueId()) ))p.openInventory(GUIItems.getRoleSelectGUI());updateRoleInventory(player);
                                    if (!p.isOp() && p.getOpenInventory() != null && p.getInventory() != null) p.closeInventory();
                                }
                            }
                        }
                        for (Player p : gameState.getInLobbyPlayers()) {
                            updateDSSoloInventory(p);
                        }
                        if (solo) {
                            player.openInventory(GUIItems.getDSSoloSelectGUI());
                            updateDSSoloInventory(player);
                        }
                        if (mahr) {
                            player.openInventory(GUIItems.getMahrGui());
                            updateMahrInventory(player);
                        }
                        if (titans) {
                            player.openInventory(GUIItems.getSecretTitansGui());
                            updateSecretTitansInventory(player);
                        }
                        if (soldat) {
                            player.openInventory(GUIItems.getSecretSoldatGui());
                            updateSoldatInventory(player);
                        }
                        event.setCancelled(true);
                        break;
                    case "§fRoles§7 ->§6 AOT":
                        if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
                            if (solo || mahr || titans || soldat) {
                                if (solo) {
                                    player.openInventory(GUIItems.getAOTSoloSelectGUI());
                                    updateAOTSoloInventory(player);
                                }
                                if (mahr) {
                                    player.openInventory(GUIItems.getMahrGui());
                                    updateMahrInventory(player);
                                }
                                if (titans) {
                                    player.openInventory(GUIItems.getSecretTitansGui());
                                    updateSecretTitansInventory(player);
                                }
                                if (soldat) {
                                    player.openInventory(GUIItems.getSecretSoldatGui());
                                    updateSoldatInventory(player);
                                }
                            }
                        }else {
                            player.openInventory(GUIItems.getRoleSelectGUI());
                            updateRoleInventory(player);
                        }
                        event.setCancelled(true);
                        break;
                    case "Configuration ->§6 Bijus":
                        if (!item.hasItemMeta())return;
                        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                            player.openInventory(GUIItems.getConfigSelectGUI());
                            updateConfigInventory(player);
                        } else {
                            for (Bijus bijus : Bijus.values()) {
                                if (item.isSimilar(bijus.getBiju().getItemInMenu())) {
                                    bijus.setEnable(!bijus.isEnable());
                                }
                            }
                        }
                        openConfigBijusInventory(player);
                        event.setCancelled(true);
                        break;
                    case "§aNaruto§7 ->§c Akatsuki":
                        if (!item.hasItemMeta())return;
                        if (!item.getItemMeta().hasDisplayName())return;
                        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                            player.openInventory(GUIItems.getRoleSelectGUI());
                            updateRoleInventory(player);
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
                                    updateNSSoloInventory(player);
                                }
                                if (akatsuki) {
                                    player.openInventory(GUIItems.getSelectAkatsukiInventory());
                                    updateNSAkatsukiInventory(player);
                                }
                                if (orochimaru) {
                                    player.openInventory(GUIItems.getSelectOrochimaruInventory());
                                    updateNSOrochimaruInventory(player);
                                }
                                if (shinobi) {
                                    player.openInventory(GUIItems.getSelectNSShinobiInventory());
                                    updateNSShinobiInventory(player);
                                }
                            }
                        }else {
                            player.openInventory(GUIItems.getRoleSelectGUI());
                            updateRoleInventory(player);
                        }
                        event.setCancelled(true);
                        break;
                    case "§aNaruto§7 ->§5 Orochimaru":
                        if (!item.getItemMeta().hasDisplayName())return;
                        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                            player.openInventory(GUIItems.getRoleSelectGUI());
                            updateRoleInventory(player);
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
                            updateRoleInventory(player);
                        } else {
                            if (item.isSimilar(GUIItems.getSelectJubiButton())) {
                                player.openInventory(GUIItems.getSelectNSJubiInventory());
                                updateNSJubiInventory(player);
                                event.setCancelled(true);
                                return;
                            }
                            if (brume) {
                                player.openInventory(GUIItems.getSelectNSBrumeInventory());
                                updateNSBrumeInventory(player);
                                event.setCancelled(true);
                                return;
                            }
                            if (kumo){
                                player.openInventory(Bukkit.createInventory(player, 54, "§eSolo§7 ->§6 Kumogakure"));
                                updateNSKumogakure(player);
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
                            updateNSSoloInventory(player);
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
                            updateNSSoloInventory(player);
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
                            updateRoleInventory(player);
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
                            updateNSSoloInventory(player);
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
                    menuUpdater(p);
                }
            }
        }
    }
    private void menuUpdater(Player p){
        updateAdminInventory(p);
        updateScenarioInventory(p);
        updateSelectInventory(p);
        updateSlayerInventory(p);
        updateDSSoloInventory(p);
        updateDemonInventory(p);
        updateMahrInventory(p);
        updateSecretTitansInventory(p);
        updateSoldatInventory(p);
        updateEventInventory(p);
        updateAOTConfiguration(p);
        updateDSInventory(p);
        updateAOTConfiguration(p);
        updateConfigInventory(p);
        updateAOTSoloInventory(p);
        updateNSAkatsukiInventory(p);
        updateNSInventory(p);
        updateNSOrochimaruInventory(p);
        updateNSSoloInventory(p);
        updateNSJubiInventory(p);
        updateNSBrumeInventory(p);
        updateNSShinobiInventory(p);
        updateNSKumogakure(p);
    }
    public void updateSecretTitansInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equalsIgnoreCase("§fAOT§7 ->§c Titans")) {
                    inv.clear();
                    inv.setItem(0, GUIItems.getRedStainedGlassPane());
                    inv.setItem(1, GUIItems.getRedStainedGlassPane());
                    inv.setItem(9, GUIItems.getRedStainedGlassPane());//haut gauche

                    inv.setItem(2, GUIItems.getSelectSoloButton());
                    inv.setItem(3, GUIItems.getSelectMahrButton());//haut milleu
                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    inv.setItem(5, GUIItems.getSelectSoldatButton());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());
                    inv.setItem(49, GUIItems.getSelectConfigAotButton());
                    inv.setItem(7, GUIItems.getRedStainedGlassPane());//haut droite
                    inv.setItem(8, GUIItems.getRedStainedGlassPane());
                    inv.setItem(17, GUIItems.getRedStainedGlassPane());

                    inv.setItem(45, GUIItems.getRedStainedGlassPane());
                    inv.setItem(46, GUIItems.getRedStainedGlassPane());
                    inv.setItem(36, GUIItems.getRedStainedGlassPane());//bas gauche

                    inv.setItem(44, GUIItems.getRedStainedGlassPane());
                    inv.setItem(52, GUIItems.getRedStainedGlassPane());
                    inv.setItem(53, GUIItems.getRedStainedGlassPane());//bas droite
                    for (GameState.Roles roles : GameState.Roles.values()) {
                        if (roles.getTeam() == TeamList.Titan) {
                            String l1;
                            if (gameState.getAvailableRoles().get(roles) > 0) {
                                l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
                            } else {
                                l1 = "§c(0)";
                            }
                            inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
                        }
                    }
                }
            }
        }
    }
    private void updateSoldatInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§fAOT§7 ->§a Soldats")) {
                    inv.clear();
                    inv.setItem(0, GUIItems.getGreenStainedGlassPane());
                    inv.setItem(1, GUIItems.getGreenStainedGlassPane());
                    inv.setItem(9, GUIItems.getGreenStainedGlassPane());//haut gauche

                    inv.setItem(2, GUIItems.getSelectSoloButton());
                    inv.setItem(3, GUIItems.getSelectTitanButton());//haut milleu
                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    inv.setItem(5, GUIItems.getSelectMahrButton());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());
                    inv.setItem(49, GUIItems.getSelectConfigAotButton());
                    inv.setItem(7, GUIItems.getGreenStainedGlassPane());//haut droite
                    inv.setItem(8, GUIItems.getGreenStainedGlassPane());
                    inv.setItem(17, GUIItems.getGreenStainedGlassPane());

                    inv.setItem(45, GUIItems.getGreenStainedGlassPane());
                    inv.setItem(46, GUIItems.getGreenStainedGlassPane());
                    inv.setItem(36, GUIItems.getGreenStainedGlassPane());//bas gauche

                    inv.setItem(44, GUIItems.getGreenStainedGlassPane());
                    inv.setItem(52, GUIItems.getGreenStainedGlassPane());
                    inv.setItem(53, GUIItems.getGreenStainedGlassPane());//bas droite


                    inv.setItem(18, new ItemBuilder(Material.ANVIL).toItemStack());
                    for (GameState.Roles roles : GameState.Roles.values()) {
                        if (roles.getTeam() == TeamList.Soldat) {
                            String l1;
                            if (gameState.getAvailableRoles().get(roles) > 0) {
                                l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
                            } else {
                                l1 = "§c(0)";
                            }
                            inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
                        }
                    }
                    inv.setItem(18, new ItemBuilder(Material.AIR).toItemStack());
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }
    private void updateNSAkatsukiInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§aNaruto§7 ->§c Akatsuki")) {
                    inv.clear();
                    inv.setItem(0, GUIItems.getRedStainedGlassPane());
                    inv.setItem(1, GUIItems.getRedStainedGlassPane());
                    inv.setItem(9, GUIItems.getRedStainedGlassPane());//haut gauche

                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());

                    inv.setItem(7, GUIItems.getRedStainedGlassPane());//haut droite
                    inv.setItem(8, GUIItems.getRedStainedGlassPane());
                    inv.setItem(17, GUIItems.getRedStainedGlassPane());

                    inv.setItem(45, GUIItems.getRedStainedGlassPane());
                    inv.setItem(46, GUIItems.getRedStainedGlassPane());
                    inv.setItem(36, GUIItems.getRedStainedGlassPane());//bas gauche

                    inv.setItem(44, GUIItems.getRedStainedGlassPane());
                    inv.setItem(52, GUIItems.getRedStainedGlassPane());
                    inv.setItem(53, GUIItems.getRedStainedGlassPane());//bas droite

                    inv.setItem(2, new ItemBuilder(Material.ANVIL).toItemStack());
                    inv.setItem(3, new ItemBuilder(Material.ANVIL).toItemStack());
                    inv.setItem(5, new ItemBuilder(Material.ANVIL).toItemStack());
                    inv.setItem(18, new ItemBuilder(Material.ANVIL).toItemStack());
                    for (GameState.Roles roles : GameState.Roles.values()) {
                        if (roles.getTeam() == TeamList.Akatsuki) {
                            String l1;
                            if (gameState.getAvailableRoles().get(roles) > 0) {
                                l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
                            } else {
                                l1 = "§c(0)";
                            }
                            inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
                        }
                    }
                    inv.setItem(2, new ItemBuilder(Material.AIR).toItemStack());
                    inv.setItem(3, new ItemBuilder(Material.AIR).toItemStack());
                    inv.setItem(5, new ItemBuilder(Material.AIR).toItemStack());
                    inv.setItem(18, new ItemBuilder(Material.AIR).toItemStack());
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }
    private void updateNSOrochimaruInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§aNaruto§7 ->§5 Orochimaru")) {
                    inv.clear();
                    inv.setItem(0, GUIItems.getPurpleStainedGlassPane());
                    inv.setItem(1, GUIItems.getPurpleStainedGlassPane());
                    inv.setItem(9, GUIItems.getPurpleStainedGlassPane());//haut gauche

                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());

                    inv.setItem(7, GUIItems.getPurpleStainedGlassPane());//haut droite
                    inv.setItem(8, GUIItems.getPurpleStainedGlassPane());
                    inv.setItem(17, GUIItems.getPurpleStainedGlassPane());

                    inv.setItem(45, GUIItems.getPurpleStainedGlassPane());
                    inv.setItem(46, GUIItems.getPurpleStainedGlassPane());
                    inv.setItem(36, GUIItems.getPurpleStainedGlassPane());//bas gauche

                    inv.setItem(44, GUIItems.getPurpleStainedGlassPane());
                    inv.setItem(52, GUIItems.getPurpleStainedGlassPane());
                    inv.setItem(53, GUIItems.getPurpleStainedGlassPane());//bas droite

                    inv.setItem(2, new ItemBuilder(Material.ANVIL).toItemStack());
                    inv.setItem(3, new ItemBuilder(Material.ANVIL).toItemStack());
                    inv.setItem(5, new ItemBuilder(Material.ANVIL).toItemStack());
                    for (GameState.Roles roles : GameState.Roles.values()) {
                        if (roles.getTeam() == TeamList.Orochimaru) {
                            String l1;
                            if (gameState.getAvailableRoles().get(roles) > 0) {
                                l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
                            } else {
                                l1 = "§c(0)";
                            }
                            inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
                        }
                    }
                    inv.setItem(2, new ItemBuilder(Material.AIR).toItemStack());
                    inv.setItem(3, new ItemBuilder(Material.AIR).toItemStack());
                    inv.setItem(5, new ItemBuilder(Material.AIR).toItemStack());
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }
    private void updateNSSoloInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§aNaruto§7 ->§e Solo")) {
                    inv.clear();
                    inv.setItem(0, GUIItems.getOrangeStainedGlassPane());
                    inv.setItem(1, GUIItems.getOrangeStainedGlassPane());
                    inv.setItem(9, GUIItems.getOrangeStainedGlassPane());//haut gauche

                    inv.setItem(2, GUIItems.getSelectJubiButton());
                    inv.setItem(3, GUIItems.getSelectBrumeButton());
                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    inv.setItem(5, GUIItems.getSelectKumogakureButton());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());

                    inv.setItem(7, GUIItems.getOrangeStainedGlassPane());//haut droite
                    inv.setItem(8, GUIItems.getOrangeStainedGlassPane());
                    inv.setItem(17, GUIItems.getOrangeStainedGlassPane());

                    inv.setItem(45, GUIItems.getOrangeStainedGlassPane());
                    inv.setItem(46, GUIItems.getOrangeStainedGlassPane());
                    inv.setItem(36, GUIItems.getOrangeStainedGlassPane());//bas gauche

                    inv.setItem(44, GUIItems.getOrangeStainedGlassPane());
                    inv.setItem(52, GUIItems.getOrangeStainedGlassPane());
                    inv.setItem(53, GUIItems.getOrangeStainedGlassPane());//bas droite

                    for (GameState.Roles roles : GameState.Roles.values()) {
                        if (roles.getTeam() == TeamList.Solo && roles.getMdj().equals("ns")) {
                            String l1;
                            if (gameState.getAvailableRoles().get(roles) > 0) {
                                l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
                            } else {
                                l1 = "§c(0)";
                            }
                            inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
                        }
                    }
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }
    private void updateNSJubiInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§eSolo§7 ->§d Jubi")) {
                    inv.clear();
                    ItemStack glass = GUIItems.getPinkStainedGlassPane();
                    inv.setItem(0, glass);
                    inv.setItem(1, glass);
                    inv.setItem(9, glass);//haut gauche

                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());

                    inv.setItem(7, glass);//haut droite
                    inv.setItem(8, glass);
                    inv.setItem(17, glass);

                    inv.setItem(45, glass);
                    inv.setItem(46, glass);
                    inv.setItem(36, glass);//bas gauche

                    inv.setItem(44, glass);
                    inv.setItem(52, glass);
                    inv.setItem(53, glass);//bas droite

                    inv.setItem(2, new ItemBuilder(Material.ANVIL).toItemStack());
                    inv.setItem(3, new ItemBuilder(Material.ANVIL).toItemStack());
                    inv.setItem(5, new ItemBuilder(Material.ANVIL).toItemStack());
                    for (GameState.Roles roles : GameState.Roles.values()) {
                        if (roles.getTeam() == TeamList.Jubi) {
                            String l1;
                            if (gameState.getAvailableRoles().get(roles) > 0) {
                                l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
                            } else {
                                l1 = "§c(0)";
                            }
                            inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
                        }
                    }
                    inv.setItem(2, new ItemBuilder(Material.AIR).toItemStack());
                    inv.setItem(3, new ItemBuilder(Material.AIR).toItemStack());
                    inv.setItem(5, new ItemBuilder(Material.AIR).toItemStack());
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }
    private void updateConfigInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("Configuration de la partie")) {
                    inv.clear();
                    inv.addItem(new ItemBuilder(Material.STAINED_GLASS_PANE).setAmount(1).setDurability(5).setName("§r§fTaille de la bordure maximum").setLore(
                            "§r§f[50b < "+Border.getMaxBorderSize()+" > 2400b",
                            "§r§fClique gauche: §a+50b",
                            "§r§fClique droit: §c-50b"
                    ).toItemStack());
                    inv.addItem(new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§r§fTaille de la bordure minimum").setDurability(14).setLore(
                            "§r§f[50b < "+Border.getMinBorderSize()+"b > "+Border.getMaxBorderSize()+"b]",
                            "§r§fClique gauche:§a +50b",
                            "§r§fClique droit: §c-50b"
                    ).toItemStack());
                    inv.addItem(new ItemBuilder(Material.STAINED_GLASS_PANE).setAmount(1).setDurability(7).setName("§r§fVitesse de la bordure")
                            .setLore("§r§f[1b/s < "+Border.getBorderSpeed()+"§r§fb/s > 10b/s",
                                    "§r§fClique gauche: §a+1b/s",
                                    "§r§fClique droit: §c-1b/s").toItemStack());
                    inv.addItem(new ItemBuilder(Material.IRON_SWORD).setName("§r§fTemp avant activation du PVP").setLore(
                            "§r§f[0 minute < "+gameState.getPvPTimer()/60+" minutes > 40 minutes]",
                            "§r§fClique gauche: §a+1 minutes",
                            "§r§fClique droit: §c-1 minutes"
                    ).toItemStack());
                    inv.addItem(new ItemBuilder(Material.SKULL_ITEM).setName("§r§fTemp avant annonce des roles").setLore(
                            "§r§f[0 minute < "+gameState.getRoleTimer()/60+" minutes > 40 minutes]",
                            "§r§fClique gauche: §a+1 minutes",
                            "§r§fClique droit: §c-1 minutes"
                    ).toItemStack());
                    inv.addItem(new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(0).setName("§r§fTemp avant réduction de la bordure").setLore(
                            "§r§f[0 minute < "+Border.getTempReduction()/60+" minutes > 60 minutes]",
                            "§r§fClique gauche: §a+1 minutes",
                            "§r§fClique droit: §c-1 minutes"
                    ).toItemStack());
                    inv.addItem(new ItemBuilder(Material.WATCH).setName("§r§fDurée du jour (et de la nuit)").setLore(
                            "§r§fDurée actuel:§6 "+StringUtils.secondsTowardsBeautiful(gameState.timeday),
                            "§r§fClique gauche: §a+10 secondes",
                            "§r§fClique droit: §c-10 secondes"
                    ).toItemStack());
                    inv.addItem(GUIItems.getTabRoleInfo(gameState));
                    inv.addItem(Items.geteclairmort());
                    inv.addItem(new ItemBuilder(Material.REDSTONE).setName("§r§fTemp avant l'§cAssassin").setLore(
                            "§r§f[10 secondes < "+StringUtils.secondsTowardsBeautiful(gameState.getTimingAssassin())+" > 5 minutes",
                            "§r§fClique gauche: §a+10 secondes",
                            "§r§fClique droit: §c-10 secondes"
                    ).toItemStack());
                    inv.addItem(new ItemBuilder(Material.WATER_BUCKET).setName("§r§fTemp avant despawn de l'§bEau").setLore(
                            "§r§f[0 secondes < "+StringUtils.secondsTowardsBeautiful(gameState.WaterEmptyTiming)+" > 1 minutes",
                            "§r§fClique gauche: §a+1 secondes",
                            "§r§fClique droit: §c-1 secondes",
                            "§r§f(0 secondes =§c désactiver"
                    ).toItemStack());
                    inv.addItem(new ItemBuilder(Material.LAVA_BUCKET).setName("§r§fTemp avant despawn de la§6 Lave").setLore(
                            "§r§f[0 seconde < "+StringUtils.secondsTowardsBeautiful(gameState.LavaEmptyTiming)+" > 1 minutes",
                            "§r§fClique gauche: §a+1 seconde",
                            "§r§fClique droit: §c-1 seconde",
                            "§r§f(0 secondes =§c désactiver"
                    ).toItemStack());
                    inv.addItem(new ItemBuilder(Material.NETHER_STAR).setName("§fBijus").setLore(gameState.BijusEnable ? "§aActivé" : "§cDésactivé","§r§fShift + Clique: Permet de configurer les bijus").toItemStack());
                    inv.addItem(new ItemBuilder(Material.GHAST_TEAR).setName("§cInfection").setLore(
                            "§fTemp avant infection: ",
                            "§a+5s§f (Clique gauche)",
                            "§c-5s§f (Clique droit)",
                            "§fTemp actuelle:§b "+StringUtils.secondsTowardsBeautiful(GameState.getInstance().timewaitingbeinfected)
                    ).toItemStack());
                    inv.addItem(new ItemBuilder(Material.TNT).setName("§fGrief du terrain par les§c TNT").setLore(gameState.isTNTGrief() ? "§aActivé" : "§cDésactivé").toItemStack());
                    inv.setItem(26, GUIItems.getSelectBackMenu());
                }
            }
        }
        player.updateInventory();
    }
    public void updateAOTConfiguration(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equalsIgnoreCase("Configuration -> AOT")) {
                    inv.clear();
                    inv.setItem(0, new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").toItemStack());
                    inv.setItem(1, new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").toItemStack());
                    inv.setItem(9, new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").toItemStack());

                    inv.setItem(7, new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").toItemStack());
                    inv.setItem(8, new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").toItemStack());
                    inv.setItem(17, new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").toItemStack());

                    inv.setItem(36, new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").toItemStack());
                    inv.setItem(45, new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").toItemStack());
                    inv.setItem(46, new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").toItemStack());

                    inv.setItem(44, new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").toItemStack());
                    inv.setItem(52, new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").toItemStack());
                    inv.setItem(53, new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").toItemStack());

                    inv.setItem(4, GUIItems.getSelectBackMenu());

                    inv.setItem(10, new ItemBuilder(Material.BOW).setName("§rCooldown Equipement Tridimentionnel").setLore("§fCooldownActuel: "+gameState.TridiCooldown).toItemStack());
                    if (gameState.rod) {
                        inv.setItem(11, new ItemBuilder(Material.FISHING_ROD).setName("§rEquipement Tridimentionnel").setLore("§fEquipement actuel:§l Rod Tridimentionnelle").toItemStack());
                    }else {
                        inv.setItem(11, new ItemBuilder(Material.BOW).setName("§rEquipement Tridimentionnel").setLore("§fÉquipement actuel:§l Arc Tridimentionnelle").toItemStack());
                    }
                    inv.setItem(12, new ItemBuilder(Material.LAVA_BUCKET).setName("§r§6Lave§f pour les titans (transformé)").setLore(gameState.LaveTitans ? "§aActivé" : "§cDésactivé").toItemStack());
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }
    public void updateMahrInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equalsIgnoreCase("§fAOT§7 ->§9 Mahr")) {
                    inv.clear();
                    inv.setItem(0, GUIItems.getSBluetainedGlassPane());
                    inv.setItem(1, GUIItems.getSBluetainedGlassPane());
                    inv.setItem(9, GUIItems.getSBluetainedGlassPane());//haut gauche

                    inv.setItem(2, GUIItems.getSelectSoloButton());
                    inv.setItem(3, GUIItems.getSelectTitanButton());//haut milleu
                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    inv.setItem(5, GUIItems.getSelectSoldatButton());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());

                    inv.setItem(7, GUIItems.getSBluetainedGlassPane());//haut droite
                    inv.setItem(8, GUIItems.getSBluetainedGlassPane());
                    inv.setItem(17, GUIItems.getSBluetainedGlassPane());

                    inv.setItem(45, GUIItems.getSBluetainedGlassPane());
                    inv.setItem(46, GUIItems.getSBluetainedGlassPane());
                    inv.setItem(36, GUIItems.getSBluetainedGlassPane());//bas gauche

                    inv.setItem(44, GUIItems.getSBluetainedGlassPane());
                    inv.setItem(52, GUIItems.getSBluetainedGlassPane());
                    inv.setItem(53, GUIItems.getSBluetainedGlassPane());//bas droite

                    inv.setItem(49, GUIItems.getSelectConfigAotButton());

                    for (GameState.Roles roles : GameState.Roles.values()) {
                        if (roles.getTeam() == TeamList.Mahr) {
                            String l1;
                            if (gameState.getAvailableRoles().get(roles) > 0) {
                                l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
                            } else {
                                l1 = "§c(0)";
                            }
                            inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
                        }
                    }
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }
    public void updateDemonInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("DemonSlayer -> §cDémons")) {
                    inv.clear();
                    inv.setItem(0, GUIItems.getRedStainedGlassPane());
                    inv.setItem(1, GUIItems.getRedStainedGlassPane());
                    inv.setItem(9, GUIItems.getRedStainedGlassPane());//haut gauche

                    //	inv.setItem(2, GUIItems.getx());
                    inv.setItem(3, GUIItems.getSelectSlayersButton());//haut milleu
                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    inv.setItem(5, GUIItems.getSelectSoloButton());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());
                    inv.setItem(7, GUIItems.getRedStainedGlassPane());//haut droite
                    inv.setItem(8, GUIItems.getRedStainedGlassPane());
                    inv.setItem(17, GUIItems.getRedStainedGlassPane());

                    inv.setItem(45, GUIItems.getRedStainedGlassPane());
                    inv.setItem(46, GUIItems.getRedStainedGlassPane());
                    inv.setItem(36, GUIItems.getRedStainedGlassPane());//bas gauche

                    inv.setItem(44, GUIItems.getRedStainedGlassPane());
                    inv.setItem(52, GUIItems.getRedStainedGlassPane());
                    inv.setItem(53, GUIItems.getRedStainedGlassPane());//bas droite

                    inv.setItem(2, new ItemBuilder(Material.ANVIL).toItemStack());
                    inv.setItem(18, new ItemBuilder(Material.ANVIL).toItemStack());
                    inv.setItem(27, new ItemBuilder(Material.ANVIL).toItemStack());
                    inv.setItem(26, new ItemBuilder(Material.ANVIL).toItemStack());
                    inv.setItem(35, new ItemBuilder(Material.ANVIL).toItemStack());
                    for (GameState.Roles roles : GameState.Roles.values()) {
                        if (roles.getTeam() == TeamList.Demon) {
                            String l1;
                            if (gameState.getAvailableRoles().get(roles) > 0) {
                                l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
                            } else {
                                l1 = "§c(0)";
                            }
                            inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
                        }
                    }
                    inv.setItem(2, new ItemBuilder(Material.AIR).toItemStack());
                    inv.setItem(18, new ItemBuilder(Material.AIR).toItemStack());
                    inv.setItem(27, new ItemBuilder(Material.AIR).toItemStack());
                    inv.setItem(26, new ItemBuilder(Material.AIR).toItemStack());
                    inv.setItem(35, new ItemBuilder(Material.AIR).toItemStack());
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }
    private void updateAOTSoloInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§fAOT§7 -> §eSolo")) {
                    inv.clear();
                    inv.setItem(0, GUIItems.getOrangeStainedGlassPane());
                    inv.setItem(1, GUIItems.getOrangeStainedGlassPane());
                    inv.setItem(9, GUIItems.getOrangeStainedGlassPane());//haut gauche

                    inv.setItem(2, GUIItems.getSelectMahrButton());
                    inv.setItem(3, GUIItems.getSelectTitanButton());//haut milleu
                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    inv.setItem(5, GUIItems.getSelectSoldatButton());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());

                    inv.setItem(7, GUIItems.getOrangeStainedGlassPane());//haut droite
                    inv.setItem(8, GUIItems.getOrangeStainedGlassPane());
                    inv.setItem(17, GUIItems.getOrangeStainedGlassPane());

                    inv.setItem(45, GUIItems.getOrangeStainedGlassPane());
                    inv.setItem(46, GUIItems.getOrangeStainedGlassPane());
                    inv.setItem(36, GUIItems.getOrangeStainedGlassPane());//bas gauche

                    inv.setItem(44, GUIItems.getOrangeStainedGlassPane());
                    inv.setItem(52, GUIItems.getOrangeStainedGlassPane());
                    inv.setItem(53, GUIItems.getOrangeStainedGlassPane());//bas droite
                    for (GameState.Roles roles : GameState.Roles.values()) {
                        if (roles.getTeam() == TeamList.Solo && roles.getMdj().equals("aot")) {
                            String l1;
                            if (gameState.getAvailableRoles().get(roles) > 0) {
                                l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
                            } else {
                                l1 = "§c(0)";
                            }
                            inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
                        }
                    }
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }
    private void updateDSSoloInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("DemonSlayer -> §eSolo")) {
                    inv.clear();
                    inv.setItem(0, GUIItems.getOrangeStainedGlassPane());
                    inv.setItem(1, GUIItems.getOrangeStainedGlassPane());
                    inv.setItem(9, GUIItems.getOrangeStainedGlassPane());//haut gauche

                    inv.setItem(3, GUIItems.getSelectDemonButton());//haut milleu
                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    inv.setItem(5, GUIItems.getSelectSlayersButton());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());

                    inv.setItem(7, GUIItems.getOrangeStainedGlassPane());//haut droite
                    inv.setItem(8, GUIItems.getOrangeStainedGlassPane());
                    inv.setItem(17, GUIItems.getOrangeStainedGlassPane());

                    inv.setItem(45, GUIItems.getOrangeStainedGlassPane());
                    inv.setItem(46, GUIItems.getOrangeStainedGlassPane());
                    inv.setItem(36, GUIItems.getOrangeStainedGlassPane());//bas gauche

                    inv.setItem(44, GUIItems.getOrangeStainedGlassPane());
                    inv.setItem(52, GUIItems.getOrangeStainedGlassPane());
                    inv.setItem(53, GUIItems.getOrangeStainedGlassPane());//bas droite

                    inv.setItem(2, new ItemBuilder(Material.ANVIL).toItemStack());
                    for (GameState.Roles roles : GameState.Roles.values()) {
                        if (roles.getTeam() == TeamList.Solo && roles.getMdj().equals("ds")) {
                            String l1;
                            if (gameState.getAvailableRoles().get(roles) > 0) {
                                l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
                            } else {
                                l1 = "§c(0)";
                            }
                            inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
                        }
                    }
                    inv.setItem(2, new ItemBuilder(Material.AIR).toItemStack());
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }
    public void updateSlayerInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("DemonSlayer ->§a Slayers")) {
                    inv.clear();
                    inv.setItem(0, GUIItems.getGreenStainedGlassPane());
                    inv.setItem(1, GUIItems.getGreenStainedGlassPane());
                    inv.setItem(9, GUIItems.getGreenStainedGlassPane());//haut gauche

                    //	inv.setItem(2, GUIItems.getx());
                    inv.setItem(3, GUIItems.getSelectDemonButton());//haut milleu
                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    inv.setItem(5, GUIItems.getSelectSoloButton());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());

                    inv.setItem(7, GUIItems.getGreenStainedGlassPane());//haut droite
                    inv.setItem(8, GUIItems.getGreenStainedGlassPane());
                    inv.setItem(17, GUIItems.getGreenStainedGlassPane());

                    inv.setItem(45, GUIItems.getGreenStainedGlassPane());
                    inv.setItem(46, GUIItems.getGreenStainedGlassPane());
                    inv.setItem(36, GUIItems.getGreenStainedGlassPane());//bas gauche

                    inv.setItem(44, GUIItems.getGreenStainedGlassPane());
                    inv.setItem(52, GUIItems.getGreenStainedGlassPane());
                    inv.setItem(53, GUIItems.getGreenStainedGlassPane());//bas droite

                    inv.setItem(2, new ItemBuilder(Material.ANVIL).toItemStack());
                    inv.setItem(18, new ItemBuilder(Material.ANVIL).toItemStack());
                    inv.setItem(27, new ItemBuilder(Material.ANVIL).toItemStack());
                    inv.setItem(26, new ItemBuilder(Material.ANVIL).toItemStack());
                    inv.setItem(35, new ItemBuilder(Material.ANVIL).toItemStack());
                    for (GameState.Roles roles : GameState.Roles.values()) {
                        if (roles.getTeam() == TeamList.Slayer) {
                            String l1;
                            if (gameState.getAvailableRoles().get(roles) > 0) {
                                l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
                            } else {
                                l1 = "§c(0)";
                            }
                            inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
                        }
                    }
                    inv.setItem(2, new ItemBuilder(Material.AIR).toItemStack());
                    inv.setItem(18, new ItemBuilder(Material.AIR).toItemStack());
                    inv.setItem(27, new ItemBuilder(Material.AIR).toItemStack());
                    inv.setItem(26, new ItemBuilder(Material.AIR).toItemStack());
                    inv.setItem(35, new ItemBuilder(Material.AIR).toItemStack());
                }
            }

        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }
    public void updateAdminInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§fConfiguration")) {
                    if (gameState.gameCanLaunch) {
                        inv.setItem(0, GUIItems.getGreenStainedGlassPane());
                        inv.setItem(1, GUIItems.getGreenStainedGlassPane());
                        inv.setItem(2, GUIItems.getGreenStainedGlassPane());
                        inv.setItem(3, GUIItems.getGreenStainedGlassPane());
                        inv.setItem(4, GUIItems.getGreenStainedGlassPane());
                        inv.setItem(5, GUIItems.getGreenStainedGlassPane());
                        inv.setItem(6, GUIItems.getGreenStainedGlassPane());
                        inv.setItem(7, GUIItems.getGreenStainedGlassPane());
                        inv.setItem(8, GUIItems.getGreenStainedGlassPane());
                        inv.setItem(9, GUIItems.getGreenStainedGlassPane());
                        inv.setItem(17, GUIItems.getGreenStainedGlassPane());
                        inv.setItem(18, GUIItems.getGreenStainedGlassPane());
                        inv.setItem(26, GUIItems.getGreenStainedGlassPane());
                        inv.setItem(27, GUIItems.getGreenStainedGlassPane());
                        inv.setItem(35, GUIItems.getGreenStainedGlassPane());
                        inv.setItem(36, GUIItems.getGreenStainedGlassPane());
                        inv.setItem(44, GUIItems.getGreenStainedGlassPane());
                        inv.setItem(45, GUIItems.getGreenStainedGlassPane());
                        inv.setItem(46, GUIItems.getGreenStainedGlassPane());
                        inv.setItem(47, GUIItems.getGreenStainedGlassPane());
                        inv.setItem(48, GUIItems.getGreenStainedGlassPane());
                        inv.setItem(49, GUIItems.getGreenStainedGlassPane());
                        inv.setItem(50, GUIItems.getGreenStainedGlassPane());
                        inv.setItem(51, GUIItems.getGreenStainedGlassPane());
                        inv.setItem(52, GUIItems.getGreenStainedGlassPane());
                        inv.setItem(22, GUIItems.getStartGameButton());
                    } else {
                        inv.setItem(22, GUIItems.getCantStartGameButton());
                        inv.setItem(0, GUIItems.getRedStainedGlassPane());
                        inv.setItem(1, GUIItems.getRedStainedGlassPane());
                        inv.setItem(2, GUIItems.getRedStainedGlassPane());
                        inv.setItem(3, GUIItems.getRedStainedGlassPane());
                        inv.setItem(4, GUIItems.getRedStainedGlassPane());
                        inv.setItem(5, GUIItems.getRedStainedGlassPane());
                        inv.setItem(6, GUIItems.getRedStainedGlassPane());
                        inv.setItem(7, GUIItems.getRedStainedGlassPane());
                        inv.setItem(8, GUIItems.getRedStainedGlassPane());
                        inv.setItem(9, GUIItems.getRedStainedGlassPane());
                        inv.setItem(17, GUIItems.getRedStainedGlassPane());
                        inv.setItem(18, GUIItems.getRedStainedGlassPane());
                        inv.setItem(26, GUIItems.getRedStainedGlassPane());
                        inv.setItem(27, GUIItems.getRedStainedGlassPane());
                        inv.setItem(35, GUIItems.getRedStainedGlassPane());
                        inv.setItem(36, GUIItems.getRedStainedGlassPane());
                        inv.setItem(44, GUIItems.getRedStainedGlassPane());
                        inv.setItem(45, GUIItems.getRedStainedGlassPane());
                        inv.setItem(46, GUIItems.getRedStainedGlassPane());
                        inv.setItem(47, GUIItems.getRedStainedGlassPane());
                        inv.setItem(48, GUIItems.getRedStainedGlassPane());
                        inv.setItem(49, GUIItems.getRedStainedGlassPane());
                        inv.setItem(50, GUIItems.getRedStainedGlassPane());
                        inv.setItem(51, GUIItems.getRedStainedGlassPane());
                        inv.setItem(52, GUIItems.getRedStainedGlassPane());
                    }
                    inv.setItem(53, GUIItems.getx());
                    inv.setItem(10, GUIItems.getSelectRoleButton());
                    inv.setItem(13, GUIItems.getPregen(gameState));
                    inv.setItem(19, GUIItems.getSelectConfigButton());
                    inv.setItem(31, GUIItems.getSelectScenarioButton());
                    inv.setItem(28, GUIItems.getSelectInvsButton());
                    inv.setItem(37, GUIItems.getSelectEventButton());
                    if (AntiPvP.isAntipvplobby()) {
                        inv.setItem(40, AntiPvP.getlobbypvp());
                    } else {
                        inv.setItem(40, AntiPvP.getnotlobbypvp());
                    }
                    inv.setItem(34, Chat.getColoritem());
                    inv.setItem(43, GUIItems.getCrit(gameState));
                }
            }
        }
        player.updateInventory();
    }
    private void updateScenarioInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§fConfiguration§7 -> §6scenarios")) {
                    int i = 0;
                    for (Scenarios sc : Scenarios.values()){
                        inv.setItem(i, sc.getScenarios().getAffichedItem());
                        i++;
                    }
                    inv.setItem(26, GUIItems.getSelectBackMenu());
                }
            }
        }
        player.updateInventory();
    }
    private void updateSelectInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§fConfiguration§7 ->§6 Inventaire")) {
                    inv.setItem(48, new ItemBuilder(Material.GOLDEN_APPLE, gameState.getNmbGap()).setName("§r§fNombre de pomme d'§eor").setLore(
                                    "§a+1§f (Clique gauche)",
                                    "§c-1§f (Clique droit)",
                                    "§r§fNombre actuelle:§e "+gameState.getNmbGap())
                            .toItemStack());
                    inv.setItem(0, new ItemBuilder(Material.DIAMOND_HELMET).setLore(
                                    "§a+1§f (Clique gauche)",
                                    "§c-1§f (Clique droit)",
                                    "§r§fNiveau de protection:§b "+GameState.pc
                            ).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, GameState.pc)

                            .toItemStack());
                    inv.setItem(9, new ItemBuilder(Material.DIAMOND_CHESTPLATE).setLore(
                                    "§a+1§f (Clique gauche)",
                                    "§c-1§f (Clique droit)",
                                    "§r§fNiveau de protection:§b "+GameState.pch
                            ).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, GameState.pch)
                            .toItemStack());
                    inv.setItem(18, new ItemBuilder(Material.IRON_LEGGINGS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, GameState.pl)
                            .setLore("§a+1§f (Clique gauche)",
                                    "§c-1§f (Clique droit)",
                                    "§r§fNiveau de protection:§b "+GameState.pl)
                            .toItemStack());
                    inv.setItem(27, new ItemBuilder(Material.DIAMOND_BOOTS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, GameState.pb)
                            .setLore("§a+1§f (Clique gauche)",
                                    "§c-1§f (Clique droit)",
                                    "§r§fNiveau de protection:§b "+GameState.pb)
                            .toItemStack());
                    inv.setItem(45, GUIItems.getdiamondsword());
                    inv.setItem(46, GUIItems.getblock());
                    inv.setItem(47, GUIItems.getbow());
                    inv.setItem(49, GUIItems.getEnderPearl());
                    inv.setItem(50, GUIItems.getGoldenCarrot());
                    inv.setItem(51, GUIItems.getlave());
                    inv.setItem(52, GUIItems.geteau());
                    inv.setItem(38, new ItemBuilder(Material.ARROW, gameState.nmbArrow).setName("§fFlèches").setLore("","§7Max:§c 64","§7Minimum:§c 1","§7Actuelle:§c "+gameState.nmbArrow).toItemStack());
                    //inv.setItem(9, GUIItems.getx());

                    inv.setItem(8, GUIItems.getSelectBackMenu());
                }
            }
        }
        player.updateInventory();
    }
    public void updateRoleInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§fConfiguration§7 ->§6 Roles")) {
                    inv.clear();
                    if (gameState.isAllMdjNull()) {
                        inv.setItem(13, new ItemBuilder(Material.SIGN).setName("§7Aucun mode de jeux activé !").toItemStack());
                    } else {
                        if (gameState.getMdj() == GameState.MDJ.DS) {
                            inv.setItem(13, GUIItems.getSelectDSButton());
                        }
                        if (gameState.getMdj() == GameState.MDJ.AOT) {
                            inv.setItem(13, GUIItems.getSelectAOTButton());
                        }
                        if (gameState.getMdj() == GameState.MDJ.NS) {
                            inv.setItem(13, GUIItems.getSelectNSButton());
                        }
                    }
                    if (player.isOp() || gameState.getHost().contains(player.getUniqueId())) {
                        inv.setItem(25, new ItemBuilder(Material.BOOKSHELF).setName("Configuration du mode de jeu").toItemStack());
                    }
                    inv.setItem(26, GUIItems.getSelectBackMenu());
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }
    public void updateSelectMDJ(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equalsIgnoreCase("Séléction du mode de jeu")) {
                    inv.clear();
                    for (GameState.MDJ mdj : GameState.MDJ.values()) {
                        if (mdj != GameState.MDJ.Aucun){
                            inv.addItem(mdj.getItem());
                        }
                    }
                    inv.setItem(8, GUIItems.getSelectBackMenu());
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }
    public void updateDSInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§fRoles§7 ->§6 DemonSlayer")) {
                    inv.clear();
                    inv.setItem(11, GUIItems.getSelectSlayersButton());
                    inv.setItem(13, GUIItems.getSelectDemonButton());
                    inv.setItem(15, GUIItems.getSelectSoloButton());
                    inv.setItem(26, GUIItems.getSelectBackMenu());
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }
    public void updateAOTInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§fRoles§7 ->§6 AOT")) {
                    inv.clear();
                    inv.setItem(10, GUIItems.getSelectMahrButton());
                    inv.setItem(12, GUIItems.getSelectTitanButton());
                    inv.setItem(14, GUIItems.getSelectSoldatButton());
                    inv.setItem(16, GUIItems.getSelectSoloButton());
                    inv.setItem(26, GUIItems.getSelectBackMenu());
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }
    public void updateNSInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§fRoles§7 ->§6 NS")) {
                    inv.clear();
                    inv.setItem(10, GUIItems.getSelectShinobiButton());
                    inv.setItem(12, GUIItems.getSelectAkatsukiButton());
                    inv.setItem(14, GUIItems.getSelectOrochimaruButton());
                    inv.setItem(16, GUIItems.getSelectSoloButton());
                    inv.setItem(26, GUIItems.getSelectBackMenu());
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }
    public void updateCutCleanInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals(GUIItems.getCutCleanConfigGUI().getTitle())) {
                    inv.setItem(0, CutClean.getXpCharbon(gameState));
                    inv.setItem(2, CutClean.getXpFer(gameState));
                    inv.setItem(4, CutClean.getXpOr(gameState));
                    inv.setItem(6, CutClean.getXpDiams(gameState));
                    inv.setItem(8, GUIItems.getSelectBackMenu());
                }
            }
        }
        player.updateInventory();
    }
    private void updateNSKumogakure(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§eSolo§7 ->§6 Kumogakure")) {
                    inv.clear();
                    ItemStack glass = GUIItems.getOrangeStainedGlassPane();
                    inv.setItem(0, glass);
                    inv.setItem(1, glass);
                    inv.setItem(9, glass);//haut gauche

                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());

                    inv.setItem(7, glass);//haut droite
                    inv.setItem(8, glass);
                    inv.setItem(17, glass);

                    inv.setItem(45, glass);
                    inv.setItem(46, glass);
                    inv.setItem(36, glass);//bas gauche

                    inv.setItem(44, glass);
                    inv.setItem(52, glass);
                    inv.setItem(53, glass);//bas droite

                    inv.setItem(2, new ItemBuilder(Material.ANVIL).toItemStack());
                    inv.setItem(3, new ItemBuilder(Material.ANVIL).toItemStack());
                    inv.setItem(5, new ItemBuilder(Material.ANVIL).toItemStack());
                    for (GameState.Roles roles : GameState.Roles.values()) {
                        if (roles.getTeam() == TeamList.Kumogakure) {
                            String l1;
                            if (gameState.getAvailableRoles().get(roles) > 0) {
                                l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
                            } else {
                                l1 = "§c(0)";
                            }
                            inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
                        }
                    }
                    inv.setItem(2, new ItemBuilder(Material.AIR).toItemStack());
                    inv.setItem(3, new ItemBuilder(Material.AIR).toItemStack());
                    inv.setItem(5, new ItemBuilder(Material.AIR).toItemStack());
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }
    private void updateNSShinobiInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§aNaruto§7 ->§a Shinobi")) {
                    inv.clear();
                    ItemStack glass = GUIItems.getGreenStainedGlassPane();
                    inv.setItem(0, glass);
                    inv.setItem(1, glass);
                    inv.setItem(9, glass);//haut gauche

                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());

                    inv.setItem(7, glass);//haut droite
                    inv.setItem(8, glass);
                    inv.setItem(17, glass);

                    inv.setItem(45, glass);
                    inv.setItem(46, glass);
                    inv.setItem(36, glass);//bas gauche

                    inv.setItem(44, glass);
                    inv.setItem(52, glass);
                    inv.setItem(53, glass);//bas droite

                    inv.setItem(2, new ItemBuilder(Material.ANVIL).toItemStack());
                    inv.setItem(3, new ItemBuilder(Material.ANVIL).toItemStack());
                    inv.setItem(5, new ItemBuilder(Material.ANVIL).toItemStack());
                    inv.setItem(18, new ItemBuilder(Material.ANVIL).toItemStack());
                    inv.setItem(27, new ItemBuilder(Material.ANVIL).toItemStack());
                    inv.setItem(26, new ItemBuilder(Material.ANVIL).toItemStack());
                    inv.setItem(35, new ItemBuilder(Material.ANVIL).toItemStack());
                    for (GameState.Roles roles : GameState.Roles.values()) {
                        if (roles.getTeam() == TeamList.Shinobi) {
                            String l1;
                            if (gameState.getAvailableRoles().get(roles) > 0) {
                                l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
                            } else {
                                l1 = "§c(0)";
                            }
                            inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
                        }
                    }
                    inv.setItem(2, new ItemBuilder(Material.AIR).toItemStack());
                    inv.setItem(3, new ItemBuilder(Material.AIR).toItemStack());
                    inv.setItem(5, new ItemBuilder(Material.AIR).toItemStack());
                    inv.setItem(18, new ItemBuilder(Material.AIR).toItemStack());
                    inv.setItem(27, new ItemBuilder(Material.AIR).toItemStack());
                    inv.setItem(26, new ItemBuilder(Material.AIR).toItemStack());
                    inv.setItem(35, new ItemBuilder(Material.AIR).toItemStack());
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }
    private void updateNSBrumeInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§eSolo§7 ->§b Zabuza et Haku")) {
                    inv.clear();
                    ItemStack glass = GUIItems.getPinkStainedGlassPane();
                    inv.setItem(0, glass);
                    inv.setItem(1, glass);
                    inv.setItem(9, glass);//haut gauche

                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());

                    inv.setItem(7, glass);//haut droite
                    inv.setItem(8, glass);
                    inv.setItem(17, glass);

                    inv.setItem(45, glass);
                    inv.setItem(46, glass);
                    inv.setItem(36, glass);//bas gauche

                    inv.setItem(44, glass);
                    inv.setItem(52, glass);
                    inv.setItem(53, glass);//bas droite

                    inv.setItem(2, new ItemBuilder(Material.ANVIL).toItemStack());
                    inv.setItem(3, new ItemBuilder(Material.ANVIL).toItemStack());
                    inv.setItem(5, new ItemBuilder(Material.ANVIL).toItemStack());
                    for (GameState.Roles roles : GameState.Roles.values()) {
                        if (roles.getTeam() == TeamList.Zabuza_et_Haku) {
                            String l1;
                            if (gameState.getAvailableRoles().get(roles) > 0) {
                                l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
                            } else {
                                l1 = "§c(0)";
                            }
                            inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
                        }
                    }
                    inv.setItem(2, new ItemBuilder(Material.AIR).toItemStack());
                    inv.setItem(3, new ItemBuilder(Material.AIR).toItemStack());
                    inv.setItem(5, new ItemBuilder(Material.AIR).toItemStack());
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }
    private void openConfigBijusInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equalsIgnoreCase("Configuration ->§6 Bijus")) {
                    inv.setItem(8, GUIItems.getSelectBackMenu());
                    for (Bijus bijus : Bijus.values()) {
                        inv.addItem( bijus.getBiju().getItemInMenu());
                    }
                }
            }
        }
    }
    private void updateEventInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§fConfiguration§7 -> §6Événements")) {
                    inv.clear();
                    inv.setItem(8, GUIItems.getSelectBackMenu());
                    for (Events e : Events.values()) {
                        ItemStack item;
                        if (e == Events.DemonKingTanjiro) {
                            if (gameState.getAvailableEvents().contains(e)) {
                                item = new ItemBuilder(Material.BLAZE_ROD)
                                        .addEnchant(Enchantment.ARROW_FIRE, 1)
                                        .hideEnchantAttributes()
                                        .setName(e.getName())
                                        .setLore("§fTiming d'apparition:§6 "+StringUtils.secondsTowardsBeautifulinScoreboard(gameState.DKminTime),
                                                "§a+1m§f (Clique gauche)",
                                                "§c-1m§f (Clique droit)",
                                                "§a+1%§f (Shift + Clique)",
                                                "§c-1%§f (Drop)",
                                                "§fPourcentage actuelle:§b "+gameState.DKTProba+"%")
                                        .toItemStack();
                                inv.addItem(item);
                            }
                        }
                        if (e == Events.AkazaVSKyojuro) {
                            if (gameState.getAvailableEvents().contains(e)) {
                                item = new ItemBuilder(Material.IRON_SWORD)
                                        .addEnchant(Enchantment.ARROW_DAMAGE, 1)
                                        .hideAllAttributes()
                                        .setName(e.getName())
                                        .setLore("§fTiming d'apparition:§6 "+StringUtils.secondsTowardsBeautifulinScoreboard(gameState.AkazaVsKyojuroTime),
                                                "§a+1m§f (Clique gauche)",
                                                "§c-1m§f (Clique droit)",
                                                "§a+1%§f (Shift + Clique)",
                                                "§c-1%§f (Drop)",
                                                "§fPourcentage actuelle:§b "+gameState.AkazaVSKyojuroProba+"%")
                                        .toItemStack();
                                inv.addItem(item);
                            }
                        }
                        if (e == Events.Alliance) {
                            if (gameState.getAvailableEvents().contains(e)) {
                                item = new ItemBuilder(Material.LAVA_BUCKET)
                                        .addEnchant(Enchantment.ARROW_DAMAGE, 1)
                                        .hideAllAttributes()
                                        .setName(e.getName())
                                        .setLore("§fTiming d'apparition:§6 "+StringUtils.secondsTowardsBeautifulinScoreboard(gameState.AllianceTime),
                                                "§a+1m§f (Clique gauche)",
                                                "§c-1m§f (Clique droit)",
                                                "§a+1%§f (Shift + Clique)",
                                                "§c-1%§f (Drop)",
                                                "§fPourcentage actuelle:§b "+gameState.AllianceProba+"%")
                                        .toItemStack();
                                inv.addItem(item);
                            }
                        }
                    }
                }
            }
        }
    }
}