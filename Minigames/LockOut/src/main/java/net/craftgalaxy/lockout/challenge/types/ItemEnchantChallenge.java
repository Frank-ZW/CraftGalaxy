package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.event.enchantment.EnchantItemEvent;

public abstract class ItemEnchantChallenge extends AbstractChallenge<EnchantItemEvent> {

	public ItemEnchantChallenge(LockOut lockOut) {
		super(lockOut, EnchantItemEvent.class);
	}
}
