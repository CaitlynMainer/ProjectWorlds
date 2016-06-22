package com.gmail.trentech.pjw.extra;

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.ImmutableBiomeArea;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.gen.GenerationPopulator;

import com.flowpowered.math.vector.Vector3i;

public class OceanBaseGeneratorPopulator implements GenerationPopulator {

	@Override
	public void populate(World world, MutableBlockVolume buffer, ImmutableBiomeArea biomes) {
		final Vector3i min = buffer.getBlockMin();
		final Vector3i max = buffer.getBlockMax();

		final int xMin = min.getX();
		final int yMin = min.getY();
		final int zMin = min.getZ();
		final int xMax = max.getX();
		final int yMax = max.getY();
		final int zMax = max.getZ();

		for (int zz = zMin; zz <= zMax; zz++) {
			for (int yy = yMin; yy <= yMax; yy++) {

				for (int xx = xMin; xx <= xMax; xx++) {
					buffer.setBlockType(xx, yy, zz, BlockTypes.WATER);
				}
			}
		}
	}

}
