package fr.nicknqck.roles.aot.soldats;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.InfoType;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.power.PowerTakeInfoEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.aot.builders.SoldatsRoles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class ErwinV2 extends SoldatsRoles {

    public ErwinV2(UUID player) {
        super(player);
    }

    @Override
    public String getName() {
        return "Erwin";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Erwin;
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new AotCampPower(this));
        super.RoleGiven(gameState);
    }
    private static final class AotCampPower extends CommandPower {

        public AotCampPower(@NonNull RoleBase role) {
            super("§a/aot camp <joueur>§r", "camp", new Cooldown(60*8), role, CommandType.AOT,
                    "§7Vous permet de savoir dans quel§a camp§7 est le joueur viser.");
            setMaxUse(2);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            final String[] args = (String[]) map.get("args");
            if (args.length == 2) {
                final Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage("§cCe joueur n'existe pas !");
                    return false;
                }
                final GamePlayer gamePlayer = GamePlayer.of(target.getUniqueId());
                if (gamePlayer == null) {
                    player.sendMessage("§cImpossible de cibler ce joueur !");
                    return false;
                }
                if (!gamePlayer.check()) {
                    player.sendMessage("§cImpossible de cibler ce joueur !");
                    return false;
                }
                final PowerTakeInfoEvent event = new PowerTakeInfoEvent(this, gamePlayer, InfoType.TEAM);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    if (event.getGameTarget().check()) {
                        player.sendMessage(Main.getInstance().getNAME()+"§a "+gamePlayer.getPlayerName()+"§7 est dans le camp:§c "+event.getGameTarget().getRole().getTeam().getName());
                    } else {
                        player.sendMessage("§cImpossible de cibler ce joueur !");
                        return false;
                    }
                } else {
                    player.sendMessage("§cVotre pouvoir a été annulé.");
                    return false;
                }
                return true;
            }
            return false;
        }
    }
}