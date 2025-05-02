package fr.nicknqck.utils.vectors;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class SwordClass {
    private List<ArmorStand> a = new ArrayList<>();
    private Player pl;
    private static List<SwordClass> sc = new ArrayList<>();
    private static final double increment = Math.PI * 2 / 10;

    public SwordClass(Player pl) {
        this.pl = pl;
        sc.add(this);
        HashMap<ArmorStand, Double> hm = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            ArmorStand sword = pl.getWorld().spawn(pl.getLocation(), ArmorStand.class);
            sword.setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
            sword.setRightArmPose(new EulerAngle(Math.toRadians(-80), 0, 0));
            sword.setVisible(false);
            sword.setBasePlate(true);
            sword.setGravity(false);
            a.add(sword);
            hm.put(sword, increment * i);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                Location loc = pl.getLocation();
                for (int i = 0; i < a.size(); i++) {
                    ArmorStand as = a.get(i);
                    double angle = hm.get(as);
                    double x = Math.sin(angle) * 5;
                    double z = Math.cos(angle) * 5;
                    Location l = new Location(
                            loc.getWorld(), loc.getX() + x, loc.getY(), loc.getZ() + z
                    );
                    teleportSword(as, l);
                    summonLosange(l.clone().add(0, 1.3, 0));
                    hm.put(as, hm.get(as) + Math.PI / 64);
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }

    public static Location lookAt(Location loc, Location lookat) {
        //Clone the loc to prevent applied changes to the input loc
        loc = loc.clone();

        // Values of change in distance (make it relative)
        double dx = lookat.getX() - loc.getX();
        double dy = lookat.getY() - loc.getY();
        double dz = lookat.getZ() - loc.getZ();

        // Set yaw
        if (dx != 0) {
            // Set yaw start value based on dx
            if (dx < 0) {
                loc.setYaw((float) (1.5 * Math.PI));
            } else {
                loc.setYaw((float) (0.5 * Math.PI));
            }
            loc.setYaw((float) loc.getYaw() - (float) Math.atan(dz / dx));
        } else if (dz < 0) {
            loc.setYaw((float) Math.PI);
        }

        // Get the distance from dx/dz
        double dxz = Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));

        // Set pitch
        loc.setPitch((float) -Math.atan(dy / dxz));

        // Set values, convert to degrees (invert the yaw since Bukkit uses a different yaw dimension format)
        loc.setYaw(-loc.getYaw() * 180f / (float) Math.PI);
        loc.setPitch(loc.getPitch() * 180f / (float) Math.PI);

        return loc;
    }

    public void launchSwords(){
        for (int i = 0; i < a.size(); i++) {
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                launchSword(null);
            } , 2L *i);
        }
    }
    public void launchSword(Entity pls) {
        if (a.isEmpty()) return;
        ArmorStand sword = a.get(0);
        if(pls == null) {
            List<Entity> s = sword.getNearbyEntities(30, 30, 30)
                    .stream()
                    .filter(entity -> !(entity instanceof ArmorStand))
                    .filter(entity -> !entity.getUniqueId().equals(pl.getUniqueId()))
                    .collect(Collectors.toList());
           pls = s.stream().min(Comparator.comparingDouble(value -> value.getLocation().distance(sword.getLocation()))).orElse(null);
        }
        if(pls == null){
            pl.sendMessage("§4Vous ne pouvez pas lancer votre épée personne n'est proche !");
            return;
        }
        a.remove(sword);
        sword.setRightArmPose(new EulerAngle(
                -sword.getRightArmPose().getX(), -sword.getRightArmPose().getY(),
                -sword.getRightArmPose().getZ()));
        Entity finalPls = pls;
        new BukkitRunnable() {

            int i = 0;
            @Override
            public void run() {
                if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                i++;
                if(i == 75){
                    cancel();
                    sword.setVelocity(sword.getEyeLocation().getDirection().normalize());
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                                cancel();
                                return;
                            }
                            sword.teleport(sword.getLocation().add(sword.getEyeLocation().getDirection().multiply(1.2)));
                            Vector v = sword.getLocation().getDirection();
                            v.multiply(0.5);
                            Location l = getArmTip(sword).add(v).clone();
                            if(!l.getBlock().getType().equals(Material.AIR)){
                                if(l.getBlock().getType().isTransparent() || l.getBlock().getType().equals(Material.LEAVES)
                                        || l.getBlock().getType().equals(Material.LEAVES_2)) {
                                    l.getBlock().breakNaturally();
                                    return;
                                }
                                cancel();
                            }
                            for (double p = 0; p <= 90; p += 15) {
                                double x2 = l.getX() + 1 * Math.cos(p + i / 10f);
                                double y2 = l.getY();
                                double z2 = l.getZ() + 1 * Math.sin(p + i / 10f);
                                Location par2 = new Location(l.getWorld(), x2, y2, z2);
                                spawnParticle(par2,-1,0,0);
                            }
                            for (Entity nearbyEntity : l.getWorld().getNearbyEntities(l, 0.2, 0.1, 0.2)) {
                                if(nearbyEntity.getUniqueId().equals(pl.getUniqueId())) continue;
                                if(!(nearbyEntity instanceof LivingEntity)) continue;
                                if(nearbyEntity instanceof ArmorStand) continue;
                                System.out.println(nearbyEntity.getType());
                                sword.remove();
                                cancel();
                                sword.getWorld().playSound(l, Sound.IRONGOLEM_HIT,0,0);
                                LivingEntity lv = (LivingEntity) nearbyEntity;
                                lv.damage(11.75,pl);
                                lv.setNoDamageTicks(0);
                            }
                        }
                    }.runTaskTimer(Main.getInstance(), 0, 1);
                    return;
                }
                sword.teleport(lookAt(sword.getLocation(), finalPls.getLocation()));
                sword.setRightArmPose(new EulerAngle(Math.toRadians(sword.getLocation().getPitch()), 0, 0));
                Vector v = sword.getLocation().getDirection();
                v.multiply(0.5);
                Location l = getArmTip(sword).add(v).clone();
                for (double p = 0; p <= 90; p += 15) {
                    double x2 = l.getX() + 1 * Math.cos(p + i / 10f);
                    double y2 = l.getY();
                    double z2 = l.getZ() + 1 * Math.sin(p + i / 10f);
                    Location par2 = new Location(l.getWorld(), x2, y2, z2);
                    spawnParticle(par2, (float) (255 - i * 3.4),0,0);
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }

    public static SwordClass getSword(Player pl) {
        return sc.stream().filter(swordClass -> swordClass.pl.equals(pl)).findFirst().orElse(null);
    }

    private Vector rotateAroundAxisY(Vector v, double angle) {
        angle = -angle;
        angle = Math.toRadians(angle);
        double x, z, cos, sin;
        cos = Math.cos(angle);
        sin = Math.sin(angle);
        x = v.getX() * cos + v.getZ() * sin;
        z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }

   private Location armorStandToSwordLoc(ArmorStand sword) {
        Vector l = sword.getEyeLocation().getDirection().multiply(0.7);
        rotateAroundAxisY(l, 38);
        Location ls = sword.getEyeLocation().clone();
        ls.setX(ls.getX() + l.getX());
        ls.setY(ls.getY() - 0.2);
        ls.setZ(ls.getZ() + l.getZ());
        return ls;
    }
    private Location getArmTip(ArmorStand as) {
        // Gets shoulder location
        Location asl = as.getLocation().clone();
        asl.setYaw(asl.getYaw() + 90f);
        Vector dir = asl.getDirection();
        asl.setX(asl.getX() + 5f / 16f * dir.getX());
        asl.setY(asl.getY() + 22f / 16f);
        asl.setZ(asl.getZ() + 5f / 16f * dir.getZ());
        // Get Hand Location

        EulerAngle ea = as.getRightArmPose();
        Vector armDir = getDirection(ea.getY(), ea.getX(), -ea.getZ());
        armDir = rotateAroundAxisY(armDir, Math.toRadians(asl.getYaw()-90f));
        asl.setX(asl.getX() + 10f / 16f * armDir.getX());
        asl.setY(asl.getY() + 10f / 16f * armDir.getY());
        asl.setZ(asl.getZ() + 10f / 16f * armDir.getZ());

        return asl;
    }

    private Vector getDirection(Double yaw, Double pitch, Double roll) {
        Vector v = new Vector(0, -1, 0);
        v = rotateAroundAxisX(v, pitch);
        v = rotateAroundAxisY(v, yaw);
        v = rotateAroundAxisZ(v, roll);
        return v;
    }

    private Vector rotateAroundAxisX(Vector v, double angle) {
        double y, z, cos, sin;
        cos = Math.cos(angle);
        sin = Math.sin(angle);
        y = v.getY() * cos - v.getZ() * sin;
        z = v.getY() * sin + v.getZ() * cos;
        return v.setY(y).setZ(z);
    }

    private Vector rotateAroundAxisZ(Vector v, double angle) {
        double x, y, cos, sin;
        cos = Math.cos(angle);
        sin = Math.sin(angle);
        x = v.getX() * cos - v.getY() * sin;
        y = v.getX() * sin + v.getY() * cos;
        return v.setX(x).setY(y);
    }

    private void teleportSword(ArmorStand sword, Location l) {
        Location loc = armorStandToSwordLoc(sword);
        Vector v = l.toVector().subtract(loc.toVector());
        Location d = sword.getLocation().add(v);
        d.setY(l.getY() - 0.2);
        sword.teleport(d);
    }

    private Location spawnParticle(Location location, float r, float g, float b) {
        location.getWorld().spigot().playEffect(location, Effect.COLOURED_DUST, 0, 1, r / 255, g / 255, b / 255, 1, 0, 64);
        return location;
    }

    private void drawLine(Location point1, Location point2, double space) {
        World world = point1.getWorld();
        Validate.isTrue(point2.getWorld().equals(world), "Lines cannot be in different worlds!");
        double distance = point1.distance(point2);
        Vector p1 = point1.toVector();
        Vector p2 = point2.toVector();
        Vector vector = p2.clone().subtract(p1).normalize().multiply(space);
        double length = 0;
        for (; length < distance; p1.add(vector)) {
            Location loc = new Location(world, p1.getX(), p1.getY(), p1.getZ());
            spawnParticle(loc, 255, 204, 255);
            length += space;
        }
    }

    private void summonLosange(Location centerd) {
        List<Location> po = new ArrayList<>();
        for (double p = 0; p <= 90; p += 22.5) {
            double x2 = centerd.getX() + 1 * Math.cos(p);
            double y2 = centerd.getY();
            double z2 = centerd.getZ() + 1 * Math.sin(p);
            Location par2 = new Location(centerd.getWorld(), x2, y2, z2);
            po.add(par2);
        }
        for (Location l : po) {
            drawLine(l, centerd.clone().add(0, 1, 0), 2.5);
            drawLine(l, centerd.clone().add(0, -1, 0), 2.5);
        }
    }
}
