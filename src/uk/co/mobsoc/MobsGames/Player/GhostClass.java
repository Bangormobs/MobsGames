package uk.co.mobsoc.MobsGames.Player;

import java.util.ArrayList;

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

public class GhostClass extends AbstractPlayerClass{
	int indexOf=0;
	public GhostClass(Player player) {
		super(player);
	}
	@Override
	public void onEnable(){
		teleToSpawn();
		getPlayer().sendMessage("You are now in spectator mode. Left and Right click to teleport to players");
		for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
			apc.getPlayer().hidePlayer(getPlayer());
		}
	}
	@Override
	public void onDisable(){
		for(AbstractPlayerClass apc : MobsGames.getGame().getParticipants()){
			apc.getPlayer().showPlayer(getPlayer());
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
				getPlayer().teleport(playersLeft.get(indexOf).getPlayer());
			}else if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
				indexOf++;
				if(indexOf>= playersLeft.size()){
					indexOf = 0;
				}
				getPlayer().teleport(playersLeft.get(indexOf).getPlayer());				
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
