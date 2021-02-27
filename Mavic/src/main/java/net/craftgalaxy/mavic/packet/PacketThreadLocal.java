package net.craftgalaxy.mavic.packet;

public class PacketThreadLocal extends ThreadLocal<AbstractPacket> {

	private final Class<? extends AbstractPacket> clazz;

	public PacketThreadLocal(Class<? extends AbstractPacket> clazz) {
		this.clazz = clazz;
	}

	@Override
	protected AbstractPacket initialValue() {
		try {
			return this.clazz.asSubclass(AbstractPacket.class).getConstructor().newInstance();
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException(e);
		}
	}
}
