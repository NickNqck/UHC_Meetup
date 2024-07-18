package fr.nicknqck.roles.mc.overworld;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.mc.builders.OverWorldRoles;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.packets.NMSPacket;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Poulet extends OverWorldRoles {

    private final ItemStack plumeItem = new ItemBuilder(Material.FEATHER).addEnchant(Enchantment.ARROW_DAMAGE, 4).setLore("§7Permet de voler pendant 3 secondes").setName("§aPlume").toItemStack();
    private int cdplume = 0;

    public Poulet(Player player) {
        super(player);
        player.spigot().sendMessage(getComponent());
        giveItem(owner, false, getItems());
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.Poulet;
    }

    @Override
    public String[] Desc() {
        return new String[]{

        };
    }

    public TextComponent getComponent() {
        TextComponent texte = new TextComponent(AllDesc.bar);
        texte.addExtra("\n");
        texte.addExtra("§7Role: §aPoulet\n");
        texte.addExtra("§7Votre objectif est de gagner avec le camp: §aOverWorld\n");

        texte.addExtra("\n" + AllDesc.point + "§7Vous possèdez §aNoFall §7permanent");
        texte.addExtra("\n\n" + AllDesc.point + "§7Vous possédez l'item");
        texte.addExtra(getPlumeText());
        texte.addExtra("§7 (1x/5m).");

        texte.addExtra("\n\n");
        texte.addExtra(AllDesc.bar);
        return texte;
    }

    private TextComponent getPlumeText() {
        TextComponent Plume = new TextComponent("§7 \"§aPlume§7\"");
        Plume.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(
                "§7Vous permez de volez pendant §c3 secondes. §7(1x/5m)")}));
        return Plume;
    }

    @Override
    public String getName() {
        return "Poulet";
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
                plumeItem,
        };
    }

    @Override
    public void resetCooldown() {
        cdplume = 0;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        setNoFall(true);
        super.RoleGiven(gameState);
    }

    @Override
    public boolean ItemUse(ItemStack item, GameState gameState) {
        if (item.isSimilar(plumeItem)) {
            if (cdplume <= 0) {
                owner.setAllowFlight(true);
                owner.setFlying(true);
                owner.sendMessage("Vous pouvez désormais voler");
                new BukkitRunnable() {
                    private int i = 0;

                    @Override
                    public void run() {
                        if (gameState.getInGamePlayers().contains(owner)) {
                            i++;
                            if (i == 4) {
                                owner.sendMessage("Vous ne pouvez plus voler.");
                                owner.setFlying(false);
                                owner.setAllowFlight(false);
                                cdplume = 60 * 5;
                                cancel();

                            }
                        } else {
                            cancel();
                        }
                    }
                }.runTaskTimer(Main.getInstance(), 0, 20);
            } else {
                sendCooldown(owner, cdplume);
                return true;
            }
            return true;
        }
        return super.ItemUse(item, gameState);
    }

    @Override
    public void Update(GameState gameState) {
        if (cdplume >= 0) {
            cdplume--;
            if (cdplume == 0) {
                owner.sendMessage("Vous pouvez de nouveau utilisez votre §aPlume");
            }
        }
        if (owner.getItemInHand() != null) {
            if (owner.getItemInHand().isSimilar(plumeItem)) {
                NMSPacket.sendActionBar(owner, (cdplume <= 0 ? "§e«§f Pouvoir utilisable§e »" : "§bCooldown: " + StringUtils.secondsTowardsBeautiful(cdplume)));
            }
        }

    }
}
