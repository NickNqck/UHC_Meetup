package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class KurenaiV2 extends ShinobiRoles {

    public KurenaiV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.INTELLIGENT;
    }

    @Override
    public String getName() {
        return "Kurenai";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Kurenai;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new GenjutsuDesBoisPower(this), true);
        addPower(new GenjutsuTemporel(this), true);
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
            if (gameState.getAttributedRole().contains(Roles.Asuma)) {
                new ForceRunneable(this).runTaskTimerAsynchronously(Main.getInstance(), 20, 20);
            }
        }, 20);
        setChakraType(Chakras.KATON);
        super.RoleGiven(gameState);
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .getText();
    }
    private static class GenjutsuDesBoisPower extends ItemPower {

        public GenjutsuDesBoisPower(@NonNull RoleBase role) {
            super("Genjutsu des bois", new Cooldown(60*5), new ItemBuilder(Material.NETHER_STAR).setName("§aGenjutsu des bois"), role,
                    "§7En ciblant un joueur, vous permet de le§a stun§7 pendant§c 5 secondes§7.",
                    "",
                    "§7Après ceci, vous serez téléporter derrière la personne, également, il subira§c 3❤§7 de§c dégâts§7.");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                Player target = RayTrace.getTargetPlayer(player, 30, null);
                if (target == null) {
                    player.sendMessage("§cIl faut viser un joueur !");
                    return false;
                }
                player.sendMessage("§7Vous utiliser votre§c Genjutsu§7 sur§a "+target.getDisplayName());
                player.setGameMode(GameMode.SPECTATOR);
                getRole().getGameState().getGamePlayer().get(target.getUniqueId()).stun(5*20);
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    Location loc = target.getLocation();
                    loc.setX(loc.getX()+Math.cos(Math.toRadians(-target.getEyeLocation().getYaw()+90)));
                    loc.setZ(loc.getZ()+Math.sin(Math.toRadians(target.getEyeLocation().getYaw()-90)));
                    loc.setPitch(0);
                    player.setGameMode(GameMode.SURVIVAL);
                    player.sendMessage("§7Votre§c Genjutsu§7 est terminer.");
                    player.teleport(loc);
                }, 100);
                return true;
            }
            return false;
        }
    }
    private static class GenjutsuTemporel extends ItemPower {

        private final KurenaiV2 kurenaiV2;

        public GenjutsuTemporel(@NonNull KurenaiV2 kurenaiV2) {
            super("Genjutsu Temporel", new Cooldown(60*5), new ItemBuilder(Material.NETHER_STAR).setName("§cGenjutsu Temporel"), kurenaiV2,
                    "§7Vous permet de crée un point de téléportation qui restera actif pendant§c 60 secondse§7,",
                    "",
                    "§7Au bout de§c 60 secondes§7 ou si vous§c mourrez§7 vous§a ressusciterez§7 à votre point de téléportation avec le même stuff qu'à l'activation");
            this.kurenaiV2 = kurenaiV2;
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                player.sendMessage("§7Vous avez utiliser votre§c Genjutsu temporel§7.");
                new KurenaiRunnable(player, this.kurenaiV2).runTaskTimer(Main.getInstance(), 0, 20);
                return true;
            }
            return false;
        }
        private static class KurenaiRunnable extends BukkitRunnable implements Listener {

            private final Location initLocation;
            private int timeRemaining = 60;
            private final UUID owner;
            private final ItemStack[] armors;
            private final KurenaiV2 kurenai;
            private final Map<Integer, ItemStack> getContents = new HashMap<>();

            private KurenaiRunnable(Player player, KurenaiV2 kurenai) {
                this.initLocation = player.getLocation().clone();
                this.owner = player.getUniqueId();
                EventUtils.registerEvents(this);
                this.armors = player.getInventory().getArmorContents();
                this.kurenai = kurenai;
                this.kurenai.getGamePlayer().setCanRevive(true);
                int i = 0;
                for (ItemStack stack : player.getInventory().getContents()) {
                    if (stack != null && stack.getType() != Material.AIR){
                        System.out.println("Amount: "+stack.getAmount() + ", hasItemMeta "+stack.hasItemMeta()+", Type: "+stack.getType());
                        getContents.put(i, stack);
                    }
                    i++;
                }
                kurenai.getGamePlayer().getActionBarManager().addToActionBar("kurenai.runnable", "§bTemp restant avant fin du§c Genjutsu§b: §c"+ StringUtils.secondsTowardsBeautiful(timeRemaining));
            }
            @Override
            public void run() {
                if (kurenai.getGameState().getServerState() != GameState.ServerStates.InGame){
                    cancel();
                    return;
                }
                if (timeRemaining <= 0){
                    if (Bukkit.getPlayer(owner) != null){
                        Player player = Bukkit.getPlayer(owner);
                        player.sendMessage("§7Votre§c Genjutsu§7 est maintenant terminé.");
                        player.setGameMode(GameMode.SURVIVAL);
                        player.teleport(initLocation);
                        player.getInventory().clear();
                        player.getInventory().setArmorContents(armors);
                        getContents.keySet().stream().filter(z -> getContents.get(z).getAmount() > 0).filter(z -> getContents.get(z).getAmount() <= 64).forEach(z -> player.getInventory().setItem(z, getContents.get(z)));
                        kurenai.getGamePlayer().setCanRevive(false);
                        player.updateInventory();
                        player.setHealth(player.getMaxHealth());
                    }
                    cancel();
                    return;
                }
                MathUtil.spawnMoovingCircle(EnumParticle.REDSTONE, initLocation, 1, 20, owner);
                timeRemaining--;
                this.kurenai.getGamePlayer().getActionBarManager().updateActionBar("kurenai.runnable", "§bTemp restant avant fin du§c Genjutsu§b: §c"+ StringUtils.secondsTowardsBeautiful(timeRemaining));
            }
            @EventHandler
            private void onUHCPlayerDie(UHCPlayerKillEvent e){
                if (e.getVictim().getUniqueId().equals(owner) && timeRemaining > 0){
                    e.getVictim().getInventory().clear();
                    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                        timeRemaining = 0;
                        e.getGameState().addInSpecPlayers(e.getVictim());
                        e.getGameState().RevivePlayer(e.getVictim());
                        if (e.getVictim().isDead()){
                            e.getVictim().spigot().respawn();
                        }
                        e.getVictim().teleport(initLocation);
                        e.getVictim().getInventory().clear();
                        if (Main.isDebug()){
                            System.out.println(timeRemaining+ " string "+StringUtils.secondsTowardsBeautiful(timeRemaining));
                        }
                    }, 21);
                }
            }
        }
    }
    private static class ForceRunneable extends BukkitRunnable {
        private final KurenaiV2 kurenai;
        public ForceRunneable(KurenaiV2 kurenai) {
            this.kurenai = kurenai;
        }

        @Override
        public void run() {
            if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (!kurenai.getGamePlayer().isAlive()) return;
            List<Player> aList = new ArrayList<>(kurenai.getListPlayerFromRole(Asuma.class));
            if (aList.isEmpty()) {
                return;
            }
            for (Player p : aList) {
                if (Loc.getNearbyPlayersExcept(p, 15).contains(kurenai.owner)) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> kurenai.owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), true));
                }
            }
        }
    }
}