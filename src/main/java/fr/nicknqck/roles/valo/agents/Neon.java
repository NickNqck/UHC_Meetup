package fr.nicknqck.roles.valo.agents;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.utils.Loc;
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
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Neon extends RoleBase {
    public Neon(UUID player) {
        super(player);
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Neon";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Neon;
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Solo;
    }

    @Override
    public void resetCooldown() {

    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .setPowers(getPowers())
                .getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new SpeedItemPower(this), true);
        addPower(new EclairRelaisPower(this), true);
        addPower(new VoieRapidePower(this), true);
    }
    private static class VoieRapidePower extends ItemPower {

        protected VoieRapidePower(@NonNull RoleBase role) {
            super("§bVoie Rapide", new Cooldown(60*10), new ItemBuilder(Material.NETHER_STAR).setName("§bVoie Rapide"), role);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final List<Block> centre = getBlocksInFront(player);
                for (final Block block : centre) {
                    MathUtil.sendParticle(EnumParticle.EXPLOSION_NORMAL, block.getLocation());
                    block.setType(Material.FIRE);
                }
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20*60, 0, false, false), true);
            }
            return false;
        }
        private List<Block> getBlocksInFront(Player player) {
            List<Block> blocks = new ArrayList<>();
            Location eyeLocation = player.getEyeLocation();
            Vector direction = eyeLocation.getDirection().normalize();

            // Parcours des 15 blocs devant le joueur
            for (int i = 1; i <= 20; i++) {
                // Calcul de la position principale (directement devant)
                Location frontLocation = eyeLocation.clone().add(direction.clone().multiply(i));

                // Calcul et ajout des blocs à gauche et à droite
                Vector right = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize();
                Location leftLocation = frontLocation.clone().add(right.clone().multiply(-2));
                Location rightLocation = frontLocation.clone().add(right.clone().multiply(2));

                blocks.add(frontLocation.getWorld().getHighestBlockAt(leftLocation));
                blocks.add(frontLocation.getWorld().getHighestBlockAt(rightLocation));
            }
            return blocks;
        }
    }
    private static class EclairRelaisPower extends ItemPower {

        protected EclairRelaisPower(@NonNull RoleBase role) {
            super("§bÉclair Relais", new Cooldown(60*10), new ItemBuilder(Material.NETHER_STAR).setName("§bÉclair Relais"), role);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final Player target = getRole().getTargetPlayer(player, 25.0);
                if (target != null) {
                    final List<Player> arounds1 = new ArrayList<>(Loc.getNearbyPlayers(player, 25));
                    final StringBuilder stringBuilder = new StringBuilder("§7Voici la liste des personnes autours de vous:\n\n");
                    arounds1.remove(player);
                    for (final Player p : arounds1) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*15, 2, false, false));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20*15, 2, false, false));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*5, 2, false, false));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*5, 2, false, false));
                        p.sendMessage("§7Vous avez été toucher par l'"+getName()+"§7 de§b Neon");
                        stringBuilder.append("§c").append(p.getDisplayName()).append("§7, ");
                    }
                    String string = stringBuilder.toString();
                    string = string.substring(string.length()-3);
                    player.sendMessage(string);
                    return true;
                } else {
                    player.sendMessage("§cIl faut viser un joueur !");
                }
            }
            return false;
        }
    }
    private static class SpeedItemPower extends ItemPower {

        private final SpeedRunnable runnable;
        private int dashRemaining = 2;

        protected SpeedItemPower(@NonNull RoleBase role) {
            super("Vitesse Supérieure", null, new ItemBuilder(Material.NETHER_STAR).setName("§bVitesse Supérieure"), role,
                    "");
            this.runnable = new SpeedRunnable(this);
            getRole().getGamePlayer().getActionBarManager().addToActionBar("neon.dash.count", "§cDashs§7: §c2§7/§62");
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                if (event.getAction().name().contains("RIGHT")) {
                    if (this.runnable.start) {
                        this.runnable.start = false;
                        player.sendMessage("§7Vous avez désactiver votre§e Speed 2");
                        player.removePotionEffect(PotionEffectType.SPEED);
                    } else {
                        this.runnable.start = true;
                        player.sendMessage("§7Vous activer votre§e Speed 2");
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false), true);
                    }
                    return true;
                } else if (event.getAction().name().contains("LEFT")) {
                    if (this.dashRemaining < 1) {
                        return false;
                    }
                    final Vector direction = player.getEyeLocation().getDirection();
                    direction.setY(0.1);
                    direction.multiply(1.8);
                    player.setVelocity(direction);
                    this.dashRemaining--;
                    getRole().getGamePlayer().getActionBarManager().updateActionBar("neon.dash.count", "§cDashs§7: §c"+dashRemaining+"§7/§62");
                    return true;
                }
            }
            return false;
        }
        private static final class SpeedRunnable extends BukkitRunnable {

            private final GameState gameState;
            private double speedBar = 100.0;
            private final GamePlayer gamePlayer;
            private boolean start = false;

            private SpeedRunnable(SpeedItemPower speedItemPower) {
                this.gamePlayer = speedItemPower.getRole().getGamePlayer();
                this.gameState = GameState.getInstance();
                this.gamePlayer.getActionBarManager().addToActionBar("valo.agents.neon.speedbar", "bar "+speedBar);
                runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                this.gamePlayer.getActionBarManager().updateActionBar("valo.agents.neon.speedbar", "§bCharge: §7["+getCharge()+"§7 (§b"+getPercentage(this.speedBar)+"§7)]");
                if (!this.start) {
                    this.speedBar = Math.min(100.0, this.speedBar+0.5);
                    return;
                }
                final Player owner = Bukkit.getPlayer(gamePlayer.getUuid());
                if (owner != null) {
                    this.speedBar-=1;
                    if (this.speedBar < 1) {
                        this.start = false;
                        owner.removePotionEffect(PotionEffectType.SPEED);
                        owner.sendMessage("§7Vous n'avez plus assez d'énergie pour courir.");
                    }
                }
            }
            private String getCharge() {
                double maxBar = 100.0;
                double bar = this.speedBar;
                StringBuilder sbar = new StringBuilder();
                for (double i = 0; i < bar; i++) {
                    sbar.append("§a|");
                }
                for (double i = bar; i < maxBar; i++) {
                    sbar.append("§c|");
                }
                return sbar.toString();
            }
            private String getPercentage(double value) {
                final DecimalFormat format = new DecimalFormat("0");
                return format.format((value / 100.0) * 100)+"%";
            }

        }
    }
}
