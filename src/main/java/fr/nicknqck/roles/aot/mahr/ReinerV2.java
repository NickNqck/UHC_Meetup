package fr.nicknqck.roles.aot.mahr;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.aot.builders.MahrRoles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.titans.TitanBase;
import fr.nicknqck.titans.impl.CuirasseV2;
import fr.nicknqck.utils.event.EventUtils;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ReinerV2 extends MahrRoles implements Listener {

    public ReinerV2(UUID player) {
        super(player);
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Reiner§7 (§6V2§7)";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Reiner;
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .addCustomLine("§7Lorsque vous n'êtes pas transformé en titan vos coups font §c10%§7 plus de dégat")
                .getText();
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public void RoleGiven(GameState gameState) {
        Main.getInstance().getTitanManager().addTitan(getPlayer(), new CuirasseV2(getGamePlayer()));
        EventUtils.registerRoleEvent(this);
        addKnowedPlayersFromTeam(TeamList.Mahr);
    }
    @EventHandler
    private void onDamage(@NonNull EntityDamageByEntityEvent event) {
        if (event.getDamager().getUniqueId().equals(getPlayer())) {
            if (Main.getInstance().getTitanManager().hasTitan(getPlayer())) {
                @NonNull TitanBase titan = Main.getInstance().getTitanManager().getTitan(getPlayer());
                if (!titan.isTransformed()) {
                    event.setDamage(event.getDamage()*1.1);
                }
            }
        }
    }
}