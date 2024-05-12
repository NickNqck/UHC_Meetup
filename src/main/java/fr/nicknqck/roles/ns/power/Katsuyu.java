package fr.nicknqck.roles.ns.power;

import fr.nicknqck.Main;
import fr.nicknqck.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Katsuyu implements Listener {
    @Getter
    private UUID user;
    @Getter
    private ItemStack katsuyu = new ItemBuilder(Material.NETHER_STAR).setName("§aKatsuyu").setLore("§7Vous permet de§d soigner§7 les personnes choisis (§6/ns katsuyu§7)").toItemStack();
    private boolean using = false;
    @Getter
    private int savedHP = 0;
    public Katsuyu(UUID user) {
        this.user = user;
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }
    @EventHandler
    private void onInteract(PlayerInteractEvent e){
        if (e.getPlayer().getUniqueId().equals(getUser())){
            if (e.getItem().isSimilar(getKatsuyu())){
                if (using){

                }
            }
        }
    }
}