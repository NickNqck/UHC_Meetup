package fr.nicknqck.roles.ns.power;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.GameEndEvent;
import fr.nicknqck.events.custom.GamePlayerEatGappleEvent;
import fr.nicknqck.events.custom.GameStartEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KatsuyuManager implements Listener {

    @Getter
    private Katsuyu katsuyu;

    public KatsuyuManager() {
        EventUtils.registerEvents(this);
    }
    @EventHandler
    private void onStart(@NonNull final GameStartEvent event) {
        katsuyu = new Katsuyu();
    }

    @EventHandler
    private void onGameStop(@NonNull final GameEndEvent event) {
        katsuyu = null;
    }
    @EventHandler
    private void onInteract(@NonNull final PlayerInteractEvent event) {
        if (katsuyu == null)return;
        if (event.getItem() == null)return;
        if (!event.getItem().isSimilar(this.katsuyu.item))return;
        final double actual = event.getPlayer().getHealth();
        event.getPlayer().setHealth(Math.min(event.getPlayer().getMaxHealth(), actual + this.katsuyu.stocked));
        final double dif = Math.abs(event.getPlayer().getHealth() - actual);
        this.katsuyu.stocked = this.katsuyu.stocked-dif;
        event.getPlayer().sendMessage("§7Vous vous êtes§d soigner§7 complètement.");
        event.setCancelled(true);
    }
    @EventHandler
    private void onEat(@NonNull final GamePlayerEatGappleEvent event) {
        final double actualHealth = event.getPlayer().getHealth();
        final double maxHealth = event.getPlayer().getMaxHealth();
        if (actualHealth + 4.0 > maxHealth) {
            final double dif = Math.abs((actualHealth + 4.0) - maxHealth);
            this.katsuyu.stocked += dif;
        }
    }
    @EventHandler
    private void onInteractEntity(@NonNull final PlayerInteractEntityEvent event) {
        if (this.katsuyu == null)return;
        final Player player = event.getPlayer();
        if (player.getItemInHand() == null)return;
        if (player.getItemInHand().isSimilar(this.getKatsuyu().item)) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
        }
    }
    @EventHandler(priority = EventPriority.LOWEST)
    private void onDrop(@NonNull final PlayerDropItemEvent event) {
        if (this.katsuyu== null)return;
        if (event.getItemDrop().getItemStack().isSimilar(this.katsuyu.item)) {
            event.setCancelled(true);
        }
    }
    public void addUser(@NonNull final Player player) {
        if (this.katsuyu == null)return;
        this.katsuyu.uuidList.add(player.getUniqueId());
        updateInventoryUsers();
    }
    public void addUser(@NonNull final UUID uuid) {
        if (this.katsuyu == null) {
            return;
        }
        this.katsuyu.uuidList.add(uuid);
        updateInventoryUsers();
    }
    public void updateInventoryUsers() {
        if (this.katsuyu == null)return;
        if (this.katsuyu.uuidList.isEmpty())return;
        for (@NonNull final UUID uuid : this.katsuyu.uuidList) {
            final Player player = Bukkit.getPlayer(uuid);
            if (player == null)continue;
            if (!player.getInventory().contains(this.katsuyu.item)) {
                player.getInventory().addItem(this.katsuyu.item);
            }
        }
    }
    public boolean containsUser(@NonNull final UUID uuid) {
        if (this.katsuyu == null)return false;
        return this.katsuyu.uuidList.contains(uuid);
    }
    public String[] getPowerDescription(){
        return new String[] {
                "§7Lorsque vous mangez une§e pomme d'or§7 la§a vie§7 qui§c n'est pas§d régénérer§7",
                "§7est§c stocked§7 à la place.",
                "",
                "§7Si vous faite un§f clique§7 avec§d Katsuyu§7, vous vous§d régénérez§7 le plus possible",
                "§7en fonction de la§a vie stocker§7."
        };
    }
    @Getter
    private static class Katsuyu {

        private final List<UUID> uuidList = new ArrayList<>();
        private final ItemStack item;
        private final KatsuyuRunnable katsuyuRunnable;
        private double stocked = 0.0;

        private Katsuyu() {
            this.item = new ItemBuilder(Material.INK_SACK).setDurability(9).setName("§dKatsuyu").toItemStack();
            this.katsuyuRunnable = new KatsuyuRunnable(this);
        }
        private static class KatsuyuRunnable extends BukkitRunnable {

            private final Katsuyu katsuyu;

            private KatsuyuRunnable(Katsuyu katsuyu) {
                this.katsuyu = katsuyu;
                runTaskTimerAsynchronously(Main.getInstance(), 20, 20);
            }

            @Override
            public void run() {
                if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (this.katsuyu.uuidList.isEmpty())return;
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> Main.getInstance().getKatsuyuManager().updateInventoryUsers());
                for (UUID uuid : this.katsuyu.uuidList) {
                    final GamePlayer gamePlayer = GamePlayer.of(uuid);
                    if (gamePlayer == null)continue;
                    if (!gamePlayer.isAlive())continue;
                    gamePlayer.getActionBarManager().updateActionBar("katsuyu.stock", "§aVie stocker:§d "+new DecimalFormat("#.#").format(this.katsuyu.stocked)+" HP");
                }
            }
        }
    }

}
