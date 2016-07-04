package com.gmail.trentech.pjw.commands;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.io.SpongeData;
import com.gmail.trentech.pjw.io.WorldData;
import com.gmail.trentech.pjw.utils.Help;

public class CMDImport implements CommandExecutor {

	public CMDImport() {
		Help help = new Help("import", "import", " Import worlds not native to Sponge");
		help.setSyntax(" /world import <world> <type> <generator>\n /w i <world> <type> <generator>");
		help.setExample(" /world import NewWorld overworld overworld");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!args.hasAny("name")) {
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}

		String worldName = args.<String> getOne("name").get();

		if (Main.getGame().getServer().getWorld(worldName).isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " is already loaded"));
			return CommandResult.empty();
		}

		WorldData worldData = new WorldData(worldName);

		if (!worldData.exists()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " is not a valid world"));
			return CommandResult.empty();
		}

		SpongeData spongeData = new SpongeData(worldName);

		if (spongeData.exists()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "Sponge world detected"));
			src.sendMessage(Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Click command for more information "))).onClick(TextActions.runCommand("/pjw:world load")).append(Text.of(" /world load")).build());
			return CommandResult.empty();
		}

		if (!args.hasAny("type") || !args.hasAny("generator")) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "Must specify dimension and generator type when importing worlds"));
			return CommandResult.empty();
		}
		String type = args.<String> getOne("type").get();
		String generator = args.<String> getOne("generator").get();

		if (!Main.getGame().getRegistry().getType(DimensionType.class, type).isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "Invalid dimension type"));
			return CommandResult.empty();
		}
		DimensionType dimensionType = Main.getGame().getRegistry().getType(DimensionType.class, type).get();

		if (!Main.getGame().getRegistry().getType(GeneratorType.class, generator).isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "Invalid generator type"));
			return CommandResult.empty();
		}
		GeneratorType generatorType = Main.getGame().getRegistry().getType(GeneratorType.class, generator).get();

		WorldCreationSettings settings = WorldCreationSettings.builder().name(worldName).dimension(dimensionType).generator(generatorType).enabled(true).keepsSpawnLoaded(true).loadsOnStartup(true).build();

		Optional<WorldProperties> optProperties = Main.getGame().getServer().createWorldProperties(settings);

		if (!optProperties.isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "something went wrong"));
			return CommandResult.empty();
		}
		WorldProperties properties = optProperties.get();

		Main.getGame().getServer().saveWorldProperties(properties);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, worldName, " imported successfully"));

		return CommandResult.success();
	}

	private Text invalidArg() {
		Main.getGame().getRegistry().getAllOf(DimensionType.class);
		Text t1 = Text.of(TextColors.YELLOW, "/world import <world> ");
		org.spongepowered.api.text.Text.Builder dimTypes = null;
		for (DimensionType dimType : Main.getGame().getRegistry().getAllOf(DimensionType.class)) {
			if (dimTypes == null) {
				dimTypes = Text.builder().append(Text.of(dimType.getName()));
			} else {
				dimTypes.append(Text.of("\n", dimType.getName()));
			}
		}
		Text t2 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(dimTypes.build())).append(Text.of("<type> ")).build();
		org.spongepowered.api.text.Text.Builder genTypes = Text.builder();
		for (GeneratorType genType : Main.getGame().getRegistry().getAllOf(GeneratorType.class)) {
			if (!genType.getName().equalsIgnoreCase("debug_all_block_states") && !genType.getName().equalsIgnoreCase("default_1_1")) {
				if (genTypes == null) {
					genTypes = Text.builder().append(Text.of(genType.getName()));
				} else {
					genTypes.append(Text.of(genType.getName(), "\n"));
				}
			}
		}
		Text t3 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(genTypes.build())).append(Text.of("<generator>")).build();
		return Text.of(t1, t2, t3);
	}
}
