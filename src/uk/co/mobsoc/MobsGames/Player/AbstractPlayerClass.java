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
package uk.co.mobsoc.MobsGames.Player;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import uk.co.mobsoc.MobsGames.MobsGames;
import uk.co.mobsoc.MobsGames.Data.LocationData;
import uk.co.mobsoc.MobsGames.Data.Utils;


public class AbstractPlayerClass {
	private int livesLeft = -2;
	// I really do NOT like this. but since bukkti.getPlayer() fails for respawn... this is my way around
	private Player player = null;
	
	private Location lastLocation;
	
	/**
	 * Send player to the prepared waiting room. Does not have to be the same location as /spawn takes you to
	 */
	public void teleToSpawn(){
		LocationData ld = MobsGames.getGame().getSpawnFor(this);
		if(ld==null){
			ld = Utils.getOneLocation("spawn");
			if(ld==null){
				System.out.println("No game location 'spawn' set. No spawn boxing");
				return;
			}
		}
		teleportTo(ld.getLocation());
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
	public AbstractPlayerClass(String playerName){
		this.playerName=playerName.toLowerCase();
	}
	/**
	 * Very special use case. In PlayerRespawnEvent the normal code paths will fail. set this from event.getPlayer() then set to null as soon as you're done
	 * do not EVER keep this set for longer than needed.
	 * @param player
	 */
	public void setPlayer(Player player){
		this.player = player;
	}
	
	public OfflinePlayer getOfflinePlayer(){
		if(player!=null){ return player; }
		return Bukkit.getOfflinePlayer(playerName);
	}
	/**
	 * Returns the Bukkit Player class related to this Player
	 * @return
	 */
	public Player getPlayer(){
		if(player!=null){ return player; }
		return Bukkit.getPlayer(playerName);
	}
	/**
	 * Returns the name of the player
	 * @returns
	 */
	public String getPlayerName(){
		return playerName;
	}
	/**s
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

	/**
	 * DO NOT OVERRIDE! Called Externally when a player returns from a short logout. Teleports player to last location they should have been teleported to
	 * @param event
	 */
	public void onLogin(PlayerJoinEvent event) {
		if(lastLocation!=null){
			getPlayer().teleport(lastLocation);
			lastLocation=null;
		}
	}

	/**
	 * DO NOT OVERRIDE! Called whenever the player should be teleported to a location. This should be used in the off-chance the player is offline
	 */
	public void teleportTo(Location l){
		if(getPlayer()!=null){
			getPlayer().teleport(l);
		}else{
			lastLocation=l;
		}
	}
	
	/**
	 * Decreases lives left and returns true if they have a lift left.
	 * returns false is the player has no remaining lives. 
	 * @return
	 */
	public boolean canRespawn(){
		if(livesLeft == -2){
			livesLeft = MobsGames.getGame().getLives(); 
		}
		if(livesLeft < 0){ 
			System.out.println("Lives = "+livesLeft+" and that means Inf... honest");
			return true; 
		}
		if(livesLeft > 0){
			//getPlayer().sendMessage("You have "+livesLeft+" lives left!");
			livesLeft--;
			return true;
		}
		return false;
	}

}
