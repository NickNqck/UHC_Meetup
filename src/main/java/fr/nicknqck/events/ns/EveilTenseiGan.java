package fr.nicknqck.events.ns;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.ResistancePatchEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.events.ds.Event;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ns.builders.IByakuganUser;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EveilTenseiGan extends Event implements Listener {

    private boolean activate = false;
    private final String print = "[EveilTenseiganEvent] ";
    private GamePlayer gamePlayer;
    private final ItemStack sword = new ItemBuilder(Material.DIAMOND_SWORD).addEnchant(Enchantment.DAMAGE_ALL, 3).setName("§cÉpée du§b Tenseigan").setLore("§7Vous permet de passer à travers la résistance des personnes frappés").toItemStack();

    @Override
    public boolean isActivated() {
        return this.activate;
    }

    @Override
    public String getName() {
        return "§bÉveil du Tenseigan";
    }

    @Override
    public void onProc(GameState gameState) {
        final List<GamePlayer> buyakuganUserList = new ArrayList<>();
        for (final GamePlayer gamePlayer : gameState.getGamePlayer().values()) {
            if (!gamePlayer.isAlive())continue;
            if (gamePlayer.getRole() == null)continue;
            if (!gamePlayer.isOnline())continue;
            if (gamePlayer.getRole() instanceof IByakuganUser) {
                buyakuganUserList.add(gamePlayer);
                System.out.println(this.print+gamePlayer.getPlayerName()+" has been added inside the list");
            }
        }
        if (buyakuganUserList.isEmpty()) {
            System.out.println(print+"Unnable to start Tenseigan Event because no one is inside the list");
            return;
        }
        Collections.shuffle(buyakuganUserList, Main.RANDOM);
        System.out.println(print+"Shuffle the list");
        final GamePlayer gamePlayer = buyakuganUserList.get(0);
        System.out.println(print+gamePlayer.getPlayerName()+" has been chose, size of the list: "+buyakuganUserList.size());
        final RoleBase role = gamePlayer.getRole();
        gamePlayer.sendMessage("§7Qu'est-ce qui se passe ? Vous sentez qu'un étrange§a chakra§7 se déverse dans vos yeux, après vérification on dirait qu'il s'agit la d'un§a chakra§e Ôtsutsuki§7, vous devriez essayer de récupérer des§a byakugans§7 pour éveiller de nouveau pouvoirs.");
        role.setTeam(TeamList.Solo, true);
        this.gamePlayer = gamePlayer;
        if (buyakuganUserList.size() == 1) {//donc si mon joueur est le seul porteur du byakugan
            giveTenseiGan();
        }
        this.activate = true;
    }

    @Override
    public ItemStack getMenuItem() {
        return new ItemBuilder(Material.EYE_OF_ENDER).setName(getName()).setLore(getLore()).toItemStack();
    }

    @Override
    public boolean canProc(GameState gameState) {
        for (final GamePlayer gamePlayer : gameState.getGamePlayer().values()) {
            if (!gamePlayer.isAlive())continue;
            if (!gamePlayer.isOnline())continue;
            if (gamePlayer.getRole() == null)continue;
            if (gamePlayer.getRole() instanceof IByakuganUser) {
                return true;//donc il y a minimum 1 utilisateur du byakugan en vie
            }
        }
        return false;
    }

    @Override
    public boolean onGameStart(GameState gameState) {
        EventUtils.registerRoleEvent(this);
        return true;
    }

    private void giveTenseiGan() {
        if (this.gamePlayer == null)return;
        if (!this.gamePlayer.isOnline())return;
        if (gamePlayer.getRole() == null)return;
        if (!gamePlayer.isAlive())return;
        final RoleBase role = gamePlayer.getRole();
        final Player player = Bukkit.getPlayer(gamePlayer.getUuid());
        role.setTeam(TeamList.Solo, true);
        int amountKill = role.getGameState().getPlayerKills().get(role.getPlayer()).size();
        if (amountKill > 0) {
            role.setMaxHealth(role.getMaxHealth()+amountKill);
            if (player != null) {
                player.setHealth(player.getMaxHealth());
            }
            System.out.println(print+ gamePlayer.getPlayerName()+" has been gived "+amountKill+" half-heart");
        }
        gamePlayer.addItems(this.sword);
        gamePlayer.sendMessage("§7Vous avez obtenue le§b Tenseigan§7, vous devenez maintenant inéluctable!");
        role.addPower(new ModeChakraPower(role));
    }
    @EventHandler(priority = EventPriority.LOWEST)
    private void onBattle(final ResistancePatchEvent event) {
        if (this.gamePlayer == null)return;
        if (!this.gamePlayer.getUuid().equals(event.getDamager().getUniqueId()))return;
        if (!event.isNegateResistance())return;
        if (event.getDamager().getItemInHand() == null)return;
        if (!event.getDamager().getItemInHand().isSimilar(this.sword))return;
        //Donc si l'item qu'il a en main est mon épée custom
        event.setCancelled(true);
    }
    @EventHandler(priority = EventPriority.LOWEST)
    private void onKill(final UHCPlayerKillEvent event) {
        if (event.getGamePlayerKiller() == null)return;
        if (this.gamePlayer == null)return;
        if (this.gamePlayer.getUuid().equals(event.getKiller().getUniqueId())) {
            if (gamePlayer.getRole() == null)return;
            if (gamePlayer.getRole().getMaxHealth() < 30.0) {
                gamePlayer.getRole().setMaxHealth(gamePlayer.getRole().getMaxHealth()+1.0);
                event.getKiller().sendMessage("§7En tuant un joueur, vous avez gagner§c 1/2❤ permanent");
            }
        }
    }
    private static class ModeChakraPower extends ItemPower {

        private int timeLeft;
        private final ChakraRunnable chakraRunnable;


        public ModeChakraPower(@NonNull RoleBase role) {
            super("Mode Chakra", null, new ItemBuilder(Material.NETHER_STAR).setName("§bMode Chakra"), role,
                    "§7Vous permet d'obtenir les effets§e Speed I§7 et§9 Résistance I§7 tant que vous avez du temp disponible",
                    "",
                    "§cAu début de la partie vous avez§4 5 minutes§c de temps, pour en gagner vous pouvez tué un joueur ce qui vous donnera§4 1 minute§c."
            );
            this.timeLeft = 60*5;
            this.chakraRunnable = new ChakraRunnable(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (this.chakraRunnable.running) {
                    this.chakraRunnable.stop();
                } else {
                    if (this.timeLeft <= 0) {
                        player.sendMessage("§7Vous n'avez plus asser de temp disponible pour utiliser votre§b mode chakra§7.");
                        return false;
                    }
                    this.chakraRunnable.start();
                }
                return true;
            }
            return false;
        }
        private final static class ChakraRunnable extends BukkitRunnable {

            private final ModeChakraPower modeChakraPower;
            private boolean running;

            private ChakraRunnable(ModeChakraPower modeChakraPower) {
                this.modeChakraPower = modeChakraPower;
                this.running = false;
                runTaskTimerAsynchronously(Main.getInstance(), 20, 20);
            }

            @Override
            public void run() {
                if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame) || this.modeChakraPower.timeLeft <= 0) {
                    stop();
                    return;
                }
                if (!running)return;
                this.modeChakraPower.timeLeft--;
                this.modeChakraPower.getRole().getGamePlayer().getActionBarManager().updateActionBar("tenseigan.chakramode", "§bTemp restant mode chakra: §c"+ StringUtils.secondsTowardsBeautiful(this.modeChakraPower.timeLeft));
            }
            public void stop() {
                this.running = false;
                this.modeChakraPower.getRole().getGamePlayer().getActionBarManager().removeInActionBar("tenseigan.chakramode");
                cancel();
            }
            public void start() {
                if (this.modeChakraPower.timeLeft <= 0) {
                    return;
                }
                this.running = true;
                this.modeChakraPower.getRole().getGamePlayer().getActionBarManager().addToActionBar("tenseigan.chakramode", "§bTemp restant mode chakra: §c"+ StringUtils.secondsTowardsBeautiful(this.modeChakraPower.timeLeft));
            }
        }
    }
}