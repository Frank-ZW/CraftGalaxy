package net.craftgalaxy.mavic.alert;

import net.craftgalaxy.mavic.check.Check;

import java.io.Serializable;

public class Alert implements Serializable {

	private static final long serialVersionUID = -1275873387357441066L;
	private final String playerName;
	private final String checkName;
	private final Check.CheckType checkType;
	private final String data;
	private final long timestamp = System.currentTimeMillis();

	public Alert(String playerName, String checkName, Check.CheckType checkType, String data) {
		this.playerName = playerName;
		this.checkName = checkName;
		this.checkType = checkType;
		this.data = data;
	}

	public String getPlayerName() {
		return this.playerName;
	}

	public String getCheckName() {
		return this.checkName;
	}

	public Check.CheckType getCheckType() {
		return this.checkType;
	}

	public String getData() {
		return this.data;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 41;
		int result = 7;
		result = result * prime + this.playerName.hashCode();
		result = result * prime + this.checkName.hashCode();
		result = result * prime + this.checkType.ordinal();
		result = result * prime + this.data.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof Alert)) {
			return false;
		}

		Alert o = (Alert) obj;
		return this.playerName.equals(o.playerName) &&
				this.checkName.equals(o.checkName) &&
				this.checkType == o.checkType &&
				this.data.equals(o.data);
	}
}
