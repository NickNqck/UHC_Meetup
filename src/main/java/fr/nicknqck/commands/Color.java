package fr.nicknqck.commands;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.scoreboard.PersonalScoreboard;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class Color implements CommandExecutor, Listener {

    private final GameState gameState;

    public Color(GameState gameState) {
        this.gameState = gameState;
        EventUtils.registerEvents(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            final Player sender = (Player) commandSender;
            if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                sender.sendMessage("§cCette commande n'est faisable qu'en jeu");
                return true;
            }
            if (!gameState.isRoleAttributed()) {
                sender.sendMessage("§cCette commande n'est faisable qu'après l'annonce des rôles ");
                return true;
            }
            final PersonalScoreboard personalScoreboard = Main.getInstance().getScoreboardManager().getScoreboards().get(sender.getUniqueId());
            if (personalScoreboard == null) {
                sender.sendMessage("§cDésoler, il semblerait que vous n'ayez aucun scoreboard pour accueillir vos couleurs");
                return true;
            }
            final List<Player> playerList = new ArrayList<>();
            for (final String string : args) {
                final Player player = Bukkit.getPlayer(string);
                if (player == null)continue;
                playerList.add(player);
            }
            if (!playerList.isEmpty()) {
                final Inventory inv = Bukkit.createInventory(sender, 54, "§cChoix des couleurs");
                final Map<ItemStack, String> colors = new HashMap<>();
                inv.setItem(0, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(5).setName(" ").toItemStack());
                inv.setItem(1, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(0).setName(" ").toItemStack());
                inv.setItem(4, GUIItems.getSelectBackMenu());
                inv.setItem(9, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(0).setName(" ").toItemStack());

                inv.setItem(8, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(5).setName(" ").toItemStack());
                inv.setItem(7, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(0).setName(" ").toItemStack());
                inv.setItem(17, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(0).setName(" ").toItemStack());

                inv.setItem(45, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(5).setName(" ").toItemStack());
                inv.setItem(46, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(0).setName(" ").toItemStack());
                inv.setItem(45-9, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(0).setName(" ").toItemStack());

                inv.setItem(53, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(5).setName(" ").toItemStack());
                inv.setItem(52, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(0).setName(" ").toItemStack());
                inv.setItem(53-9, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(0).setName(" ").toItemStack());

                final ItemStack red = new ItemBuilder(Material.INK_SACK).setName("§cRouge").setDurability(1).toItemStack();
                final ItemStack yellow = new ItemBuilder(Material.INK_SACK).setName("§eJaune").setDurability(11).toItemStack();
                final ItemStack bleu = new ItemBuilder(Material.INK_SACK).setName("§9Bleu").setDurability(4).toItemStack();
                final ItemStack bleuciel = new ItemBuilder(Material.INK_SACK).setName("§bAqua").setDurability(12).toItemStack();
                final ItemStack rose = new ItemBuilder(Material.INK_SACK).setName("§dRose").setDurability(9).toItemStack();
                final ItemStack violet = new ItemBuilder(Material.INK_SACK).setName("§5Violet").setDurability(5).toItemStack();
                final ItemStack orange = new ItemBuilder(Material.INK_SACK).setName("§6Orange").setDurability(14).toItemStack();
                final ItemStack vertclair = new ItemBuilder(Material.INK_SACK).setName("§aVert").setDurability(10).toItemStack();
                final ItemStack vertfonce = new ItemBuilder(Material.INK_SACK).setName("§2Vert foncé").setDurability(2).toItemStack();
                final ItemStack rougefonce = new ItemBuilder(Material.REDSTONE).setName("§4Rouge foncé").toItemStack();

                inv.setItem(20, red);
                inv.setItem(21, yellow);
                inv.setItem(22, bleu);
                inv.setItem(23, bleuciel);
                inv.setItem(24, rose);
                inv.setItem(29, violet);
                inv.setItem(30, orange);
                inv.setItem(31, vertclair);
                inv.setItem(32, vertfonce);
                inv.setItem(33, rougefonce);
                sender.openInventory(inv);

                colors.put(red, "§c");
                colors.put(yellow, "§e");
                colors.put(bleu, "§9");
                colors.put(bleuciel, "§b");
                colors.put(rose, "§d");
                colors.put(violet, "§5");
                colors.put(orange, "§6");
                colors.put(vertclair, "§a");
                colors.put(vertfonce, "§2");
                colors.put(rougefonce, "§4");
                new ColorSetter(sender.getUniqueId(), playerList, colors);
            }
            return true;
        }
        commandSender.sendMessage("§cUne erreur c'est produite...");
        return false;
    }
    @EventHandler
    private void onEndGame(EndGameEvent event) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager != null) {
            Scoreboard mainScoreboard = manager.getMainScoreboard();
            for (Team team : mainScoreboard.getTeams()) {
                team.unregister();
            }
        }
    }
    private static class ColorSetter implements Listener {

        private final List<Player> toColor;
        private final UUID uuid;
        private final Map<ItemStack, String> colors;

        private ColorSetter(@NonNull UUID uuid, @NonNull List<Player> players, @NonNull Map<ItemStack, String> map) {
            this.uuid = uuid;
            this.toColor = new ArrayList<>(players);
            this.colors = map;
            EventUtils.registerRoleEvent(this);
        }
        @EventHandler
        private void onInventoryClose(InventoryCloseEvent event) {
            if (event.getPlayer().getUniqueId().equals(uuid)) {
                if (event.getInventory().getTitle().equals("§cChoix des couleurs")) {
                    EventUtils.unregisterEvents(this);
                }
            }
        }
        @EventHandler
        private void onInventoryClick(InventoryClickEvent event) {
            if (event.getInventory() == null)return;
            if (event.getInventory().getTitle() == null)return;
            if (event.getInventory().getTitle().equals("§cChoix des couleurs")) {
                if (event.getWhoClicked() == null)return;
                if (event.getWhoClicked().getUniqueId().equals(uuid)) {
                    if (event.getCurrentItem() == null)return;
                    if (event.getCurrentItem().getItemMeta() == null)return;
                    if (event.getCurrentItem().getItemMeta().getDisplayName() == null)return;
                    if (!(event.getWhoClicked() instanceof Player))return;
                    if (event.getCurrentItem().getType().equals(Material.INK_SACK) || event.getCurrentItem().getType().equals(Material.REDSTONE)) {
                        if (colors.containsKey(event.getCurrentItem())) {
                            Player clicker = (Player) event.getWhoClicked();
                            for (final Player target : this.toColor) {
                                if (target == null)continue;
                                Main.getInstance().getScoreboardManager().getScoreboards().get(uuid)
                                        .changeDisplayName(clicker, target, this.colors.get(event.getCurrentItem()));
                            }
                            clicker.closeInventory();
                        }
                    } else {
                        if (event.getCurrentItem().isSimilar(GUIItems.getSelectBackMenu())) {
                            event.getWhoClicked().sendMessage("§7Vous avez§c annulé§7 la coloration d'un ou plusieurs joueur(s)");
                            event.getWhoClicked().closeInventory();
                        }
                    }
                }
            }
        }
    }
}
