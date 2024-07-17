package fr.nicknqck.roles.builder;

import fr.nicknqck.GameState;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.potion.PotionEffect;

import java.util.Map;
import java.util.UUID;

public interface Role {

    UUID getPlayer();
    String getName();
    GameState.Roles getRoles();
    TeamList getOriginTeam();
    TeamList getTeam();
    Map<PotionEffect, EffectWhen> getEffects();
    void resetCooldown();
    TextComponent getComponent();
}