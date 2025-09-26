package fr.nicknqck.roles.ns.orochimaru;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.akatsuki.ItachiV2;
import fr.nicknqck.roles.ns.builders.EUchiwaType;
import fr.nicknqck.roles.ns.builders.IUchiwa;
import fr.nicknqck.roles.ns.builders.OrochimaruRoles;
import fr.nicknqck.roles.ns.orochimaru.edov2.OrochimaruV2;
import fr.nicknqck.roles.ns.power.Amaterasu;
import fr.nicknqck.roles.ns.power.Genjutsu;
import fr.nicknqck.roles.ns.power.Izanagi;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SasukeV2 extends OrochimaruRoles implements IUchiwa, Listener {

    private boolean orochimaruDEAD = false;
    private boolean itachiDEAD = false;

    public SasukeV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.INTELLIGENT;
    }

    @Override
    public String getName() {
        return "Sasuke";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Sasuke;
    }

    @Override
    public @NonNull EUchiwaType getUchiwaType() {
        return EUchiwaType.IMPORTANT;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        addPower(new Amaterasu(this), true);
        addPower(new SusanoPower(this), true);
        addPower(new Izanagi(this));
        addKnowedRole(OrochimaruV2.class);
        EventUtils.registerRoleEvent(this);
        setChakraType(Chakras.KATON);
        setCanBeHokage(true);
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if (!gameState.getAttributedRole().contains(Roles.Itachi)) {
                onItachiKill(false);
                getGamePlayer().sendMessage("§cItachi§7 n'étant pas dans la composition de la partie vous avez reçus tout de même le bonus dû à son kill");
            }
        }, 5*20);
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if (!gameState.getAttributedRole().contains(Roles.Orochimaru)) {
                setTeam(TeamList.Sasuke);
                getGamePlayer().sendMessage("§5Orochimaru§7 est mort, vous devenez maintenant un rôle§e Solitaire§7, pour vous aidez vous obtenez §c3❤ supplémentaire§7 ainsi que l'effet §cForce I§7 de manière§c permanente§7.", "§7Pour vous aidez à venger votre clan, vous obtenez un traqueur qui pointe en direction de§c Itachi§7.");
                giveHealedHeartatInt(3.0);
                givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0, false, false), EffectWhen.PERMANENT);
                for (@NonNull final GamePlayer gamePlayer : getGameState().getGamePlayer().values()) {
                    if (!gamePlayer.isAlive())continue;
                    if (gamePlayer.getRole() == null)continue;
                    if (!(gamePlayer.getRole() instanceof ItachiV2))continue;
                    @NonNull final ItachiV2 itachi = (ItachiV2) gamePlayer.getRole();
                    new ItachiTraqueur(getGameState(), itachi, this);
                    break;
                }
                this.orochimaruDEAD = true;
            }
        }, 10*20);
    }

    @EventHandler
    private void UHCDeathEvent(@NonNull final UHCDeathEvent event) {
        if (event.isCancelled())return;
        if (event.getRole() instanceof OrochimaruV2 && getTeam() != TeamList.Sasuke) {
            setTeam(TeamList.Sasuke);
            getGamePlayer().sendMessage("§5Orochimaru§7 est mort, vous devenez maintenant un rôle§e Solitaire§7, pour vous aidez vous obtenez §c3❤ supplémentaire§7 ainsi que l'effet §cForce I§7 de manière§c permanente§7.", "§7Pour vous aidez à venger votre clan, vous obtenez un traqueur qui pointe en direction de§c Itachi§7.");
            giveHealedHeartatInt(3.0);
            givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0, false, false), EffectWhen.PERMANENT);
            for (@NonNull final GamePlayer gamePlayer : event.getGameState().getGamePlayer().values()) {
                if (!gamePlayer.isAlive())continue;
                if (gamePlayer.getRole() == null)continue;
                if (!(gamePlayer.getRole() instanceof ItachiV2))continue;
                @NonNull final ItachiV2 itachi = (ItachiV2) gamePlayer.getRole();
                new ItachiTraqueur(event.getGameState(), itachi, this);
                break;
            }
            this.orochimaruDEAD = true;
        }
    }
    @EventHandler
    private void UHCKillEvent(@NonNull final UHCPlayerKillEvent event) {
        if (event.isCancel())return;
        if (event.getGamePlayerKiller() == null)return;
        if (event.getKiller().getUniqueId().equals(getPlayer())) {
            if (event.getGameState().hasRoleNull(event.getVictim().getUniqueId()))return;
            @NonNull final RoleBase role = event.getGameState().getGamePlayer().get(event.getVictim().getUniqueId()).getRole();
            if (role instanceof ItachiV2) {
                onItachiKill(true);
            }
        }
    }
    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .addCustomLine(
                        !orochimaruDEAD ?
                                "§7A la mort de§5 Orochimaru§7 vous deviendrez un rôle§e solitaire§7, pour ce faire vous gagnerez§c 3❤ permanents§7 ainsi que l'effet§c Force I permanent§7, de plus vous obtiendrez pendant§c 5 minutes§7 un traqueur vers§c Itachi§7."
                                :
                                ""
                )
                .addCustomLine(
                        !itachiDEAD ?
                                "§7Si vous tuez§c Itachi§7 vous obtiendrez§c 2❤ permanent§7 ainsi qu'un§c Genjutsu§7."
                                :
                                ""
                )
                .getText();
    }
    private void onItachiKill(boolean msg) {
        if (msg) {
            getGamePlayer().sendMessage("§7Bravo, vous avez enfin vengé votre clan, était-ce une bonne idée ? Seul l'avenir nous le dira, en attendant vous avez obtenue §c2❤ supplémentaire§7 ainsi que les§c Genjutsus§7 de§c Itachi§7 (§c/ns me pour plus détail§7).");
        }
        this.itachiDEAD = true;
        addPower(new Genjutsu(this), true);
        giveHealedHeartatInt(2);
    }
    private static class ItachiTraqueur extends BukkitRunnable {

        private final GameState gameState;
        private final ItachiV2 itachi;
        private final SasukeV2 sasuke;
        private int timeRemaining;

        private ItachiTraqueur(GameState gameState, ItachiV2 itachi, SasukeV2 sasuke) {
            this.gameState = gameState;
            this.itachi = itachi;
            this.sasuke = sasuke;
            this.timeRemaining = 60*5;
            this.sasuke.getGamePlayer().getActionBarManager().addToActionBar("sasuke.itachitraqueur", "§cItachi§7: "+ Loc.getDirectionMate(this.sasuke.getGamePlayer(), this.itachi.getGamePlayer(), false));
            runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
        }

        @Override
        public void run() {
            if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            this.sasuke.getGamePlayer().getActionBarManager().updateActionBar("sasuke.itachitraqueur", "§cItachi§7: "+ Loc.getDirectionMate(this.sasuke.getGamePlayer(), this.itachi.getGamePlayer(), false));
            if (!this.itachi.getGamePlayer().isAlive()) {
                this.sasuke.getGamePlayer().getActionBarManager().removeInActionBar("sasuke.itachitraqueur");
                this.sasuke.getGamePlayer().sendMessage("§cItachi§7 est mort, votre traque s'arrête ici...");
                cancel();
                return;
            }
            if (this.timeRemaining <= 0) {
                this.sasuke.getGamePlayer().getActionBarManager().removeInActionBar("sasuke.itachitraqueur");
                this.sasuke.getGamePlayer().sendMessage("§7Votre traqueur en direction d'§cItachi§7 s'estompe...");
                cancel();
                return;
            }
            this.timeRemaining--;
        }
    }
    private static class SusanoPower extends ItemPower {

        protected SusanoPower(@NonNull RoleBase role) {
            super("Susano (Sasuke)", new Cooldown(60*20), new ItemBuilder(Material.NETHER_STAR).setName("§c§lSusanô"), role,
                    "§7Vous permet d'obtenir l'effet§c Résistance I§7 pendant§c 5 minutes§7. (1x/20m)");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                player.sendMessage("§cActivation du§l Susanô§c.");
                final ArcDesFlamesPower flamesPower = new ArcDesFlamesPower(this.getRole());
                getRole().giveItem(player, true, flamesPower.bow);
                this.getRole().addPower(flamesPower);
                new SusanoPower.SusanoRunnable(this.getRole().getGameState(), this.getRole().getGamePlayer(), flamesPower);
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60*5, 0, false, false), EffectWhen.NOW);
                return true;
            }
            return false;
        }
        private static class SusanoRunnable extends BukkitRunnable {

            private final GameState gameState;
            private final GamePlayer gamePlayer;
            private final ArcDesFlamesPower flamesPower;
            private int timeLeft = 60*5;

            private SusanoRunnable(GameState gameState, GamePlayer gamePlayer, ArcDesFlamesPower flamesPower) {
                this.gameState = gameState;
                this.gamePlayer = gamePlayer;
                this.flamesPower = flamesPower;
                this.gamePlayer.getActionBarManager().addToActionBar("sasuke.susano", "§bTemp restant du§c§l Susanô§b: "+ StringUtils.secondsTowardsBeautiful(this.timeLeft));
                runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (this.timeLeft <= 0) {
                    this.gamePlayer.getActionBarManager().removeInActionBar("sasuke.susano");
                    this.gamePlayer.sendMessage("§cVotre§l Susanô§c s'arrête");
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        this.gamePlayer.getRole().getPowers().remove(this.flamesPower);
                        final Player player = Bukkit.getPlayer(this.gamePlayer.getUuid());
                        if (player != null) {
                            player.getInventory().remove(this.flamesPower.bow);
                        }
                    });
                    cancel();
                    return;
                }
                this.timeLeft--;
                this.gamePlayer.getActionBarManager().updateActionBar("sasuke.susano", "§bTemp restant du§c§l Susanô§b: "+StringUtils.secondsTowardsBeautiful(this.timeLeft));
            }
        }
        private static class ArcDesFlamesPower extends Power implements Listener {

            private final ItemStack bow;

            public ArcDesFlamesPower(@NonNull RoleBase role) {
                super("Honõ no ko", new Cooldown(10), role);
                this.bow = new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 7).setName("§cHonõ no ko").setUnbreakable(true).setDroppable(false).toItemStack();
                EventUtils.registerRoleEvent(this);
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                return true;
            }
            @EventHandler
            private void ProjectileLaunch(@NonNull final ProjectileLaunchEvent event) {
                if (event.isCancelled())return;
                if (!(event.getEntity() instanceof Arrow))return;
                if (event.getEntity().getShooter() == null)return;
                if (!(event.getEntity().getShooter() instanceof Player))return;
                @NonNull final Player shooter = (Player) event.getEntity().getShooter();
                if (!shooter.getUniqueId().equals(getRole().getPlayer()))return;
                if (!shooter.getItemInHand().isSimilar(this.bow))return;
                if (getCooldown().isInCooldown()) {
                    shooter.sendMessage("§cVous êtes en cooldown: §b"+this.getCooldown().getCooldownRemaining()+"s");
                    event.setCancelled(true);
                    return;
                }
                if (!checkUse(shooter, new HashMap<>()))return;
                @NonNull final Arrow arrow = (Arrow) event.getEntity();
                arrow.setFireTicks(800);
                arrow.setMetadata("sasuke.susano.hononoko", new FixedMetadataValue(Main.getInstance(), shooter));
            }
            @EventHandler
            private void EntityDamageByEntityEvent(@NonNull final EntityDamageByEntityEvent event) {
                if (!(event.getDamager() instanceof Arrow))return;
                if (!(event.getEntity() instanceof Player))return;
                @NonNull final Arrow arrow = (Arrow) event.getDamager();
                if (!(arrow.getShooter() instanceof Player))return;
                if (!((Player) arrow.getShooter()).getUniqueId().equals(getRole().getPlayer()))return;
                if (arrow.hasMetadata("sasuke.susano.hononoko")) {
                    event.getEntity().setFireTicks(800);
                    getRole().getGamePlayer().sendMessage("§7Une flèche de§c Honõ no ko§7 a toucher§c "+((Player) event.getEntity()).getDisplayName());
                }
            }
        }
    }
}