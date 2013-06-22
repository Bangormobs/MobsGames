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

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import uk.co.mobsoc.MobsGames.Player.AbstractPlayerClass;

/**
 * Once a player logs out, it starts a timer. If they remain offline long enough they lose.
 * @author triggerhapp
 *
 */
public class LogoutTimer implements Runnable{
	AbstractPlayerClass apc = null;
	public static HashMap<String, Integer> logouts = new HashMap<String, Integer>();
	
	public LogoutTimer(AbstractPlayerClass apc, int timer){
		if(logouts.containsKey(apc.getPlayerName().toLowerCase())){
			Bukkit.getScheduler().cancelTask(logouts.get(apc.getPlayerName().toLowerCase()));
		}
		logouts.put(apc.getPlayerName().toLowerCase(), Bukkit.getScheduler().scheduleSyncDelayedTask(MobsGames.instance, this, timer*20));
		this.apc = apc;
		apc.setPlayer(apc.getPlayer());
	}

	@Override
	public void run() {
		if(MobsGames.getGame()==null){ return ; }
		Player player = Bukkit.getPlayer(apc.getPlayerName());
		if(player==null){
			MobsGames.getGame().removeParticipant(apc.getPlayerName(),true);
		}
		apc.setPlayer(null);
	}

}
