package com.gmail.trentech.pjw.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.gmail.trentech.pjw.Main;

public class Zip {

	String worldName;
	File backupDir;
	File worldDir;

	public Zip(String worldName) {
		this.worldName = worldName;
		this.backupDir = new File("backups");

		if (!backupDir.isDirectory()) {
			backupDir.mkdirs();
		}

		File savesDir = Main.getGame().getSavesDirectory().toFile();

		String defaultWorld = Main.getGame().getServer().getDefaultWorldName();

		if (worldName.equalsIgnoreCase(defaultWorld)) {
			worldDir = new File(savesDir, worldName);
		} else {
			worldDir = new File(savesDir, defaultWorld + File.separator + worldName);
		}
	}

	public void save() {
		Main.getLog().info("Backing up " + worldName);

		String zipFile = this.backupDir.getAbsolutePath() + File.separator + this.worldName + ".zip";

		try {
			FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
			ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
			addDir(this.worldDir, zipOutputStream);
			zipOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addDir(File directory, ZipOutputStream zipOutputStream) throws IOException {
		File[] files = directory.listFiles();
		byte[] buffer = new byte[1024];

		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				String name = files[i].getName();
				if (!Main.getGame().getServer().getWorldProperties(name).isPresent()) {
					addDir(files[i], zipOutputStream);
				}
				continue;
			}

			FileInputStream fileInputStream = new FileInputStream(files[i]);

			String relativePath = files[i].getAbsolutePath().replace(Main.getGame().getSavesDirectory().toFile().getAbsolutePath(), "").replace(" ", "").replace(File.separator + Main.getGame().getServer().getDefaultWorldName() + File.separator, "").replace(this.worldName + File.separator, "");

			zipOutputStream.putNextEntry(new ZipEntry(relativePath));

			int length;

			while ((length = fileInputStream.read(buffer)) > 0) {
				zipOutputStream.write(buffer, 0, length);
			}

			zipOutputStream.closeEntry();
			fileInputStream.close();
		}
	}
}
