package net.craftgalaxy.manhunt.minigame.types;

import net.craftgalaxy.manhunt.ManhuntCore;
import net.craftgalaxy.minigameservice.bukkit.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public abstract class AbstractSurvivalManhunt extends AbstractManhunt {

	public AbstractSurvivalManhunt(int gameKey, Location lobby) {
		super(gameKey, lobby);
	}

	public AbstractSurvivalManhunt(int gameKey, Location lobby, boolean netherAccess, boolean endAccess) {
		super(gameKey, lobby, netherAccess, endAccess);
	}

	/**
	 * @param event The event being triggered.
	 */
	@Override
	public void handlePlayerEvent(@NotNull Event event) {
		super.handlePlayerEvent(event);
		if (event instanceof PlayerEvent) {
			Player player = ((PlayerEvent) event).getPlayer();
			if (event instanceof PlayerInteractEvent) {
				PlayerInteractEvent e = (PlayerInteractEvent) event;
				Block clicked = e.getClickedBlock();
				if (this.isHunter(player.getUniqueId()) && (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) && e.getItem() != null && this.isPlayerTracker(e.getItem())) {
					this.updatePlayerTracker(player, e.getItem());
				}

				if (e.getAction() == Action.RIGHT_CLICK_BLOCK && player.getWorld().getEnvironment() == World.Environment.NETHER && ItemUtil.isBedType(clicked) && !ManhuntCore.BED_BOMBING_ENABLED) {
					e.setCancelled(true);
					player.sendMessage("");
					player.sendMessage(ChatColor.RED + "Bed bombing has been disabled. If you believe this is a mistake, submit a request for bed bombing to be enabled.");
					player.sendMessage("");
				}
			} else if (event instanceof PlayerDropItemEvent) {
				PlayerDropItemEvent e = (PlayerDropItemEvent) event;
				if (this.isHunter(player.getUniqueId())) {
					ItemStack drop = e.getItemDrop().getItemStack();
					if (this.isPlayerTracker(drop)) {
						e.setCancelled(true);
						player.sendMessage(ChatColor.RED + "You cannot drop your Player Tracker!");
					}
				}
			} else if (event instanceof PlayerRespawnEvent) {
				PlayerRespawnEvent e = (PlayerRespawnEvent) event;
				if (this.getOverworld() == null) {
					return;
				}

				e.setRespawnLocation(player.getBedSpawnLocation() == null ? this.getOverworld().getSpawnLocation() : player.getBedSpawnLocation());
				if (this.isHunter(player.getUniqueId())) {
					player.getInventory().setItem(8, ItemUtil.createPlayerTracker(ItemUtil.MANHUNT_PLAYER_TRACKER));
				}
			} else if (event instanceof PlayerAdvancementDoneEvent) {
				PlayerAdvancementDoneEvent e = (PlayerAdvancementDoneEvent) event;
				this.addAwardedAdvancement(player, e.getAdvancement());
			}
		} else if (event instanceof PlayerDeathEvent) {
			PlayerDeathEvent e = (PlayerDeathEvent) event;
			if (this.isSpeedrunner(e.getEntity().getUniqueId()) && this.status.isInProgress()) {
				this.endMinigame(false, false);
			} else {
				e.getDrops().removeIf(this::isPlayerTracker);
			}
		} else if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			if (!(e.getDamager() instanceof Player) || e.isCancelled()) {
				return;
			}

			if ((e.getEntity() instanceof Player && TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - this.startTimestamp) <= 5)) {
				e.setCancelled(true);
			}
		} else if (event instanceof EnderDragonChangePhaseEvent) {
			EnderDragonChangePhaseEvent e = (EnderDragonChangePhaseEvent) event;
			if (e.getNewPhase() == EnderDragon.Phase.DYING) {
				this.endMinigame(true, false);
			}
		} else if (event instanceof EntityDamageEvent) {
			EntityDamageEvent e = (EntityDamageEvent) event;
			if (this.isPlayer(e.getEntity().getUniqueId()) && this.status.isFinished()) {
				e.setCancelled(true);
			}
		} else if (event instanceof BlockPlaceEvent) {
			BlockPlaceEvent e = (BlockPlaceEvent) event;
			Player player = e.getPlayer();
			if (player.getWorld().getEnvironment() == World.Environment.NETHER && ItemUtil.isBedType(e.getBlock().getType()) && !ManhuntCore.BED_BOMBING_ENABLED) {
				player.sendMessage("");
				player.sendMessage(ChatColor.RED + "Bed bombing has been disabled. If you believe this is a mistake, submit a request for bed bombing to be enabled.");
				player.sendMessage("");
			}
		}
	}
}
