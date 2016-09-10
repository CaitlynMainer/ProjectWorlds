package com.gmail.trentech.pjw.commands;

import java.util.HashMap;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldBorder;
import org.spongepowered.api.world.WorldBorder.ChunkPreGenerate;
import org.spongepowered.api.world.storage.WorldProperties;

import com.flowpowered.math.vector.Vector3d;
import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.Help;

public class CMDFill implements CommandExecutor {

	public static HashMap<String, Task> list = new HashMap<>();

	public CMDFill() {
		Help help = new Help("fill", "fill", " Pre generate chunks in a world outwards from center spawn");
		help.setSyntax(" /world fill <world> <diameter> [interval]\n /w f <world> <diameter> [interval]");
		help.setExample(" /world fill MyWorld 1000 \n /world fill MyWorld stop");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		WorldProperties properties = args.<WorldProperties> getOne("world").get();

		if (!Sponge.getServer().getWorld(properties.getUniqueId()).isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, properties.getWorldName(), " must be loaded"));
			return CommandResult.empty();
		}
		World world = Sponge.getServer().getWorld(properties.getUniqueId()).get();
		
		String value = args.<String> getOne("value").get();

		if (value.equalsIgnoreCase("stop")) {
			if (!list.containsKey(properties.getWorldName())) {
				src.sendMessage(Text.of(TextColors.YELLOW, "Pre-Generator not running for this world"));
				return CommandResult.empty();
			}
			list.get(properties.getWorldName()).cancel();
			list.remove(properties.getWorldName());

			src.sendMessage(Text.of(TextColors.DARK_GREEN, "Pre-Generator cancelled for ", properties.getWorldName()));
			return CommandResult.success();
		}
		
		if (list.containsKey(properties.getWorldName())) {
			if (Sponge.getScheduler().getScheduledTasks(Main.getPlugin()).contains(list.get(properties.getWorldName()))) {
				src.sendMessage(Text.of(TextColors.YELLOW, "Pre-Generator already running for this world"));
				return CommandResult.empty();
			}
			list.remove(properties.getWorldName());
		}
		
		double diameter;
		try {
			diameter = Double.parseDouble(value);
		} catch (Exception e) {
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}

		WorldBorder border = world.getWorldBorder();

		Vector3d center = border.getCenter();
		double diam = border.getDiameter();

		border.setCenter(world.getSpawnLocation().getX(), world.getSpawnLocation().getZ());
		border.setDiameter(diameter);

		ChunkPreGenerate generator = border.newChunkPreGenerate(world).owner(Main.getPlugin());
		generator.logger(Main.instance().getLog());
		
		if (args.hasAny("interval")) {
			generator.tickInterval(args.<Integer> getOne("interval").get());		
		}

		Task task = generator.start();

		list.put(properties.getWorldName(), task);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Pre-Generator starting for ", properties.getWorldName()));
		src.sendMessage(Text.of(TextColors.GOLD, "This can cause significant lag while running"));

		status(src, task);

		border.setDiameter(diam);
		border.setCenter(center.getX(), center.getZ());

		return CommandResult.success();
	}

	private Text invalidArg() {
		Text t1 = Text.of(TextColors.YELLOW, "/world fill ");
		Text t2 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Enter world name"))).append(Text.of("<world> ")).build();
		Text t3 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Enter diameter or \"stop\""))).append(Text.of("<diameter> ")).build();
		Text t4 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Enter the tick interval between generation runs. Default is 10"))).append(Text.of("[interval]")).build();
		return Text.of(t1, t2, t3, t4);
	}

	private void status(CommandSource src, Task task) {
		Sponge.getScheduler().createTaskBuilder().delayTicks(100).execute(c -> {
			if (!Sponge.getScheduler().getScheduledTasks(Main.getPlugin()).contains(task)) {
				src.sendMessage(Text.of(TextColors.DARK_GREEN, "Pre-Generator finished"));
			} else {
				status(src, task);
			}
		}).submit(Main.getPlugin());
	}
}
