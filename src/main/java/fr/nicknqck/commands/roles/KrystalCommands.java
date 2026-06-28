package fr.nicknqck.commands.roles;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.BailliInfoType;
import fr.nicknqck.interfaces.BailliInfo;
import fr.nicknqck.interfaces.IGotReputation;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class KrystalCommands implements CommandExecutor {

    private final GameState gameState;

    public KrystalCommands(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            final Player sender = (Player) commandSender;
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("roles") || args[0].equalsIgnoreCase("compo")) {
                    sender.sendMessage(gameState.getRolesList());
                    return true;
                }
                if (args[0].equalsIgnoreCase("me") || args[0].equalsIgnoreCase("role")) {
                    gameState.sendDescription(sender);
                    return true;
                }
                if (args[0].equalsIgnoreCase("bailli")) {
                    if (args.length == 3) {
                        final Player target = Bukkit.getPlayer(args[1]);
                        if (target != null) {
                            final GamePlayer gamePlayer = GamePlayer.of(target.getUniqueId());
                            if (gamePlayer != null) {
                                if (gamePlayer.check()) {
                                    final GamePlayer me = GamePlayer.of(((Player) commandSender).getUniqueId());
                                    if (me == null || !me.check()) {
                                        commandSender.sendMessage("§cImpossible de faire cette commande !");
                                        return true;
                                    }
                                    if (me.getRole() instanceof IGotReputation) {
                                        final BailliInfoType bailliInfoType = BailliInfoType.fromString(args[2]);
                                        if (bailliInfoType != null) {
                                            if (((IGotReputation) me.getRole()).getReputation() >= bailliInfoType.getReputationCost()) {
                                                final List<BailliInfo> bailliInfoList = Main.getInstance().getCrystalManager().getBailliManager().getBailliInfos(bailliInfoType);
                                                if (!bailliInfoList.isEmpty()) {
                                                    for (@NonNull final BailliInfo bailliInfo : bailliInfoList) {
                                                        if (bailliInfo.getGamePlayer().getUuid().equals(gamePlayer.getUuid())) {
                                                            bailliInfo.sendValueInfoTo(me);
                                                            break;
                                                        }
                                                    }
                                                }
                                                ((IGotReputation) me.getRole()).setReputation(((IGotReputation) me.getRole()).getReputation() - bailliInfoType.getReputationCost());
                                            } else {
                                                sender.sendMessage("§aBailli Thomas§7: Hors de question que je te donne cette information, tu ne m'inspire pas confiance.");
                                            }
                                        } else {
                                            sender.sendMessage("§aBailli Thomas§7: C'est quoi ça un \"§c"+args[2]+"\"§7 ?");
                                        }
                                        return true;
                                    }
                                }
                            }
                            sender.sendMessage("§aBailli Thomas§7: Désolé, je ne te connais pas assez pour pouvoir t'aider.");
                            return true;
                        }
                    } else {
                        commandSender.sendMessage("§7La commande est§c /cr bailli <joueur>§7.");
                        return true;
                    }
                }
                if (!gameState.hasRoleNull(sender.getUniqueId())) {
                    final RoleBase role = gameState.getGamePlayer().get(sender.getUniqueId()).getRole();
                    if (role.getGamePlayer().isAlive()) {
                        if (!role.getPowers().isEmpty()) {
                            for (final Power power : role.getPowers()) {
                                if (power instanceof CommandPower) {
                                    ((CommandPower) power).call(args, CommandPower.CommandType.CRYSTAL, sender);
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

}