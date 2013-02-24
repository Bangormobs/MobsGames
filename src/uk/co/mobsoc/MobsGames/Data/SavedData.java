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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.World;

import uk.co.mobsoc.MobsGames.MobsGames;

public class SavedData {
	
	public static void saveWorld(World world){
        try {
        	FileOutputStream fos = new FileOutputStream(world.getName()+"/MobsGames.data");
			DataOutputStream dos = new DataOutputStream(fos);
			WorldData wd = new WorldData(world);
			// Don't save the Worlds name, assumption must be made by directory
			dos.writeLong(wd.timeStamp);
			dos.writeLong(wd.seed);
			ArrayList<GameData> gameData = Utils.getGameList(world);
			dos.writeInt(gameData.size());
			for(GameData gd : gameData){
				writeString(dos, gd.key);
				writeString(dos, gd.klass);
				writeString(dos, gd.getOtherData());
				dos.writeInt(gd.timeLimit);
				dos.writeInt(gd.minPlayers);
				dos.writeInt(gd.maxPlayers);
				dos.writeBoolean(gd.autostart);
			}
			ArrayList<LocationData> locationData = Utils.getLocations("%", "%", world.getName());
			dos.writeInt(locationData.size());
			for(LocationData ld : locationData){
				writeString(dos, ld.type);
				writeString(dos, ld.name);
				writeString(dos, ld.key);
				dos.writeInt(ld.x);
				dos.writeInt(ld.y);
				dos.writeInt(ld.z);
			}
			ArrayList<BlockData> blockData = Utils.getBlocks("%", "%", world.getName());
			dos.writeInt(blockData.size());
			for(BlockData bd : blockData){
				writeString(dos, bd.type);
				writeString(dos, bd.name);
				writeString(dos, bd.key);
				dos.writeInt(bd.x);
				dos.writeInt(bd.y);
				dos.writeInt(bd.z);
				dos.writeInt(bd.id);
				dos.writeInt(bd.data);
				dos.writeBoolean(bd.doPhysics);
			}
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeString(DataOutputStream dos, String string){
		try {
			dos.writeInt(string.length());
			dos.writeChars(string);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String readString(DataInputStream dis){
		String s = "";
		int max;
		try {
			max = dis.readInt();
			for(int i = 0; i<max; i++){
				s=s+dis.readChar();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static void loadWorld(World world){
		try {
			FileInputStream fis = new FileInputStream(world.getName()+"/MobsGames.data");
			DataInputStream dis = new DataInputStream(fis);
			
			WorldData wd = new WorldData();
			ArrayList<GameData> gameData = new ArrayList<GameData>();
			ArrayList<LocationData> locationData = new ArrayList<LocationData>();
			ArrayList<BlockData> blockData = new ArrayList<BlockData>();
			
			wd.worldName = world.getName();
			wd.timeStamp = dis.readLong();
			wd.seed = dis.readLong();
			
			WorldData currentWd = Utils.getWorldDataFromName(world.getName());
			// Basic header out of the way, we now need to compare validity of this saved data
			
			boolean loadIt = false;
			if(currentWd!=null && wd.seed == currentWd.seed){
				// The worlds are the same, this is not a new world with an old name
				if(wd.timeStamp > currentWd.timeStamp){
					// The file is a newer version of this world. Lets load!
					loadIt = true;
					MobsGames.getLog().info("MobsGames.data file for world '"+world.getName()+"' is newer. Loading");
				}else{
					MobsGames.getLog().warning("MobsGames.data file for world '"+world.getName()+"' is older than data in Database. Not loading");
				}
			}else{
				// The world has been replaced or is new
				if(wd.seed == world.getSeed()){
					MobsGames.getLog().info("MobsGames.data file for world '"+world.getName()+"' appears to be a whole new world. Loading");
					loadIt = true;
				}else{
					MobsGames.getLog().warning("MobsGames.data file for world '"+world.getName()+"' contains data for a different world. Not Loading");
				}
			}
			
			if(!loadIt){ dis.close(); return; }
			
			int max = dis.readInt();
			for(int count = 0; count < max; count++){
				GameData gd = new GameData();
				gd.key = readString(dis);
				gd.klass = readString(dis);
				gd.setOtherData(readString(dis));
				gd.timeLimit = dis.readInt();
				gd.minPlayers = dis.readInt();
				gd.maxPlayers = dis.readInt();
				gd.autostart = dis.readBoolean();
				gd.world = world.getName();
				gameData.add(gd);
			}
			max = dis.readInt();
			for(int count = 0; count < max; count++){
				LocationData ld = new LocationData();
				ld.type = readString(dis);
				ld.name = readString(dis);
				ld.key = readString(dis);
				ld.x = dis.readInt();
				ld.y = dis.readInt();
				ld.z = dis.readInt();
				ld.worldName = world.getName();
				locationData.add(ld);
			}
			max = dis.readInt();
			for(int count = 0; count < max; count++){
				BlockData bd = new BlockData();
				bd.type = readString(dis);
				bd.name = readString(dis);
				bd.key = readString(dis);
				bd.x = dis.readInt();
				bd.y = dis.readInt();
				bd.z = dis.readInt();
				bd.id = dis.readInt();
				bd.data = dis.readInt();
				bd.doPhysics = dis.readBoolean();
				bd.world = world;
				blockData.add(bd);
			}
			
			Utils.deleteAllDataForWorld(world.getName());
			for(GameData gd : gameData){
				String key = gd.key;
				while(Utils.getGameData(key)!=null){
					key = key+":1";
				}
				gd.key=key;
				Utils.addGame(gd);
			}
			for(BlockData bd : blockData){
				String key = bd.key;
				while(Utils.getOneBlock(key)!=null){
					key=key+":1";
				}
				bd.key = key;
				Utils.addBlockData(bd);
			}
			for(LocationData ld : locationData){
				String key = ld.key;
				while(Utils.getOneLocation(key)!=null){
					key=key+":1";
				}
				ld.key = key;
				Utils.addLocationData(ld);
			}
			Utils.updateWorld(world, wd.timeStamp);
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			// Silently Drop this. We dont need to warn people if no flatfile is present
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
