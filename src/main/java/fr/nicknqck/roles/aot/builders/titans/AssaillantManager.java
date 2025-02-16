package fr.nicknqck.roles.aot.builders.titans;

import fr.nicknqck.GameState;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.events.custom.roles.aot.PrepareStealCommandEvent;
import fr.nicknqck.events.custom.roles.aot.PrepareTitanStealEvent;
import fr.nicknqck.events.custom.roles.aot.TitanStealEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.aot.builders.AotRoles;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AssaillantManager {
    @Getter
    private static AssaillantManager instance;
    @Getter
    private final List<RoleBase> titans;

    public AssaillantManager() {
        instance = this;
        this.titans = new ArrayList<>();
    }
    public static void addAssaillant(@NonNull final RoleBase role) {
        getInstance().getTitans().add(role);
        role.addPower(new TransformationPower(role), true);
    }
    public static void removeAssaillant(@NonNull final RoleBase role) {
        getInstance().getTitans().remove(role);
    }
    private static class TransformationPower extends ItemPower implements Listener {

        private boolean transformer = false;
        private final List<GamePlayer> canSteal;

        protected TransformationPower(@NonNull RoleBase role) {
            super("Transformation", new Cooldown(60*8), new ItemBuilder(Material.FEATHER).setName("§6Transformation"), role);
            this.canSteal = new ArrayList<>();
            setWorkWhenInCooldown(true);
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> map) {
            if (!transformer) {
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60*4, 0, false, false), EffectWhen.NOW);
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*60*4, 0, false, false), EffectWhen.NOW);
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*4, 0, false, false), EffectWhen.NOW);
                TransfoMessage(player, true);
                player.sendMessage("§7Transformation en§6 Titan Assaillant");
                getRole().addBonusforce(10.0);
                getRole().addBonusResi(10.0);
                this.transformer = true;
            } else {
                player.removePotionEffect(PotionEffectType.SPEED);
                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                TransfoMessage(player, false);
                player.sendMessage("§7Transformation en§6 humain");
                getRole().addBonusforce(-10.0);
                getRole().addBonusResi(-10.0);
                this.transformer = false;
            }
            return true;
        }
        @EventHandler
        private void OnDeath(@NonNull final UHCDeathEvent event) {
            if (event.isCancelled())return;
            if (!event.getPlayer().getUniqueId().equals(getRole().getPlayer()))return;
            final List<GamePlayer> canSteals = new ArrayList<>();
            for (@NonNull final GamePlayer gamePlayer : Loc.getNearbyGamePlayers(event.getPlayer().getLocation(), 30.0)) {
                if (gamePlayer.getRole() == null)continue;
                if (gamePlayer.getRole() instanceof AotRoles) {
                    if (!((AotRoles) gamePlayer.getRole()).isCanVoleTitan())continue;
                    canSteals.add(gamePlayer);
                }
            }
            final PrepareTitanStealEvent stealEvent = new PrepareTitanStealEvent(PrepareTitanStealEvent.TitanForm.ASSAILLANT, event.getGameState(), canSteals, getRole().getGamePlayer());
            Bukkit.getPluginManager().callEvent(stealEvent);
            if (!stealEvent.isCancelled()) {
                this.canSteal.addAll(stealEvent.getCanSteal());
            }
            AssaillantManager.removeAssaillant(getRole());
        }
        @EventHandler
        private void prepareStealEvent(@NonNull final PrepareStealCommandEvent event) {
            if (event.getRole().getGamePlayer() == null)return;
            if (this.canSteal.contains(event.getRole().getGamePlayer())) {
                if (!event.getRole().canShift) {
                    final TitanStealEvent stealEvent = new TitanStealEvent(event.getRole(), event.getRole().getGamePlayer(), event.getPlayer());
                    Bukkit.getPluginManager().callEvent(stealEvent);
                    if (!stealEvent.isCancelled()) {
                        stealEvent.getRole().canShift = true;
                        this.canSteal.clear();
                        AssaillantManager.addAssaillant(stealEvent.getRole());
                    }
                }
            }
        }
        private void TransfoMessage(Player player, boolean eclair) {
            if (eclair) {
                GameState.getInstance().spawnLightningBolt(player.getWorld(), player.getLocation());
                for (UUID u : getRole().getGameState().getInGamePlayers()) {
                    Player p = Bukkit.getPlayer(u);
                    if (p == null)continue;
                    p.playSound(p.getLocation(), "aotmtp.transfo", 8, 1);
                }
            }
            for (UUID u : GameState.getInstance().getInGamePlayers()) {
                Player p = Bukkit.getPlayer(u);
                if (p == null)continue;
                p.sendMessage("\n§6§lUn Titan c'est transformé !");p.sendMessage("");
            }
        }
    }
}