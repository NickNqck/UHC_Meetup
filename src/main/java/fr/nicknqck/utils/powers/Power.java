package fr.nicknqck.utils.powers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.roles.PowerActivateEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.Map;

@Getter
@Setter
public abstract class Power {

    protected final Main plugin = Main.getInstance();
    private final String name;
    private Cooldown cooldown;
    private int maxUse = -1;
    private int use;
    private boolean cooldownResetSended = true;
    private final RoleBase role;

    public Power(@NonNull String name,@NonNull Cooldown cooldown,@NonNull RoleBase role, String... descriptions) {
        this.name = name;
        this.cooldown = cooldown;
        this.use = 0;
        this.role = role;
    }

    public boolean checkUse(Player player, Map<String, Object> args) {
        GamePlayer gamePlayer = GameState.getInstance().getGamePlayer().get(player.getUniqueId());

        if (gamePlayer.getRole() == null) {
            player.sendMessage("§cVous n'avez pas de rôle.");
            return false;
        }

        RoleBase role = gamePlayer.getRole();

        if (!role.getPowers().contains(this)) {
            player.sendMessage("§cVous ne possédez pas ce pouvoir.");
            return false;
        }

        int maxUse = this.getMaxUse();
        int use = this.getUse();

        if (use >= maxUse && maxUse != -1) {
            player.sendMessage("§cVous avez atteint le nombre maximum d'utilisation pour ce pouvoir, à savoir "+getMaxUse()+" fois.");
            return false;
        }

        PowerActivateEvent powerActivateEvent = new PowerActivateEvent(this.plugin, player, this);
        this.plugin.getServer().getPluginManager().callEvent(powerActivateEvent);
        if (powerActivateEvent.isCancelled()) {
            if (powerActivateEvent.getCancelMessage() != null) {
                player.sendMessage(powerActivateEvent.getCancelMessage());
            } else {
                player.sendMessage("§cDésolé, pouvoir inutilisable.");
                return false;
            }
        }

        Cooldown powerCooldown = this.getCooldown();
        if (powerCooldown != null && powerCooldown.isInCooldown()) {
            role.sendCooldown(player, getCooldown().getCooldownRemaining());
            return false;
        }

        boolean canUse = this.onUse(player, args);
        if (canUse) {
            this.setUse(this.getUse() + 1);
            if (powerCooldown != null) {
                powerCooldown.use();
                this.setCooldownResetSended(false);
            }
        }
        return canUse;
    }
    public abstract boolean onUse(Player player, Map<String, Object> args);

    public void onEndCooldown(Cooldown cooldown) {
        if (cooldown.equals(this.getCooldown())) {//donc si c'est EXACTEMENT le même "Cooldown"
            getRole().owner.sendMessage("§7Vous pouvez à nouveau utiliser le pouvoir \""+this.getName()+"§7\".");
        }
    }
}