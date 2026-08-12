package fr.nicknqck.roles.valo.agents;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.EffectWhen;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.enums.TeamList;
import fr.nicknqck.interfaces.IRoles;
import fr.nicknqck.interfaces.ITeam;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.PropulserUtils;
import fr.nicknqck.utils.RandomUtils;
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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.UUID;

public final class Le_DOC extends RoleBase {

    public Le_DOC(UUID player) {
        super(player);
    }

    @Override
    public String getName() {
        return "Doc";
    }

    @Override
    public @NonNull IRoles<?> getRoles() {
        return Roles.Doc;
    }

    @Override
    public @NonNull ITeam getOriginTeam() {
        return TeamList.Solo;
    }

    @Override
    public @NonNull TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new MarteauPower(this), true);
        givePotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 60, 0, false, false), EffectWhen.PERMANENT);
        givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 1, false, false), EffectWhen.MID_LIFE);
    }
    private static final class MarteauPower extends ItemPower implements Listener {

        private final Cooldown feuCooldown = new Cooldown(3);

        public MarteauPower(@NonNull RoleBase role) {
            super("Forgekraken", new Cooldown(90), new ItemBuilder(Material.DIAMOND_AXE).addEnchant(Enchantment.DAMAGE_ALL,4).setName("Forgekraken"), role,
                    "§7En frappant avec votre marteau sur un joueur vous aurez 30% de chance de §6l'enflammé§7.",
                    "§7En faisant clique droit une flèche partira et elle propulsera le joueur et lui mettra §42,5❤ de dégâts§7.");

            EventUtils.registerRoleEvent(this);
            setSendCooldown(false);
        }
        
        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                    Arrow arrow = player.launchProjectile(Arrow.class);
                    arrow.setMetadata("doc.marteau."+player.getUniqueId(), new FixedMetadataValue(getPlugin(), event));
                    return true;
                }
            }
            return false;
        }
        @EventHandler(priority = EventPriority.NORMAL)
        public void onDegat(final EntityDamageByEntityEvent event) {
            if (!(event.getDamager() instanceof Player))return;
            Player owner = (Player) event.getDamager();
            if (!owner.getUniqueId().equals(getRole().getPlayer()))return;
            if (!(event.getEntity() instanceof Player))return;
            if (owner.getItemInHand() == null)return;
            if (!owner.getItemInHand().isSimilar(getItem()))return;
            final Player victim = (Player) event.getEntity();
            if (RandomUtils.getOwnRandomProbability(30)) {
                if(feuCooldown.isInCooldown()) {
                    return;
                } else {
                    victim.setFireTicks(20*15);
                    feuCooldown.use();
                }
            }
        }
        @EventHandler(priority = EventPriority.NORMAL)
        public void onFlecheTouche(final  EntityDamageByEntityEvent event) {
            if (!(event.getDamager() instanceof Arrow))return;
            if (!(event.getEntity() instanceof Player))return;
            final Arrow arrow = (Arrow) event.getDamager();
            if (arrow.getShooter() instanceof Player) {
                final Player owner = (Player) arrow.getShooter();
                if (owner.getUniqueId().equals(getRole().getPlayer())) {
                    if (!arrow.hasMetadata("doc.marteau."+owner.getUniqueId())) {
                        return;
                    }
                    final Player victim = (Player) event.getEntity();
                    new PropulserUtils(owner, 20).setNoFall(true).applyPropulsion(victim);
                    arrow.removeMetadata("doc.marteau."+owner.getUniqueId(), getPlugin());
                    victim.setHealth(Math.max(victim.getHealth()-5, 1));
                }
            }
        }
    }
}