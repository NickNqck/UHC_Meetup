package fr.nicknqck.roles.ds.demons.lune;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EffectGiveEvent;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.events.custom.roles.PowerActivateEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.demons.Muzan;
import fr.nicknqck.roles.ds.slayers.pillier.PilierRoles;
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
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class EnmuV2 extends DemonsRoles {

    public EnmuV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull DemonType getRank() {
        return DemonType.INFERIEUR;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Enmu§7 (§6V2§7)§r";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Enmu;
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Demon;
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
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.NIGHT);
        getKnowedRoles().add(Muzan.class);
        addPower(new EndormissementPower(this), true);
        addPower(new SommeilUltime(this), true);
    }
    private static class SommeilUltime extends ItemPower {

        private final World arena;
        private final Map<UUID, Integer> duelMap = new HashMap<>();

        protected SommeilUltime(@NonNull RoleBase role) {
            super("§fSommeil Ultime", new Cooldown(60*15), new ItemBuilder(Material.NETHER_STAR).setName("§fSommeil ultime"), role,
                    "§7En visant un joueur, vous permet de charger sa§b bar de sommeil§7, une fois remplie vous pourrez alors l'affronter dans un§c duel",
                    "§7durant ce duel, aucun joueur ne pourra utiliser de§c pouvoir§7, également, votre adversaire ne possédera aucun effet,",
                    "",
                    "§7Si vous perdez votre§c duel§7 vous réapparaitrez en perdant§c 2❤ permanents§7 ainsi que ce pouvoir",
                    "",
                    "§7Si vous gagnez votre§c duel§7 vous obtiendrez §c1/2❤ permanent§7, ainsi qu'une utilisation de ce pouvoir",
                    "§7Si la personne que vous aviez tué était un§a pilier§7 ou un rôle§e solitaire§7 vous gagnerez §c1/2❤ permanent§7 en§c plus§7.");
            this.arena = getWorld();
            clearArena();
            setMaxUse(1);
        }

        private World getWorld() {
            Main.getInstance().deleteWorld("enmuv2_duel");
            final WorldCreator worldCreator = new WorldCreator("enmuv2_duel");
            worldCreator.generator(getBase());
            final World world = worldCreator.createWorld();
            final WorldBorder worldBorder = world.getWorldBorder();
            worldBorder.setCenter(0.0, 0.0);
            worldBorder.setSize(100.0);
            world.setGameRuleValue("doMobSpawning", "false");
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("spectatorsGenerateChunks", "false");
            world.setGameRuleValue("naturalRegeneration", "false");
            world.setGameRuleValue("announceAdvancements", "false");
            world.setDifficulty(Difficulty.HARD);
            world.setSpawnLocation(0, world.getHighestBlockYAt(0, 0), 0);
            world.setGameRuleValue("doMobSpawning", "false");
            world.setGameRuleValue("doFireTick", "false");
            return world;
        }
        private String getBase() {
            return "{\"coordinateScale\":684.412,\"heightScale\":684.412,\"lowerLimitScale\":512.0,\"upperLimitScale\":512.0,\"depthNoiseScaleX\":" + 600+
                    ",\"depthNoiseScaleZ\":" + 600 +
                    ",\"depthNoiseScaleExponent\":0.5,\"mainNoiseScaleX\":80.0,\"mainNoiseScaleY\":160.0,\"mainNoiseScaleZ\":80.0,\"baseSize\":8.5,\"stretchY\":12.0,\"biomeDepthWeight\":1.0,\"biomeDepthOffset\":0.0,\"biomeScaleWeight\":1.0,\"biomeScaleOffset\":0.0," +
                    "\"seaLevel\":" + 10 +
                    ",\"useCaves\":" + false + //S'il y aura des caves ou non
                    ",\"useDungeons\":" + false + //S'il y aura des dongeons ou non
                    ",\"dungeonChance\":" + 8 + //Probo dongeon 8 = vanilla
                    ",\"useStrongholds\":" + false + //S'il y aura des strongholds
                    ",\"useVillages\":" + false + //S'il y aura des villages ou non
                    ",\"useMineShafts\":" + false + //S'il y aura des mineshafts ou non
                    ",\"useTemples\":" + false + //S'il y aura des temples de la jungle
                    ",\"useMonuments\":" + false+ //
                    ",\"useRavines\":" + false + //Si il y aura des failles ou non
                    ",\"useWaterLakes\":" + false + //S'il y aura des lacs d'eau
                    ",\"waterLakeChance\":" + 2 + //La proba d'avoir des lacs d'eau 4 = vanilla
                    ",\"useLavaLakes\":" + false + //S'il y aura des lacs de lave ou non
                    ",\"lavaLakeChance\":" + 20 + //S'il y aura des lacs de lave 80 = vanilla
                    ",\"useLavaOceans\":" + false + //Si on remplace les Océans d'eau par de la lave ou non
                    ",\"fixedBiome\":-1,\"biomeSize\":4,\"riverSize\":" + 4 + //Taille des rivières 4 = vanilla
                    ",\"dirtSize\":33,\"dirtCount\":10,\"dirtMinHeight\":0,\"dirtMaxHeight\":256,\"gravelSize\":33,\"gravelCount\":8,\"gravelMinHeight\":0,\"gravelMaxHeight\":256,\"graniteSize\":33,\"graniteCount\":10,\"graniteMinHeight\":0,\"graniteMaxHeight\":80,\"dioriteSize\":33,\"dioriteCount\":10,\"dioriteMinHeight\":0,\"dioriteMaxHeight\":80,\"andesiteSize\":33,\"andesiteCount\":10,\"andesiteMinHeight\":0,\"andesiteMaxHeight\":80"+
                    ",\"coalSize\":"+ 17 +
                    ",\"coalCount\":" + 20 +
                    ",\"coalMinHeight\":" + 0 +
                    ",\"coalMaxHeight\":"+ 128 +
                    ",\"ironSize\":" + 9+
                    ",\"ironCount\":" + 20 +
                    ",\"ironMinHeight\":" + 0 +
                    ",\"ironMaxHeight\":" + 64 +
                    ",\"goldSize\":" + 10 +
                    ",\"goldCount\":" + 3 +
                    ",\"goldMinHeight\":" + 0 +
                    ",\"goldMaxHeight\":" + 32 +
                    ",\"redstoneSize\":" + 8 +
                    ",\"redstoneCount\":" + 20 +
                    ",\"redstoneMinHeight\":" + 0 +
                    ",\"redstoneMaxHeight\":" + 16+
                    ",\"diamondSize\":" + 8 + ",\"diamondCount\":" + 1 +
                    ",\"diamondMinHeight\":" + 0 + ",\"diamondMaxHeight\":" + 22 +
                    ",\"lapisSize\":" + 7 + ",\"lapisCount\":" + 1 + ",\"lapisCenterHeight\":" + 16 +",\"lapisSpread\":" + 16+ "}";
        }
        private void clearArena() {
            for (int x = -150; x <= 150; x++) {
                for (int z = -150; z <= 150; z++) {
                    for (int y = 59; y <= 65; y++) {
                        final Block block = this.arena.getBlockAt(x ,y ,z);
                        final String name = block.getType().name();
                        if (name.contains("WATER") || name.contains("LAVA")) {
                            block.setType(Material.AIR);
                        }
                    }
                }
            }
        }
        @Override
        public boolean onUse(Player player, Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final Player target = RayTrace.getTargetPlayer(player, 30, Objects::nonNull);
                if (target == null) {
                    player.sendMessage("§cIl faut viser un joueur !");
                    return false;
                }
                final GamePlayer gameTarget = getRole().getGameState().getGamePlayer().get(target.getUniqueId());
                if (gameTarget == null) {
                    player.sendMessage("§cIl faut viser un joueur !§7 (le joueur viser n'a pas de rôle)");
                    return false;
                }
                if (this.duelMap.containsKey(target.getUniqueId())) {
                    player.sendMessage("§c"+target.getName()+"§7 a déjà été placer dans votre sommeil");
                    return false;
                }
                new SommeilRunnable(this, target.getUniqueId()).runTaskTimerAsynchronously(getPlugin(), 0, 20);
                this.duelMap.put(target.getUniqueId(), 30);
                return true;
            }
            return false;
        }
        private void tryStartDuel(final Player target, final Player owner) {
            owner.sendMessage("§7Vous avez§c endormie§7 le joueur: "+target.getDisplayName());
            target.sendMessage("§7Vous avez été§c endormie§7 par§c Enmu§7 (§6V2§7)");
            final Location loc1 = new Location(this.arena, 25, this.arena.getHighestBlockYAt(25, 25), 25);
            final Location loc2 = new Location(this.arena, -25, this.arena.getHighestBlockYAt(-25, -25), -25);
            Bukkit.getScheduler().runTask(getPlugin(), () -> {
                target.getLocation().getBlock().setType(Material.SIGN_POST);
                final Sign sign = (Sign) target.getLocation().getBlock().getState();
                sign.setLine(1, "zzz");
                sign.update();
                target.teleport(loc2, PlayerTeleportEvent.TeleportCause.PLUGIN);
                owner.teleport(loc1, PlayerTeleportEvent.TeleportCause.PLUGIN);
            });
            getRole().getGamePlayer().setLastLocation(loc1);
            for (final PotionEffect potionEffect : target.getActivePotionEffects()) {
                target.removePotionEffect(potionEffect.getType());
            }
            new DuelManager(this);
        }
        private static class DuelManager implements Listener {

            private final GameState gameState;
            private final SommeilUltime sommeilUltime;
            private ItemStack[] enmuItems;
            private ItemStack[] enmuArmors;

            private DuelManager(SommeilUltime sommeilUltime) {
                this.sommeilUltime = sommeilUltime;
                this.gameState = sommeilUltime.getRole().getGameState();
                EventUtils.registerRoleEvent(this);
                final Player owner = Bukkit.getPlayer(sommeilUltime.getRole().getPlayer());
                if (owner != null) {
                    this.enmuItems = owner.getInventory().getContents();
                    this.enmuArmors = owner.getInventory().getArmorContents();
                } else {
                    this.enmuItems = sommeilUltime.getRole().getGamePlayer().getLastInventoryContent();
                }
            }

            @EventHandler
            private void onKill(UHCPlayerKillEvent event) {
                if (event.getGamePlayerKiller() == null)return;
                if (!event.getKiller().getWorld().getName().equals("enmuv2_duel"))return;
                final GamePlayer victim = this.gameState.getGamePlayer().get(event.getVictim().getUniqueId());
                if (victim == null)return;
                //Si le gagnant c'est Enmu
                if (event.getGamePlayerKiller().getUuid().equals(this.sommeilUltime.getRole().getPlayer())) {
                    this.sommeilUltime.getRole().setMaxHealth(this.sommeilUltime.getRole().getMaxHealth()+1.0);
                    event.getPlayerKiller().setMaxHealth(this.sommeilUltime.getRole().getMaxHealth());
                    event.getGamePlayerKiller().sendMessage("§7Bravo, vous avez vaincu§c "+victim.getPlayerName()+"§7 dans son sommeil, vous gagnez donc§a +§c1/2❤ permanent§7 ainsi qu'une utilisation de ce pouvoir, vous serez téléporter en dehors du rêve dans§c 10 secondes§7.");
                    this.sommeilUltime.setMaxUse(this.sommeilUltime.getMaxUse()+1);
                    //Du coup la je vais tp QUE enmu
                    new ReturnBackRunnable(this, event.getGamePlayerKiller(), false).runTaskTimerAsynchronously(this.sommeilUltime.getPlugin(), 0, 20);
                    if (victim.getRole() != null) {
                        if (victim.getRole() instanceof PilierRoles) {
                            event.getGamePlayerKiller().sendMessage("§7On dirait que vous avez vaincu un§a pilier§7, vous gagnez donc§a +§c1/2❤ permanent");
                            this.sommeilUltime.getRole().setMaxHealth(this.sommeilUltime.getRole().getMaxHealth()+1.0);
                            event.getPlayerKiller().setMaxHealth(this.sommeilUltime.getRole().getMaxHealth());
                        }
                        final TeamList team = victim.getRole().getOriginTeam();
                        if (team.equals(TeamList.Solo) || team.equals(TeamList.Jubi) || team.equals(TeamList.Jigoro) || team.equals(TeamList.Alliance) || team.equals(TeamList.Sasuke)) {
                            event.getGamePlayerKiller().sendMessage("§7On dirait que vous avez vaincu un rôle§e solitaire§7, vous gagnez donc§a +§c1/2❤ permanent");
                            this.sommeilUltime.getRole().setMaxHealth(this.sommeilUltime.getRole().getMaxHealth()+1.0);
                            event.getPlayerKiller().setMaxHealth(this.sommeilUltime.getRole().getMaxHealth());
                        }
                    }
                }
                //Si le perdant c'est Enmu
                if (victim.getUuid().equals(this.sommeilUltime.getRole().getPlayer())) {
                    event.getPlayerKiller().getInventory().remove(this.sommeilUltime.getItem());
                    victim.getRole().getPowers().remove(this.sommeilUltime);
                    victim.getRole().setMaxHealth(this.sommeilUltime.getRole().getMaxHealth()-4.0);
                    event.getVictim().setMaxHealth(this.sommeilUltime.getRole().getMaxHealth());
                    victim.sendMessage("§7Vous avez perdu votre§c duel§7, pourtant il était à votre avantage... Tant pis vous allez ressusciter dans§c 10 secondes§7 en perdant§c 2❤ permanents");
                    for (final ItemStack itemStack : this.enmuItems) {
                        if (itemStack == null)continue;
                        if (itemStack.getType().equals(Material.AIR))continue;
                        GameListener.dropItem(event.getVictim().getLocation(), itemStack);
                    }
                    //Et la je tp les deux joueurs avec chacun sont propres runnable
                    new ReturnBackRunnable(this, event.getGamePlayerKiller(), false).runTaskTimerAsynchronously(this.sommeilUltime.getPlugin(), 0, 20);
                    new ReturnBackRunnable(this, victim, true).runTaskTimerAsynchronously(this.sommeilUltime.getPlugin(), 0, 20);
                    this.enmuItems = event.getVictim().getInventory().getContents();
                    this.enmuArmors = event.getVictim().getInventory().getArmorContents();
                    event.setCancel(true);
                }
            }
            @EventHandler
            private void PowerActiveEvent(@NonNull final PowerActivateEvent event) {
                if (event.getPlayer().getWorld().getName().equals("enmuv2_duel")) {
                    event.setCancel(true);
                    event.setCancelMessage("§cCe pouvoir n'est pas utilisable ici !");
                }
            }
            @EventHandler
            private void EffectGiveEvent(final EffectGiveEvent effectGiveEvent) {
                if (effectGiveEvent.getPlayer().getWorld().getName().equals("enmuv2_duel")) {
                    if (effectGiveEvent.getPlayer().getUniqueId().equals(this.sommeilUltime.getRole().getPlayer()))return;
                    effectGiveEvent.setCancelled(true);
                }
            }
            @EventHandler
            private void onEndGame(final EndGameEvent event) {
                Main.getInstance().deleteWorld("enmuv2_duel");
            }

            private static class ReturnBackRunnable extends BukkitRunnable {

                private int timeLeft = 10;
                private final GamePlayer winer;
                private final DuelManager duelManager;
                private final GameState gameState;
                private final boolean enmuLOOSE;

                private ReturnBackRunnable(final DuelManager duelManager, GamePlayer winer, final boolean enmuLoose) {
                    this.duelManager = duelManager;
                    this.winer = winer;
                    this.gameState = duelManager.gameState;
                    this.enmuLOOSE = enmuLoose;
                }

                @Override
                public void run() {
                    if (!this.gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                        cancel();
                        return;
                    }
                    if (this.timeLeft <= 0) {
                        Bukkit.getScheduler().runTask(this.duelManager.sommeilUltime.getPlugin(), () -> {
                            final Location loc = GameListener.generateRandomLocation(Bukkit.getWorld("arena"));
                            final Player owner = Bukkit.getPlayer(winer.getUuid());
                            if (owner != null) {
                                owner.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
                                if (this.enmuLOOSE) {
                                    owner.getInventory().setArmorContents(this.duelManager.enmuArmors);
                                    owner.getInventory().setContents(this.duelManager.enmuItems);
                                    owner.updateInventory();
                                }
                            }
                        });
                        cancel();
                        this.winer.getActionBarManager().removeInActionBar("enmuv2.duelend");
                        return;
                    }
                    this.winer.getActionBarManager().addToActionBar("enmuv2.duelend", "§bTemp avant téléportation:§c "+this.timeLeft+"s");
                    this.timeLeft--;
                }
            }
        }
        private static class SommeilRunnable extends BukkitRunnable {

            private final GameState gameState;
            private final SommeilUltime sommeilUltime;
            private final UUID uuid;

            private SommeilRunnable(@NonNull final SommeilUltime sommeilUltime, @NonNull final UUID uuidTarget) {
                this.sommeilUltime = sommeilUltime;
                this.gameState = sommeilUltime.getRole().getGameState();
                this.uuid = uuidTarget;
                sommeilUltime.getRole().getGamePlayer().getActionBarManager().addToActionBar("enmuv2.dueltime", "§bTemp avant sommeil:§c 5 minutes");
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (!this.sommeilUltime.getRole().getGamePlayer().isAlive()) {
                    return;
                }
                if (this.sommeilUltime.getRole().getGamePlayer().getLastLocation().getWorld().getName().equals("enmuv2_duel")) {
                    return;
                }
                final Player owner = this.sommeilUltime.getPlugin().getServer().getPlayer(this.sommeilUltime.getRole().getPlayer());
                if (owner == null) {
                    return;
                }
                for (final Player around : Loc.getNearbyPlayers(owner.getLocation(), 30)) {
                    if (this.sommeilUltime.duelMap.containsKey(around.getUniqueId()) && this.uuid.equals(around.getUniqueId())) {
                        Integer time = this.sommeilUltime.duelMap.get(around.getUniqueId());
                        if (time < 0)continue;
                        this.sommeilUltime.duelMap.remove(around.getUniqueId(), time);
                        time--;
                        this.sommeilUltime.duelMap.put(around.getUniqueId(), time);
                        this.sommeilUltime.getRole().getGamePlayer().getActionBarManager().updateActionBar("enmuv2.dueltime", "§bTemp avant sommeil:§c "+StringUtils.secondsTowardsBeautiful(this.sommeilUltime.duelMap.get(uuid)));
                        if (time == 0) {
                            this.sommeilUltime.tryStartDuel(around, owner);
                            this.sommeilUltime.getRole().getGamePlayer().getActionBarManager().removeInActionBar("enmuv2.dueltime");
                            cancel();
                            break;
                        }
                    }
                }
            }
        }
    }
    private static class EndormissementPower extends ItemPower {

        private final CliqueDroit cliqueDroit;
        private final CliqueGauche cliqueGauche;

        protected EndormissementPower(@NonNull RoleBase role) {
            super("§cEndormissement", new Cooldown(5), new ItemBuilder(Material.FERMENTED_SPIDER_EYE).setName("§cEndormissement"), role,
                    "§7Vous permet d'§cendormir§7 un ou plusieurs joueur(s) en fonction de votre clique:",
                    "",
                    "§8 • §fClique droit:§7 En §cvisant§7 un joueur, vous permet de l'§cendormir§7 pendant§c 8 secondes§7 (1x/10m)",
                    "",
                    "§8 • §fClique gauche:§7 Vous permet d'endormir tout joueurs présent autours de vous dans un rayon de§c 30 blocs§7 pendant§c 3 secondes§7 (1x/15m)",
                    "",
                    "§7Un joueur§c endormie§7 ne peut pas bouger mais il peut être§c frappé§7,",
                    "§7Les§c démons§7 étant§c endormie§7 seront toucher§c 2x§7 moins longtemps (dont§a Nezuko§7)");
            setShowCdInDesc(false);
            this.cliqueDroit = new CliqueDroit(getRole());
            getRole().addPower(this.cliqueDroit);
            this.cliqueGauche = new CliqueGauche(getRole());
            getRole().addPower(this.cliqueGauche);
            Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> {
                getShowCdRunnable().setCustomText(true);
                getShowCdRunnable().setCustomTexte("§fClique droit est§c utilisable§7 |§f Clique gauche est§c utilisable");
            }, 10);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                if (event.getAction().name().contains("RIGHT")) {
                    return this.cliqueDroit.checkUse(player, map);
                } else if (event.getAction().name().contains("LEFT")) {
                    return this.cliqueGauche.checkUse(player, map);
                }
            }
            return false;
        }

        @Override
        public void tryUpdateActionBar() {
            getShowCdRunnable().setCustomTexte((this.cliqueDroit.getCooldown().isInCooldown() ?
                    "§fClique droit: §c"+ StringUtils.secondsTowardsBeautiful(this.cliqueDroit.getCooldown().getCooldownRemaining()) :
                    "§fClique droit est§c utilisable") + "§7 | " + (this.cliqueGauche.getCooldown().isInCooldown() ?
                    "§fClique gauche: §c"+StringUtils.secondsTowardsBeautiful(this.cliqueGauche.getCooldown().getCooldownRemaining()):
                    "§fClique gauche est§c utilisable"));
        }

        private static class CliqueDroit extends Power {

            public CliqueDroit(@NonNull RoleBase role) {
                super("§cEndormissement§7 (§fClique droit§7)", new Cooldown(60*10), role);
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(Player player, Map<String, Object> map) {
                final Player target = RayTrace.getTargetPlayer(player, 20, null);
                if (target == null) {
                    player.sendMessage("§cIl faut viser un joueur !");
                    return false;
                }
                final GamePlayer gamePlayer = getRole().getGameState().getGamePlayer().get(target.getUniqueId());
                if (gamePlayer == null) {
                    player.sendMessage("§cIl faut viser un joueur !");
                    return false;
                }
                if (gamePlayer.getRole() == null) {
                    player.sendMessage("§cIl faut viser un joueur valide !");
                    return false;
                }
                if (gamePlayer.getRole() instanceof DemonsRoles) {
                    gamePlayer.stun(20*4, true);
                } else {
                    gamePlayer.stun(20*8, true);
                }
                player.sendMessage("§7Vous avez§c endormi§7(§ce§7):§c "+target.getDisplayName());
                return true;
            }
        }
        private static class CliqueGauche extends Power {

            public CliqueGauche(@NonNull RoleBase role) {
                super("§cEndormissement§7 (§fClique gauche§7)", new Cooldown(60*15), role);
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(Player player, Map<String, Object> map) {
                final GameState gameState = GameState.getInstance();
                final List<Player> aroundPlayers = Loc.getNearbyPlayersExcept(player, 30);
                if (aroundPlayers.isEmpty()) {
                    player.sendMessage("§cIl n'y a pas asser de joueur autours de vous !");
                    return false;
                }
                for (final Player target : aroundPlayers) {
                    if (!gameState.hasRoleNull(target.getUniqueId())) {
                        final GamePlayer gameTarget = gameState.getGamePlayer().get(target.getUniqueId());
                        if (gameTarget.getRole() instanceof DemonsRoles) {
                            gameTarget.stun(30, true);
                        } else {
                            gameTarget.stun(60, true);
                        }
                    }
                }
                return true;
            }
        }
    }
}