package net.craftgalaxy.galaxycore.bukkit.listener;

import net.craftgalaxy.galaxycore.bukkit.player.PlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public final class PlayerListener implements Listener {

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		PlayerManager.getInstance().handleFrozenEvent(e);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		PlayerManager.getInstance().handleFrozenEvent(e);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		PlayerManager.getInstance().handleFrozenEvent(e);
	}

	@EventHandler
	public void onMultiBlockPlace(BlockMultiPlaceEvent e) {
		PlayerManager.getInstance().handleFrozenEvent(e);
	}
}
