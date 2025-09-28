package fr.nicknqck.roles.ds.slayers;

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
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.roles.ds.demons.MuzanV2;
import fr.nicknqck.roles.ds.demons.lune.KokushiboV2;
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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class KaigakuSlayer extends SlayerRoles implements RoleCustomLore, Listener {

    private boolean dead = false;

    public KaigakuSlayer(UUID player) {
        super(player);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.FOUDRE;
    }

    @Override
    public String getName() {
        return "Kaigaku";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.KaigakuSlayer;
    }

    @Override
    public String[] getCustomLore(String amount, String gDesign) {
        return new String[] {
                amount,
                "",
                gDesign,
                "",
                "§7Cette version du rôle n'est pas affecté par les§c pactes§7 de§e Jigoro§7 (§6V2§7)"
        };
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0, false, false), EffectWhen.NIGHT);
        addPower(new TonnerreLointainItem(this), true);
        addPower(new OrageBrulantItem(this), true);
        EventUtils.registerRoleEvent(this);
        new ZenItsuRunnable(this).runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
        super.RoleGiven(gameState);
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createAutomaticDesc(this)
                .addCustomLine("§7Vous n'êtes pas affecté par les§c pactes§7 de§e Jigoro§7 (§6V2§7)")
                .addCustomLines(
                        dead ? new String[0] :
                                new String[] {
                                        "§7Si vous vous faite tué par un§c Démon§7 vous aurez un certain§c pourcentage de chance§7 (§ben fonction de son rôle§7) de vous transformez à votre tour en§c démon§7 :",
                                        "",
                                        "§8 -§c Muzan§7 ou§c Kokushibo§7:§c 90%",
                                        "§8 -§c Lunes Supérieurs§7 (hors§c Kokushibo§7 et dont§a Nezuko§7): §c70%",
                                        "§8 -§c Lunes Inférieurs§7:§c 50%",
                                        "§8 -§c Autres Démons§7 (dont le joueur§c infecter§7):§c 30%",
                                        "",
                                        "§7Une fois devenu un§c Démon§7 vous aurez§b Speed I§7 de manière§c permanente§7 et§c Force I§7 la§c nuit§7."
                        }
                )
                .addCustomLines(
                        getKnowedRoles().contains(ZenItsuV2.class) ? new String[0] : new String[] {
                                "§7Vous possédez une§c bar de points§7 montant lorsque vous êtes proche de§a Zen'Itsu§7:",
                                "",
                                "§8 -§a 5 blocs§7:§c +25 points",
                                "§8 -§a 15 blocs§7:§c +15 points",
                                "§8 -§a 25 blocs§7:§c +5 points",
                                "§8 -§a 50 blocs§7:§c 1 point",
                                "",
                                "§7Une fois que la§c bar de points§7 à atteint les§c 1000 points§7 vous obtiendrez son§c identité§7.",
                                "§7Toute les§c 5 minutes§7, vous obtiendrez l'information du§c nombre de points§7 que vous avez."
                        }
                )
                .getText();
    }

    @EventHandler
    private void onKill(final UHCPlayerKillEvent event) {
        if (event.getGamePlayerKiller() == null)return;
        if (event.getVictim() == null)return;
        if (!event.getVictim().getUniqueId().equals(getPlayer()))return;
        if (event.getGamePlayerKiller().getRole() == null)return;
        if (dead)return;
        final RoleBase role = event.getGamePlayerKiller().getRole();
        if (role instanceof DemonsRoles || role.getTeam().equals(TeamList.Demon) || role.getOriginTeam().equals(TeamList.Demon)) {
            int random = Main.RANDOM.nextInt(101);
            int proba = (
                    role instanceof DemonsRoles ?
                            ((DemonsRoles) role).getRank().equals(DemonType.SUPERIEUR) ?
                                    70  :
                                    ((DemonsRoles) role).getRank().equals(DemonType.INFERIEUR)
                                            ? 50
                                            :
                                            ((DemonsRoles) role).getRank().equals(DemonType.NEZUKO) ? 70
                                                    :
                                                    role instanceof KokushiboV2 ? 90
                                                            :
                                                            role instanceof MuzanV2 ? 90 : 30
                            :
                            30
            );
            if (proba <= random) {
                final ItemStack[] itemStacks = event.getVictim().getInventory().getContents();
                final ItemStack[] armors = event.getVictim().getInventory().getArmorContents();
                event.setCancel(true);
                setTeam(TeamList.Demon, true);
                getGamePlayer().sendMessage("§7Vous avez été§c infecté§7, vous êtes maintenant dans le camp des§c Démons§7.");
                event.getGamePlayerKiller().sendMessage("§a"+getGamePlayer().getPlayerName()+"§7 à rejoint votre§a camp§7.");
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    final Player player = Bukkit.getPlayer(getGamePlayer().getUuid());
                    if (player != null) {
                        player.spigot().respawn();
                        player.getInventory().setArmorContents(armors);
                        player.getInventory().setContents(itemStacks);
                    }
                    assert event.getGamePlayerKiller().getLastLocation() != null;
                    getGamePlayer().teleport(event.getGamePlayerKiller().getLastLocation());
                },20);
                Map<PotionEffect, EffectWhen> copy = new HashMap<>(getEffects());
                for (PotionEffect potionEffect : copy.keySet()) {
                    if (potionEffect.getType().equals(PotionEffectType.SPEED)) {
                        getEffects().remove(potionEffect);
                        break;
                    }
                }
                givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0, false, false), EffectWhen.NIGHT);
                givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60*60, 0, false, false), EffectWhen.PERMANENT);
                setSuffixString(getSuffixString() + " §7(§cDémon§7)§r");
            }
            dead = true;
        }
    }

    private static class ZenItsuRunnable extends BukkitRunnable {

        private final KaigakuSlayer kaigakuSlayer;
        private int points = 0;
        private int time = 0;

        private ZenItsuRunnable(KaigakuSlayer kaigakuSlayer) {
            this.kaigakuSlayer = kaigakuSlayer;
        }

        @Override
        public void run() {
            if (!kaigakuSlayer.getGameState().getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            final Player owner = Bukkit.getPlayer(this.kaigakuSlayer.getPlayer());
            if (owner == null)return;
            final List<GamePlayer> zenItsuPlayers = kaigakuSlayer.getListGamePlayerFromRole(ZenItsuV2.class);
            for (GamePlayer gamePlayer : zenItsuPlayers) {
                if (!gamePlayer.isAlive())continue;
                if (!gamePlayer.isOnline())continue;
                if (gamePlayer.getLastLocation() == null)continue;
                if (!owner.getWorld().equals(gamePlayer.getLastLocation().getWorld()))continue;
                double distance = owner.getLocation().distance(gamePlayer.getLastLocation());
                if (distance <= 5) {
                    points+=25;
                    continue;
                }
                if (distance <= 15) {
                    points+=10;
                    continue;
                }
                if (distance <= 25) {
                    points+=5;
                    continue;
                }
                if (distance <= 50) {
                    points+=1;
                }
            }
            if (this.time >= 60*5) {
                this.time = 0;
                this.kaigakuSlayer.getGamePlayer().sendMessage("§7Vous êtes actuellement à§c "+points+" points§7.");
                return;
            }
            if (points >= 1000) {
                this.kaigakuSlayer.addKnowedRole(ZenItsuV2.class);
                this.kaigakuSlayer.getGamePlayer().sendMessage("§7Vous connaissez maintenant l'identité de§a Zen'Itsu§7 (§6/ds me§7).");
                cancel();
                return;
            }
            time++;
        }
    }
    private static class TonnerreLointainItem extends ItemPower implements Listener {

        public TonnerreLointainItem(@NonNull RoleBase role) {
            super("Tonnerre Lointain", new Cooldown(60*7),
                    new ItemBuilder(Material.BLAZE_ROD).setName("§eTonnerre Lointain"), role,
                    "§7Lance une§c ligne droite§7 d'§eéclair§7, lorsqu'un§e éclair§7 touche un joueur,",
                    "§7il obtient§c 30 secondes§7 de§c Slowness I§7 et perdra§c 1,5❤§7 de§c dégâts§7.");
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
                    if (!"§eTonnerre Lointain".equals(player.getItemInHand().getItemMeta().getDisplayName())) return false;

                    startThunder(player);
                    return true;
                }
            }
            return false;
        }

        private void startThunder(Player player) {
            Location start = player.getEyeLocation();
            Vector direction = player.getLocation().getDirection().normalize();

            new BukkitRunnable() {
                int strikes = 0;
                final Location current = start.clone();

                @Override
                public void run() {
                    if (!player.isOnline()) {
                        cancel();
                        return;
                    }

                    if (strikes >= 20) { // 20 éclairs
                        cancel();
                        return;
                    }

                    // Avancer de 2 blocs par éclair
                    current.add(direction.clone().multiply(2));
                    Location strikeLoc = current.clone();
                    strikeLoc.setY(strikeLoc.getWorld().getHighestBlockYAt(strikeLoc));

                    // Partie sync : effet + dégâts
                    Bukkit.getScheduler().runTask(getPlugin(), () -> {
                        strikeLoc.getWorld().strikeLightningEffect(strikeLoc);

                        for (Player target : strikeLoc.getWorld().getPlayers()) {
                            if (target.equals(player)) continue;
                            if (target.getLocation().distanceSquared(strikeLoc) <= 1.0) {
                                double newHealth = Math.max(1.0, target.getHealth() - 3.0); // 1.5 cœur
                                target.setHealth(newHealth);
                                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 30, 0)); // 30s Slowness I
                                target.sendMessage("§cVous avez été frappé par le §eTonnerre Lointain§c !");
                            }
                        }
                    });

                    strikes++;
                }
            }.runTaskTimerAsynchronously(getPlugin(), 0L, 10L); // async, toutes les 0.5s (10 ticks)
        }
    }
    private static class OrageBrulantItem extends ItemPower implements Listener {

        public OrageBrulantItem(@NonNull RoleBase role) {
            super("Orage Brûlant", new Cooldown(60 * 8),
                    new ItemBuilder(Material.NETHER_STAR).setName("§eOrage Brûlant"), role,
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
                        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 0, false, false), true);
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

    /*OLD
    private static class OrageBrulantItem extends ItemPower implements Listener {

        public OrageBrulantItem(@NonNull RoleBase role) {
            super("Orage Brûlant", new Cooldown(60 * 8),
                    new ItemBuilder(Material.NETHER_STAR).setName("§eOrage Brûlant"), role,
                    "§7Faites jaillir un arc d’éclairs jusqu’à votre cible !");
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

            // Vecteurs perpendiculaires pour l'arc
            Vector perpRight = new Vector(-dirToTarget.getZ(), 0, dirToTarget.getX()).normalize();
            Vector perpLeft = perpRight.clone().multiply(-1);

            new BukkitRunnable() {
                int step = 0;

                @Override
                public void run() {
                    if (!caster.isOnline() || !target.isOnline()) {
                        cancel();
                        return;
                    }

                    // Vérifier distance atteinte
                    if (start.distance(target.getLocation()) <= step * 2) {
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

                        cancel();
                        return;
                    }

                    // Calcul positions des éclairs (progression de l’arc)
                    Location center = start.clone().add(dirToTarget.clone().multiply(step * 2));
                    Location locRight = center.clone().add(perpRight.clone().multiply(step));
                    Location locLeft = center.clone().add(perpLeft.clone().multiply(step));

                    // Aligner au sol
                    locRight.setY(locRight.getWorld().getHighestBlockYAt(locRight));
                    locLeft.setY(locLeft.getWorld().getHighestBlockYAt(locLeft));

                    // Spawn éclairs + effets (sync obligatoire)
                    Bukkit.getScheduler().runTask(getPlugin(), () -> {
                        strikeWithEffect(locRight, caster);
                        strikeWithEffect(locLeft, caster);
                    });

                    step++;
                }

                private void strikeWithEffect(Location loc, Player caster) {
                    World world = loc.getWorld();
                    world.strikeLightningEffect(loc);

                    for (Player near : world.getPlayers()) {
                        if (near.equals(caster)) continue;
                        if (near.equals(target)) continue;
                        if (near.getLocation().distance(loc) <= 1.0) {
                            // 0.5 cœur de dégâts (mais jamais létal)
                            double newHp = Math.max(1.0, near.getHealth() - 1.0);
                            near.setHealth(newHp);
                            // Slowness I 10s
                            near.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 10, 0));
                            near.sendMessage("§7Vous avez été secoué par un éclair de l'§eOrage Brûlant§7 !");
                        }
                    }
                }
            }.runTaskTimerAsynchronously(getPlugin(), 0L, 5L); // toutes les 5 ticks (~0.25s)
        }
    }
*/

}