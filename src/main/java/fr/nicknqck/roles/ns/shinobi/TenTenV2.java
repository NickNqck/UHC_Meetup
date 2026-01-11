package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
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
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TenTenV2 extends ShinobiRoles {

    public TenTenV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.MOYENNE;
    }

    @Override
    public String getName() {
        return "Tenten";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.TenTen;
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        setChakraType(getRandomChakras());
        addPower(new KunaiItem(this), true);
        addPower(new ParcheminItem(this), true);
        super.RoleGiven(gameState);
    }
    private static class KunaiItem extends ItemPower implements Listener {

        public KunaiItem(@NonNull final RoleBase role) {
            super("§aKunai§r", new Cooldown(45), new ItemBuilder(Material.SNOW_BALL).setName("§aKunai"), role,
                    "§7La personne touché par votre§a Kunai§7 perdra immédiatement§c 1❤§7 (il pourrait en mourir).");
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            return map.containsKey("snowball") && map.get("snowball") instanceof Snowball && ((Snowball) map.get("snowball")).hasMetadata(getRole().StringID);
        }
        @EventHandler
        private void onProjectileLaunch(@NonNull final ProjectileLaunchEvent event) {
            if (event.getEntity() instanceof Snowball) {
                Snowball snow = (Snowball) event.getEntity();
                if (!(snow.getShooter() instanceof Player))return;
                if (!((Player) snow.getShooter()).getUniqueId().equals(getRole().getPlayer()))return;
                if (getCooldown().isInCooldown()) {
                    sendCooldown((Player) snow.getShooter());
                    getRole().giveItem((Player) snow.getShooter(), false, getItem());
                    event.setCancelled(true);
                } else {
                    snow.setMetadata(getRole().StringID, new FixedMetadataValue(Main.getInstance(), snow.getShooter()));
                }
            }
        }
        @EventHandler
        private void onProjectileHit(@NonNull final ProjectileHitEvent event) {
            if (event.getEntity() instanceof Snowball) {
                Snowball snow = (Snowball) event.getEntity();
                if (snow.hasMetadata(getRole().StringID) && snow.getShooter() instanceof Player && ((Player) snow.getShooter()).getUniqueId().equals(getRole().getPlayer())) {
                    getRole().giveItem((Player) snow.getShooter(), false, getItem());
                }
            }
        }
        @EventHandler
        private void onDamageByEntity(@NonNull final EntityDamageByEntityEvent event) {
            if (event.getDamager() instanceof Snowball && event.getEntity() instanceof Player) {
                final Player victim = (Player) event.getEntity();
                final Snowball snow = (Snowball) event.getDamager();
                if (snow.getShooter() instanceof Player) {
                    final Player damager = (Player) snow.getShooter();
                    if (damager.getUniqueId().equals(getRole().getPlayer())) {
                        if (snow.hasMetadata(getRole().StringID)) {
                            if (!getCooldown().isInCooldown()) {
                                final Map<String, Object> map = new HashMap<>();
                                map.put("snowball", snow);
                                if (checkUse(damager, map)) {
                                    getRole().damage(victim, 2.0, 1, damager, true);
                                    damager.sendMessage(victim.getDisplayName()+"§7 à subit les effets de votre§a Kunai");
                                    victim.sendMessage("§7Vous avez été toucher par le§a Kunai§7 de§a Tenten");
                                    snow.removeMetadata(getRole().StringID, Main.getInstance());
                                    getRole().giveItem(damager, false, getItem());
                                    event.setDamage(0.0);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private static class ParcheminItem extends ItemPower {

        public ParcheminItem(@NonNull RoleBase role) {
            super("§aParchemin§r", new Cooldown(120), new ItemBuilder(Material.NETHER_STAR).setName("§aParchemin"), role,
                    "§7Crée une zone de rayon§c 20§7 autours de vous pendant§c 20 secondes§7,",
                    "§7à l'intérieur, tout les joueurs sauf vous auront des§c flèches§7 qui apparaitront au dessus d'eux");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            player.sendMessage("§7Vous activez votre zone de§a parchemin");
            new ParcheminRunnable(this, player.getLocation()).runTaskTimer(getPlugin(), 0, 10);
            return true;
        }
        private static class ParcheminRunnable extends BukkitRunnable {

            private final ParcheminItem parcheminItem;
            private final Location initLoc;
            private int i = 40;

            private ParcheminRunnable(@NonNull ParcheminItem parcheminItem,@NonNull Location initLoc) {
                this.parcheminItem = parcheminItem;
                this.initLoc = initLoc;
            }

            @Override
            public void run() {
                if (!GameState.inGame()) {
                    cancel();
                    return;
                }
                if (i == 0) {
                    this.parcheminItem.getRole().getGamePlayer().sendMessage("§7Votre zone de§a Parchemin§7 c'est terminé");
                    cancel();
                    return;
                }
                final Player owner = Bukkit.getPlayer(this.parcheminItem.getRole().getPlayer());
                for (@NonNull final Player p : Loc.getNearbyPlayers(initLoc, 20)) {
                    //		if (p.getUniqueId() != owner.getUniqueId()) {
                    Arrow arrow = (Arrow) p.getWorld().spawnEntity(new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY()+2.95, p.getLocation().getZ()), EntityType.ARROW);
                    if (owner != null){
                        arrow.setShooter(owner);
                    }
                    arrow.setCritical(true);
                    arrow.setFallDistance(100f);
                    arrow.setBounce(false);
                    arrow.setKnockbackStrength(2);

                    //	    }
                }
                MathUtil.sendCircleParticle(EnumParticle.BARRIER, this.initLoc, 20, 100);
                i--;
            }
        }
    }
}