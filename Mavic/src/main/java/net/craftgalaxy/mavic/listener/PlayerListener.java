package net.craftgalaxy.mavic.listener;

import net.craftgalaxy.mavic.data.PlayerData;
import net.craftgalaxy.mavic.data.manager.PlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

public final class PlayerListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		PlayerManager.getInstance().execute(() -> PlayerManager.getInstance().addPlayer(e.getPlayer()));
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		PlayerManager.getInstance().execute(() -> PlayerManager.getInstance().removePlayer(e.getPlayer()));
	}

	@EventHandler
	public void onVehicleEnter(VehicleEnterEvent e) {
		PlayerData playerData = PlayerManager.getInstance().getPlayerData(e.getEntered().getUniqueId());
		if (playerData != null) {
			playerData.setInVehicle(true);
		}
	}

	@EventHandler
	public void onVehicleExit(VehicleExitEvent e) {
		PlayerData playerData = PlayerManager.getInstance().getPlayerData(e.getExited().getUniqueId());
		if (playerData != null) {
			playerData.setInVehicle(false);
		}
	}
}
