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
import fr.nicknqck.utils.powers.CommandPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

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
        addPower(new DSWATER(this));
        super.RoleGiven(gameState);
    }
    private static class DSWATER extends CommandPower {

        public DSWATER(@NonNull RoleBase role) {
            super("/ds water <joueur>", "water", null, role, CommandType.DS,
                    "§7Si la personne visée est l'un de vos élève, vous obtiendrez l'effet§e Speed I§7 de manière§c permanente§7.");
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
                                //Trouver autre chose à ajouter
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
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