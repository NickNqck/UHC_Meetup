package fr.nicknqck.roles.ds.builders;

import fr.nicknqck.GameState;
import lombok.Getter;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
public enum Lames {

    Force(event -> GameState.getInstance().getPlayerRoles().get(event.getPlayer()).addBonusforce(10.0)),
    Resistance(event -> GameState.getInstance().getPlayerRoles().get(event.getPlayer()).addBonusResi(10.0)),
    Coeur(event -> GameState.getInstance().getPlayerRoles().get(event.getPlayer()).setMaxHealth(event.getPlayer().getMaxHealth()+4.0)),
    NoFall(event -> GameState.getInstance().getPlayerRoles().get(event.getPlayer()).setNoFall(true)),
    Speed(event -> GameState.getInstance().getPlayerRoles().get(event.getPlayer()).addSpeedAtInt(event.getPlayer(), 10));

    private final HashMap<UUID, Integer> users = new HashMap<>();
    private final Consumer<PlayerInteractEvent> consumer;
    Lames(Consumer<PlayerInteractEvent> consumer){
        this.consumer = consumer;
    }
}