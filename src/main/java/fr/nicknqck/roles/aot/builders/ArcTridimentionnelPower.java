package fr.nicknqck.roles.aot.builders;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.death.FinalDeathEvent;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.PotionUtils;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ArcTridimentionnelPower extends ItemPower implements Listener {

    public ArcTridimentionnelPower(@NonNull RoleBase role) {
        super("§bArc tridimensionnel", new Cooldown(Main.getInstance().getGameConfig().getAotConfig().getTridiCooldown()), new ItemBuilder(Material.BOW)
                .setName("§bArc tridimensionnel")
                .addEnchant(Enchantment.ARROW_INFINITE, 1), role,
                "§7Lancez une§c flèche§7, lorsqu'elle atterrit, vous vous§c téléportez§7 à cette endroit."
        );
        EventUtils.registerRoleEvent(this);
        getShowCdRunnable().setCustomText(true);
    }

    @Override
    public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
        if (GameState.inGame()) {
            if (getRole() instanceof AotRoles) {
                if (((AotRoles) getRole()).getGazAmount() > 0.0) {
                    if (Main.getInstance().getTitanManager().hasTitan(player.getUniqueId())) {
                        if (Main.getInstance().getTitanManager().getTitan(player.getUniqueId()).isTransformed()) {
                            player.sendMessage("§cLes titans n'ont pas accès à§b l'équipement tridimensionnel§c.");
                            return false;
                        }
                    }
                    return map.isEmpty();
                } else {
                    player.sendMessage("§cRéserve de§b gaz vespène§c épuiser. ");
                }
            }
        }
        return false;
    }

    @Override
    public void tryUpdateActionBar() {
        double gaz = 0.0;
        if (getRole() instanceof AotRoles) {
            gaz = ((AotRoles) getRole()).getGazAmount();
        }
        getShowCdRunnable().setCustomTexte("§bGaz:§c "+getGazBar(gaz)+"§b (§c"+new DecimalFormat("0.0").format(gaz)+"%§b)§7 |§b Arc tridimensionnel:§c "+(getCooldown().isInCooldown() ? StringUtils.secondsTowardsBeautiful(getCooldown().getCooldownRemaining()) : "utilisable"));
    }
    private String getGazBar(double number) {
        double maxGaz = 100/ (double) 2;
        double gaz = number/ (double) 2;
        StringBuilder bar = new StringBuilder(" ");
        for (double i = 0; i < gaz; i++) {
            bar.append("§a|");
        }
        for (double i = gaz; i < maxGaz; i++) {
            bar.append("§c|");
        }
        bar.append(" ");
        return bar.toString();
    }
    @EventHandler
    private void onProjectileLaunch(@NonNull final ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player && event.getEntity() instanceof Arrow) {
            if (((Player) event.getEntity().getShooter()).getItemInHand().isSimilar(this.getItem())) {
                final ItemStack bow = ((Player) event.getEntity().getShooter()).getItemInHand();
                if(!bow.hasItemMeta())return;
                if (!bow.getItemMeta().hasLore())return;
                if (!bow.getItemMeta().getLore().equals(Arrays.asList(getDescriptions())))return;
                //Si la personne a un titan
                if (Main.getInstance().getTitanManager().hasTitan(((Player)event.getEntity().getShooter()).getUniqueId())) {
                    //Si la personne est transformé en titan
                    if (Main.getInstance().getTitanManager().getTitan(((Player)event.getEntity().getShooter()).getUniqueId()).isTransformed()) {
                        //Alors, on annule la flèche
                        ((Player)event.getEntity().getShooter()).sendMessage("§cLes titans n'ont pas accès à§b l'équipement tridimensionnel§c.");
                        event.setCancelled(true);
                        return;
                    }
                }
                if (this.getCooldown().isInCooldown()) {
                    event.setCancelled(true);
                    return;
                }
                event.getEntity().setMetadata("aot.arctridi."+this.getCooldown().getUniqueId(), new FixedMetadataValue(getPlugin(), this));
            }
        }
    }
    @EventHandler
    private void onProjectileHit(@NonNull final ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow && event.getEntity().getShooter() instanceof Player) {
            if (!event.getEntity().hasMetadata("aot.arctridi."+this.getCooldown().getUniqueId()))return;
            if (!this.checkUse((Player) event.getEntity().getShooter(), new HashMap<>())) {
                return;
            }
            final ItemStack bow = ((Player) event.getEntity().getShooter()).getItemInHand();
            if(!bow.hasItemMeta())return;
            if (!bow.getItemMeta().hasLore())return;
            if (!bow.getItemMeta().getLore().equals(Arrays.asList(getDescriptions())))return;
            PotionUtils.addTempNoFall(((Player) event.getEntity().getShooter()).getUniqueId(), 1);
            final double distance = event.getEntity().getLocation().distance(((Player) event.getEntity().getShooter()).getLocation());
            final double gazToRemove = Math.max((Main.RANDOM.nextInt(2)+Main.RANDOM.nextDouble()), distance/8);
            ((Player) event.getEntity().getShooter()).teleport(event.getEntity().getLocation().add(0.0, 0.5, 0.0));
            getCooldown().setActualCooldown(getCooldown().getOriginalCooldown());
            if (getRole() instanceof AotRoles) {
                ((AotRoles) getRole()).setGazAmount(Math.max(0.0, ((AotRoles) getRole()).getGazAmount()-gazToRemove));
            }
            event.getEntity().removeMetadata("aot.arctridi."+this.getCooldown().getUniqueId(), getPlugin());
        }
    }
    @EventHandler
    private void onUHCKill(@NonNull final FinalDeathEvent event) {
        if (event.getRole().getGamePlayer().getKiller() == null)return;
        if (event.getRole().getGamePlayer().getKiller().check()) {
            if (event.getRole().getGamePlayer().getKiller().getRole() instanceof AotRoles) {
                AotRoles role = (AotRoles) event.getRole().getGamePlayer().getKiller().getRole();
                if (role.getGazAmount() < 100.0) {
                    double victimgaz = role.getGazAmount();
                    if (victimgaz > 1.0) {
                        double killergaz = role.getGazAmount();
                        DecimalFormat df = new DecimalFormat("0.0");
                        if (killergaz + victimgaz > 100.0) {
                            event.getRole().getGamePlayer().getKiller().sendMessage("§7Vous venez de récupérer§c "+df.format(victimgaz/3)+"%§7 de gaz");
                            role.setGazAmount(100);
                        }else {
                            event.getRole().getGamePlayer().getKiller().sendMessage("§7Vous venez de récupérer§c "+df.format(victimgaz/3)+"%§7 de gaz");
                            role.setGazAmount(victimgaz/3);
                        }
                    }
                }
            }
        }
    }
}