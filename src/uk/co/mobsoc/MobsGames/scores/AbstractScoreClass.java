package uk.co.mobsoc.MobsGames.scores;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import uk.co.mobsoc.MobsGames.MobsGames;

public class AbstractScoreClass implements Runnable{
	public static HashMap<String, Integer> values = new HashMap<String, Integer>();
	Scoreboard board;
	Objective objective;
	private boolean canContinue=true;
	public void onEnable(){
		
	}
	public void onDisable(){
		
	}
	
	public static int getValue(String name){
		name = name.toLowerCase();
		return values.get(name);
	}
	
	public static void setValue(String name, int val){
		name = name.toLowerCase();
		values.put(name, val);
	}
	
	@Override
	public void run() {
		reschedule();
		if(objective.getCriteria().equalsIgnoreCase("dummy")){
			for(Player player: Bukkit.getOnlinePlayers()){
				Score score = objective.getScore(player);
				score.setScore(getValue(player.getName()));
			}
		}
		for(Player player: Bukkit.getOnlinePlayers()){
			player.setScoreboard(board);
		}
	}
	
	public void reschedule() {
		if(!canContinue){ onDisable(); return; }
		Bukkit.getScheduler().scheduleSyncDelayedTask(MobsGames.instance, this, 5);
	}
	public void stop() {
		canContinue=false;
	}
	
}
