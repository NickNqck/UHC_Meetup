package fr.nicknqck.roles.ds.slayers.pillier;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.UUID;

public class KanaeV2 extends PillierRoles{

    private TextComponent textComponent;

    public KanaeV2(UUID player) {
        super(player);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.EAU;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Kanae";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.Kanae;
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
        addPower(new VegetalPower(this), true);
        AutomaticDesc desc = new AutomaticDesc(this).setPowers(getPowers());
        this.textComponent = desc.getText();
    }

    @Override
    public TextComponent getComponent() {
        return textComponent;
    }
    private static class VegetalPower extends ItemPower {

        protected VegetalPower(@NonNull RoleBase role){
            super("§aÉpée Végétal", new Cooldown(40), new ItemBuilder(Material.DIAMOND_SWORD).addEnchant(Enchantment.DAMAGE_ALL, 3).setName("§aLame végétal"), role,
                    "§7Lorsque vous frappez un joueur il y a un certain§c pourcentage§7 de chance§7 que certaine action se produise",
                    "",
                    AllDesc.point+"§c25%§7 de ne rien faire du tout",
                    "",
                    AllDesc.point+"§c25%§7 d'infliger§c Weakness I§7 pendant§c 15s",
                    "",
                    AllDesc.point+"§c20%§7 d'infliger§c Slowness I§7 pendant§c 12s",
                    "",
                    AllDesc.point+"§c15%§7 d'infliger§c Poison I§7 pendant§c 10s",
                    "",
                    AllDesc.point+"§c10%§7 de vous§c soignez§7 de§c 2❤",
                    "",
                    AllDesc.point+"§c5%§7 d'infliger§c Weakness I§7,§c Slowness I§7 et§c Poison I§7 pendant§c 10s");
            setSendCooldown(false);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            if (getInteractType().equals(InteractType.ATTACK_ENTITY)) {
                EntityDamageByEntityEvent event = ((UHCPlayerBattleEvent) args.get("event")).getOriginEvent();
                if (!(event.getDamager() instanceof Player))return false;
                StringBuilder toKanae = new StringBuilder("§c"+((Player) event.getEntity()).getDisplayName()+"§7 à reçus§c ");
                StringBuilder toVictim = new StringBuilder("§7Vous avez reçus§c ");
                int rdm = Main.RANDOM.nextInt(100);
                System.out.println("rdm "+rdm);
                if (rdm < 25) {
                    toKanae = null;
                    toVictim = null;
                }
                if (rdm >= 25 && rdm < 50) {
                    ((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*15, 0, false, false), true);
                    toKanae.append("15 secondes§7 de§c Weakness§7.");
                    toVictim.append("15 secondes§7 de§c Weakness§7");
                } else if (rdm >= 50 && rdm < 70) {
                    ((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*12, 0, false, false), true);
                    toKanae.append("12 secondes§7 de§c Slowness§7.");
                    toVictim.append("12 secondes§7 de§c Slowness§7");
                } else if (rdm >= 70 && rdm < 85) {
                    ((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*10, 0, false, false), true);
                    toKanae.append("10 secondes§7 de§c Poison§7.");
                    toVictim.append("10 secondes§7 de§c Poison§7");
                } else if (rdm >= 85 && rdm < 95){
                    toKanae = null;
                    toVictim = null;
                    ((Player) event.getDamager()).setHealth(Math.min(((Player) event.getDamager()).getMaxHealth(), ((Player) event.getDamager()).getHealth()+4.0));
                    event.getDamager().sendMessage("§7Vous vous êtes soigner de§c 2"+ AllDesc.coeur+"§7.");
                } else if (rdm >= 95) {
                    toKanae.append("10 secondes§7 de§c Weakness§7,§c Slowness§7 et§c Poison§7.");
                    toVictim.append("10 secondes§7 de§c Weakness§7,§c Slowness§7 et§c Poison");
                    ((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*10, 0, false, false), true);
                    ((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*10, 0, false, false), true);
                    ((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*10, 0, false, false), true);
                }
                if (toVictim != null && !toVictim.toString().equals("§7Vous avez reçus§c ")) {
                    toVictim.append("§7 de la part de§a Kanae");
                    event.getEntity().sendMessage(toVictim.toString());
                    event.getDamager().sendMessage(toKanae.toString());
                }
                return true;
            }
            return false;
        }
    }
}
