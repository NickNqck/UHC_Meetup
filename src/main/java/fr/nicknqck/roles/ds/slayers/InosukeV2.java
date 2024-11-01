package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.UUID;

public class InosukeV2 extends SlayerRoles {

    private TextComponent textComponent;


    public InosukeV2(UUID player) {
        super(player);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.VENT;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Inosuke";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.Inosuke;
    }

    @Override
    public void resetCooldown() {

    }

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
        this.textComponent = new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .getText();
    }
    private static class SentimentCommand extends CommandPower {

        private boolean traque = false;

        public SentimentCommand(@NonNull RoleBase role) {
            super("/ds sentiments", "sentiments", new Cooldown(60*10), role, CommandType.DS);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> strings) {
            String[] args = (String[]) strings.get("args");
            if (args.length == 1) {
                TextComponent component = new TextComponent("§7Voici la liste de tout les§c joueurs§7 autours de vous:\n\n");
                for (final Player aroundPlayer : Loc.getNearbyPlayersExcept(player, 50)) {
                    TextComponent toAdd = new TextComponent("§7 -§c "+aroundPlayer.getDisplayName()+"§7 (§c"+ new DecimalFormat("0").format(aroundPlayer.getLocation().distance(player.getLocation()))+"§7)\n");
                    toAdd.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
                            new TextComponent("§a§lCLIQUEZ ICI POUR TRAQUER")
                    }));
                    toAdd.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ds sentiments "+aroundPlayer.getName()));
                    component.addExtra(toAdd);
                }
                player.spigot().sendMessage(component);
                traque = true;
                Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> {
                    if (traque) {
                        player.sendMessage("§cVous avez mis trop de temp à vous décidez, vous ne pouvez plus traquer personne.");
                        traque = false;
                        setWorkWhenInCooldown(false);
                    }
                }, 20*10);
                setWorkWhenInCooldown(true);
                return true;
            } else if (args.length == 2) {
                if (!traque) {
                    player.sendMessage("§cIl est trop tard pour démarrer la traque");
                    return false;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    setWorkWhenInCooldown(false);
                    traque = false;
                }
            }
            return false;
        }
    }
}
