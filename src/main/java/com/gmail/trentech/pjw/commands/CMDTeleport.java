package com.gmail.trentech.pjw.commands;

import java.util.HashMap;
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
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.TeleportHelper;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.Help;
import com.gmail.trentech.pjw.utils.Rotation;

public class CMDTeleport implements CommandExecutor {

	public static HashMap<Player, Location<World>> players = new HashMap<>();

	public CMDTeleport() {
		Help help = new Help("teleport", "teleport", " Teleport to specified world and location");
		help.setSyntax(" /world teleport <world> [-c <x,y,z>] [-d <direction>]\n /w tp <world> [-c <x,y,z>] [-d <direction>]");
		help.setExample(" /world tp MyWorld\n /world teleport MyWorld -c -153,75,300\n /world tp MyWorld -c -153,75,300 -d WEST");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}
		Player player = (Player) src;

		if (!args.hasAny("world")) {
			src.sendMessage(getUsage());
			return CommandResult.empty();
		}
		String worldName = args.<String> getOne("world").get();

		if (worldName.equalsIgnoreCase("confirm")) {
			if (!players.containsKey(player)) {
				return CommandResult.success();
			}
			Location<World> location = players.get(player);

			player.setLocation(location);

			players.remove(player);

			return CommandResult.success();
		}

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

		if (!src.hasPermission("pjw.worlds." + worldName)) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "You do not have permission to travel to ", worldName));
			return CommandResult.empty();
		}

		TeleportHelper teleportHelper = Main.getGame().getTeleportHelper();

		Optional<Location<World>> optionalLocation = teleportHelper.getSafeLocation(location);

		if (!optionalLocation.isPresent()) {
			players.put(player, location);
			src.sendMessage(Text.builder().color(TextColors.DARK_RED).append(Text.of("Unsafe spawn point detected. Teleport anyway? ")).onClick(TextActions.runCommand("/pjw:world teleport confirm")).append(Text.of(TextColors.GOLD, TextStyles.UNDERLINE, "Click Here")).build());
			return CommandResult.empty();
		}

		player.setLocationAndRotation(optionalLocation.get(), rotation.toVector3d());

		player.sendTitle(Title.of(Text.of(TextColors.DARK_GREEN, worldName), Text.of(TextColors.AQUA, "x: ", location.getBlockX(), ", y: ", location.getBlockY(), ", z: ", location.getBlockZ())));

		return CommandResult.success();
	}

	private Text getUsage() {
		Text usage = Text.of(TextColors.YELLOW, "/world teleport");

		usage = Text.join(usage, Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Enter the world name the player will teleport to"))).append(Text.of(" <world>")).build());
		usage = Text.join(usage, Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Enter x y z coordinates"))).append(Text.of(" [-c <x,y,z>]")).build());
		usage = Text.join(usage, Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("NORTH\nNORTHEAST\nEAST\nSOUTHEAST\nSOUTH\nSOUTHWEST\nWEST\nNORTHWEST"))).append(Text.of(" [-d <direction>]]")).build());

		return usage;
	}
}
