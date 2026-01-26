package fr.nicknqck.roles.aot.solo;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.RoleGiveEvent;
import fr.nicknqck.events.custom.roles.aot.TitanOwnerChangeEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.aot.builders.AotRoles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.titans.TitanBase;
import fr.nicknqck.titans.impl.*;
import fr.nicknqck.utils.event.EventUtils;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class GabiV2 extends AotRoles implements Listener {

    private final List<GamePlayer> titanList = new ArrayList<>();

    public GabiV2(UUID player) {
        super(player);
    }

    @Override
    public String getName() {
        return "Gabi";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Gabi;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Solo;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        super.RoleGiven(gameState);
        EventUtils.registerRoleEvent(this);
        setStealPriority(8000);
        setMaxHealth(getMaxHealth()+10.0);
        setCanVoleTitan(true);
    }
    @EventHandler
    private void onEndGiveRole(@NonNull final RoleGiveEvent event) {
        if (!event.isEndGive())return;
        final List<TitanBase> list = new ArrayList<>();
        for (GamePlayer gamePlayer : event.getGameState().getGamePlayer().values()) {
            if (gamePlayer.getRole() == null)continue;
            if (!gamePlayer.isAlive())continue;
            if (!gamePlayer.isOnline())continue;
            final TitanBase titan = Main.getInstance().getTitanManager().getTitan(gamePlayer.getUuid());
            if (titan == null)continue;
            list.add(titan);
        }
        if (!list.isEmpty()) {
            Collections.shuffle(list, Main.RANDOM);
            int i = 0;
            for (TitanBase titan : list) {
                if (i == 3)continue;
                i++;
                this.titanList.add(titan.getGamePlayer());
            }
            final Player player = Bukkit.getPlayer(getPlayer());
            if (player != null) {
                player.setMaxHealth(getMaxHealth());
                player.setHealth(player.getMaxHealth());
            }
            new GabiForceRunnable(this).runTaskTimerAsynchronously(Main.getInstance(), 0L, 20L);
        } else {
            setCanVoleTitan(false);
            //On va lui donner un Titan aléatoire
            setMaxHealth(24.0);
            final List<Class<? extends TitanBase>> classes = new ArrayList<>();
            classes.add(AssaillantV2.class);
            classes.add(BestialV2.class);
            classes.add(CharetteV2.class);
            classes.add(ColossalV2.class);
            classes.add(CuirasseV2.class);
            classes.add(MachoireV2.class);
            classes.add(WarhammerV2.class);
            Collections.shuffle(classes, Main.RANDOM);
            try {
                final Class<? extends TitanBase> clazz = classes.get(0);
                final TitanBase titan = clazz.getConstructor(GamePlayer.class).newInstance(getGamePlayer());
                getGamePlayer().sendMessage("§7Aucun titan volable est dans la partie, vous avez donc reçus par hasard le titan: "+titan.getName());
                Main.getInstance().getTitanManager().addTitan(getPlayer(), titan);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            final Player player = Bukkit.getPlayer(getPlayer());
            if (player != null) {
                player.setMaxHealth(getMaxHealth());
                player.setHealth(player.getMaxHealth());
            }
        }
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createAutomaticDesc(this)
                .addCustomLines(
                        this.titanList.isEmpty() && !Main.getInstance().getTitanManager().hasTitan(getPlayer()) ? new String[0] : getTitanListInDesc()//Si c'est vide alors je n'affiche rien
                )
                .getText();
    }
    private String[] getTitanListInDesc() {
        final boolean hasTitan = Main.getInstance().getTitanManager().hasTitan(getPlayer());
        return new String[]{
                titanList.isEmpty() ? "/*" : "§7Voici une liste de joueurs qui possèdent un§c titan shifter§7:",
                titanList.isEmpty() ? "/*" : "",
                !this.titanList.isEmpty() ? isGamePlayerGood(this.titanList.get(0)) ? "§8 - §b"+this.titanList.get(0).getPlayerName()+"§7 possède un Titan" : "/*" : "/*",
                titanList.isEmpty() ? "/*" :  titanList.size() < 2 ? "/*" : isGamePlayerGood(this.titanList.get(1)) ? "§8 - §b"+this.titanList.get(1).getPlayerName()+"§7 possède un Titan" : "/*",
               titanList.isEmpty() ? "/*" : titanList.size() < 3 ? "/*" : isGamePlayerGood(this.titanList.get(2)) ? "§8 - §b"+this.titanList.get(2).getPlayerName()+"§7 possède un Titan" : "/*",
                hasTitan ? "/*" :"",
                hasTitan ? "/*" :"§8 - §7Au moment de la mort d'un§c titan shifter§7 si vous êtes assez §7proche,"+
                "§7vous §7pourrez récupérer son§c titan§7 via la commande §6/aot steal§7.",
                hasTitan ? "/*" :"",
                hasTitan ? "/*" :"§8 - §7Qu'importe le nombre de joueurs essayant de récupérer §7ce §ctitan§7, vous serez toujours§c prioritaire§7.",
                hasTitan ? "/*" :"",
                hasTitan ? "/*" :"§8 - §7Si vous récupérez de cette façon un titan appartenenant §7originellement au camp§9 Mahr§7, vous rejoindrez ce dernier.",
                hasTitan ? "/*" : "",
                hasTitan ? "/*" : "§8 - §7En récupérant un§c titan§7, vous perdez immédiatement§c 3"+ AllDesc.coeur+" §cpermanents§7."
        };
    }
    private boolean isGamePlayerGood(GamePlayer gamePlayer) {
        return gamePlayer.getRole() != null && gamePlayer.isOnline() && gamePlayer.isAlive();
    }
    @EventHandler
    private void onOwnerChange(@NonNull final TitanOwnerChangeEvent event) {
        if (event.getNewGamePlayer().getUuid().equals(getPlayer())) {
            if (event.getTitan().getTitanForm().isMahr()) {
                setTeam(TeamList.Mahr);
                addKnowedPlayersFromTeam(TeamList.Mahr);
                event.getNewGamePlayer().sendMessage("§7Vous avez rejoint le camp:§9 Mahr§7.");
                setMaxHealth(getMaxHealth()-6.0);
            }
        }
    }
    private static class GabiForceRunnable extends BukkitRunnable {

        private final GabiV2 gabi;

        private GabiForceRunnable(GabiV2 gabi) {
            this.gabi = gabi;
        }

        @Override
        public void run() {
            if (!GameState.inGame()) {
                cancel();
                return;
            }
            if (this.gabi.titanList.isEmpty()) {
                cancel();
                return;
            }
            if (!gabi.isGamePlayerGood(gabi.getGamePlayer()))return;
            final List<GamePlayer> copyList = new ArrayList<>(this.gabi.titanList);
            for (final GamePlayer gamePlayer : copyList) {
                if (!gabi.isGamePlayerGood(gamePlayer)) {
                    this.gabi.titanList.remove(gamePlayer);
                }
                if (!gamePlayer.getLastLocation().getWorld().equals(gabi.getGamePlayer().getLastLocation().getWorld())) {
                    continue;
                }
                if (gamePlayer.getLastLocation().distance(gabi.getGamePlayer().getLastLocation()) <= 30) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> gabi.givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.NOW));
                    break;
                }
            }
        }
    }
}