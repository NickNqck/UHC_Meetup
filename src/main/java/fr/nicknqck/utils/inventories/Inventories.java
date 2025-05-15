package fr.nicknqck.utils.inventories;

import fr.nicknqck.Border;
import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.entity.bijuv2.BijuBase;
import fr.nicknqck.events.ds.Event;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.scenarios.Scenarios;
import fr.nicknqck.scenarios.impl.AntiPvP;
import fr.nicknqck.scenarios.impl.CutClean;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.rank.ChatRank;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class Inventories {
    private final GameState gameState;
    public Inventories(GameState gameState) {
        this.gameState = gameState;
    }
    public void updateSlayerInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("DemonSlayer ->§a Slayers")) {
                    this.setRoleInventory(inv, GUIItems.getGreenStainedGlassPane());
                    inv.setItem(3, GUIItems.getSelectDemonButton());//haut milleu
                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    inv.setItem(5, GUIItems.getSelectSoloButton());
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
                    clearRoleInventory(inv);
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
                    this.setRoleInventory(inv, GUIItems.getRedStainedGlassPane());

                    inv.setItem(3, GUIItems.getSelectSlayersButton());//haut milleu
                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    inv.setItem(5, GUIItems.getSelectSoloButton());

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
                    this.clearRoleInventory(inv);
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }
    public void updateDSSoloInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("DemonSlayer -> §eSolo")) {
                    this.setRoleInventory(inv, GUIItems.getOrangeStainedGlassPane());

                    inv.setItem(3, GUIItems.getSelectDemonButton());//haut milleu
                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    inv.setItem(5, GUIItems.getSelectSlayersButton());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());

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
                    clearRoleInventory(inv);
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }

    public void menuUpdater(Player p){
        updateAdminInventory(p);
        updateScenarioInventory(p);
        updateSelectInventory(p);
        updateSlayerInventory(p);
        updateDSSoloInventory(p);
        updateDemonInventory(p);
        updateMahrInventory(p);
        updateTitansInventory(p);
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
        updateMCInventory(p);
        updateOverworldInventory(p);
    }

    public void updateSoldatInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§fAOT§7 ->§a Soldats")) {
                    this.setRoleInventory(inv, GUIItems.getGreenStainedGlassPane());

                    inv.setItem(2, GUIItems.getSelectSoloButton());
                    inv.setItem(3, GUIItems.getSelectTitanButton());//haut milleu
                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    inv.setItem(5, GUIItems.getSelectMahrButton());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());
                    inv.setItem(49, GUIItems.getSelectConfigAotButton());

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
                    this.clearRoleInventory(inv);
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }
    public void updateTitansInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equalsIgnoreCase("§fAOT§7 ->§c Titans")) {
                    this.setRoleInventory(inv, GUIItems.getRedStainedGlassPane());

                    inv.setItem(2, GUIItems.getSelectSoloButton());
                    inv.setItem(3, GUIItems.getSelectMahrButton());//haut milleu
                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    inv.setItem(5, GUIItems.getSelectSoldatButton());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());
                    inv.setItem(49, GUIItems.getSelectConfigAotButton());

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
                    this.clearRoleInventory(inv);
                }
            }
        }
    }
    public void updateMahrInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equalsIgnoreCase("§fAOT§7 ->§9 Mahr")) {
                    this.setRoleInventory(inv, GUIItems.getSBluetainedGlassPane());

                    inv.setItem(2, GUIItems.getSelectSoloButton());
                    inv.setItem(3, GUIItems.getSelectTitanButton());//haut milleu
                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    inv.setItem(5, GUIItems.getSelectSoldatButton());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());

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
                    this.clearRoleInventory(inv);
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }
    public void updateAOTSoloInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§fAOT§7 -> §eSolo")) {
                    this.setRoleInventory(inv, GUIItems.getOrangeStainedGlassPane());

                    inv.setItem(2, GUIItems.getSelectMahrButton());
                    inv.setItem(3, GUIItems.getSelectTitanButton());//haut milleu
                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    inv.setItem(5, GUIItems.getSelectSoldatButton());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());

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
                    this.clearRoleInventory(inv);
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }
    public void updateAOTConfiguration(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equalsIgnoreCase("Configuration -> AOT")) {
                    this.setRoleInventory(inv, new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").toItemStack());

                    inv.setItem(4, GUIItems.getSelectBackMenu());

                    inv.setItem(10, new ItemBuilder(Material.BOW).setName("§rCooldown Equipement Tridimentionnel").setLore("§fCooldownActuel: "+Main.getInstance().getGameConfig().getTridiCooldown()).toItemStack());
                    if (gameState.rod) {
                        inv.setItem(11, new ItemBuilder(Material.FISHING_ROD).setName("§rEquipement Tridimentionnel").setLore("§fEquipement actuel:§l Rod Tridimentionnelle").toItemStack());
                    }else {
                        inv.setItem(11, new ItemBuilder(Material.BOW).setName("§rEquipement Tridimentionnel").setLore("§fÉquipement actuel:§l Arc Tridimentionnelle").toItemStack());
                    }
                    inv.setItem(12, new ItemBuilder(Material.LAVA_BUCKET).setName("§r§6Lave§f pour les titans (transformé)").setLore(Main.getInstance().getGameConfig().isLaveTitans() ? "§aActivé" : "§cDésactivé").toItemStack());
                    this.clearRoleInventory(inv);
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }

    public void updateConfigInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§fConfiguration de la partie")) {
                    inv.clear();
                    inv.addItem(new ItemBuilder(Material.STAINED_GLASS_PANE).setAmount(1).setDurability(5).setName("§r§fTaille de la bordure maximum").setLore(
                            "§r§f[50b < "+ Border.getMaxBorderSize()+" > 2400b",
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
                            "§r§fDurée actuel:§6 "+ StringUtils.secondsTowardsBeautiful(Main.getInstance().getGameConfig().getMaxTimeDay()),
                            "§r§fClique gauche: §a+10 secondes",
                            "§r§fClique droit: §c-10 secondes"
                    ).toItemStack());
                    inv.addItem(GUIItems.getTabRoleInfo(gameState));
                    inv.addItem(Items.geteclairmort());
                    inv.addItem(new ItemBuilder(Material.REDSTONE).setName("§r§fTemp avant l'§cAssassin").setLore(
                            "§r§f[10 secondes < "+StringUtils.secondsTowardsBeautiful(Main.getInstance().getGameConfig().getTimingAssassin())+" > 5 minutes",
                            "§r§fClique gauche: §a+10 secondes",
                            "§r§fClique droit: §c-10 secondes"
                    ).toItemStack());
                    inv.addItem(new ItemBuilder(Material.WATER_BUCKET).setName("§r§fTemp avant despawn de l'§bEau").setLore(
                            "§r§f[0 secondes < "+StringUtils.secondsTowardsBeautiful(Main.getInstance().getGameConfig().getWaterEmptyTiming())+" > 1 minutes",
                            "§r§fClique gauche: §a+1 secondes",
                            "§r§fClique droit: §c-1 secondes",
                            "§r§f(0 secondes =§c désactiver"
                    ).toItemStack());
                    inv.addItem(new ItemBuilder(Material.LAVA_BUCKET).setName("§r§fTemp avant despawn de la§6 Lave").setLore(
                            "§r§f[0 seconde < "+StringUtils.secondsTowardsBeautiful(Main.getInstance().getGameConfig().getLavaEmptyTiming())+" > 1 minutes",
                            "§r§fClique gauche: §a+1 seconde",
                            "§r§fClique droit: §c-1 seconde",
                            "§r§f(0 secondes =§c désactiver"
                    ).toItemStack());
                    inv.addItem(new ItemBuilder(Material.NETHER_STAR).setName("§fBijus").setLore(Main.getInstance().getBijuManager().isBijuEnable() ?
                            "§aActivé" : "§cDésactivé",
                            "§r§fShift + Clique: Permet de configurer les bijus§7 (§aNaruto UHC§7)").toItemStack());
                    inv.addItem(new ItemBuilder(Material.GHAST_TEAR).setName("§cInfection").setLore(
                            "§fTemp avant infection: ",
                            "§a+5s§f (Clique gauche)",
                            "§c-5s§f (Clique droit)",
                            "§fTemp actuelle:§b "+StringUtils.secondsTowardsBeautiful(Main.getInstance().getGameConfig().getInfectionTime())
                    ).toItemStack());
                    inv.addItem(new ItemBuilder(Material.TNT).setName("§fGrief du terrain par les§c TNT").setLore(gameState.isTNTGrief() ? "§aActivé" : "§cDésactivé").toItemStack());
                    inv.addItem(new ItemBuilder(Material.INK_SACK).setName("§fLame").setLore("§7Si activé, donne une§a lame§7 à l'annonce des rôles à tout les joueurs de la partie","",Main.getInstance().getGameConfig().isGiveLame() ? "§aActivé" : "§cDésactivé").toItemStack());
                    inv.addItem(new ItemBuilder(Material.EMERALD).setName("§dKrystalBeast").setLore(
                            "§4// NOUVEAUTÉ \\",
                            "",
                            "§a Shift§f + Clique pour configurer les§d bêtes de krystal§7 (§dKrystal UHC§7)"
                    ).toItemStack());
                    inv.addItem(new ItemBuilder(Material.DIAMOND_SWORD).setName("§fPourcentage de force").setLore(
                            "§c"+Main.getInstance().getGameConfig().getForcePercent()+"%",
                            "",
                            "§fVanilla: §c130%",
                            "§aMinimum:§c 10%"
                    ).toItemStack());
                    inv.setItem(26, GUIItems.getSelectBackMenu());
                }
            }
        }
        player.updateInventory();
    }

    public void updateNSShinobiInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§aNaruto§7 ->§a Shinobi")) {
                    ItemStack glass = GUIItems.getGreenStainedGlassPane();
                    setRoleInventory(inv, glass);

                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());

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
                    this.clearRoleInventory(inv);
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }
    public void updateNSAkatsukiInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§aNaruto§7 ->§c Akatsuki")) {
                    this.setRoleInventory(inv, GUIItems.getRedStainedGlassPane());

                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());

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
                    this.clearRoleInventory(inv);
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }
    public void updateNSOrochimaruInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§aNaruto§7 ->§5 Orochimaru")) {
                    this.setRoleInventory(inv, GUIItems.getPurpleStainedGlassPane());

                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());

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
                    this.clearRoleInventory(inv);
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }
    public void updateNSSoloInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§aNaruto§7 ->§e Solo")) {
                    this.setRoleInventory(inv, GUIItems.getOrangeStainedGlassPane());

                    inv.setItem(2, GUIItems.getSelectJubiButton());
                    inv.setItem(3, GUIItems.getSelectBrumeButton());
                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    inv.setItem(5, GUIItems.getSelectKumogakureButton());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());

                    for (GameState.Roles roles : GameState.Roles.values()) {
                        if (!roles.getTeam().equals(TeamList.Solo) && !roles.getTeam().equals(TeamList.Shisui))continue;
                        if (roles.getMdj().equals("ns")) {
                            String l1;
                            if (gameState.getAvailableRoles().get(roles) > 0) {
                                l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
                            } else {
                                l1 = "§c(0)";
                            }
                            inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
                        }
                    }
                    this.clearRoleInventory(inv);
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }
    public void updateNSJubiInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§eSolo§7 ->§d Jubi")) {
                    ItemStack glass = GUIItems.getPinkStainedGlassPane();
                    this.setRoleInventory(inv, glass);

                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());

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
                    this.clearRoleInventory(inv);
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }
    public void updateNSBrumeInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§eSolo§7 ->§b Zabuza et Haku")) {
                    ItemStack glass = GUIItems.getPinkStainedGlassPane();
                    this.setRoleInventory(inv, glass);

                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());

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
                    this.clearRoleInventory(inv);
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }
    public void updateNSKumogakure(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§eSolo§7 ->§6 Kumogakure")) {
                    ItemStack glass = GUIItems.getOrangeStainedGlassPane();
                    this.setRoleInventory(inv, glass);

                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());

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
                    this.clearRoleInventory(inv);
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }

    public void updateOverworldInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§aMinecraft§7 ->§a Overworld")) {
                    final ItemStack glass = GUIItems.getGreenStainedGlassPane();
                    this.setRoleInventory(inv, glass);

                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());

                    for (GameState.Roles roles : GameState.Roles.values()) {
                        if (roles.getTeam() == TeamList.OverWorld) {
                            String l1;
                            if (gameState.getAvailableRoles().get(roles) > 0) {
                                l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
                            } else {
                                l1 = "§c(0)";
                            }
                            inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
                        }
                    }
                    this.clearRoleInventory(inv);
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }
    public void updateNetherInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§aMinecraft§7 ->§c Nether")) {
                    final ItemStack glass = GUIItems.getRedStainedGlassPane();
                    this.setRoleInventory(inv, glass);

                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());

                    for (GameState.Roles roles : GameState.Roles.values()) {
                        if (roles.getTeam() == TeamList.Nether) {
                            String l1;
                            if (gameState.getAvailableRoles().get(roles) > 0) {
                                l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
                            } else {
                                l1 = "§c(0)";
                            }
                            inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
                        }
                    }
                    this.clearRoleInventory(inv);
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

                    if (Main.getInstance().isGoodServer()) {
                        inv.setItem(12, new ItemBuilder(Material.DIAMOND_PICKAXE).setName("§cMinage").setLore("§7État: "+(Main.getInstance().getGameConfig().isMinage() ? "§aActivé" : "§cDésactiver")).toItemStack());
                    }

                    inv.setItem(13, GUIItems.getPregen(gameState));
                    inv.setItem(16, new ItemBuilder(Material.GRASS).setName("§aChanger le monde de jeu").toItemStack());
                    inv.setItem(19, GUIItems.getSelectConfigButton());
                    inv.setItem(31, GUIItems.getSelectScenarioButton());
                    inv.setItem(28, GUIItems.getSelectInvsButton());
                    inv.setItem(37, GUIItems.getSelectEventButton());
                    if (AntiPvP.isAntipvplobby()) {
                        inv.setItem(40, AntiPvP.getlobbypvp());
                    } else {
                        inv.setItem(40, AntiPvP.getnotlobbypvp());
                    }
                    inv.setItem(43, GUIItems.getCrit(gameState));
                }
            }
        }
        player.updateInventory();
    }
    public void updateScenarioInventory(Player player) {
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
    public void updateSelectInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§fConfiguration§7 ->§6 Inventaire")) {
                    inv.setItem(48, new ItemBuilder(Material.GOLDEN_APPLE, Main.getInstance().getGameConfig().getStuffConfig().getNmbGap()).setName("§r§fNombre de pomme d'§eor").setLore(
                                    "§a+1§f (Clique gauche)",
                                    "§c-1§f (Clique droit)",
                                    "§r§fNombre actuelle:§e "+Main.getInstance().getGameConfig().getStuffConfig().getNmbGap())
                            .toItemStack());
                    inv.setItem(0, new ItemBuilder(Material.DIAMOND_HELMET).setLore(
                                    "§a+1§f (Clique gauche)",
                                    "§c-1§f (Clique droit)",
                                    "§r§fNiveau de protection:§b "+Main.getInstance().getGameConfig().getStuffConfig().getProtectionHelmet()
                            ).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)

                            .toItemStack());
                    inv.setItem(9, new ItemBuilder(Material.DIAMOND_CHESTPLATE).setLore(
                                    "§a+1§f (Clique gauche)",
                                    "§c-1§f (Clique droit)",
                                    "§r§fNiveau de protection:§b "+Main.getInstance().getGameConfig().getStuffConfig().getProtectionChestplate()
                            ).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).hideEnchantAttributes()
                            .toItemStack());
                    inv.setItem(18, new ItemBuilder(Material.IRON_LEGGINGS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).hideEnchantAttributes()
                            .setLore("§a+1§f (Clique gauche)",
                                    "§c-1§f (Clique droit)",
                                    "§r§fNiveau de protection:§b "+Main.getInstance().getGameConfig().getStuffConfig().getProtectionLeggings())
                            .toItemStack());
                    inv.setItem(27, new ItemBuilder(Material.DIAMOND_BOOTS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).hideEnchantAttributes()
                            .setLore("§a+1§f (Clique gauche)",
                                    "§c-1§f (Clique droit)",
                                    "§r§fNiveau de protection:§b "+Main.getInstance().getGameConfig().getStuffConfig().getProtectionBoost())
                            .toItemStack());
                    inv.setItem(45, GUIItems.getdiamondsword());
                    inv.setItem(46, GUIItems.getblock());
                    inv.setItem(47, GUIItems.getbow());
                    inv.setItem(49, GUIItems.getEnderPearl());
                    inv.setItem(50, GUIItems.getGoldenCarrot());
                    inv.setItem(51, GUIItems.getlave());
                    inv.setItem(52, GUIItems.geteau());
                    inv.setItem(38, new ItemBuilder(Material.ARROW, Main.getInstance().getGameConfig().getStuffConfig().getNmbArrow()).setName("§fFlèches").setLore("","§7Max:§c 64","§7Minimum:§c 1","§7Actuelle:§c "+Main.getInstance().getGameConfig().getStuffConfig().getNmbArrow()).toItemStack());
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
                        switch (gameState.getMdj()) {
                            case DS:
                                inv.setItem(13, GUIItems.getSelectDSButton());
                                break;
                            case MC:
                                inv.setItem(13, GUIItems.getSelectMCButton());
                                break;
                            case AOT:
                                inv.setItem(13, GUIItems.getSelectAOTButton());
                                break;
                            case NS:
                                inv.setItem(13, GUIItems.getSelectNSButton());
                                break;
                            case KRYSTAL:
                                inv.setItem(13, GameState.MDJ.KRYSTAL.getItem());
                                break;
                        }
                    }
                    if (ChatRank.isHost(player)) {
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
                        if (mdj != GameState.MDJ.Aucun && mdj != GameState.MDJ.KRYSTAL){
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
    public void openConfigBijusInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equalsIgnoreCase("Configuration ->§6 Bijus")) {
                    for (int i = 0; i <= 8; i+=7){
                        inv.setItem(i, GUIItems.getOrangeStainedGlassPane());
                        inv.setItem(i+1, GUIItems.getOrangeStainedGlassPane());
                    }
                    for (int i = 27; i <= 35; i+=7){
                        inv.setItem(i, GUIItems.getOrangeStainedGlassPane());
                        inv.setItem(i+1, GUIItems.getOrangeStainedGlassPane());
                    }
                    for (int i = 9; i <= 18; i+=9){
                        inv.setItem(i, GUIItems.getOrangeStainedGlassPane());
                    }
                    for (int i = 9; i <= 18; i+=9){
                        inv.setItem(i, GUIItems.getOrangeStainedGlassPane());
                    }
                    for (int i = 17; i <= 26; i+=9){
                        inv.setItem(i, GUIItems.getOrangeStainedGlassPane());
                    }
                    inv.setItem(10, new ItemBuilder(Material.STAINED_GLASS)
                            .setAmount(1)
                            .setDurability(3)
                            .setLore("§f"+(Border.getMinBorderSize()+50)+" <§b "+Border.getMinBijuSpawn()+"§f > "+(Border.getMaxBijuSpawn()-50),"","§aClique gauche§f: §a+50 blocs", "§cClique droit§f: §c-50 blocs")
                            .setName("§r§fCoordonnée minimal de spawn des bijus")
                            .toItemStack());
                    inv.setItem(11, new ItemBuilder(Material.STAINED_GLASS)
                            .setAmount(1)
                            .setDurability(11)
                            .setLore("§f"+(Border.getMinBijuSpawn()+50)+" <§b "+Border.getMaxBijuSpawn()+"§f > "+(Border.getMaxBorderSize()-50),"","§aClique gauche§f: §a+50 blocs", "§cClique droit§f: §c-50 blocs")
                            .setName("§r§fCoordonnée maximal de spawn des bijus")
                            .toItemStack());
                    int i = 19;
                    if (!Main.getInstance().getBijuManager().getClassBijuMap().isEmpty()) {
                        for (@NonNull final Class<? extends BijuBase> clazz : Main.getInstance().getBijuManager().getClassBijuMap().keySet()) {
                            @NonNull final BijuBase bijuBase = Main.getInstance().getBijuManager().getClassBijuMap().get(clazz);
                            @NonNull final ItemBuilder item = new ItemBuilder(bijuBase.getItemInMenu());
                            if (Main.getInstance().getBijuManager().getBijuEnables().get(clazz)) {
                                item.setAmount(1);
                                item.setLore("§a§lActivé");
                            } else {
                                item.setAmount(0);
                                item.setLore("§c§lDésactivé");
                            }
                            inv.setItem(i, item.toItemStack());
                            i++;
                        }
                    }
                    inv.setItem(31, GUIItems.getSelectBackMenu());
                }
            }
        }
    }
    public void updateEventInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§fConfiguration§7 -> §6Événements")) {
                    inv.clear();
                    inv.setItem(8, GUIItems.getSelectBackMenu());
                    for (final Event event : Main.getInstance().getEventsManager().getEventsList()) {
                        inv.addItem(event.getMenuItem());
                    }
                }
            }
        }
    }
    public void updateMCInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§fRoles§7 ->§a Minecraft")) {
                    inv.clear();
                    inv.setItem(10, GUIItems.getSelectOverworldButton());
                    inv.setItem(12, GUIItems.getSelectNetherButton());
                    inv.setItem(26, GUIItems.getSelectBackMenu());
                }
            }
        }
        player.updateInventory();
        gameState.updateGameCanLaunch();
    }

    private void setRoleInventory(@NonNull final Inventory inventaire, @NonNull final ItemStack glass) {
        inventaire.clear();
        inventaire.setItem(0, glass);
        inventaire.setItem(1, glass);
        inventaire.setItem(9, glass);//haut gauche

        //	inv.setItem(2, GUIItems.getx());
        if (gameState.gameCanLaunch) inventaire.setItem(6, GUIItems.getStartGameButton());
        if (!gameState.gameCanLaunch) inventaire.setItem(6, GUIItems.getCantStartGameButton());

        inventaire.setItem(7, glass);//haut droite
        inventaire.setItem(8, glass);
        inventaire.setItem(17, glass);

        inventaire.setItem(45, glass);
        inventaire.setItem(46, glass);
        inventaire.setItem(36, glass);//bas gauche

        inventaire.setItem(44, glass);
        inventaire.setItem(52, glass);
        inventaire.setItem(53, glass);//bas droite

        inventaire.setItem(2, new ItemBuilder(Material.BRICK_STAIRS).toItemStack());
        inventaire.setItem(3, new ItemBuilder(Material.BRICK_STAIRS).toItemStack());
        inventaire.setItem(5, new ItemBuilder(Material.BRICK_STAIRS).toItemStack());
        inventaire.setItem(18, new ItemBuilder(Material.BRICK_STAIRS).toItemStack());
        inventaire.setItem(27, new ItemBuilder(Material.BRICK_STAIRS).toItemStack());
        inventaire.setItem(26, new ItemBuilder(Material.BRICK_STAIRS).toItemStack());
        inventaire.setItem(35, new ItemBuilder(Material.BRICK_STAIRS).toItemStack());
    }
    private void clearRoleInventory (@NonNull final Inventory inventory) {
        for (int slot = inventory.getSize()-1; slot > 0; slot--) {
            final ItemStack item = inventory.getItem(slot);
            if (item == null)continue;
            if (item.getType() == null)continue;
            if (item.getType().equals(Material.AIR))continue;
            if (item.getType().equals(Material.BRICK_STAIRS)) {
                inventory.setItem(slot, new ItemStack(Material.AIR));
            }
        }
    }

    public void openKrystalInventory(@NonNull final Player player) {
        final Inventory inv = Bukkit.createInventory(player, 27, "§dKrystal UHC");
        inv.setItem(13, GUIItems.getSelectSoloButton());
        inv.setItem(26, GUIItems.getSelectBackMenu());
        player.openInventory(inv);
    }

    public void openKrystalSoloInventory(@NonNull final Player player) {
        final Inventory inv = Bukkit.createInventory(player, 54, "§dKrystal UHC§7 ->§e Solo");
        setRoleInventory(inv, GUIItems.getPurpleStainedGlassPane());

        inv.setItem(3, GUIItems.getSelectBackMenu());//haut milleu
        inv.setItem(4, GUIItems.getSelectBackMenu());
        inv.setItem(5, GUIItems.getSelectBackMenu());

        for (final GameState.Roles roles : GameState.Roles.values()) {
            if (!roles.getTeam().equals(TeamList.Solo))continue;
            if (roles.getMdj().equals("custom") || roles.getMdj().equals("krystal")) {
                String l1;
                if (gameState.getAvailableRoles().get(roles) > 0) {
                    l1 = "§c("+gameState.getAvailableRoles().get(roles)+")";
                } else {
                    l1 = "§c(0)";
                }
                inv.addItem(new ItemBuilder(roles.getItem()).setAmount(gameState.getAvailableRoles().get(roles)).setLore(l1, "", "§fGDesign: "+roles.getGDesign()).toItemStack());
            }
        }
        clearRoleInventory(inv);
        player.openInventory(inv);
    }
}