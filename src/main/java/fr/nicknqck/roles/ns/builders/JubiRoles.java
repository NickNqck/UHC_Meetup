package fr.nicknqck.roles.ns.builders;

import fr.nicknqck.GameState;
import fr.nicknqck.entity.bijus.Biju;
import fr.nicknqck.entity.bijus.Bijus;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Jubi;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new TraqueurPower(this), true);
    }

    @Override
    public UchiwaType getUchiwaType() {
        return UchiwaType.LEGENDAIRE;
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
                if (!this.getPlugin().getGameConfig().isBijusEnable()) {
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
                if (bijus.getBiju().getItemInMenu().getItemMeta().getDisplayName().equals(event.getCurrentItem().getItemMeta().getDisplayName()) || event.getCurrentItem().getItemMeta().getDisplayName().contains(bijus.name())) {
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
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
        }
        private void printTraqueMessage(Player player) {
            if (this.traqued == null) {
                player.sendMessage("§cVous ne traquezcaucun§d biju§c actuellement§c !");
                return;
            }
            player.sendMessage("§7Voici tout les informations disponnible sur "+this.traqued.getName());
            player.sendMessage("");
            if (this.traqued.getHote() == null) {
                if (this.traqued.getLivingEntity() == null) {
                    player.sendMessage("§7Il apparaitra en "+getGoodCoord(this.traqued.getSpawn()));
                    player.setCompassTarget(this.traqued.getSpawn());
                    player.sendMessage("§7Il apparaitra dans§c "+
                            StringUtils.secondsTowardsBeautiful(this.traqued.getTimeSpawn()-this.roles.getGameState().getInGameTime())+
                            "§7 c'est à dire à §c"+
                            StringUtils.secondsTowardsBeautiful(this.traqued.getTimeSpawn())+"§7.");
                } else {
                    player.sendMessage("§7Il est actuellement en "+getGoodCoord(this.traqued.getLivingEntity().getLocation()));
                    player.setCompassTarget(this.traqued.getLivingEntity().getLocation());
                }
            } else {
                GamePlayer gamePlayer = this.roles.getGameState().getGamePlayer().get(this.traqued.getHote());
                player.sendMessage("§c"+gamePlayer.getPlayerName()+"§7 est en "+getGoodCoord(gamePlayer.getLastLocation()));
            }
        }
        private String getGoodCoord(Location location) {
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();
            return "§cx§7: §c"+x+"§7,§c y§7:§c "+y+"§7,§c z§7:§c "+z;
        }
    }
}