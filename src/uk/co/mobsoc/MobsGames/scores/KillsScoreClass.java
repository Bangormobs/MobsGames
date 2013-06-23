package uk.co.mobsoc.MobsGames.scores;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class KillsScoreClass extends AbstractScoreClass{
	@Override
	public void onEnable(){
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		board = manager.getNewScoreboard();
		objective = board.getObjective("killCount");
		if(objective==null){
			objective= board.registerNewObjective("killCount", "playerKillCount");
			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
			objective.setDisplayName("Kill Count");
		}
	}

}
