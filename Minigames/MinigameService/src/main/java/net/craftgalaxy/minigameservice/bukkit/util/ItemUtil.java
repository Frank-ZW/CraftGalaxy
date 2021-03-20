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

	public static final String LOCKOUT_BOARD = ChatColor.GREEN + "Click to view the available challenges";
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

	private static final Set<Material> MUSIC_DISCS = ImmutableSet.of(
			Material.MUSIC_DISC_11,
			Material.MUSIC_DISC_13,
			Material.MUSIC_DISC_BLOCKS,
			Material.MUSIC_DISC_CAT,
			Material.MUSIC_DISC_MALL,
			Material.MUSIC_DISC_CHIRP,
			Material.MUSIC_DISC_FAR,
			Material.MUSIC_DISC_MELLOHI,
			Material.MUSIC_DISC_PIGSTEP,
			Material.MUSIC_DISC_STAL,
			Material.MUSIC_DISC_STRAD,
			Material.MUSIC_DISC_WAIT,
			Material.MUSIC_DISC_WARD
	);

	public static ItemStack createLockoutBoard() {
		ItemStack item = new ItemStack(Material.NETHER_STAR);
		ItemMeta itemMeta = item.getItemMeta();
		if (itemMeta != null) {
			itemMeta.setDisplayName(ItemUtil.LOCKOUT_BOARD);
			item.setItemMeta(itemMeta);
		}

		return item;
	}

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

	public static boolean isMusicDiscType(Material material) {
		return ItemUtil.MUSIC_DISCS.contains(material);
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

	public static boolean isChainmailArmorType(Material material) {
		return material == Material.CHAINMAIL_BOOTS || material == Material.CHAINMAIL_LEGGINGS || material == Material.CHAINMAIL_CHESTPLATE || material == Material.CHAINMAIL_HELMET;
	}

	public static boolean isGoldenArmorType(Material material) {
		return material == Material.GOLDEN_BOOTS || material == Material.GOLDEN_LEGGINGS || material == Material.GOLDEN_CHESTPLATE || material == Material.GOLDEN_HELMET;
	}

	public static boolean isIronArmorType(Material material) {
		return material == Material.IRON_BOOTS || material == Material.IRON_LEGGINGS || material == Material.IRON_CHESTPLATE || material == Material.IRON_HELMET;
	}
}
