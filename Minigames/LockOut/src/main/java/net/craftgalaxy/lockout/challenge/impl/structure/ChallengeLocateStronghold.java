package net.craftgalaxy.lockout.challenge.impl.structure;

import net.craftgalaxy.lockout.challenge.types.StructureLocateChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import net.minecraft.server.v1_16_R1.StructureGenerator;
import org.bukkit.StructureType;

public class ChallengeLocateStronghold extends StructureLocateChallenge {

	public ChallengeLocateStronghold(LockOut lockOut) {
		super(lockOut, StructureType.STRONGHOLD, StructureGenerator.STRONGHOLD);
	}

	@Override
	public String getDisplayMessage() {
		return "Locate a Stronghold!";
	}
}
