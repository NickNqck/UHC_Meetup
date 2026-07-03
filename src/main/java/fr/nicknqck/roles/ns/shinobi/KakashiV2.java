package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.InfoType;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.power.PowerTakeInfoEvent;
import fr.nicknqck.interfaces.IRoleGotSubWorld;
import fr.nicknqck.interfaces.ISubRoleWorld;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.enums.EChakras;
import fr.nicknqck.enums.Intelligence;
import fr.nicknqck.roles.ns.builders.HShinobiRoles;
import fr.nicknqck.roles.ns.power.KamuiPower;
import fr.nicknqck.roles.ns.power.YameruPower;
import fr.nicknqck.utils.GlobalUtils;
import fr.nicknqck.utils.KamuiDimension;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.fastinv.FastInv;
import fr.nicknqck.utils.fastinv.InventoryScheme;
import fr.nicknqck.utils.fastinv.PaginatedFastInv;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.util.*;

public class KakashiV2 extends HShinobiRoles implements IRoleGotSubWorld {

    private KamuiPower kamuiPower;

    public KakashiV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.GENIE;
    }

    @Override
    public EChakras[] getChakrasCanHave() {
        return new EChakras[] {
                EChakras.RAITON
        };
    }

    @Override
    public String getName() {
        return "Kakashi";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Kakashi;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        this.kamuiPower = new KamuiPower(this);
        addPower(this.kamuiPower, true);
        addPower(new SharinganPower(this), true);
        addPower(new YameruPower(this));
    }

    @Nonnull
    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

    @Override
    public ISubRoleWorld getSubWorld() {
        if (this.kamuiPower == null) {
            //Je peux faire ceci, car normalement, ce sera utiliser juste pour le menu donc c'est OK
            return new KamuiDimension();
        }
        return this.kamuiPower.getKamuiDimension();
    }

    private static class SharinganPower extends ItemPower {

        private static final InventoryScheme SCHEME = new InventoryScheme()
                .mask(" 1111111 ")
                .mask(" 1111111 ")
                .bindPagination('1');
        private final HashMap<String, List<PotionEffectType>> Copied = new HashMap<>();
        private boolean coping = false;
        private int actualPoint = 0;
        private Player targetOfCopy = null;

        public SharinganPower(@NonNull RoleBase role) {
            super("§cSharingan§r", null, new ItemBuilder(Material.NETHER_STAR).setName("§cSharingan"), role,
                    "§7Ouvre un menu ayant plusieurs choix de pouvoir: ",
                    "§7     →§a Copie§7: Ouvre un menu permettant de sélectionner un joueur,",
                    "§7en le sélectionnant cela crée une§c bar§7 de§a 1600 points§7,",
                    "§7vous augmentez ces dernier en étant plus ou moins proche de la cible:",
                    "",
                    "§8 -§f 5 blocs:§a + 20 points",
                    "§8 -§f 10 blocs:§6 + 10 points",
                    "§8 -§f 20 blocs:§c + 5 points",
                    "",
                    "§7Une fois les§a 1600§7 points atteint vous obtenez les mêmes effets que la cible (seulement les effets permanent)",
                    "",
                    "§7     →§a Technique§7: Ouvre un menu ayant à l'intérieur la liste des joueurs a qui vous avez copié les effets,",
                    "§7en cliquant sur la tête d'un de ces joueurs",
                    "§7vous obtiendrez a nouveau les effets permanent de la cible (vous retire vos effet actuel).",
                    "");
            new SharinganRunnable(this).runTaskTimerAsynchronously(getPlugin(), 100, 20);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            openInv(player);
            return true;
        }
        private void openInv(@NonNull final Player player) {
            @NonNull final FastInv fastInv = new FastInv(27, "§cSharingan");
            fastInv.setItems(fastInv.getCorners(), new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").setDurability(7).toItemStack());
            fastInv.setItem(12, new ItemBuilder(Material.PAPER).setName("§aTechnique").toItemStack(),
                    event -> {
                if (event.getWhoClicked() instanceof Player) {
                    openTechniqueInventory((Player) event.getWhoClicked());
                }
                    });
            if (!coping) {
                fastInv.setItem(14, new ItemBuilder(Material.EYE_OF_ENDER)
                        .setName("§aCopie")
                        .toItemStack(), event -> {
                    @NonNull final FastInv inv = new FastInv(54, "§cSharingan§7 ->§a Copie");
                    inv.setItems(inv.getBorders(), new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
                    for (@NonNull final GamePlayer gamePlayer : Loc.getNearbyGamePlayers(event.getWhoClicked().getLocation(), 30)) {
                        if (gamePlayer.getRole() == null)continue;
                        if (!gamePlayer.isAlive())continue;
                        if (!gamePlayer.isOnline())continue;
                        inv.addItem(new ItemBuilder(GlobalUtils.getPlayerHead(gamePlayer.getUuid())).setName("§a"+gamePlayer.getPlayerName()).toItemStack(),
                                event1 -> {
                            String name = event1.getCurrentItem().getItemMeta().getDisplayName().substring(0, event1.getCurrentItem().getItemMeta().getDisplayName().length()-2);
                            final Player target = Bukkit.getPlayer(name);
                            if (target != null){
                                coping = true;
                                targetOfCopy = target;
                                actualPoint = 0;
                            }
                            event1.getWhoClicked().closeInventory();
                                });
                    }
                });
            } else {
                fastInv.setItem(14, new ItemBuilder(Material.BARRIER)
                        .setName("§cAnnuler")
                        .setLore(targetOfCopy == null ? "§cLa cible n'est pas connecter" : "§cLa cible est§4 "+targetOfCopy.getName()+"§c.")
                        .toItemStack());
            }
            fastInv.open(player);
        }
        private void openTechniqueInventory(@NonNull final Player player) {
            @NonNull final PaginatedFastInv paginatedFastInv = new PaginatedFastInv(27, "§cSharingan§7 ->§a Technique");
            paginatedFastInv.setItems(paginatedFastInv.getCorners(), new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").setDurability(7).toItemStack());
            paginatedFastInv.previousPageItem(20, p -> new ItemBuilder(Material.ARROW).setName("Page "+p+"/"+paginatedFastInv.lastPage()).toItemStack());
            paginatedFastInv.nextPageItem(24, p -> new ItemBuilder(Material.ARROW).setName("Page "+p+"/"+paginatedFastInv.lastPage()).toItemStack());
            for (String string : Copied.keySet()) {
                paginatedFastInv.addContent(new ItemBuilder(Material.PAPER).setName(string).toItemStack());
            }
            paginatedFastInv.open(player);
            SCHEME.apply(paginatedFastInv);
        }
        private static class SharinganRunnable extends BukkitRunnable {

            private final SharinganPower sharinganPower;

            private SharinganRunnable(SharinganPower sharinganPower) {
                this.sharinganPower = sharinganPower;
            }

            @Override
            public void run() {
                if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (!sharinganPower.getRole().getGamePlayer().isAlive() || !sharinganPower.getRole().getGamePlayer().isOnline() || this.sharinganPower.targetOfCopy == null) {
                    sharinganPower.getRole().getGamePlayer().getActionBarManager().removeInActionBar("kakashi.copy");
                    return;
                }
                final Player owner = Bukkit.getPlayer(sharinganPower.getRole().getPlayer());
                if (owner == null)return;
                if (this.sharinganPower.actualPoint >= 1600) {
                    this.sharinganPower.coping = false;
                    PowerTakeInfoEvent event = new PowerTakeInfoEvent(this.sharinganPower, GamePlayer.of(this.sharinganPower.targetOfCopy.getUniqueId()), InfoType.EFFETS);
                    this.sharinganPower.getPlugin().getServer().getPluginManager().callEvent(event);
                    if (event.isCancelled()) {
                        event.sendCancelMessage(owner);
                        return;
                    }
                    this.sharinganPower.Copied.put(this.sharinganPower.targetOfCopy.getName(), event.getGameTarget().getRole().getPermanentPotionEffects(event.getGameTarget().getPlayer()));
                    this.sharinganPower.getRole().getGamePlayer().sendMessage("§7La copie est terminer.");
                    this.sharinganPower.targetOfCopy = null;
                    return;
                }
                sharinganPower.getRole().getGamePlayer().getActionBarManager().updateActionBar("kakashi.copy", "§aPoints:§c "+this.sharinganPower.actualPoint+"/§6"+1600);
                if (Loc.getNearbyPlayersExcept(owner, 5).contains(this.sharinganPower.targetOfCopy)) {
                    this.sharinganPower.actualPoint+=20;
                } else if (Loc.getNearbyPlayersExcept(owner, 10).contains(this.sharinganPower.targetOfCopy)) {
                    this.sharinganPower.actualPoint+=10;
                } else if (Loc.getNearbyPlayersExcept(owner, 20).contains(this.sharinganPower.targetOfCopy)) {
                    this.sharinganPower.actualPoint+=5;
                } else if (Loc.getNearbyPlayersExcept(owner, 40).contains(this.sharinganPower.targetOfCopy)) {
                    this.sharinganPower.actualPoint+=1;
                }
            }
        }
    }
}