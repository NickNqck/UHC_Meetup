package fr.nicknqck.roles.ns.solo;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.IUchiwa;
import fr.nicknqck.roles.ns.builders.NSSoloRoles;
import fr.nicknqck.roles.ns.power.Izanagi;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class DanzoV2 extends NSSoloRoles implements Listener {

    private final PotionEffect resistance;
    private boolean killUchiwa = false;
    @Setter
    @Getter
    private boolean killHokage = false;

    public DanzoV2(UUID player) {
        super(player);
        this.resistance = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 0, false, false);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.INTELLIGENT;
    }

    @Override
    public String getName() {
        return "Danzo";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Danzo;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Solo;
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .addCustomLine(killUchiwa ?
                        "" :
                        "§7Jusqu'à ce que vous tuez un§4 Uchiwa§7, toute les§c 2 minutes§7 vous recevez l'information si un§4 Uchiwa§7 est présent autours de vous ou non, s'il y en a un vous obtiendrez sont rang (§6/ns uchirang§7)")
                .addCustomLine("§7Vous infligez§c +10%§7 de§c dégâts§7 aux§4 Uchiwa")
                .addCustomLine(killUchiwa ?
                        "" :
                        "§7En tuant un§4 Uchiwa§7 vous obtiendrez l'effet§9 Résistance I§7 de manière§c permanente"
                )
                .getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        EventUtils.registerRoleEvent(this);
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.PERMANENT);
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false), EffectWhen.PERMANENT);
        addPower(new SceauPower(this), true);
        addPower(new IzanagiOffensif(this), true);
        addPower(new FutonPower(this), true);
        addPower(new Izanagi(this));
        new FindersRunnable(this, this.getGamePlayer());
        setChakraType(Chakras.FUTON);
        giveHealedHeartatInt(2.0);
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            int nmbUchiwa = 0;
            for (Player p : Bukkit.getOnlinePlayers()){
                if (isUchiwa(p)){
                    nmbUchiwa++;
                }
            }
            if (nmbUchiwa == 0){
                getGamePlayer().sendMessage("§7Il n'y a pas de§c Uchiwa§7 dans la partie, vous obtenez donc directement l'effet§c Résistance I permanent");
                givePotionEffect(this.resistance, EffectWhen.PERMANENT);
                this.killUchiwa = true;
            }
        }, 20*10);
        super.RoleGiven(gameState);
    }
    private boolean isUchiwa(Player p){
        if (!gameState.hasRoleNull(p.getUniqueId())) {
            return gameState.getGamePlayer().get(p.getUniqueId()).getRole() instanceof IUchiwa;
        }
        return false;
    }
    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager().getUniqueId().equals(getPlayer())) {
            if (!(event.getEntity() instanceof Player))return;
            if (isUchiwa((Player) event.getEntity())) {
                event.setDamage(event.getDamage()*1.1);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    private void onKill(UHCPlayerKillEvent event) {
        if (event.getGamePlayerKiller() == null)return;
        if (event.isCancel())return;
        if (event.getGamePlayerKiller().getUuid().equals(getPlayer())) {
            if (event.getPlayerKiller().getMaxHealth() < 24.0) {
                setMaxHealth(event.getPlayerKiller().getMaxHealth()+1.0);
                event.getPlayerKiller().setMaxHealth(getMaxHealth());
                event.getPlayerKiller().sendMessage("§7Tuer un joueur vous a rendu §c1/2❤§7 permanent");
            }
            if (isUchiwa(event.getVictim())) {
                event.getPlayerKiller().sendMessage("§7Vous venez de tuer un de ces démons du clan §4§lUchiwa !");
                if (!killUchiwa) {
                    givePotionEffect(this.resistance, EffectWhen.PERMANENT);
                    event.getPlayerKiller().sendMessage("§7Vous obtenez l'effet §9Résistance 1§7 de manière permanente");
                    this.killUchiwa = true;
                }
            }
        }
    }
    private static class SceauPower extends ItemPower implements Listener {

        private final Map<UUID, SceauAction> sceauMap;
        private final List<UUID> absoLessList;

        public SceauPower(@NonNull RoleBase role) {
            super("Sceau", new Cooldown(60*3), new ItemBuilder(Material.NETHER_STAR).setName("§cSceau"), role,
                    "§7En visant un joueur, vous permet de lui apposer un§c Sceau§7 aléatoirement parmis ceux ci-dessous:",
                    "",
                    "§8 -§7 Pendant§c 12 secondes§7, la§c cible§7 aura l'effet§c Wither II§7.",
                    "",
                    "§8 -§7 Pendant§c 12 secondes§7, la§c cible§7 ne pourra pas avoir d'§eabsorbtion§7 en mangeant une§e pomme d'or");
            this.sceauMap = new HashMap<>();
            this.absoLessList = new ArrayList<>();
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (!getInteractType().equals(InteractType.INTERACT))return false;
            final Player target = RayTrace.getTargetPlayer(player, 30, null);
            if (target != null) {
                if (RandomUtils.getOwnRandomProbability(50)) {
                    sceauMap.put(target.getUniqueId(), SceauAction.Wither);
                } else {
                    sceauMap.put(target.getUniqueId(), SceauAction.AntiAbso);
                }
                player.sendMessage("§7Votre§c Sceau§7 à toucher§c "+target.getDisplayName()+"§7, il se déclenchera des que vous le/la frapperez");
                return true;
            } else {
                player.sendMessage("§cIl faut viser un joueur !");
            }
            return false;
        }
        @EventHandler
        private void onDamage(@NonNull final EntityDamageByEntityEvent event) {
            if (!(event.getDamager() instanceof Player))return;
            if (!(event.getEntity() instanceof Player))return;
            if (event.getDamager().getUniqueId().equals(getRole().getPlayer())) {
                if (this.sceauMap.containsKey(event.getEntity().getUniqueId())) {
                    if (((Player) event.getDamager()).getItemInHand() == null)return;
                    if (((Player) event.getDamager()).getItemInHand().getType().name().contains("SWORD")) {
                        final SceauAction sceauAction = this.sceauMap.get(event.getEntity().getUniqueId());
                        this.sceauMap.remove(event.getEntity().getUniqueId());
                        if (sceauAction.equals(SceauAction.Wither)) {
                            ((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20*12, 1, false, false), true);
                        } else {
                            this.absoLessList.add(event.getEntity().getUniqueId());
                        }
                        event.getEntity().sendMessage("§7Vous êtes sous l'effet du§c Sceau§7 de§e Danzo");
                        new SceauRunnable(getRole().getGameState(), event.getEntity().getUniqueId(), sceauAction, this);
                    }
                }
            }
        }
        @EventHandler(priority = EventPriority.HIGHEST)
        private void onEat(@NonNull final PlayerItemConsumeEvent event) {
            if (event.getItem().getType().equals(Material.GOLDEN_APPLE)) {
                if (!this.absoLessList.contains(event.getPlayer().getUniqueId()))return;
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    ((CraftPlayer)event.getPlayer()).getHandle().setAbsorptionHearts(0f);
                    event.getPlayer().sendMessage("§7Un§c Sceau§7 vous empêche d'avor de l'§eabsorbtion§7.");
                }, 1);
            }
        }
        private enum SceauAction {
            Wither,
            AntiAbso
        }
        private static class SceauRunnable extends BukkitRunnable {

            private final GameState gameState;
            private final UUID uuidTarget;
            private final SceauAction sceauAction;
            private final SceauPower sceauPower;
            private int timeLeft = 12;

            private SceauRunnable(GameState gameState, UUID uuidTarget, SceauAction sceauAction, SceauPower sceauPower) {
                this.gameState = gameState;
                this.uuidTarget = uuidTarget;
                this.sceauAction = sceauAction;
                this.sceauPower = sceauPower;
                runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                final Player target = Bukkit.getPlayer(this.uuidTarget);
                if (target == null)return;
                if (this.timeLeft <= 0) {
                    target.sendMessage("§7Vous n'êtes plus sous l'effet du§c Sceau§7 de§e Danzo§7.");
                    if (this.sceauAction.equals(SceauAction.AntiAbso)) {
                        this.sceauPower.absoLessList.remove(this.uuidTarget);
                    }
                    cancel();
                    return;
                }
                this.timeLeft--;
            }
        }
    }
    private static class IzanagiOffensif extends ItemPower {

        public IzanagiOffensif(@NonNull RoleBase role) {
            super("Izanagi Offensif", new Cooldown(60*3), new ItemBuilder(Material.NETHER_STAR).setName("§cIzanagi (Offensif)"), role,
                    "§7En visant un joueur et en échange de§c 1/2❤ permanent§7, vous permet de:",
                    "",
                    "§8 -§7 Vous téléportez à moins de §c 10 blocs§7",
                    "§8 -§7 Vous vous§d régénérez§c entièrement",
                    "§8 -§7 Vous gagnerez§e 2 pommes d'or");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                Player target = RayTrace.getTargetPlayer(player, 30, null);
                if (target == null) {
                    player.sendMessage("§cMerci de viser un joueur !");
                    return false;
                }
                final Location loc = Loc.getRandomLocationAroundPlayer(target, 10);
                player.sendMessage("§cIzanagi !");
                player.teleport(loc);
                getRole().setMaxHealth(getRole().getMaxHealth()-1.0);
                player.setMaxHealth(getRole().getMaxHealth());
                player.setHealth(player.getMaxHealth());
                getRole().giveItem(player, false, new ItemStack(Material.GOLDEN_APPLE, 2));
                player.updateInventory();
                return true;
            }
            return false;
        }
    }
    private static class FutonPower extends ItemPower {

        public FutonPower(@NonNull RoleBase role) {
            super("Futon", new Cooldown(60*2), new ItemBuilder(Material.FEATHER).setName("§aFuton"), role,
                    "§7Vous permet de vous propulsez la ou vous regardez");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final Vector vector = player.getEyeLocation().getDirection();
                vector.multiply(3);
                player.setVelocity(vector);
                return true;
            }
            return false;
        }
    }
    private static class FindersRunnable extends BukkitRunnable {

        private final DanzoV2 danzo;
        private final GamePlayer gamePlayer;
        private final GameState gameState;
        private int time = 0;
        public FindersRunnable(DanzoV2 role, GamePlayer gamePlayer) {
            this.danzo = role;
            this.gamePlayer = gamePlayer;
            this.gameState = role.getGameState();
            runTaskTimerAsynchronously(Main.getInstance(), 20, 20);
        }

        @Override
        public void run() {
            if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            Player owner = Bukkit.getPlayer(gamePlayer.getUuid());
            if (!gamePlayer.isAlive() || owner == null) {
                return;
            }
            this.time++;
            if (danzo.killUchiwa) {
                cancel();
                return;
            }
            if (this.time < 120) return;
            final List<Player> players = Loc.getNearbyPlayers(gamePlayer.getLastLocation(), 15);
            if (players.isEmpty()) {
                owner.sendMessage("§7Aucun membre de ce maudit clan des§4§l Uchiwas§7 n'est présent autours de vous (§c15 blocs§7)");
                return;
            }
            int nmbUchiwa = 0;
            for (Player player : players) {
                if (!danzo.getGameState().getGamePlayer().containsKey(player.getUniqueId())) continue;
                GamePlayer gm = danzo.getGameState().getGamePlayer().get(player.getUniqueId());
                if (!gm.isAlive())continue;
                if (gm.getRole() == null)continue;
                if (gm.getRole() instanceof IUchiwa) {
                    nmbUchiwa++;
                    owner.sendMessage("§7Il y a au moins un§4§l Uchiwa§7 autours de vous, son aura vous fait donné l'impression qu'il est§c "+((IUchiwa)gm.getRole()).getUchiwaType().getName());
                    break;
                }
            }
            if (nmbUchiwa == 0) {
                owner.sendMessage("§7Aucun membre de ce maudit clan des§4§l Uchiwas§7 n'est présent autours de vous (§c15 blocs§7)");
            }
            this.time = 0;
        }
    }
}