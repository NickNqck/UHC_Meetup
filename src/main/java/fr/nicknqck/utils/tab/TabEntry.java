package fr.nicknqck.utils.tab;

import com.mojang.authlib.GameProfile;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

import java.util.UUID;

/**
 * Représente une entrée dans la tab list d'un joueur.
 * Contient toutes les données nécessaires pour afficher le joueur
 * même s'il est déconnecté.
 */
@Getter
public class TabEntry {

    private final UUID uuid;
    private final String playerName;

    @Setter private String prefix          = "";
    @Setter private String suffix          = "";
    @Setter private ChatColor color        = ChatColor.WHITE;
    @Setter private boolean keepWhenOffline = false;
    @Setter private boolean online         = true;

    /**
     * GameProfile stocké pour pouvoir garder l'entrée dans le tab
     * même après déconnexion. Renseigné à la connexion du joueur.
     */
    @Setter private GameProfile gameProfile;

    public TabEntry(UUID uuid, String playerName) {
        this.uuid       = uuid;
        this.playerName = playerName;
    }

    /**
     * Retourne le préfixe complet avec la couleur du joueur.
     * Ex: pour color=RED et prefix="[Host]" → "§c[Host]"
     */
    public String getColoredPrefix() {
        return color + prefix;
    }

    /**
     * Retourne le nom du joueur coloré (sans prefix/suffix).
     */
    public String getColoredName() {
        return color + playerName;
    }
}