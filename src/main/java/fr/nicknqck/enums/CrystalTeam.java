package fr.nicknqck.enums;

import fr.nicknqck.interfaces.ITeam;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public enum CrystalTeam implements ITeam {

    Royaume("§a", "Royaume", "cr", false),
    Guilde("§5", "Guilde", "cr", false)
    ;

    private final List<Player> list;
    private final String color;
    private final String name;
    private final String mdj;
    private final boolean solo;

    CrystalTeam(String color, String name, String mdj, boolean solo) {
        this.list = new ArrayList<>();
        this.color = color;
        this.name = name;
        this.mdj = mdj;
        this.solo = solo;
    }

    @Override
    public List<Player> getList() {
        return this.list;
    }

    @Override
    public String getColor() {
        return this.color;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getMdj() {
        return this.mdj;
    }

    @Override
    public boolean isSolo() {
        return this.solo;
    }

    @Override
    public void addPlayer(Player player) {
        this.list.add(player);
    }
}