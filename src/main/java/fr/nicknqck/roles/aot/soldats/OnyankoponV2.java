package fr.nicknqck.roles.aot.soldats;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.aot.builders.SoldatsRoles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.ADNParticle2;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class OnyankoponV2 extends SoldatsRoles {

    public OnyankoponV2(UUID player) {
        super(player);
    }

    @Override
    public String getName() {
        return "Onyankopon";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Onyankopon;
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        super.RoleGiven(gameState);
        addPower(new TelortationItem(this), true);
    }
    private static final class TelortationItem extends ItemPower {

        private ADNParticle2 adnParticle2;

        public TelortationItem(@NonNull RoleBase role) {
            super("§aTéleportation§r", new Cooldown(60*6), new ItemBuilder(Material.NETHER_STAR).setName("§aTéleportation"), role,
                    "§7Après§c 5 secondes§7 vous téléporte vous ainsi que tout les joueurs",
                    "§7étant à moins de§c 8 blocs§7 de vous dans une direction aléatoire très loin",
                    "",
                    "§7§oLes joueurs se font tous téléportation au même endroit.");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                this.adnParticle2 = new ADNParticle2(player.getLocation(), 11, 55, 12, 2.5, 5);
                this.adnParticle2.start();
                new BukkitRunnable() {

                    private int time = 5;

                    @Override
                    public void run() {
                        if (!GameState.inGame()) {
                            cancel();
                            return;
                        }
                        if (adnParticle2 == null) {
                            cancel();
                            return;
                        }
                        final GamePlayer gamePlayer = getRole().getGamePlayer();
                        if (!gamePlayer.check()) {
                            cancel();
                            return;
                        }
                        adnParticle2.setCenter(gamePlayer.getLastLocation());
                        if (this.time <= 0) {
                            gamePlayer.getActionBarManager().removeInActionBar("onyankopon.tp");
                            Bukkit.getScheduler().runTask(getPlugin(), () -> {
                                final Location loc = Loc.getLocationAtDistance(gamePlayer.getLastLocation(), 150);
                                for (GamePlayer nearbyGamePlayer : Loc.getNearbyGamePlayers(gamePlayer.getLastLocation(), 8.0)) {
                                    if (!nearbyGamePlayer.check())continue;
                                    nearbyGamePlayer.teleport(loc);
                                    gamePlayer.sendMessage(Main.getInstance().getNAME()+"§7 Vous avez été téléporter par le pouvoir de§a Onyankopon§7.");
                                }
                            });
                            return;
                        }
                        gamePlayer.getActionBarManager().updateActionBar("onyankopon.tp", "§bTemps avant§c téléportation§b:§c "+ StringUtils.secondsTowardsBeautiful(this.time));
                        this.time--;
                    }

                }.runTaskTimerAsynchronously(getPlugin(), 0, 20);
                return true;
            }
            return false;
        }
    }
}