package uk.co.mobsoc.MobsGames.Data;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class StoredInventory {
	public int x,y,z;
	public World world;
	public ArrayList<StoredItem> itemList = new ArrayList<StoredItem>();
	
	public StoredInventory(InventoryHolder inv){
		Location l = null;
		if(inv instanceof BlockState){
			l=((BlockState) inv).getLocation();
		}
		for(int i = 0; i < inv.getInventory().getSize(); i++){
			ItemStack item = inv.getInventory().getItem(i);
			if(item==null){
				itemList.add(null);
			}else{
				itemList.add(new StoredItem(item));
			}
		}
		x=l.getBlockX();
		y=l.getBlockY();
		z=l.getBlockZ();
		world = l.getWorld();
	}
	
	public void revertNow(){
		Block b = world.getBlockAt(x,y,z);
		// The block may have been temporary for the game, and removed now, check this first
		if(b!=null && b.getState()!=null){
			BlockState bs = b.getState();
			if(bs instanceof InventoryHolder){
				Inventory inv = ((InventoryHolder) bs).getInventory();
				for(int i = 0; ( i < inv.getSize() && i < itemList.size() ) ; i++){
					StoredItem sI = itemList.get(i);
					if(sI == null){
						inv.setItem(i, null);
					}else{
						inv.setItem(i, itemList.get(i).makeItem());
					}
				}
			}
		}
	}

	public boolean equalLocation(BlockState iH) {
		Location l = iH.getBlock().getLocation();
		return l.getBlockX()==x && l.getBlockY()==y && l.getBlockZ()==z && l.getWorld().getName().equalsIgnoreCase(world.getName());
	}
}
