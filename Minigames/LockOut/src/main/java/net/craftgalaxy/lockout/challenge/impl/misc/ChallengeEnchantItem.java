package net.craftgalaxy.lockout.challenge.impl.misc;

import net.craftgalaxy.lockout.challenge.types.ItemEnchantChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.event.enchantment.EnchantItemEvent;

public class ChallengeEnchantItem extends ItemEnchantChallenge {

	public ChallengeEnchantItem(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public boolean handle(EnchantItemEvent e) {
		this.lockOut.completeChallenge(e.getEnchanter(), this);
		return true;
	}

	@Override
	public String getDisplayMessage() {
		return "Enchant an item!";
	}
}
