package com.gmail.trentech.pjw.listeners;

import java.util.List;
import java.util.Map.Entry;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.command.TabCompleteEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.utils.Help;
import com.gmail.trentech.pjw.utils.Rotation;

public class TabEventManager {

	@Listener
	public void onTabCompleteEvent(TabCompleteEvent event, @First Player player) {
		String rawMessage = event.getRawMessage();
		
		String[] args = rawMessage.split(" ");
		
		List<String> list = event.getTabCompletions();
		
		if((args[0].equalsIgnoreCase("gr") || args[0].equalsIgnoreCase("gamerule"))) {
			if(args.length == 1 || args.length == 2) {
				for(WorldProperties world : Sponge.getServer().getAllWorldProperties()) {
					String name = world.getWorldName();
					
					if(args.length == 2) {
						if(name.contains(args[1].toLowerCase()) && !name.equalsIgnoreCase(args[1])) {
							list.add(name);
						}
					} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
						list.add(name);
					}
				}
			}
			
			if(args.length == 2 || args.length == 3) {
				for(Entry<String, String> gamerule : Sponge.getServer().getDefaultWorld().get().getGameRules().entrySet()) {
					String name = gamerule.getKey();
					
					if(args.length == 3) {
						if(name.contains(args[2].toLowerCase()) && !name.equalsIgnoreCase(args[2])) {
							list.add(name);
						}
					} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
						list.add(name);
					}
				}
			}
			return;
		} else if((!args[0].equalsIgnoreCase("w") && !args[0].equalsIgnoreCase("world")) || args.length < 2) {
			return;
		}

		if(args[1].equalsIgnoreCase("create") || args[1].equalsIgnoreCase("c")) {
			if(args[args.length - 1].equalsIgnoreCase("-d") || args[args.length - 2].equalsIgnoreCase("-d")) {
				for(DimensionType dimensionType : Sponge.getRegistry().getAllOf(DimensionType.class)) {
					String id = dimensionType.getId();
					
					if(args[args.length - 2].equalsIgnoreCase("-d")) {
						if(id.contains(args[args.length - 1].toLowerCase()) && !id.equalsIgnoreCase(args[args.length - 1])) {
							list.add(id);
						}
					} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
						list.add(id);
					}
				}
			} else if(args[args.length - 1].equalsIgnoreCase("-g") || args[args.length - 2].equalsIgnoreCase("-g")) {
				for(GeneratorType generatorType : Sponge.getRegistry().getAllOf(GeneratorType.class)) {
					String id = generatorType.getId();
					
					if(args[args.length - 2].equalsIgnoreCase("-g")) {
						if(id.contains(args[args.length - 1].toLowerCase()) && !id.equalsIgnoreCase(args[args.length - 1])) {
							list.add(id);
						}
					} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
						list.add(id);
					}
				}
			} else if(args[args.length - 1].equalsIgnoreCase("-m") || args[args.length - 2].equalsIgnoreCase("-m")) {
				for(WorldGeneratorModifier generatorType : Sponge.getRegistry().getAllOf(WorldGeneratorModifier.class)) {
					String id = generatorType.getId();
					
					if(args[args.length - 2].equalsIgnoreCase("-m")) {
						if(id.contains(args[args.length - 1].toLowerCase()) && !id.equalsIgnoreCase(args[args.length - 1])) {
							list.add(id);
						}
					} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
						list.add(id);
					}
				}
			}
		} else if(args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("dl")) {
			if(args.length == 2 || args.length == 3) {
				for(WorldProperties world : Sponge.getServer().getUnloadedWorlds()) {
					String name = world.getWorldName();
					
					if(args.length == 3) {
						if(name.contains(args[2].toLowerCase()) && !name.equalsIgnoreCase(args[2])) {
							list.add(name);
						}
					} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
						list.add(name);
					}
				}
			}
		} else if(args[1].equalsIgnoreCase("difficulty") || args[1].equalsIgnoreCase("df")) {
			if(args.length == 2 || args.length == 3) {
				for(WorldProperties world : Sponge.getServer().getAllWorldProperties()) {
					String name = world.getWorldName();
					
					if(args.length == 3) {
						if(name.contains(args[2].toLowerCase()) && !name.equalsIgnoreCase(args[2])) {
							list.add(name);
						}
					} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
						list.add(name);
					}
				}
			}
			
			if(args.length == 3 || args.length == 4) {
				for(Difficulty difficulty : Sponge.getRegistry().getAllOf(Difficulty.class)) {
					String id = difficulty.getName();
					
					if(args.length == 4) {
						if(id.contains(args[3].toLowerCase()) && !id.equalsIgnoreCase(args[3])) {
							list.add(id);
						}
					} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
						list.add(id);
					}
				}
			}
		} else if(args[1].equalsIgnoreCase("enable") || args[1].equalsIgnoreCase("e")) {
			if(args.length == 2 || args.length == 3) {
				for(WorldProperties world : Sponge.getServer().getAllWorldProperties()) {
					String name = world.getWorldName();
					
					if(args.length == 3) {
						if(name.contains(args[2].toLowerCase()) && !name.equalsIgnoreCase(args[2])) {
							list.add(name);
						}
					} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
						list.add(name);
					}
				}
			}
			
			if(args.length == 3 || args.length == 4) {
				String t = "true";
				String f = "false";
				
				if(args.length == 4) {
					if((t.contains(args[3].toLowerCase()) && !t.equalsIgnoreCase(args[3]))) {
						list.add(t);
					} else if((f.contains(args[3].toLowerCase()) && !f.equalsIgnoreCase(args[3]))) {
						list.add(f);
					}
				} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
					list.add(t);
					list.add(f);
				}
			}
		} else if(args[1].equalsIgnoreCase("fill") || args[1].equalsIgnoreCase("f")) {
			if(args.length == 2 || args.length == 3) {
				for(WorldProperties world : Sponge.getServer().getAllWorldProperties()) {
					String name = world.getWorldName();
					
					if(args.length == 3) {
						if(name.contains(args[2].toLowerCase()) && !name.equalsIgnoreCase(args[2])) {
							list.add(name);
						}
					} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
						list.add(name);
					}
				}
			}
		} else if(args[1].equalsIgnoreCase("gamemode") || args[1].equalsIgnoreCase("gm")) {
			if(args.length == 2 || args.length == 3) {
				for(WorldProperties world : Sponge.getServer().getAllWorldProperties()) {
					String name = world.getWorldName();
					
					if(args.length == 3) {
						if(name.contains(args[2].toLowerCase()) && !name.equalsIgnoreCase(args[2])) {
							list.add(name);
						}
					} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
						list.add(name);
					}
				}
			}
			
			if(args.length == 3 || args.length == 4) {
				for(GameMode gameMode : Sponge.getRegistry().getAllOf(GameMode.class)) {
					String id = gameMode.getName();
					
					if(args.length == 4) {
						if(id.contains(args[3].toLowerCase()) && !id.equalsIgnoreCase(args[3])) {
							list.add(id);
						}
					} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
						list.add(id);
					}
				}
			}
		} else if(args[1].equalsIgnoreCase("gamerule") || args[1].equalsIgnoreCase("gr")) {

		} else if(args[1].equalsIgnoreCase("hardcore") || args[1].equalsIgnoreCase("h")) {
			if(args.length == 2 || args.length == 3) {
				for(WorldProperties world : Sponge.getServer().getAllWorldProperties()) {
					String name = world.getWorldName();
					
					if(args.length == 3) {
						if(name.contains(args[2].toLowerCase()) && !name.equalsIgnoreCase(args[2])) {
							list.add(name);
						}
					} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
						list.add(name);
					}
				}
			}
			
			if(args.length == 3 || args.length == 4) {
				String t = "true";
				String f = "false";
				
				if(args.length == 4) {
					if((t.contains(args[3].toLowerCase()) && !t.equalsIgnoreCase(args[3]))) {
						list.add(t);
					} else if((f.contains(args[3].toLowerCase()) && !f.equalsIgnoreCase(args[3]))) {
						list.add(f);
					}
				} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
					list.add(t);
					list.add(f);
				}
			}
		} else if(args[1].equalsIgnoreCase("help") || args[1].equalsIgnoreCase("gr")) {
			if(args.length == 2 || args.length == 3) {
				for (Help help : Help.getAll()) {
					String name = help.getCommand();
					
					if(args.length == 3) {
						if(name.contains(args[2].toLowerCase()) && !name.equalsIgnoreCase(args[2])) {
							list.add(name);
						}
					} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
						list.add(name);
					}
				}
			}
		} else if(args[1].equalsIgnoreCase("import") || args[1].equalsIgnoreCase("i")) {
			if(args.length == 3 || args.length == 4) {
				for(DimensionType dimensionType : Sponge.getRegistry().getAllOf(DimensionType.class)) {
					String id = dimensionType.getId();
					
					if(args.length == 4) {
						if(id.contains(args[3].toLowerCase()) && !id.equalsIgnoreCase(args[3])) {
							list.add(id);
						}
					} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
						list.add(id);
					}
				}
			}
			
			if(args.length == 4 || args.length == 5) {
				for(GeneratorType generatorType : Sponge.getRegistry().getAllOf(GeneratorType.class)) {
					String id = generatorType.getId();
					
					if(args.length == 5) {
						if(id.contains(args[4].toLowerCase()) && !id.equalsIgnoreCase(args[4])) {
							list.add(id);
						}
					} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
						list.add(id);
					}
				}
			}
		} else if(args[1].equalsIgnoreCase("keepspawnloaded") || args[1].equalsIgnoreCase("k")) {
			if(args.length == 2 || args.length == 3) {
				for(WorldProperties world : Sponge.getServer().getAllWorldProperties()) {
					String name = world.getWorldName();
					
					if(args.length == 3) {
						if(name.contains(args[2].toLowerCase()) && !name.equalsIgnoreCase(args[2])) {
							list.add(name);
						}
					} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
						list.add(name);
					}
				}
			}
			
			if(args.length == 3 || args.length == 4) {
				String t = "true";
				String f = "false";
				
				if(args.length == 4) {
					if((t.contains(args[3].toLowerCase()) && !t.equalsIgnoreCase(args[3]))) {
						list.add(t);
					} else if((f.contains(args[3].toLowerCase()) && !f.equalsIgnoreCase(args[3]))) {
						list.add(f);
					}
				} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
					list.add(t);
					list.add(f);
				}
			}
		} else if(args[1].equalsIgnoreCase("load") || args[1].equalsIgnoreCase("l")) {
			if(args.length == 2 || args.length == 3) {
				for(WorldProperties world : Sponge.getServer().getUnloadedWorlds()) {
					String name = world.getWorldName();
					
					if(args.length == 3) {
						if(name.contains(args[2].toLowerCase()) && !name.equalsIgnoreCase(args[2])) {
							list.add(name);
						}
					} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
						list.add(name);
					}
				}
			}
		} else if(args[1].equalsIgnoreCase("properties") || args[1].equalsIgnoreCase("pp")) {
			if(args.length == 2 || args.length == 3) {
				for(WorldProperties world : Sponge.getServer().getAllWorldProperties()) {
					String name = world.getWorldName();
					
					if(args.length == 3) {
						if(name.contains(args[2].toLowerCase()) && !name.equalsIgnoreCase(args[2])) {
							list.add(name);
						}
					} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
						list.add(name);
					}
				}
			}
		} else if(args[1].equalsIgnoreCase("pvp") || args[1].equalsIgnoreCase("p")) {
			if(args.length == 2 || args.length == 3) {
				for(WorldProperties world : Sponge.getServer().getAllWorldProperties()) {
					String name = world.getWorldName();
					
					if(args.length == 3) {
						if(name.contains(args[2].toLowerCase()) && !name.equalsIgnoreCase(args[2])) {
							list.add(name);
						}
					} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
						list.add(name);
					}
				}
			}
			
			if(args.length == 3 || args.length == 4) {
				String t = "true";
				String f = "false";
				
				if(args.length == 4) {
					if((t.contains(args[3].toLowerCase()) && !t.equalsIgnoreCase(args[3]))) {
						list.add(t);
					} else if((f.contains(args[3].toLowerCase()) && !f.equalsIgnoreCase(args[3]))) {
						list.add(f);
					}
				} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
					list.add(t);
					list.add(f);
				}
			}
		} else if(args[1].equalsIgnoreCase("regen") || args[1].equalsIgnoreCase("r")) {
			if(args.length == 2 || args.length == 3) {
				for(WorldProperties world : Sponge.getServer().getAllWorldProperties()) {
					String name = world.getWorldName();
					
					if(args.length == 3) {
						if(name.contains(args[2].toLowerCase()) && !name.equalsIgnoreCase(args[2])) {
							list.add(name);
						}
					} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
						list.add(name);
					}
				}
			}
			
			if(args.length == 3 || args.length == 4) {
				String t = "true";
				String f = "false";
				
				if(args.length == 4) {
					if((t.contains(args[3].toLowerCase()) && !t.equalsIgnoreCase(args[3]))) {
						list.add(t);
					} else if((f.contains(args[3].toLowerCase()) && !f.equalsIgnoreCase(args[3]))) {
						list.add(f);
					}
				} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
					list.add(t);
					list.add(f);
				}
			}
		} else if(args[1].equalsIgnoreCase("rename") || args[1].equalsIgnoreCase("rn")) {
			if(args.length == 2 || args.length == 3) {
				for(WorldProperties world : Sponge.getServer().getAllWorldProperties()) {
					String name = world.getWorldName();
					
					if(args.length == 3) {
						if(name.contains(args[2].toLowerCase()) && !name.equalsIgnoreCase(args[2])) {
							list.add(name);
						}
					} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
						list.add(name);
					}
				}
			}
		} else if(args[1].equalsIgnoreCase("setspawn") || args[1].equalsIgnoreCase("s")) {
			if(args.length == 2 || args.length == 3) {
				for(WorldProperties world : Sponge.getServer().getAllWorldProperties()) {
					String name = world.getWorldName();
					
					if(args.length == 3) {
						if(name.contains(args[2].toLowerCase()) && !name.equalsIgnoreCase(args[2])) {
							list.add(name);
						}
					} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
						list.add(name);
					}
				}
			}
		} else if(args[1].equalsIgnoreCase("teleport") || args[1].equalsIgnoreCase("tp")) {
			if(args.length == 2 || args.length == 3) {
				for(WorldProperties world : Sponge.getServer().getAllWorldProperties()) {
					String name = world.getWorldName();
					
					if(args.length == 3) {
						if(name.contains(args[2].toLowerCase()) && !name.equalsIgnoreCase(args[2])) {
							list.add(name);
						}
					} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
						list.add(name);
					}
				}
			} else if(args[args.length - 1].equalsIgnoreCase("-d") || args[args.length - 2].equalsIgnoreCase("-d")) {
				for (Rotation rotation : Rotation.values()) {
					String id = rotation.getName();
					
					if(args[args.length - 2].equalsIgnoreCase("-d")) {
						if(id.contains(args[args.length - 1].toLowerCase()) && !id.equalsIgnoreCase(args[args.length - 1])) {
							list.add(id);
						}
					} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
						list.add(id);
					}
				}
			}
		} else if(args[1].equalsIgnoreCase("unload") || args[1].equalsIgnoreCase("u")) {
			if(args.length == 2 || args.length == 3) {
				for(World world : Sponge.getServer().getWorlds()) {
					String name = world.getName();
					
					if(args.length == 3) {
						if(name.contains(args[2].toLowerCase()) && !name.equalsIgnoreCase(args[2])) {
							list.add(name);
						}
					} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
						list.add(name);
					}
				}
			}
		}
	}
}
