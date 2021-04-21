package net.craftgalaxy.mavic.check.impl.nofall;

import net.craftgalaxy.mavic.check.types.PositionCheck;
import net.craftgalaxy.mavic.data.PlayerData;
import net.craftgalaxy.mavic.util.location.PlayerLocation;
import org.bukkit.Bukkit;

public final class NoFallA extends PositionCheck {

	public NoFallA(PlayerData playerData) {
		super(playerData, "No Fall A", 24, CheckType.RELEASE);
	}

	@Override
	public void handle(PlayerLocation to, PlayerLocation from, long timestamp) {
		Bukkit.broadcastMessage(this.playerData.isOnGround() + ", " + to.isOnGround() + ", " + from.isOnGround());
		if (!playerData.isOnGround() && to.isOnGround() && from.isOnGround()) {
			this.handleViolation();
		}
	}
}
