package fr.nicknqck.roles.ns.solo.jubi;

import com.avaje.ebean.validation.NotNull;
import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.interfaces.IRoleGotSubWorld;
import fr.nicknqck.interfaces.IRoles;
import fr.nicknqck.interfaces.ISubRoleWorld;
import fr.nicknqck.interfaces.IUncompatibleRole;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.enums.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.enums.EChakras;
import fr.nicknqck.enums.Intelligence;
import fr.nicknqck.roles.ns.builders.ISAkatsukiChief;
import fr.nicknqck.roles.ns.builders.IUchiwa;
import fr.nicknqck.roles.ns.builders.JubiRoles;
import fr.nicknqck.roles.ns.power.Genjutsu;
import fr.nicknqck.roles.ns.power.Izanagi;
import fr.nicknqck.roles.ns.power.KamuiPower;
import fr.nicknqck.roles.ns.power.YameruPower;
import fr.nicknqck.roles.ns.shinobi.KakashiV2;
import fr.nicknqck.utils.KamuiDimension;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.*;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ObitoV2 extends JubiRoles implements ISAkatsukiChief, IUncompatibleRole, IRoleGotSubWorld {

    private KamuiPower kamuiPower;

    public ObitoV2(UUID player) {
        super(player);
    }

    @Override
    public String getName() {
        return "Obito";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Obito;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        super.RoleGiven(gameState);
        Main.getInstance().getRoleWorldManager().addWorldManaged(getSubWorld());
        givePotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        this.kamuiPower = new KamuiPower(this);
        addPower(this.kamuiPower, true);
        addPower(new NinjutsuSpatioTemporel(this), true);
        addPower(new Genjutsu(this), true);
        addPower(new YameruPower(this));
        addPower(new ObtainSusanoPower(this));
        addPower(new Izanagi(this));
        addKnowedRole(MadaraV2.class);
        getGamePlayer().startChatWith("§dObito:", "!", MadaraV2.class);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.INTELLIGENT;
    }

    @Override
    public EChakras[] getChakrasCanHave() {
        return new EChakras[] {
                EChakras.KATON
        };
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .getText();
    }

    @Override
    public IRoles<?>[] getUncompatibleList() {
        return new IRoles[] {
                Roles.JubiSasuke
        };
    }

    @Override
    public ISubRoleWorld getSubWorld() {
        if (this.kamuiPower == null) {
            //Je peux faire ceci, car normalement, ce sera utiliser juste pour le menu donc c'est OK
            return new KamuiDimension();
        }
        return this.kamuiPower.getKamuiDimension();
    }

    private static class ObtainSusanoPower extends CommandPower implements Listener {

        private final Map<String, Location> deathLocations;

        public ObtainSusanoPower(@NonNull RoleBase role) {
            super("/ns obtain", "obtain", null, role, CommandType.NS,
                    "§7Lorsqu'un§4 Uchiwa§7 meurt vous obtiendrez ses coordonnées, une fois que vous y serez, en faisant cette commande vous obtiendrez le§c§l Susanô§7.",
                    "",
                    "§cEn faisant /ns obtain list vous obtiendrez les coordonnées de la ou sont mort les§4 Uchiwa§c.");
            this.deathLocations = new HashMap<>();
            EventUtils.registerRoleEvent(this);
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                int amountUchiwa = 0;
                for (final GamePlayer gamePlayer : getRole().getGameState().getGamePlayer().values()) {
                    if (gamePlayer.getRole() == null)continue;
                    if (gamePlayer.getRole() instanceof IUchiwa) {
                        amountUchiwa++;
                    }
                }
                if (amountUchiwa == 1) {//1 parce que Obito lui même est un Uchiwa (。_。)
                    getRole().removePower(this);
                    this.getRole().addPower(new SusanoPower(this.getRole()), true);
                    this.getRole().getGamePlayer().sendMessage("§7Vous avez reçus le§c§l Susanô");
                    this.deathLocations.clear();
                    EventUtils.unregisterEvents(this);
                }
            }, 10*20);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            @NonNull final String[] args = (String[]) map.get("args");
            if (this.deathLocations.isEmpty()) {
                player.sendMessage("§7Aucun§4§l Uchiwa§7 est mort...");
                return false;
            }
            if (args.length < 2) {
                for (@NonNull final String string : this.deathLocations.keySet()) {
                    final Location location = this.deathLocations.get(string);
                    if (player.getLocation().distance(location) <= 5) {
                        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                            this.getRole().addPower(new SusanoPower(this.getRole()), true);
                            this.getRole().getPowers().remove(this);
                            this.getRole().getGamePlayer().sendMessage("§7Vous avez reçus le§c§l Susanô");
                            this.deathLocations.clear();
                            if (string.toLowerCase().contains("kakashi")) {
                                this.getRole().getPowers().forEach(power -> power.setCooldown(new Cooldown(power.getCooldown().getOriginalCooldown()/2)));
                            }
                        }, 20);
                        EventUtils.unregisterEvents(this);
                        return true;
                    } /*2else {
                 //       player.sendMessage("§7Vous êtes trop loin de la mort de §c"+string+"§7 pour récupérer ses yeux"+(location.getWorld().equals(player.getWorld()) ? " §7(§c"+new DecimalFormat("0").format(player.getLocation().distance(location)) : ""));
                    }*/
                }
            } else {
                if (args[1].equalsIgnoreCase("list")) {
                    for (@NonNull final String string : this.deathLocations.keySet()) {
                        player.sendMessage(new String[] {
                                string+"§7 est mort en: ",
                                "",
                                "§cx: "+this.deathLocations.get(string).getBlockX(),
                                "",
                                "§cy: "+this.deathLocations.get(string).getBlockY(),
                                "",
                                "§cz: "+this.deathLocations.get(string).getBlockZ()
                        });
                    }
                    return false;
                }
            }
            return false;
        }
        @EventHandler
        private void GamePlayerDeathEvent(@NonNull final UHCDeathEvent event) {
            if (event.getPlayer().getUniqueId().equals(getRole().getPlayer()))return;
            if (!event.getPlayer().getWorld().getName().equals("arena"))return;
            if (!this.getRole().getPowers().contains(this))return;
            if (event.getRole() instanceof IUchiwa) {
                getRole().getGamePlayer().sendMessage("§cUn§4 Uchiwa§c est mort ! C'est sûrement l'occasion pour vous de récupérez un§4 Sharingan§c, avec un peux de chance vous pourriez obtenir un Susanô...",
                        " ",
                        "§cx: "+event.getPlayer().getLocation().getBlockX(),
                        " ",
                        "§cz: "+event.getPlayer().getLocation().getBlockZ());
                this.deathLocations.put(event.getRole().getTeamColor()+event.getRole().getName(), event.getPlayer().getLocation());
            }
        }
        @EventHandler(priority = EventPriority.HIGH)//Priorité élever = ça passe après le code au-dessus
        private void GamePlayerDeathEvent2(@NonNull final UHCDeathEvent event) {
            if (!Main.getInstance().getGameConfig().getNarutoConfig().isObitoCanGetKakashiEye())return;
            if (event.getPlayer().getUniqueId().equals(getRole().getPlayer()))return;
            if (!event.getPlayer().getWorld().getName().equals("arena") || !event.getPlayer().getWorld().getName().equalsIgnoreCase("kamui"))return;
            if (!this.getRole().getPowers().contains(this))return;
            if (event.getRole() instanceof KakashiV2) {
                getRole().getGamePlayer().sendMessage("§aKakashi§c est mort ! C'est sûrement l'occasion pour vous de récupérez §nvotre§4 Sharingan§c, avec un peux de chance vous pourriez obtenir un Susanô voir même diminuer vos cooldowns...",
                        " ",
                        "§cx: "+event.getPlayer().getLocation().getBlockX(),
                        " ",
                        "§cz: "+event.getPlayer().getLocation().getBlockZ());
                this.deathLocations.put(event.getRole().getTeamColor()+event.getRole().getName(), event.getPlayer().getLocation());
            }
        }

        @Override
        public List<String> getCompletor(String[] args) {
            if (args.length >= 2) {
                final List<String> stringList = new ArrayList<>();
                stringList.add("list");
                return stringList;
            }
            return super.getCompletor(args);
        }

        private static class SusanoPower extends ItemPower {

            protected SusanoPower(@NonNull RoleBase role) {
                super("Susano (Obito)", new Cooldown(60*20), new ItemBuilder(Material.NETHER_STAR).setName("§c§lSusanô"), role,
                        "§7Vous permet d'obtenir l'effet§c Résistance I§7 pendant§c 5 minutes§7. (1x/20m)");
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                if (getInteractType().equals(InteractType.INTERACT)) {
                    new SusanoRunnable(this.getRole().getGameState(), this.getRole().getGamePlayer());
                    getRole().givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60*5, 0, false, false), EffectWhen.NOW);
                    player.sendMessage("§cActivation du§l Susanô§c.");
                    return true;
                }
                return false;
            }
            private static class SusanoRunnable extends BukkitRunnable {

                private final GameState gameState;
                private final GamePlayer gamePlayer;
                private int timeLeft = 60*5;

                private SusanoRunnable(GameState gameState, GamePlayer gamePlayer) {
                    this.gameState = gameState;
                    this.gamePlayer = gamePlayer;
                    this.gamePlayer.getActionBarManager().addToActionBar("obito.susano", "§bTemp restant du§c§l Susanô§b: "+StringUtils.secondsTowardsBeautiful(this.timeLeft));
                    runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
                }

                @Override
                public void run() {
                    if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                        cancel();
                        return;
                    }
                    if (this.timeLeft <= 0) {
                        this.gamePlayer.getActionBarManager().removeInActionBar("obito.susano");
                        this.gamePlayer.sendMessage("§cVotre§l Susanô§c s'arrête");
                        cancel();
                        return;
                    }
                    this.timeLeft--;
                    this.gamePlayer.getActionBarManager().updateActionBar("obito.susano", "§bTemp restant du§c§l Susanô§b: "+StringUtils.secondsTowardsBeautiful(this.timeLeft));
                }
            }
        }
    }
    private static class NinjutsuSpatioTemporel extends ItemPower implements Listener{

        @NotNull
        private final NinjutsuRunnable ninjutsuRunnable;

        protected NinjutsuSpatioTemporel(@NonNull RoleBase role) {
            super("Ninjutsu Spatio-Temporel", new Cooldown(60*8), new ItemBuilder(Material.NETHER_STAR).setName("§dNinjutsu Spatio-Temporel"), role,
                    "§7Vous permet de vous rendre§c invisible§7 pendant une durée de§c 60 secondes§7.",
                    "",
                    "§7(Vous pouvez retirer votre invisibilité en réapuyant sur cette item)");
            this.ninjutsuRunnable = new NinjutsuRunnable(role.getGameState(), this);
            setWorkWhenInCooldown(true);
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (!getCooldown().isInCooldown()) {
                    this.ninjutsuRunnable.start(player);
                    return true;
                } else {
                    if (getCooldown().getCooldownRemaining() > 60*9) {
                        if (getCooldown().getCooldownRemaining() < (60*10)-5) {
                            this.ninjutsuRunnable.stop(player);
                        } else {
                            player.sendMessage("§cIl faut attendre avant de pouvoir annuler votre Ninjutsu.");
                        }
                    } else {
                        player.sendMessage("§cVous êtes en cooldown:§b "+StringUtils.secondsTowardsBeautiful(getCooldown().getCooldownRemaining()));
                    }
                    return false;
                }
            }
            return false;
        }
        @EventHandler
        private void EntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
            if (event.isCancelled())return;
            if (!event.getDamager().getUniqueId().equals(getRole().getPlayer()))return;
            if (this.ninjutsuRunnable != null) {
                if (this.ninjutsuRunnable.running) {
                    event.setCancelled(true);
                }
            }
        }
        @EventHandler
        private void onShootBow(@NonNull final EntityShootBowEvent event) {
            if (event.getEntity().getUniqueId().equals(getRole().getPlayer())) {
                if (this.ninjutsuRunnable != null) {
                    if (this.ninjutsuRunnable.running) {
                        event.setCancelled(true);
                        if (event.getEntity() instanceof Player) {
                            ((Player) event.getEntity()).updateInventory();
                        }
                    }
                }
            }
        }
        private static class NinjutsuRunnable extends BukkitRunnable {

            private final GameState gameState;
            private final NinjutsuSpatioTemporel ninjutsu;
            private int timeLeft;
            private boolean running = false;

            private NinjutsuRunnable(GameState gameState, NinjutsuSpatioTemporel ninjutsu) {
                this.gameState = gameState;
                this.ninjutsu = ninjutsu;
                this.timeLeft = 60;
            }

            @Override
            public void run() {
                if (!this.gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    this.running = false;
                    return;
                }
                this.timeLeft--;
                ninjutsu.getRole().getGamePlayer().getActionBarManager().updateActionBar("ninjutsu.runnable", "§bTemp restant§c invisible§b:§c "+StringUtils.secondsTowardsBeautiful(this.timeLeft));
                if (this.timeLeft <= 0) {
                    final Player player = Bukkit.getPlayer(this.ninjutsu.getRole().getPlayer());
                    if (player != null) {
                        Bukkit.getScheduler().runTask(Main.getInstance(), () -> this.stop(player));
                    }
                }
            }

            public void start(@NonNull Player player) {
                ninjutsu.getRole().getGamePlayer().getActionBarManager().addToActionBar("ninjutsu.runnable", "§bTemp restant§c invisible§b:§c "+StringUtils.secondsTowardsBeautiful(this.timeLeft));
                this.ninjutsu.getRole().givePotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20*60, 0, false, false), EffectWhen.NOW);
                player.setSleepingIgnored(true);
                for (@NonNull final UUID uuid : gameState.getInGamePlayers()) {
                    @NonNull final Player target = Bukkit.getPlayer(uuid);
                    if (target == null)continue;
                    if (target.getUniqueId().equals(player.getUniqueId()))continue;
                    target.hidePlayer(player);
                }
                this.timeLeft = 60;
                this.running = true;
                runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
            }
            public void stop(@NonNull final Player player) {
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> player.removePotionEffect(PotionEffectType.INVISIBILITY));
                for (@NonNull final Player target : Bukkit.getOnlinePlayers()) {
                    if (!target.canSee(player)) {
                        target.showPlayer(player);
                    }
                }
                this.ninjutsu.getRole().getGamePlayer().getActionBarManager().removeInActionBar("ninjutsu.runnable");
                cancel();
                this.running = false;
            }
        }
    }
}