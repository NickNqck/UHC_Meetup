package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.EChakras;
import fr.nicknqck.enums.EffectWhen;
import fr.nicknqck.enums.Intelligence;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.interfaces.IRoles;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
import fr.nicknqck.utils.ArrowTargetUtils;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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

public class Shino extends ShinobiRoles {

    public Shino(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.INTELLIGENT;
    }

    @Override
    public EChakras[] getChakrasCanHave() {
        return new EChakras[] {
                EChakras.DOTON, EChakras.KATON
        };
    }

    @Override
    public String getName() {
        return "Shino";
    }

    @Override
    public @NonNull IRoles<?> getRoles() {
        return Roles.Shino;
    }

    @Override
    public @NonNull TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        super.RoleGiven(gameState);
        addPower(new RinkaichuPower(this), true);
        addPower(new TracagePower(this));
    }

    private static final class RinkaichuPower extends ItemPower implements Listener {

        private boolean activate = false;
        private int timeBeforeLooseHeart = 60*3;

        public RinkaichuPower(@NonNull RoleBase role) {
            super("§aRinkaichu", new Cooldown(3), new ItemBuilder(Material.NETHER_STAR).setName("§aRinkaichu"), role,
                    "§7Tant que ce pouvoir est§c actif§7, les joueurs qui vous frappe auront§c 40% de chance§7",
                    "§7d'être§2 empoisonné§7.",
                    "",
                    "§7Ce pouvoir est§a Activable§7/§cDésactivable§7 via n'importe quel§f clique§7.",
                    "§7Toute les§c 3 minutes cumulés§7 avec ce§c pouvoir§a actif§7, vous perdrez§c 1/2❤ permanent",
                    "§7le pouvoir se§c désactivera§7 également§c automatiquement§7."
            );
            setSendCooldown(false);
            setShowCdInDesc(false);
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (!activate) {
                if (player.getMaxHealth() < 10.0) {
                    player.sendMessage("§cVous êtes trop faible pour vos insectes !");
                    return false;
                }
                this.activate = true;
                player.sendMessage("§7Vos insectes§a Rinkaichu§7 commencent à vous recouvrir.");
                new RinkaichuRunnable(this).runTaskTimerAsynchronously(getPlugin(), 0, 20);
            } else {
                this.activate = false;
                player.sendMessage("§7Vos insectes§a Rinkaichu§7 se sépare de vous.");
            }
            return true;
        }
        @EventHandler(priority = EventPriority.MONITOR)
        private void onDamage(@NonNull final EntityDamageByEntityEvent event) {
            if (event.isCancelled())return;
            if (!activate)return;
            if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
                if (event.getEntity().getUniqueId().equals(getRole().getPlayer())) {
                    final GamePlayer gamePlayer = GamePlayer.of(event.getDamager().getUniqueId());
                    if (gamePlayer != null) {
                        if (gamePlayer.check() && RandomUtils.getOwnRandomProbability(40)) {
                            gamePlayer.getRole().givePotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0, false, false), EffectWhen.NOW);
                            gamePlayer.sendMessage("§aShino§7 vous a§2 empoisonné§7 avec son§a Rinkaichu§7.");
                        }
                    }
                }
            }
        }
        private static final class RinkaichuRunnable extends BukkitRunnable {

            private final RinkaichuPower power;

            private RinkaichuRunnable(RinkaichuPower power) {
                this.power = power;
            }

            @Override
            public void run() {
                if (!GameState.inGame()) {
                    cancel();
                    return;
                }
                if (!this.power.getRole().getGamePlayer().check()) {
                    this.power.activate = false;
                    this.power.getRole().getGamePlayer().getActionBarManager().removeInActionBar("shino.rinkaichu");
                    cancel();
                    return;
                }
                if (!this.power.activate) {
                    this.power.getRole().getGamePlayer().getActionBarManager().removeInActionBar("shino.rinkaichu");
                    cancel();
                    return;
                }
                if (this.power.timeBeforeLooseHeart <= 0) {
                    this.power.timeBeforeLooseHeart = 60*3;
                    this.power.activate = false;
                    this.power.getRole().getGamePlayer().getActionBarManager().removeInActionBar("shino.rinkaichu");
                    Bukkit.getScheduler().runTask(this.power.getPlugin(), () -> {
                        this.power.getRole().setMaxHealth(this.power.getRole().getMaxHealth() - 1.0);
                        this.power.getRole().getGamePlayer().sendMessage("§7Vous avez perdu§c 1/2❤§c permanent§7 à cause de votre§a Rinkaichu§7.");
                    });
                    return;
                }
                this.power.getRole().getGamePlayer().getActionBarManager().updateActionBar("shino.rinkaichu", "§bTemps avant perte de§c ❤§b:§c "+ StringUtils.secondsTowardsBeautiful(this.power.timeBeforeLooseHeart));
                this.power.timeBeforeLooseHeart--;
            }
        }
    }
    private static final class TracagePower extends CommandPower {

        @Getter
        private final List<Tracker> trackers;

        public TracagePower(@NonNull RoleBase role) {
            super("/ns tracer <joueur>", "tracer", new Cooldown(60*8), role, CommandType.NS,
                    "§7Permet de suivre à la trace un joueur, cependant, il faut être à moins de§c 10 blocs§7 pour démarer le§c traçage§7."
            );
            setMaxUse(3);
            this.trackers = new ArrayList<>();
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            final String[] args = (String[]) map.get("args");
            if (args.length == 2) {
                final Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    final GamePlayer gamePlayer = GamePlayer.of(target.getUniqueId());
                    if (gamePlayer != null) {
                        if (gamePlayer.check() && target.getWorld().equals(player.getWorld()) && target.getLocation().distance(player.getLocation()) <= 10 && !target.getUniqueId().equals(player.getUniqueId())) {
                            player.sendMessage("§7La traque de§a "+target.getName()+"§7 commence.");
                            this.trackers.add(new Tracker(this, getUse()+1, gamePlayer));
                            return true;
                        } else {
                            player.sendMessage("§cErreur | Impossible de viser§b "+target.getName()+"§c.");
                            return false;
                        }
                    }
                    player.sendMessage("§cErreur | Impossible de viser un joueur mort !");
                    return false;
                }
                player.sendMessage("§cImpossible de trouver le joueur§b "+args[1]+"§c.");
                return false;
            }
            return false;
        }

        @Override
        public List<String> getCompletor(String[] args) {
            if (args.length == 2) {
                final List<Player> players = new ArrayList<>(Loc.getNearbyPlayers(getRole().getGamePlayer().getLastLocation(), 20.0));
                final List<String> test = new ArrayList<>();
                for (Player player : players) {
                    if (player.getUniqueId().equals(getRole().getPlayer()))continue;
                    if (player.hasPotionEffect(PotionEffectType.INVISIBILITY))continue;
                    if (!player.getGameMode().equals(GameMode.SURVIVAL))continue;
                    final GamePlayer gamePlayer = GamePlayer.of(player.getUniqueId());
                    if (gamePlayer == null)continue;
                    if (!gamePlayer.check())continue;
                    test.add(player.getName());
                }
                final List<String> toReturn = new ArrayList<>();
                for (String string : test) {
                    if (string.toLowerCase().startsWith(args[1].toLowerCase())) {
                        toReturn.add(string);
                    }
                }
                return toReturn;
            }
            return new ArrayList<>();
        }

        private static final class Tracker extends BukkitRunnable {

            private final TracagePower power;
            private final int id;
            private final GamePlayer target;

            private Tracker(TracagePower power, int id, GamePlayer target) {
                this.power = power;
                this.id = id;
                this.target = target;
                runTaskTimerAsynchronously(this.power.getPlugin(), 5, 1);
            }

            @Override
            public void run() {
                if (!GameState.inGame()) {
                    cancel();
                    return;
                }
                if (!this.target.check()) {
                    cancel();
                    return;
                }
                if (!this.power.getRole().getGamePlayer().check()) {
                    cancel();
                    return;
                }
                final Player target = this.target.getPlayer();
                final Player owner = this.power.getRole().getGamePlayer().getPlayer();
                if (!owner.getWorld().equals(target.getWorld())) {
                    this.power.getRole().getGamePlayer().getActionBarManager().updateActionBar("shina.tracker"+this.id, "§a"+target.getName()+":§b ?");
                } else {
                    this.power.getRole().getGamePlayer().getActionBarManager().updateActionBar("shina.tracker"+this.id, "§a"+target.getName()+":§b "+ ArrowTargetUtils.calculateArrow(owner.getLocation(), target.getLocation()));
                }
            }
        }
    }
}