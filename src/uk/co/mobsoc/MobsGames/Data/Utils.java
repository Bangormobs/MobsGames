package uk.co.mobsoc.MobsGames.Data;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;

import uk.co.mobsoc.MobsGames.MobsGames;
/**
 * This class contains all the static Utility functions that are required too often to add to any one particular class, and ALL MySQL commands should be kept here.
 * @author triggerhapp
 *
 */
public class Utils {
	public static PreparedStatement sqlLocation, sqlBlock, sqlGames, sqlNewGame, sqlOneBlock, sqlOneLocation, sqlAddBlock, sqlAddLocation, sqlDelBlock, sqlDelLocation, sqlWorld, sqlDelWorld, sqlAddWorld, sqlDelWorldLocation, sqlDelWorldBlock, sqlDelWorldGame, sqlAutoGames, sqlUpdateGame;
	/**
	 * Initialise the Utility Class, and set up MySQL for usage.
	 * Also any alterations to tables from older versions MUST be done here
	 * @param userName MySQL Database credentials
	 * @param passWord MySQL Database credentials
	 * @param dataBase MySQL Database
	 * @param IP MySQL Server address
	 */
	public static void init(String userName, String passWord, String dataBase, String IP){
		Statement stat;

		try {
			MobsGames.conn = DriverManager.getConnection("jdbc:mysql://"+IP+"/"+dataBase+"?user="+userName+"&password="+passWord);
			stat = MobsGames.conn.createStatement();
			stat.execute("drop procedure if exists AddColumnUnlessExists;");
			String s = "create procedure AddColumnUnlessExists( ";
			       s+= " IN dbName tinytext, ";
			       s+= " IN tableName tinytext, ";
			       s+= " IN fieldName tinytext, ";
			       s+= " IN fieldDef text ) ";
			       s+= "begin ";
			       s+= " IF NOT EXISTS ( ";
			       s+= "  SELECT * FROM information_schema.COLUMNS ";
			       s+= "  WHERE column_name=fieldName ";
			       s+= "  and table_name = tableName ";
			       s+= "  and table_schema = dbName ";
			       s+= "  ) ";
			       s+= " THEN ";
			       s+= "  set @ddl=CONCAT('ALTER TABLE ', dbName, '.', tableName, ' ADD COLUMN ',fieldName,' ',fieldDef); ";
			       s+= "  prepare stmt from @ddl; ";
			       s+= "  execute stmt; ";
			       s+= " END IF; END; ";
			stat.executeUpdate(s);
			stat.execute("CREATE TABLE IF NOT EXISTS `Locations` (`key` text, `x` int(11), `y` int(11), `z` int(11), `world` text, `type` text, `name` text);");
			stat.execute("CREATE TABLE IF NOT EXISTS `Blocks` (`key` text, `x` int(11), `y` int(11), `z` int(11), `world` text, `newID` int(11), `newData` int(11), `type` text, `doPhysics` bool, `name` text);");
			stat.execute("CREATE TABLE IF NOT EXISTS `Games` (`key` text, `klass` text, `timeLimit` int(11), `otherData` text, `autostart` bool, `minPlayers` int(11), `maxPlayers` int(11));");
			stat.execute("CREATE TABLE IF NOT EXISTS `Worlds` (`worldName` text, `seed` bigint, `timestamp` bigint);");
			stat.execute("CALL AddColumnUnlessExists('"+ dataBase +"' , 'Games', 'maxPlayers', 'int (11)');");
			stat.execute("CALL AddColumnUnlessExists('"+ dataBase +"' , 'Games', 'minPlayers', 'int (11)');");
			stat.execute("CALL AddColumnUnlessExists('"+ dataBase +"' , 'Games', 'autostart', 'bool');");
			stat.execute("CALL AddColumnUnlessExists('"+ dataBase +"' , 'Games', 'world', 'text');");
			stat.execute("CALL AddColumnUnlessExists('"+ dataBase +"' , 'Blocks', 'name', 'text');");
			stat.execute("CALL AddColumnUnlessExists('"+ dataBase +"' , 'Locations', 'name', 'text');");
			sqlLocation = MobsGames.conn.prepareStatement("SELECT `world`, `x`, `y`, `z`, `name`, `type`, `key` FROM Locations WHERE ( `key` LIKE ? OR `key` = 'any' ) AND `type` LIKE ? AND `world` LIKE ?");
			sqlOneLocation = MobsGames.conn.prepareStatement("SELECT `world`, `x`, `y`, `z`, `name`, `type`, `key` FROM Locations WHERE `name` = ?");
			sqlAddLocation = MobsGames.conn.prepareStatement("INSERT INTO Locations (`world`, `x`, `y`, `z`, `type`, `name`, `key`) VALUES ( ? , ? , ? , ? , ? , ? , ?);");
			sqlDelLocation = MobsGames.conn.prepareStatement("DELETE FROM Locations WHERE name = ?");
			sqlDelWorldLocation = MobsGames.conn.prepareStatement("DELETE FROM Locations WHERE `world` = ?");

			sqlBlock = MobsGames.conn.prepareStatement("SELECT `world`, `x`, `y`, `z`, `newID`, `newData`, `type`, `doPhysics`, `name`, `key` FROM Blocks WHERE ( `key` LIKE ? OR `key` = 'any' ) AND `type` LIKE ? AND `world` LIKE ?");
			sqlOneBlock = MobsGames.conn.prepareStatement("SELECT `world`, `x`, `y`, `z`, `newID`, `newData`, `type`, `doPhysics`, `name`, `key` FROM Blocks WHERE  `name` = ?");
			sqlAddBlock = MobsGames.conn.prepareStatement("INSERT INTO Blocks (`world`, `x`, `y`, `z`, `newID`, `newData`, `type`, `doPhysics`, `name`, `key`) VALUES (? , ? , ? , ? , ? , ? , ? , ? , ?, ?);");
			sqlDelBlock = MobsGames.conn.prepareStatement("DELETE FROM Blocks WHERE name = ?");
			sqlDelWorldBlock = MobsGames.conn.prepareStatement("DELETE FROM Blocks WHERE `world` = ?");
			
			sqlGames = MobsGames.conn.prepareStatement("SELECT `key`, `klass`, `timeLimit`, `otherData`, `minPlayers`, `maxPlayers`, `autostart`, `world` FROM Games");
			sqlAutoGames = MobsGames.conn.prepareStatement("SELECT `key`, `klass`, `timeLimit`, `otherData`, `minPlayers`, `maxPlayers`, `autostart`, `world` FROM Games WHERE autostart = true");
			sqlNewGame = MobsGames.conn.prepareStatement("INSERT INTO Games (`key` , `klass`, `timeLimit`, `otherData`, `minPlayers`, `maxPlayers`, `autostart`, `world`) VALUES ( ? , ? , ? , ? , ? , ? , ? , ?);");
			sqlDelWorldGame = MobsGames.conn.prepareStatement("DELETE FROM Games WHERE `world` = ?");
			sqlUpdateGame = MobsGames.conn.prepareStatement("UPDATE Games SET `timeLimit` = ? , `otherData` = ? , `minPlayers` = ? , `maxPlayers` = ? , `autostart` = ?, `world` = ? WHERE `key` = ?");

			sqlWorld = MobsGames.conn.prepareStatement("SELECT `worldName`, `seed`, `timestamp` FROM Worlds WHERE `worldName` LIKE ?");
			sqlDelWorld = MobsGames.conn.prepareStatement("DELETE FROM Worlds WHERE `worldName`=?");
			sqlAddWorld = MobsGames.conn.prepareStatement("INSERT INTO Worlds (`worldName`, `seed`, `timestamp`) VALUES ( ? , ? , ? );");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Takes a single ResultSet from either sqlOneLocation or sqlLocation and turns it into an instance of LocationData
	 * @param set
	 * @return
	 */
	public static LocationData getLocation(ResultSet set){
		// `world`, `x`, `y`, `z`, `name`, `type`, `key`
		LocationData l=null;
		try {
			l = new LocationData();
			l.worldName = set.getString(1);
			l.x = set.getInt(2);
			l.y = set.getInt(3);
			l.z = set.getInt(4);
			l.name = set.getString(5);
			l.type = set.getString(6);
			l.key = set.getString(7);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return l;
		
	}

	/**
	 * Gets all LocationData that coincides with the parameters passed in
	 * @param key Name of the Game to get LocationData for. Also takes all Locations with "any" as Game Name
	 * @param type Type of LocationData to return. % means all types
	 * @param world World that all returned LocationData must belong to. % means all worlds
	 * @return 
	 */
	public static ArrayList<LocationData> getLocations(String key, String type, String world) {
		ArrayList<LocationData> locs = new ArrayList<LocationData>();
		try {
			sqlLocation.setString(1, key);
			sqlLocation.setString(2, type);
			sqlLocation.setString(3, world);
			sqlLocation.execute();
			ResultSet rs = sqlLocation.getResultSet();
			while(rs.next()){
				locs.add(getLocation(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

		return locs;
	}
	
	/**
	 * Gets all BlockData that coincides with the parameters passed in
	 * @param key Name of the Game to get BlockData for. Also takes all Blocks with "any" as Game Name
	 * @param type Type of BlockData to return. % means all types
	 * @param world World that all returned BlockData must belong to. % means all worlds
	 * @return
	 */
	public static ArrayList<BlockData> getBlocks(String key, String type, String world){
		ArrayList<BlockData> blocks = new ArrayList<BlockData>();

		try {
			sqlBlock.setString(1, key);
			sqlBlock.setString(2, type);
			sqlBlock.setString(3, world);
			sqlBlock.execute();
			ResultSet rs = sqlBlock.getResultSet();
			while(rs.next()){
				blocks.add(getBlock(rs));
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return blocks;
	}

	/**
	 * Takes a single ResultSet from either sqlOneBlock or sqlBlock and turns it into an instance of BlockData
	 * @param rs
	 * @return
	 */
	private static BlockData getBlock(ResultSet rs) {
		BlockData bd = new BlockData();
		try {
			String worldName = rs.getString(1);
			World w = Bukkit.getWorld(worldName);
			if(w==null){
				System.out.println("Error, null world in Block from SQL");
				return null;
			}
			bd.world=w;
			bd.x = rs.getInt(2);
			bd.y = rs.getInt(3);
			bd.z = rs.getInt(4);
			bd.id = rs.getInt(5);
			bd.data = rs.getInt(6);
			bd.type = rs.getString(7);
			bd.doPhysics = rs.getBoolean(8);
			bd.name = rs.getString(9);
			bd.key = rs.getString(10);

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return bd;
	}
	
	/**
	 * Get all GameData's from the MySQL server
	 * @return
	 */
	public static ArrayList<GameData> getGameList(){
		ArrayList<GameData> gdList = new ArrayList<GameData>();
		try {
			sqlGames.execute();
			ResultSet rs = sqlGames.getResultSet();
			while(rs.next()){
				gdList.add(getGameData(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

		return gdList;
	}
	
	/**
	 * Takes a single ResultSet from sqlGames and turns it into an instance of GameData
	 * @param rs
	 * @return
	 */
	private static GameData getGameData(ResultSet rs){
		GameData gd = new GameData();
		try {
			gd.key = rs.getString(1);
			gd.klass = rs.getString(2);
			gd.timeLimit = rs.getInt(3);
			gd.setOtherData(rs.getString(4));
			gd.minPlayers = rs.getInt(5);
			gd.maxPlayers = rs.getInt(6);
			gd.autostart = rs.getBoolean(7);
			gd.world = rs.getString(8);
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return gd;
	}

	/**
	 * Create a new GameData instance in the database
	 * @param name Unique name of this game
	 * @param type Name of the class that controls this game
	 */
	public static void addGame(String name, String type, String worldName) {
		// `key` , `klass`, `timeLimit`, `otherData`, `minPlayers`, `maxPlayers`, `autostart`, `world`
		try {
			sqlNewGame.setString(1, name);
			sqlNewGame.setString(2, type);
			sqlNewGame.setInt(3, 600);
			sqlNewGame.setString(4, "");
			sqlNewGame.setInt(5,1);
			sqlNewGame.setInt(6,100);
			sqlNewGame.setBoolean(7, false);
			sqlNewGame.setString(8, worldName);
			sqlNewGame.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		updateWorld(worldName);
	}

	/**
	 * Return the One LocationData from MySQL that holds this unique name, or null
	 * @param name
	 * @return
	 */
	public static LocationData getOneLocation(String name){
		try {
			sqlOneLocation.setString(1, name);
			sqlOneLocation.execute();
			ResultSet rs = sqlOneLocation.getResultSet();
			if(rs.next()){
				return getLocation(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Create a new LocationData instance in the database
	 * @param l
	 */
	public static void addLocationData(LocationData l) {
		// `world`, `x`, `y`, `z`, `type`, `name`, `key`
		try {
			sqlAddLocation.setString(1, l.worldName);
			sqlAddLocation.setInt(2, l.x);
			sqlAddLocation.setInt(3, l.y);
			sqlAddLocation.setInt(4, l.z);
			sqlAddLocation.setString(5, l.type);
			sqlAddLocation.setString(6, l.name);
			sqlAddLocation.setString(7, l.key);
			sqlAddLocation.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		updateWorld(l.worldName);
	}
	
	/**
	 * Delete the LocationData specified from the database
	 * @param l
	 */
	public static void delLocationData(LocationData l){
		try {
			sqlDelLocation.setString(1, l.name);
			sqlDelLocation.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		updateWorld(l.worldName);
	}
	
	/**
	 * Create a new BlockData instance in the database
	 * @param bd
	 */
	public static void addBlockData(BlockData bd){
		//  `world`, `x`, `y`, `z`, `newID`, `newData`, `type`, `doPhysics`, `name`

		try {
			sqlAddBlock.setString(1, bd.world.getName());
			sqlAddBlock.setInt(2, bd.x);
			sqlAddBlock.setInt(3, bd.y);
			sqlAddBlock.setInt(4, bd.z);
			sqlAddBlock.setInt(5, bd.id);
			sqlAddBlock.setInt(6, bd.data);
			sqlAddBlock.setString(7, bd.type);
			sqlAddBlock.setBoolean(8, bd.doPhysics);
			sqlAddBlock.setString(9, bd.name);
			sqlAddBlock.setString(10, bd.key);
			sqlAddBlock.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		updateWorld(bd.world.getName());


	}
	
	/**
	 * Return the One BlockData from MySQL that holds this unique name, or null
	 * @param name
	 * @return
	 */
	public static BlockData getOneBlock(String string) {
		try{
			sqlOneBlock.setString(1, string);
			sqlOneBlock.execute();
			ResultSet rs = sqlOneBlock.getResultSet();
			if(rs.next()){
				return getBlock(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Delete the BlockData specified from the database
	 * @param l
	 */
	public static void delBlockData(BlockData bd) {
		try {
			sqlDelBlock.setString(1, bd.name);
			sqlDelBlock.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		updateWorld(bd.world.getName());

	}
	
	

	public static long getWorldTimestamp(String name) {
		WorldData worldData = getWorldDataFromName(name);
		if(worldData!=null){
			return worldData.timeStamp;
		}
		return 0;
	}

	public static WorldData getWorldDataFromName(String name) {
		try{
			sqlWorld.setString(1, name);
			sqlWorld.execute();
			ResultSet rs = sqlWorld.getResultSet();
			if(rs.next()){
				return getWorldFromRS(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static WorldData getWorldFromRS(ResultSet rs) {
		//`worldName`, `UUID`, `timestamp`
		WorldData wd = new WorldData();
		try {
			wd.worldName = rs.getString(1);
			wd.seed = rs.getLong(2);
			wd.timeStamp = rs.getLong(3);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return wd;
	}

	public static ArrayList<GameData> getGameList(World world) {
		ArrayList<GameData> gdList = new ArrayList<GameData>();
		try {
			sqlGames.execute();
			ResultSet rs = sqlGames.getResultSet();
			while(rs.next()){
				GameData gd = getGameData(rs);
				if(gd.world.equalsIgnoreCase(world.getName())){
					gdList.add(gd);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

		return gdList;
	}
	
	public static void updateWorld(String worldName){
		World world = Bukkit.getWorld(worldName);
		if(world==null){ return; }
		updateWorld(world);
	}
	
	public static void updateWorld(World world){
		if(getWorldDataFromName(world.getName())!=null){
			deleteWorld(world.getName());
		}
		addWorld(new WorldData(world, System.currentTimeMillis()));
	}

	private static void addWorld(WorldData worldData) {
		// `worldName`, `UUID`, `timestamp`
		try {
			sqlAddWorld.setString(1, worldData.worldName);
			sqlAddWorld.setLong(2, worldData.seed);
			sqlAddWorld.setLong(3, worldData.timeStamp);
			sqlAddWorld.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	private static void deleteWorld(String worldName) {
		try {
			sqlDelWorld.setString(1, worldName);
			sqlDelWorld.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}

	public static void deleteAllDataForWorld(String name) {
		try {
			sqlDelWorldBlock.setString(1, name);
			sqlDelWorldBlock.execute();
			sqlDelWorldLocation.setString(1, name);
			sqlDelWorldLocation.execute();
			sqlDelWorldGame.setString(1, name);
			sqlDelWorldGame.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	public static void addGame(GameData gd) {
		// `key` , `klass`, `timeLimit`, `otherData`, `minPlayers`, `maxPlayers`, `autostart`, `world`
		try {
			sqlNewGame.setString(1, gd.key);
			sqlNewGame.setString(2, gd.klass);
			sqlNewGame.setInt(3, gd.timeLimit);
			sqlNewGame.setString(4, gd.getOtherData());
			sqlNewGame.setInt(5,gd.minPlayers);
			sqlNewGame.setInt(6,gd.maxPlayers);
			sqlNewGame.setBoolean(7, gd.autostart);
			sqlNewGame.setString(8, gd.world);
			sqlNewGame.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		updateWorld(gd.world);
	}

	public static void updateWorld(World world, long timeStamp) {
		if(getWorldDataFromName(world.getName())!=null){
			deleteWorld(world.getName());
		}
		addWorld(new WorldData(world, timeStamp));
		
	}

	public static Object getGameData(String key) {
		ArrayList<GameData> gdList = getGameList();
		for(GameData gd : gdList){
			if(gd.key.equalsIgnoreCase(key)){
				return gd;
			}
		}
		return null;
	}

	public static void updateGame(GameData gd) {
		// "UPDATE Games SET `timeLimit` = ? , `otherData` = ? , `minPlayers` = ? , `maxPlayers` = ? , `autostart` = ?, `world` = ? WHERE `key` = ?
		try {
			sqlUpdateGame.setInt(1, gd.timeLimit);
			sqlUpdateGame.setString(2, gd.getOtherData());
			sqlUpdateGame.setInt(3, gd.minPlayers);
			sqlUpdateGame.setInt(4, gd.maxPlayers);
			sqlUpdateGame.setBoolean(5, gd.autostart);
			sqlUpdateGame.setString(6, gd.world);
			sqlUpdateGame.setString(7, gd.key);
			sqlUpdateGame.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
