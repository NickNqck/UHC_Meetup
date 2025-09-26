package fr.nicknqck.entity.krystalbeast;

import fr.nicknqck.Border;
import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

@Setter
public abstract class Beast implements IBeast{

    @Getter
    private int minTiming= 60;
    @Getter
    private int maxTiming= 120;
    private UUID uuid;
    private int timingProc = 90;//90 En valeur par défaut
    @Getter
    private final AntiMooveRunnable antiMooveRunnable;
    private boolean hasSpawn = false;

    protected Beast() {
        this.uuid = UUID.randomUUID();
        this.antiMooveRunnable = new AntiMooveRunnable(this);
        calculeProc();
    }

    public ItemStack getItemStack() {
        return getItemBuilder().setLore(getLore()).setAmount(Math.max(Main.getInstance().getKrystalBeastManager().getAmountReady(this.getClass()), 1)).toItemStack();
    }

    @Override
    public boolean checkCanSpawn() {
        if (getOriginSpawn() == null) {
            System.out.println("[BeastCheckSpawnManager] can't spawn "+this+" because getOriginSpawn() is null");
            return false;
        }
        if (getName() == null) {
            System.out.println("[BeastCheckSpawnManager] can't spawn "+this+" because getName() is null");
            return false;
        }
        if (getName().isEmpty()) {
            System.out.println("[BeastCheckSpawnManager] can't spawn "+this+" because getName() is Empty");
            return false;
        }
      /*  if (GameState.getInstance().getInGameTime() != getTimingProc()) {
            System.out.println("[BeastCheckSpawnManager] can't spawn "+getName()+" because InGameTime != getTimingProc(), ("+GameState.getInstance().getInGameTime()+"/"+getTimingProc()+")");
            return false;
        }*/
        System.out.println("[BeastCheckSpawnManager] "+getName()+" gonna spawn at "+getOriginSpawn());
        return spawn();
    }

    @Override
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public final int getTimingProc() {
        return this.timingProc;
    }

    protected String[] getLore() {
        return new String[] {
                "§fRang:§c "+getBeastRank().name(),
                "",
                "§fTemp minimal d'apparition:§c "+StringUtils.secondsTowardsBeautiful(getMinTiming()),
                "§fTemp maximal d'apparition:§c "+StringUtils.secondsTowardsBeautiful(getMaxTiming()),
                "",
                "Nombre d'exemplaire: "+Main.getInstance().getKrystalBeastManager().getAmountReady(this.getClass())
        };
    }

    public void addMaxTiming(int i) {
        if (this.maxTiming > this.minTiming+15) {
            this.maxTiming+=i;
        }
        if (this.maxTiming < 30) {
            this.maxTiming = 30;
        }
    }

    public void addMinTiming(int i) {
        if (this.minTiming+i < this.maxTiming && this.minTiming+i > 15) {
            this.minTiming+=i;
        }
    }

    public void calculeProc() {
        do {
            this.timingProc = Main.RANDOM.nextInt((getMaxTiming() - getMinTiming() +1)+getMinTiming());
        } while (this.timingProc == 90 || this.timingProc <= this.getMinTiming() || this.timingProc >= this.getMaxTiming());
    }

    @Override
    public boolean hasSpawn() {
        return this.hasSpawn;
    }

    @Override
    public void setHasSpawn(boolean b) {
        this.hasSpawn = b;
    }

    public static class AntiMooveRunnable extends BukkitRunnable {

        private final GameState gameState;
        private final Beast beast;
        double maxDistance = 8.0;

        public AntiMooveRunnable(Beast beast) {
            this.gameState = GameState.getInstance();
            this.beast = beast;
        }
        public void start() {
            runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
        }

        @Override
        public void run() {
            if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (this.beast.getBeast() == null)return;
            if (!this.beast.hasSpawn())return;
            final Entity entity = this.beast.getBeast();
            final Location loc = entity.getLocation();
            if (loc.getX()+10 == Border.getActualBorderSize()) {
                this.beast.getOriginSpawn().add(-1, 0, 0);
            }
            if (loc.getX()-10 == -Border.getActualBorderSize()) {
                this.beast.getOriginSpawn().add(1, 0, 0);
            }
            if (loc.getZ()+10 == Border.getActualBorderSize()) {
                this.beast.getOriginSpawn().add(0, 0, -1);
            }
            if (loc.getZ()-10 == -Border.getActualBorderSize()) {
                this.beast.getOriginSpawn().add(0, 0, 1);
            }
            if (entity.getLocation().distance(this.beast.getOriginSpawn()) > this.maxDistance) {
                entity.teleport(this.beast.getOriginSpawn());
            }
        }
    }
}