package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.EChakras;
import fr.nicknqck.enums.EffectWhen;
import fr.nicknqck.enums.Intelligence;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.GamePlayerEatGappleEvent;
import fr.nicknqck.interfaces.IRoles;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.builders.HShinobiRoles;
import fr.nicknqck.roles.ns.builders.NSRoles;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TsunadeV2 extends HShinobiRoles {

    public TsunadeV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.INTELLIGENT;
    }

    @Override
    public EChakras[] getChakrasCanHave() {
        return EChakras.values();
    }

    @Override
    public String getName() {
        return "Tsunade";
    }

    @Override
    public @NonNull IRoles<?> getRoles() {
        return Roles.Tsunade;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.PERMANENT);
        addPower(new KatsuyuPower(this), true);
        addPower(new EnseignementPower(this));
        super.RoleGiven(gameState);
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }
    private static final class KatsuyuPower extends ItemPower implements Listener {

        private double stocked = 0.0;

        public KatsuyuPower(@NonNull RoleBase role) {
            super("§dKatsuyu§r", null, new ItemBuilder(Material.INK_SACK)
                    .setDurability(9)
                    .setName("§dKatsuyu")
                    .addEnchant(Enchantment.ARROW_DAMAGE, 1)
                    .hideEnchantAttributes(), role,
                    "§7Lorsque vous mangez une§e pomme d'or§7 ce qui ne vous soigne pas directement est stocker",
                    "§7lorsque vous utilisez§d Katsuyu§7 avec un§c clique droit§7, celà vous§d soigne§7 le plus possible (en fonction des réserves)",
                    "§7tandis que si vous utilisez le§c clique gauche§7 en visant un joueur§7, il sera§d soigner§7 (distance maximal:§c 10m§7)."
            );
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (this.stocked <= 0.0) {
                    player.sendMessage(Main.getInstance().getNAME()+"§c Vous n'avez aucun§a point de vie§c stocker actuellement !");
                    return false;
                } else {
                    final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                    if (event.getAction().name().contains("RIGHT")) {
                        final double healNeeded = player.getMaxHealth() - player.getHealth();
                        if (this.stocked >= healNeeded) {
                            player.setHealth(player.getHealth() + healNeeded);
                            this.stocked -= healNeeded;
                            player.sendMessage("§7Vous vous êtes§d soigner§7 de manière§c complète§7.");
                        } else {
                            player.sendMessage("§7Vous vous êtes§d soigner§7 de§c "+(this.stocked/2)+"❤");
                            player.setHealth(player.getHealth() + this.stocked);
                            this.stocked = 0.0;
                        }
                    } else {
                        final Player target = RayTrace.getTargetPlayer(player, 10.0, null);
                        if (target == null) {
                            player.sendMessage("§cIl faut viser un joueur !");
                            return false;
                        }
                        final double healNeeded = target.getMaxHealth() - target.getHealth();
                        if (this.stocked >= healNeeded) {
                            target.setHealth(target.getHealth() + healNeeded);
                            this.stocked -= healNeeded;
                            target.sendMessage("§7Vous avez été§d soigner§7 de manière§c complète§7 par§d Katsuyu§7.");
                            player.sendMessage("§c"+target.getName()+"§7 a été§d soigner§7 de manière§c complète§7.");
                        } else {
                            target.sendMessage("§7Vous avez été§d soigner§7 de§c "+(this.stocked/2)+"❤§7 par§d Katsuyu§7.");
                            player.sendMessage("§c"+target.getName()+"§7 a été§d soigner§7 de§c "+(this.stocked/2)+"❤§7 par§d Katsuyu§7.");
                            target.setHealth(target.getHealth() + this.stocked);
                            this.stocked = 0.0;
                        }
                        final Location location = target.getEyeLocation().clone();
                        location.add(0.0, 1.2, 0.0);
                        MathUtil.sendParticle(EnumParticle.HEART, location);
                    }
                    getRole().getGamePlayer().getActionBarManager().updateActionBar("tsunadev2.katsuyu", "§aVie stocker:§c "+(this.stocked/2)+"❤");
                    return true;
                }
            }
            return false;
        }
        @EventHandler
        private void onEat(@NonNull final GamePlayerEatGappleEvent event) {
            final double actualHealth = event.getPlayer().getHealth();
            final double maxHealth = event.getPlayer().getMaxHealth();
            if (actualHealth + 4.0 > maxHealth) {
                final double dif = Math.abs((actualHealth + 4.0) - maxHealth);
                this.stocked += dif;
                getRole().getGamePlayer().getActionBarManager().updateActionBar("tsunadev2.katsuyu", "§aVie stocker:§c "+(this.stocked/2)+"❤");
            }
        }
    }
    private static final class EnseignementPower extends CommandPower {

        public EnseignementPower(@NonNull RoleBase role) {
            super("/ns enseignement <joueur>", "enseignement", null, role, CommandType.NS,
                    "§7En restant à moins de§c 30 blocs§7 du joueur§c cibler§7,",
                    "§7vous lui§a enseignerez§7 comment utiliser§d Katsuyu§7,",
                    "§7celà durera plus ou moins de temps en fonction de son§a intelligence§7.",
                    "",
                    "§7Une fois fait, vous obtiendrez§c 2❤ permanents supplémentaire§7."
            );
            setMaxUse(1);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            final String[] args = (String[]) map.get("args");
            if (args.length == 2) {
                final Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    final GamePlayer gamePlayer = GamePlayer.of(target.getUniqueId());
                    if (gamePlayer != null) {
                        if (gamePlayer.check()) {
                            if (gamePlayer.getRole() instanceof NSRoles) {
                                final NSRoles role = (NSRoles) gamePlayer.getRole();
                                final Intelligence intelligence = role.getIntelligence();
                                new EnseignementRunnable(this, role, intelligence).runTaskTimerAsynchronously(getPlugin(), 0, 20);
                                return true;
                            } else {
                                player.sendMessage("§b"+gamePlayer.getPlayerName()+"§c ne possède pas un rôle du§a Naruto UHC§c.");
                            }
                        } else {
                            player.sendMessage("§b"+args[1]+"§c n'est pas connecté(e) ou n'existe pas !");
                        }
                    } else {
                        player.sendMessage("§b"+args[1]+"§c n'est pas connecté(e) ou n'existe pas !");
                    }
                } else {
                    player.sendMessage("§b"+args[1]+"§c n'est pas connecté(e) ou n'existe pas !");
                }
            }
            return false;
        }
        private static final class EnseignementRunnable extends BukkitRunnable {

            private final EnseignementPower power;
            private final NSRoles nsRoles;
            private final Intelligence intelligence;
            private int timeLeft;

            private EnseignementRunnable(EnseignementPower power, NSRoles nsRoles, Intelligence intelligence) {
                this.power = power;
                this.nsRoles = nsRoles;
                this.intelligence = intelligence;
                this.timeLeft = 0;
            }

            @Override
            public void run() {
                if (!GameState.inGame()) {
                    cancel();
                    return;
                }
                if (!this.power.getRole().getGamePlayer().check())return;
                if (!this.nsRoles.getGamePlayer().check()) {
                    this.power.getRole().getGamePlayer().getActionBarManager().removeInActionBar("tsunadev2.enseignement");
                    return;
                }
                if (this.timeLeft >= intelligence.getEnseignemenTime()) {
                    this.nsRoles.addPower(new KatsuyuPower(nsRoles), true);
                    this.nsRoles.getGamePlayer().sendMessage("§aTsunade§7 vous a§a enseigner§7 comment utiliser§d Katsuyu§7.");
                    this.power.getRole().getGamePlayer().sendMessage("§7Vous avez§a enseigner§7 comment uriliser§d Katsuyu§7 a§a "+nsRoles.getGamePlayer().getPlayerName());
                    this.power.getRole().getGamePlayer().getActionBarManager().removeInActionBar("tsunadev2.enseignement");
                    Bukkit.getScheduler().runTask(this.power.getPlugin(), () -> this.power.getRole().giveHealedHeartatInt(2.0));
                    cancel();
                    return;
                }
                final List<Player> playerList = new ArrayList<>(Loc.getNearbyPlayers(power.getRole().getGamePlayer().getLastLocation(), 30));
                for (Player player : playerList) {
                    if (player.getUniqueId().equals(nsRoles.getPlayer())) {
                        this.timeLeft++;
                        final String percent = getCompletionPercent(this.timeLeft, this.intelligence.getEnseignemenTime());
                        this.power.getRole().getGamePlayer().getActionBarManager().updateActionBar("tsunadev2.enseignement", "§bEnseignement:§c "+percent);
                        break;
                    }
                }
            }
            /**
             * Retourne un pourcentage de complétion formaté en String.
             *
             * @param current valeur actuelle
             * @param total   valeur totale
             * @return ex: "75.00%", ou "0.00%" si total == 0
             */
            public static String getCompletionPercent(int current, int total) {
                if (total == 0) return "0.00%";
                return String.format("%.2f%%", (current * 100.0) / total);
            }
        }
    }
}