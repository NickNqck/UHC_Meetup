package fr.nicknqck.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import fr.nicknqck.Main;
import fr.nicknqck.player.GamePlayer;
import lombok.NonNull;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.builder.TeamList;

public class Loc {

	public static List<List<Location>> getShape(Player player){
        List<List<Location>> shape = new ArrayList<>();
        Location initialLocation = player.getLocation().clone();
        Vector direction = initialLocation.getDirection();
        for (int i = 1; i <= 10; i++) {
            List<Location> line = new ArrayList<>();

            Vector front = direction.clone().multiply(i);

            line.add(initialLocation.clone().add(front));
            for (int j = 0; j <= 2; j++) {
                Vector right = getRightHeadDirection(player).multiply(j), left = getLeftHeadDirection(player).multiply(j);

                line.add(initialLocation.clone().add(front.clone().add(right)));
                line.add(initialLocation.clone().add(front.clone().add(left)));
            }
            shape.add(line);
        }
        return shape;
    }
	public static Location getRandomLocationAroundPlayer(Player player, double radius) {
	    World world = player.getWorld();
	    double angle = Math.random() * Math.PI * 2; // Angle aléatoire
	    double x = Math.cos(angle) * radius; // Calcul des coordonnées x et z
	    double z = Math.sin(angle) * radius;

	    Location playerLocation = player.getLocation();
	    double newX = playerLocation.getX() + x; // Ajout des coordonnées à la position actuelle du joueur
	    double newZ = playerLocation.getZ() + z;

	    double newY = world.getHighestBlockYAt((int) newX, (int) newZ); // Obtention de la hauteur du sol
	    return new Location(world, newX, newY, newZ); // Création et retour de la nouvelle location
	}

    public static Vector getRightHeadDirection(Player player) {
        Vector direction = player.getLocation().getDirection().normalize();
        return new Vector(-direction.getZ(), 0.0, direction.getX()).normalize();
    }
    public static Vector getLeftHeadDirection(Player player) {
        Vector direction = player.getLocation().getDirection().normalize();
        return new Vector(direction.getZ(), 0.0, -direction.getX()).normalize();
    }
    public static void teleportBehindPlayer(final Player teleported,final Player target) {
		teleported.teleport(getLocationBehindPlayer(target));
    }
    public static Location getLocationBehindPlayer(@NonNull final Player player) {
        Location loc = player.getLocation().clone();
        loc.setX(loc.getX()+Math.cos(Math.toRadians(-player.getEyeLocation().getYaw()+90)));
        loc.setZ(loc.getZ()+Math.sin(Math.toRadians(player.getEyeLocation().getYaw()-90)));
        loc.setPitch(0);
        return loc;
    }
    public static Location getLocationBehindPlayer(@NonNull final Player player, double distance) {
        Location loc = player.getLocation().clone();

        float yaw = player.getEyeLocation().getYaw();

        // Calcul du vecteur derrière le joueur
        double x = Math.cos(Math.toRadians(-yaw + 90)) * distance;
        double z = Math.sin(Math.toRadians(yaw - 90)) * distance;

        loc.add(x, 0, z);
        loc.setPitch(0);

        return loc;
    }

