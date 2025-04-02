package fr.nicknqck.roles.builder;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.aot.builders.MahrRoles;
import fr.nicknqck.roles.aot.builders.TitansRoles;
import fr.nicknqck.roles.aot.titanrouge.Sieg;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GetterList {
    private final GameState gameState;
    
    public GetterList(GameState gameState) {
        this.gameState = gameState;
    }

    public void getDemonList(Player player){
        if (!gameState.getGamePlayer().isEmpty()){
            TextComponent text = new TextComponent("§cListe des lunes:\n");
            for (final GamePlayer gamePlayer : GameState.getInstance().getGamePlayer().values()){
                if (gamePlayer.getRole() == null) continue;
                final RoleBase role = gamePlayer.getRole();
                if (role instanceof DemonsRoles) {
                    DemonsRoles demon = (DemonsRoles) role;
                    if (!demon.getRank().equals(DemonType.DEMON)){
                        Player list = Bukkit.getPlayer(demon.getPlayer());
                        if (list != null){
                            if (demon.getGamePlayer().isAlive()) {
                                text.addExtra("\n§7 - §c"+list.getName());
                            }
                        }
                    }
                }
            }
            text.addExtra("\n");
            Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> player.spigot().sendMessage(text), 1L);
        }
    }
    public void getMahrList(Player player){
        if (!gameState.getGamePlayer().isEmpty()){
            TextComponent text = new TextComponent("§9Liste des Mahrs:\n");
            for (final GamePlayer gamePlayer : GameState.getInstance().getGamePlayer().values()){
                if (gamePlayer.getRole() == null)continue;
                final RoleBase role = gamePlayer.getRole();
                if (role instanceof MahrRoles) {
                    MahrRoles mahr = (MahrRoles ) role;
                    Player list = Bukkit.getPlayer(mahr.getPlayer());
                    if (list != null){
                        text.addExtra("\n§7 - §9"+list.getName());
                    }
                }
            }
            text.addExtra("\n");
            Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> player.spigot().sendMessage(text), 1L);
        }
    }
    public void getTitanRougeList(Player player){
        if (!gameState.getGamePlayer().isEmpty()){
            TextComponent text = new TextComponent("§cListe des Titans Rouge:\n");
            for (final GamePlayer gamePlayer : GameState.getInstance().getGamePlayer().values()){
                if (gamePlayer.getRole() == null)continue;
                final RoleBase role = gamePlayer.getRole();
                if (role instanceof TitansRoles) {
                    TitansRoles Titans = (TitansRoles ) role;
                    if (!(role instanceof Sieg)){
                        Player list = Bukkit.getPlayer(Titans.getPlayer());
                        if (list != null){
                            text.addExtra("\n§7 - §c"+list.getName());
                        }
                    }
                }
            }
            text.addExtra("\n");
            Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> player.spigot().sendMessage(text), 1L);
        }
    }
}