package fr.nicknqck.roles.ns.solo.zabuza_haku;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.events.custom.roles.PowerActivateEvent;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.NSRoles;
import fr.nicknqck.utils.AttackUtils;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ZabuzaV2 extends NSRoles implements Listener {

    public ZabuzaV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.INTELLIGENT;
    }

    @Override
    public String getName() {
        return "Zabuza";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Zabuza;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Zabuza_et_Haku;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        getGamePlayer().startChatWith("§bZabuza:", "!", Haku.class);
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.PERMANENT);
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false), EffectWhen.PERMANENT);
        EventUtils.registerRoleEvent(this);
        addPower(new InvisibilitePower(this), true);
        addPower(new KubikiribochoPower(this), true);
        setChakraType(Chakras.SUITON);
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if (!gameState.getAttributedRole().contains(GameState.Roles.Haku)) {
                onHakuDeath(false);
                getGamePlayer().sendMessage("§bHaku§7 n'est pas dans la partie, vous récupérez donc le bonus dû à sa mort");
            }
        }, 20*10);
        super.RoleGiven(gameState);
    }
    @EventHandler
    private void onPowerUse(@NonNull final PowerActivateEvent event) {
        if (event.isCancel())return;
        if (event.getPower().getCooldown() != null) {
            if (event.getPower().isCooldownResetSended())return;
        }
        if (event.getPower() instanceof KubikiribochoPower)return;
        String name = event.getPower().getName();
        getGamePlayer().sendMessage("§aLe pouvoir \"§r"+name+"§a\" a été utiliser par§c "+event.getPlayer().getName());
    }
    @EventHandler
    private void uhcDeathEvent(@NonNull final UHCDeathEvent event) {
        if (event.getRole() instanceof Haku) {
            onHakuDeath(true);
        }
    }
    private void onHakuDeath(boolean msg) {
        if (msg) {
            owner.sendMessage("§bHaku§7 est mort, pour vous vengez vous obtenez§c 10 minutes§f de§e Speed 2§7 ainsi que l'effet§9 Résistance 1§7 de manière§c permanente");
        }
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*10, 1, false, false), EffectWhen.NOW);
        givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 1, false, false), EffectWhen.PERMANENT);
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .getText();
    }

    private static class InvisibilitePower extends ItemPower {

        private InvisibiliteRunnable runnable;
        private final ZabuzaV2 zabuza;
        private final HashMap<Integer, ItemStack> armorContents = new HashMap<>();
        private boolean invisible = false;
        private final Cooldown cooldown;

        protected InvisibilitePower(ZabuzaV2 role) {
            super("Invisibilité", null, new ItemBuilder(Material.NETHER_STAR).setName("§aInvisibilité").setLore("§7Vous permez de devenir invisible"), role);
            this.zabuza = role;
            this.cooldown = new Cooldown(60*5);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> args) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (cooldown.isInCooldown()) {
                    zabuza.sendCooldown(player, cooldown.getCooldownRemaining());
                    return false;
                }
                if (this.invisible) {
                    this.runnable.timeLeft = 0;
                    return false;
                }
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20*60*5, 0, false, false), true);
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
                player.sendMessage("§aVous êtes maintenant invisible.");
                AttackUtils.CantAttack.add(player.getUniqueId());
                AttackUtils.CantReceveAttack.add(player.getUniqueId());
                runnable = new InvisibilitePower.InvisibiliteRunnable(this);
                return true;
            }
            return false;
        }

        private void removeInvisibility() {
            Player owner = Bukkit.getPlayer(zabuza.getPlayer());
            if (owner == null)return;
            AttackUtils.CantAttack.remove(owner.getUniqueId());
            AttackUtils.CantReceveAttack.remove(owner.getUniqueId());
            owner.sendMessage("§cVous n'êtes plus invisible.");
            owner.removePotionEffect(PotionEffectType.INVISIBILITY);
            this.runnable.timeLeft = 0;
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
            this.runnable.cancel();
            this.runnable = null;
            cooldown.use();
            getRole().getGamePlayer().getActionBarManager().removeInActionBar("zabuza.invisibilite");
        }

        private static class InvisibiliteRunnable extends BukkitRunnable {

            private final ZabuzaV2 zabuza;
            private int timeLeft = 60*5*20;
            private final GameState gameState;
            private final InvisibilitePower power;
            public InvisibiliteRunnable(InvisibilitePower power) {
                this.zabuza = power.zabuza;
                this.gameState = this.zabuza.getGameState();
                this.power = power;
                runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
                this.power.invisible = true;
                this.power.zabuza.getGamePlayer().getActionBarManager().addToActionBar("zabuza.invisibilite", "§bTemp d'invisibilité:§c 60s");
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (timeLeft <= 0) {
                    this.power.removeInvisibility();
                    this.power.invisible = false;
                    return;
                }
                timeLeft--;
                int toshow = timeLeft/20;
                this.power.zabuza.getGamePlayer().getActionBarManager().updateActionBar("zabuza.invisibilite", "§bTemp d'invisibilité:§c "+(StringUtils.secondsTowardsBeautiful((60*5)-toshow)));
                for (Player p : zabuza.getGamePlayer().getLastLocation().getWorld().getPlayers()) {
                    if (zabuza.getListPlayerFromRole(GameState.Roles.Haku).contains(p) || zabuza.getListPlayerFromRole(GameState.Roles.Zabuza).contains(p)) {
                        MathUtil.sendParticleTo(p, EnumParticle.CLOUD, zabuza.owner.getLocation().clone());
                    }
                }
            }
        }
    }
    private static class KubikiribochoPower extends ItemPower {

        private int coup = 0;

        private KubikiribochoPower(@NonNull RoleBase role) {
            super("Kubikirobocho", null, new ItemBuilder(Material.NETHER_STAR).setName("§bKubikiribôchô").addEnchant(Enchantment.DAMAGE_ALL,4 ), role);
            role.getGamePlayer().getActionBarManager().addToActionBar("zabuza.kubikiribocho", "§7Coups: §c"+this.coup+"§7/§625");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.ATTACK_ENTITY)) {
                final UHCPlayerBattleEvent event = (UHCPlayerBattleEvent) map.get("event");
                if (!event.isPatch())return false;
                if (event.getOriginEvent().isCancelled())return false;
                this.coup++;
                if (this.coup >= 25) {
                    player.setHealth(Math.min(player.getMaxHealth(), player.getHealth()+4.0));
                    this.coup = 0;
                    player.sendMessage("§bKubikiribôchô§7 vous à régénérer");
                }
                this.getRole().getGamePlayer().getActionBarManager().updateActionBar("zabuza.kubikiribocho", "§7Coups: §c"+this.coup+"§7/§625");
                return true;
            }
            return false;
        }
    }
}