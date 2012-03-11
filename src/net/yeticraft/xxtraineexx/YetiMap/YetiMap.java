package net.yeticraft.xxtraineexx.YetiMap;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

public class YetiMap extends JavaPlugin {
	Logger log = Logger.getLogger("Minecraft");

	// Create a new YetiMapRenderer object and name it renderer
	YetiMapRenderer renderer = new YetiMapRenderer();
	String world = null;
	
	// Run this sub when the plugin is enabled	
	public void onEnable() {
		// log to the minecraft logger 
		log.info("Loading net.yeticraft.xxtraineexx.YetiMap");
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
		
		//Now we see if they typed /map
		if (cmd.getName().equalsIgnoreCase("map")) {
			
			//Create an itemstack object called currentItem and place the current item in a players hand into tha tobject
			ItemStack currentItem = player.getItemInHand();
			
			//check to see if currentItem is a map
			if (currentItem.getType() == Material.MAP) {
				
			
				// Check to see if they typed any arguments
				if (args.length < 1) {
					player.sendMessage("Missing Argument.. did you type /map [yeticraft, nether, the_end, reset]?");
					return false;
				}

				//Check to see if they typed too many arguments
				if (args.length > 1) {
					
					player.sendMessage("Too many Arguments.. did you type /map [yeticraft, nether, the_end, reset]?");
					return false;
					
				}
				
				//Check to see if they typed "show" as the first argument after /map
				if (args[0].equalsIgnoreCase("yeticraft")) {
					
					//clear the current map
					renderer.setDirty(player.getName(), true);
					
					
					//Create a short integer to store the mapID of the map they are holding
					short mapId = currentItem.getData().getData();
					
					// Create a mapview object called MAP and store this servers map with the ID pulled from the player
					MapView map = this.getServer().getMap(mapId);
										
					// Send the player the entire map
					//player.sendMap(map);
					world = "yeticraft";
					
					// Call the applytomap() function in the maplines object we just created called renderer. Pass it our map 
					renderer.applyToMap(map, world);
										
					// Tell the user we are applying their lines
					//sender.sendMessage("Displaying Map" + mapId);
					return true;
					
					
				}
				//Check to see if they typed "show" as the first argument after /map
				if (args[0].equalsIgnoreCase("nether")) {
					
					//clear the current map
					renderer.setDirty(player.getName(), true);
					
					//Create a short integer to store the mapID of the map they are holding
					short mapId = currentItem.getData().getData();
					
					// Create a mapview object called MAP and store this servers map with the ID pulled from the player
					MapView map = this.getServer().getMap(mapId);
										
					// Send the player the entire map
					//player.sendMap(map);
					world = "nether";
									
										
					// Call the applytomap() function in the maplines object we just created called renderer. Pass it our map 
					renderer.applyToMap(map, world);
										
					// Tell the user we are applying their lines
					//sender.sendMessage("Displaying Map" + mapId);
					return true;
					
					
				}	
				
				//Check to see if they typed "show" as the first argument after /map
				if (args[0].equalsIgnoreCase("the_end")) {

					//clear the current map
					renderer.setDirty(player.getName(), true);

					//Create a short integer to store the mapID of the map they are holding
					short mapId = currentItem.getData().getData();
					
					// Create a mapview object called MAP and store this servers map with the ID pulled from the player
					MapView map = this.getServer().getMap(mapId);
										
					// Send the player the entire map
					//player.sendMap(map);
					world = "the_end";
										
					// Call the applytomap() function in the maplines object we just created called renderer. Pass it our map 
					renderer.applyToMap(map, world);
										
					// Tell the user we are applying their lines
					//sender.sendMessage("Displaying Map" + mapId);
					return true;
					
					
				}
				
				
				if (args[0].equalsIgnoreCase("reset")) {
					
					renderer.setDirty(player.getName(), true);
					player.sendMessage("You will now pull the new map");
					return true;
					
				}
				
				player.sendMessage("You did not enter a valid command. Try /map [yeticraft, nether, the_end, reset]");
				return false;
			}
			player.sendMessage("There is not a map in your hand.");
			return false;
		}
					
		
		return false;
	}
	
		
		
		
	// Run this sub when the plugin is disabled
	public void onDisable() {

		log.info("Unloading net.yeticraft.xxtraineexx.YetiMap");
	}
}
