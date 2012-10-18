package uk.co.mobsoc.MobsGames.Data;

import org.bukkit.inventory.ItemStack;

public class StoredItem {
	public StoredItem(int ID, int DURA, int COUNT) {
		this.ID = ID; this.DURA = DURA; this.COUNT=COUNT;
	}

	public StoredItem(ItemStack item) {
		ID = item.getTypeId(); DURA = item.getDurability(); COUNT = item.getAmount();
	}

	public int ID, DURA, COUNT;

	public ItemStack makeItem() {
		return new ItemStack(ID, COUNT, (short) DURA);
	}
}
