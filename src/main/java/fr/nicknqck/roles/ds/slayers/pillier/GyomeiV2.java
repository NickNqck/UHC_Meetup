package fr.nicknqck.roles.ds.slayers.pillier;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.GameEndEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.enums.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.enums.Soufle;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;

public class GyomeiV2 extends PilierRoles implements Listener {

    private TextComponent desc;

    public GyomeiV2(UUID player) {
        super(player);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.ROCHE;
    }

    @Override
    public String getName() {
        return "Gyomei";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Gyomei;
    }

    @Nonnull
    @Override
    public TextComponent getComponent() {
        return this.desc;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        setMaxHealth(24.0);
        owner.setMaxHealth(getMaxHealth());
        owner.setHealth(owner.getMaxHealth());
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 9999, 0, false, false), EffectWhen.PERMANENT);
        addPower(new SouffleDeLaRochePower(this), true);
        EventUtils.registerEvents(this);
        AutomaticDesc automaticDesc = new AutomaticDesc(this).addEffects(getEffects()).addCustomLine("§7Vous possédez§c 2❤§c permanent§7 supplémentaire").setPowers(getPowers());
        this.desc = automaticDesc.getText();
        setCanuseblade(true);
    }

    @EventHandler
    private void onEndGame(GameEndEvent event) {
        EventUtils.unregisterEvents(this);
    }

    private static class SouffleDeLaRochePower extends ItemPower {

        private final FracasPower fracasPower;
        private final SuperGrabPower superGrabPower;

        protected SouffleDeLaRochePower(@NonNull RoleBase role) {
            super("Souffle de la roche", new Cooldown(20), new ItemBuilder(Material.STONE_AXE).setName("§aSouffle de la Roche").addEnchant(Enchantment.DAMAGE_ALL, 4).hideEnchantAttributes(), role,
                    "§7Vous permet (en visant un joueur) d'activer un pouvoir différant en fonction de votre§c clique§7:",
                    "",
                    "§aFracas§7 (§cClique gauche§7): Vous permet de§c repousser§7 le joueur viser puis de lui infliger§c 2❤§7 de dégats§c 5 secondes§7 plus tard (1x/5m)",
                    "",
                    "§aGrab§7 (§cClique droit§7): Vous permet d'attirer le joueur devant vous et de lui donner§c 15 secondes§7 de§8 Slowness II§7 (1x/5m)",
                    "",
                    "§cVous ne pouvez utiliser qu'un pouvoir toute les§4 20 secondes");
            final FracasPower fracasPower = new FracasPower(getRole());
            final SuperGrabPower superGrabPower1 = new SuperGrabPower(role);
            getRole().addPower(fracasPower);
            getRole().addPower(superGrabPower1);
            this.fracasPower = fracasPower;
            this.superGrabPower = superGrabPower1;
            getShowCdRunnable().setCustomText(true);
            setTargetDistance(50);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                if (event.getAction().equals(Action.PHYSICAL))return false;
                if (event.getAction().name().contains("LEFT")) {
                    return this.fracasPower.checkUse(player, map);
                } else {
                    return this.superGrabPower.checkUse(player, map);
                }
            }
            return false;
        }

        @Override
        public void tryUpdateActionBar() {
            getShowCdRunnable().setCustomTexte(
                    (this.getCooldown().isInCooldown() ?
                            "§c"+getName()+"§f est en cooldown§7 (§c"+StringUtils.secondsTowardsBeautiful(this.getCooldown().getCooldownRemaining())+"§7)" :
                    "§fFracas est "+
                            (this.fracasPower.getCooldown().isInCooldown() ?
                                    "en cooldown§7 (§c"+ StringUtils.secondsTowardsBeautiful(this.fracasPower.getCooldown().getCooldownRemaining())+"§7)" :
                                    "utilisable§7 (§cClique gauche§7)") + "§7 |§f Grab est " +
                            (this.superGrabPower.getCooldown().isInCooldown() ?
                                    "en cooldown§7 (§c"+ StringUtils.secondsTowardsBeautiful(this.superGrabPower.getCooldown().getCooldownRemaining())+"§7)" :
                                    "utilisable§7 (§cClique droit§7)")
            ));
        }

        private static class FracasPower extends Power {

            public FracasPower(@NonNull RoleBase role) {
                super("Fracas rocheux", new Cooldown(60*5), role);
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                final Player target = getTarget();
                if (target == null) {
                    player.sendMessage("§cIl faut viser un joueur à moins de§b 30 blocs");
                    return false;
                }
                new RepousserRunnable(getRole().getGameState(), target, getRole().getGamePlayer());
                return true;
            }
            private static class RepousserRunnable extends BukkitRunnable {

                private final GameState gameState;
                private final UUID uuidTarget;
                private final GamePlayer gamePlayer;
                private final String nameTarget;
                private int timeLeft;

                private RepousserRunnable(GameState gameState, Player target, GamePlayer gamePlayer) {
                    this.gameState = gameState;
                    this.uuidTarget = target.getUniqueId();
                    this.nameTarget = target.getDisplayName();
                    this.gamePlayer = gamePlayer;
                    this.timeLeft = 5;
                    this.gamePlayer.getActionBarManager().addToActionBar("gyomei.repousser", "§bTemp avant dégât contre§c "+this.nameTarget+"§b:§c "+timeLeft+"s");
                    final Vector direction = target.getLocation().getDirection();
                    direction.multiply(-2);
                    target.setVelocity(direction.normalize().multiply(8).setY(0.8));
                    target.sendMessage("§cVous avez été repousser par§a Gyomei");
                    gamePlayer.sendMessage("Vous avez repousser§c "+this.nameTarget+"§f, il subira§c 2❤§f de dégât dans§c 5 secondes");
                    runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
                }

                @Override
                public void run() {
                    if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                        cancel();
                        return;
                    }
                    this.gamePlayer.getActionBarManager().updateActionBar("gyomei.repousser", "§bTemp avant dégât contre§c "+this.nameTarget+"§b:§c "+timeLeft+"s");
                    if (this.timeLeft <= 0) {
                        this.gamePlayer.getActionBarManager().removeInActionBar("gyomei.repousser");
                        final Player target = Bukkit.getPlayer(uuidTarget);
                        if (target != null) {
                            Bukkit.getScheduler().runTask(Main.getInstance(), () -> target.setHealth(Math.max(0.1, target.getHealth()-4.0)));
                            this.gamePlayer.sendMessage("§b"+this.nameTarget+"§f à subit§c 2❤§f de§c dégâts");
                            target.sendMessage("§aGyomei§f vous a infliger§c 2❤§f de dégâts");
                            target.damage(0.0);
                        } else {
                            this.gamePlayer.sendMessage("§b"+this.nameTarget+"§c à déconnecter, il n'a donc pas subit les dégâts voulu");
                        }
                        cancel();
                        return;
                    }
                    this.timeLeft--;
                }
            }
        }
        private static class SuperGrabPower extends Power {

            public SuperGrabPower(@NonNull RoleBase role) {
                super("Grab", new Cooldown(60*5), role);
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                final Player target = getTarget();
                if (target == null) {
                    player.sendMessage("§cIl faut viser un joueur à moins de§b 50 blocs");
                    return false;
                }
                final Location location = player.getLocation();
                location.add(location.getDirection().add(new Vector(1, 0, 1)));
                target.teleport(location);
                target.sendMessage("§aGyomei§7 vous à attirer à sa position");
                player.sendMessage("§cVous avez récupérer§b "+target.getDisplayName());
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*15, 1, false, false), true);
                return true;
            }
        }
    }
}
