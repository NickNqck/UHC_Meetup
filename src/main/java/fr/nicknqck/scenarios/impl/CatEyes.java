package fr.nicknqck.scenarios.impl;

import fr.nicknqck.Main;
import fr.nicknqck.events.custom.GameStartEvent;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.scenarios.BasicScenarios;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CatEyes extends BasicScenarios implements Listener {

    private boolean isActivated = false;

    @Override
    public String getName() {
        return "§r§fCat Eyes";
    }

    @Override
    public ItemStack getAffichedItem() {
        return new ItemBuilder(Material.EYE_OF_ENDER).setName(getName()).setLore("§r§fLe "+getName()+" est actuellement: "+(isActivated ? "§aActivé" : "§cDésactivé"),"", AllDesc.tab+"§7 Au lancement de la partie donne l'effet§9 Night Vision I§7 à tout les joueurs").toItemStack();
    }

    @Override
    public void onClick(Player player) {
        isActivated = !isActivated;
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }
    @EventHandler
    private void onStartGame(GameStartEvent e){
        System.out.println("game started for CatEyes");
        if (isActivated){
            e.getInGamePlayers().stream().filter(u -> Bukkit.getPlayer(u) != null).forEach(u -> Bukkit.getPlayer(u).addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false), true));
        }
    }
}
