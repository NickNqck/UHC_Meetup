package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.EffectWhen;
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
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TanjiroV2 extends SlayerRoles implements Listener {
    private final ItemStack danseItem = new ItemBuilder(Material.BLAZE_ROD).setName("§6Danse du dieu du Feu").setUnbreakable(true).setDroppable(false).toItemStack();
    private int cdDanse;
    public TanjiroV2(Player player) {
        super(player);
        player.spigot().sendMessage(desc());
        getEffects().put(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false), EffectWhen.DAY);
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public String getName() {
        return "Tanjiro";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.Tanjiro;
    }

    @Override
    public void resetCooldown() {
        cdDanse = 0;
    }

    @Override
    public String[] Desc() {
        return new String[] {

        };
    }
    private TextComponent desc() {
        TextComponent texte = new TextComponent(AllDesc.bar);
        texte.addExtra("\n");
        texte.addExtra("§7Role: §aTanjiro\n");
        texte.addExtra("§7Votre objectif est de gagner avec le camp: §aSlayers\n");
        TextComponent danseItem = getDanseText();
        texte.addExtra(AllDesc.point+"§7Vous possédez l'effet§c Speed I§7 le§c jour§7, ainsi que l'item");
        texte.addExtra(danseItem);
        texte.addExtra("§7 ");
        return texte;
    }

    private TextComponent getDanseText() {
        TextComponent danseItem = new TextComponent(" §7\"§6Danse §6du dieu §6du Feu§7\"");
        danseItem.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Pendant§c 5 minutes§7 vous obtiendrez l'effet§c résistance 1§7, également,\n" +
                "§7pendant§c 1 minutes§7 vos coups mettront en §cfeu§7 les joueurs frappés, de plus,\n§7" +
                "le joueur possédant le rôle de§a Nezuko§7 obtiendra l'effet§c speed 2§7 pendant§c 5 minutes§7,\n§7" +
                " après ce temp là vous perdrez§c 2"+AllDesc.coeur+"§c permanent§7.")}));
        return danseItem;
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
                danseItem
        };
    }
    @EventHandler
    private void onUse(PlayerInteractEvent event) {
        if (event.getItem().isSimilar(danseItem)) {
            if (event.getPlayer().getUniqueId().equals(getPlayer())) {
                if (cdDanse <= 0) {
                    event.getPlayer().sendMessage("§7Vous utilisez votre§6 Danse du dieu du Feu");
                    cdDanse = 60*15;
                }
            }
        }
    }
}