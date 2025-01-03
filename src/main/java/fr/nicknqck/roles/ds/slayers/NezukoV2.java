package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NezukoV2 extends DemonsRoles {

    private TextComponent textComponent;
    private boolean pauseForce = false;

    public NezukoV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull DemonType getRank() {
        return DemonType.NEZUKO;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Nezuko";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Nezuko;
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Slayer;
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
        return this.textComponent;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        getKnowedRoles().add(Tanjiro.class);
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.NIGHT);
        addPower(new FormePower(this),true);
        addPower(new SangPower(this), true);
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> new TanjiroRunnable(getListGamePlayerFromRole(Tanjiro.class), this).runTaskTimerAsynchronously(Main.getInstance(), 0, 20), 20);
        AutomaticDesc desc = new AutomaticDesc(this).setPowers(getPowers()).addEffects(getEffects());
        this.textComponent = desc.getText();
    }
    private static class SangPower extends ItemPower implements Listener {

        protected SangPower(@NonNull RoleBase role) {
            super("§cSang Démoniaque", new Cooldown(60*10), new ItemBuilder(Material.NETHER_STAR).setName("§cSang Démoniaque"), role);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                player.sendMessage("§7Votre§4 sang§7 commence à§c bouilloner");
                EventUtils.registerEvents(this);
                Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> {
                    player.sendMessage("§7Votre§4 sang§7 est à nouveau tiède");
                    EventUtils.unregisterEvents(this);
                }, 20*30);
                return true;
            }
            return false;
        }
        @EventHandler
        private void onBattle(EntityDamageByEntityEvent event) {
            if (event.getEntity().getUniqueId().equals(getRole().getPlayer())) {
                event.getDamager().setFireTicks(200);
            }
        }
    }
    private static class FormePower extends ItemPower implements Listener {

        private final NezukoV2 nezukoV2;

        protected FormePower(@NonNull NezukoV2 role) {
            super("§cForme Démoniaque", new Cooldown(60*20), new ItemBuilder(Material.NETHER_STAR).setName("§cForme Démoniaque"), role);
            setMaxUse(2);
            EventUtils.registerRoleEvent(this);
            this.nezukoV2 = role;
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*5, 0, false, false));
                getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> {
                    getCooldown().addSeconds(60*5);
                    nezukoV2.pauseForce = true;
                    nezukoV2.getEffects().remove(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.NIGHT);
                    Bukkit.getScheduler().runTask(getPlugin(), () -> {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Main.getInstance().getGameConfig().isMinage() ? 20*60*5 : 20*60*3, 0, false, false));
                        Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> {
                            nezukoV2.pauseForce = false;
                            nezukoV2.givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.NIGHT);
                        }, Main.getInstance().getGameConfig().isMinage() ? 20*60*10 : 20*60*5);
                    });
                }, 20*60*5);
                return true;
            }
            return false;
        }
        @EventHandler
        private void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
            if (event.getEntity().getUniqueId().equals(getRole().getPlayer()) && event.getDamager()instanceof Player && event.getEntity() instanceof Player) {
                if (Loc.getPlayerFacing((Player) event.getDamager()).equals(Loc.getPlayerFacing((Player) event.getEntity()))) {
                    if (!getCooldown().isInCooldown())return;
                    if (getCooldown().getCooldownRemaining() <= 60*20)return;
                    event.getEntity().sendMessage("§7Vous avez gagner§c 10%§7 de§e Speed");
                    getRole().addSpeedAtInt((Player) event.getEntity(), 10);
                    Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                        getRole().addSpeedAtInt((Player) event.getEntity(), -10);
                        event.getEntity().sendMessage("§7Vous avez perdu§c 10%§7 de§e Speed");
                    }, 60);
                }
            }
        }
        @EventHandler
        private void onEat(PlayerItemConsumeEvent event) {
            if (event.getItem().getType().equals(Material.GOLDEN_APPLE) && event.getPlayer().getUniqueId().equals(getRole().getPlayer())) {
                if (!getCooldown().isInCooldown())return;
                if (getCooldown().getCooldownRemaining() <= 60*20)return;
                event.getPlayer().sendMessage("§7Vous avez gagner§c 5%§7 de§9 Résistance");
                getRole().addBonusResi(5.0);
                Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> {
                    event.getPlayer().sendMessage("§7Vous avez perdu§c 5%§7 de§9 Résistance");
                   getRole().addBonusResi(-5.0);
                }, 100);
            }
        }
    }
    private static class TanjiroRunnable extends BukkitRunnable {

        private final List<GamePlayer> tanjiros;
        private final NezukoV2 nezukoV2;
        private int timeHeal = 30;
        private boolean does = false;
        public TanjiroRunnable(List<GamePlayer> listPlayerFromRole, NezukoV2 nezukoV2) {
            this.nezukoV2 = nezukoV2;
            this.tanjiros = new ArrayList<>();
            if (listPlayerFromRole.isEmpty())return;
            this.tanjiros.addAll(listPlayerFromRole);
        }

        @Override
        public void run() {
            if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (tanjiros.isEmpty()) {
                Player owner = Bukkit.getPlayer(nezukoV2.getPlayer());
                if (owner != null) {
                    owner.sendMessage("§cDésolé,§a Tanjiro§c n'est pas dans la partie.");
                }
                cancel();
                return;
            }
            Player owner = Bukkit.getPlayer(nezukoV2.getPlayer());
            if (owner == null)return;
            for (final GamePlayer gamePlayer : tanjiros) {
                if (!gamePlayer.isAlive())continue;
                if (gamePlayer.getLastLocation().distance(owner.getLocation()) <= 20) {
                    if (!nezukoV2.pauseForce){
                        Bukkit.getScheduler().runTask(Main.getInstance(), () -> owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), true));
                    }
                    if (!does) {
                        timeHeal--;
                        does = true;
                    }
                    if (timeHeal == 0) {
                        timeHeal = 30;
                        owner.setHealth(Math.min(owner.getMaxHealth(), owner.getHealth()+1.0));
                    }
                }
            }
            does = false;
        }
    }
}
