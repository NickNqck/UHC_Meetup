package fr.nicknqck.roles.ns.shinobi.porte;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.events.custom.doublejump.JumpStartEvent;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.TripleMap;
import fr.nicknqck.utils.event.EventUtils;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.UUID;

public class RockLeeV2 extends PortesRoles implements Listener {

    private final TextComponent desc;

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
        desc.addParticularites(
          new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous possédez la nature de chakra: "+getChakras().getShowedName())})
        );
        this.desc = desc.getText();
        EventUtils.registerEvents(this);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new TroisPortePower(this), true);
        addPower(new SixPortesPower(this), true);
        addPower(new HuitPortesPower(this), true);
        addPower(new SakePower(this), true);
    }

    @Override
    public TextComponent getComponent() {
        return desc;
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public String getName() {
        return "Rock Lee";
    }

    @EventHandler
    private void onEnd(EndGameEvent event) {
        EventUtils.unregisterEvents(this);
    }
    @EventHandler
    private void onDoubleJump(JumpStartEvent event) {
        event.setCancelled(false);
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.RockLee;
    }

    private static class SakePower extends ItemPower {

        protected SakePower(RoleBase role) {
            super("§aAlcoolique No Jutsu", new Cooldown(60*3), new ItemBuilder(Material.GLASS_BOTTLE).setName("§aAlcoolique no Jutsu"), role);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                player.setAllowFlight(true);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60, 0, false, false), true);
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*15, 0, false, false), true), 20*60);
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> player.setAllowFlight(false), 20*60);
                return true;
            }
            return false;
        }
    }
}