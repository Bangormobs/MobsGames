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
		String[] keyValPairs = oD.split("|");
		HashMap<String, String> pairs = new HashMap<String, String>();
		for(String pair : keyValPairs){
			if(pair==""){ continue; }
			String[] split = pair.split("=");
			if(split.length==2){
				pairs.put(split[0], split[1]);
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
