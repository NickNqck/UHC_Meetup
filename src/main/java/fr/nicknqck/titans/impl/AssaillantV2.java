package fr.nicknqck.titans.impl;

import fr.nicknqck.events.custom.roles.aot.PrepareTitanStealEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.titans.TitanBase;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AssaillantV2 extends TitanBase {

    private final List<PotionEffect> effects;

    public AssaillantV2(GamePlayer gamePlayer) {
        super(gamePlayer);
        this.effects = new ArrayList<>();
        this.effects.add(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*60*2, 0, false, false));
        this.effects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 0, false, false));
        this.effects.add(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false));
        getGamePlayer().getRole().addPower(new ShutDownPower(gamePlayer.getRole()), true);
    }

    @Override
    public @NonNull String getParticularites() {
        return "§8 -§7 Lorsque vous êtes transformés en§c Titan§7 vous avez les effets§c Speed I, Force I et Résistance I§7.\n"+
                " \n"+
                "§8 -§7 Lorsque votre transformation se termine vous obtenez§c 2 minutes§7 de l'effet§c Force I§7.\n"+
                " \n"+
                "§8 -§7 Votre transformation à une durée de§c 4 minutes§7.\n"+
                " \n"+
                "§8 -§7 Vous possédez l'item \"§lShutDown§r§7\", il vous permet d'empêcher de bouger§c tout les joueurs§7 étant à moins de§c 50 blocs§7 de vous, il possède un cooldown de§c 10 minutes§7.";
    }

    @Override
    public @NonNull String getName() {
        return "§6Assaillant";
    }

    @Override
    public @NonNull Material getTransformationMaterial() {
        return Material.ROTTEN_FLESH;
    }

    @Override
    public @NonNull List<PotionEffect> getEffects() {
        return this.effects;
    }

    @Override
    public int getTransfoDuration() {
        return 60*4;
    }

    @Override
    public @NonNull PrepareTitanStealEvent.TitanForm getTitanForm() {
        return PrepareTitanStealEvent.TitanForm.ASSAILLANT;
    }
    private static class ShutDownPower extends ItemPower {

        protected ShutDownPower(@NonNull RoleBase role) {
            super("ShutDown", new Cooldown(60*10), new ItemBuilder(Material.RECORD_11).addEnchant(Enchantment.DAMAGE_ALL, 1).hideEnchantAttributes().setName("§7§lShutDown"), role,
                    "§7Vous permet d'empêcher tout les autres joueurs présent à moins de§c 50 blocs§7 de vous de bouger");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                @NonNull final List<GamePlayer> aroundGamePlayers = Loc.getNearbyGamePlayers(player.getLocation(), 50);
                aroundGamePlayers.remove(this.getRole().getGamePlayer());
                if (aroundGamePlayers.isEmpty()) {
                    player.sendMessage("§7Il n'y a personne autours de vous,§c impossible§7 d'utiliser ce pouvoir...");
                } else {
                    for (@NonNull final GamePlayer gamePlayer : aroundGamePlayers) {
                        gamePlayer.stun(20*5);
                    }
                    return true;
                }
            }
            return false;
        }
    }
}