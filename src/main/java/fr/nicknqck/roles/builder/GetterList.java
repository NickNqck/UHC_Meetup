package fr.nicknqck.roles.builder;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GetterList {

    public static void getDemonList(Player player){
        if (!GameState.getInstance().getPlayerRoles().isEmpty()){
            TextComponent text = new TextComponent("§cListe des lunes:\n");
            for (RoleBase role : GameState.getInstance().getPlayerRoles().values()){
                if (role instanceof DemonsRoles) {
                    DemonsRoles demon = (DemonsRoles) role;
                    if (demon.getRank().name().contains("Lune")){
                        Player list = Bukkit.getPlayer(demon.getUuidOwner());
                        if (list != null){
                            text.addExtra("\n§7 - §c"+list.getName());
                        }
                    }
                }
            }
            Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> player.spigot().sendMessage(text), 1L);
        }
    }

}