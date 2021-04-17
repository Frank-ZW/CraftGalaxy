package net.craftgalaxy.lockout.challenge;

public interface IChallenge<T> {

	void reset();
	void setCompleted(boolean completed);
	boolean isCompleted();
	boolean handle(T e);
	String getDisplayMessage();
	Class<? extends T> getType();
}
