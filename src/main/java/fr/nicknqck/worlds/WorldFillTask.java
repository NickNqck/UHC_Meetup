package fr.nicknqck.worlds;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

import fr.nicknqck.utils.NMSPacket;
import fr.nicknqck.utils.StringUtils;

public class WorldFillTask implements Runnable {
    private Server server = Bukkit.getServer();
    private final World world;
    private final BorderData border;
    private final WorldFileData worldData;
    private boolean readyToGo = false;
    private boolean paused = false;
    private boolean pausedForMemory = false;
    private int taskID = -1;
    private final int chunksPerRun;
    private int x = 0;
    private int z = 0;
    private boolean isZLeg = false;
    private boolean isNeg = false;
    private int length = -1;
    private float current = 0.0F;
    private boolean insideBorder = true;
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private final List<CoordXZ> storedChunks = new LinkedList();
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private final Set<CoordXZ> originalChunks = new HashSet();
    private final CoordXZ lastChunk = new CoordXZ(0, 0);
    private long lastReport = System.currentTimeMillis();
    private long lastAutoSave = System.currentTimeMillis();
    private int reportTarget = 0;
    private int reportTotal = 0;
    private int reportNum = 0;
    public static boolean finish = false;

    public WorldFillTask(World world, int chunksPerRun, int radius) {
        this.chunksPerRun = chunksPerRun;
        this.world = world;
        Location spawn = this.world.getSpawnLocation();
        this.border = new BorderData(spawn.getX(), spawn.getZ(), radius, radius);
        this.worldData = WorldFileData.create(this.world);
        if (this.worldData == null) {
            this.stop();
        } else {
            this.x = CoordXZ.blockToChunk((int)this.border.getX());
            this.z = CoordXZ.blockToChunk((int)this.border.getZ());
            int chunkWidthX = (int)Math.ceil((double)((this.border.getRadiusX() + 16) * 2) / 16.0D);
            int chunkWidthZ = (int)Math.ceil((double)((this.border.getRadiusZ() + 16) * 2) / 16.0D);
            int biggerWidth = Math.max(chunkWidthX, chunkWidthZ);
            this.reportTarget = biggerWidth * biggerWidth + biggerWidth + 1;
            Chunk[] originals = this.world.getLoadedChunks();
            Chunk[] var9 = originals;
            int var10 = originals.length;

            for(int var11 = 0; var11 < var10; ++var11) {
                Chunk original = var9[var11];
                this.originalChunks.add(new CoordXZ(original.getX(), original.getZ()));
            }

            this.readyToGo = true;
        }
    }

    public void setTaskID(int ID) {
        if (ID == -1) {
            this.stop();
        }

        this.taskID = ID;
    }

    public void run() {
        @SuppressWarnings({ "rawtypes" })
		Iterator var1 = Bukkit.getOnlinePlayers().iterator();

        while(var1.hasNext()) {
            Player player = (Player)var1.next();
            DecimalFormat df = new DecimalFormat("0.00");
            NMSPacket.sendActionBarPregen(player,  "§7" + df.format(getPercentageCompleted()) + "% §b["  + StringUtils.getProgressBar((int)this.getPercentageCompleted(), 100, 20, '|', ChatColor.YELLOW, ChatColor.GRAY) + "§b]");
        }

        if (this.pausedForMemory) {
            if (this.AvailableMemoryTooLow()) {
                return;
            }

            this.pausedForMemory = false;
            this.readyToGo = true;
            this.sendMessage("Available memory is sufficient, automatically continuing.");
        }

        if (this.server != null && this.readyToGo && !this.paused) {
            this.readyToGo = false;
            long loopStartTime = System.currentTimeMillis();

            for(int loop = 0; loop < this.chunksPerRun; ++loop) {
                if (this.paused || this.pausedForMemory) {
                    return;
                }

                long now = System.currentTimeMillis();
                if (now > this.lastReport + 5000L) {
                    this.reportProgress();
                }

                if (now > loopStartTime + 45L) {
                    this.readyToGo = true;
                    return;
                }

                while(!this.border.insideBorder((double)(CoordXZ.chunkToBlock(this.x) + 8), (double)(CoordXZ.chunkToBlock(this.z) + 8))) {
                    if (this.cannotMoveToNext()) {
                        return;
                    }
                }

                this.insideBorder = true;

                while(this.worldData.isChunkFullyGenerated(this.x, this.z)) {
                    this.insideBorder = true;
                    if (this.cannotMoveToNext()) {
                        return;
                    }
                }

                this.world.loadChunk(this.x, this.z, true);
                this.worldData.chunkExistsNow(this.x, this.z);
                int popX = !this.isZLeg ? this.x : this.x + (this.isNeg ? -1 : 1);
                int popZ = this.isZLeg ? this.z : this.z + (!this.isNeg ? -1 : 1);
                this.world.loadChunk(popX, popZ, false);
                if (!this.storedChunks.contains(this.lastChunk) && !this.originalChunks.contains(this.lastChunk)) {
                    this.world.loadChunk(this.lastChunk.x, this.lastChunk.z, false);
                    this.storedChunks.add(new CoordXZ(this.lastChunk.x, this.lastChunk.z));
                }

                this.storedChunks.add(new CoordXZ(popX, popZ));
                this.storedChunks.add(new CoordXZ(this.x, this.z));

                while(this.storedChunks.size() > 8) {
                    CoordXZ cord = (CoordXZ)this.storedChunks.remove(0);
                    if (!this.originalChunks.contains(cord)) {
                        this.world.unloadChunkRequest(cord.x, cord.z);
                    }
                }

                if (this.cannotMoveToNext()) {
                    return;
                }
            }

            this.readyToGo = true;
        }
    }

