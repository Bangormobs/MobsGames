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
package uk.co.mobsoc.MobsGames;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ContainerBlock;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;

import uk.co.mobsoc.MobsGames.Data.BlockData;
import uk.co.mobsoc.MobsGames.Game.AbstractGame;
/**
 * This Class is responsible for logging all Game related block and inventory changes, so that games may revert the arena afterwards
 * @author triggerhapp
 *
 */
@SuppressWarnings("deprecation")
public class RevertingListener implements Listener {
	/**
	 * Adds broken blocks to the list of blocks that return to initial state
	 * @param event
	 */
	@EventHandler(priority=EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event){
		if(event.isCancelled()){ return; }
		AbstractGame game = MobsGames.getGame();
		if(game==null){ return; }
		if(game.isParticipant(event.getPlayer())){
			BlockData bd = new BlockData(event.getBlock());
			MobsGames.getGame().addRevert(bd);
		}
	}
	
	/**
	 * Adds Placed blocks to the list of blocks that return to initial state
	 * @param event
	 */
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlacePlace(BlockPlaceEvent event){
		if(event.isCancelled()){ return; }
		AbstractGame game = MobsGames.getGame();
		if(game==null){ return; }
		if(game.isParticipant(event.getPlayer())){
			BlockData bd = new BlockData(event.getBlock());
			bd.data = event.getBlockReplacedState().getRawData();
			bd.id = event.getBlockReplacedState().getTypeId();
			MobsGames.getGame().addRevert(bd);
		}
	}
	


	/**
	 * Add Exploded blocks to the list of blocks that return to initial state.
	 * This might be a bit buggy, since it does not check (Because it is not logically possible) if the explosion is related to the game or not.
	 * @param event
	 */
	@EventHandler(priority=EventPriority.MONITOR)
	public void onExplosion(EntityExplodeEvent event){
		AbstractGame game = MobsGames.getGame();
		if(game!=null){
			for(Block block : event.blockList()){
				game.addRevert(new BlockData(block));
			}
		}
	}

	/**
	 * Add Liquid that flows into the list of blocks that return to initial state.
	 * This might be a bit buggy, since it does not check (due to excessive CPU usage) if the liquid is related to the game or not.
	 * @param event
	 */
	@EventHandler(priority=EventPriority.MONITOR)
	public void onLiquidFlow(BlockFromToEvent event){
		AbstractGame game = MobsGames.getGame();
		if(game!=null){
			game.addRevert(new BlockData(event.getToBlock()));
		}
	}
	
	/**
	 * Add Liquid that is taken to the list of blocks to return to initial state
	 * @param event
	 */
	@EventHandler(priority=EventPriority.MONITOR)
	public void onBucketFill(PlayerBucketFillEvent event){
		if(event.isCancelled()){ return; }
		AbstractGame game = MobsGames.getGame();
		if(game==null){ return; }
		if(game.isParticipant(event.getPlayer())){
			BlockData bd = new BlockData(event.getBlockClicked().getRelative(event.getBlockFace()));
			game.addRevert(bd);
		}
	}
	
	/**
	 * Add Liquid that is placed to the list of blocks to return to initial state
	 * @param event
	 */
	@EventHandler(priority=EventPriority.MONITOR)
	public void onBucketEmpty(PlayerBucketEmptyEvent event){
		if(event.isCancelled()){ return; }
		AbstractGame game = MobsGames.getGame();
		if(game==null){ return; }
		if(game.isParticipant(event.getPlayer())){
			BlockData bd = new BlockData(event.getBlockClicked().getRelative(event.getBlockFace()));
			game.addRevert(bd);
		}
	}
	
	/**
	 * Add Levers to the list of blocks to return to initial state
	 * @param event
	 */
	@EventHandler(priority=EventPriority.MONITOR)
	public void onInteractBlock(PlayerInteractEvent event){
		if(event.isCancelled()){ return; }
		if(event.hasBlock()){
			AbstractGame game = MobsGames.getGame();
			if(game==null){ return; }
			if(game.isParticipant(event.getPlayer())){
				if(event.getClickedBlock().getType() == Material.LEVER){
					BlockData bd = new BlockData(event.getClickedBlock());
					game.addRevert(bd);
				}
			}
		}
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onInventoryOpen(InventoryOpenEvent event){
		if(event.isCancelled()){ return; }
		AbstractGame game = MobsGames.getGame();
		if(game==null){ return; }
		if(game.isParticipant((Player) event.getPlayer())){
			InventoryHolder iH = event.getInventory().getHolder();
			// To Hell with depreciating a perfectly good class.
			if(iH instanceof ContainerBlock){
				game.addRevert(iH);
			}
			
		}
	}
}
