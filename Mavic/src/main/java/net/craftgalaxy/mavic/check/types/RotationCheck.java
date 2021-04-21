package net.craftgalaxy.mavic.check.types;

import net.craftgalaxy.mavic.check.Check;
import net.craftgalaxy.mavic.data.PlayerData;
import net.craftgalaxy.mavic.util.location.PlayerLocation;

public abstract class RotationCheck extends Check {

	public RotationCheck(PlayerData playerData, String name, int maxViolations, CheckType checkType) {
		super(playerData, name, maxViolations, checkType);
	}

	public abstract void handle(PlayerLocation to, PlayerLocation from, long timestamp);
}
