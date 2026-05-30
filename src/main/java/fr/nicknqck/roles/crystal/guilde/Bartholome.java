package fr.nicknqck.roles.crystal.guilde;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.CrystalFaction;
import fr.nicknqck.enums.CrystalRoles;
import fr.nicknqck.events.custom.RoleGiveEvent;
import fr.nicknqck.interfaces.IGotMultpleFaction;
import fr.nicknqck.interfaces.IRoles;
import fr.nicknqck.managers.crystaluhc.CrystalManager;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.crystal.CrystalBase;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.event.EventUtils;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class Bartholome extends GuildeBase implements IGotMultpleFaction, Listener {

    public Bartholome(UUID player) {
        super(player);
    }

    @Override
    public void onRoleGive(@NonNull GameState gameState) {
        addKnowedPlayersWithRoles("§7Voici vos§5 alliés§7 que vous aviez recruter pour vous enrichir:", Bartholome.class);
        EventUtils.registerRoleEvent(this);
    }

    @Nonnull
    @Override
    public CrystalFaction getCrystalFaction() {
        return CrystalFaction.NOBLE;
    }

    @Override
    public String getName() {
        return "Bartholome";
    }

    @Override
    public @NonNull IRoles<?> getRoles() {
        return CrystalRoles.Bartholome;
    }

    @Nonnull
    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createAutomaticDesc(this)
                .addCustomLine("§7Vous avez§c 25%§7 de§c chance§7 d'être§c perçu§7 comme un membre du§c peuple§7.")
                .addCustomLine("§7Vous subissez§c 5%§7 de§c dégâts§7 en moins par§c équipier proche§7 (dont vous)")
                .getText();
    }

    @Override
    public @NonNull CrystalFaction getOneFaction() {
        if (RandomUtils.getOwnRandomProbability(25)) {
            return CrystalFaction.PEUPLE;
        }
        return getCrystalFaction();
    }

    @Override
    public @NonNull CrystalFaction[] getPossiblesFaction() {
        return new CrystalFaction[] {
                CrystalFaction.NOBLE,
                CrystalFaction.PEUPLE
        };
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onDamage(@NonNull final EntityDamageByEntityEvent event) {
        if (!event.getEntity().getUniqueId().equals(getPlayer())) return;
        final List<GamePlayer> list = Loc.getNearbyGamePlayers(event.getEntity().getLocation(), 25);
        double toRemove = 0;
        for (GamePlayer g : list) {
            if (g == null)continue;
            if (!g.check())continue;
            if (g.getRole() instanceof CrystalBase) {
                if (g.getRole().getOriginTeam().equals(getOriginTeam()) || g.getRole().getTeam().equals(getOriginTeam())) {
                    toRemove+=5;
                }
            }
        }
        toRemove = toRemove/100;
        toRemove = toRemove + 1;
        event.setDamage(event.getDamage()*toRemove);
    }
    @EventHandler
    private void onEndGiveRole(@NonNull final RoleGiveEvent event) {
        if (!event.isEndGive())return;
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            getGamePlayer().addItems(CrystalManager.refinedCrystalItem);
            Main.getInstance().getCrystalManager().tryToGiveRefinedCrystal(getGamePlayer());
        }, 20);
    }
}
