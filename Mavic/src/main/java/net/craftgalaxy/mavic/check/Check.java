package net.craftgalaxy.mavic.check;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.craftgalaxy.mavic.alert.manager.AlertManager;
import net.craftgalaxy.mavic.data.PlayerData;

import java.util.concurrent.*;

public class Check {

	private final String name;
	private final int maxViolations;
	private double violations;
	private int lastViolation;
	private boolean enabled;
	private final CheckType checkType;
	private final ExecutorService executor;

	public Check(String name, int maxViolations, CheckType checkType) {
		this.name = name;
		this.maxViolations = maxViolations;
		this.checkType = checkType;
		this.enabled = true;
		this.executor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat(name + " Executor").build());
	}

	public void handleViolation(PlayerData playerData) {
		this.handleViolation(playerData, "");
	}

	public void handleViolation(PlayerData playerData, String data) {
		this.handleViolation(playerData, data, 1.0D);
	}

	public void handleViolation(PlayerData playerData, String data, double increment) {
		AlertManager.getInstance().handleViolation(playerData, this, data, this.violations, this.checkType, increment);
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

	public String getName() {
		return this.name;
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
