package fr.nicknqck.roles.krystal;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.custom.CustomRolesBase;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Heldige extends KrystalBase {

    private BattleSwapPower battleSwapPower;
    private BowSwapPower bowSwapPower;
    private EnderSwapPower enderSwapPower;

    public Heldige(UUID player) {
        super(player);
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Heldige";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Heldige;
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Solo;
    }

    @Override
    public void resetCooldown() {}

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        this.enderSwapPower = new EnderSwapPower(this);
        this.battleSwapPower = new BattleSwapPower(this);
        this.bowSwapPower = new BowSwapPower(this);
        addPower(this.battleSwapPower);
        addPower(this.enderSwapPower);
        addPower(this.bowSwapPower);
        addPower(new CustomiseSwapPower(this));
    }
    private static class BattleSwapPower extends Power implements Listener {

        public BattleSwapPower(@NonNull RoleBase role) {
            super("Swap du combattant", null, role,
                    "§7Lorsque vous frappez un§c joueur",
                    "§7Vous aurez§c 5%§7 de§c chance§7 de lui faire changer l'objet",
                    "§7dans sa main par un objet§c aléatoire§7 de sa§c barre d'action");
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> map) {
            return true;
        }
        @EventHandler
        private void UHCPlayerBattleEvent(final UHCPlayerBattleEvent event) {
            if (!event.getDamager().getUuid().equals(getRole().getPlayer()))return;
            if (!checkUse((Player) event.getOriginEvent().getDamager(), new HashMap<>()))return;
            if (!((Player)event.getOriginEvent().getDamager()).getItemInHand().getType().name().contains("SWORD"))return;
            if (!event.isPatch())return;
            if (Main.RANDOM.nextInt(20) != 1) return;//Une chance sur 20
            final int random = Main.RANDOM.nextInt(9);//On choisit un slot random
            final Player victim = (Player) event.getOriginEvent().getEntity();
            for (int i = 0; i <= 8; i++) {
                final ItemStack stack = victim.getInventory().getItem(i);
                if (stack == null)continue;
                if (stack.getType().equals(Material.AIR))continue;
                if (i == random) {//si on est sur le bon slot
                    final ItemStack oldStack = victim.getItemInHand();
                    victim.setItemInHand(stack);
                    victim.getInventory().setItem(i, oldStack);
                    break;
                }
            }
        }
    }
    private static class BowSwapPower extends Power implements Listener {

        public BowSwapPower(@NonNull RoleBase role) {
            super("Swap d'archer", null, role,
                    "§7Lorsque que vous tirez sur une§a entité§7 ou que vous vous faite tirez dessus,",
                    "§7Vous aurez§c 25%§7 de§c chance§7 d'échanger votre position avec la§c cible");
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> map) {
            return true;
        }
        @EventHandler
        private void EntityDamageByEntityEvent(final EntityDamageByEntityEvent event) {
            if (event.getDamager() instanceof Arrow) {
                final Arrow arrow = (Arrow) event.getDamager();
                if (arrow.getShooter() instanceof Player) {
                    final Player shooter = (Player) arrow.getShooter();
                    final int random = Main.RANDOM.nextInt(101);
                    if (shooter.getUniqueId().equals(getRole().getPlayer())) {
                        if (random <= 25) {
                            swap(shooter, event.getEntity());
                        }
                    } else if (event.getEntity().getUniqueId().equals(getRole().getPlayer()) && event.getEntity() instanceof Player) {
                        if (random <= 25){
                            swap((Player) event.getEntity(), shooter);
                        }
                    }
                }
            }
        }
        private void swap(final Player owner, final Entity victim) {
            if (!checkUse(owner, new HashMap<>()))return;
            final Location loc1 = owner.getLocation();
            final Location loc2 = victim.getLocation();
            victim.teleport(loc2);
            owner.teleport(loc1);
            victim.sendMessage("§cVous avez échanger votre position avec§6 "+getRole().getName());
            owner.sendMessage("Vous avez échanger votre position avec§c "+victim.getName());
        }
    }
    private static class EnderSwapPower extends Power implements Listener {

        public EnderSwapPower(@NonNull RoleBase role) {
            super("Swap interdimensionel", null, role,
                    "§7Lorsque vous lancez une§c perle de l'ender",
                    "§7Vous aurez§c 25%§7 de§c chance§7 de téléporter un autre§c joueur§7 avec vous");
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> map) {
            return true;
        }
        @EventHandler
        private void TeleportEvent(final PlayerTeleportEvent event) {
            if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) {
                if (event.getPlayer().getUniqueId().equals(getRole().getPlayer())) {
                    final int random = Main.RANDOM.nextInt(101);
                    if (random <= 25) {
                        if (!checkUse(event.getPlayer(), new HashMap<>()))return;
                        final Location to = event.getTo();
                        GamePlayer tp = getRole().getGamePlayer();
                        Location oldTpLoc = event.getPlayer().getLocation();
                        for (final GamePlayer gamePlayer : Loc.getNearbyGamePlayers(to, 100)) {
                            if (!gamePlayer.isAlive())continue;
                            if (gamePlayer.getDiscRunnable() != null){
                                if (!gamePlayer.getDiscRunnable().isOnline())continue;
                            }
                            if (to.distance(gamePlayer.getLastLocation()) < tp.getLastLocation().distance(to)) {
                                tp = gamePlayer;
                                oldTpLoc = gamePlayer.getLastLocation();
                            }
                        }
                        final Player player = Bukkit.getPlayer(tp.getUuid());
                        if (player != null) {
                            player.teleport(to, PlayerTeleportEvent.TeleportCause.PLUGIN);
                            event.getPlayer().teleport(oldTpLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);
                        }
                    }
                }
            }
        }
    }
    private static class CustomiseSwapPower extends CommandPower implements Listener {

        private final Heldige heldige;

        public CustomiseSwapPower(@NonNull Heldige role) {
            super("/c config", "config", null, role, CommandType.CUSTOM,
                    "§7Vous permet d'§aActivé§7/§cDésactivé§7 vos pouvoir \"Swap\"",
                    "",
                    "§7(par défaut les pouvoirs sont§a activé§7)");
            this.heldige = role;
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> map) {
            final Inventory inv = Bukkit.createInventory(player, 27, "§fConfiguration de pouvoir");
            inv.setItem(11, new ItemBuilder(Material.IRON_SWORD)
                    .setName(this.heldige.battleSwapPower.getName())
                    .setLore(this.heldige.getPowers().contains(this.heldige.battleSwapPower) ? "§aActivé" : "§cDésactivé")
                    .toItemStack());
            inv.setItem(13, new ItemBuilder(Material.ENDER_PEARL)
                    .setName(this.heldige.enderSwapPower.getName())
                    .setLore(this.heldige.getPowers().contains(this.heldige.enderSwapPower) ? "§aActivé" : "§cDésactivé")
                    .toItemStack());
            inv.setItem(15, new ItemBuilder(Material.BOW)
                    .setName(this.heldige.bowSwapPower.getName())
                    .setLore(this.heldige.getPowers().contains(this.heldige.bowSwapPower) ? "§aActivé" : "§cDésactivé")
                    .toItemStack());
            player.openInventory(inv);
            EventUtils.registerEvents(this);
            return true;
        }
        @EventHandler
        private void onInventoryClose(final InventoryCloseEvent event) {
            if (event.getInventory().getTitle() != null && event.getInventory().getTitle().equals("§fConfiguration de pouvoir")) {
                if (!event.getPlayer().getUniqueId().equals(getRole().getPlayer()))return;
                EventUtils.unregisterEvents(this);
            }
        }
        @EventHandler
        private void onInventoryClick(final InventoryClickEvent event) {
            if (event.getInventory().getTitle() != null && event.getInventory().getTitle().equals("§fConfiguration de pouvoir")) {
                if (event.getCurrentItem() == null)return;
                if (event.getCurrentItem().getType().equals(Material.AIR))return;
                final ItemStack item = event.getCurrentItem();
                if (item.getType().equals(Material.IRON_SWORD)) {
                    if (this.heldige.getPowers().contains(this.heldige.battleSwapPower)) {
                        removePower(this.heldige.battleSwapPower);
                    } else {
                        recupPower(this.heldige.battleSwapPower);
                    }
                }
                if (item.getType().equals(Material.BOW)) {
                    if (this.heldige.getPowers().contains(this.heldige.bowSwapPower)) {
                        removePower(this.heldige.bowSwapPower);
                    } else {
                        recupPower(this.heldige.bowSwapPower);
                    }
                }
                if (item.getType().equals(Material.ENDER_PEARL)) {
                    if (this.heldige.getPowers().contains(this.heldige.enderSwapPower)) {
                        removePower(this.heldige.enderSwapPower);
                    } else {
                        recupPower(this.heldige.enderSwapPower);
                    }
                }
                event.setCancelled(true);
                event.getWhoClicked().closeInventory();
            }
        }
        private void removePower(final Power power) {
            getRole().getGamePlayer().sendMessage("§cVous avez perdu le pouvoir \"§b"+power.getName()+"§c\"");
            this.heldige.getPowers().remove(power);
        }
        private void recupPower(final Power power) {
            getRole().getGamePlayer().sendMessage("§aVous avez récupérer le pouvoir \"§b"+power.getName()+"§a\"");
            this.heldige.addPower(power);
        }
    }
}
