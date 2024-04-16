package fr.nicknqck.roles.mc.solo;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class WitherBoss extends RoleBase {
    private boolean isFlying = false;
    private final ItemStack FlyItem = new ItemBuilder(Material.FEATHER).setName("§aFly").setLore("§7Vous permet de voler pendant un temp maximum de§c 15s").toItemStack();
    private int cdFly = 0;
    public WitherBoss(Player player, GameState.Roles roles, GameState gameState) {
        super(player, roles, gameState);
    }

    @Override
    public void GiveItems() {
        super.GiveItems();
        super.giveItem(owner, false, getItems());
        super.giveHealedHeartatInt(2.0);
    }

    @Override
    public void Update(GameState gameState) {
        super.Update(gameState);
        if (!isFlying){
            givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, false);
            givePotionEffet(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false);
        }
    }

    @Override
    public String[] Desc() {
        return new String[]{

        };
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
                FlyItem
        };
    }
    @Override
    public void resetCooldown() {
        isFlying = false;
        cdFly = 0;
    }

    @Override
    public boolean ItemUse(ItemStack item, GameState gameState) {
        if (item.isSimilar(FlyItem)){
            if (cdFly <= 0) {
                isFlying = true;
                owner.sendMessage("§7Vous avez activé votre§a Fly");
                cdFly = 60*7+15;
                new BukkitRunnable(){
                    private int timeRemaining = 15;
                    @Override
                    public void run() {
                        if (gameState.getServerState() != GameState.ServerStates.InGame || !getIGPlayers().contains(owner)){
                            cancel();
                            return;
                        }
                        if (timeRemaining == 0){
                            owner.sendMessage("§7Votre§a Fly§7 prend fin.");
                            owner.setFlying(false);
                            owner.setAllowFlight(false);
                            owner.setFallDistance(0f);
                            isFlying = false;
                            cancel();
                            return;
                        }
                        sendCustomActionBar(owner, "§bTemp de vole restant:§c "+ StringUtils.secondsTowardsBeautiful(timeRemaining));
                        timeRemaining--;
                    }
                }.runTaskTimer(Main.getInstance(), 0, 20);
            } else {
                if (cdFly > 60*7){
                    owner.sendMessage("§7Votre§a Fly§7 prend fin.");
                    owner.setFlying(false);
                    owner.setAllowFlight(false);
                    owner.setFallDistance(0f);
                    isFlying = false;
                } else {
                    sendCooldown(owner, cdFly);
                }
            }
            return true;
        }
        return super.ItemUse(item, gameState);
    }
}
