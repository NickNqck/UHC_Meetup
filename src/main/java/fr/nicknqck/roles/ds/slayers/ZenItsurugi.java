package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ZenItsurugi extends SlayerRoles implements Listener {
    private final ItemStack vitesseItem = new ItemBuilder(Material.NETHER_STAR).setName("§eVitesse").setUnbreakable(true).setDroppable(false).toItemStack();
    private int cdVitesse;
    public ZenItsurugi(Player player) {
        super(player);
        player.spigot().sendMessage(getComponent());
        setCanuseblade(true);
        giveItem(player, false, getItems());
        giveItem(player, false, Items.getLamedenichirin());
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public String getName() {
        return "Zen'Itsu";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.ZenItsu;
    }

    @Override
    public void resetCooldown() {
        cdVitesse = 0;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public TextComponent getComponent() {
        TextComponent text = new TextComponent(AllDesc.bar+"\n");
        text.addExtra("§7Role: §a"+getName()+"\n\n");
        text.addExtra(AllDesc.point+"§7Lorsque votre vie est en dessous de§c 5"+AllDesc.coeur+"§7 vous aurez les effets§c Speed II§7 et§c Force I\n\n");
        return super.getComponent();
    }
    private TextComponent getVitesseText(){
        TextComponent text = new TextComponent("§7\""+vitesseItem.getItemMeta().getDisplayName()+"§7\"");
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous donne l'effet§c Speed I")}));
        return text;
    }
    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
                vitesseItem
        };
    }
    @EventHandler
    private void onEndGame(EndGameEvent event) {
        HandlerList.unregisterAll(this);
    }
    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (event.getItem() != null) {
            if (event.getItem().isSimilar(vitesseItem)) {
                if (event.getPlayer().getUniqueId().equals(getPlayer())) {

                }
            }
        }
    }
}
