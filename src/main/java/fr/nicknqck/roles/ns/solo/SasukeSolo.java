package fr.nicknqck.roles.ns.solo;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.events.custom.roles.PowerActivateEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.akatsuki.ItachiV2;
import fr.nicknqck.roles.ns.builders.EUchiwaType;
import fr.nicknqck.roles.ns.builders.IUchiwa;
import fr.nicknqck.roles.ns.builders.NSRoles;
import fr.nicknqck.roles.ns.power.Amaterasu;
import fr.nicknqck.roles.ns.power.Genjutsu;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SasukeSolo extends NSRoles implements IUchiwa, Listener {

    private boolean itachiKill = false;

    public SasukeSolo(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.INTELLIGENT;
    }

    @Override
    public String getName() {
        return "Sasuke§7 (§eSolo§7)§r";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Sasuke;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Solo;
    }

    @Override
    public @NonNull EUchiwaType getUchiwaType() {
        return EUchiwaType.IMPORTANT;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false), EffectWhen.PERMANENT);
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.PERMANENT);
        givePotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 60, 0, false, false), EffectWhen.PERMANENT);
        addPower(new KatanaRaiton(this), true);
        addPower(new MilleOiseaux(this), true);
        addPower(new Kirin(this), true);
        addPower(new Amaterasu(this), true);
        addPower(new Genjutsu(this, false), true);
        addPower(new SusanoPower(this), true);
        addPower(new Rinnegan(this), true);
        setChakraType(Chakras.KATON);
        EventUtils.registerRoleEvent(this);
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .addCustomLine(!itachiKill ? "§7En tuant§c Itachi§7 tout vos cooldown seront§c divisé§7 par§c deux§7." : "")
                .getText();
    }

    @EventHandler
    private void onUHCDeath(final UHCPlayerKillEvent event) {
        if (event.getGamePlayerKiller() == null)return;
        if (!event.getGamePlayerKiller().getUuid().equals(getPlayer()))return;
        if (event.getGameState().hasRoleNull(event.getVictim().getUniqueId()))return;
        final RoleBase role = event.getGameState().getGamePlayer().get(event.getVictim().getUniqueId()).getRole();
        if (role instanceof ItachiV2 && ! this.itachiKill) {
            this.itachiKill = true;
            event.getGamePlayerKiller().sendMessage("§7Vous avez tué§c Itachi§7 vous obtenez donc un§c Caléidoscope hypnotique du sharingan Éternel");
        }
    }
    @EventHandler
    private void onPowerUse(final PowerActivateEvent event) {
        if (event.getPlayer().getUniqueId().equals(getPlayer()) && this.itachiKill) {
            if (event.getPower().getCooldown() == null)return;
            //Vérification de si on est déjà en CD ou pas
            if (!event.getPower().getCooldown().isInCooldown()) {
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    //On vérifie que 15ticks après le pouvoir est en cd et que donc le pouvoir a bien été utilisé
                    if (event.getPower().getCooldown().isInCooldown()) {
                        event.getPower().getCooldown().setActualCooldown(event.getPower().getCooldown().getCooldownRemaining()/2);
                    }
                }, 15);
            }
        }
    }

    private static class KatanaRaiton extends ItemPower implements Listener{

        public KatanaRaiton(@NonNull RoleBase role) {
            super("Katana Raiton", null, new ItemBuilder(Material.DIAMOND_SWORD).addEnchant(Enchantment.DAMAGE_ALL, 3).setName("§5Katana Raiton"), role,
                    "§7Vous offre§c 5%§7 de chance d'infliger§e Raiton§7 en frappant un joueur");
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            return RandomUtils.getOwnRandomProbability(5.0) && player.getItemInHand() != null && player.getItemInHand().isSimilar(getItem());
        }
        @EventHandler
        private void onBattle(final EntityDamageByEntityEvent event) {
            if (event.getDamager().getUniqueId().equals(getRole().getPlayer()) && event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
                if (checkUse((Player) event.getDamager(), new HashMap<>())) {
                    LivingEntity victim = (LivingEntity) event.getEntity();
                    victim.damage(0);
                    victim.setHealth(Math.max(1.0, victim.getHealth()-1.0));
                    victim.getEyeLocation().getWorld().strikeLightningEffect(victim.getEyeLocation());
                    event.getDamager().sendMessage("§7Votre§e Katana Raiton§7 à fait effet sur §c"+victim.getName());
                }
            }
        }
    }
    private static class MilleOiseaux extends ItemPower implements Listener {

        public MilleOiseaux(@NonNull RoleBase role) {
            super("Mille Oiseaux", new Cooldown(150), new ItemBuilder(Material.NETHER_STAR).setName("§cMille Oiseaux"), role,
                    "§7En frappant un joueur, vous permet de lui infliger§c 1❤§7 de§c dégâts§7 via un§e éclair");
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            return player.getItemInHand() != null && player.getItemInHand().isSimilar(getItem()) && getInteractType().equals(InteractType.ATTACK_ENTITY);
        }
        @EventHandler
        private void onBattle(final EntityDamageByEntityEvent event) {
            if (event.getDamager().getUniqueId().equals(getRole().getPlayer()) && event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
                if (!((Player) event.getDamager()).getItemInHand().isSimilar(getItem()))return;
                if (checkUse((Player) event.getDamager(), new HashMap<>())) {
                    LivingEntity victim = (LivingEntity) event.getEntity();
                    Player player = (Player) event.getDamager();
                    victim.damage(0, player);
                    victim.setHealth(Math.max(1.0, victim.getHealth()-2.0));
                    victim.getWorld().strikeLightningEffect(victim.getEyeLocation());
                    event.getDamager().sendMessage("§7Votre technique des§c Mille Oiseaux§7 à été utiliser contre§c "+victim.getName());
                }
            }
        }
    }
    private static class Kirin extends ItemPower {

        public Kirin(@NonNull RoleBase role) {
            super("Kirin", new Cooldown(60*5), new ItemBuilder(Material.NETHER_STAR).setName("§cKirin"), role,
                    "§7Pendant§c 10 secondes§7 toute les personnes autours de vous subissent§c -1/2❤§7 de§c dégâts§7 via un§e éclair§7 toute les§c secondes§7.",
                    "",
                    "§7Au bout de§c 10 secondes§7, toute les personnes qui ont été touchés par le§c Kirin§7 recevront:",
                    "",
                    "§8 -§c -2❤§7 de§c dégâts§7.",
                    "§8 -§c Blindness 3§7 pendant§c 2 secondes§7.",
                    "§8 -§c Slowness 1§7 pendant§c 8 secondes");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                new KirinRunnable(getRole().getGameState(), getRole());
                return true;
            }
            return false;
        }
        private static class KirinRunnable extends BukkitRunnable {

            private final GameState gameState;
            private final RoleBase role;
            private final List<UUID> uuidList;
            private int time;

            private KirinRunnable(GameState gameState, RoleBase role) {
                this.gameState = gameState;
                this.role = role;
                this.time = 0;
                this.uuidList = new ArrayList<>();
                runTaskTimer(Main.getInstance(), 0, 20);
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (this.time >= 10 || !this.role.getGamePlayer().isAlive()) {
                    for (final UUID uuid : this.uuidList) {
                        final Player player = Bukkit.getPlayer(uuid);
                        if (player == null)continue;
                        player.setHealth(Math.max(1.0, player.getHealth()-4));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 2, false ,false), true);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*8, 0, false, false), true);
                        player.sendMessage("§eSasuke§7 vous inflige des effets complet de son§c Kirin§7.");
                    }
                    cancel();
                    return;
                }
                final List<Location> locationList = new ArrayList<>();
                final Location loc = this.role.getGamePlayer().getLastLocation();
                while (locationList.size() <= 10) {
                    for (int x = loc.getBlockX()-10; x < loc.getBlockX()+10; x++) {
                        for (int z = loc.getBlockZ()-10; z < loc.getBlockZ()+10; z++) {
                            if (locationList.size() <= 10 && RandomUtils.getOwnRandomProbability(10)) {
                                locationList.add(new Location(loc.getWorld(), x, loc.getWorld().getHighestBlockYAt(x ,z), z));
                            }
                        }
                    }
                }
                for (final Player target : Loc.getNearbyPlayers(loc, 15)) {
                    if (target.getUniqueId().equals(this.role.getPlayer()))continue;
                    locationList.add(target.getLocation());
                    target.setHealth(Math.max(1.0, target.getHealth()-1.0));
                    target.sendMessage("§eSasuke§7 vous inflige des effets partiel de son§c Kirin§7.");
                    this.uuidList.add(target.getUniqueId());
                }
                for (final Location location : locationList) {
                    location.getWorld().strikeLightningEffect(location);
                }
                this.time++;
            }
        }
    }
    private static class SusanoPower extends ItemPower {

        protected SusanoPower(@NonNull RoleBase role) {
            super("Susano (Sasuke)",
                    new Cooldown(60*20),
                    new ItemBuilder(Material.NETHER_STAR).setName("§c§lSusanô"),
                    role,
                    "§7Vous permet d'obtenir l'effet§9 Résistance I§7 pendant§c 5 minutes§7. (1x/20m)");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                player.sendMessage("§cActivation du§l Susanô§c.");
                new SusanoPower.SusanoRunnable(this.getRole().getGameState(), this.getRole().getGamePlayer());
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60*5, 0, false, false), EffectWhen.NOW);
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
                this.gamePlayer.getActionBarManager().addToActionBar("sasuke.susano", "§bTemp restant du§c§l Susanô§b: "+ StringUtils.secondsTowardsBeautiful(this.timeLeft));
                runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (this.timeLeft <= 0) {
                    this.gamePlayer.getActionBarManager().removeInActionBar("sasuke.susano");
                    this.gamePlayer.sendMessage("§cVotre§l Susanô§c s'arrête");
                    cancel();
                    return;
                }
                this.timeLeft--;
                this.gamePlayer.getActionBarManager().updateActionBar("sasuke.susano", "§bTemp restant du§c§l Susanô§b: "+StringUtils.secondsTowardsBeautiful(this.timeLeft));
            }
        }
    }
    private static class Rinnegan extends ItemPower {

        public Rinnegan(@NonNull RoleBase role) {
            super("Rinnegan", new Cooldown(60*5), new ItemBuilder(Material.NETHER_STAR).setName("§dRinnegan"), role,
                    "§7Vous permet d'échanger votre position avec le joueur viser");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final Player target = RayTrace.getTargetPlayer(player, 50, null);
                if (target == null) {
                    player.sendMessage("§cIl faut viser un joueur !");
                    return false;
                }
                final Location oldPlayerLoc = player.getLocation();
                player.teleport(target, PlayerTeleportEvent.TeleportCause.PLUGIN);
                target.teleport(oldPlayerLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);
                target.sendMessage("§eSasuke§c a utilisé son§d Rinnegan§c sur vous !");
                player.sendMessage("§cVous avez échanger votre position avec§a "+target.getDisplayName());
                return true;
            }
            return false;
        }
    }
}