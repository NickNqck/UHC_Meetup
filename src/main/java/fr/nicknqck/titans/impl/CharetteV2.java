package fr.nicknqck.titans.impl;

import fr.nicknqck.enums.TitanForm;
import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.events.custom.roles.aot.PrepareTitanStealEvent;
import fr.nicknqck.events.custom.roles.aot.TitanTransformEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.titans.TitanBase;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CharetteV2 extends TitanBase implements Listener {

    private int speed;
    private double strengthLoosed;

    public CharetteV2(GamePlayer gamePlayer) {
        super(gamePlayer);
        this.speed = 1;
        this.strengthLoosed = 0;
        EventUtils.registerRoleEvent(this);
        gamePlayer.getRole().addPower(new StackPower(this), true);
    }

    @Override
    public @NonNull String getParticularites() {
        return "§8 -§7 Votre transformation dur§c 3m30\n"+
                " \n"+
                "§8 -§7 Lorsque vous vous transformez en titan vous avez l'effet Speed I ainsi que§c 15❤ permanents§7.\n"+
                " \n"+
                "§8 -§7 Lorsque vous êtes transformés vous pouvez utiliser votre \"§fÉquipement du Charette§7\" qui fera une action différente en fonction de votre clique:\n"+
                " \n"+
                "§7     →§f Clique gauche§7: Vous permet de sacrifier§c 2,5❤ permanents§7 ainsi que§c 15%§7 de§c force§7 pour gagner un niveau de§e speed§7.\n"+
                " \n"+
                "§7     →§f Clique droit§7: Vous permet de regagner§c 2,5❤ permanents§7 ainsi que§c 15%§7 de§c force§7 en échange d'un niveau de§e speed§7.";
    }

    @Override
    public @NonNull String getName() {
        return "§9Charette";
    }

    @Override
    public @NonNull Material getTransformationMaterial() {
        return Material.FEATHER;
    }

    @Override
    public @NonNull List<PotionEffect> getEffects() {
        @NonNull final List<PotionEffect> potionEffect = new ArrayList<>();
        potionEffect.add(new PotionEffect(PotionEffectType.SPEED, 60, this.speed-1, false, false));
        return potionEffect;
    }

    @Override
    public int getTransfoDuration() {
        return 30*7;
    }

    @Override
    public @NonNull TitanForm getTitanForm() {
        return TitanForm.CHARETTE;
    }
    @EventHandler
    private void onTitanTransform(@NonNull final TitanTransformEvent event) {
        if (!event.getTitan().getGamePlayer().getUuid().equals(getGamePlayer().getUuid()))return;
        if (event.isTransforming()) {
            getGamePlayer().getRole().setMaxHealth(getGamePlayer().getRole().getMaxHealth()+10.0);
            event.getPlayer().setMaxHealth(getGamePlayer().getRole().getMaxHealth());
            event.getPlayer().setHealth(event.getPlayer().getHealth()+10.0);
            this.strengthLoosed = 0;
        } else {
            this.strengthLoosed = 0;
            for (int i = this.speed; i > 1; i--) {
                event.getTitan().getGamePlayer().getRole().setMaxHealth(event.getTitan().getGamePlayer().getRole().getMaxHealth()+5.0);
                event.getPlayer().setMaxHealth(event.getTitan().getGamePlayer().getRole().getMaxHealth());
            }
            getGamePlayer().getRole().setMaxHealth(getGamePlayer().getRole().getMaxHealth()-10.0);
            event.getPlayer().setMaxHealth(getGamePlayer().getRole().getMaxHealth());
        }
        this.speed = 1;
    }
    @EventHandler
    private void UHCPlayerBattleEvent(@NonNull final UHCPlayerBattleEvent event) {
        if (!event.isPatch())return;
        if (event.getDamager().getUuid().equals(getGamePlayer().getUuid())) {
            double force = this.strengthLoosed/100;
            force = 1-force;
            event.setDamage(event.getDamage()*force);
        }
    }
    private static class StackPower extends ItemPower {

        private final CharetteV2 charette;

        protected StackPower(@NonNull final CharetteV2 charetteV2) {
            super("Équipement du Charette", null,
                    new ItemBuilder(Material.MINECART).addEnchant(Enchantment.DAMAGE_ALL, 1).hideEnchantAttributes().setName("§fÉquipement du Charette")
                    , charetteV2.getGamePlayer().getRole(), "§7Description disponible dans le§6 /aot titan");
            this.charette = charetteV2;
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (!this.charette.isTransformed()) {
                    player.sendMessage("§7Il faut être transformé pour utiliser ce pouvoir");
                    return false;
                }
                @NonNull final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                final double vie = player.getMaxHealth();
                if (event.getAction().name().contains("LEFT")) {
                    if (vie > 10.0) {
                        getRole().setMaxHealth(getRole().getMaxHealth()-5.0);
                        player.setMaxHealth(getRole().getMaxHealth());
                        this.charette.speed++;
                        this.charette.strengthLoosed+=15;
                        player.sendMessage("§bVous avez augmenté votre§c vitesse§b de§c 1§b, elle est maintenant à§c "+this.charette.speed+"§b, vous avez maintenant§c "+this.charette.strengthLoosed+"%§b de§c force§b en moins");
                        return true;
                    }
                } else {
                    if (this.charette.speed > 1) {
                        getRole().setMaxHealth(getRole().getMaxHealth()+5.0);
                        player.setMaxHealth(getRole().getMaxHealth());
                        this.charette.speed--;
                        this.charette.strengthLoosed-=15;
                        player.sendMessage("§bVous avez baissé votre§c vitesse§b de§c 1§b, elle est maintenant à§c "+this.charette.speed+"§b, vous avez maintenant §c"+this.charette.strengthLoosed+"%§b de§c force§b en moins");
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
