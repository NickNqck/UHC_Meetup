package fr.nicknqck.managers;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.entity.bijuv2.BijuBase;
import fr.nicknqck.entity.bijuv2.impl.Matatabi;
import fr.nicknqck.entity.bijuv2.impl.Saiken;
import fr.nicknqck.entity.bijuv2.impl.SonGoku;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.events.custom.StartGameEvent;
import fr.nicknqck.events.custom.biju.*;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.rank.ChatRank;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class BijuManager implements Listener {

    public static String a = "[BijuManager]";

    @Getter
    @Setter
    private boolean bijuEnable = true;
    @Getter
    private final Map<BijuBase, Integer> bijuSpawnMap;
    @Getter
    private final Map<Class<? extends BijuBase>, Boolean> bijuEnables;
    private final Map<GamePlayer, BijuBase> ownerMap;
    private final List<BukkitRunnable> runnableList = new ArrayList<>();
    private final List<BijuBase> bijusDeads = new ArrayList<>();
    @Getter
    private final Map<Class<? extends BijuBase>, BijuBase> classBijuMap = new HashMap<>();

    public BijuManager() {
        EventUtils.registerEvents(this);
        this.bijuSpawnMap = new HashMap<>();
        this.bijuEnables = new HashMap<>();
        this.ownerMap = new HashMap<>();
        addBijuInRegistery(Saiken.class);
        addBijuInRegistery(SonGoku.class);
        addBijuInRegistery(Matatabi.class);
    }

    @EventHandler
    private void onStartGame(final StartGameEvent event) {
        if (this.isBijuEnable()) {
            for (final Class<? extends BijuBase> clazz : this.bijuEnables.keySet()) {
                if (this.bijuEnables.get(clazz)) {
                    try {
                        final BijuBase biju = clazz.newInstance();
                        int time = RandomUtils.getRandomInt(biju.getMinTimeProc(), biju.getMaxTimeProc());
                        this.bijuSpawnMap.put(biju, time);
                        final BijuInstantiateEvent bijuInstantiateEvent = new BijuInstantiateEvent(biju, time);
                        Bukkit.getPluginManager().callEvent(bijuInstantiateEvent);
                        this.runnableList.add(new BijuUpdateSpawnLocationRunnable(biju));
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
    @EventHandler
    private void bijuInstantiateEvent(final BijuInstantiateEvent event) {
     //   this.runnableList.add(new BijuSpawnRunnable(event.getBiju(), event.getSpawnTime()));
        this.runnableList.add(new BijuSpawnRunnable(event.getBiju(), 60));
    }
    @EventHandler
    private void onEndGame(final EndGameEvent event) {
        for (final BukkitRunnable runnable : this.runnableList) {
            runnable.cancel();
        }
        this.runnableList.clear();
        this.bijuSpawnMap.clear();
        this.bijusDeads.clear();
    }
    @EventHandler
    private void bijuSpawnEvent(final BijuSpawnEvent event) {
        this.runnableList.add(new BijuTPRunnable(30.0, event.getBiju()));
    }
    @EventHandler
    private void EntityDeathEvent(final EntityDeathEvent event) {
        if (this.bijuSpawnMap.isEmpty())return;
        boolean call = false;
        BijuBase bijus = null;
        for (final BijuBase biju : this.bijuSpawnMap.keySet()) {
            if (biju.getEntity() == null)continue;
            if (biju.getEntity().getUniqueId().equals(event.getEntity().getUniqueId())) {
                call = true;
                bijus = biju;
                break;
            }
        }
        if (this.bijusDeads.contains(bijus)) {
            return;
        }
        if (call) {
            this.bijusDeads.add(bijus);
            final BijuDeathEvent deathEvent = new BijuDeathEvent(bijus, event.getEntity().getKiller(), event.getEntity().getLocation());
            Bukkit.getPluginManager().callEvent(deathEvent);
            if (event.getEntity() instanceof Slime || event.getEntity() instanceof MagmaCube) {
                ((Slime) event.getEntity()).setSize(0);
            }
            event.getDrops().clear();
        }
    }
    @EventHandler
    private void BijuDeathEvent(final BijuDeathEvent event) {
        if (event.getBiju().getEntity() instanceof Slime || event.getBiju().getEntity() instanceof MagmaCube) {
            ((Slime) event.getBiju().getEntity()).setSize(0);
        }
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§6[Naruto UHC] §c"+event.getBiju().getName()+"§f est mort !");
        if (event.getKiller() == null) {
            GameListener.dropItem(event.getLocation(), event.getBiju().getItemInMenu());
            System.out.println(a+" "+event.getBiju().getName()+" has been drop");
            return;
        } else {
            if (!GameState.getInstance().hasRoleNull(event.getKiller().getUniqueId())) {
                final RoleBase role = GameState.getInstance().getGamePlayer().get(event.getKiller().getUniqueId()).getRole();
                if (!this.ownerMap.containsKey(role.getGamePlayer())) {
                    role.addPower(new BijuBase.BijuPower(event.getBiju(), role), true);
                    role.getGamePlayer().sendMessage("§7Vous êtes devenus l'hôte de "+event.getBiju().getName());
                    this.ownerMap.put(role.getGamePlayer(), event.getBiju());
                } else {
                    role.giveItem(event.getKiller(), false, event.getBiju().getItemInMenu());
                    role.getGamePlayer().sendMessage("§7Vous avez récupérer "+event.getBiju().getName());
                }
            } else {
                event.getKiller().sendMessage("§cIl faut avoir un rôle pour utiliser les bijus !");
                GameListener.dropItem(event.getLocation(), event.getBiju().getItemInMenu());
                System.out.println(a+" "+event.getBiju().getName()+" has been drop");
            }
        }
        System.out.println(a+" "+event.getBiju().getName()+" is dead");
    }
    @EventHandler
    private void onRecup(final PlayerPickupItemEvent event) {
        if (this.bijuSpawnMap.isEmpty())return;
        for (final BijuBase biju : this.bijuSpawnMap.keySet()) {
            if (event.getItem().getItemStack().isSimilar(biju.getItemInMenu())) {
                if (GameState.getInstance().hasRoleNull(event.getPlayer().getUniqueId())) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("§cIl faut avoir un rôle pour récupérer un Biju");
                    return;
                }
                if (biju.getHote() != null) {
                    event.getPlayer().sendMessage("§7Vous avez récupérer "+biju.getName());
                    return;
                }
                GamePlayer gamePlayer = GameState.getInstance().getGamePlayer().get(event.getPlayer().getUniqueId());
                if (biju.getBijuPower() != null){
                    event.getItem().setItemStack(biju.getBijuPower().getItem());
                }
                biju.setGamePlayer(gamePlayer);
                gamePlayer.getRole().addPower(biju.getBijuPower());
                Bukkit.getPluginManager().callEvent(new BijuRecupItemEvent(biju, gamePlayer));
                break;
            }
        }
    }
    @EventHandler
    private void onInventoryClick(@NonNull final InventoryClickEvent event) {
        if (event.getWhoClicked() == null)return;
        if (event.getCurrentItem() == null)return;
        if (event.getClickedInventory() == null)return;
        if (this.classBijuMap.isEmpty())return;
        if (event.getCurrentItem().getItemMeta() == null)return;
        if (event.getCurrentItem().getItemMeta().getDisplayName() == null)return;
        for (@NonNull final BijuBase bijuBase : this.classBijuMap.values()) {
            if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(bijuBase.getName())) {
                @NonNull final InventoryClickBijuEvent bijuEvent = new InventoryClickBijuEvent(bijuBase, true, event.getClickedInventory(), event.getWhoClicked());
                Bukkit.getPluginManager().callEvent(bijuEvent);
                event.setCancelled(bijuEvent.isCancelled());
                break;
            } else {
                if (bijuBase.getBijuPower() != null) {
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(bijuBase.getName())) {
                        @NonNull final InventoryClickBijuEvent bijuEvent = new InventoryClickBijuEvent(bijuBase, false, event.getClickedInventory(), event.getWhoClicked());
                        Bukkit.getPluginManager().callEvent(bijuEvent);
                        event.setCancelled(bijuEvent.isCancelled());
                        break;
                    }
                }
            }
        }
    }
    public void addBijuInRegistery(final Class<? extends BijuBase> clazz) {
        if (!this.bijuEnables.containsKey(clazz)) {
            try {
                this.classBijuMap.put(clazz, clazz.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            this.bijuEnables.put(clazz, false);
        }
    }
    @EventHandler
    private void onBijuClick(@NonNull final InventoryClickBijuEvent event) {
        if (event.isInventoryItem()) {
            if (GameState.getInstance().getServerState().equals(GameState.ServerStates.InLobby)) {
                if (ChatRank.isHost(event.getWhoClicked().getUniqueId())) {
                    this.bijuEnables.replace(event.getBiju().getClass(), !this.bijuEnables.get(event.getBiju().getClass()));
                    event.setCancelled(true);
                } else {
                    event.getWhoClicked().sendMessage("§cIl faut au minimum être Host pour configurer les bijus !");
                    event.setCancelled(true);
                }
                Main.getInstance().getInventories().openConfigBijusInventory((Player) event.getWhoClicked());
            }
        }
    }
    @EventHandler
    private void onDamage(@NonNull final EntityDamageEvent event) {
        if (event.getEntity() instanceof MagmaCube) {
            event.setDamage(event.getDamage()*8);
        }
        if (event.getEntity() instanceof Blaze) {
            if (event.getCause().equals(EntityDamageEvent.DamageCause.DROWNING)) {//Pour pas que les blazes meures par de l'eau
                event.setDamage(0.0);
            }
        }
    }
    @EventHandler
    private void onEntityDamage(@NonNull final EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player))return;
        if (!((Player) event.getDamager()).hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))return;
        if (event.getEntity() instanceof HumanEntity)return;
        event.setDamage(event.getDamage()*0.5304740497679363);//NERF DE LA FORCE
    }
    private static class BijuUpdateSpawnLocationRunnable extends BukkitRunnable {

        private final BijuBase biju;

        private BijuUpdateSpawnLocationRunnable(BijuBase biju) {
            this.biju = biju;
            System.out.println(a+" Started Biju Update Spawn Runnable for "+biju.getName());
            runTaskTimer(Main.getInstance(), 0, 100);
        }

        @Override
        public void run() {
            if (this.isOutsideOfBorder(this.biju.getOriginSpawn())) {
                final Location location = this.moveToOrigin(this.biju.getOriginSpawn());
                this.biju.getOriginSpawn().setX(location.getX());
                this.biju.getOriginSpawn().setY(location.getY());
                this.biju.getOriginSpawn().setZ(location.getZ());
            }
        }
        private boolean isOutsideOfBorder(Location location) {
            WorldBorder border = location.getWorld().getWorldBorder();
            double x = location.getX();
            double z = location.getZ();
            double halfSize = border.getSize() / 2;

            return (Math.abs(x) >= halfSize - 10 || Math.abs(z) >= halfSize - 10);
        }

        private Location moveToOrigin(Location location) {
            double x = location.getX();
            double z = location.getZ();
            if (x < 0.0) {
                location.setX(location.getBlockX()+5);
                location.setY(location.getWorld().getHighestBlockYAt(location));
                return location;
            }
            if (x > 0.0) {
                location.setX(location.getBlockX()-5);
                location.setY(location.getWorld().getHighestBlockYAt(location));
                return location;
            }
            if (z < 0.0) {
                location.setZ(location.getBlockZ()+5);
            } else {
                location.setX(location.getBlockZ()-5);
            }
            location.setY(location.getWorld().getHighestBlockYAt(location));
            return location;
        }
    }
    private static class BijuTPRunnable extends BukkitRunnable {

        private final double autorisedDistance;
        private final BijuBase biju;

        private BijuTPRunnable(double autorisedDistance, BijuBase biju) {
            this.autorisedDistance = autorisedDistance;
            this.biju = biju;
            System.out.println(a+" Started Biju TP Runnable for "+biju.getName()+", actual origin loc is "+ biju.getOriginSpawn());
            runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
        }

        @Override
        public void run() {
            if (this.biju.getEntity() == null) {
                return;
            }
            if (this.biju.getEntity().isDead())return;
            final Location location = this.biju.getEntity().getLocation();
            if (location.distance(this.biju.getOriginSpawn()) >= this.autorisedDistance) {
                this.biju.getEntity().teleport(this.biju.getOriginSpawn());
            }
        }
    }
    private static class BijuSpawnRunnable extends BukkitRunnable {

        private final BijuBase biju;
        private final Integer timeToGo;
        private int actualTime = 0;

        private BijuSpawnRunnable(BijuBase biju, Integer timeToGo) {
            this.biju = biju;
            this.timeToGo = timeToGo;
            System.out.println(a+" Starting "+biju.getName()+" runnable to spawn him at "+timeToGo+" ("+ StringUtils.secondsTowardsBeautiful(timeToGo)+")");
            runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
        }

        @Override
        public void run() {
            if (this.actualTime == timeToGo-30) {
                Bukkit.broadcastMessage(biju.getName() + "§7 va bientôt apparaître !");
            }
            if (this.actualTime == timeToGo) {
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    if (this.biju.checkCanSpawn()) {
                        Bukkit.broadcastMessage(biju.getName() + "§7 est apparu !");
                    }
                });
                cancel();
                return;
            }
            this.actualTime++;
        }
    }
}