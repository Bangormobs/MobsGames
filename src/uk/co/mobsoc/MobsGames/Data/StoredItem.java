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
