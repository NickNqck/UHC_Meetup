package fr.nicknqck.titans.impl;

import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.events.custom.roles.aot.PrepareTitanStealEvent;
import fr.nicknqck.events.custom.roles.aot.TitanTransformEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.titans.TitanBase;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CuirasseV2 extends TitanBase implements Listener {

    private final List<PotionEffect> potionEffects;
    private boolean hasCuirasse = true;
    private final PotionEffect resistance;
    private final PotionEffect speed;
    private int prepaDash = 0;

    public CuirasseV2(GamePlayer gamePlayer) {
        super(gamePlayer);
        this.potionEffects = new ArrayList<>();
        this.potionEffects.add(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*60, 0, false, false));
        this.resistance = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 0, false, false);
        this.speed = new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false);
        this.potionEffects.add(this.resistance);
        this.getGamePlayer().getRole().addPower(new ChangementDeCuirassePower(this), true);
        EventUtils.registerRoleEvent(this);
    }

    @Override
    public @NonNull String getParticularites() {
        return "§8 -§7 Votre transformation a une durée de§c 3 minutes\n"+
                " \n"+
                "§8 -§7 Lorsque vous êtes transformé en titan vous avez l'effet§c Force I§7 ainsi que l'effet§c Résistance I§7 ou l'effet§c Speed I§7 en fonction de votre§c cuirasse§7.\n"+
                " \n"+
                "§8 -§7 Vous pouvez utiliser votre objet \"§f§lChangement de Cuirasse§r§7\" pour retirer ou équiper votre cuirasse, lorsque vous l'avez vous avez l'effet§c Résistance I§7, sans, vous avez l'effet§c Speed I§7.\n"+
                " \n"+
                "§8 -§7 Lorsque vous êtes transformé en titan vous possédez un passif nommé \"§cCharge Énergique§7\"§7 il fait que tout les§c 25 coups infligés§7 vous faite un petit§c dash§7 en avant§7 vous soignant de§c 2,5❤§7 et infligeant§c 1,5❤§7 de§c dégats§7 à tout les joueurs proche";
    }

    @Override
    public @NonNull String getName() {
        return "§9Cuirasse";
    }

    @Override
    public @NonNull Material getTransformationMaterial() {
        return Material.FEATHER;
    }

    @Override
    public @NonNull List<PotionEffect> getEffects() {
        return this.potionEffects;
    }

    @Override
    public int getTransfoDuration() {
        return 60*3;
    }

    @Override
    public @NonNull PrepareTitanStealEvent.TitanForm getTitanForm() {
        return PrepareTitanStealEvent.TitanForm.CUIRASSE;
    }

    @EventHandler
    private void UHCPlayerBattleEvent(@NonNull final UHCPlayerBattleEvent event) {
        if (event.getDamager().getUuid().equals(getGamePlayer().getUuid())) {
            if (!event.isPatch())return;
            if (!isTransformed())return;
            this.prepaDash++;
            getGamePlayer().getActionBarManager().updateActionBar("cuirassev2.dash", "§cDash§7:§c "+this.prepaDash+"§7/§c25");
            if (this.prepaDash == 25) {
                event.getOriginEvent().getDamager().setVelocity(event.getOriginEvent().getDamager().getLocation().getDirection().multiply(1.8));
                this.prepaDash = 0;
                event.getDamager().sendMessage("§7Vous avez utiliser votre§c Dash Énergique");
                getGamePlayer().getActionBarManager().updateActionBar("cuirassev2.dash", "§cDash§7:§c "+this.prepaDash+"§7/§c25");
            }
        }
    }
    @EventHandler
    private void TitanTransformEvent(@NonNull final TitanTransformEvent event) {
        if (!event.getTitan().getGamePlayer().getUuid().equals(getGamePlayer().getUuid()))return;
        if (event.isTransforming()) {
            this.prepaDash = 0;
            event.getTitan().getGamePlayer().getActionBarManager().addToActionBar("cuirassev2.dash", "§cDash§7:§c "+this.prepaDash+"§7/§c25");
        } else {
            this.prepaDash = 0;
            event.getTitan().getGamePlayer().getActionBarManager().removeInActionBar("cuirassev2.dash");
        }
    }

    private static class ChangementDeCuirassePower extends ItemPower {

        private final CuirasseV2 cuirasse;

        protected ChangementDeCuirassePower(@NonNull CuirasseV2 cuirasseV2) {
            super("Changement de Cuirasse", new Cooldown(5), new ItemBuilder(Material.QUARTZ).setName("§f§lChangement de Cuirasse").addEnchant(Enchantment.DAMAGE_ALL, 1).hideEnchantAttributes(), cuirasseV2.getGamePlayer().getRole(),
                    "§7Vous permet de retirer ou de vous rééquipez de votre cuirasse");
            this.cuirasse = cuirasseV2;
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (this.cuirasse.hasCuirasse) {
                    this.cuirasse.hasCuirasse = false;
                    this.cuirasse.potionEffects.remove(this.cuirasse.resistance);
                    this.cuirasse.potionEffects.add(this.cuirasse.speed);
                    if (this.cuirasse.isTransformed()) {
                        player.sendMessage("§7Vous avez retiré votre§c cuirasse§7, vous êtes maintenant plus§c rapide");
                    } else {
                        player.sendMessage("§7Votre prochaine transformation en titan se passera sans cuirasse");
                    }
                } else {
                    this.cuirasse.hasCuirasse = true;
                    this.cuirasse.potionEffects.remove(this.cuirasse.speed);
                    this.cuirasse.potionEffects.add(this.cuirasse.resistance);
                    if (this.cuirasse.isTransformed()) {
                        player.sendMessage("§7Vous avez mis votre§c cuirasse§7, vous êtes maintenant plus§c résistant");
                    } else {
                        player.sendMessage("§7Votre prochaine transformation en titan se passera avec une cuirasse");
                    }
                }
            }
            return false;
        }
    }
}
