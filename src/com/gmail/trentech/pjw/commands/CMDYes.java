package com.gmail.trentech.pjw.commands;

import java.util.HashMap;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Titles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class CMDYes implements CommandExecutor {

	public static HashMap<Player, Location<World>> players = new HashMap<>();
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)){
			return CommandResult.success();
		}
		Player player = (Player) src;
		
		if(!players.containsKey(player)){
			return CommandResult.success();
		}
		Location<World> location = players.get(player);
		
		player.setLocation(location);
		player.sendTitle(Titles.of(Texts.of(TextColors.GOLD, location.getExtent().getName()), Texts.of(TextColors.DARK_PURPLE, "x: ", location.getExtent().getSpawnLocation().getBlockX(), ", y: ", location.getExtent().getSpawnLocation().getBlockY(),", z: ", location.getExtent().getSpawnLocation().getBlockZ())));

		return CommandResult.success();
	}

}
