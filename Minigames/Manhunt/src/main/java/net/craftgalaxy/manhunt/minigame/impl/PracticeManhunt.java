package net.craftgalaxy.manhunt.minigame.impl;

import net.craftgalaxy.manhunt.minigame.types.AbstractManhunt;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PracticeManhunt extends AbstractManhunt {

	public PracticeManhunt(int gameKey, Location lobby) {
		super(gameKey, lobby, false, false);
	}

	@Override
	protected boolean playerStartTeleport(@NotNull Player player, Location to) {
		player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
		player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
		player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
		player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
		player.getInventory().setItem(0, new ItemStack(Material.IRON_SWORD));

		return super.playerStartTeleport(player, to);
	}

	@Override
	public void handlePlayerEvent(@NotNull Event event) {
		super.handlePlayerEvent(event);
		if (event instanceof PlayerDeathEvent) {
			PlayerDeathEvent e = (PlayerDeathEvent) event;
			e.getDrops().removeIf(this::isPlayerTracker);
		}
	}
}
