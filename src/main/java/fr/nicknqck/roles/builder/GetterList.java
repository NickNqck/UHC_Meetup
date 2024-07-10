package fr.nicknqck.roles.builder;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.aot.builders.MahrRoles;
import fr.nicknqck.roles.aot.builders.TitansRoles;
import fr.nicknqck.roles.aot.titanrouge.TitanBestial;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ns.builders.AkatsukiRoles;
import fr.nicknqck.roles.ns.solo.jubi.Obito;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GetterList {
    private final GameState gameState;
    public GetterList(GameState gameState) {
        this.gameState = gameState;
    }

    public void getDemonList(Player player){
        if (!gameState.getPlayerRoles().isEmpty()){
            TextComponent text = new TextComponent("§cListe des lunes:\n");
            for (RoleBase role : GameState.getInstance().getPlayerRoles().values()){
                if (role instanceof DemonsRoles) {
                    DemonsRoles demon = (DemonsRoles) role;
                    if (demon.getRank().name().contains("Lune")){
                        Player list = Bukkit.getPlayer(demon.getPlayer());
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
    public void getMahrList(Player player){
        if (!gameState.getPlayerRoles().isEmpty()){
            TextComponent text = new TextComponent("§9Liste des Mahrs:\n");
            for (RoleBase role : GameState.getInstance().getPlayerRoles().values()){
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
        if (!gameState.getPlayerRoles().isEmpty()){
            TextComponent text = new TextComponent("§cListe des Titans Rouge:\n");
            for (RoleBase role : GameState.getInstance().getPlayerRoles().values()){
                if (role instanceof TitansRoles) {
                    TitansRoles Titans = (TitansRoles ) role;
                    if (!(role instanceof TitanBestial)){
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
    public void getAkatsukiList(Player player){
        if (!gameState.getPlayerRoles().isEmpty()){
            TextComponent text = new TextComponent("§cListe de l'Akatsuki:\n");
            for (RoleBase role : GameState.getInstance().getPlayerRoles().values()){
                if (role instanceof AkatsukiRoles || role instanceof Obito) {
                    Player list = Bukkit.getPlayer(role.getPlayer());
                    if (list != null){
                        text.addExtra("\n§7 - §c"+list.getName());
                    }
                }
            }
            text.addExtra("\n");
            Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> player.spigot().sendMessage(text), 1L);
        }
    }
}