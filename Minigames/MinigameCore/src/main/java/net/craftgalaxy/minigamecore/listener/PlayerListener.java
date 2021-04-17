package net.craftgalaxy.minigamecore.listener;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import net.craftgalaxy.minigamecore.minigame.MinigameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.spigotmc.event.entity.EntityMountEvent;

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
		MinigameManager.getInstance().handleEvent(e, e.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getEntity().getUniqueId());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getEntity().getUniqueId());
		MinigameManager.getInstance().handleEvent(e, e.getDamager().getUniqueId());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getEntity().getUniqueId());
	}

	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onPlayerPortal(PlayerPortalEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onEnderdragonChangePhase(EnderDragonChangePhaseEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getEntity().getUniqueId());
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onPlayerPickupExperience(PlayerPickupExperienceEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onPlayerPickupArrow(PlayerPickupArrowEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getEntity().getUniqueId());
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getWhoClicked().getUniqueId());
	}

	@EventHandler
	public void onVehicleEnter(VehicleEnterEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getEntered().getUniqueId());
	}

	@EventHandler
	public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getEntity().getUniqueId());
	}

	@EventHandler
	public void onEntityCombust(EntityCombustEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getEntity().getUniqueId());
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onPlayerCriteriaGrant(PlayerAdvancementCriterionGrantEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onPortalCreate(PortalCreateEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getEntity() == null ? null : e.getEntity().getUniqueId());
	}

	@EventHandler
	public void onPlayerBedEnter(PlayerBedEnterEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onEntityTame(EntityTameEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getOwner().getUniqueId());
	}

	@EventHandler
	public void onPlayerConsumeFood(PlayerItemConsumeEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onPlayerApplyArmor(PlayerArmorChangeEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onEntityBreed(EntityBreedEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getEntity().getUniqueId());
	}

	@EventHandler
	public void onItemEnchant(EnchantItemEvent e) {
		MinigameManager.getInstance().handleEvent(e, null);
	}

	@EventHandler
	public void onPlayerShear(PlayerShearEntityEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getEntity().getUniqueId());
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getEntity().getUniqueId());
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onPlayerEditBook(PlayerEditBookEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onTNTPrime(TNTPrimeEvent e) {
		MinigameManager.getInstance().handleEvent(e, null);
	}

	@EventHandler
	public void onEntityMount(EntityMountEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getEntity().getUniqueId());
	}

	@EventHandler
	public void onEntityPotionEffect(EntityPotionEffectEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getEntity().getUniqueId());
	}

	@EventHandler
	public void onPlayerEggThrow(PlayerEggThrowEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onPlayerAttemptPickupItem(PlayerAttemptPickupItemEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onEntityPickupItem(EntityPickupItemEvent e) {
		MinigameManager.getInstance().handleEvent(e, e.getEntity().getUniqueId());
	}
}
