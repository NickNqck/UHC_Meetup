package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.roles.ds.slayers.pillier.TomiokaV2;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UrokodakiV3 extends SlayerRoles {

    public UrokodakiV3(UUID player) {
        super(player);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.EAU;
    }

    @Override
    public String getName() {
        return "Urokodaki§7 (§6V2§7)";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Urokodaki;
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createAutomaticDesc(this)
                .addCustomLine("§7Vous possédez l'effet§c Force I§7 dans l'§beau")
                .getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        new ForceWaterRunnable(this);
        ItemStack Book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta BookMeta = (EnchantmentStorageMeta) Book.getItemMeta();
        BookMeta.addStoredEnchant(Enchantment.DEPTH_STRIDER, 2, false);
        Book.setItemMeta(BookMeta);giveItem(owner, false, Book);
        if (!Main.getInstance().getGameConfig().isMinage()) {
            owner.setLevel(owner.getLevel()+6);
        }
        addPower(new ImpacteItem(this), true);
        addPower(new DSWATER(this));
        super.RoleGiven(gameState);
    }
    private static class ImpacteItem extends ItemPower {

        public ImpacteItem(@NonNull RoleBase role) {
            super("Impacte de la Cascade", new Cooldown(60*7), new ItemBuilder(Material.NETHER_STAR).setName("§bImpacte de la cascade"), role);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                onPlayerInteract(event);
            }
            return false;
        }
        private void onPlayerInteract(PlayerInteractEvent event) {
            Player player = event.getPlayer();

            if (!event.getAction().toString().contains("RIGHT")) return;

            // Récupère la cible
            Player target = RayTrace.getTargetPlayer(player, 30, null);
            if (target == null) return;

            // Super jump
            player.setVelocity(new Vector(0, 2, 0));

            // Crée une ligne de blocs d’eau figés entre le joueur et la cible
            Location start = player.getLocation().clone();
            Location end = target.getLocation().clone();
            Vector direction = end.toVector().subtract(start.toVector()).normalize();

            int distance = (int) start.distance(end);
            List<Location> waterBlocks = new ArrayList<>();

            for (int i = 1; i <= distance; i++) {
                Location loc = start.clone().add(direction.clone().multiply(i));
                loc.setY(start.getY()); // garde la même hauteur que le joueur
                Block block = loc.getBlock();

                if (block.getType() == Material.AIR) {
                    block.setType(Material.WATER);
                    waterBlocks.add(block.getLocation());
                }
            }

            // Supprime les blocs d’eau après 5 secondes
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Location loc : waterBlocks) {
                        if (loc.getBlock().getType() == Material.WATER) {
                            loc.getBlock().setType(Material.AIR);
                        }
                    }
                }
            }.runTaskLater(plugin, 20 * 5);

            // Détection de l’atterrissage
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnGround()) {
                        // Inflige 2 cœurs sans tuer
                        double newHealth = Math.max(1.0, player.getHealth() - 4.0);
                        player.setHealth(newHealth);

                        // Applique Slowness I pendant 15s
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 15 * 20, 0));

                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 0L, 2L);
        }

    }
    private static class DSWATER extends CommandPower {

        public DSWATER(@NonNull RoleBase role) {
            super("/ds water <joueur>", "water", null, role, CommandType.DS,
                    "§7Si la personne visée est l'un de vos élève, vous obtiendrez l'effet§e Speed I§7 de manière§c permanente§7 et",
                    "§9Résistance I§7 proche de la§c cible§7.",
                    "",
                    "§7Vos élèves sont:§a Tanjiro§7,§a Tomioka§7,§a Sabito§7,§a Makomo§7.");
            setMaxUse(1);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            final String[] args = (String[]) map.get("args");
            if (args.length == 2) {
                final Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    final GamePlayer gamePlayer = GameState.getInstance().getGamePlayer().get(target.getUniqueId());
                    if (gamePlayer != null) {
                        if (gamePlayer.getRole() != null) {
                            if (gamePlayer.getRole() instanceof Tanjiro ||
                                    gamePlayer.getRole() instanceof TomiokaV2 ||
                                    gamePlayer.getRole() instanceof SabitoV2 ||
                                    gamePlayer.getRole() instanceof Makomo) {
                                getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
                                new ResistanceRunnable(this, gamePlayer).runTaskTimerAsynchronously(getPlugin(), 20, 20);
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }
        private static class ResistanceRunnable extends BukkitRunnable {

            private final DSWATER dswater;
            private final GamePlayer gamePlayer;

            private ResistanceRunnable(DSWATER dswater, GamePlayer gamePlayer) {
                this.dswater = dswater;
                this.gamePlayer = gamePlayer;
            }

            @Override
            public void run() {
                if (!dswater.getRole().getGameState().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                final Player owner = Bukkit.getPlayer(dswater.getRole().getPlayer());
                if (owner == null)return;
                if (!gamePlayer.isOnline())return;
                if (!gamePlayer.isAlive())return;
                for (Player player : owner.getWorld().getPlayers()) {
                    if (player.getUniqueId().equals(owner.getUniqueId()))continue;
                    if (player.getLocation().distance(owner.getLocation()) > 25)continue;
                    if (!player.getUniqueId().equals(gamePlayer.getUuid()))continue;
                    Bukkit.getScheduler().runTask(this.dswater.getPlugin(), () -> this.dswater.getRole().givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0, false
                    , false), EffectWhen.NOW));
                }
            }
        }
    }
    private static class ForceWaterRunnable extends BukkitRunnable {

        private final UrokodakiV3 urokodaki;

        private ForceWaterRunnable(UrokodakiV3 urokodaki) {
            this.urokodaki = urokodaki;
            runTaskTimerAsynchronously(Main.getInstance(), 20, 1);
        }

        @Override
        public void run() {
            if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (!urokodaki.getGamePlayer().isAlive() || !urokodaki.getGamePlayer().isOnline()) {
                return;
            }
            final Player player = Bukkit.getPlayer(this.urokodaki.getPlayer());
            if (player == null)return;
            if (player.getLocation().getBlock().getType().name().contains("WATER") || player.getEyeLocation().getBlock().getType().name().contains("WATER")) {
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> this.urokodaki.givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.NOW));
            }
        }
    }
}