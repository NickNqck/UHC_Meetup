package fr.nicknqck.scenarios.impl;

import fr.nicknqck.Main;
import fr.nicknqck.scenarios.BasicScenarios;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class BowSwap extends BasicScenarios implements Listener {

    private boolean isActivated = false;
    private int Percent = 0;
    public BowSwap(){
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }
    @Override
    public String getName() {
        return "§r§fBowSwap";
    }

    @Override
    public ItemStack getAffichedItem() {
        return new ItemBuilder(Material.BOW).setLore("§r§fLe "+getName()+" est actuellement: "+ (isActivated ? "§aActivé" : "§cDésactivé"),"§r§fLe pourcentage de proc du "+getName()+"§b "+Percent+"%").setName(getName()).toItemStack();
    }

    @Override
    public void onClick(Player player) {
        if (isClickGauche()){
            isActivated = true;
        }
        if (isClickDroit()){
            isActivated = false;
        }
        if (isShiftClick() && Percent < 100){
            Percent++;
        }
        if (isDropClick() && Percent > 0){
            Percent--;
        }
    }
    @EventHandler
    private void onShoot(EntityDamageByEntityEvent e){
        if (e.getDamager() instanceof Arrow && Percent > 0 && isActivated && e.getEntity() instanceof Player && ((Arrow) e.getDamager()).getShooter() instanceof Player && !e.getEntity().getUniqueId().equals(((Player) ((Arrow) e.getDamager()).getShooter()).getUniqueId())){
            if (RandomUtils.getOwnRandomProbability(Percent)){
                Location loc1 = e.getEntity().getLocation();
                Location loc2 = ((Player) ((Arrow) e.getDamager()).getShooter()).getLocation();
                e.getEntity().teleport(loc2);
                ((Player) ((Arrow) e.getDamager()).getShooter()).teleport(loc1);
                ((Player) ((Arrow) e.getDamager()).getShooter()).sendMessage("§7[§cBowSwap§7] §aVous avez échanger votre place avec §9"+((Player) e.getEntity()).getDisplayName());
                e.getEntity().sendMessage("§7[§cBowSwap§7] §aVous avez échanger votre place avec §9"+((Player) ((Arrow) e.getDamager()).getShooter()).getDisplayName());
            }
        }
    }
}