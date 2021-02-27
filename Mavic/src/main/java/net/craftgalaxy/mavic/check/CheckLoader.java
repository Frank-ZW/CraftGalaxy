package net.craftgalaxy.mavic.check;

import net.craftgalaxy.mavic.check.impl.aura.KillAuraA;
import net.craftgalaxy.mavic.check.impl.fly.FlyA;
import net.craftgalaxy.mavic.check.impl.fly.FlyB;
import net.craftgalaxy.mavic.check.impl.fly.FlyC;
import net.craftgalaxy.mavic.check.impl.fly.FlyD;
import net.craftgalaxy.mavic.check.impl.nofall.NoFallA;
import net.craftgalaxy.mavic.check.types.PacketCheck;
import net.craftgalaxy.mavic.check.types.PositionCheck;
import net.craftgalaxy.mavic.check.types.RotationCheck;

import java.util.*;

public final class CheckLoader {

	private final Map<String, Check> checkMap = new HashMap<>();
	private final Set<PacketCheck> packetChecks = new HashSet<>();
	private final Set<PositionCheck> positionChecks = new HashSet<>();
	private final Set<RotationCheck> rotationChecks = new HashSet<>();
	private static CheckLoader instance;

	public CheckLoader() {
		List<Class<? extends Check>> commands = Arrays.asList(
				FlyA.class,
				FlyB.class,
				FlyC.class,
				FlyD.class,
				KillAuraA.class,
				NoFallA.class
		);

		try {
			for (Class<? extends Check> clazz : commands) {
				Check check = clazz.asSubclass(Check.class).getConstructor().newInstance();
				if (check instanceof PacketCheck) {
					this.packetChecks.add((PacketCheck) check);
				} else if (check instanceof PositionCheck) {
					this.positionChecks.add((PositionCheck) check);
				} else if (check instanceof RotationCheck) {
					this.rotationChecks.add((RotationCheck) check);
				}

				this.checkMap.put(check.getName(), check);
			}
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}

	public static void enable() {
		instance = new CheckLoader();
	}

	public static CheckLoader getInstance() {
		return instance;
	}

	public static void disable() {
		if (instance != null) {
			instance.packetChecks.clear();
			instance.positionChecks.clear();
			instance.rotationChecks.clear();
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

	public boolean enableCheck(String name) {
		Check check = this.checkMap.get(name);
		if (check != null) {
			check.setEnabled(true);
			this.checkMap.put(name, check);
			return true;
		}

		return false;
	}

	public boolean disableCheck(String name) {
		Check check = this.checkMap.get(name);
		if (check != null) {
			check.setEnabled(false);
			this.checkMap.put(name, check);
			return true;
		}

		return false;
	}
}
