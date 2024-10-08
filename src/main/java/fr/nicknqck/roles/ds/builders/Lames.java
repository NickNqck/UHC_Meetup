package fr.nicknqck.roles.ds.builders;

import fr.nicknqck.GameState;
import lombok.Getter;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
public enum Lames {

    Force(event -> GameState.getInstance().getPlayerRoles().get(event.getPlayer()).addBonusforce(10.0), "§cForce"),
    Resistance(event -> GameState.getInstance().getPlayerRoles().get(event.getPlayer()).addBonusResi(10.0), "§9Resistance"),
    Coeur(event -> GameState.getInstance().getPlayerRoles().get(event.getPlayer()).setMaxHealth(event.getPlayer().getMaxHealth()+4.0), "§cCoeur"),
    NoFall(event -> GameState.getInstance().getPlayerRoles().get(event.getPlayer()).setNoFall(true), "§aNo Fall"),
    Speed(event -> GameState.getInstance().getPlayerRoles().get(event.getPlayer()).addSpeedAtInt(event.getPlayer(), 10), "§eSpeed"),
    FireResistance(event -> event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false)), "§6Résistance au Feu");

    private final HashMap<UUID, Integer> users = new HashMap<>();
    private final Consumer<PlayerInteractEvent> consumer;
    private final String name;
    Lames(Consumer<PlayerInteractEvent> consumer, String name){
        this.consumer = consumer;
        this.name = name;
    }
}