    public boolean cannotMoveToNext() {
        if (!this.paused && !this.pausedForMemory) {
            ++this.reportNum;
            if (this.current < (float)this.length) {
                ++this.current;
            } else {
                this.current = 0.0F;
                this.isZLeg ^= true;
                if (this.isZLeg) {
                    this.isNeg ^= true;
                    ++this.length;
                }
            }

            this.lastChunk.x = this.x;
            this.lastChunk.z = this.z;
            if (this.isZLeg) {
                this.z += this.isNeg ? -1 : 1;
            } else {
                this.x += this.isNeg ? -1 : 1;
            }

            if (this.isZLeg && this.isNeg && this.current == 0.0F) {
                if (!this.insideBorder) {
                    this.finish();
                    return true;
                }

                this.insideBorder = false;
            }

            return false;
        } else {
            return true;
        }
    }

	public void finish() {
        this.paused = true;
        finish = true;
        this.reportProgress();
        this.world.save();
        this.sendMessage("task successfully completed for world \"" + this.refWorld() + "\"!");
        this.stop();
    }

    public void stop() {
        if (this.server != null) {
            this.readyToGo = false;
            if (this.taskID != -1) {
                this.server.getScheduler().cancelTask(this.taskID);
            }

            this.server = null;

            while(!this.storedChunks.isEmpty()) {
                CoordXZ cord = (CoordXZ)this.storedChunks.remove(0);
                if (!this.originalChunks.contains(cord)) {
                    this.world.unloadChunkRequest(cord.x, cord.z);
                }
            }

        }
    }

    private void reportProgress() {
        this.lastReport = System.currentTimeMillis();
        double percentage = this.getPercentageCompleted();
        if (percentage > 100.0D) {
            percentage = 100.0D;
        }

        this.sendMessage(this.reportNum + " more chunks processed (" + (this.reportTotal + this.reportNum) + " total, ~" + (new DecimalFormat("0.0")).format(percentage) + "%)");
        this.reportTotal += this.reportNum;
        this.reportNum = 0;
        int fillAutoSaveFrequency = 30;
        if (this.lastAutoSave + (long)(fillAutoSaveFrequency * 1000) < this.lastReport) {
            this.lastAutoSave = this.lastReport;
            this.sendMessage("Saving the world to disk, just to be on the safe side.");
            this.world.save();
        }

    }

    private void sendMessage(String text) {
        int availMem = this.AvailableMemory();
        Bukkit.getLogger().info("[Fill] " + text + " (free mem: " + availMem + " MB)");
        if (availMem < 200) {
            this.pausedForMemory = true;
            text = "Available memory is very low, task is pausing. A cleanup will be attempted now, and the task will automatically continue if/when sufficient memory is freed up.\n Alternatively, if you restart the server, this task will automatically continue once the server is back up.";
            Bukkit.getLogger().info("[Fill] " + text);
            System.gc();
        }

    }

    public String refWorld() {
        return this.world.getName();
    }

    public double getPercentageCompleted() {
        return finish ? 100.0D : Math.min(100.0D, (double)((float)(this.reportTotal + this.reportNum) / (float)this.reportTarget) * 100.0D);
    }
    public void cancel(){
        this.stop();
    }
    public int AvailableMemory() {
        Runtime rt = Runtime.getRuntime();
        return (int)((rt.maxMemory() - rt.totalMemory() + rt.freeMemory()) / 1048576L);
    }

    public boolean AvailableMemoryTooLow() {
        return this.AvailableMemory() < 500;
    }
}
