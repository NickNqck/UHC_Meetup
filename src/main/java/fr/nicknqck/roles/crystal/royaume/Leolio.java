package fr.nicknqck.roles.crystal.royaume;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.CrystalRoles;
import fr.nicknqck.enums.CrystalTeam;
import fr.nicknqck.enums.EffectWhen;
import fr.nicknqck.interfaces.IGotBuyable;
import fr.nicknqck.interfaces.IRoles;
import fr.nicknqck.interfaces.ITeam;
import fr.nicknqck.managers.CrystalManager;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.crystal.CrystalBase;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.interfaces.Buyable;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Leolio extends CrystalBase implements IGotBuyable {

    public Leolio(UUID player) {
        super(player);
    }

    @Override
    public void onRoleGive(@NonNull GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 60, 0, false, false), EffectWhen.DAY);
        addPower(new MoreCrystalPower(this));
    }

    @Override
    public String getName() {
        return "Leolio";
    }

    @Override
    public @NonNull IRoles<?> getRoles() {
        return CrystalRoles.Leolio;
    }

    @Override
    public @NonNull ITeam getOriginTeam() {
        return CrystalTeam.Royaume;
    }

    @Nonnull
    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

    @Nonnull
    @Override
    public Buyable getBuyable() {
        return new BuyableThing();
    }

    private static final class BuyableThing implements Buyable {

        @Nonnull
        @Override
        public Class<? extends RoleBase> getAssossiatedClass() {
            return Leolio.class;
        }

        @Override
        public @NonNull ItemStack getMenuItem() {
            return new ItemBuilder(Material.DIAMOND_PICKAXE)
                    .setName("§aCassage instantané")
                    .hideAllAttributes()
                    .setLore("§7Une fois§a acheter§7, vous aurez§c 1 chance sur deux§7 de§a casser instantanément",
                            "§7les§c blocs§7 que vous§c taperez§7 avec une§f pioche§7.",
                            "",
                            "§7(§c1 fois maximum par seconde§7)",
                            "",
                            "§cCe pouvoir est uniquement achetable par vous, il faut donc le garder secret.",
                            "",
                            "§fCoût:§d "+getCrystalCost()+" cristaux"
                    )
                    .toItemStack();
        }

        @Override
        public @NonNull Power createPower(@NonNull RoleBase role) {
            return new InstantBreakPower(role);
        }

        @Override
        public int getCrystalCost() {
            return 4;
        }

        private static final class InstantBreakPower extends Power implements Listener{

            public InstantBreakPower(@NonNull RoleBase role) {
                super("§aCassage instantané§r", new Cooldown(1), role,
                        "§7Vous avez§c 1 chance sur deux§7 de§a casser instantanément",
                        "§7les§c blocs§7 que vous§c tapez§7 avec une§f pioche§7.");
                setSendCooldown(false);
                EventUtils.registerRoleEvent(this);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                if (map.isEmpty()) {
                    return RandomUtils.getOwnRandomProbability(50.0);
                }
                return false;
            }
            @EventHandler
            private void onDamage(@NonNull final BlockDamageEvent event) {
                final Player player = event.getPlayer();
                if (!player.getUniqueId().equals(getRole().getPlayer()))return;
                if (!player.getGameMode().equals(GameMode.SURVIVAL))return;
                final ItemStack item = player.getItemInHand();
                // Vérifie que le joueur tient une pioche
                if (!isPickaxe(item)) return;

                // 50% de chance
                if (!checkUse(player, new HashMap<>()))return;

                // Appelle un BlockBreakEvent pour vérifier que les autres plugins/protections l'autorisent
                final BlockBreakEvent breakEvent = new BlockBreakEvent(event.getBlock(), player);
                Bukkit.getPluginManager().callEvent(breakEvent);
                if (breakEvent.isCancelled()) return;

                // Casse le bloc instantanément en droppant les items normalement
                event.getBlock().breakNaturally(item);
            }

            private boolean isPickaxe(ItemStack item) {
                if (item == null) return false;
                switch (item.getType()) {
                    case WOOD_PICKAXE:
                    case STONE_PICKAXE:
                    case IRON_PICKAXE:
                    case GOLD_PICKAXE:
                    case DIAMOND_PICKAXE:
                        return true;
                    default:
                        return false;
                }
            }
        }
    }
    private static final class MoreCrystalPower extends Power implements Listener {

        public MoreCrystalPower(@NonNull RoleBase role) {
            super("§aChance du mineur§r", null, role,
                    "§7Lorsque vous§c miner§7 des§d cristaux§7, vous avez§c 50%§7 de§a chance§7 de faire§c x2§7 sur la somme§c miner§7.");
            EventUtils.registerRoleEvent(this);
            if (!getPlugin().getGameConfig().isMinage()) {
                getRole().giveItem(getRole().owner, false, new ItemBuilder(Material.EMERALD_ORE).setAmount(10).setName("§dCrystal Ore").toItemStack());
            }
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            return RandomUtils.getOwnRandomProbability(50.0);
        }
        @EventHandler(priority = EventPriority.NORMAL)
        private void onBlockBreak(@NonNull final BlockBreakEvent event) {
            if (event.getPlayer().getUniqueId().equals(getRole().getPlayer())) {
                if (event.getBlock().getType().equals(Material.EMERALD_ORE)) {
                    if (checkUse(event.getPlayer(), new HashMap<>())) {
                        getRole().giveItem(event.getPlayer(), false, CrystalManager.crystalItem);
                    }
                }
            }
        }
    }
}