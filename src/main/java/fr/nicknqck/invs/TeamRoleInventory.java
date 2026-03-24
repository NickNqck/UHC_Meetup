package fr.nicknqck.invs;

import fr.nicknqck.GameState;
import fr.nicknqck.HubListener;
import fr.nicknqck.Main;
import fr.nicknqck.enums.TeamList;
import fr.nicknqck.events.essential.inventorys.EasyRoleAdder;
import fr.nicknqck.interfaces.IRole;
import fr.nicknqck.interfaces.RoleCustomLore;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.builders.NSRoles;
import fr.nicknqck.utils.fastinv.PaginatedFastInv;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public abstract class TeamRoleInventory extends PaginatedFastInv {

    // ── Constantes de layout ─────────────────────────────────────────────────
    private static final int BACK_SLOT  = 4;
    private static final int START_SLOT = 6;
    private static final int PREV_SLOT  = 47;
    private static final int NEXT_SLOT  = 51;

    /** Rangées 1→4, colonnes 1→7 = 28 slots de contenu par page */
    private static final List<Integer> CONTENT_SLOTS;
    static {
        CONTENT_SLOTS = new ArrayList<>();
        for (int row = 1; row <= 4; row++)
            for (int col = 1; col <= 7; col++)
                CONTENT_SLOTS.add(row * 9 + col);
    }

    // ── Registre statique des inventaires ouverts par team ───────────────────
    private static final Map<TeamList, List<TeamRoleInventory>> OPEN_INVENTORIES = new EnumMap<>(TeamList.class);

    // ── Champs d'instance ────────────────────────────────────────────────────
    @Getter
    private final TeamList team;
    private final GameState gameState;

    // ── Constructeur ─────────────────────────────────────────────────────────

    public TeamRoleInventory(String title, TeamList team, String mdj) {
        super(54, title);

        this.team      = team;
        this.gameState = GameState.getInstance();

        // ── 1. Bordure complète en air ───────────────────────────────────────
        final ItemStack air = new ItemStack(Material.AIR);
        for (int slot : getBorders()) setItem(slot, air);

        // ── 2. Coins → vitre colorée du camp ────────────────────────────────
        final ItemStack glass = buildTeamGlass(team);
        for (int slot : getCorners()) setItem(slot, glass);

        // ── 3. Bouton retour ─────────────────────────────────────────────────
        setItem(BACK_SLOT, GUIItems.getSelectBackMenu(), e -> {
            final Player player = (Player) e.getWhoClicked();
            player.closeInventory();
            onBackClick(player);
        });

        // ── 4. Bouton démarrage de partie ────────────────────────────────────
        refreshStartButton();

        // ── 5. Slots de contenu paginé ───────────────────────────────────────
        setContentSlots(new ArrayList<>(CONTENT_SLOTS));

        // ── 6. Boutons de pagination (boutons en bois) ───────────────────────
        previousPageItem(PREV_SLOT, new ItemBuilder(Material.WOOD_BUTTON)
                .setName("§7◄ Page précédente").toItemStack());
        nextPageItem(NEXT_SLOT, new ItemBuilder(Material.WOOD_BUTTON)
                .setName("§7Page suivante ►").toItemStack());

        // ── 7. Chargement initial des rôles ──────────────────────────────────
        loadRoles(mdj);

        addSomeItem(this);
        // ── 8. Enregistrement / désenregistrement automatique ────────────────
        addOpenHandler(e -> register(this));
        addCloseHandler(e -> unregister(this));
    }

    // ── Méthodes abstraites ──────────────────────────────────────────────────

    protected abstract void onBackClick(Player player);

    protected void addSomeItem(@NonNull final TeamRoleInventory teamRoleInventory) {}

    // ── Refresh ──────────────────────────────────────────────────────────────

    public void refresh(String mdj) {
        clearContent();
        loadRoles(mdj);
        refreshStartButton();
        refreshCurrentPage();
        addSomeItem(this);
    }

    public static void refreshAll(TeamList team, String mdj) {
        final List<TeamRoleInventory> openList = OPEN_INVENTORIES.get(team);
        if (openList == null) return;
        for (final TeamRoleInventory inv : openList) {
            inv.refresh(mdj);
        }
    }

    // ── Chargement interne des rôles ─────────────────────────────────────────

    private void loadRoles(String mdj) {
        for (final IRole iRole : getRolesByTeam(team)) {
            if (!iRole.getRoles().getMdj().equalsIgnoreCase(mdj)) continue;

            final Integer available = gameState.getAvailableRoles().get(iRole.getRoles());
            final int count         = (available != null) ? available : 0;

            final String l1     = count > 0 ? "§c(" + count + ")" : "§c(0)";
            final String design = "§fGDesign: " + iRole.getRoles().getGDesign();

            final ItemStack roleItem = new ItemBuilder(iRole.getRoles().getItem())
                    // Si count == 0 → amount 0 (affiche "0" dans le GUI),
                    // sinon → le nombre réel de slots disponibles
                    .setAmount(count)
                    .setLore(iRole instanceof RoleCustomLore
                            ? ((RoleCustomLore) iRole).getCustomLore(l1, design)
                            : iRole instanceof NSRoles ?
                            new String[] { getChakraLine((NSRoles) iRole), l1, "", design}
                            :
                            new String[]{ l1, "", design })
                    .toItemStack();

            addContent(roleItem, e -> {
                final Player player = (Player) e.getWhoClicked();
                final ItemStack clicked = e.getCurrentItem();

                if (clicked == null) { refresh(mdj); return; }
                if (clicked.isSimilar(GUIItems.getSelectBackMenu())) { refresh(mdj); return; }

                if (clicked.hasItemMeta() && clicked.getItemMeta().hasDisplayName()) {
                    if (clicked.isSimilar(GUIItems.getStartGameButton()) && GameState.getInstance().gameCanLaunch) {
                        HubListener.getInstance().StartGame(player);
                        return;
                    }
                    final String name = clicked.getItemMeta().getDisplayName();
                    if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {
                        EasyRoleAdder.addRoles(name);
                    } else if (e.getAction().equals(InventoryAction.PICKUP_HALF)) {
                        EasyRoleAdder.removeRoles(name);
                    }
                }
                refreshAll(team, mdj);
            });
        }
    }

    private String getChakraLine(final NSRoles roles) {
        StringBuilder sb = new StringBuilder();
        for (@NonNull final Chakras chakras : roles.getChakrasCanHave()) {
            sb.append(chakras.getShowedName()).append("§7, ");
        }
        return sb.substring(0, sb.length()-4);
    }

    private void refreshStartButton() {
        if (gameState.gameCanLaunch) {
            setItem(START_SLOT, GUIItems.getStartGameButton(), e -> {
                if (gameState.gameCanLaunch)
                    HubListener.getInstance().StartGame((Player) e.getWhoClicked());
            });
        } else {
            setItem(START_SLOT, GUIItems.getCantStartGameButton());
        }
    }

    // ── Registre ─────────────────────────────────────────────────────────────

    private static void register(TeamRoleInventory inv) {
        OPEN_INVENTORIES
                .computeIfAbsent(inv.team, k -> new CopyOnWriteArrayList<>())
                .add(inv);
    }

    private static void unregister(TeamRoleInventory inv) {
        final List<TeamRoleInventory> list = OPEN_INVENTORIES.get(inv.team);
        if (list != null) list.remove(inv);
    }

    // ── getRolesByTeam ────────────────────────────────────────────────────────

    private static List<IRole> getRolesByTeam(TeamList team) {
        return Main.getInstance().getRoleManager().getRolesRegistery().values().stream()
                .filter(iRole -> iRole.getRoles().getTeam() == team)
                .sorted(Comparator.comparingInt(iRole -> iRole.getRoles().getNmb()))
                .collect(Collectors.toList());
    }

    // ── Vitre colorée ─────────────────────────────────────────────────────────

    private static ItemStack buildTeamGlass(TeamList team) {
        final ItemStack pane = new ItemStack(Material.STAINED_GLASS_PANE, 1, teamColorToGlassData(team));
        final ItemMeta meta = pane.getItemMeta();
        meta.setDisplayName(" ");
        pane.setItemMeta(meta);
        return pane;
    }

    private static short teamColorToGlassData(TeamList team) {
        switch (team) {
            case Demon:
            case Titan:
            case Akatsuki:
                return 14;
            case Slayer:
            case Soldat:
            case Shinobi:
                return 5;
            case Solo:
            case Sasuke:
            case Shisui:
                return 4;
            case Jigoro:
            case Alliance:
            case Kabuto:
            case Kumogakure:
                return 1;
            case Mahr:           return 11;
            case Jubi:           return 2;
            case Orochimaru:     return 10;
            case Zabuza_et_Haku: return 3;
            default:             return 7;
        }
    }
}