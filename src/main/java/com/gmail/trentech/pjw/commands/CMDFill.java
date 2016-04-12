package com.gmail.trentech.pjw.commands;

import java.util.HashMap;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldBorder;
import org.spongepowered.api.world.WorldBorder.ChunkPreGenerate;
import org.spongepowered.api.world.storage.WorldProperties;

import com.flowpowered.math.vector.Vector3d;
import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.ConfigManager;
import com.gmail.trentech.pjw.utils.Help;

public class CMDFill implements CommandExecutor {
	
	public static HashMap<String, Task> list = new HashMap<>();
	
	public CMDFill(){
		String alias = new ConfigManager().getConfig().getNode("settings", "commands", "world").getString();
		
		Help help = new Help("fill", " Pre generate chunks in a world outwards from center spawn");
		help.setSyntax(" /world fill <world> <diameter>\n /" + alias + " f <world> <diameter>");
		help.setExample(" /world fill MyWorld 1000\n /world fill MyWorld stop");
		CMDHelp.getList().add(help);
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("name")) {
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}
		String worldName = args.<String>getOne("name").get();
		
		if(worldName.equalsIgnoreCase("@w")){
			if(src instanceof Player){
				worldName = ((Player) src).getWorld().getName();
			}
		}

		if(!args.hasAny("value")) {
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}
		String value = args.<String>getOne("value").get();
		
		if(value.equalsIgnoreCase("stop")){
			if(!list.containsKey(worldName)){
				src.sendMessage(Text.of(TextColors.YELLOW, "Pre-Generator not running for this world"));
				return CommandResult.empty();
			}
			list.get(worldName).cancel();
			list.remove(worldName);
			
			src.sendMessage(Text.of(TextColors.DARK_GREEN, "Pre-Generator cancelled for ", worldName));
			return CommandResult.success();
		}
		double diameter;
		try{
			diameter = Double.parseDouble(value);
		}catch(Exception e){
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}

		if(list.containsKey(worldName)){
			if (Main.getGame().getScheduler().getScheduledTasks(Main.getPlugin()).contains(list.get(worldName))) {
				src.sendMessage(Text.of(TextColors.YELLOW, "Pre-Generator already running for this world"));
				return CommandResult.empty();
			}
			list.remove(worldName);		
		}
		
		if(!Main.getGame().getServer().getWorldProperties(worldName).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " does not exist"));
			return CommandResult.empty();
		}
		WorldProperties properties = Main.getGame().getServer().getWorldProperties(worldName).get();
		
		if(!Main.getGame().getServer().getWorld(properties.getUniqueId()).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " must be loaded"));
			return CommandResult.empty();
		}
		World world = Main.getGame().getServer().getWorld(properties.getUniqueId()).get();
		
		WorldBorder border = world.getWorldBorder();
		
		Vector3d center = border.getCenter();
		double diam = border.getDiameter();
		
		border.setCenter(world.getSpawnLocation().getX(), world.getSpawnLocation().getZ());
		border.setDiameter(diameter);
		
		ChunkPreGenerate generator = border.newChunkPreGenerate(world).owner(Main.getPlugin());
		generator.logger(Main.getLog());

		Task task = generator.start();

		list.put(worldName, task);
		
		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Pre-Generator starting for ", worldName));
		src.sendMessage(Text.of(TextColors.GOLD, "This can cause significant lag while running"));
		
		border.setDiameter(diam);
		border.setCenter(center.getX(), center.getZ());
		
		return CommandResult.success();
	}
	
	private Text invalidArg(){
		Text t1 = Text.of(TextColors.YELLOW, "/world fill ");
		Text t2 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Enter world name"))).append(Text.of("<world> ")).build();
		Text t3 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("enter diameter or \"stop\""))).append(Text.of("<diameter>")).build();
		return Text.of(t1,t2,t3);
	}
}
