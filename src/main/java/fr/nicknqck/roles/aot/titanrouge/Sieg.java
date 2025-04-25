package fr.nicknqck.roles.aot.titanrouge;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.aot.builders.AotRoles;
import fr.nicknqck.roles.aot.builders.TitansRoles;
import fr.nicknqck.roles.aot.solo.TitanUltime;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.titans.TitanBase;
import fr.nicknqck.titans.impl.BestialV2;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Sieg extends TitansRoles {

    public Sieg(UUID player) {
        super(player);
    }

    @Override
    public String getName() {
        return "Sieg";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Sieg;
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .addCustomLine("§7Lorsque vous êtes transformé en titan vous donné l'effet§c Force I§7 aux joueurs transformés en titan étant dans votre camp")
                .getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        Main.getInstance().getTitanManager().addTitan(getPlayer(), new BestialV2(getGamePlayer()));
        addPower(new CriPower(this));
        addPower(new GiveStrengthPower(this));
        addKnowedPlayersWithRoles("§7Voici la liste des§c Titans Rouge§7 (§cAttention il y a un traitre dans cette liste ayant le rôle de§e Titan Ultime§7):",
                GrandTitan.class, PetitTitan.class, TitanDeviant.class,
                TitanUltime.class, TitanSouriant.class, Jelena.class);
    }
    private static class CriPower extends CommandPower {

        public CriPower(@NonNull RoleBase role) {
            super("/aot cri", "cri", null, role, CommandType.AOT);
            setMaxUse(1);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            final List<GamePlayer> titansRouge = new ArrayList<>();
            for (@NonNull final GamePlayer gamePlayer : getRole().getGameState().getGamePlayer().values()) {
                if (!gamePlayer.isAlive())continue;
                if (gamePlayer.getRole() == null)continue;
                if (gamePlayer.getRole().getTeam().equals(TeamList.Titan) || gamePlayer.getRole().getOriginTeam().equals(TeamList.Titan) || gamePlayer.getRole() instanceof TitanUltime) {
                    titansRouge.add(gamePlayer);
                }
            }
            if (!titansRouge.isEmpty()) {
                player.sendMessage("§7Votre cri retentira dans §c 10 secondes");
                new CriRunnable(getRole().getGameState(), titansRouge, getRole().getGamePlayer()).runTaskTimerAsynchronously(getPlugin(), 0, 20);
                return true;
            }
            return false;
        }
        private static class CriRunnable extends BukkitRunnable {

            private final GameState gameState;
            private final List<GamePlayer> titansRouge;
            private final GamePlayer gamePlayer;
            private int timeLeft;

            private CriRunnable(GameState gameState, List<GamePlayer> titansRouge, GamePlayer gamePlayer) {
                this.gameState = gameState;
                this.titansRouge = titansRouge;
                this.gamePlayer = gamePlayer;
                this.timeLeft = 10;
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (this.timeLeft <= 0) {
                    this.gamePlayer.sendMessage("§cVotre cri commence à ce faire entendre");
                    for (@NonNull final GamePlayer gp : this.gameState.getGamePlayer().values()) {
                        final Player player = Bukkit.getPlayer(gp.getUuid());
                        if (player == null)continue;
                        player.sendMessage("");
                        player.sendMessage("§c§lSieg vient de crier !!!");
                        if (this.titansRouge.contains(gp)) {
                            if (!gp.isAlive())continue;
                            player.sendMessage("§c§lAu vue du son il doit sûrement être en§c x:"+
                                    this.gamePlayer.getLastLocation().getBlockX()+"§7,§c y:"+
                                    this.gamePlayer.getLastLocation().getBlockY()+"§7,§c z:"+
                                    this.gamePlayer.getLastLocation().getBlockZ()+"§7.");
                            continue;
                        }
                        player.sendMessage("");
                        player.playSound(player.getLocation(), "goldenuhc.bestialcri", 1, 8);
                    }
                    cancel();
                    return;
                }
                this.timeLeft--;
            }
        }
    }
    private static class GiveStrengthPower extends Power {

        public GiveStrengthPower(@NonNull RoleBase role) {
            super("Don de force (Bestial)", null, role);
            new GiveForceRunnable(this).runTaskTimerAsynchronously(getPlugin(), 0, 20);
            setShowInDesc(false);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            return true;
        }
        private static class GiveForceRunnable extends BukkitRunnable {

            private final GameState gameState;
            private final GamePlayer gamePlayer;
            private final GiveStrengthPower power;

            private GiveForceRunnable(@NonNull final GiveStrengthPower power) {
                this.gameState = power.getRole().getGameState();
                this.gamePlayer = power.getRole().getGamePlayer();
                this.power = power;
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (!this.gamePlayer.isAlive() || !Main.getInstance().getTitanManager().hasTitan(this.gamePlayer.getUuid())) {
                    return;
                }
                if (!power.checkUse(Bukkit.getPlayer(this.gamePlayer.getUuid()), new HashMap<>())) {
                    return;
                }
                @NonNull TitanBase titan = Main.getInstance().getTitanManager().getTitan(this.gamePlayer.getUuid());
                if (!titan.isTransformed())return;
                for (@NonNull final GamePlayer gamePlayer : Loc.getNearbyGamePlayers(this.gamePlayer.getLastLocation(), 25)) {
                    final Player player = Bukkit.getPlayer(gamePlayer.getUuid());
                    if (player == null)continue;
                    if (player.getUniqueId().equals(this.gamePlayer.getUuid()))continue;
                    if (gamePlayer.getRole() == null)continue;
                    if (!(gamePlayer.getRole() instanceof AotRoles))continue;
                    if (!((AotRoles) gamePlayer.getRole()).isTransformedinTitan)continue;
                    if (!gamePlayer.getRole().getOriginTeam().equals(TeamList.Titan))continue;
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false)));
                }
            }
        }
    }
}