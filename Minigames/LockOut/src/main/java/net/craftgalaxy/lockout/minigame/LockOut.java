package net.craftgalaxy.lockout.minigame;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.google.common.collect.Iterables;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.challenge.impl.advancements.ChallengeMaximumBeacon;
import net.craftgalaxy.lockout.challenge.impl.advancements.ChallengeObtainDiamonds;
import net.craftgalaxy.lockout.challenge.impl.advancements.ChallengeObtainWitherSkull;
import net.craftgalaxy.lockout.challenge.impl.advancements.ChallengeSummonWither;
import net.craftgalaxy.lockout.challenge.impl.armor.ChallengeWearChainArmor;
import net.craftgalaxy.lockout.challenge.impl.armor.ChallengeWearGoldArmor;
import net.craftgalaxy.lockout.challenge.impl.armor.ChallengeWearIronArmor;
import net.craftgalaxy.lockout.challenge.impl.armor.ChallengeWearTurtleShell;
import net.craftgalaxy.lockout.challenge.impl.consume.ChallengeConsumeNotchApple;
import net.craftgalaxy.lockout.challenge.impl.consume.ChallengeConsumeSuspiciousStew;
import net.craftgalaxy.lockout.challenge.impl.entity.*;
import net.craftgalaxy.lockout.challenge.impl.interact.*;
import net.craftgalaxy.lockout.challenge.impl.inventory.ChallengeBrewHealingPotion;
import net.craftgalaxy.lockout.challenge.impl.inventory.ChallengeCraftEnderChest;
import net.craftgalaxy.lockout.challenge.impl.inventory.ChallengeRepairItem;
import net.craftgalaxy.lockout.challenge.impl.misc.*;
import net.craftgalaxy.lockout.challenge.impl.movement.ChallengeExploreIceSpikedBiome;
import net.craftgalaxy.lockout.challenge.impl.movement.ChallengeExploreMushroomBiome;
import net.craftgalaxy.lockout.challenge.impl.movement.ChallengeReachBedrock;
import net.craftgalaxy.lockout.challenge.impl.movement.ChallengeReachHeightLimit;
import net.craftgalaxy.lockout.challenge.impl.structure.*;
import net.craftgalaxy.lockout.challenge.types.*;
import net.craftgalaxy.lockout.runnable.StructureReportRunnable;
import net.craftgalaxy.lockout.team.Team;
import net.craftgalaxy.minigameservice.bukkit.BukkitService;
import net.craftgalaxy.minigameservice.bukkit.minigame.SurvivalMinigame;
import net.craftgalaxy.minigameservice.bukkit.util.ItemUtil;
import net.craftgalaxy.minigameservice.bukkit.util.java.MathUtil;
import net.craftgalaxy.minigameservice.bukkit.util.java.StringUtil;
import net.milkbowl.vault.chat.Chat;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.*;
import java.util.logging.Level;

public final class LockOut extends SurvivalMinigame {

	private static final int HEIGHT = 5;
	private static final String BOARD_TITLE = ChatColor.DARK_BLUE + "LockOut Challenge Board";
	private static final LinkedHashMap<ChatColor, Material> GUI_COLORS = new LinkedHashMap<>() {{
		this.put(ChatColor.GREEN, Material.LIME_CONCRETE);
		this.put(ChatColor.BLUE, Material.BLUE_CONCRETE);
		this.put(ChatColor.YELLOW, Material.YELLOW_CONCRETE);
		this.put(ChatColor.GRAY, Material.GRAY_CONCRETE);
	}};
	private static final List<Class<? extends AbstractChallenge<?>>> CHALLENGES = Arrays.asList(
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
			ChallengePlayMusicDisc.class,
			ChallengeBrewHealingPotion.class,
			ChallengeCraftEnderChest.class,
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
			ChallengeMaximumBeacon.class,
			ChallengeObtainWitherSkull.class,
			ChallengeSummonWither.class,
			ChallengeMountLlama.class
	);

	private List<StructureLocateChallenge> structures = new ArrayList<>();
	private List<AbstractChallenge<?>> uncompleted = new ArrayList<>();
	private Map<UUID, Team> teams = new Object2ObjectOpenHashMap<>();
	private Map<AbstractChallenge<?>, Integer> guiIndex = new Object2ObjectOpenHashMap<>();
	private BukkitRunnable structureReportRunnable;
	private int completionThreshold;
	private Inventory challengeGui;

