package fr.nicknqck.roles.ds.slayers.pillier;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.*;

public class ObanaiV2 extends PilierRoles implements Listener{

    private TextComponent textComponent;
    private MitsuriV2 mitsuriV2;

    public ObanaiV2(UUID player) {
        super(player);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.EAU;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Obanai";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Obanai;
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
        return this.textComponent;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new SerpentCommand(this));
        addPower(new CrocDuSerpent(this), true);
        AutomaticDesc automaticDesc = new AutomaticDesc(this)
                .addCustomLine("§7Vous possédez l'effet§e Speed I§7 proche de§a Mitsuri")
                .setPowers(getPowers())
                .addCustomLine("§7A la mort de§a Mitsuri§7 vous obtenez l'effet§e Speed I§7 durant le cycle ou elle est morte, également, vous obtiendrez le§c pseudo§7 de son§c tueur§7.");
        this.textComponent = automaticDesc.getText();
        new SpeedRunnable(this).runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
            for (final GamePlayer gamePlayer : gameState.getGamePlayer().values()) {
                if (gamePlayer.isAlive() && gamePlayer.getRole() != null) {
                    if (gamePlayer.getRole() instanceof MitsuriV2) {
                        this.mitsuriV2 = (MitsuriV2) gamePlayer.getRole();
                        break;
                    }
                }
            }
        }, 20);
        EventUtils.registerRoleEvent(this);
    }
    @EventHandler
    private void onDie(UHCDeathEvent event) {
        if (event.getRole() == null)return;
        if (event.getRole() instanceof MitsuriV2) {
            if (event.getRole().getGamePlayer() == null)return;
            if (event.getRole().getGamePlayer().getKiller() == null)return;
            onMitsuriDie(event.getGameState().isNightTime() ? EffectWhen.NIGHT: EffectWhen.DAY, event.getRole().getGamePlayer().getKiller().getPlayerName());
        }
    }

    private void onMitsuriDie(EffectWhen effectWhen, @Nullable String killerName) {
        owner.sendMessage("§aMitsuri§7 est morte, votre§c seul§7 raison de vivre à disparu "+ (killerName != null ? "§7vous pouvez vous venger contre§c "+killerName+"§7, si vous en avez le courage...." : ""));
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), effectWhen);
    }
    private static class SpeedRunnable extends BukkitRunnable {

        private final ObanaiV2 obanai;

        private SpeedRunnable(ObanaiV2 obanai) {
            this.obanai = obanai;
        }

        @Override
        public void run() {
            if (!obanai.getGameState().getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (obanai.mitsuriV2 == null)return;
            Player owner = Bukkit.getPlayer(obanai.getPlayer());
            if (owner != null) {
                Player mitsuri = Bukkit.getPlayer(obanai.mitsuriV2.getPlayer());
                if (mitsuri != null) {
                    if (Loc.getNearbyPlayersExcept(owner, 15).contains(mitsuri)) {
                        Bukkit.getScheduler().runTask(Main.getInstance(), () -> owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false), true));
                    }
                }
            }
        }
    }
    private static class CrocDuSerpent extends ItemPower {

        protected CrocDuSerpent(@NonNull RoleBase role) {
            super("§2Croc du Serpent", new Cooldown(60*7), new ItemBuilder(Material.FERMENTED_SPIDER_EYE).setName("§2Croc du Serpent"), role,
                    "§7En frappant un joueur, vous permet de donner§c 5 secondes§7 de §2Poison III§7 à la§c cible");
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            if (getInteractType().equals(InteractType.ATTACK_ENTITY)) {
                UHCPlayerBattleEvent event = (UHCPlayerBattleEvent) args.get("event");
                ((Player)event.getOriginEvent().getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 2, false, false));
                player.sendMessage("§7Vous infusez votre§2 venin§7 dans "+event.getVictim().getPlayerName());
                return true;
            }
            return false;
        }
    }
    private static class SerpentCommand extends CommandPower implements Listener {

        private final Map<String, RoleBase[]> roleBaseMap;

        public SerpentCommand(@NonNull RoleBase role) {
            super("§6/ds drop", "drop", new Cooldown(60*10), role, CommandType.DS, "§7Vous permet de lacher votre serpent pendant§c 5 minutes§7,",
                    "§7A chaque mort, le rôle du tueur ainsi que 2 rôle aléatoires de la partie seront enregistrer",
                    "",
                    "§7Au bout des§c 5 minutes§7 le§2 serpent§7 reviens et vous donnera§c TOUTE§7 les informations accumulé");
            this.roleBaseMap = new HashMap<>();
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            player.sendMessage("§7Vous avez lancer votre§2 Serpent");
            EventUtils.registerEvents(this);
            Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> {
                EventUtils.unregisterEvents(this);
                if (roleBaseMap.isEmpty()) {
                    player.sendMessage("§7Votre§2 Serpent§7 n'a trouver aucune information");
                } else {
                    for (final String pseudo : roleBaseMap.keySet()) {
                        final List<RoleBase> roles = new ArrayList<>(Arrays.asList(roleBaseMap.get(pseudo)));
                        Collections.shuffle(roles, Main.RANDOM);
                        player.sendMessage(new String[]{
                                "§7Voici la liste des potentiels tueurs de§c "+pseudo,
                                "",
                                roles.get(0).getName()+"§7, "+roles.get(1).getName()+"§7, "+roles.get(2).getName()
                        });

                    }
                }
            }, 20*60*5);
            return true;
        }
        @EventHandler
        private void onUHCDie(UHCPlayerKillEvent event) {
            if (event.getGamePlayerKiller() == null)return;
            if (event.getGamePlayerKiller().getRole() == null)return;
            final Map<UUID, GamePlayer> map = new HashMap<>(event.getGameState().getGamePlayer());
            map.remove(event.getGamePlayerKiller().getUuid(), event.getGamePlayerKiller());
            map.remove(event.getVictim().getUniqueId());
            final List<GamePlayer> list = new ArrayList<>(map.values());
            Collections.shuffle(list, Main.RANDOM);
            RoleBase zero = list.get(0).getRole();
            if (zero == null) {
                zero = event.getGamePlayerKiller().getRole();
            }
            RoleBase un = list.get(1).getRole();
            if (un == null) {
                un = event.getGamePlayerKiller().getRole();
            }
            RoleBase deux = list.get(1).getRole();
            if (deux == null) {
                deux = event.getGamePlayerKiller().getRole();
            }
            roleBaseMap.put(event.getVictim().getName(), new RoleBase[]{
                    zero,
                    un,
                    deux
            });
        }
    }
}