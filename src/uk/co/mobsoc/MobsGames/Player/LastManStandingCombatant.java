package uk.co.mobsoc.MobsGames.Player;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.painting.PaintingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import uk.co.mobsoc.MobsGames.MobsGames;

public class LastManStandingCombatant extends AbstractPlayerClass{
	public LastManStandingCombatant(Player player) {
		super(player);
	}
	@Override
	public void onEnable(){
		System.out.println(getPlayer().getName()+" is combatant");
	}
	@Override
	public void onDisable(){
		System.out.println(getPlayer().getName()+" is no longer combatant");
	}
	@Override
	public void onEvent(Event event){
		if(event instanceof PlayerBucketFillEvent){
			((PlayerBucketFillEvent) event).setCancelled(true);
		}else if(event instanceof PlayerBucketEmptyEvent){
			((PlayerBucketEmptyEvent) event).setCancelled(true);
		}else if(event instanceof BlockBreakEvent){
			((BlockBreakEvent) event).setCancelled(true);
		}else if(event instanceof BlockPlaceEvent){
			((BlockPlaceEvent) event).setCancelled(true);
		}else if(event instanceof PaintingBreakByEntityEvent){
			((PaintingBreakByEntityEvent) event).setCancelled(true);
		}else if(event instanceof PaintingPlaceEvent){
			((PaintingPlaceEvent) event).setCancelled(true);
		}else if(event instanceof PlayerDeathEvent){
			System.out.println("Event "+event);
			MobsGames.getGame().setPlayerClass(new GhostClass(getPlayer()));
		}else if(event instanceof EntityDamageEvent){
			System.out.println("Event "+event);
			if(!MobsGames.getGame().hasBegun()){
				((EntityDamageEvent) event).setCancelled(true);
			}
		}
	}
}
