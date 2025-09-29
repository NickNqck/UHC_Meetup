package fr.nicknqck.roles.ds.slayers.pillier;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.utils.GlobalUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.fastinv.FastInv;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class ShinobuV2 extends PilierRoles {

    private PapillonsCommand papillonsCommand;

    public ShinobuV2(UUID player) {
        super(player);
    }

    @Override
    public Soufle getSoufle() {
    return Soufle.EAU;
    }

    @Override
    public String getName() {
        return "Shinobu";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Shinobu;
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 0, false, false), EffectWhen.DAY);
        this.papillonsCommand = new PapillonsCommand(this);
        addPower(new HealPower(this), true);
        addPower(new PoisonsPower(this), true);
        addPower(papillonsCommand);
    }
    private static class PapillonsCommand extends CommandPower implements Listener {

        private final List<UUID> affected;

        public PapillonsCommand(@NonNull RoleBase role) {
            super("§a/ds papillons", "papillons", null, role, CommandType.DS,
                    "§7Vous permet de choisir qu'elles§c joueurs§7 seront affecter par votre §aSoins");
            this.affected = new ArrayList<>();
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> stringObjectMap) {
            if (getRole().getGamePlayer().isAlive()) {
                openMenu(player);
                return true;
            }
            return false;
        }
        private void openMenu(final Player p) {
            Inventory inv = Bukkit.createInventory(p, 54, "§a/ds papillons");
            for (final GamePlayer gamePlayer : getRole().getGameState().getGamePlayer().values()) {
                if (gamePlayer.getUuid().equals(getRole().getPlayer()))continue;
                if (gamePlayer.isAlive() && gamePlayer.getRole() != null) {
                    Player player = Bukkit.getPlayer(gamePlayer.getUuid());
                    if (player != null) {
                        ItemStack head = new ItemBuilder(GlobalUtils.getPlayerHead(player.getUniqueId())).setSkullOwner(player.getName()).setName("§c"+player.getName()).toItemStack();
                        if (head != null) {
                            inv.addItem(head);
                        }
                    }
                }
            }
            p.openInventory(inv);
        }
        @EventHandler
        private void onInventoryClick(InventoryClickEvent event) {
            if (event.getClickedInventory() == null)return;
            if (event.getClickedInventory().getTitle().equalsIgnoreCase("§a/ds papillons") && event.getWhoClicked() instanceof Player) {
                event.setCancelled(true);
                ItemStack item = event.getCurrentItem();
                if (item == null)return;
                if (!event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                    event.getWhoClicked().closeInventory();
                    return;
                }
                if (item.getType().equals(Material.SKULL_ITEM)) {
                    Player player = Bukkit.getPlayer(item.getItemMeta().getDisplayName().substring(2));
                    if (player != null) {
                        if (this.affected.contains(player.getUniqueId())) {
                            this.affected.remove(player.getUniqueId());
                        } else {
                            this.affected.add(player.getUniqueId());
                        }
                        ItemMeta itemMeta = item.getItemMeta();
                        itemMeta.setDisplayName((affected.contains(player.getUniqueId()) ? "§a" : "§c")+player.getName());
                        item.setItemMeta(itemMeta);
                        player.closeInventory();
                    }
                } else {
                    event.getWhoClicked().closeInventory();
                }
            }
        }
    }
    private static class HealPower extends ItemPower implements Listener{

        private final ShinobuV2 shinobuV2;
        private UUID lastPlayerlow;
        private int gapToEat = -1;

        protected HealPower(@NonNull ShinobuV2 role) {
            super("§aSoins", null, new ItemBuilder(Material.NETHER_STAR).setName("§aSoins"), role,
                    "§7Lorsqu'un de vos protéger choisis via le§6 /ds papillons§7 est en dessous de§c 4❤§7 vous recevrez une notification pour le§d soigner§c complètement§7.",
                    "",
                    "§c! Pour pouvoir réutiliser ce pouvoir il faudra que vous et/ou le dernier joueur soigner§c mangiez un total de§e 25 pommes d'or");
            setMaxUse(1);
            this.shinobuV2 = role;
            this.lastPlayerlow = role.getPlayer();
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> args) {
            Player toHeal = Bukkit.getPlayer(lastPlayerlow);
            if (toHeal != null && toHeal.getUniqueId() != getRole().getPlayer()) {
                toHeal.setHealth(toHeal.getMaxHealth());
                toHeal.sendMessage("§aShinobu§7 à décider de vous§d soigner§7.");
                gapToEat = 25;
                return true;
            } else {
                player.sendMessage("§7La personne que vous essayez de§d soignez§7 n'est pas connecter");
            }
            return false;
        }
        @EventHandler
        private void onDamage(EntityDamageEvent event) {
            if (shinobuV2.papillonsCommand.affected.contains(event.getEntity().getUniqueId())) {
                if (event.getEntity() instanceof Player) {
                    if (((Player) event.getEntity()).getHealth() <= 8.0) {
                        this.lastPlayerlow = event.getEntity().getUniqueId();
                        Player shinobu = Bukkit.getPlayer(shinobuV2.getPlayer());
                        if (shinobu != null) {
                            shinobu.sendMessage("§7L'un de vos protéger (§c"+event.getEntity().getName()+"§7) est en dessous de§c 4❤§7, utiliser votre item "+getName()+"§7 pour le§d soigner§7.");
                        }
                    }
                }
            }
        }
        @EventHandler
        private void onEat(PlayerItemConsumeEvent event) {
            if (event.getPlayer().getUniqueId().equals(lastPlayerlow) || event.getPlayer().getUniqueId().equals(getRole().getPlayer())) {
                if (event.getItem().getType().equals(Material.GOLDEN_APPLE) && gapToEat >= 0) {
                    gapToEat--;
                    if (gapToEat == 0) {
                        getRole().getGamePlayer().sendMessage("§7Vous pouvez à nouveau§d soigner§7 l'un de vos allier");
                        setMaxUse(getMaxUse()+1);
                        gapToEat = -1;
                    }
                }
            }
        }
    }
    private static class PoisonsPower extends ItemPower implements Listener{

        private final Map<Integer, PotionEffect> mapEffect = new HashMap<>();
        private int actual = 1;
        private boolean kanaeDead = false;

        public PoisonsPower(@NonNull RoleBase role) {
            super("Poisons", new Cooldown(30), new ItemBuilder(Material.NETHER_STAR).setName("§2Poisons"), role,
                    "§7Cette objet vous permet de changer le§c Poison§7 que vous§c utiliser§7",
                    "§7voici ceux qui sont utilisable:§c Poison§7,§c Nausée§7,§c Wither§7,§c Slowness§7,§c Blindness§7,§c Weakness§7.",
                    "",
                    "§7Chacun des effets est appliquer en frappant un joueur avec une épée, également les effets ont une durée de§c 10 secondes§7.",
                    "",
                    "§7Si§a Kanae§7 viens à mourir, lorsqu'un effet de§c Poison§7 doit être appliqué vous appliquerez le suivant (dans l'ordre de la description)");
            role.getGamePlayer().getActionBarManager().addToActionBar("shinobu.poisons", "§bVous utilisez actuellement le poison:§c "+getActualString());
            mapEffect.put(1, new PotionEffect(PotionEffectType.POISON, 20*10, 0, false, false));
            mapEffect.put(2, new PotionEffect(PotionEffectType.CONFUSION, 20*10, 0, false, false));
            mapEffect.put(3, new PotionEffect(PotionEffectType.WITHER, 20*10, 0, false, false));
            mapEffect.put(4, new PotionEffect(PotionEffectType.SLOW, 20*10, 0, false, false));
            mapEffect.put(5, new PotionEffect(PotionEffectType.BLINDNESS, 20*10, 0, false, false));
            mapEffect.put(6, new PotionEffect(PotionEffectType.WEAKNESS, 20*10, 0, false, false));
            EventUtils.registerRoleEvent(this);
            Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> {
                boolean bool = false;
                for (final GamePlayer gamePlayer : getRole().getGameState().getGamePlayer().values()) {
                    if (gamePlayer.getRole() == null)continue;
                    if (gamePlayer.getRole() instanceof KanaeV2) {
                        bool = true;
                        break;
                    }
                }
                if (!bool) {
                    this.kanaeDead = true;
                    getRole().getGamePlayer().sendMessage("§aKanae§7 n'étant pas dans la partie vous avez directement reçus les§c bonus§7 du à sa§c mort§7.");
                }
            }, 20*10);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final PlayerInteractEvent playerInteractEvent = (PlayerInteractEvent) map.get("event");
                if (playerInteractEvent.getAction().name().contains("LEFT")) {
                    this.actual++;
                    if (actual > 6) actual = 1;
                    player.sendMessage("§7Vous avez modifié les§c proportions§7 de votre§2 Poison§7.");
                    getRole().getGamePlayer().getActionBarManager().updateActionBar("shinobu.poisons", "§bVous utilisez actuellement le poison:§c "+getActualString());
                } else {
                    final FastInv fastInv = new FastInv(27, "§2Poisons");
                    int a = 9;
                    for (int i = 1; i <= 6; i++) {
                        a = a+1;
                        fastInv.setItem(a, new ItemBuilder(Material.PAPER).setName("§a"+i).toItemStack(), event -> {
                            this.actual = Integer.parseInt(event.getCurrentItem().getItemMeta().getDisplayName().substring(2, 3));
                            event.getWhoClicked().sendMessage("§7Vous avez modifié les§c proportions§7 de votre§2 Poison§7.");
                            getRole().getGamePlayer().getActionBarManager().updateActionBar("shinobu.poisons", "§bVous utilisez actuellement le poison:§c "+getActualString());
                            event.getWhoClicked().closeInventory();
                        });
                    }
                    fastInv.open(player);
                }
            }
            return false;
        }
        @EventHandler
        private void onDamage(final EntityDamageByEntityEvent event) {
            if (!(event.getDamager() instanceof Player))return;
            if (!(event.getEntity() instanceof Player))return;
            if (!event.getDamager().getUniqueId().equals(getRole().getPlayer()))return;
            if (((Player) event.getDamager()).getItemInHand() == null)return;
            if (((Player) event.getDamager()).getItemInHand().getType().equals(Material.AIR))return;
            if (!((Player) event.getDamager()).getItemInHand().getType().name().contains("SWORD"))return;
            if (getCooldown().isInCooldown())return;
            final GamePlayer gameTarget = GameState.getInstance().getGamePlayer().get(event.getEntity().getUniqueId());
            if (gameTarget == null)return;
            if (!gameTarget.isOnline() || !gameTarget.isAlive() || gameTarget.getRole() == null)return;
            gameTarget.getRole().givePotionEffect(this.mapEffect.get(actual), EffectWhen.NOW);
            if (kanaeDead) {
                int deux = (this.actual+1);
                if (deux > 6)deux = 1;
                gameTarget.getRole().givePotionEffect(this.mapEffect.get(deux), EffectWhen.NOW);
            }
            getCooldown().use();
            event.getDamager().sendMessage("§c"+((Player) event.getEntity()).getDisplayName()+"§7 a subit votre§2 Poison§7.");
            getRole().getGamePlayer().getActionBarManager().updateActionBar("shinobu.poisons", "§bVous utilisez actuellement le poison:§c "+getActualString());
        }
        @EventHandler
        private void onDeath(final UHCDeathEvent event) {
            if (event.getRole() instanceof KanaeV2) {
                this.kanaeDead = true;
                getRole().getGamePlayer().sendMessage("§aKanae§7 est§c morte§7, votre§2 Poison§7 a été§c améliorer§7.");
            }
        }
        private String getActualString() {
            int plus = (actual+1);
            if (plus > 6)plus = 1;
            return "§c"+actual+(kanaeDead ? "§b et§c "+(plus) : "");
        }
    }
}