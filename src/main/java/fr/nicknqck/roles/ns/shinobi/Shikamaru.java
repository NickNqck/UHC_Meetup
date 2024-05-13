package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.Loc;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Shikamaru extends RoleBase {
    private final ItemStack stunItem = new ItemBuilder(Material.NETHER_STAR).setName("§aStun").setLore("§7Vous permet d'empêcher de bouger un joueur").toItemStack();
    private int cdStun = 0;
    private int powerDistance = 25;
    private int poisonUse = 0;
    public Shikamaru(Player player, GameState.Roles roles, GameState gameState) {
        super(player, roles, gameState);
        setChakraType(getRandomChakrasBetween(Chakras.DOTON, Chakras.KATON));
        owner.sendMessage(Desc());
    }

    @Override
    public void GiveItems() {
        super.GiveItems();
        giveItem(owner, false, getItems());
    }

    @Override
    public void Update(GameState gameState) {
        super.Update(gameState);
        //Si c'est la nuit
        if (gameState.isNightTime()){
            powerDistance = 35;
        } else {
            powerDistance = 25;
        }
    }
    @Override
    public String[] Desc() {
        return new String[]{
                AllDesc.bar,
                AllDesc.role+"§aShikamaru",
                AllDesc.objectifteam+"§aShinobi",
                "",
                AllDesc.items,
                "",
                AllDesc.point+"§aStun§f: Ouvre un menu affichant tout les joueurs étant à moins de§c "+powerDistance+" blocs§f de vous, en sélectionnant un joueur, vous et le joueur viser ne pourrez plus bouger pendant§c 10 secondes§f, de plus si vous lui cliqué dessus avec votre§a clique droit§f vous lui infligerz§c -1/2"+AllDesc.coeur+"§f toute les§c 2 secondes§f.§7 (1x/5min)",
                "§c(Vous et le joueur viser pourrez prendre des dégats pendant le stun)",
                "",
                AllDesc.point+"ez"
        };
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
                stunItem
        };
    }

    @Override
    public void resetCooldown() {
        cdStun = 0;
    }

    @Override
    public boolean ItemUse(ItemStack item, GameState gameState) {
        if (item.isSimilar(stunItem)){
            if (cdStun > 0){
                sendCooldown(owner, cdStun);
                return true;
            }
            openStunMenu();
        }
        return super.ItemUse(item, gameState);
    }
    private void openStunMenu(){
        final Inventory inv = Bukkit.createInventory(owner, 54, "§aShikamaru§7 ->§a Stun");
        inv.setItem(0, GUIItems.getGreenStainedGlassPane());
        inv.setItem(1, GUIItems.getGreenStainedGlassPane());
        inv.setItem(9, GUIItems.getGreenStainedGlassPane());

        inv.setItem(7, GUIItems.getGreenStainedGlassPane());//haut droite
        inv.setItem(8, GUIItems.getGreenStainedGlassPane());
        inv.setItem(17, GUIItems.getGreenStainedGlassPane());

        inv.setItem(45, GUIItems.getGreenStainedGlassPane());
        inv.setItem(46, GUIItems.getGreenStainedGlassPane());
        inv.setItem(36, GUIItems.getGreenStainedGlassPane());//bas gauche

        inv.setItem(44, GUIItems.getGreenStainedGlassPane());
        inv.setItem(52, GUIItems.getGreenStainedGlassPane());
        inv.setItem(53, GUIItems.getGreenStainedGlassPane());//bas droite

        inv.setItem(2, new ItemBuilder(Material.ANVIL).toItemStack());
        inv.setItem(18, new ItemBuilder(Material.ANVIL).toItemStack());
        inv.setItem(27, new ItemBuilder(Material.ANVIL).toItemStack());
        inv.setItem(26, new ItemBuilder(Material.ANVIL).toItemStack());
        inv.setItem(35, new ItemBuilder(Material.ANVIL).toItemStack());

        int i = 10;
        for (Player p : Loc.getNearbyPlayers(owner, this.powerDistance)){
            if (inv.getItem(i).getType().equals(Material.AIR)){
                inv.setItem(i, new ItemBuilder(Material.SKULL_ITEM).setDurability(3).setSkullOwner(p.getName()).setLore("",poisonUse < 2 ? "§aClique droit§7 pour infliger des§c dégats§7 à la §ccible§7 pendant le §astun§7" : "").toItemStack());
                i++;
            }
        }
        inv.setItem(2, new ItemBuilder(Material.AIR).toItemStack());
        inv.setItem(18, new ItemBuilder(Material.AIR).toItemStack());
        inv.setItem(27, new ItemBuilder(Material.AIR).toItemStack());
        inv.setItem(26, new ItemBuilder(Material.AIR).toItemStack());
        inv.setItem(35, new ItemBuilder(Material.AIR).toItemStack());
        owner.openInventory(inv);
        owner.updateInventory();
    }

    @Override
    public void onAllPlayerInventoryClick(InventoryClickEvent event, ItemStack item, Inventory inv, Player clicker) {
        super.onAllPlayerInventoryClick(event, item, inv, clicker);
        if (clicker.getUniqueId().equals(getUuidOwner())){
            if (inv.getTitle() != null && inv.getTitle().equalsIgnoreCase("§aShikamaru§7 ->§a Stun")){
                if (item.getType().equals(Material.SKULL_ITEM)) {
                    if (item.hasItemMeta())return;
                    if (item.getItemMeta().hasDisplayName())return;
                    final Player player = Bukkit.getPlayer(item.getItemMeta().getDisplayName());
                    if (player != null){
                        owner.sendMessage("§7Vous empêchez§c "+player.getDisplayName()+"§7 de bouger");
                        player.sendMessage("§aShikamaru§7 vous empêche de bouger");
                        GamePlayer.get(player).stun(10.0);
                        getGamePlayer().stun(10.0);
                        if (event.getAction().equals(InventoryAction.PICKUP_HALF) && poisonUse < 2){
                            new PoisonPowerRunnable(this, player.getUniqueId()).runTaskTimerAsynchronously(Main.getInstance(), 0, 40);
                            poisonUse++;
                        }
                        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
                            owner.sendMessage("§c"+player.getDisplayName()+"§7 peut à nouveau bouger");
                            player.sendMessage("§7Vous pouvez à nouveau bouger");
                        }, 200L);
                    }
                }
            }
        }
    }
    private static class PoisonPowerRunnable extends BukkitRunnable {
        private final Shikamaru shikamaru;
        private int timeRemaining = 5;
        private final UUID uuidTarget;
        private PoisonPowerRunnable(Shikamaru shikamaru, UUID uuidTarget){
            this.shikamaru = shikamaru;
            this.uuidTarget = uuidTarget;
        }
        @Override
        public void run() {
            if (timeRemaining == 0 || !shikamaru.getGameState().getServerState().equals(GameState.ServerStates.InGame)){
                cancel();
                return;
            }
            Player target = Bukkit.getPlayer(uuidTarget);
            if (target != null){
                shikamaru.damage(target, 1.0, 1, shikamaru.owner, true);
            }
            timeRemaining--;
        }
    }
}