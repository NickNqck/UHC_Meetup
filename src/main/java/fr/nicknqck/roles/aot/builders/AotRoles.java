package fr.nicknqck.roles.aot.builders;

import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.RoleBase;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;

public abstract class AotRoles extends RoleBase {
    public boolean canShift = false;
    public boolean isTransformedinTitan = false;
    public double RodSpeedMultipliyer = 0;
    public double gazAmount = 0;
    public AotRoles(Player player) {
        super(player);
        gazAmount= 100.0;
        new RodCooldownRunnable(this).runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
    }
    private static class RodCooldownRunnable extends BukkitRunnable {
        private final AotRoles role;
        public RodCooldownRunnable(AotRoles role) {
            this.role = role;
        }

        @Override
        public void run() {
            if (role.actualTridiCooldown > 0) {
                role.actualTridiCooldown--;
                if (role.owner.getItemInHand().isSimilar(role.gameState.EquipementTridi())) {
                    DecimalFormat df = new DecimalFormat("0.0");
                    //	sendCustomActionBar(owner, aqua+"Gaz:§c "+df.format(gazAmount)+"%"+aqua+" Cooldown:§6 "+actualTridiCooldown+"s");
                    role.sendCustomActionBar(role.owner, "Gaz restant§8»"+role.gameState.sendGazBar(role.gazAmount, 2)+"§7("+aqua+df.format(role.gazAmount)+"%§7), Cooldown:§b "+role.cd(role.actualTridiCooldown));
                }
            }else if (role.actualTridiCooldown == 0){
                role.owner.sendMessage("§7§l"+role.gameState.EquipementTridi().getItemMeta().getDisplayName()+"§7 utilisable !");
                role.actualTridiCooldown--;
            }
            if (role.actualTridiCooldown <= 0) {
                if (role.owner.getItemInHand().isSimilar(role.gameState.EquipementTridi())) {
                    DecimalFormat df = new DecimalFormat("0.0");
                    role.sendCustomActionBar(role.owner, aqua+"Gaz:§c "+df.format(role.gazAmount)+"% "+"§7§lArc Tridimentionnel§r:§6 Utilisable");
                }
            }
        }
    }
}
