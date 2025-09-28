package fr.nicknqck.roles.ds.slayers.pillier;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.builders.Lames;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;

public class SanemiV2 extends PilierRoles {

    public SanemiV2(UUID player) {
        super(player);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.VENT;
    }

    @Override
    public String getName() {
        return "Sanemi";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Sanemi;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false), EffectWhen.PERMANENT);
        setNoFall(true);
        addPower(new VentPower(this), true);
        setCanuseblade(true);
        getCantHave().add(Lames.NoFall);
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

    private static class VentPower extends ItemPower {

        protected VentPower(RoleBase role) {
            super("§aSouffle du Vent", new Cooldown(60*7+150), new ItemBuilder(Material.FEATHER).setName("§aSouffle du Vent"), role, "§7Effectue un§c dash§7 dans la direction ou vous regardez, également, vous donne l'effet§b Speed II§7 pendant§c "+ StringUtils.secondsTowardsBeautiful(150)+"§7.");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> args) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final Vector vector = player.getEyeLocation().getDirection();
                vector.multiply(3);
                player.setVelocity(vector);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*150, 1, false, false), true);
                return true;
            }
            return false;
        }
    }
}
