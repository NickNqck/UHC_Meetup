package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.EChakras;
import fr.nicknqck.enums.EffectWhen;
import fr.nicknqck.enums.Intelligence;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.interfaces.IRoles;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.builders.HShinobiRoles;
import fr.nicknqck.roles.ns.orochimaru.edov2.OrochimaruV2;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Hiruzen extends HShinobiRoles implements Listener {

    public Hiruzen(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.MOYENNE;
    }

    @Override
    public EChakras[] getChakrasCanHave() {
        return EChakras.values();
    }

    @Override
    public String getName() {
        return "Hiruzen";
    }

    @Override
    public @NonNull IRoles<?> getRoles() {
        return Roles.Hiruzen;
    }

    @Override
    public @NonNull TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        super.RoleGiven(gameState);
        addPower(new Enma(this), true);
        addPower(new Balsamine(this), true);
        addPower(new CouleDeBoue(this), true);
        givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 500, 0, false, false), EffectWhen.PERMANENT);
        EventUtils.registerRoleEvent(this);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onDamage(@NonNull final EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player))return;
        if (!(event.getDamager() instanceof Player))return;
        if (!event.getDamager().getUniqueId().equals(this.getPlayer()))return;
        final GamePlayer gamePlayer = GamePlayer.of(event.getEntity().getUniqueId());
        if (gamePlayer == null)return;
        if (!gamePlayer.check())return;
        if (gamePlayer.getRole() instanceof OrochimaruV2) {
            if (RandomUtils.getOwnRandomProbability(50.0)) {
                event.setDamage(event.getDamage()*1.2);
            }
        }
    }
    private static final class Enma extends ItemPower {

        public Enma(@NonNull RoleBase role) {
            super("§aEnma", null, new ItemBuilder(Material.DIAMOND_SWORD).setName("§aEnma"), role, "§7Qui à besoin d'§aami§7 quand§a Enma§7 est la pour nous.");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            return true;
        }
    }
    private static final class Balsamine extends ItemPower implements Listener {

        private boolean active = false;

        public Balsamine(@NonNull RoleBase role) {
            super("§cKaton: Balsamine", new Cooldown(60*5), new ItemBuilder(Material.NETHER_STAR).setName("§cKaton: Balsamine"), role,
                    "§7Suite à l'§aactivation§7 et ce pendant§c 10 secondes§7,",
                    "§7tout les joueurs qui vous frappe se verront§6 enflammer§7."
            );
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                this.startBalsamine();
                return true;
            }
            return false;
        }

        private void startBalsamine() {
            if (active) return;
            active = true;

            final Player owner = Bukkit.getPlayer(getRole().getPlayer());
            if (owner != null) {
                owner.sendMessage("§cKaton: Balsamine §7est maintenant§a actif§7.");
            }

            // Désactiver après 10 secondes
            Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
                active = false;
                final Player p = Bukkit.getPlayer(getRole().getPlayer());
                if (p != null && GameState.inGame()) {
                    p.sendMessage("§cKaton: Balsamine §7est maintenant§c inactif§7.");
                }
            }, 20L * 10);
        }

        @EventHandler
        private void onDamage(EntityDamageByEntityEvent event) {
            if (!active) return;

            // La victime doit être le propriétaire du pouvoir
            if (!(event.getEntity() instanceof Player)) return;
            if (!event.getEntity().getUniqueId().equals(getRole().getPlayer())) return;

            // L'attaquant doit être un joueur
            if (!(event.getDamager() instanceof Player)) return;
            final Player attacker = (Player) event.getDamager();

            attacker.setFireTicks(20 * 10);
            attacker.sendMessage("§7Vous avez été§6 enflammé§7 par§c Katon: Balsamine§7.");
        }
    }
    private static final class CouleDeBoue extends ItemPower {

        public CouleDeBoue(@NonNull RoleBase role) {
            super("§6Doton: coulé de boue", new Cooldown(60*8), new ItemBuilder(Material.NETHER_STAR).setName("§6Doton: Coulé de boue"), role,
                    "§7Crée une§c zone fixe§7 de§c 15x15§7, tout les joueurs à l'intérieur sauf vous",
                    "§7obtiendront l'effet§c Slowness II§7.",
                    "",
                    "§7La§c zone§7 reste pour une durée de§c 1 minute§7."
            );
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                player.sendMessage("§7Vous avez créer une grande§6 coulé de boue§7.");
                new CouleDeBoueRunnable(this, player.getLocation()).runTaskTimerAsynchronously(getPlugin(), 0,20);
                return true;
            }
            return false;
        }
        private static final class CouleDeBoueRunnable extends BukkitRunnable {

            private final CouleDeBoue couleDeBoue;
            private final Location centerLocation;
            private int timeLeft = 60;

            private CouleDeBoueRunnable(CouleDeBoue couleDeBoue, Location centerLocation) {
                this.couleDeBoue = couleDeBoue;
                this.centerLocation = centerLocation;
            }

            @Override
            public void run() {
                if (!GameState.inGame()) {
                    cancel();
                    return;
                }
                final List<Location> locations = new ArrayList<>(MathUtil.getCircle(this.centerLocation, 15.0));
                for (Location location : locations) {
                    MathUtil.sendParticle(EnumParticle.BARRIER, location);
                }
                if (!this.couleDeBoue.getRole().getGamePlayer().check() || this.timeLeft <= 0) {
                    this.couleDeBoue.getRole().getGamePlayer().getActionBarManager().removeInActionBar("hiruzen.boue");
                    cancel();
                    return;
                }
                @NonNull
                final Player owner = this.couleDeBoue.getRole().getGamePlayer().getPlayer();
                this.couleDeBoue.getRole().getGamePlayer().getActionBarManager().updateActionBar("hiruzen.boue", "§bDoton: coulé de boue:§c "+ StringUtils.secondsTowardsBeautiful(this.timeLeft));
                final List<Player> list = new ArrayList<>(owner.getWorld().getPlayers());
                for (Player player : list) {
                    if (player.getUniqueId().equals(owner.getUniqueId())) continue;
                    if (!player.getGameMode().equals(GameMode.SURVIVAL)) continue;
                    final GamePlayer gamePlayer = GamePlayer.of(player.getUniqueId());
                    if (gamePlayer == null) continue;
                    if (!gamePlayer.check()) continue;
                    Bukkit.getScheduler().runTask(this.couleDeBoue.getPlugin(), () -> gamePlayer.getRole().givePotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1, false, false), EffectWhen.NOW));
                }
                this.timeLeft--;
            }
        }
    }
}