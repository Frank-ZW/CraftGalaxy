package net.craftgalaxy.minigameservice.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;

public final class BukkitService extends JavaPlugin {

	private Advancement netherAdvancement;
	private Advancement endAdvancement;
	private static BukkitService instance;

	@Override
	public void onEnable() {
		instance = this;
		Iterator<Advancement> iterator = Bukkit.advancementIterator();
		while (iterator.hasNext()) {
			Advancement advancement = iterator.next();
			if (advancement.getKey().getKey().equals("story/enter_the_nether")) {
				this.netherAdvancement = advancement;
			}

			if (advancement.getKey().getKey().equals("story/enter_the_end")) {
				this.endAdvancement = advancement;
			}

			if (this.netherAdvancement != null && this.endAdvancement != null) {
				break;
			}
		}
	}

	@Override
	public void onDisable() {
		instance = null;
	}

	public void grantNetherAdvancement(@NotNull Player player) {
		AdvancementProgress progress = player.getAdvancementProgress(this.netherAdvancement);
		Collection<String> remaining = progress.getRemainingCriteria();
		for (String criteria : remaining) {
			progress.awardCriteria(criteria);
		}
	}

	public void grantEndAdvancement(@NotNull Player player) {
		AdvancementProgress progress = player.getAdvancementProgress(this.endAdvancement);
		Collection<String> remaining = progress.getRemainingCriteria();
		for (String criteria : remaining) {
			progress.awardCriteria(criteria);
		}
	}

	public static BukkitService getInstance() {
		return instance;
	}
}
