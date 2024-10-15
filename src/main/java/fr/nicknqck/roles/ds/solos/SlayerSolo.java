package fr.nicknqck.roles.ds.solos;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonsSlayersRoles;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.UUID;

public class SlayerSolo extends DemonsSlayersRoles {

    public SlayerSolo(UUID player) {
        super(player);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new EauPower(this), true);
        addPower(new VentPower(this), true);
        addPower(new FoudrePower(this), true);
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Pourfendeur Solitaire";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.SlayerSolo;
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Solo;
    }

    @Override
    public void resetCooldown() {

    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }
    private static class FoudrePower extends ItemPower implements Listener {

        private boolean using = false;
        private boolean used = false;

        protected FoudrePower(RoleBase role) {
            super("§eSoufle de la Foudre", new Cooldown(60*5), new ItemBuilder(Material.GLOWSTONE_DUST).setName("§eSoufle de la Foudre"), role);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                this.using = true;
                player.sendMessage("§7Votre§e Foudre§7 est prête, vous avez maintenant§c 60 secondes§7 pour l'utiliser sur un§c joueur§7.");
                Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
                    if (!used) {
                        this.using = false;
                        player.sendMessage("§7Il est trop tard, vous ne ressentez plus la§e Foudre§7 dans votre corp.");
                        EventUtils.unregisterEvents(this);
                    }
                }, 20*60);
                EventUtils.registerEvents(this);
                return true;
            }
            return false;
        }
        @EventHandler
        private void onDamage(UHCPlayerBattleEvent event) {
            if (!event.getDamager().getUuid().equals(getRole().getPlayer()))return;
            if (this.using) {
                Player victim = event.getVictim().getRole().owner;
                victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*15, 0, false, false));
                if (victim.getHealth() - 4.0 <= 0.0) {
                     victim.damage(9999.0, event.getOriginEvent().getDamager());
                } else {
                    victim.setHealth(victim.getHealth()-4.0);
                    victim.damage(0.0);
                }
                victim.sendMessage("§7Vous subissez une§e foudre§c très puissante§7.");
                this.used = true;
                EventUtils.unregisterEvents(this);
            }
        }
    }
    private static class VentPower extends ItemPower implements Listener{

        protected VentPower(RoleBase role) {
            super("§aSoufle du vent", new Cooldown(60*5), new ItemBuilder(Material.FEATHER).setName("§aSoufle du Vent"), role);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*120, 1, false, false), true);
                player.sendMessage("§7Vous activez votre §aSoufle du Vent");
                EventUtils.registerEvents(this);
                Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> {
                    assert player.isOnline();
                    player.sendMessage("§7Votre§a Soufle du Vent§7 est maintenant désactiver");
                    EventUtils.unregisterEvents(this);
                },20*120);
                return true;
            }
            return false;
        }
        @EventHandler
        private void onFall(EntityDamageEvent event) {
            if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                if (event.getEntity().getUniqueId().equals(getRole().getPlayer())) {
                    event.setCancelled(true);
                }
            }
        }
    }
    private static class EauPower extends ItemPower implements Listener {

        protected EauPower(RoleBase role) {
            super("§bSoufle de l'Eau", new Cooldown(60*5), new ItemBuilder(Material.NETHER_STAR).setName("§bSoufle de l'Eau"), role);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                player.sendMessage("§7Vous activez le§b Soufle de l'Eau");
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*180, 0, false, false));
                ItemStack boots = player.getInventory().getBoots();
                if (boots != null) {
                    ItemMeta mBoots = boots.getItemMeta();
                    if (mBoots != null) {
                        mBoots.addEnchant(Enchantment.DEPTH_STRIDER, 3, true);
                        boots.setItemMeta(mBoots);
                        player.getInventory().setBoots(boots);
                        Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> {
                            ItemStack boot = player.getInventory().getBoots();
                            if (boot != null) {
                                ItemMeta mBoot = boot.getItemMeta();
                                if (mBoot == null)return;
                                if (mBoot.hasEnchant(Enchantment.DEPTH_STRIDER)) {
                                    mBoot.removeEnchant(Enchantment.DEPTH_STRIDER);
                                    boot.setItemMeta(mBoot);
                                    player.getInventory().setBoots(boot);
                                    player.sendMessage("§7Votre§b Soufle de l'Eau§7 ne fais plus effet");
                                }
                            }
                        }, 20*180);
                    }
                }
                return true;
            }
            return false;
        }
        @EventHandler
        private void onDie(UHCDeathEvent event) {
            if (event.getRole() == null)return;
            if (event.getRole().getPowers().contains(this)) {
                if (event.getPlayer().getUniqueId().equals(getRole().getPlayer())) {
                    ItemStack boots = event.getPlayer().getInventory().getBoots();
                    if (boots != null) {
                        if (!boots.hasItemMeta())return;
                        if (boots.getItemMeta().hasEnchant(Enchantment.DEPTH_STRIDER)) {
                            boots.getItemMeta().removeEnchant(Enchantment.DEPTH_STRIDER);
                        }
                    }
                }
            }
        }
    }

}
