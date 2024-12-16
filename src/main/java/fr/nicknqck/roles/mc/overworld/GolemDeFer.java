package fr.nicknqck.roles.mc.overworld;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.mc.builders.OverWorldRoles;
import fr.nicknqck.roles.ns.Chakras;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class GolemDeFer extends OverWorldRoles {

    private final TextComponent automaticDesc;

    public GolemDeFer(UUID player) {
        super(player);
        AutomaticDesc desc = new AutomaticDesc(this);
        desc.addEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20, 0, false, false), EffectWhen.PERMANENT)
                .addParticularites(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous possédez §aNoFall§7.")}), new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous possédez §c2"+AllDesc.coeur+" §7permanent.")}));
        this.automaticDesc = desc.getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        getEffects().put(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20,0,false,false), EffectWhen.PERMANENT);
        setMaxHealth(24.0);
        owner.setMaxHealth(getMaxHealth());
        owner.setHealth(owner.getMaxHealth());
        setNoFall(true);
        super.RoleGiven(gameState);
    }
    @Override
    public TextComponent getComponent(){
        return automaticDesc;
    }
    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public String getName() {
        return "Golem de fer";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.GolemDeFer;
    }

    @Override
    public void resetCooldown() {

    }
}
