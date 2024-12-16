package fr.nicknqck.pregen;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nicknqck.Main;
import fr.nicknqck.utils.packets.NMSPacket;

public class PregenerationTask extends BukkitRunnable {
	
	private Double percent;
	private Double currentChunkLoad;
	private final Double totalChunkToLoad;
	
	private Integer cx;
	private Integer cz;
	private final Integer mx;
	private final Integer mz;
	private final Integer radius;
	private final World world;
	private boolean finished;
	private int loadedChunks;
	private int chunkAlreadyLoad;
	private int chunkFinded;
	public PregenerationTask(World world, Integer r) {
		r+=150;
		this.percent = 0.0D;
		this.totalChunkToLoad = Math.pow(r, 2.0D) / 64.0D;
		this.currentChunkLoad = 0.0D;
		this.cx = world.getSpawnLocation().getBlockX() - r;
		this.cz = world.getSpawnLocation().getBlockZ() - r;
		this.world = world;
		this.radius = r;
		this.mx = r + world.getSpawnLocation().getBlockX();
		this.mz = r + world.getSpawnLocation().getBlockZ();
		this.finished = false;
		Bukkit.broadcastMessage("§8[§cPregen§8] §fLancement de la pré-génération de §c"+world.getName());
		runTaskTimer(Main.getInstance(), 0L, 5L);
	}
	
	@Override
	public void run() {
		for(int i = 0; i < 30 && !this.finished; i++) {
			Location loc = new Location(this.world, this.cx, 0.0D, this.cz);
			if (!loc.getChunk().isLoaded()) {
				loc.getWorld().loadChunk(loc.getChunk().getX(), loc.getChunk().getZ(), true);
				loadedChunks++;
			} else {
				chunkAlreadyLoad++;
			}
			chunkFinded++;
			this.cx+=16;
			this.currentChunkLoad = this.currentChunkLoad + 1.0D;
			if(this.cx > this.mx) {
				this.cx = world.getSpawnLocation().getBlockX() - this.radius;
				this.cz+=16;
				if(this.cz > this.mz) {
					this.currentChunkLoad = this.totalChunkToLoad;
					this.finished = true;
				}
			}
		}
		this.percent = this.currentChunkLoad / this.totalChunkToLoad * 100.0D;
		for (Player p : Bukkit.getOnlinePlayers()) {
			NMSPacket.sendActionBar(p, "§fPré-génération de §c"+world.getName()+" §8» §c"+this.percent.intValue()+"%");
		}
		if(this.finished) {
			Bukkit.broadcastMessage("§8[§cPregen§8] §fLa pré-génération de§c "+world.getName()+"§f est terminée !");
			cancel();
			Bukkit.broadcastMessage("§c"+chunkFinded+"§7 on essayer d'être généré, §c"+chunkAlreadyLoad+"§7 l'était déjà et §c"+loadedChunks+"§7 on été pregen");
			//Bukkit.getOnlinePlayers().forEach(players -> NMSMethod.sendActionbar(players, "§fPré-génération §8» [§r"+UHCAPI.get().getGameManager().getProgressBar(this.percent.intValue(), 100, 20, "|", "§c", "§f")+"§8] §c"+this.percent.intValue()+"%"));
		}
	}
}