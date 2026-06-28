package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.EChakras;
import fr.nicknqck.enums.EffectWhen;
import fr.nicknqck.enums.Intelligence;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.interfaces.IRoles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
import fr.nicknqck.scenarios.impl.Anti_Abso;
import fr.nicknqck.utils.PotionUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;

public class Choji extends ShinobiRoles {

    public Choji(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.MOYENNE;
    }

    @Override
    public EChakras[] getChakrasCanHave() {
        return new EChakras[] {
                EChakras.KATON,
                EChakras.DOTON
        };
    }

    @Override
    public String getName() {
        return "Choji";
    }

    @Override
    public @NonNull IRoles<?> getRoles() {
        return Roles.Choji;
    }

    @Override
    public @NonNull TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new BouletHumain(this), true);
        addPower(new Gloutonnerie(this));
    }
    private static final class BouletHumain extends ItemPower {

        public BouletHumain(@NonNull RoleBase role) {
            super("§aBoulet Humain", new Cooldown(60*5), new ItemBuilder(Material.FEATHER).setName("§aBoulet Humain"), role,
                    "§7Vous propulse dans la direction regarder et vous donne§b Speed I§7 pendant§c 30 secondes§7."
            );
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final Vector vector = player.getEyeLocation().getDirection();
                vector.multiply(2.8);
                player.setVelocity(vector);
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*30, 0, false, false), EffectWhen.NOW);
                PotionUtils.addTempNoFall(player.getUniqueId(), 1);
                return true;
            }
            return false;
        }
    }
    private static final class Gloutonnerie extends CommandPower implements Listener {

        @NonNull
        private final PotionEffect hunger;
        private boolean activate = false;
        private final Cooldown cooldown;

        public Gloutonnerie(@NonNull RoleBase role) {
            super("§a/ns gloutonnerie", "gloutonnerie", null, role, CommandType.NS,
                    "§7Tant que la§a gloutonnerie§7 est§a activer§7, vous pouvez manger une§e pomme d'or§c instantanément§7.",
                    "",
                    "§7Vous ne pouvez manger qu'une§e pomme d'or§7 toute les§c 5 secondes§7 avec ce pouvoir.",
                    "§7Tant que ce§c pouvoir§7 est§a actif§7 vous avez§2 Hunger II§7 de manière§c permanente§7.",
                    "§7Manger une§e pomme d'or§7 avec ce pouvoir§c ne remplie pas§7 la§a barre de nouriture§7. "
            );
            EventUtils.registerRoleEvent(this);
            hunger = new PotionEffect(PotionEffectType.HUNGER, 60, 1, false, false);
            cooldown = new Cooldown(5);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (!this.activate) {
                player.sendMessage("§7Vous avez§a activer§7 votre§a gloutonnerie§7.");
                this.activate = true;
                getRole().givePotionEffect(this.hunger, EffectWhen.PERMANENT);
            } else {
                getRole().getEffects().remove(this.hunger);
                player.sendMessage("§7Vous avez§c désactiver§7 votre§a gloutonnerie§7.");
                this.activate = false;
            }
            return true;
        }
        @EventHandler(priority = EventPriority.HIGHEST)
        private void onInteract(PlayerInteractEvent event) {
            if (!activate) return;

            // Uniquement le propriétaire du pouvoir
            if (!event.getPlayer().getUniqueId().equals(getRole().getPlayer())) return;
            if (this.cooldown.isInCooldown()) {
                return;
            }

            // Uniquement clic droit
            if (event.getAction() != Action.RIGHT_CLICK_AIR
                    && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

            final ItemStack item = event.getPlayer().getItemInHand();
            if (item == null) return;
            if (item.getType() != Material.GOLDEN_APPLE) return;

            // Annuler l'interaction normale (empêche faim/saturation/effets)
            event.setCancelled(true);

            // Consommer une pomme d'or instantanément sans aucun effet
            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                event.getPlayer().setItemInHand(null);
            }
            this.cooldown.use();
            if (event.getPlayer().getSaturation() > 0f) {
                event.getPlayer().setSaturation(0f);
                event.getPlayer().setFoodLevel(Math.max(1, event.getPlayer().getFoodLevel() - 1));
            }
            if (Anti_Abso.isAntiabsoall()) {
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20*60*2, 0, false, false), EffectWhen.NOW);
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*4, 1, false, false), EffectWhen.NOW);
            } else if (Anti_Abso.isAntiabsoinvi()) {
                if (event.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    getRole().givePotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20*60*2, 0, false, false), EffectWhen.NOW);
                    getRole().givePotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*4, 1, false, false), EffectWhen.NOW);
                } else {
                    getRole().givePotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20*60*2, 0, false, true), EffectWhen.NOW);
                    getRole().givePotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*4, 1, false, true), EffectWhen.NOW);
                }
            } else if (Anti_Abso.isAntiabsooff()) {
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20*60*2, 0, false, true), EffectWhen.NOW);
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*4, 1, false, true), EffectWhen.NOW);
            }
        }
    }
}