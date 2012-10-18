package uk.co.mobsoc.MobsGames.Player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import uk.co.mobsoc.MobsGames.Data.LocationData;
import uk.co.mobsoc.MobsGames.Data.Utils;


public class AbstractPlayerClass {
	/**
	 * Send player to the prepared waiting room. Does not have to be the same location as /spawn takes you to
	 */
	public void teleToSpawn(){
		LocationData ld = Utils.getOneLocation("spawn");
		if(ld==null){
			System.out.println("No game location 'spawn' set. No spawn boxing");
		}else{
			getPlayer().teleport(ld.getLocation());
		}
	}
	
	/**
	 * Simple test that two players are the same.
	 * @param p1
	 * @param p2
	 * @return true if both players passed in are the same player
	 */
	public boolean isPlayerEqual(Player p1, Player p2){
		if(p1==null || p2==null){ return false; }
		return p1.getEntityId()==p2.getEntityId();
	}
	
	private String playerName;
	@Override
	public boolean equals(Object o){
		if(o instanceof AbstractPlayerClass){
			if(playerName.equalsIgnoreCase(((AbstractPlayerClass) o).getPlayerName())){
				return true;
			}
		}
		return false;
	}
	public AbstractPlayerClass(Player player){
		playerName=player.getName().toLowerCase();
	}
	/**
	 * Returns the Bukkit Player class related to this Player
	 * @return
	 */
	public Player getPlayer(){
		return Bukkit.getPlayer(playerName);
	}
	/**
	 * Returns the name of the player
	 * @return
	 */
	public String getPlayerName(){
		return playerName;
	}
	/**
	 * Needs Overriding! This is called when a player is moved from another class into this one.
	 */
	public void onEnable(){
	}
	/**
	 * Needs Overriding! This is called when a player is moved to another class from this one.
	 */
	public void onDisable(){
		
	}
	/**
	 * Needs Overriding! This is called for every Player-related event for this specific player. Handle all the events you need, ignore the others.
	 * @param event
	 */
	public void onEvent(Event event){
		System.out.println(this+" is not handling events correctly!");
	}
}
