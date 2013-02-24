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
package uk.co.mobsoc.MobsGames.Player;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.painting.PaintingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import uk.co.mobsoc.MobsGames.MobsGames;
import uk.co.mobsoc.MobsGames.Data.BlockData;
import uk.co.mobsoc.MobsGames.Game.Race;

@SuppressWarnings("deprecation")
public class RaceRunner extends AbstractPlayerClass {

	public RaceRunner(String player) {
		super(player);
	}
	@Override
	public void onEnable(){
		getPlayer().setAllowFlight(false);
		getPlayer().setGameMode(GameMode.SURVIVAL);
	}
	@Override
	public void onDisable(){
	}
	
	public ArrayList<BlockData> taggedBlocks = new ArrayList<BlockData>();	
	@Override
	public void onEvent(Event event){
		if(event instanceof PlayerBucketFillEvent){
			((PlayerBucketFillEvent) event).setCancelled(true);
		}else if(event instanceof PlayerBucketEmptyEvent){
			((PlayerBucketEmptyEvent) event).setCancelled(true);
		}else if(event instanceof BlockBreakEvent){
			if(MobsGames.getGame().allowBreak(((BlockBreakEvent) event).getBlock())){ return; }
			((BlockBreakEvent) event).setCancelled(true);
		}else if(event instanceof BlockPlaceEvent){
			if(MobsGames.getGame().allowPlace(((BlockPlaceEvent) event).getPlayer().getItemInHand())){ return; }
			((BlockPlaceEvent) event).setCancelled(true);
		}else if(event instanceof PaintingBreakByEntityEvent){
			((PaintingBreakByEntityEvent) event).setCancelled(true);
		}else if(event instanceof PaintingPlaceEvent){
			((PaintingPlaceEvent) event).setCancelled(true);
		}else if(event instanceof EntityDamageByEntityEvent){
			((EntityDamageByEntityEvent) event).setCancelled(true);
		}else if(event instanceof EntityDamageEvent){
			((EntityDamageEvent) event).setCancelled(true);
		}else if(event instanceof PlayerInteractEvent){
			Race race = (Race) MobsGames.getGame();
			PlayerInteractEvent e2 = (PlayerInteractEvent) event;
			if(e2.hasBlock()){
				Block b = e2.getClickedBlock();
				BlockData bd = new BlockData(b);
				if(race.isWinningBlock(bd)){
					e2.setCancelled(true);
					for(BlockData bd2 : taggedBlocks){
						if(bd2.isEqualLocation(bd)){
							return;
						}
					}
					taggedBlocks.add(bd);
				}
			}
		}
	}
}

