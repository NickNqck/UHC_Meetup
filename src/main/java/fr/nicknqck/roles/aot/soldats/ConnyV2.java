package fr.nicknqck.roles.aot.soldats;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.UpdatablePowerLore;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.FinalDeathEvent;
import fr.nicknqck.events.custom.RoleGiveEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.aot.builders.SoldatsRoles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ConnyV2 extends SoldatsRoles {

    public ConnyV2(UUID player) {
        super(player);
    }

    @Override
    public String getName() {
        return "Conny";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Conny;
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new AmisCommand(this));
        super.RoleGiven(gameState);
    }
    private static final class AmisCommand extends CommandPower implements Listener, UpdatablePowerLore {

        private final List<GamePlayer> amisList = new ArrayList<>();
        private Boolean sasha = null;

        public AmisCommand(@NonNull RoleBase role) {
            super("§a/aot ami <joueur>§r", "ami", new Cooldown(180), role, CommandType.AOT,
                    "§7Vous permet d'ajouter§c un joueur§7 en tant qu'\"§aami§7\".",
                    "",
                    "§7Lorsque l'un de vos§a amis§7 meurs, vous obtiendrez§c deux minutes",
                    "§7 de§c Force I§7 (§ccumulable§7)§7 puis§c 2 minutes§7 de§c Faiblesse 1§7,",
                    "§7vous obtiendrez aussi les coordonnées de l'emplacement de la mort.",
                    "",
                    "§7Par défaut vous avez au moins un§a ami§7 qui est choisi§c aléatoirement",
                    "§7entre§a Sasha§7 et§a Jean§7, cette partie il s'agit de:§a /aot me pour actualiser"
            );
            setMaxUse(2);
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            final String[] args = (String[]) map.get("args");
            if (args.length == 2) {
                final Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    GamePlayer gamePlayer = GamePlayer.of(target.getUniqueId());
                    if (gamePlayer != null) {
                        if (gamePlayer.getRole() != null && gamePlayer.isOnline() && gamePlayer.isOnline()) {
                            amisList.add(gamePlayer);
                            player.sendMessage("§a"+target.getName()+"§7 est maintenant considérer comme un/une§a ami§7§ae§7).");
                            return true;
                        }
                    }
                    player.sendMessage("§cImpossible de cibler ce joueur !");
                } else {
                    player.sendMessage("§b"+args[1]+"§c n'existe pas.");
                }
            }
            return false;
        }
        @EventHandler
        private void onGiveRole(@NonNull final RoleGiveEvent event) {
            if (!event.isEndGive())return;
            boolean sasha = false;
            boolean sIG = event.getGameState().getAttributedRole().contains(Roles.Sasha);
            boolean jIG = event.getGameState().getAttributedRole().contains(Roles.Jean);
            if (sIG && jIG) {
                sasha = Main.RANDOM.nextBoolean();
            } else if (sIG) {
                sasha = true;
            }
            for (@NonNull final GamePlayer gamePlayer : event.getGameState().getGamePlayer().values()) {
                if (gamePlayer.getRole() == null)continue;
                if (!gamePlayer.isAlive())continue;
                if (!gamePlayer.isOnline())continue;
                if (gamePlayer.getRole() instanceof ConnyV2) {
                    this.amisList.add(gamePlayer);
                    continue;
                }
                if (gamePlayer.getRole() instanceof SashaV2 && sasha) {
                    this.amisList.add(gamePlayer);
                    continue;
                }
                if (gamePlayer.getRole() instanceof JeanV2 && !sasha) {
                    this.amisList.add(gamePlayer);
                }
            }
            this.sasha = sasha;
        }
        @EventHandler
        private void onDeath(@NonNull final FinalDeathEvent event) {
            if (this.amisList.contains(event.getRole().getGamePlayer())) {
                
            }
        }
        @Override
        public String[] getCustomPowerLore() {
            return new String[] {
                    "§7Vous permet d'ajouter§c un joueur§7 en tant qu'\"§aami§7\".",
                    "",
                    "§8 -§7 Lorsque l'un de vos§a amis§7 meurs, vous obtiendrez§c deux minutes",
                    "§7 de§c Force I§7 (§ccumulable§7)§7 puis§c 2 minutes§7 de§c Faiblesse 1§7,",
                    "§7vous obtiendrez aussi les coordonnées de l'emplacement de la mort.",
                    "",
                    "§8 -§7 Par défaut vous avez au moins un§a ami§7 qui est choisi§c aléatoirement",
                    "§7entre§a Sasha§7 et§a Jean§7, cette partie il s'agit de:§a "+(this.sasha == null ? "/aot me pour actualiser" : this.sasha ? "Sasha" : "Jean")
            };
        }
    }
}