	public LockOut(int gameKey, Location lobby) {
		super("Lock Out", gameKey, lobby);
		this.challengeGui = Bukkit.createInventory(null, LockOut.HEIGHT * 9, LockOut.BOARD_TITLE);
		List<Class<? extends AbstractChallenge<?>>> selected = MathUtil.selectNRandomElements(LockOut.CHALLENGES, 25, this.random);
		int i = 0;
		try {
			for (int y = 0; y < 5; y++) {
				for (int x = 2; x < 7; x++) {
					AbstractChallenge<?> challenge = selected.get(i++).getConstructor(LockOut.class).newInstance(this);
					if (challenge instanceof StructureLocateChallenge) {
						this.structures.add((StructureLocateChallenge) challenge);
					} else {
						this.uncompleted.add(challenge);
					}

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
		this.guiIndex.keySet().forEach(AbstractChallenge::reset);
		this.structures.clear();
		this.uncompleted.clear();
		this.teams.clear();
		this.guiIndex.clear();
		this.structures = null;
		this.uncompleted = null;
		this.teams = null;
		this.guiIndex = null;
		this.structureReportRunnable = null;
		this.challengeGui = null;
	}

	@Override
	protected String startMessage(@NotNull UUID uniqueId) {
		return ChatColor.GREEN + "Right click the Nether Star to view the available challenges. The player with the most amount of challenges completed wins the Lock Out.";
	}

	@Override
	public void connectMessage(@NotNull Player player) {
		Team team = this.teams.get(player.getUniqueId());
		if (team != null) {
			Bukkit.broadcastMessage(team.getChatColor() + player.getName() + ChatColor.GREEN + " reconnected.");
		}
	}

	@Override
	public void disconnectMessage(@NotNull Player player) {
		Team team = this.teams.get(player.getUniqueId());
		if (team != null) {
			Bukkit.broadcastMessage(team.getChatColor() + player.getName() + ChatColor.GREEN + " disconnected.");
		}
	}

	public boolean isLockOutBoard(@Nullable ItemStack item) {
		return item != null && item.getType() == Material.NETHER_STAR && item.getItemMeta().getDisplayName().equals(ItemUtil.LOCKOUT_BOARD);
	}

	public void openLockOutBoard(@NotNull Player player) {
		player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
		player.openInventory(this.challengeGui);
	}

	@Override
	protected boolean playerStartTeleport(@NotNull Player player, int radius, float angle) {
		player.getInventory().setItem(8, ItemUtil.createLockoutBoard());
		return super.playerStartTeleport(player, radius, angle);
	}

	@Override
	public void removePlayer(@NotNull Player player) {
		super.removePlayer(player);
		this.teams.remove(player.getUniqueId());
		switch (this.teams.size()) {
			case 0:
				this.endMinigame(true, null);
				break;
			case 1:
				Team team = Iterables.getOnlyElement(this.teams.values(), null);
				this.endMinigame(false, team);
				break;
			default:
		}
	}

	@Override
	public String getPlayerFormat(Player player) {
		String prefix = null;
		Chat formatter = this.plugin.getChatFormatter();
		if (formatter != null) {
			prefix = formatter.getPlayerPrefix(player);
		}

		Team team = this.teams.get(player.getUniqueId());
		return StringUtil.MINIGAME_PREFIX + ChatColor.RESET + (prefix == null ? "" : ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.RESET + " ") + (team == null ? ChatColor.RED : team.getChatColor()) + ChatColor.stripColor("%s") + ChatColor.DARK_GRAY + ChatColor.BOLD + " » " + ChatColor.RESET + ChatColor.WHITE + "%s";
	}

	public boolean checkStructureChallenges(Player player) {
		this.structures.removeIf(challenge -> challenge.handleEvent(player));
		return this.structures.isEmpty();
	}

	@Override
	public void handleEvent(@NotNull Event event) {
		if (event instanceof PlayerEvent) {
			Player player = ((PlayerEvent) event).getPlayer();
			if (event instanceof PlayerPortalEvent) {
				PlayerPortalEvent e = (PlayerPortalEvent) event;
				if (e.getTo().getWorld() == null || e.getFrom().getWorld() == null || e.isCancelled()) {
					return;
				}

				World fromWorld = e.getFrom().getWorld();
				switch (e.getCause()) {
					case NETHER_PORTAL:
						switch (fromWorld.getEnvironment()) {
							case NORMAL:
								e.getTo().setWorld(this.getNether());
								if (this.players.contains(player.getUniqueId()) && !this.spectators.contains(player.getUniqueId())) {
									BukkitService.getInstance().grantNetherAdvancement(player);
								}

								break;
							case NETHER:
								e.setTo(new Location(this.getOverworld(), e.getFrom().getX() * 8.0D, e.getFrom().getY(), e.getFrom().getZ() * 8.0D));
								break;
							default:
						}

						break;
					case END_PORTAL:
						switch (fromWorld.getEnvironment()) {
							case NORMAL:
								e.getTo().setWorld(this.getEnd());
								if (this.players.contains(player.getUniqueId()) && !this.spectators.contains(player.getUniqueId())) {
									BukkitService.getInstance().grantEndAdvancement(player);
								}

								break;
							case THE_END:
								e.setTo(player.getBedSpawnLocation() == null ? this.getOverworld().getSpawnLocation() : player.getBedSpawnLocation());
								break;
							default:
						}

						break;
					default:
				}
			} else if (event instanceof PlayerRespawnEvent) {
				PlayerRespawnEvent e = (PlayerRespawnEvent) event;
				player.getInventory().setItem(8, ItemUtil.createLockoutBoard());
				e.setRespawnLocation(player.getBedSpawnLocation() == null ? this.getOverworld().getSpawnLocation() : player.getBedSpawnLocation());
			} else if (event instanceof PlayerBedEnterEvent) {
				PlayerBedEnterEvent e = (PlayerBedEnterEvent) event;
				this.uncompleted.removeIf(challenge -> challenge instanceof PlayerBedEnterChallenge && ((PlayerBedEnterChallenge) challenge).handleEvent(e));
			} else if (event instanceof PlayerInteractEvent) {
				PlayerInteractEvent e = (PlayerInteractEvent) event;
				if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
					ItemStack item = player.getInventory().getItemInMainHand();
					if (item.getType().isAir()) {
						item = player.getInventory().getItemInOffHand();
					}

					if (this.isLockOutBoard(item)) {
						this.openLockOutBoard(player);
						return;
					}
				}

				this.uncompleted.removeIf(challenge -> challenge instanceof PlayerInteractChallenge && ((PlayerInteractChallenge) challenge).handleEvent(e));
			} else if (event instanceof PlayerAdvancementDoneEvent) {
				PlayerAdvancementDoneEvent e = (PlayerAdvancementDoneEvent) event;
				this.uncompleted.removeIf(challenge -> challenge instanceof PlayerAdvancementChallenge && ((PlayerAdvancementChallenge) challenge).handleEvent(e));
			} else if (event instanceof PlayerArmorChangeEvent) {
				PlayerArmorChangeEvent e = (PlayerArmorChangeEvent) event;
				this.uncompleted.removeIf(challenge -> challenge instanceof PlayerArmorChallenge && ((PlayerArmorChallenge) challenge).handleEvent(e));
			} else if (event instanceof PlayerItemConsumeEvent) {
				PlayerItemConsumeEvent e = (PlayerItemConsumeEvent) event;
				this.uncompleted.removeIf(challenge -> challenge instanceof PlayerConsumeChallenge && ((PlayerConsumeChallenge) challenge).handleEvent(e));
			} else if (event instanceof PlayerEditBookEvent) {
				PlayerEditBookEvent e = (PlayerEditBookEvent) event;
				this.uncompleted.removeIf(challenge -> challenge instanceof PlayerEditBookChallenge && ((PlayerEditBookChallenge) challenge).handleEvent(e));
			} else if (event instanceof PlayerInteractEntityEvent) {
				PlayerInteractEntityEvent e = (PlayerInteractEntityEvent) event;
				this.uncompleted.removeIf(challenge -> challenge instanceof PlayerEntityInteractChallenge && ((PlayerEntityInteractChallenge) challenge).handleEvent(e));
			} else if (event instanceof PlayerMoveEvent) {
				if (this.status.isInProgress()) {
					PlayerMoveEvent e = (PlayerMoveEvent) event;
					this.uncompleted.removeIf(challenge -> challenge instanceof PlayerMovementChallenge && ((PlayerMovementChallenge) challenge).handleEvent(e));
				}
			} else if (event instanceof PlayerShearEntityEvent) {
				PlayerShearEntityEvent e = (PlayerShearEntityEvent) event;
				this.uncompleted.removeIf(challenge -> challenge instanceof PlayerShearChallenge && ((PlayerShearChallenge) challenge).handleEvent(e));
			} else if (event instanceof PlayerDropItemEvent) {
				PlayerDropItemEvent e = (PlayerDropItemEvent) event;
				if (this.isLockOutBoard(e.getItemDrop().getItemStack())) {
					e.setCancelled(true);
					player.sendMessage(ChatColor.RED + "You cannot drop your Challenge Board.");
				}
			}
		} else if (event instanceof EntityDamageEvent) {
			EntityDamageEvent e = (EntityDamageEvent) event;
			if (!this.status.isFinished()) {
				this.uncompleted.removeIf(challenge -> challenge instanceof EntityDamageChallenge && ((EntityDamageChallenge) challenge).handleEvent(e));
			} else if (e.getEntity() instanceof Player) {
				e.setCancelled(true);
			}
		} else if (event instanceof PlayerDeathEvent) {
			PlayerDeathEvent e = (PlayerDeathEvent) event;
			e.getDrops().removeIf(this::isLockOutBoard);
		} else if (event instanceof EntityBreedEvent) {
			EntityBreedEvent e = (EntityBreedEvent) event;
			this.uncompleted.removeIf(challenge -> challenge instanceof EntityBreedChallenge && ((EntityBreedChallenge) challenge).handleEvent(e));
		} else if (event instanceof EntityDeathEvent) {
			EntityDeathEvent e = (EntityDeathEvent) event;
			this.uncompleted.removeIf(challenge -> challenge instanceof EntityDeathChallenge && ((EntityDeathChallenge) challenge).handleEvent(e));
		} else if (event instanceof CreatureSpawnEvent) {
			CreatureSpawnEvent e = (CreatureSpawnEvent) event;
			this.uncompleted.removeIf(challenge -> challenge instanceof EntitySpawnChallenge && ((EntitySpawnChallenge) challenge).handleEvent(e));
		} else if (event instanceof EntityTameEvent) {
			EntityTameEvent e = (EntityTameEvent) event;
			this.uncompleted.removeIf(challenge -> challenge instanceof EntityTameChallenge && ((EntityTameChallenge) challenge).handleEvent(e));
		} else if (event instanceof EnchantItemEvent) {
			EnchantItemEvent e = (EnchantItemEvent) event;
			this.uncompleted.removeIf(challenge -> challenge instanceof ItemEnchantChallenge && ((ItemEnchantChallenge) challenge).handleEvent(e));
		} else if (event instanceof InventoryClickEvent) {
			InventoryClickEvent e = (InventoryClickEvent) event;
			if (e.getView().getTitle().equals(LockOut.BOARD_TITLE)) {
				e.setCancelled(true);
				return;
			}

			this.uncompleted.removeIf(challenge -> challenge instanceof PlayerInventoryClickChallenge && ((PlayerInventoryClickChallenge) challenge).handleEvent(e));
		} else if (event instanceof TNTPrimeEvent) {
			TNTPrimeEvent e = (TNTPrimeEvent) event;
			this.uncompleted.removeIf(challenge -> challenge instanceof TNTPrimeChallenge && ((TNTPrimeChallenge) challenge).handleEvent(e));
		} else if (event instanceof EntityMountEvent) {
			EntityMountEvent e = (EntityMountEvent) event;
			this.uncompleted.removeIf(challenge -> challenge instanceof EntityMountChallenge && ((EntityMountChallenge) challenge).handleEvent(e));
		}
	}

	@Override
	public void startCountdown(@NotNull List<UUID> players) {
		this.completionThreshold = (int) Math.ceil(25.0D / players.size());
		Set<Map.Entry<ChatColor, Material>> entries = LockOut.GUI_COLORS.entrySet();
		int index = 0;
		for (UUID uniqueId : players) {
			OfflinePlayer offline = Bukkit.getOfflinePlayer(uniqueId);
			Map.Entry<ChatColor, Material> entry = Iterables.get(entries, index++);
			this.teams.put(uniqueId, new Team(offline.getName(), entry.getKey(), entry.getValue()));
		}

		this.structureReportRunnable = new StructureReportRunnable(this);
		this.structureReportRunnable.runTaskTimerAsynchronously(this.plugin, 400, 20);
		super.startCountdown(players);
	}

	@Override
	public void cancelCountdown() {
		super.cancelCountdown();
		this.structureReportRunnable.cancel();
		this.completionThreshold = 0;
		this.teams.clear();
	}

	public void endMinigame(boolean urgently, @Nullable Team team) {
		if (!this.structureReportRunnable.isCancelled()) {
			this.structureReportRunnable.cancel();
		}

		if (team != null) {
			Bukkit.broadcastMessage("");
			Bukkit.broadcastMessage(team.getChatColor() + team.getName() + ChatColor.WHITE + " won the " + ChatColor.GREEN + this.getName() + ChatColor.WHITE + " with " + ChatColor.GREEN + team.getCompleted() + " challenges " + ChatColor.WHITE + "completed!");
			Bukkit.broadcastMessage("");
		}

		super.endMinigame(urgently);
	}

	public void completeChallenge(Player player, AbstractChallenge<?> challenge) {
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
			this.endMinigame(false, team);
		}
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
