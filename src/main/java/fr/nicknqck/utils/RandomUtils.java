package fr.nicknqck.utils;

import java.util.Random;

import fr.nicknqck.Main;
import org.bukkit.ChatColor;

public class RandomUtils {
    public static int getRandomInt(int min, int max) {
    	int toReturn =min + (Main.RANDOM).nextInt(max) - min;
    	if (toReturn <min) {
    		toReturn = min;
    	}
    	if (toReturn > max) {
    		toReturn = max;
    	}
        System.out.println("toReturn: "+toReturn);
        return toReturn;
    }
    public static int getRandomDeviationValue(int value, int min, int max) {
        int i = max - min;
        int a = (Main.RANDOM).nextInt(i * 2) - i;
        return a + ((a < value) ? -1 : 1) * min;
    }
    private static final String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generateRandomString(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("La longueur doit être supérieure à zéro.");
        }

        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
            char randomChar = ALLOWED_CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }
    private static final ChatColor[] COLORS = ChatColor.values();

    public static String generateRandomString(int length, boolean color) {
        if (length <= 0) {
            throw new IllegalArgumentException("La longueur doit être supérieure à zéro.");
        }

        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = Main.RANDOM.nextInt(ALLOWED_CHARACTERS.length());
            char randomChar = ALLOWED_CHARACTERS.charAt(randomIndex);

            if (color) {
                ChatColor randomColor;
                do {
                    randomColor = COLORS[Main.RANDOM.nextInt(COLORS.length)];
                } while (randomColor == ChatColor.MAGIC || randomColor == ChatColor.UNDERLINE || randomColor == ChatColor.STRIKETHROUGH);

                sb.append(randomColor.toString());
            }

            sb.append(randomChar);
        }

        if (color) {
            // Réinitialiser la couleur à la fin de la chaîne générée
            sb.append(ChatColor.RESET);
        }

        return sb.toString();
    }
    public static boolean getRandomProbability(double pourcentage) {
    	return performActionWithProbability(pourcentage/100);
    }
    public static boolean performActionWithProbability(double probability) {

        // Générez un nombre aléatoire entre 0 et 1
        double randomValue = Main.RANDOM.nextDouble();
        // Vérifiez si le nombre aléatoire est inférieur ou égal à la probabilité spécifiée
        if (randomValue != 0) {
            return randomValue <= probability;
        } else {
        	return false;
        }
    }
    public static boolean getOwnRandomProbability(double pourcentage) {
    	if (pourcentage >= 100) {
    		return true;
    	}
    	if (pourcentage <= 0) {
    		return false;
    	}
    	
    	double random = Main.RANDOM.nextDouble();
    	double value = random*100;
        return value <= pourcentage;
    }
}