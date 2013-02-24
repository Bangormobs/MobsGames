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
package uk.co.mobsoc.MobsGames.Game;

import java.util.ArrayList;


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
