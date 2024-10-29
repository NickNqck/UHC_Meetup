package fr.nicknqck.utils.packets;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

@Getter
@Setter
public class ArmorStandUtils {

    private Location loc;
    private String text;
    private boolean gravity;
    private boolean basePlate;
    private boolean invisible;
    private boolean customNameVisible;
    private boolean marker;
    private EntityArmorStand stand;

    public ArmorStandUtils(final Location loc, final String text) {
        this(loc, text, false, false, true, true, true);
    }

    public ArmorStandUtils(final Location loc, final String text, final boolean gravity, final boolean basePlate, final boolean invisible, final boolean customNameVisible, final boolean marker) {
        this.loc = loc;
        this.text = text;
        this.gravity = gravity;
        this.basePlate = basePlate;
        this.invisible = invisible;
        this.customNameVisible = customNameVisible;
        this.marker = marker;
    }
    public ArmorStandUtils display(final Player player) {
        final EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        final World nmsWorld = ((CraftWorld) this.loc.getWorld()).getHandle();
        (this.stand = new EntityArmorStand(nmsWorld)).setGravity(this.gravity);
        this.stand.setLocation(this.loc.getX(), this.loc.getY() + (this.marker ? 2 : 0), this.loc.getZ(), this.loc.getYaw(), this.loc.getPitch());
        this.stand.setBasePlate(this.basePlate);
        this.stand.setInvisible(this.invisible);
        this.stand.setCustomName(this.text);
        this.stand.setCustomNameVisible(this.customNameVisible);
        this.stand.n(this.marker);
        final PacketPlayOutSpawnEntityLiving packetSpawn = new PacketPlayOutSpawnEntityLiving(this.stand);
        nmsPlayer.playerConnection.sendPacket(packetSpawn);
        return this;
    }

    public void teleport(final Location loc, final Player player) {
        this.setLoc(loc);
        final EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        this.stand.setLocation(loc.getX(), loc.getY() + (this.marker ? 2 : 0), loc.getZ(), loc.getYaw(), loc.getPitch());
        final PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(this.stand);
        nmsPlayer.playerConnection.sendPacket(packet);
    }
    public ArmorStandUtils delete(final Player player) {
        final EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        final PacketPlayOutEntityDestroy packetDestroy = new PacketPlayOutEntityDestroy(this.stand.getId());
        nmsPlayer.playerConnection.sendPacket(packetDestroy);
        return this;
    }
    public void rename(final String text, final Player player) {
        this.setText(text);
        final EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        this.stand.setCustomName(text);
        final PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(this.stand.getId(), this.stand.getDataWatcher(), false);
        nmsPlayer.playerConnection.sendPacket(packet);
    }
    public void hide(final Player player) {
        this.customNameVisible = false;
        final EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        this.stand.setCustomNameVisible(customNameVisible);
        final PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(this.stand.getId(), this.stand.getDataWatcher(), false);
        nmsPlayer.playerConnection.sendPacket(packet);
    }
    public void show(final Player player) {
        this.customNameVisible = true;
        final EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        this.stand.setCustomNameVisible(customNameVisible);
        final PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(this.stand.getId(), this.stand.getDataWatcher(), false);
        nmsPlayer.playerConnection.sendPacket(packet);
    }

}
