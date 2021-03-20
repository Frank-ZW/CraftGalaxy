package net.craftgalaxy.lockout.challenge;

public interface IChallenge<T> {

	boolean handleEvent(T e);
	String getDisplayMessage();
}
