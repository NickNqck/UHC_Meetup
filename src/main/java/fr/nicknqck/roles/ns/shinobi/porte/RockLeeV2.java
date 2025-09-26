package fr.nicknqck.roles.ns.shinobi.porte;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.shinobi.Gai;
import fr.nicknqck.utils.TripleMap;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.UUID;

public class RockLeeV2 extends PortesRoles implements Listener {


    public RockLeeV2(UUID player) {
        super(player);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new TroisPortePower(this), true);
        addPower(new SixPortesPower(this), true);
        addPower(new SakePower(this), true);
        EventUtils.registerRoleEvent(this);
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this).setItems(troisPorteMap(), sixPorteMap(), huitPorteMap(),
                new TripleMap<>(
                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous permet d'obtenir l'effet§b Speed I§7 pendant§c 1 minute§7, puis, vous obtiendrez§c 15 secondes§7 de§2 nausé§7. (1x/3m)")}),
                        "§aAlcoolique no Jutsu",
                        60*3
                )).addParticularites(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7A la mort de §aGai Maito§7 vous obtiendrez l'item "+huitPorteMap().getSecond())})
        ).getText();
    }

    @Override
    public String getName() {
        return "Rock Lee";
    }

    @EventHandler
    private void onDie(UHCDeathEvent event) {
        if (event.getGameState().getServerState().equals(GameState.ServerStates.InGame)) {
            if (event.getRole() == null)return;
            if (event.getRole() instanceof Gai || event.getRole() instanceof GaiV2) {
                if (!getGamePlayer().isAlive()){
                    addPower(new HuitPortesPower(this));
                    return;
                }
                addPower(new HuitPortesPower(this), true);
            }
        }
    }
    @Override
    public @NonNull Roles getRoles() {
        return Roles.RockLee;
    }

    private static class SakePower extends ItemPower {

        protected SakePower(RoleBase role) {
            super("§aAlcoolique No Jutsu", new Cooldown(60*3), new ItemBuilder(Material.GLASS_BOTTLE).setName("§aAlcoolique no Jutsu"), role);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> args) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                player.setAllowFlight(true);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60, 0, false, false), true);
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*15, 0, false, false), true), 20*60);
                return true;
            }
            return false;
        }
    }
}