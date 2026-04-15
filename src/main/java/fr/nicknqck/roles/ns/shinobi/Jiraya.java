package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.EffectWhen;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.interfaces.IRoleGotSubWorld;
import fr.nicknqck.interfaces.ISubRoleWorld;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.EChakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.HShinobiRoles;
import fr.nicknqck.roles.ns.power.Rasengan;
import fr.nicknqck.runnables.PregenerationTask;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Jiraya extends HShinobiRoles implements IRoleGotSubWorld {

    private JirayaSubWorld jirayaSubWorld;

	public Jiraya(UUID player) {
		super(player);
	}

	@Override
	public void RoleGiven(GameState gameState) {
        Main.getInstance().getRoleWorldManager().addWorldManaged("Gamabunta2", new JirayaSubWorld());
        addPower(new Rasengan(this), true);
        addPower(new FukasakuEtShimaPower(this), true);
        addPower(new GamabuntaPower(this), true);
	}

	@Override
	public @NonNull Roles getRoles() {
		return Roles.Jiraya;
	}

    @Override
    public EChakras[] getChakrasCanHave() {
        return new EChakras[] {EChakras.KATON};
    }

	@Override
	public @NonNull Intelligence getIntelligence() {
		return Intelligence.INTELLIGENT;
	}

	@Override
	public String getName() {
		return "Jiraya";
	}

    @Override
    public ISubRoleWorld getSubWorld() {
        if (this.jirayaSubWorld == null) {
            this.jirayaSubWorld = new JirayaSubWorld();
        }
        return this.jirayaSubWorld;
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }
    private static final class FukasakuEtShimaPower extends ItemPower {

        private final ShimaPower shimaPower;
        private final FukasakuPower fukasakuPower;

        public FukasakuEtShimaPower(@NonNull RoleBase role) {
            super("§aFukasaku et Shima§r", new Cooldown(1), new ItemBuilder(Material.NETHER_STAR).setName("§aFukasaku et Shima"), role,
                    "§7Effectue une action différente en fonction du clique:",
                    "",
                    "§fClique droit§7:§a Shima§7 vous offre pendant§c 3 minutes§7 un effet aléatoire parmi:§e Speed I§7,§c Force I§7 et§9 Résistance I§7. (1x/7min)",
                    "",
                    "§fClique gauche§7:§a Fukasaku§7 lancera des§c particules de feu§7, les joueurs touchés§c s'enflammeront§7. (1x/90s)"
            );
            this.shimaPower = new ShimaPower(role);
            this.fukasakuPower = new FukasakuPower(role);
            setSendCooldown(false);
            getShowCdRunnable().setCustomText(true);
            setShowCdInDesc(false);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                if (event.getAction().name().contains("RIGHT")) {
                    return this.shimaPower.checkUse(player, map);
                } else if (event.getAction().name().contains("LEFT")) {
                    return this.fukasakuPower.checkUse(player, map);
                }
            }
            return false;
        }

        @Override
        public void tryUpdateActionBar() {
            getShowCdRunnable().setCustomTexte(
                    this.fukasakuPower.getName()+(
                            !this.fukasakuPower.getCooldown().isInCooldown() ?
                                    "§a est§c disponible" :
                                    "§a est en§c cooldown§a:§c "+StringUtils.secondsTowardsBeautiful(this.fukasakuPower.getCooldown().getCooldownRemaining()))
                    +"§7 | "+
                            this.shimaPower.getName()+(
                                    !this.shimaPower.getCooldown().isInCooldown() ?
                            "§a est§c disponible"
                            :
                            "§a est en§c cooldown§a:§c "+StringUtils.secondsTowardsBeautiful(this.shimaPower.getCooldown().getCooldownRemaining()))
            );
        }

        private static final class ShimaPower extends Power {

            public ShimaPower(@NonNull RoleBase role) {
                super("§aShima§r", new Cooldown(60*7), role);
                setShowInDesc(false);
                role.addPower(this);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                final int random = RandomUtils.getRandomInt(1, 3);
                if (random == 1) {
                    player.sendMessage("§aShima vous offre§c 3 minutes§a de§e Speed I§a.");
                    getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*180, 0, false, false), EffectWhen.NOW);
                    return true;
                } else if (random == 2) {
                    player.sendMessage("§aShima vous offre§c 3 minutes§a de§c Force I§a.");
                    getRole().givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*180, 0, false, false), EffectWhen.NOW);
                    return true;
                } else if (random == 3) {
                    player.sendMessage("§aShima vous offre§c 3 minutes§a de§9 Résistance I§a.");
                    getRole().givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*180, 0, false, false), EffectWhen.NOW);
                    return true;
                }
                return false;
            }
        }
        private static final class FukasakuPower extends Power {

            public FukasakuPower(@NonNull RoleBase role) {
                super("§aFukasaku§r", new Cooldown(90), role);
                role.addPower(this);
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                final List<Player> playerList = MathUtil.flameShot(player, 14.0, 0.8);
                if (playerList.isEmpty()) {
                    return true;
                }
                for (Player target : playerList) {
                    if (target == player)continue;
                    target.setHealth(Math.max(0.1, target.getHealth()-1.0));
                    target.setFireTicks(180);
                    target.sendMessage("§aVous avez été toucher par la§c déflagration§2 de Fukasaku");
                    player.sendMessage("§c"+target.getName()+"§a a été toucher par la§c déflagration§a de§2 Fukasaku§a.");
                }
                return true;
            }
        }
    }
    private static final class GamabuntaPower extends ItemPower {

        public GamabuntaPower(@NonNull RoleBase role) {
            super("§aVentre du crapaud§r", new Cooldown(60*10), new ItemBuilder(Material.NETHER_STAR).setName("§aVentre du crapaud"), role,
                    "§7Téléporte tout les joueurs autours de vous dans une autre dimensions",
                    "§7toute les§c 5 secondes§7 a l'intérieur un joueur reçoit§c 5 secondes§7 de§2 poison§7.");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (getRole() instanceof IRoleGotSubWorld) {
                    final ISubRoleWorld iSubRoleWorld = ((IRoleGotSubWorld) getRole()).getSubWorld();
                    if (iSubRoleWorld instanceof JirayaSubWorld) {
                        final List<Player> playerList = new ArrayList<>(Loc.getNearbyPlayers(player, 25));
                        Collections.shuffle(playerList);
                        final List<Location> locationList = new ArrayList<>(iSubRoleWorld.getPossibleTeleportLocations());
                        for (@NonNull final Player target : playerList) {
                            Collections.shuffle(locationList);
                            target.teleport(locationList.get(0));
                            target.sendMessage("§7Vous avez été aspirer dans le§a Ventre du crapaud");
                        }
                        new GamabuntaRunnable(this, (JirayaSubWorld) iSubRoleWorld).runTaskTimerAsynchronously(getPlugin(), 0, 20);
                        return true;
                    }
                } else {
                    player.sendMessage("§cVotre rôle n'est lié a aucun monde !");
                }
            }
            return false;
        }
        private void stopGamabunta(@NonNull final World world, @NonNull final String zipName) {
            getCooldown().setActualCooldown(getCooldown().getOriginalCooldown()-120);
            getRole().getGamePlayer().sendMessage(Main.getInstance().getNAME()+"§7 Tout les joueurs sont sortie du§a Ventre du crapaud§7.");
            for (Player worldPlayer : world.getPlayers()) {
                worldPlayer.setNoDamageTicks(60);
                GameListener.RandomTp(worldPlayer);
                final GamePlayer gamePlayer = GamePlayer.of(worldPlayer.getUniqueId());
                if (gamePlayer != null){
                    gamePlayer.getActionBarManager().removeInActionBar(zipName);
                }
            }
        }
        private static final class GamabuntaRunnable extends BukkitRunnable {

            private final GamabuntaPower gamabuntaPower;
            private final JirayaSubWorld jirayaSubWorld;
            private int timeLeft = 60*2;
            private String lastPoison = "§cPersonne n'a encore été§2 empoisonné";
            private int timeBeforePoison = 5;

            private GamabuntaRunnable(GamabuntaPower gamabuntaPower, JirayaSubWorld jirayaSubWorld) {
                this.gamabuntaPower = gamabuntaPower;
                this.jirayaSubWorld = jirayaSubWorld;
            }

            @Override
            public void run() {
                if (!GameState.inGame()) {
                    cancel();
                    return;
                }
                final World world = Bukkit.getWorld(jirayaSubWorld.getWorldName());
                if (world != null) {
                    if (this.timeLeft <= 0) {
                        Bukkit.getScheduler().runTask(this.gamabuntaPower.getPlugin(), () -> this.gamabuntaPower.stopGamabunta(world, this.jirayaSubWorld.getZipFileName()));
                        cancel();
                        return;
                    }
                    final List<Player> playerList = new ArrayList<>(world.getPlayers());
                    for (Player worldPlayer : playerList) {
                        final GamePlayer gamePlayer = GamePlayer.of(worldPlayer.getUniqueId());
                        if (gamePlayer == null)continue;
                        gamePlayer.getActionBarManager().updateActionBar(this.jirayaSubWorld.getZipFileName(), "§bTemps restant:§c "+StringUtils.secondsTowardsBeautiful(this.timeLeft)+"§7 | "+this.lastPoison);
                    }
                    if (this.timeBeforePoison <= 0) {
                        this.timeBeforePoison = 5;
                        int essai = 15;
                        Player target = null;
                        while (target == null ||target.getUniqueId().equals(this.gamabuntaPower.getRole().getPlayer())) {
                            Collections.shuffle(playerList);
                            target = playerList.get(0);
                            if (target != null) {
                                final GamePlayer gamePlayer = GamePlayer.of(target.getUniqueId());
                                if (gamePlayer != null) {
                                    if (!gamePlayer.check()) {
                                        target = null;
                                    }
                                    if (gamePlayer.getUuid().equals(this.gamabuntaPower.getRole().getPlayer())) {
                                        target = null;
                                    }
                                }
                            }
                            essai--;
                            if (essai < 0) {
                                break;
                            }
                        }
                        if (target != null) {
                            Player finalTarget = target;
                            this.lastPoison = "§c"+finalTarget.getName()+"§7 a été§2 empoisonné";
                            Bukkit.getScheduler().runTask(gamabuntaPower.getPlugin(), () -> {
                                final GamePlayer gamePlayer = GamePlayer.of(finalTarget.getUniqueId());
                                if (gamePlayer != null) {
                                    if (gamePlayer.check()) {
                                        gamePlayer.getRole().givePotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0, false, false), EffectWhen.NOW);
                                    }
                                }
                            });
                        }
                    }
                }
                final GamePlayer gamePlayer = gamabuntaPower.getRole().getGamePlayer();
                if (!gamePlayer.isAlive() && this.timeLeft > 10) {
                    this.timeLeft = 10;
                }
                this.timeLeft--;
                this.timeBeforePoison--;
            }
        }
    }

    //Ce monde sera donc créer automatiquement à chaque fois que le rôle est donné
    private static final class JirayaSubWorld implements ISubRoleWorld {

        private double percent = 0.0;
        private boolean pregen = false;
        private final List<Location> locationList;

        private JirayaSubWorld() {
            this.locationList = new ArrayList<>();
        }

        @Override
        public String getWorldName() {
            return "Gamabunta2";
        }

        @Override
        public String getZipFileName() {
            return "Gamabunta2.zip";
        }

        @Override
        public World createWorld() {
            final World world = Bukkit.createWorld(getWorldCreator().generateStructures(false));
            world.setGameRuleValue("doMobSpawning", "false");
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("spectatorsGenerateChunks", "false");
            world.setGameRuleValue("naturalRegeneration", "false");
            world.setGameRuleValue("announceAdvancements", "false");
            world.setGameRuleValue("doMobSpawning", "false");
            world.setGameRuleValue("doFireTick", "false");
            this.locationList.add(new Location(world, 0.0, 6.0, 0.0, 1f, 0.2f));
            this.locationList.add(new Location(world, 25.25, 5.0, 16.5, 90.0f, 0.4f));
            this.locationList.add(new Location(world, 25.5, 5.0, -14.0, 90.0f, 0.4f));
            this.locationList.add(new Location(world, -26.5, 5.0, -15.0, -90.0f, 0.4f));
            this.locationList.add(new Location(world, -25.5, 5.0, 16.5, -90.0f, 0.4f));
            return world;
        }

        @Override
        public double getActualPercentPregenTask() {
            return this.percent;
        }

        @Override
        public void startPregen(World world) {
            final PregenerationTask pregenerationTask = new PregenerationTask(world, 10);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (isPregen()) {
                        cancel();
                        return;
                    }
                    pregenerationTask.run();
                    percent = pregenerationTask.getPercent();
                    if (pregenerationTask.isFinished()) {
                        cancel();
                        setHasBeenPregen(true);
                    }
                }
            }.runTaskTimer(Main.getInstance(), 1, 20);
        }

        @Override
        public WorldCreator getWorldCreator() {
            return new WorldCreator(getWorldName());
        }

        @Override
        public void setHasBeenPregen(boolean pregen) {
            this.pregen = pregen;
        }

        @Override
        public boolean isPregen() {
            return this.pregen;
        }

        @Override
        public List<Location> getPossibleTeleportLocations() {
            final World world = Bukkit.getWorld(getWorldName());
            if (world != null) {
                this.locationList.add(new Location(world, 0.0, 6.0, 0.0, 1f, 0.2f));
                this.locationList.add(new Location(world, 25.25, 5.0, 16.5, 90.0f, 0.4f));
                this.locationList.add(new Location(world, 25.5, 5.0, -14.0, 90.0f, 0.4f));
                this.locationList.add(new Location(world, -26.5, 5.0, -15.0, -90.0f, 0.4f));
                this.locationList.add(new Location(world, -25.5, 5.0, 16.5, -90.0f, 0.4f));
            }
            return this.locationList;
        }
    }
}