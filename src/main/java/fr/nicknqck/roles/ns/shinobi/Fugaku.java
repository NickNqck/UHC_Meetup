package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.UchiwaRoles;
import fr.nicknqck.roles.ns.solo.Danzo;
import fr.nicknqck.utils.Loc;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Fugaku extends UchiwaRoles {
    private TextComponent desc;
    public Fugaku(UUID player) {
        super(player);
        givePotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 2, 0), EffectWhen.PERMANENT);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        setChakraType(Chakras.KATON);
        AutomaticDesc desc = new AutomaticDesc(this);
        desc.addEffects(getEffects())
                .addParticularites(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous possédez la nature de chakra "+Chakras.KATON.getShowedName())})
                , new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Toutes les§c 5 minutes§7, vous saurez si vous avez croisé un porteur de §cSharingan§7 à moins de §c10 blocs§7.")})
                );
        this.desc = desc.getText();
        new CroiserRunnable(this).runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
    }

    @Override
    public Intelligence getIntelligence() {
        return Intelligence.INTELLIGENT;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public String getName() {
        return "Fugaku";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Fugaku;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Shinobi;
    }

    @Override
    public void resetCooldown() {

    }

    @Override
    public @NonNull TextComponent getComponent() {
        return this.desc;
    }
    private static class CroiserRunnable extends BukkitRunnable {
        private final Fugaku fugaku;
        private int timeRemaining = 60*5;
        private boolean croised = false;

        public CroiserRunnable(Fugaku fugaku) {
            this.fugaku = fugaku;
        }

        @Override
        public void run() {
            if (!fugaku.getGameState().getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            Player owner = Bukkit.getPlayer(fugaku.getPlayer());
            if (owner != null) {
                timeRemaining--;
                if (!croised) {//donc je fais le code juste en dessous que s'il n'a croiser personne
                    for (Player p : Loc.getNearbyPlayersExcept(owner, 10)) {
                        if (fugaku.getGameState().hasRoleNull(p))continue;
                        RoleBase role = fugaku.getGameState().getPlayerRoles().get(p);
                        if (!role.getGamePlayer().isAlive())continue;
                        if (role instanceof UchiwaRoles || role instanceof Danzo) {
                            this.croised = true;
                        }
                    }
                }
                if (timeRemaining == 0) {
                    if (croised) {
                        owner.sendMessage("§7Vous avez croiser un porteur du§c Sharingan§7 durant ces§c 5 dernières minutes§7.");
                    } else {
                        owner.sendMessage("§7Vous n'avez croiser aucun porteur du §cSharingan§7 ces dernières§c 5 minutes§7.");
                    }
                    timeRemaining = 60*5;
                }
            }
        }
    }
}