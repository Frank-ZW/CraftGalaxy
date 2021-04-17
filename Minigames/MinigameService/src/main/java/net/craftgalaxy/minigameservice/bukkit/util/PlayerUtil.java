package net.craftgalaxy.minigameservice.bukkit.util;

import net.minecraft.server.v1_16_R1.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;

public class PlayerUtil {

	public static void clearAdvancements(@NotNull Player player) {
		Iterator<Advancement> iterator = Bukkit.advancementIterator();
		while (iterator.hasNext()) {
			Advancement advancement = iterator.next();
			AdvancementProgress progress = player.getAdvancementProgress(advancement);
			Collection<String> awarded = progress.getAwardedCriteria();
			for (String criteria : awarded) {
				progress.revokeCriteria(criteria);
			}
		}
	}

	public static void resetAttributes(@NotNull Player player) {
		player.setHealth(20.0D);
		player.setFoodLevel(20);
		player.setSaturation(5.0F);
		player.setExhaustion(0.0F);
		player.setLevel(0);
		player.setExp(0.0F);
		player.setTotalExperience(0);
		player.setFlying(false);
		player.setAllowFlight(false);
		player.setFallDistance(0.0F);
		player.setBedSpawnLocation(null);
		player.getInventory().clear();
		player.setGameMode(GameMode.SURVIVAL);
		if (player.getFireTicks() > 0) {
			player.setFireTicks(0);
		}

		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
	}

	public static void setSpectator(@NotNull Player player) {
		player.setHealth(20.0D);
		player.setFoodLevel(20);
		player.setSaturation(5.0F);
		player.setExhaustion(0.0F);
		player.setLevel(0);
		player.setExp(0.0F);
		player.setBedSpawnLocation(null);
		player.getInventory().clear();
		player.setGameMode(GameMode.ADVENTURE);
		player.setFallDistance(0.0F);
		if (player.getFireTicks() > 0) {
			player.setFireTicks(0);
		}

		player.setAllowFlight(true);
		player.setFlying(true);
		player.getInventory().setItem(0, ItemUtil.createSpectatorCompass());
		player.getInventory().setItem(8, ItemUtil.createSpectatorQuit());
	}

	public static void unsetSpectator(@NotNull Player player) {
		player.setHealth(20.0D);
		player.setFoodLevel(20);
		player.setSaturation(5.0F);
		player.setExhaustion(0.0F);
		player.setLevel(0);
		player.setExp(0.0F);
		player.getInventory().clear();
		player.setBedSpawnLocation(null);
		player.setGameMode(GameMode.SURVIVAL);
		player.setFallDistance(0.0F);
		player.setFlying(false);
		player.setAllowFlight(false);
		if (player.getFireTicks() > 0) {
			player.setFireTicks(0);
		}
	}

	public static boolean isWearingIronArmor(Player player) {
		PlayerInventory inventory = player.getInventory();
		if (inventory.getHelmet() == null || inventory.getHelmet().getType() != Material.IRON_HELMET) {
			return false;
		}

		if (inventory.getChestplate() == null || inventory.getChestplate().getType() != Material.IRON_CHESTPLATE) {
			return false;
		}

		if (inventory.getLeggings() == null || inventory.getLeggings().getType() != Material.IRON_LEGGINGS) {
			return false;
		}

		return inventory.getBoots() != null && inventory.getBoots().getType() == Material.IRON_BOOTS;

	}

	public static boolean isWearingGoldenArmor(Player player) {
		PlayerInventory inventory = player.getInventory();
		if (inventory.getHelmet() == null || inventory.getHelmet().getType() != Material.GOLDEN_HELMET) {
			return false;
		}

		if (inventory.getChestplate() == null || inventory.getChestplate().getType() != Material.GOLDEN_CHESTPLATE) {
			return false;
		}

		if (inventory.getLeggings() == null || inventory.getLeggings().getType() != Material.GOLDEN_LEGGINGS) {
			return false;
		}

		return inventory.getBoots() != null && inventory.getBoots().getType() == Material.GOLDEN_BOOTS;
	}

	public static boolean isWearingChainmailArmor(Player player) {
		PlayerInventory inventory = player.getInventory();
		if (inventory.getHelmet() == null || inventory.getHelmet().getType() != Material.CHAINMAIL_HELMET) {
			return false;
		}

		if (inventory.getChestplate() == null || inventory.getChestplate().getType() != Material.CHAINMAIL_CHESTPLATE) {
			return false;
		}

		if (inventory.getLeggings() == null || inventory.getLeggings().getType() != Material.CHAINMAIL_LEGGINGS) {
			return false;
		}

		return inventory.getBoots() != null && inventory.getBoots().getType() == Material.CHAINMAIL_BOOTS;
	}

	public static boolean isInsideStructure(@NotNull Player player, StructureType type, StructureGenerator<?> generator) {
		Location location = player.getWorld().locateNearestStructure(player.getLocation(), type, 1, false);
		if (location == null) {
			return false;
		}

		net.minecraft.server.v1_16_R1.World world = ((CraftWorld) player.getWorld()).getHandle();
		net.minecraft.server.v1_16_R1.Chunk chunk = world.getChunkAt(location.getChunk().getX(), location.getChunk().getZ());
		StructureStart<?> structureStart = chunk.a(generator);
		if (structureStart != null) {
			for (StructurePiece piece : structureStart.d()) {
				StructureBoundingBox cuboid = piece.g();
				if (player.getLocation().getX() >= cuboid.a && player.getLocation().getX() <= cuboid.d && player.getLocation().getY() >= cuboid.b && player.getLocation().getY() <= cuboid.e && player.getLocation().getZ() >= cuboid.c && player.getLocation().getZ() <= cuboid.f) {
					return true;
				}
			}
		}

		return false;
	}
}
