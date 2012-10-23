package uk.co.mobsoc.MobsGames;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.mobsoc.MobsGames.Data.BlockData;
import uk.co.mobsoc.MobsGames.Data.GameData;
import uk.co.mobsoc.MobsGames.Data.LocationData;
import uk.co.mobsoc.MobsGames.Data.SavedData;
import uk.co.mobsoc.MobsGames.Data.Utils;
import uk.co.mobsoc.MobsGames.Game.AbstractGame;
import uk.co.mobsoc.MobsGames.Game.LastManStanding;
import uk.co.mobsoc.MobsGames.Game.Race;
import uk.co.mobsoc.MobsGames.Game.Tag;

/**
 * The Main Class
 * @author triggerhapp
 *
 */
public class MobsGames extends JavaPlugin{
	/** The current instance of the plugin */
	public static MobsGames instance;
	/** The current instance of the connection to MySQL */
	public static Connection conn;
	private String userName, passWord, dataBase, IP;
	@SuppressWarnings("unchecked")
	private static HashMap<String, Class> gameTypes = new HashMap<String, Class>();
	private AbstractGame game;
	private ChatColor b=ChatColor.BLUE,g=ChatColor.GREEN;
	private String options = b+"["+g+"join"+b+"|"+g+"leave"+b+"|"+g+"start"+b+"|"+g+"stop"+b+"|"+g+"new"+b+"|"+g+"select"+b+"|"+g+"unselect"+b+"|"+g+"block"+b+"|"+g+"location"+b+"|"+g+"save"+b+"|"+g+"alter"+b+"|"+g+"list"+b+"]";
	/** The list of users who are willing to join as soon as a game is called. Should not be used while getGame()!=null */
	public ArrayList<String> waitingList = new ArrayList<String>();

