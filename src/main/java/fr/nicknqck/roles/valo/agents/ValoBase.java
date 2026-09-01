package fr.nicknqck.roles.valo.agents;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.UUID;

public abstract class ValoBase extends RoleBase {

    public ValoBase(UUID player) {
        super(player);
    }

    @NonNull
    public abstract ValoItemPower getValoItemPower();

    @NonNull
    public abstract ItemPower getUltime();

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(getValoItemPower(), true);
        addPower(getUltime(), true);
    }
    @Override
    public @NonNull TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

}