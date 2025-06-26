package fr.nicknqck.roles.ns.orochimaru.edov2;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.NSRoles;
import fr.nicknqck.roles.ns.orochimaru.*;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrochimaruV2 extends EdoOrochimaruRoles implements Listener {

    private final ItemStack kusanagi = new ItemBuilder(Material.DIAMOND_SWORD).setName("§5Kusanagi").addEnchant(Enchantment.DAMAGE_ALL, 4).
        setLore("§7Vous permet d'avoir§c 25%§7 de§c chance§7 de voler la nature de chakra des joueurs que vous§c tuées").setUnbreakable(true).setDroppable(false).toItemStack();
    private final List<Chakras> chakrasVoled = new ArrayList<>();

    public OrochimaruV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.GENIE;
    }

    @Override
    public String getName() {
        return "Orochimaru";
    }

    @Override
    public GameState.@NonNull Roles getRoles() {
        return GameState.Roles.Orochimaru;
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .addCustomLine("§7En tuant un joueur, vous obtiendrez§e +4💛 absorption§7 et aurez§c 25%§7 de chance de lui voler sa§a nature de chakra")
                .getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        setChakraType(getRandomChakras());
        givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 0, false, false), EffectWhen.PERMANENT);
        giveItem(owner, false, getItems());
        this.chakrasVoled.add(getChakras());
        EventUtils.registerRoleEvent(this);
        addKnowedPlayersWithRoles("§7Voici la liste du camp§5 Orochimaru§7:", Jugo.class, SasukeV2.class, SuigetsuV2.class, KabutoV2.class, Kimimaro.class, Karin.class, Tayuya.class);
        super.RoleGiven(gameState);
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[] {
                this.kusanagi
        };
    }
    public String getChakraString() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Chakras chakras : chakrasVoled) {
            i++;
            if (i + 1 != chakrasVoled.size()+1) {
                sb.append(chakras.getShowedName()).append("§f,");
            }else {
                sb.append(chakras.getShowedName()).append("§f.");
            }
        }
        return sb.toString();
    }
    @EventHandler
    private void onUHCKill(@NonNull final UHCPlayerKillEvent event) {
        if (event.getGamePlayerKiller() == null)return;
        if (event.getGamePlayerKiller().getUuid().equals(getPlayer())) {
            ((CraftPlayer) event.getPlayerKiller()).getHandle().setAbsorptionHearts(((CraftPlayer) event.getPlayerKiller()).getHandle().getAbsorptionHearts()+8.0f);
            if (!event.getGameState().hasRoleNull(event.getVictim().getUniqueId())) {
                final RoleBase role = event.getGameState().getGamePlayer().get(event.getVictim().getUniqueId()).getRole();
                if (role instanceof NSRoles) {
                    if (((NSRoles) role).getChakras() != null) {
                        if (!this.chakrasVoled.contains(((NSRoles) role).getChakras())) {
                            if (RandomUtils.getOwnRandomProbability(25.0)) {
                                this.chakrasVoled.add(((NSRoles) role).getChakras());
                                ((NSRoles) role).getChakras().getChakra().getList().add(getPlayer());
                                event.getPlayerKiller().sendMessage("§7Vous maitrisez maintenant la nature de chakra: "+((NSRoles) role).getChakras().getShowedName());
                            }
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    private void onConsume(@NonNull final PlayerItemConsumeEvent event) {
        if (event.getItem().getType().equals(Material.GOLDEN_APPLE)) {
            if (!event.getPlayer().getUniqueId().equals(getPlayer()))return;
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                event.getPlayer().removePotionEffect(PotionEffectType.ABSORPTION);
                ((CraftPlayer) event.getPlayer()).getHandle().setAbsorptionHearts(0);
                ((CraftPlayer) event.getPlayer()).getHandle().setAbsorptionHearts(((CraftPlayer) event.getPlayer()).getHandle().getAbsorptionHearts()+6.0f);
            }, 1);
        }
    }
}
