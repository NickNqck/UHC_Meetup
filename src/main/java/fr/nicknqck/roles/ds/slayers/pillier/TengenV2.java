package fr.nicknqck.roles.ds.slayers.pillier;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.utils.AttackUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TengenV2 extends PillierRoles {

    private TextComponent desc;

    public TengenV2(UUID player) {
        super(player);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.FOUDRE;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Tengen";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.Tengen;
    }

    @Override
    public void resetCooldown() {
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public TextComponent getComponent() {
        return desc;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false), EffectWhen.PERMANENT);
        addPower(new AttaqueFurtive(this), true);
        addPower(new Kunai(this), true);
        AutomaticDesc automaticDesc = new AutomaticDesc(this).addEffects(getEffects()).setPowers(getPowers());
        this.desc = automaticDesc.getText();
    }
    private static class AttaqueFurtive extends ItemPower implements Listener {

        private final HashMap<Integer, ItemStack> armorContents = new HashMap<>();
        private boolean invisible = false;

        protected AttaqueFurtive(@NonNull RoleBase role) {
            super("§eSoufle du son", new Cooldown(60*5), new ItemBuilder(Material.FEATHER).setName("§eSoufle du son"), role,
                    "§7Vous met §cinvisible§7 et vous donne§e Speed III§7 pendant§c 60 secondes§7, le premier §cjoueur§7 que vous frapperez subira ses§c dégats§7 multiplié par§c 1,5");
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                player.sendMessage("§7Activation du "+getName());
                player.setSprinting(false);
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20*60, 0, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED , 20*60, 2, false, false), true);
                if (player.getInventory().getHelmet() != null) {
                    armorContents.put(1, player.getInventory().getHelmet());
                    player.getInventory().setHelmet(null);
                }
                if (player.getInventory().getChestplate() != null) {
                    armorContents.put(2, player.getInventory().getChestplate());
                    player.getInventory().setChestplate(null);
                }
                if (player.getInventory().getLeggings() != null) {
                    armorContents.put(3, player.getInventory().getLeggings());
                    player.getInventory().setLeggings(null);
                }
                if (player.getInventory().getBoots() != null) {
                    armorContents.put(4, player.getInventory().getBoots());
                    player.getInventory().setBoots(null);
                }
                AttackUtils.CantAttack.add(player.getUniqueId());
                AttackUtils.CantReceveAttack.add(player.getUniqueId());
                invisible = true;
                Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> {
                    if (invisible) {
                        removeInvisibility();
                    }
                }, 20*60);
                getCooldown().addSeconds(60);
                return true;
            }
            return false;
        }
        private void removeInvisibility() {
            Player owner = Bukkit.getPlayer(getRole().getPlayer());
            if (owner == null)return;
            AttackUtils.CantAttack.remove(owner.getUniqueId());
            AttackUtils.CantReceveAttack.remove(owner.getUniqueId());
            owner.sendMessage("§cVous n'êtes plus invisible.");
            owner.removePotionEffect(PotionEffectType.INVISIBILITY);
            if (armorContents.get(1) != null) {
                owner.getInventory().setHelmet(armorContents.get(1));
            }
            if (armorContents.get(2) != null) {
                owner.getInventory().setChestplate(armorContents.get(2));
            }
            if (armorContents.get(3) != null) {
                owner.getInventory().setLeggings(armorContents.get(3));
            }
            if (armorContents.get(4) != null) {
                owner.getInventory().setBoots(armorContents.get(4));
            }
            invisible = false;
            Bukkit.getScheduler().runTask(getPlugin(), () -> owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60, 0, false, false), true));

        }
        @EventHandler
        private void onSprint(PlayerToggleSprintEvent event) {
            if (event.getPlayer().getUniqueId().equals(getRole().getPlayer())) {
                if (getCooldown().getCooldownRemaining() >= 60*5) {
                    event.setCancelled(true);
                }
            }
        }
        @EventHandler
        private void onDamageByEntity(EntityDamageByEntityEvent event) {
            if (event.getDamager().getUniqueId().equals(getRole().getPlayer()) && event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
                if (getCooldown().getCooldownRemaining() >= 60*5) {
                    removeInvisibility();
                    event.setDamage(event.getDamage()*1.5);
                }
            }
        }
    }
    private static class Kunai extends ItemPower implements Listener {

        protected Kunai(@NonNull RoleBase role) {
            super("§cKunai empoisonné", new Cooldown(60), new ItemBuilder(Material.SNOW_BALL).setName("§cKunai empoisonné"), role, "coucou");
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            return false;
        }
        @EventHandler
        private void onDamage(EntityDamageByEntityEvent event) {
            if (event.getDamager() instanceof Snowball) {
                if (((Snowball) event.getDamager()).getShooter() instanceof Player && event.getEntity() instanceof Player) {
                    Player shooter = (Player) ((Snowball) event.getDamager()).getShooter();
                    if (shooter.getUniqueId().equals(getRole().getPlayer())) {
                        if (Main.RANDOM.nextInt(100) <= 25) {
                            ((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*6, 0, false, false), true);
                            shooter.sendMessage("§c"+event.getEntity().getName()+"§7 à bien reçus §c6 secondes§7 de§2 Poison I");
                        } else {
                            shooter.sendMessage("§7Le§2 Poison§7 n'a pas atteind §c"+event.getEntity().getName()+"§7.");
                        }
                    }
                }
            }
        }
        @EventHandler
        private void onProjectileLaunch(ProjectileLaunchEvent event) {
            if (event.getEntity() instanceof Snowball) {
                if (event.getEntity().getShooter() instanceof Player) {
                    if (((Player) event.getEntity().getShooter()).getUniqueId().equals(getRole().getPlayer())) {
                        ((Player) event.getEntity().getShooter()).getInventory().addItem(getItem());
                        if (getCooldown().isInCooldown()) {
                            event.setCancelled(true);
                            return;
                        }
                        getCooldown().use();
                    }
                }
            }
        }
    }
}