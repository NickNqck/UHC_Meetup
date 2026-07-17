package fr.nicknqck.commands;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.items.Items;
import fr.nicknqck.items.ItemsManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class InvCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player))return false;
        final Player sender = (Player) commandSender;
        if (GameState.inGame()) {
            sender.sendMessage("§cImpossible de configurer son inventaire en pleine partie !");
            return true;
        }
        if (!Main.getInstance().getGameConfig().getStuffConfig().isDefaultInventory()) {
            sender.sendMessage("§cImpossible de customiser un inventaire custom !");
            return true;
        }
        if (!Main.getInstance().getInvManager().getConfiguring().contains(sender.getUniqueId())) {
            Main.getInstance().getInvManager().getConfiguring().add(sender.getUniqueId());
            giveInventory(sender);
            sender.sendMessage("§aFaite de nouveau la commande§6 /inv§a pour sauvegarder l'inventaire");
        } else {
            Main.getInstance().getInvManager().getConfiguring().remove(sender.getUniqueId());
            Main.getInstance().getInvManager().saveInventory(sender);
            ItemsManager.ClearInventory(sender);
            ItemsManager.GiveHubItems(sender);
            sender.sendMessage("§aVous avez sauvegardez votre inventaire de départ.");
        }
        return true;
    }
    public void giveInventory(Player p) {
        if (Main.getInstance().getInvManager().hasSavedInventory(p)) {
            Main.getInstance().getInvManager().restoreInventory(p);
        } else {
            p.getInventory().setItem(0, Items.getdiamondsword());
            p.getInventory().setItem(2, Items.getbow());
                p.getInventory().setItem(4, new ItemStack(Material.ENDER_PEARL, 1));

            p.getInventory().setItem(5, new ItemStack(Material.GOLDEN_CARROT, 64));
            p.getInventory().setItem(9, new ItemStack(Material.ARROW, Main.getInstance().getGameConfig().getStuffConfig().getNmbArrow()));
            p.getInventory().setItem(20, new ItemStack(Material.ANVIL, 1));
            p.getInventory().setItem(11, Items.getironshovel());
            p.getInventory().setItem(12, Items.getironpickaxe());

                p.getInventory().setItem(1, new ItemStack(Material.BRICK, 64));
                p.getInventory().setItem(28, new ItemStack(Material.BRICK, 64));
                p.getInventory().setItem(19, new ItemStack(Material.BRICK, 64));
                p.getInventory().setItem(10, new ItemStack(Material.BRICK, 64));

                p.getInventory().setItem(7, new ItemStack(Material.WATER_BUCKET, 1));
                p.getInventory().setItem(16, new ItemStack(Material.WATER_BUCKET, 1));
                p.getInventory().setItem(25, new ItemStack(Material.WATER_BUCKET, 1));
                p.getInventory().setItem(34, new ItemStack(Material.WATER_BUCKET, 1));

                p.getInventory().setItem(6, new ItemStack(Material.LAVA_BUCKET, 1));
                p.getInventory().setItem(15, new ItemStack(Material.LAVA_BUCKET, 1));
                p.getInventory().setItem(24, new ItemStack(Material.LAVA_BUCKET, 1));
                p.getInventory().setItem(33, new ItemStack(Material.LAVA_BUCKET, 1));

            p.getInventory().setItem(3, new ItemStack(Material.GOLDEN_APPLE, Main.getInstance().getGameConfig().getStuffConfig().getNmbGap()));
            p.updateInventory();
        }
    }
}