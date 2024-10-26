package fr.nicknqck.roles.ds.slayers.pillier;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.ds.builders.Soufle;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class MitsuriV2 extends PillierRoles{

    private TextComponent desc;

    public MitsuriV2(UUID player) {
        super(player);
    }

    @Override
    public Soufle getSoufle() {
        return null;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public GameState.Roles getRoles() {
        return null;
    }

    @Override
    public void resetCooldown() {

    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public void RoleGiven(GameState gameState) {
        super.RoleGiven(gameState);
    }

    @Override
    public TextComponent getComponent() {
        return this.desc;
    }
}
