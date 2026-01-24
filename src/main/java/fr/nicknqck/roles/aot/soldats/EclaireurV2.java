package fr.nicknqck.roles.aot.soldats;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.roles.aot.builders.SoldatsRoles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import hm.zelha.particlesfx.particles.ParticleDustColored;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EclaireurV2 extends SoldatsRoles {

    public EclaireurV2(UUID player) {
        super(player);
    }

    @Override
    public String getName() {
        return "Eclaireur";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Eclaireur;
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new Eclairage(this), true);
        super.RoleGiven(gameState);
    }
    private static final class Eclairage extends ItemPower implements Listener {

        private final EclairageRunnable runnable;

        public Eclairage(@NonNull RoleBase role) {
            super("§aEclairage !§r", new Cooldown(60*6), new ItemBuilder(Material.NETHER_STAR).setName("§aEclairage !"), role,
                    "§7Vous offre§c 3 minutes§7 d'§cinvisibilité§7 tout-en cachant",
                    "§7votre§c armure§7 auprès des§c autres joueurs§7.",
                    "",
                    "§8 -§7 En réutilisant votre§c item§7, vous serez de nouveau§a visible§7.",
                    "§8 -§7 Si vous vous§c faites attaquer§7 ou qu'on§c vous attaque§7,",
                    "§7vous deviendrez à nouveau§a visible§7.",
                    "§8 -§7 Lors de vos§a eclairages§7, les autres joueurs ne",
                    "§7verront pas les objets que vous avez en main.");
            this.runnable = new EclairageRunnable(this);
            setWorkWhenInCooldown(true);
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getCooldown().isInCooldown() && getCooldown().getCooldownRemaining() <= 60*3) {
                sendCooldown(player);
                return false;
            }
            if (this.runnable.running) {
                player.sendMessage(Main.getInstance().getNAME()+"§7 Vous avez annuler votre opération d'§aEclairage !");
                this.runnable.stop();
                this.runnable.cancel();
            } else {
                player.sendMessage(Main.getInstance().getNAME()+"§7 Vous commencez votre§a Eclairage !");
                this.runnable.updateEquipmentForObservers(player, true);
                this.runnable.start();
                return true;
            }
            return false;
        }
        @EventHandler
        private void onBaston(@NonNull final EntityDamageByEntityEvent event) {
            if (!this.runnable.running)return;
            if (event.getEntity().getUniqueId().equals(getRole().getPlayer()) && this.runnable.running && event.getDamager() instanceof Player) {
                this.runnable.stop();
                this.runnable.cancel();
                event.getEntity().sendMessage(Main.getInstance().getNAME()+"§7 Vous avez annuler votre opération d'§aEclairage !");
                this.runnable.updateEquipmentForObservers((Player) event.getEntity(), false);
            }
            if (event.getDamager().getUniqueId().equals(getRole().getPlayer()) && this.runnable.running && event.getEntity() instanceof Player) {
                this.runnable.stop();
                this.runnable.cancel();
                event.getDamager().sendMessage(Main.getInstance().getNAME()+"§7 Vous avez annuler votre opération d'§aEclairage !");
                this.runnable.updateEquipmentForObservers((Player) event.getDamager(), false);
            }
        }
        // Quand le joueur pose un bloc
        @EventHandler
        public void onBlockPlace(BlockPlaceEvent e) {
            if (!runnable.running)return;
            if (e.getPlayer().getUniqueId().equals(getRole().getPlayer())) {
                // On force le renvoi du paquet "invisible" 1 tick après pour écraser la mise à jour du serveur
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> this.runnable.updateEquipmentForObservers(e.getPlayer(), true), 1);
            }
        }

        // Quand le joueur change d'item dans sa barre d'inventaire
        @EventHandler
        public void onItemSwitch(PlayerItemHeldEvent e) {
            if (!runnable.running)return;
            if (e.getPlayer().getUniqueId().equals(getRole().getPlayer())) {
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> this.runnable.updateEquipmentForObservers(e.getPlayer(), true), 1);
            }
        }

        // Quand le joueur fait un clic (droit ou gauche), cela peut déclencher une animation de main
        @EventHandler
        public void onInteract(PlayerInteractEvent e) {
            if (!runnable.running)return;
            if (e.getPlayer().getUniqueId().equals(getRole().getPlayer())) {
                // On ne le fait que si ce n'est pas un bloc physique (car BlockPlace le gère déjà)
                if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_AIR) {
                    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> this.runnable.updateEquipmentForObservers(e.getPlayer(), true), 1);
                }
            }
        }
        private static final class EclairageRunnable extends BukkitRunnable {

            private final Eclairage eclaireage;
            private boolean running = false;
            private int actualTime = 0;

            private EclairageRunnable(Eclairage eclaireage) {
                this.eclaireage = eclaireage;
            }


            @Override
            public void run() {
                if (!GameState.inGame() || !running) {
                    cancel();
                    return;
                }
                this.eclaireage.getRole().getGamePlayer().getActionBarManager().updateActionBar("eclaireur.eclairage", "§bTemps d'éclairage restant:§c "+ StringUtils.secondsTowardsBeautiful(this.actualTime));
                if (this.actualTime <=0) {
                    Bukkit.getScheduler().runTask(this.eclaireage.getPlugin(), () -> {
                        final Player player = Bukkit.getPlayer(this.eclaireage.getRole().getPlayer());
                        if (player != null) {
                            updateEquipmentForObservers(player, false);
                        }
                    });
                    stop();
                    cancel();
                    return;
                }
                this.actualTime--;
                final List<Player> list = new ArrayList<>(this.eclaireage.getRole().getListPlayerFromRole(EclaireurV2.class));
                if (!list.isEmpty()) {
                    for (@NonNull Player player : list) {
                        new ParticleDustColored().displayForPlayers(this.eclaireage.getRole().getGamePlayer().getLastLocation(), player);
                    }
                }
                Bukkit.getScheduler().runTask(this.eclaireage.getPlugin(), () -> {
                    this.eclaireage.getRole().givePotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 0, false, false), EffectWhen.NOW);
                    final Player player = Bukkit.getPlayer(this.eclaireage.getRole().getPlayer());
                    if (player != null) {
                        updateEquipmentForObservers(player, true);
                    }
                });
            }
            /**
             * Envoie les paquets d'équipement aux autres joueurs.
             * @param hide Si true, envoie de l'air (invisible). Si false, envoie le vrai item.
             */
            private void updateEquipmentForObservers(@NonNull final Player target, final boolean hide) {
                int entityId = target.getEntityId();

                // Préparation des items NMS
                // Slot 0 = Main, 1 = Bottes, 2 = Jambières, 3 = Plastron, 4 = Casque
                ItemStack hand = hide ? null : CraftItemStack.asNMSCopy(target.getInventory().getItemInHand());
                ItemStack boots = hide ? null : CraftItemStack.asNMSCopy(target.getInventory().getBoots());
                ItemStack legs = hide ? null : CraftItemStack.asNMSCopy(target.getInventory().getLeggings());
                ItemStack chest = hide ? null : CraftItemStack.asNMSCopy(target.getInventory().getChestplate());
                ItemStack helmet = hide ? null : CraftItemStack.asNMSCopy(target.getInventory().getHelmet());

                // Création des 5 paquets
                PacketPlayOutEntityEquipment pHand = new PacketPlayOutEntityEquipment(entityId, 0, hand);
                PacketPlayOutEntityEquipment pBoots = new PacketPlayOutEntityEquipment(entityId, 1, boots);
                PacketPlayOutEntityEquipment pLegs = new PacketPlayOutEntityEquipment(entityId, 2, legs);
                PacketPlayOutEntityEquipment pChest = new PacketPlayOutEntityEquipment(entityId, 3, chest);
                PacketPlayOutEntityEquipment pHelmet = new PacketPlayOutEntityEquipment(entityId, 4, helmet);

                // Envoi aux observateurs uniquement
                for (Player observer : target.getWorld().getPlayers()) {
                    // Le joueur ciblé doit continuer de voir son propre inventaire normalement
                    if (observer.getUniqueId().equals(target.getUniqueId())) continue;

                    CraftPlayer cpObserver = (CraftPlayer) observer;
                    cpObserver.getHandle().playerConnection.sendPacket(pHand);   // Main
                    cpObserver.getHandle().playerConnection.sendPacket(pBoots);  // Bottes
                    cpObserver.getHandle().playerConnection.sendPacket(pLegs);   // Jambières
                    cpObserver.getHandle().playerConnection.sendPacket(pChest);  // Plastron
                    cpObserver.getHandle().playerConnection.sendPacket(pHelmet); // Casque
                }

            }
            private synchronized void start() {
                this.actualTime = 60*3;
                this.running = true;
                runTaskTimerAsynchronously(this.eclaireage.getPlugin(), 0, 20);
            }
            private synchronized void stop() {
                this.actualTime = 0;
                this.running = false;
                this.eclaireage.getCooldown().setActualCooldown(60*3);
                this.eclaireage.getRole().getGamePlayer().getActionBarManager().removeInActionBar("eclaireur.eclairage");
            }
        }
    }
}