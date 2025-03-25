package fr.nicknqck.titans;

import fr.nicknqck.GameState;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class TitanBase implements ITitan {

    private final List<UUID> stealers;
    private boolean transformed = false;
    private final GamePlayer gamePlayer;
    private final TransformationPower transformationPower;

    protected TitanBase(final GamePlayer gamePlayer) {
        this.stealers = new ArrayList<>();
        this.gamePlayer = gamePlayer;
        this.transformationPower = new TransformationPower(this);
        this.gamePlayer.getRole().addPower(transformationPower, true);
    }

    @Override
    public @NonNull List<UUID> getStealers() {
        return this.stealers;
    }

    @Override
    public boolean isTransformed() {
        return transformed;
    }

    @Override
    public void setTransformed(final boolean transformed) {
        this.transformed = transformed;
    }

    @Override
    public @NonNull GamePlayer getGamePlayer() {
        return this.gamePlayer;
    }

    public static class TransformationPower extends ItemPower {

        private final TitanBase titanBase;

        protected TransformationPower(@NonNull TitanBase titan) {
            super("§fTransformation", new Cooldown(titan.getTransfoDuration()*2), new ItemBuilder(titan.getTransformationMaterial()).setName("§fTransformation"), titan.getGamePlayer().getRole());
            this.titanBase = titan;
            setWorkWhenInCooldown(true);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (this.getCooldown().isInCooldown()) {
                    if (this.getCooldown().getCooldownRemaining() <= this.titanBase.getTransfoDuration()) {
                        player.sendMessage("§7Vous êtes en cooldown pour la transformation, il vous reste§c "+StringUtils.secondsTowardsBeautiful(getCooldown().getCooldownRemaining()));
                        return false;
                    }
                }
                if (!this.titanBase.isTransformed()) {
                    this.startTransformation();
                    player.getWorld().strikeLightningEffect(player.getLocation());
                    return true;
                } else {
                    if (this.getCooldown().isInCooldown()) {
                        if (this.getCooldown().getCooldownRemaining() <= this.getCooldown().getOriginalCooldown()-5) {
                            this.stopTransformation();
                            return false;
                        }
                    }
                }
            }
            return false;
        }
        private synchronized void startTransformation() {
            this.titanBase.setTransformed(true);
            Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(new String[]{
                    "",
                    "§6Un humain c'est transformé en titan !",
                    ""
            }));
            new TransformationRunnable(this).runTaskTimerAsynchronously(getPlugin(), 0, 20);
        }
        private synchronized void stopTransformation() {
            this.titanBase.setTransformed(false);
            Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(new String[]{
                    "",
                    "§6Un titan est redevenu humain !",
                    ""
            }));
            getCooldown().setActualCooldown(this.titanBase.getTransfoDuration());
        }
        private static class TransformationRunnable extends BukkitRunnable {

            private final TransformationPower power;
            private int timeLeft;

            protected TransformationRunnable(@NonNull TransformationPower power) {
                this.power = power;
                this.timeLeft = power.titanBase.getTransfoDuration();
                power.titanBase.getGamePlayer().getActionBarManager().addToActionBar(
                        "titanbase."+power.titanBase.getName(),
                        "§bTemp de transformation: "+ StringUtils.secondsTowardsBeautiful(this.timeLeft)
                );
            }
            @Override
            public void run() {
                if (!this.power.getRole().getGameState().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (!this.power.titanBase.getGamePlayer().isAlive()) {
                    this.power.stopTransformation();
                    cancel();
                    return;
                }
                if (!this.power.titanBase.isTransformed()) {
                    power.titanBase.getGamePlayer().getActionBarManager().removeInActionBar("titanbase."+power.titanBase.getName());
                    cancel();
                    return;
                }
                this.timeLeft--;
                power.titanBase.getGamePlayer().getActionBarManager().updateActionBar(
                        "titanbase."+power.titanBase.getName(),
                        "§bTemp de transformation: "+ StringUtils.secondsTowardsBeautiful(this.timeLeft)
                );
                if (this.timeLeft > 1) {
                    Bukkit.getScheduler().runTask(this.power.getPlugin(), () -> {
                        if (!this.power.titanBase.getEffects().isEmpty()) {
                            for (final PotionEffect potionEffect : this.power.titanBase.getEffects()) {
                                this.power.getRole().givePotionEffect(potionEffect, EffectWhen.NOW);
                            }
                        }
                    });
                }
                if (this.power.getCooldown().getCooldownRemaining() <= this.power.titanBase.getTransfoDuration()) {
                    power.titanBase.getGamePlayer().getActionBarManager().removeInActionBar("titanbase."+power.titanBase.getName());
                    this.power.stopTransformation();
                    cancel();
                }
            }
        }
    }
}