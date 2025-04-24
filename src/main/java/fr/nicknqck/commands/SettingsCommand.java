package fr.nicknqck.commands;

import fr.nicknqck.items.GUIItems;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SettingsCommand implements CommandExecutor, Listener {

    @Getter
    private static final List<UUID> roleParticleViewers = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Seul un joueur peut faire cette commande");
            return false;
        }
        final Player sender = (Player) commandSender;
        openParticleParametreInventory(sender);
        return true;
    }
    @EventHandler
    private void onInventoryClick(@NonNull final InventoryClickEvent event) {
        if (event.getClickedInventory() == null)return;
        if (event.getClickedInventory().getTitle() == null)return;
        if (event.getCurrentItem() == null)return;
        if (event.getClickedInventory().getTitle().equalsIgnoreCase("§fParamètres")) {
            if (event.getCurrentItem().getType().equals(Material.REDSTONE)) {
                event.setCancelled(true);
                openParticleParametreInventory(event.getWhoClicked());
            }
        } else if (event.getClickedInventory().getTitle().equalsIgnoreCase("§fParamètres des particules")) {
            event.setCancelled(true);
            System.out.println(event.getCurrentItem());
            if (event.getCurrentItem().getType().equals(Material.NETHER_STAR)) {
                if (getRoleParticleViewers().contains(event.getWhoClicked().getUniqueId())) {
                    getRoleParticleViewers().remove(event.getWhoClicked().getUniqueId());
                    event.getWhoClicked().sendMessage("§7Vous ne verrez plus de particule lorsque vous utiliserez un pouvoir");
                } else {
                    getRoleParticleViewers().add(event.getWhoClicked().getUniqueId());
                    event.getWhoClicked().sendMessage("§7Vous verrez maintenant les particules lorsque vous utilisez un pouvoir");
                }
                openParticleParametreInventory(event.getWhoClicked());
            }
            if (event.getCurrentItem().isSimilar(GUIItems.getSelectBackMenu())) {
                openParametreInventory(event.getWhoClicked());
            }
        }
    }
    private void openParametreInventory(HumanEntity human) {
        Inventory inv = Bukkit.createInventory(human, 27, "§fParamètres");
        inv.setItem(13, new ItemBuilder(Material.REDSTONE)
                .setName("§fParamètre des particules")
                .toItemStack());
        human.openInventory(inv);
    }
    private void openParticleParametreInventory(HumanEntity human) {
        Inventory inv = Bukkit.createInventory(human, 9, "§fParamètres des particules");
        inv.setItem(1, new ItemBuilder(Material.NETHER_STAR)
                .setName("§fParticule de visée")
                .setLore((roleParticleViewers.contains(human.getUniqueId()) ? "§a§lActivé" : "§c§lDésactivé"),
                        "",
                        "§7Permet de voir des particules quand vous essayez de viser avec le pouvoir d'un rôle")
                .toItemStack());
        human.openInventory(inv);
    }
}
