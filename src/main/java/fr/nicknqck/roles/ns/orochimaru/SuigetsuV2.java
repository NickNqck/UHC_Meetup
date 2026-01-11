package fr.nicknqck.roles.ns.orochimaru;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.RoleGiveEvent;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.OrochimaruRoles;
import fr.nicknqck.roles.ns.orochimaru.edov2.KabutoV2;
import fr.nicknqck.roles.ns.orochimaru.edov2.OrochimaruV2;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class SuigetsuV2 extends OrochimaruRoles implements Listener {

    private boolean orochimaruDead = false;
    private boolean karinDead = false;

    public SuigetsuV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.PEUINTELLIGENT;
    }

    @Override
    public String getName() {
        return "Suigetsu";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Suigetsu;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        super.RoleGiven(gameState);
        EventUtils.registerRoleEvent(this);
        setChakraType(Chakras.SUITON);
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .addCustomLine(this.orochimaruDead ? "" : "§7A la mort d'§5Orochimaru§7 vous connaitrez§5 Kabuto§7 et obtiendrez l'effet§c Force I permanent§7.")
                .addCustomLine(this.karinDead ? "" : "§7Quand§5 Karin§7 meurt vous obtiendrez l'information de qui est§5 Jugo§7.")
                .addCustomLine(this.orochimaruDead ? "" : "§7Vous possédez l'effet§c Force I§7 proche de§5 Sasuke§7, §5Karin§7 et§5 Orochimaru§7.")
                .getText();
    }

    @Override
    public void GiveItems() {
        super.GiveItems();
        ItemStack Book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta BookMeta = (EnchantmentStorageMeta) Book.getItemMeta();
        BookMeta.addStoredEnchant(Enchantment.DEPTH_STRIDER, 3, false);
        Book.setItemMeta(BookMeta);
        giveItem(owner, false, Book);
        if (!Main.getInstance().getGameConfig().isMinage()) {
            owner.setLevel(owner.getLevel()+6);
        }
    }

    @EventHandler
    private void onRoleGive(final RoleGiveEvent event) {
        if (!event.isEndGive())return;
        if (!event.getGameState().getDeadRoles().contains(Roles.Orochimaru)) {
            new EffectRunnable(this);
        } else {
            givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
            getGamePlayer().sendMessage("§5Orochimaru§7 est mort, vous obtenez l'effet§c Force 1§7 permanent");
            this.orochimaruDead = true;
        }
        if (event.getGameState().getAttributedRole().contains(Roles.Karin)){
            addKnowedRole(KarinV2.class);
        } else {
            this.karinDead = true;
            if (event.getGameState().getAvailableRoles().containsKey(Roles.Jugo)) {
                addKnowedRole(Jugo.class);
            } else {
                addKnowedRole(SasukeV2.class);
            }
        }
        addKnowedRole(OrochimaruV2.class);
        new ResistanceRunnable(this);
        addPower(new SuikaPower(this), true);
    }
    @EventHandler
    private void onDeath(final UHCDeathEvent event) {
        if (event.getRole() == null)return;
        if (event.getRole() instanceof KarinV2) {
            this.karinDead = true;
            addKnowedRole(Jugo.class);
        }
        if (event.getRole() instanceof OrochimaruV2) {
            addKnowedRole(KabutoV2.class);
            this.orochimaruDead = true;
        }
    }
    private static class EffectRunnable extends BukkitRunnable {

        private final SuigetsuV2 suigetsuV2;

        private EffectRunnable(SuigetsuV2 suigetsuV2) {
            this.suigetsuV2 = suigetsuV2;
            runTaskTimerAsynchronously(Main.getInstance(), 1, 1);
        }

        @Override
        public void run() {
            if (!this.suigetsuV2.getGameState().getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            final Map<PotionEffect, EffectWhen> effectEffectWhenMap = new HashMap<>(this.suigetsuV2.getEffects());
            for (final PotionEffect potionEffect : effectEffectWhenMap.keySet()) {
                if (!effectEffectWhenMap.get(potionEffect).equals(EffectWhen.PERMANENT))continue;
                if (!potionEffect.getType().equals(PotionEffectType.INCREASE_DAMAGE))continue;
                //donc suigetsu a force perma
                cancel();
                break;
            }
            final List<GamePlayer> gamePlayerList = Loc.getNearbyGamePlayers(this.suigetsuV2.getGamePlayer().getLastLocation(), 15);
            for (final GamePlayer gamePlayer : gamePlayerList) {
                if (!gamePlayer.isAlive()) continue;
                if (gamePlayer.getRole() == null)continue;
                if (!gamePlayer.isOnline()) continue;
                if (gamePlayer.getUuid().equals(this.suigetsuV2.getPlayer()))continue;
                final RoleBase role = gamePlayer.getRole();
                if (role instanceof OrochimaruV2 || role instanceof SasukeV2 || role instanceof KarinV2) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> this.suigetsuV2.givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.NOW));
                    break;
                }
            }
        }
    }
    private static class ResistanceRunnable extends BukkitRunnable {

        private final SuigetsuV2 suigetsuV2;

        private ResistanceRunnable(SuigetsuV2 suigetsuV2) {
            this.suigetsuV2 = suigetsuV2;
            runTaskTimerAsynchronously(Main.getInstance(), 1, 1);
        }
        @Override
        public void run() {
            if (!this.suigetsuV2.getGameState().getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            final Player owner = Bukkit.getPlayer(this.suigetsuV2.getPlayer());
            if (owner == null) return;
            if (owner.getLocation().getBlock().getType().name().contains("WATER") || owner.getEyeLocation().getBlock().getType().name().contains("WATER")) {
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> this.suigetsuV2.givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30, 0, false, false), EffectWhen.NOW));
            }
        }
    }
    private static class SuikaPower extends ItemPower {

        public SuikaPower(@NonNull RoleBase role) {
            super("Suika", new Cooldown(60*5), new ItemBuilder(Material.NETHER_STAR).setName("§bSuika"), role,
                    "§7Vous permet de vous téléportez environ§c 5 blocs plus loins§7 (à travers les murs et les obstacles)");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                Location loc = player.getLocation();
                Vector direction = loc.getDirection().normalize().multiply(5.0);
                Location target = loc.add(direction);

                player.teleport(target);
                return true;
            }
            return false;
        }

    }
}