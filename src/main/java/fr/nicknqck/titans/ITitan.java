package fr.nicknqck.titans;

import fr.nicknqck.enums.TitanForm;
import fr.nicknqck.player.GamePlayer;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public interface ITitan {

    @NonNull List<GamePlayer> getStealers();
    @NonNull String getName();
    boolean isTransformed();
    void setTransformed(boolean transformed);
    @NonNull Material getTransformationMaterial();
    @NonNull GamePlayer getGamePlayer();
    @NonNull List<PotionEffect> getEffects();
    int getTransfoDuration();
    @NonNull String[] getDescription();
    @NonNull
    TitanForm getTitanForm();

}