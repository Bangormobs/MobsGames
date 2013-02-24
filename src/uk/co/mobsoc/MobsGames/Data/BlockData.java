/*
 * This file is part of MobsGames.

    MobsGames is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    MobsGames is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with MobsGames.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.mobsoc.MobsGames.Data;

import org.bukkit.World;
import org.bukkit.block.Block;
/**
 * Stores data about Blocks that were or will be, also stores metadata that will be useful to games
 * @author triggerhapp
 *
 */
public class BlockData {
	World world;
	int x,y,z;
	/** The Block ID that was or will be in the location this class has stored */
	public int id;
	/** The Block Metadata that was or will be in the location this class has stored */
	public int data;
	/** Meta-data that games will use to decide how this affects gameplay */
	public String type;
	/** Allow physics to take effect immediately */
	public boolean doPhysics;
	/** The name that this is stored in the MySQL database as. Null is acceptable if it is generated internally (say, by RevertingListener) */
	public String name;
	/** The name of the Game that this is related to, % means it is to be used for every game */
	public String key;
	
	public BlockData(){
		
	}

	public BlockData(Block block){
		world = block.getWorld();
		x = block.getX();
		y = block.getY();
		z = block.getZ();
		id = block.getTypeId();
		data = block.getData();
		doPhysics = true;
	}

	/**
	 * For timer-based blocks, find out what percentage of the time elapsed this block should trigger at
	 * @return The time elapsed in percent of total time, or -1 if not a timer block
	 */
	public int getTimePercent(){
		if(type.toLowerCase().startsWith("time:")){
			return Integer.parseInt(type.substring(5));
		}
		return -1;
	}

	/**
	 * Set the block at this Location to the ID/data defined in the fields
	 */
	public void doPlacement() {
		world.getBlockAt(x, y, z).setTypeIdAndData(id, (byte) data, doPhysics);
		
	}

	/**
	 * 
	 * @param Another BlockData to compare to
	 * @return true is both BlockData are the same World and location, false otherwise
	 */
	public boolean isEqualLocation(BlockData bd){
		return bd.x == x &&
		       bd.y == y && 
		       bd.z == z &&
		       bd.world.getName().equalsIgnoreCase(world.getName());
	}

	/**
	 * 
	 * @return a BlockData instance where the location is the same as this one, but the ID and data are derived from the block currently present
	 */
	public BlockData getCurrentBlockDataAt(){
		return new BlockData(world.getBlockAt(x,y,z));
	}
	
	/**
	 * 
	 * @return a Block instance which has the data of the block in the world specified in the location specified
	 */
	public Block getCurrectBlockAt(){
		return world.getBlockAt(x,y,z);
	}
}
