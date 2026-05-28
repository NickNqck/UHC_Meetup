package fr.nicknqck.managers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.DemonKillEvent;
import fr.nicknqck.events.custom.death.FinalDeathEvent;
import fr.nicknqck.events.custom.death.UHCDeathEvent;
import fr.nicknqck.events.custom.death.UHCDeathMessageEvent;
import fr.nicknqck.events.custom.death.UHCTimerDeathEvent;
import fr.nicknqck.items.ItemsManager;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.enums.TeamList;
import fr.nicknqck.roles.ds.demons.lune.KaigakuV2;
import fr.nicknqck.roles.ds.slayers.NezukoV2;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.NonNull;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static fr.nicknqck.GameListener.*;

public class DeathManager implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    private void VanillaDeath(PlayerDeathEvent e) {
        e.setDroppedExp(5);
        e.getEntity().getInventory().clear();
        e.setDeathMessage(null);
    }
    @EventHandler
    private void onEntityDeath(EntityDeathEvent e){
        if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame))return;
        if (e.getEntity() instanceof Player){
            Player player = (Player) e.getEntity();
            if (player.getKiller() != null) {
                KillHandler(player, player.getKiller());
            } else {
                KillHandler(player, player.getLastDamageCause().getEntity());
            }
            e.setDroppedExp(15);
            e.getDrops().clear();
        }
    }
    public void KillHandler(@Nonnull Player killedPlayer, @Nonnull Entity entityKiller) {
        final GameState gameState = GameState.getInstance();
        if (gameState.getGamePlayer().containsKey(killedPlayer.getUniqueId())) {
            final GamePlayer gamePlayer = gameState.getGamePlayer().get(killedPlayer.getUniqueId());
            gamePlayer.setLastInventoryContent(killedPlayer.getInventory().getContents());
            gamePlayer.setDeathLocation(gamePlayer.getLastLocation());
        }
        if (this.cantDie(gameState, killedPlayer)) {
            return;
        }
        final GamePlayer gameDeathPlayer = GamePlayer.of(killedPlayer.getUniqueId());
        if (gameDeathPlayer != null) {
            Main.getInstance().debug("Killing " + killedPlayer.getDisplayName());
            this.ReelKillHandler(killedPlayer, entityKiller);
         /*test   gameDeathPlayer.setAlive(false);
            gameDeathPlayer.sendMessage("§7Vous êtes§c mort§7, mais ne vous§c déconnectez pas§7 vous avez une chance d'être§a réscucité§7.");
            gameDeathPlayer.teleport(Main.getInstance().getWorldManager().getLobbyWorld().getSpawnLocation());
            new DeathRunnable(gameDeathPlayer, entityKiller).runTaskTimer(Main.getInstance(), 0 , 20);*/
        } else {
            this.ReelKillHandler(killedPlayer, entityKiller);
        }
    }
    public void ReelKillHandler(@NonNull final Player killedPlayer, @NonNull final Entity entityKiller) {
        final GameState gameState = GameState.getInstance();
        final GamePlayer gamePlayerKiller = GamePlayer.of(entityKiller.getUniqueId());
        UHCDeathEvent uhcDeathEvent = new UHCDeathEvent(killedPlayer, gameState, gameState.getGamePlayer().get(killedPlayer.getUniqueId()).getRole(), gamePlayerKiller);
        Bukkit.getPluginManager().callEvent(uhcDeathEvent);
        if (uhcDeathEvent.isCancelled()) {
            return;
        }
        final FinalDeathEvent finalDeathEvent = new FinalDeathEvent(killedPlayer, gameState, gameState.getGamePlayer().get(killedPlayer.getUniqueId()).getRole(), entityKiller);
        Bukkit.getPluginManager().callEvent(finalDeathEvent);
        if (gameState.getGamePlayer().containsKey(killedPlayer.getUniqueId())) {
            GamePlayer gamePlayer = gameState.getGamePlayer().get(killedPlayer.getUniqueId());
            gamePlayer.setAlive(false);
            gamePlayer.setDeathLocation(killedPlayer.getLocation());
            gameState.getDeadRoles().add(gameState.getGamePlayer().get(killedPlayer.getUniqueId()).getRole().getRoles());
        }
        for (ItemStack item : killedPlayer.getInventory().getContents()){
            if (item == null)continue;
            if (item.getType().equals(Material.AIR))continue;
            if (item.getAmount() <= 64) {
                if (item.getAmount() > 0) {
                    dropItem(killedPlayer.getLocation().clone(), item.clone());
                } else {
                    dropItem(killedPlayer.getLocation().clone(), new ItemBuilder(item).setAmount(1).toItemStack());
                }
            }
        }
        removeRoleItem(gameState, killedPlayer);
        dropDeathItems(killedPlayer.getLocation());
        //damager = le tueur
        //player = la victim/le mort
        if (entityKiller instanceof Player) {
            Player killer = (Player) entityKiller;
            DeathMessage(killedPlayer);
            if (!gameState.hasRoleNull(killer.getUniqueId())) {
                RoleBase role = gameState.getGamePlayer().get(killer.getUniqueId()).getRole();
                if (role.getTeam() == TeamList.Demon || role instanceof KaigakuV2 || role instanceof NezukoV2) {
                    onDemonKill(killer.getName());
                }
            }
            for (UUID u : gameState.getInGamePlayers()) {
                Player p = Bukkit.getPlayer(u);
                if (p == null)continue;
                if (!gameState.hasRoleNull(killedPlayer.getUniqueId())) {
                    gameState.getGamePlayer().get(p.getUniqueId()).getRole().OnAPlayerDie(killedPlayer, gameState, killer);
                }
                if (!gameState.hasRoleNull(p.getUniqueId())) {
                    gameState.getGamePlayer().get(p.getUniqueId()).getRole().PlayerKilled(killer, killedPlayer, gameState);
                    if (!gameState.getPlayerKills().get(killer.getUniqueId()).containsKey(killedPlayer) && !gameState.hasRoleNull(killedPlayer.getUniqueId())) {
                        RoleBase fakeRole = gameState.getGamePlayer().get(killedPlayer.getUniqueId()).getRole();
                        gameState.getPlayerKills().get(killer.getUniqueId()).put(killedPlayer, fakeRole);
                    }
                }
            }
        }else {
            if (entityKiller instanceof Arrow) {
                Arrow arr = (Arrow) entityKiller;
                if (arr.getShooter() instanceof Player) {
                    Player killer = (Player) arr.getShooter();
                    DeathMessage(killedPlayer);
                    if (!gameState.hasRoleNull(((Player) arr.getShooter()).getUniqueId())) {
                        RoleBase role = gameState.getGamePlayer().get(((Player) arr.getShooter()).getUniqueId()).getRole();
                        if (role.getTeam() == TeamList.Demon || role instanceof KaigakuV2 || role instanceof NezukoV2) {
                            onDemonKill(killer.getName());
                        }
                    }
                    for (UUID u : gameState.getInGamePlayers()) {
                        Player p = Bukkit.getPlayer(u);
                        if (p == null)continue;
                        if (!gameState.hasRoleNull(p.getUniqueId()))
                            gameState.getGamePlayer().get(p.getUniqueId()).getRole().PlayerKilled((Player)arr.getShooter(), killedPlayer, gameState);
                        if (!gameState.getPlayerKills().get(((Player) arr.getShooter()).getUniqueId()).containsKey(killedPlayer)) {
                            RoleBase fakeRole = gameState.getGamePlayer().get(killedPlayer.getUniqueId()).getRole();
                            gameState.getPlayerKills().get(((Player) arr.getShooter()).getUniqueId()).put(killedPlayer, fakeRole);
                        }
                    }
                    for (UUID u : gameState.getInGamePlayers()) {
                        Player p = Bukkit.getPlayer(u);
                        if (p == null)continue;
                        if (!gameState.hasRoleNull(killedPlayer.getUniqueId())) {
                            gameState.getGamePlayer().get(p.getUniqueId()).getRole().OnAPlayerDie(killedPlayer, gameState, killer);
                        }
                    }
                } else {//La cause de la mort n'est pas un projectile
                    DeathMessage(killedPlayer);
                }
            } else {//La cause de la mort n'est pas une flèche
                DeathMessage(killedPlayer);
            }
        }
        gameState.delInGamePlayers(killedPlayer);
        gameState.addInSpecPlayers(killedPlayer);
        if (Main.getInstance().getGameConfig().isMortEclair()) {
            killedPlayer.getWorld().strikeLightningEffect(killedPlayer.getLocation());
        }
        if (gameState.getInGamePlayers().size()-1 <= 0) {
            ItemsManager.ClearInventory(killedPlayer);
        } else {
            dropItem(killedPlayer.getLocation(), new ItemStack(Material.ARROW, 8));
            dropItem(killedPlayer.getLocation(), new ItemStack(Material.BRICK, 16));
        }
        killedPlayer.spigot().respawn();
        killedPlayer.setFoodLevel(20);
        killedPlayer.setGameMode(GameMode.SPECTATOR);
        killedPlayer.updateInventory();
        killedPlayer.teleport(new Location(Main.getInstance().getWorldManager().getGameWorld(), 0.0, 100, 0.0));
    /*5    if (gameState.getGamePlayer().containsKey(killedPlayer.getUniqueId())) {
            gameState.getGamePlayer().get(killedPlayer.getUniqueId()).setKiller(playerKillEvent.getGamePlayerKiller());
        }*/
        detectWin(gameState);
    }
    private void removeRoleItem(final GameState gameState, final Player player) {
        if (!gameState.hasRoleNull(player.getUniqueId()) && gameState.getGamePlayer().get(player.getUniqueId()).getRole().getItems() != null) {
            for (ItemStack item : gameState.getGamePlayer().get(player.getUniqueId()).getRole().getItems()) {
                if (player.getInventory().contains(item)) {
                    player.getInventory().remove(item);
                }
            }
        }
    }
    public void DisconnectKillHandler(@Nonnull GamePlayer gamePlayer) {
        GameState gameState = GameState.getInstance();
        gamePlayer.setAlive(false);
        gamePlayer.setDeathLocation(gamePlayer.getLastLocation());
        gameState.getDeadRoles().add(gamePlayer.getRole().getRoles());
        for (ItemStack item : gamePlayer.getLastInventoryContent()){
            if (item == null)continue;//l'IDE me dit que c'est impossible que item sois égale à null mais c'est faux
            if (item.getType() != Material.AIR) {
                if (item.getAmount() <= 64) {
                    if (item.getAmount() > 0) {
                        dropItem(gamePlayer.getDeathLocation(), item.clone());
                    } else {
                        dropItem(gamePlayer.getDeathLocation(), new ItemBuilder(item).setAmount(1).toItemStack());
                    }
                }
            }
        }
        if (gamePlayer.getRole().getOriginTeam().equals(TeamList.Demon) || gamePlayer.getRole() instanceof NezukoV2) {
            onDemonKill(gamePlayer.getPlayerName());
        }
        dropDeathItems(gamePlayer.getDeathLocation());
        GameState.getInstance().getInGamePlayers().remove(gamePlayer.getUuid());
        if (GameState.getInstance().getInGamePlayers().size()-1 > 0) {
            dropItem(gamePlayer.getDeathLocation(), new ItemStack(Material.ARROW, 8));
            dropItem(gamePlayer.getDeathLocation(), new ItemStack(Material.BRICK, 16));
        }
        sendDiscDeathMessage(gamePlayer);
        detectWin(GameState.getInstance());
    }
    private void onDemonKill(String killerName) {
        GameState gameState = GameState.getInstance();
        final List<UUID> demons = new ArrayList<>();
        for (UUID u : gameState.getInGamePlayers()) {
            Player p = Bukkit.getPlayer(u);
            if (p == null)continue;
            if (!gameState.hasRoleNull(p.getUniqueId())) {
                RoleBase role2 = gameState.getGamePlayer().get(p.getUniqueId()).getRole();
                if (role2.getOriginTeam() == TeamList.Demon || role2 instanceof KaigakuV2) {
                    demons.add(u);
                    p.sendMessage("§cLe joueur§4 "+killerName+"§c à tué quelqu'un....");
                }
            }
        }
        Bukkit.getPluginManager().callEvent(new DemonKillEvent(demons, killerName));
    }
    private void dropDeathItems(@NonNull Location loc) {
        for (ItemStack item : Main.getInstance().getGameConfig().getItemOnKill()) {
            dropItem(loc, item);
        }
    }
    private void DeathMessage(@Nonnull Player victim) {
        @NonNull final List<String> toSend = new ArrayList<>();
        toSend.add(ChatColor.DARK_GRAY+"§o§m-----------------------------------");
        toSend.add(victim.getDisplayName()+"§7 est mort,");
        if (!GameState.getInstance().hasRoleNull(victim.getUniqueId())) {
            World world = Bukkit.getWorld("nakime");
            RoleBase role = GameState.getInstance().getGamePlayer().get(victim.getUniqueId()).getRole();
            if (world != null && victim.getWorld().equals(world)){
                toSend.add("§7Son rôle était: "+(victim.getWorld().equals(Objects.requireNonNull(Bukkit.getWorld("nakime"))) ? role.getTeam().getColor()+role.getName() : "§k"+victim.getDisplayName()));
            } else {
                toSend.add("§7Son rôle était: "+role.getTeam().getColor()+role.getName()+role.getSuffixString());
            }
        } else {
            toSend.add(victim.getDisplayName()+"§c est mort, il n'avait pas de rôle");
        }
        toSend.add(ChatColor.DARK_GRAY+"§o§m-----------------------------------");
        @NonNull final UHCDeathMessageEvent event = new UHCDeathMessageEvent(toSend, victim);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isSendToVictim()) {
            victim.sendMessage(toSend.toArray(new String[0]));
        }
        for (@Nonnull final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getUniqueId().equals(victim.getUniqueId())) {continue;}
            onlinePlayer.sendMessage(toSend.toArray(new  String[0]));
        }
    }
    private boolean cantDie(final GameState gameState, final Player killedPlayer) {
        if (!gameState.hasRoleNull(killedPlayer.getUniqueId())) {
            return gameState.getGamePlayer().get(killedPlayer.getUniqueId()).isCanRevive();
        }
        return false;
    }
    private void sendDiscDeathMessage(@Nonnull GamePlayer gamePlayer) {
        SendToEveryone(ChatColor.DARK_GRAY+"§o§m-----------------------------------§r§7 (§cDéconnexion§7)");
        SendToEveryone(gamePlayer.getPlayerName()+"§7 est mort,");
        RoleBase role = gamePlayer.getRole();
        SendToEveryone("§7Son rôle était: " + role.getOriginTeam().getColor() + role.getName() + role.getSuffixString());
        SendToEveryone(ChatColor.DARK_GRAY+"§o§m-----------------------------------");
    }

    @SuppressWarnings("unused")
    private static final class DeathRunnable extends BukkitRunnable {

        private final GamePlayer gameDeathPlayer;
        private final Entity entityKiller;
        private final UUID entityKillerUUID;

        private int timeLeft = 6;

        private DeathRunnable(GamePlayer gameDeathPlayer, Entity entityKiller) {
            this.gameDeathPlayer = gameDeathPlayer;
            this.entityKiller = entityKiller;
            this.entityKillerUUID = entityKiller.getUniqueId();
        }

        @Override
        public void run() {
            if (!GameState.inGame()) {
                cancel();
                return;
            }
            @NonNull final UHCTimerDeathEvent uhcTimerDeathEvent = new UHCTimerDeathEvent(gameDeathPlayer, entityKiller, entityKillerUUID, this.timeLeft);
            Bukkit.getPluginManager().callEvent(uhcTimerDeathEvent);
            if (uhcTimerDeathEvent.isCancelled()) {
                cancel();
                return;
            }
            if (this.timeLeft <= 0) {
                Main.getInstance().getDeathManager().ReelKillHandler(gameDeathPlayer.getPlayer(), entityKiller);
                cancel();
                return;
            }
            gameDeathPlayer.sendMessage("monde: "+gameDeathPlayer.getLastLocation().getWorld().getName());
            this.timeLeft--;
        }
    }

}