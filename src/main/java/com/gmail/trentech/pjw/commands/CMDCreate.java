package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.WorldCreationSettings.Builder;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.io.SpongeData;
import com.gmail.trentech.pjw.utils.ConfigManager;
import com.gmail.trentech.pjw.utils.Help;

public class CMDCreate implements CommandExecutor {

	public static List<String> worlds = new ArrayList<>();

	public CMDCreate() {
		Help help = new Help("create", "create", " Allows you to create new worlds with any combination of optional arguments -d " + "for dimension type, -g for generator type, -s for seed and -m for generator modifiers");
		help.setSyntax(" /world create <world> [-d <type>] [-g <generator>] [-m <modifer>]  [-s <seed>]\n /w cr <world> [-d <type>] [-g <generator>] [-m <modifer>]  [-s <seed>]");
		help.setExample(" /world create NewWorld -s -12309830198412353456\n /world create NewWorld -d overworld -g overworld\n" + " /world create NewWorld -d nether -m sponge:skylands\n /world create -m pjw:void");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!args.hasAny("name")) {
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}
		String worldName = args.<String> getOne("name").get();

		if (worldName.equalsIgnoreCase("-d") || worldName.equalsIgnoreCase("-g") || worldName.equalsIgnoreCase("-m") || worldName.equalsIgnoreCase("-s")) {
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}

		if (Main.getGame().getServer().getWorldProperties(worldName).isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " already exists"));
			return CommandResult.empty();
		}

		Builder builder = WorldCreationSettings.builder().name(worldName);

		if (args.hasAny("type")) {
			String type = args.<String> getOne("type").get();

			Optional<DimensionType> optionalType = Main.getGame().getRegistry().getType(DimensionType.class, type);

			if (!optionalType.isPresent()) {
				src.sendMessage(Text.of(TextColors.DARK_RED, "Invalid Dimension Type"));
				src.sendMessage(invalidArg());
				return CommandResult.empty();
			}
			builder.dimension(optionalType.get());
		}

		if (args.hasAny("generator")) {
			String type = args.<String> getOne("generator").get();

			Optional<GeneratorType> optionalType = Main.getGame().getRegistry().getType(GeneratorType.class, type);

			if (!optionalType.isPresent()) {
				src.sendMessage(Text.of(TextColors.DARK_RED, "Invalid Generator Type"));
				src.sendMessage(invalidArg());
				return CommandResult.empty();
			}
			builder.generator(optionalType.get());
		}

		if (args.hasAny("modifier")) {
			String modifier = args.<String> getOne("modifier").get();

			if (!Main.getModifiers().containsKey(modifier)) {
				src.sendMessage(Text.of(TextColors.DARK_RED, "Invalid Modifier Type"));
				src.sendMessage(invalidArg());
				return CommandResult.empty();
			}
			builder.generatorModifiers(Main.getModifiers().get(modifier));
		}

		if (args.hasAny("seed")) {
			String seed = args.<String> getOne("seed").get();

			try {
				Long s = Long.parseLong(seed);
				builder.seed(s);
			} catch (Exception e) {
				builder.seed(seed.hashCode());
			}
		}

		WorldCreationSettings settings = builder.enabled(true).keepsSpawnLoaded(true).loadsOnStartup(true).build();

		final Optional<WorldProperties> optionalProperties = Main.getGame().getServer().createWorldProperties(settings);

		if (!optionalProperties.isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "something went wrong"));
			return CommandResult.empty();
		}
		WorldProperties properties = optionalProperties.get();

		Main.getGame().getServer().saveWorldProperties(properties);

		SpongeData.getIds().add((int) optionalProperties.get().getPropertySection(DataQuery.of("SpongeData")).get().get(DataQuery.of("dimensionId")).get());

		ConfigManager configManager = new ConfigManager();
		configManager.getConfig().getNode("dimension_ids").setValue(SpongeData.getIds());
		configManager.save();

		worlds.add(worldName);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, worldName, " created successfully"));

		return CommandResult.success();
	}

	private Text invalidArg() {
		Main.getGame().getRegistry().getAllOf(DimensionType.class);
		Text t1 = Text.of(TextColors.YELLOW, "/world create <world> ");
		org.spongepowered.api.text.Text.Builder dimTypes = null;
		for (DimensionType dimType : Main.getGame().getRegistry().getAllOf(DimensionType.class)) {
			if (dimTypes == null) {
				dimTypes = Text.builder().append(Text.of(dimType.getName()));
			} else {
				dimTypes.append(Text.of("\n", dimType.getName()));
			}
		}
		Text t2 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(dimTypes.build())).append(Text.of("[-d <type>] ")).build();
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
		Text t3 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(genTypes.build())).append(Text.of("[-g <generator>] ")).build();
		org.spongepowered.api.text.Text.Builder modifiers = null;
		for (Entry<String, WorldGeneratorModifier> modifier : Main.getModifiers().entrySet()) {
			if (modifiers == null) {
				modifiers = Text.builder().append(Text.of(modifier.getKey()));
			} else {
				modifiers.append(Text.of("\n", modifier.getKey()));
			}
		}
		Text t4 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(modifiers.build())).append(Text.of("[-m <modifier>] ")).build();
		Text t5 = Text.of(TextColors.YELLOW, "[-s <seed>]");
		return Text.of(t1, t2, t3, t4, t5);
	}
}
