package com.gmail.trentech.pjw.commands;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.Help;

public class CMDCopy implements CommandExecutor {

	public CMDCopy() {
		Help help = new Help("copy", "copy", " Allows you to make a new world from an existing world");
		help.setSyntax(" /world copy <world> <world>\n /w cp <world> <world>");
		help.setExample(" /world copy srcWorld newWorld\n /world copy @w newWorld");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!args.hasAny("old")) {
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}

		String oldWorldName = args.<String> getOne("old").get();

		if (oldWorldName.equalsIgnoreCase("@w") && src instanceof Player) {
			oldWorldName = ((Player) src).getWorld().getName();
		}

		if (!args.hasAny("new")) {
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}

		String newWorldName = args.<String> getOne("new").get();

		for (WorldProperties world : Main.getGame().getServer().getAllWorldProperties()) {
			if (!world.getWorldName().equalsIgnoreCase(newWorldName)) {
				continue;
			}

			src.sendMessage(Text.of(TextColors.DARK_RED, newWorldName, " already exists"));
			return CommandResult.empty();
		}

		if (!Main.getGame().getServer().getWorld(oldWorldName).isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "World ", oldWorldName, " does not exists"));
			return CommandResult.empty();
		}

		Optional<WorldProperties> copy = null;
		try {
			copy = Main.getGame().getServer().copyWorld(Main.getGame().getServer().getWorld(oldWorldName).get().getProperties(), newWorldName).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		if (!copy.isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "Could not copy ", oldWorldName));
			return CommandResult.empty();
		}

		src.sendMessage(Text.of(TextColors.DARK_GREEN, oldWorldName, " copied to ", newWorldName));

		return CommandResult.success();
	}

	private Text invalidArg() {
		Text t1 = Text.of(TextColors.YELLOW, "/world copy ");
		Text t2 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Enter source world"))).append(Text.of("<world> ")).build();
		Text t3 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Enter new world name"))).append(Text.of("<world>")).build();
		return Text.of(t1, t2, t3);
	}
}
