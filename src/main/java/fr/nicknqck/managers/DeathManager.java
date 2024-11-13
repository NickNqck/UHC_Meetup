package fr.nicknqck.managers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.DemonKillEvent;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.items.ItemsManager;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.aot.builders.titans.Titans;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.demons.lune.Kaigaku;
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
        UHCPlayerKillEvent playerKillEvent = new UHCPlayerKillEvent(killedPlayer, entityKiller, gameState);
        Bukkit.getPluginManager().callEvent(playerKillEvent);
        UHCDeathEvent uhcDeathEvent = new UHCDeathEvent(killedPlayer, gameState, gameState.getPlayerRoles().get(killedPlayer));
        Bukkit.getPluginManager().callEvent(uhcDeathEvent);
        if (this.cantDie(gameState, killedPlayer, entityKiller) || playerKillEvent.isCancel() || uhcDeathEvent.isCancelled()) {
            return;
        }

        if (gameState.getGamePlayer().containsKey(killedPlayer.getUniqueId())) {
            GamePlayer gamePlayer = gameState.getGamePlayer().get(killedPlayer.getUniqueId());
            gamePlayer.setAlive(false);
            gamePlayer.setDeathLocation(killedPlayer.getLocation());
            gameState.getDeadRoles().add(gameState.getPlayerRoles().get(killedPlayer).getRoles());
        }
        for (ItemStack item : killedPlayer.getInventory().getContents()){
            if (item != null){
                if (item.getType() != Material.AIR){
                    if (item.getAmount() <= 64){
                        if (item.getAmount() > 0) {
                            dropItem(killedPlayer.getLocation().clone(), item.clone());
                        } else {
                            dropItem(killedPlayer.getLocation().clone(), new ItemBuilder(item).setAmount(1).toItemStack());
                        }
                    }
                }
            }
        }
        if (gameState.getHokage() != null) {
            gameState.getHokage().onDeath(killedPlayer, entityKiller, gameState);
        }
        removeRoleItem(gameState, killedPlayer);
        dropDeathItems(killedPlayer.getLocation());
        //damager = le tueur
        //player = la victim/le mort
        if (entityKiller instanceof Player) {
            Player killer = (Player) entityKiller;
            DeathMessage(killedPlayer);
            for (UUID u : gameState.getInGamePlayers()) {
                Player p = Bukkit.getPlayer(u);
                if (p == null)continue;
                if (gameState.getPlayerRoles().containsKey(p)) {
                    gameState.getPlayerRoles().get(p).OnAPlayerKillAnotherPlayer(killedPlayer, killer, gameState);
                }
            }
            if (gameState.getPlayerRoles().containsKey(killer)) {
                RoleBase role = gameState.getPlayerRoles().get(killer);
                if (role.getTeam() == TeamList.Demon || role instanceof Kaigaku || role instanceof NezukoV2) {
                    onDemonKill(killer.getName());
                }
            }
            for (UUID u : gameState.getInGamePlayers()) {
                Player p = Bukkit.getPlayer(u);
                if (p == null)continue;
                if (!gameState.hasRoleNull(killedPlayer)) {
                    gameState.getPlayerRoles().get(p).OnAPlayerDie(killedPlayer, gameState, killer);
                }
                if (gameState.getPlayerRoles().containsKey(p)) {
                    gameState.getPlayerRoles().get(p).PlayerKilled(killer, killedPlayer, gameState);
                    if (!gameState.getPlayerKills().get(killer.getUniqueId()).containsKey(killedPlayer)) {
                        RoleBase fakeRole = gameState.getPlayerRoles().get(killedPlayer);
                        fakeRole.setOldRole(gameState.getPlayerRoles().get(killedPlayer).getOldRole());
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
                    for (UUID u : gameState.getInGamePlayers()) {
                        Player p = Bukkit.getPlayer(u);
                        if (p == null)continue;
                        if (gameState.getPlayerRoles().containsKey(p)) {
                            gameState.getPlayerRoles().get(p).OnAPlayerKillAnotherPlayer(killedPlayer, killer, gameState);
                        }
                    }
                    if (gameState.getPlayerRoles().containsKey((Player)arr.getShooter())) {
                        RoleBase role = gameState.getPlayerRoles().get((Player)arr.getShooter());
                        if (role.getTeam() == TeamList.Demon || role instanceof Kaigaku || role instanceof NezukoV2) {
                            onDemonKill(killer.getName());
                        }
                    }
                    for (UUID u : gameState.getInGamePlayers()) {
                        Player p = Bukkit.getPlayer(u);
                        if (p == null)continue;
                        if (gameState.getPlayerRoles().containsKey(p))
                            gameState.getPlayerRoles().get(p).PlayerKilled((Player)arr.getShooter(), killedPlayer, gameState);
                        if (!gameState.getPlayerKills().get(((Player) arr.getShooter()).getUniqueId()).containsKey(killedPlayer)) {
                            RoleBase fakeRole = gameState.getPlayerRoles().get(killedPlayer);
                            fakeRole.setOldRole(gameState.getPlayerRoles().get(killedPlayer).getOldRole());
                            gameState.getPlayerKills().get(((Player) arr.getShooter()).getUniqueId()).put(killedPlayer, fakeRole);
                        }
                    }
                    for (UUID u : gameState.getInGamePlayers()) {
                        Player p = Bukkit.getPlayer(u);
                        if (p == null)continue;
                        if (!gameState.hasRoleNull(killedPlayer)) {
                            gameState.getPlayerRoles().get(p).OnAPlayerDie(killedPlayer, gameState, killer);
                        }
                    }
                } else {//La cause de la mort n'est pas un projectile
                    DeathMessage(killedPlayer);
                }
            } else {//La cause de la mort n'est pas une flèche
                DeathMessage(killedPlayer);
            }
        }
        for (Titans t : Titans.values()) {
            t.getTitan().PlayerKilled(killedPlayer, entityKiller);
        }
        gameState.delInGamePlayers(killedPlayer);
        gameState.addInSpecPlayers(killedPlayer);
        if (gameState.morteclair) {
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
        if (gameState.getGamePlayer().containsKey(killedPlayer.getUniqueId())) {
            gameState.getGamePlayer().get(killedPlayer.getUniqueId()).setKiller(playerKillEvent.getGamePlayerKiller());
        }
        detectWin(gameState);
    }
    private void removeRoleItem(final GameState gameState, final Player player) {
        if (gameState.getPlayerRoles().get(player).getItems() != null) {
            for (ItemStack item : gameState.getPlayerRoles().get(player).getItems()) {
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
            if (!gameState.hasRoleNull(p)) {
                RoleBase role2 = gameState.getPlayerRoles().get(p);
                if (role2.getOriginTeam() == TeamList.Demon || role2 instanceof Kaigaku) {
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
        SendToEveryone(ChatColor.DARK_GRAY+"§o§m-----------------------------------");
        SendToEveryone(victim.getDisplayName()+"§7 est mort,");
        if (!GameState.getInstance().hasRoleNull(victim)) {
            World world = Bukkit.getWorld("nakime");
            RoleBase role = GameState.getInstance().getPlayerRoles().get(victim);
            if (world != null && victim.getWorld().equals(world)){
                SendToEveryone("§7Son rôle était: "+(victim.getWorld().equals(Objects.requireNonNull(Bukkit.getWorld("nakime"))) ? role.getTeam().getColor()+role.getName() : "§k"+victim.getDisplayName()));
            } else {
                SendToEveryone("§7Son rôle était: "+role.getTeam().getColor()+role.getName()+role.getSuffixString());
            }
        } else {
            SendToEveryone(victim.getDisplayName()+"§c est mort, il n'avait pas de rôle");
        }
        SendToEveryone(ChatColor.DARK_GRAY+"§o§m-----------------------------------");
    }
    private boolean cantDie(final GameState gameState, final Player killedPlayer, final Entity entityKiller) {
        if (!gameState.hasRoleNull(killedPlayer)) {
            return gameState.getGamePlayer().get(killedPlayer.getUniqueId()).getRole().onPreDie(entityKiller, gameState) ||
                    gameState.getGamePlayer().get(killedPlayer.getUniqueId()).isCanRevive();
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
}