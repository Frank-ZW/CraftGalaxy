package net.craftgalaxy.minigameservice.bukkit.minigame;

import org.bukkit.Location;

public abstract class CompetitionMinigame extends AbstractMinigame {

	public CompetitionMinigame(String name, int gameKey, Location lobby) {
		super(name, gameKey, lobby);
	}
}
