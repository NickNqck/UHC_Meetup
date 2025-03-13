package fr.nicknqck.events.ds.dkt;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.utils.WorldUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class DemonKingTanjiroRole extends DemonsRoles implements Listener {
    private final ItemStack energieItem = new ItemBuilder(Material.NETHER_STAR).setName("§r§f§lBoule d'énergie").setUnbreakable(true).setDroppable(false).toItemStack();
    private int cdEnergie;
    public DemonKingTanjiroRole(UUID player) {
        super(player);
        owner.getInventory().remove(Material.BLAZE_ROD);
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        setNoFall(true);
        giveItem(owner, false, getItems());
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public String getName() {
        return "Tanjiro§7 (§cRoi des démons§7)";
    }

    @Override
    public GameState.@NonNull Roles getRoles() {
        return GameState.Roles.Tanjiro;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Demon;
    }

    @Override
    public void resetCooldown() {
        cdEnergie = 0;
    }

    @Override
    public String[] Desc() {
        Main.getInstance().getGetterList().getDemonList(owner);
        return new String[] {

        };
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[] {
                this.energieItem
        };
    }

    @Override
    public @NonNull DemonType getRank() {
        return DemonType.SUPERIEUR;
    }
    @EventHandler
    private void onEndGame(EndGameEvent event) {
        HandlerList.unregisterAll(this);
    }
    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getDamager().getUniqueId().equals(getPlayer())) {
            if (event.getEntity() instanceof Player) {
                if (((Player) event.getDamager()).getItemInHand() != null && ((Player) event.getDamager()).getItemInHand().isSimilar(this.energieItem)) {
                    if (cdEnergie > 1) {
                        sendCooldown((Player) event.getDamager(), cdEnergie);
                        return;
                    }
                    WorldUtils.createBeautyExplosion(event.getEntity().getLocation(), 3, true);
                    Heal(((Player) event.getEntity()), -2);
                    event.getDamager().sendMessage("§7Vous avez touchez: §c"+ event.getEntity().getName()+"§7 avec votre "+((Player) event.getDamager()).getItemInHand().getItemMeta().getDisplayName());
                    event.getEntity().sendMessage("§7Vous avez été toucher par la "+((Player) event.getDamager()).getItemInHand().getItemMeta().getDisplayName()+"§7 de§c "+getName());
                    cdEnergie = 120;
                }
            }
        }
    }
}