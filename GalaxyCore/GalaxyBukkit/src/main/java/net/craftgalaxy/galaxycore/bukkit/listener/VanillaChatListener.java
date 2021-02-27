package net.craftgalaxy.galaxycore.bukkit.listener;

import com.gmail.nossr50.api.PartyAPI;
import com.google.common.collect.Sets;
import net.craftgalaxy.galaxycore.bukkit.CorePlugin;
import net.craftgalaxy.galaxycore.bukkit.player.PlayerManager;
import net.craftgalaxy.galaxycore.bukkit.util.StringUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class VanillaChatListener implements Listener {

	private final CorePlugin plugin;
	private final Pattern URL_REGEX = Pattern.compile("^(http://www\\.|https://www\\.|http://|https://)?[a-z0-9]+([\\-.][a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(/.*)?$");
	private final Pattern IP_REGEX = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])([.,])){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");

	public VanillaChatListener(CorePlugin plugin) {
		this.plugin = plugin;
	}

	private boolean handlePreChat(Player player, AsyncPlayerChatEvent e) {
		if (PlayerManager.getInstance().isChatSilenced() && !player.hasPermission(StringUtil.BYPASS_SILENT_CHAT_PERMISSION)) {
			e.setCancelled(true);
			player.sendMessage(ChatColor.RED + "The chat has been silenced.");
			return false;
		}

		if (PlayerManager.getInstance().isChatSlowed() && !player.hasPermission(StringUtil.BYPASS_SLOW_CHAT_PERMISSION)) {
			long seconds = PlayerManager.getInstance().getSecondsLeft(player.getUniqueId());
			if (seconds != 0) {
				e.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You have " + seconds + " seconds remaining before you can chat again.");
				return false;
			}
		}

		return true;
	}

	@EventHandler
	public void handleAsyncPlayerChat(AsyncPlayerChatEvent e) {
		if (e.isCancelled()) {
			return;
		}

		Player player = e.getPlayer();
		if (this.handlePreChat(player, e)) {
			Set<Player> pingPlayers = Sets.newHashSet();
			FilterType filterType = FilterType.PASS;
			String message = StringUtils.lowerCase(e.getMessage());
			String simpleMessage = message
					.replace("3", "e")
					.replace("1", "i")
					.replace("!", "i")
					.replace("@", "a")
					.replace("7", "t")
					.replace("0", "o")
					.replace("5", "s")
					.replace("8", "b")
					.replace("l", "i")
					.replace("/\\/","n")
					.replace("\\p{Punct}|\\d", "")
					.trim();
			String[] words = simpleMessage.split(" ");
			for (String word : words) {
				if (word.equalsIgnoreCase("kys") || word.equalsIgnoreCase("dox") || word.equalsIgnoreCase("ddos") || word.equalsIgnoreCase("spic") || word.equalsIgnoreCase("swastika") || word.equalsIgnoreCase("swastikas") || word.equalsIgnoreCase("hitler") || word.equalsIgnoreCase("porn") || word.equalsIgnoreCase("cum") || word.equalsIgnoreCase("sex") || word.equalsIgnoreCase("pornhub") || word.equalsIgnoreCase("xvideos") || word.equalsIgnoreCase("fag")) {
					filterType = FilterType.SILENT;
					break;
				}

				if (this.plugin.isMcMMO() && !PartyAPI.inParty(player)) {
					if (word.equalsIgnoreCase("la") || word.equalsIgnoreCase("vamos") || word.equalsIgnoreCase("estar") || word.equalsIgnoreCase("dos") || word.equalsIgnoreCase("estamos") || word.equalsIgnoreCase("estoy") || word.equalsIgnoreCase("estabamos") || word.equalsIgnoreCase("estuvimos") || word.equalsIgnoreCase("estariamos") || word.equalsIgnoreCase("nosotros") || word.equalsIgnoreCase("vosotros") || word.equalsIgnoreCase("usted") || word.equalsIgnoreCase("en") || word.equalsIgnoreCase("que") || word.equalsIgnoreCase("solamente")) {
						filterType = FilterType.NOTIFY;
						break;
					}
				}
			}

			words = message.trim().split(" ");
			for (String word : words) {
				if (word.length() <= 3) {
					continue;
				}

				Player target = Bukkit.getPlayer(word);
				if (target != null && target.hasPermission(StringUtil.CHAT_PING_PERMISSION)) {
					pingPlayers.add(target);
				}
			}

			words = e.getMessage().replace("(dot)", ".").replace("[dot]", ".").trim().split(" ");
			for (String word : words) {
				Matcher matcher = IP_REGEX.matcher(word);
				if (matcher.matches()) {
					filterType = FilterType.SILENT;
					break;
				}

				matcher = URL_REGEX.matcher(word);
				if (matcher.matches() && !word.contains("craftgalaxy.net")) {
					filterType = FilterType.NOTIFY;
					break;
				}
			}

			Set<String> filtered = this.plugin.getBlacklistedWords().stream().map(s -> s.replaceAll(" ", "")).filter(simpleMessage::contains).collect(Collectors.toSet());
			if (!filtered.isEmpty()) {
				filterType = FilterType.SILENT;
			}

			String format = e.getFormat();
			if (!filterType.isPassed()) {
				if (player.hasPermission(StringUtil.BYPASS_FILTER_CHAT_PERMISSION)) {
					player.sendMessage(ChatColor.RED + "That would have been filtered.");
				} else {
					e.setCancelled(true);
					pingPlayers.clear();
					if (filterType.isNotify()) {
						BaseComponent[] filterComponent = new ComponentBuilder(ChatColor.RED.asBungee() + "Your message was blocked because it breaks the ")
								.append("Craft")
								.color(ChatColor.GREEN.asBungee())
								.append("Galaxy")
								.color(ChatColor.BLUE.asBungee())
								.append(" community guidelines. Continued attempts to break the rules will be met with a punishment. Please review our rules on our ")
								.color(ChatColor.RED.asBungee())
								.append("Discord")
								.color(ChatColor.BLUE.asBungee())
								.event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/TKYXzBZ"))
								.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GREEN.asBungee() + "Join the Discord!")))
								.append(". If you believe this is a mistake, contact a staff member or file a ticket on the Discord.")
								.color(ChatColor.RED.asBungee())
								.event((ClickEvent) null)
								.event((HoverEvent) null)
								.create();
						player.sendMessage("");
						player.sendMessage(filterComponent);
						player.sendMessage("");
					} else {
						player.sendMessage(String.format(format, player.getDisplayName(), e.getMessage()));
					}

					format = ChatColor.DARK_GRAY + "[" + ChatColor.RED + "Filtered" + ChatColor.DARK_GRAY + "] " + format;
					Bukkit.getLogger().info(String.format(format, player.getDisplayName(), e.getMessage()));
					for (Player p : Bukkit.getOnlinePlayers()) {
						if (p.hasPermission(StringUtil.NOTIFICATION_PERMISSION)) {
							p.sendMessage("");
							p.sendMessage(String.format(format, player.getDisplayName(), e.getMessage()));
							p.sendMessage("");
						}
					}

					e.setFormat(format);
				}
			}

			for (Player p : pingPlayers) {
				p.playEffect(p.getLocation(), Effect.CLICK2, Effect.CLICK2.getData());
			}

			pingPlayers.clear();
			if (PlayerManager.getInstance().isChatSlowed() && !player.hasPermission(StringUtil.BYPASS_SLOW_CHAT_PERMISSION)) {
				PlayerManager.getInstance().putChatCooldown(player.getUniqueId());
			}

			e.setFormat(format);
		}
	}

	public enum FilterType {
		PASS,
		SILENT,
		NOTIFY;

		public boolean isPassed() {
			return this == FilterType.PASS;
		}

		public boolean isNotify() {
			return this == FilterType.NOTIFY;
		}
	}
}
