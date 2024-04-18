package fr.nicknqck.utils.particles;

import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public abstract class ParticleEffect {

    private final int timeInTicks;

    public ParticleEffect(int timeInTicks) {
        super();
        this.timeInTicks = timeInTicks;
    }

    public abstract void start(Player player);
        
}
