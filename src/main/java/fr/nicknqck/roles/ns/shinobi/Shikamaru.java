package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.Loc;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Shikamaru extends RoleBase {
    private final ItemStack stunItem = new ItemBuilder(Material.NETHER_STAR).setName("§aStun").setLore("§7Vous permet d'empêcher de bouger un joueur").toItemStack();
    private int cdStun = 0;
    private int powerDistance = 25;
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
                AllDesc.point+"§aStun§f: Ouvre un menu affichant tout les joueurs étant à moins de§c 25 blocs§f de vous, en sélectionnant un joueur, vous et le joueur viser ne pourrez plus bouger pendant§c 10 secondes§f.§7 (1x/5min)",
                "§c(Vous et le joueur viser pourrez prendre des dégats pendant le stun)"
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
        Inventory inv = Bukkit.createInventory(owner, 54, "§aShikamaru§7 ->§a Stun");
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
                inv.setItem(i, new ItemBuilder(Material.SKULL_ITEM).setDurability(3).setSkullOwner(p.getName()).toItemStack());
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
                    Player player = Bukkit.getPlayer(item.getItemMeta().getDisplayName());
                    if (player != null){

                    }
                }
            }
        }
    }
}