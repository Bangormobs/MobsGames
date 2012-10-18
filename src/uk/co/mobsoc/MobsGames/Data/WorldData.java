package uk.co.mobsoc.MobsGames.Data;

import java.util.UUID;

import org.bukkit.World;

/**
 * Stores data about the worlds on the server.
 * Used to validate Saved games data.
 * @author triggerhapp
 *
 */
		
public class WorldData {
	String worldName;
	long seed;
	long timeStamp;
	
	public WorldData(World world, long timestamp){
		worldName = world.getName();
		seed = world.getSeed();
		timeStamp = timestamp;		
	}
	
	public WorldData(World world){
		this(world, Utils.getWorldTimestamp(world.getName()));
	}

	public WorldData() {
		// TODO Auto-generated constructor stub
	}
}
