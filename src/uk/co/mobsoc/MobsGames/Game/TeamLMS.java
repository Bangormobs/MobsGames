package uk.co.mobsoc.MobsGames.Game;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import uk.co.mobsoc.MobsGames.MobsGames;
import uk.co.mobsoc.MobsGames.Player.AbstractPlayerClass;
import uk.co.mobsoc.MobsGames.Player.LastManStandingCombatant;

public class TeamLMS extends AbstractGame{
	Scoreboard sb;
	Team tRed, tBlue;
	boolean hasStarted;
	@Override
	public void onStartCountdown(){
		MobsGames.announce("A game of Team Last Man standing has been called. Join before 20 seconds!");
	}
	
	@Override
	public AbstractPlayerClass getDefaultClassForPlayer(String player){
		return new LastManStandingCombatant(player);
	}
	
	@Override
	public void onStart(){
		ScoreboardManager sbm = Bukkit.getScoreboardManager();
		sb = sbm.getNewScoreboard();
		// TODO Allow customisation via game metadata... changing colour and name of teams
		tRed = sb.registerNewTeam("red");
		tBlue = sb.registerNewTeam("blue");
		tRed.setDisplayName("Red Team");
		tBlue.setDisplayName("Blue Team");
		tRed.setPrefix(Color.RED+"");
		tBlue.setPrefix(Color.BLUE+"");
		tRed.setAllowFriendlyFire(false);
		tBlue.setAllowFriendlyFire(false);
		tRed.setCanSeeFriendlyInvisibles(true);
		tBlue.setCanSeeFriendlyInvisibles(true);
	}
	
	@Override
	public void onEnd(){
		int blueplayers = 0, redplayers=0;
		for(OfflinePlayer p : tRed.getPlayers()){
			if(!p.isOnline()){ continue; }
			AbstractPlayerClass apc = this.getPlayerClass(p.getName());
			if(apc instanceof LastManStandingCombatant){
				redplayers++;
			}
		}
		for(OfflinePlayer p : tBlue.getPlayers()){
			if(!p.isOnline()){ continue; }
			AbstractPlayerClass apc = this.getPlayerClass(p.getName());
			if(apc instanceof LastManStandingCombatant){
				blueplayers++;
			}
		}
		if(blueplayers == 0 && redplayers == 0){
			MobsGames.announce("It's a draw!");
		}else if(blueplayers == 0){
			MobsGames.announce("Red team wins!");
		}else if(redplayers == 0){
			MobsGames.announce("Blue team wins!");
		}
	}
	
	@Override
	public boolean checkGameOver(){
		int blueplayers = 0, redplayers=0;
		for(OfflinePlayer p : tRed.getPlayers()){
			if(!p.isOnline()){ continue; }
			AbstractPlayerClass apc = this.getPlayerClass(p.getName());
			if(apc instanceof LastManStandingCombatant){
				redplayers++;
			}
		}
		for(OfflinePlayer p : tBlue.getPlayers()){
			if(!p.isOnline()){ continue; }
			AbstractPlayerClass apc = this.getPlayerClass(p.getName());
			if(apc instanceof LastManStandingCombatant){
				blueplayers++;
			}
		}
		return blueplayers == 0 || redplayers == 0;
	}
	
	@Override
	public void onTick(){
	}
}
