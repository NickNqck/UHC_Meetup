package fr.nicknqck.events.ns;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EffectGiveEvent;
import fr.nicknqck.events.custom.ResistancePatchEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.events.custom.roles.TeamChangeEvent;
import fr.nicknqck.events.ds.Event;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.builders.IByakuganUser;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EveilTenseiGan extends Event implements Listener {

    private boolean activate = false;
    private final String print = "[EveilTenseiganEvent] ";
    private GamePlayer gamePlayer;
    private final ItemStack sword = new ItemBuilder(Material.DIAMOND_SWORD).addEnchant(Enchantment.DAMAGE_ALL, 3).setUnbreakable(true).setName("§cÉpée du§b Tenseigan").setLore("§7Vous permet de passer à travers la résistance des personnes frappés").toItemStack();

    @Override
    public boolean isActivated() {
        return this.activate;
    }

    @Override
    public String getName() {
        return "§bÉveil du Tenseigan";
    }

    @Override
    public void onProc(GameState gameState) {
        final List<GamePlayer> buyakuganUserList = new ArrayList<>();
        for (final GamePlayer gamePlayer : gameState.getGamePlayer().values()) {
            if (!gamePlayer.isAlive())continue;
            if (gamePlayer.getRole() == null)continue;
            if (!gamePlayer.isOnline())continue;
            if (gamePlayer.getRole() instanceof IByakuganUser) {
                buyakuganUserList.add(gamePlayer);
                System.out.println(this.print+gamePlayer.getPlayerName()+" has been added inside the list");
            }
        }
        if (buyakuganUserList.isEmpty()) {
            System.out.println(print+"Unnable to start Tenseigan Event because no one is inside the list");
            return;
        }
        Collections.shuffle(buyakuganUserList, Main.RANDOM);
        System.out.println(print+"Shuffle the list");
        final GamePlayer gamePlayer = buyakuganUserList.get(0);
        System.out.println(print+gamePlayer.getPlayerName()+" has been chose, size of the list: "+buyakuganUserList.size());
        final RoleBase role = gamePlayer.getRole();
        gamePlayer.sendMessage("§7Qu'est-ce qui se passe ? Vous sentez qu'un étrange§a chakra§7 se déverse dans vos yeux, après vérification on dirait qu'il s'agit la d'un§a chakra§e Otsutsuki§7, vous devriez essayer de récupérer d'autres§a byakugans§7 pour éveiller de nouveau pouvoirs.");
        role.setTeam(TeamList.Solo, true);
        this.gamePlayer = gamePlayer;
        if (buyakuganUserList.size() == 1) {//donc si mon joueur est le seul porteur du byakugan
            giveTenseiGan();
        } else {
            for (final GamePlayer g : buyakuganUserList) {
                if (g.getUuid().equals(this.gamePlayer.getUuid()))continue;//donc si ce n'est pas le même joueur
                if (!g.isAlive()) {
                    new CadavreRunnable(this.gamePlayer, g, this);
                    this.gamePlayer.sendMessage("§7Vous ressentez le§a chakra§7 d'un autre§a Byakugan§7, il est en§c x§7:§c "+
                            g.getDeathLocation().getBlockX()+"§7,§c y§7:§c "+g.getDeathLocation().getBlockY()+"§7,§c z§7:§c "+g.getDeathLocation().getBlockZ(),
                            "§e",
                            "§7Si vous y allez peut-être que vous pourriez récupérer ses§a Byakugan§7.");
                } else {
                    this.gamePlayer.sendMessage("§a"+g.getPlayerName()+"§7 possède le§a Byakugan§7, actuellement il est en§c x§7: §c"+
                            g.getLastLocation().getBlockX()+
                            "§7,§c y§7:§c "+
                            g.getLastLocation().getBlockY()+
                            "§7,§c z§7:§c "+
                            g.getLastLocation().getBlockZ());
                }
            }
        }
        this.activate = true;
    }

    @Override
    public ItemStack getMenuItem() {
        return new ItemBuilder(Material.EYE_OF_ENDER).setName(getName()).setLore(getLore()).toItemStack();
    }

    @Override
    public boolean canProc(GameState gameState) {
        for (final GamePlayer gamePlayer : gameState.getGamePlayer().values()) {
            if (!gamePlayer.isAlive())continue;
            if (!gamePlayer.isOnline())continue;
            if (gamePlayer.getRole() == null)continue;
            if (gamePlayer.getRole() instanceof IByakuganUser) {
                return true;//donc il y a minimum 1 utilisateur du byakugan en vie
            }
        }
        return false;
    }

    @Override
    public String[] getExplications() {
        return new String[] {
                "§7Un joueur possédant le§a Byakugan§7 est choisis aléatoirement pour devenir un§e rôle solitaire§7,",
                "§7ce joueur recevra les effets§b Speed I§7 et§9 Résistance I§7, s'il parvient a rester§c 5 secondes",
                "§7sur le cadavre d'un autre porteur du§a Byakugan§7 (ou s'il n'y en a pas d'autre dans la partie),",
                "§7il recevra les items§b Mode Chakra§7 et§a Sphère de Vérité§7.","",
                "§8 -§b Mode Chakra§7: Possède une banque de temps de§c 5 minutes§7,",
                "§7quand le pouvoir est activé l'utilisateur possède§b Speed II§7,§a NoFall§7 et",
                "§7à§c 5%§7 de§c chance§7 d'§6enflammer§7 le joueur attaquer.",
                "",
                "§8 -§b Sphere de vérité§7: Permet d'activer le fait de pouvoir§a voler§7,",
                "§7ce pouvoir n'est utilisable que quand le§b Mode Chakra§7 est actif et coûte§c 2x plus de temps",
                "",
                "§8 -§c Épée du§b Tenseigan§7: Permet de passer à travers la résistance des joueurs frappés,",
                "§7elle est également enchantée tranchant III",
                "",
                "§cLa personne ayant été toucher par l'event restera§e Solitaire§7 jusqu'a la fin de la partie",
                "§7(§cCela outrepasse aussi l'infection de§e§l Shisui§r§7)",
                "",
                "§fL'annonce de l'évènement ce fera après que l'utilisateur ai utiliser pendant§c 1 minute",
                "§fson§b Mode Chakra"
        };
    }

    @Override
    public boolean onGameStart(GameState gameState) {
        EventUtils.registerRoleEvent(this);
        return true;
    }

    private void giveTenseiGan() {
        if (this.gamePlayer == null)return;
        if (!this.gamePlayer.isOnline())return;
        if (gamePlayer.getRole() == null)return;
        if (!gamePlayer.isAlive())return;
        final RoleBase role = gamePlayer.getRole();
        final Player player = Bukkit.getPlayer(gamePlayer.getUuid());
        role.setTeam(TeamList.Solo, true);
        int amountKill = role.getGameState().getPlayerKills().get(role.getPlayer()).size();
        if (amountKill > 0) {
            role.setMaxHealth(role.getMaxHealth()+amountKill);
            if (player != null) {
                player.setHealth(player.getMaxHealth());
            }
            System.out.println(print+ gamePlayer.getPlayerName()+" has been gived "+amountKill+" half-heart");
        }
        gamePlayer.addItems(this.sword);
        gamePlayer.sendMessage("§7Vous avez obtenue le§b Tenseigan§7, vous devenez maintenant inéluctable!");
        role.addPower(new ModeChakraPower(role), true);
        role.givePotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        role.givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
    }
    @EventHandler(priority = EventPriority.LOWEST)
    private void onBattle(final ResistancePatchEvent event) {
        if (this.gamePlayer == null)return;
        if (!this.gamePlayer.getUuid().equals(event.getDamager().getUniqueId()))return;
        if (!event.isNegateResistance())return;
        if (event.getDamager().getItemInHand() == null)return;
        if (!event.getDamager().getItemInHand().isSimilar(this.sword))return;
        //Donc si l'item qu'il a en main est mon épée custom
        event.setCancelled(true);
    }
    @EventHandler(priority = EventPriority.LOWEST)
    private void onKill(final UHCPlayerKillEvent event) {
        if (event.getGamePlayerKiller() == null)return;
        if (this.gamePlayer == null)return;
        if (this.gamePlayer.getUuid().equals(event.getKiller().getUniqueId())) {
            if (gamePlayer.getRole() == null)return;
            if (gamePlayer.getRole().getMaxHealth() < 30.0) {
                gamePlayer.getRole().setMaxHealth(gamePlayer.getRole().getMaxHealth()+1.0);
                event.getKiller().sendMessage("§7En tuant un joueur, vous avez gagner§c 1/2❤ permanent");
            }
        }
    }
    @EventHandler
    private void teamChangeEvent(final TeamChangeEvent event) {
        if (this.gamePlayer == null)return;
        if (this.gamePlayer.getUuid().equals(event.getRole().getPlayer())) {
            if (!event.getNewTeam().equals(TeamList.Solo)) {
                this.gamePlayer.sendMessage("§7Quelqu'un a essayer de vous faire changer de camp mais on dirait que sa n'a pas fonctionner.");
                event.setCancelled(true);
            }
        }
    }
    private static class ModeChakraPower extends ItemPower implements Listener {

        private int timeLeft;
        private final ChakraRunnable chakraRunnable;
        private final SphereVeritePower sphereVeritePower;
        private int timeBeforeMSG = 60;


        public ModeChakraPower(@NonNull RoleBase role) {
            super("Mode Chakra", null, new ItemBuilder(Material.NETHER_STAR).setName("§bMode Chakra"), role,
                    "§7Vous possédez une banque de temps§a activable§7/§cdésactivable§7 de§c 5 minutes§7 qui augmente de§c 1 minute§7 par§c kill§7,",
                    "",
                    "§7Quand votre§b Mode Chakra§7 est§a activé§7, vous possédez l'effet§e Speed II§7 ainsi que§c 5%§7 de§c chance§7 de mettre en§c feu§7 les joueurs que vous attaquez§7."
            );
            this.timeLeft = 90;
            this.chakraRunnable = new ChakraRunnable(this);
            EventUtils.registerRoleEvent(this);
            this.sphereVeritePower = new SphereVeritePower(this);
            role.addPower(this.sphereVeritePower, true);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (this.chakraRunnable.running) {
                    if (sphereVeritePower.fly) {
                        player.sendMessage("§cImpossible d'enlever votre§b Mode Chakra§c, vos§a Spheres de vérités§c sont trop puissante pour que vous puissiez le supporter sans lui.");
                        return false;
                    }
                    this.chakraRunnable.stop();
                } else {
                    if (this.timeLeft <= 0) {
                        player.sendMessage("§7Vous n'avez plus asser de temp disponible pour utiliser votre§b mode chakra§7.");
                        return false;
                    }
                    this.chakraRunnable.start();
                }
                return true;
            }
            return false;
        }
        @EventHandler
        private void onEffectGive(EffectGiveEvent event) {
            if (event.isCancelled())return;
            if (!this.chakraRunnable.running)return;
            if (!event.getPlayer().getUniqueId().equals(getRole().getPlayer()))return;
            if (!event.getEffectWhen().equals(EffectWhen.PERMANENT))return;
            if (!event.getPotionEffect().getType().equals(PotionEffectType.SPEED))return;
            event.setCancelled(true);
        }
        @EventHandler
        private void onAttack(EntityDamageByEntityEvent event) {
            if (!(event.getDamager() instanceof Player))return;
            if (!(event.getEntity() instanceof Player))return;
            if (!event.getDamager().getUniqueId().equals(getRole().getPlayer()))return;
            if (!this.chakraRunnable.running)return;
            if (!RandomUtils.getOwnRandomProbability(5.0))return;
            event.getEntity().setFireTicks(20*5);
        }
        @EventHandler
        private void onFall(EntityDamageEvent event) {
            if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL))return;
            if (!event.getEntity().getUniqueId().equals(getRole().getPlayer()))return;
            if (!this.chakraRunnable.running)return;
            event.setCancelled(true);
        }
        @EventHandler
        private void onKill(UHCPlayerKillEvent event) {
            if (event.getGamePlayerKiller() == null)return;
            if (event.getKiller().getUniqueId().equals(getRole().getPlayer())) {
                this.timeLeft+=60;
                event.getKiller().sendMessage("§7Vous avez gagner§c 60 secondes§7 dans votre§c banque de temp§7.");
            }
        }
        private final static class ChakraRunnable extends BukkitRunnable {

            private final ModeChakraPower modeChakraPower;
            private boolean running;

            private ChakraRunnable(ModeChakraPower modeChakraPower) {
                this.modeChakraPower = modeChakraPower;
                this.running = false;
                runTaskTimerAsynchronously(Main.getInstance(), 20, 20);
            }

            @Override
            public void run() {
                if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                    stop();
                    return;
                }
                if (!running || this.modeChakraPower.timeLeft <= 0) return;
                this.modeChakraPower.timeLeft--;
                if (this.modeChakraPower.sphereVeritePower.fly) {
                    this.modeChakraPower.timeLeft--;
                }
                if (this.modeChakraPower.timeLeft <= 0) {
                    final Player player = Bukkit.getPlayer(this.modeChakraPower.getRole().getPlayer());
                    if (player != null) {
                        this.modeChakraPower.sphereVeritePower.stop(player);
                        stop();
                    }
                    return;
                }
                this.modeChakraPower.getRole().getGamePlayer().getActionBarManager().updateActionBar("tenseigan.chakramode", "§bTemp restant mode chakra: §c"+ StringUtils.secondsTowardsBeautiful(this.modeChakraPower.timeLeft));
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> this.modeChakraPower.getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 1, false, false), EffectWhen.NOW));
                this.modeChakraPower.timeBeforeMSG--;
                if (this.modeChakraPower.timeBeforeMSG == 0) {
                    Bukkit.broadcastMessage(AllDesc.bar);
                    Bukkit.broadcastMessage("§fL'évènement aléatoire §7\"§bÉveil du Tenseigan§7\"§f a eu lieu, un possésseur aléatoire du§a Byakugan§f est devenue un rôle§e Solitaire§7 (Peut importe son camp d'origine et s'il a changer de camp ou pas).");
                    Bukkit.broadcastMessage(AllDesc.bar);

                }
            }
            public void stop() {
                this.running = false;
                this.modeChakraPower.getRole().getGamePlayer().getActionBarManager().removeInActionBar("tenseigan.chakramode");
                final Player player = Bukkit.getPlayer(this.modeChakraPower.getRole().getPlayer());
                if (player != null) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), true));
                }
            }
            public void start() {
                if (this.modeChakraPower.timeLeft <= 0) {
                    return;
                }
                this.running = true;
                this.modeChakraPower.getRole().getGamePlayer().getActionBarManager().addToActionBar("tenseigan.chakramode", "§bTemp restant mode chakra: §c"+ StringUtils.secondsTowardsBeautiful(this.modeChakraPower.timeLeft));
            }
        }
        private static class SphereVeritePower extends ItemPower implements Listener {

            private final ModeChakraPower modeChakraPower;
            private boolean fly = false;

            public SphereVeritePower(@NonNull ModeChakraPower chakraPower) {
                super("Sphere de vérité", new Cooldown(5), new ItemBuilder(Material.FEATHER).setName("§aSphere de vérité"), chakraPower.getRole(),
                        "§7Via un clique droit§a active§7/§cdésactive§7 votre§a fly§7,",
                        "§7Si un joueur vous frappe ou que vous attaquez un autre joueur votre§a vole§7 s'arrêtera.",
                        "§7A l'activation vous perdrez§c 15 secondes directement§7.",
                        "",
                        "§c!ATTENTION! Quand votre§a fly§7 est§a activé§7 le temp dépensé dans votre§c banque de temps§7 sera§c doublé§7.");
                this.modeChakraPower = chakraPower;
                EventUtils.registerRoleEvent(this);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                if (getInteractType().equals(InteractType.INTERACT)) {
                    if (!this.modeChakraPower.chakraRunnable.running) {
                        player.sendMessage("§cImpossible sans le§b Mode Chakra§c.");
                        return false;
                    }
                    if (!fly) {
                        start(player);
                    } else {
                        stop(player);
                    }
                    return true;
                }
                return false;
            }
            @EventHandler
            private void onDamage(EntityDamageEvent event) {
                if (!event.getEntity().getUniqueId().equals(getRole().getPlayer()))return;
                if (!fly)return;
                ((Player) event.getEntity()).setFlying(false);
                ((Player) event.getEntity()).setAllowFlight(false);
                event.getEntity().sendMessage("§7Quelqu'un a stoppé votre envole.");
                event.setDamage(event.getDamage()*0.55);
            }
            @EventHandler(priority = EventPriority.LOW)
            private void onDamage2(EntityDamageByEntityEvent event) {
                if (!(event.getDamager() instanceof Player))return;
                if (!(event.getEntity() instanceof Player))return;
                if (!this.modeChakraPower.chakraRunnable.running)return;
                if (!fly)return;
                if (event.getEntity().getUniqueId().equals(getRole().getPlayer())) {
                    ((Player) event.getEntity()).setFlying(false);
                    ((Player) event.getEntity()).setAllowFlight(false);
                    event.getEntity().sendMessage("§7Quelqu'un a stoppé votre envole.");
                    this.fly = false;
                    return;
                }
                if (event.getDamager().getUniqueId().equals(getRole().getPlayer())) {
                    event.getDamager().sendMessage("§7Votre vole s'arrête");
                    ((Player) event.getDamager()).setFlying(false);
                    ((Player) event.getDamager()).setAllowFlight(false);
                    this.fly = false;
                }
            }
            private void stop(final Player player) {
                player.setFlying(false);
                player.setAllowFlight(false);
                this.fly = false;
                player.sendMessage("§7Vous arrêtez de volé...");
            }
            private void start(final Player player) {
                player.setAllowFlight(true);
                player.setFlying(true);
                this.fly = true;
                player.sendMessage("§7Vous commencez à volé...");
                this.modeChakraPower.timeLeft-=15;
            }
        }
    }
    private static class CadavreRunnable extends BukkitRunnable {

        private final GamePlayer user;
        private final GamePlayer cadavre;
        private final EveilTenseiGan eveilTenseiGan;
        private int timeStayClose = 0;

        private CadavreRunnable(GamePlayer user, GamePlayer cadavre, EveilTenseiGan eveilTenseiGan) {
            this.user = user;
            this.cadavre = cadavre;
            this.eveilTenseiGan = eveilTenseiGan;
            runTaskTimerAsynchronously(Main.getInstance(), 20, 20);
        }

        @Override
        public void run() {
            if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (this.timeStayClose >= 5) {
                this.user.getActionBarManager().removeInActionBar("eveil.cadavre");
                Bukkit.getScheduler().runTask(Main.getInstance(), this.eveilTenseiGan::giveTenseiGan);
                cancel();
                return;
            }
            final Location loc = cadavre.getDeathLocation();
            if (!loc.getWorld().getName().equalsIgnoreCase(Main.getInstance().getWorldManager().getGameWorld().getName())) {
                loc.setWorld(Main.getInstance().getWorldManager().getGameWorld());
            }
            if (user.getLastLocation().getWorld().equals(loc.getWorld())) {
                if (user.getLastLocation().distance(this.cadavre.getDeathLocation()) <= 5.0) {
                    this.timeStayClose++;
                    this.user.getActionBarManager().addToActionBar("eveil.cadavre", "§bTemp rester proche du cadavre:§c "+this.timeStayClose+"s");
                } else {
                    if (this.user.getActionBarManager().containsKey("eveil.cadavre")) {
                        this.user.getActionBarManager().removeInActionBar("eveil.cadavre");
                    }
                }
            }
        }
    }
}