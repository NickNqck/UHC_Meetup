package fr.nicknqck.roles.ds.solos.jigorov2;

import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.enums.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.demons.lune.KaigakuV2;
import fr.nicknqck.roles.ds.slayers.ZenItsuV2;
import fr.nicknqck.utils.event.EventUtils;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class JigoroV2PSolo extends JigoroV2 implements Listener {

    private boolean killZen = false;
    private boolean killKai = false;

    public JigoroV2PSolo(final UUID player, final GamePlayer gamePlayer) {
        super(player);
        gamePlayer.setRole(this);
        setGamePlayer(gamePlayer);
        addKnowedRole(ZenItsuV2.class);
        addKnowedRole(KaigakuV2.class);
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0, false, false), EffectWhen.PERMANENT);
        EventUtils.registerRoleEvent(this);
    }

    @Override
    public String[] Desc() {
        return AllDesc.JigoroV2Pacte1;
    }

    @EventHandler
    private void onKill(@NonNull final UHCDeathEvent event) {
        if (event.getGamePlayerKiller() == null)return;
        if (event.getGamePlayerKiller().getRole() == null)return;
        if (!event.getGamePlayerKiller().getUuid().equals(getPlayer()))return;
        if (event.getGameState().hasRoleNull(event.getPlayer().getUniqueId()))return;
        final RoleBase role = event.getGameState().getGamePlayer().get(event.getPlayer().getUniqueId()).getRole();
        if (role instanceof ZenItsuV2) {
            if (killZen)return;
            this.killZen = true;
            addSpeedAtInt(event.getGamePlayerKiller().getPlayer(), 10);
            givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 80, 0, false, false), EffectWhen.DAY);
            event.getGamePlayerKiller().sendMessage("Vous venez de tuez§a Zen'Itsu§f vous obtenez donc§9 résistance 1§f le§e jour§f, ainsi que§c 10%§f de §bSpeed");
        }
        if (role instanceof KaigakuV2) {
            if (killKai)return;
            killKai = true;
            addSpeedAtInt(event.getGamePlayerKiller().getPlayer(), 10);
            givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 80, 0, false, false), EffectWhen.NIGHT);
            event.getGamePlayerKiller().sendMessage("Vous venez de tuez§c Kaigaku§f vous obtenez donc§9 résistance 1§f la§c nuit§f, ainsi que§c 10%§f de§b Speed");

        }
    }
}
