package net.craftgalaxy.minigamecore.listener;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import net.craftgalaxy.minigamecore.minigame.MinigameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.world.PortalCreateEvent;

import java.io.IOException;

public final class PlayerListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		try {
			MinigameManager.getInstance().handleConnect(e.getPlayer());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) throws IOException {
		MinigameManager.getInstance().handleDisconnect(e.getPlayer());
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		MinigameManager.getInstance().handleEvent(e);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		MinigameManager.getInstance().handleEvent(e);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		MinigameManager.getInstance().handleEvent(e);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		MinigameManager.getInstance().handleEvent(e);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent e) {
		MinigameManager.getInstance().handleEvent(e);
	}

	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
		MinigameManager.getInstance().handleEvent(e);
	}

	@EventHandler
	public void onPlayerPortal(PlayerPortalEvent e) {
		MinigameManager.getInstance().handleEvent(e);
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		MinigameManager.getInstance().handleEvent(e);
	}

	@EventHandler
	public void onEnderdragonChangePhase(EnderDragonChangePhaseEvent e) {
		MinigameManager.getInstance().handleEvent(e);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		MinigameManager.getInstance().handleEvent(e);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		MinigameManager.getInstance().handleEvent(e);
	}

	@EventHandler
	public void onPlayerPickupExperience(PlayerPickupExperienceEvent e) {
		MinigameManager.getInstance().handleEvent(e);
	}

	@EventHandler
	public void onPlayerPickupArrow(PlayerPickupArrowEvent e) {
		MinigameManager.getInstance().handleEvent(e);
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		MinigameManager.getInstance().handleEvent(e);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		MinigameManager.getInstance().handleEvent(e);
	}

	@EventHandler
	public void onVehicleEnter(VehicleEnterEvent e) {
		MinigameManager.getInstance().handleEvent(e);
	}

	@EventHandler
	public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent e) {
		MinigameManager.getInstance().handleEvent(e);
	}

	@EventHandler
	public void onEntityCombust(EntityCombustEvent e) {
		MinigameManager.getInstance().handleEvent(e);
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		MinigameManager.getInstance().handleEvent(e);
	}

	@EventHandler
	public void onPlayerCriteriaGrant(PlayerAdvancementCriterionGrantEvent e) {
		MinigameManager.getInstance().handleEvent(e);
	}

	@EventHandler
	public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent e) {
		MinigameManager.getInstance().handleEvent(e);
	}

	@EventHandler
	public void onPortalCreate(PortalCreateEvent e) {
		MinigameManager.getInstance().handleEvent(e);
	}

	@EventHandler
	public void onPlayerAttemptPickupItem(PlayerAttemptPickupItemEvent e) {
		MinigameManager.getInstance().handleEvent(e);
	}

	@EventHandler
	public void onPlayerBedEnter(PlayerBedEnterEvent e) {
		MinigameManager.getInstance().handleEvent(e);
	}
}
