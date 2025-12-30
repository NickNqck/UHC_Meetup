package fr.nicknqck.roles.aot.soldats;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.roles.aot.builders.SoldatsRoles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.PotionUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SashaV2 extends SoldatsRoles {

    public SashaV2(UUID player) {
        super(player);
    }

    @Override
    public String getName() {
        return "Sasha";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Sasha;
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new ArcDuChasseurItem(this), true);
        super.RoleGiven(gameState);
    }
    private static class ArcDuChasseurItem extends ItemPower implements Listener {

        public ArcDuChasseurItem(@NonNull RoleBase role) {
            super("§aArc du Chasseur§r", new Cooldown(60), new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 3).setName("§aArc du chasseur"), role,
                    "§7Lorsque vous tirez une flèche et qu'elle atteint un joueur,",
                    "§7cela lui inflige un malus différent en fonction de l'endroit toucher",
                    "",
                    "Tête: Elle obtiendra 7 secondes de Blindness",
                    "Torse: Elle perdra 1 coeur supplémentaire (en plus des dégâts de la flèche)",
                    "Jambes: Elle obtient 7 secondes de Slowness");
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (map.containsKey("sachasse")) {
                if (map.get("sachasse") instanceof Player) {
                    return ((Player) map.get("sachasse")).getUniqueId().equals(player.getUniqueId());
                }
            }
            return false;
        }
        @EventHandler
        private void onShoot(@NonNull final ProjectileLaunchEvent event) {
            if (event.getEntity() instanceof Arrow && event.getEntity().getShooter() instanceof Player && ((Player) event.getEntity().getShooter()).getUniqueId().equals(getRole().getPlayer())) {
                final Arrow arrow = (Arrow) event.getEntity();
                final Player owner = (Player) event.getEntity().getShooter();
                if (getCooldown().isInCooldown()) {
                    sendCooldown(owner);
                    return;
                }
                arrow.setMetadata("sachaV2.chasseur", new FixedMetadataValue(getPlugin(), owner));
            }
        }
        @EventHandler
        private void onDamage(@NonNull final EntityDamageByEntityEvent event) {
            if (event.getDamager() instanceof Arrow) {
                final Arrow arrow = (Arrow) event.getDamager();
                if (arrow.getShooter() instanceof Player && ((Player) arrow.getShooter()).getUniqueId().equals(getRole().getPlayer())) {
                    final Player owner = (Player) arrow.getShooter();
                    if (!(event.getEntity() instanceof Player)) return;
                    final Player victim = (Player) event.getEntity();
                    if (arrow.hasMetadata("sachaV2.chasseur")) {
                        final Map<String, Object> map = new HashMap<>();
                        map.put("sachasse", owner);
                        if (checkUse(owner, map)) {
                            Vector arrowLocation = arrow.getLocation().toVector();
                            Vector targetLocation = victim.getLocation().toVector();

                            double heightDifference = arrowLocation.getY() - targetLocation.getY();
                            if (heightDifference > 1.5) {
                                owner.sendMessage("§7Tu as touché la tête de§a " + victim.getName());
                                PotionUtils.givePotionEffect(victim, new PotionEffect(PotionEffectType.BLINDNESS, 20*7, 0, false, false), EffectWhen.NOW);
                                victim.sendMessage("§aSasha§7 vous à infliger§l 7secondes§7 de "+ AllDesc.blind);
                            } else if (heightDifference < 0.9) {
                                owner.sendMessage("§7Tu as touché les jambes de§a " + victim.getName());
                                PotionUtils.givePotionEffect(victim, new PotionEffect(PotionEffectType.SLOW, 20*7, 0, false, false), EffectWhen.NOW);
                                victim.sendMessage("§aSasha§7 vous à infliger§l 7secondes§7 de "+AllDesc.slow);
                            } else {
                                owner.sendMessage("§7Tu as touché le torse de§a " + victim.getName());
                                victim.setHealth(Math.max(0.1, victim.getHealth()-2.0));
                                victim.damage(0.0);
                                victim.sendMessage("§7Vous avez été toucher par l'arc de§a Sasha§7 au niveau de votre torse");
                                victim.sendMessage("§aSasha§7 vous à infliger§l 1"+AllDesc.Coeur("§c"));
                            }
                        }
                    }
                }
            }
        }
    }
}