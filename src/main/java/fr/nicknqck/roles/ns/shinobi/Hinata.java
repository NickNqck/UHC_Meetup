package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.power.CooldownFinishEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.enums.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.enums.EChakras;
import fr.nicknqck.enums.Intelligence;
import fr.nicknqck.roles.ns.builders.*;
import fr.nicknqck.utils.ArrowTargetUtils;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
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
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Hinata extends HShinobiRoles implements IByakuganUser {

    public Hinata(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.MOYENNE;
    }

    @Override
    public EChakras[] getChakrasCanHave() {
        return new EChakras[] {
                EChakras.KATON,
                EChakras.RAITON
        };
    }

    @Override
    public String getName() {
        return "Hinata";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Hinata;
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .addCustomLine("§7Vous possédez l'effet§8 Weakness I§7 proche de§a Naruto")
                .getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new Byakugan(this), true);
        addPower(new ByakuganCommand(this));
        addPower(new TenketsuPower(this));
        addPower(new PowersInfo(this));
        new WeaknessRunnable(this);
        setCanBeHokage(true);
        super.RoleGiven(gameState);
    }

    @Override
    public @NonNull EByakuganUserType getUserType() {
        return EByakuganUserType.HINATA;
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
                TextComponent text = getText(target);
                textComponent.addExtra(text);
                textComponent.addExtra("\n");
            }
            player.spigot().sendMessage(textComponent);
            return true;
        }

        @Nonnull
        private static TextComponent getText(@Nonnull Player target) {
            TextComponent text = new TextComponent("§8 - §a"+ target.getName());
            text.setHoverEvent(new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    new BaseComponent[]{
                            new TextComponent("§c§lCLIQUEZ ICI POUR TRAQUEZ §a"+ target.getName()+"§7 (§6/ns byakugan <joueur>§7)")
                    }
            ));
            text.setClickEvent(new ClickEvent(
                    ClickEvent.Action.RUN_COMMAND,
                    "/ns byakugan "+ target.getName()
            ));
            return text;
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
                    "§7Vous permet de traquer un joueur pendant§c 30 secondes§7.");
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
                    new TraqueRunnable(this.getRole().getGameState().getGamePlayer().get(target.getUniqueId()), getRole().getGamePlayer());
                    return true;
                }
            }
            return false;
        }
        private static class TraqueRunnable extends BukkitRunnable {

            private final GamePlayer gameTarget;
            private final GamePlayer gamePlayer;
            private int timeLeft = 30*20;

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
    private static class TenketsuPower extends CommandPower {

        public TenketsuPower(@NonNull RoleBase role) {
            super("/ns tenketsu <joueur>", "tenketsu", new Cooldown(60*5), role, CommandType.NS,
                    "§7Vous permet de savoir quel est la§a nature de chakra§7 du joueur visée");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            final String[] args = (String[]) map.get("args");
            if (args.length == 2) {
                final Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    if (getRole().getGameState().hasRoleNull(player.getUniqueId())) {
                        player.sendMessage("§cCette personne n'a pas de rôle, impossible de voir ses§a tenketsus");
                        return false;
                    }
                    if (Loc.getNearbyPlayersExcept(player, 10).contains(target)) {
                        final RoleBase role = getRole().getGameState().getGamePlayer().get(target.getUniqueId()).getRole();
                        if (role instanceof NSRoles) {
                            if (((NSRoles) role).getChakras() != null) {
                                player.sendMessage("§c"+target.getDisplayName()+"§7 possède le chakra: "+((NSRoles) role).getChakras().getShowedName());
                            } else {
                                player.sendMessage("§cC'est étrange, on dirait que§b "+target.getDisplayName()+"§c ne possède pas de nature de chakra");
                            }
                        } else {
                            player.sendMessage("§cC'est étrange, on dirait que§b "+target.getDisplayName()+"§c n'a pas chakra dans son corp");
                        }
                        return true;
                    } else {
                        player.sendMessage("§b"+target.getDisplayName()+"§c est trop loin pour que vous puissiez voir ses§a tenketsu");
                        return false;
                    }
                } else {
                    player.sendMessage("§c"+args[1]+" n'est pas connectée");
                }
            }
            return false;
        }
    }
    private static final class PowersInfo extends CommandPower implements Listener {

        private boolean madeChoice = false;
        private int amountPowerChoice = -1;
        private UUID lastTargetUuid;

        public PowersInfo(@NonNull RoleBase role) {
            super("§a/ns pouvoirs <joueur> <nombre de pouvoirs>§r", "pouvoirs", new Cooldown(60*10), role, CommandType.NS
            );
            this.lastTargetUuid = role.getPlayer();
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            final String[] args = (String[]) map.get("args");
            if (args.length == 3) {
                final Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    final GamePlayer gamePlayer = GamePlayer.of(target.getUniqueId());
                    if (gamePlayer != null) {
                        if (gamePlayer.check()) {
                             final List<Power> powerList = new ArrayList<>(gamePlayer.getRole().getPowers());
                             try {
                                 int grp = Integer.parseInt(args[2]);
                                 if (powerList.size() == grp) {
                                     final TextComponent textComponent = new TextComponent("§aHinata§7 vous demande de divulger le nom de l'un de vos pouvoirs, si vous n'en choisissez pas un, vous perdez§c 1❤ permanent§7.\n\n");
                                     int amount = 0;
                                     for (Power power : powerList) {
                                         final TextComponent powerComponent = new TextComponent(AutomaticDesc.fromLegacyTextSafe("\n§8 -§a "+(power instanceof ItemPower ? ((ItemPower) power).getItem().getItemMeta().getDisplayName() : power.getName())+"\n\n"));
                                         powerComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent("§cCliquez pour montrer à§a Hinata§c le§a nom de ce pouvoir§c.")}));
                                         powerComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ns choice "+amount));
                                         textComponent.addExtra(powerComponent);
                                         amount++;
                                     }
                                     this.lastTargetUuid = gamePlayer.getUuid();
                                     target.spigot().sendMessage(textComponent);
                                     new PowerRunnable(this, gamePlayer, powerList).runTaskTimerAsynchronously(getPlugin(), 0, 20);
                                 } else {
                                     player.sendMessage("§b"+target.getName()+"§c n'a pas§b "+grp+" pouvoirs§c.");
                                 }
                                 return true;
                             } catch (NumberFormatException ignored) {
                             }
                        }
                    }
                    player.sendMessage("§cImpossible de viser§b "+target.getName()+"§c.");
                } else {
                    player.sendMessage("§b"+args[1]+"§c n'existe pas ou n'est pas connecter !");
                }
                return false;
            } else {
                player.sendMessage("§cLa commande est "+getName()+"§c.");
            }
            return false;
        }
        private void stopRunnableNoChoice(GamePlayer gameTarget) {
            gameTarget.getRole().setMaxHealth(gameTarget.getRole().getMaxHealth()-2.0);
            gameTarget.sendMessage("§aHinata§7 vous a fait perdre§c 1❤ permanent§7.");
            getRole().getGamePlayer().sendMessage("§c"+gameTarget.getPlayerName()+"§7 a perdu§c 1❤§7.");
        }
        @EventHandler(priority = EventPriority.MONITOR)
        private void onStopCooldown(@NonNull final CooldownFinishEvent event) {
            if (event.getCooldown().getUniqueId().equals(getCooldown().getUniqueId())) {
                this.madeChoice = false;
            }
        }

        @EventHandler
        private void onCommandPreprocess(@NonNull final PlayerCommandPreprocessEvent event) {
            String message = event.getMessage();
            String[] args = message.split(" ");
            if (args[0].equalsIgnoreCase("/ns")) {
                if (args.length == 3 && args[1].equalsIgnoreCase("choice") && this.lastTargetUuid.equals(event.getPlayer().getUniqueId()) && !this.madeChoice) {
                    String valueStr = args[2];
                    try {
                        this.amountPowerChoice = Integer.parseInt(valueStr);
                        this.madeChoice = true;
                        this.lastTargetUuid = getRole().getPlayer();
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }

        private void stopRunnableWithChoice(@NonNull final GamePlayer gameTarget, @NonNull final List<Power> powerList) {
            final Power theChosedOne = powerList.get(this.amountPowerChoice);
            final String name = (theChosedOne instanceof ItemPower ? ((ItemPower) theChosedOne).getItem().getItemMeta().getDisplayName() : theChosedOne.getName());
            gameTarget.sendMessage("§aHinata§7 sait maintenant que vous avez le pouvoir \"§a"+name+"§7\".");
            getRole().getGamePlayer().sendMessage("§c"+gameTarget.getPlayerName()+"§7 a l'un de ses pouvoirs qui se nomme \"§a"+name+"§7\".");
            this.madeChoice = false;
            this.amountPowerChoice = -1;
        }

        @Override
        public List<String> getCompletor(String[] args) {
            if (args.length == 2) {
                final List<String> stringList = new ArrayList<>();
                for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (onlinePlayer.getUniqueId().equals(getRole().getPlayer())) {continue;}
                    final GamePlayer gamePlayer = GamePlayer.of(onlinePlayer.getUniqueId());
                    if (gamePlayer == null)continue;
                    if (!gamePlayer.check())continue;
                    if (onlinePlayer.getName().contains(args[args.length-1])){
                        stringList.add(onlinePlayer.getName());
                    }
                }
                return stringList;
            }
            if (args.length == 3) {
                final List<String> stringList = new ArrayList<>();
                for (int i = 0; i <= 10; i++) {
                    stringList.add(""+i);
                }
                return stringList;
            }
            return super.getCompletor(args);
        }

        private static final class PowerRunnable extends BukkitRunnable {

            private final PowersInfo powersInfo;
            private final GamePlayer gameTarget;
            private final List<Power> powerList;

            private int timeLeft = 30;

            private PowerRunnable(PowersInfo powersInfo, GamePlayer gameTarget, List<Power> powerList) {
                this.powersInfo = powersInfo;
                this.gameTarget = gameTarget;
                this.powerList = powerList;
            }
            @Override
            public void run() {
                if (!GameState.inGame()) {
                    cancel();
                    return;
                }
                if (this.powersInfo.madeChoice) {
                    this.powersInfo.getRole().getGamePlayer().getActionBarManager().removeInActionBar("hinata.power");
                    this.powersInfo.stopRunnableWithChoice(this.gameTarget, this.powerList);
                    cancel();
                    return;

                }
                if (this.timeLeft <= 0) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> this.powersInfo.stopRunnableNoChoice(gameTarget));
                    cancel();
                    return;
                }
                this.powersInfo.getRole().getGamePlayer().getActionBarManager().updateActionBar("hinata.power", "§bTemps de choix restant pour§c "+gameTarget.getPlayerName()+"§b:§c "+ StringUtils.secondsTowardsBeautiful(this.timeLeft));
                this.timeLeft--;
            }

        }
    }
    private static class WeaknessRunnable extends BukkitRunnable {

        private final Hinata hinata;

        private WeaknessRunnable(Hinata hinata) {
            this.hinata = hinata;
            runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
        }

        @Override
        public void run() {
            final GameState gameState = GameState.getInstance();
            if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            for (final Player player : Loc.getNearbyPlayers(this.hinata.getGamePlayer().getLastLocation(), 15)) {
                if (this.hinata.getListPlayerFromRole(NarutoV2.class).contains(player)) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> this.hinata.givePotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 0, false, false), EffectWhen.NOW));
                    break;
                }
            }
        }
    }
}