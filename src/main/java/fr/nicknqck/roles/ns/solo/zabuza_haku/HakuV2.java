package fr.nicknqck.roles.ns.solo.zabuza_haku;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.NSRoles;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class HakuV2 extends NSRoles {

    public HakuV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.MOYENNE;
    }

    @Override
    public String getName() {
        return "Haku";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Haku;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Zabuza_et_Haku;
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .addCustomLine("§7A la mort de§b Zabuza§7, vous obtiendrez l'effet§c Force I§7 de manière§c permanente§7 ainsi qu'une réduction de cooldown sur votre§d dome Hyôton")
                .getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false), EffectWhen.PERMANENT);
        givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 0, false, false), EffectWhen.PERMANENT);
        addKnowedRole(ZabuzaV2.class);
        getGamePlayer().startChatWith("§bHaku:", "!", ZabuzaV2.class);
        addPower(new HyotonPower(this));
        setChakraType(Chakras.SUITON);
        super.RoleGiven(gameState);
    }
    private static class HyotonPower extends Power {

        private final List<Block> blockList = new ArrayList<>();
        private Cooldown cooldown;
        private boolean zabuzaDEAD = false;

        public HyotonPower(@NonNull RoleBase role) {
            super("Hyoton", null, role);
            this.cooldown = new Cooldown(60*8);
            setShowInDesc(false);
            role.addPower(new HyotonItem(this), true);
            role.addPower(new HyotonCommand(this));
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            return true;
        }
        private static class HyotonItem extends ItemPower implements Listener {

            private final HyotonPower power;
            private final Cooldown tpCD = new Cooldown(2);

            public HyotonItem(@NonNull final HyotonPower power) {
                super("Hyôton", power.cooldown, new ItemBuilder(Material.NETHER_STAR).setName("§bHyôton"), power.getRole(),
                        "§7Vous permet de crée un§b dôme de glace§7 pendant§c 2 minutes§7 vous donnant l'effet§c Force I§7 ainsi que§a NoFall");
                setWorkWhenInCooldown(true);
                EventUtils.registerRoleEvent(this);
                this.power = power;
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame))return;
                    if (GameState.getInstance().getAttributedRole().contains(GameState.Roles.Zabuza))return;
                    final Cooldown old = this.power.cooldown;
                    final Cooldown nouveau = new Cooldown(60*5);
                    if (old.isInCooldown()) {
                        nouveau.setActualCooldown(old.getCooldownRemaining());
                    }
                    this.power.cooldown = nouveau;
                    this.getRole().givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.PERMANENT);
                    this.power.zabuzaDEAD = true;
                }, 20*10);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                if (getInteractType().equals(InteractType.INTERACT)) {
                    if (!getCooldown().isInCooldown()) {
                        for (@NonNull final Location location : sphere(player.getLocation())) {
                            if (location.getBlock() == null)continue;
                            final Material type = location.getBlock().getType();
                            if (type.equals(Material.AIR) || type.equals(Material.YELLOW_FLOWER) ||type.equals(Material.RED_ROSE) || type.equals(Material.LONG_GRASS)) {
                                location.getBlock().setType(Material.PACKED_ICE);
                                this.power.blockList.add(location.getBlock());
                            }
                        }
                        player.sendMessage("§7Activation du§b Hyôton§7.");
                        new HyotonRunnable(getRole().getGameState(), this);
                        return true;
                    } else {
                        if (!this.power.blockList.isEmpty()) {
                            if (this.tpCD.isInCooldown()) {
                                return false;
                            }
                            final RayTrace rayTrace = new RayTrace(player.getEyeLocation().toVector(), player.getEyeLocation().getDirection());
                            final List<Vector> positions = rayTrace.traverse(30, 0.2D);
                            Block prevBlock = null;
                            for (Vector vector : positions) {
                                final Location position = vector.toLocation(player.getWorld());
                                if (position.getBlock().getType().equals(Material.AIR) || prevBlock == null) {
                                    prevBlock = position.getBlock();
                                    continue;
                                }
                                prevBlock.getLocation().setPitch(player.getEyeLocation().getPitch());
                                prevBlock.getLocation().setYaw(player.getEyeLocation().getYaw());
                                this.tpCD.use();
                                player.teleport(prevBlock.getLocation());
                                break;
                            }
                        } else {
                            player.sendMessage("§bVous êtes en cooldown: §c"+ StringUtils.secondsTowardsBeautiful(this.getCooldown().getCooldownRemaining()));
                        }
                    }
                }
                return false;
            }
            @EventHandler
            private void BlockBreakEvent(@NonNull final BlockBreakEvent event) {
                if (this.power.blockList.contains(event.getBlock())) {
                    event.setCancelled(true);
                }
            }
            @EventHandler
            private void onDeath(@NonNull final UHCDeathEvent event) {
                if (event.getRole() instanceof ZabuzaV2 && !this.power.zabuzaDEAD) {
                    final Cooldown old = this.power.cooldown;
                    final Cooldown nouveau = new Cooldown(60*5);
                    if (old.isInCooldown()) {
                        nouveau.setActualCooldown(old.getCooldownRemaining());
                    }
                    this.power.cooldown = nouveau;
                    this.getRole().givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.PERMANENT);
                    this.power.zabuzaDEAD = true;
                }
            }
            @EventHandler
            private void onFall(EntityDamageEvent event) {
                if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL) && event.getEntity().getUniqueId().equals(getRole().getPlayer()) && !this.power.blockList.isEmpty()) {
                    event.setCancelled(true);
                }
            }
            @EventHandler
            private void onEndGame(@NonNull final EndGameEvent event) {
                if (!this.power.blockList.isEmpty()) {
                    for (final Block block : this.power.blockList) {
                        block.setType(Material.AIR);
                    }
                    this.power.blockList.clear();
                }
            }
            private Set<Location> sphere(Location location){
                Set<Location> blocks = new HashSet<>();
                World world = location.getWorld();
                int X = location.getBlockX();
                int Y = location.getBlockY();
                int Z = location.getBlockZ();
                int radiusSquared = 20 * 20;
                for (int x = X - 20; x <= X + 20; x++) {
                    for (int y = Y - 20; y <= Y + 20; y++) {
                        for (int z = Z - 20; z <= Z + 20; z++) {
                            if ((X - x) * (X - x) + (Y - y) * (Y - y) + (Z - z) * (Z - z) <= radiusSquared) {
                                Location block = new Location(world, x, y, z);
                                blocks.add(block);
                            }
                        }
                    }
                }
                return makeHollow(blocks);
            }
            private Set<Location> makeHollow(Set<Location> blocks){
                Set<Location> edge = new HashSet<>();
                for (Location l : blocks) {
                    World w = l.getWorld();
                    int X = l.getBlockX();
                    int Y = l.getBlockY();
                    int Z = l.getBlockZ();
                    Location front = new Location(w, X + 1, Y, Z);
                    Location back = new Location(w, X - 1, Y, Z);
                    Location left = new Location(w, X, Y, Z + 1);
                    Location right = new Location(w, X, Y, Z - 1);
                    Location top = new Location(w, X, Y + 1, Z);
                    Location bottom = new Location(w, X, Y - 1, Z);
                    if (!(blocks.contains(front) && blocks.contains(back) && blocks.contains(left) && blocks.contains(right) && blocks.contains(top) && blocks.contains(bottom))) {
                        edge.add(l);
                    }
                }
                return edge;
            }
            private static class HyotonRunnable extends BukkitRunnable {

                private final GameState gameState;
                private final HyotonItem hyoton;
                private int timeLeft = 60*3;

                private HyotonRunnable(GameState gameState, HyotonItem hyoton) {
                    this.gameState = gameState;
                    this.hyoton = hyoton;
                    runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
                    hyoton.getRole().getGamePlayer().getActionBarManager().addToActionBar("haku.hyoton", "§bHyôton:§c 3 minutes");
                }

                @Override
                public void run() {
                    if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                        cancel();
                        return;
                    }
                    if (this.hyoton.power.blockList.isEmpty()) {
                        this.hyoton.getRole().getGamePlayer().getActionBarManager().removeInActionBar("haku.hyoton");
                        cancel();
                        return;
                    }
                    this.timeLeft--;
                    this.hyoton.getRole().getGamePlayer().getActionBarManager().updateActionBar("haku.hyoton", "§bHyôton:§c "+StringUtils.secondsTowardsBeautiful(this.timeLeft));
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        this.hyoton.getRole().givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.NOW);
                        if (this.timeLeft <= 0) {
                            this.hyoton.getRole().getGamePlayer().getActionBarManager().removeInActionBar("haku.hyoton");
                            for (final Block block : this.hyoton.power.blockList) {
                                block.setType(Material.AIR);
                            }
                            this.hyoton.power.blockList.clear();
                            this.hyoton.getRole().getGamePlayer().sendMessage("§7Votre§b Hyôton§7 s'arrête.");
                            cancel();
                        }
                    });
                }
            }
        }
        private static class HyotonCommand extends CommandPower {

            private final HyotonPower power;

            public HyotonCommand(@NonNull HyotonPower hyotonPower) {
                super("/ns hyoton", "hyoton", null, hyotonPower.getRole(), CommandType.NS,
                        "§7Vous permet de forcer la fermeture du§b dôme Hyôton");
                this.power = hyotonPower;
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                if (!this.power.blockList.isEmpty()) {
                    player.sendMessage("§7Vous avez forcez l'arrêt de votre technique§b Hyôton§7.");
                    for (final Block block : this.power.blockList) {
                        block.setType(Material.AIR);
                    }
                    this.power.blockList.clear();
                    return true;
                }
                return false;
            }
        }
    }
}