package com.gmail.trentech.pjw.utils;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjw.Main;

public class Utils {

	public static void createPlatform(Location<World> center) {
		platform(center, BlockTypes.STONE);

		platform(center.getRelative(Direction.UP), BlockTypes.AIR);
		platform(center.getRelative(Direction.UP).getRelative(Direction.UP), BlockTypes.AIR);
		platform(center.getRelative(Direction.UP).getRelative(Direction.UP).getRelative(Direction.UP), BlockTypes.AIR);
	}

	private static void platform(Location<World> center, BlockType type) {
		List<Location<World>> list = new ArrayList<>();

		Location<World> south = center.getRelative(Direction.SOUTH);
		Location<World> north = center.getRelative(Direction.NORTH);
		Location<World> east = center.getRelative(Direction.EAST);
		Location<World> west = center.getRelative(Direction.WEST);

		Location<World> north2 = north.getRelative(Direction.NORTH);
		Location<World> south2 = south.getRelative(Direction.SOUTH);
		Location<World> east2 = east.getRelative(Direction.EAST);
		Location<World> west2 = west.getRelative(Direction.WEST);

		list.add(center);

		list.add(north);
		list.add(south);
		list.add(east);
		list.add(west);

		list.add(north.getRelative(Direction.EAST));
		list.add(north.getRelative(Direction.WEST));
		list.add(south.getRelative(Direction.EAST));
		list.add(south.getRelative(Direction.WEST));

		list.add(north2);
		list.add(north2.getRelative(Direction.EAST));
		list.add(north2.getRelative(Direction.EAST).getRelative(Direction.EAST));
		list.add(north2.getRelative(Direction.WEST));
		list.add(north2.getRelative(Direction.WEST).getRelative(Direction.WEST));

		list.add(south2);
		list.add(south2.getRelative(Direction.EAST));
		list.add(south2.getRelative(Direction.EAST).getRelative(Direction.EAST));
		list.add(south2.getRelative(Direction.WEST));
		list.add(south2.getRelative(Direction.WEST).getRelative(Direction.WEST));

		list.add(east2);
		list.add(east2.getRelative(Direction.NORTH));
		list.add(east2.getRelative(Direction.SOUTH));

		list.add(west2);
		list.add(west2.getRelative(Direction.NORTH));
		list.add(west2.getRelative(Direction.SOUTH));

		for (Location<World> location : list) {
			location.setBlock(Main.getGame().getRegistry().createBuilder(BlockState.Builder.class).blockType(type).build());
		}
	}
}
