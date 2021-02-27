package net.craftgalaxy.mavic.listener;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import net.craftgalaxy.mavic.data.manager.PlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class TickListener implements Listener {

	@EventHandler
	public void onTickStart(ServerTickStartEvent e) {
		PlayerManager.getInstance().updatePlayerTicks();
	}
}
