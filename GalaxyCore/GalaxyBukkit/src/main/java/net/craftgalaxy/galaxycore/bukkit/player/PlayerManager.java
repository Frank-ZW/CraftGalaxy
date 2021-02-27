package net.craftgalaxy.galaxycore.bukkit.player;

import com.destroystokyo.paper.Title;
import net.craftgalaxy.galaxycore.bukkit.CorePlugin;
import net.craftgalaxy.galaxycore.bukkit.util.CooldownList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerManager {

	private CorePlugin plugin;
	private CooldownList<UUID> chatCooldowns;
	private final Set<UUID> frozenPlayers;
	private boolean chatSilenced;
	private boolean chatSlowed;
	private static PlayerManager instance;

	public PlayerManager(CorePlugin plugin) {
		this.plugin = plugin;
		this.frozenPlayers = new HashSet<>();
		this.chatCooldowns = new CooldownList<>(TimeUnit.SECONDS, this.plugin.getSlowModeCooldown());
	}

	public static void enable(CorePlugin plugin) {
		instance = new PlayerManager(plugin);
	}

	public static void disable() {
		if (instance == null) {
			return;
		}

		instance.chatCooldowns.clear();
		instance.chatCooldowns = null;
		instance.plugin = null;
		instance = null;
	}

	public static PlayerManager getInstance() {
		return instance;
	}

	public long getSecondsLeft(UUID uniqueId) {
		return this.chatCooldowns.getSecondsLeft(uniqueId);
	}

	public boolean isChatSilenced() {
		return this.chatSilenced;
	}

	public void setChatSilenced(boolean chatSilenced) {
		this.chatSilenced = chatSilenced;
	}

	public boolean isChatSlowed() {
		return this.chatSlowed;
	}

	public void setChatSlowed(boolean chatSlowed) {
		this.chatSlowed = chatSlowed;
	}

	public void freezePlayer(@NotNull Player player, boolean advancedAlert, boolean unknownAddress) {
		if (this.frozenPlayers.add(player.getUniqueId())) {
			if (advancedAlert) {
				if (unknownAddress) {
					Bukkit.getScheduler().runTask(this.plugin, () -> {
						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0, true, false, false));
						player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 0.375F, 0.375F);
						player.sendTitle(new Title(ChatColor.RED + "Detected new IP login...", ChatColor.RED + "Type /authenticate <password> to confirm your connection", 20, 20, 20));
					});
				} else {
					Bukkit.getScheduler().runTask(this.plugin, () -> {
						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0, true, false, false));
						player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 0.375F, 0.375F);
						player.sendTitle(new Title(ChatColor.RED + "You have been frozen", null, 20, 20, 20));
					});
				}
			} else {
				player.sendMessage(ChatColor.GREEN + "You are frozen and cannot interact with other players. For further instructions, join the Discord: https://discord.gg/wuqtEYpAjg");
			}
		}
	}

	public void unfreezePlayer(@NotNull Player player, boolean advancedAlert) {
		if (this.frozenPlayers.remove(player.getUniqueId())) {
			if (advancedAlert) {
				Bukkit.getScheduler().runTask(this.plugin, () -> player.removePotionEffect(PotionEffectType.BLINDNESS));
			} else {
				player.sendMessage(ChatColor.GREEN + "You are no longer frozen.");
			}
		}
	}

	public boolean isFrozenPlayer(UUID uniqueId) {
		return this.frozenPlayers.contains(uniqueId);
	}

	public void putChatCooldown(UUID uniqueId) {
		this.chatCooldowns.putCooldown(uniqueId);
	}

	public void handleFrozenEvent(Event e) {
		if (e instanceof PlayerEvent && e instanceof Cancellable) {
			Player player = ((PlayerEvent) e).getPlayer();
			if (this.frozenPlayers.contains(player.getUniqueId())) {
				((Cancellable) e).setCancelled(true);
			}
		}
	}
}
