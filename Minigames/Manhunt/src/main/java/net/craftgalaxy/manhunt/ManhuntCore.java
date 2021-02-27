package net.craftgalaxy.manhunt;

import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class ManhuntCore extends JavaPlugin {

	private boolean bedBombing;
	private Chat chatFormatter;
	private static ManhuntCore instance;

	@Override
	public void onEnable() {
		instance = this;
		this.saveDefaultConfig();
		this.readConfig();
		RegisteredServiceProvider<Chat> provider = Bukkit.getServicesManager().getRegistration(Chat.class);
		if (provider != null) {
			this.chatFormatter = provider.getProvider();
		}
	}

	@Override
	public void onDisable() {
		this.writeConfig();
		instance = null;
	}

	public void readConfig() {
		this.bedBombing = this.getConfig().getBoolean("settings.bed-bombing");
	}

	public void writeConfig() {
		this.getConfig().set("settings.bed-bombing", this.bedBombing);
		this.saveConfig();
	}

	public boolean isBedBombingDisabled() {
		return !this.bedBombing;
	}

	public Chat getChatFormatter() {
		return this.chatFormatter;
	}

	public static ManhuntCore getInstance() {
		return instance;
	}
}
