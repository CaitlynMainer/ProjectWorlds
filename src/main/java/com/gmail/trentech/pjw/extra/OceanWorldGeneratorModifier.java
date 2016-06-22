package com.gmail.trentech.pjw.extra;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.gen.WorldGenerator;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;

public class OceanWorldGeneratorModifier implements WorldGeneratorModifier {

	@Override
	public void modifyWorldGenerator(WorldCreationSettings world, DataContainer settings, WorldGenerator worldGenerator) {

		worldGenerator.getGenerationPopulators().clear();
		worldGenerator.getPopulators().clear();

		worldGenerator.setBaseGenerationPopulator(new OceanBaseGeneratorPopulator());
	}

	@Override
	public String getId() {
		return "pjw:ocean";
	}

	@Override
	public String getName() {
		return "Ocean";
	}

}
