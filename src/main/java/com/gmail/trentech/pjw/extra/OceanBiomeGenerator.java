package com.gmail.trentech.pjw.extra;

import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.extent.MutableBiomeArea;
import org.spongepowered.api.world.gen.BiomeGenerator;

import com.flowpowered.math.vector.Vector2i;

public class OceanBiomeGenerator implements BiomeGenerator {

	@Override
	public void generateBiomes(MutableBiomeArea buffer) {
        final Vector2i min = buffer.getBiomeMin();
        final Vector2i max = buffer.getBiomeMax();
        
        for (int y = min.getY(); y <= max.getY(); y++) {
            for (int x = min.getX(); x <= max.getX(); x++) {
                buffer.setBiome(x, y, BiomeTypes.OCEAN);
            }
        }
	}

}
