package com.gmail.trentech.pjw.commands;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.WorldCreationSettings.Builder;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.Help;
import com.gmail.trentech.pjw.utils.Utils;

public class CMDRegen implements CommandExecutor {
	
	public CMDRegen() {
		Help help = new Help("regen", "regen", " Regenerates a world. You can preserve the seed or generate new random");
		help.setSyntax(" /world regen <world> [true/false]\n /w r <world>  [true/false]");
		help.setExample(" /world regen MyWorld\n /world regen MyWorld true");
		help.save();
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("name")) {
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}

		String worldName = args.<String>getOne("name").get();
		
		if(worldName.equalsIgnoreCase("@w") && src instanceof Player) {
			worldName = ((Player) src).getWorld().getName();
		}
		
		boolean preserve = false;
		if(args.hasAny("value")) {
			preserve = Boolean.parseBoolean(args.<String>getOne("value").get());
		}
		
		if(Main.getGame().getServer().getWorld(worldName).isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " must be unloaded before you can rename"));
			return CommandResult.empty();
		}

		if(!Main.getGame().getServer().getWorldProperties(worldName).isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " does not exist"));
			return CommandResult.empty();
		}
		WorldProperties properties = Main.getGame().getServer().getWorldProperties(worldName).get();

		Builder builder = WorldCreationSettings.builder().name(properties.getWorldName()).dimension(properties.getDimensionType()).generatorSettings(properties.getGeneratorSettings());
		
		if(preserve) {
			builder.seed(properties.getSeed());
		}

		try {
			CompletableFuture<Boolean> delete = Main.getGame().getServer().deleteWorld(properties);
			while(!delete.isDone()) {}
			if(!delete.get()) {
				src.sendMessage(Text.of(TextColors.DARK_RED, "Could not delete ", worldName));
				return CommandResult.empty();
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Regenerating world.."));
		WorldCreationSettings settings = builder.enabled(true).loadsOnStartup(true).build();

		final Optional<WorldProperties> optProperties = Main.getGame().getServer().createWorldProperties(settings);

        if (!optProperties.isPresent()) {
        	src.sendMessage(Text.of(TextColors.DARK_RED, "Could not regenerate ", worldName));
        	return CommandResult.empty();
        }

		Optional<World> load = Main.getGame().getServer().loadWorld(optProperties.get());

		if(!load.isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "Could not load ", worldName));
			return CommandResult.empty();
		}

		Utils.createPlatform(load.get().getSpawnLocation().getRelative(Direction.DOWN));
		
		src.sendMessage(Text.of(TextColors.DARK_GREEN, worldName, " regenerated successfully"));
		
		return CommandResult.success();
	}
	
	private Text invalidArg() {
		Text t1 = Text.of(TextColors.YELLOW, "/world regen ");
		Text t2 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Enter world name"))).append(Text.of("<world> ")).build();
		Text t3 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Preserve seed? true/false"))).append(Text.of("[value]")).build();
		return Text.of(t1,t2,t3);
	}
}
