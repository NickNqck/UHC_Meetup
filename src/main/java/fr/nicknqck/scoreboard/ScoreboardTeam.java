package fr.nicknqck.scoreboard;


import java.lang.reflect.Field;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by Alexis on 19/02/2017.
 */
@Setter
@Getter
public class ScoreboardTeam {

    private String name;
    private String prefix;
    @Getter
    public enum Modes {
        Creation(0),
        Removed(1),
        Updated(2),
        AddTeam(3),
        RemoveTeam(4);
        final int anInt;
        Modes(int i){
            this.anInt = i;
        }
    }

    public ScoreboardTeam(String name, String prefix) {
        this.name = name;
        this.prefix = prefix;
    }

    private PacketPlayOutScoreboardTeam createPacket(int i){
        PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();

        // a : team name

        // h : mode
        /**
         If 0 then the team is created.
         If 1 then the team is removed.
         If 2 the team team information is updated.
         If 3 then new players are added to the team.
         If 4 then players are removed from the team.
         */

        // b : display name
        // c : prefix
        // d : suffix
        // i : friendly fire (0 off, 1 on)
        // e : name tag visibility
        // f : chat color

        setField(packet, "a", name);
        setField(packet, "h", i);
        setField(packet, "b", "");
        setField(packet, "c", prefix);
        setField(packet, "d", "");
        setField(packet, "i", 0);
        setField(packet, "e", "always");
        setField(packet, "f", 0);

        return packet;
    }

    public PacketPlayOutScoreboardTeam createTeam(){
        return createPacket(0);
    }

    public PacketPlayOutScoreboardTeam updateTeam(){
        return createPacket(2);
    }

    public PacketPlayOutScoreboardTeam removeTeam(){
        PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
        setField(packet, "a", name);
        setField(packet, "h", 1);

        return packet;
    }

    public PacketPlayOutScoreboardTeam setFriendlyFire(boolean v){
        PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
        setField(packet, "i", (v ? 1 : 0));

        return packet;
    }

    @SuppressWarnings("unchecked")
	public PacketPlayOutScoreboardTeam addOrRemovePlayer(int mode, String playerName){
        PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
        setField(packet, "a", name);
        setField(packet, "h", mode);

        try {
            Field f = packet.getClass().getDeclaredField("g");
            f.setAccessible(true);
            ((List<String>) f.get(packet)).add(playerName);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return packet;
    }

    public static void updateDisplayName(Player player) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_DISPLAY_NAME, (EntityPlayer) player);
        ((EntityPlayer)player).playerConnection.sendPacket(packet);
    }

    private void setField(Object edit, String fieldName, Object value) {
        try {
            Field field = edit.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(edit, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}