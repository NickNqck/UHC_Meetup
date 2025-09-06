package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.ns.builders.NSRoles;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
import fr.nicknqck.utils.TripleMap;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.powers.Cooldown;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
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

public class Shikamaru extends ShinobiRoles {

    private final ItemStack stunItem = new ItemBuilder(Material.NETHER_STAR).setName("§aStun").setLore("§7Vous permet d'empêcher de bouger un joueur").toItemStack();
    private int cdStun = 0;
    private int powerDistance = 25;
    private int poisonUse = 0;
    private final ItemStack zoneItem = new ItemBuilder(Material.NETHER_STAR).setName("§aZone d'ombre").setLore("§7Vous permet d'empêcher tout les joueurs autours de vous de bouger").toItemStack();
    private int cdZone = 0;
    private int cdShogi = 0;
    private TextComponent desc;

    public Shikamaru(UUID player) {
        super(player);
        setChakraType(getRandomChakrasBetween(Chakras.DOTON, Chakras.KATON));
    }
    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Shikamaru;
    }
    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.GENIE;
    }

    @Override
    public void GiveItems() {
        super.GiveItems();
        giveItem(owner, false, getItems());
    }

    @Override
    public void RoleGiven(GameState gameState) {
        new StunExecutable(this);
        AutomaticDesc desc = new AutomaticDesc(this).setItems(new TripleMap<>(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(
                "§7Permet d'ouvrir un menu vous permettant de choisir un§c joueur§7 étant à moins de§c 25 blocs§7 (portée augmenter de§c 10 blocs§7 s'il fait§c nuit§7),\n§7Ce joueur ainsi que vous serrez§a stun§7 pendant§c 10 secondes§7.\n\n"
                +"§7Vous et le joueur§a stun§7 pourrez vous faire§c frappez§7 pendant le temp du§a stun§7.\n\n"
                +"§7Si au moment de choisir le joueur vous faite un§c clique droit§7 vous infligerez au joueur viser§c 1/2❤§7 toute les§c 2 secondes§7.")}),
                "§aStun",
                60*5
        ),
        new TripleMap<>(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(
                        "§7Vous permet de§a stun§7 toute les personnes autours de vous dans un rayon de§c 15 blocs§7.\n\n"
                        +"§7Les joueurs touchés par votre§a stun§7 pourront être§c frappez§7."
                )}),
                "§aZone d'ombre",
                60*5
        ));
        desc.setCommands(
          new TripleMap<>(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
                  new TextComponent(
                          "§7Vous permet d'obtenir l'information du niveau d'intélligence de la personne cibler, si la personne cibler est§a Chôji§7 ou§a Ino§7, alors vous le saurez§c immédiatement§7.\n\n"
                          +"§7L'§cannexe§7 des niveaux d'§aintelligence§7 est disponnible avec la commande§6 /ns intelligences§7."
                  )}),
                  "§6/ns shogi <joueur>",
                  60*5)
        );
        this.desc = desc.getText();
        addKnowedRole(InoV2.class);
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
        if (cdShogi >= 0){
            cdShogi--;
            if (cdShogi == 0){
                owner.sendMessage("§7Vous pouvez à nouveau utiliser votre§6 /ns shogi <joueur>");
            }
        }
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
    public TextComponent getComponent() {
        return this.desc;
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
                    if (!gameState.hasRoleNull(target.getUniqueId())) {
                        if (gameState.getGamePlayer().get(target.getUniqueId()).getRole() instanceof NSRoles){
                            Intelligence intelligence = ((NSRoles) gameState.getGamePlayer().get(target.getUniqueId()).getRole()).getIntelligence();
                            if (intelligence.equals(Intelligence.CONNUE)) {
                                owner.sendMessage("§7Le rôle de§c "+target.getDisplayName()+"§7 est "+gameState.getGamePlayer().get(target.getUniqueId()).getRole().getName());
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
            for (Player p : Loc.getNearbyPlayersExcept(owner, 15)){
                if (gameState.getGamePlayer().containsKey(p.getUniqueId())) {
                    gameState.getGamePlayer().get(p.getUniqueId()).stun(5*20);
                }
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
                inv.setItem(i, new ItemBuilder(Material.SKULL_ITEM).setDurability(3).setSkullOwner(p.getName()).setLore("",poisonUse != 2 ? "§aClique droit§7 pour infliger des§c dégats§7 à la §ccible§7 pendant le §astun§c "+poisonUse+"§7/2": "§7Vous ne pouvez plus utiliser le deuxième pouvoirs (§fClique droit§7)").setName(p.getName()).toItemStack());
                i++;
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

    private static class StunExecutable implements Listener {

        private Shikamaru shikamaru;
        private final Cooldown stunCD = new Cooldown(60*5);

        private StunExecutable(Shikamaru shikamaru){
            this.shikamaru = shikamaru;
            EventUtils.registerEvents(this);
        }
        @EventHandler
        private void onInventoryClick(InventoryClickEvent e){
            if (shikamaru == null)return;
            if (e.getWhoClicked().getUniqueId().equals(shikamaru.getPlayer())){
                if (e.getClickedInventory() == null)return;
                if (e.getClickedInventory().getTitle() == null)return;
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
                            if (this.stunCD.isInCooldown()) {
                                e.getWhoClicked().closeInventory();
                                e.getWhoClicked().sendMessage("§bVous êtes en cooldown:§c "+this.stunCD.getCooldownRemaining()+"s");
                                return;
                            }
                            final Player player = Bukkit.getPlayer(item.getItemMeta().getDisplayName());
                            if (player != null){
                                if (!GameState.getInstance().getGamePlayer().containsKey(player.getUniqueId()))return;
                                e.getWhoClicked().sendMessage("§7Vous empêchez§c "+player.getDisplayName()+"§7 de bouger");
                                player.sendMessage("§aShikamaru§7 vous empêche de bouger");
                                shikamaru.getGamePlayer().stun(10*20);
                                GameState.getInstance().getGamePlayer().get(player.getUniqueId()).stun(10*20);
                                if (e.getAction().equals(InventoryAction.PICKUP_HALF) && shikamaru.poisonUse < 2){
                                    new PoisonPowerRunnable(shikamaru, player.getUniqueId()).runTaskTimerAsynchronously(Main.getInstance(), 0, 40);
                                    shikamaru.poisonUse++;
                                }
                                player.closeInventory();
                                this.stunCD.use();
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
            EventUtils.unregisterEvents(this);
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
                    timeRemaining--;
                    if (target.getHealth() - 1.0 <= 0.0) {
                        target.damage(9999.0, shikamaru.owner);
                        return;
                    }
                    target.setHealth(target.getHealth()-1.0);
                }
            }
        }
    }
}