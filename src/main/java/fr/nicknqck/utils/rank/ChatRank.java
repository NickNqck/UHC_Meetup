package fr.nicknqck.utils.rank;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public enum ChatRank {
    Spec("§1Spec ","§b"),
    Op("§c§lAdmin ","§c"),
    Host("§cHost ", "§c"),
    Joueur("Joueur ", "§r");

    private final String prefix;
    private final String color;
    private final List<UUID> players;
    ChatRank(String prefix, String color){
        this.color = color;
        this.prefix = prefix;
        this.players = new CopyOnWriteArrayList<>();
    }

    public void setPlayer(Player player) {
        for(ChatRank chatRank : ChatRank.values()){
            chatRank.players.remove(player.getUniqueId());
        }
        players.add(player.getUniqueId());

    }
    public static boolean isHost(Object uuid) {
        if (uuid instanceof UUID){
            return ChatRank.Op.getPlayers().contains(uuid) || ChatRank.Host.getPlayers().contains(uuid);
        }
        if (uuid instanceof Player){
            return ChatRank.Op.getPlayers().contains(((Player) uuid).getUniqueId()) || ChatRank.Host.getPlayers().contains(((Player) uuid).getUniqueId());
        }
        return uuid instanceof CommandSender;
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

    public static void resetRanks(UUID player){
        for (ChatRank rank : ChatRank.values()){
            rank.getPlayers().remove(player);
        }
    }
    public static void resetRanksExcepts(UUID player, ChatRank... ranks){
        final List<ChatRank> rankList = new ArrayList<>(Arrays.asList(ranks));
        for (ChatRank rank : ChatRank.values()){
            if (rankList.contains(rank)){
                continue;
            }
            rank.getPlayers().remove(player);
        }
    }
    public static void updateRank(Player player){
        resetRanksExcepts(player.getUniqueId(), ChatRank.Host);
        if (!ChatRank.hasRank(player.getUniqueId())){
            if (player.isOp()){
                ChatRank.Op.setPlayer(player);
                return;
            }
            ChatRank.Joueur.setPlayer(player);
        }
    }
    public static ChatRank getPlayerGrade(Player player){
        for (ChatRank rank : ChatRank.values()){
            if (rank.getPlayers().contains(player.getUniqueId())){
                return rank;
            }
        }
        return Joueur;
    }
    public void add(UUID uuid) {
        players.add(uuid);
    }

    public boolean contains(UUID uniqueId) {
        return players.contains(uniqueId);
    }

    public void remove(UUID uniqueId) {
        players.remove(uniqueId);
    }
}