package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class KonohamaruV2 extends ShinobiRoles implements Listener {

    private boolean knowNaruto = false;

    public KonohamaruV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.MOYENNE;
    }

    @Override
    public String getName() {
        return "Konohamaru";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Konohamaru;
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .addCustomLine("§7Vous possédez une barre de point invisible augmentant quand vous êtes proche de§a Naruto§7, à chaque palier vous obtiendrez quelque chose: \n\n" +
                        "§8 - §a1000 points§7: Vous obtiendrez l'effet§c Force I§7 proche de§a Naruto§7.\n\n" +
                        "§8 -§a 2000 points§7: Vous connaitrez le joueur possédant le rôle de§a Naruto§7, à partir de la à sa mort vous obtiendrez le§a Rasengan§7.")
                .getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        new NarutoRunnable(this, getGameState());
        addPower(new NueArdente(this), true);
        setChakraType(Chakras.KATON);
        super.RoleGiven(gameState);
    }
    @EventHandler
    private void onDeath(@NonNull final UHCDeathEvent event) {
        if (event.getRole() instanceof NarutoV2) {
            if (knowNaruto) {
                addPower(new Rasengan(this), true);
            }
        }
    }
    private static class Rasengan extends ItemPower {

        private Rasengan(@NonNull RoleBase role) {
            super("Rasengan", new Cooldown(120), new ItemBuilder(Material.NETHER_STAR).setName("§aRasengan"), role,
                    "§7En frappant un joueur, vous permet§a repousse le joueur§7 en lui infligeant§c 2❤§7 de§c dégâts");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.ATTACK_ENTITY)) {
                @NonNull final UHCPlayerBattleEvent uhcEvent = (UHCPlayerBattleEvent) map.get("event");
                @NonNull final EntityDamageByEntityEvent event = uhcEvent.getOriginEvent();
                if (!(event.getEntity() instanceof Player))return false;
                ((Player) event.getEntity()).setHealth(Math.max(1.0, ((Player) event.getEntity()).getHealth()-4.0));
                MathUtil.sendParticle(EnumParticle.EXPLOSION_LARGE, event.getEntity().getLocation());
                Location loc = event.getEntity().getLocation().clone();
                loc.setX(loc.getX()+Math.cos(Math.toRadians(-(((Player)event.getEntity())).getEyeLocation().getYaw()+90)));
                loc.setZ(loc.getZ()+Math.sin(Math.toRadians(((Player)event.getEntity()).getEyeLocation().getYaw()-90)));
                loc.setPitch(0);
                event.getEntity().setVelocity(loc.getDirection().multiply(3.0));
                player.sendMessage("§aRASENGAN !");
                event.getEntity().sendMessage("§7Vous avez été toucher par un§a Rasengan");
                event.setCancelled(true);
                return true;
            }
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (((PlayerInteractEvent) map.get("event")).getAction().name().contains("RIGHT")){
                    player.sendMessage("§7Il faut frapper un joueur pour déclencher le§a Rasengan");
                }
                return false;
            }
            return false;
        }
    }
    private static class NueArdente extends ItemPower {

        public NueArdente(@NonNull RoleBase role) {
            super("Nuée Ardente", new Cooldown(60*3), new ItemBuilder(Material.SULPHUR).setName("§aNuée Ardente"), role,
                    "§7Donne l'effet§c Blindness I§7 pendant§c 20 secondes§7 à tout les joueurs proche.");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final List<Player> playerList = new ArrayList<>(Loc.getNearbyPlayersExcept(player, 25));
                if (playerList.isEmpty())return false;
                player.sendMessage("§aNuées Ardentes!");
                for (Player p : playerList) {
                    if (player.canSee(p)) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*20, 0, false, false), true);
                        p.sendMessage("Vous venez d'être touche par la §8Nuées Ardentes §fde §aKonohamaru");
                        player.sendMessage("§7§l"+p.getName()+"§7 à été touchée");
                    }
                }
                return true;
            }
            return false;
        }
    }
    private static class NarutoRunnable extends BukkitRunnable {

        private final KonohamaruV2 konohamaru;
        private final GameState gameState;
        private int point = 0;

        private NarutoRunnable(KonohamaruV2 konohamaru, GameState gameState) {
            this.konohamaru = konohamaru;
            this.gameState = gameState;
            runTaskTimerAsynchronously(Main.getInstance(), 20, 20);
        }

        @Override
        public void run() {
            if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (this.point >= 2000 && !this.konohamaru.knowNaruto) {
                this.konohamaru.knowNaruto = true;
                this.konohamaru.addKnowedRole(NarutoV2.class);
                EventUtils.registerRoleEvent(this.konohamaru);
            }
            final List<GamePlayer> gamePlayerList = Loc.getNearbyGamePlayers(this.konohamaru.getGamePlayer().getLastLocation(), 25);
            if (gamePlayerList.isEmpty())return;
            for (final GamePlayer gamePlayer : gamePlayerList) {
                if (gamePlayer.getRole() == null)continue;
                if (gamePlayer.getRole() instanceof NarutoV2) {
                    final double distance = gamePlayer.getLastLocation().distance(this.konohamaru.getGamePlayer().getLastLocation());
                    if (distance <= 10) {
                        this.point+=5;
                    }
                    if (distance <= 20) {
                        this.point+=5;
                    }
                    this.point++;
                    if (point < 1000)continue;
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> this.konohamaru.givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.NOW));
                }
            }
        }
    }
}