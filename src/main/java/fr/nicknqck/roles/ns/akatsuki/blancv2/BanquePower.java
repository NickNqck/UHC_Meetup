package fr.nicknqck.roles.ns.akatsuki.blancv2;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.RoleGiveEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class BanquePower implements Listener {

    private final ItemStack banqueItem = new ItemBuilder(Material.NETHER_STAR).setName("§cBanque").setDroppable(false).toItemStack();
    private final List<UUID> zetsus = new ArrayList<>();
    private final Map<PotionEffect, ItemStack> effects = new HashMap<>();
    private final Map<ItemStack, Boolean> giveds = new HashMap<>();
    @EventHandler
    private void onRoleGive(RoleGiveEvent event) {
        if (event.getRole() instanceof ZetsuBlancV2) {
            Player player = Bukkit.getPlayer(event.getGamePlayer().getUuid());
            if (player != null) {
                event.getRole().giveItem(player, false, banqueItem);
            }
            zetsus.add(event.getRole().getPlayer());
        }
    }
    @EventHandler
    private void onUse(PlayerInteractEvent event) {
        if (zetsus.contains(event.getPlayer().getUniqueId())) {
            if (!event.hasItem())return;
            if (!event.getItem().isSimilar(this.banqueItem))return;
            openBanque(event.getPlayer());
            event.setCancelled(true);
        }
    }
    private void openBanque(Player player) {
        Inventory inv = Bukkit.createInventory(player, 54, "Banque");
        inv.setItem(0, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        inv.setItem(1, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        inv.setItem(2, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        inv.setItem(3, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        inv.setItem(4, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        inv.setItem(5, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        inv.setItem(6, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        inv.setItem(7, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        inv.setItem(8, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        inv.setItem(9, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        inv.setItem(17, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        inv.setItem(18, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        inv.setItem(26, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        inv.setItem(27, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        inv.setItem(35, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        inv.setItem(36, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        inv.setItem(44, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        inv.setItem(45, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        inv.setItem(46, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        inv.setItem(47, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        inv.setItem(48, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        inv.setItem(49, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        inv.setItem(50, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        inv.setItem(51, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        inv.setItem(52, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        inv.setItem(53, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        int i = 10;
        for (final PotionEffect potionEffect : this.effects.keySet()) {
            inv.setItem(i, this.effects.get(potionEffect));
            i+=2;
        }
        player.openInventory(inv);
    }
    @EventHandler
    private void onKill(UHCPlayerKillEvent event) {
        if (zetsus.contains(event.getKiller().getUniqueId())) {
            if (event.getPlayerKiller() == null)return;
            if (!event.getGameState().hasRoleNull(event.getPlayerKiller())) {
                RoleBase role = event.getGameState().getGamePlayer().get(event.getKiller().getUniqueId()).getRole();
                if (!(role instanceof ZetsuBlancV2))return;
                if (!getPermanentPotionEffects(event.getVictim()).isEmpty()) {
                    List<PotionEffect> effectList = getPermanentPotionEffects(event.getVictim());
                    if (!effectList.isEmpty()){
                        Collections.shuffle(effectList, Main.RANDOM);
                        PotionEffect potionEffect = effectList.get(0);
                        final List<PotionEffect> reste = new ArrayList<>();
                        for (PotionEffect po : effectList) {
                            if (!effectList.get(0).equals(po)) {
                                reste.add(po);
                            }
                        }
                        if (!reste.isEmpty()) {
                            sendMessagetoZetsus("§c"+reste.size()+"§7 ont été ajouter à la§f §nBanque");
                            for (PotionEffect potion : reste) {
                                addToBanque(potion);
                            }
                        }
                        if (getPermanentPotionEffects(event.getPlayerKiller()).isEmpty()) {
                            role.givePotionEffect(potionEffect, EffectWhen.PERMANENT);
                        } else {
                            if (!this.effects.containsKey(potionEffect)) {
                                sendMessagetoZetsus("§7Un nouvelle effet à été ajouter à la§f §nBanque");
                                addToBanque(potionEffect);
                            } else {
                                event.getPlayerKiller().sendMessage("L'effet est déjà dans la banque (§c"+potionEffect.getType().getName()+"§f)");
                            }
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null)return;
        if (event.getClickedInventory().getTitle() == null)return;
        if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame))return;
        if (event.getClickedInventory().getTitle().equals("Banque")) {
            if (!event.getCurrentItem().getType().equals(Material.STAINED_GLASS_PANE)) {
                ItemStack item = event.getCurrentItem();
                if (!item.hasItemMeta())return;
                if (!item.getItemMeta().hasDisplayName())return;
                for (final PotionEffect potionEffect : this.effects.keySet()) {
                    ItemStack itemStack = this.effects.get(potionEffect);
                    boolean bool = this.giveds.get(itemStack);
                    if (itemStack.getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) {
                        if (!bool) {
                            this.giveds.remove(itemStack, false);
                            event.getWhoClicked().addPotionEffect(potionEffect, true);
                            this.giveds.put(itemStack, true);
                            sendMessagetoZetsus("§7Un§c Zetsu§7 à pris l'effet "+itemStack.getItemMeta().getDisplayName()+"§7 dans la§f Banque");
                        } else {
                            event.getWhoClicked().sendMessage("§7Un autre§c Zetsu Blanc§7 (§6V2§7) à déjà choisis cette effet.");
                        }
                        break;
                    }
                }
            }
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
        }
    }
    private void addToBanque(PotionEffect effect) {
        final ItemStack itemStack = new ItemBuilder(Material.INK_SACK).setDurability(1).toItemStack();
        this.effects.put(effect, itemStack);
        this.giveds.put(itemStack, false);
    }
    private void sendMessagetoZetsus(String message) {
        if (zetsus.isEmpty())return;
        for (UUID uuid : this.zetsus) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null)continue;
            player.sendMessage(message);
        }
    }
    private List<PotionEffect> getPermanentPotionEffects(Player player) {
        final List<PotionEffect> permanentEffects = new ArrayList<>();

        for (final PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getDuration() >= 999999) {
                permanentEffects.add(effect);
            }
        }
        permanentEffects.removeIf(effect -> !effect.getType().equals(PotionEffectType.INCREASE_DAMAGE) &&
                !effect.getType().equals(PotionEffectType.DAMAGE_RESISTANCE) &&
                !effect.getType().equals(PotionEffectType.SPEED) &&
                !effect.getType().equals(PotionEffectType.FIRE_RESISTANCE) &&
                !effect.getType().equals(PotionEffectType.REGENERATION));
        return permanentEffects;
    }
}