package fr.nicknqck.roles.ns.builders;

import fr.nicknqck.GameState;
import fr.nicknqck.bijus.Biju;
import fr.nicknqck.bijus.Bijus;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import java.util.Map;
import java.util.UUID;

public abstract class JubiRoles extends UchiwaRoles {

    public JubiRoles(UUID player) {
        super(player);
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Jubi;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new TraqueurPower(this), true);
    }

    static class TraqueurPower extends ItemPower implements Listener {

        private final JubiRoles roles;
        private Biju traqued = null;
        protected TraqueurPower(@NonNull JubiRoles role) {
            super("§dTraqueur de biju", null, new ItemBuilder(Material.COMPASS).setName("! §dTraqueur"), role);
            this.roles = role;
            EventUtils.registerEvents(this);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                PlayerInteractEvent event = (PlayerInteractEvent) args.get("event");
                if (this.roles == null)return false;
                if (!this.roles.getGameState().BijusEnable) {
                    this.roles.getPowers().remove(this);
                    player.sendMessage("§7Les bijus sont désactiver pendant cette partie.");
                    player.setItemInHand(null);
                    return true;
                }
                Action action = event.getAction();
                if (action.name().contains("RIGHT")) {
                    Inventory inv = Bukkit.createInventory(player, 9, "§7Traqueur de§d Biju");
                    for (Bijus b : Bijus.values()) {
                        if (b.getBiju().isEnable()) {
                            inv.addItem(b.getBiju().getItem());
                        }
                    }
                    player.openInventory(inv);
                } else {
                    printTraqueMessage(player);
                }
                return true;
            }
            return false;
        }
        @EventHandler
        private void onEndGame(EndGameEvent event) {
            EventUtils.unregisterEvents(this);
        }
        @EventHandler
        private void onInventoryClick(InventoryClickEvent event) {
            if (event.getClickedInventory() == null)return;
            if (event.getClickedInventory().getTitle() == null)return;
            if (!(event.getWhoClicked() instanceof Player))return;
            if (!event.getWhoClicked().getUniqueId().equals(this.roles.getPlayer()))return;
            if (!event.getClickedInventory().getTitle().equals("§7Traqueur de§d Biju"))return;
            if (event.getCurrentItem() == null)return;
            if (!event.getCurrentItem().hasItemMeta())return;
            if (!event.getCurrentItem().getItemMeta().hasDisplayName())return;
            for (Bijus bijus : Bijus.values()) {
                if (bijus.getBiju().getItemInMenu().getItemMeta().getDisplayName().equals(event.getCurrentItem().getItemMeta().getDisplayName())) {
                    if (bijus.getBiju().isEnable()) {
                        this.traqued = bijus.getBiju();
                        event.getWhoClicked().sendMessage("§7Vous§c traquez§7 maintenant "+bijus.getBiju().getName());
                        printTraqueMessage((Player) event.getWhoClicked());
                    } else {
                        event.getWhoClicked().sendMessage("§7Ce§d Biju§7 a été désactiver.");
                    }
                    break;
                }
            }
            event.getWhoClicked().sendMessage(""+event.getCurrentItem());
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
        }
        private void printTraqueMessage(Player player) {
            if (this.traqued == null) {
                player.sendMessage("§7Vous ne traquez §caucun§d biju§c actuellement§7.");
                return;
            }
            player.sendMessage("§7Voici tout les informations disponnible sur "+this.traqued.getName());
            player.sendMessage("");
            if (this.traqued.getHote() == null) {
                if (this.traqued.getLivingEntity() == null) {
                    player.sendMessage("§7Il apparaitra en §cx: "+this.traqued.getSpawn().getBlockX()+"§7, §cy:§7 "+this.traqued.getSpawn().getBlockY()+"§7, §cz: "+this.traqued.getSpawn().getBlockZ());
                    player.setCompassTarget(this.traqued.getSpawn());
                    player.sendMessage("§7Il apparaitra dans§c "+ StringUtils.secondsTowardsBeautiful(this.roles.getGameState().getInGameTime()-this.traqued.getTimeSpawn()));
                } else {
                    player.sendMessage("§7Il est actuellement en §cx: "+this.traqued.getLivingEntity().getLocation().getBlockX()+"§7, §cy: "+this.traqued.getLivingEntity().getLocation().getBlockY()+"§7, §cz: "+this.traqued.getLivingEntity().getLocation().getBlockZ());
                    player.setCompassTarget(this.traqued.getLivingEntity().getLocation());
                }
            } else {
                GamePlayer gamePlayer = this.roles.getGameState().getGamePlayer().get(this.traqued.getHote());
                player.sendMessage("§c"+gamePlayer.getPlayerName()+"§7 est en§c x: "+gamePlayer.getLastLocation().getBlockX()+"§7, §cy: "+gamePlayer.getLastLocation().getBlockY()+"§7,§c z: "+gamePlayer.getLastLocation().getBlockZ());
            }
        }
    }
}