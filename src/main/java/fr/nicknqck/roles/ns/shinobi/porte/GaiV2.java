package fr.nicknqck.roles.ns.shinobi.porte;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;

import javax.annotation.Nonnull;
import java.util.UUID;

public class GaiV2 extends PortesRoles{

    public GaiV2(UUID player) {
        super(player);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new TroisPortePower(this), true);
        addPower(new SixPortesPower(this), true);
        addPower(new HuitPortesPower(this), true);
    }

    @Override
    public String getName() {
        return "Gai Maito";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Gai;
    }

    @Nonnull
    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this).setItems(troisPorteMap(), sixPorteMap(), huitPorteMap()).getText();
    }
}
