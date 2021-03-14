package net.craftgalaxy.minigameservice.bukkit.util;

import com.google.common.collect.ImmutableSet;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Set;

public class ItemUtil {


	public static final String PLAYER_TRACKER = ChatColor.RED + "Player Tracker";
	public static final String SPECTATOR_COMPASS = ChatColor.GREEN + "Spectator Compass";
	public static final String SPECTATOR_QUIT = ChatColor.RED + "Leave the game";
	private static final Set<Material> BED_ITEMS = ImmutableSet.of(
			Material.BLACK_BED,
			Material.BLUE_BED,
			Material.BROWN_BED,
			Material.CYAN_BED,
			Material.GRAY_BED,
			Material.GREEN_BED,
			Material.LIGHT_BLUE_BED,
			Material.LIGHT_GRAY_BED,
			Material.LIME_BED,
			Material.MAGENTA_BED,
			Material.ORANGE_BED,
			Material.PINK_BED,
			Material.PURPLE_BED,
			Material.RED_BED,
			Material.WHITE_BED,
			Material.YELLOW_BED
	);


	public static ItemStack createPlayerTracker() {
		ItemStack item = new ItemStack(Material.COMPASS);
		CompassMeta meta = (CompassMeta) item.getItemMeta();
		if (meta != null) {
			meta.setDisplayName(ItemUtil.PLAYER_TRACKER);
			item.setItemMeta(meta);
		}

		return item;
	}

	public static ItemStack createSpectatorCompass() {
		return ItemUtil.createItemStack(Material.COMPASS, ItemUtil.SPECTATOR_COMPASS);
	}

	public static ItemStack createSpectatorQuit() {
		return ItemUtil.createItemStack(Material.RED_BED, ItemUtil.SPECTATOR_QUIT);
	}

	public static ItemStack createItemStack(Material material, String displayName, String ... lore) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			meta.setDisplayName(displayName);
			meta.setLore(Arrays.asList(lore));
			item.setItemMeta(meta);
		}

		return item;
	}

	public static boolean isBedType(Material material) {
		return ItemUtil.BED_ITEMS.contains(material);
	}

	public static boolean isBedType(Block block) {
		if (block == null) {
			return false;
		} else {
			return ItemUtil.isBedType(block.getType());
		}
	}
}
