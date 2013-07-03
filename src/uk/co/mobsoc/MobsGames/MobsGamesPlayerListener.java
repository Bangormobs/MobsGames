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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.painting.PaintingPlaceEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import uk.co.mobsoc.MobsGames.Player.AbstractPlayerClass;
/**
 * This class collects all possibly Player-caused events and sends them to any PlayerClass's that relate in some way.
 * @author triggerhapp
 *
 */
@SuppressWarnings("deprecation")
public class MobsGamesPlayerListener implements Listener{
	public boolean cP(Player p1, Player p2){
		if(p1==null || p2==null){ return false; }
		return p1.getEntityId()==p2.getEntityId();
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		if(MobsGames.getGame()==null){ return ; }
		for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
			apc.setPlayer(event.getPlayer());
			apc.onLogin(event);
			apc.onEvent(event);
			apc.setPlayer(null);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		if(MobsGames.getGame()==null){
			if(!event.getPlayer().hasPermission("games.alter")){
				event.setCancelled(true);
			}
			return ;
		}
		if(!MobsGames.getGame().allowBreak(event.getBlock())){
			event.setCancelled(true);
		}
		for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
			if(cP(event.getPlayer(),apc.getPlayer())){
				apc.onEvent(event);
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		if(MobsGames.getGame()==null){
			if(!event.getPlayer().hasPermission("games.alter")){
				event.setCancelled(true);
			}
			return ;
		}
		if(event.getItemInHand().getType()==Material.PISTON_BASE || event.getItemInHand().getType() == Material.PISTON_STICKY_BASE){ event.setCancelled(true); }
		if(!MobsGames.getGame().allowPlace(event.getBlock().getState())){
			event.setCancelled(true);
		}
		for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
			if(cP(event.getPlayer(),apc.getPlayer())){
				apc.onEvent(event);
			}
		}
	}
	
	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event){
		if(MobsGames.getGame()==null){ return ; }
		if(event.getPlayer()==null){ return; }
		for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
			if(cP(event.getPlayer(),apc.getPlayer())){
				apc.onEvent(event);
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event){
		if(MobsGames.getGame()==null){ return ; }
		if(event instanceof EntityDamageByEntityEvent){
			return;
		}
		Entity damagee = event.getEntity();
		if(damagee instanceof Player){
			Player player = (Player) damagee;
			for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
				if(cP(player,apc.getPlayer())){
					apc.onEvent(event);
				}
			}
		}
	}
	
	@EventHandler
	public void onDamageByEntity(EntityDamageByEntityEvent event){
		if(MobsGames.getGame()==null && event.getEntity() instanceof Player){
			event.setCancelled(true);
			return ;
		}
		if(MobsGames.getGame()==null){ return ; }
		Entity damager = event.getDamager();
		Entity damagee = event.getEntity();
		Player pDamager = null;
		Player pDamagee = null;
		if(damager instanceof Player){
			pDamager = (Player) damager;
		}
		if(damagee instanceof Player){
			pDamagee = (Player) damagee;
		}
		for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
			if(pDamager!=null && cP(pDamager,apc.getPlayer())){
				apc.onEvent(event);
			}
			if(pDamagee!=null && cP(pDamagee,apc.getPlayer())){
				apc.onEvent(event);
			}
		}		
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event){
		if(MobsGames.getGame()==null){ return ; }
		Player player = event.getEntity();
		for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
			if(cP(player,apc.getPlayer())){
				apc.onEvent(event);
			}
		}
	}
	
	@EventHandler
	public void onEntityTarget(EntityTargetEvent event){
		if(MobsGames.getGame()==null){ return ; }
		if(event.getTarget() instanceof Player){
			Player player = (Player) event.getTarget();
			for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
				if(cP(player,apc.getPlayer())){
					apc.onEvent(event);
				}
			}
		}
	}
	
	@EventHandler
	public void onPaintingBreak(PaintingBreakByEntityEvent event){
		if(MobsGames.getGame()==null){ return ; }
		if(event.getRemover() instanceof Player){
			Player player = (Player) event.getRemover();
			for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
				if(cP(player,apc.getPlayer())){
					apc.onEvent(event);
				}
			}
		}
	}
	
	@EventHandler
	public void onPaintingPlace(PaintingPlaceEvent event){
		if(MobsGames.getGame()==null){ return ; }
		Player player = event.getPlayer();
		for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
			if(cP(player,apc.getPlayer())){
				apc.onEvent(event);
			}
		}
	}
	
	@EventHandler
	public void onBedEnter(PlayerBedEnterEvent event){
		if(MobsGames.getGame()==null){ return ; }
		Player player = event.getPlayer();
		for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
			if(cP(player,apc.getPlayer())){
				apc.onEvent(event);
			}
		}
	}
	
	@EventHandler
	public void onBedLeave(PlayerBedLeaveEvent event){
		if(MobsGames.getGame()==null){ return ; }
		Player player = event.getPlayer();
		for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
			if(cP(player,apc.getPlayer())){
				apc.onEvent(event);
			}
		}
	}
	
	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent event){
		if(MobsGames.getGame()==null){ return ; }
		Player player = event.getPlayer();
		for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
			if(cP(player,apc.getPlayer())){
				apc.onEvent(event);
			}
		}
	}
	
	@EventHandler
	public void onBucketFill(PlayerBucketFillEvent event){
		if(MobsGames.getGame()==null){ return ; }
		Player player = event.getPlayer();
		for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
			if(cP(player,apc.getPlayer())){
				apc.onEvent(event);
			}
		}
	}
	
	@EventHandler
	public void onChangeWorld(PlayerChangedWorldEvent event){
		if(MobsGames.getGame()==null){ return ; }
		Player player = event.getPlayer();
		for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
			if(cP(player,apc.getPlayer())){
				apc.onEvent(event);
			}
		}
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event){
		if(MobsGames.getGame()==null){ return ; }
		Player player = event.getPlayer();
		for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
			if(cP(player,apc.getPlayer())){
				apc.onEvent(event);
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		if(MobsGames.getGame()==null){ return ; }
		Player player = event.getPlayer();
		for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
			if(cP(player,apc.getPlayer())){
				apc.onEvent(event);
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event){
		if(MobsGames.getGame()==null){ return ; }
		Player player = event.getPlayer();
		for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
			if(cP(player,apc.getPlayer())){
				apc.onEvent(event);
			}
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event){
		if(MobsGames.getGame()==null){ return ; }
		Player player = event.getPlayer();
		for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
			if(cP(player,apc.getPlayer())){
				apc.onEvent(event);
			}
		}
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event){
		if(MobsGames.getGame()==null){ return ; }
		Player player = event.getPlayer();
		for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
			if(cP(player,apc.getPlayer())){
				apc.onEvent(event);
			}
		}
	}
	
	@EventHandler
	public void onPlayerPortal(PlayerPortalEvent event){
		if(MobsGames.getGame()==null){ return ; }
		Player player = event.getPlayer();
		for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
			if(cP(player,apc.getPlayer())){
				apc.onEvent(event);
			}
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event){
		if(MobsGames.getGame()==null){ return ; }
		Player player = event.getPlayer();
		for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
			if(player.getName().equalsIgnoreCase(apc.getPlayerName())){
				apc.onEvent(event);
			}else{
			}
		}
	}
	
	@EventHandler
	public void onPlayerShear(PlayerShearEntityEvent event){
		if(MobsGames.getGame()==null){ return ; }
		Player player = event.getPlayer();
		for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
			if(cP(player,apc.getPlayer())){
				apc.onEvent(event);
			}
		}
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event){
		if(MobsGames.getGame()==null){ return ; }
		Player player = event.getPlayer();
		for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
			if(cP(player,apc.getPlayer())){
				apc.onEvent(event);
			}
		}
	}
	
	@EventHandler
	public void onVehicleDamageEvent(VehicleDamageEvent event){
		if(MobsGames.getGame()==null){ return ; }
		if(event.getAttacker() instanceof Player){
			Player player = (Player) event.getAttacker();
			for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
				if(cP(player,apc.getPlayer())){
					apc.onEvent(event);
				}
			}
		}
	}
	
	@EventHandler
	public void onVehicleEnterEvent(VehicleEnterEvent event){
		if(MobsGames.getGame()==null){ return ; }
		if(event.getEntered() instanceof Player){
			Player player = (Player) event.getEntered();
			for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
				if(cP(player,apc.getPlayer())){
					apc.onEvent(event);
				}
			}
		}
	}
	
	@EventHandler
	public void onVehicleExitEvent(VehicleExitEvent event){
		if(MobsGames.getGame()==null){ return ; }
		if(event.getExited() instanceof Player){
			Player player = (Player) event.getExited();
			for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
				if(cP(player,apc.getPlayer())){
					apc.onEvent(event);
				}
			}
		}
	}
	
	
	
}
