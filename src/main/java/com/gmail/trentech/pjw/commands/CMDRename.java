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
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.Help;

public class CMDRename implements CommandExecutor {

	public CMDRename() {
		Help help = new Help("rename", "rename", " Allows for renaming worlds. World must be unloaded before you can rename world");
		help.setSyntax(" /world rename <world> <world>\n /w rn <world> <world>");
		help.setExample(" /world rename MyWorld NewWorldName");
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

		if (Main.getGame().getServer().getWorld(oldWorldName).isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, oldWorldName, " must be unloaded before you can rename"));
			return CommandResult.empty();
		}

		if (!args.hasAny("new")) {
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}

		String newWorldName = args.<String> getOne("new").get();

		for (WorldProperties world : Main.getGame().getServer().getAllWorldProperties()) {
			if (world.getWorldName().equalsIgnoreCase(newWorldName)) {
				src.sendMessage(Text.of(TextColors.DARK_RED, newWorldName, " already exists"));
				return CommandResult.empty();
			}
		}

		for (WorldProperties worldInfo : Main.getGame().getServer().getUnloadedWorlds()) {
			if (worldInfo.getWorldName().equalsIgnoreCase(oldWorldName)) {
				Optional<WorldProperties> rename = Main.getGame().getServer().renameWorld(worldInfo, newWorldName);

				if (!rename.isPresent()) {
					src.sendMessage(Text.of(TextColors.DARK_RED, "Could not rename ", oldWorldName));
					return CommandResult.empty();
				}

				src.sendMessage(Text.of(TextColors.DARK_GREEN, newWorldName, " renamed successfully"));

				return CommandResult.success();
			}
		}

		src.sendMessage(Text.of(TextColors.DARK_RED, "Could not locate ", oldWorldName));

		return CommandResult.empty();
	}

	private Text invalidArg() {
		Text t1 = Text.of(TextColors.YELLOW, "/world rename ");
		Text t2 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Enter world or @w for current world"))).append(Text.of("<world> ")).build();
		Text t3 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Enter new world name"))).append(Text.of("<world>")).build();
		return Text.of(t1, t2, t3);
	}
}
