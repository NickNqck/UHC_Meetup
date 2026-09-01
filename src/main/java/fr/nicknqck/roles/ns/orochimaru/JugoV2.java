package fr.nicknqck.roles.ns.orochimaru;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.EChakras;
import fr.nicknqck.enums.EffectWhen;
import fr.nicknqck.enums.Intelligence;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.interfaces.IRoles;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.builders.OrochimaruRoles;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class JugoV2 extends OrochimaruRoles {

    public JugoV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.MOYENNE;
    }

    @Override
    public EChakras[] getChakrasCanHave() {
        return new EChakras[] {
                EChakras.DOTON,
                EChakras.SUITON,
                EChakras.FUTON
        };
    }

    @Override
    public String getName() {
        return "Jugo";
    }

    @Override
    public @NonNull IRoles<?> getRoles() {
        return Roles.Jugo;
    }

    @Override
    public @NonNull TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new TransformationErmite(this), true);
        addKnowedRole(KimimaroV2.class);
        addKnowedRole(OrochimaruRoles.class);
    }
    private static final class TransformationErmite extends ItemPower implements Listener {

        private final Senninka senninka;
        private int level = 0;

        public TransformationErmite(@NonNull RoleBase role) {
            super("§aTransformation ermite§r", new Cooldown(2), new ItemBuilder(Material.NETHER_STAR).setName("§aTransformation ermite"), role,
                    "§fClique gauche§7: Permet d'augmenter le niveau de§a Transformation ermite§7.",
                    "",
                    "§fClique droit§7: Permet de réduire le niveau de§a Transformation ermite§7.",
                    "",
                    "§8 -§c Niveau 0§7:",
                    "",
                    AllDesc.tab+"§7 Rien ne se passe de spécial.",
                    "",
                    "§8 -§c Niveau 1§7:",
                    "",
                    AllDesc.tab+"§7 Vous offre§e Speed I§7 et§c 10%§7 de§c force§7.",
                    AllDesc.tab+"§7 Coûte§c 6 points§5 senninka§7 par§c seconde§7.",
                    "",
                    "§8 -§c Niveau 2§7:",
                    "",
                    AllDesc.tab+"§7 Vous offre§e Speed I§7,§c Force I§7 et§c 10%§7 de§b résistance§7.",
                    AllDesc.tab+"§7 Coûte§c 12 points§5 seninka§7 par§c seconde§7."
            );
            setSendCooldown(false);
            getShowCdRunnable().setCustomText(true);
            this.senninka = new Senninka(role);
            role.addPower(this.senninka);
            new ErmiteRunnable(this).runTaskTimerAsynchronously(getPlugin(), 1, 20);
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                if (event.getAction().name().contains("RIGHT")) {
                    if (level == 0) {
                        player.sendMessage("§cErreur | Impossible d'aller plus bas !");
                        return false;
                    }
                    this.level--;
                    player.sendMessage("§7Votre "+getName()+"§7 est maintenant au§c niveau "+this.level);
                } else {
                    if (this.level == 2) {
                        player.sendMessage("§cErreur | Impossible d'aller plus haut !");
                        return true;
                    }
                    this.level++;
                    player.sendMessage("§7Votre "+getName()+"§7 a atteint le§c niveau "+this.level);
                }
                return true;
            }
            return false;
        }

        @Override
        public void tryUpdateActionBar() {
            getShowCdRunnable().setCustomTexte(
                    "§fNiveau de "+getName()+"§f:§c "+this.level
            );
        }
        @EventHandler
        public void onDamage(EntityDamageByEntityEvent event) {
            if (event.getDamager() instanceof Player && event.getDamager().getUniqueId().equals(getRole().getPlayer())) {
                if (this.level == 1) {
                    event.setDamage(event.getDamage() * 1.1);
                }
            }
            if (event.getEntity() instanceof Player && event.getEntity().getUniqueId().equals(getRole().getPlayer())) {
                if (this.level == 2) {
                    event.setDamage(event.getDamage() * 0.9);
                }
            }
        }
        private static final class ErmiteRunnable extends BukkitRunnable {

            private final TransformationErmite power;

            private ErmiteRunnable(TransformationErmite power) {
                this.power = power;
            }

            @Override
            public void run() {
                if (!GameState.inGame()) {
                    cancel();
                    return;
                }
                if (!this.power.getRole().getGamePlayer().check())return;
                if (this.power.level < 1)return;
                if (this.power.senninka.pointsErmite < 12 && this.power.level == 2) {
                    this.power.level--;
                    this.power.getRole().getGamePlayer().sendMessage("§7Votre niveau de "+this.power.getName()+"§7 a automatiquement§c diminué§7.");
                    return;
                }
                if (this.power.senninka.pointsErmite < 6 && this.power.level == 1) {
                    this.power.level--;
                    this.power.getRole().getGamePlayer().sendMessage("§7Votre niveau de "+this.power.getName()+"§7 a automatiquement§c diminué§7.");
                    return;
                }
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                   if (this.power.level == 1 && this.power.senninka.pointsErmite >= 6) {
                       this.power.getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 0, false, false), EffectWhen.NOW);
                       this.power.senninka.pointsErmite-=6;
                   } else if (this.power.level == 2 && this.power.senninka.pointsErmite >= 12) {
                       this.power.senninka.pointsErmite-=12;
                       this.power.getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 0, false, false), EffectWhen.NOW);
                       this.power.getRole().givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 40, 0, false, false), EffectWhen.NOW);
                   }
                });
            }
        }
        private static final class Senninka extends Power {

            private int pointsErmite = 200;

            public Senninka(@NonNull RoleBase role) {
                super("§5Senninka§r", null, role,
                        "§7Toute les§c secondes§7 vous récupérez§c 3 points§5 Senninka",
                        "",
                        "§7Vous gagnez§c 2 points supplémentaire§7 si§5 Kimimaro§7,§5 Suigetsu§7 ou§5 Karin§7 sont proche de vous§7 (§c20 blocs§7).",
                        "§7Si vos§c points§5 Senninka§7 sont en dessous de§c 50 points§7, vous avez§c Hunger I§7.");
                new SenninkaRunnable(this).runTaskTimerAsynchronously(this.getPlugin(), 0, 20);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                return false;
            }
            private static final class SenninkaRunnable extends BukkitRunnable {

                private final Senninka power;

                private SenninkaRunnable(Senninka power) {
                    this.power = power;
                }

                @Override
                public void run() {
                    if (!GameState.inGame()) {
                        cancel();
                        return;
                    }
                    if (!this.power.getRole().getGamePlayer().check())return;
                    final List<GamePlayer> gamePlayerList = new ArrayList<>(Loc.getNearbyGamePlayers(this.power.getRole().getGamePlayer().getLastLocation(), 20.0));
                    for (GamePlayer gamePlayer : gamePlayerList) {
                        if (!gamePlayer.check())continue;
                        if (gamePlayer.getRole() instanceof SuigetsuV2 || gamePlayer.getRole() instanceof KimimaroV2 || gamePlayer.getRole() instanceof KarinV2) {
                            this.power.pointsErmite+=2;
                        }
                    }
                    this.power.pointsErmite+=3;
                    if (this.power.pointsErmite <= 50) {
                        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                            if (this.power.pointsErmite <= 50) {
                                this.power.getRole().givePotionEffect(new PotionEffect(PotionEffectType.HUNGER, 40, 0, false, false), EffectWhen.NOW);
                            }
                        });
                    }
                    this.power.getRole().getGamePlayer().getActionBarManager().updateActionBar("jugo.senninka", "§aÉnergie ermite:§c "+this.power.pointsErmite);
                }
            }

        }
    }
}