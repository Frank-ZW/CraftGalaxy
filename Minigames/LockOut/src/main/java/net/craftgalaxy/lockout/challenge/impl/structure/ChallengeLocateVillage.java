package net.craftgalaxy.lockout.challenge.impl.structure;

import net.craftgalaxy.lockout.challenge.types.StructureLocateChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import net.minecraft.server.v1_16_R1.StructureGenerator;
import org.bukkit.StructureType;

public class ChallengeLocateVillage extends StructureLocateChallenge {

	public ChallengeLocateVillage(LockOut lockOut) {
		super(lockOut, StructureType.VILLAGE, StructureGenerator.VILLAGE);
	}

	@Override
	public String getDisplayMessage() {
		return "Locate a Village!";
	}
}
