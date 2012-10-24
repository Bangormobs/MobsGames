package uk.co.mobsoc.MobsGames.Game;

import java.util.HashMap;

import org.bukkit.entity.Player;

import uk.co.mobsoc.MobsGames.MobsGames;
import uk.co.mobsoc.MobsGames.Player.AbstractPlayerClass;
import uk.co.mobsoc.MobsGames.Player.TagIt;
import uk.co.mobsoc.MobsGames.Player.TagRunner;

public class Tag extends AbstractGame{
	@Override
	public void onStartCountdown(){
	}
	
	@Override
	public void onParticipantQuit(AbstractPlayerClass player){
		if(player instanceof TagIt){
			MobsGames.announce(player.getPlayerName()+" has forfiet!");			
		}
	}
	
	@Override
	public AbstractPlayerClass getDefaultClassForPlayer(String player){
		return new TagRunner(player);
	}
	
	@Override
	public void onStart(){
	}
	
	public String getIt(){
		for(AbstractPlayerClass apc: getParticipants()){
			if(apc instanceof TagIt){
				return apc.getPlayerName();
			}
		}
		return null;
	}
	
	@Override
	public void onEnd(){
		if(cancelEarly){
			MobsGames.announce("The game has been ended without a winner!");
		}else{
			
		}
	}
	boolean cancelEarly=true;
	
	@Override
	public boolean checkGameOver(){
		cancelEarly=true;
		if(noPlayers){
			return true;
		}
		if(timeElapsed>=timeLimit){
			cancelEarly=false;
			return true;
		}
		return false;
		
	}
	
	HashMap<String, Integer> timeAsIt = new HashMap<String, Integer>();
	boolean noPlayers=false;
	
	@Override
	public void onTick(){
		if(hasBegun()){
			String p = getIt();
			if(p==null){
				AbstractPlayerClass apc = getRandomParticipant(TagRunner.class);
				if(apc==null){
					MobsGames.announce("No players remaining!");
					noPlayers=true;
					//fin = true;
				}else{
					setPlayerClass(new TagIt(apc.getPlayerName()));
				}
				return;
			}
			String k =p.toLowerCase();
			if(timeAsIt.containsKey(k)){
				timeAsIt.put(k,timeAsIt.get(k)+1);
			}else{
				timeAsIt.put(k, 1);
			}
		}


	}
}
