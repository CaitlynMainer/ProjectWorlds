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
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.Help;

public class CMDDifficulty implements CommandExecutor {

	public CMDDifficulty() {
		Help help = new Help("difficulty", "difficulty", " Set the difficulty level for each world");
		help.setSyntax(" /world difficulty <world> [value]\n /w df <world> [value]");
		help.setExample(" /world difficulty MyWorld\n /world difficulty MyWorld HARD\n /world difficulty @w PEACEFUL\n /world difficulty @a SURVIVAL");
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

		Difficulty difficulty = null;

		if (args.hasAny("value")) {
			String value = args.<String> getOne("value").get();

			Optional<Difficulty> optionalDifficulty = Main.getGame().getRegistry().getType(Difficulty.class, value);

			if (!optionalDifficulty.isPresent()) {
				src.sendMessage(Text.of(TextColors.DARK_RED, "Invalid difficulty type"));
				return CommandResult.empty();
			}
			difficulty = optionalDifficulty.get();
		}

		List<Text> list = new ArrayList<>();

		for (WorldProperties properties : worlds) {
			if (difficulty == null) {
				list.add(Text.of(TextColors.GREEN, properties.getWorldName(), ": ", TextColors.WHITE, properties.getDifficulty().getName().toUpperCase()));
				continue;
			}

			properties.setDifficulty(difficulty);

			src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set difficulty of ", worldName, " to ", TextColors.YELLOW, difficulty.getName().toUpperCase()));
		}

		if (!list.isEmpty()) {
			if (src instanceof Player) {
				PaginationList.Builder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();

				pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Difficulty")).build());

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
		Text t1 = Text.of(TextColors.YELLOW, "/world difficulty ");
		Text t2 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Enter world or @w for current world or @a for all worlds"))).append(Text.of("<world> ")).build();
		Text t3 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("PEACEFUL\nEASY\nNORMAL\nHARD"))).append(Text.of("[value]")).build();
		return Text.of(t1, t2, t3);
	}

}
