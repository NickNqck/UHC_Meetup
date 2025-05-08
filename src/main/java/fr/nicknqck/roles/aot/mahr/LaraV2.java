package fr.nicknqck.roles.aot.mahr;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.aot.builders.MahrRoles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.titans.impl.WarhammerV2;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.UUID;

public class LaraV2 extends MahrRoles implements Listener {

    public LaraV2(UUID player) {
        super(player);
    }

    @Override
    public String getName() {
        return "Lara§7 (§6V2§7)";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Lara;
    }
    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .addCustomLine("§7Tant que vous êtes transformé en titan vous recevez §c5%§7 de §cdégâts en moins")
                .addCustomLine("§7Même si vous n'êtes pas transformé en titan, vous pourrez§c réssusciter§7 comme si vous l'étiez (voir §6/aot info§7)")
                .getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        @NonNull final WarhammerV2 warhammerV2 = new WarhammerV2(this.getGamePlayer());
        setTitan(warhammerV2);
        Main.getInstance().getTitanManager().addTitan(getPlayer(), warhammerV2);
        addKnowedPlayersWithRoles("§7Voici la liste de vos coéquipier§9 Mahr§7: ", BertoltV2.class, LaraV2.class, PorcoV2.class, ReinerV2.class, Magath.class, PieckV2.class);
        super.RoleGiven(gameState);
    }
    @EventHandler
    private void onDamage(@NonNull final EntityDamageEvent event) {
        if (!event.getEntity().getUniqueId().equals(getPlayer()))return;
        if (getTitan() == null)return;
        if (!getTitan().isTransformed())return;
        event.setDamage(event.getDamage()*0.95);
    }
}