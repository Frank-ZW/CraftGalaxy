package net.craftgalaxy.mavic.util;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Fence;
import org.bukkit.block.data.type.TrapDoor;

import java.util.Set;

public class BlockUtil {

	private static final Set<Material> BAD_VELOCITY_SET = ImmutableSet.of(
			Material.WATER,
			Material.LAVA,
			Material.COBWEB,
			Material.SLIME_BLOCK,
			Material.LADDER,
			Material.VINE,
			Material.TWISTING_VINES,
			Material.WEEPING_VINES,
			Material.PISTON_HEAD,
			Material.MOVING_PISTON,
			Material.SNOW,
			Material.SOUL_SAND,
			Material.CHEST,
			Material.TRAPPED_CHEST,
			Material.ICE,
			Material.BLUE_ICE,
			Material.FROSTED_ICE,
			Material.PACKED_ICE,
			Material.HOPPER,
			Material.FLOWER_POT,
			Material.SKELETON_SKULL,
			Material.WITHER_SKELETON_SKULL,
			Material.PLAYER_HEAD,
			Material.CACTUS,
			Material.POTTED_CACTUS,
			Material.FLOWER_POT,
			Material.CAKE,
			Material.ENCHANTING_TABLE,
			Material.ENDER_CHEST
	);

	public static boolean isBadVelocityBlock(Block block) {
		BlockData blockData = block.getBlockData();
		return BlockUtil.BAD_VELOCITY_SET.contains(block.getType()) || blockData instanceof TrapDoor || blockData instanceof Fence;
	}
}
