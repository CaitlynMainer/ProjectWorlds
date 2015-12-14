package com.gmail.trentech.pjw.portal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.ConfigManager;
import com.gmail.trentech.pjw.utils.Resource;

import ninja.leaping.configurate.ConfigurationNode;

public class Portal implements Iterable<BlockSnapshot> {

	protected final String worldName;
	protected final String worldDest;

	protected BlockState block = BlockState.builder().blockType(BlockTypes.STONE).build();
	protected final int x1, y1, z1;
	protected final int x2, y2, z2;

	public Portal(BlockState block, String worldDest, Location<World> loc1, Location<World> loc2) {
		if (!loc1.getExtent().equals(loc2.getExtent())){
			throw new IllegalArgumentException("Locations must be on the same world");
		}
		this.worldDest = worldDest;

		if(block != null){
			this.block = block;
		}
		
		this.worldName = loc1.getExtent().getName();
		this.x1 = Math.min(loc1.getBlockX(), loc2.getBlockX());
		this.y1 = Math.min(loc1.getBlockY(), loc2.getBlockY());
		this.z1 = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
		this.x2 = Math.max(loc1.getBlockX(), loc2.getBlockX());
		this.y2 = Math.max(loc1.getBlockY(), loc2.getBlockY());
		this.z2 = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
	}
	
	public Portal(BlockState block, String worldName, String worldDest, int x1, int y1, int z1, int x2, int y2, int z2) {
		if(block != null){
			this.block = block;
		}
		this.worldName = worldName;
		this.worldDest = worldDest;
		this.x1 = Math.min(x1, x2);
		this.y1 = Math.min(y1, y2);
		this.z1 = Math.min(z1, z2);
		this.x2 = Math.max(x1, x2);
		this.y2 = Math.max(y1, y2);
		this.z2 = Math.max(z1, z2);
	}

	private World getWorld() {
		if (!Main.getGame().getServer().getWorld(worldName).isPresent()) {
			throw new IllegalStateException("World " + this.worldName + " is not loaded");
		}
		return Main.getGame().getServer().getWorld(worldName).get();
	}

	public Iterator<BlockSnapshot> iterator() {
		return new PortalIterator(this.getWorld(), this.x1, this.y1, this.z1, this.x2, this.y2, this.z2);
	}

	@Override
	public String toString() {
		return new String("Cuboid: " + this.worldName + "," + this.x1 + "," + this.y1 + "," + this.z1 + "=>" + this.x2 + "," + this.y2 + "," + this.z2);
	}

	public class PortalIterator implements Iterator<BlockSnapshot> {
		private World world;
		private int baseX, baseY, baseZ;
		private int x, y, z;
		private int sizeX, sizeY, sizeZ;

		public PortalIterator(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
			this.world = world;
			this.baseX = x1;
			this.baseY = y1;
			this.baseZ = z1;
			this.sizeX = Math.abs(x2 - x1) + 1;
			this.sizeY = Math.abs(y2 - y1) + 1;
			this.sizeZ = Math.abs(z2 - z1) + 1;
			this.x = this.y = this.z = 0;
		}

		public boolean hasNext() {
			return this.x < this.sizeX && this.y < this.sizeY && this.z < this.sizeZ;
		}

		public BlockSnapshot next() {
			BlockSnapshot b = this.world.createSnapshot(this.baseX + this.x, this.baseY + this.y, this.baseZ + this.z);
			if (++x >= this.sizeX) {
				this.x = 0;
				if (++this.y >= this.sizeY) {
					this.y = 0;
					++this.z;
				}
			}
			return b;
		}
	}

	public List<String> getLocations(){
		List<String> locations = new ArrayList<>();
	    for (BlockSnapshot block : this){
	    	Location<World> location = block.getLocation().get();

	    	String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

	    	ConfigManager loader = new ConfigManager("portals.conf");
			
			if(loader.getPortal(locationName) != null){
				return null;
			}
			
			locations.add(locationName);
	    }
	    return locations;
	}
	
	public void build(){

        List<String> locations = getLocations();

        for(String loc : locations){
        	String[] info = loc.split("\\.");

        	Location<World> location = Main.getGame().getServer().getWorld(info[0]).get().getLocation(Integer.parseInt(info[1]), Integer.parseInt(info[2]), Integer.parseInt(info[3]));
        	
        	ConfigurationNode config = new ConfigManager().getConfig();
        	
        	if(config.getNode("Options", "Portal", "Replace-Frame").getBoolean()){	
            	if(location.getBlockType() != BlockTypes.AIR){
            		location.setBlock(block);
            	}
        	}
        	
        	Resource.spawnParticles(location, 1.0, false);
        }
        
        String uuid = UUID.randomUUID().toString();
        
        ConfigManager loaderPortals = new ConfigManager("portals.conf");
		ConfigurationNode configPortals = loaderPortals.getConfig();
		
        configPortals.getNode("Portals", uuid, "Locations").setValue(locations);
        configPortals.getNode("Portals", uuid, "World").setValue(worldDest);

        loaderPortals.save();
	}

}
