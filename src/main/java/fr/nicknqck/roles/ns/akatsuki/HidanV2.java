package fr.nicknqck.roles.ns.akatsuki;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.RoleGiveEvent;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.AkatsukiRoles;
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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HidanV2 extends AkatsukiRoles implements Listener {

    private boolean kakuzuDeath = false;

    public HidanV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.PEUINTELLIGENT;
    }

    @Override
    public String getName() {
        return "Hidan";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Hidan;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        super.RoleGiven(gameState);
        addKnowedRole(KakuzuV2.class);
        new ForceRunnable(this);
        EventUtils.registerRoleEvent(this);
        addPower(new RituelPower(this), true);
        setChakraType(getRandomChakras());
    }

    @EventHandler
    private void onDeath(final UHCDeathEvent event) {
        if (event.getRole() == null)return;
        if (event.getRole() instanceof KakuzuV2) {
            this.kakuzuDeath = true;
            givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
            getGamePlayer().sendMessage("§cKakuzu§7 est mort ! En son honneur vous devenez plus puissant, vous obtenez l'effet§c Force I permanent§7.");
        }
    }
    @EventHandler
    private void onRoleGive(final RoleGiveEvent event) {
        if (!event.isEndGive())return;
        if (event.getGameState().getDeadRoles().contains(GameState.Roles.Kakuzu)) {
            this.kakuzuDeath = true;
            givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
            getGamePlayer().sendMessage("§cKakuzu§7 n'est pas dans la partie, vous obtenez donc directement l'effet§c Force I permanent§7.");
        }
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .addCustomLine(this.kakuzuDeath ? "" : "§7Vous possédez l'effet§c Force I§7 proche de§c Kakuzu§7.")
                .getText();
    }
    private static class RituelPower extends ItemPower implements Listener {

        private final ItemStack sword;
        private UUID uuidTarget = null;
        private boolean inRituel = false;
        private Location location = null;

        public RituelPower(@NonNull RoleBase role) {
            super("Rituel de Jashin", new Cooldown(60*5), new ItemBuilder(Material.NETHER_STAR).setName("§cRituel de Jashin"), role,
                    "§7Lorsque vous frappez un§b joueur§7 avec votre§c Faux§7, vous pouvez activer votre pouvoir afin de renvoyer",
                    "§7les§c dégâts§7 que vous subissez dans votre§c zone§7 d'activation à§b celui-ci§7.",
                    "",
                    "§7Si jamais votre§c cible§7 n'est plus dans la même§c dimension§7 que vous ou qu'elle se situe à plus de§c 100 blocs§7",
                    "§7alors, elle ne subira pas les dégâts (uniquement si elle à moins de§c 1❤§7)",
                    "",
                    "§7Si elle se situe à plus de§c 50 blocs");
            this.sword = new ItemBuilder(Material.DIAMOND_SWORD).setName("§cFaux§7 (§cRituel de Jashin§7)").addEnchant(Enchantment.DAMAGE_ALL, 4).setUnbreakable(true)
                    .setLore("§7Vous permet de déclencher le§c Rituel de Jashin§7.").toItemStack();
            role.getGamePlayer().addItems(this.sword);
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (!inRituel) {
                    if (this.uuidTarget == null) {
                        player.sendMessage("§cIl faut d'abord frappé un joueur avec votre Faux !");
                        return false;
                    }
                    final Player target = Bukkit.getPlayer(this.uuidTarget);
                    if (target == null) {
                        player.sendMessage("§cVotre cible n'est pas connecté !");
                        return false;
                    }
                    if (!target.getWorld().equals(player.getWorld())) {
                        player.sendMessage("§c"+target.getDisplayName()+"§7 n'est pas dans la même dimension que vous, impossible de faire le§c rituel§7.");
                        return false;
                    }
                    new RituelRunnable(this, target, player.getLocation()).runTaskTimerAsynchronously(getPlugin(), 0, 20);

                    return true;
                }
            }
            return false;
        }
        @EventHandler
        private void onTap(final EntityDamageByEntityEvent event) {
            if (!(event.getDamager() instanceof Player))return;
            if (!(event.getEntity() instanceof Player))return;
            if (!event.getDamager().getUniqueId().equals(getRole().getPlayer()))return;
            if (((Player) event.getDamager()).getItemInHand() == null)return;
            if (!((Player) event.getDamager()).getItemInHand().isSimilar(this.sword))return;
            if (!this.inRituel) {
                if (this.uuidTarget == null) {
                    this.uuidTarget = event.getEntity().getUniqueId();
                    event.getDamager().sendMessage("§7Votre§c Rituel§7 est prêt à agir contre§c "+((Player) event.getEntity()).getDisplayName());
                } else {
                    if (this.uuidTarget.equals(event.getEntity().getUniqueId()))return;
                    this.uuidTarget = event.getEntity().getUniqueId();
                    event.getDamager().sendMessage("§7Votre§c Rituel§7 est maintenant prêt à agir contre§c "+((Player) event.getEntity()).getDisplayName());
                }
            }
        }
        @EventHandler(priority = EventPriority.HIGHEST)
        private void onDamage(final EntityDamageEvent event) {
            if (!event.getEntity().getUniqueId().equals(getRole().getPlayer()))return;
            if (!this.inRituel)return;
            if (this.location == null)return;
            if (this.uuidTarget == null)return;
            final Player target = Bukkit.getPlayer(this.uuidTarget);
            if (target == null)return;
            if (this.location.distance(event.getEntity().getLocation()) > 2.0) {
                event.getEntity().sendMessage("§7Trop loin");
                return;
            }
            event.setDamage(0);
            if (target.getWorld() != event.getEntity().getWorld())return;
            if (target.getLocation().distance(event.getEntity().getLocation()) > 100)return;
            if (target.getLocation().distance(event.getEntity().getLocation()) > 50 && target.getHealth() < 2.0)return;
            final double damage = event.getOriginalDamage(EntityDamageEvent.DamageModifier.BASE);
            target.damage(damage, event.getEntity());

        }
        private final static class RituelRunnable extends BukkitRunnable {

            private final RituelPower rituelPower;
            private Player target;
            private final UUID uuidTarget;
            private final Location initLocation;
            private int timeLeft;

            private RituelRunnable(RituelPower rituelPower, Player target, Location initLocation) {
                this.rituelPower = rituelPower;
                this.target = target;
                this.uuidTarget = target.getUniqueId();
                this.initLocation = initLocation;
                this.timeLeft = 10;
                this.rituelPower.inRituel = true;
                this.rituelPower.location = initLocation;
            }

            @Override
            public void run() {
                if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (this.timeLeft <= 0) {
                    this.rituelPower.getRole().getGamePlayer().getActionBarManager().removeInActionBar("hidanv2.ritueltime");
                    this.rituelPower.inRituel = false;
                    this.rituelPower.location = null;
                    cancel();
                    return;
                }
                this.timeLeft--;
                final Player owner = Bukkit.getPlayer(this.rituelPower.getRole().getPlayer());
                if (this.target == null) {
                    final Player p = Bukkit.getPlayer(this.uuidTarget);
                    if (p != null){
                        this.target = p;
                    }
                }
                if (owner == null || this.target == null)return;
                MathUtil.sendCircleParticle(EnumParticle.FLAME, this.initLocation, 2, 20);
                this.rituelPower.getRole().getGamePlayer().getActionBarManager().addToActionBar("hidanv2.ritueltime", "§bTemp restant du§c Rituel de Jashin§b:§c "+this.timeLeft+"s");
            }
        }
    }
    private static class ForceRunnable extends BukkitRunnable {

        private final HidanV2 hidan;

        private ForceRunnable(HidanV2 hidan) {
            this.hidan = hidan;
            runTaskTimerAsynchronously(Main.getInstance(), 20, 20);
        }

        @Override
        public void run() {
            if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (!this.hidan.getGamePlayer().isAlive() || !this.hidan.getGamePlayer().isOnline()) {
                return;
            }
            final List<GamePlayer> gamePlayerList = Loc.getNearbyGamePlayers(this.hidan.getGamePlayer().getLastLocation(), 15);
            if (gamePlayerList.isEmpty())return;
            for (final GamePlayer gamePlayer : gamePlayerList) {
                if (!gamePlayer.isOnline())return;
                if (!gamePlayer.isAlive())return;
                if (gamePlayer.getRole() == null)return;
                if ((gamePlayer.getRole() instanceof KakuzuV2)) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> this.hidan.givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 40, 0, false, false), EffectWhen.NOW));
                    break;
                }
            }
        }
    }
}