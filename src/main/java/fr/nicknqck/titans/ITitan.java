package fr.nicknqck.titans;

import fr.nicknqck.player.GamePlayer;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.UUID;

public interface ITitan {

    @NonNull List<UUID> getStealers();
    @NonNull String getName();
    boolean isTransformed();
    void setTransformed(boolean transformed);
    @NonNull Material getTransformationMaterial();
    @NonNull GamePlayer getGamePlayer();
    @NonNull List<PotionEffect> getEffects();
    int getTransfoDuration();
    @NonNull String[] getDescription();

}