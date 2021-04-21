package net.craftgalaxy.mavic.check;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.craftgalaxy.mavic.alert.manager.AlertManager;
import net.craftgalaxy.mavic.data.PlayerData;
import org.bukkit.entity.Player;

import java.util.concurrent.*;

public class Check {

	private final String displayName;
	private final String rawName;
	private final int maxViolations;
	protected final Player player;
	protected final PlayerData playerData;
	protected double violations;
	private int lastViolation;
	private boolean enabled;
	private final CheckType checkType;
	private final ExecutorService executor;

	public Check(PlayerData playerData, String displayName, int maxViolations, CheckType checkType) {
		this.player = playerData.getPlayer();
		this.playerData = playerData;
		this.displayName = displayName;
		this.rawName = this.displayName.replaceAll("\\s+", "");
		this.maxViolations = maxViolations;
		this.checkType = checkType;
		this.enabled = true;
		this.executor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat(displayName + " Executor").build());
	}

	public void handleViolation() {
		this.handleViolation("");
	}

	public void handleViolation(String data) {
		this.handleViolation(data, 1.0D);
	}

	public void handleViolation(String data, double increment) {
		AlertManager.getInstance().handleViolation(this.playerData, this, data, this.violations, this.checkType, increment);
	}

	public double getViolations() {
		return this.violations;
	}

	public void setViolations(double violations) {
		this.violations = violations;
	}

	public int getLastViolation() {
		return this.lastViolation;
	}

	public void setLastViolation(int lastViolation) {
		this.lastViolation = lastViolation;
	}

	public int getMaxViolations() {
		return this.maxViolations;
	}

	public CheckType getCheckType() {
		return this.checkType;
	}

	public void decreaseViolation(double amount) {
		this.violations -= Math.min(this.violations, amount);
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public String getRawName() {
		return this.rawName;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void run(Runnable runnable) {
		this.executor.execute(runnable);
	}

	public enum CheckType {
		RELEASE,
		UNSTABLE,
		DEVELOPMENT;

		CheckType() {

		}

		public boolean isRelease() {
			return this == CheckType.RELEASE;
		}
	}
}
