package net.craftgalaxy.manhunt.minigame.impl;

import net.craftgalaxy.manhunt.minigame.types.AbstractSurvivalManhunt;
import org.bukkit.*;

public final class SurvivalManhunt extends AbstractSurvivalManhunt {

	public SurvivalManhunt(int gameKey, Location lobby) {
		super(gameKey, lobby, false, false);
	}

	/**
	 * Attempts to generate new worlds for the Manhunt game. If an error occurs while
	 * loading up one or more worlds, the method returns false.
	 *
	 * @return  True if all worlds were loaded successfully, false otherwise.
	 */
	@Override
	public boolean createWorlds() {
		if (worlds.size() == 1) {
			return true;
		}

		World world = new WorldCreator(this.getWorldName(World.Environment.NORMAL)).environment(World.Environment.NORMAL).createWorld();
		if (world != null) {
			world.getWorldBorder().setCenter(world.getSpawnLocation());
			world.getWorldBorder().setSize(1000.0D);
			world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
			world.setDifficulty(Difficulty.NORMAL);
			world.setKeepSpawnInMemory(false);
			world.setAutoSave(false);
			this.worlds.put(World.Environment.NORMAL, world);
		}

		return this.worlds.size() == 1;
	}
}
