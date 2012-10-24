package uk.co.mobsoc.MobsGames.Player;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
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
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;


public class TagRunner extends AbstractPlayerClass {

	public TagRunner(String player) {
		super(player);
	}
	@Override
	public void onEvent(Event event){
		if(event instanceof PlayerDropItemEvent){
			((PlayerDropItemEvent) event).setCancelled(true);
		}else if(event instanceof PlayerInteractEntityEvent){
			((PlayerInteractEntityEvent) event).setCancelled(true);
		}else if(event instanceof PlayerPickupItemEvent){
			//((PlayerPickupItemEvent) event).setCancelled(true);
		}else if(event instanceof PlayerShearEntityEvent){
			((PlayerShearEntityEvent) event).setCancelled(true);
		}else if(event instanceof PlayerBucketFillEvent){
			((PlayerBucketFillEvent) event).setCancelled(true);
		}else if(event instanceof PlayerBucketEmptyEvent){
			((PlayerBucketEmptyEvent) event).setCancelled(true);
		}else if(event instanceof BlockBreakEvent){
			//((BlockBreakEvent) event).setCancelled(true);
		}else if(event instanceof BlockPlaceEvent){
			//((BlockPlaceEvent) event).setCancelled(true);
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
		}
	}
}
