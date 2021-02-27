package net.craftgalaxy.galaxycore.bungee.util;

public class PreviousNameEntry {

	private final String name;
	private final long timestamp;

	public PreviousNameEntry(String name, long timestamp) {
		this.name = name;
		this.timestamp = timestamp;
	}

	public String getName() {
		return this.name;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public boolean isOriginalName() {
		return this.timestamp == 0L;
	}

	@Override
	public int hashCode() {
		final int prime = 17;
		int result = 1;
		result = result * prime + this.name.hashCode();
		result = result * prime + Long.hashCode(this.timestamp);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof PreviousNameEntry)) {
			return false;
		}

		PreviousNameEntry o = (PreviousNameEntry) obj;
		return this.name.equals(o.name) && this.timestamp == o.timestamp;
	}
}
