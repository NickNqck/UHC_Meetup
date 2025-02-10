package fr.nicknqck.roles.ds.solos.jigorov2;

import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.demons.lune.Kaigaku;
import fr.nicknqck.roles.ds.slayers.ZenItsuV2;
import fr.nicknqck.utils.event.EventUtils;
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
        addKnowedRole(Kaigaku.class);
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0, false, false), EffectWhen.PERMANENT);
        EventUtils.registerRoleEvent(this);
    }

    @Override
    public String[] Desc() {
        return AllDesc.JigoroV2Pacte1;
    }

    @EventHandler
    private void onKill(final UHCPlayerKillEvent event) {
        if (event.getGamePlayerKiller().getRole() == null)return;
        if (!event.getGamePlayerKiller().getUuid().equals(getPlayer()))return;
        if (event.getGameState().hasRoleNull(event.getVictim().getUniqueId()))return;
        final RoleBase role = event.getGameState().getGamePlayer().get(event.getVictim().getUniqueId()).getRole();
        if (role instanceof ZenItsuV2) {
            if (killZen)return;
            this.killZen = true;
            addSpeedAtInt(event.getPlayerKiller(), 10);
            givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 80, 0, false, false), EffectWhen.DAY);
            event.getKiller().sendMessage("Vous venez de tuez§a Zen'Itsu§f vous obtenez donc§9 résistance 1§f le§e jour§f, ainsi que§c 10%§f de §bSpeed");
        }
        if (role instanceof Kaigaku) {
            if (killKai)return;
            killKai = true;
            addSpeedAtInt(event.getPlayerKiller(), 10);
            givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 80, 0, false, false), EffectWhen.NIGHT);
            event.getKiller().sendMessage("Vous venez de tuez§c Kaigaku§f vous obtenez donc§9 résistance 1§f la§c nuit§f, ainsi que§c 10%§f de§b Speed");

        }
    }
}
