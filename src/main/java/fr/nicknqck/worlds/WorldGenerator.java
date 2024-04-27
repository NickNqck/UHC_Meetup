package fr.nicknqck.worlds;

import java.util.Objects;

import lombok.Getter;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.Listener;

import fr.nicknqck.GameState;

@Getter
public class WorldGenerator implements Listener{

	private GameState gameState;
	public WorldGenerator(GameState gameState) {
		this.gameState = gameState;
	}

	public static void CreateWorld(String name) {
		WorldCreator world = new WorldCreator(name);
		world.generateStructures(false);
	}
	 public void initWorldsRules(World world){
	        if (Objects.nonNull(world)) {
	            world.setTime(6000);
	            world.setGameRuleValue("doMobSpawning", "false");
	            world.setGameRuleValue("doDaylightCycle", "false");
	            world.setGameRuleValue("spectatorsGenerateChunks", "true");
	            world.setGameRuleValue("naturalRegeneration", "false");
	            world.setGameRuleValue("announceAdvancements", "false");
	            world.setDifficulty(Difficulty.HARD);
	            world.setSpawnLocation(0, 64, 0);
	            world.setStorm(false);
	            world.setThundering(false);
	        }
	 }
}