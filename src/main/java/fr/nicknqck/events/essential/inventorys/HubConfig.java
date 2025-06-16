package fr.nicknqck.events.essential.inventorys;

import fr.nicknqck.Border;
import fr.nicknqck.GameState;
import fr.nicknqck.HubListener;
import fr.nicknqck.Main;
import fr.nicknqck.config.GameConfig;
import fr.nicknqck.entity.bijus.Bijus;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.items.Items;
import fr.nicknqck.runnables.PregenerationTask;
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
                            if (Main.getInstance().getGameConfig().getCritPercent() < 50) {
                                Main.getInstance().getGameConfig().setCritPercent(Main.getInstance().getGameConfig().getCritPercent()+1);
                            }
                        } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                            if (Main.getInstance().getGameConfig().getCritPercent() > 0) {
                                Main.getInstance().getGameConfig().setCritPercent(Main.getInstance().getGameConfig().getCritPercent()-1);
                            }
                        }
                    } else if (item.isSimilar(GUIItems.getPregen(gameState))) {
                        if (!gameState.hasPregen){
                            new PregenerationTask(Main.getInstance().getWorldManager().getGameWorld(), Border.getMaxBorderSize());
                            gameState.hasPregen = true;
                        }
                    } else if (item.isSimilar(GUIItems.getSelectEventButton())) {
                        player.openInventory(GUIItems.getEventSelectGUI());
                        Main.getInstance().getInventories().updateEventInventory(player);
                    } else if (item.getType().equals(Material.GRASS)) {
                        if (goodIP()) {
                            Main.getInstance().getWorldConfig().openInitConfig(player);
                        } else {
                            player.closeInventory();
                            Main.getInstance().getWorldListener().setEnable(true);
                            if (!Main.getInstance().initGameWorld()) {
                                player.sendMessage("§cImpossible de supprimer le monde de jeu actuel, un joueur est peut être encore dedans ?");
                                event.setCancelled(true);
                                return;
                            }
                            event.getWhoClicked().sendMessage("§7Vous avez crée un nouveau monde.");
                            if (gameState.hasPregen) {
                                gameState.hasPregen = false;
                            }
                            Main.getInstance().getWorldListener().setEnable(false);
                        }
                    } else if (item.getType().equals(Material.DIAMOND_PICKAXE) && Main.getInstance().isGoodServer()) {
                        Main.getInstance().getGameConfig().setMinage(!Main.getInstance().getGameConfig().isMinage());
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
                case "§fConfiguration de la partie":
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
                                Main.getInstance().getGameConfig().setTimingAssassin(Math.min(60*5, Main.getInstance().getGameConfig().getTimingAssassin()+10));
                            } else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                Main.getInstance().getGameConfig().setTimingAssassin(Math.max(10, Main.getInstance().getGameConfig().getTimingAssassin()-10));
                            }
                        }
                        if (item.getType().equals(Material.TNT)) {
                            gameState.setTNTGrief(!gameState.isTNTGrief());
                        }
                        if (name.equals("§fLame")) {
                            Main.getInstance().getGameConfig().setGiveLame(!Main.getInstance().getGameConfig().isGiveLame());
                        }
                        if (item.getType().equals(Material.EMERALD)) {
                            if (action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                                Main.getInstance().getKrystalBeastManager().openConfigBeastInventory(player);
                            }
                        }
                        Border.setMaxBorderSize(Math.max(50, Math.min(Border.getMaxBorderSize(), 2400)));
                        Border.setMinBorderSize(Math.max(50, Math.min(Border.getMinBorderSize(), Border.getMaxBorderSize())));
                        gameState.pvpTimer = Math.max(0, Math.min(gameState.pvpTimer, 40*60));
                        gameState.roleTimer = Math.max(0, Math.min(gameState.roleTimer, 40*60));
                        if (name.contains("Durée du jour (et de la nuit)")) {
                            if (ChatRank.isHost(player)) {
                                if (action.equals(InventoryAction.PICKUP_ALL)) {
                                    Main.getInstance().getGameConfig().setMaxTimeDay(Main.getInstance().getGameConfig().getMaxTimeDay()+10);
                                    player.updateInventory();
                                    Main.getInstance().getInventories().updateConfigInventory(player);
                                } else {
                                    if (action.equals(InventoryAction.PICKUP_HALF)) {
                                        Main.getInstance().getGameConfig().setMaxTimeDay(Main.getInstance().getGameConfig().getMaxTimeDay()-10);
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
                        if (name.equals("§fBijus")) {
                            if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                                player.closeInventory();
                                player.openInventory(Bukkit.createInventory(player, 9*4, "Configuration ->§6 Bijus"));
                                Main.getInstance().getInventories().openConfigBijusInventory(player);
                            } else {
                                Main.getInstance().getBijuManager().setBijuEnable(!Main.getInstance().getBijuManager().isBijuEnable());
                            }
                        }
                        if (name.equals("§cInfection")) {
                            if (action.equals(InventoryAction.PICKUP_ALL)) {
                                Main.getInstance().getGameConfig().setInfectionTime(Math.min(60*20, Main.getInstance().getGameConfig().getInfectionTime()+10));
                            }else if (action.equals(InventoryAction.PICKUP_HALF)) {
                                Main.getInstance().getGameConfig().setInfectionTime(Math.max(10, Main.getInstance().getGameConfig().getInfectionTime()-10));
                            }
                        }
                        if (name.equalsIgnoreCase("§fPourcentage de Force")) {
                            if (event.isLeftClick()) {
                                Main.getInstance().getGameConfig().setForcePercent(Math.max(10, Main.getInstance().getGameConfig().getForcePercent()+5));
                            } else {
                                Main.getInstance().getGameConfig().setForcePercent(Math.max(10, Main.getInstance().getGameConfig().getForcePercent()-5));
                            }
                        }
                        if (name.equals("§fTypes de stun")) {
                            if (Main.getInstance().getGameConfig().getStunType().equals(GameConfig.StunType.TELEPORT)) {
                                Main.getInstance().getGameConfig().setStunType(GameConfig.StunType.STUCK);
                            } else {
                                Main.getInstance().getGameConfig().setStunType(GameConfig.StunType.TELEPORT);
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
                            bijus.getBiju().setEnable(!bijus.getBiju().isEnable());
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
            }
            if (item.isSimilar(GUIItems.getStartGameButton()) && ChatRank.isHost(player) && gameState.gameCanLaunch) {
                HubListener.getInstance().StartGame(player);
                event.setCancelled(true);
            }
        }
    }
    private boolean goodIP() {
        return true;
      /*  String serverIP = Bukkit.getServer().getIp();
        int serverPort = Bukkit.getServer().getPort();


        if ("127.0.0.1".equals(serverIP) || "localhost".equals(serverIP)) {
            return true;
        } else return "62.210.100.59".equals(serverIP) && serverPort == 25565;*/
    }
}