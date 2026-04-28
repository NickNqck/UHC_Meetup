package fr.nicknqck.utils.powers;

import fr.nicknqck.interfaces.ICommandType;
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

    private final ICommandType commandType;
    private final String arg0;

    public CommandPower(@NonNull String name, @NonNull String arg0, Cooldown cooldown, @NonNull RoleBase role, final @NonNull ICommandType commandType, String... descriptions) {
        super(name, cooldown, role, descriptions);
        this.commandType = commandType;
        this.arg0 = arg0;
    }
    public void call(String[] strings, final ICommandType type, final Player player) {
        if (commandType.getAlias().equals(type.getAlias())) {
            if (!strings[0].equalsIgnoreCase(arg0)) return;
            Map<String, Object> maps = new HashMap<>();
            maps.put("args", strings);
            this.checkUse(player, maps);
        }
    }
    public List<String> getCompletor(String[] args) {
        return new ArrayList<>();
    }
    public enum CommandType implements ICommandType {
        DS("ds"),
        AOT("aot"),
        NS("ns"),
        MC("mc"),
        KRYSTAL("kr");

        private final String alias;

        CommandType(String alias) {
            this.alias = alias;
        }

        @Override
        public String getAlias() {
            return this.alias;
        }
    }
}