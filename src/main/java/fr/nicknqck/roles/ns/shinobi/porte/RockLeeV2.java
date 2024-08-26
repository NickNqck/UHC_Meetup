package fr.nicknqck.roles.ns.shinobi.porte;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.TripleMap;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
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

import java.util.Map;
import java.util.UUID;

public class RockLeeV2 extends PortesRoles implements Listener {

    private final TextComponent desc;
    private final ItemStack sakeItem = new ItemBuilder(Material.GLASS_BOTTLE).setName("§aAlcoolique no Jutsu").setUnbreakable(true).setDroppable(false).toItemStack();
    private int cdSake;
    public RockLeeV2(UUID player) {
        super(player);
        AutomaticDesc desc = new AutomaticDesc(this);
        desc.setItems(new TripleMap<>(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous donne l'effet§b Speed I§7 pendant§c 1m30s§7.\n\n§7Coût:§c 1" + AllDesc.coeur + "§7 (non permanent)")}),
                "§aTroisième Porte",
                90
                ),
        new TripleMap<>(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous donne les effets§b Speed I§7 et§c Force I§7 pendant§c 3 minutes§7.\n\n§7Coût:§c 1❤ permanent")}),
                "§aSixième Porte",
                180
        ),
        new TripleMap<>(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous donne les effets§b Speed II§7,§c Force I§7,§9 Résistance I§7 et§6 Fire Résistance I§7 pendant§c 5 minutes§7, également, vous obtiendrez§c 15❤§7 permanent.\n\n§7Coût: (Après§c 5 minutes§7) Vous fait tomber à§c 5❤ permanent§7, après ceci vous ne pourrez plus utiliser vos§a portes§7.")}),
                "§aHuitième Porte",
                -500
        ),
        new TripleMap<>(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous permet d'obtenir l'effet§b Speed I§7 pendant§c 1 minute§7, puis, vous obtiendrez§c 15 secondes§7 de§2 nausé§7. (1x/3m)")}),
                "§aAlcoolique no Jutsu",
                60*3
        ));
        this.desc = desc.getText();
        addPower(new Test(this), true);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        giveItem(owner, false, getItems());
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public TextComponent getComponent() {
        return desc;
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
                this.troisPorteItem,
                this.sixPorteItem,
                this.huitPorteItem,
                this.sakeItem
        };
    }

    @Override
    public String getName() {
        return "Rock Lee";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.RockLee;
    }

    @Override
    public void resetCooldown() {
        cdSake = 0;
    }

    @Override
    public void Update(GameState gameState) {
        if (cdSake >= 0) {
            cdSake--;
            if (cdSake == 0) {
                owner.sendMessage("§7Vous pouvez à nouveau boire de l'alcool.");
            }
        }
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (!event.hasItem()) return;
        if (event.isCancelled()) return;
        if (!event.getPlayer().getUniqueId().equals(getPlayer())) return;
        ItemStack item = event.getItem();
        if (item.isSimilar(this.sakeItem)) {
            event.setCancelled(true);
            if (cdSake > 0) {
                sendCooldown(owner, cdSake);
                return;
            }
            owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60, 0, false, false), true);
            cdSake = 60*3;
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> owner.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*15, 0, false, false), true), 20*60);
        }
    }
    private static class Test extends ItemPower {

        protected Test(RockLeeV2 role) {
            super("§aTest", new Cooldown(80), new ItemBuilder(Material.FEATHER), role);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                player.sendMessage("§dGAY");
                return true;
            }
            return false;
        }
    }
}