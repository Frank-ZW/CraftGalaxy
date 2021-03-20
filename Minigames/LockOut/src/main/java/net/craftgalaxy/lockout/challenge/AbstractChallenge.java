package net.craftgalaxy.lockout.challenge;

import net.craftgalaxy.lockout.minigame.LockOut;

public abstract class AbstractChallenge<T> implements IChallenge<T> {

	protected LockOut lockOut;
	protected boolean completed;

	public AbstractChallenge(LockOut lockOut) {
		this.lockOut = lockOut;
		this.completed = false;
	}

	public void reset() {
		this.lockOut = null;
		this.completed = false;
	}

	public boolean isCompleted() {
		return this.completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
}
