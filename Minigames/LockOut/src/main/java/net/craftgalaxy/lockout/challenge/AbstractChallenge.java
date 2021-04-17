package net.craftgalaxy.lockout.challenge;

import net.craftgalaxy.lockout.minigame.LockOut;

public abstract class AbstractChallenge<T> implements IChallenge<T> {

	protected LockOut lockOut;
	protected boolean completed;
	protected Class<T> clazz;

	public AbstractChallenge(LockOut lockOut, Class<T> clazz) {
		this.lockOut = lockOut;
		this.completed = false;
		this.clazz = clazz;
	}

	@Override
	public void reset() {
		this.lockOut = null;
		this.completed = false;
	}

	@Override
	public boolean isCompleted() {
		return this.completed;
	}

	@Override
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	@Override
	public Class<? extends T> getType() {
		return this.clazz;
	}
}
