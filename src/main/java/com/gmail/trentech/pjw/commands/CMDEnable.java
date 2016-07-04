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
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.Help;

public class CMDEnable implements CommandExecutor {

	public CMDEnable() {
		Help help = new Help("enable", "enable", " Enable and disable worlds from loading");
		help.setSyntax(" /world enable <world> <value>\n /w e <world> <value>");
		help.setExample(" /world enable MyWorld true\n /world enable MyWorld false");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!args.hasAny("name")) {
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}
		String worldName = args.<String> getOne("name").get();

		if (worldName.equalsIgnoreCase("@w") && src instanceof Player) {
			worldName = ((Player) src).getWorld().getName();
		}

		if (!Main.getGame().getServer().getWorldProperties(worldName).isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " does not exist"));
			return CommandResult.empty();
		}
		WorldProperties properties = Main.getGame().getServer().getWorldProperties(worldName).get();

		if (!args.hasAny("value")) {
			List<Text> list = new ArrayList<>();

			list.add(Text.of(TextColors.GREEN, "Enabled: ", TextColors.WHITE, properties.isEnabled()));
			list.add(Text.of(TextColors.GREEN, "Command: ", invalidArg()));

			if (src instanceof Player) {
				PaginationList.Builder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();

				pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, properties.getWorldName())).build());

				pages.contents(list);

				pages.sendTo(src);
			} else {
				for (Text text : list) {
					src.sendMessage(text);
				}
			}

			return CommandResult.success();
		}
		String value = args.<String> getOne("value").get();

		if ((!value.equalsIgnoreCase("true")) && (!value.equalsIgnoreCase("false"))) {
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}

		properties.setEnabled(Boolean.parseBoolean(value));

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set enabled of ", worldName, " to ", value));

		return CommandResult.success();
	}

	private Text invalidArg() {
		Text t1 = Text.of(TextColors.YELLOW, "/world enable ");
		Text t2 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Enter world or @w for current world"))).append(Text.of("<world> ")).build();
		Text t3 = Text.of(TextColors.YELLOW, "[true/false]");
		return Text.of(t1, t2, t3);
	}
}
