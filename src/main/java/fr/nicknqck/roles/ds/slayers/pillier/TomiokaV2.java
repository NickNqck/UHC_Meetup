package fr.nicknqck.roles.ds.slayers.pillier;

import fr.nicknqck.GameState;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.builders.DemonsSlayersRoles;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class TomiokaV2 extends PillierRoles {

    private TextComponent desc;

    public TomiokaV2(UUID player) {
        super(player);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.EAU;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Tomioka";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.Tomioka;
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
        return desc;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false), EffectWhen.PERMANENT);
        ItemStack Book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta BookMeta = (EnchantmentStorageMeta) Book.getItemMeta();
        BookMeta.addStoredEnchant(Enchantment.DEPTH_STRIDER, 3, false);
        Book.setItemMeta(BookMeta);
        giveItem(owner, false, Book);
        addPower(new FindOthersPower(this));
    }

    private static class FindOthersPower extends Power {

        public FindOthersPower(@NonNull RoleBase role) {
            super("(Passif) Chercheur d'eau", new Cooldown(0), role, "§7Permet de savoir toute les "+(GameState.getInstance().isMinage() ? "§c5 minutes" : "§c2 minutes")+"§7 si un utilisateur du Soufle de l'§bEau§7 est présent autours de vous ou non");
            new FinderRunnable(role.getGameState(), this);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            return false;
        }
        private static class FinderRunnable extends BukkitRunnable {

            private final GameState gameState;
            private final FindOthersPower power;
            private final int maxTime;
            private int actualTime;


            private FinderRunnable(GameState gameState, FindOthersPower power) {
                this.gameState = gameState;
                this.power = power;
                if (gameState.isMinage()) {
                    maxTime = 60*5;
                } else {
                    maxTime = 60*2;
                }
                this.actualTime = maxTime;
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (actualTime == 0) {
                    GamePlayer gamePlayer = power.getRole().getGamePlayer();
                    if (!gamePlayer.isAlive()) {
                        return;
                    }
                    Player owner = Bukkit.getPlayer(gamePlayer.getUuid());
                    if (owner != null) {
                        boolean isPresent = false;
                        for (Player p : Loc.getNearbyPlayersExcept(owner, 20)) {
                            if (!gameState.hasRoleNull(p)) {
                                GamePlayer gm = gameState.getGamePlayer().get(p.getUniqueId());
                                if (gm.isAlive()) {
                                    if (gm.getRole() instanceof DemonsSlayersRoles) {
                                        if (((DemonsSlayersRoles) gm.getRole()).getSoufle().equals(Soufle.EAU) || ((DemonsSlayersRoles) gm.getRole()).getSoufle().equals(Soufle.TOUS)) {
                                            owner.sendMessage("§7Quelque chose vous fait pensez qu'un utilisateur du§b Soufle de l'Eau§7 est proche de vous (§c20 blocs§7)");
                                            isPresent = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        if (!isPresent) {
                            owner.sendMessage("§7Vous ne sentez aucun autre utilisateur du§b Soufle de l'eau§7 autours de vous");
                        }
                    }
                    actualTime = maxTime;
                }
                actualTime--;
            }
        }
    }
}