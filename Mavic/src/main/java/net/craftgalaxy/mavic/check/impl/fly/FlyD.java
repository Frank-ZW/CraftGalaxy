package net.craftgalaxy.mavic.check.impl.fly;

import net.craftgalaxy.mavic.check.types.PositionCheck;
import net.craftgalaxy.mavic.data.PlayerData;
import net.craftgalaxy.mavic.util.location.PlayerLocation;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FlyD extends PositionCheck {

	public FlyD(PlayerData playerData) {
		super(playerData, "Fly D", 24, CheckType.DEVELOPMENT);
	}

	@Override
	public void handle(PlayerLocation to, PlayerLocation from, long timestamp) {
		if (!this.playerData.isAllowedFlight() && !this.playerData.isInWeb() && this.playerData.getLiquidTicks() >= 40 && !this.playerData.isInVehicle() && !this.playerData.isOnGround() && !this.playerData.isInClimable() && to.getY() > from.getY()) {
			float limit = 2.0F;
			double distY = to.getY() - this.playerData.getLastGroundY();
			PotionEffect potionEffect = this.player.getPotionEffect(PotionEffectType.JUMP);
			if (potionEffect != null) {
				int amplitude = potionEffect.getAmplifier() + 1;
				limit += Math.pow(amplitude + 4.2D, 2.0D) / 16.0F;
			}

			if (distY > limit) {
				this.handleViolation();
			} else {
				this.decreaseViolation(0.025D);
			}
		}
	}
}
