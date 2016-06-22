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

public class CMDProperties implements CommandExecutor {

	public CMDProperties() {
		Help help = new Help("properties", "properties", " View all properties associated with a world");
		help.setSyntax(" /world properties <world>\n /w p <world>");
		help.setExample(" /world properties\n /world properties MyWorld");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String worldName;
		if (args.hasAny("name")) {
			worldName = args.<String> getOne("name").get();
		} else {
			if (!(src instanceof Player)) {
				Text t1 = Text.of(TextColors.YELLOW, "/world properties ");
				Text t2 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Enter world or @w for current world"))).append(Text.of("[world] ")).build();
				src.sendMessage(Text.of(t1, t2));
				return CommandResult.empty();
			}
			Player player = (Player) src;
			worldName = player.getWorld().getName();
		}

		if (!Main.getGame().getServer().getWorldProperties(worldName).isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " does not exist"));
			return CommandResult.empty();
		}
		WorldProperties properties = Main.getGame().getServer().getWorldProperties(worldName).get();

		List<Text> list = new ArrayList<>();

		list.add(Text.of(TextColors.GREEN, "Name: ", TextColors.WHITE, worldName));
		list.add(Text.of(TextColors.GREEN, "UUID: ", TextColors.WHITE, properties.getUniqueId().toString()));
		list.add(Text.of(TextColors.GREEN, "Enabled: ", TextColors.WHITE, properties.isEnabled()));
		list.add(Text.of(TextColors.GREEN, "Dimension Type: ", TextColors.WHITE, properties.getDimensionType().getName().toUpperCase()));
		list.add(Text.of(TextColors.GREEN, "Generator Type: ", TextColors.WHITE, properties.getGeneratorType().getName()));
		list.add(Text.of(TextColors.GREEN, "Seed: ", TextColors.WHITE, properties.getSeed()));
		list.add(Text.of(TextColors.GREEN, "GameMode: ", TextColors.WHITE, properties.getGameMode().getName().toUpperCase()));
		list.add(Text.of(TextColors.GREEN, "Difficulty: ", TextColors.WHITE, properties.getDifficulty().getName().toUpperCase()));
		list.add(Text.of(TextColors.GREEN, "PVP: ", TextColors.WHITE, properties.isPVPEnabled()));
		list.add(Text.of(TextColors.GREEN, "Keep Spawn Loaded: ", TextColors.WHITE, properties.doesKeepSpawnLoaded()));
		list.add(Text.of(TextColors.GREEN, "Hardcore: ", TextColors.WHITE, properties.isHardcore()));
		list.add(Text.of(TextColors.GREEN, "Spawn On Death: ", TextColors.WHITE, properties.getGameRule("spawnOnDeath").get()));
		list.add(Text.of(TextColors.GREEN, "Nether Portal: ", TextColors.WHITE, properties.getGameRule("netherPortal").get()));
		list.add(Text.of(TextColors.GREEN, "End Portal: ", TextColors.WHITE, properties.getGameRule("endPortal").get()));
		list.add(Text.of(TextColors.GREEN, "Freeze Weather: ", TextColors.WHITE, properties.getGameRule("doWeatherCycle").get()));
		list.add(Text.of(TextColors.GREEN, "Command Block Output: ", TextColors.WHITE, properties.getGameRule("commandBlockOutput").get()));
		list.add(Text.of(TextColors.GREEN, "Freeze Time: ", TextColors.WHITE, properties.getGameRule("doDaylightCycle").get()));
		list.add(Text.of(TextColors.GREEN, "Fire Spread: ", TextColors.WHITE, properties.getGameRule("doFireTick").get()));
		list.add(Text.of(TextColors.GREEN, "Mob Loot: ", TextColors.WHITE, properties.getGameRule("doMobLoot").get()));
		list.add(Text.of(TextColors.GREEN, "Mob Spawning: ", TextColors.WHITE, properties.getGameRule("doMobSpawning").get()));
		list.add(Text.of(TextColors.GREEN, "Tile Drops: ", TextColors.WHITE, properties.getGameRule("doTileDrops").get()));
		list.add(Text.of(TextColors.GREEN, "Keep Inventory: ", TextColors.WHITE, properties.getGameRule("keepInventory").get()));
		list.add(Text.of(TextColors.GREEN, "Log Admin Commands: ", TextColors.WHITE, properties.getGameRule("logAdminCommands").get()));
		list.add(Text.of(TextColors.GREEN, "Mob Griefing: ", TextColors.WHITE, properties.getGameRule("mobGriefing").get()));
		list.add(Text.of(TextColors.GREEN, "Natural Regeneration: ", TextColors.WHITE, properties.getGameRule("naturalRegeneration").get()));
		list.add(Text.of(TextColors.GREEN, "Tick Speed: ", TextColors.WHITE, properties.getGameRule("randomTickSpeed").get()));
		list.add(Text.of(TextColors.GREEN, "Reduced Debug Info: ", TextColors.WHITE, properties.getGameRule("reducedDebugInfo").get()));
		list.add(Text.of(TextColors.GREEN, "Send Command Feedback: ", TextColors.WHITE, properties.getGameRule("sendCommandFeedback").get()));
		list.add(Text.of(TextColors.GREEN, "Show Death Messages: ", TextColors.WHITE, properties.getGameRule("showDeathMessages").get()));

		if (src instanceof Player) {
			PaginationList.Builder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();

			pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Settings")).build());

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
