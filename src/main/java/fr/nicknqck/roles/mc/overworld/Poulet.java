package fr.nicknqck.roles.mc.overworld;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.mc.builders.OverWorldRoles;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Poulet extends OverWorldRoles {

    private final ItemStack plumeItem = new ItemBuilder(Material.FEATHER).addEnchant(Enchantment.ARROW_DAMAGE, 4).setLore("§7Permet de voler pendant 3 secondes").setName("§aPlume").toItemStack();
    private int cdplume = 0;
    public Poulet(Player player) {
        super(player);
        owner.sendMessage(Desc());
        giveItem(owner,false,getItems());
    }
    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.Poulet;
    }
    @Override
    public String[] Desc() {
        return new String[]{
                AllDesc.bar,
                AllDesc.role+"§aPoulet",
                AllDesc.objectifteam+"§aOverWorld",
                "",
                AllDesc.items,
                "",
                AllDesc.point+"§aPlume :§r A son activation vous permez de voler pendant 3 secondes.§7 (1x/5m)",
                "",
                AllDesc.particularite,
                "",
                "Vous possédez §aNofall",
                "",
                AllDesc.bar
        };
    }

    @Override
    public String getName() {
        return "§aPoulet";
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
        plumeItem,
        };
    }

    @Override
    public void resetCooldown() {
        cdplume = 0;
    }
    @Override
    public void RoleGiven(GameState gameState) {
        setNoFall(true);
        super.RoleGiven(gameState);
    }

    @Override
    public boolean ItemUse(ItemStack item, GameState gameState) {
        if (item.isSimilar(plumeItem)){
            if (cdplume <= 0){
                owner.setAllowFlight(true);
                owner.setFlying(true);
                owner.sendMessage("Vous pouvez désormais voler");
                new BukkitRunnable(){
                private int i = 0;
                    @Override
                    public void run() {
                        if(getIGPlayers().contains(owner)) {
                            i ++;
                            if (i == 4) {
                                owner.sendMessage("Vous ne pouvez plus voler.");
                                owner.setFlying(false);
                                owner.setAllowFlight(false);
                                cdplume = 60*5;
                                cancel();

                            }
                        } else {
                            cancel();
                        }
                    }
                }.runTaskTimer(Main.getInstance(), 0, 20);
            } else {
                sendCooldown(owner, cdplume);
                return true;
            }
            return true;
        }
        return super.ItemUse(item, gameState);
    }

    @Override
    public void Update(GameState gameState) {
        if (cdplume >= 0){
            cdplume --;
            if (cdplume == 0){
                owner.sendMessage("Vous pouvez de nouveau réutilisez votre §aPlume");
            }
        }
        super.Update(gameState);
    }
}
