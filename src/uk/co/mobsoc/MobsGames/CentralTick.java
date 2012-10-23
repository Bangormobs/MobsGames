package uk.co.mobsoc.MobsGames;

import org.bukkit.Bukkit;

/**
 * This Class is run on Bukkit Timer to tick every 1 second in-game.
 * Server lag may cause Server ticks to not be running at 20 ticks a second, in which case it will affect the count of seconds in the server.
 * @author triggerhapp
 *
 */
public class CentralTick implements Runnable{
	public CentralTick(){
		addTick();
	}

	/**
	 * Automatically called every 1 second
	 */
	@Override
	public void run() {
		addTick();
		if(MobsGames.getGame()!=null){
			MobsGames.getGame().tick();
		}else{
			if(MobsGames.autostart){
				MobsGames.instance.chooseRandomGame();
			}
		}
		
	}

	/**
	 * Adds itself back into the list of scheduled operations.
	 */
	private void addTick() {
		// Every second.
		Bukkit.getScheduler().scheduleSyncDelayedTask(MobsGames.instance, this, 20);
	}

}
