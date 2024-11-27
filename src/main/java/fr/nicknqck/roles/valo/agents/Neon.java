package fr.nicknqck.roles.valo.agents;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class Neon extends RoleBase {
    public Neon(UUID player) {
        super(player);
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Neon";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Neon;
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Solo;
    }

    @Override
    public void resetCooldown() {

    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .setPowers(getPowers())
                .getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new SpeedItemPower(this), true);
    }

    private static class SpeedItemPower extends ItemPower {

        private final SpeedRunnable runnable;

        protected SpeedItemPower(@NonNull RoleBase role) {
            super("Vitesse Supérieure", null, new ItemBuilder(Material.NETHER_STAR).setName("§bVitesse Supérieure"), role,
                    "");
            this.runnable = new SpeedRunnable(getRole().getGamePlayer());
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                if (event.getAction().name().contains("RIGHT")) {
                    this.runnable.start = !this.runnable.start;
                    if (!runnable.start) {
                        player.removePotionEffect(PotionEffectType.SPEED);
                    }
                }
            }
            return false;
        }
        private static final class SpeedRunnable extends BukkitRunnable {

            private final GameState gameState;
            private double speedBar = 100.0;
            private final GamePlayer gamePlayer;
            private boolean start = false;

            private SpeedRunnable(GamePlayer gamePlayer) {
                this.gamePlayer = gamePlayer;
                this.gameState = GameState.getInstance();
                this.gamePlayer.getActionBarManager().addToActionBar("valo.agents.neon.speedbar", "bar "+speedBar);
                runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                this.gamePlayer.getActionBarManager().updateActionBar("valo.agents.neon.speedbar", "§bCharge: §7["+getCharge()+"§7 (§b"+this.speedBar+"§7)]");
                if (!this.start) {
                    this.speedBar = Math.min(100.0, this.speedBar+1.0);
                    return;
                }
                final Player owner = Bukkit.getPlayer(gamePlayer.getUuid());
                if (owner != null) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 1, false, false), true);
                    });
                    this.speedBar-=1;
                }
            }
            private String getCharge() {
                double maxBar = 100.0;
                double bar = this.speedBar;
                StringBuilder sbar = new StringBuilder();
                for (double i = 0; i < bar; i++) {
                    sbar.append("§a|");
                }
                for (double i = bar; i < maxBar; i++) {
                    sbar.append("§c|");
                }
                return sbar.toString();
            }
        }
    }
}
