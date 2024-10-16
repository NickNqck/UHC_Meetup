package fr.nicknqck.utils.powers;

import fr.nicknqck.roles.builder.RoleBase;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class CommandPower extends Power{

    private final CommandType commandType;

    public CommandPower(@NonNull String name, Cooldown cooldown, @NonNull RoleBase role, final @NonNull CommandType commandType, String... descriptions) {
        super(name, cooldown, role, descriptions);
        this.commandType = commandType;
    }
    public void call(String[] args, final CommandType type, final Player player) {
        if (commandType.equals(type)) {
            Map<String, Object> maps = new HashMap<>();
            maps.put("args", args);
            this.checkUse(player, maps);
        }
    }
    public enum CommandType {
        DS,
        AOT,
        NS,
        MC,
        CUSTOM
    }
}