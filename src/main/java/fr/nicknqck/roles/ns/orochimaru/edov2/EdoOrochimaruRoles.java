package fr.nicknqck.roles.ns.orochimaru.edov2;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.builders.OrochimaruRoles;
import fr.nicknqck.utils.GlobalUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;

public abstract class EdoOrochimaruRoles extends OrochimaruRoles implements Listener {

    private final Map<UUID, Location> killLocation;

    public EdoOrochimaruRoles(UUID player) {
        super(player);
        this.killLocation = new LinkedHashMap<>();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        if (getRoles().equals(Roles.Orochimaru)){
            addPower(new EdoTenseiPower(this), true);
        }
        super.RoleGiven(gameState);
        EventUtils.registerRoleEvent(new EdoRegister(this));
    }

    private static class EdoRegister implements Listener {

        private final EdoOrochimaruRoles role;

        public EdoRegister(EdoOrochimaruRoles roles) {
            this.role = roles;
        }
        @EventHandler
        private void onUHCKill(UHCPlayerKillEvent event){
            if (event.getPlayerKiller() != null) {
                if (event.getPlayerKiller().getUniqueId().equals(this.role.getPlayer())) {
                    this.role.killLocation.put(event.getVictim().getUniqueId(), event.getVictim().getLocation());
                }
            }
        }
    }
    public static class EdoTenseiPower extends ItemPower implements Listener {

        private final EdoOrochimaruRoles role;
        private final Map<UUID, RoleBase> edoTenseis;
        @Setter
        private boolean canEdoTensei = true;

        public EdoTenseiPower(@NonNull EdoOrochimaruRoles role) {
            super("Edo Tensei", null, new ItemBuilder(Material.NETHER_STAR).setName("§5Edo Tensei"), role,
                    "§7Quand vous tuez un joueur, vous pourrez le ressusciter en échange d");
            this.role = role;
            EventUtils.registerRoleEvent(this);
            this.edoTenseis = new LinkedHashMap<>();
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                if (!this.edoTenseis.isEmpty()) {
                    player.sendMessage("§7Vous avez déjà un§5 Edo Tensei§7 en§a vie");
                    event.setCancelled(true);
                    return false;
                }
                if (this.role.killLocation.isEmpty()) {
                    player.sendMessage("§7Il faut avoir tuer un joueur pour utiliser cette technique.");
                    event.setCancelled(true);
                    return false;
                }
                if (!canEdoTensei) {
                    player.sendMessage("§cVous ne pouvez pas utiliser ce pouvoir !");
                    event.setCancelled(true);
                    return false;
                }
                Inventory inv = Bukkit.createInventory(player, 54, "§7(§c!§7)§5 Edo Tensei");
                for (UUID uuid : this.role.killLocation.keySet()) {
                    final Player target = Bukkit.getPlayer(uuid);
                    if (target != null) {
                        if (player.getWorld().equals(this.role.killLocation.get(uuid).getWorld())) {
                            if (player.getLocation().distance(this.role.killLocation.get(uuid)) <= 50) {
                                inv.addItem(new ItemBuilder(GlobalUtils.getPlayerHead(uuid)).setName(target.getName()).setLore("§7Cliquez ici pour réssusciter ce joueur !\n\n/7Coût: §c"+
                                        (Main.getInstance().getGameConfig().getNarutoConfig().getEdoHealthRemove()/2)
                                        +"❤ permanent").toItemStack());
                            }
                        }
                    }
                }
                player.openInventory(inv);
                event.setCancelled(true);
            }
            return false;
        }

        @EventHandler
        private void onUHCDeath(UHCDeathEvent event) {
            if (edoTenseis.containsKey(event.getPlayer().getUniqueId())) {
                edoTenseis.remove(event.getPlayer().getUniqueId(), event.getRole());
            }
        }
        @SuppressWarnings("deprecation")
        @EventHandler
        private void onInventoryClick(InventoryClickEvent event) {
            if (event.getInventory().getTitle().isEmpty())return;
            if (event.getInventory().getTitle().equals("§7(§c!§7)§5 Edo Tensei")) {
                if (event.getWhoClicked() instanceof Player) {
                    if (event.getWhoClicked().getUniqueId().equals(role.getPlayer())) {
                        if (event.getCurrentItem() != null) {
                            if (event.getCurrentItem().hasItemMeta()) {
                                if (event.getCurrentItem().getItemMeta().hasDisplayName()) {
                                    Player clicked = Bukkit.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName());
                                    if (clicked != null) {
                                        GameState gameState = GameState.getInstance();
                                        if (!gameState.hasRoleNull(clicked.getUniqueId())) {
                                            RoleBase role = gameState.getGamePlayer().get(clicked.getUniqueId()).getRole();
                                            Player owner = (Player) event.getWhoClicked();
                                            edoTenseis.put(clicked.getUniqueId(), role);
                                            owner.closeInventory();
                                            clicked.sendMessage("§7Vous avez été invoquée par l'§5Edo Tensei");
                                            owner.sendMessage("§5Edo Tensei !");
                                            role.setTeam(this.role.getTeam());
                                            role.setMaxHealth(20.0);
                                            clicked.getInventory().setContents(role.getGamePlayer().getLastInventoryContent());
                                            clicked.getInventory().setArmorContents(role.getGamePlayer().getLastArmorContent());
                                            gameState.RevivePlayer(clicked);
                                            this.role.setMaxHealth(this.role.getMaxHealth()-Main.getInstance().getGameConfig().getNarutoConfig().getEdoHealthRemove());
                                            owner.setMaxHealth(this.role.getMaxHealth());
                                            clicked.teleport(owner);
                                            final List<Power> copyPower = new ArrayList<>(role.getPowers());
                                            if (!copyPower.isEmpty()) {
                                                for (Power power : copyPower) {
                                                    if (power instanceof ItemPower) {
                                                        clicked.getInventory().removeItem(((ItemPower) power).getItem());
                                                    }
                                                    role.removePower(power);
                                                }
                                            }
                                            role.GiveItems();
                                            role.RoleGiven(this.role.gameState);
                                            this.role.killLocation.remove(clicked.getUniqueId());
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
}