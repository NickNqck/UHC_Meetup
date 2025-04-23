package fr.nicknqck.entity.bijuv2;

import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public interface IBiju {

    @NonNull String getName();
    GamePlayer getHote();
    @NonNull ItemStack getItemInMenu();
    boolean checkCanSpawn();
    void spawn();
    Entity getEntity();
    @NonNull Material getItemMaterial();
    @NonNull String[] getItemDescription();
    @NonNull
    Location getOriginSpawn();
    int getMaxTimeProc();
    void setMaxTimeProc(int i);
    int getMinTimeProc();
    void setMinTimeProc(int i);
    @NonNull
    List<PotionEffect> getEffectsWhenUse();
    void onUse(@NonNull final Player player, @NonNull final RoleBase role);
    void onEnd(@NonNull final RoleBase role);

}