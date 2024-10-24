package fr.nicknqck.roles.ds.slayers.pillier;

import fr.nicknqck.GameState;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.UUID;

public class GyomeiV2 extends PillierRoles implements Listener {

    private MarquePower marquePower;
    private TextComponent desc;

    public GyomeiV2(UUID player) {
        super(player);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.ROCHE;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Gyomei";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.Gyomei;
    }

    @Override
    public void resetCooldown() {}

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public TextComponent getComponent() {
        return this.desc;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        setMaxHealth(24.0);
        owner.setMaxHealth(getMaxHealth());
        owner.setHealth(owner.getMaxHealth());
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 9999, 0, false, false), EffectWhen.PERMANENT);
        MarquePower marquePower = new MarquePower(this);
        addPower(marquePower, true);
        this.marquePower = marquePower;
        EventUtils.registerEvents(this);
        AutomaticDesc automaticDesc = new AutomaticDesc(this).addEffects(getEffects()).addCustomLine("§7Vous possédez§c 2❤§c permanent§7 supplémentaire").setPowers(getPowers());
        this.desc = automaticDesc.getText();
        setCanuseblade(true);
    }

    @EventHandler
    private void onEndGame(EndGameEvent event) {
        this.marquePower.end = true;
        EventUtils.unregisterEvents(this);
        this.marquePower = null;
    }
    @EventHandler
    private void onKill(UHCPlayerKillEvent event) {
        if (event.getKiller().getUniqueId().equals(getPlayer())) {
            GamePlayer gamePlayer = event.getGameState().getGamePlayer().get(event.getVictim().getUniqueId());
            if (gamePlayer == null)return;
            if (gamePlayer.getRole() == null)return;
            if (this.marquePower == null)return;
            if (gamePlayer.getRole() instanceof DemonsRoles && !this.marquePower.getCooldown().isInCooldown()) {
                this.marquePower.demonsKills++;
                event.getKiller().sendMessage("§7En tuant un§c démon§7 la puissance de votre "+this.marquePower.getItem().getItemMeta().getDisplayName()+"§7 de§c 1 point§7 ce qui vous fait montez à §c"+this.marquePower.demonsKills+"§7(§cs§7).");
            }
        }
    }

    private static class MarquePower extends ItemPower {

        private int demonsKills = 0;
        private boolean end = false;

        protected MarquePower(RoleBase role) {
            super("§aMarque des Pourfendeurs§7 (§aGyomei§7)", new Cooldown(-500), new ItemBuilder(Material.NETHER_STAR).setName("§aMarque des Pourfendeurs"), role
                    , "§c1 fois§7 par partie, vous permet d'obtenir§c +3❤ permanent§7 ainsi que l'effet§c Résistance I§7 pendant§c 5 minutes§7, cependant, vous§c mourrez§7 après l'utilisation.","","§7En tuant un§c joueur§7 appartenant au camp des§c Démons§7 vous obtiendrez§e +1/2💛§7 d'§eabsorbtion§7 au moment de l'activation§7.");
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                getRole().setMaxHealth(getRole().getMaxHealth()+6.0);
                player.setMaxHealth(getRole().getMaxHealth());
                player.setHealth(player.getMaxHealth());
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60*5, 0, false, false), true);
                CraftPlayer craftPlayer = (CraftPlayer) player;
                craftPlayer.getHandle().setAbsorptionHearts(craftPlayer.getHandle().getAbsorptionHearts()+demonsKills);
                Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> {
                    if (end)return;
                    getRole().setMaxHealth(getRole().getMaxHealth()-6.0);
                    Player owner = Bukkit.getPlayer(getRole().getPlayer());
                    if (owner != null) {
                        owner.damage(9999.0);
                        owner.sendMessage("§7Vous §cmourrez§7 suite à l'utilisation de votre "+getItem().getItemMeta().getDisplayName()+"§7.");
                    }
                }, 20*60*5);
                return true;
            }
            return false;
        }
    }
}
