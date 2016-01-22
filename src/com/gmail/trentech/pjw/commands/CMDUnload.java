package com.gmail.trentech.pjw.commands;

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
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.ConfigManager;
import com.gmail.trentech.pjw.utils.Help;

public class CMDUnload implements CommandExecutor {

	public CMDUnload(){
		String alias = new ConfigManager().getConfig().getNode("settings", "commands", "world").getString();
		
		Help help = new Help("unload", " Unloads specified world. If players are in world, they will be teleported to default spawn");
		help.setSyntax(" /world unload <world>\n /" + alias + " u <world>");
		help.setExample(" /world unload MyWorld");
		CMDHelp.getList().add(help);
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("name")) {
			src.sendMessage(Text.of(TextColors.GOLD, "/world unload <world>"));
			return CommandResult.empty();
		}
		String worldName = args.<String>getOne("name").get();
		
		if(worldName.equalsIgnoreCase("@w")){
			if(src instanceof Player){
				worldName = ((Player) src).getWorld().getName();
			}
		}
		
		if(Main.getGame().getServer().getDefaultWorld().get().getWorldName().equalsIgnoreCase(worldName)){
			src.sendMessage(Text.of(TextColors.DARK_RED, "Default world cannot be unloaded"));
			return CommandResult.empty();
		}
		
		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " does not exist"));
			return CommandResult.empty();
		}
		World world = Main.getGame().getServer().getWorld(worldName).get();
		
		for(Entity entity : world.getEntities()){
			if(entity instanceof Player){
				Player player = (Player) entity;
				WorldProperties properties = Main.getGame().getServer().getDefaultWorld().get();
				player.setLocationSafely(Main.getGame().getServer().getWorld(properties.getWorldName()).get().getSpawnLocation());
				player.sendMessage(Text.of(TextColors.YELLOW, properties.getWorldName(), " is being unloaded"));
			}
		}
		
		if(!Main.getGame().getServer().unloadWorld(world)){
			src.sendMessage(Text.of(TextColors.DARK_RED, "Could not unload ", worldName));
			return CommandResult.empty();
		}
		
		src.sendMessage(Text.of(TextColors.DARK_GREEN, worldName, " unloaded successfully"));
		
		return CommandResult.success();
	}


}
