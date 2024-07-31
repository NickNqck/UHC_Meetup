package fr.nicknqck.roles.custom;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class LeJuge extends CustomRolesBase{
    private final TextComponent automaticDesc;
    private final int timeKill;
    private final KillRunnable killRunnable;
    public LeJuge(Player player) {
        super(player);
        if (getGameState().isMinage()) {
            timeKill = 60*10;
        } else {
            timeKill = 60*5;
        }
        getEffects().put(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        AutomaticDesc desc = new AutomaticDesc(this);
        Map<HoverEvent, String> particularitees = new HashMap<>();

        particularitees.put(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Toute les§c "+ StringUtils.secondsTowardsBeautiful(timeKill)+"§7 vous recevez une cible, si vous arrivez à la tuer vous obtiendrez un§c pourcentage§7 d'un effet aléatoire parmis:§b Speed§7,§c Force§7 et§9 Résistance§7,\n§7Sinon, si vous n'y arriver pas vous perdrez§c "+(getGameState().isMinage() ? "1/2" : "1")+ AllDesc.coeur+"§7 de manière§c permanente§7.")}), "§6Missions");
        desc.addEffects(getEffects());
        desc.setParticularites(particularitees);
        this.automaticDesc = desc.getText();
        this.killRunnable = new KillRunnable(this);
        killRunnable.runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
    }

    @Override
    public String getName() {
        return "Le Juge";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.LeJuge;
    }

    @Override
    public TeamList getOriginTeam() {
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
    private static class KillRunnable extends BukkitRunnable {
        private final LeJuge leJuge;
        private int actualTimer;
        private UUID uuidTarget;
        private boolean killTarget = false;
        private KillRunnable(LeJuge juge) {
            this.leJuge = juge;
            this.actualTimer = leJuge.timeKill;
            chooseTarget();
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
                    
                }
            }
        }
        private void chooseTarget() {
            final GameState gameState = leJuge.getGameState();
            final List<Player> igPlayers = new ArrayList<>(gameState.getInGamePlayers());
            final List<Player> goodPlayers = new ArrayList<>();
            for (final Player p : igPlayers) {
                if (!gameState.hasRoleNull(p)) {
                    RoleBase role = gameState.getPlayerRoles().get(p);
                    if (!role.getOriginTeam().equals(TeamList.Solo) && !role.getOriginTeam().equals(TeamList.Jubi) && !role.getOriginTeam().equals(TeamList.Kumogakure)) {
                        goodPlayers.add(p);
                    }
                }
            }
            while (this.uuidTarget == null) {
                Collections.shuffle(goodPlayers, Main.RANDOM);
                if (RandomUtils.getOwnRandomProbability(10)) {
                    this.uuidTarget = goodPlayers.get(0).getUniqueId();
                }
            }
        }
        private void onKillTarget() {
            killTarget = true;
        }
    }
}
