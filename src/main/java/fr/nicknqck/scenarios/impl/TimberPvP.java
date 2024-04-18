package fr.nicknqck.scenarios.impl;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.scenarios.BasicScenarios;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.zephyr.SchedulerRunnable;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TimberPvP extends BasicScenarios implements Listener {

    private boolean isActivated = false;
    @Override
    public String getName() {
        return "§f§rTimberPvP";
    }

    @Override
    public ItemStack getAffichedItem() {
        return new ItemBuilder(Material.LOG).setName(getName()).setLore("§r§fLe "+getName()+" est actuellement: "+(isActivated ? "§aActivé" : "§cDésactivé"),"", AllDesc.tab+"§7 Permet de casser des arbres plus facilement").toItemStack();
    }

    @Override
    public void onClick(Player player) {
        if (isClickGauche()){
            isActivated = true;
            EventUtils.registerEvents(this, Main.getInstance());
        }
        if (isClickDroit()){
            isActivated = false;
            EventUtils.unregisterEvents(this);
        }
    }

    @EventHandler
    private void onBreak(BlockBreakEvent event) {
        if (GameState.getInstance().getInGameTime() > GameState.getInstance().getPvPTimer()) return;
        Player player = event.getPlayer();
        Material mat = event.getBlock().getType();
        if (mat.equals(Material.LOG_2) || mat.equals(Material.LOG)) {
            List<Block> bList = new ArrayList<>();
            List<ItemStack> finalItems = new ArrayList<>();
            bList.add(event.getBlock());
            SchedulerRunnable.CreateTimerScheduler(Main.getInstance(), 0, new fr.nicknqck.utils.SchedulerRunnable.IScheduler() {
                @Override
                public void run() {
                    for (int i = 0; i < bList.size(); ++i) {
                        Block block = bList.get(i);

                        if (block.getType().equals(Material.LOG_2)  || block.getType().equals(Material.LOG)) {
                            List<ItemStack> items = new ArrayList<>(block.getDrops());
                            block.setType(Material.AIR);
                            finalItems.addAll(items);
                        }
                        BlockFace[] values;
                        for (int length = (values = BlockFace.values()).length, j = 0; j < length; ++j) {
                            BlockFace face = values[j];
                            if (block.getRelative(face).getType().equals(Material.LOG_2)  || block.getRelative(face).getType().equals(Material.LOG))
                                bList.add(block.getRelative(face));

                        }
                        bList.remove(block);
                    }
                    if (bList.isEmpty()) {
                        for (ItemStack item2 : finalItems)
                            player.getWorld().dropItemNaturally(event.getBlock().getLocation(), item2);

                        SchedulerRunnable.cancel(this);
                    }
                }
            });
        }
    }
}
