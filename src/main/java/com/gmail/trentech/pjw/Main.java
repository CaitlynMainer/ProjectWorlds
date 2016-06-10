package com.gmail.trentech.pjw;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.commands.CommandManager;
import com.gmail.trentech.pjw.extra.VoidWorldGeneratorModifier;
import com.gmail.trentech.pjw.io.Migrator;
import com.gmail.trentech.pjw.io.SpongeData;
import com.gmail.trentech.pjw.listeners.EventManager;
import com.gmail.trentech.pjw.utils.ConfigManager;
import com.gmail.trentech.pjw.utils.Resource;

import me.flibio.updatifier.Updatifier;
import ninja.leaping.configurate.ConfigurationNode;

@Updatifier(repoName = "ProjectWorlds", repoOwner = "TrenTech", version = Resource.VERSION)
@Plugin(id = Resource.ID, name = Resource.NAME, authors = Resource.AUTHOR, url = Resource.URL, dependencies = {@Dependency(id = "Updatifier", optional = true)})
public class Main {

	private static Game game;
	private static Logger log;	
	private static PluginContainer plugin;

	private static HashMap<String, WorldGeneratorModifier> modifiers = new HashMap<>();
	
	@Listener
    public void onPreInitialization(GamePreInitializationEvent event) {
		game = Sponge.getGame();
		plugin = getGame().getPluginManager().getPlugin(Resource.ID).get();
		log = getPlugin().getLogger();
    }
	
    @Listener
    public void onInitialization(GameInitializationEvent event) {   	
    	getGame().getEventManager().registerListeners(this, new EventManager());

    	getGame().getRegistry().register(WorldGeneratorModifier.class, new VoidWorldGeneratorModifier());
    	
    	for(WorldGeneratorModifier modifier : getGame().getRegistry().getAllOf(WorldGeneratorModifier.class)) {
    		getModifiers().put(modifier.getId(), modifier);
    	}

    	getGame().getCommandManager().register(this, new CommandManager().cmdWorld, "world", "w");
    	getGame().getCommandManager().register(this, new CommandManager().cmdGamerule, "gamerule", "gr");
    	
    	new ConfigManager().init();
    }

    @Listener
    public void onAboutToStartServer(GameAboutToStartServerEvent event) {
    	ConfigurationNode node = new ConfigManager().getConfig().getNode("dimension_ids");
    	
    	SpongeData.getIds().addAll(node.getChildrenList().stream().map(ConfigurationNode::getInt).collect(Collectors.toList()));
    	
    	if(!node.isVirtual() && new File(Main.getGame().getServer().getDefaultWorldName()).exists()) {
        	Migrator.init();
    	}
    }
    
    @Listener
    public void onStartedServer(GameStartedServerEvent event) {
    	ConfigManager configManager = new ConfigManager();

    	List<Integer> list = new ArrayList<>();
    	
		for(WorldProperties world : Main.getGame().getServer().getAllWorldProperties()) {
			Main.getGame().getServer().saveWorldProperties(world);
			list.add((int) world.getPropertySection(DataQuery.of("SpongeData")).get().get(DataQuery.of("dimensionId")).get());
		}
		
		SpongeData.setIds(list);
		
		configManager.getConfig().getNode("dimension_ids").setValue(list).setComment("DO NOT EDIT");
		configManager.save();
    }
    
    public static Logger getLog() {
        return log;
    }
    
	public static Game getGame() {
		return game;
	}

	public static PluginContainer getPlugin() {
		return plugin;
	}

	public static HashMap<String, WorldGeneratorModifier> getModifiers() {
		return modifiers;
	}
}