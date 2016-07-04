package com.gmail.trentech.pjw.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;

import com.gmail.trentech.pjw.Main;

public class Migrator {

	public static void init() {
		Main.getLog().info("Running World Migration tool..");

		File directory = new File(Main.getGame().getSavesDirectory().toFile(), "imports");

		if (!directory.exists()) {
			directory.mkdir();
		}

		File[] files = directory.listFiles();

		if ((files.length - 1) > 0) {
			Main.getLog().info("Found " + (files.length - 1) + " possible worlds");
		} else {
			Main.getLog().info("No worlds to migrate");
		}

		for (File world : directory.listFiles()) {
			if (!world.isDirectory()) {
				continue;
			}

			WorldData worldData = new WorldData(world);

			if (!worldData.exists()) {
				continue;
			}

			String name = world.getName();

			Main.getLog().info("Migrating world: " + name);

			if (!worldData.isCorrectLevelName()) {
				Main.getLog().warn("  * Repairing level name mismatch");

				try {
					worldData.setLevelName(world.getName());
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
			}

			SpongeData spongeData = new SpongeData(world);

			if (spongeData.exists()) {
				if (!spongeData.isFreeDimId()) {

					Main.getLog().warn("  * Repairing dimension id conflict");
					try {
						spongeData.setDimId(spongeData.getFreeDimId());
					} catch (IOException e) {
						e.printStackTrace();
						continue;
					}
				}
			}

			File dest = new File(new File(Main.getGame().getSavesDirectory().toFile(), Main.getGame().getServer().getDefaultWorldName()), name);

			int i = 1;
			while (name.equalsIgnoreCase(Main.getGame().getServer().getDefaultWorldName()) || dest.exists()) {
				Main.getLog().error(" * A world with this name already exists");
				name = world.getName() + "_" + i;

				try {
					worldData.setLevelName(name);
					dest = new File(new File(Main.getGame().getSavesDirectory().toFile(), Main.getGame().getServer().getDefaultWorldName()), name);
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
				i++;
			}

			try {
				Main.getLog().info("  * Copying world to final resting place");
				copyWorld(world, dest);
			} catch (IOException e) {
				Main.getLog().error(" * Could not copy world");
				e.printStackTrace();
				continue;
			}

			if (spongeData.exists()) {
				Main.getLog().info("  * Complete!");
			} else {
				Main.getLog().warn("  * Complete! Requires importing. /world import " + world.getName() + " <type> <generator>");
			}

			Main.getGame().getScheduler().createTaskBuilder().delayTicks(40).execute(e -> {
				try {
					deleteWorld(world);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}).submit(Main.getPlugin());
		}
	}

	private static void copyWorld(File src, File dest) throws IOException {
		dest.mkdirs();

		for (String file : src.list()) {
			File srcFile = new File(src, file);
			File destFile = new File(dest, file);

			if (srcFile.isDirectory()) {
				copyWorld(srcFile, destFile);
			} else {
				Files.copy(new FileInputStream(srcFile), destFile.toPath(), new CopyOption[0]);
			}
		}
	}

	private static synchronized void deleteWorld(File src) throws IOException {
		for (String file : src.list()) {
			File srcFile = new File(src, file);

			if (srcFile.isDirectory()) {
				deleteWorld(srcFile);
			} else {
				Files.delete(srcFile.toPath());
			}
		}

		Files.delete(src.toPath());
	}
}
