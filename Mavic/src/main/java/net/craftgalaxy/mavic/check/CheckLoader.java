package net.craftgalaxy.mavic.check;

import net.craftgalaxy.mavic.check.impl.aura.KillAuraA;
import net.craftgalaxy.mavic.check.impl.aura.KillAuraB;
import net.craftgalaxy.mavic.check.impl.fly.FlyA;
import net.craftgalaxy.mavic.check.impl.fly.FlyB;
import net.craftgalaxy.mavic.check.impl.fly.FlyC;
import net.craftgalaxy.mavic.check.impl.fly.FlyD;
import net.craftgalaxy.mavic.check.impl.inventory.InventoryA;
import net.craftgalaxy.mavic.check.impl.inventory.InventoryB;
import net.craftgalaxy.mavic.check.impl.nofall.NoFallA;
import net.craftgalaxy.mavic.check.types.PacketCheck;
import net.craftgalaxy.mavic.check.types.PositionCheck;
import net.craftgalaxy.mavic.check.types.RotationCheck;
import net.craftgalaxy.mavic.data.PlayerData;

import java.util.*;

public final class CheckLoader {

	private static final List<Class<? extends Check>> commands = Arrays.asList(
			FlyA.class, FlyB.class, FlyC.class, FlyD.class,
			KillAuraA.class, KillAuraB.class,
			NoFallA.class,
			InventoryA.class, InventoryB.class
	);

	private final Map<String, Check> checkMap = new HashMap<>();
	private final Set<PacketCheck> packetChecks = new HashSet<>();
	private final Set<PositionCheck> positionChecks = new HashSet<>();
	private final Set<RotationCheck> rotationChecks = new HashSet<>();

	public CheckLoader(PlayerData playerData) {
		try {
			for (Class<? extends Check> clazz : commands) {
				Check check = clazz.asSubclass(Check.class).getConstructor(PlayerData.class).newInstance(playerData);
				if (check instanceof PacketCheck) {
					this.packetChecks.add((PacketCheck) check);
				} else if (check instanceof PositionCheck) {
					this.positionChecks.add((PositionCheck) check);
				} else if (check instanceof RotationCheck) {
					this.rotationChecks.add((RotationCheck) check);
				}

				this.checkMap.put(check.getRawName(), check);
			}
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}

	public Set<PacketCheck> getPacketChecks() {
		return this.packetChecks;
	}

	public Set<PositionCheck> getPositionChecks() {
		return this.positionChecks;
	}

	public Set<RotationCheck> getRotationChecks() {
		return this.rotationChecks;
	}

	/**
	 * @param rawName   The raw name of the check to be enabled.
	 * @return          True if the check was successfully enabled, false otherwise.
	 */
	public boolean enableCheck(String rawName) {
		Check check = this.checkMap.get(rawName);
		if (check != null) {
			check.setEnabled(true);
			this.checkMap.put(rawName, check);
			return true;
		}

		return false;
	}

	/**
	 * @param rawName   The raw name of the check to be disabled.
	 * @return          True if the check was successfully disabled, false otherwise.
	 */
	public boolean disableCheck(String rawName) {
		Check check = this.checkMap.get(rawName);
		if (check != null) {
			check.setEnabled(false);
			this.checkMap.put(rawName, check);
			return true;
		}

		return false;
	}
}
