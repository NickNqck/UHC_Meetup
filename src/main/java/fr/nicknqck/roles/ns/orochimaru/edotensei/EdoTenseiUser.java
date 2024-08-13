package fr.nicknqck.roles.ns.orochimaru.edotensei;

import fr.nicknqck.GameState;
import fr.nicknqck.HubListener;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.builders.NSRoles;
import fr.nicknqck.utils.GlobalUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
@Getter
public class EdoTenseiUser implements Listener {

    private final ItemStack edoTenseiItem = new ItemBuilder(Material.NETHER_STAR).setName("§5Edo Tensei").setLore("§7Permet de réssusciter jusqu'à deux personnes que vous avez tuer, Coût:§c 2"+ AllDesc.coeur+"§c permanent").toItemStack();
    private final Map<UUID, Location> killLocation;
    private final Map<UUID, RoleBase> edoTenseis;
    private final NSRoles role;
    public EdoTenseiUser(NSRoles role) {
        this.role = role;
        EventUtils.registerEvents(this);
        this.killLocation = new LinkedHashMap<>();
        this.edoTenseis = new LinkedHashMap<>();
    }
    @EventHandler
    private void onEndGame(EndGameEvent event) {
        EventUtils.unregisterEvents(this);
    }
    @EventHandler
    private void onUHCKill(UHCPlayerKillEvent event){
        if (event.getPlayerKiller() != null) {
            if (event.getPlayerKiller().getUniqueId().equals(role.getPlayer())) {
                killLocation.put(event.getVictim().getUniqueId(), event.getVictim().getLocation());
            }
        }
    }
    @EventHandler
    private void onUHCDeath(UHCDeathEvent event) {
        if (edoTenseis.containsKey(event.getPlayer().getUniqueId())) {
            edoTenseis.remove(event.getPlayer().getUniqueId(), event.getRole());
        }
    }
    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getUniqueId().equals(role.getPlayer())) {
            if (event.hasItem()) {
                if (event.getItem().isSimilar(edoTenseiItem)) {
                    if (!this.edoTenseis.isEmpty()) {
                        event.getPlayer().sendMessage("§7Vous avez déjà un§5 Edo Tensei§7 en§a vie");
                        event.setCancelled(true);
                        return;
                    }
                    if (this.killLocation.isEmpty()) {
                        event.getPlayer().sendMessage("§7Il faut avoir tuer un joueur pour utiliser cette technique.");
                        event.setCancelled(true);
                        return;
                    }
                    Inventory inv = Bukkit.createInventory(event.getPlayer(), 54, "§7[§c!§7]§5 Edo Tensei");
                    for (UUID uuid : killLocation.keySet()) {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player != null) {
                            if (event.getPlayer().getWorld().equals(killLocation.get(uuid).getWorld())) {
                                if (event.getPlayer().getLocation().distance(killLocation.get(uuid)) <= 50) {
                                    inv.addItem(new ItemBuilder(GlobalUtils.getPlayerHead(uuid)).setName(player.getName()).setLore("§7Cliquez ici pour réssusciter ce joueur !\n\n/7Coût: §c2"+AllDesc.coeur+" permanent").toItemStack());
                                }
                            }
                        }
                    }
                    event.getPlayer().openInventory(inv);
                    event.setCancelled(true);
                }
            }
        }
    }
    @SuppressWarnings("deprecation")
    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getTitle().isEmpty())return;
        if (event.getInventory().getTitle().equals("§7[§c!§7]§5 Edo Tensei")) {
            if (event.getWhoClicked() instanceof Player) {
                if (event.getWhoClicked().getUniqueId().equals(role.getPlayer())) {
                    if (event.getCurrentItem() != null) {
                        if (event.getCurrentItem().hasItemMeta()) {
                            if (event.getCurrentItem().getItemMeta().hasDisplayName()) {
                                Player clicked = Bukkit.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName());
                                if (clicked != null) {
                                    GameState gameState = GameState.getInstance();
                                    if (!gameState.hasRoleNull(clicked)) {
                                        RoleBase role = gameState.getPlayerRoles().get(clicked);
                                        Player owner = (Player) event.getWhoClicked();
                                        edoTenseis.put(clicked.getUniqueId(), role);
                                        owner.closeInventory();
                                        clicked.sendMessage("§7Vous avez été invoquée par l'§5Edo Tensei");
                                        owner.sendMessage("§5Edo Tensei !");
                                        role.setTeam(this.role.getTeam());
                                        HubListener.getInstance().giveStartInventory(clicked);
                                        gameState.RevivePlayer(clicked);
                                        this.role.setMaxHealth(this.role.getMaxHealth()-4.0);
                                        owner.setMaxHealth(this.role.getMaxHealth());
                                        clicked.teleport(owner);
                                        role.giveItem(clicked, false, role.getItems());
                                        killLocation.remove(clicked.getUniqueId());
                                        clicked.resetTitle();
                                        clicked.sendTitle("§5Edo Tensei !", "Vous êtes maintenant dans le camp "+this.role.getTeam().getName());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            event.setCancelled(true);
        }
    }

}