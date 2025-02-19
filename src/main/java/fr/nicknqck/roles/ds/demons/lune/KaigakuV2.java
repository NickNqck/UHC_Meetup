package fr.nicknqck.roles.ds.demons.lune;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.DayEvent;
import fr.nicknqck.events.custom.NightEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.slayers.ZenItsuV2;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class KaigakuV2 extends DemonsRoles {
    public KaigakuV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull DemonType getRank() {
        return DemonType.SUPERIEUR;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Kaigaku§7 (§6V2§7)§r";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Kaigaku;
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Demon;
    }

    @Override
    public void resetCooldown() {}

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public void RoleGiven(GameState gameState) {
        setCanuseblade(true);
        addPower(new ElectroKinesiePower(this), true);
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.NIGHT);
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this).addEffects(getEffects()).setPowers(getPowers()).addParticularites(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
                        new TextComponent("§7Pour charger votre§c charge§7 d'§cÉlectrokinésie§7 vous pourrez faire ces choses:\n\n" +
                                "§7 -§a Rester en vie pendant§c 1 minute§7:§a +1%\n" +
                                "§7 -§c Infliger§7/§cRecevoir§7 un§c coup d'épée§7:§a +1%\n" +
                                "§7 -§a Manger une§e pomme d'or§7:§a +1%\n" +
                                "§7 -§a Parcourir§c 150 blocs§7:§a +5%\n" +
                                "§7 -§c Tuer§7 un§c joueur§7:§a +10%")
                }),
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
                        new TextComponent("§7Si vous tué§a Zen'Itsu§7 votre§c Électrokinésie§7 aura des changements:\n\n" +
                                "§7 - Votre§c clique droit§7 fera§c toujours§7 les mêmes effet que s'il est à pleine charge\n" +
                                "§7 - Le dernier palier de charge aura un autre pouvoir qui s'activera en essayant de§c drop§7 votre§c item§7:\n\n" +
                                "§7     → Créera une ligne d'§eéclair§7 qui infligera§c 2❤§7 à tout les joueurs qui seront touché")
                })
        ).getText();
    }

    private static class ElectroKinesiePower extends ItemPower implements Listener {

        private int charge = 0;
        private double deplacement = 0.0;
        private final TPPower tpPower;
        private final FoudreZonePower foudrePower;
        private final LigneEclairPower lignePower;
        private boolean firstGive = false;
        private boolean secondGive = false;
        private boolean killZenItsu = false;

        protected ElectroKinesiePower(@NonNull RoleBase role) {
            super("Électrokinésie", new Cooldown(30), new ItemBuilder(Material.BLAZE_ROD).setName("§eÉlectrokinésie"), role);
            this.tpPower = new TPPower(role);
            this.foudrePower = new FoudreZonePower(this);
            this.lignePower = new LigneEclairPower(role);
            role.addPower(tpPower);
            role.addPower(foudrePower);
            EventUtils.registerRoleEvent(this);
            getRole().getGamePlayer().getActionBarManager().addToActionBar("kaigaku.electro", "§bCharge actuel: "+charge+"%");
            new LifeRunnable(role.getGamePlayer(), this, role.getGameState());
            getShowCdRunnable().setCustomText(true);
            getShowCdRunnable().setCustomTexte("");
            setDescriptions(getNewDescription());
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                if (event.getAction().name().contains("LEFT")) {
                    if (this.charge < 20) {
                        player.sendMessage("§cVous n'avez pas asser d'§eélectrécité§c dans votre corp pour cette technique...");
                        return false;
                    }
                    if (this.tpPower.checkUse(player, map)) {
                        addCharge(-20);
                        return true;
                    }
                } else if (event.getAction().name().contains("RIGHT")) {
                    if (this.charge < 60) {
                        player.sendMessage("§cVous n'avez pas asser d'§eélectrécité§c dans votre corp pour cette technique...");
                        return false;
                    }
                    return this.foudrePower.checkUse(player, map);
                }
            } else if (getInteractType().equals(InteractType.DROP_ITEM)) {
                if (this.killZenItsu) {
                    if (this.charge >= 100) {
                        if (this.lignePower.checkUse(player, map)) {
                            addCharge(-100);
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        private void addCharge(int add) {
            this.charge = Math.min(100, this.charge+add);
            if (this.charge >= 40) {
                if (getRole().getGameState().isNightTime()) {
                    if (!firstGive) {
                        final Player owner = Bukkit.getPlayer(getRole().getPlayer());
                        if (owner != null) {
                            getRole().addSpeedAtInt(owner, 10);
                            firstGive = true;
                        }
                    }
                    if (!secondGive) {
                        if (this.charge >= 80) {
                            final Player owner = Bukkit.getPlayer(getRole().getPlayer());
                            if (owner != null) {
                                getRole().addSpeedAtInt(owner, 10);
                                secondGive = true;
                            }
                        }
                    }
                }
            } else {
                removeSpeed();
            }
            getRole().getGamePlayer().getActionBarManager().updateActionBar("kaigaku.electro", "§bCharge actuel: "+charge+"%");
        }
        @EventHandler
        private void EntityDamageByEntityEvent(final EntityDamageByEntityEvent event) {
            if (!(event.getDamager() instanceof Player))return;
            if (!(event.getEntity() instanceof Player))return;
            if (((Player) event.getDamager()).getItemInHand() == null)return;
            if (((Player) event.getEntity()).getItemInHand() == null)return;
            if (event.getDamager().getUniqueId().equals(getRole().getPlayer())) {
                if (((Player) event.getDamager()).getItemInHand().getType().name().contains("SWORD"))return;
                addCharge(1);
            } else if (event.getEntity().getUniqueId().equals(getRole().getPlayer())) {
                if (((Player) event.getEntity()).getItemInHand().getType().name().contains("SWORD"))return;
                addCharge(1);
            }
        }
        @EventHandler
        private void onEat(final PlayerItemConsumeEvent event) {
            if (event.getPlayer().getUniqueId().equals(getRole().getPlayer())) {
                if (event.getItem().getType().equals(Material.GOLDEN_APPLE)) {
                    addCharge(1);
                }
            }
        }
        @EventHandler
        private void UHCKillEvent(final UHCPlayerKillEvent event) {
            if (event.getKiller().getUniqueId().equals(getRole().getPlayer())) {
                addCharge(10);
            }
        }
        @EventHandler
        private void PlayerMooveEvent(final PlayerMoveEvent event) {
            if (event.getPlayer().getUniqueId().equals(getRole().getPlayer())) {
                if (this.deplacement >= 50.0) {
                    addCharge(5);
                    this.deplacement = 0.0;
                    return;
                }
                this.deplacement = Math.min(150.0, this.deplacement+event.getFrom().distance(event.getTo()));
            }
        }
        @EventHandler
        private void DayEvent(final DayEvent event) {
            removeSpeed();
        }
        @EventHandler
        private void NightEvent(final NightEvent event) {
            if (this.charge < 40)return;
            final Player owner = Bukkit.getPlayer(getRole().getPlayer());
            if (owner != null) {
                if (!firstGive) {
                    getRole().addSpeedAtInt(owner, 10);
                    firstGive = true;
                }
                if (this.charge < 80)return;
                if (!secondGive) {
                    getRole().addSpeedAtInt(owner, 10);
                    secondGive = true;
                }
            }
        }
        @Override
        public void tryUpdateActionBar() {
            getShowCdRunnable().setCustomTexte(this.charge >= 20 ? "§fClique gauche est "+
                    (this.tpPower.getCooldown().isInCooldown() ?
                            "en cooldown (§c"+ StringUtils.secondsTowardsBeautiful(this.tpPower.getCooldown().getCooldownRemaining())+"§f)" :
                            "§cutilisable")+ (this.charge >= 60 ? "§7 |§f Clique droit est "+
                    (this.foudrePower.getCooldown().isInCooldown() ?
                            "en cooldown (§c"+StringUtils.secondsTowardsBeautiful(this.foudrePower.getCooldown().getCooldownRemaining())+"§f)" :
                            "§cutilisable") : "")
                    : "§cAucun Pouvoir n'est utilisable actuellement ");
        }
        private void removeSpeed() {
            final Player owner = Bukkit.getPlayer(getRole().getPlayer());
            if (owner != null) {
                if (firstGive) {
                    getRole().addSpeedAtInt(owner, -10);
                    firstGive = false;
                }
                if (secondGive) {
                    getRole().addSpeedAtInt(owner, -10);
                    secondGive = false;
                }
            }
        }
        private String[] getNewDescription() {
            return new String[] {
                    "§7Vous permet d'utiliser un§c pourcentage§7 de§c charge§7, vous aurez plusieurs bonus en fonction de votre§c chargement§7 actuel",
                    "§7pour la remplir voir votre§c particularité 1§7:",
                    "",
                    "§7 -§c 0%§7: Aucun bonus",
                    "§7 -§c 20%§7: (§fClique gauche§7) Vous permettra de vous téléportez derrière le joueur viser en lui infligeant§c 1❤§7 de dégat",
                    "§7 -§6 40%§7: Vous irez§c 10%§7 plus§c vite§7 pendant la§c nuit",
                    "§7 -§6 60%§7: (§fClique droit§7) Vous§c infligerez 2❤§7 de§c dégats§7 "+(killZenItsu ? "ainsi que l'effet§c Slowness I§7 et§6 enflammera§7" : "à tout")+" les joueurs proche (§c25 blocs§7)",
                    "§7 -§a 80%§7: Vous irez§c 20%§7 plus§c vite§7 pendant la§c nuit",
                    "§7 -§a 100%§7: "+(killZenItsu ?
                            "(§fDrop§7) Vous permet de lancé une§c ligne§7 d'§eéclair§7 infligeant§c 2❤§7 de§c dégats§7 au joueurs touché" :
                            "Votre clique droit (en plus du bonus précédent) infligera l'effet§c Slowness I§7 et§6 enflammera§7 les§c joueurs§7 touchés pendant§c 10 secondes")
            };
        }
        @EventHandler
        private void onUHCKill(final UHCPlayerKillEvent event) {
            if (event.getGamePlayerKiller() == null)return;
            if (!event.getKiller().getUniqueId().equals(getRole().getPlayer()))return;
            if (this.killZenItsu)return;
            if (!event.getGameState().hasRoleNull(event.getVictim().getUniqueId())) {
                final RoleBase role = event.getGameState().getGamePlayer().get(event.getVictim().getUniqueId()).getRole();
                if (role instanceof ZenItsuV2) {
                    event.getKiller().sendMessage("Vous avez§c tué§a Zen'Itsu§f vous obtenez donc une amélioration de votre§c "+getName()+"§7 (voir description)");
                    this.killZenItsu = true;
                    getRole().addPower(this.lignePower);
                    setDescriptions(getNewDescription());
                }
            }
        }
        private static class LifeRunnable extends BukkitRunnable {

            private final GamePlayer gamePlayer;
            private final ElectroKinesiePower power;
            private final GameState gameState;
            private int timeLeft;

            private LifeRunnable(GamePlayer gamePlayer, ElectroKinesiePower power, GameState gameState) {
                this.gamePlayer = gamePlayer;
                this.power = power;
                this.gameState = gameState;
                this.timeLeft = 60;
                runTaskTimerAsynchronously(power.getPlugin(), 0, 20);
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (!gamePlayer.isAlive())return;
                if (this.timeLeft <= 0) {
                    this.timeLeft = 60;
                    power.addCharge(1);
                }
                this.timeLeft = Math.max(0, this.timeLeft-1);

            }
        }
        private static class TPPower extends Power {

            public TPPower(@NonNull RoleBase role) {
                super("Électrokinésie (Clique gauche)", new Cooldown(5), role);
                setShowInDesc(false);
                setSendCooldown(false);
            }

            @Override
            public boolean onUse(Player player, Map<String, Object> map) {
                final Player target = RayTrace.getTargetPlayer(player, 30.0, null);
                if (target == null) {
                    player.sendMessage("§cIl faut viser un joueur");
                    return false;
                }
                final Location loc = target.getLocation();
                loc.setX(loc.getX()+Math.cos(Math.toRadians(-target.getEyeLocation().getYaw()+90)));
                loc.setZ(loc.getZ()+Math.sin(Math.toRadians(target.getEyeLocation().getYaw()-90)));
                loc.setPitch(0f);
                loc.getWorld().strikeLightningEffect(loc);
                player.teleport(loc);
                if (target.getHealth()-2.0 <= 0.0) {
                    target.setHealth(0.1);
                } else {
                    target.setHealth(target.getHealth()-2.0);
                }
                target.sendMessage("§cVous sentez un§e éclair§c parcourir votre corp");
                player.sendMessage("§eSoufle de la foudre: Quatrième Mouvement !");
                return true;
            }
        }
        private static class FoudreZonePower extends Power {

            private final ElectroKinesiePower power;

            public FoudreZonePower(@NonNull ElectroKinesiePower power) {
                super("Électrokinésie (Clique droit)", new Cooldown(5), power.getRole());
                this.power = power;
                setShowInDesc(false);
                setSendCooldown(false);
            }

            @Override
            public boolean onUse(Player player, Map<String, Object> map) {
                final List<Player> aroundPlayers = Loc.getNearbyPlayersExcept(player, 25);
                if (aroundPlayers.isEmpty()) {
                    player.sendMessage("§cIl n'y a pas assez de joueurs autours de vous pour cette technique !");
                    return false;
                }
                for (final Player target : aroundPlayers) {
                    target.getWorld().strikeLightningEffect(target.getLocation());
                    target.setHealth(Math.max(0.1, target.getHealth()-4.0));
                    if (this.power.charge == 100) {
                        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*10, 0, false, false), true);
                        target.setFireTicks(20*10);
                    }
                    target.sendMessage("§cKaigaku§7 (V2§7)§f vous à§e foudroyez");
                }
                if (this.power.charge == 100) {
                    this.power.addCharge(-100);
                } else if (this.power.charge >= 60) {
                    this.power.addCharge(-60);
                }
                return true;
            }
        }
        private static class LigneEclairPower extends Power {

            public LigneEclairPower(@NonNull RoleBase role) {
                super("Électrokinésie (drop)", new Cooldown(5), role);
                setShowInDesc(false);
                setSendCooldown(false);
            }

            @Override
            public boolean onUse(Player player, Map<String, Object> map) {
                new LigneRunnable(getRole().getGameState(), player);
                return true;
            }
            private static class LigneRunnable extends BukkitRunnable {

                private final GameState gameState;
                private final Location location;
                private final List<UUID> damaged;
                private final UUID user;
                private int foudreLeft;
                private final Vector initDirection;

                private LigneRunnable(GameState gameState, final Player player) {
                    this.gameState = gameState;
                    this.location = player.getEyeLocation();
                    this.user = player.getUniqueId();
                    this.foudreLeft = 10;
                    this.damaged = new ArrayList<>();
                    this.initDirection = this.location.getDirection().normalize().multiply(2);
                    runTaskTimer(Main.getInstance(), 0, 15);
                }

                @Override
                public void run() {
                    if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                        cancel();
                        return;
                    }
                    location.add(initDirection);
                    location.setY(location.getWorld().getHighestBlockYAt(location));
                    location.getWorld().strikeLightningEffect(location);
                    for (int y = 50; y < 128; y++) {
                        final Location loc = new Location(location.getWorld(), location.getBlockX(), y, location.getBlockZ());
                        final List<Player> players = Loc.getNearbyPlayers(loc, 5.0);
                        if (players.isEmpty())continue;
                        for (final Player player : players) {
                            if (player.getUniqueId().equals(this.user))continue;
                            if (!this.damaged.contains(player.getUniqueId())) {
                                if (player.getHealth()-4.0 <= 0.0) {
                                    player.setHealth(0.1);
                                } else {
                                    player.setHealth(player.getHealth()-4.0);
                                }
                                this.damaged.add(player.getUniqueId());
                            }
                        }
                    }
                    this.damaged.clear();
                    if (foudreLeft <= 0) {
                        cancel();
                        return;
                    }
                    foudreLeft--;
                }
            }
        }
    }
}
