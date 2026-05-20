package fr.nicknqck.roles.crystal.guilde;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.CrystalRoles;
import fr.nicknqck.enums.CrystalTeam;
import fr.nicknqck.interfaces.IRoles;
import fr.nicknqck.interfaces.ITeam;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.crystal.CrystalBase;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;

import javax.annotation.Nonnull;
import java.util.UUID;

public class Bartholome extends CrystalBase {

    public Bartholome(UUID player) {
        super(player);
    }

    @Override
    public void onRoleGive(@NonNull GameState gameState) {

    }

    @Override
    public String getName() {
        return "Bartholome";
    }

    @Override
    public @NonNull IRoles<?> getRoles() {
        return CrystalRoles.Bartholome;
    }

    @Override
    public @NonNull ITeam getOriginTeam() {
        return CrystalTeam.Guilde;
    }

    @Nonnull
    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }
}
