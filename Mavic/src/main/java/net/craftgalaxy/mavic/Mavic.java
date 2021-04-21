package net.craftgalaxy.mavic;

import net.craftgalaxy.mavic.alert.manager.AlertManager;
import net.craftgalaxy.mavic.check.CheckLoader;
import net.craftgalaxy.mavic.commands.MavicCommand;
import net.craftgalaxy.mavic.data.manager.PlayerManager;
import net.craftgalaxy.mavic.listener.AlertListener;
import net.craftgalaxy.mavic.listener.PlayerListener;
import net.craftgalaxy.mavic.listener.TickListener;
import net.craftgalaxy.mavic.packet.manager.NMSManager;
import net.craftgalaxy.mavic.packet.manager.PacketManager;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class Mavic extends JavaPlugin {

	private boolean banEnabled;
	private static Mavic instance;

	@Override
	public void onEnable() {
		instance = this;
		NMSManager.getInstance();
		PacketManager.enable();
		PlayerManager.enable();
		AlertManager.enable(this);
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		Bukkit.getPluginManager().registerEvents(new AlertListener(this), this);
		Bukkit.getPluginManager().registerEvents(new TickListener(), this);
		PluginCommand mavic = this.getCommand("mavic");
		if (mavic == null) {
			Bukkit.getLogger().warning("Failed to register /mavic command.");
		} else {
			mavic.setExecutor(new MavicCommand());
		}
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		AlertManager.disable();
		PlayerManager.disable();
		PacketManager.disable();
		instance = null;
	}

	public boolean isBanEnabled() {
		return this.banEnabled;
	}

	public static Mavic getInstance() {
		return instance;
	}
}
