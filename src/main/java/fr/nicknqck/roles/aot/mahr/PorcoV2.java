package fr.nicknqck.roles.aot.mahr;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.aot.builders.MahrRoles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.titans.impl.MachoireV2;
import fr.nicknqck.utils.event.EventUtils;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.UUID;

public class PorcoV2 extends MahrRoles implements Listener {

    public PorcoV2(UUID player) {
        super(player);
    }

    @Override
    public String getName() {
        return "Porco§7 (§6V2§7)";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Porco;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        Main.getInstance().getTitanManager().addTitan(getPlayer(), new MachoireV2(getGamePlayer()));
        EventUtils.registerRoleEvent(this);
        addKnowedPlayersWithRoles("§7Voici la liste de vos coéquipier§9 Mahr§7: ", BertoltV2.class, LaraV2.class, PorcoV2.class, ReinerV2.class, Magath.class, PieckV2.class);
        super.RoleGiven(gameState);
    }
    @EventHandler
    private void onDamage(@NonNull final EntityDamageByEntityEvent event) {
        if (event.getDamager().getUniqueId().equals(getPlayer())) {
            event.setDamage(event.getDamage()*1.1);
        }
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .addCustomLine("§7Lorsque vous tapez un joueur vous ferez§c 10%§7 de§c dégâts §7supplémentaires§7.")
                .getText();
    }
}