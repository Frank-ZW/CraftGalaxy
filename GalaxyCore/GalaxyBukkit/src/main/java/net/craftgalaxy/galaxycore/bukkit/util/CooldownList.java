package net.craftgalaxy.galaxycore.bukkit.util;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class CooldownList<E> {

	private final Map<E, Long> timestamps = new HashMap<>();
	private final long duration;

	public CooldownList(TimeUnit unit, int duration) {
		this.duration = unit.toMillis(duration);
	}

	public long getSecondsLeft(E element) {
		Long timestamp = this.timestamps.get(element);
		if (timestamp == null) {
			return 0;
		}

		long seconds = TimeUnit.MILLISECONDS.toSeconds(this.duration - System.currentTimeMillis() + timestamp);
		if (seconds <= 0) {
			this.timestamps.remove(element);
			return 0;
		}

		return seconds;
	}

	public void putCooldown(E element) {
		this.timestamps.put(element, System.currentTimeMillis());
	}

	public void removeCooldown(E element) {
		this.timestamps.remove(element);
	}

	public void clear() {
		this.timestamps.clear();
	}
}
