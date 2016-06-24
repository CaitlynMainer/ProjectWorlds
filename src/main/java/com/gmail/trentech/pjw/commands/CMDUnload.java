package com.gmail.trentech.pjw.commands;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.ConfigManager;
import com.gmail.trentech.pjw.utils.Help;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDUnload implements CommandExecutor {

	public CMDUnload() {
		Help help = new Help("unload", "unload", " Unloads specified world. If players are in world, they will be teleported to default spawn");
		help.setSyntax(" /world unload <world>\n /w u <world>");
		help.setExample(" /world unload MyWorld");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!args.hasAny("name")) {
			src.sendMessage(Text.of(TextColors.GOLD, "/world unload <world>"));
			return CommandResult.empty();
		}
		String worldName = args.<String> getOne("name").get();

		if (worldName.equalsIgnoreCase("@w") && src instanceof Player) {
			worldName = ((Player) src).getWorld().getName();
		}

		if (Main.getGame().getServer().getDefaultWorld().get().getWorldName().equalsIgnoreCase(worldName)) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "Default world cannot be unloaded"));
			return CommandResult.empty();
		}

		if (!Main.getGame().getServer().getWorld(worldName).isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " does not exist"));
			return CommandResult.empty();
		}
		World world = Main.getGame().getServer().getWorld(worldName).get();

		ConfigurationNode node = new ConfigManager().getConfig().getNode("options");

		World defaultWorld = Main.getGame().getServer().getWorld(Main.getGame().getServer().getDefaultWorld().get().getWorldName()).get();

		String joinWorldName = node.getNode("first_join", "world").getString();

		Optional<World> optionalWorld = Main.getGame().getServer().getWorld(joinWorldName);

		if (optionalWorld.isPresent()) {
			defaultWorld = optionalWorld.get();
		}

		for (Entity entity : world.getEntities()) {
			if (entity instanceof Player) {
				Player player = (Player) entity;
				
				player.setLocationSafely(defaultWorld.getSpawnLocation());
				player.sendMessage(Text.of(TextColors.YELLOW, worldName, " is being unloaded"));
			}
		}

		if (!Main.getGame().getServer().unloadWorld(world)) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "Could not unload ", worldName));
			return CommandResult.empty();
		}

		src.sendMessage(Text.of(TextColors.DARK_GREEN, worldName, " unloaded successfully"));

		return CommandResult.success();
	}

}
