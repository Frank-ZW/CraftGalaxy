package net.craftgalaxy.mavic.check.types;

import net.craftgalaxy.mavic.check.Check;
import net.craftgalaxy.mavic.data.PlayerData;
import net.craftgalaxy.mavic.util.location.PlayerLocation;

public abstract class PositionCheck extends Check {

	public PositionCheck(String name, int maxViolations, CheckType checkType) {
		super(name, maxViolations, checkType);
	}

	public abstract void handle(PlayerData playerData, PlayerLocation to, PlayerLocation from, long timestamp);
}
