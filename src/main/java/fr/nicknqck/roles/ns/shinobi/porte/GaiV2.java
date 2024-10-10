package fr.nicknqck.roles.ns.shinobi.porte;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.builder.AutomaticDesc;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class GaiV2 extends PortesRoles{

    private TextComponent textComponent;

    public GaiV2(UUID player) {
        super(player);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        super.RoleGiven(gameState);
        AutomaticDesc automaticDesc = new AutomaticDesc(this).setItems(troisPorteMap(), sixPorteMap(), huitPorteMap());
        this.textComponent = automaticDesc.getText();
    }

    @Override
    public String getName() {
        return "Gai Maito";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.Gai;
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public TextComponent getComponent() {
        return textComponent;
    }
}
