package net.craftgalaxy.lockout.challenge.impl.structure;

import net.craftgalaxy.lockout.challenge.types.StructureLocateChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import net.minecraft.server.v1_16_R1.StructureGenerator;
import org.bukkit.StructureType;

public class ChallengeLocateEndCity extends StructureLocateChallenge {

	public ChallengeLocateEndCity(LockOut lockOut) {
		super(lockOut, StructureType.END_CITY, StructureGenerator.ENDCITY);
	}

	@Override
	public String getDisplayMessage() {
		return "Locate an End City!";
	}
}
