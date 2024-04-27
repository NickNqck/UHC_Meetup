package fr.nicknqck.utils.rank;

import fr.nicknqck.GameState;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public enum ChatRank {
    Spec("§1Spec ","§b"),
    Op("§c§lAdmin ","§c"),
    Host("§cHost ", "§c"),
    Joueur("Joueur ", "§r");

    private final String prefix;
    private final String color;
    private final List<UUID> players;
    private static final List<UUID> ranked = new ArrayList<>();
    ChatRank(String prefix, String color){
        this.color = color;
        this.prefix = prefix;
        this.players = new ArrayList<>();
    }

    public void setPlayer(Player player) {
        for(ChatRank chatRank : ChatRank.values()){
            chatRank.players.remove(player.getUniqueId());
        }
        players.add(player.getUniqueId());
        ranked.add(player.getUniqueId());

    }

    public String getFullPrefix(){
        return prefix + color;
    }
    public static boolean hasRank(UUID player){
        for (ChatRank chatRank : ChatRank.values()){
            if (chatRank.players.contains(player)){
                return true;
            }
        }
        return false;
    }

    public static void resetRank(UUID player){
        for (ChatRank rank : ChatRank.values()){
            rank.getPlayers().remove(player);
        }
    }
    public static void updateRank(Player player){
        ChatRank.resetRank(player.getUniqueId());
        if (!ChatRank.hasRank(player.getUniqueId())){
            if (player.isOp()){
                ChatRank.Op.setPlayer(player);
                return;
            }
            if (GameState.getInstance().getHost().contains(player.getUniqueId())){
                ChatRank.Host.setPlayer(player);
                return;
            }
            ChatRank.Joueur.setPlayer(player);
        }
    }
    public static ChatRank getPlayerGrade(Player player){
        return Arrays.stream(ChatRank.values()).filter(chatRank -> chatRank.players.contains(player.getUniqueId())).findAny().get();
    }
}