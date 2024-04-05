package fr.nicknqck.utils;

import org.bukkit.ChatColor;

import com.google.common.base.Strings;

public class StringUtils {
    public static String getProgressBar(int current, int max, int totalBars, char symbol, ChatColor completedColor, ChatColor notCompletedColor) {
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);

        return Strings.repeat("" + completedColor + symbol, progressBars)
                + Strings.repeat("" + notCompletedColor + symbol, totalBars - progressBars);
    }

    public static String secondTowardsConventional(int second){
        int hours = second/3600;
        int minAndSec = second%3600;
        int min = minAndSec/60;
        int sec = minAndSec%60;

        if(hours == 0){
            if(min == 0) return (sec > 9 ?  sec : "0" + sec) + "s";
            return (min > 9 ?  min : "0" + min) + "m" + (sec > 9 ?  sec : "0" + sec) + "s";
        }
        return  hours + "h" + (min > 9 ?  min : "0" + min) + "m" + (sec > 9 ?  sec : "0" + sec) + "s";
    }

    public static String secondsTowardsBeautiful(int seconds){
        int hours = seconds/3600;
        int minAndSec = seconds%3600;
        int min = minAndSec/60;
        int sec = minAndSec%60;

        if(hours == 0 && min == 0){
            return sec + "s";
        }

        if(hours == 0 && sec == 0){
            return min + "m";
        }        

        if(min == 0 && sec == 0){
            return  hours + "h";
        }
        
        if (hours == 0 && min != 0 && sec != 0) {
        	return min+"m "+sec+"s";
        }
        
        if(sec == 0){
            return  hours + "h " + min + "m";
        }
        return  hours + "h " + min + "m " + sec + "s";
    }
    public static String secondsTowardsBeautifulinScoreboard(int seconds){
        int hours = seconds/3600;
        int minAndSec = seconds%3600;
        int min = minAndSec/60;
        int sec = minAndSec%60;
        
        if(hours == 0 && min == 0){
            return "§c"+ sec + "s";
        }

        if(hours == 0 && sec == 0){
            return "§c"+ min + "m";
        }        

        if(min == 0 && sec == 0){
            return  "§c"+hours + "h";
        }
        
        if (hours == 0 && min != 0 && sec != 0) {
        	return "§c"+min+"m "+sec+"s";
        }
        
        if(sec == 0){
            return  "§c"+hours + "h " + min + "m";
        }
        
        return  "§c"+hours + "h " + min + "m " + sec + "s";
    }
    public static String replaceUnderscoreWithSpace(String... strings) {
        StringBuilder result = new StringBuilder();

        for (String str : strings) {
            if (str != null) {
                if (result.length() > 0) {
                    result.append(" ");  // Ajoute un espace entre les mots
                }

                result.append(str.replace("_", " "));
            }
        }

        return result.toString();
    }
}
