package fr.nicknqck.utils;

import java.util.ArrayList;
import java.util.List;

import fr.nicknqck.Border;
import fr.nicknqck.utils.packets.NMSPacket;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.utils.SchedulerRunnable.IScheduler;
import fr.nicknqck.utils.zephyr.Cuboid;
import fr.nicknqck.utils.zephyr.SchedulerRunnable;

public class PlayerPlate {
    @SuppressWarnings("unused")
	private final Material material;
    @SuppressWarnings("unused")
	private final byte data;

    public PlayerPlate(Material material, byte data,int destroy) {
        List<Player> tp = new ArrayList<>();
        this.material = material;
        this.data = data;
        int b = 0;
        int a = tp.size();
        CircleMesh mesh = new CircleMesh((Border.getMaxBorderSize() / 1.2D), 160, Main.getInstance().gameWorld);
        for(int i = 0;i < a;i++){
            b++;
            Player gp = tp.get(0);
            for(Player player : Bukkit.getOnlinePlayers()){
                player.playSound(player.getLocation(), Sound.DIG_SNOW,1,1);
                NMSPacket.sendActionBarPregen(player,"§1Téléportation du joueur §6" + gp.getName() + "§1 [§6" + b + "§1/§6" + GameState.getInstance().getInGamePlayers().size() + "§1]");
            }
            Location calc = mesh.calc(tp.size(), GameState.getInstance().getInGamePlayers().size());
            calc.setY(165);
            Location aa = calc.clone().add(3,0,3);
            Location bb = calc.clone().add(-3,0,-3);
            fill(aa,bb,material,data);
            gp.getPlayer().teleport(calc.add(0,2,0));
            tp.remove(0);
            SchedulerRunnable.CreateDelayedScheduler(Main.getInstance(), destroy, new IScheduler() {
                @Override
                public void run() {
                    destroy(aa,bb);
                }
            });
        }

    }

    @SuppressWarnings("deprecation")
	private void fill(Location loc1, Location loc2, Material material, byte data) {
        Cuboid floor = new Cuboid(loc1, loc2);
        floor.getBlockList().forEach((b) -> {
            b.setTypeIdAndData(material.getId(), data, true);
        });
        Cuboid walls = new Cuboid(loc1.clone().add(0.0D, 1.0D, 0.0D), loc2.clone().add(0.0D, 7.0D, 0.0D));
        walls.getWalls().forEach((b) -> {
            b.setType(Material.BARRIER);
        });
    }
    private void destroy(Location loc1, Location loc2) {
        Cuboid floor = new Cuboid(loc1, loc2);
        floor.getBlockList().forEach((b) -> {
            b.setType(Material.AIR);
        });
        Cuboid walls = new Cuboid(loc1.clone().add(0.0D, 1.0D, 0.0D), loc2.clone().add(0.0D, 7.0D, 0.0D));
        walls.getWalls().forEach((b) -> {
            b.setType(Material.AIR);
        });
    }
}
