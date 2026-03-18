package fr.nicknqck.titans.impl;

import fr.nicknqck.enums.TitanForm;
import fr.nicknqck.events.custom.roles.aot.PrepareTitanStealEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.titans.TitanBase;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MachoireV2 extends TitanBase implements Listener {

    private final List<PotionEffect> effects;

    public MachoireV2(GamePlayer gamePlayer) {
        super(gamePlayer);
        this.effects = new ArrayList<>();
        this.effects.add(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false));
        gamePlayer.getRole().addPower(new JumpPower(gamePlayer.getRole(), this), true);
        EventUtils.registerRoleEvent(this);
    }

    @Override
    public @NonNull String getParticularites() {
        return "§8 -§7 Votre transformation à une durée de§c 3 minutes§7.\n"+
                " \n"+
                "§8 -§7 Lorsque vous êtes transformé en titan vous avez l'effet§c Speed I§7 ainsi que§a No Fall§7.\n"+
                " \n"+
                "§8 -§7 Vous pouvez utiliser votre item \"§aSaut§7\" pour effectuer un grand bon en avant.\n"+
                " \n"+
                "§8 -§7 Lorsque vous êtes transformé, vous passez à travers la résistance des gens que vous frappez§7.";
    }

    @Override
    public @NonNull String getName() {
        return "§9Machoire";
    }

    @Override
    public @NonNull Material getTransformationMaterial() {
        return Material.FEATHER;
    }

    @Override
    public @NonNull List<PotionEffect> getEffects() {
        return this.effects;
    }

    @Override
    public int getTransfoDuration() {
        return 60*3;
    }

    @Override
    public @NonNull TitanForm getTitanForm() {
        return TitanForm.MACHOIRE;
    }

    @EventHandler
    private void onDamage(@NonNull final EntityDamageEvent event) {
        if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            if (!isTransformed())return;
            event.setDamage(0.0);
            event.setCancelled(true);
        }
    }

    private static class JumpPower extends ItemPower {

        private final MachoireV2 machoire;

        protected JumpPower(@NonNull RoleBase role, MachoireV2 machoire) {
            super("Saut", new Cooldown(25), new ItemBuilder(Material.SLIME_BALL).setName("§aSaut").addEnchant(Enchantment.DAMAGE_ALL, 1).hideEnchantAttributes(), role,
                    "§7Les informations sur ce pouvoirs sont contenue dans votre§6 /aot titan");
            this.machoire = machoire;
            setShowInDesc(false);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (!this.machoire.isTransformed()) {
                    player.sendMessage("§cIl faut être transformé en titan pour utiliser ce pouvoir");
                    return false;
                }
                @NonNull final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                @NonNull final Vector direction = player.getLocation().getDirection();
                if (event.getAction().name().contains("LEFT")) {
                    direction.setY(0.8);
                    player.setVelocity(direction.multiply(1.35));
                } else {
                    direction.setY(0.5);
                    player.setVelocity(direction.multiply(1.85));
                }
                player.sendMessage("§aJump !");
                return true;
            }
            return false;
        }
    }
}
