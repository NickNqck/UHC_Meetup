package fr.nicknqck.roles.ds.slayers.pillier;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class MuichiroV2 extends PilierRoles {

    private TextComponent desc;

    public MuichiroV2(UUID player) {
        super(player);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.VENT;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Muichiro";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.Muichiro;
    }

    @Override
    public void resetCooldown() {

    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new T4ItemPower(this), true);
        addPower(new FrappeBrumeuse(this), true);
        AutomaticDesc automaticDesc = new AutomaticDesc(this).setPowers(getPowers());
        this.desc = automaticDesc.getText();
    }

    @Override
    public TextComponent getComponent() {
        return this.desc;
    }
    private static class T4ItemPower extends ItemPower implements Listener {

        protected T4ItemPower(@NonNull RoleBase role) {
            super("§bSoufle de la Brume", new Cooldown(60), new ItemBuilder(Material.DIAMOND_SWORD).setName("§bLame de Muichiro").addEnchant(Enchantment.DAMAGE_ALL, 4), role,
                    "§7Épée enchanter§c Tranchant IV","§7Lorsque vous§c tapez§7 un joueur vous lui infligerez§c 5 secondes§7 de§1 Blindness I");
            EventUtils.registerRoleEvent(this);
            setSendCooldown(false);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            return false;
        }
        @EventHandler
        private void onBattle(EntityDamageByEntityEvent event) {
            if (event.getDamager().getUniqueId().equals(getRole().getPlayer()) && event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
                if (!getCooldown().isInCooldown() && ((Player) event.getDamager()).getItemInHand().isSimilar(getItem())) {
                    ((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0, false, false));
                    getCooldown().use();
                }
            }
        }
    }
    private static class FrappeBrumeuse extends ItemPower {

        protected FrappeBrumeuse(@NonNull RoleBase role) {
            super("§bFrappe Brumeuse", new Cooldown(60*5), new ItemBuilder(Material.NETHER_STAR).setName("§bFrappe Brumeuse"), role,
                    "§7Vous permet de vous§c téléportez§7 dans un rayon de§c 10 blocs§7 autours de vous",
                    "§7Le premier joueur que vous§c frapperez§7 après ceci prendra des §cdégats triplés",
                    "§7Il obtiendra également§1 Blindness I§7 pendant§c 15 secondes");
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            if (getInteractType().equals(InteractType.INTERACT)){
                final List<Location> locs = new ArrayList<>(MathUtil.getCircle(player.getLocation(), 10));
                locs.removeIf(loc -> !loc.getBlock().getType().equals(Material.AIR));
                if (locs.isEmpty()) {
                    player.sendMessage("§cAucun endroit n'est apte pour vous téléportez là bas");
                    return false;
                }
                Collections.shuffle(locs, Main.RANDOM);
                final int i = Main.RANDOM.nextInt(locs.size());
                int trye = 0;
                for (final Location loc : locs) {
                    if (trye == i) {
                        player.setNoDamageTicks(20);
                        player.teleport(loc);
                        player.sendMessage("§7Vous avez utiliser votre "+getName());
                        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 0, false, false));
                        return true;
                    }
                    trye++;
                }
            }
            return false;
        }
    }
}
