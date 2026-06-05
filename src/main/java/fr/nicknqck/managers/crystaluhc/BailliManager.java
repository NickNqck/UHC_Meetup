package fr.nicknqck.managers.crystaluhc;

import fr.nicknqck.Main;
import fr.nicknqck.enums.BailliInfoType;
import fr.nicknqck.events.custom.GameEndEvent;
import fr.nicknqck.events.custom.RoleGiveEvent;
import fr.nicknqck.events.custom.bailli.BailliActivateEvent;
import fr.nicknqck.events.custom.bailli.BailliDesactivateEvent;
import fr.nicknqck.events.custom.death.FinalDeathEvent;
import fr.nicknqck.interfaces.BailliInfo;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.utils.event.EventUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BailliManager implements Listener {

    @Getter
    @Setter
    private boolean activate = false;
    @Getter
    private final List<BailliInfo> bailliInfos = new ArrayList<>();

    public BailliManager() {
        EventUtils.registerEvents(this);
    }

    @EventHandler
    private void onEndGiveRole(@NonNull final RoleGiveEvent event) {
        if (!event.isEndGive())return;
        activate = true;
        @NonNull final BailliActivateEvent bailliActivateEvent = new BailliActivateEvent(this);
        Bukkit.getPluginManager().callEvent(bailliActivateEvent);
    }
    @EventHandler
    private void onGameStop(@NonNull final GameEndEvent event) {
        activate = false;
        @NonNull final BailliDesactivateEvent bailliDesactivateEvent = new BailliDesactivateEvent(this);
        Bukkit.getPluginManager().callEvent(bailliDesactivateEvent);
    }
    @EventHandler(priority = EventPriority.MONITOR)
    private void onDesactivate(@NonNull final BailliDesactivateEvent event) {
        this.bailliInfos.clear();
    }
    @EventHandler
    private void onDeath(@NonNull final FinalDeathEvent event) {
        if (!activate) return;
        final GamePlayer gamePlayer = event.getRole().getGamePlayer();
        final LocationInfo info = new LocationInfo(gamePlayer, gamePlayer.getDeathLocation());
        Main.getInstance().debug("Info of "+gamePlayer.getPlayerName()+" is "+ info);
        this.bailliInfos.add(info);
    }
    private static final class LocationInfo implements BailliInfo {

        private final GamePlayer gamePlayer;
        private final Location location;

        private LocationInfo(GamePlayer gamePlayer, Location location) {
            this.gamePlayer = gamePlayer;
            this.location = location;
        }

        @Nonnull
        @Override
        public BailliInfoType getType() {
            return BailliInfoType.LOCATION;
        }

        @Nonnull
        @Override
        public GamePlayer getGamePlayer() {
            return this.gamePlayer;
        }

        @Override
        public @NonNull Object getValue() {
            return this.location;
        }

        @Override
        public String toString() {
            return "InfoType="+this.getType()+", GamePlayer="+this.gamePlayer+", Location="+this.location;
        }
    }
}