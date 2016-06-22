package com.gmail.trentech.pjw.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.flowpowered.math.vector.Vector3i;
import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.Help;

public class CMDSetSpawn implements CommandExecutor {

	public CMDSetSpawn() {
		Help help = new Help("rename", "rename", " Sets the spawn point of specified world. If no arguments present sets spawn of current world to player location");
		help.setSyntax(" /world setspawn <world> <x,y,z>\n /w s <world> <x,y,z>");
		help.setExample(" /world setspawn\n /world setspawn MyWorld -153,75,300");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}
		Player player = (Player) src;

		String worldName = player.getWorld().getName();
		if (args.hasAny("name")) {
			worldName = args.<String> getOne("name").get();
		}

		if (!Main.getGame().getServer().getWorldProperties(worldName).isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " does not exist"));
			return CommandResult.empty();
		}
		WorldProperties properties = Main.getGame().getServer().getWorldProperties(worldName).get();

		if (!args.hasAny("value")) {
			properties.setSpawnPosition(player.getLocation().getBlockPosition());
			src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set spawn of world ", worldName, " to x: ", properties.getSpawnPosition().getX(), ", y: ", properties.getSpawnPosition().getY(), ", z: ", properties.getSpawnPosition().getZ()));
			return CommandResult.success();
		}

		String[] coords = args.<String> getOne("value").get().split(",");

		if (!isValidLocation(coords)) {
			src.sendMessage(Text.of(TextColors.DARK_RED, coords, " is not valid"));
			return CommandResult.empty();
		}

		properties.setSpawnPosition(new Vector3i().add(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2])));

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set spawn of world ", worldName, " to x: ", properties.getSpawnPosition().getX(), ", y: ", properties.getSpawnPosition().getY(), ", z: ", properties.getSpawnPosition().getZ()));

		return CommandResult.success();
	}

	private boolean isValidLocation(String[] coords) {
		if (coords == null) {
			return false;
		}

		for (String coord : coords) {
			try {
				Integer.parseInt(coord);
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

}
