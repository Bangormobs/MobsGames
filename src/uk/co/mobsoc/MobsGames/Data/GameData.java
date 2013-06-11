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

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;

import uk.co.mobsoc.MobsGames.MobsGames;
import uk.co.mobsoc.MobsGames.Game.AbstractGame;

/**
 * Stores Data about an exact instance of a game.
 * @author triggerhapp
 *
 */
public class GameData {
	/** Stores the name of the Game Type this game is */
	public String klass;
	/** Stores other data that the specific Game Type might wish to use */
	@SuppressWarnings("unused")
	private String otherData;
	/** Stores the name of this exact version of the game */
	public String key;
	/** Stores the time in seconds this exact game should continue for */
	public int timeLimit;
	/** Stores the minimum number of participants required for this game */
	public int minPlayers;
	/** Stores the maximum number of participants required for this game */
	public int maxPlayers;
	/** Stores the world this game is held in, or % if any world will do */
	public String world;
	/** Stores if this game is allowed to be started automatically without an admin present */
	public boolean autostart;
	public HashMap<String, String> extraData = new HashMap<String, String>();
	
	public void setOtherData(String oD){
		String[] keyValPairs = oD.split("\\|");
		for(String pair : keyValPairs){
			if(pair==""){ continue; }
			String[] split = pair.split("=");
			if(split.length==2){
				extraData.put(split[0], split[1]);
			}else{

				System.out.println("Unknown extra data : '"+pair+"' in game '"+key+"'");
			}
		}
	}
	
	public String getOtherData(){
		String s = "", sep="";
		for(String key : extraData.keySet()){
			s=s+sep+key+"="+extraData.get(key);
			sep="|";
		}
		return s;
	}

	/**
	 * Returns the world this game is held in, or NULL if any is allowed
	 * @return 
	 */
	public World getWorld(){
		World w = Bukkit.getWorld(world);
		if(w==null){ MobsGames.getLog().warning("Could not find world '"+world+"'"); }
		return w;
	}
	
	/**
	 * Returns a new instance of this game. 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public AbstractGame createGame() {
		AbstractGame agame=null;
		Class myLeanKlass = MobsGames.getGameType(klass);
		if(myLeanKlass == null){
			System.out.println("There is no Game available for '"+klass+"'");
			return null;
		}
		try {
			agame = (AbstractGame)myLeanKlass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
		agame.setGameData(this);
		return agame;
		
	}
	
}