	/**
	 * Called automatically on server start or reload
	 */
	public void onEnable(){
		instance = this;
		readMySqlData();
		new CentralTick();
		getServer().getPluginManager().registerEvents(new MobsGamesPlayerListener(), this);
		getServer().getPluginManager().registerEvents(new RevertingListener(), this);
		getServer().getPluginManager().registerEvents(new GenericListener(), this);


		addGameType("LastManStanding", LastManStanding.class);
		addGameType("Tag", Tag.class);
		addGameType("Race", Race.class);


		Utils.init(userName, passWord, dataBase, IP);
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new WorldFinder(), 1);
	}
	
	/**
	 * Add a new Game type in via a plugin. Override the necessary functions in AbstractGame to define how your game should start, play, and end.
	 * @param gameTypeName
	 * @param gameType
	 */
	@SuppressWarnings("unchecked")
	public static void addGameType(String gameTypeName, Class gameType){
		gameTypeName = gameTypeName.toLowerCase();
		if(gameTypeName==null || gameType==null){
			return;
		}
		if(gameTypes.containsKey(gameTypeName)){
			System.out.println("Game type '"+gameTypeName+"' already registered!");
			return;
		}
		gameTypes.put(gameTypeName, gameType);
	}
	
	private void readMySqlData() {
		saveDefaultConfig();
		userName = getConfig().getString("sql-username");
		passWord = getConfig().getString("sql-password");
		dataBase = getConfig().getString("sql-database");
		IP = getConfig().getString("sql-ip");

	}

	/**
	 * Returns the current instance of the game.
	 * @return
	 */
	public static AbstractGame getGame(){
		return instance.game;
	}
	
	/**
	 * Called automatically on server shutdown or reload
	 */
	public void onDisable(){
		if(game!=null){
			game.stop();
		}
	}

	/**
	 * Send a message to all players on Server, other servers, and IRC. 
	 * @param string
	 */
	public static void announce(String string) {
		
		// TODO add IRCChat capabilities, falling back to Bukkit.broadcast if not present
		Bukkit.broadcastMessage(string);
	}
	
	/**
	 * Send a message to all players on Server, but not on other servers or on IRC. 
	 * @param string
	 */
	public static void announceNoIRC(String string) {
		Bukkit.broadcastMessage(string);	
	}
	
	/**
	 * Called every time someone uses the /game command
	 */
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
    	Player player = null;
    	if(sender instanceof Player){
    		player = (Player) sender;
    	}
     	if(args.length==0){
    		sender.sendMessage("/game "+options);
    		return true;
    	}else{
    		if(args[0].equalsIgnoreCase("start")){
    			if(sender.hasPermission("games.start")){
    				if(getGame()==null || getGame().isFinished()){
    					GameData gd;
    					if(sender instanceof Player){
    						gd = getGameData(playerSelected.get(player.getName().toLowerCase()));
    					}else{
    						gd = getGameData(playerSelected.get(sender.getName().toLowerCase()));
    					}
   						if(gd!=null){
   							game= gd.createGame();
   	    					getGame().start();
   	    					if(sender instanceof Player){
   	    						getGame().addParticipant(player);
   	    					}
   						}else{
   							sender.sendMessage("No game selected! /game select GameName");
   						}

    				}else{
    					sender.sendMessage("There is alread a game going, stop that before starting a new one!");
    				}
    			}else{
    				sender.sendMessage("You do not have permission to do that");
    			}
    		}else if(args[0].equalsIgnoreCase("stop")){
    			if(sender.hasPermission("games.stop")){
    				if(getGame() == null){
    					sender.sendMessage("No game currently running!");
    					return true;
    				}
    				getGame().stop();
    				game=null;
    			}else{
    				sender.sendMessage("You do not have permission to do that");
    			}
    		}else if(args[0].equalsIgnoreCase("list")){
    			String list = "", sep="";
    			ArrayList<GameData> gdList = Utils.getGameList();
    			for(GameData gd : gdList){
    				list = list + sep + gd.key;
    				sep=", ";
    			}
    			sender.sendMessage(list);
    			return true;
    		}else if(args[0].equalsIgnoreCase("join")){
    			if(! (sender instanceof Player)){
    				sender.sendMessage("And all the other reindeers used to laugh and call him names...");
    				return true;
    			}
    			if(sender.hasPermission("games.join")){
    				if(getGame()==null){
    					waitingList.add(player.getName().toLowerCase());
    					sender.sendMessage("Theres no game running right now. You have been added to the waiting list");
    				}else{
    					getGame().addParticipant(player);
    					sender.sendMessage("You have been added to the waiting list");
    				}
    			}else{
    				sender.sendMessage("You do not have permission to join a game");
    			}
    		}else if(args[0].equalsIgnoreCase("leave")){
    			if(! (sender instanceof Player)){
    				sender.sendMessage("And all the other reindeers used to laugh and call him names...");
    				return true;
    			}
    			if(sender.hasPermission("games.leave")){
    				if(getGame()==null){
    					waitingList.remove(player.getName().toLowerCase());
    					sender.sendMessage("You have left the waiting list");
    				}else{
    					getGame().removeParticipant(player,true);
    					MobsGames.announce(sender.getName()+" has forfeit the game");
    				}
    			}else{
    				sender.sendMessage("You do not have permission to leave a game");
    			}
    		}else if(args[0].equalsIgnoreCase("unselect")){
				playerSelected.put(player.getName().toLowerCase(), null);
				sender.sendMessage("No game is selected. All new Blocks and Locations will be added to every game.");
				return true;
    		}else if(args[0].equalsIgnoreCase("select")){

    			if(args.length==1){
    				sender.sendMessage("/game select GameName");
    				return true;
    			}
    			GameData gd = getGameData(args[1]);
    			if(gd!=null){
    				sender.sendMessage("Selected game '"+gd.key+"'");
    				playerSelected.put(player.getName().toLowerCase(), gd.key);
    			}else{
    				sender.sendMessage("Could not find game '"+args[1]+"'");
    			}
    		}else if(args[0].equalsIgnoreCase("new")){
    			if(!sender.hasPermission("games.alter")){
    				sender.sendMessage("You do not have permission to alter games");
    				return true;
    			}
    			if(! (sender instanceof Player)){
    				sender.sendMessage("You'll need to log in to alter games");
    				return true;
    			}
    			if(args.length!=3){
    				sender.sendMessage("/game new GameName GameType");
    				String list="",sep="";
    				for(String s : gameTypes.keySet()){
    					list = list + sep + s;
    					sep=", ";
    				}
    				sender.sendMessage("Game Types : "+list);
    			}else{
    				if(getGameType(args[2])==null){
    					sender.sendMessage("Game type '"+args[2]+"' unknown");
    					return true;
    				}
        			GameData gd = getGameData(args[1]);
        			if(gd!=null){
        				sender.sendMessage("Game named '"+args[1]+"' already exists. use another name!");
        				return true;
        			}
        			sender.sendMessage("Created new Game '"+args[1]+"' of type '"+args[2]+"'");
   					Utils.addGame(args[1], args[2], player.getLocation().getWorld().getName());
    			}
    		}else if(args[0].equalsIgnoreCase("location")){
    			if(!sender.hasPermission("games.alter")){
    				sender.sendMessage("You do not have permission to alter games");
    				return true;
    			}
    			if(!(sender instanceof Player)){
    				sender.sendMessage("You cannot alter games from console!");
    			}
    			boolean inCorrectvalues=false;
    			if(args.length>1){
        			if(args[1].equalsIgnoreCase("add")){
   						GameData gd = getGameData(playerSelected.get(player.getName().toLowerCase()));
   						String key;
   						if(gd==null){
   							key="any";
   							sender.sendMessage("You have not selected a game... location added to every game!");
   						}else{
   							key = gd.key;
   						}
						if(args.length==4){
        					LocationData l = Utils.getOneLocation(args[2]);
        					if(l!=null){
        						sender.sendMessage("The name '"+args[2]+"' is already used for a loction");
        						return true;
        					}else{
        						sender.sendMessage("Location '"+args[2]+"' of type '"+args[3]+"' added");
        						l = new LocationData(player.getLocation(), args[2], args[3], key);
        						Utils.addLocationData(l);
        						return true;
        					}
        				}else{
        					inCorrectvalues=true;
        				}
        			}else if(args[1].equalsIgnoreCase("del") || args[1].equalsIgnoreCase("remove")){
        				if(args.length==3){
        					LocationData l = Utils.getOneLocation(args[2]);
        					if(l==null){
        						sender.sendMessage("The name '"+args[2]+"' is not used for any location");
        						return true;
        					}else{
        						// TODO Add the location to DB
        						Utils.delLocationData(l);
        					}
        				}else{
        					inCorrectvalues=true;
        				}
        			}else if(args[1].equalsIgnoreCase("list")){
   						GameData gd = getGameData(playerSelected.get(player.getName().toLowerCase()));
   						if(gd==null){
   							sender.sendMessage("You have not selected a game yet! /game select gameName");
   						}else{
   							ArrayList<LocationData> lList = Utils.getLocations(gd.key, "%", "%");
   							String list="", sep="";
   							for(LocationData l : lList){
   								list = list + sep + l.name;
   								sep=" ";
   							}
   							sender.sendMessage(list);
   						}
        			}else if(args[1].equalsIgnoreCase("info")){
        				if(args.length==3){
            				LocationData l = Utils.getOneLocation(args[2]);
            				if(l!=null){
            					sender.sendMessage(l.name+" - "+l.getLocation()+" - "+l.type);
            				}else{
            					sender.sendMessage("Got null data for location. Oops!");
            				}
        				}else{
        					inCorrectvalues=true;
        				}
        			}
    			}else{
    				inCorrectvalues=true;
    			}
    			if(inCorrectvalues){
    				sender.sendMessage("/game location "+b+"["+g+"add ID TYPE"+b+"|"+g+"remove ID"+b+"|"+g+"list"+b+"|"+g+"info ID"+b+"]");
    				return true;
    			}
    		}else if(args[0].equalsIgnoreCase("block")){
    			if(!sender.hasPermission("games.alter")){
    				sender.sendMessage("You do not have permission to alter games");
    				return true;
    			}
    			if(!(sender instanceof Player)){
    				sender.sendMessage("You cannot alter games from console!");
    			}
    			boolean inCorrectvalues=false;
    			if(args.length>1){
        			if(args[1].equalsIgnoreCase("add")){
        				BlockData bdSel = blockSelected.get(player.getName().toLowerCase());
        				bdSel = new BlockData(bdSel.getCurrectBlockAt());
        				if(bdSel==null){
        					sender.sendMessage("You need to select a block by right clicking it with flint");
        					return true;
        				}
   						GameData gd = getGameData(playerSelected.get(player.getName().toLowerCase()));
   						String key;
   						if(gd==null){
   							key="any";
   							sender.sendMessage("You have not selected a game... block added to every game!");
   						}else{
   							key = gd.key;
   						}
						if(args.length==4){
        					BlockData bd = Utils.getOneBlock(args[2]);
        					if(bd!=null){
        						sender.sendMessage("The name '"+args[2]+"' is already used for a block");
        						return true;
        					}else{
        						sender.sendMessage("The block '"+args[2]+"' of type '"+args[3]+"' added");
        						bdSel.key = key;
        						bdSel.name= args[2];
        						bdSel.type = args[3];
        						Utils.addBlockData(bdSel);
        					}
        				}else{
        					inCorrectvalues=true;
        				}
        			}else if(args[1].equalsIgnoreCase("del") || args[1].equalsIgnoreCase("remove")){
        				if(args.length==3){
        					BlockData bd = Utils.getOneBlock(args[2]);
        					if(bd==null){
        						sender.sendMessage("The name '"+args[2]+"' is not used for any block");
        						return true;
        					}else{
        						Utils.delBlockData(bd);
        					}
        				}else{
        					inCorrectvalues=true;
        				}
        			}else if(args[1].equalsIgnoreCase("list")){
   						GameData gd = getGameData(playerSelected.get(player.getName().toLowerCase()));
   						if(gd==null){
   							sender.sendMessage("You have not selected a game yet! /game select gameName");
   						}else{
   							ArrayList<BlockData> bdList = Utils.getBlocks(gd.key, "%", "%");
   							String list="", sep="";
   							for(BlockData bd : bdList){
   								list = list + sep + bd.name;
   								sep=" ";
   							}
   							sender.sendMessage(list);
   						}
        			}else if(args[1].equalsIgnoreCase("info")){
        				if(args.length==3){
            				BlockData bd = Utils.getOneBlock(args[2]);
            				if(bd!=null){
            					sender.sendMessage(bd.name+" - "+bd.type+":"+bd.data+" - "+bd.type);
            				}else{
            					sender.sendMessage("Got null data for block. Oops!");
            				}
        				}else{
        					inCorrectvalues=true;
        				}
        			}
    			}else{
    				inCorrectvalues=true;
    			}
    			if(inCorrectvalues){
    				sender.sendMessage("/game block "+b+"["+g+"add ID TYPE"+b+"|"+g+"remove ID"+b+"|"+g+"list"+b+"|"+g+"info ID"+b+"]");
    				return true;
    			}
    		}else if(args[0].equalsIgnoreCase("save")){
    			if(!sender.hasPermission("games.save")){
    				sender.sendMessage("You do not have permission to save games");
    				return true;
    			}
				if(args.length==2){
					World world = Bukkit.getWorld(args[1]);
					if(world==null){
						sender.sendMessage("World '"+args[1]+"' not found. Not saving world data");
						return true;
					}
					SavedData.saveWorld(world);
				}else{
					sender.sendMessage("/game save WorldName");
				}
			}else if(args[0].equalsIgnoreCase("alter")){
    			if(!sender.hasPermission("game.alter")){
    				sender.sendMessage("You do not have permission to alter games");
    				return true;
    			}
				GameData gd;
				if(sender instanceof Player){
					gd = getGameData(playerSelected.get(player.getName().toLowerCase()));
				}else{
					gd = getGameData(playerSelected.get(sender.getName().toLowerCase()));
				}
				if(args.length<3){
					sender.sendMessage("/game alter KEY VALUE");
				}

				if(gd!=null){
					String key = args[1].toLowerCase();
					String s="", sep="";
					for(int i = 2; i<args.length; i++){
						s=s+sep+args[i];
						sep=" ";
					}
					if(key.equals("autostart")){
						if(s.equalsIgnoreCase("true")){
							gd.autostart=true;
							sender.sendMessage("'"+gd.key+"' set to autostart.");
						}else if(s.equalsIgnoreCase("false")){
							gd.autostart=false;
							sender.sendMessage("'"+gd.key+"' set to not autostart.");
						}else{
							sender.sendMessage("autostart must be true or false");
						}
					}else if(key.equals("minplayer") || key.equals("minplayers")){
						int i = Integer.parseInt(s);
						gd.minPlayers=i;
						sender.sendMessage("'"+gd.key+"' now requires "+i+" players to start");

					}else if(key.equals("maxplayer") || key.equals("maxplayers")){
						int i = Integer.parseInt(s);
						gd.maxPlayers=i;
						sender.sendMessage("'"+gd.key+"' now allows a maximum of "+i+" players");
					}else if(key.equals("time") || key.equals("timelimit")){
						int i = Integer.parseInt(s);
						gd.timeLimit=i;
						sender.sendMessage("'"+gd.key+"' now has a time limit of "+i+" seconds");
					}else{
						gd.extraData.put(key, s);
						sender.sendMessage("Set '"+key+"' = '"+s+"' in Game '"+gd.key+"'");
					}
					Utils.updateGame(gd);
				}else{
					sender.sendMessage("No game selected! /game select GameName");
				}
				return true;

			}
    		return true;
    			
    	}
    }
    
    private GameData getGameData(String string) {
		ArrayList<GameData> gdlist = Utils.getGameList();
		if(gdlist==null){ return null; }
		for(GameData gd : gdlist){
			if(gd.key.equalsIgnoreCase(string)){
				return gd;
			}
		}
		return null;
	}

	HashMap<String, String> playerSelected = new HashMap<String, String>();
	HashMap<String, BlockData> blockSelected = new HashMap<String, BlockData>();


	/**
	 * Returns the Class Description of the game requested
	 * @param typeName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Class<AbstractGame> getGameType(String typeName) {
		typeName= typeName.toLowerCase();
		if(!gameTypes.containsKey(typeName)){ return null; }
		return gameTypes.get(typeName);
	}
	/**
	 * Returns the plugin Logger.
	 * @return
	 */
	public static Logger getLog() {
		return instance.getLogger();
	}

	/**
	 * Change the current game. Does not cleanly exit the last game. Use with caution. 
	 * @param object
	 */
	public static void setGame(AbstractGame object) {
		instance.game = object;
	}

	public static void findWorlds() {
		for(World w : Bukkit.getWorlds()){
			SavedData.loadWorld(w);
		}
	}
}
