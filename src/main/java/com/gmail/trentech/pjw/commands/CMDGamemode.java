package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.Gamemode;
import com.gmail.trentech.pjw.utils.Help;

public class CMDGamemode implements CommandExecutor {

	public CMDGamemode() {
		Help help = new Help("gamemode", "gamemode", " Change gamemode of the specified world");
		help.setSyntax(" /world gamemode <world> <gamemode>\n /w g <world> <gamemode>");
		help.setExample(" /world gamemode\n /world gamemode MyWorld SURVIVAL\n /world gamemode @w 1\n /world gamemode @a 2");
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

		Collection<WorldProperties> worlds = new ArrayList<>();

		if (worldName.equalsIgnoreCase("@a")) {
			worlds = Main.getGame().getServer().getAllWorldProperties();
		} else {
			if (!Main.getGame().getServer().getWorldProperties(worldName).isPresent()) {
				src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " does not exist"));
				return CommandResult.empty();
			}
			worlds.add(Main.getGame().getServer().getWorldProperties(worldName).get());
		}

		GameMode gamemode = null;

		if (args.hasAny("value")) {
			String value = args.<String> getOne("value").get();

			Optional<GameMode> optionalGamemode = Optional.empty();
			try {
				int index = Integer.parseInt(value);
				optionalGamemode = Gamemode.get(index);
			} catch (Exception e) {
				optionalGamemode = Gamemode.get(value);
			}

			if (!optionalGamemode.isPresent()) {
				src.sendMessage(Text.of(TextColors.DARK_RED, "Invalid gamemode Type"));
				return CommandResult.empty();
			}
			gamemode = optionalGamemode.get();
		}

		List<Text> list = new ArrayList<>();

		for (WorldProperties properties : worlds) {
			if (gamemode == null) {
				list.add(Text.of(TextColors.GREEN, properties.getWorldName(), ": ", TextColors.WHITE, properties.getGameMode().getName().toUpperCase()));
				continue;
			}

			properties.setGameMode(gamemode);

			src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set gamemode of ", worldName, " to ", TextColors.YELLOW, gamemode.getName().toUpperCase()));
		}

		if (!list.isEmpty()) {
			if (src instanceof Player) {
				PaginationList.Builder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();

				pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "GameMode")).build());

				pages.contents(list);

				pages.sendTo(src);
			} else {
				for (Text text : list) {
					src.sendMessage(text);
				}
			}
		}

		return CommandResult.success();
	}

	private Text invalidArg() {
		Text t1 = Text.of(TextColors.YELLOW, "/world gamemode ");
		Text t2 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Enter world or @w for current world"))).append(Text.of("<world> ")).build();

		Text.Builder builder = null;

		Gamemode[] gamemodes = Gamemode.values();

		for (Gamemode gamemode : gamemodes) {
			if (builder == null) {
				builder = Text.builder().append(Text.of(gamemode.getIndex(), ": ", gamemode.getGameMode().getName()));
			} else {
				builder.append(Text.of("\n", gamemode.getIndex(), ": ", gamemode.getGameMode().getName()));
			}
		}

		Text t3 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(builder.build())).append(Text.of("[value]")).build();

		return Text.of(t1, t2, t3);
	}
}