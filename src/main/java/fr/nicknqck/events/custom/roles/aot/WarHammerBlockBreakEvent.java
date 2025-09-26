package fr.nicknqck.events.custom.roles.aot;

import fr.nicknqck.titans.impl.WarhammerV2;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

@Getter
public class WarHammerBlockBreakEvent extends BlockBreakEvent {

    private final WarhammerV2 warhammer;

    public WarHammerBlockBreakEvent(Block theBlock, Player player, WarhammerV2 warhammer) {
        super(theBlock, player);
        this.warhammer = warhammer;
    }
}