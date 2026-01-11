package fr.nicknqck.roles.ns.power;

import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Map;

public class Rasengan extends ItemPower {

    public Rasengan(@NonNull RoleBase role) {
        super("§aRasengan§r", new Cooldown(120), new ItemBuilder(Material.NETHER_STAR).setName("§aRasengan"), role,
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