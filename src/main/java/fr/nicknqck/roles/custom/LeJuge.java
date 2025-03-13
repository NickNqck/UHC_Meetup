package fr.nicknqck.roles.custom;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class LeJuge extends CustomRolesBase implements Listener {
    private TextComponent automaticDesc;
    private int timeKill;
    private KillRunnable killRunnable;
    private boolean minage;

    public LeJuge(UUID player) {
        super(player);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        this.minage = Main.getInstance().getGameConfig().isMinage();
        if (minage) {
            timeKill = 60*10;
        } else {
            timeKill = 60*5;
        }
        getEffects().put(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        AutomaticDesc desc = new AutomaticDesc(this);
        desc.addEffects(getEffects());
        desc.addParticularites(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Toute les§c "+ StringUtils.secondsTowardsBeautiful(timeKill)+"§7 vous recevez une cible, si vous arrivez à la tuer vous obtiendrez un§c pourcentage§7 d'un effet aléatoire parmis:§b Speed§7,§c Force§7 et§9 Résistance§7,\n§7Sinon, si vous n'y arriver pas vous perdrez§c "+(minage ? "1/2" : "1")+ AllDesc.coeur+"§7 de manière§c permanente§7.")}));
        this.automaticDesc = desc.getText();
        this.killRunnable = new KillRunnable(this);
        killRunnable.runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), this.killRunnable::chooseTarget, minage ? 20*60*10:  20*100);
    }

    @Override
    public String getName() {
        return "Le Juge";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.LeJuge;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Solo;
    }

    @Override
    public void resetCooldown() {

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
    public TextComponent getComponent() {
        return this.automaticDesc;
    }
    @EventHandler
    private void onEndGame(EndGameEvent event) {
        HandlerList.unregisterAll(this);
    }
    @EventHandler
    private void onUHCKill(UHCPlayerKillEvent event) {
        if (this.killRunnable != null) {
            if (this.killRunnable.uuidTarget != null && !killRunnable.killTarget) {
                if (event.getVictim().getUniqueId().equals(killRunnable.uuidTarget) && event.getPlayerKiller() != null && event.getPlayerKiller().getUniqueId().equals(getPlayer())) {
                    killRunnable.onKillTarget(event.getPlayerKiller());
                }
            }
        }
    }
    private static class KillRunnable extends BukkitRunnable {
        private final LeJuge leJuge;
        private int actualTimer;
        private UUID uuidTarget;
        private boolean killTarget = false;
        private KillRunnable(LeJuge juge) {
            this.leJuge = juge;
            this.actualTimer = leJuge.timeKill;
        }
        @Override
        public void run() {
            if (leJuge.getGameState().getServerState() != GameState.ServerStates.InGame) {
                cancel();
                return;
            }
            if (leJuge.getGamePlayer().isAlive()) {
                actualTimer--;
                if (actualTimer == 0) {
                    if (!killTarget && uuidTarget != null) {
                        leJuge.setMaxHealth(leJuge.getMaxHealth()-(leJuge.minage ? 2 : 1));
                        Player player = Bukkit.getPlayer(leJuge.getPlayer());
                        if (player != null) {
                            player.sendMessage("§7Vous avez perdu de la§c vie§7 suite à l'échec de votre mission.");
                            player.setMaxHealth(leJuge.getMaxHealth());
                        }
                        chooseTarget();
                    }
                }
            }
        }
        private void chooseTarget() {
            final GameState gameState = leJuge.getGameState();
            final List<UUID> igPlayers = new ArrayList<>(gameState.getInGamePlayers());
            final List<Player> goodPlayers = new ArrayList<>();
            for (final UUID u : igPlayers) {
                Player p = Bukkit.getPlayer(u);
                if (p == null)continue;
                if (!gameState.hasRoleNull(u)) {
                    RoleBase role = gameState.getGamePlayer().get(u).getRole();
                    if (!role.getOriginTeam().equals(TeamList.Solo) && !role.getOriginTeam().equals(TeamList.Jubi) && !role.getOriginTeam().equals(TeamList.Kumogakure)) {
                        goodPlayers.add(p);
                    }
                }
            }
            if (goodPlayers.isEmpty()) {
                Player owner = Bukkit.getPlayer(leJuge.getPlayer());
                if (owner != null) {
                    owner.sendMessage("§7Aucune§c cible§7 n'a été trouver.");
                }
                return;
            }
            while (this.uuidTarget == null) {
                Collections.shuffle(goodPlayers, Main.RANDOM);
                if (RandomUtils.getOwnRandomProbability(10)) {
                    this.uuidTarget = goodPlayers.get(0).getUniqueId();
                }
            }

            Player owner = Bukkit.getPlayer(leJuge.getPlayer());
            if (owner != null) {
                owner.sendMessage("§7Votre §ccible§7 est maintenant: "+Bukkit.getPlayer(uuidTarget).getName());
            }
        }
        private void onKillTarget(Player killer) {
            killTarget = true;
            uuidTarget = null;
            //1 speed 2 force 3 resi
            int random = RandomUtils.getRandomInt(1, 3);
            StringBuilder sb = new StringBuilder();
            sb.append("§7Vous avez réussi à tuer votre§c cible§7, vous obtenez donc§c +5%§7 de ");
            if (random == 1) {
                leJuge.addSpeedAtInt(killer, 5f);
                sb.append("§cSpeed");
            } else if (random == 2) {
                leJuge.addBonusforce(5.0);
                sb.append("§cForce");
            } else if (random == 3) {
                leJuge.addBonusResi(5.0);
                sb.append("§cRésistance");
            }
            sb.append("§7.");
            killer.sendMessage(sb.toString());
        }
    }
}