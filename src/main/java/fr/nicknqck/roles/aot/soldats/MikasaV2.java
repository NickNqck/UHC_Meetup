package fr.nicknqck.roles.aot.soldats;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.EffectWhen;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.RoleGiveEvent;
import fr.nicknqck.events.custom.power.CooldownUpdateEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.aot.builders.Ackerman;
import fr.nicknqck.roles.aot.builders.AckermanPower;
import fr.nicknqck.roles.aot.builders.AotRoles;
import fr.nicknqck.roles.aot.builders.SoldatsRoles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class MikasaV2 extends SoldatsRoles implements Ackerman, Listener {

    private SoldatsRoles master = null;
    private boolean knowHisMaster = false;
    private final AckermanPower ackermanPower = new AckermanPower(this, this);

    public MikasaV2(UUID player) {
        super(player);
    }

    @Override
    public String getName() {
        return "Mikasa";
    }

    @Nullable
    @Override
    public SoldatsRoles getMaster() {
        return this.master;
    }

    @Override
    public boolean knowHisMaster() {
        return this.knowHisMaster;
    }

    @Override
    public void setKnowMaster(boolean b) {
        this.knowHisMaster = b;
    }

    @Override
    public AckermanPower getAckermanPower() {
        return this.ackermanPower;
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Mikasa;
    }

    @Nonnull
    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createAutomaticDesc(this)
                .getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        EventUtils.registerRoleEvent(this);
        addPower(new BoostPower(this), true);
    }
    @EventHandler(priority = EventPriority.HIGH)
    private void onGiveRole(@NonNull final RoleGiveEvent event) {
        if (!event.isEndGive())return;
        SoldatsRoles most = null;
        int mostPoints = 0;
        for (GamePlayer gamePlayer : new ArrayList<>(event.getGameState().getGamePlayer().values())) {
            if (!gamePlayer.check())continue;
            //Donc le joueur a un rôle & est en ligne & est en vie
            if (!(gamePlayer.getRole() instanceof SoldatsRoles))continue;
            final SoldatsRoles role = (SoldatsRoles) gamePlayer.getRole();
            if (role instanceof Ackerman)continue;
            int random = RandomUtils.getRandomInt(0, 100);
            if (role instanceof ArminV2) {
                random = random+10;
            }
            if (random > mostPoints) {
                most = role;
            }
        }
        if (most != null) {
            this.master = most;
            this.master.setAckerman(this);
            getGamePlayer().sendMessage("§7Un§a maitre§7 vous a été désigner, vous saurez qui sait en vous approchant de lui");
            addPower(this.ackermanPower);
        } else {
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                getGamePlayer().sendMessage("§cAucun maitre n'a pus vous être attribuer, en compensation vous avez reçus 2❤ permanents.");
                setMaxHealth(24.0);
                owner.setMaxHealth(getMaxHealth());
            }, 2);
        }
    }
    private static final class BoostPower extends ItemPower implements Listener{

        private boolean activate = false;

        public BoostPower(@NonNull RoleBase role) {
            super("§aBoost d'Ackerman§r", new Cooldown(60*5), new ItemBuilder(Material.SUGAR).setName("§aBoost d'Ackerman"), role,
                    "§7Vous permet d'obtenir pendant§c 2 minutes§7 l'effet§e Speed I§7.",
                    "",
                    "§7Tant que ce pouvoir est§a actif§7 vous avez§c Force I§7 contre les§c titans transformés§7."
            );
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*120, 0, false, false), EffectWhen.NOW);
                this.activate = true;
                return true;
            }
            return false;
        }
        @EventHandler(priority = EventPriority.HIGH)
        private void onTime(final CooldownUpdateEvent event) {
            if (!this.getCooldown().isInCooldown())return;
            if (!this.getCooldown().getUniqueId().equals(event.getCooldown().getUniqueId()))return;
            if (event.getCooldown().getCooldownRemaining() == (event.getCooldown().getOriginalCooldown()-120)) {
                this.activate = false;
                getRole().getGamePlayer().sendMessage("§7Vous n'êtes plus sous l'effet de votre§a Boost Ackerman§7.");
            }
        }
        @EventHandler(priority = EventPriority.HIGH)
        private void onDamage(final EntityDamageByEntityEvent event) {
            if (!(event.getEntity() instanceof Player))return;
            if (!(event.getDamager() instanceof Player))return;
            if (!event.getDamager().getUniqueId().equals(getRole().getPlayer()))return;
            final GamePlayer gamePlayer = GamePlayer.of(event.getEntity().getUniqueId());
            if (gamePlayer != null) {
                if (gamePlayer.getRole() != null) {
                    if (gamePlayer.getRole() instanceof AotRoles) {
                        if (((AotRoles) gamePlayer.getRole()).isTransformedinTitan && this.activate) {
                            int forcePercent = Main.getInstance().getGameConfig().getForcePercent()/100;
                            forcePercent = forcePercent +1;
                            event.setDamage(event.getDamage()*forcePercent);
                        }
                    }
                }
            }
        }
    }
}