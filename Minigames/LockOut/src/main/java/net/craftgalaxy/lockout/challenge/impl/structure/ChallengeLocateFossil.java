package net.craftgalaxy.lockout.challenge.impl.structure;

import net.craftgalaxy.lockout.challenge.types.StructureLocateChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import net.minecraft.server.v1_16_R1.StructureGenerator;
import org.bukkit.StructureType;

public class ChallengeLocateFossil extends StructureLocateChallenge {

	public ChallengeLocateFossil(LockOut lockOut) {
		super(lockOut, StructureType.NETHER_FOSSIL, StructureGenerator.NETHER_FOSSIL);
	}

	@Override
	public String getDisplayMessage() {
		return "Locate a Nether Fossil!";
	}
}
