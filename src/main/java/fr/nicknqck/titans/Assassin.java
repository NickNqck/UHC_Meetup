package fr.nicknqck.titans;

import fr.nicknqck.player.GamePlayer;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class Assassin extends TitanBase {

    private final List<PotionEffect> effects;

    public Assassin(GamePlayer gamePlayer) {
        super(gamePlayer);
        this.effects = new ArrayList<>();
        this.effects.add(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*60*5, 0, false, false));
        this.effects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60*5, 0, false, false));
        this.effects.add(new PotionEffect(PotionEffectType.SPEED, 20*60*5, 0, false, false));
    }

    @Override
    public @NonNull String getName() {
        return "Titan Assaillant";
    }

    @Override
    public @NonNull Material getTransformationMaterial() {
        return Material.ROTTEN_FLESH;
    }

    @Override
    public @NonNull List<PotionEffect> getEffects() {
        return this.effects;
    }

    @Override
    public int getTransfoDuration() {
        return 60*4;
    }
}