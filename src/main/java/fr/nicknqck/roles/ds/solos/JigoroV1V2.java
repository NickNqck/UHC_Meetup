package fr.nicknqck.roles.ds.solos;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.RoleCustomLore;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonsSlayersRoles;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.roles.ds.demons.lune.KaigakuV2;
import fr.nicknqck.roles.ds.slayers.ZenItsuV2;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class JigoroV1V2 extends DemonsSlayersRoles implements Listener, RoleCustomLore {

    private boolean killZenItsu = false;
    private boolean killKaigaku = false;
    private OrageBrulantItem orageBrulantItem;
    private SpeedTroisPower speedTroisPower;

    public JigoroV1V2(UUID player) {
        super(player);
        setCanuseblade(true);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.FOUDRE;
    }

    @Override
    public String getName() {
        return "Jigoro§7 (§eSolo§7)§r";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Jigoro;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Solo;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        addPower(new ZoneDeFoudre(this), true);
        EventUtils.registerRoleEvent(this);
        this.orageBrulantItem = new OrageBrulantItem(this);
        this.speedTroisPower = new SpeedTroisPower(this);
        super.RoleGiven(gameState);
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createAutomaticDesc(this)
                .addCustomLine(this.killZenItsu ? "" : "§7Si vous parvenez à tuer un joueur ayant le rôle§a Zen'Itsu§7, vous obtiendrez l'accès à l'item \""+this.speedTroisPower.getItem().getItemMeta().getDisplayName()+"§7\"")
                .addCustomLines(this.killZenItsu ? new String[0] : this.speedTroisPower.getDescriptions())
                .addCustomLine(this.killKaigaku ? "" : "§7Si vous parvenez à tuer un joueur ayant le rôle§c Kaigaku§7, vous obtiendrez l'accès à l'item \""+this.orageBrulantItem.getItem().getItemMeta().getDisplayName()+"§7\"")
                .addCustomLines(this.killKaigaku ? new String[0] : this.orageBrulantItem.getDescriptions())
                .getText();
    }

    @EventHandler
    private void onKill(final UHCPlayerKillEvent event) {
        if (event.getVictim() == null)return;
        if (event.getGamePlayerKiller() == null)return;
        if (event.getGamePlayerKiller().getUuid().equals(getPlayer())) {
            if (event.getGameState().hasRoleNull(event.getVictim().getUniqueId()))return;
            final RoleBase role = event.getGameState().getGamePlayer().get(event.getVictim().getUniqueId()).getRole();
            if (role instanceof ZenItsuV2 && !killZenItsu) {
                boolean give = false;
                //Si Jigoro a déjà force 1 alors il obtient force perma
                for (final PotionEffect potionEffect : new ArrayList<>(getEffects().keySet())) {
                    if (!potionEffect.getType().equals(PotionEffectType.INCREASE_DAMAGE))continue;
                    if (getEffects().get(potionEffect).equals(EffectWhen.NIGHT)) {
                        getEffects().remove(potionEffect);
                        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
                        give = true;
                    }
                    break;
                }
                if (!give) {
                    givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.DAY);
                }
                event.getGamePlayerKiller().sendMessage("§7En tuant§a Zen'Itsu§7 vous avez obtenue l'effet§c Force I§7 le§c jour§7.");
                addPower(this.speedTroisPower, true);
                this.killZenItsu = true;
            }
            if (role instanceof KaigakuV2 && !killKaigaku) {
                boolean give = false;
                //Si Jigoro a déjà force 1 alors il obtient force perma
                for (final PotionEffect potionEffect : new ArrayList<>(getEffects().keySet())) {
                    if (!potionEffect.getType().equals(PotionEffectType.INCREASE_DAMAGE))continue;
                    if (getEffects().get(potionEffect).equals(EffectWhen.NIGHT)) {
                        getEffects().remove(potionEffect);
                        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
                        give = true;
                    }
                    break;
                }
                if (!give) {
                    givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.NIGHT);
                }
                event.getGamePlayerKiller().sendMessage("§7En tuant§c Kaigaku§7 vous avez obtenue l'effet§c Force I§7 la§c nuit§7.");
                addPower(this.orageBrulantItem, true);
                this.killKaigaku = true;
            }
        }
    }

    @Override
    public String[] getCustomLore(String amount, String gDesign) {
        return new String[] {
                amount,
                "",
                gDesign,
                "",
                "§7Cette version de§e Jigoro§7 n'à pas accès aux différents§c pactes§7."
        };
    }

    private static class OrageBrulantItem extends ItemPower implements Listener {

        public OrageBrulantItem(@NonNull RoleBase role) {
            super("Orage Brûlant", new Cooldown(60 * 8),
                    new ItemBuilder(Material.NETHER_STAR).setName("§eQuatrième mouvement: Orage Brûlant"), role,
                    "§7Faites jaillir un arc d’éclairs convergeant vers votre§c cible§7, une fois atteint,",
                    "§7vous vous§c téléporterez§7 derrière elle et elle obtiendra§c 10 secondes§7 de§c Slowness I§7.");
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (map.containsKey("event") && map.get("event") instanceof PlayerInteractEvent) {
                    PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");

                    if (!event.getAction().name().contains("RIGHT")) return false;
                    if (player.getItemInHand() == null) return false;
                    if (!player.getItemInHand().hasItemMeta()) return false;
                    if (!"§eOrage Brûlant".equals(player.getItemInHand().getItemMeta().getDisplayName())) return false;

                    Player target = RayTrace.getTargetPlayer(player, 30, null);
                    if (target == null) {
                        player.sendMessage("§cAucun joueur trouvé dans votre ligne de mire.");
                        return false;
                    }

                    launchStorm(player, target);
                    return true;
                }
            }
            return false;
        }

        private void launchStorm(Player caster, Player target) {
            Location start = caster.getEyeLocation();
            Vector dirToTarget = target.getLocation().toVector().subtract(start.toVector()).normalize();

            // Vecteurs perpendiculaires de base
            Vector perpRight = new Vector(-dirToTarget.getZ(), 0, dirToTarget.getX()).normalize();
            Vector perpLeft = perpRight.clone().multiply(-1);

            double maxDistance = start.distance(target.getLocation());

            new BukkitRunnable() {
                int step = 0;

                @Override
                public void run() {
                    if (!caster.isOnline() || !target.isOnline()) {
                        cancel();
                        return;
                    }

                    double progressDist = step * 2;
                    if (progressDist >= maxDistance) {
                        target.damage(0.0);
                        // Effet final sur la cible
                        double newHealth = Math.max(1.0, target.getHealth() - 2.0); // -1 cœur
                        target.setHealth(newHealth);

                        // TP derrière target
                        Location behind = target.getLocation().clone();
                        behind.setDirection(target.getLocation().getDirection());
                        behind.add(target.getLocation().getDirection().multiply(-1));
                        caster.teleport(behind);

                        caster.sendMessage("§eVous avez frappé " + target.getName() + " §eavec l'Orage Brûlant !");
                        target.sendMessage("§cVous avez été touché par l'Orage Brûlant de " + caster.getName());
                        Bukkit.getScheduler().runTask(getPlugin(), () -> target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 0, false, false), true));
                        cancel();
                        return;
                    }

                    // Calcul positions des éclairs (effet entonnoir)
                    Location center = start.clone().add(dirToTarget.clone().multiply(progressDist));

                    // Facteur d’ouverture → plus grand au début, 0 à la fin
                    double funnelFactor = 1.0 - (progressDist / maxDistance); // 1 au départ, 0 à la fin
                    double spread = 6 * funnelFactor; // 6 blocs max d’écart, se réduit vers 0

                    Location locRight = center.clone().add(perpRight.clone().multiply(spread));
                    Location locLeft = center.clone().add(perpLeft.clone().multiply(spread));

                    // Alignement au sol
                    locRight.setY(locRight.getWorld().getHighestBlockYAt(locRight));
                    locLeft.setY(locLeft.getWorld().getHighestBlockYAt(locLeft));

                    // Spawn éclairs + effets (sync obligatoire)
                    Bukkit.getScheduler().runTask(getPlugin(), () -> {
                        strikeWithEffect(locRight, caster, target);
                        strikeWithEffect(locLeft, caster, target);
                    });

                    step++;
                }

                private void strikeWithEffect(Location loc, Player caster, Player target) {
                    World world = loc.getWorld();
                    world.strikeLightningEffect(loc);

                    for (Player near : world.getPlayers()) {
                        if (near.equals(caster)) continue;
                        if (near.equals(target)) continue;
                        if (near.getLocation().distance(loc) <= 1.0) {
                            near.damage(0.0);
                            // 0.5 cœur de dégâts (non létal)
                            double newHp = Math.max(1.0, near.getHealth() - 1.0);
                            near.setHealth(newHp);
                            // Slowness I 10s
                            near.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 10, 0, false, false), true);
                            near.sendMessage("§7Vous avez été secoué par un éclair de l'§eOrage Brûlant§7 !");
                        }
                    }
                }
            }.runTaskTimerAsynchronously(getPlugin(), 0L, 5L); // toutes les 5 ticks (~0.25s)
        }
    }
    private static class SpeedTroisPower extends ItemPower {

        protected SpeedTroisPower(@NonNull RoleBase role) {
            super("Soufle de la foudre: Premier Mouvement", new Cooldown(60*5), new ItemBuilder(Material.NETHER_STAR).setName("§ePremier Mouvement: Accélération"), role,
                    "§7Vous donne l'effet§e Speed III§7 pendant§c 60 secondes§7.");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                player.sendMessage("§7Vous venez d'utiliser votre §eSpeed III");
                getRole().getGameState().spawnLightningBolt(player.getWorld(), player.getLocation());
                getRole().getEffects().remove(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60, 2, false, false), true);
                new SpeedTroisPower.RecupSpeedRunnable(this);
                return true;
            }
            return false;
        }
        private static class RecupSpeedRunnable extends BukkitRunnable {

            private final SpeedTroisPower power;
            private int timeLeft;

            private RecupSpeedRunnable(SpeedTroisPower power) {
                this.power = power;
                this.timeLeft = 0;
                runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
            }

            @Override
            public void run() {
                if (!power.getRole().getGameState().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (this.timeLeft == 0) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> this.power.getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 80, 0, false, false), EffectWhen.PERMANENT));
                    cancel();
                    return;
                }
                this.timeLeft--;
            }
        }
    }
    private static class ZoneDeFoudre extends ItemPower {

        public ZoneDeFoudre(@NonNull RoleBase role) {
            super("Zone de Foudre", new Cooldown(60*10), new ItemBuilder(Material.NETHER_STAR).setName("§eZone de Foudre"), role,
                    "§7Crée une zone autours de vous d'une taille de§c 7x7x7§7 dans laquelle toute les§c 4 secondes§7,",
                    "§7tout les joueurs présent dans la§e zone de Foudre§7 perdront§c 1,5❤§7 via un§e éclair§7,",
                    "§7pour chaque joueur§e foudroyé§7 vous obtiendrez§e 1❤ d'absorption§7.",
                    "",
                    "§7Votre§e Zone de Foudre§7 à une durée d'§cactivation§7 de§c 15 secondes§7,",
                    "§7tout les§c 3 joueurs§7 touché par un§e éclair§7, votre§c zone§7 gagne§c 1 seconde§7 (Uniquement la zone actuel).");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                player.sendMessage("§7Vous avez§a activé§7 votre§e Zone de Foudre§7.");
                new ZoneRunnable(this).runTaskTimerAsynchronously(getPlugin(), 0, 20);
                return true;
            }
            return false;
        }
        private static class ZoneRunnable extends BukkitRunnable {

            private final ZoneDeFoudre zoneDeFoudre;
            private int timeLeft = 15;
            private int timeFoudre = 1;
            private int amountFoudroyed = 0;

            private ZoneRunnable(ZoneDeFoudre zoneDeFoudre) {
                this.zoneDeFoudre = zoneDeFoudre;
            }

            @Override
            public void run() {
                if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                final Player owner = Bukkit.getPlayer(this.zoneDeFoudre.getRole().getPlayer());
                if (owner == null)return;
                if (this.timeLeft<=0) {
                    this.zoneDeFoudre.getRole().getGamePlayer().getActionBarManager().removeInActionBar("jigoro.zone");
                    owner.sendMessage("§7Votre§e Zone de Foudre§7 s'arrête...");
                    cancel();
                    return;
                }
                this.zoneDeFoudre.getRole().getGamePlayer().getActionBarManager().updateActionBar("jigoro.zone", "§bTemps restant (§eZone de Foudre§b):§c "+ StringUtils.secondsTowardsBeautiful(this.timeLeft));
                if (this.timeFoudre <= 0) {
                    Bukkit.getScheduler().runTask(this.zoneDeFoudre.getPlugin(), () -> {
                        for (final GamePlayer gamePlayer : Loc.getNearbyGamePlayers(owner.getLocation(), 7)) {
                            if (!gamePlayer.isAlive())continue;
                            if (!gamePlayer.isOnline())continue;
                            final Player player = Bukkit.getPlayer(gamePlayer.getUuid());
                            if (player.getUniqueId().equals(this.zoneDeFoudre.getRole().getPlayer()))continue;
                            player.damage(0.0);
                            player.sendMessage("§7Vous avez été toucher par un§e éclair§7 de§e Jigoro§7.");
                            player.getWorld().strikeLightningEffect(player.getLocation());
                            player.setHealth(Math.max(1.0, player.getHealth()-3));
                            this.amountFoudroyed++;
                            if (this.amountFoudroyed == 3) {
                                this.timeLeft++;
                                owner.sendMessage("§7La durée de votre§e Zone de Foudre§7 a été§c augmenter§7 de§c 1 seconde§7.");
                            }
                            if (owner instanceof CraftPlayer) {
                                ((CraftPlayer)owner).getHandle().setAbsorptionHearts(((CraftPlayer) owner).getHandle().getAbsorptionHearts()+2f );
                            }
                        }
                    });
                    this.timeFoudre = 4;
                }
                this.timeFoudre--;
                this.timeLeft--;
            }
        }
    }
}