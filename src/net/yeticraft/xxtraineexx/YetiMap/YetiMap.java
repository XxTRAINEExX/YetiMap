package net.yeticraft.xxtraineexx.YetiMap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;
//import java.util.HashMap;

public class YetiMap extends JavaPlugin {
	Logger log = Logger.getLogger("Minecraft");
	public FileConfiguration config;
	public String mapimage;
	public String prefix = "[YetiMap] ";
	
	// Create a new YetiMapRenderer object and name it renderer
	YetiMapRenderer renderer = new YetiMapRenderer(this);
	// private static Map<Player, YetiMapRenderer> playerRenderers = new HashMap<Player, YetiMapRenderer>();
	ItemStack yeticraftMap = new ItemStack(Material.MAP,1,(short)0);
	ItemStack netherMap = new ItemStack(Material.MAP,1,(short)1);
	
	
	
	// Run this sub when the plugin is enabled	
	public void onEnable() {
		// log to the minecraft logger 
		log.info("Loading YetiMap");
		loadMainConfig();
		
	}

	// run this sub when the player issues a command
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		//create an empty player object and set it to null
		Player player = null;

		//check to see if the sender is a player... if so... assign their object attributes to "player" object. If not, bail out
		if (sender instanceof Player) {
			player = (Player) sender;
		}
		else {
			sender.sendMessage(ChatColor.RED + "Must be run by a player.");
			return true;
		}
		
		/*YetiMapRenderer renderer = playerRenderers.get(player);
		if (renderer == null)
		{
			renderer = new YetiMapRenderer();
			playerRenderers.put(player, renderer);
		}*/
		