    public static List<Player> getNearbyPlayersExcept(Entity entity, int distance) {
        List<Player> toReturn = new ArrayList<>();
        entity.getWorld().getPlayers().stream()
        		.filter(target -> target.getWorld().equals(entity.getWorld()))
                .filter(target -> target.getGameMode() != GameMode.SPECTATOR)
                .filter(target -> target.getUniqueId() != entity.getUniqueId())
                .filter(target -> target.getLocation().distance(entity.getLocation()) <= distance)
                .forEach(toReturn::add);
        return toReturn;
    }
    public static List<Player> getNearbyPlayersExcept(Entity entity, int distance, Player... imunised) {
        List<Player> toReturn = new ArrayList<>();
        List<Player> imun = new ArrayList<>();
        Collections.addAll(imun, imunised);
        Bukkit.getOnlinePlayers().stream()
        		.filter(target -> target.getWorld().equals(entity.getWorld()))
                .filter(target -> target.getGameMode() != GameMode.SPECTATOR)
                .filter(target -> target.getUniqueId() != entity.getUniqueId())
                .filter(target -> target.getLocation().distance(entity.getLocation()) <= distance)
                .filter(target -> !imun.contains(target))
                .forEach(toReturn::add);
        return toReturn;
    }
    public static List<Player> getNearbyPlayers(Entity entity, int distance) {
        List<Player> toReturn = new ArrayList<>();
        entity.getWorld().getPlayers().stream()
                .filter(target -> target.getGameMode() != GameMode.SPECTATOR)
                .filter(target -> target.getLocation().distance(entity.getLocation()) <= distance)
                .forEach(toReturn::add);
        return toReturn;
    }
    public static List<Player> getNearbyPlayers(Location loc, double distance) {
        List<Player> toReturn = new ArrayList<>();
        loc.getWorld().getPlayers().stream()
                .filter(target -> target.getGameMode() != GameMode.SPECTATOR)
                .filter(target -> target.getLocation().distance(loc) <= distance)
                .forEach(toReturn::add);
        return toReturn;
    }
    public static List<GamePlayer> getNearbyGamePlayers(Location loc, double distance) {
        final List<GamePlayer> toReturn = new ArrayList<>();
        for (final Player player : loc.getWorld().getPlayers()) {
            if (player.getLocation().distance(loc) <= distance) {
                if (GameState.getInstance().getGamePlayer().containsKey(player.getUniqueId())) {
                    final GamePlayer gamePlayer = GameState.getInstance().getGamePlayer().get(player.getUniqueId());
                    if (!gamePlayer.isAlive())continue;
                    toReturn.add(gamePlayer);
                }
            }
        }
        return toReturn;
    }
    public static Location getLocationAtDistance(Location origin, double distance) {
        World world = origin.getWorld();
        Random random = Main.RANDOM;

        // 1. Calculer un angle aléatoire (0 à 2 PI)
        double angle = random.nextDouble() * 2 * Math.PI;

        // 2. Calculer les coordonnées brutes à "distance" (150 blocs)
        double x = origin.getX() + (distance * Math.cos(angle));
        double z = origin.getZ() + (distance * Math.sin(angle));

        // 3. Récupérer les infos de la WorldBorder
        WorldBorder border = world.getWorldBorder();
        double size = border.getSize();
        double centerX = border.getCenter().getX();
        double centerZ = border.getCenter().getZ();

        // On calcule le rayon (moitié de la taille)
        double radius = size / 2.0;

        // 4. Définir les limites sûres (Bordure - 5 blocs)
        double minX = centerX - radius + 5;
        double maxX = centerX + radius - 5;
        double minZ = centerZ - radius + 5;
        double maxZ = centerZ + radius - 5;

        // 5. "Clamp" (Restreindre) les coordonnées X et Z
        // Si x est trop à droite, on le ramène à maxX, s'il est trop à gauche, à minX
        if (x > maxX) x = maxX;
        if (x < minX) x = minX;

        if (z > maxZ) z = maxZ;
        if (z < minZ) z = minZ;

        // 6. Ajuster la hauteur Y pour être au sol
        // On convertit en int pour chercher le bloc le plus haut à ces coordonnées
        int groundY = world.getHighestBlockYAt((int) x, (int) z);

        // On retourne la location finale (+1 en Y pour être sur le bloc et pas dedans)
        return new Location(world, x, groundY + 1, z);
    }
    public static void inverserDirectionJoueur(Player joueurCible) {
        // Récupérer l'emplacement actuel du joueur
        Location location = joueurCible.getLocation();
        
        // Inverser la direction en ajoutant 180 degrés à l'angle Yaw
        float nouveauYaw = (location.getYaw() + 180) % 360;
        location.setYaw(nouveauYaw);
        
        // Mettre à jour la nouvelle position du joueur
        joueurCible.teleport(location);
    }
    public static String getPlayerFacing(Player player) {
        Vector direction = player.getLocation().getDirection();
        double x = direction.getX();
        double z = direction.getZ();

        if (Math.abs(x) > Math.abs(z)) {
            return x > 0 ? "EAST" : "WEST";
        } else {
            return z > 0 ? "SOUTH" : "NORTH";
        }
    }
    public static double getDirectionTo(final Player paramPlayer, final Location paramLocation) {
        final Location localLocation = paramPlayer.getLocation().clone();
        localLocation.setY(0.0);
        paramLocation.setY(0.0);
        final Vector localVector1 = localLocation.getDirection();
        final Vector localVector2 = paramLocation.subtract(localLocation).toVector().normalize();
        double d1 = Math.toDegrees(Math.atan2(localVector1.getX(), localVector1.getZ()));
        d1 -= Math.toDegrees(Math.atan2(localVector2.getX(), localVector2.getZ()));
        d1 = (int) (d1 + 22.5) % 360;
        if (d1 < 0.0) {
            d1 += 360.0;
        }
        return d1;
    }
    public static double getDirectionTo(final GamePlayer paramPlayer, final Location paramLocation) {
        final Location localLocation = paramPlayer.getLastLocation().clone();
        localLocation.setY(0.0);
        paramLocation.setY(0.0);
        final Vector localVector1 = localLocation.getDirection();
        final Vector localVector2 = paramLocation.subtract(localLocation).toVector().normalize();
        double d1 = Math.toDegrees(Math.atan2(localVector1.getX(), localVector1.getZ()));
        d1 -= Math.toDegrees(Math.atan2(localVector2.getX(), localVector2.getZ()));
        d1 = (int) (d1 + 22.5) % 360;
        if (d1 < 0.0) {
            d1 += 360.0;
        }
        return d1;
    }
    public static Player getNearestPlayerforNakime(Player referencePlayer, double radius, GameState gameState) {
        Player nearestPlayer = null;
        double nearestDistanceSquared = Double.MAX_VALUE;

        for (Player onlinePlayer : referencePlayer.getWorld().getPlayers()) {
            // Assurez-vous de ne pas comparer le joueur avec lui-même
            if (!onlinePlayer.equals(referencePlayer)) {
            	if (referencePlayer.getWorld().getName().equals("nakime")) {
            		if (onlinePlayer.getWorld().equals(referencePlayer.getWorld())) {
                		if (!gameState.hasRoleNull(onlinePlayer.getUniqueId())) {
                			if (gameState.getGamePlayer().get(onlinePlayer.getUniqueId()).getRole().getOriginTeam() != TeamList.Demon) {
                				double distanceSquared = referencePlayer.getLocation().distanceSquared(onlinePlayer.getLocation());

                                if (distanceSquared <= radius * radius && distanceSquared < nearestDistanceSquared) {
                                    nearestPlayer = onlinePlayer;
                                    nearestDistanceSquared = distanceSquared;
                                }
                			}
                		}
                	}
            	}                
            }
        }

        return nearestPlayer;
    }

