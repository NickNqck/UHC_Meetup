package fr.nicknqck.roles.mc.nether;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.mc.builders.NetherRoles;
import fr.nicknqck.utils.TripleMap;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class Brute extends NetherRoles {

    private final ItemStack GoldAxe = new ItemBuilder(Material.GOLD_AXE).setName("§eHache en or").addEnchant(Enchantment.DAMAGE_ALL, 7).setUnbreakable(true).setLore("").toItemStack();
    private final TextComponent automaticDesc;

    public Brute(UUID player) {
        super(player);
        giveItem(owner,false,getItems());
        AutomaticDesc desc = new AutomaticDesc(this);
        desc.addEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,20,0,false,false), EffectWhen.PERMANENT)
                .setItems(new TripleMap<>(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Une §eHache en or §7Tranchant 7")}),"§eHache en or", 0));
        this.automaticDesc = desc.getText();
    }

    public TextComponent getComponent(){
        return automaticDesc;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        getEffects().put(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20,0,false,false), EffectWhen.PERMANENT);
        super.RoleGiven(gameState);
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
                GoldAxe
        };
    }

    @Override
    public String getName() {
        return "Brute";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.Brute;
    }

    @Override
    public void resetCooldown() {

    }
}
