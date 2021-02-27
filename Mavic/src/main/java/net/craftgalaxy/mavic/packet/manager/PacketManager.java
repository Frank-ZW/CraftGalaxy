package net.craftgalaxy.mavic.packet.manager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.*;
import com.google.common.collect.Sets;
import net.craftgalaxy.mavic.Mavic;
import net.craftgalaxy.mavic.data.PlayerData;
import net.craftgalaxy.mavic.data.manager.PlayerManager;
import net.craftgalaxy.mavic.packet.AbstractPacket;
import net.craftgalaxy.mavic.packet.PacketThreadLocal;
import net.craftgalaxy.mavic.packet.v1_16_R3.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PacketManager {

	private final Mavic plugin;
	private final ProtocolManager manager;
	private final Set<PacketType> incoming = Sets.newHashSet(
			PacketType.Play.Client.FLYING,
			PacketType.Play.Client.POSITION,
			PacketType.Play.Client.LOOK,
			PacketType.Play.Client.POSITION_LOOK,
			PacketType.Play.Client.KEEP_ALIVE,
			PacketType.Play.Client.USE_ENTITY,
			PacketType.Play.Client.BLOCK_DIG,
			PacketType.Play.Client.BLOCK_PLACE,
			PacketType.Play.Client.ENTITY_ACTION
	);
	private final Set<PacketType> outgoing = Sets.newHashSet(
			PacketType.Play.Server.KEEP_ALIVE,
			PacketType.Play.Server.ENTITY_VELOCITY
	);
	private final Map<PacketType, PacketThreadLocal> packetMap;
	private final PacketListener incomingAdapter;
	private final PacketListener outgoingAdapter;
	private static PacketManager instance;

	public PacketManager() {
		this.plugin = Mavic.getInstance();
		this.manager = ProtocolLibrary.getProtocolManager();
		this.packetMap = new ConcurrentHashMap<>();

		this.registerProtocol(MPacketPlayInFlying.class, PacketType.Play.Client.FLYING);
		this.registerProtocol(MPacketPlayInPosition.class, PacketType.Play.Client.POSITION);
		this.registerProtocol(MPacketPlayInLook.class, PacketType.Play.Client.LOOK);
		this.registerProtocol(MPacketPlayInPositionLook.class, PacketType.Play.Client.POSITION_LOOK);
		this.registerProtocol(MPacketPlayInUseEntity.class, PacketType.Play.Client.USE_ENTITY);
		this.registerProtocol(MPacketPlayInKeepAlive.class, PacketType.Play.Client.KEEP_ALIVE);
		this.registerProtocol(MPacketPlayInBlockDig.class, PacketType.Play.Client.BLOCK_DIG);
		this.registerProtocol(MPacketPlayInBlockPlace.class, PacketType.Play.Client.BLOCK_PLACE);
		this.registerProtocol(MPacketPlayInEntityAction.class, PacketType.Play.Client.ENTITY_ACTION);

		this.registerProtocol(MPacketPlayOutEntityVelocity.class, PacketType.Play.Server.ENTITY_VELOCITY);
		this.registerProtocol(MPacketPlayOutKeepAlive.class, PacketType.Play.Server.KEEP_ALIVE);

		this.incomingAdapter = new PacketAdapter(this.plugin, ListenerPriority.MONITOR, this.incoming) {

			@Override
			public void onPacketReceiving(PacketEvent e) {
				Player player = e.getPlayer();
				PlayerData playerData = PlayerManager.getInstance().getPlayerData(player);
				if (playerData == null) {
					return;
				}

				PacketContainer packet = e.getPacket();
				AbstractPacket abstractPacket = mutatePacket(player, packet, e.getPacketType());
				if (abstractPacket != null) {
					playerData.handlePacket(abstractPacket, true);
				}
			}
		};

		this.outgoingAdapter = new PacketAdapter(this.plugin, ListenerPriority.MONITOR, this.outgoing) {

			@Override
			public void onPacketSending(PacketEvent e) {
				Player player = e.getPlayer();
				PlayerData playerData = PlayerManager.getInstance().getPlayerData(player);
				if (playerData == null) {
					return;
				}

				PacketContainer packet = e.getPacket();
				AbstractPacket abstractPacket = mutatePacket(player, packet, e.getPacketType());
				if (abstractPacket != null) {
					playerData.handlePacket(abstractPacket, false);
				}
			}
		};

		this.manager.addPacketListener(this.incomingAdapter);
		this.manager.addPacketListener(this.outgoingAdapter);
	}

	public static void enable() {
		instance = new PacketManager();
	}

	public static void disable() {
		if (instance != null) {
			instance.unregister();
			instance.packetMap.clear();
			instance.incoming.clear();
			instance.outgoing.clear();
			instance = null;
		}
	}

	public void registerProtocol(Class<? extends AbstractPacket> clazz, PacketType type) {
		this.packetMap.put(type, new PacketThreadLocal(clazz));
	}

	public void unregister() {
		this.manager.removePacketListener(this.incomingAdapter);
		this.manager.removePacketListener(this.outgoingAdapter);
	}

	@Nullable
	public AbstractPacket mutatePacket(Player player, PacketContainer packet, PacketType type) {
		PacketThreadLocal threadLocal = this.packetMap.get(type);
		if (threadLocal != null) {
			AbstractPacket abstractPacket = threadLocal.get();
			abstractPacket.accept(player, packet);
			return abstractPacket;
		}

		return null;
	}
}