    public static String getDirectionMate(Player player, Player mate, int distance) {
        if (player.getWorld().equals(mate.getWorld())) {
            if (player.getLocation().distance(mate.getLocation()) > distance) {
                if (getDirectionTo(player, mate.getLocation().clone()) <= 45.0) {
                    return "§6" + mate.getName() + "§f ⬆ ? ";
                } else if (getDirectionTo(player, mate.getLocation().clone()) <= 90.0) {
                    return "§6" + mate.getName() + "§f ⬈ ? ";
                } else if (getDirectionTo(player, mate.getLocation().clone()) <= 135.0) {
                    return "§6" + mate.getName() + "§f➨ ? ";
                } else if (getDirectionTo(player, mate.getLocation().clone()) <= 180.0) {
                    return "§6" + mate.getName() + "§f ⬊ ? ";
                } else if (getDirectionTo(player, mate.getLocation().clone()) <= 225.0) {
                    return "§6" + mate.getName() + "§f ⬇ ? ";
                } else if (getDirectionTo(player, mate.getLocation().clone()) <= 270.0) {
                    return "§6" + mate.getName() + "§f ⬋ ? ";
                } else if (getDirectionTo(player, mate.getLocation().clone()) <= 315.0) {
                    return "§6" + mate.getName() + "§f ⬅ ? ";
                } else {
                    return "§6" + mate.getName() + "§f ⬉ ? ";
                }
            } else {
                if (getDirectionTo(player, mate.getLocation().clone()) <= 45.0) {
                    return "§6" + mate.getName() + "§f ⬆ " + ((int) player.getLocation().distance(mate.getLocation())) + " ";
                } else if (getDirectionTo(player, mate.getLocation().clone()) <= 90.0) {
                    return "§6" + mate.getName() + "§f ⬈ " + ((int) player.getLocation().distance(mate.getLocation())) + " ";
                } else if (getDirectionTo(player, mate.getLocation().clone()) <= 135.0) {
                    return "§6" + mate.getName() + "§f➨ " + ((int) player.getLocation().distance(mate.getLocation())) + " ";
                } else if (getDirectionTo(player, mate.getLocation().clone()) <= 180.0) {
                    return "§6" + mate.getName() + "§f ⬊ " + ((int) player.getLocation().distance(mate.getLocation())) + " ";
                } else if (getDirectionTo(player, mate.getLocation().clone()) <= 225.0) {
                    return "§6" + mate.getName() + "§f ⬇ " + ((int) player.getLocation().distance(mate.getLocation())) + " ";
                } else if (getDirectionTo(player, mate.getLocation().clone()) <= 270.0) {
                    return "§6" + mate.getName() + "§f ⬋ " + ((int) player.getLocation().distance(mate.getLocation())) + " ";
                } else if (getDirectionTo(player, mate.getLocation().clone()) <= 315.0) {
                    return "§6" + mate.getName() + "§f ⬅ " + ((int) player.getLocation().distance(mate.getLocation())) + " ";
                } else {
                    return "§6" + mate.getName() + "§f ⬉ " + ((int) player.getLocation().distance(mate.getLocation())) + " ";
                }
            }
        } else {
            return "§6" + mate.getName() + " §f? ";
        }
    }

