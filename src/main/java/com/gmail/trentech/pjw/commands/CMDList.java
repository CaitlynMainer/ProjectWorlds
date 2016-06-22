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
import org.spongepowered.api.text.Text.Builder;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.Help;
import com.google.common.collect.Lists;

public class CMDList implements CommandExecutor {

	public CMDList() {
		Help help = new Help("list", "list", " Lists all known worlds, loaded or unloaded");
		help.setSyntax(" /world list\n /w ls");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		List<Text> list = new ArrayList<>();

		for (World world : Main.getGame().getServer().getWorlds()) {
			Builder builder = Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of(TextColors.WHITE, "Click to view properies")));
			builder.onClick(TextActions.runCommand("/pjw:world properties " + world.getName())).append(Text.of(TextColors.GREEN, world.getName(), ": ", Lists.newArrayList(world.getLoadedChunks()).size(), " Loaded chunks, ", world.getEntities().size(), " Entities"));
			list.add(builder.build());
		}

		for (WorldProperties world : Main.getGame().getServer().getUnloadedWorlds()) {
			Builder builder = Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of(TextColors.WHITE, "Click to load world")));
			builder.onClick(TextActions.runCommand("/pjw:world load " + world.getWorldName())).append(Text.of(TextColors.GREEN, world.getWorldName(), ": ", TextColors.GRAY, " Unloaded"));
			list.add(builder.build());
		}

		if (src instanceof Player) {
			PaginationList.Builder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();

			pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Worlds")).build());

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
