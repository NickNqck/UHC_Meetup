package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.UchiwaRoles;
import fr.nicknqck.roles.ns.solo.Danzo;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.packets.NMSPacket;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Fugaku extends UchiwaRoles implements Listener {
    private TextComponent desc;
    private final ItemStack oeilItem = new ItemBuilder(Material.EYE_OF_ENDER).addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().setUnbreakable(true).setName("§cOeil Maléfique").setDroppable(false).toItemStack();
    private int cdAffaiblissement;
    private int cdAttaque;

    public Fugaku(UUID player) {
        super(player);
        givePotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 2, 0), EffectWhen.PERMANENT);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        setChakraType(Chakras.KATON);
        AutomaticDesc desc = new AutomaticDesc(this);
        desc.addEffects(getEffects())
                .addParticularites(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous possédez la nature de chakra "+Chakras.KATON.getShowedName())})
                , new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Toutes les§c 5 minutes§7, vous saurez si vous avez croisé un porteur de §cSharingan§7 à moins de §c10 blocs§7.")})
                );
        this.desc = desc.getText();
        new CroiserRunnable(this).runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
        EventUtils.registerEvents(this);
    }

    @Override
    public Intelligence getIntelligence() {
        return Intelligence.INTELLIGENT;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[] {
                oeilItem
        };
    }

    @Override
    public String getName() {
        return "Fugaku";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Fugaku;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Shinobi;
    }

    @Override
    public void resetCooldown() {
        cdAffaiblissement = 0;
        cdAttaque = 0;
    }

    @Override
    public @NonNull TextComponent getComponent() {
        return this.desc;
    }
    @Override
    public void GiveItems() {
        giveItem(owner, false, getItems());
    }

    @Override
    public void Update(GameState gameState) {
        if (this.cdAffaiblissement >= 0) {
            cdAffaiblissement--;
            if (this.cdAffaiblissement == 0) {
                owner.sendMessage("§7Vous pouvez à nouveau utiliser la technique §8Affaiblissement§7 de votre§c Oeil Maléfique");
            }
        }
        if (this.cdAttaque >= 0) {
            cdAttaque--;
            if (this.cdAttaque == 0) {
                owner.sendMessage("§7Vous pouvez à nouveau utiliser la technique d'§cAttaque§7 de votre§c Oeil Maléfique");
            }
        }
    }

    @EventHandler
    private void onEndGame(EndGameEvent event) {
        EventUtils.unregisterEvents(this);
    }
    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (event.hasItem()) {
            if (event.getItem().isSimilar(this.oeilItem)) {
                if (event.getPlayer().getUniqueId().equals(getPlayer())) {
                    openFirstInventory(event.getPlayer());
                    event.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() != null) {
            if (event.getWhoClicked().getUniqueId().equals(getPlayer())) {
                if (event.getInventory().getTitle() != null) {
                    ItemStack item = event.getCurrentItem();
                    switch (event.getInventory().getTitle()) {
                        case "§cOeil Maléfique":
                            if (event.getSlot() == 2) {
                                if (cdAffaiblissement <= 0) {
                                    openChoosePlayerInventory((Player) event.getWhoClicked(), "Affaiblissement");
                                }
                            }
                            if (event.getSlot() == 4) {
                                if (cdAttaque <= 0) {
                                    openChoosePlayerInventory((Player) event.getWhoClicked(), "§cAttaque");
                                }
                            }
                            if (event.getSlot() == 6) {
                                openChoosePlayerInventory((Player) event.getWhoClicked(), "§6§lPlace au combat !");
                            }
                            event.setCancelled(true);
                            break;
                        case "Affaiblissement":
                            if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                                event.getWhoClicked().closeInventory();
                                openFirstInventory((Player) event.getWhoClicked());
                                event.setCancelled(true);
                                return;
                            }
                            if (item.hasItemMeta()) {
                                if (item.getItemMeta().hasDisplayName()) {
                                    Player clicked = Bukkit.getPlayer(item.getItemMeta().getDisplayName());
                                    if (clicked != null) {
                                        clicked.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*18, 0, false, false), true);
                                        clicked.sendMessage("§aFugaku§7 vous fait sentir impuissant");
                                        event.getWhoClicked().sendMessage("§7Vous avez donner à §c"+clicked.getName()+"§7 l'effet§8 Weakness I§7 pendant§c 18 secondes");
                                        event.getWhoClicked().closeInventory();
                                        this.cdAffaiblissement = 120;
                                    }
                                }
                            }
                            event.setCancelled(true);
                            break;
                        case "§cAttaque":
                            if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                                event.getWhoClicked().closeInventory();
                                openFirstInventory((Player) event.getWhoClicked());
                                event.setCancelled(true);
                                return;
                            }
                            if (item.hasItemMeta()) {
                                if (item.getItemMeta().hasDisplayName()) {
                                    Player clicked = Bukkit.getPlayer(item.getItemMeta().getDisplayName());
                                    if (clicked != null) {
                                        Location loc = Loc.getRandomLocationAroundPlayer(clicked, 10);
                                        event.getWhoClicked().teleport(loc);
                                        clicked.sendMessage("§7Vous sentez quelque chose de nouveau autours de vous.");
                                        event.getWhoClicked().sendMessage("§7Vous vous êtes téléportez autours de§c "+clicked.getName());
                                        event.getWhoClicked().closeInventory();
                                        cdAttaque = 60*5;
                                    }
                                }
                            }
                            event.setCancelled(true);
                            break;
                        case "§6§lPlace au combat !":
                            if (item.isSimilar(GUIItems.getSelectBackMenu())) {
                                event.getWhoClicked().closeInventory();
                                openFirstInventory((Player) event.getWhoClicked());
                                event.setCancelled(true);
                                return;
                            }
                            if (item.hasItemMeta()) {
                                if (item.getItemMeta().hasDisplayName()) {
                                    Player clicked = Bukkit.getPlayer(item.getItemMeta().getDisplayName());
                                    if (clicked != null) {
                                        new CombatManager(this, clicked);
                                        event.getWhoClicked().closeInventory();
                                    }
                                }
                            }
                            event.setCancelled(true);
                            break;
                    }
                }
            }
        }
    }
    private void openFirstInventory(Player player) {
        Inventory inv = Bukkit.createInventory(player, 9, "§cOeil Maléfique");
        inv.setItem(2, new ItemBuilder(Material.FERMENTED_SPIDER_EYE).setName("§r§fAffaiblissement").setLore("§7Inflige l'effet§8 Weakness I§7 pendant §c18 secondes§7 a un joueur visée","",(cdAffaiblissement <= 0 ? "§cPouvoir Utilisable" : "§7Cooldown: §c"+ StringUtils.secondsTowardsBeautiful(cdAffaiblissement))).toItemStack());
        inv.setItem(4, new ItemBuilder(Material.IRON_SWORD).hideAllAttributes().setName("§cAttaque").setLore("§7Vous permet de vous téléporter dans un rayon de§c 10 blocs§7 autours d'un joueur visée","",(cdAttaque <= 0 ? "§cPouvoir Utilisable":"§7Cooldown:§c "+StringUtils.secondsTowardsBeautiful(cdAttaque))).toItemStack());
        inv.setItem(6, new ItemBuilder(Material.BOW).setName("§6§lPlace au combat !").setLore("§7Pendant§c une minute§7, vous permet de vous rendre insensible à tout les projectiles, également, vous infligerez§c 25%§7 de dégat supplémentaire au joueur viser§7 et subirez§c 25%§7 de dégat en moins de sa part").toItemStack());
        player.openInventory(inv);
    }
    private void openChoosePlayerInventory(Player player, String invName) {
        Inventory inv = Bukkit.createInventory(player, 54, invName);
        for (int i = 0; i <= 8; i++) {
            if (i == 4) {
                inv.setItem(i, GUIItems.getSelectBackMenu());
            } else {
                inv.setItem(i, GUIItems.getOrangeStainedGlassPane());
            }
        }
        for (Player p : Loc.getNearbyPlayers(owner, 30)) {
            if (!p.hasPotionEffect(PotionEffectType.INVISIBILITY) && p.getGameMode() != GameMode.SPECTATOR) {
                inv.addItem(new ItemBuilder(Material.SKULL_ITEM).setDurability((3)).setName(p.getDisplayName()).toItemStack());
            }
        }
        player.openInventory(inv);
    }


    private static class CombatManager implements Listener {

        private final Fugaku fugaku;
        private final UUID uTarget;

        public CombatManager(Fugaku fugaku, Player target) {
            this.fugaku = fugaku;
            this.uTarget = target.getUniqueId();
            EventUtils.registerEvents(this);
            new CombatRunnable(this).runTaskTimer(Main.getInstance(), 0, 20);
        }
        @EventHandler
        private void onDamage(UHCPlayerBattleEvent event) {
            if (event.isPatch()) {
                if (event.getDamager().getUuid().equals(fugaku.getPlayer())) {
                    if (event.getVictim().getUuid().equals(uTarget)) {
                        event.setDamage(event.getDamage()*1.25);
                    }
                } else if (event.getDamager().getUuid().equals(uTarget)) {
                    if (event.getVictim().getUuid().equals(fugaku.getPlayer())) {
                        event.setDamage(event.getDamage()*0.75);
                    }
                }
            }
        }
        @EventHandler
        private void onBowDamage(EntityDamageEvent event) {
            if (event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
                if (event.getEntity().getUniqueId().equals(fugaku.getPlayer())) {
                    event.setCancelled(true);
                }
            }
        }
        private static class CombatRunnable extends BukkitRunnable {
            private final CombatManager manager;
            private int time = 60;
            public CombatRunnable(CombatManager combatManager) {
                this.manager = combatManager;
            }

            @Override
            public void run() {
                if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancelAll();
                    return;
                }
                time--;
                if (time == 0) {
                    cancelAll();
                    return;
                }
                Player fugaku = Bukkit.getPlayer(manager.fugaku.getPlayer());
                if (fugaku != null) {
                    NMSPacket.sendActionBar(fugaku, "§bTemp avant fin du§c combat§b: §c"+StringUtils.secondsTowardsBeautiful(time));
                }
            }
            private void cancelAll() {
                EventUtils.unregisterEvents(manager);
                cancel();
            }
        }
    }
    private static class CroiserRunnable extends BukkitRunnable {
        private final Fugaku fugaku;
        private int timeRemaining = 60*5;
        private boolean croised = false;

        public CroiserRunnable(Fugaku fugaku) {
            this.fugaku = fugaku;
        }

        @Override
        public void run() {
            if (!fugaku.getGameState().getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            Player owner = Bukkit.getPlayer(fugaku.getPlayer());
            if (owner != null) {
                timeRemaining--;
                if (!croised) {//donc je fais le code juste en dessous que s'il n'a croiser personne
                    for (Player p : Loc.getNearbyPlayersExcept(owner, 10)) {
                        if (fugaku.getGameState().hasRoleNull(p))continue;
                        RoleBase role = fugaku.getGameState().getPlayerRoles().get(p);
                        if (!role.getGamePlayer().isAlive())continue;
                        if (role instanceof UchiwaRoles || role instanceof Danzo) {
                            this.croised = true;
                        }
                    }
                }
                if (timeRemaining == 0) {
                    if (croised) {
                        owner.sendMessage("§7Vous avez croiser un porteur du§c Sharingan§7 durant ces§c 5 dernières minutes§7.");
                    } else {
                        owner.sendMessage("§7Vous n'avez croiser aucun porteur du §cSharingan§7 ces dernières§c 5 minutes§7.");
                    }
                    timeRemaining = 60*5;
                }
            }
        }
    }
}