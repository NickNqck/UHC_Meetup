package fr.nicknqck.roles.ds.demons.lune;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.roles.PowerActivateEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.demons.MuzanV2;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DomaV2 extends DemonsRoles {

    public DomaV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull DemonType getRank() {
        return DemonType.SUPERIEUR;
    }

    @Override
    public String getName() {
        return "Doma§7 (§6V2§7)";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Doma;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Demon;
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new ZoneDeGlacePower(this), true);
        addKnowedRole(MuzanV2.class);
        addPower(new GeleProgressif(this));
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0,  false, false), EffectWhen.NIGHT);
        super.RoleGiven(gameState);
    }
    private static class ZoneDeGlacePower extends ItemPower {

        public ZoneDeGlacePower(@NonNull RoleBase role) {
            super("Zone de glace", new Cooldown(60*10), new ItemBuilder(Material.NETHER_STAR).setName("§bZone de glace§f"), role,
                    "§7A l'activation crée une§c zone§7 autours de vous, les joueurs étant à l'intérieur obtiendront l'effet§c Lenteur I§7",
                    "",
                    "§7Si une personne reste dans cette§c zone§7 pendant plus de§c 5 secondes§7, auront de la§b glace§7 à leurs pied",
                    "",
                    "§7Lorsque la§c zone§7 expire, tout les joueurs ayant eu de la§b glace§7 à leurs§c pied§7 obtiendront§c 30 secondes§7 de§c Lenteur II§7.");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                player.sendMessage("§7Vous avez§a activé§7 votre§b Zone de glace§7.");
                new ZoneRunnable(this).runTaskTimer(Main.getInstance(), 0, 20);
                return true;
            }
            return false;
        }
        private static class ZoneRunnable extends BukkitRunnable {

            private final ZoneDeGlacePower zoneDeGlacePower;
            private int timeLeft = 30;
            private final HashMap<UUID, Integer> map;

            private ZoneRunnable(ZoneDeGlacePower zoneDeGlacePower) {
                this.zoneDeGlacePower = zoneDeGlacePower;
                this.map = new HashMap<>();
            }

            @Override
            public void run() {
                if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (!this.zoneDeGlacePower.getRole().getGamePlayer().isAlive()) timeLeft = 0;
                this.zoneDeGlacePower.getRole().getGamePlayer().getActionBarManager().updateActionBar("doma.zone", "§bTemp restant (Zone de glace):§c "+ StringUtils.secondsTowardsBeautiful(timeLeft));
                MathUtil.spawnRGBCircleParticle(0, 255, 245, this.zoneDeGlacePower.getRole().getGamePlayer().getLastLocation(), 8, 32);
                for (final GamePlayer gamePlayer : Loc.getNearbyGamePlayers(this.zoneDeGlacePower.getRole().getGamePlayer().getLastLocation(), 8)) {
                    if (!gamePlayer.isAlive())continue;
                    if (!gamePlayer.isOnline())continue;
                    if (gamePlayer.getRole() == null)continue;
                    if (gamePlayer.getUuid().equals(this.zoneDeGlacePower.getRole().getPlayer()))continue;
                    if (!this.map.containsKey(gamePlayer.getUuid())) {
                        this.map.put(gamePlayer.getUuid(), 0);
                    }
                    this.map.replace(gamePlayer.getUuid(), this.map.get(gamePlayer.getUuid())+1);
                    boolean glace = this.map.get(gamePlayer.getUuid()) >= 5;
                    if (glace) {
                        Location loc = gamePlayer.getLastLocation().clone();
                        loc.setY(loc.getY() - 1);
                        for (int x = -2; x <= 2; x++) {
                            for (int z = -2; z <= 2; z++) {
                                Block block = loc.clone().add(x, 0, z).getBlock();
                                if (block.getType().equals(Material.AIR)) block = loc.clone().add(x, -1, z).getBlock();
                                block.setType(Material.PACKED_ICE);
                            }
                        }
                    }
                    gamePlayer.getRole().givePotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 0, false, false), EffectWhen.NOW);
                }
                if (this.timeLeft <= 0) {
                    this.zoneDeGlacePower.getRole().getGamePlayer().getActionBarManager().removeInActionBar("doma.zone");
                    Bukkit.getScheduler().runTask(zoneDeGlacePower.getPlugin(), () -> {
                        for (UUID uuid : this.map.keySet()) {
                            final Player player = Bukkit.getPlayer(uuid);
                            if (player == null)continue;
                            final GamePlayer gamePlayer = this.zoneDeGlacePower.getRole().getGameState().getGamePlayer().get(uuid);
                            if (gamePlayer == null)continue;
                            if (!gamePlayer.isOnline())continue;
                            if (!gamePlayer.isAlive())continue;
                            player.sendMessage("§7Vous avez touché par la§b décharge de gèle§7 de§c Doma§7.");
                            if (gamePlayer.getRole() == null) {
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*30, 1, false, false), true);
                                continue;
                            }
                            gamePlayer.getRole().givePotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*30, 1, false, false), EffectWhen.NOW);
                        }
                    });

                    cancel();
                    return;
                }
                this.timeLeft--;
            }
        }
    }
    private static class GeleProgressif extends CommandPower implements Listener {

        private boolean activate = false;
        private final HashMap<UUID, Integer> map = new HashMap<>();
        private final HashMap<UUID, Long> muteds = new HashMap<>();

        public GeleProgressif(@NonNull RoleBase role) {
            super("/ds gel", "gel", null, role, CommandType.DS,
                    "§7Vous permet d'§aactiver§7 et de§c désactiver§7 votre§b gel§7.",
                    "",
                    "§7Lorsque votre§b gel§7 est§a activé§7, tout les§c 15 coups§7 (unique à chaque joueur§7)",
                    "§7la personne que vous avez frapper ne pourra§c pas utiliser ses pouvoirs§7 pendant§c 10 secondes§7.");
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (!activate) {
                activate = true;
                player.sendMessage("§7Votre§b gèle§7 est maintenant§a activer§7.");
            } else {
                activate = false;
                player.sendMessage("§7Votre§b gèle§7 est maintenant§c désactiver§7.");
            }
            return true;
        }
        @EventHandler
        private void onDamage(final EntityDamageByEntityEvent event) {
            if (!(event.getDamager() instanceof Player))return;
            if (!(event.getEntity() instanceof Player))return;
            if (!activate)return;
            if (!event.getDamager().getUniqueId().equals(getRole().getPlayer()))return;
            if (!map.containsKey(event.getEntity().getUniqueId()))map.put(event.getEntity().getUniqueId(), 0);
            int coups = map.getOrDefault(event.getEntity().getUniqueId(), 0);
            coups++;
            if (coups == 15) {
                coups = 0;
                this.muteds.put(event.getEntity().getUniqueId(), System.currentTimeMillis());
                event.getEntity().sendMessage("§7Vous avez été§b gelé§7 par§c Doma§7.");
            }
            getRole().getGamePlayer().getActionBarManager().updateActionBar("doma.gele", "§bCoups (§c"+((Player) event.getEntity()).getDisplayName()+"§b):§c "+coups+"§b/§615");
            map.replace(event.getEntity().getUniqueId(), coups);
        }
        @EventHandler
        private void onPowerUse(final PowerActivateEvent event) {
            if (!this.muteds.containsKey(event.getPlayer().getUniqueId()))return;
            if (System.currentTimeMillis() - this.muteds.get(event.getPlayer().getUniqueId()) <= 10000) {
                event.setCancel(true);
                event.setCancelMessage("§cVous êtes actuellement§b gelé§c par§b Doma§c.");
            }
        }
    }
}