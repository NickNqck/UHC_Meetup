package fr.nicknqck.roles.ns.shinobi.porte;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.builder.AutomaticDesc;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.UUID;

public class GaiV2 extends PortesRoles{

    public GaiV2(UUID player) {
        super(player);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        super.RoleGiven(gameState);
        setChakraType(getRandomChakras());
        addPower(new TroisPortePower(this), true);
        addPower(new SixPortesPower(this), true);
        addPower(new HuitPortesPower(this), true);
    }

    @Override
    public String getName() {
        return "Gai Maito";
    }

    @Override
    public GameState.@NonNull Roles getRoles() {
        return GameState.Roles.Gai;
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this).setItems(troisPorteMap(), sixPorteMap(), huitPorteMap()).getText();
    }
}
