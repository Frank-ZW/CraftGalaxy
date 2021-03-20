package net.craftgalaxy.lockout.team;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Team {

	private final String name;
	private final ChatColor chatColor;
	private final Material icon;
	private int completed;

	public Team(String name, ChatColor chatColor, Material icon) {
		this.name = name;
		this.chatColor = chatColor;
		this.icon = icon;
		this.completed = 0;
	}

	public String getName() {
		return this.name;
	}

	public ChatColor getChatColor() {
		return this.chatColor;
	}

	public Material getIcon() {
		return this.icon;
	}

	public int getCompleted() {
		return this.completed;
	}

	public Team incrementCompleted() {
		this.completed++;
		return this;
	}
}
