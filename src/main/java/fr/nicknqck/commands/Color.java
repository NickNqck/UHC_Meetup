package fr.nicknqck.commands;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.scoreboard.PersonalScoreboard;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
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
import org.bukkit.material.Wool;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class Color implements CommandExecutor, Listener {

    private final GameState gameState;
    private final Map<Wool, String> supportedColors;

    public Color(GameState gameState) {
        this.gameState = gameState;
        this.supportedColors = new HashMap<>();
        EventUtils.registerEvents(this);
        registerColors();
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
                final List<Wool> woolList = new ArrayList<>(this.supportedColors.keySet());
                final Map<ItemStack, String> colors = new HashMap<>();
                for (int i = 20; i <= 40; i++) {
                    if (i > 24 && i < 29)continue;
                    if (!woolList.isEmpty()) {
                        Wool wool = woolList.get(0);
                        if (wool == null)continue;
                        final ItemStack item = new ItemBuilder(Material.WOOL).setDyeColor(wool.getColor()).setName(this.supportedColors.get(wool)+wool.getColor().name()).toItemStack();
                        inv.setItem(i, item);
                        colors.put(item, this.supportedColors.get(wool));
                        woolList.remove(0);
                    }
                }
                sender.openInventory(inv);
                new ColorSetter(sender.getUniqueId(), playerList, colors);
            }
            return true;
        }
        commandSender.sendMessage("§cUne erreur c'est produite...");
        return false;
    }
    private void registerColors() {
        for (final DyeColor dyeColor : DyeColor.values()) {
            if (dyeColor.equals(DyeColor.ORANGE) || dyeColor.equals(DyeColor.MAGENTA) || dyeColor.equals(DyeColor.SILVER) || dyeColor.equals(DyeColor.CYAN))continue;
            if (dyeColor.equals(DyeColor.WHITE) || dyeColor.equals(DyeColor.BLACK))continue;
            this.supportedColors.put(new Wool(dyeColor), getColorfromDyeColor(dyeColor));
        }
    }
    private String getColorfromDyeColor(final DyeColor dyeColor) {
        switch (dyeColor) {
            case WHITE:
                return "§f";
            case BROWN:
                return "§6";
            case PINK:
                return "§d";
            case LIGHT_BLUE:
                return "§b";
            case YELLOW:
                return "§e";
            case LIME:
                return "§a";
            case GRAY:
                return "§7";
            case SILVER:
                return "§7"; // Silver est traité comme Gray ici
            case CYAN:
                return "§3";
            case PURPLE:
                return "§5";
            case BLUE:
                return "§9";
            case GREEN:
                return "§2";
            case RED:
                return "§c";
            case BLACK:
                return "§0";
            default:
                return "§f"; // Blanc par défaut
        }
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
                    if (event.getCurrentItem().getType().equals(Material.WOOL)) {
                        if (colors.containsKey(event.getCurrentItem())) {
                            for (final Player target : this.toColor) {
                                Main.getInstance().getScoreboardManager().getScoreboards().get(uuid).changeDisplayName((Player) event.getWhoClicked(), target, this.colors.get(event.getCurrentItem()));
                            }
                            event.getWhoClicked().closeInventory();
                        }
                    }
                }
            }
        }
    }
}
