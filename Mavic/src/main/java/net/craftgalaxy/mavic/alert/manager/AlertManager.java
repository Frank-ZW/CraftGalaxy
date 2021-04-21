package net.craftgalaxy.mavic.alert.manager;

import net.craftgalaxy.mavic.Mavic;
import net.craftgalaxy.mavic.alert.Alert;
import net.craftgalaxy.mavic.check.Check;
import net.craftgalaxy.mavic.data.PlayerData;
import net.craftgalaxy.mavic.event.PlayerAlertEvent;
import net.craftgalaxy.mavic.event.PlayerBanEvent;
import org.bukkit.Bukkit;

public class AlertManager {

	private final Mavic plugin;
	private static AlertManager instance;

	public AlertManager(Mavic plugin) {
		this.plugin = plugin;
	}

	public static void enable(Mavic plugin) {
		instance = new AlertManager(plugin);
	}

	public static void disable() {
		if (instance != null) {
			instance = null;
		}
	}

	public static AlertManager getInstance() {
		return instance;
	}

	public void handleViolation(PlayerData playerData, Check check, String data, double vl, Check.CheckType checkType, double increment) {
		int violations = (int) vl;
		check.setViolations(check.getViolations() + increment);
		if (violations > check.getLastViolation()) {
			PlayerAlertEvent event = new PlayerAlertEvent(playerData.getPlayer(), check, playerData.getPing(), violations, data);
			Bukkit.getPluginManager().callEvent(event);
			if (!event.isCancelled()) {
				Alert alert = new Alert(playerData.getName(), check.getDisplayName(), check.getCheckType(), data);
				playerData.registerAlert(alert);
			}

			if (check.getViolations() >= check.getMaxViolations() && this.plugin.isBanEnabled() && checkType.isRelease()) {
				Bukkit.getPluginManager().callEvent(new PlayerBanEvent(playerData.getPlayer()));
			}
		}

		check.setLastViolation(violations);
	}
}
