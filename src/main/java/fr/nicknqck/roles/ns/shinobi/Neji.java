package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.NSRoles;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
import fr.nicknqck.utils.ArrowTargetUtils;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Neji extends ShinobiRoles {

    public Neji(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.MOYENNE;
    }

    @Override
    public String getName() {
        return "Neji";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Neji;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addKnowedRole(Hinata.class);
        addPower(new Byakugan(this), true);
        addPower(new ByakuganCommand(this));
        addPower(new ChakraCommand(this));
        new ForceRunnable(getGameState(), this);
        setChakraType(getRandomChakrasBetween(Chakras.DOTON, Chakras.RAITON, Chakras.KATON));
        super.RoleGiven(gameState);
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .getText();
    }
    private static class ForceRunnable extends BukkitRunnable {

        final GameState gameState;
        final Neji neji;
        int amountAbsent = 0;

        private ForceRunnable(GameState gameState, Neji neji) {
            this.gameState = gameState;
            this.neji = neji;
            runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
        }

        @Override
        public void run() {
            if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (!neji.getGamePlayer().isAlive())return;
            if (this.amountAbsent > 10) {
                cancel();
                return;
            }
            final List<Player> players = neji.getListPlayerFromRole(Hinata.class);
            if (players.isEmpty()) {
                this.amountAbsent++;
                return;
            }
            for (final Player aroundPlayer : Loc.getNearbyPlayers(this.neji.getGamePlayer().getLastLocation(), 15)) {
                if (players.contains(aroundPlayer)) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> neji.givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.NOW));
                    break;
                }
            }
        }
    }
    private static class Byakugan extends ItemPower implements Listener {

        public Byakugan(@NonNull RoleBase role) {
            super("Byakugan", new Cooldown(60*5), new ItemBuilder(Material.SNOW_BALL).setName("§fByakugan"), role,
                    "§7Vous permet d'obtenir la liste des joueurs présent autours de vous dans un rayon de§c 100 blocs§7,",
                    "",
                    "§7Si vous cliquez sur l'un des pseudos afficher dans le chat vous effectuerez la commande§6 /ns byakugan§7 sur ce dernier");
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (!getInteractType().equals(InteractType.INTERACT))return false;
            final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
            event.setCancelled(true);
            TextComponent textComponent = new TextComponent("§7Voici la liste des joueurs étant à moins de§c 100 blocs§7:\n\n");
            for (@NonNull final Player target : Loc.getNearbyPlayers(player.getLocation(), 100.0)) {
                TextComponent text = new TextComponent("§8 - §a"+target.getName());
                text.setHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new BaseComponent[]{
                                new TextComponent("§c§lCLIQUEZ ICI POUR TRAQUEZ §a"+target.getName()+"§7 (§6/ns byakugan <joueur>§7)")
                        }
                ));
                text.setClickEvent(new ClickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        "/ns byakugan "+target.getName()
                ));
                textComponent.addExtra(text);
                textComponent.addExtra("\n");
            }
            player.spigot().sendMessage(textComponent);
            return true;
        }
        @EventHandler(priority = EventPriority.HIGHEST)
        private void onLaunch(ProjectileLaunchEvent event) {
            if (event.getEntity() instanceof Snowball) {
                if (event.getEntity().getShooter() instanceof Player){
                    if (((Player) event.getEntity().getShooter()).getUniqueId().equals(getRole().getPlayer())){
                        ((Player) event.getEntity().getShooter()).getInventory().addItem(getItem());
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
    private static class ByakuganCommand extends CommandPower {

        public ByakuganCommand(@NonNull RoleBase role) {
            super("/ns byakugan <joueur>", "byakugan", new Cooldown(60*10), role, CommandType.NS,
                    "§7Vous permet de traquer un joueur pendant§c 60 secondes§7.");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            final String[] args = (String[]) map.get("args");
            if (args.length == 2) {
                final Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    if (getRole().getGameState().hasRoleNull(target.getUniqueId())) {
                        player.sendMessage("§cImpossible de traquez§b "+target.getName());
                        return false;
                    }
                    player.sendMessage("§7Vous commencez a traquer§a "+target.getName());
                    new ByakuganCommand.TraqueRunnable(this.getRole().getGameState().getGamePlayer().get(target.getUniqueId()), getRole().getGamePlayer());
                    return true;
                }
            }
            return false;
        }
        private static class TraqueRunnable extends BukkitRunnable {

            private final GamePlayer gameTarget;
            private final GamePlayer gamePlayer;
            private int timeLeft = 60*20;

            private TraqueRunnable(GamePlayer gameTarget, GamePlayer gamePlayer) {
                this.gameTarget = gameTarget;
                this.gamePlayer = gamePlayer;
                this.gamePlayer.getActionBarManager().addToActionBar("byakugan.traque", "§bTraqueur:§c "+ ArrowTargetUtils.calculateArrow(gamePlayer, this.gameTarget.getLastLocation()));
                runTaskTimerAsynchronously(Main.getInstance() ,0, 1);
            }

            @Override
            public void run() {
                if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                this.timeLeft--;
                this.gamePlayer.getActionBarManager().updateActionBar("byakugan.traque","§bTraqueur:§c "+ArrowTargetUtils.calculateArrow(this.gamePlayer, this.gameTarget.getLastLocation()));
                if (this.timeLeft <= 0 || !this.gameTarget.isAlive()) {
                    this.gamePlayer.sendMessage("§7La traque se termine...");
                    this.gamePlayer.getActionBarManager().removeInActionBar("byakugan.traque");
                    cancel();
                }
            }
        }
    }
    private static class ChakraCommand extends CommandPower {

        public ChakraCommand(@NonNull RoleBase role) {
            super("/ns chakra <chakra> <joueur>", "chakra", new Cooldown(60*5), role, CommandType.NS);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            final String[] args = (String[]) map.get("args");
            if (args.length == 3) {
                Chakras chakras = null;
                for (final Chakras chakra : Chakras.values()) {
                    if (args[1].equalsIgnoreCase(chakra.name())) {
                        chakras = chakra;
                        break;
                    }
                }
                if (chakras != null) {
                    final Player target = Bukkit.getPlayer(args[2]);
                    if (target != null) {
                        if (!Loc.getNearbyPlayers(player, 5).contains(target)) {
                            player.sendMessage("§cLe joueur que vous avez visé est trop loin.");
                            return false;
                        }
                        if (!getRole().getGameState().hasRoleNull(target.getUniqueId())) {
                            final RoleBase role = getRole().getGameState().getGamePlayer().get(target.getUniqueId()).getRole();
                            if (role instanceof NSRoles) {
                                if (((NSRoles) role).getChakras() != null) {
                                    if (((NSRoles) role).getChakras().equals(chakras)) {
                                        player.sendMessage("§c"+target.getDisplayName()+"§7 possède§c "+role.getPowers().size()+" pouvoirs");
                                    } else {
                                        player.sendMessage("§7Il semblerait que vous vous soyez tromper de§a nature de chakra§7.");
                                    }
                                    return true;
                                } else {
                                    player.sendMessage("§7Impossible de savoir,§c "+target.getDisplayName()+"§7 ne possède pas de nature de chakra.");
                                }
                            } else {
                                player.sendMessage("§7Impossible de savoir,§c "+target.getDisplayName()+"§7 ne possède pas de nature de chakra.");
                            }
                        } else {
                            player.sendMessage("§7Impossible de savoir,§c "+target.getDisplayName()+"§7 ne possède pas de nature de chakra.");
                        }
                    } else {
                        player.sendMessage("§b"+args[2]+"§c n'est pas connecté");
                    }
                } else {
                    player.sendMessage(args[1]+"§c n'est pas une nature de chakra");
                }
            }
            return false;
        }

        @Override
        public List<String> getCompletor(String[] args) {
            final List<String> list = new ArrayList<>();
            if (args.length > 2) {
                return super.getCompletor(args);
            }
            for (final Chakras chakras : Chakras.values()) {
                list.add(chakras.name().toLowerCase());
            }
            return list;
        }
    }
}