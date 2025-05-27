package fr.nicknqck.roles.ds.demons;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.slayers.NezukoV2;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class FurutoV2 extends DemonInferieurRole {

    public FurutoV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull DemonType getRank() {
        return DemonType.DEMON;
    }

    @Override
    public String getName() {
        return "Furuto";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Furuto;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Demon;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new FlutePower(this), true);
        addPower(new ChiensPower(this));
        super.RoleGiven(gameState);
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .getText();
    }

    private static class FlutePower extends ItemPower implements Listener {

        private static final List<UUID> bonusForceList = new ArrayList<>();
        private static final List<UUID> bonusSpeedList = new ArrayList<>();
        private static final List<UUID> bonusResistanceList = new ArrayList<>();
        private static final List<UUID> bonusWeaknessList = new ArrayList<>();

        public FlutePower(@NonNull RoleBase role) {
            super("Flûte", new Cooldown(60*3), new ItemBuilder(Material.NETHER_STAR).setName("§cFlûte"), role,
                    "§7Vous permet de donner un léger bonus pendant§c 15 secondes§7 à toute les personnes autours de vous en fonction de leurs camp:",
                    "",
                    "§f Camp§c Démon§7 (et§a Nezuko§7): Les joueurs auront une chance sur trois d'obtenir§c 10%§7 de§e Speed§7 ou§c 10%§7 de§c Force§7 ou§c 10%§7 de §9Resistance",
                    "",
                    "§fAutre camps§7: Les joueurs auront 1 chance sur deux d'obtenir Slowness 1 ou§c 15%§7 de dégât en moins");
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                for (final GamePlayer gamePlayer : Loc.getNearbyGamePlayers(player.getLocation(), 50)) {
                    if (gamePlayer.getRole() == null)continue;
                    final Player p = Bukkit.getPlayer(gamePlayer.getUuid());
                    final Bonus bonus;
                    if (gamePlayer.getRole().getTeam().equals(TeamList.Demon) || gamePlayer.getRole() instanceof NezukoV2 || gamePlayer.getRole() instanceof DemonsRoles) {
                        bonus = getBonus(RandomUtils.getRandomInt(0, 2));
                    } else {
                        bonus = getBonus(RandomUtils.getRandomInt(3, 4));
                    }
                    bonus.consumer.accept(p);
                    new BonusRunnable(getRole().getGameState(), bonus, gamePlayer, p, this);
                    player.sendMessage("§7Vous avez toucher§c "+p.getName()+"§7 avec votre§c Flûte§7.");
                    p.sendMessage("§7Vous avez entendu la§c Flûte§7 de§c Furuto");
                }
                return true;
            }
            return false;
        }
        private Bonus getBonus(int random) {
            for (final Bonus bonus : Bonus.values()) {
                if (bonus.rPoint == random) {
                    return bonus;
                }
            }
            return Bonus.FORCE;
        }
        private enum Bonus {
            FORCE(0, "§cForce", player -> bonusForceList.add(player.getUniqueId())),
            SPEED(1, "§eSpeed", player -> player.setWalkSpeed(player.getWalkSpeed()+0.02f)),
            RESISTANCE(2, "§9Résistance", player -> bonusResistanceList.add(player.getUniqueId())),
            WEAKNESS(3, "§8Weakness", player -> bonusWeaknessList.add(player.getUniqueId())),
            SLOWNESS(4, "§8Slowness", player -> player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*15, 0, false, false), true));

            private final int rPoint;
            private final String goodString;
            private final Consumer<Player> consumer;

            Bonus(int rPoint, String goodString, Consumer<Player> consumer) {
                this.rPoint = rPoint;
                this.goodString = goodString;
                this.consumer = consumer;
            }
        }
        @EventHandler
        private void onDamage(EntityDamageEvent event) {
            if (bonusForceList.contains(event.getEntity().getUniqueId())) {
                event.setDamage(event.getDamage()*1.1);
            }
            if (bonusResistanceList.contains(event.getEntity().getUniqueId())) {
                event.setDamage(event.getDamage()*0.9);
            }
        }
        @EventHandler
        private void onDamageAgainst(EntityDamageByEntityEvent event) {
            if (bonusWeaknessList.contains(event.getDamager().getUniqueId())) {
                event.setDamage(event.getDamage()*0.85);
            }
        }
        private static class BonusRunnable extends BukkitRunnable {

            private final GameState gameState;
            private final Bonus bonus;
            private final GamePlayer gamePlayer;
            private final Player player;
            private final FlutePower flutePower;
            private int timeLeft;

            private BonusRunnable(GameState gameState, Bonus bonus, GamePlayer gamePlayer, Player player, FlutePower flutePower) {
                this.gameState = gameState;
                this.bonus = bonus;
                this.gamePlayer = gamePlayer;
                this.player = player;
                this.flutePower = flutePower;
                this.timeLeft = 15;
                runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (this.timeLeft <= 0) {
                    cancel();
                    this.gamePlayer.getActionBarManager().removeInActionBar("furuto.bonus");
                    this.flutePower.removeFromList(player.getUniqueId());
                    return;
                }
                this.timeLeft--;
                gamePlayer.getActionBarManager().addToActionBar("furuto.bonus", "§cFuruto§7 vous à donné un bonus ("+this.bonus.goodString+"§7), plus que§c "+this.timeLeft+" secondes");
            }
        }

        private void removeFromList(UUID uniqueId) {
            if (bonusSpeedList.contains(uniqueId)) {
                final Player player = Bukkit.getPlayer(uniqueId);
                if (player != null) {
                    player.setWalkSpeed(player.getWalkSpeed()-0.02f);
                }
            }
            bonusForceList.remove(uniqueId);
            bonusSpeedList.remove(uniqueId);
            bonusResistanceList.remove(uniqueId);
            bonusWeaknessList.remove(uniqueId);
        }
    }
    private static class ChiensPower extends CommandPower {

        public ChiensPower(@NonNull RoleBase role) {
            super("/ds chiens", "chiens", new Cooldown(60*7), role, CommandType.DS,
                    "§7Fait apparaitre§c 3 chiens§7 ayant chacun les effets§c Force I§7 (vanilla) et§e Speed III§7, ils auront également§c 25❤ permanents");
            setMaxUse(2);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            for (int i = 1; i <= 3; i++) {
                Wolf wolf = (Wolf) player.getWorld().spawnEntity(player.getLocation(), EntityType.WOLF);
                wolf.setAdult();
                wolf.setCanPickupItems(false);
                wolf.setCustomName("§cLoup de Furuto "+i);
                wolf.setCustomNameVisible(true);
                wolf.setTamed(true);
                wolf.setOwner(player);
                wolf.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false));
                wolf.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2, false, false));
                wolf.setSitting(false);
                wolf.setMaxHealth(50);
                wolf.setHealth(wolf.getMaxHealth());
                new WolfRunnable(wolf, player).runTaskTimerAsynchronously(Main.getInstance(), 20, 20);
            }
            return true;
        }
        private static class WolfRunnable extends BukkitRunnable {

            private final Wolf wolf;
            private final Player player;

            private WolfRunnable(Wolf wolf, Player player) {
                this.wolf = wolf;
                this.player = player;
            }

            @Override
            public void run() {
                if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (player == null || wolf == null) {
                    cancel();
                    return;
                }
                if (wolf.isDead() || player.isDead()) {
                    cancel();
                    return;
                }
                if (player.getWorld().equals(this.wolf.getWorld())) {
                    if (player.getLocation().distance(this.wolf.getLocation()) > 15) {
                        this.wolf.teleport(player);
                    }
                }
            }
        }
    }
}