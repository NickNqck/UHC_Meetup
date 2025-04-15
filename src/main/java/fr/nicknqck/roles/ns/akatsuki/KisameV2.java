package fr.nicknqck.roles.ns.akatsuki;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.AkatsukiRoles;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class KisameV2 extends AkatsukiRoles {

    public KisameV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.MOYENNE;
    }

    @Override
    public String getName() {
        return "Kisame";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Kisame;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0, false, false), EffectWhen.PERMANENT);
        givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0, false, false), EffectWhen.PERMANENT);
        setChakraType(Chakras.SUITON);
        addPower(new SamehadaSwordPower(this), true);
        addPower(new SuibunPower(this), true);
        final ItemStack Book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta BookMeta = (EnchantmentStorageMeta) Book.getItemMeta();
        BookMeta.addStoredEnchant(Enchantment.DEPTH_STRIDER, 3, false);
        Book.setItemMeta(BookMeta);
        giveItem(owner, false, Book);
        if (!Main.getInstance().getGameConfig().isMinage()){
            owner.setLevel(owner.getLevel()+6);
        }
        addKnowedRole(ItachiV2.class);
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .getText();
    }

    private static class SamehadaSwordPower extends ItemPower {

        private final Map<String, Integer> map = new HashMap<>();

        protected SamehadaSwordPower(@NonNull RoleBase role) {
            super("Samehada", null, new ItemBuilder(Material.DIAMOND_SWORD).setName("§cSamehada").addEnchant(Enchantment.DAMAGE_ALL, 3), role,
                    "§7Tout les§c 25 coups§7 infligé a un joueur, vous lui infligerez§c 2❤ supplémentaire§7 de§c dégâts");
            role.getGamePlayer().getActionBarManager().addToActionBar("kisame.coupremaining", "§7Coup restant: §c0§7/§625");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.ATTACK_ENTITY)) {
                @NonNull final UHCPlayerBattleEvent event = (UHCPlayerBattleEvent) map.get("event");
                if (!event.isPatch())return false;
                if (!event.getDamager().getUuid().equals(getRole().getPlayer()))return false;
                if (!(event.getOriginEvent().getEntity() instanceof Player))return false;
                @NonNull final Player victim = (Player) event.getOriginEvent().getEntity();
                if (this.map.containsKey(victim.getDisplayName())) {
                    int a = this.map.get(victim.getDisplayName());
                    this.map.remove(victim.getDisplayName(), a);
                    a++;
                    this.map.put(victim.getDisplayName(), a);
                    if (a == 25) {
                        victim.setHealth(Math.max(victim.getHealth()-4.0, 1.0));
                        this.map.remove(victim.getDisplayName(), a);
                        a = 0;
                        player.sendMessage("§cSamehada§7 fait effet sur§c "+victim.getDisplayName());
                        victim.sendMessage("§cSamehada fait effet sur vous !");
                    }
                    this.getRole().getGamePlayer().getActionBarManager().updateActionBar("kisame.coupremaining", "§7Coup restant (§b"+victim.getDisplayName()+"§7): §c"+a+"§7/§625");
                    return true;
                } else {
                    int a = 1;
                    this.map.put(victim.getDisplayName(), a);
                    this.getRole().getGamePlayer().getActionBarManager().updateActionBar("kisame.coupremaining", "§7Coup restant (§b"+victim.getDisplayName()+"§7): §c"+a+"§7/§625");
                    return true;
                }
            }
            return false;
        }
    }
    private static class SuibunPower extends ItemPower implements Listener {

        private final List<Block> blockList;

        protected SuibunPower(@NonNull RoleBase role) {
            super("Suibun", new Cooldown(60*10), new ItemBuilder(Material.NETHER_STAR).setName("§bSuibun"), role,
                    "§7Remplace les blocs d'air autours de vous par de l'§beau§7 en téléportant tout les joueurs proche de vous à votre position.",
                    "",
                    "§7L'§beau§7 disparaitra au bout de§c 5 secondes§7 passé hors de l'§beau§7.");
            this.blockList = new ArrayList<>();
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                for(@NonNull final Location loc : new MathUtil().sphere(player.getLocation(), 14, false)) {
                    if (loc.getBlock().getType() == Material.AIR) {
                        this.blockList.add(loc.getBlock());
                        loc.getBlock().setType(Material.STATIONARY_WATER, false);
                    }
                }
                for (@NonNull final Player target : Loc.getNearbyPlayersExcept(player, 15)) {
                    target.teleport(player.getLocation().clone().add(0.0, 1.0, 0.0), PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
                new SuibunRunnable(getRole().getGameState(), getRole().getGamePlayer(), this);
                EventUtils.registerRoleEvent(this);
                return true;
            }
            return false;
        }
        private void stop(SuibunRunnable suibunRunnable) {
            suibunRunnable.cancel();
            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                for (@NonNull final Block block : this.blockList) {
                    block.setType(Material.AIR);
                }
                this.blockList.clear();
            });
            this.getRole().getGamePlayer().sendMessage("§7Votre§b bulle§7 a disparu...");
            EventUtils.unregisterEvents(this);
        }
        @EventHandler
        public void onWaterFlow(BlockFromToEvent event) {
            Material type = event.getBlock().getType();
            if (type == Material.WATER || type == Material.STATIONARY_WATER) {
                if (this.blockList.contains(event.getBlock())){
                    event.setCancelled(true);
                }
            }
        }
        private static class SuibunRunnable extends BukkitRunnable {

            private final GameState gameState;
            private final GamePlayer gamePlayer;
            private final SuibunPower suibun;
            private int timeLeft = 5;

            private SuibunRunnable(GameState gameState, GamePlayer gamePlayer, SuibunPower suibun) {
                this.gameState = gameState;
                this.gamePlayer = gamePlayer;
                this.suibun = suibun;
                runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (!gamePlayer.getLastLocation().getBlock().getType().name().contains("WATER")) {
                    this.gamePlayer.sendMessage("§7Temp avant disparition de la bulle: §c"+this.timeLeft+"s");
                    if (this.timeLeft <= 0) {
                        this.suibun.stop(this);
                    }
                    this.timeLeft--;
                }
            }
        }
    }
}
