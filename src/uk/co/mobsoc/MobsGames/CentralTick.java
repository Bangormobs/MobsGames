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
