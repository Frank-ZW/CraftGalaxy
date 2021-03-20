package net.craftgalaxy.lockout.challenge.impl.structure;

import net.craftgalaxy.lockout.challenge.types.StructureLocateChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import net.minecraft.server.v1_16_R1.StructureGenerator;
import org.bukkit.StructureType;

public class ChallengeLocateDesertPyramid extends StructureLocateChallenge {

	public ChallengeLocateDesertPyramid(LockOut lockOut) {
		super(lockOut, StructureType.DESERT_PYRAMID, StructureGenerator.DESERT_PYRAMID);
	}

	@Override
	public String getDisplayMessage() {
		return "Locate a Desert Temple!";
	}
}
