package fr.nicknqck.titans.impl;

import fr.nicknqck.GameState;
import fr.nicknqck.events.custom.roles.aot.PrepareTitanStealEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.titans.TitanBase;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ColossalV2 extends TitanBase {

    private final List<PotionEffect> potionEffects;
    private final PotionEffect resistance;

    public ColossalV2(GamePlayer gamePlayer) {
        super(gamePlayer);
        this.potionEffects = new ArrayList<>();
        this.resistance = (new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 1, false, false));
        this.potionEffects.add(this.resistance);
        this.potionEffects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20*60, 0, false, false));
        gamePlayer.getRole().addPower(new ChaleurPower(gamePlayer.getRole(), this), true);
    }

    @Override
    public @NonNull String getParticularites() {
        return "§8 -§7 Votre transformation à une durée de§c 3 minutes§7.\n"+
                " \n"+
                "§8 -§7 Lorsque vous êtes transformé en titan vous avez les effets§c Résistance II§7 ainsi que l'effet§c Résistance au Feu§7.\n"+
                " \n"+
                "§8 -§7 Vous pouvez utiliser votre item \"§cChaleur Titanesque§7\" pour activer autours de vous une§c Zone de Feu§7 brulant toute personne étant à l'intérieur.\n"+
                " \n"+
                "§8 -§7 Lorsque votre \"§cZone de Feu§7\" est activé vous n'avez plus l'effet§c Résistance II§7.";
    }

    @Override
    public @NonNull String getName() {
        return "§9Colossal";
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
        return PrepareTitanStealEvent.TitanForm.COLOSSAL;
    }

    private static class ChaleurPower extends ItemPower {

        private final ColossalV2 colossal;

        protected ChaleurPower(@NonNull RoleBase role, ColossalV2 colossal) {
            super("Chaleur Titanesque", new Cooldown(60), new ItemBuilder(Material.MAGMA_CREAM).setName("§cChaleur Titanesque"), role,
                    "§7Vous permet de perdre temporairement de la résistance pour§6 brûler§7 les joueurs autours de vous");
            this.colossal = colossal;
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (!this.colossal.isTransformed()) {
                    player.sendMessage("§cIl faut être transformé pour utilisé ce pouvoir.");
                    return false;
                }
                this.colossal.potionEffects.remove(this.colossal.resistance);
                new ZoneDeFeuRunnable(this, this.getRole().getGameState());
                return true;
            }
            return false;
        }
        private static class ZoneDeFeuRunnable extends BukkitRunnable {

            private final ChaleurPower chaleurPower;
            private final GameState gameState;
            private int timeLeft = 15;

            private ZoneDeFeuRunnable(ChaleurPower chaleurPower, GameState gameState) {
                this.chaleurPower = chaleurPower;
                this.gameState = gameState;
                runTaskTimerAsynchronously(chaleurPower.getPlugin(), 0, 20);
                chaleurPower.colossal.getGamePlayer().getActionBarManager().addToActionBar("colossal.zonedefeu", "§bTemp restant (§cZone de Feu§b):§c 15s");
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (this.timeLeft > 0) {
                    @NonNull final List<Player> players = Loc.getNearbyPlayers(this.chaleurPower.colossal.getGamePlayer().getLastLocation(), 25.0);
                    for (@NonNull final Player player : players) {
                        player.setFireTicks(250);
                    }
                    MathUtil.sendCircleParticle(EnumParticle.FLAME, this.chaleurPower.colossal.getGamePlayer().getLastLocation(), 8, 24);
                    this.chaleurPower.colossal.getGamePlayer().getActionBarManager().updateActionBar("colossal.zonedefeu", "§bTemp restant (§cZone de Feu§b):§c "+this.timeLeft+"s");
                } else {
                    this.chaleurPower.colossal.potionEffects.add(this.chaleurPower.colossal.resistance);
                    this.chaleurPower.colossal.getGamePlayer().getActionBarManager().removeInActionBar("colossal.zonedefeu");
                    this.chaleurPower.colossal.getGamePlayer().sendMessage("§7Votre§c Zone de Feu§7 s'arrête...");
                    cancel();
                    return;
                }
                this.timeLeft--;
            }
        }
    }
}