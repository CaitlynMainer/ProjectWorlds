package com.gmail.trentech.pjw.commands;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.TeleportHelper;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.Help;
import com.gmail.trentech.pjw.utils.Rotation;

public class CMDTeleportP implements CommandExecutor {

	public CMDTeleportP() {
		Help help = new Help("teleportplayer", "teleportplayer", " Teleport others to specified world and location");
		help.setSyntax(" /world teleportplayer <player> <world> [-c <x,y,z>] [-d <direction>]\n /w tpp <player> <world> [-c <x,y,z>] [-d <direction>]");
		help.setExample(" /world tpp Notch MyWorld\n /world tpp Notch MyWorld -c -153,75,300 -d WEST");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!args.hasAny("player")) {
			src.sendMessage(getUsage());
			return CommandResult.empty();
		}
		String playerName = args.<String> getOne("player").get();

		Optional<Player> optionalPlayer = Main.getGame().getServer().getPlayer(playerName);

		if (!optionalPlayer.isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "Player ", playerName, " does not exist"));
			return CommandResult.empty();
		}
		Player player = optionalPlayer.get();

		if (!args.hasAny("world")) {
			src.sendMessage(getUsage());
			return CommandResult.empty();
		}
		String worldName = args.<String> getOne("world").get();

		Optional<World> optionalWorld = Main.getGame().getServer().getWorld(worldName);

		if (!optionalWorld.isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " does not exist"));
			return CommandResult.empty();
		}
		World world = optionalWorld.get();

		Location<World> location = world.getSpawnLocation();

		if (args.hasAny("x,y,z")) {
			String[] coords = args.<String> getOne("x,y,z").get().split(",");

			try {
				int x = Integer.parseInt(coords[0]);
				int y = Integer.parseInt(coords[1]);
				int z = Integer.parseInt(coords[2]);

				location = world.getLocation(x, y, z);
			} catch (Exception e) {
				src.sendMessage(Text.of(TextColors.RED, "Incorrect coordinates"));
				src.sendMessage(getUsage());
				return CommandResult.empty();
			}
		}

		Rotation rotation = Rotation.getClosest(player.getRotation().getFloorY());

		if (args.hasAny("direction")) {
			String direction = args.<String> getOne("direction").get();

			Optional<Rotation> optionalRotation = Rotation.get(direction);

			if (!optionalRotation.isPresent()) {
				src.sendMessage(Text.of(TextColors.RED, "Incorrect direction"));
				src.sendMessage(getUsage());
				return CommandResult.empty();
			}

			rotation = optionalRotation.get();
		}

		if (!player.hasPermission("pjw.worlds." + worldName)) {
			src.sendMessage(Text.of(TextColors.DARK_RED, playerName, " does not have permission to travel to ", worldName));
			return CommandResult.empty();
		}

		TeleportHelper teleportHelper = Main.getGame().getTeleportHelper();

		Optional<Location<World>> optionalLocation = teleportHelper.getSafeLocation(location);

		if (!optionalLocation.isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "This location is not safe"));
			return CommandResult.empty();
		}

		player.setLocationAndRotation(optionalLocation.get(), rotation.toVector3d());

		player.sendTitle(Title.of(Text.of(TextColors.DARK_GREEN, worldName), Text.of(TextColors.AQUA, "x: ", location.getBlockX(), ", y: ", location.getBlockY(), ", z: ", location.getBlockZ())));

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Teleported ", player.getName(), " to ", location.getExtent().getName(), ", x: ", location.getBlockX(), ", y: ", location.getBlockY(), ", z: ", location.getBlockZ()));

		return CommandResult.success();
	}

	private Text getUsage() {
		Text usage = Text.of(TextColors.YELLOW, "/world teleport");

		usage = Text.join(usage, Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Enter the player that you are teleporting"))).append(Text.of(" [player]")).build());
		usage = Text.join(usage, Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Enter the world name the player will teleport to"))).append(Text.of(" <world>")).build());
		usage = Text.join(usage, Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Enter x y z coordinates"))).append(Text.of(" [-c <x,y,z>]")).build());
		usage = Text.join(usage, Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("NORTH\nNORTHEAST\nEAST\nSOUTHEAST\nSOUTH\nSOUTHWEST\nWEST\nNORTHWEST"))).append(Text.of(" [-d <direction>]]")).build());

		return usage;
	}
}
