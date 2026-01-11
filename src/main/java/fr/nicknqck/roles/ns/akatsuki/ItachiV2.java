package fr.nicknqck.roles.ns.akatsuki;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.AkatsukiRoles;
import fr.nicknqck.roles.ns.builders.EUchiwaType;
import fr.nicknqck.roles.ns.builders.IUchiwa;
import fr.nicknqck.roles.ns.power.*;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.powers.Cooldown;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class ItachiV2 extends AkatsukiRoles implements IUchiwa {

    public ItachiV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.GENIE;
    }

    @Override
    public String getName() {
        return "Itachi";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Itachi;
    }

    @Override
    public @NonNull EUchiwaType getUchiwaType() {
        return EUchiwaType.IMPORTANT;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        addPower(new SuperSusanoPower(this, new TheSubClass(this),
                "§fClique droit§7: Vous offre pendant§c 5 minutes§7 l'effet§9 Résistance I§7.",
                "",
                "§fClique gauche§7: Pendant§c 2 minutes§7, vos coups infligeront§c 35%§7 de§c dégâts supplémentaires§7",
                "§7et vous recevrez§c 10%§7 de§c dégâts en moins§7 (En plus de vos effets)."), true);
        addPower(new Genjutsu(this), true);
        addPower(new Amaterasu(this), true);
        addPower(new Izanagi(this));
        setChakraType(Chakras.KATON);
        addKnowedRole(KisameV2.class);
        super.RoleGiven(gameState);
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .getText();
    }

    private static final class TheSubClass extends SubSusanoPower implements Listener {

        private boolean activate = false;

        public TheSubClass(@NonNull RoleBase role) {
            super("§cSusanô (Armes légendaires)", new Cooldown(150), role);
        }

        @Override
        public void onSusanoEnd() {
            this.activate = false;
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            this.activate = true;
            new BukkitRunnable() {
                private int timeLeft = 120;
                @Override
                public void run() {
                    if (!GameState.inGame()) {
                        cancel();
                        return;
                    }
                    getRole().getGamePlayer().getActionBarManager().updateActionBar("itachi.sub", "§bTemps restant (§cArc Divin§b):§c "+ StringUtils.secondsTowardsBeautiful(timeLeft));
                    if (this.timeLeft <= 0 || !activate) {
                        activate = false;
                        getRole().getGamePlayer().getActionBarManager().removeInActionBar("itachi.sub");
                        getRole().getGamePlayer().sendMessage("§7Les effets de vos§c Armes légendaires§7 s'estompe...");
                        cancel();
                        return;
                    }
                    this.timeLeft--;
                }
            }.runTaskTimerAsynchronously(this.getPlugin(), 0, 20);
            return true;
        }
        @EventHandler
        private void onDoc(@NonNull final EntityDamageByEntityEvent event) {
            if (event.getDamager() instanceof Player && event.getDamager().getUniqueId().equals(getRole().getPlayer()) && activate) {
                event.setDamage(event.getDamage()*1.35);
            } else if (event.getEntity() instanceof Player && event.getEntity().getUniqueId().equals(getRole().getPlayer()) && activate) {
                event.setDamage(event.getDamage()*0.9);
            }
        }
    }
}