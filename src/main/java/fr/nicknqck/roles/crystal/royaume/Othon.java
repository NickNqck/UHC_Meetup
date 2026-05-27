package fr.nicknqck.roles.crystal.royaume;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.CrystalFaction;
import fr.nicknqck.enums.CrystalRoles;
import fr.nicknqck.interfaces.IRoles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.UUID;

public class Othon extends RoyaumeBase {

    public Othon(UUID player) {
        super(player);
    }

    @Override
    public void onRoleGive(@NonNull GameState gameState) {

    }

    @Override
    public @NonNull CrystalFaction getCrystalFaction() {
        return CrystalFaction.ROYAL;
    }

    @Override
    public String getName() {
        return "Othon";
    }

    @Override
    public @NonNull IRoles<?> getRoles() {
        return CrystalRoles.Othon;
    }

    @Override
    public @NonNull TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }



}