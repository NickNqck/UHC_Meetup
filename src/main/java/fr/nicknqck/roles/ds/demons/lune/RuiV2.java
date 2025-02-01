package fr.nicknqck.roles.ds.demons.lune;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.demons.Kumo;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class RuiV2 extends DemonsRoles {

    public RuiV2(UUID player) {
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
        return "Rui§7 (§6V2§7)";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Rui;
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Demon;
    }

    @Override
    public void resetCooldown() {}

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
        this.addPower(new FilPower(this), true);
    }
    private static class FilPower extends ItemPower {

        private Power equipedPower;
        private final Map<Integer, Power> powerMap;

        protected FilPower(@NonNull RuiV2 role) {
            super("Fils de Rui", new Cooldown(5), new ItemBuilder(Material.NETHER_STAR).setName("§cFils de Rui"), role,
                    "§7Vous permet d'utiliser vos fils, pour les changées il vous faudra effectuer un§n clique gauche§r§7:",
                    "",
                    "§fAttaque longue porté§7: En visant un §cjoueur§7, vous permet de lui infliger §c 2,5❤§7 ainsi que§c 15%§7 de chance de lui donner en plus§c 15 secondes§7 de§2 poison 1§7.",
                    "",
                    "§fFil attractif§7: En visant un§c joueur§7, vous permet de l'attirer très fortement vers vous",
                    "",
                    "§fEmprisonnement dans la toile§7: En visant un §cjoueur§7, vous permet de l'enfermer à l'intérieur de plusieurs§c toiles d'araignées",
                    "",
                    "§fAllègement des toiles§7: Si§c maximum 1§7 de vos§c pouvoirs§7 est en cooldown, vous obtiendrez§c 5 minutes§7 de l'effet§b Speed I§7,",
                    "§7Également vous donnerez l'effet§c résistance I§7 à§c Kumo§7 si elle est dans un§c rayon§7 de§c 30 blocs"
            );
            this.powerMap = new LinkedHashMap<>();
            final LongAttackFilPower longAttackFilPower = new LongAttackFilPower(role);
            this.equipedPower = longAttackFilPower;
            final GrabPower grabPower = new GrabPower(role);
            final CobWebPower cobWebPower = new CobWebPower(role);
            final AllegementPower allegementPower = new AllegementPower(role, this);
            role.addPower(grabPower);
            role.addPower(longAttackFilPower);
            role.addPower(cobWebPower);
            role.addPower(allegementPower);
            this.powerMap.put(0, longAttackFilPower);
            this.powerMap.put(1, grabPower);
            this.powerMap.put(2, cobWebPower);
            this.powerMap.put(3, allegementPower);
            getShowCdRunnable().setCustomText(true);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                if (event.getAction().name().contains("LEFT")) {//Pour changer de pouvoir
                    int power = getIntFromEquipedPower();
                    getShowCdRunnable().setCustomTexte("");
                    power++;
                    if (!this.powerMap.containsKey(power)) {
                        power = 0;
                    }
                    final Power futurePower = this.powerMap.get(power);
                    getShowCdRunnable().setCustomTexte((!futurePower.getCooldown().isInCooldown() ?
                                    "§c"+futurePower.getName()+" est§6 utilisable" :
                                    "§c"+futurePower.getName()+" est en cooldown (§b"+StringUtils.secondsTowardsBeautiful(futurePower.getCooldown().getCooldownRemaining())+"§c)"));
                    this.equipedPower = futurePower;
                } else if (event.getAction().name().contains("RIGHT")){
                    if (this.equipedPower == null) {
                        player.sendMessage("§cAucun pouvoir n'a été équiper.");
                        return false;
                    }
                    if (this.equipedPower.getCooldown().isInCooldown()) {
                        player.sendMessage("§c"+this.equipedPower.getName()+" est en cooldown:§b "+StringUtils.secondsTowardsBeautiful(this.equipedPower.getCooldown().getCooldownRemaining()));
                        return false;
                    }
                    return this.equipedPower.checkUse(player, map);
                }
            }
            return false;
        }

        @Override
        public void tryUpdateActionBar() {
            getShowCdRunnable().setCustomTexte((!this.equipedPower.getCooldown().isInCooldown() ?
                            "§c"+this.equipedPower.getName()+" est§6 utilisable" :
                            "§c"+this.equipedPower.getName()+" est en cooldown (§b"+StringUtils.secondsTowardsBeautiful(this.equipedPower.getCooldown().getCooldownRemaining())+"§c)"));
        }

        private Integer getIntFromEquipedPower() {
            if (this.equipedPower == null) {
                return 0;
            }
            for (final Integer integer : powerMap.keySet()) {
                if (powerMap.get(integer).equals(this.equipedPower)) {
                    return integer;
                }
            }
            return 0;
        }
        private static final class LongAttackFilPower extends Power {

            public LongAttackFilPower(@NonNull RuiV2 role) {
                super("Attaque longue porté", new Cooldown(60*7), role);
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(Player player, Map<String, Object> map) {
                final Player target = RayTrace.getTargetPlayer(player, 25, null);
                if (target == null) {
                    player.sendMessage("§cIl faut viser un joueur !");
                    return false;
                }
                if (target.getHealth() - 5.0 <= 0.0) {
                    target.setHealth(0.1);
                } else {
                    target.setHealth(target.getHealth()-5.0);
                }
                target.damage(0.0);
                if (Main.RANDOM.nextInt(100) <= 15) {
                    target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*10, 0, false, false), true);
                    target.sendMessage("§7Vous avez été atteint par le§2 poison§7 de§c Rui§7 (§6V2§7)");
                    player.sendMessage("§7Votre§2 poison§7 à atteint§c "+target.getDisplayName());
                }
                player.sendMessage("§7Vous avez utiliser votre§c "+getName()+"§7 sur§c "+target.getDisplayName());
                target.sendMessage("§cRui§7 (§6V2§7)§c a utilisé son "+getName()+" sur vous");
                return true;
            }
        }
        private static final class GrabPower extends Power {

            public GrabPower(@NonNull RuiV2 role) {
                super("Fil attractif", new Cooldown(60*5), role);
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(Player player, Map<String, Object> map) {
                final Player target = RayTrace.getTargetPlayer(player, 25, null);
                if (target == null) {
                    player.sendMessage("§cIl faut viser un joueur !");
                    return false;
                }
                pullPlayerTowards(player, target);
                return false;
            }
            private void pullPlayerTowards(final @NonNull Player owner,final @NonNull Player target) {
                final Location locOwner = owner.getLocation();
                final Location locTarget = target.getLocation();

                final Vector direction = locOwner.toVector().subtract(locTarget.toVector()).normalize();
                direction.multiply(8.0);

                target.setVelocity(direction);
            }

        }
        private static final class CobWebPower extends Power {

            public CobWebPower(@NonNull RuiV2 role) {
                super("Emprisonnement dans la toile", new Cooldown(60*7), role);
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(Player player, Map<String, Object> map) {
                final Player target = RayTrace.getTargetPlayer(player, 50, null);
                if (target == null) {
                    player.sendMessage("§cIl faut viser un joueur !");
                    return false;
                }
                final List<Block> blockList = this.getBlockAround(target);
                for (final Block block : blockList) {
                    block.setType(Material.WEB);
                }
                player.sendMessage("§c"+target.getDisplayName()+"§7 à complètement été recouvert de toile d'araignée");
                target.sendMessage("§cRui§7 (§6V2§7) vous à attraper dans sa§c toile d'araignée");
                return true;
            }
            private List<Block> getBlockAround(@NonNull final Player target) {
                final List<Block> toReturn = new ArrayList<>();
                final Location initLoc = target.getLocation().clone();
                for (int x = initLoc.getBlockX()-1; x <= initLoc.getBlockX()+1; x++) {
                    for (int y = initLoc.getBlockY()-1; y <= initLoc.getBlockY()+1; y++) {
                        for (int z = initLoc.getBlockZ()-1; z <= initLoc.getBlockZ()+1; z++) {
                            final Location newLoc = new Location(initLoc.getWorld(), x, y, z);
                            toReturn.add(newLoc.getBlock());
                        }
                    }
                }
                return toReturn;
            }
        }
        private static final class AllegementPower extends Power {

            private final FilPower filPower;

            public AllegementPower(@NonNull RuiV2 role, @NonNull FilPower filPower) {
                super("Allègement d'araignée", new Cooldown(60*10), role);
                this.filPower = filPower;
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(Player player, Map<String, Object> map) {
                int amountInCd = 0;
                for (final Power power : this.filPower.powerMap.values()) {
                    if (power.getCooldown().isInCooldown()) {
                        amountInCd++;
                    }
                }
                if (amountInCd >= 2) {
                    player.sendMessage("§cVous avez trop utiliser vos fils pour pouvoir utiliser cette technique-ci");
                    return false;
                }
                for (final Power power : this.filPower.powerMap.values()) {
                    if (!power.getCooldown().isInCooldown()) {
                        power.getCooldown().use();
                    } else {
                        power.getCooldown().setActualCooldown(power.getCooldown().getOriginalCooldown());
                    }
                }
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*5, 0, false, false), EffectWhen.NOW);
                new KumoResiRunnable(this.getRole().getGameState(), (RuiV2) this.getRole());
                return true;
            }
            private static final class KumoResiRunnable extends BukkitRunnable {

                private final GameState gameState;
                private final RuiV2 ruiV2;
                private int timeRemaining;


                private KumoResiRunnable(GameState gameState, RuiV2 ruiV2) {
                    this.gameState = gameState;
                    this.ruiV2 = ruiV2;
                    this.timeRemaining = 60*5;
                    runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
                }

                @Override
                public void run() {
                    if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                        cancel();
                        return;
                    }
                    if (timeRemaining <= 0) {
                        cancel();
                        return;
                    }
                    if (!this.ruiV2.getGamePlayer().isAlive()) {
                        System.out.println("Returned");
                        return;
                    }
                    final Player owner = Bukkit.getPlayer(this.ruiV2.getPlayer());
                    if (owner == null)return;
                    final String key = "ruiv2.resirunnable";
                    if (!ruiV2.getGamePlayer().getActionBarManager().containsKey(key)) {
                        ruiV2.getGamePlayer().getActionBarManager().addToActionBar(key, "§cTemp restant: §b"+StringUtils.secondsTowardsBeautiful(this.timeRemaining));
                    } else {
                        ruiV2.getGamePlayer().getActionBarManager().updateActionBar(key, "§cTemp restant:§b "+StringUtils.secondsTowardsBeautiful(this.timeRemaining));
                    }
                    final List<GamePlayer> aroundPlayers = Loc.getNearbyGamePlayers(owner.getLocation(), 30);
                    for (final GamePlayer gamePlayer : aroundPlayers) {
                        if (gamePlayer.getRole() == null)continue;
                        if (gamePlayer.getRole() instanceof Kumo) {
                            gamePlayer.getRole().givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 0, false, false), EffectWhen.NOW);
                        }
                    }
                    timeRemaining--;
                }
            }
        }
    }
}