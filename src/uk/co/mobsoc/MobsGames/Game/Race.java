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

import org.bukkit.entity.Player;

import uk.co.mobsoc.MobsGames.MobsGames;
import uk.co.mobsoc.MobsGames.Data.BlockData;
import uk.co.mobsoc.MobsGames.Player.AbstractPlayerClass;
import uk.co.mobsoc.MobsGames.Player.RaceRunner;

public class Race extends AbstractGame{

	int count;
	boolean hasStarted;
	public boolean canPvP, canPvM;

	@Override
	public void onStartCountdown(){
		for(BlockData bd : blockAlterations){
			if(bd.type.equalsIgnoreCase("target")){
				winners.add(bd);
			}
		}
		if(winners.size()==0){
			System.out.println("No 'target' type blocks assosciated with this game... No one can win");
		}
		MobsGames.announce("A race has been called. Join before 20 seconds!");
		canPvP = false;
		if(gameData.extraData.containsKey("canPvP")){
			canPvP = Boolean.parseBoolean(gameData.extraData.get("canPvP"));
		}
		canPvM = false;
		if(gameData.extraData.containsKey("canPvM")){
			canPvM = Boolean.parseBoolean(gameData.extraData.get("canPvM"));
		}
	}
	
	@Override
	public AbstractPlayerClass getDefaultClassForPlayer(String player){
		return new RaceRunner(player);
	}
	
	@Override
	public void onStart(){
		count=0;

	}
	
	public ArrayList<String> playersLeft(){
		ArrayList<String> list = new ArrayList<String>();
		for(AbstractPlayerClass apc : getParticipants()){
			if(apc instanceof RaceRunner){
				list.add(apc.getPlayerName());
			}
		}
		return list;
	}
	
	@Override
	public void onEnd(){
		if(winner!=null){
			MobsGames.announce("We have a winner!");
			MobsGames.announce(winner.getName()+" has won the race!");
		}else{
			MobsGames.announce("The game has been ended without a winner!");
		}
		ghostAll();
	}
	
	Player winner = null;
	
	@Override
	public boolean checkGameOver(){
		for(AbstractPlayerClass apc : participants){
			if(apc instanceof RaceRunner){
				RaceRunner rr = (RaceRunner) apc;
				if(rr.taggedBlocks.size()==needBlocks){
					winner = rr.getPlayer();
					return true;
				}
			}
		}
		return playersLeft().size()==0;
		
	}
	
	@Override
	public void onTick(){
		if(count==0){
			//MobsGames.announce("A game of Last Man Standing has been started. Join in the next 20 seconds!");
		}
		count++;
	}
	
	ArrayList<BlockData> winners = new ArrayList<BlockData>();
	private int needBlocks=1;
	
	public boolean isWinningBlock(BlockData bd){
		for(BlockData bd2 : winners){
			if(bd.isEqualLocation(bd2)){
				return true;
			}
		}
		return false;
	}
	

}
