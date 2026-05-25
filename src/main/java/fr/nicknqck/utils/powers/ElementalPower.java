package fr.nicknqck.utils.powers;

import fr.nicknqck.interfaces.IElements;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.particles.MathUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

@Getter
@Setter
public abstract class ElementalPower extends Power{

    private final IElements element;
    private final int red;
    private final int green;
    private final int blue;
    private double ballRadius = 0.5;
    private double maxDistance = 50.0;

    public ElementalPower(@NonNull String name, Cooldown cooldown, @NonNull RoleBase role, IElements element, int red, int green, int blue) {
        super(name, cooldown, role);
        this.element = element;
        this.red = red;
        this.green = green;
        this.blue = blue;
        setMaxUse(0);
        setShowInDesc(false);
    }
    public String getBallName() {
        return "crystal."+getRole().getName();
    }
    public abstract void onImpact(@NonNull final Location impactLocation, @Nullable final Player hitPlayer);

    public void launchBall(@NonNull final Player player) {
        MathUtil.launchParticleBall(player, getBallName(), red, green, blue, ballRadius, maxDistance);
    }

}