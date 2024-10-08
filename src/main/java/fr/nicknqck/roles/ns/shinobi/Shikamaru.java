package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.roles.ns.builders.NSRoles;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.Loc;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

import static fr.nicknqck.player.StunManager.stun;

public class Shikamaru extends ShinobiRoles {
    private final ItemStack stunItem = new ItemBuilder(Material.NETHER_STAR).setName("§aStun").setLore("§7Vous permet d'empêcher de bouger un joueur").toItemStack();
    private int cdStun = 0;
    private int powerDistance = 25;
    private int poisonUse = 0;
    private final ItemStack zoneItem = new ItemBuilder(Material.NETHER_STAR).setName("§aZone d'ombre").setLore("§7Vous permet d'empêcher tout les joueurs autours de vous de bouger").toItemStack();
    private int cdZone = 0;
    private int cdShogi = 0;
    public Shikamaru(UUID player) {
        super(player);
        setChakraType(getRandomChakrasBetween(Chakras.DOTON, Chakras.KATON));
        new StunExecutable(this);
    }
    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.Shikamaru;
    }
    @Override
    public Intelligence getIntelligence() {
        return Intelligence.GENIE;
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
        if (cdStun >= 0){
            cdStun--;
            if (cdStun == 0){
                owner.sendMessage("§7Vous pouvez à nouveau empêcher un joueur de bouger");
            }
        }
        if (cdZone >= 0){
            cdZone--;
            if (cdZone == 0){
                owner.sendMessage("§7Vous pouvez à nouveau utiliser votre§a Zone d'ombre");
            }
        }
    }
    @Override
    public String[] Desc() {
        KnowRole(owner, GameState.Roles.Ino, 15);
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
                AllDesc.point+"§aZone d'ombre§f: Immobilise tout les joueurs étant à moins de§c "+(powerDistance-10)+" blocs§f de vous pendant§c 5 secondes§f, les joueurs touchés prendront§c 20%§f de dégat en moins et seront frappable",
                "",
                AllDesc.commande,
                "",
                AllDesc.point+"§6/ns shogi <joueur>§f: Vous permet d'obtenir le niveau d'intelligence du joueur viser, si le joueur est§a Ino§f ou§a Choji§f vous obtiendrez directement son rôle.§7 (1x/5min)",
                "Annexe (§ltemporaire§r) du système d'intelligence:§b  https://discord.com/channels/925124675372208189/1239619280664788993",
                "",
                AllDesc.particularite,
                "",
                "Votre nature de chakra est aléatoire",
                "Vous connaissez le§c joueur§f possédant le rôle de§a Ino",
                "",
                AllDesc.chakra+getChakras().getShowedName(),
                "",
                AllDesc.bar
        };
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
                stunItem,
                this.zoneItem
        };
    }

    @Override
    public void resetCooldown() {
        cdStun = 0;
        cdZone = 0;
        poisonUse = 0;
    }

    @Override
    public void onNsCommand(String[] args) {
        super.onNsCommand(args);
        if  (args[0].equalsIgnoreCase("shogi")){
            if (cdShogi > 0){
                sendCooldown(owner, cdShogi);
                return;
            }
            if (args.length == 2) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    if (!gameState.hasRoleNull(target)) {
                        if (gameState.getPlayerRoles().get(target) instanceof NSRoles){
                            Intelligence intelligence = ((NSRoles) gameState.getPlayerRoles().get(target)).getIntelligence();
                            if (intelligence.equals(Intelligence.CONNUE)) {
                                owner.sendMessage("§7Le rôle de§c "+target.getDisplayName()+"§7 est "+gameState.getPlayerRoles().get(target).getName());
                            } else {
                                owner.sendMessage("§c"+target.getDisplayName()+"§7 est§a "+intelligence.getName());
                            }
                            cdShogi = 60*5;
                        } else {
                            owner.sendMessage("§c"+target.getDisplayName()+"§7 ne viens pas du§a Naruto§2 UHC");
                        }
                    }
                } else {
                    owner.sendMessage("§b"+args[1]+"§c n'est pas connecter");
                }
            }
        }
    }

    @Override
    public boolean ItemUse(ItemStack item, GameState gameState) {
        if (item.isSimilar(stunItem)){
            if (cdStun > 0){
                sendCooldown(owner, cdStun);
                return true;
            }
            openStunMenu();
        } else if (item.isSimilar(zoneItem)){
            if (cdZone > 0){
                sendCooldown(owner, cdZone);
                return true;
            }
            cdZone = 60*5;
            for (Player p : Loc.getNearbyPlayersExcept(owner, powerDistance-10)){
               stun(p.getUniqueId(), 5.0, true);
            }
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
        for (Player p : Loc.getNearbyPlayersExcept(owner, this.powerDistance)){
                inv.setItem(i, new ItemBuilder(Material.SKULL_ITEM).setDurability(3).setSkullOwner(p.getName()).setLore("","§aClique droit§7 pour infliger des§c dégats§7 à la §ccible§7 pendant le §astun§c "+poisonUse+"§7/2").setName(p.getName()).toItemStack());
                i++;
                if (Main.isDebug()) {
                    System.out.println(i + " int");
                    System.out.println(inv.getItem(i) != null ? inv.getItem(i).getType().name() : "ez" + i);
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
    public String getName() {
        return "Shikamaru";
    }
    private static class StunExecutable implements Listener{
        private Shikamaru shikamaru;
        private StunExecutable(Shikamaru shikamaru){
            this.shikamaru = shikamaru;
            Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
        }
        @EventHandler
        private void onInventoryClick(InventoryClickEvent e){
            if (shikamaru == null)return;
            if (e.getWhoClicked().getUniqueId().equals(shikamaru.getPlayer())){
                if (e.getClickedInventory() == null)return;
                if (e.getClickedInventory().getTitle() == null)return;
                if (Main.isDebug()){
                    System.out.println("PULL UP");
                }
                if (e.getClickedInventory().getTitle().equals("§aShikamaru§7 ->§a Stun")){
                    e.setCancelled(true);
                    ItemStack item = e.getCurrentItem();
                    if (item != null) {
                        if (item.getType().equals(Material.SKULL_ITEM)) {
                            if (!item.hasItemMeta()){
                                if (Main.isDebug()){
                                    System.out.println(item.getType().name()+" has not itemMeta");
                                }
                                return;
                            }
                            if (!item.getItemMeta().hasDisplayName()){
                                if (Main.isDebug()){
                                    System.out.println(item.getType().name()+item.getItemMeta()+" has no display name");
                                }
                                return;
                            }
                            if (Main.isDebug()){
                                System.out.println(item.getItemMeta().getDisplayName());
                            }
                            final Player player = Bukkit.getPlayer(item.getItemMeta().getDisplayName());
                            if (player != null){
                                e.getWhoClicked().sendMessage("§7Vous empêchez§c "+player.getDisplayName()+"§7 de bouger");
                                player.sendMessage("§aShikamaru§7 vous empêche de bouger");
                                stun(player.getUniqueId(), 10.0, false);
                                stun(shikamaru.getPlayer(), 10.0, false);
                                if (e.getAction().equals(InventoryAction.PICKUP_HALF) && shikamaru.poisonUse < 2){
                                    new PoisonPowerRunnable(shikamaru, player.getUniqueId()).runTaskTimerAsynchronously(Main.getInstance(), 0, 40);
                                    shikamaru.poisonUse++;
                                }
                                player.closeInventory();
                                Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
                                    e.getWhoClicked().sendMessage("§c"+player.getDisplayName()+"§7 peut à nouveau bouger");
                                    player.sendMessage("§7Vous pouvez à nouveau bouger");
                                }, 200L);
                            } else {
                                System.out.println("Le joueur viser par Shikamaru est null");
                            }
                        }
                    }
                }
            }
        }
        @EventHandler
        private void onEndGame(EndGameEvent e){
            shikamaru = null;
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
                timeRemaining--;
            }
        }
    }
}