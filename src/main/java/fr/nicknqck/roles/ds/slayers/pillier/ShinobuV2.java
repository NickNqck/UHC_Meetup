package fr.nicknqck.roles.ds.slayers.pillier;

import fr.nicknqck.GameState;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.utils.GlobalUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class ShinobuV2 extends PilierRoles {

    private TextComponent desc;
    private PapillonsCommand papillonsCommand;

    public ShinobuV2(UUID player) {
        super(player);
    }

    @Override
    public Soufle getSoufle() {
    return Soufle.EAU;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Shinobu";
    }

    @Override
    public GameState.@NonNull Roles getRoles() {
        return GameState.Roles.Shinobu;
    }

    @Override
    public void resetCooldown() {
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public TextComponent getComponent() {
        return this.desc;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 0, false, false), EffectWhen.PERMANENT);
        this.papillonsCommand = new PapillonsCommand(this);
        addPower(papillonsCommand);
        HealPower healPower = new HealPower(this);
        addPower(healPower, true);
        this.desc = new AutomaticDesc(this).addEffects(getEffects()).setPowers(getPowers()).getText();
    }
    private static class PapillonsCommand extends CommandPower implements Listener {

        private final Map<UUID, ItemStack> heads;
        private final List<UUID> affected;

        public PapillonsCommand(@NonNull RoleBase role) {
            super("§a/ds papillons", "papillons", null, role, CommandType.DS,
                    "§7Vous permet de choisir qu'elles§c joueurs§7 seront affecter par votre §aSoins");
            this.heads = new HashMap<>();
            initMap();
            this.affected = new ArrayList<>();
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> stringObjectMap) {
            if (getRole().getGamePlayer().isAlive()) {
                openMenu(player);
                return true;
            }
            return false;
        }
        private void initMap() {
            for (final GamePlayer gamePlayer : getRole().getGameState().getGamePlayer().values()) {
                if (gamePlayer.getUuid().equals(getRole().getPlayer()))continue;
                if (gamePlayer.isAlive() && gamePlayer.getRole() != null) {
                    Player player = Bukkit.getPlayer(gamePlayer.getUuid());
                    if (player != null) {
                        ItemStack head = new ItemBuilder(GlobalUtils.getPlayerHead(player.getUniqueId())).setSkullOwner(player.getName()).setName("§c"+player.getName()).toItemStack();
                        if (head != null) {
                            heads.put(gamePlayer.getUuid(), head);
                        }
                    }
                }
            }
        }
        private void openMenu(final Player player) {
            Inventory inv = Bukkit.createInventory(player, 54, "§a/ds papillons");
            for (final ItemStack item : heads.values()) {
                inv.addItem(item);
            }
            player.openInventory(inv);
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
                        heads.remove(player.getUniqueId(), item);
                        if (this.affected.contains(player.getUniqueId())) {
                            this.affected.remove(player.getUniqueId());
                        } else {
                            this.affected.add(player.getUniqueId());
                        }
                        ItemMeta itemMeta = item.getItemMeta();
                        itemMeta.setDisplayName((affected.contains(player.getUniqueId()) ? "§a" : "§c")+player.getName());
                        item.setItemMeta(itemMeta);
                        heads.put(player.getUniqueId(), item);
                        openMenu((Player) event.getWhoClicked());
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
                    "§c! Pour pouvoir réutiliser ce pouvoir il faudrat que vous et/ou le dernier joueur soigner§c mangiez un total de§e 25 pommes d'or");
            setMaxUse(1);
            this.shinobuV2 = role;
            this.lastPlayerlow = role.getPlayer();
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
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
                        Player shinobu = Bukkit.getPlayer(getRole().getPlayer());
                        if (shinobu != null) {
                            shinobu.sendMessage("§7Vous pouvez à nouveau§d soigner§7 l'un de vos allier");
                        }
                        setMaxUse(getMaxUse()+1);
                        gapToEat = -1;
                    }
                }
            }
        }
    }
}