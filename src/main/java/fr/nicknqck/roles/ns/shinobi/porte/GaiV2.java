package fr.nicknqck.roles.ns.shinobi.porte;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.builder.AutomaticDesc;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
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
        setChakraType(getRandomChakras());
        AutomaticDesc automaticDesc = new AutomaticDesc(this).setItems(troisPorteMap(), sixPorteMap(), huitPorteMap()).addParticularites(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous possédez une nature de chakra aléatoire, cette partie vous avez la nature de chakra: "+getChakras().getShowedName())}));
        this.textComponent = automaticDesc.getText();
        addPower(new TroisPortePower(this), true);
        addPower(new SixPortesPower(this), true);
        addPower(new HuitPortesPower(this), true);
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
