package fr.nicknqck.roles.aot.soldats;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.FinalDeathEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.aot.builders.SoldatsRoles;
import fr.nicknqck.roles.aot.solo.ErenV2;
import fr.nicknqck.roles.aot.solo.GabiV2;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JeanV2 extends SoldatsRoles implements Listener {

    public JeanV2(UUID player) {
        super(player);
    }

    @Override
    public String getName() {
        return "Jean";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Jean;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new FuseCommand(this));
        super.RoleGiven(gameState);
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createAutomaticDesc(this)
                .addCustomLine("§7A la mort de§a Conny§7 et de§a Sasha§7, vous obtiendrez leurs §7position.")
                .getText();
    }
    @EventHandler
    private void onDeath(@NonNull final FinalDeathEvent event) {
        if (event.getRole() instanceof ConnyV2 || event.getRole() instanceof SashaV2) {
            getGamePlayer().sendMessage("§7Un ami extrêmement proche de vous est mort, heureusement il a eu le temps de vous envoyez sa position,§c x§7:§c "+event.getPlayer().getLocation().getBlockX()+"§7,§c y§7:§c "+event.getPlayer().getLocation().getBlockY()+"§7 et§c z§7:§c "+event.getPlayer().getLocation().getBlockZ());
        }
    }
    private static final class FuseCommand extends CommandPower {

        public FuseCommand(@NonNull RoleBase role) {
            super("/aot fuse", "fuse", new Cooldown(60*10), role, CommandType.AOT,
                    "§c15 secondes§7 après l'éxécution de cette commande, lance une§a fusée",
                    "§7sa couleur dépendra de s'il y§a a§7 ou§c non§7 au moins§c 1 ennemi§7.",
                    "",
                    "§7Lors de l'explosion de votre§a fusé§7, vous obtiendrez§c 5 minutes§7 de",
                    "§9résistance 1§7 s'il y a au moins§c méchant§7 autours de vous.",
                    "",
                    "§7Vous obtiendrez la liste des joueurs étant à moins de§c 30 blocs§7 au",
                    "§7moment de l'§cexplosion§7.");
            setMaxUse(3);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            player.sendMessage("§bVotre§a fusé§b partira dans§c 15 secondes§b.");
            new LaunchFuseRunnable(this).runTaskTimerAsynchronously(Main.getInstance(), 0L, 20L);
            return true;
        }
        private static final class LaunchFuseRunnable extends BukkitRunnable {

            private final FuseCommand fuseCommand;
            private int time = 15;

            private LaunchFuseRunnable(FuseCommand fuseCommand) {
                this.fuseCommand = fuseCommand;
            }


            @Override
            public void run() {
                if (!GameState.inGame()) {
                    cancel();
                    return;
                }
                final GamePlayer gamePlayer = fuseCommand.getRole().getGamePlayer();
                if (!gamePlayer.isAlive()) {
                    cancel();
                    return;
                }
                if (!gamePlayer.isOnline())return;
                if (this.time <= 0) {
                    final List<Player> mechant = new ArrayList<>();
                    final List<GamePlayer> inZone = new ArrayList<>();
                    for (Player nearbyPlayer : Loc.getNearbyPlayers(gamePlayer.getLastLocation(), 30)) {
                        GamePlayer gm = GamePlayer.of(nearbyPlayer.getUniqueId());
                        if (gm == null) continue;
                        if (gm.getRole() == null)continue;
                        if (!gm.isAlive())continue;
                        if (!gm.isOnline())continue;
                        final RoleBase role = gm.getRole();
                        if (role instanceof GabiV2 || role instanceof ErenV2) {
                            inZone.add(gm);
                            continue;
                        }
                        if (!role.getTeam().getColor().equalsIgnoreCase("§a")) {
                            mechant.add(nearbyPlayer);
                        }
                        inZone.add(gm);
                    }
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> createFirework(gamePlayer.getLastLocation(), mechant.isEmpty() ? Color.GREEN : Color.RED));
                    gamePlayer.sendMessage(Main.getInstance().getNAME()+"§b Voici la liste des personnes étant proche de vous: ");
                    inZone.forEach(e -> gamePlayer.sendMessage("§8 -§a "+e.getPlayerName()));
                    cancel();
                    return;
                }
                gamePlayer.getActionBarManager().updateActionBar("jean.fuse", "§bPréparation en cours de la§a fusé§b (§c"+ StringUtils.secondsTowardsBeautiful(this.time)+"§b)");
                this.time--;
            }
            private synchronized void createFirework(Location location, Color color) {
                Firework firework = location.getWorld().spawn(location, Firework.class);
                FireworkMeta meta = firework.getFireworkMeta();
                FireworkEffect.Builder builder = FireworkEffect.builder();

                builder.withColor(color);
                builder.with(FireworkEffect.Type.BALL);

                meta.addEffect(builder.build());
                meta.setPower(1);
                firework.setFireworkMeta(meta);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        firework.detonate();
                    }
                }.runTaskLater(Main.getInstance(), 20L); // 20L équivaut à une seconde
                if (color.equals(Color.RED)) {
                    this.fuseCommand.getRole().givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60*5, 0, false, false), EffectWhen.NOW);
                    this.fuseCommand.getRole().getGamePlayer().sendMessage("§7Comme il y a au moins§c 1 traitre§7 autours de vous, vous avez reçus§c 5 minutes§7 de§9 Résistance 1§7.");
                }
            }
        }
    }
}