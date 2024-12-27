package fr.nicknqck.managers;

import fr.nicknqck.Main;
import fr.nicknqck.events.custom.RoleGiveEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.slayers.Tanjiro;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class AssassinManager implements Listener {

    @Getter
    @Setter
    private boolean enable = true;
    private final List<GamePlayer> canBeAssassin;
    private UUID gameID;
    @Getter
    @Setter
    private int timingAssassin = 60;

    public AssassinManager() {
        this.canBeAssassin = new ArrayList<>();
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    private void onGiveRole(RoleGiveEvent event) {
        if (!this.enable)return;
        if (this.gameID == null) {
            this.gameID = UUID.randomUUID();
        }
        if (event.isEndGive()) {
            if (!this.canBeAssassin.isEmpty()) {
                System.out.println("[UHC] Assassin system is now enable, and it will use a list of "+canBeAssassin.stream());
                final UUID goodID = this.gameID;
                Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
                    if (this.gameID != null && this.gameID.equals(goodID)) {//Juste pour vérifier que c'est bien la même game que tout à l'heure
                        final List<GamePlayer> goodList = new ArrayList<>();
                        this.canBeAssassin.stream().filter(GamePlayer::isAlive).filter(gamePlayer -> gamePlayer.getDiscRunnable().isOnline()).forEach(goodList::add);
                        if (!goodList.isEmpty()) {
                            Collections.shuffle(goodList, Main.RANDOM);
                            final GamePlayer gamePlayer = goodList.get(0);
                            gamePlayer.getRole().setMaxHealth(gamePlayer.getRole().getMaxHealth()+4.0);
                            final Player player = Bukkit.getPlayer(gamePlayer.getUuid());
                            if (player != null) {
                                player.setMaxHealth(gamePlayer.getRole().getMaxHealth());
                                player.setHealth(player.getHealth()+4.0);
                                player.resetTitle();
                                player.sendTitle("§c§lVous êtes l'§4§lAssassin", "§cVous obtenez donc 2❤ supplémentaires !");
                            }
                            gamePlayer.sendMessage("Vous êtes l'assassin vous possédez désormais§c 2❤ supplémentaire de manière permanente, de plus faite attention au rôle de§a Tanjiro§f qui obtiendra un bonus s'il vous tue.");
                            gamePlayer.getRole().setSuffixString(gamePlayer.getRole().getSuffixString()+"§7 (§cAssassin§7)§r");
                            System.out.println(gamePlayer.getPlayerName()+" is now the Assassin of the game");
                            System.out.println("Ending Assassin System");
                            for (final UUID u : event.getGameState().getInGamePlayers()) {
                                if (!event.getGameState().hasRoleNull(u)) {
                                    final RoleBase role = event.getGameState().getGamePlayer().get(u).getRole();
                                    if (role.getTeam() != null) {
                                        if (role instanceof DemonsRoles ||
                                                role instanceof Tanjiro) {
                                            role.getGamePlayer().sendMessage("§cL'Assassin à été désigné");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }, 20L *this.getTimingAssassin());
                Collections.shuffle(this.canBeAssassin, Main.RANDOM);
                return;
            }
            System.out.println("[UHC] Assassin system can't be enable, because the value of demon_size is 0");
        }
        if (event.getRole() != null) {
            if (event.getRole() instanceof DemonsRoles) {
                canBeAssassin.add(event.getGamePlayer());
            }
        }
    }
}
