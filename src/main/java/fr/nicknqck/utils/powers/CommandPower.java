package fr.nicknqck.utils.powers;

import fr.nicknqck.roles.builder.RoleBase;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public abstract class CommandPower extends Power{

    private final CommandType commandType;
    private final String arg0;

    public CommandPower(@NonNull String name, @NonNull String arg0, Cooldown cooldown, @NonNull RoleBase role, final @NonNull CommandType commandType, String... descriptions) {
        super(name, cooldown, role, descriptions);
        this.commandType = commandType;
        this.arg0 = arg0;
    }
    public void call(String[] strings, final CommandType type, final Player player) {
        if (commandType.equals(type)) {
            if (!strings[0].equalsIgnoreCase(arg0)) return;
            Map<String, Object> maps = new HashMap<>();
            maps.put("args", strings);
            this.checkUse(player, maps);
        }
    }
    public List<String> getCompletor(String[] args) {
        return new ArrayList<>();
    }
    public enum CommandType {
        DS,
        AOT,
        NS,
        MC,
        KRYSTAL
    }
}