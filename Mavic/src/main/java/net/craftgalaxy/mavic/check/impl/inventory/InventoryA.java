package net.craftgalaxy.mavic.check.impl.inventory;

import net.craftgalaxy.mavic.check.types.PacketCheck;
import net.craftgalaxy.mavic.data.PlayerData;
import net.craftgalaxy.mavic.packet.AbstractPacket;
import net.craftgalaxy.mavic.packet.impl.APacketPlayInFlying;
import net.craftgalaxy.mavic.packet.impl.APacketPlayInHeldItemSlot;
import org.bukkit.GameMode;

public class InventoryA extends PacketCheck {

	private long lastFlyingTimestamp = Long.MIN_VALUE;

	public InventoryA(PlayerData playerData) {
		super(playerData, "AutoTool A", 24, CheckType.DEVELOPMENT);
	}

	@Override
	public void handle(AbstractPacket abstractPacket, long timestamp) {
		if (abstractPacket instanceof APacketPlayInFlying) {
			if (this.lastFlyingTimestamp != Long.MIN_VALUE) {
				if (timestamp - this.lastFlyingTimestamp > 40L && this.player.getGameMode() == GameMode.SURVIVAL) {
					this.handleViolation("", 0.25D);
				}

				this.lastFlyingTimestamp = Long.MIN_VALUE;
			}
		} else if (abstractPacket instanceof APacketPlayInHeldItemSlot) {
			long lastFlyingTimestamp = this.playerData.getLastFlyingTimestamp();
			if (timestamp - lastFlyingTimestamp < 10L) {
				this.lastFlyingTimestamp = lastFlyingTimestamp;
			} else {
				this.violations -= Math.min(this.violations + 1.0D, 0.025D);
			}
		}
	}
}
