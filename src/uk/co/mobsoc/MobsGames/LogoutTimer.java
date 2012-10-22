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
	}

	@Override
	public void run() {
		if(MobsGames.getGame()==null){ return ; }
		Player player = Bukkit.getPlayer(apc.getPlayerName());
		if(player==null){
			MobsGames.getGame().removeParticipant(apc.getPlayerName(),true);
		}
	}

}
