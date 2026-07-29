package fr.nicknqck.utils;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.module.glow.GlowModule;
import com.lunarclient.apollo.player.ApolloPlayer;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.Optional;
import java.util.UUID;

public final class LunarHandler {

    public static void resetGlow(Player owner) {
        Optional<ApolloPlayer> lunarPlayer = Apollo.getPlayerManager().getPlayer(owner.getUniqueId());
        lunarPlayer.ifPresent(lp -> Apollo.getModuleManager().getModule(GlowModule.class).resetGlow(lp));
    }

    public static void setGlow(Player owner, UUID targetUuid) {
        Optional<ApolloPlayer> lunarPlayer = Apollo.getPlayerManager().getPlayer(owner.getUniqueId());
        lunarPlayer.ifPresent(lp -> Apollo.getModuleManager().getModule(GlowModule.class).overrideGlow(lp, targetUuid, Color.ORANGE));
    }
}
