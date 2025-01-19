package fr.nicknqck.roles.ds.demons.lune;

import fr.nicknqck.GameState;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EnmuV2 extends DemonsRoles {

    public EnmuV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull DemonType getRank() {
        return DemonType.INFERIEUR;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Enmu§7 (§6V2§7)§r";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Enmu;
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Demon;
    }

    @Override
    public void resetCooldown() {

    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .setPowers(getPowers())
                .getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new EndormissementPower(this), true);
    }

    private static class EndormissementPower extends ItemPower {

        private final CliqueDroit cliqueDroit;
        private final CliqueGauche cliqueGauche;

        protected EndormissementPower(@NonNull RoleBase role) {
            super("§cEndormissement", new Cooldown(5), new ItemBuilder(Material.FERMENTED_SPIDER_EYE).setName("§cEndormissement"), role,
                    "§7Vous permet d'§cendormir§7 un ou plusieurs joueur(s) en fonction de votre clique:",
                    "",
                    "§8 • §fClique droit:§7 En §cvisant§7 un joueur, vous permet de l'§cendormir§7 pendant§c 8 secondes",
                    "",
                    "§8 • §fClique gauche:§7 Vous permet d'endormir tout joueurs présent autours de vous dans un rayon de§c 30 blocs§7 pendant§c 10 secondes");
            this.cliqueDroit = new CliqueDroit(getRole());
            getRole().addPower(this.cliqueDroit);
            this.cliqueGauche = new CliqueGauche(getRole());
            getRole().addPower(this.cliqueGauche);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                if (event.getAction().name().contains("RIGHT")) {
                    return this.cliqueDroit.checkUse(player, map);
                } else if (event.getAction().name().contains("LEFT")) {
                    return this.cliqueGauche.checkUse(player, map);
                }
            }
            return false;
        }
        private static class CliqueDroit extends Power {

            public CliqueDroit(@NonNull RoleBase role) {
                super("§cEndormissement§7 (§fClique droit§7)", new Cooldown(60*10), role);
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(Player player, Map<String, Object> map) {
                final Player target = RayTrace.getTargetPlayer(player, 20, null);
                if (target == null) {
                    player.sendMessage("§cIl faut viser un joueur !");
                    return false;
                }
                final GamePlayer gamePlayer = getRole().getGameState().getGamePlayer().get(target.getUniqueId());
                if (gamePlayer == null) {
                    player.sendMessage("§cIl faut viser un joueur !");
                    return false;
                }
                if (gamePlayer.getRole() == null) {
                    player.sendMessage("§cIl faut viser un joueur valide !");
                    return false;
                }
                if (gamePlayer.getRole() instanceof DemonsRoles) {
                    gamePlayer.stun(20*4, true);
                } else {
                    gamePlayer.stun(20*8, true);
                }
                player.sendMessage("§7Vous avez§c endormi§7(§ce§7):§c "+target.getDisplayName());
                return true;
            }
        }
        private static class CliqueGauche extends Power {

            public CliqueGauche(@NonNull RoleBase role) {
                super("§cEndormissement§7 (§fClique gauche§7)", new Cooldown(60*20), role);
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(Player player, Map<String, Object> map) {
                final GameState gameState = GameState.getInstance();
                final List<Player> aroundPlayers = Loc.getNearbyPlayersExcept(player, 30);
                if (aroundPlayers.isEmpty()) {
                    player.sendMessage("§cIl n'y a pas asser de joueur autours de vous !");
                    return false;
                }
                for (final Player target : aroundPlayers) {
                    if (!gameState.hasRoleNull(target.getUniqueId())) {
                        final GamePlayer gameTarget = gameState.getGamePlayer().get(target.getUniqueId());
                        if (gameTarget.getRole() instanceof DemonsRoles) {
                            gameTarget.stun(100, true);
                        } else {
                            gameTarget.stun(200, true);
                        }
                    }
                }
                return true;
            }
        }
    }
}
