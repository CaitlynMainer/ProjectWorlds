package com.gmail.trentech.pjw.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.gmail.trentech.pjw.Main;

import net.obnoxint.xnbt.XNBT;
import net.obnoxint.xnbt.types.CompoundTag;
import net.obnoxint.xnbt.types.IntegerTag;
import net.obnoxint.xnbt.types.NBTTag;

public class SpongeData {

	private File dataFile;
	private CompoundTag compoundTag;
	private int dimId;
	private boolean exists = false;
	private static List<Integer> ids = new ArrayList<>();

	public SpongeData(String worldName) {
		String defaultWorld = Main.getGame().getServer().getDefaultWorldName();

		if (defaultWorld.equalsIgnoreCase(worldName)) {
			dataFile = new File(defaultWorld, "level_sponge.dat");
		} else {
			dataFile = new File(defaultWorld + File.separator + worldName, "level_sponge.dat");
		}

		initialize();
	}

	public SpongeData(File directory) {
		dataFile = new File(directory, "level_sponge.dat");

		initialize();
	}

	private void initialize() {
		if (dataFile.exists()) {
			exists = true;

			try {
				for (NBTTag root : XNBT.loadTags(dataFile)) {
					CompoundTag compoundRoot = (CompoundTag) root;

					for (Entry<String, NBTTag> rootItem : compoundRoot.entrySet()) {
						if (rootItem.getKey().equalsIgnoreCase("SpongeData")) {
							compoundTag = (CompoundTag) rootItem.getValue();
						}
					}
				}

				for (Entry<String, NBTTag> entry : compoundTag.entrySet()) {
					if (entry.getKey().equalsIgnoreCase("dimensionId")) {
						dimId = (Integer) entry.getValue().getPayload();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static List<Integer> getIds() {
		return ids;
	}

	public static void setIds(List<Integer> list) {
		ids = list;
	}

	public boolean exists() {
		return exists;
	}

	public int getDimId() {
		return dimId;
	}

	public boolean isFreeDimId() {
		if (ids.contains(getDimId())) {
			return false;
		}
		return true;
	}

	public int getFreeDimId() {
		for (int i = 1; i < Integer.MAX_VALUE; i++) {
			if (!ids.contains(i)) {
				return i;
			}
		}

		throw new NullPointerException();
	}

	public void setDimId(int id) throws IOException {
		// DataView container =
		// world.getAdditionalProperties().getView(DataQuery.of("SpongeData")).get();
		// container.set(DataQuery.of("dimensionId"), id);
		// world.setPropertySection(DataQuery.of("SpongeData"), container);
		// Main.getGame().getServer().saveWorldProperties(world);

		compoundTag.put(new IntegerTag("dimensionId", id));

		CompoundTag compoundRoot = new CompoundTag("", null);

		compoundRoot.put(compoundTag);

		List<NBTTag> list = new ArrayList<>();

		list.add(compoundRoot);

		XNBT.saveTags(list, dataFile);

		ids.add(id);
	}
}