package uk.co.mobsoc.MobsGames.Game;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import uk.co.mobsoc.MobsGames.MobsGames;
import uk.co.mobsoc.MobsGames.Data.BlockData;
import uk.co.mobsoc.MobsGames.Data.LocationData;
import uk.co.mobsoc.MobsGames.Data.Utils;
import uk.co.mobsoc.MobsGames.Player.AbstractPlayerClass;
import uk.co.mobsoc.MobsGames.Player.LastManStandingCombatant;
import uk.co.mobsoc.MobsGames.Player.TeamLastManStandingCombatant;

public class TeamLMS extends AbstractGame{
	public static int redW=0, blueW=0;
	ArrayList<BlockData> bds;
	ArrayList<LocationData> redStarts, blueStarts;
	LocationData redSpawn, blueSpawn;
	boolean hasStarted;
	
	@Override
	public void onStartCountdown(){
		bds = Utils.getBlocks(getKey(), "join-red", "%");
		bds.addAll(Utils.getBlocks(getKey(), "join-blue", "%"));
		redStarts = Utils.getLocations(getKey(), "start-red", "%");
		blueStarts = Utils.getLocations(getKey(), "start-blue", "%");
		if(Utils.getLocations(getKey(), "spawn-red", "%").size()>0){
			redSpawn = Utils.getLocations(getKey(), "spawn-red", "%").get(0);
		}
		if(Utils.getLocations(getKey(), "spawn-blue", "%").size()>0){
			blueSpawn = Utils.getLocations(getKey(), "spawn-blue", "%").get(0);
		}
		System.out.println("blue has "+blueStarts.size()+" starts / red has "+redStarts.size()+" starts");
		if(redSpawn!=null){
			System.out.println("red have a valid spawn");
		}
		if(blueSpawn!=null){
			System.out.println("blue have a valid spawn");
		}
		// TODO Allow customisation via game metadata... changing colour and name of teams
		Team tRed = getRedTeam();
		Team tBlue = getBlueTeam();
		tRed.setDisplayName("RedTeam");
		tBlue.setDisplayName("BlueTeam");
		tRed.setPrefix(ChatColor.RED+"");
		tBlue.setPrefix(ChatColor.BLUE+"");
		tRed.setAllowFriendlyFire(false);
		tBlue.setAllowFriendlyFire(false);
		tRed.setCanSeeFriendlyInvisibles(true);
		tBlue.setCanSeeFriendlyInvisibles(true);
		
		MobsGames.announce("A game of Team Last Man standing has been called. Join before 20 seconds!");
	}
	
	@Override
	public LocationData getSpawnFor(AbstractPlayerClass apc) {
		OfflinePlayer op = Bukkit.getOfflinePlayer(apc.getPlayerName());
		if(getRedTeam().hasPlayer(op)){
			System.out.println("Player is Red");
			return redSpawn;
		}else if(getBlueTeam().hasPlayer(op)){
			System.out.println("Player is Blue");
			return blueSpawn;
		}
		System.out.println("Player is Teamless");
		return null;
	}
	
	@Override
	public ArrayList<LocationData> getStartLocationsFor(AbstractPlayerClass apc){
		OfflinePlayer op = Bukkit.getOfflinePlayer(apc.getPlayerName());
		if(getRedTeam().hasPlayer(op)){
			return redStarts;
		}else if(getBlueTeam().hasPlayer(op)){
			return blueStarts;
		}
		// Failsafe I guess?
		return startLocations;
	}
	
	@Override
	public AbstractPlayerClass getDefaultClassForPlayer(String player){
		return new TeamLastManStandingCombatant(player);
	}
	
	@Override
	public void onStart(){
		
	}
	
	public Team getRedTeam(){
		ScoreboardManager sbm = Bukkit.getScoreboardManager();
		Scoreboard sb = sbm.getMainScoreboard();
		Team tRed;
		try{
			tRed = sb.registerNewTeam("red");
		}catch(IllegalArgumentException e){
			tRed = sb.getTeam("red");
		}
		
		return tRed;
	}

	public Team getBlueTeam(){
		ScoreboardManager sbm = Bukkit.getScoreboardManager();
		Scoreboard sb = sbm.getMainScoreboard();
		Team tBlue;
		try{
			tBlue = sb.registerNewTeam("blue");
		}catch(IllegalArgumentException e){
			tBlue = sb.getTeam("blue");
		}
		
		return tBlue;
	}
	
	@Override
	public void onEnd(){
		ScoreboardManager sbm = Bukkit.getScoreboardManager();
		Scoreboard sb = sbm.getMainScoreboard();
		Team tRed = sb.getTeam("red");
		Team tBlue = sb.getTeam("blue");
		int blueplayers = 0, redplayers=0;
		for(OfflinePlayer p : tRed.getPlayers()){
			if(!p.isOnline()){ continue; }
			AbstractPlayerClass apc = this.getPlayerClass(p.getName());
			if(apc instanceof TeamLastManStandingCombatant){
				redplayers++;
			}
		}
		for(OfflinePlayer p : tBlue.getPlayers()){
			if(!p.isOnline()){ continue; }
			AbstractPlayerClass apc = this.getPlayerClass(p.getName());
			if(apc instanceof TeamLastManStandingCombatant){
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
		for(OfflinePlayer p : getRedTeam().getPlayers()){
			if(!p.isOnline()){ continue; }
			AbstractPlayerClass apc = this.getPlayerClass(p.getName());
			if(apc instanceof TeamLastManStandingCombatant){
				redplayers++;
			}
		}
		for(OfflinePlayer p : getBlueTeam().getPlayers()){
			if(!p.isOnline()){ continue; }
			AbstractPlayerClass apc = this.getPlayerClass(p.getName());
			if(apc instanceof TeamLastManStandingCombatant){
				blueplayers++;
			}
		}
		return blueplayers == 0 || redplayers == 0;
	}
	
	@Override
	public void onTick(){
	}

	public void joinTeam(Player player, Block clickedBlock) {
		for(BlockData bd : bds){
			if(bd.isEqualLocation(clickedBlock)){
				if(bd.type.equalsIgnoreCase("join-red")){
					System.out.println("Adding "+player.getDisplayName()+" to Red");
					player.sendMessage("You have joined the Red team");
					getRedTeam().addPlayer(player);
					MobsGames.getGame().addParticipant(player.getName());
				}else if(bd.type.equalsIgnoreCase("join-blue")){
					player.sendMessage("You have joined the Blue team");
					System.out.println("Adding "+player.getDisplayName()+" to Blue");
					getBlueTeam().addPlayer(player);
					MobsGames.getGame().addParticipant(player.getName());
				}else{
					System.out.println("Unsure how to follow instruction to '"+bd.type+"' for '"+player.getDisplayName()+"'");
				}
			}
		}
	}
}
