package fr.nicknqck.roles.ds.solos;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KyogaiV3 extends DemonsRoles {

    public KyogaiV3(UUID player) {
        super(player);
    }

    @Override
    public @NonNull DemonType getRank() {
        return DemonType.DEMON;
    }

    @Override
    public String getName() {
        return "Kyogai§7 (§6V2§7)";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.KyogaiV2;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Solo;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new TambourItem(this), true);
        setMaxHealth(26.0);
        owner.setHealth(owner.getMaxHealth());
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 80, 0, false, false), EffectWhen.NIGHT);
        super.RoleGiven(gameState);
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }
    private static class TambourItem extends ItemPower implements Listener {

        private final CielPower cielPower;
        private final SolPower solPower;
        private final RetournagePower retournagePower;
        private final Map<UUID, Long> powerUsedsPlayers;

        public TambourItem(@NonNull RoleBase role) {
            super("Tambour", new Cooldown(1), new ItemBuilder(Material.STICK).setName("§eTambour"), role,
                    "§7Cette objet possède plusieurs utilisation en fonction du clique effectué:",
                    "",
                    "§8 -§b Ciel§7 (§cClique gauche§7): Le joueur visé se verra regarder le§b ciel§7.",
                    "§8 -§e Sol§7 (§cClique droit§7): Le joueur visé se verra regarder le§e sol§7.",
                    "§8 -§c Retournement§7 (§cShift§7 +§c Clique§7): Le joueur visé se fera retourner.",
                    "",
                    "§7Le joueur visé devra se situé à moins de§c 30 blocs§7 de§c vous§7.",
                    "§7Si la personne visé vous§c attaque§7 dans les§c 3 secondes§7 après avoir été§c toucher§7",
                    "§7par votre§c pouvoir§7 vous gagnerez§c 25 secondes§7 de§e Speed I§7."
            );
            setSendCooldown(false);
            this.cielPower = new CielPower(role);
            role.addPower(cielPower);
            this.solPower = new SolPower(role);
            role.addPower(solPower);
            this.retournagePower = new RetournagePower(role);
            role.addPower(retournagePower);
            getShowCdRunnable().setCustomText(true);
            EventUtils.registerRoleEvent(this);
            this.powerUsedsPlayers = new HashMap<>();
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final Player target = RayTrace.getTargetPlayer(player, 30, null);
                if (target == null) {
                    player.sendMessage("§cIl faut viser un joueur.");
                    return false;
                }
                final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                map.put("target", target);
                if (player.isSneaking()) {
                    if (this.retournagePower.checkUse(player, map)) {
                        this.powerUsedsPlayers.put(target.getUniqueId(), System.currentTimeMillis());
                        return true;
                    }
                }
                if (event.getAction().name().contains("LEFT")) {
                    if (this.cielPower.checkUse(player, map)) {
                        this.powerUsedsPlayers.put(target.getUniqueId(), System.currentTimeMillis());
                        return true;
                    }
                }
                if (event.getAction().name().contains("RIGHT")) {
                    if (this.solPower.checkUse(player, map)) {
                        this.powerUsedsPlayers.put(target.getUniqueId(), System.currentTimeMillis());
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void tryUpdateActionBar() {
            getShowCdRunnable().setCustomTexte(
                    "§bCiel§7 "+
                            (
                                    this.cielPower.getCooldown().isInCooldown() ?
                                            "est en§c cooldown§7:§c "+ StringUtils.secondsTowardsBeautiful(this.cielPower.getCooldown().getCooldownRemaining())
                                            :
                                            "est§c utilisable"
                                    )+
                            "§7 | "+
                            "§eSol§7 "+
                            (
                                    this.solPower.getCooldown().isInCooldown() ?
                                            "est en§c cooldown§7:§c "+StringUtils.secondsTowardsBeautiful(this.solPower.getCooldown().getCooldownRemaining())
                                            :
                                            "est§c utilisable"
                                    )+
                            "§7 | "+
                            "§cRetournement§7 "+
                            (
                                    this.retournagePower.getCooldown().isInCooldown() ?
                                            "est en§c cooldown§7:§c "+StringUtils.secondsTowardsBeautiful(this.retournagePower.getCooldown().getCooldownRemaining())
                                            :
                                            "est§c utilisable"
                                    )
            );
        }

        @EventHandler
        private void onDamage(final EntityDamageByEntityEvent event) {
            if (!(event.getDamager() instanceof Player))return;
            if (!(event.getEntity() instanceof Player))return;
            if (!event.getDamager().getUniqueId().equals(getRole().getPlayer()))return;
            if (!this.powerUsedsPlayers.containsKey(event.getEntity().getUniqueId()))return;
            if (System.currentTimeMillis() - this.powerUsedsPlayers.get(event.getEntity().getUniqueId()) <= 3000) {
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*25, 0, false, false), EffectWhen.NOW);
                event.getDamager().sendMessage("§c"+((Player) event.getEntity()).getDisplayName()+"§7 vous a offert§c 25 secondes§7 de§e Speed§7.");
            }
        }

        private static class CielPower extends Power {

            public CielPower(@NonNull RoleBase role) {
                super("Kyogai (Ciel)", new Cooldown(30), role);
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                if (map.containsKey("target") && map.get("target") instanceof Player) {
                    final Player target = (Player) map.get("target");
                    target.teleport(new Location(target.getWorld(), target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ(), target.getEyeLocation().getYaw(), -90));
                    player.sendMessage("§c"+target.getDisplayName()+"§7 regarde maintenant le§b ciel§7.");
                    target.sendMessage("§eKyogai§7 vous a fait regarder le§b ciel§7.");
                    return true;
                }
                return false;
            }
        }
        private static class SolPower extends Power {

            public SolPower(@NonNull RoleBase role) {
                super("Kyogai (Sol)", new Cooldown(30), role);
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                if (map.containsKey("target") && map.get("target") instanceof Player) {
                    final Player target = (Player) map.get("target");
                    target.teleport(new Location(target.getWorld(), target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ(), target.getEyeLocation().getYaw(), 90));
                    player.sendMessage("§c"+target.getDisplayName()+"§7 regarde maintenant le§b sol§7.");
                    target.sendMessage("§eKyogai§7 vous a fait regarder le sol");
                    return true;
                }
                return false;
            }
        }
        private static class RetournagePower extends Power {

            public RetournagePower(@NonNull RoleBase role) {
                super("Retournage (Kyogai)", new Cooldown(30), role);
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                if (map.containsKey("target") && map.get("target") instanceof Player) {
                    final Player target = (Player) map.get("target");
                    Loc.inverserDirectionJoueur(target);
                    player.sendMessage("§7Vous avez retourner§c "+target.getDisplayName());
                    target.sendMessage("§7Vous avez été retourner par§e Kyogai§7.");
                    return true;
                }
                return false;
            }
        }
    }
}