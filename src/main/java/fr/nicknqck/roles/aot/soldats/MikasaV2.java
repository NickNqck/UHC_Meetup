package fr.nicknqck.roles.aot.soldats;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.RoleGiveEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.aot.builders.Ackerman;
import fr.nicknqck.roles.aot.builders.AckermanPower;
import fr.nicknqck.roles.aot.builders.AotRoles;
import fr.nicknqck.roles.aot.builders.SoldatsRoles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.particles.MathUtil;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
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

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createAutomaticDesc(this)
                .addCustomLine("§7Vous avez une§c force 0,25§7 contre tout les autres joueurs")
                .addCustomLine("§7Vous avez une§9 résistance 1,5§7 contre les autres joueurs§a transformé§7 en§c titan")
                .addCustomLine("§7Vous avez une§9 résistance 0,75§7 contre les autres joueurs§c non-transformé§7 en§c titan")
                .getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        super.RoleGiven(gameState);
        EventUtils.registerRoleEvent(this);
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
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onDamageAttackEditor(@NonNull final EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) return;
        final Player damager = (Player) event.getDamager();
        final Player victim = (Player) event.getEntity();
        if (!damager.getUniqueId().equals(getPlayer()))return;
        if (getMaster() != null) {
            if (getMaster().getPlayer().equals(victim.getUniqueId())) {
                event.setDamage(1.0);
                return;
            }
        }
        final double forcePercent = Main.getInstance().getGameConfig().getForcePercent();
        final double toApply = 1 + ((forcePercent/4)/100);
        event.setDamage(event.getDamage()*toApply);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onDamageDefenseEditor(@NonNull final EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player))return;
        if (!event.getEntity().getUniqueId().equals(getPlayer()))return;
        boolean titan = false;
        if (Main.getInstance().getTitanManager().hasTitan(event.getDamager().getUniqueId())) {
            if (Main.getInstance().getTitanManager().getTitan(event.getDamager().getUniqueId()).isTransformed()) {
                titan = true;
            }
        }
        GamePlayer gamePlayer = GamePlayer.of(event.getDamager().getUniqueId());
        if (gamePlayer != null) {
            if (gamePlayer.getRole() != null) {
                if (gamePlayer.getRole() instanceof AotRoles) {
                    if (((AotRoles) gamePlayer.getRole()).isTransformedinTitan) {
                        titan = true;
                    }
                }
            }
        }
        final double resiPercent = (double) Main.getInstance().getGameConfig().getResiPercent() /100;
        if (!titan) {
            event.setDamage(event.getDamage()*(1-MathUtil.get34(resiPercent)));
        } else {
            event.setDamage(event.getDamage()*(1+(resiPercent*1.5)));
        }
    }
}