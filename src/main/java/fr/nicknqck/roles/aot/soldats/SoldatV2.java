package fr.nicknqck.roles.aot.soldats;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.EffectWhen;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.power.aot.GamePlayerTridimentionnelEvent;
import fr.nicknqck.interfaces.IRoles;
import fr.nicknqck.roles.aot.builders.SoldatsRoles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.event.EventUtils;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public final class SoldatV2 extends SoldatsRoles implements Listener {

    public SoldatV2(UUID player) {
        super(player);
    }

    @Override
    public String getName() {
        return "Soldat";
    }

    @Override
    public @NonNull IRoles<?> getRoles() {
        return Roles.Soldat;
    }

    @Override
    public @NonNull TextComponent getComponent() {
        return AutomaticDesc.createAutomaticDesc(this).addCustomLine("§7En vous téléportant avec l'§bArc Tridimensionnel§7 vous obtenez l'effet§e Speed I§7 pendant§c 15§7 ou§c 25 secondes§7.").getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        EventUtils.registerRoleEvent(this);
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onTeleport(final GamePlayerTridimentionnelEvent event) {
        if (event.getGamePlayer().getUuid().equals(this.getPlayer())) {
            boolean speed = RandomUtils.getRandomProbability(50);
            if (speed) {
                givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*25, 0, false, false), EffectWhen.NOW);
                event.getGamePlayer().sendMessage("§7Vous avez reçus§c 25 secondes§7 de§e Speed I§7.");
            } else {
                givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*15, 0, false, false), EffectWhen.NOW);
                event.getGamePlayer().sendMessage("§7Vous avez reçus§c 15 secondes§7 de§e Speed I§7.");
            }
        }
    }
}