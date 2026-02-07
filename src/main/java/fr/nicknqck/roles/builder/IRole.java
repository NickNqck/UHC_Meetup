package fr.nicknqck.roles.builder;

import fr.nicknqck.enums.Roles;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IRole {

    UUID getPlayer();
    String getName();
    @NonNull
    Roles getRoles();
    @NonNull TeamList getOriginTeam();
    TeamList getTeam();
    Map<PotionEffect, EffectWhen> getEffects();
    void resetCooldown();
    TextComponent getComponent();
    ItemStack[] getItems();
    List<Power> getPowers();
}