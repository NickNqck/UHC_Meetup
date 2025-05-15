package fr.nicknqck.roles.ns.orochimaru.edov2;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.OrochimaruRoles;
import fr.nicknqck.roles.ns.orochimaru.Jugo;
import fr.nicknqck.roles.ns.orochimaru.Karin;
import fr.nicknqck.roles.ns.orochimaru.Kimimaro;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.UUID;

public class KabutoV2 extends EdoOrochimaruRoles implements Listener {

    private HealPower healPower;
    private boolean karinDEAD = false;
    private boolean jugoDEAD = false;
    private boolean kimimaroDEAD = false, orochimaruDEAD = false, solo = false;

    public KabutoV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.INTELLIGENT;
    }

    @Override
    public String getName() {
        return "Kabuto";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Kabuto;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        setChakraType(Chakras.SUITON);
        addKnowedRole(OrochimaruV2.class);
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false), EffectWhen.PERMANENT);
        this.healPower = new HealPower(this);
        addPower(this.healPower, true);
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
            if (!this.karinDEAD && !gameState.getAttributedRole().contains(GameState.Roles.Karin)) {
                onKarinDeath();
            }
            if (!this.jugoDEAD && !gameState.getAttributedRole().contains(GameState.Roles.Jugo)) {
                onJugoDeath();
            }
            if (!this.kimimaroDEAD && !gameState.getAttributedRole().contains(GameState.Roles.Kimimaro)) {
                onKimimaruDeath();
            }
            if (!this.orochimaruDEAD && !gameState.getAttributedRole().contains(GameState.Roles.Orochimaru)) {
                onOrochimaruDeath();
            }
            tryProcSolo(gameState);
        }, 20*10);
        EventUtils.registerRoleEvent(this);
        super.RoleGiven(gameState);
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .addCustomLine(
                        !karinDEAD ?
                                "§7A la mort de§5 Karin§7 votre§d Soins§7 soignera de§c 5❤§7 au lieu de§c 2❤§7.":""
                )
                .addCustomLine(
                        !this.jugoDEAD ?
                                "§7A la mort de§5 Jugo§7, vous recevrez§c 10%§7 de§e Speed§7.":""
                )
                .addCustomLine(
                        !this.kimimaroDEAD ?
                                "§7A la mort de§5 Kimimaro§7, vous subirez§c 10%§7 de dégâts en moins et infligerez§c 10%§7 de dégâts supplémentaire.":""
                )
                .addCustomLine(
                        !this.orochimaruDEAD ?
                                "§7A la mort de§5 Orochimaru§7, vous obtiendrez l'accès l'§5Edo Tensei§7.":""
                )
                .addCustomLine(
                        !solo ?
                                "§7Si vous êtes le dernier disciple d'§5Orochimaru§7, vous devriendrez un rôle§e Solitaire§7.":""
                )
                .getText();
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onDie(final UHCDeathEvent event) {
        if (event.isCancelled())return;
        if (event.getRole() instanceof Karin && !this.karinDEAD) {
            onKarinDeath();
        }
        if (event.getRole() instanceof Jugo && !this.jugoDEAD) {
            onJugoDeath();
        }
        if (event.getRole() instanceof Kimimaro && !kimimaroDEAD) {
            onKimimaruDeath();
        }
        if (event.getRole() instanceof OrochimaruV2 && !orochimaruDEAD) {
            onOrochimaruDeath();
        }
        tryProcSolo(event.getGameState());
    }
    private void onKarinDeath() {
        getGamePlayer().sendMessage("§5Karin§7 est morte, en inspiration de son pouvoirs de guérison votre§d Soins§7 s'améliore, il soignera maintenant de§c 5❤§7 au lieu de§c 2❤§7.");
        this.healPower.toHeal = 10.0;
        this.karinDEAD = true;
    }
    private void onJugoDeath() {
        getGamePlayer().sendMessage("§5Jugo§7 est mort, en implémentant sa§5 marque maudite§7 vous avez reçus§c 10%§7 de§e Speed§7.");
        Bukkit.getScheduler().runTask(Main.getInstance(), () -> addSpeedAtInt(owner, 10f));
        this.jugoDEAD = true;
    }
    private void onKimimaruDeath() {
        getGamePlayer().sendMessage("§5Kimimaro§7 est mort, grâce à la génétique de ses os vous avez pus renforcé votre corp, vous infligez§c 10%§7 de§c dégâts supplémentaires§7 et recevez§c 10%§7 de§c dégâts en moins§7.");
        this.kimimaroDEAD = true;
        addBonusforce(10);
        addBonusResi(10);
    }
    private void onOrochimaruDeath() {
        getGamePlayer().sendMessage("§7Maitre§5 Orochimaru§7 est malheureusement mort, en son hônneur vous allez vous aussi utiliser l'§5Edo Tensei§7.");
        addPower(new EdoTenseiPower(this), true);
        this.orochimaruDEAD = true;
    }
    private void tryProcSolo(@NonNull final GameState gameState) {
        int amountOrochimaru = 0;
        for (final GamePlayer gamePlayer : gameState.getGamePlayer().values()) {
            if (!gamePlayer.isAlive())continue;
            if (gamePlayer.getRole() == null)continue;
            if (gamePlayer.getRole().getTeam().equals(TeamList.Orochimaru) && gamePlayer.getRole() instanceof OrochimaruRoles) {
                amountOrochimaru++;
            }
        }
        if (amountOrochimaru == 1) {
            Bukkit.getScheduler().runTask(Main.getInstance(), this::procSolo);
        }
    }
    private void procSolo() {
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        setTeam(TeamList.Kabuto);
        giveHealedHeartatInt(2.0);
        getGamePlayer().sendMessage("§7Vous êtes le dernier disciple d'§5Orochimaru§7, en sa mémoire vous allez essayer de le venger, vous devenez un rôle§e Solitaire§7.");
        this.solo = true;
    }
    private static class HealPower extends ItemPower {

        private final Cooldown gaucheCD;
        private final Cooldown droiteCD;
        private double toHeal;

        public HealPower(@NonNull RoleBase role) {
            super("Soins", null, new ItemBuilder(Material.NETHER_STAR).setName("§dSoins"), role,
                    "§7Vous permet de vous soignez ou de soigner quelqu'un d'autre: ",
                    "",
                    "§fClique gauche§7: Vous soigne de§c 2❤§7. (1x/1m30s)",
                    "",
                    "§fClique droite§7: Soigne la personne visé de§c 2❤§7. (1x/2m)");
            this.toHeal = 4.0;
            this.gaucheCD = new Cooldown(90);
            this.droiteCD = new Cooldown(120);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                event.setCancelled(true);
                if (event.getAction().name().contains("LEFT")) {
                    if (gaucheCD.isInCooldown()) {
                        player.sendMessage("§cImpossible de vous soignez, vous êtes en cooldown:§b "+ StringUtils.secondsTowardsBeautiful(this.gaucheCD.getCooldownRemaining()));
                        return false;
                    }
                    player.setHealth(Math.min(player.getMaxHealth(), player.getHealth()+toHeal));
                    player.sendMessage("§7Vous vous êtes soignés de§c "+new DecimalFormat("0").format(this.toHeal/2)+"❤§7.");
                    this.gaucheCD.use();
                    return true;
                } else if (event.getAction().name().contains("RIGHT")) {
                    if (this.droiteCD.isInCooldown()) {
                        player.sendMessage("§cImpossible de soigner un autre joueur, vous êtes en cooldown:§b "+StringUtils.secondsTowardsBeautiful(this.droiteCD.getCooldownRemaining()));
                        return false;
                    }
                    final Player target = RayTrace.getTargetPlayer(player, 30, null);
                    if (target == null) {
                        player.sendMessage("§cIl faut viser un joueur !");
                        return false;
                    }
                    target.setHealth(Math.min(target.getMaxHealth(), target.getHealth()+this.toHeal));
                    player.sendMessage("§7Vous avez soignés§c "+target.getDisplayName()+"§7 de§c "+new DecimalFormat("0").format(this.toHeal/2)+"❤§7.");
                    this.droiteCD.use();
                    return true;
                }
            }
            return false;
        }
    }
}