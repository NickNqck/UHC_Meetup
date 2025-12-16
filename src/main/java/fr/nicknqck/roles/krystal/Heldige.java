package fr.nicknqck.roles.krystal;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Heldige extends BonusKrystalBase {

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
    public @NonNull Roles getRoles() {
        return Roles.Heldige;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Solo;
    }

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
        setKrystalAmount(50);
        addBonus(new ForcePermaBonus(50, this));
    }
/*
    @Override
    public @NonNull Map<PotionEffect, Integer> getBonus() {
        final Map<PotionEffect, Integer> map = new HashMap<>();
        map.put(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), 50);
        return map;
    }*/

    private static class BattleSwapPower extends Power implements Listener {

        public BattleSwapPower(@NonNull RoleBase role) {
            super("Swap du combattant", null, role,
                    "§7Lorsque vous frappez un§c joueur",
                    "§7Vous aurez§c 10%§7 de§c chance§7 de lui faire changer l'objet",
                    "§7dans sa main par un objet§c aléatoire§7 de sa§c barre d'action");
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            return true;
        }
        @EventHandler
        private void UHCPlayerBattleEvent(final UHCPlayerBattleEvent event) {
            if (!event.getDamager().getUuid().equals(getRole().getPlayer()))return;
            if (!checkUse((Player) event.getOriginEvent().getDamager(), new HashMap<>()))return;
            if (!((Player)event.getOriginEvent().getDamager()).getItemInHand().getType().name().contains("SWORD"))return;
            if (!event.isPatch())return;
            if (Main.RANDOM.nextInt(100) > 10) return;//Une chance sur 20
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
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
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
            victim.teleport(loc1);
            owner.teleport(loc2);
            victim.sendMessage("§cVous avez échanger votre position avec§6 "+getRole().getName());
            owner.sendMessage("Vous avez échanger votre position avec§c "+victim.getName());
        }
    }
    private static class EnderSwapPower extends Power implements Listener {

        private final TeleportCommandPower teleportCommandPower;

        public EnderSwapPower(@NonNull RoleBase role) {
            super("Swap interdimensionel", null, role,
                    "§7Lorsque qu'un joueur utilise une§c ender perle§7 vous aurez§c 10 secondes§7 pour vous§c téléportez§7 à sa position");
            this.teleportCommandPower = new TeleportCommandPower(role);
            role.addPower(teleportCommandPower);
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            return true;
        }
        @EventHandler
        private void TeleportEvent(final PlayerTeleportEvent event) {
            if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) {
                final Player player = event.getPlayer();
                if (player.getUniqueId().equals(getRole().getPlayer()))return;
                if (!checkUse(player, new HashMap<>()))return;
                if (!player.getWorld().getName().equalsIgnoreCase("arena"))return;
                this.teleportCommandPower.tryStartWith(player);
            }
        }
        private static class TeleportCommandPower extends CommandPower {

            private final Map<UUID, Integer> targetMap;
            private final List<UUID> alreadyTeleported;

            public TeleportCommandPower(@NonNull RoleBase role) {
                super("Swap interdimensionel", "enderswap", new Cooldown(60*5), role, CommandType.CUSTOM);
                setShowInDesc(false);
                this.alreadyTeleported = new ArrayList<>();
                this.targetMap = new HashMap<>();
                setSendCooldown(false);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                final String[] args = (String[]) map.get("args");
                if (args.length == 2) {
                    final Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        return teleport(player, target);
                    } else {
                        player.sendMessage("§cLe joueur ne semble pas être connecté");
                    }
                }
                return false;
            }
            private void tryStartWith(@NonNull final Player target) {
                if (!this.targetMap.containsKey(target.getUniqueId())) {
                    this.targetMap.put(target.getUniqueId(), 10);
                    new TeleportLimitRunnable(this, target);
                    final TextComponent text = new TextComponent("§c"+target.getDisplayName()+"§7 s'est§c téléporter§7, voulez vous le§c suivre ?\n\n");
                    final TextComponent cliquezici = new TextComponent("§a§l[CLIQUEZ-ICI POUR VOUS TÉLÉPORTEZ A SA POSITION]");
                    cliquezici.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
                            new TextComponent(cliquezici.getText())
                    }));
                    cliquezici.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/c enderswap "+target.getName()));
                    text.addExtra(cliquezici);
                    final Player owner = Bukkit.getPlayer(getRole().getPlayer());
                    if (owner != null) {
                        owner.spigot().sendMessage(text);
                    }
                }
                
            }

            private boolean teleport(@NonNull final Player owner, @NonNull final Player target) {
                if (this.alreadyTeleported.contains(target.getUniqueId()) || !this.targetMap.containsKey(target.getUniqueId())) {
                    owner.sendMessage("§cVous ne pouvez plus vous téléportez à ce joueur");
                    return false;
                }
                owner.teleport(target);
                owner.sendMessage("§7Vous vous§c téléportez§7 sur§c "+target.getName());
                target.sendMessage("§eHeldige§7 c'est§c téléporter§7 sur§c vous");
                this.targetMap.remove(target.getUniqueId());
                this.targetMap.put(target.getUniqueId(), -1);
                this.alreadyTeleported.add(target.getUniqueId());
                return true;
            }
            private static class TeleportLimitRunnable extends BukkitRunnable {

                private final GameState gameState;
                private final UUID uuidTarget;
                private final String nameTarget;
                private final RoleBase role;
                private final TeleportCommandPower commandPower;

                private TeleportLimitRunnable(TeleportCommandPower commandPower, Player target) {
                    this.gameState = commandPower.getRole().getGameState();
                    this.uuidTarget = target.getUniqueId();
                    this.nameTarget = target.getName();
                    this.role = commandPower.getRole();
                    this.commandPower = commandPower;
                    this.role.getGamePlayer().getActionBarManager().addToActionBar(
                            "heldige.teleport."+uuidTarget,
                            "§fVous avez§c 10 secondes§f pour vous§c téléportez§f sur "+this.nameTarget
                    );
                    runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
                }

                @Override
                public void run() {
                    if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                        cancel();
                        return;
                    }
                    int i = this.commandPower.targetMap.get(this.uuidTarget);
                    if (i <= 0) {
                        if (!this.commandPower.alreadyTeleported.contains(this.uuidTarget)){
                            this.role.getGamePlayer().sendMessage("Tu peux plus te tp a§c "+this.nameTarget);
                            this.commandPower.targetMap.remove(this.uuidTarget);
                        }
                        this.role.getGamePlayer().getActionBarManager().removeInActionBar("heldige.teleport."+this.uuidTarget);
                        cancel();
                        return;
                    }
                    this.commandPower.targetMap.remove(this.uuidTarget, i);
                    i--;
                    this.commandPower.targetMap.put(this.uuidTarget, i);
                    this.role.getGamePlayer().getActionBarManager().updateActionBar(
                            "heldige.teleport."+uuidTarget,
                            "§fVous avez encore§c "+i+" secondes§f pour vous§c téléportez§f sur§b "+this.nameTarget
                    );

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
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            final String[] args = (String[]) map.get("args");
            if (args.length == 1) {
                final Inventory inv = Bukkit.createInventory(player, 27, "§fConfiguration de pouvoir");
                inv.setItem(11, new ItemBuilder(Material.IRON_SWORD)
                        .setName("§b"+this.heldige.battleSwapPower.getName())
                        .setLore(this.heldige.getPowers().contains(this.heldige.battleSwapPower) ? "§aActivé" : "§cDésactivé")
                        .toItemStack());
                inv.setItem(13, new ItemBuilder(Material.ENDER_PEARL)
                        .setName("§b"+this.heldige.enderSwapPower.getName())
                        .setLore(this.heldige.getPowers().contains(this.heldige.enderSwapPower) ? "§aActivé" : "§cDésactivé")
                        .toItemStack());
                inv.setItem(15, new ItemBuilder(Material.BOW)
                        .setName("§b"+this.heldige.bowSwapPower.getName())
                        .setLore(this.heldige.getPowers().contains(this.heldige.bowSwapPower) ? "§aActivé" : "§cDésactivé")
                        .toItemStack());
                player.openInventory(inv);
                EventUtils.registerEvents(this);
                return true;
            }
            return false;
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
