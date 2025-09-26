package fr.nicknqck.roles.aot.mahr;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.roles.aot.TitanTransformEvent;
import fr.nicknqck.roles.aot.builders.MahrRoles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.titans.impl.ColossalV2;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.event.EventUtils;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class BertoltV2 extends MahrRoles implements Listener {

    public BertoltV2(UUID player) {
        super(player);
    }

    @Override
    public String getName() {
        return "Bertolt§7 (§6V2§7)";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Bertolt;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        Main.getInstance().getTitanManager().addTitan(getPlayer(), new ColossalV2(getGamePlayer()));
        EventUtils.registerRoleEvent(this);
        addKnowedPlayersWithRoles("§7Voici la liste de vos coéquipier§9 Mahr§7: ", BertoltV2.class, LaraV2.class, PorcoV2.class, ReinerV2.class, Magath.class, PieckV2.class);
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .addCustomLine("§7Lorsque vous vous transformez/détransformez en titan vous donnerez l'effet§c Weakness I§7 pendant§c 30 secondes§7 à toute les personnes (sauf vos coéquipier) dans un rayon de§c 50 blocs")
                .getText();
    }
    @EventHandler
    private void TitanTransformEvent(@NonNull final TitanTransformEvent event) {
        if (event.getPlayer().getUniqueId().equals(getPlayer())) {
            for (@NonNull Player player : Loc.getNearbyPlayersExcept(event.getPlayer(), 50)) {
                if (!gameState.hasRoleNull(player.getUniqueId())) {
                    @NonNull final RoleBase role = gameState.getGamePlayer().get(player.getUniqueId()).getRole();
                    if (role.getTeam().equals(getTeam()) || role.getOriginTeam().equals(getTeam())) {
                        continue;
                    }
                }
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 0, false, false));
            }
        }
    }
}