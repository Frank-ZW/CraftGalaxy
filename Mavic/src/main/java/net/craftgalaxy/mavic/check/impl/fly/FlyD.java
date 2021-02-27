package net.craftgalaxy.mavic.check.impl.fly;

import net.craftgalaxy.mavic.check.types.PositionCheck;
import net.craftgalaxy.mavic.data.PlayerData;
import net.craftgalaxy.mavic.util.location.PlayerLocation;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FlyD extends PositionCheck {

	public FlyD() {
		super("Fly D", 24, CheckType.DEVELOPMENT);
	}

	@Override
	public void handle(PlayerData playerData, PlayerLocation to, PlayerLocation from, long timestamp) {
		if (!playerData.isAllowedFlight() && !playerData.isInWeb() && playerData.getLiquidTicks() > 40 && !playerData.isInVehicle() && !playerData.isOnGround() && !playerData.isInClimable() && to.getY() > from.getY()) {
			float limit = 2.0F;
			double distY = to.getY() - playerData.getLastGroundY();
			PotionEffect effect = playerData.getPlayer().getPotionEffect(PotionEffectType.JUMP);
			if (effect != null) {
				int amplitude = effect.getAmplifier() + 1;
				limit += Math.pow(amplitude + 4.2D, 2.0) / 16.0F;
			}

			if (distY > limit) {
				this.handleViolation(playerData);
			} else {
				this.decreaseViolation(0.05D);
			}
		}
	}
}
