package fr.nicknqck;

import fr.nicknqck.events.EventBase;
import fr.nicknqck.events.Events;
import fr.nicknqck.events.custom.DemonKillEvent;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.items.ItemsManager;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.aot.builders.titans.Titans;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.demons.lune.Kaigaku;
import fr.nicknqck.roles.ds.slayers.Nezuko;
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
        if (e.getEntity() instanceof Player){
            Player player = (Player) e.getEntity();
            KillHandler(player, player.getKiller());
            e.setDroppedExp(15);
            e.getDrops().clear();
        }
    }
    public void KillHandler(@Nonnull Player killedPlayer, @Nonnull Entity entityKiller) {
        GameState gameState = GameState.getInstance();
        UHCPlayerKillEvent playerKillEvent = new UHCPlayerKillEvent(killedPlayer, entityKiller, gameState);
        Bukkit.getPluginManager().callEvent(playerKillEvent);
        UHCDeathEvent uhcDeathEvent = new UHCDeathEvent(killedPlayer, gameState, gameState.getPlayerRoles().get(killedPlayer));
        Bukkit.getPluginManager().callEvent(uhcDeathEvent);
        for (EventBase event : gameState.getInGameEvents()) {
            if (entityKiller instanceof Player) {
                event.OnPlayerKilled((Player) entityKiller, killedPlayer, gameState);
            } else if (entityKiller instanceof Arrow) {
                Arrow arrow = (Arrow)entityKiller;
                if (arrow.getShooter() instanceof Player) {
                    event.OnPlayerKilled((Player) arrow.getShooter(), killedPlayer, gameState);
                }
            } else {
                event.OnPlayerKilled(null, killedPlayer, gameState);
            }
        }
        boolean cantDie = false;
        if (!gameState.hasRoleNull(killedPlayer)) {
            if (gameState.getPlayerRoles().get(killedPlayer).onPreDie(entityKiller, gameState) || gameState.getPlayerRoles().get(killedPlayer).getGamePlayer().isCanRevive()) {
                cantDie = true;
            }
            if (gameState.getPlayerRoles().get(killedPlayer).getItems() != null) {
                for (ItemStack item : gameState.getPlayerRoles().get(killedPlayer).getItems()) {
                    if (killedPlayer.getInventory().contains(item)) {
                        killedPlayer.getInventory().remove(item);
                    }
                }
            }
        }
        if (cantDie || playerKillEvent.isCancel() || uhcDeathEvent.isCancelled()) {
            return;
        }
        GamePlayer gamePlayer = gameState.getGamePlayer().get(killedPlayer.getUniqueId());
        gamePlayer.setAlive(false);
        gamePlayer.setDeathLocation(killedPlayer.getLocation());
        gameState.getDeadRoles().add(gameState.getPlayerRoles().get(killedPlayer).getRoles());
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
            gameState.getHokage().onDeath(killedPlayer, gameState);
        }
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
                if (role.getTeam() == TeamList.Demon || role instanceof Kaigaku || role instanceof Nezuko) {
                    onDemonKill(killer.getName());
                }
            }
            for (UUID u : gameState.getInGamePlayers()) {
                Player p = Bukkit.getPlayer(u);
                if (p == null)continue;
                if (gameState.getPlayerRoles().containsKey(p)) {
                    gameState.getPlayerRoles().get(p).PlayerKilled(killer, killedPlayer, gameState);
                    if (!gameState.getPlayerKills().get(killer).containsKey(killedPlayer)) {
                        RoleBase fakeRole = gameState.getPlayerRoles().get(killedPlayer);
                        fakeRole.setOldRole(gameState.getPlayerRoles().get(killedPlayer).getOldRole());
                        gameState.getPlayerKills().get(killer).put(killedPlayer, fakeRole);
                    }
                }
            }
            for (UUID u : gameState.getInGamePlayers()) {
                Player p = Bukkit.getPlayer(u);
                if (p == null)continue;
                if (!gameState.hasRoleNull(killedPlayer)) {
                    gameState.getPlayerRoles().get(p).OnAPlayerDie(killedPlayer, gameState, killer);
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
                        if (role.getTeam() == TeamList.Demon || role instanceof Kaigaku || role instanceof Nezuko) {
                            onDemonKill(killer.getName());
                        }
                    }
                    for (UUID u : gameState.getInGamePlayers()) {
                        Player p = Bukkit.getPlayer(u);
                        if (p == null)continue;
                        if (gameState.getPlayerRoles().containsKey(p))
                            gameState.getPlayerRoles().get(p).PlayerKilled((Player)arr.getShooter(), killedPlayer, gameState);
                        if (!gameState.getPlayerKills().get((Player)arr.getShooter()).containsKey(killedPlayer)) {
                            RoleBase fakeRole = gameState.getPlayerRoles().get(killedPlayer);
                            fakeRole.setOldRole(gameState.getPlayerRoles().get(killedPlayer).getOldRole());
                            gameState.getPlayerKills().get((Player)arr.getShooter()).put(killedPlayer, fakeRole);
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
        for (Events event : Events.values()) {
            event.getEvent().onPlayerKilled(entityKiller, killedPlayer, gameState);
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
        killedPlayer.setMaxHealth(20.0);
        killedPlayer.setHealth(killedPlayer.getMaxHealth());
        killedPlayer.setFoodLevel(20);
        killedPlayer.setGameMode(GameMode.SPECTATOR);
        ItemsManager.ClearInventory(killedPlayer);
        killedPlayer.updateInventory();
        detectWin(gameState);
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
        if (gamePlayer.getRole().getOriginTeam().equals(TeamList.Demon) || gamePlayer.getRole() instanceof Nezuko) {
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
    private void sendDiscDeathMessage(@Nonnull GamePlayer gamePlayer) {
        SendToEveryone(ChatColor.DARK_GRAY+"§o§m-----------------------------------§r§7 (§cDéconnexion§7)");
        SendToEveryone(gamePlayer.getPlayerName()+"§7 est mort,");
        RoleBase role = gamePlayer.getRole();
        SendToEveryone("§7Son rôle était: " + role.getOriginTeam().getColor() + role.getName() + role.getSuffixString());
        SendToEveryone(ChatColor.DARK_GRAY+"§o§m-----------------------------------");
    }
}