package net.craftgalaxy.deathswap;

import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public final class DeathSwapCore extends JavaPlugin {

	private Chat chatFormatter;
	private static DeathSwapCore instance;

	@Override
	public void onEnable() {
		instance = this;
		RegisteredServiceProvider<Chat> provider = Bukkit.getServicesManager().getRegistration(Chat.class);
		if (provider != null) {
			this.chatFormatter = provider.getProvider();
		}
	}

	@Override
	public void onDisable() {
		instance = null;
	}

	@Nullable
	public Chat getChatFormatter() {
		return this.chatFormatter;
	}

	public static DeathSwapCore getInstance() {
		return instance;
	}
}
