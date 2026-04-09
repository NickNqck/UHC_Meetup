package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.EffectWhen;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.interfaces.IRoles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.HShinobiRoles;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AsumaV2 extends HShinobiRoles {

    public AsumaV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.CONNUE;
    }

    @Override
    public Chakras[] getChakrasCanHave() {
        return new Chakras[] {
                Chakras.FUTON
        };
    }

    @Override
    public String getName() {
        return "Asuma";
    }

    @Override
    public @NonNull IRoles<?> getRoles() {
        return Roles.Asuma;
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new LameDeChakra(this), true);
        addPower(new NueArdente(this), true);
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0, false, false), EffectWhen.PERMANENT);
        super.RoleGiven(gameState);
    }

    private static final class LameDeChakra extends ItemPower {

        public LameDeChakra(@NonNull RoleBase role) {
            super("§aLame de chakra§r", null, new ItemBuilder(Material.IRON_SWORD).setName("§aLame de chakra").addEnchant(Enchantment.DAMAGE_ALL, 4), role,
                    "§7En frappant un autre joueur avec cette épée, il y aura§c 10% de chance§7 que la§c cible§7 prenne§c feu§7.");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.ATTACK_ENTITY)) {
                final UHCPlayerBattleEvent event = (UHCPlayerBattleEvent) map.get("event");
                if (!event.isPatch())return false;
                if (!RandomUtils.getOwnRandomProbability(10.0))return false;
                event.getOriginEvent().getEntity().setFireTicks(160);
                player.sendMessage(getPlugin().getNAME()+"§7Vous avez§6 brûlé§c "+event.getVictim().getPlayerName());
                return true;
            }
            return false;
        }
    }
    private static final class NueArdente extends ItemPower {

        public NueArdente(@NonNull RoleBase role) {
            super("Nuée Ardente", new Cooldown(30*7), new ItemBuilder(Material.SULPHUR).setName("§aNuée Ardente"), role,
                    "§7Donne l'effet§c Blindness I§7 pendant§c 20 secondes§7 à tout les joueurs proche.");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final List<Player> playerList = new ArrayList<>(Loc.getNearbyPlayersExcept(player, 25));
                if (playerList.isEmpty())return false;
                player.sendMessage("§aNuées Ardentes!");
                for (Player p : playerList) {
                    if (player.canSee(p)) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*20, 0, false, false), true);
                        p.sendMessage(getPlugin().getNAME()+"§7Vous venez d'être toucher par la§a Nuées Ardentes §7de§a Asuma");
                        player.sendMessage(getPlugin().getNAME()+"§7§l"+p.getName()+"§7 a été touchée");
                    }
                }
                return true;
            }
            return false;
        }
    }
}