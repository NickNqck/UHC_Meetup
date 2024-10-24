package fr.nicknqck.roles.ds.slayers.pillier;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.UUID;

public class MuichiroV2 extends PillierRoles {

    private TextComponent desc;

    public MuichiroV2(UUID player) {
        super(player);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.VENT;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Muichiro";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.Muichiro;
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
        addPower(new T4ItemPower(this), true);
        AutomaticDesc automaticDesc = new AutomaticDesc(this);
        this.desc = automaticDesc.getText();
    }

    @Override
    public TextComponent getComponent() {
        return this.desc;
    }
    private static class T4ItemPower extends ItemPower implements Listener {

        protected T4ItemPower(@NonNull RoleBase role) {
            super("§bSoufle de la Brume", new Cooldown(60), new ItemBuilder(Material.DIAMOND_SWORD).setName("§bLame de Muichiro").addEnchant(Enchantment.DAMAGE_ALL, 4), role, "§7Lorsque vous§c tapez§7 un joueur vous lui infligerez§c 5 secondes de§4 Blindness I");
            EventUtils.registerRoleEvent(this);
            setSendCooldown(false);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            return false;
        }
        @EventHandler
        private void onBattle(EntityDamageByEntityEvent event) {
            if (event.getDamager().getUniqueId().equals(getRole().getPlayer()) && event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
                if (!getCooldown().isInCooldown() && ((Player) event.getDamager()).getItemInHand().isSimilar(getItem())) {
                    ((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0, false, false));
                    getCooldown().use();
                }
            }
        }
    }
}
