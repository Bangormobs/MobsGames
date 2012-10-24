package uk.co.mobsoc.MobsGames.Game;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import uk.co.mobsoc.MobsGames.MobsGames;
import uk.co.mobsoc.MobsGames.Player.AbstractPlayerClass;
import uk.co.mobsoc.MobsGames.Player.LastManStandingCombatant;

public class LastManStanding extends AbstractGame{

	int count;
	boolean hasStarted;
	@Override
	public void onStartCountdown(){
		MobsGames.announce("A game of Last Man standing has been called. Join before 20 seconds!");
	}
	
	@Override
	public AbstractPlayerClass getDefaultClassForPlayer(String player){
		return new LastManStandingCombatant(player);
	}
	
	@Override
	public void onStart(){
		count=0;
	}
	
	public ArrayList<String> playersLeft(){
		ArrayList<String> list = new ArrayList<String>();
		for(AbstractPlayerClass apc : getParticipants()){
			if(apc instanceof LastManStandingCombatant){
				list.add(apc.getPlayerName());
			}
		}
		return list;
	}
	
	@Override
	public void onEnd(){
		if(playersLeft().size()==1){
			MobsGames.announce("We have a winner!");
			MobsGames.announce(playersLeft().get(0)+" has won this round!");
		}else{
			MobsGames.announce("The game has been ended without a winner!");
		}
	}
	
	@Override
	public boolean checkGameOver(){
		return playersLeft().size()==1;
		
	}
	
	@Override
	public void onTick(){
		if(count==0){
			//MobsGames.announce("A game of Last Man Standing has been started. Join in the next 20 seconds!");
		}
		count++;
	}

}
