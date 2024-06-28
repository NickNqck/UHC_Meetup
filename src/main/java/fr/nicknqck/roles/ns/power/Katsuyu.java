package fr.nicknqck.roles.ns.power;

import fr.nicknqck.Main;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Katsuyu implements Listener {
    @Getter
    private UUID user;
    @Getter
    private ItemStack katsuyu = new ItemBuilder(Material.NETHER_STAR).setName("§aKatsuyu").setLore("§7Vous permet de§d soigner§7 les personnes choisis (§6/ns katsuyu§7)").toItemStack();
    private boolean using = false;
    @Getter
    private int savedHP = 0;
    @Getter
    private List<UUID> KatsuyuList = new ArrayList<>();
    @Getter
    private enum Usage{
        Absorbe,
        Distribe
    }
    @Getter
    private Usage usage;
    public Katsuyu(UUID user) {
        this.user = user;
        this.usage = Usage.Absorbe;
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }
    @EventHandler
    private void onInteract(PlayerInteractEvent e){
        if (e.getPlayer().getUniqueId().equals(getUser())){
            if (e.getItem().isSimilar(getKatsuyu())){
                switch (usage){
                    case Absorbe:
                        if (e.getPlayer().getHealth() > 1.0){

                        } else {
                            e.getPlayer().sendMessage("§cVous n'avez pas asser de§l vie§c pour charger votre");
                        }
                        break;
                    case Distribe:
                        break;
                }
            }
        }
    }
}