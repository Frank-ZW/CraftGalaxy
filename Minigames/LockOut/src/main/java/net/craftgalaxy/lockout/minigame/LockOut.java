package net.craftgalaxy.lockout.minigame;

import com.google.common.collect.Iterables;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.craftgalaxy.lockout.challenge.IChallenge;
import net.craftgalaxy.lockout.challenge.impl.advancements.ChallengeObtainDiamonds;
import net.craftgalaxy.lockout.challenge.impl.advancements.ChallengeObtainWitherSkull;
import net.craftgalaxy.lockout.challenge.impl.advancements.ChallengeSummonWither;
import net.craftgalaxy.lockout.challenge.impl.armor.ChallengeWearChainArmor;
import net.craftgalaxy.lockout.challenge.impl.armor.ChallengeWearGoldArmor;
import net.craftgalaxy.lockout.challenge.impl.armor.ChallengeWearIronArmor;
import net.craftgalaxy.lockout.challenge.impl.armor.ChallengeWearTurtleShell;
import net.craftgalaxy.lockout.challenge.impl.block.ChallengePlaceBeacon;
import net.craftgalaxy.lockout.challenge.impl.consume.ChallengeConsumeNotchApple;
import net.craftgalaxy.lockout.challenge.impl.consume.ChallengeConsumeSuspiciousStew;
import net.craftgalaxy.lockout.challenge.impl.entity.*;
import net.craftgalaxy.lockout.challenge.impl.interact.*;
import net.craftgalaxy.lockout.challenge.impl.inventory.ChallengeBrewHealingPotion;
import net.craftgalaxy.lockout.challenge.impl.inventory.ChallengeCraftDispenser;
import net.craftgalaxy.lockout.challenge.impl.inventory.ChallengeCraftEnderChest;
import net.craftgalaxy.lockout.challenge.impl.inventory.ChallengeRepairItem;
import net.craftgalaxy.lockout.challenge.impl.misc.*;
import net.craftgalaxy.lockout.challenge.impl.movement.ChallengeExploreIceSpikedBiome;
import net.craftgalaxy.lockout.challenge.impl.movement.ChallengeExploreMushroomBiome;
import net.craftgalaxy.lockout.challenge.impl.movement.ChallengeReachBedrock;
import net.craftgalaxy.lockout.challenge.impl.movement.ChallengeReachHeightLimit;
import net.craftgalaxy.lockout.challenge.impl.structure.*;
import net.craftgalaxy.lockout.runnable.TrackerRunnable;
import net.craftgalaxy.lockout.runnable.StructureReportRunnable;
import net.craftgalaxy.lockout.team.Team;
import net.craftgalaxy.minigameservice.bukkit.minigame.SurvivalMinigame;
import net.craftgalaxy.minigameservice.bukkit.util.ItemUtil;
import net.craftgalaxy.minigameservice.bukkit.util.java.MathUtil;
import net.milkbowl.vault.chat.Chat;
import net.minecraft.server.v1_16_R1.NBTTagCompound;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public final class LockOut extends SurvivalMinigame {

	private static final int HEIGHT = 5;
	private static final String BOARD_TITLE = ChatColor.BLUE + "LockOut Challenge Board";
	private static final String PLAYERS_GUI_TITLE = ChatColor.BLUE + "Player Tracker";
	private static final LinkedHashMap<ChatColor, Material> GUI_COLORS = new LinkedHashMap<>() {{
		this.put(ChatColor.GREEN, Material.LIME_CONCRETE);
		this.put(ChatColor.BLUE, Material.BLUE_CONCRETE);
		this.put(ChatColor.YELLOW, Material.YELLOW_CONCRETE);
		this.put(ChatColor.GRAY, Material.GRAY_CONCRETE);
	}};
	private static final List<Class<? extends IChallenge<?>>> CHALLENGES = Arrays.asList(
			ChallengeObtainDiamonds.class,
			ChallengeWearChainArmor.class,
			ChallengeWearGoldArmor.class,
			ChallengeWearIronArmor.class,
			ChallengeWearTurtleShell.class,
			ChallengeConsumeSuspiciousStew.class,
			ChallengeConsumeNotchApple.class,
			ChallengeBreedChicken.class,
			ChallengeBreedHorse.class,
			ChallengeKillWither.class,
			ChallengeShearSheep.class,
			ChallengeSignBook.class,
			ChallengeSummonWither.class,
			ChallengeTameCat.class,
			ChallengeUseNametag.class,
			ChallengeFillComposter.class,
			ChallengeLightFirework.class,
			ChallengeLootBuriedTreasure.class,
			ChallengeLootShipwreckTreasure.class,
			ChallengeLootDungeonTreasure.class,
			ChallengePlayMusicDisc.class,
			ChallengeBrewHealingPotion.class,
			ChallengeCraftEnderChest.class,
			ChallengeCraftDispenser.class,
			ChallengeEnchantItem.class,
			ChallengeEnterBed.class,
			ChallengeFallDamageDeath.class,
			ChallengePrimeTNT.class,
			ChallengeExploreIceSpikedBiome.class,
			ChallengeExploreMushroomBiome.class,
			ChallengeReachBedrock.class,
			ChallengeReachHeightLimit.class,
			ChallengeMountLlama.class,
			ChallengeKillSlime.class,
			ChallengeRepairItem.class,
			ChallengeLevitationEffect.class,
			ChallengeWitherDamage.class,
			ChallengeLocateBastion.class,
			ChallengeLocateFossil.class,
			ChallengeLocateEndCity.class,
			ChallengeLocateNetherFortress.class,
			ChallengeLocateStronghold.class,
			ChallengeLocateVillage.class,
			ChallengeLocateSwampHut.class,
			ChallengeLocateDesertPyramid.class,
			ChallengeObtainWitherSkull.class,
			ChallengeSummonWither.class,
			ChallengePlaceBeacon.class,
			ChallengeHatchChicken.class
	);

	private Map<Class<?>, List<IChallenge>> uncompleted = new HashMap<>();
	private Map<UUID, Team> teams = new Object2ObjectOpenHashMap<>();
	private Map<IChallenge<?>, Integer> guiIndex = new Object2ObjectOpenHashMap<>();
	private BukkitRunnable structureRunnable;
	private BukkitRunnable trackerRunnable;
	private int completionThreshold;
	private Inventory challengeGui;

	public LockOut(int gameKey, Location lobby) {
		super("Lock Out", gameKey, lobby);
		this.challengeGui = Bukkit.createInventory(null, LockOut.HEIGHT * 9, LockOut.BOARD_TITLE);
		List<Class<? extends IChallenge<?>>> selected = MathUtil.selectNRandomElements(LockOut.CHALLENGES, 25, this.random);
		int i = 0;
		try {
			for (int y = 0; y < 5; y++) {
				for (int x = 2; x < 7; x++) {
					IChallenge<?> challenge = selected.get(i++).getConstructor(LockOut.class).newInstance(this);
					this.uncompleted.computeIfAbsent(challenge.getType(), v -> new ArrayList<>()).add(challenge);
					ItemStack icon = new ItemStack(Material.RED_CONCRETE);
					ItemMeta iconMeta = icon.getItemMeta();
					if (iconMeta != null) {
						iconMeta.setDisplayName(ChatColor.RED + challenge.getDisplayMessage());
						icon.setItemMeta(iconMeta);
					}

					int index = x + 9 * y;
					this.challengeGui.setItem(index, icon);
					this.guiIndex.put(challenge, index);
				}
			}
		} catch (ReflectiveOperationException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Failed to create one or more challenges", e);
			Bukkit.broadcastMessage(ChatColor.RED + "An error occurred while loading in one or more challenge(s). Contact an administrator if this occurs.");
			this.endMinigame(true);
		}
	}

	@Override
	public boolean createWorlds() {
		if (this.worlds.size() == 3) {
			return true;
		}

		for (int i = 0; i < 3; i++) {
			World.Environment environment = World.Environment.values()[i];
			World world = new WorldCreator( StringUtils.capitalize(this.getRawName()) + "_" + this.gameKey + "_" + StringUtils.lowerCase(String.valueOf(environment))).environment(environment).createWorld();
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
		this.guiIndex.keySet().forEach(IChallenge::reset);
		this.uncompleted.clear();
		this.teams.clear();
		this.guiIndex.clear();
		this.uncompleted = null;
		this.teams = null;
		this.guiIndex = null;
		this.trackerRunnable = null;
		this.structureRunnable = null;
		this.challengeGui = null;
	}

	@Override
	protected String startMessage(@NotNull UUID uniqueId) {
		return ChatColor.GREEN + "Right click the Nether Star to view the available challenges. The player with the most amount of challenges completed wins the Lock Out.";
	}

	public boolean isLockOutBoard(@Nullable ItemStack item) {
		if (item == null) {
			return false;
		}

		net.minecraft.server.v1_16_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
		NBTTagCompound compound = nmsItem.getTag();
		return item.getType() == Material.NETHER_STAR && compound != null && compound.getBoolean("lockout_board");
	}

	public boolean isPlayerTracker(@Nullable ItemStack item) {
		if (item == null) {
			return false;
		}

		net.minecraft.server.v1_16_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
		NBTTagCompound compound = nmsItem.getTag();
		return item.getType() == Material.COMPASS && compound != null && compound.getBoolean("player_tracker");
	}

	public void openLockOutBoard(@NotNull Player player) {
		player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
		player.openInventory(this.challengeGui);
	}

	@Override
	protected boolean playerStartTeleport(@NotNull Player player, Location to) {
		player.getInventory().setItem(0, ItemUtil.createPlayerTracker(ItemUtil.LOCKOUT_PLAYER_TRACKER));
		player.getInventory().setItem(8, ItemUtil.createLockoutBoard());
		return super.playerStartTeleport(player, to);
	}

	@Override
	public void removePlayer(@NotNull Player player) {
		super.removePlayer(player);
		if (this.teams.remove(player.getUniqueId()) != null && this.status.isInProgress()) {
			switch (this.teams.size()) {
				case 0:
					this.endMinigame(null, true);
					break;
				case 1:
					Team team = Iterables.getOnlyElement(this.teams.values(), null);
					this.endMinigame(team, team == null);
					break;
				default:
			}
		}
	}

	@Override
	public String getFormattedDisplayName(OfflinePlayer offline) {
		if (offline.getPlayer() == null) {
			return null;
		}

		String prefix = null;
		Chat formatter = this.plugin.getChatFormatter();
		if (formatter != null) {
			prefix = formatter.getPlayerPrefix(offline.getPlayer());
		}

		Team team = this.teams.get(offline.getUniqueId());
		return (prefix == null ? "" : ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.RESET + " ") + (team == null ? ChatColor.RED : team.getChatColor()) + offline.getName();
	}

	public boolean handleStructureChallenge(Player player) {
		List<IChallenge> challenges = this.uncompleted.get(Player.class);
		if (challenges == null) {
			return true;
		}

		challenges.removeIf(challenge -> challenge.handle(player));
		this.uncompleted.put(Player.class, challenges);
		return challenges.isEmpty();
	}

	public void handleChallenge(@NotNull Event e) {
		this.uncompleted.computeIfPresent(e.getClass(), (clazz, challenges) -> {
			challenges.removeIf(challenge -> challenge.handle(e));
			return challenges;
		});
	}

	@Override
	public void handlePlayerEvent(@NotNull Event event) {
		super.handlePlayerEvent(event);
		if (event instanceof PlayerEvent) {
			Player player = ((PlayerEvent) event).getPlayer();
			if (event instanceof PlayerRespawnEvent) {
				PlayerRespawnEvent e = (PlayerRespawnEvent) event;
				player.getInventory().setItem(0, ItemUtil.createPlayerTracker(ItemUtil.LOCKOUT_PLAYER_TRACKER));
				player.getInventory().setItem(8, ItemUtil.createLockoutBoard());
				e.setRespawnLocation(player.getBedSpawnLocation() == null ? this.getOverworld().getSpawnLocation() : player.getBedSpawnLocation());
			} else if (event instanceof PlayerInteractEvent) {
				PlayerInteractEvent e = (PlayerInteractEvent) event;
				ItemStack item = e.getItem();
				if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if (this.isLockOutBoard(item)) {
						this.openLockOutBoard(player);
					} else if (this.isPlayerTracker(item)) {
						List<Player> online = this.players.stream().map(Bukkit::getPlayer).filter(p -> p != null && !p.getUniqueId().equals(player.getUniqueId()) && !this.isSpectator(p.getUniqueId())).collect(Collectors.toList());
						switch (online.size()) {
							case 0: {
								player.sendActionBar(ChatColor.RED + "There are no players to track!");
								break;
							} case 1: {
								this.teams.computeIfPresent(player.getUniqueId(), (k, v) -> {
									v.setTracking(online.get(0).getUniqueId());
									return v;
								});
								break;
							} default: {
								Inventory players = Bukkit.createInventory(player, 27, LockOut.PLAYERS_GUI_TITLE);
								for (Player target : online) {
									if (target.getUniqueId().equals(player.getUniqueId())) {
										continue;
									}

									ItemStack head = new ItemStack(Material.PLAYER_HEAD);
									SkullMeta meta = (SkullMeta) head.getItemMeta();
									if (meta != null) {
										meta.setDisplayName(ChatColor.GREEN + target.getName());
										meta.setOwningPlayer(Bukkit.getOfflinePlayer(target.getUniqueId()));
										head.setItemMeta(meta);
									}

									players.addItem(head);
								}

								player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
								player.openInventory(players);
							}
						}
					}
				}
			} else if (event instanceof PlayerDropItemEvent) {
				PlayerDropItemEvent e = (PlayerDropItemEvent) event;
				ItemStack item = e.getItemDrop().getItemStack();
				if (this.isLockOutBoard(item) || this.isPlayerTracker(item)) {
					e.setCancelled(true);
					player.sendMessage(ChatColor.RED + "You cannot drop this item.");
				}
			}
		} else if (event instanceof EntityDamageEvent) {
			EntityDamageEvent e = (EntityDamageEvent) event;
			if (this.status.isFinished() && e.getEntity() instanceof Player) {
				e.setCancelled(true);
			}
		} else if (event instanceof PlayerDeathEvent) {
			PlayerDeathEvent e = (PlayerDeathEvent) event;
			e.getDrops().removeIf(this::isLockOutBoard);
		} else if (event instanceof InventoryClickEvent) {
			InventoryClickEvent e = (InventoryClickEvent) event;
			if (e.getView().getTitle().equals(LockOut.BOARD_TITLE)) {
				e.setCancelled(true);
				return;
			}

			ItemStack clicked = e.getCurrentItem();
			if (e.getView().getTitle().equals(LockOut.PLAYERS_GUI_TITLE) && clicked != null) {
				e.setCancelled(true);
				Player player = (Player) e.getWhoClicked();
				ItemStack compass = player.getInventory().getItemInMainHand();
				if (compass.getType().isAir()) {
					compass = player.getInventory().getItemInOffHand();
				}

				if (this.isPlayerTracker(compass)) {
					Player target = Bukkit.getPlayer(ChatColor.stripColor(clicked.getItemMeta().getDisplayName()));
					if (target == null) {
						player.sendMessage(ChatColor.RED + "That player is not online.");
						return;
					}

					this.teams.computeIfPresent(player.getUniqueId(), (k, v) -> {
						v.setTracking(target.getUniqueId());
						return v;
					});

					player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
				} else {
					player.sendMessage(ChatColor.RED + "You must be holding the Player Tracker to target a specific player.");
				}

				return;
			}
		}

		this.handleChallenge(event);
	}

	@Override
	public void startCountdown(@NotNull List<UUID> players) {
		super.startCountdown(players);
		this.completionThreshold = (int) Math.ceil(25.0D / players.size());
		Set<Map.Entry<ChatColor, Material>> entries = LockOut.GUI_COLORS.entrySet();
		int index = 0;
		for (UUID uniqueId : players) {
			OfflinePlayer offline = Bukkit.getOfflinePlayer(uniqueId);
			Map.Entry<ChatColor, Material> entry = Iterables.get(entries, index++);
			this.teams.put(uniqueId, new Team(offline.getName(), entry.getKey(), entry.getValue()));
		}

		this.structureRunnable = new StructureReportRunnable(this);
		this.structureRunnable.runTaskTimerAsynchronously(this.plugin, 940, 20);
		this.trackerRunnable = new TrackerRunnable(this);
		this.trackerRunnable.runTaskTimer(this.plugin, 300, 40);
	}

	@Override
	public void cancelCountdown() {
		super.cancelCountdown();
		this.trackerRunnable.cancel();
		this.structureRunnable.cancel();
		this.completionThreshold = 0;
		this.teams.clear();
	}

	public void endMinigame(@Nullable Team team, boolean urgently) {
		if (this.structureRunnable != null) {
			this.structureRunnable.cancel();
		}

		if (this.trackerRunnable != null) {
			this.trackerRunnable = null;
		}

		if (team != null) {
			Bukkit.broadcastMessage("");
			Bukkit.broadcastMessage(team.getChatColor() + team.getName() + ChatColor.WHITE + " won the " + ChatColor.GREEN + this.getName() + ChatColor.WHITE + " with " + ChatColor.GREEN + team.getCompleted() + " challenges " + ChatColor.WHITE + "completed!");
			Bukkit.broadcastMessage("");
		}

		super.endMinigame(urgently);
	}

	public void completeChallenge(Player player, IChallenge<?> challenge) {
		Team team = this.teams.get(player.getUniqueId());
		if (challenge.isCompleted() || team == null) {
			return;
		}

		challenge.setCompleted(true);
		int index = this.guiIndex.get(challenge);
		ItemStack item = this.challengeGui.getItem(index);
		if (item != null) {
			item.setType(team.getIcon());
			ItemMeta itemMeta = item.getItemMeta();
			if (itemMeta != null) {
				itemMeta.setDisplayName(ChatColor.GREEN + challenge.getDisplayMessage());
				itemMeta.setLore(Collections.singletonList(team.getChatColor() + "Completed by " + player.getName()));
				item.setItemMeta(itemMeta);
			}

			this.challengeGui.setItem(index, item);
			this.teams.computeIfPresent(player.getUniqueId(), (k, v) -> v.incrementCompleted());
			this.challengeGui.getViewers().forEach(v -> ((Player) v).updateInventory());
		}

		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(team.getChatColor() + player.getName() + ChatColor.RESET + ChatColor.WHITE + " has completed the challenge: " + ChatColor.GREEN + ChatColor.BOLD + challenge.getDisplayMessage());
		Bukkit.broadcastMessage("");
		if (team.getCompleted() >= this.completionThreshold || this.uncompleted.size() - 1 <= 0) {
			this.endMinigame(team, false);
		}
	}

	public Map<UUID, Team> getTeams() {
		return this.teams;
	}

	@Override
	public int hashCode() {
		return 19 * this.gameKey;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof LockOut)) {
			return false;
		}

		LockOut o = (LockOut) obj;
		return this.gameKey == o.getGameKey();
	}
}
