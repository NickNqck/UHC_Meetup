package fr.nicknqck.interfaces;

import org.bukkit.entity.Player;

import java.util.List;

public interface ITeam {

    List<Player> getList();
    String getColor();
    String getName();
    String getMdj();
    boolean isSolo();
    void addPlayer(Player player);
    String name();

}