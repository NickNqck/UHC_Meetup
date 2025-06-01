package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class NarutoV2 extends ShinobiRoles {

    public NarutoV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.PEUINTELLIGENT;
    }

    @Override
    public String getName() {
        return "Naruto";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Naruto;
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new Rasengan(this), true);
        addPower(new KyubiPower(this), true);
        addPower(new NSClone(this));
        setChakraType(Chakras.FUTON);
        setMaxHealth(getMaxHealth()+4.0);
        setCanBeHokage(true);
        if (owner != null) {
            owner.setHealth(owner.getHealth()+4.0);
        }
        super.RoleGiven(gameState);
    }
    private static class KyubiPower extends ItemPower implements Listener{

        private final Cooldown cooldown;

        private KyubiPower(@NonNull RoleBase role) {
            super("Kyubi", new Cooldown(60*20), new ItemBuilder(Material.INK_SACK).setDurability(14).setName("§6Kyubi"), role,
                    "§7Vous donne les effets§c Force I§7 et§e Speed II§7 pendant§c 5 minutes§7, également, vous permet en infligeant un coup à un joueur vous lui infligerez un \"§fCoup Spécial§7\".",
                    "",
                    "§fCoup Spécial:§7 (1x/30s) Vous permet d'infliger un malus au joueur frappé entre: ",
                    "",
                    "§8 -§7 Le coup mettra la personne en§c feu§7.",
                    "",
                    "§8 -§7 Le coup infligera§c 1/2❤§7 de§c dégâts§7 supplémentaire.");
            this.cooldown = new Cooldown(30);
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                player.sendMessage("§7Vous activez les effets de§6 Kyubi§7.");
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*5, 1, false, false), EffectWhen.NOW);
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*60*5, 0, false, false), EffectWhen.NOW);
                return true;
            }
            return false;
        }
        @EventHandler
        private void onEntityDamageByEntity(@NonNull final EntityDamageByEntityEvent event) {
            if (!event.getDamager().getUniqueId().equals(getRole().getPlayer()))return;
            if (!getCooldown().isInCooldown())return;
            if (!(event.getEntity() instanceof Player))return;
            if (!(event.getDamager() instanceof Player))return;
            if (getCooldown().getCooldownRemaining() >= 60*15) {
                if (this.cooldown.isInCooldown())return;
                if (Main.RANDOM.nextInt(100) <= 50) {
                    event.getEntity().setFireTicks(200);
                    event.getDamager().sendMessage("§7La§c rage§7 de§6 Kyubi§7 enflamment§c "+((Player) event.getEntity()).getDisplayName());
                    this.cooldown.use();
                } else {
                    ((Player) event.getEntity()).setHealth(Math.max(((Player) event.getEntity()).getHealth()-1.0, 1.0));
                    event.getDamager().sendMessage("§7La§c rage§7 de§6 Kyubi§7 inflige§c 1/2❤ supplémentaire de dégât.");
                    this.cooldown.use();
                }
            }
        }
    }
    private static class Rasengan extends ItemPower {

        protected Rasengan(@NonNull RoleBase role) {
            super("Rasengan", new Cooldown(120), new ItemBuilder(Material.NETHER_STAR).setName("§aRasengan"), role,
                    "§7En frappant un joueur, vous permet§a repousse le joueur§7 en lui infligeant§c 2❤§7 de§c dégâts");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.ATTACK_ENTITY)) {
                @NonNull final UHCPlayerBattleEvent uhcEvent = (UHCPlayerBattleEvent) map.get("event");
                @NonNull final EntityDamageByEntityEvent event = uhcEvent.getOriginEvent();
                if (!(event.getEntity() instanceof Player))return false;
                ((Player) event.getEntity()).setHealth(Math.max(1.0, ((Player) event.getEntity()).getHealth()-4.0));
                MathUtil.sendParticle(EnumParticle.EXPLOSION_LARGE, event.getEntity().getLocation());
                Location loc = event.getEntity().getLocation().clone();
                loc.setX(loc.getX()+Math.cos(Math.toRadians(-(((Player)event.getEntity())).getEyeLocation().getYaw()+90)));
                loc.setZ(loc.getZ()+Math.sin(Math.toRadians(((Player)event.getEntity()).getEyeLocation().getYaw()-90)));
                loc.setPitch(0);
                event.getEntity().setVelocity(loc.getDirection().multiply(3.0));
                player.sendMessage("§aRASENGAN !");
                event.getEntity().sendMessage("§7Vous avez été toucher par un§a Rasengan");
                event.setCancelled(true);
                return true;
            }
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (((PlayerInteractEvent) map.get("event")).getAction().name().contains("RIGHT")){
                    player.sendMessage("§7Il faut frapper un joueur pour déclencher le§a Rasengan");
                }
                return false;
            }
            return false;
        }
    }
    private static class NSClone extends CommandPower implements Listener {

        private CloneRunnable cloneRunnable = null;

        public NSClone(@NonNull RoleBase role) {
            super("/ns clone", "clone", new Cooldown(60*10), role, CommandType.NS,
                    "§7Vous permet de faire apparaitre un§a villageois§7 portant le nom de§a Naruto§7, tant qu'il est en vie il accumule du temp,",
                    "§7en refaisant la commande, vous obtiendrez les effets§e Speed I§7 et§c Force I§7 pendant le temp accumulé");
            EventUtils.registerRoleEvent(this);
            setWorkWhenInCooldown(true);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (this.cloneRunnable != null) {
                this.cloneRunnable.stop();
                this.cloneRunnable = null;
                return true;
            }
            if (getCooldown().isInCooldown()) {
                player.sendMessage("§bVous êtes en cooldown pour encore: §c"+StringUtils.secondsTowardsBeautiful(getCooldown().getCooldownRemaining()));
                return false;
            }
            final Villager villager = (Villager) player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
            villager.setAdult();
            villager.setProfession(Villager.Profession.PRIEST);
            villager.setCustomName("§aNaruto");
            villager.setCustomNameVisible(true);
            villager.setRemoveWhenFarAway(false);
            villager.setMaxHealth(100.0);
            villager.setHealth(villager.getMaxHealth());
            villager.setMetadata("narutoClone", new FixedMetadataValue(Main.getInstance(), player.getUniqueId()));
            this.cloneRunnable = new CloneRunnable(villager, player.getLocation(), getRole().getGameState(), this);
            return true;
        }
        @EventHandler
        private void onEntityDeath(@NonNull final EntityDeathEvent event) {
            if (!(event.getEntity() instanceof Villager))return;
            if (!event.getEntity().getCustomName().equals("§aNaruto"))return;
            if (!((Villager) event.getEntity()).getProfession().equals(Villager.Profession.PRIEST))return;
            if (this.cloneRunnable == null)return;
            if (!this.getRole().getGamePlayer().getActionBarManager().containsKey("naruto.clone"))return;
            this.cloneRunnable.death(((Villager)event.getEntity()));
        }
        private static class CloneRunnable extends BukkitRunnable {

            private final Villager villager;
            private final Location initLocation;
            private final GameState gameState;
            private final NSClone nsClone;
            private int accumuledTime;

            private CloneRunnable(Villager villager, Location initLocation, GameState gameState, NSClone nsClone) {
                this.villager = villager;
                this.initLocation = initLocation;
                this.gameState = gameState;
                this.nsClone = nsClone;
                this.accumuledTime = 0;
                nsClone.getRole().getGamePlayer().getActionBarManager().addToActionBar("naruto.clone", "§bTemps accumulé:§c 0 secondes");
                runTaskTimerAsynchronously(Main.getInstance(), 20, 20);
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (this.villager == null) {
                    return;
                }
                if (this.villager.isDead()) {
                    return;
                }
                this.villager.teleport(this.initLocation);
                this.accumuledTime = Math.min(60*5, this.accumuledTime+1);
                this.nsClone.getRole().getGamePlayer().getActionBarManager().updateActionBar("naruto.clone", "§bTemps accumulé:§c "+ StringUtils.secondsTowardsBeautiful(this.accumuledTime));
            }

            public void death(Villager villager) {
                this.nsClone.getRole().getGamePlayer().getActionBarManager().removeInActionBar("naruto.clone");
                final StringBuilder sb = new StringBuilder("§7Votre§a clone§7 est§c mort§7, voici la liste des joueurs autours de votre§a clone\n\n");
                for (@NonNull final Player aroundPlayers : Loc.getNearbyPlayersExcept(villager, 30)) {
                    sb.append("§8 - §c").append(aroundPlayers.getDisplayName()).append("\n");
                }
                this.nsClone.getRole().getGamePlayer().sendMessage(sb.toString());
                this.nsClone.cloneRunnable = null;
                cancel();
            }

            public void stop() {
                if (this.villager == null) {
                    this.nsClone.getRole().getGamePlayer().sendMessage("§7Votre§a clone§7 est§c mort§7, impossible de récupérer son énergie");
                    return;
                }
                if (this.villager.isDead()) {
                    this.nsClone.getRole().getGamePlayer().sendMessage("§7Votre§a clone§7 est§c mort§7, impossible de récupérer son énergie");
                    return;
                }
                this.nsClone.getRole().getGamePlayer().getActionBarManager().removeInActionBar("naruto.clone");
                this.nsClone.getRole().givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*this.accumuledTime, 0, false, false), EffectWhen.NOW);
                this.nsClone.getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*this.accumuledTime, 0, false, false), EffectWhen.NOW);
                this.nsClone.getRole().getGamePlayer().sendMessage("§7Vous récupérez les effets que votre§a Clone§7 a accumulé.");
                this.villager.damage(9999);
                cancel();
            }
        }
    }
}