		//Now we see if they typed /map
		if (cmd.getName().equalsIgnoreCase("map")) {
			
			//Create an itemstack object called currentItem and place the current item in a players hand into tha tobject
			ItemStack currentItem = player.getItemInHand();
			
			//check to see if currentItem is a map
			if (currentItem.getType() == Material.MAP) {
				
			
				// Check to see if they typed any arguments
				if (args.length < 1) {
					player.sendMessage(ChatColor.YELLOW + "Missing Argument.. did you type /map [yeticraft, nether, reset]?");
					return false;
				}

				//Check to see if they typed too many arguments
				if (args.length > 1) {
					
					player.sendMessage(ChatColor.YELLOW + "Too many Arguments.. did you type /map [yeticraft, nether, reset]?");
					return false;
					
				}
				
				//Check to see if they typed "yeticraft" as the first argument after /map
				if (args[0].equalsIgnoreCase("yeticraft") && player.getWorld().getName().equalsIgnoreCase("yeticraft")) {
					
					//clear the current map
					renderer.setDirty(player.getName(), true);
					
					
					//Create a short integer to store the mapID of the map they are holding
					//short mapId = currentItem.getData().getData();
					// currentItem.getData().setData((byte) 0);
					
					// Create a mapview object called MAP and store this servers map with the ID pulled from the player
					MapView map = this.getServer().getMap((short)0);
					//player.setItemInHand(new ItemStack(Material.MAP,1,map.getId()));
					player.setItemInHand(yeticraftMap);
					
					
					// Call the applytomap() function in the maplines object we just created called renderer. Pass it our map 
					
					renderer.applyToMap(map, player.getWorld().getName(), player);
					
					// Tell the user we are applying their lines
					//sender.sendMessage("Displaying Map" + mapId);
					return true;
					
					
				}
				//Check to see if they typed "nether" as the first argument after /map
				if (args[0].equalsIgnoreCase("nether") && player.getWorld().getName().equalsIgnoreCase("yeticraft_nether")) {
					
					//clear the current map
					renderer.setDirty(player.getName(), true);
					
					//Create a short integer to store the mapID of the map they are holding
					//short mapId = currentItem.getData().getData();
					
					// Create a mapview object called MAP and store this servers map with the ID pulled from the player
					MapView map = this.getServer().getMap((short)1);
					//player.setItemInHand(new ItemStack(Material.MAP,1,map.getId()));
					player.setItemInHand(netherMap);
					
					
					// Call the applytomap() function in the maplines object we just created called renderer. Pass it our map 
					renderer.applyToMap(map, player.getWorld().getName(), player);
					
					// Tell the user we are applying their lines
					//sender.sendMessage("Displaying Map" + mapId);
					return true;
					
					
				}	
				
				if (args[0].equalsIgnoreCase("reset")) {
					
					renderer.setDirty(player.getName(), true);
					player.sendMessage(ChatColor.GRAY + "Map reset triggered...");
					return true;
					
				}
				
				if (player.hasPermission("yetimap.create") && args[0].equalsIgnoreCase("createimage")) {
					
					createImage(player.getWorld(), player, true);
					player.sendMessage(ChatColor.GRAY + "Generated new image on the server.");
					return true;
				}
				
				if (player.hasPermission("yetimap.nolines") && args[0].equalsIgnoreCase("createimage_nolines")) {
					createImage(player.getWorld(), player, false);
					player.sendMessage(ChatColor.GRAY + "Generated new image on the server.");
					return true;
				}
				
				player.sendMessage(ChatColor.YELLOW + "You did not enter a valid command. Try /map [yeticraft, nether, reset]");
				return false;
			}
			player.sendMessage(ChatColor.YELLOW + "There is not a map in your hand.");
			return false;
		}
					
		
		return false;
	}
	
		
		
		
	// Run this sub when the plugin is disabled
	public void onDisable() {

		log.info("Unloading net.yeticraft.xxtraineexx.YetiMap");
	}
	
	public void createImage(World world, Player player, Boolean addFactionLines){
		
		// Setting up some variables we will need.
		// img for the image we are going to render
		// buffer[][] to store rgb integer values for each pixel on the map
		// worldx/z foor the map we are trying to render
		BufferedImage img = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
		int[][] buffer = new int[128][128];
		double worldx;
		double worldz;
		
		// **** Code to determine which world they are in.
		if (world.getName().equalsIgnoreCase("yeticraft")){
			worldx = -4000;
			worldz = -4000;
			player.sendMessage("You appear to be in the world " + world.getName() + ".  Setting your border to 4000.");
		}
		else if (world.getName().equalsIgnoreCase("yeticraft_nether")){
			worldx = -500;
			worldz = -500;
			player.sendMessage("You appear to be in the world " + world.getName() + ".  Setting your border to 500.");
		}
		else{
			player.sendMessage("It appears your current world is not yeticraft or yeticraft_nether... existing.");
			return;
		}
		
		// Filling the buffer with RGB color codes. The buffer[x][y] will represent an RGB integer color code. This code will be used to paint the img later.
		// RGB codes taken from here: http://www.minecraftwiki.net/wiki/Map_Item_Format
		// RGB converted to integer using this site: http://www.shodor.org/stella2java/rgbint.html
		
		// Outer loop moving across the x access of the image we are drawing
		// One confusing note... The X access on an image is the same as a Z access in minecraft... don't ask me... it is :)
			for (int canvasX = 0; canvasX < 128; ++canvasX)
			{
				// **** Code to determine which world they are in.
				if (world.getName().equalsIgnoreCase("yeticraft")){worldz = -4000;}
				else {worldz = -500;}
				
				// Inner loop moving across the Y access of the image we are drawing
				for (int canvasY = 0; canvasY < 128; ++canvasY)
				{
					
					int worldY;
					
					// Added this if statement for the nether. If we aren't in the regular world we will start at Y block 64
					if (world.getName().equalsIgnoreCase("yeticraft")){
						worldY = world.getHighestBlockAt((int)worldx, (int)worldz).getY();	

						// This should get a better picture of the terrain. Cycling down through the AIR until we reach a non-air block.
						while (world.getBlockAt((int)worldx, worldY, (int)worldz).getType()==Material.AIR 
								&& worldY > 0){
							worldY = worldY - 1;
						}
					}
					else{
						worldY = 64;
					}
						
					Material type = world.getBlockAt((int)worldx, worldY, (int)worldz).getType();
					if (type == Material.WATER || type==Material.STATIONARY_WATER || type==Material.WATER_LILY) {buffer[canvasX][canvasY] = 2960820;}
					else if (type == Material.DIRT) {buffer[canvasX][canvasY] = 10312488;}
					else if (type == Material.GRASS || type==Material.LONG_GRASS) {buffer[canvasX][canvasY] = 5864743;}
					else if (type==Material.LEAVES) {buffer[canvasX][canvasY] = 22272;}
					else if (type == Material.WOOD || type == Material.WOOD_STAIRS || type == Material.LOG) {buffer[canvasX][canvasY] = 6837042;}
					else if (type == Material.SNOW || type==Material.SNOW_BALL || type==Material.SNOW_BLOCK) {buffer[canvasX][canvasY] = 16777215;}
					else if (type == Material.ICE) {buffer[canvasX][canvasY] = 14474460;}
					else if (type == Material.LAVA || type==Material.STATIONARY_LAVA || type==Material.FIRE) {buffer[canvasX][canvasY] = 16711680;}
					else if (type == Material.STONE || type == Material.COBBLESTONE ||type == Material.GRAVEL) {buffer[canvasX][canvasY] = 7368816;}
					else if (type == Material.SAND || type == Material.SANDSTONE) {buffer[canvasX][canvasY] = 11445363;}
					else if (type == Material.AIR) {buffer[canvasX][canvasY] = 0;}
					else {
						// The following line was for troubleshooting missing pixels on the map.
						// player.sendMessage("Block at X:" + (int)worldx + " Y:" + worldY + " Z:" + (int)worldz + "  is " + type.toString());
						// Making all missed pixels black.
						if (world.getName().equalsIgnoreCase("yeticraft")){buffer[canvasX][canvasY] = 5864743;}
						else {buffer[canvasX][canvasY] = 8388608;}
					
					}
					
					
					//8000 blocks in yeticraft. 8000 / 128 pixels = 62.5
					if (world.getName().equalsIgnoreCase("yeticraft")){worldz = worldz + 62.5;}
					//1000 blocks in our nether. 1000 / 128 pixels = 7.8125
					else {worldz = worldz + 7.8125;}
					
				}
				
			if (world.getName().equalsIgnoreCase("yeticraft")){worldx = worldx + 62.5;}
			else {worldx = worldx + 7.8125;}
			
			}
			
			player.sendMessage("We made it out of the loops... about to write the buffer to the img file.");
			// Writing the buffer out to the map
			for (int canvasX = 0; canvasX < 128; ++canvasX) {
				for (int canvasY = 0; canvasY < 128; ++canvasY) {
					
					img.setRGB(canvasX, canvasY, buffer[canvasX][canvasY]);
										
				}
			}
			
			
			//The following code draws the lines for the yeticraft map
			if (world.getName().equalsIgnoreCase("yeticraft") && addFactionLines){
				
				player.sendMessage("Looks like current world is " + world.getName() + " so we are going to draw some faction lines.");
				//Some code to paint the lines (Red line)
				for (int canvasX = 0; canvasX < 128; ++canvasX) {img.setRGB(canvasX, 27, 11796480);}
				
				//Some code to paint the lines (Red/Black line)
				for (int canvasX = 0; canvasX < 128; ++canvasX) {img.setRGB(canvasX, 54, 4799011);}
				//Some code to paint the lines (Blue/Black line)
				for (int canvasX = 0; canvasX < 128; ++canvasX) {img.setRGB(canvasX, 75, 4799011);}
				//Some code to paint the lines (Blue line)
				for (int canvasX = 0; canvasX < 128; ++canvasX) {img.setRGB(canvasX, 102, 4210943);}
			
				//Making spawn
				for (int canvasX = 57; canvasX < 73; ++canvasX) {img.setRGB(canvasX, 57, 4799011);}
				for (int canvasX = 57; canvasX < 73; ++canvasX) {img.setRGB(canvasX, 72, 4799011);}
				for (int canvasY = 57; canvasY < 73; ++canvasY) {img.setRGB(57, canvasY, 4799011);}
				for (int canvasY = 57; canvasY < 73; ++canvasY) {img.setRGB(72, canvasY, 4799011);}
		
				// The following was to verify the highest blocks were showing properly in Taiga
				// player.sendMessage("Taiga block shows: " + world.getHighestBlockAt(2162, 3603).getType().toString());
				
				// Making the temples
				
				img.setRGB(relMap(-3000), relMap(3125), 16733695);//SRIRANGAN
				img.setRGB(relMap(-957), relMap(3159), 16733695);//SEVILLE
				img.setRGB(relMap(973), relMap(3131), 16733695);//ZAHIR
				img.setRGB(relMap(3018), relMap(3094), 16733695);//CHOLULA
				img.setRGB(relMap(-3001), relMap(1395), 16733695);//QORIKANCHA
				img.setRGB(relMap(-1000), relMap(1375), 16733695);//UPPSALA
				img.setRGB(relMap(1000), relMap(1375), 16733695);//PANTHEON
				img.setRGB(relMap(3018), relMap(1362), 16733695);//TIKAL
				img.setRGB(relMap(-2991), relMap(-3138), 16733695);//BEITI
				img.setRGB(relMap(-1000), relMap(-3099), 16733695);//LUXOR
				img.setRGB(relMap(1000), relMap(-3124), 16733695);//COBA
				img.setRGB(relMap(3034), relMap(-3174), 16733695);//TOJI
				img.setRGB(relMap(-3000), relMap(-1383), 16733695);//BAALBEK
				img.setRGB(relMap(-954), relMap(-1382), 16733695);//CONFUCION
				img.setRGB(relMap(1008), relMap(-1373), 16733695);//JOKHANG
				img.setRGB(relMap(2967), relMap(-1366), 16733695);//PRAMBANAN
				img.setRGB(relMap(-22), relMap(-109), 16733695);//PARTHENON		
				
			}
			
			
			player.sendMessage("Preparing to write img to disk...");
			// Writing the new image out to the operating system.
			try {
				
				DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
				Date date = new Date();
				
				// player.sendMessage("Saving image to: /srv/minecraft/plugins/yetimap/" + world.getName() + "_" + dateFormat.format(date).toString() + ".png");
				// ImageIO.write(img, "png",new File("/srv/minecraft/plugins/yetimap/" + world.getName() + "_" + dateFormat.format(date).toString() + ".png"));
				
				player.sendMessage("Saving image to: " + this.getDataFolder().toString() + "\\images\\" + world.getName() + "_" + dateFormat.format(date).toString() + ".png");
				ImageIO.write(img, "png",new File(this.getDataFolder().toString() + "\\images\\" + world.getName() + "_" + dateFormat.format(date).toString() + ".png"));
				
			}
			catch (IOException e) {
				e.printStackTrace();
				player.sendMessage("Image no save.. you fix  now!");
			}
		
	}
	
	public int relMap(float loc){
		
		// This method returns the relative map location for a given world location.
		// This assumes an 8k world.
		int relativeLoc = (int)(((4000 + loc) / 8000) * 128);
		return relativeLoc;
		
	}

	public void loadMainConfig(){
		// Read the config file
    	config = getConfig();
    	
    	if (config.getString("mapimage") == null)
    	{
    		// mapimage was not set. We need to load the default image.
    		log.info(prefix + "Map image not found: " + mapimage);
    		mapimage = (this.getDataFolder().toString() + "\\images\\" + "defaultimage.png");
    		log.info(prefix + "Loading default image:" + mapimage);
    		
        }
    	else{
    		mapimage = (this.getDataFolder().toString() + "\\images\\" + config.getString("mapimage"));
    		log.info(prefix + "Loaded map image: " + mapimage); 
    	}
    	
    	
	}
	
}
