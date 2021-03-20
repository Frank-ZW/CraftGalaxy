package net.craftgalaxy.lockout.challenge.types;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;

public abstract class TNTPrimeChallenge extends AbstractChallenge<TNTPrimeEvent> {

	public TNTPrimeChallenge(LockOut lockOut) {
		super(lockOut);
	}
}
