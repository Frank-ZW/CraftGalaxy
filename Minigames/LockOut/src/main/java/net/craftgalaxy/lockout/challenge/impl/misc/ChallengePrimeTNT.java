package net.craftgalaxy.lockout.challenge.impl.misc;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import net.craftgalaxy.lockout.challenge.types.TNTPrimeChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.entity.Player;

public class ChallengePrimeTNT extends TNTPrimeChallenge {

	public ChallengePrimeTNT(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public boolean handle(TNTPrimeEvent e) {
		if (e.getPrimerEntity() instanceof Player) {
			this.lockOut.completeChallenge((Player) e.getPrimerEntity(), this);
			return true;
		}

		return false;
	}

	@Override
	public String getDisplayMessage() {
		return "Detonate a block of TNT!";
	}
}
