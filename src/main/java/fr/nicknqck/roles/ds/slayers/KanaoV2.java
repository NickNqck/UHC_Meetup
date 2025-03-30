package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.GameState;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class KanaoV2 extends SlayerRoles {

    private TextComponent textComponent;

    public KanaoV2(UUID player) {
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
        return "Kanao";
    }

    @Override
    public GameState.@NonNull Roles getRoles() {
        return GameState.Roles.Kanao;
    }

    @Override
    public void resetCooldown() {}

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public TextComponent getComponent() {
        return this.textComponent;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new TanjiroCommand(this));
        addPower(new FouilleCommand(this));
        this.textComponent = new AutomaticDesc(this).addEffects(getEffects()).setPowers(getPowers()).getText();
    }
    private static class TanjiroCommand extends CommandPower {

        public TanjiroCommand(@NonNull RoleBase role) {
            super("§a/ds tanjiro <joueur>", "tanjiro", new Cooldown(-500), role, CommandType.DS);
            setMaxUse(1);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            String[] args = (String[]) map.get("args");
            if (args.length == 2) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    final GamePlayer gamePlayer = getRole().getGameState().getGamePlayer().get(target.getUniqueId());
                    if (gamePlayer == null) {
                        player.sendMessage("§b"+args[1]+"§c ne possède pas de rôle");
                        return false;
                    }
                    if (gamePlayer.getRole() == null || !gamePlayer.isAlive()) {
                        player.sendMessage("§b"+args[1]+"§c ne possède pas de rôle");
                        return false;
                    }
                    if (gamePlayer.getRole() instanceof Tanjiro) {
                        player.sendMessage("§a"+args[1]+"§7 est bien§a Tanjiro§7 vous obtenez§c +2❤ permanents§7 ainsi que l'effet§c Force I§7 à moins de§c ");
                        getRole().setMaxHealth(getRole().getMaxHealth()+4.0);
                        player.setMaxHealth(getRole().getMaxHealth());
                        player.setHealth(player.getHealth()+4.0);
                    } else {
                        player.sendMessage("§c"+args[1]+"§7 n'est§c pas§a Tanjiro§7, vous perdez§c 1❤ permanent§7.");
                        getRole().setMaxHealth(getRole().getMaxHealth()+4.0);
                        player.setMaxHealth(getRole().getMaxHealth());
                    }
                    return true;
                } else {
                    player.sendMessage("§b"+args[1]+"§c n'est pas connectée");
                }
            } else {
                player.sendMessage("§cIl faut viser un joueur");
            }
            return false;
        }
    }
    private static class FouilleCommand extends CommandPower {

        public FouilleCommand(@NonNull RoleBase role) {
            super("/ds fouille <joueur>", "fouille", new Cooldown(60*10), role, CommandType.DS);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            String[] args = (String[]) map.get("args");
            if (args.length == 2) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    if (!Loc.getNearbyPlayers(player, 25).contains(target)) {
                        player.sendMessage("§b"+args[1]+"§c n'est pas asser proche pour se faire fouiller");
                        return false;
                    }
                    final String invName = "§aInventaire de§c "+target.getDisplayName();
                    final Inventory inv = Bukkit.createInventory(player, 9*5, invName);
                    inv.setContents(target.getInventory().getContents());
                    int i = 9*4;
                    for (final ItemStack armor : target.getInventory().getArmorContents()) {
                        if (armor == null)continue;
                        inv.setItem(i, armor);
                        i++;
                    }
                    player.openInventory(inv);
                    new FouilleListener(invName, player.getUniqueId());
                    return true;
                } else {
                    player.sendMessage("§b"+args[1]+"§c n'est pas connecter");
                    return false;
                }
            }
            player.sendMessage("§cCette commande prend un joueur en entrer");
            return false;
        }
        private static class FouilleListener implements Listener {

            private final String invName;
            private final UUID uuidOwner;

            private FouilleListener(String invName, UUID uuidOwner) {
                this.invName = invName;
                this.uuidOwner = uuidOwner;
                EventUtils.registerEvents(this);
            }
            @EventHandler
            private void onInventoryClick(InventoryClickEvent event) {
                if (event.getClickedInventory() == null) return;
                if (event.getCurrentItem() == null)return;
                if (!event.getWhoClicked().getUniqueId().equals(uuidOwner))return;
                if (event.getClickedInventory().getTitle().equals(invName) || event.getWhoClicked().getOpenInventory().getTitle().equals(invName)) {
                    event.setCancelled(true);
                }
            }
            @EventHandler
            private void onInventoryQuit(InventoryCloseEvent event) {
                if (event.getInventory() == null)return;
                if (event.getInventory().getTitle().equals(invName)) {
                    if (event.getPlayer().getUniqueId().equals(uuidOwner)) {
                        EventUtils.unregisterEvents(this);
                    }
                }
            }
        }
    }
}