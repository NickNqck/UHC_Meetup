package fr.nicknqck.titans;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.roles.aot.TitanTransformEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public abstract class TitanBase implements ITitan {

    private final List<GamePlayer> stealers;
    private boolean transformed = false;
    private GamePlayer gamePlayer;
    @Getter
    private final TransformationPower transformationPower;

    protected TitanBase(final GamePlayer gamePlayer) {
        this.stealers = new ArrayList<>();
        this.gamePlayer = gamePlayer;
        this.transformationPower = new TransformationPower(this);
        this.gamePlayer.getRole().addPower(transformationPower, true);
    }

    @Override
    public @NonNull List<GamePlayer> getStealers() {
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

    @Override
    public @NonNull String[] getDescription() {
        return new String[] {
                "§8§o§m-----------------------------------",
                "§7Vous possédez le titan§c "+getName(),
                "",
                "§8 • §7Particularités:",
                "",
                getParticularites(),
                "",
                "§8§o§m-----------------------------------"
        };
    }
    public abstract @NonNull String getParticularites();

    public void setNewOwner(@NonNull GamePlayer gamePlayer) {
        Main.getInstance().getTitanManager().replaceOwner(this.getGamePlayer().getUuid(), gamePlayer, this);
        this.gamePlayer = gamePlayer;
    }

    public static class TransformationPower extends ItemPower {

        private final TitanBase titanBase;

        public TransformationPower(@NonNull TitanBase titan) {
            super("§fTransformation", new Cooldown(titan.getTransfoDuration()*2), new ItemBuilder(titan.getTransformationMaterial()).addEnchant(Enchantment.DAMAGE_ALL, 1).hideEnchantAttributes().setName("§fTransformation"), titan.getGamePlayer().getRole(),
                    "§7Cette objet vous permet de vous transformez en Titan "+titan.getName()+"§7, plus de détail sur ce dernier dans le§6 /aot info");
            this.titanBase = titan;
            setWorkWhenInCooldown(true);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (this.getCooldown().isInCooldown()) {
                    if (this.getCooldown().getCooldownRemaining() <= this.titanBase.getTransfoDuration()) {
                        player.sendMessage("§7Vous êtes en cooldown pour la transformation, il vous reste§c "+StringUtils.secondsTowardsBeautiful(getCooldown().getCooldownRemaining()));
                        return false;
                    }
                }
                if (!this.titanBase.isTransformed()) {
                    this.startTransformation(player);
                    player.getWorld().strikeLightningEffect(player.getLocation());
                    return true;
                } else {
                    if (this.getCooldown().isInCooldown()) {
                        if (this.getCooldown().getCooldownRemaining() <= this.getCooldown().getOriginalCooldown()-5) {
                            this.stopTransformation(player);
                            return false;
                        }
                    }
                }
            }
            return false;
        }
        public synchronized void startTransformation(@NonNull final Player player) {
            this.titanBase.setTransformed(true);
            @NonNull final TitanTransformEvent event = new TitanTransformEvent(this.titanBase, true, player);
            Bukkit.getPluginManager().callEvent(event);
            for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.sendMessage(new String[]{
                        "",
                        "§6Un humain c'est transformé en titan !",
                        ""
                });
                onlinePlayer.playSound(onlinePlayer.getLocation(), "aotmtp.transfo", 8, 1);
            }
            new TransformationRunnable(this).runTaskTimerAsynchronously(getPlugin(), 0, 20);
        }
        public synchronized void stopTransformation(@NonNull final Player p) {
            this.titanBase.setTransformed(false);
            @NonNull final TitanTransformEvent event = new TitanTransformEvent(this.titanBase, false, p);
            Bukkit.getPluginManager().callEvent(event);
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
                    this.power.stopTransformation(Bukkit.getPlayer(this.power.titanBase.getGamePlayer().getUuid()));
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
                    this.power.stopTransformation(Bukkit.getPlayer(this.power.titanBase.getGamePlayer().getUuid()));
                    cancel();
                }
            }
        }
    }
}