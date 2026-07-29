package fr.nicknqck.commands;

import fr.nicknqck.Main;
import fr.nicknqck.player.PlayerInfo;
import fr.nicknqck.utils.fastinv.FastInv;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class SettingsCommand implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Seul un joueur peut faire cette commande");
            return false;
        }
        final Player sender = (Player) commandSender;
        openParamInv(sender);
        return true;
    }
    private void openParamInv(final Player player) {
        final FastInv fastInv = new FastInv(27, "§fParamètres");
        fastInv.setItems(fastInv.getCorners(), new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
        fastInv.setItem(12, new ItemBuilder(Material.REDSTONE).setName("§fAfficher les particules des rôles")
                .setLore("§7Lorsqu'§aactiver§7, vous verrez des particules lorsque vous essayez de viser avec un pouvoir",
                        "",
                        "§fActuellement§7: "+(Main.getInstance().getInfoManager().getPlayerInfo(player.getUniqueId()).isShowRoleParticle() ? "§aActiver" : "§cDésactiver"))
                .toItemStack(), event -> {
            final PlayerInfo info = Main.getInstance().getInfoManager().getPlayerInfo(event.getWhoClicked().getUniqueId());
            info.setShowRoleParticle(!info.isShowRoleParticle());
            event.getWhoClicked().sendMessage(Main.getInstance().getNAME()+"§7 \"§fAfficher les particules des rôles§7\" est maintenant sur "+(info.isShowRoleParticle() ? "§aActiver" : "§cDésactiver"));
            Main.getInstance().getInfoManager().save(event.getWhoClicked().getUniqueId());
            openParamInv((Player) event.getWhoClicked());
        });
        fastInv.setItem(14, new ItemBuilder(Material.DIAMOND_SWORD)
                .setName("§fEmpêcher le drop d'§cépée")
                .setLore("§7Lorsqu'§aactiver§7, vous ne pourrez plus jeter vos§cc épées§7.",
                        "§o§7(sauf si vous êtes accroupie)",
                        "",
                        "§fActuellement§7: "+(Main.getInstance().getInfoManager().getPlayerInfo(player.getUniqueId()).isAntiDropSword() ? "§aActiver" : "§cDésactiver"))
                .toItemStack(), event -> {
            final PlayerInfo info = Main.getInstance().getInfoManager().getPlayerInfo(event.getWhoClicked().getUniqueId());
            info.setAntiDropSword(!info.isAntiDropSword());
            event.getWhoClicked().sendMessage(Main.getInstance().getNAME()+"§7 \"§fEmpêcher le drop d'§cépée§7\" est maintenant définie sur: "+(info.isAntiDropSword() ? "§aActiver" : "§cDésactiver"));
            Main.getInstance().getInfoManager().save(event.getWhoClicked().getUniqueId());
            openParamInv((Player) event.getWhoClicked());
        });
        fastInv.open(player);
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onDrop(@NonNull final PlayerDropItemEvent event) {
        if (event.isCancelled())return;
        if (event.getPlayer().isSneaking())return;
        final PlayerInfo info = Main.getInstance().getInfoManager().getPlayerInfo(event.getPlayer().getUniqueId());
        if (info.isAntiDropSword() && isSword(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Main.getInstance().getNAME()+"§7 Vos paramètres vous empêche de jeter votre§c épéé§7.");
        }
    }
    /**
     * Vérifie si un ItemStack est une épée (Bois, Pierre, Fer, Or, Diamant).
     *
     * @param item L'itemStack à vérifier
     * @return true si l'item est une épée, sinon false
     */
    public boolean isSword(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        // En 1.8.8, le nom de l'enum de chaque épée se termine par "_SWORD"
        return item.getType().name().endsWith("_SWORD");
    }
}