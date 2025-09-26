package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.enums.Roles;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import fr.nicknqck.roles.ds.builders.Soufle;
import lombok.NonNull;

import java.util.UUID;

public class KaigakuSlayer extends SlayerRoles {

    public KaigakuSlayer(UUID player) {
        super(player);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.FOUDRE;
    }

    @Override
    public String getName() {
        return "Kaigaku";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Kaigaku;
    }
}
