package fr.nicknqck.managers;

import fr.nicknqck.Main;
import fr.nicknqck.events.custom.GameEndEvent;
import fr.nicknqck.events.custom.GameStartEvent;
import fr.nicknqck.events.custom.RoleGiveEvent;
import fr.nicknqck.interfaces.ISubRoleWorld;
import fr.nicknqck.utils.event.EventUtils;
import lombok.Getter;
import lombok.NonNull;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RoleWorldManager implements Listener {

    @Getter
    private final Map<String, ISubRoleWorld> subRoleWorldMap = new HashMap<>();
    private boolean startedManaging = false;

    public RoleWorldManager() {
        EventUtils.registerEvents(this);
    }

    public void addWorldManaged(@NonNull final String worldName, @NonNull final ISubRoleWorld iSubRoleWorld) {
        subRoleWorldMap.put(worldName, iSubRoleWorld);
    }
    @EventHandler
    private void onGameStart(@NonNull final GameStartEvent event) {
        this.startedManaging = true;
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onGameStop(@NonNull final GameEndEvent event) {
        this.startedManaging = false;
        for (String string : this.subRoleWorldMap.keySet()) {
            Main.getInstance().deleteWorld(string);
            final ISubRoleWorld iSubRoleWorld = this.getSubRoleWorldMap().get(string);
            iSubRoleWorld.setHasBeenPregen(false);
        }
        this.subRoleWorldMap.clear();
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onEndGiveRole(@NonNull final RoleGiveEvent event) {
        if (!event.isEndGive())return;
        if (!startedManaging)return;
        for (@NonNull final String string : this.subRoleWorldMap.keySet()) {
            final ISubRoleWorld iSubRoleWorld = this.subRoleWorldMap.get(string);
            final World world = Bukkit.getWorld(string);
            if (world != null) {
                for (Player player : world.getPlayers()) {
                    player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                }
                Main.getInstance().deleteWorld(iSubRoleWorld.getWorldName());
            }
            extractWorld(iSubRoleWorld.getZipFileName());
            final World w = iSubRoleWorld.createWorld();
            iSubRoleWorld.startPregen(w);
        }
    }
    public void extractWorld(String source) {
        Main.getInstance().saveResource(source, true);
        String link = Main.getInstance().getDataFolder().getAbsolutePath() + "/" + source;
        try {
            ZipFile zipFile = new ZipFile(link);
            zipFile.extractAll(Bukkit.getWorldContainer().getAbsolutePath());
            System.out.println("Extracting " + source);

            // Supprimer le uid.dat pour éviter le "duplicate world" au rechargement
            final String worldName = source.replace(".zip", "");
            final File uidFile = new File(Bukkit.getWorldContainer(), worldName + "/uid.dat");
            if (uidFile.exists()) {
                if (uidFile.delete()) {
                    Main.getInstance().debug("[RoleWorldManager] uid.dat supprimé pour " + worldName);
                } else {
                    Main.getInstance().getLogger().warning("[RoleWorldManager] Impossible de supprimer uid.dat pour " + worldName);
                }
            }
        } catch (ZipException e) {
            e.fillInStackTrace();
        }
    }
}