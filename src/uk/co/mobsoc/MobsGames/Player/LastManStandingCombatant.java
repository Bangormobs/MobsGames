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

import org.bukkit.GameMode;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.painting.PaintingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import uk.co.mobsoc.MobsGames.MobsGames;

@SuppressWarnings("deprecation")
public class LastManStandingCombatant extends AbstractPlayerClass{
	public LastManStandingCombatant(String player) {
		super(player);
	}
	@Override
	public void onEnable(){
		getPlayer().setAllowFlight(false);
		getPlayer().setGameMode(GameMode.SURVIVAL);
		getPlayer().getInventory().clear();
		getPlayer().updateInventory();
	}
	@Override
	public void onDisable(){
	}
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
		}else if(event instanceof PlayerRespawnEvent){
			if(!canRespawn()){
				MobsGames.getGame().setPlayerClass(new GhostClass(getPlayerName()));
			}else{
				((PlayerRespawnEvent) event).setRespawnLocation(MobsGames.getGame().getNextStartSpawn());
			}
		}else if(event instanceof EntityDamageEvent){
			if(!MobsGames.getGame().hasBegun()){
				((EntityDamageEvent) event).setCancelled(true);
			}
		}
	}
}
