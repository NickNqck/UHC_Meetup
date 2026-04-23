package fr.nicknqck.roles.ns.solo.jubi;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.EffectWhen;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.RoleGiveEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.events.custom.roles.PowerActivateAfterCheckEvent;
import fr.nicknqck.interfaces.IRoles;
import fr.nicknqck.interfaces.IUncompatibleRole;
import fr.nicknqck.interfaces.RoleCustomLore;
import fr.nicknqck.interfaces.UpdatablePowerLore;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.enums.EChakras;
import fr.nicknqck.enums.Intelligence;
import fr.nicknqck.roles.ns.akatsuki.ItachiV2;
import fr.nicknqck.roles.ns.builders.JubiRoles;
import fr.nicknqck.roles.ns.power.Amaterasu;
import fr.nicknqck.roles.ns.power.Genjutsu;
import fr.nicknqck.roles.ns.power.Izanagi;
import fr.nicknqck.roles.ns.power.SuperSusanoPower;
import fr.nicknqck.utils.ArrowTargetUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class JubiSasuke extends JubiRoles implements IUncompatibleRole, RoleCustomLore, Listener {

    private boolean killItachi = false;

    public JubiSasuke(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.GENIE;
    }

    @Override
    public EChakras[] getChakrasCanHave() {
        return new EChakras[] {
                EChakras.KATON
        };
    }

    @Override
    public String getName() {
        return "Sasuke";
    }

    @Override
    public @NonNull IRoles<?> getRoles() {
        return Roles.JubiSasuke;
    }

    @Override
    public IRoles<?>[] getUncompatibleList() {
        return new IRoles[] {
                Roles.Obito,
                Roles.Sasuke
        };
    }

    @Override
    public void RoleGiven(GameState gameState) {
        EventUtils.registerRoleEvent(this);
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        givePotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        addPower(new SuperSusanoPower(this, null,
                "§7Vous offre pendant§c 5 minutes§7 l'effet§9 Résistance I§7."), true);
        addPower(new Genjutsu(this), true);
        addPower(new Amaterasu(this), true);
        addPower(new ItachiTracker(this));
        addPower(new Observation(this));
        addPower(new Izanagi(this));
        getGamePlayer().startChatWith("§dSasuke:", "!", MadaraV2.class);
        giveHealedHeartatInt(2);
        super.RoleGiven(gameState);
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createAutomaticDesc(this)
                .addCustomLine(killItachi ? "" :
                        "§7Si vous parvenez à tuer§c Itachi§7, la distance pour savoir si un §7joueur utilise un pouvoir change (§c100§7 -> §c150§7)," +
                                " la distance §7pour savoir quel pouvoir a été utiliser a aussi changer (§c25§7 -> §c100§7),"+
                                "§7de plus, l'§cintégralité§7 de§c vos pouvoirs§7 verront leurs §ccooldowns diviser par deux§7."
                )
                .getText();
    }

    @Override
    public String[] getCustomLore(String amount, String gDesign) {
        return new String[] {
                "§7Peut devenir§a Hokage§7:§c Non",
                "§7Peut avoir un§a chakra§c aléatoire§7 parmi ceux-ci:§c Katon",
                "§7Ce rôle est incompatible avec le/les role(s): "+Roles.Obito.getItem().getItemMeta().getDisplayName()+"§7 et§d "+Roles.Sasuke.getItem().getItemMeta().getDisplayName(),
                amount,
                "",
                gDesign,
                "",
                "§7Ce rôle viens d'un univers ou§d Obito§7 est§c mort§7 écraser par le rocher",
                "§7(Inspirer de cette vidéo:§c https://youtu.be/iDIgKG1xbHI?si=5GnBelnNN4_Sugny§7)"
        };
    }
    @EventHandler
    private void onGiveRole(@NonNull final RoleGiveEvent event) {
        if (!event.isEndGive())return;
        if (event.getGameState().getDeadRoles().contains(Roles.Itachi)) {
            getGamePlayer().sendMessage("§cItachi§7 n'étant pas dans la partie, vous recevez les§c bonus§7 dû à son§c élimination§7.");
            onKillItachi();
        }
    }
    @EventHandler
    private void onKill(@NonNull final UHCPlayerKillEvent event) {
        if (event.getGamePlayerKiller() == null)return;
        if (!event.getGamePlayerKiller().check())return;
        if (event.getGamePlayerKiller().getUuid().equals(getPlayer())) {
            final GamePlayer gameVictim = event.getGameState().getGamePlayer().get(event.getVictim().getUniqueId());
            if (gameVictim.getRole() == null)return;
            if (gameVictim.getRole() instanceof ItachiV2) {
                onKillItachi();
            }
        }
    }
    private void onKillItachi() {
        this.killItachi = true;
        final List<Power> powerList = new ArrayList<>(getPowers());
        for (Power power : powerList) {
            if (power == null)continue;
            if (power.getCooldown() == null)continue;
            power.setCooldown(new Cooldown(power.getCooldown().getOriginalCooldown()/2));
            if (power instanceof Observation) {
                power.setDescriptions(new String[] {
                        "§7Lorsqu'un joueur utilise un§c pouvoir§7 à§c moins§7 de§c 150 blocs§7 vous saurez qu'il la utiliser",
                        "§7(Si la personne utilise son§c pouvoir§7 à moins de§c 100 blocs§7, vous saurez quel pouvoir a été utiliser)"
                });
            }
        }
        getPowers().clear();
        getPowers().addAll(powerList);
    }

    private static final class Observation extends Power implements Listener, UpdatablePowerLore {

        public Observation(@NonNull RoleBase role) {
            super("§cObservation§r", null, role,
                    "§7Lorsqu'un joueur utilise un§c pouvoir§7 à§c moins§7 de§c 100 blocs§7 vous saurez qu'il la utiliser",
                    "§7(Si la personne utilise son§c pouvoir§7 à moins de§c 25 blocs§7, vous saurez quel pouvoir a été utiliser)"
            );
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            return true;
        }
        @EventHandler
        private void onPowerUse(@NonNull final PowerActivateAfterCheckEvent event) {
            final Location myLoc = getRole().getGamePlayer().getLastLocation();
            if (event.getPlayer().getWorld().equals(myLoc.getWorld())) {
                final double distance = myLoc.distance(event.getPlayer().getLocation());
                final boolean killItachi = getRole() instanceof JubiSasuke && ((JubiSasuke) getRole()).killItachi;
                double distGood = killItachi ? 150 : 100;
                if (distance <= distGood) {
                    final double distPerfect = killItachi ? 100 : 25;
                    if (distance <= distPerfect) {
                        getRole().getGamePlayer().sendMessage("§b"+event.getPlayer().getName()+"§a a utiliser la technique \"§6"+(event.getPower() instanceof ItemPower ? ((ItemPower) event.getPower()).getItem().getItemMeta().getDisplayName() : event.getPower().getName())+"§a\".");
                    } else {
                        getRole().getGamePlayer().sendMessage("§b"+event.getPlayer().getName()+"§a a utiliser une technique");
                    }
                }

            }
        }

        @Override
        public String[] getCustomPowerLore() {
            final boolean killItachi = getRole() instanceof JubiSasuke && ((JubiSasuke) getRole()).killItachi;
            if (killItachi) {
                return new String[]{
                        "§7Lorsqu'un joueur utilise un§c pouvoir§7 à§c moins§7 de§c 150 blocs§7 vous saurez qu'il la utiliser",
                        "§7(Si la personne utilise son§c pouvoir§7 à moins de§c 100 blocs§7, vous saurez quel pouvoir a été utiliser)"
                };
            }
            return getDescriptions();
        }
    }
    private static final class ItachiTracker extends Power {

        public ItachiTracker(@NonNull RoleBase role) {
            super("§cVengeance§r", null, role,
                    "§7Tant que vous serez à§c plus§7 de§c 30 blocs§7 d'§cItachi§7,",
                    "§7une§c flèche§7 pointra dans sa§c direction§7.");
            new TrackerRunnable(this).runTaskTimerAsynchronously(getPlugin(), 1, 15);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            return true;
        }
        private static final class TrackerRunnable extends BukkitRunnable {

            private final ItachiTracker itachiTracker;

            private TrackerRunnable(ItachiTracker itachiTracker) {
                this.itachiTracker = itachiTracker;
            }

            @Override
            public void run() {
                if (!GameState.inGame()) {
                    cancel();
                    return;
                }
                ItachiV2 itachiV2 = null;
                for (GamePlayer gamePlayer : new ArrayList<>(itachiTracker.getRole().getGameState().getGamePlayer().values())) {
                    if (!gamePlayer.check())continue;
                    if (gamePlayer.getRole() instanceof ItachiV2) {
                        itachiV2 = (ItachiV2) gamePlayer.getRole();
                        break;
                    }
                }
                if (itachiV2 != null) {
                    final Location location = itachiV2.getGamePlayer().getLastLocation();
                    final Location ownerLoc = this.itachiTracker.getRole().getGamePlayer().getLastLocation();
                    if (ownerLoc.getWorld().equals(location.getWorld())) {
                        this.itachiTracker.getRole().getGamePlayer().getActionBarManager().updateActionBar("jubisuke.vengeance", "§cVengeance§b: "+ ArrowTargetUtils.calculateArrow(ownerLoc, location));
                    } else {
                        this.itachiTracker.getRole().getGamePlayer().getActionBarManager().updateActionBar("jubisuke.vengeance", "§cVengeance§b: "+location.getWorld().getName());
                    }
                } else {
                    this.itachiTracker.getRole().getGamePlayer().getActionBarManager().removeInActionBar("jubisuke.vengeance");
                }
            }
        }
    }
}