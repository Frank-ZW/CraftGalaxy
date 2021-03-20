package net.craftgalaxy.lockout.challenge.impl.structure;

import net.craftgalaxy.lockout.challenge.types.StructureLocateChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import net.minecraft.server.v1_16_R1.StructureGenerator;
import org.bukkit.StructureType;

public class ChallengeLocateBastion extends StructureLocateChallenge {

	public ChallengeLocateBastion(LockOut lockOut) {
		super(lockOut, StructureType.BASTION_REMNANT, StructureGenerator.BASTION_REMNANT);
	}

	@Override
	public String getDisplayMessage() {
		return "Locate a Bastion!";
	}
}
