package fr.nicknqck.roles.ns.akatsuki.blancv2;

import fr.nicknqck.GameState;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.AkatsukiRoles;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ZetsuBlancV2 extends AkatsukiRoles {

    private TextComponent desc;


    public ZetsuBlancV2(UUID player) {
        super(player);
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public Intelligence getIntelligence() {
        return Intelligence.PEUINTELLIGENT;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        setChakraType(getRandomChakras());
        AutomaticDesc desc = new AutomaticDesc(this);
        desc.addParticularites(
          new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous possédez la nature de chakra: "+getChakras().getShowedName())}),
          new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7")})
        );
        this.desc = desc.getText();
    }

    @Override
    public String getName() {
        return "Zetsu Blanc §7(§6V2§7)";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.ZetsuBlancV2;
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
        return this.desc;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAllPlayerChat(PlayerChatEvent e, Player p) {
        if (p.getUniqueId().equals(getPlayer())) {
            if (e.getMessage().startsWith("!")) {
                if (e.getMessage().length() == 1) {
                    return;
                }
                for (GamePlayer gamePlayer : gameState.getGamePlayer().values()) {
                    if (gamePlayer.isAlive()) {
                        if (gamePlayer.getRole() != null) {
                            if (gamePlayer.getRole() instanceof ZetsuBlancV2) {
                                Player zetsu = Bukkit.getPlayer(gamePlayer.getUuid());
                                if (zetsu != null) {
                                    String message = e.getMessage().substring(1);
                                    zetsu.sendMessage("§cZetsu Blanc V2 "+message);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
