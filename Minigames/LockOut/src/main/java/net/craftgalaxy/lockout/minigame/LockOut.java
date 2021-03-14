package net.craftgalaxy.lockout.minigame;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.craftgalaxy.minigameservice.bukkit.minigame.SurvivalMinigame;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class LockOut extends SurvivalMinigame {

	private final Random random = new Random();
	private final BossBar challengeBar;
	private final List<ChallengeType> incompleted = new ObjectArrayList<>(ChallengeType.values());
	private final Map<UUID, Integer> completed = new Object2ObjectOpenHashMap<>();
	private ChallengeType challenge;

	public LockOut(int gameKey, Location lobby) {
		super("Lock Out", gameKey, lobby);
		this.challenge = this.incompleted.remove(this.random.nextInt(this.incompleted.size()));
		this.challengeBar = Bukkit.createBossBar(this.challenge.getDisplayName(), this.challenge.getBarColor(), BarStyle.SOLID);
		this.challengeBar.setVisible(true);
		this.challengeBar.setProgress(1.0D);
	}

	@Override
	public boolean createWorlds() {
		if (this.worlds.size() == 3) {
			return true;
		}

		for (int i = 0; i < 3; i++) {
			World.Environment environment = World.Environment.values()[i];
			World world = new WorldCreator(this.gameKey + "_" + StringUtils.lowerCase(String.valueOf(environment))).environment(environment).createWorld();
			if (world != null) {
				if (environment == World.Environment.NORMAL) {
					world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
				}

				world.setDifficulty(Difficulty.NORMAL);
				world.setKeepSpawnInMemory(false);
				world.setAutoSave(false);
				this.worlds.put(environment, world);
			}
		}

		return this.worlds.size() == 3;
	}

	@Override
	public boolean worldsLoaded() {
		return !this.worlds.isEmpty();
	}

	@Override
	public void unload() {
		super.unload();
		this.completed.clear();
		this.incompleted.clear();
		Collections.addAll(this.incompleted, ChallengeType.values());
	}

	@Override
	protected String startMessage(@NotNull UUID uniqueId) {
		return ChatColor.GREEN + "This is a test";
	}

	@Override
	protected boolean onPlayerStartTeleport(@NotNull Player player, int radius, float angle) {
		this.challengeBar.addPlayer(player);
		return super.onPlayerStartTeleport(player, radius, angle);
	}

	@Override
	public void endTeleport() {
		this.challengeBar.removeAll();
		super.endTeleport();
	}

	@Override
	public void handleEvent(@NotNull Event event) {
		if (event instanceof PlayerEvent) {
			Player player = ((PlayerEvent) event).getPlayer();
			if (event instanceof PlayerBedEnterEvent) {
				if (this.challenge == ChallengeType.LIE_IN_BED && this.challenge.isIncomplete()) {
					this.completeChallenge(player);
				}
			} else if (event instanceof PlayerAttemptPickupItemEvent) {
				PlayerAttemptPickupItemEvent e = (PlayerAttemptPickupItemEvent) event;
				if (this.challenge == ChallengeType.OBTAIN_DIAMONDS && this.challenge.isIncomplete() && e.getItem().getItemStack().getType() == Material.DIAMOND) {
					this.completeChallenge(player);
				}
			} else if (event instanceof PlayerRespawnEvent) {
				PlayerRespawnEvent e = (PlayerRespawnEvent) event;
				e.setRespawnLocation(player.getBedSpawnLocation() == null ? this.getOverworld().getSpawnLocation() : player.getBedSpawnLocation());
			}
		} else if (event instanceof EntityDamageEvent) {
			EntityDamageEvent e = (EntityDamageEvent) event;
			if (!(e.getEntity() instanceof Player)) {
				return;
			}

			if (this.status.isFinished()) {
				e.setCancelled(true);
			} else {
				Player player = (Player) e.getEntity();
				if (player.getHealth() - e.getDamage() <= 0 && this.challenge == ChallengeType.DEATH_FALL_DAMAGE && this.challenge.isIncomplete() && e.getCause() == EntityDamageEvent.DamageCause.FALL) {
					this.completeChallenge(player);
				}
			}
		}
	}

	@Override
	public void startCountdown(@NotNull List<UUID> players) {
		super.startCountdown(players);
		players.parallelStream().forEach(uniqueId -> this.completed.put(uniqueId, 0));
	}

	@Override
	public void endMinigame(boolean urgently) {
		int score = 0;
		List<UUID> winners = new ArrayList<>();
		for (Map.Entry<UUID, Integer> entry : this.completed.entrySet()) {
			if (entry.getValue() == 0) {
				continue;
			}

			if (winners.isEmpty()) {
				winners.add(entry.getKey());
				score = entry.getValue();
				continue;
			}

			if (entry.getValue() > score) {
				winners.clear();
				winners.add(entry.getKey());
				score = entry.getValue();
			} else if (entry.getValue() == score) {
				winners.add(entry.getKey());
			}
		}

		switch (winners.size()) {
			case 0: {
				Bukkit.broadcastMessage(ChatColor.RED + "There were no winners! You guys all sucked.");
				break;
			} case 1: {
				OfflinePlayer winner = Bukkit.getOfflinePlayer(winners.get(0));
				Bukkit.broadcastMessage(ChatColor.GREEN + winner.getName() + " won the " + this.getName() + " with " + score + " challenges completed.");
				break;
			} case 2: {
				OfflinePlayer winner1 = Bukkit.getOfflinePlayer(winners.get(0));
				OfflinePlayer winner2 = Bukkit.getOfflinePlayer(winners.get(1));
				Bukkit.broadcastMessage(ChatColor.GREEN + winner1.getName() + " and " + winner2.getName() + " won the " + this.getName() + " with " + score + " challenges completed.");
			} default: {
				StringBuilder result = new StringBuilder();
				for (int i = 0; i < winners.size(); i++) {
					OfflinePlayer winner = Bukkit.getOfflinePlayer(winners.get(i));
					if (i == winners.size() - 1) {
						result.append("and ").append(winner.getName());
					} else {
						result.append(winner.getName()).append(", ");
					}
				}

				Bukkit.broadcastMessage(ChatColor.GREEN + result.toString() + " have won the " + this.getName() + " with " + score + " challenges completed.");
			}
		}

		super.endMinigame(urgently);
	}

	private void completeChallenge(@NotNull Player player) {
		this.challenge.setCompleted(true);
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(ChatColor.GREEN + player.getName() + " has completed the challenge: " + this.challenge.getDisplayName());
		Bukkit.broadcastMessage("");

		Integer challenges = this.completed.get(player.getUniqueId());
		if (challenges == null) {
			challenges = 0;
		}

		this.completed.put(player.getUniqueId(), ++challenges);
		if (this.incompleted.isEmpty()) {
			this.endMinigame(false);
		} else {
			this.challenge = this.incompleted.remove(this.random.nextInt(this.incompleted.size()));
			this.challengeBar.setTitle(this.challenge.getDisplayName());
			this.challengeBar.setColor(this.challenge.getBarColor());
		}
	}

	public enum ChallengeType {
		DEATH_FALL_DAMAGE(BarColor.BLUE, "Die from Fall Damage"),
		LIE_IN_BED(BarColor.GREEN, "Lie down in a bed"),
		OBTAIN_DIAMONDS(BarColor.GREEN, "Find diamonds"),
		BREED_HORSE(BarColor.GREEN, "Breed a horse"),
		BREED_CHICKEN(BarColor.GREEN, "Breed a chicken"),
		SHEAR_SHEEP(BarColor.BLUE, "Shear a sheep"),
		REACH_HEIGHT_LIMIT(BarColor.BLUE, "Reach the world height limit"),
		OBTAIN_GOLD_ARMOR(BarColor.BLUE, "Obtain a full set of gold armor"),
		BREW_HEALING_POTION(BarColor.BLUE, "Brew a potion of healing"),
		SUMMON_WHITHER(BarColor.GREEN, "Summon the Whither"),
		EAT_SUSPICIOUS_STEW(BarColor.BLUE, "Eat a suspicious stew"),
		WEAR_TURTLE_SHELL(BarColor.GREEN, "Wear a turtle shell"),
		MAKE_ENDER_CHEST(BarColor.GREEN, "Craft an Ender Chest"),
		LIGHT_FIREWORK(BarColor.BLUE, "Light a firework"),
		OBTAIN_CHAINMAIL_ARMOR(BarColor.GREEN, "Obtain a full set of chainmail armor"),
		APPLY_NAMETAG(BarColor.BLUE, "Use a nametag"),
		ENCHANT_ITEM_STACK(BarColor.GREEN, "Enchant an item"),
		FIND_STRONGHOLD(BarColor.GREEN, "Find a Stronghold"),
		FIND_SWAMP_HUT(BarColor.GREEN, "Find a swamp hut"),
		FIND_BURIED_TREASURE(BarColor.GREEN, "Find buried treasure"),
		REACH_BEDROCK(BarColor.BLUE, "Reach the bedrock level"),
		PUBLISH_BOOK(BarColor.GREEN, "Publish a book"),
		COMPOST_FOOD(BarColor.BLUE, "Compost some food"),
		FIND_END_CITY(BarColor.GREEN, "Find an End City"),
		TAME_CAT(BarColor.GREEN, "Tame an Ocelot"),
		KILL_WHITHER(BarColor.BLUE, "Kill the Whither"),
		FIND_JUNGLE_TEMPLE(BarColor.BLUE, "Find a Jungle Temple");

		private final BarColor barColor;
		private final String displayName;
		private boolean completed;

		ChallengeType(BarColor barColor, String displayName) {
			this.barColor = barColor;
			this.displayName = displayName;
			this.completed = false;
		}

		public BarColor getBarColor() {
			return this.barColor;
		}

		public String getDisplayName() {
			return this.displayName;
		}

		public boolean isIncomplete() {
			return !this.completed;
		}

		public void setCompleted(boolean completed) {
			this.completed = completed;
		}
	}
}
