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
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.painting.PaintingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import uk.co.mobsoc.MobsGames.MobsGames;

@SuppressWarnings("deprecation")
public class GhostClass extends AbstractPlayerClass{
	int indexOf=0;
	public GhostClass(String player) {
		super(player);
	}
	@Override
	public void onEnable(){
		teleToSpawn();
		if(getPlayer()!=null){ 
			getPlayer().sendMessage("You are now in spectator mode. Left and Right click to teleport to players"); 
		}else{
			return; 
		}
		for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
			Player p= apc.getPlayer();
			if(p!=null){
				p.hidePlayer(getPlayer());
			}
		}
		getPlayer().setAllowFlight(true);
		getPlayer().setGameMode(GameMode.SURVIVAL);
	}
	@Override
	public void onDisable(){
		if(getPlayer()!=null){
			for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
				Player p= apc.getPlayer();
				if(p!=null && getPlayer()!=null){
					p.showPlayer(getPlayer());
				}
			}
			getPlayer().setAllowFlight(false);
		}
	}
	@Override
	public void onEvent(Event event){
		if(event instanceof PlayerDropItemEvent){
			((PlayerDropItemEvent) event).setCancelled(true);
		}else if(event instanceof PlayerInteractEntityEvent){
			((PlayerInteractEntityEvent) event).setCancelled(true);
		}else if(event instanceof PlayerPickupItemEvent){
			((PlayerPickupItemEvent) event).setCancelled(true);
		}else if(event instanceof PlayerShearEntityEvent){
			((PlayerShearEntityEvent) event).setCancelled(true);
		}else if(event instanceof PlayerBucketFillEvent){
			((PlayerBucketFillEvent) event).setCancelled(true);
		}else if(event instanceof PlayerBucketEmptyEvent){
			((PlayerBucketEmptyEvent) event).setCancelled(true);
		}else if(event instanceof BlockBreakEvent){
			((BlockBreakEvent) event).setCancelled(true);
		}else if(event instanceof BlockPlaceEvent){
			((BlockPlaceEvent) event).setCancelled(true);
		}else if(event instanceof PlayerInteractEvent){
			PlayerInteractEvent e = (PlayerInteractEvent) event;
			e.setCancelled(true);
			ArrayList<AbstractPlayerClass> playersLeft = MobsGames.getGame().getNonGhosts();
			if(playersLeft.size()==0){ return; }
			if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction()== Action.LEFT_CLICK_BLOCK){
				indexOf--;
				if(indexOf<0 || indexOf>= playersLeft.size()){
					indexOf = playersLeft.size()-1;
				}
				Player p = playersLeft.get(indexOf).getPlayer();
				if(p!=null){
					teleportTo(p.getLocation());
				}
			}else if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
				indexOf++;
				if(indexOf>= playersLeft.size()){
					indexOf = 0;
				}
				Player p = playersLeft.get(indexOf).getPlayer();
				if(p!=null){
					teleportTo(p.getLocation());
				}
			}
		}else if(event instanceof BlockIgniteEvent){
			((BlockIgniteEvent) event).setCancelled(true);
		}else if(event instanceof EntityDamageByEntityEvent){
			((EntityDamageByEntityEvent) event).setCancelled(true);
		}else if(event instanceof EntityDamageEvent){
			((EntityDamageEvent) event).setCancelled(true);
		}else if(event instanceof VehicleDamageEvent){
			((VehicleDamageEvent) event).setCancelled(true);
		}else if(event instanceof VehicleEnterEvent){
			((VehicleEnterEvent) event).setCancelled(true);
		}else if(event instanceof PaintingBreakByEntityEvent){
			((PaintingBreakByEntityEvent) event).setCancelled(true);
		}else if(event instanceof PaintingPlaceEvent){
			((PaintingPlaceEvent) event).setCancelled(true);
		}else if(event instanceof EntityTargetEvent){
			((EntityTargetEvent) event).setCancelled(true);
		}else if(event instanceof ProjectileLaunchEvent){
			((ProjectileLaunchEvent) event).setCancelled(true);
		}else if(event instanceof PlayerJoinEvent){
			Player p = ((PlayerJoinEvent) event).getPlayer();
			p.hidePlayer(this.getPlayer());
		}
	}
}