    public static String getDirectionMate(Player player, Player mate, boolean afficherMatePseudo) {
    	if (afficherMatePseudo) {
    		if (player.getWorld().equals(mate.getWorld())) {
                if (getDirectionTo(player, mate.getLocation().clone()) <= 45.0) {
                    return "§6" + mate.getName() + "§f "+ArrowTargetUtils.calculateArrow(player, mate.getLocation())+" " + ((int) player.getLocation().distance(mate.getLocation())) + " ";
                } else if (getDirectionTo(player, mate.getLocation().clone()) <= 90.0) {
                    return "§6" + mate.getName() + "§f "+ArrowTargetUtils.calculateArrow(player, mate.getLocation())+" " + ((int) player.getLocation().distance(mate.getLocation())) + " ";
                } else if (getDirectionTo(player, mate.getLocation().clone()) <= 135.0) {
                    return "§6" + mate.getName() + "§f"+ArrowTargetUtils.calculateArrow(player, mate.getLocation())+" " + ((int) player.getLocation().distance(mate.getLocation())) + " ";
                } else if (getDirectionTo(player, mate.getLocation().clone()) <= 180.0) {
                    return "§6" + mate.getName() + "§f "+ArrowTargetUtils.calculateArrow(player, mate.getLocation())+" " + ((int) player.getLocation().distance(mate.getLocation())) + " ";
                } else if (getDirectionTo(player, mate.getLocation().clone()) <= 225.0) {
                    return "§6" + mate.getName() + "§f "+ArrowTargetUtils.calculateArrow(player, mate.getLocation())+" " + ((int) player.getLocation().distance(mate.getLocation())) + " ";
                } else if (getDirectionTo(player, mate.getLocation().clone()) <= 270.0) {
                    return "§6" + mate.getName() + "§f "+ArrowTargetUtils.calculateArrow(player, mate.getLocation())+" " + ((int) player.getLocation().distance(mate.getLocation())) + " ";
                } else if (getDirectionTo(player, mate.getLocation().clone()) <= 315.0) {
                    return "§6" + mate.getName() + "§f "+ArrowTargetUtils.calculateArrow(player, mate.getLocation())+" " + ((int) player.getLocation().distance(mate.getLocation())) + " ";
                } else {
                    return "§6" + mate.getName() + "§f "+ArrowTargetUtils.calculateArrow(player, mate.getLocation())+" " + ((int) player.getLocation().distance(mate.getLocation())) + " ";
                }
            } else {
                return "§6" + mate.getName() + " §f? ";
            }
    	}else {
    		if (player.getWorld().equals(mate.getWorld())) {
                if (getDirectionTo(player, mate.getLocation().clone()) <= 45.0) {
                    return "§f "+ArrowTargetUtils.calculateArrow(player, mate.getLocation())+" " + ((int) player.getLocation().distance(mate.getLocation())) + " ";
                } else if (getDirectionTo(player, mate.getLocation().clone()) <= 90.0) {
                    return "§f "+ArrowTargetUtils.calculateArrow(player, mate.getLocation())+" " + ((int) player.getLocation().distance(mate.getLocation())) + " ";
                } else if (getDirectionTo(player, mate.getLocation().clone()) <= 135.0) {
                    return "§f"+ArrowTargetUtils.calculateArrow(player, mate.getLocation())+" " + ((int) player.getLocation().distance(mate.getLocation())) + " ";
                } else if (getDirectionTo(player, mate.getLocation().clone()) <= 180.0) {
                    return "§f "+ArrowTargetUtils.calculateArrow(player, mate.getLocation())+" " + ((int) player.getLocation().distance(mate.getLocation())) + " ";
                } else if (getDirectionTo(player, mate.getLocation().clone()) <= 225.0) {
                    return "§f "+ArrowTargetUtils.calculateArrow(player, mate.getLocation())+" " + ((int) player.getLocation().distance(mate.getLocation())) + " ";
                } else if (getDirectionTo(player, mate.getLocation().clone()) <= 270.0) {
                    return "§f "+ArrowTargetUtils.calculateArrow(player, mate.getLocation())+" " + ((int) player.getLocation().distance(mate.getLocation())) + " ";
                } else if (getDirectionTo(player, mate.getLocation().clone()) <= 315.0) {
                    return "§f "+ArrowTargetUtils.calculateArrow(player, mate.getLocation())+" " + ((int) player.getLocation().distance(mate.getLocation())) + " ";
                } else {
                    return "§f "+ArrowTargetUtils.calculateArrow(player, mate.getLocation())+" " + ((int) player.getLocation().distance(mate.getLocation())) + " ";
                }
            } else {
                return " §f? ";
            }
    	}
    }
    public static String getDirectionMate(GamePlayer player, GamePlayer mate, boolean afficherMatePseudo) {
        if (afficherMatePseudo) {
            if (player.getLastLocation().getWorld().equals(mate.getLastLocation().getWorld())) {
                if (getDirectionTo(player, mate.getLastLocation().clone()) <= 45.0) {
                    return "§6" + mate.getPlayerName() + "§f "+ArrowTargetUtils.calculateArrow(player, mate.getLastLocation())+" " + ((int) player.getLastLocation().distance(mate.getLastLocation())) + " ";
                } else if (getDirectionTo(player, mate.getLastLocation().clone()) <= 90.0) {
                    return "§6" + mate.getPlayerName() + "§f "+ArrowTargetUtils.calculateArrow(player, mate.getLastLocation())+" " + ((int) player.getLastLocation().distance(mate.getLastLocation())) + " ";
                } else if (getDirectionTo(player, mate.getLastLocation().clone()) <= 135.0) {
                    return "§6" + mate.getPlayerName() + "§f"+ArrowTargetUtils.calculateArrow(player, mate.getLastLocation())+" " + ((int) player.getLastLocation().distance(mate.getLastLocation())) + " ";
                } else if (getDirectionTo(player, mate.getLastLocation().clone()) <= 180.0) {
                    return "§6" + mate.getPlayerName() + "§f "+ArrowTargetUtils.calculateArrow(player, mate.getLastLocation())+" " + ((int) player.getLastLocation().distance(mate.getLastLocation())) + " ";
                } else if (getDirectionTo(player, mate.getLastLocation().clone()) <= 225.0) {
                    return "§6" + mate.getPlayerName() + "§f "+ArrowTargetUtils.calculateArrow(player, mate.getLastLocation())+" " + ((int) player.getLastLocation().distance(mate.getLastLocation())) + " ";
                } else if (getDirectionTo(player, mate.getLastLocation().clone()) <= 270.0) {
                    return "§6" + mate.getPlayerName() + "§f "+ArrowTargetUtils.calculateArrow(player, mate.getLastLocation())+" " + ((int) player.getLastLocation().distance(mate.getLastLocation())) + " ";
                } else if (getDirectionTo(player, mate.getLastLocation().clone()) <= 315.0) {
                    return "§6" + mate.getPlayerName() + "§f "+ArrowTargetUtils.calculateArrow(player, mate.getLastLocation())+" " + ((int) player.getLastLocation().distance(mate.getLastLocation())) + " ";
                } else {
                    return "§6" + mate.getPlayerName() + "§f "+ArrowTargetUtils.calculateArrow(player, mate.getLastLocation())+" " + ((int) player.getLastLocation().distance(mate.getLastLocation())) + " ";
                }
            } else {
                return "§6" + mate.getPlayerName() + " §f? ";
            }
        }else {
            if (player.getLastLocation().getWorld().equals(mate.getLastLocation().getWorld())) {
                if (getDirectionTo(player, mate.getLastLocation().clone()) <= 45.0) {
                    return "§f "+ArrowTargetUtils.calculateArrow(player, mate.getLastLocation())+" " + ((int) player.getLastLocation().distance(mate.getLastLocation())) + " ";
                } else if (getDirectionTo(player, mate.getLastLocation().clone()) <= 90.0) {
                    return "§f "+ArrowTargetUtils.calculateArrow(player, mate.getLastLocation())+" " + ((int) player.getLastLocation().distance(mate.getLastLocation())) + " ";
                } else if (getDirectionTo(player, mate.getLastLocation().clone()) <= 135.0) {
                    return "§f"+ArrowTargetUtils.calculateArrow(player, mate.getLastLocation())+" " + ((int) player.getLastLocation().distance(mate.getLastLocation())) + " ";
                } else if (getDirectionTo(player, mate.getLastLocation().clone()) <= 180.0) {
                    return "§f "+ArrowTargetUtils.calculateArrow(player, mate.getLastLocation())+" " + ((int) player.getLastLocation().distance(mate.getLastLocation())) + " ";
                } else if (getDirectionTo(player, mate.getLastLocation().clone()) <= 225.0) {
                    return "§f "+ArrowTargetUtils.calculateArrow(player, mate.getLastLocation())+" " + ((int) player.getLastLocation().distance(mate.getLastLocation())) + " ";
                } else if (getDirectionTo(player, mate.getLastLocation().clone()) <= 270.0) {
                    return "§f "+ArrowTargetUtils.calculateArrow(player, mate.getLastLocation())+" " + ((int) player.getLastLocation().distance(mate.getLastLocation())) + " ";
                } else if (getDirectionTo(player, mate.getLastLocation().clone()) <= 315.0) {
                    return "§f "+ArrowTargetUtils.calculateArrow(player, mate.getLastLocation())+" " + ((int) player.getLastLocation().distance(mate.getLastLocation())) + " ";
                } else {
                    return "§f "+ArrowTargetUtils.calculateArrow(player, mate.getLastLocation())+" " + ((int) player.getLastLocation().distance(mate.getLastLocation())) + " ";
                }
            } else {
                return " §f? ";
            }
        }
    }

}
