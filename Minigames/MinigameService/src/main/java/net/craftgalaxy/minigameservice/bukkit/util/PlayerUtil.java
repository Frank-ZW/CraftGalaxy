package net.craftgalaxy.minigameservice.bukkit.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
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

		Bukkit.getLogger().info(ChatColor.GREEN + player.getName() + " had all their advancements cleared.");
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
		player.setAllowFlight(true);
		player.setFlying(true);
		player.setBedSpawnLocation(null);
		player.getInventory().clear();
		player.setGameMode(GameMode.ADVENTURE);
		player.setFallDistance(0.0F);
		if (player.getFireTicks() > 0) {
			player.setFireTicks(0);
		}
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
		player.setGameMode(GameMode.ADVENTURE);
		player.setFallDistance(0.0F);
		player.setFlying(false);
		player.setAllowFlight(false);
		if (player.getFireTicks() > 0) {
			player.setFireTicks(0);
		}
	}
}
