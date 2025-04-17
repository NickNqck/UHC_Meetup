package fr.nicknqck.roles.ns.akatsuki;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.AkatsukiRoles;
import fr.nicknqck.roles.ns.builders.EUchiwaType;
import fr.nicknqck.roles.ns.builders.IUchiwa;
import fr.nicknqck.roles.ns.power.Amaterasu;
import fr.nicknqck.roles.ns.power.Genjutsu;
import fr.nicknqck.roles.ns.power.Izanagi;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class ItachiV2 extends AkatsukiRoles implements IUchiwa {

    public ItachiV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.GENIE;
    }

    @Override
    public String getName() {
        return "Itachi";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Itachi;
    }

    @Override
    public @NonNull EUchiwaType getUchiwaType() {
        return EUchiwaType.IMPORTANT;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        addPower(new SusanoPower(this), true);
        addPower(new Genjutsu(this), true);
        addPower(new Amaterasu(this), true);
        addPower(new Izanagi(this));
        setChakraType(Chakras.KATON);
        addKnowedRole(KisameV2.class);
        super.RoleGiven(gameState);
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .getText();
    }

    private static class SusanoPower extends ItemPower {

        private final TotsukaPower totsuka;

        protected SusanoPower(@NonNull RoleBase role) {
            super("Susano (Itachi)", new Cooldown(60*20), new ItemBuilder(Material.NETHER_STAR).setName("§c§lSusanô"), role,
                    "§7Vous permet d'obtenir l'effet§c Résistance I§7 pendant§c 5 minutes",
                    "§7Vous recevrez également une épée nommé§c Totsuka§7 enchanté§c Tranchant VII§7 utilisable toute les§c 10 secondes§7. (1x/20m)");
            this.totsuka = new TotsukaPower(role);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                getRole().addPower(this.totsuka, true);
                new SusanoRunnable(this.getRole().getGameState(), this.getRole().getGamePlayer(), this);
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60*5, 0, false, false), EffectWhen.NOW);
                player.sendMessage("§cActivation du§l Susanô§c.");
                return true;
            }
            return false;
        }
        private static class SusanoRunnable extends BukkitRunnable {

            private final GameState gameState;
            private final GamePlayer gamePlayer;
            private final SusanoPower susanoPower;
            private int timeLeft = 60*5;

            private SusanoRunnable(GameState gameState, GamePlayer gamePlayer, SusanoPower susanoPower) {
                this.gameState = gameState;
                this.gamePlayer = gamePlayer;
                this.susanoPower = susanoPower;
                this.gamePlayer.getActionBarManager().addToActionBar("itachi.susano", "§bTemp restant du§c§l Susanô§b: "+ StringUtils.secondsTowardsBeautiful(this.timeLeft));
                runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (this.timeLeft <= 0) {
                    this.gamePlayer.getActionBarManager().removeInActionBar("itachi.susano");
                    this.gamePlayer.sendMessage("§cVotre§l Susanô§c s'arrête");
                    this.gamePlayer.getRole().getPowers().remove(this.susanoPower.totsuka);
                    Player player = Bukkit.getPlayer(this.gamePlayer.getUuid());
                    if (player != null) {
                        player.getInventory().remove(this.susanoPower.totsuka.getItem());
                    }
                    EventUtils.unregisterEvents(this.susanoPower.totsuka);
                    cancel();
                    return;
                }
                this.timeLeft--;
                this.gamePlayer.getActionBarManager().updateActionBar("itachi.susano", "§bTemp restant du§c§l Susanô§b: "+StringUtils.secondsTowardsBeautiful(this.timeLeft));
            }
        }
        private static class TotsukaPower extends ItemPower implements Listener {

            protected TotsukaPower(@NonNull RoleBase role) {
                super("Totsuka", new Cooldown(10), new ItemBuilder(Material.DIAMOND_SWORD).setName("§cTotsuka").addEnchant(Enchantment.DAMAGE_ALL, 7), role);
                EventUtils.registerRoleEvent(this);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                return getInteractType().equals(InteractType.ATTACK_ENTITY);
            }
            @EventHandler
            private void EntityDamageEvent(@NonNull final EntityDamageByEntityEvent event) {
                if (event.isCancelled())return;
                if (!(event.getDamager() instanceof Player))return;
                if (!event.getDamager().getUniqueId().equals(getRole().getPlayer()))return;
                if (((Player) event.getDamager()).getItemInHand().isSimilar(getItem())) {
                    if (getCooldown().isInCooldown()) {
                        event.setDamage(0.0);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}