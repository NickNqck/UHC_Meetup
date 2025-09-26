package fr.nicknqck.roles.valo.agents;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
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
    public @NonNull Roles getRoles() {
        return Roles.Neon;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Solo;
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
            super("§bVoie Rapide", new Cooldown(60*3), new ItemBuilder(Material.NETHER_STAR).setName("§bVoie Rapide"), role,
                    "§7Permet de crée un chemin entourer par§c deux§6 murs de feu§7 vers la ou vous regardez§7.");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final List<Block> centre = getBlocksInFront(player);
                for (final Block block : centre) {
                    MathUtil.sendParticle(EnumParticle.EXPLOSION_NORMAL, block.getLocation());
                    block.setType(Material.FIRE);
                }
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 200, 0, false, false), true);
                return true;
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
            super("§bÉclair Relais", new Cooldown(60*10), new ItemBuilder(Material.NETHER_STAR).setName("§bÉclair Relais"), role,
                    "§7En visant un §cjoueur§7 vous permet de le ralentir lui ainsi que les autres joueurs proche de lui pendant§c 15 secondes§7");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
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

        private final DashPower dashPower;
        private final CoursePower coursePower;

        protected SpeedItemPower(@NonNull RoleBase role) {
            super("Vitesse Supérieure", null, new ItemBuilder(Material.NETHER_STAR).setName("§bVitesse Supérieure"), role,
                    "§7Vous possédez une bar de§b Charge§7, elle se régénère lorsque vous ne l'utiliser pas et s'utilise différemment en fonction du clique que vous faite:",
                    "",
                    "§bClique droit§7: Vous permet d'obtenir§e Speed II§7 (§cConssomation§7:§b 1%§7/s).",
                    "",
                    "§bClique gauche§7: Vous permet de faire un§c dash§7 en avant, vous gagnez§a +1§c dash§7 tout les§c 2 kills§7 (§cConssomation§7: §b5%§7) (2x/partie) (1x/2m)");
            this.coursePower = new CoursePower(role);
            role.addPower(this.coursePower);
            this.dashPower = new DashPower(this);
            role.addPower(this.dashPower);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                if (event.getAction().name().contains("RIGHT")) {
                    return this.coursePower.checkUse(player, map);
                } else if (event.getAction().name().contains("LEFT")) {
                    return this.dashPower.checkUse(player, map);
                }
            }
            return false;
        }
        private static final class CoursePower extends Power {

            private final SpeedRunnable runnable;

            public CoursePower(@NonNull RoleBase role) {
                super("§bCourse", null, role);
                this.runnable = new SpeedRunnable(this);
                setShowInDesc(false);
                this.getRole().getGamePlayer().getActionBarManager().addToActionBar("valo.agents.neon.speedbar", "bar "+this.runnable.speedBar);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                if (this.runnable.start) {
                    this.runnable.start = false;
                    player.sendMessage("§7Vous avez désactiver votre§e Speed 2");
                    player.removePotionEffect(PotionEffectType.SPEED);
                } else {
                    if (this.runnable.speedBar <= 1) {
                        player.sendMessage("§7Vous n'avez pas asser d'énergie pour courir");
                        return false;
                    }
                    this.runnable.start = true;
                    player.sendMessage("§7Vous activer votre§e Speed 2");
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false), true);
                }
                return true;
            }
            private static final class SpeedRunnable extends BukkitRunnable {

                private final GameState gameState;
                private double speedBar = 100.0;
                private final GamePlayer gamePlayer;
                private boolean start = false;

                private SpeedRunnable(CoursePower speedItemPower) {
                    this.gamePlayer = speedItemPower.getRole().getGamePlayer();
                    this.gameState = GameState.getInstance();
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
        private static final class DashPower extends Power implements Listener{

            private int killCount = 0;
            private final SpeedItemPower speedItemPower;
            private boolean fallDamage = true;

            public DashPower(@NonNull SpeedItemPower speedItemPower) {
                super("§cDash§7 (§bNeon§7)", new Cooldown(120), speedItemPower.getRole());
                this.speedItemPower = speedItemPower;
                setMaxUse(2);
                setShowInDesc(false);
                EventUtils.registerRoleEvent(this);
                getRole().getGamePlayer().getActionBarManager().addToActionBar("neon.dash.count", "§cDashs§7: §c2§7/§62");
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                if (this.speedItemPower.coursePower.runnable.speedBar <= 5) {
                    player.sendMessage("§7Vous n'avez pas asser d'énergie pour dash");
                    return false;
                }
                final Vector direction = player.getEyeLocation().getDirection();
                direction.setY(0.1);
                direction.multiply(1.8);
                player.setVelocity(direction);
                getRole().getGamePlayer().getActionBarManager().updateActionBar("neon.dash.count", "§cDashs§7: §c"+(getUse()+1)+"§7/§6"+getMaxUse());
                player.sendMessage("§7Vous avez utiliser un dash...");
                if (this.getUse() == getMaxUse()) {
                    getRole().getGamePlayer().getActionBarManager().removeInActionBar("neon.dash.count");
                    getRole().getGamePlayer().sendMessage("§cVous n'avez plus de dash...");
                }
                this.speedItemPower.coursePower.runnable.speedBar-=5;
                this.fallDamage = false;
                return true;
            }
            @EventHandler
            private void onDamage(EntityDamageEvent event) {
                if (event.getEntity().getUniqueId().equals(this.getRole().getPlayer())) {
                    if (!this.fallDamage) {
                        if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                            this.fallDamage = true;
                            event.setDamage(0);
                            event.setCancelled(true);
                        }
                    }
                }
            }
            @EventHandler
            private void onKill(UHCPlayerKillEvent event) {
                if (event.getGamePlayerKiller() != null) {
                    if (!event.getGamePlayerKiller().getUuid().equals(getRole().getPlayer()))return;
                    if (this.getUse() <= this.getMaxUse() && this.getUse() > 0) {
                        this.killCount++;
                        if (this.killCount == 2) {
                            setUse(getUse()-1);
                            this.killCount = 0;
                            getRole().getGamePlayer().getActionBarManager().updateActionBar("neon.dash.count", "§cDashs§7: §c"+(getUse())+"§7/§6"+getMaxUse());
                        } else {
                            event.getKiller().sendMessage("§7Plus que§c 1 kill§7 avant de gagner un§c dash§7.");
                        }
                    }
                }
            }
        }
    }
}
