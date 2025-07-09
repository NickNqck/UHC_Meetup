package fr.nicknqck.roles.ns.orochimaru;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.OrochimaruRoles;
import fr.nicknqck.roles.ns.orochimaru.edov2.OrochimaruV2;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;

public class Tayuya extends OrochimaruRoles {

    public Tayuya(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.MOYENNE;
    }

    @Override
    public String getName() {
        return "Tayuya";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Tayuya;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        addPower(new MarqueMaudite(this), true);
        addPower(new FluteDemoniaque(this), true);
        setChakraType(getRandomChakras());
        addKnowedRole(OrochimaruV2.class);
        super.RoleGiven(gameState);
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }
    private static class MarqueMaudite extends ItemPower {

        public MarqueMaudite(@NonNull RoleBase role) {
            super("Marque Maudite", new Cooldown(60*10), new ItemBuilder(Material.NETHER_STAR).setName("§5Marque Maudite"), role,
                    "§7Vous permet d'obtenir§9 Résistance I§7 pendant§c 3 minutes§7.",
                    "",
                    "§7Ce pouvoir à un coût de§c 1❤ permanent§7, à la mort d'§5Orochimaru§7 ce pouvoir ne coûtera plus rien.");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (getRole().getMaxHealth() < 3) {
                    player.sendMessage("§cImpossible, vous êtes trop §nfaible§r§c pour utiliser ce pouvoir");
                    return false;
                }
                if (!getRole().getGameState().getDeadRoles().contains(GameState.Roles.Orochimaru)) {
                    getRole().setMaxHealth(getRole().getMaxHealth()-2.0);
                    player.setMaxHealth(getRole().getMaxHealth());
                    player.setHealth(player.getMaxHealth());
                }
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60*3, 0, false, false), EffectWhen.NOW);
                return true;
            }
            return false;
        }
    }
    private static class FluteDemoniaque extends ItemPower implements Listener {

        public FluteDemoniaque(@NonNull RoleBase role) {
            super("Flûte Démoniaque", new Cooldown(60*10), new ItemBuilder(Material.STICK).setName("§5Flûte Démoniaque").addEnchant(Enchantment.DAMAGE_ALL, 1).hideEnchantAttributes(), role,
                    "§7Fait apparaitre§c 3 golems§7 qui auront pour but d'attaquer le joueur viser,",
                    "",
                    "§7Lorsque l'un de vos golems est attaquer par un joueur, ce dernier aura§c 5%§7 de§c chance§7 d'§cexploser",
                    "§7Si l'un de vos§c golems§7 est à plus de§c 35 blocs§7 de la§c cible§7 ou dans l'§beau§7 alors il sera téléporter autours de la personne"
            );
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final Player target = RayTrace.getTargetPlayer(player, 50, null);
                if (target == null) {
                    player.sendMessage("§cIl faut viser un joueur.");
                    return false;
                }
                for (int i = 1; i <= 3; i++) {
                    IronGolem ironGolem = (IronGolem) player.getWorld().spawnEntity(player.getLocation(), EntityType.IRON_GOLEM);
                    ironGolem.setPlayerCreated(true);
                    ironGolem.setCustomName("§5Golem de Tayuya "+i);
                    ironGolem.setCustomNameVisible(true);
                    ironGolem.setMaxHealth(80);
                    ironGolem.setHealth(ironGolem.getMaxHealth());
                    ironGolem.setRemoveWhenFarAway(false);
                    ironGolem.setTarget(target);
                    ironGolem.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1, false, false));
                    ironGolem.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1, false, false));
                    ironGolem.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
                    ironGolem.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 0, false, false));
                    new GolemRunnable(this, ironGolem, target);
                }
                player.sendMessage("§7Vos§c 3 golems§7 sont apparue, ils vont essayer d'aller se battre contre§c "+target.getDisplayName());
                return true;
            }
            return false;
        }
        @EventHandler
        private void onDamage(EntityDamageByEntityEvent event) {
            if (!(event.getEntity() instanceof IronGolem))return;
            final IronGolem golem = (IronGolem) event.getEntity();
            if (!golem.isCustomNameVisible())return;
            if (!golem.getCustomName().contains("Golem de Tayuya"))return;//donc c'est forcément MES golems
            if (RandomUtils.getOwnRandomProbability(5.0)) {
                event.getDamager().setVelocity(new Vector(0 ,1.8, 0));
                event.getDamager().sendMessage("§7Vous avez été frappé par une défense de§5 Tayuya§7.");
                if (event.getDamager() instanceof Player) {
                    getRole().getGamePlayer().sendMessage("§7L'un de vos golem à exploser§c "+((Player) event.getDamager()).getDisplayName());
                }
            }
        }
        private static class GolemRunnable extends BukkitRunnable {

            private final FluteDemoniaque fluteDemoniaque;
            private final IronGolem ironGolem;
            private Player target;
            private final UUID uuid;

            private GolemRunnable(FluteDemoniaque fluteDemoniaque, IronGolem ironGolem, Player target) {
                this.fluteDemoniaque = fluteDemoniaque;
                this.ironGolem = ironGolem;
                this.target = target;
                this.uuid = this.target.getUniqueId();
                runTaskTimerAsynchronously(Main.getInstance(), 20, 20);
            }

            @Override
            public void run() {
                if (!fluteDemoniaque.getRole().getGameState().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (ironGolem == null) {
                    cancel();
                    return;
                }
                if (target == null) {
                    final Player player = Bukkit.getPlayer(this.uuid);
                    if (player == null)return;
                    this.target = player;
                }
                if (!ironGolem.getType().isAlive()) {
                    cancel();
                    return;
                }
                if (!target.getWorld().equals(ironGolem.getWorld())) {
                    ironGolem.teleport(Loc.getRandomLocationAroundPlayer(this.target, 10));
                } else {
                    if (ironGolem.getLocation().distance(target.getLocation()) > 35.0 || ironGolem.getLocation().getBlock().getType().name().contains("WATER")) {
                        ironGolem.teleport(Loc.getRandomLocationAroundPlayer(this.target, 10));
                    }
                }
                this.ironGolem.setTarget(this.target);
            }
        }
    }
}