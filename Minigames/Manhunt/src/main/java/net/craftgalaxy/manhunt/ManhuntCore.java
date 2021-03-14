package net.craftgalaxy.manhunt;

import org.bukkit.plugin.java.JavaPlugin;

public final class ManhuntCore extends JavaPlugin {

	public static boolean BED_BOMBING_ENABLED;
	private static ManhuntCore instance;

	@Override
	public void onEnable() {
		instance = this;
		this.saveDefaultConfig();
		this.readConfig();
	}

	@Override
	public void onDisable() {
		this.writeConfig();
		instance = null;
	}

	public void readConfig() {
		ManhuntCore.BED_BOMBING_ENABLED = this.getConfig().getBoolean("settings.bed-bombing");
	}

	public void writeConfig() {
		this.getConfig().set("settings.bed-bombing", ManhuntCore.BED_BOMBING_ENABLED);
		this.saveConfig();
	}

	public static ManhuntCore getInstance() {
		return instance;
	}
}
