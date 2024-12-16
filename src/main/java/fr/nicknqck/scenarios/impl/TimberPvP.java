package fr.nicknqck.scenarios.impl;

import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.scenarios.BasicScenarios;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class TimberPvP extends BasicScenarios implements Listener {

    private boolean isActivated = false;
    @Override
    public String getName() {
        return "§f§rTimberPvP";
    }

    @Override
    public ItemStack getAffichedItem() {
        return new ItemBuilder(Material.LOG).setName(getName()).setLore("§r§fLe "+getName()+" est actuellement: "+(isActivated ? "§aActivé" : "§cDésactivé"),"", AllDesc.tab+"§7 Permet de casser des arbres plus facilement").toItemStack();
    }

    @Override
    public void onClick(Player player) {
        if (isClickGauche()){
            isActivated = true;
        }
        if (isClickDroit()){
            isActivated = false;
        }
    }

}
