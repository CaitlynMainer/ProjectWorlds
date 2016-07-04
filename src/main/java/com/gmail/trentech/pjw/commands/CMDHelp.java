package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.Help;

public class CMDHelp implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!args.hasAny("command")) {
			Text t1 = Text.of(TextColors.YELLOW, "/world help ");
			Text t2 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Enter the command you need help with"))).append(Text.of("<command> ")).build();
			src.sendMessage(Text.of(t1, t2));
			return CommandResult.empty();
		}
		String command = args.<String> getOne("command").get();

		for (Help help : Help.getAll()) {
			if (help.getCommand().equalsIgnoreCase(command)) {
				List<Text> list = new ArrayList<>();

				list.add(Text.of(TextColors.GREEN, "Description:"));
				list.add(Text.of(TextColors.WHITE, help.getDescription()));

				if (help.getSyntax().isPresent()) {
					list.add(Text.of(TextColors.GREEN, "Syntax:"));
					list.add(Text.of(TextColors.WHITE, help.getSyntax().get()));
				}
				if (help.getExample().isPresent()) {
					list.add(Text.of(TextColors.GREEN, "Example:"));
					list.add(Text.of(TextColors.WHITE, help.getExample().get(), TextColors.DARK_GREEN));
				}

				if (src instanceof Player) {
					PaginationList.Builder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();

					pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, command.toLowerCase())).build());

					pages.contents(list);

					pages.sendTo(src);
				} else {
					for (Text text : list) {
						src.sendMessage(text);
					}
				}

				return CommandResult.success();
			}
		}

		src.sendMessage(Text.of(TextColors.DARK_RED, command, " is not a valid command"));
		return CommandResult.empty();
	}
}
