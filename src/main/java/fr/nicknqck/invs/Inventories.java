package fr.nicknqck.invs;

import fr.nicknqck.Border;
import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.config.GameConfig;
import fr.nicknqck.entity.bijuv2.BijuBase;
import fr.nicknqck.enums.MDJ;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.ds.Event;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.items.Items;
import fr.nicknqck.interfaces.IRole;
import fr.nicknqck.enums.TeamList;
import fr.nicknqck.scenarios.Scenarios;
import fr.nicknqck.scenarios.impl.AntiPvP;
import fr.nicknqck.scenarios.impl.CutClean;
import fr.nicknqck.utils.fastinv.PaginatedFastInv;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.StringUtils;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Inventories {
    private final GameState gameState;
    public Inventories(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * Retourne la liste des IRole dont le rôle appartient à la TeamList donnée,
     * triés par ordre croissant de getNmb().
     *
     * @param team La TeamList voulue
     * @return Liste triée de IRole
     */
    public List<IRole> getRolesByTeam(TeamList team) {
        return Main.getInstance().getRoleManager().getRolesRegistery().values().stream()
                .filter(iRole -> iRole.getRoles().getTeam() == team)
                .sorted(Comparator.comparingInt(iRole -> iRole.getRoles().getNmb()))
                .collect(Collectors.toList());
    }

    public void updateSlayerInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§fDemonSlayer§7 ->§a Slayers")) {
                    new TeamRoleInventory(inv.getTitle(), TeamList.Slayer, "ds") {

                        @Override
                        protected void onBackClick(Player player) {
                            player.openInventory(GUIItems.getDemonSlayerInventory());
                            Main.getInstance().getInventories().updateDSInventory(player);
                        }
                    }.open(player);
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
                if (inv.getTitle().equals("§fDemonSlayer§7 -> §cDémons")) {
                    new TeamRoleInventory(inv.getTitle(), TeamList.Demon, "ds") {

                        @Override
                        protected void onBackClick(Player player) {
                            player.openInventory(GUIItems.getDemonSlayerInventory());
                            Main.getInstance().getInventories().updateDSInventory(player);
                        }
                    }.open(player);
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
                if (inv.getTitle().equals("§fDemonSlayer§7 -> §eSolo")) {
                    new TeamRoleInventory(inv.getTitle(), TeamList.Solo, "ds") {

                        @Override
                        protected void onBackClick(Player player) {
                            player.openInventory(GUIItems.getDemonSlayerInventory());
                            Main.getInstance().getInventories().updateDSInventory(player);
                        }
                    }.open(player);
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
    }

    public void updateSoldatInventory(Player player) {
        InventoryView invView = player.getOpenInventory();
        if (invView != null) {
            Inventory inv = invView.getTopInventory();
            if (inv != null) {
                if (inv.getTitle().equals("§fAOT§7 ->§a Soldats")) {
                    new TeamRoleInventory(inv.getTitle(), TeamList.Soldat, "aot") {

                        @Override
                        protected void onBackClick(Player player) {
                            player.openInventory(GUIItems.getSelectAOTInventory());
                            updateAOTInventory(player);
                        }
                    }.open(player);
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
                    new TeamRoleInventory(inv.getTitle(), TeamList.Titan, "aot") {

                        @Override
                        protected void onBackClick(Player player) {
                            player.openInventory(GUIItems.getSelectAOTInventory());
                            updateAOTInventory(player);
                        }
                    }.open(player);
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
                    new TeamRoleInventory(inv.getTitle(), TeamList.Mahr, "aot") {

                        @Override
                        protected void onBackClick(Player player) {
                            player.openInventory(GUIItems.getSelectAOTInventory());
                            updateAOTInventory(player);
                        }
                    }.open(player);
                    /*9
                    this.setRoleInventory(inv, GUIItems.getSBluetainedGlassPane());

                    inv.setItem(2, GUIItems.getSelectSoloButton());
                    inv.setItem(3, GUIItems.getSelectTitanButton());//haut milleu
                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    inv.setItem(5, GUIItems.getSelectSoldatButton());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());

                    inv.setItem(49, GUIItems.getSelectConfigAotButton());

                    for (Roles roles : Roles.values()) {
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

                     */
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
                    new TeamRoleInventory(inv.getTitle(), TeamList.Solo, "aot") {

                        @Override
                        protected void onBackClick(Player player) {
                            player.openInventory(GUIItems.getSelectAOTInventory());
                            updateAOTInventory(player);
                        }
                    }.open(player);
                    /*9
                    this.setRoleInventory(inv, GUIItems.getOrangeStainedGlassPane());

                    inv.setItem(2, GUIItems.getSelectMahrButton());
                    inv.setItem(3, GUIItems.getSelectTitanButton());//haut milleu
                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    inv.setItem(5, GUIItems.getSelectSoldatButton());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());

                    for (Roles roles : Roles.values()) {
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

                     */
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

                    inv.setItem(10, new ItemBuilder(Material.BOW).setName("§rCooldown Equipement Tridimentionnel").setLore("§fCooldownActuel: "+Main.getInstance().getGameConfig().getAotConfig().getTridiCooldown()).toItemStack());
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
                    inv.addItem(new ItemBuilder(Material.TNT).setName("§fGrief du terrain par les§c TNT").setLore(Main.getInstance().getGameConfig().isTntGrief() ? "§aActivé" : "§cDésactivé").toItemStack());
                    inv.addItem(new ItemBuilder(Material.DIAMOND_SWORD).setName("§fPourcentage de force").setLore(
                            "§c"+Main.getInstance().getGameConfig().getForcePercent()+"%",
                            "",
                            "§fVanilla: §c130%",
                            "§aMinimum:§c 10%"
                    ).toItemStack());
                    inv.addItem(new ItemBuilder(Material.IRON_CHESTPLATE).setName("§fPourcentage de Résistance").setLore(
                            "§9Résistance I§f: "+Main.getInstance().getGameConfig().getResiPercent()+"%",
                            "§9Résistance II§f: "+(Main.getInstance().getGameConfig().getResiPercent()*2)+"%",
                            "",
                            "§fVanilla: §c20%",
                            "§aMinimum:§c 10%"
                    ).toItemStack());
                    inv.addItem(new ItemBuilder(Material.TRIPWIRE_HOOK).setName("§fTypes de stun").setLore(
                            "",
                            (Main.getInstance().getGameConfig().getStunType().equals(GameConfig.StunType.TELEPORT) ?
                                    "§8 -§r "+ GameConfig.StunType.TELEPORT.getColor()+"§l"+ GameConfig.StunType.TELEPORT.getName()
                                    :
                                    "§8 -§r "+ GameConfig.StunType.TELEPORT.getColor() + GameConfig.StunType.TELEPORT.getName()),
                            (Main.getInstance().getGameConfig().getStunType().equals(GameConfig.StunType.STUCK) ?
                                    "§8 -§r"+ GameConfig.StunType.STUCK.getColor()+" §l"+ GameConfig.StunType.STUCK.getName()
                                    :
                                    "§8 -§r "+ GameConfig.StunType.STUCK.getColor() + GameConfig.StunType.STUCK.getName())
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
                    new TeamRoleInventory(inv.getTitle(), TeamList.Shinobi, "ns") {

                        @Override
                        protected void onBackClick(Player player) {
                            player.openInventory(GUIItems.getSelectNSInventory());
                            Main.getInstance().getInventories().updateNSInventory(player);
                        }
                    }.open(player);
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
                    new TeamRoleInventory(inv.getTitle(), TeamList.Akatsuki, "ns") {

                        @Override
                        protected void onBackClick(Player player) {
                            player.openInventory(GUIItems.getSelectNSInventory());
                            Main.getInstance().getInventories().updateNSInventory(player);
                        }
                    }.open(player);
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
                    new TeamRoleInventory(inv.getTitle(), TeamList.Orochimaru, "ns") {

                        @Override
                        protected void onBackClick(Player player) {
                            player.openInventory(GUIItems.getSelectNSInventory());
                            Main.getInstance().getInventories().updateNSInventory(player);
                        }
                    }.open(player);
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
                    new TeamRoleInventory(inv.getTitle(), TeamList.Solo, "ns") {

                        @Override
                        protected void onBackClick(Player player) {
                            player.openInventory(GUIItems.getSelectNSInventory());
                            Main.getInstance().getInventories().updateNSInventory(player);
                        }

                        @Override
                        protected void addSomeItem(@NonNull TeamRoleInventory teamRoleInventory) {
                            teamRoleInventory.setItem(2, GUIItems.getSelectJubiButton(), event -> {
                                event.getWhoClicked().openInventory(GUIItems.getSelectNSJubiInventory());
                                Main.getInstance().getInventories().updateNSJubiInventory((Player) event.getWhoClicked());
                            });
                            teamRoleInventory.setItem(3, GUIItems.getSelectBrumeButton(), event -> {
                                event.getWhoClicked().openInventory(GUIItems.getSelectNSBrumeInventory());
                                Main.getInstance().getInventories().updateNSBrumeInventory((Player) event.getWhoClicked());
                            });
                            teamRoleInventory.setItem(5, GUIItems.getSelectKumogakureButton(), event -> {
                                event.getWhoClicked().openInventory(Bukkit.createInventory(event.getWhoClicked(), 54, "§eSolo§7 ->§6 Kumogakure"));
                                Main.getInstance().getInventories().updateNSKumogakure((Player) event.getWhoClicked());
                            });
                            super.addSomeItem(teamRoleInventory);
                        }
                    }.open(player);
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
                    new TeamRoleInventory(inv.getTitle(), TeamList.Jubi, "ns") {

                        @Override
                        protected void onBackClick(Player player) {
                            player.openInventory(GUIItems.getSelectNSSoloInventory());
                            Main.getInstance().getInventories().updateNSSoloInventory(player);
                        }
                    }.open(player);
                    /*9
                    ItemStack glass = GUIItems.getPinkStainedGlassPane();
                    this.setRoleInventory(inv, glass);

                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());

                    for (Roles roles : Roles.values()) {
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
                     */
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
                    new TeamRoleInventory(inv.getTitle(), TeamList.Zabuza_et_Haku, "ns") {

                        @Override
                        protected void onBackClick(Player player) {
                            player.openInventory(GUIItems.getSelectNSSoloInventory());
                            Main.getInstance().getInventories().updateNSSoloInventory(player);
                        }
                    }.open(player);
                    /*2
                    ItemStack glass = GUIItems.getPinkStainedGlassPane();
                    this.setRoleInventory(inv, glass);

                    inv.setItem(4, GUIItems.getSelectBackMenu());
                    if (gameState.gameCanLaunch)inv.setItem(6, GUIItems.getStartGameButton());
                    if (!gameState.gameCanLaunch)inv.setItem(6, GUIItems.getCantStartGameButton());

                    for (Roles roles : Roles.values()) {
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

                     */
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
                    new TeamRoleInventory(inv.getTitle(), TeamList.Kumogakure, "ns") {

                        @Override
                        protected void onBackClick(Player player) {
                            player.openInventory(GUIItems.getSelectNSSoloInventory());
                            Main.getInstance().getInventories().updateNSSoloInventory(player);
                        }
                    }.open(player);
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
                if (inv.getTitle().equalsIgnoreCase("§7(§c!§7)§f Configuration")) {

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

     //               new FirstConfigurationInventory().open(player);
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
                final Configuration_Inventory config = new Configuration_Inventory();
                if (inv.getTitle().equals(config.getInventory().getTitle())) {
                    config.open(player);
                }
            }
        }
        player.updateInventory();
    }
    public void updateRoleInventory(Player player) {
        new Configuration_RolesInventory(player).open(player);
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
                    for (MDJ mdj : MDJ.values()) {
                        if (mdj != MDJ.Aucun && mdj != MDJ.KRYSTAL){
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
                if (inv.getTitle().equals("§7(§c!§7)§f Configuration§7 ->§6 Événements")) {
                    inv.clear();
                    PaginatedFastInv paginatedFastInv = new PaginatedFastInv(27, "§7(§c!§7)§f Configuration§7 ->§6 Événements");
                    paginatedFastInv.setItems(paginatedFastInv.getCorners(), new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
                    final List<Integer> list = new ArrayList<>();
                    for (int i = 10; i <= 16; i++) {
                        list.add(i);
                    }
                    paginatedFastInv.setContentSlots(list);
                    paginatedFastInv.setItem(4, GUIItems.getSelectBackMenu(), event -> {
                        event.getWhoClicked().openInventory(GUIItems.getAdminWatchGUI());
                        Main.getInstance().getInventories().updateAdminInventory(player);
                    });
                    paginatedFastInv.previousPageItem(3, new ItemBuilder(Material.WOOD_BUTTON)
                            .setName("§7◄ Page précédente").toItemStack());
                    paginatedFastInv.nextPageItem(5, new ItemBuilder(Material.WOOD_BUTTON)
                            .setName("§7Page suivante ►").toItemStack());
                    for (@NonNull final Event gameEvent : Main.getInstance().getEventsManager().getEventsList()) {
                        paginatedFastInv.addContent(gameEvent.getMenuItem(), event -> {
                            if (event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                                gameEvent.setPercent(Math.min(100, gameEvent.getPercent() + 1));
                            } else if (event.getAction().equals(InventoryAction.PICKUP_HALF)) {
                                gameEvent.setPercent(Math.max(1, gameEvent.getPercent()) - 1);
                            } else if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                                Main.getInstance().getEventsManager().openInv(player, gameEvent.getName(), gameEvent);
                            }
                            updateEventInventory(player);
                        });
                    }
                    paginatedFastInv.open(player);
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

        for (final Roles roles : Roles.values()) {
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