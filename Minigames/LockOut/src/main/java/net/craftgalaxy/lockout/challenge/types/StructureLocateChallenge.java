package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import net.craftgalaxy.minigameservice.bukkit.util.PlayerUtil;
import net.minecraft.server.v1_16_R1.StructureGenerator;
import org.bukkit.StructureType;
import org.bukkit.entity.Player;

public abstract class StructureLocateChallenge extends AbstractChallenge<Player> {

	private final StructureType structureType;
	private final StructureGenerator<?> structureGenerator;

	public StructureLocateChallenge(LockOut lockOut, StructureType structureType, StructureGenerator<?> structureGenerator) {
		super(lockOut);
		this.structureType = structureType;
		this.structureGenerator = structureGenerator;
	}

	@Override
	public boolean handleEvent(Player player) {
		if (PlayerUtil.isInsideStructure(player, this.structureType, this.structureGenerator)) {
			this.lockOut.completeChallenge(player, this);
			return true;
		}

		return false;
	}
}
