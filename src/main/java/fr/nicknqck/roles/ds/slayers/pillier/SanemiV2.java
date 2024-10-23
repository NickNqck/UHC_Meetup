package fr.nicknqck.roles.ds.slayers.pillier;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.builders.Lames;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;

public class SanemiV2 extends PillierRoles {

    private TextComponent textComponent;

    public SanemiV2(UUID player) {
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
        return "Sanemi";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.Sanemi;
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
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false), EffectWhen.PERMANENT);
        setNoFall(true);
        addPower(new VentPower(this), true);
        AutomaticDesc desc = new AutomaticDesc(this)
                .addEffects(getEffects())
                .addCustomLine("§7Vous possédez§a No Fall§7 de manière§c permanente")
                .setPowers(getPowers());
        this.textComponent = desc.getText();
        setCanuseblade(true);
        getCantHave().add(Lames.NoFall);
    }

    @Override
    public TextComponent getComponent() {
        return this.textComponent;
    }

    private static class VentPower extends ItemPower {

        protected VentPower(RoleBase role) {
            super("§aSoufle du Vent", new Cooldown(60*7+150), new ItemBuilder(Material.FEATHER).setName("§aSoufle du Vent"), role, "§7Effectue un§c dash§7 dans la direction ou vous regardez, également, vous donne l'effet§b Speed II§7 pendant§c "+ StringUtils.secondsTowardsBeautiful(150)+"§7.");
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                Vector direction = player.getLocation().getDirection();
                direction.setY(0.5);
                player.setVelocity(direction.multiply(1.8));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*150, 1, false, false), true);
                return true;
            }
            return false;
        }
    }
}
