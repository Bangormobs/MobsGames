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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
/**
 * Stores Data about Locations related to games
 * @author triggerhapp
 *
 */
public class LocationData {
	/** Stores the world this Location belongs to */
	public String worldName;
	/** Stores the type of Location this is */
	public String type;
	/** Stores the unique name of this Location */
	public String name;
	int x,y,z;
	/** Stores the name of the game this is linked to, or % if any game is allowed */
	public String key;
	
	/**
	 * Returns a Bukkit-Location defined by the data in this instance
	 * @return
	 */
	public Location getLocation(){
		World w = Bukkit.getWorld(worldName);
		if(w==null){
			System.out.println("Error, null world in Location from SQL");
			return null;
		}
		return new Location(w, x+0.5, y, z+0.5);
		
	}
	
	public LocationData(){
		
	}
	
	/**
	 * Create a new instance
	 * @param l Location that this instance should hold
	 * @param n Name of this Location
	 * @param t Type of Location
	 * @param k Name of the Game this Location belongs to or % for all
	 */
	public LocationData(Location l, String n, String t, String k){
		worldName = l.getWorld().getName();
		x= (int) l.getX();
		y= (int) l.getY();
		z= (int) l.getZ();
		name = n;
		type = t;
		key = k ;
	}
	
}
