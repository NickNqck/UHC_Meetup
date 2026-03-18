package fr.nicknqck.roles.aot.soldats;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.InfoType;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.enums.TitanForm;
import fr.nicknqck.events.custom.RoleGiveEvent;
import fr.nicknqck.events.power.PowerTakeInfoEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.aot.builders.SoldatsRoles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.titans.impl.ColossalV2;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.fastinv.FastInv;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ArminV2 extends SoldatsRoles implements Listener{

    public ArminV2(UUID player) {
        super(player);
    }

    @Override
    public String getName() {
        return "Armin";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Armin;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new AotInvCommand(this));
        EventUtils.registerRoleEvent(this);
        super.RoleGiven(gameState);
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .getText();
    }
    @EventHandler
    private void onEndGiveRole(final RoleGiveEvent event) {
        if (!event.isEndGive())return;
        if (!Main.getInstance().getTitanManager().isTitanAttributed(TitanForm.COLOSSAL)) {
            Main.getInstance().getTitanManager().addTitan(getPlayer(), new ColossalV2(getGamePlayer()));
            getGamePlayer().sendMessage("§7Comme§9 Bertolt§7 n'est pas dans la partie vous recevez le§c titan Colossal§7.");
        }
    }
    private static class AotInvCommand extends CommandPower implements Listener {

        public AotInvCommand(@NonNull RoleBase role) {
            super("/aot inv <joueur>", "inv", new Cooldown(60*5), role, CommandType.AOT,
                    "§7Vous permet d'ouvrir l'inventaire d'une personne proche de vous en la ciblant");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            final String[] args = (String[]) map.get("args");
            if (args.length == 2) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    GamePlayer gamePlayer = GamePlayer.of(target.getUniqueId());
                    if (gamePlayer == null  || !gamePlayer.check()) {
                        player.sendMessage("§cImpossible de viser ce joueur !");
                        return false;
                    }
                    final PowerTakeInfoEvent event = new PowerTakeInfoEvent(this, gamePlayer, InfoType.INV);
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        final FastInv inv = new FastInv(54, "§fInventaire de§a "+target.getName());
                        gamePlayer = event.getGameTarget();
                        target = Bukkit.getPlayer(gamePlayer.getUuid());
                        if (target != null && gamePlayer.check()) {
                            final List<ItemStack> itemStackList = new ArrayList<>();
                            for (ItemStack itemStack : target.getInventory().getContents()) {
                                if (itemStack == null || itemStack.getType().equals(Material.AIR))continue;
                                itemStackList.add(itemStack);
                            }
                            inv.setItem(0, target.getInventory().getHelmet());
                            inv.setItem(1, target.getInventory().getChestplate());
                            inv.setItem(2, target.getInventory().getLeggings());
                            inv.setItem(3, target.getInventory().getBoots());
                            for (final ItemStack itemStack : itemStackList) {
                                if (itemStack == null)continue;
                                if (itemStack.getType().equals(Material.AIR))continue;
                                for (int i = 18; i < 53; i++) {
                                    if (inv.getInventory().getItem(i) == null) {
                                        inv.setItem(i, itemStack);
                                        break;
                                    }
                                }
                            }
                            inv.open(player);
                        }
                    }
                    return true;
                } else {
                    player.sendMessage("§b"+args[1]+"§c n'est pas connecté");
                    return false;
                }
            }
            return false;
        }
    }
}