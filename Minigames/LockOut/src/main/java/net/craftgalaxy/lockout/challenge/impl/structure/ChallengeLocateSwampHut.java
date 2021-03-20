package net.craftgalaxy.lockout.challenge.impl.structure;

import net.craftgalaxy.lockout.challenge.types.StructureLocateChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import net.minecraft.server.v1_16_R1.StructureGenerator;
import org.bukkit.StructureType;

public class ChallengeLocateSwampHut extends StructureLocateChallenge {

	public ChallengeLocateSwampHut(LockOut lockOut) {
		super(lockOut, StructureType.SWAMP_HUT, StructureGenerator.SWAMP_HUT);
	}

	@Override
	public String getDisplayMessage() {
		return "Locate a Swamp Hut!";
	}
}
