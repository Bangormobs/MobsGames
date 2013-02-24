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
