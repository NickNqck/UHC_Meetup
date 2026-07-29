package fr.nicknqck.entity.bijuv2;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.GlobalUtils;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

@Getter
public class BijuPower extends ItemPower {

    private final BijuBase biju;

    public BijuPower(@NonNull BijuBase biju, @NonNull RoleBase role) {
        super(biju.getName(), new Cooldown(60*20),
                new ItemBuilder(biju.getItemMaterial())
                        .setName(biju.getName())
                        .addEnchant(Enchantment.ARROW_INFINITE, 1)
                        .hideEnchantAttributes(), role, biju.getItemDescription());
        this.biju = biju;
    }

    @Override
    public ItemStack getItem() {
        return GlobalUtils.setNBT(super.getItem(), "biju.power", "biju");
    }

    @Override
    public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
        if (getInteractType().equals(InteractType.INTERACT)) {
            for (@NonNull final PotionEffect potionEffect : this.biju.getEffectsWhenUse()) {
                if (!player.hasPotionEffect(potionEffect.getType())) {
                    player.addPotionEffect(potionEffect, true);
                } else {
                    for (PotionEffect po : player.getActivePotionEffects()) {
                        if (po.getType().equals(potionEffect.getType())) {
                            if (po.getAmplifier() <= potionEffect.getAmplifier()) {
                                player.addPotionEffect(potionEffect, true);
                            }
                            break;
                        }
                    }
                }
            }
            Bukkit.getScheduler().runTask(Main.getInstance(), () -> this.biju.onUse(player, getRole()));
            new BijuRunnable(getRole().getGameState(), getRole().getGamePlayer(), this);
            return true;
        }
        return false;
    }

    private static class BijuRunnable extends BukkitRunnable {

        private final GameState gameState;
        private final GamePlayer gamePlayer;
        private final BijuPower bijuPower;
        private int timeRemaining;

        private BijuRunnable(GameState gameState, GamePlayer gamePlayer, BijuPower bijuPower) {
            this.gameState = gameState;
            this.timeRemaining = 60*5;
            this.gamePlayer = gamePlayer;
            this.bijuPower = bijuPower;
            this.gamePlayer.getActionBarManager().addToActionBar("bijurunnable."+this.bijuPower.getBiju().getName(), "§bTemp restant sous l'effet de "+this.bijuPower.getBiju().getName()+"§b: §c5 minutes");
            runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
        }

        @Override
        public void run() {
            if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (this.timeRemaining <= 0) {
                this.gamePlayer.getActionBarManager().removeInActionBar("bijurunnable."+this.bijuPower.getBiju().getName());
                this.bijuPower.getBiju().onEnd(this.gamePlayer.getRole());
                this.gamePlayer.sendMessage("§7Vous n'êtes plus sous l'effet de "+this.bijuPower.getBiju().getName()+"§7.");
                cancel();
                return;
            }
            this.gamePlayer.getActionBarManager().updateActionBar(
                    "bijurunnable."+this.bijuPower.getBiju().getName(),
                    "§bTemp restant sous l'effet de "+this.bijuPower.getBiju().getName()+"§b: §c"+ StringUtils.secondsTowardsBeautiful(this.timeRemaining)
            );
            this.timeRemaining--;
        }
    }
}