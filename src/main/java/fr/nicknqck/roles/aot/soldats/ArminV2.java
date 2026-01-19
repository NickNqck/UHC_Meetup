package fr.nicknqck.roles.aot.soldats;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.enums.TitanForm;
import fr.nicknqck.events.custom.RoleGiveEvent;
import fr.nicknqck.roles.aot.builders.SoldatsRoles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.titans.impl.ColossalV2;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ArminV2 extends SoldatsRoles implements Listener{

    public ArminV2(UUID player) {
        super(player);
    }

    @Override
    public String getName() {
        return "Armin";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Armin;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new AotInvCommand(this));
        EventUtils.registerRoleEvent(this);
        super.RoleGiven(gameState);
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .getText();
    }
    @EventHandler
    private void onEndGiveRole(final RoleGiveEvent event) {
        if (!event.isEndGive())return;
        if (!Main.getInstance().getTitanManager().isTitanAttributed(TitanForm.COLOSSAL)) {
            Main.getInstance().getTitanManager().addTitan(getPlayer(), new ColossalV2(getGamePlayer()));
            getGamePlayer().sendMessage("§7Comme§9 Bertolt§7 n'est pas dans la partie vous recevez le§c titan Colossal§7.");
        }
    }
    private static class AotInvCommand extends CommandPower implements Listener {

        public AotInvCommand(@NonNull RoleBase role) {
            super("/aot inv <joueur>", "inv", new Cooldown(60*5), role, CommandType.AOT, "§7Vous permet d'ouvrir l'inventaire d'une personne proche de vous en la ciblant");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            final String[] args = (String[]) map.get("args");
            if (args.length == 2) {
                final Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    final Inventory inv = Bukkit.createInventory(player, 54, "§7(§c!§7)§f Inventaire de §a"+target.getName());
                    inv.setItem(0, target.getInventory().getHelmet());
                    inv.setItem(1, target.getInventory().getChestplate());
                    inv.setItem(2, target.getInventory().getLeggings());
                    inv.setItem(3, target.getInventory().getBoots());
                    final List<ItemStack> itemStackList = new LinkedList<>(Arrays.asList(target.getInventory().getContents()));
                    for (final ItemStack itemStack : itemStackList) {
                        if (itemStack == null)continue;
                        if (itemStack.getType().equals(Material.AIR))continue;
                        for (int i = 18; i < 53; i++) {
                            if (inv.getItem(i) == null) {
                                inv.setItem(i, itemStack);
                                break;
                            }
                        }
                    }
                    player.openInventory(inv);
                    EventUtils.registerRoleEvent(this);
                    return true;
                } else {
                    player.sendMessage("§b"+args[1]+"§c n'est pas connecté");
                    return false;
                }
            }
            return false;
        }
        @EventHandler
        private void onInvClick(@NonNull final InventoryClickEvent event) {
            if (event.getInventory() == null)return;
            if (event.getInventory().getTitle() == null)return;
            if (!event.getWhoClicked().getUniqueId().equals(getRole().getPlayer()))return;
            if (event.getInventory().getTitle().contains("§7(§c!§7)§f Inventaire de §a")) {
                event.setCancelled(true);
            }
        }
        @EventHandler
        private void onInvClose(@NonNull final InventoryCloseEvent event) {
            if (event.getInventory() == null)return;
            if (event.getInventory().getTitle() == null)return;
            if (!event.getPlayer().getUniqueId().equals(getRole().getPlayer()))return;
            if (event.getInventory().getTitle().contains("§7(§c!§7)§f Inventaire de §a")) {
                EventUtils.unregisterEvents(this);
            }
        }
    }
}