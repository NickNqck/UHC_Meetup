package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.EffectGiveEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class RaikageV2 extends ShinobiRoles {

    public RaikageV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.INTELLIGENT;
    }

    @Override
    public String getName() {
        return "Yondaime Raikage";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Raikage;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        super.RoleGiven(gameState);
        addKnowedRole(KillerBeeV2.class);
        addKnowedRole(KillerBee.class);
        addKnowedRole(YondaimeRaikage.class);
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        addPower(new ArmureRaiton(this), true);
        setChakraType(Chakras.RAITON);
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }
    private static class ArmureRaiton extends ItemPower implements Listener {

        private int timeLeft = 60*5;
        private boolean activate = false;

        public ArmureRaiton(@NonNull RoleBase role) {
            super("Armure Raiton", null, new ItemBuilder(Material.NETHER_STAR).setName("§eArmure Raiton"), role,
                    "§7Vous possédez une banque de temp de§c 5 minutes§7 initialement qui ce remplis de§c 1 minutes§7 par§c kill§7 obtenu",
                    "",
                    "§7Via un clique quelconque vous pouvez§a activer§7/§cdésactiver§7 votre§e Armure Raiton§7.",
                    "§7Quand elle est§a activer§7 vous avez les effets§e Speed II§7 ainsi que§9 Résistance I§7.");
            new ArmureRunnable(this);
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (!this.activate) {
                    this.activate = true;
                    player.sendMessage("§7Activation de voter§e Armure Raiton§7.");
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 1, false, false), true);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 0, false, false), true);
                } else {
                    this.activate = false;
                    player.sendMessage("§7Vous avez désactiver votre§e Armure Raiton§7.");
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), true);
                }
                return true;
            }
            return false;
        }
        @EventHandler
        private void onEffectGive(EffectGiveEvent event) {
            if (event.isCancelled())return;
            if (!event.getPlayer().getUniqueId().equals(getRole().getPlayer()))return;
            if (!activate)return;
            if (event.getPotionEffect().getType().equals(PotionEffectType.SPEED) && event.getPotionEffect().getAmplifier() < 1) {
                event.setCancelled(true);
            }
        }
        @EventHandler
        private void onKill(UHCPlayerKillEvent event) {
            if (event.getKiller().getUniqueId().equals(getRole().getPlayer())) {
                this.timeLeft+=60;
                event.getKiller().sendMessage("§7Vous avez gagner§c 60 secondes§7 dans votre§c banque de temp§7.");
            }
        }
        private static class ArmureRunnable extends BukkitRunnable {

            private final ArmureRaiton armureRaiton;

            private ArmureRunnable(ArmureRaiton armureRaiton) {
                this.armureRaiton = armureRaiton;
                runTaskTimerAsynchronously(Main.getInstance(), 20, 20);
            }

            @Override
            public void run() {
                if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                final Player owner = Bukkit.getPlayer(this.armureRaiton.getRole().getPlayer());
                if (owner != null) {
                    if (owner.getItemInHand() != null && owner.getItemInHand().isSimilar(this.armureRaiton.getItem())) {
                        this.armureRaiton.getRole().getGamePlayer().getActionBarManager().addToActionBar("raikage.armure", "§bTemp restant (§eArmure Raiton§b): "+ StringUtils.secondsTowardsBeautiful(this.armureRaiton.timeLeft));
                        return;
                    }
                }
                if (!this.armureRaiton.activate) {
                    if (this.armureRaiton.getRole().getGamePlayer().getActionBarManager().containsKey("raikage.armure")) {
                        this.armureRaiton.getRole().getGamePlayer().getActionBarManager().removeInActionBar("raikage.armure");
                    }
                    return;
                }
                if (!this.armureRaiton.getRole().getGamePlayer().isAlive()) {
                    this.armureRaiton.activate = false;
                    this.armureRaiton.getRole().getGamePlayer().sendMessage("§7Votre§e Armure Raiton§7 s'est arrêter automatiquement suite à votre mort");
                    return;
                }
                if (this.armureRaiton.timeLeft <= 0) {
                    this.armureRaiton.getRole().getGamePlayer().sendMessage("§7Vous n'avez plus asser de temp pour que votre§e Armure Raiton§7 fonctionne");
                    this.armureRaiton.activate = false;
                    final Player player = Bukkit.getPlayer(this.armureRaiton.getRole().getPlayer());
                    if (player != null) {
                        Bukkit.getScheduler().runTask(Main.getInstance(), () -> player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false), true));
                    }
                    return;
                }
                this.armureRaiton.timeLeft--;
                this.armureRaiton.getRole().getGamePlayer().getActionBarManager().addToActionBar("raikage.armure", "§bTemp restant (§eArmure Raiton§b): "+ StringUtils.secondsTowardsBeautiful(this.armureRaiton.timeLeft));
                final Player player = Bukkit.getPlayer(this.armureRaiton.getRole().getPlayer());
                if (player != null) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 1, false, false), true);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 0, false, false), true);
                    });
                }
            }
        }
    }
}