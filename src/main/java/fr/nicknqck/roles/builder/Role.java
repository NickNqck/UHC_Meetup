package fr.nicknqck.roles.builder;

import fr.nicknqck.GameState;

import java.util.UUID;

public interface Role {

    UUID getPlayer();
    String getName();
    GameState.Roles getRoles();

}