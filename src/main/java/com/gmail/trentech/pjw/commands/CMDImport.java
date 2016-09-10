package com.gmail.trentech.pjw.commands;

import java.io.IOException;

import org.spongepowered.api.Sponge;
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
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.storage.WorldProperties;

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
		String worldName = args.<String> getOne("name").get();

		if (Sponge.getServer().getWorld(worldName).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, worldName, " is already loaded"));
		}

		WorldData worldData = new WorldData(worldName);

		if (!worldData.exists()) {
			throw new CommandException(Text.of(TextColors.RED, worldName, " is not a valid world"));
		}

		SpongeData spongeData = new SpongeData(worldName);

		if (spongeData.exists()) {
			src.sendMessage(Text.of(TextColors.RED, "Sponge world detected"));
			src.sendMessage(Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Click command for more information "))).onClick(TextActions.runCommand("/pjw:world load")).append(Text.of(" /world load")).build());
			return CommandResult.success();
		}

		DimensionType dimensionType = args.<DimensionType> getOne("dimensionType").get();
		GeneratorType generatorType = args.<GeneratorType> getOne("generatorType").get();

		WorldArchetype settings = WorldArchetype.builder().dimension(dimensionType)
				.generator(generatorType).enabled(true).keepsSpawnLoaded(true).loadsOnStartup(true).build(worldName, worldName);

		WorldProperties properties;
		try {
			properties = Sponge.getServer().createWorldProperties(worldName, settings);
		} catch (IOException e) {
			e.printStackTrace();
			throw new CommandException(Text.of(TextColors.RED, "Something went wrong. Check server log for details"));
		}

		Sponge.getServer().saveWorldProperties(properties);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, worldName, " imported successfully"));

		return CommandResult.success();
	}

}
