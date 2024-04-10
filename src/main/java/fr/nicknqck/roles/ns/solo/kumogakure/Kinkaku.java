package fr.nicknqck.roles.ns.solo.kumogakure;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.StringUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Kinkaku extends RoleBase {
    private final ItemStack KyubiItem = new ItemBuilder(Material.NETHER_STAR).setName("§6§lKyubi").setLore("§7Vous permet d'obtenir des effets").toItemStack();
    private int cdKyubi = 0;
    private final ItemStack EventailItem = new ItemBuilder(Material.DIAMOND_SWORD).addEnchant(Enchantment.DAMAGE_ALL, 3).setName("§aEventail de bananier").setLore("§7Vous permet de cumulé la nature de chakra des joueurs tués avec la votre").toItemStack();
    private final ItemStack MissionItem = new ItemBuilder(Material.NETHER_STAR).setName("§aMission").setLore("§7Vous permet en ayant cibler un joueur de lui donner une mission").toItemStack();
    public Kinkaku(Player player, GameState.Roles roles, GameState gameState) {
        super(player, roles, gameState);
        owner.sendMessage(Desc());
    }

    @Override
    public void GiveItems() {
        super.GiveItems();
        super.giveItem(owner, false, getItems());
    }

    @Override
    public String[] Desc() {
        return new String[]{

        };
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
                KyubiItem,
                EventailItem
        };
    }

    @Override
    public void resetCooldown() {
        cdKyubi = 0;
    }

    @Override
    public void Update(GameState gameState) {
        super.Update(gameState);
        if (!gameState.nightTime && cdKyubi <= 12*60){
            super.givePotionEffet(PotionEffectType.SPEED, 60, 1, true);
        }
        if (cdKyubi >= 0) {
            cdKyubi--;
            if (cdKyubi == 0) {
                owner.sendMessage("§7Vous pouvez à nouveau utiliser§6§l Kyubi§7.");
            }
        }
    }

    @Override
    public void OnAPlayerDie(Player player, GameState gameState, Entity killer) {
        super.OnAPlayerDie(player, gameState, killer);
        if (owner != null && killer.getUniqueId().equals(owner.getUniqueId())){
            if (owner.getItemInHand().isSimilar(EventailItem)){
                if (getPlayerRoles(player).hasChakras() && !getPlayerRoles(player).getChakras().getChakra().getList().contains(owner.getUniqueId())){
                    getPlayerRoles(player).getChakras().getChakra().getList().add(owner.getUniqueId());
                    owner.sendMessage("En tuant§c "+player.getDisplayName()+"§f vous avez obtenue sa nature de Chakra: "+getPlayerRoles(player).getChakras().getShowedName());
                }
            }
        }
    }
    @Override
    public boolean ItemUse(ItemStack item, GameState gameState) {
        if (item.isSimilar(KyubiItem)) {
            if (cdKyubi <= 0) {
                owner.sendMessage("§7Activation de§6 Kyubi");
                cdKyubi = 60*15;
                new BukkitRunnable() {
                    private int time = 60;
                    private int state = 3;
                    @Override
                    public void run() {
                        if (owner == null) {
                            cancel();
                            return;
                        }
                        if (gameState.getServerState() != GameState.ServerStates.InGame) {
                            cancel();
                            return;
                        }
                        if (state == 0) {
                            owner.sendMessage("§7L'utilisation du chakra de§6 Kyubi§7 est maintenant§c terminer§7.");
                            cancel();
                            return;
                        }
                        if (state == 3) {
                            givePotionEffet(PotionEffectType.SPEED, 60, 2, true);
                            givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
                        }
                        if (state == 2) {
                            givePotionEffet(PotionEffectType.SPEED, 60, 1, true);
                            givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
                        }
                        if (state == 1) {
                            givePotionEffet(PotionEffectType.SPEED, 60, 1, true);
                        }
                        if (time == 0) {
                            state--;
                            time = 60;
                        }
                        sendCustomActionBar(owner, "Temp avant prochain stade de§6 Kyubi§f:§c "+ StringUtils.secondsTowardsBeautiful(time));
                        time--;
                    }
                }.runTaskTimer(Main.getInstance(), 0, 20);
            } else {
                sendCooldown(owner, cdKyubi);
                return true;
            }
        }
        return super.ItemUse(item, gameState);
    }
    private static class KinkakuMissions implements Listener {
        private final UUID user;
        private final UUID target;
        private KinkakuMissions(UUID user, UUID target){
            this.user = user;
            this.target = target;
        }
        @Getter
        private enum Missions {
            Crits("Vous infligez 3 coups critique");
            private final String mission;
            Missions(String mission){
                this.mission = mission;
            }
        }
        @EventHandler
        private void onPlayerEat(PlayerItemConsumeEvent e){

        }
    }
}