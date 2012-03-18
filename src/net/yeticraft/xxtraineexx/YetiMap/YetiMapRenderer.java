package net.yeticraft.xxtraineexx.YetiMap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class YetiMapRenderer extends MapRenderer {

	private final Map<String,Boolean> dirtyPlayers = new HashMap<String,Boolean>();		
	protected boolean overlay = false;
	private boolean dirty;
	private MapCursor cursor = new MapCursor((byte)0, (byte)0, (byte)0,	MapCursor.Type.WHITE_POINTER.getValue(), true);
	Logger log = Logger.getLogger("Minecraft");

	
	
//Method to remove other renderers and add my image
	
	public void applyToMap(MapView map, String world) {
		if (!this.overlay) {
			//remove all non-vanilla renderers
		    for (MapRenderer renderer : map.getRenderers()) {
		    	//if (!renderer.getClass().toString().equalsIgnoreCase("class org.bukkit.craftbukkit.map.CraftMapRenderer")) {
		    		map.removeRenderer(renderer);
		    	//}
		    }
		}
		
		// Then add a new NameListRenderer
        map.addRenderer(this);
		
	}

	
	

	@Override
	public void render(MapView map, MapCanvas canvas, Player player) {

		byte xloc;
		byte yloc;
		
		
		// creating a byte to store the byte value of the current players location. 
		// Dividing by 4000 or 500 to get relative position on a 128 pixel map.  Multiplying by 126 to determine actual position on the smaller map (128 wraps the cursor on the map)
		if (map.getWorld().getName().equalsIgnoreCase("yeticraft")){
				xloc = (byte)Math.rint(((player.getLocation().getX()) / 4000.0) * 126.0); 
				yloc = (byte)Math.rint(((player.getLocation().getZ()) / 4000.0) * 126.0); 
		}
		else{
			xloc = (byte)Math.rint(((player.getLocation().getX()) / 500.0) * 126.0); 
			yloc = (byte)Math.rint(((player.getLocation().getZ()) / 500.0) * 126.0); 
		}
		
		
		//Check to see if the new x/ylocs are different than the current cursor position... if so...move them.
		if (xloc != cursor.getX()){cursor.setX((byte)xloc);} 
		if (yloc != cursor.getY()){cursor.setY((byte)yloc);}
		
		
		
		//Get YAW from the player
		float yaw = player.getLocation().getYaw();
		// Make Yaw positive no matter what.... 
		if (yaw < 0) { yaw = 360 + yaw; }
		// throw resulting YAW in to a byte recognized by the cursor object (0-15)
		byte pos = (byte)Math.rint((yaw/360)*15);
		// Throw byte at the cursor object if the cursor direction has changed 
		if (pos != cursor.getDirection()) { cursor.setDirection(pos); }
			
		
	
		
		//Checking to see if the player is dirty (aka.. they do not have a good working map.)
		if (isDirty(player.getName())) {
			
			
			MapCursorCollection coll = new MapCursorCollection();
			coll.addCursor(cursor);
			canvas.setCursors(coll);
			
			try {
				player.sendMessage("Your " + map.getWorld().getName() + " map is being rendered...");
				BufferedImage img = null;
				img = ImageIO.read(new File("/srv/minecraft/plugins/yetimap/" + map.getWorld().getName().toString() + ".png"));
				canvas.drawImage(0, 0, img);
				setDirty(player.getName(), false);
				player.sendMap(map);
				
			
			
			}		
			
			//catching the error if it doesn't work
			catch (IOException e) {
				e.printStackTrace();
				setDirty(player.getName(), false);
				player.sendMessage("Rendering of " + map.getWorld().getName().toString() + " map failed... You were pwned by Java");
			}
			
			// The following was to verify the player dirty status was toggling.
			// player.sendMessage("Player dirty status: " + isDirty(player.getName().toString()));
			
		}
	}
		
				
		
	
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * Get the "dirty" status for this view - whether or not a repaint is needed for the given player.
	 * 
	 * @param playerName
	 * @return
	 */
	
	public boolean isDirty(String playerName) {
		return dirtyPlayers.containsKey(playerName) ? dirtyPlayers.get(playerName) : dirty;
	}
	
	/**
	 * Set the "dirty" status for this view - whether or not a repaint is needed for all players.
	 * 
	 * @param dirty	true if a repaint is needed, false otherwise
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
		if (dirty) {
			dirtyPlayers.clear();
		}
	}

	/**
	 * Set the "dirty" status for this view - whether or not a repaint is needed for the given player.
	 * 
	 * @param playerName	The player
	 * @param dirty			Whether or not a repaint is needed
	 */
	public void setDirty(String playerName, boolean dirty) {
		dirtyPlayers.put(playerName, dirty);
	}
	
	

}
