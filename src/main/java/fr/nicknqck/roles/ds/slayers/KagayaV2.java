package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.packets.ArmorStandUtils;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KagayaV2 extends SlayerRoles {

    private TextComponent textComponent;

    public KagayaV2(UUID player) {
        super(player);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.AUCUN;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Kagaya";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.Kagaya;
    }

    @Override
    public void resetCooldown() {}

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public TextComponent getComponent() {
        return textComponent;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 0), EffectWhen.PERMANENT);
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> addPower(new ShowHealthPower(this)), 20);
        AutomaticDesc desc = new AutomaticDesc(this).addEffects(getEffects()).setPowers(getPowers());
        this.textComponent = desc.getText();
    }
    private static class ShowHealthPower extends Power implements Listener {

        private final Map<UUID, ArmorStandUtils> armorStands = new HashMap<>();

        public ShowHealthPower(@NonNull RoleBase role) {
            super("§avoir la vie des joueurs", null, role);
            EventUtils.registerRoleEvent(this);
            for (final UUID uuid : role.gameState.getGamePlayer().keySet()) {
                if (uuid.equals(role.getPlayer()))continue;
                ArmorStandUtils armorStand = new ArmorStandUtils(role.gameState.getGamePlayer().get(uuid).getLastLocation(), "TEST");
                armorStand.display(role.owner);
                this.armorStands.put(uuid, armorStand);
            }
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            return false;
        }
        @EventHandler
        private void PlayerMooveEvent(PlayerMoveEvent event) {
            if (armorStands.containsKey(event.getPlayer().getUniqueId())) {
                Player owner = Bukkit.getPlayer(getRole().getPlayer());
                if (owner == null)return;
                armorStands.get(event.getPlayer().getUniqueId()).rename("§c"+new DecimalFormat("0").format(event.getPlayer().getHealth())+"❤", owner);
                final Location to = new Location(event.getTo().getWorld(), event.getTo().getX(), event.getTo().getY()+0.5, event.getTo().getZ());
                armorStands.get(event.getPlayer().getUniqueId()).teleport(to, owner);
            }
        }
    }
}
