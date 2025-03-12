package fr.nicknqck.managers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.entity.krystalbeast.Beast;
import fr.nicknqck.entity.krystalbeast.beast.Lijen;
import fr.nicknqck.entity.krystalbeast.configurable.IConfigurable;
import fr.nicknqck.entity.krystalbeast.creator.BeastCreator;
import fr.nicknqck.entity.krystalbeast.creator.IBeastCreator;
import fr.nicknqck.events.custom.RoleGiveEvent;
import fr.nicknqck.events.custom.beast.BeastDamageEvent;
import fr.nicknqck.events.custom.beast.BeastDeathEvent;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.krystal.KrystalBase;
import fr.nicknqck.utils.event.EventUtils;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class KrystalBeastManager implements Listener {

    private final Map<Class<? extends Beast>, Integer> beastMap;
    private final Map<Class<? extends Beast>, Beast> originBeast;
    private final List<Beast> inGameBeast;

    public KrystalBeastManager() {
        this.beastMap = new HashMap<>();
        this.originBeast = new HashMap<>();
        this.inGameBeast = new ArrayList<>();
        initBeastMap();
        EventUtils.registerEvents(this);
    }

    public void initBeastMap() {
        addPlayableBeast(Lijen.class);
        initOriginBeastMap();
    }
    private void initOriginBeastMap() {
        try {
            for (final Class<? extends Beast> beastClass : this.beastMap.keySet()) {
                this.originBeast.put(beastClass, beastClass.newInstance());
            }
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    public void addPlayableBeast(@NonNull final Class<? extends Beast> beastClass) {
        if (isAlreadyPlayable(beastClass)) {
            int i = this.beastMap.get(beastClass);
            this.beastMap.remove(beastClass, i);
            i++;
            this.beastMap.put(beastClass, i);
        } else {
            this.beastMap.put(beastClass, 0);
        }
    }
    public boolean removePlayableBeast(@NonNull final Class<? extends Beast> beastClass) {
        int amount = getAmountReady(beastClass);
        if (amount <= 0)return false;
        if (this.isAlreadyPlayable(beastClass)) {
            this.beastMap.remove(beastClass, amount);
            amount--;
            this.beastMap.put(beastClass, amount);
            return true;
        }
        return false;
    }
    public boolean isAlreadyPlayable(@NonNull final Class<? extends Beast> beastClass) {
        return this.beastMap.containsKey(beastClass);
    }
    public int getAmountReady(@NonNull final Class<? extends Beast> beastClass) {
        if (isAlreadyPlayable(beastClass)) {
            return beastMap.get(beastClass);
        }
        return 0;
    }
    @EventHandler
    private void onInventoryClick(@NonNull final InventoryClickEvent event) {
        if (event.getCurrentItem() == null)return;
        if (event.getInventory() == null)return;
        if (event.getInventory().getTitle() == null)return;
        if (event.getInventory().getTitle().isEmpty())return;
        final ItemStack item = event.getCurrentItem();
        if (!item.hasItemMeta())return;
        if (event.getInventory().getTitle().equals("§fConfiguration§7 ->§d KrystalBeast")) {
            event.setCancelled(true);
            if (this.beastMap.isEmpty()) {
                event.getWhoClicked().sendMessage("§cImpossible, il y a eu un bug dans la beastMap merci dans informer un administrateur");
                return;
            }
            for (final Beast beast : this.originBeast.values()) {
                if (beast.getItemStack().isSimilar(item)) {
                    if (event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                        addPlayableBeast(beast.getClass());
                        openConfigBeastInventory(event.getWhoClicked());
                    } else if (event.getAction().equals(InventoryAction.PICKUP_HALF)) {
                        removePlayableBeast(beast.getClass());
                        openConfigBeastInventory(event.getWhoClicked());
                    }
                    if (beast instanceof BeastCreator) {
                        if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                            ((IConfigurable) beast).openBeastInventory(event.getWhoClicked());
                        }
                    }
                    break;

                }
            }
        }
        if (!event.getInventory().getTitle().contains("§dKrystalBeast§7 ->"))return;
        if (item.isSimilar(GUIItems.getSelectBackMenu())) {
            event.setCancelled(true);
            openConfigBeastInventory(event.getWhoClicked());
            return;
        }
        for (final Beast beast : this.originBeast.values()) {
            if (beast instanceof BeastCreator) {
                if (((BeastCreator) beast).getInventoryName().equals(event.getInventory().getName())) {
                    event.setCancelled(true);
                    if (item.getItemMeta().getDisplayName().equals("§bTemp maximal")) {
                        if (event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                            beast.addMaxTiming(15);
                        } else if (event.getAction().equals(InventoryAction.PICKUP_HALF)){
                            beast.addMaxTiming(-15);
                        }
                        ((BeastCreator) beast).openBeastInventory(event.getWhoClicked());
                        break;
                    }
                    if (item.getItemMeta().getDisplayName().equals("§bTemp minimal")) {
                        if (event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                            beast.addMinTiming(15);
                        } else if (event.getAction().equals(InventoryAction.PICKUP_HALF)){
                            beast.addMinTiming(-15);
                        }
                        ((BeastCreator) beast).openBeastInventory(event.getWhoClicked());
                        break;
                    }
                    break;
                }
            }
        }
    }
    public void openConfigBeastInventory(HumanEntity player) {
        final Inventory inv = Bukkit.createInventory(player, 27, "§fConfiguration§7 ->§d KrystalBeast");
        for (final Class<? extends Beast> beastClass : this.beastMap.keySet()) {
            if (this.originBeast.containsKey(beastClass)) {
                final Beast beast = this.originBeast.get(beastClass);
                inv.addItem(beast.getItemStack());
            }
        }
        player.openInventory(inv);
    }
    @EventHandler
    private void onRoleGive(RoleGiveEvent event) {
        if (event.isEndGive()){
            for (Class<? extends Beast> beastClass : this.originBeast.keySet()){
                if (this.beastMap.containsKey(beastClass)){
                    int nmb = this.beastMap.get(beastClass);
                    if (nmb>0) {
                        for (int i = nmb; i != 0; i--) {
                            try {
                                final Beast beast = beastClass.newInstance();
                                beast.setMaxTiming(this.originBeast.get(beastClass).getMaxTiming());
                                beast.setMinTiming(this.originBeast.get(beastClass).getMinTiming());
                                beast.calculeProc();
                                new BeastSpawnerRunnable(beast, event.getGameState(), beast.getTimingProc());
                                this.inGameBeast.add(beast);
                            } catch (Exception e) {
                                e.fillInStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    private void EntityDeathEvent(EntityDeathEvent event) {
        for (final Beast beast : this.inGameBeast) {
            if (beast.getBeast() == null)continue;
            if (event.getEntity().getCustomName() == null)continue;
            if (!event.getEntity().getCustomName().equals(beast.getName()))continue;
            if (beast.getBeast().getUniqueId().equals(event.getEntity().getUniqueId())) {
                event.setDroppedExp(0);
                event.getDrops().clear();
                if (event.getEntity().getKiller() == null) {
                    final BeastDeathEvent beastDeathEvent = new BeastDeathEvent(beast, event.getDrops(), null);
                    Bukkit.getPluginManager().callEvent(beastDeathEvent);
                    event.getDrops().addAll(beastDeathEvent.getDrops());
                } else {
                    Player killer = event.getEntity().getKiller();
                    final BeastDeathEvent beastDeathEvent = new BeastDeathEvent(beast, event.getDrops(), killer);
                    Bukkit.getPluginManager().callEvent(beastDeathEvent);
                    event.getDrops().addAll(beastDeathEvent.getDrops());
                }
            }
        }
    }
    @EventHandler
    private void EntityDamageEvent(EntityDamageEvent event) {
        for (@NonNull final Beast beast : this.inGameBeast) {
            if (beast.getBeast() == null)continue;
            if (event.getEntity().getUniqueId().equals(beast.getBeast().getUniqueId())) {
                final BeastDamageEvent beastDamageEvent = new BeastDamageEvent(beast, event.getDamage(), event.getFinalDamage(), event.getCause());
                Bukkit.getPluginManager().callEvent(beastDamageEvent);
                event.setDamage(beastDamageEvent.getDamage());
                event.setCancelled(beastDamageEvent.isCancelled());
                break;
            }
        }
    }
    @EventHandler
    private void BeastDamageEvent(BeastDamageEvent event) {
        if (!(event.getBeast() instanceof IBeastCreator)) return;
        if (((IBeastCreator) event.getBeast()).getImmunisedDamageCause().isEmpty())return;
        if (((IBeastCreator) event.getBeast()).getImmunisedDamageCause().contains(event.getDamageCause())) {
            event.setDamage(0.0);
            event.setCancelled(true);
        }
    }
    @EventHandler
    private void BeastDeathEvent(BeastDeathEvent event) {
        Bukkit.broadcastMessage(event.getBeast().getName()+"§f est§c mort§f.");
        if (event.getBeast() instanceof IBeastCreator) {
            final ItemStack itemStack = getRandomItemFromList(((IBeastCreator) event.getBeast()).getLoots());
            if (itemStack != null){
                event.getDrops().add(itemStack);
            }
            if (event.getKiller() != null) {
                if (GameState.getInstance().hasRoleNull(event.getKiller().getUniqueId()))return;
                final RoleBase role = GameState.getInstance().getGamePlayer().get(event.getKiller().getUniqueId()).getRole();
                if (!(role instanceof KrystalBase))return;
                final int random = Math.max(1, Main.RANDOM.nextInt(((IBeastCreator) event.getBeast()).getMaxKrystalDrop()+1));
                event.getKiller().sendMessage("§bEn tuant la bête:§f "+event.getBeast().getName()+"§b vous avez gagnez §c"+random+" "+(random == 1 ? "krystal" : "krystaux"));
                ((KrystalBase) role).setKrystalAmount(((KrystalBase) role).getKrystalAmount()+random);
            }
        }
    }
    private ItemStack getRandomItemFromList(final List<ItemStack> list) {
        if (list.isEmpty()) {
            return null;
        }
        Collections.shuffle(list, Main.RANDOM);
        final ItemStack item = list.get(0);
        if (item.getAmount() > 1){
            item.setAmount(item.getAmount()/2);
        }
        return item;
    }
    private static class BeastSpawnerRunnable extends BukkitRunnable {

        private final Beast beast;
        private final GameState gameState;
        private int timeLeft;

        private BeastSpawnerRunnable(Beast beast, GameState gameState, int originTimeBeforeSpawn) {
            this.beast = beast;
            this.gameState = gameState;
            this.timeLeft = originTimeBeforeSpawn;
            runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
            if (Main.isDebug()){
                System.out.println("[BeastSpawnerRunnable] has started, TaskID: "+getTaskId()+", Beast: "+beast+", originTimeBeforeSpawn: "+originTimeBeforeSpawn);
            }
        }

        @Override
        public void run() {
            if (!this.gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (this.timeLeft == 30) {
                Bukkit.broadcastMessage("§7[§dKrystalBeast UHC§7]§f Une bête pleine d'énergie se fait ressentir, elle devrait arriver dans environ§c 30 secondes");
            }
            if (this.timeLeft <= 0) {
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    if (this.beast.checkCanSpawn()) {
                        Bukkit.broadcastMessage("§7[§dKrystalBeast UHC§7]§f La bête "+this.beast.getName()+"§f est apparu en ce bas monde, elle ce situe à "+this.getDistanceScale(this.beast.getOriginSpawn()));
                        Bukkit.broadcastMessage("");
                        this.beast.setUuid(beast.getBeast().getUniqueId());
                        this.beast.getAntiMooveRunnable().start();
                        this.beast.setHasSpawn(true);
                        cancel();
                    }
                });
                return;
            }
            this.timeLeft--;
        }
        private String getDistanceScale(@NonNull Location loc) {
            double distance = Math.sqrt(loc.getX() * loc.getX() + loc.getZ() * loc.getZ());
            int scale = ((int) distance / 100) * 100; // Arrondit à la centaine inférieure
            return "entre§c " + scale + "§f et §c" + (scale + 100) + " blocs";
        }

    }
}