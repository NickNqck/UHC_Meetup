package fr.nicknqck.roles.ds.slayers.pillier;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.ds.builders.Soufle;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ObanaiV2 extends PillierRoles{

    private TextComponent textComponent;

    public ObanaiV2(UUID player) {
        super(player);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.EAU;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Obanai";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.Obanai;
    }

    @Override
    public void resetCooldown() {

    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public TextComponent getComponent() {
        return this.textComponent;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        super.RoleGiven(gameState);
    }
}
