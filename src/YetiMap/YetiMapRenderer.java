package YetiMap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class YetiMapRenderer extends MapRenderer {

	private final Map<String,Boolean> dirtyPlayers = new HashMap<String,Boolean>();		
	protected boolean overlay = false;
	private boolean dirty;
	private MapCursor cursor = new MapCursor((byte)0, (byte)0, (byte)0,	MapCursor.Type.WHITE_POINTER.getValue(), true);
	Logger log = Logger.getLogger("Minecraft");
	protected byte buffer[][] = new byte[128][128];
	
	
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

		
		
		// creating a byte to store the byte value of the current players location. Adding in an offset because we moved spawn. 
		// Dividing by 4000 to get relative position on a 128 pixel map.  Multiplying by 126 to determine actual position on the smaller map (128 wraps the cursor on the map)
		byte xloc = (byte)Math.rint(((player.getLocation().getX() + 2042.5) / 4000.0) * 126.0); //the +2042.5 is the offset for new spawn
		byte yloc = (byte)Math.rint(((player.getLocation().getZ() - 1007.5) / 4000.0) * 126.0); //the -1007.5 is the offset for new spawn
		
		//Check to see if the new xloc is different than the current cursor position... if so...move it.
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
			
		
		
		
		//Checking to see if the player is dirty (aka.. they do not have a good working map.
		if (isDirty(player.getName())) {
			
			
			MapCursorCollection coll = new MapCursorCollection();
			coll.addCursor(cursor);
			canvas.setCursors(coll);
			
			/*
			try {
				
				
				BufferedImage img = null;
				img = ImageIO.read(new File("/home/minecraft/plugins/yetimap/yetonia128.png"));
				canvas.drawImage(0, 0, img);
				setDirty(player.getName(), false);
				player.sendMap(map);
				player.sendMessage("Your map has been rendered");
				
			*/
			
			
			//Attempting to render map from in game blocks
			World world = map.getWorld();	
			
			byte buffer[][] = new byte[128][128];
			double worldx = -4000 + (-2042.5);
			double worldz = -4000 + (1007.5);
									
				for (int canvasX = 0; canvasX < 128; ++canvasX)
				{
					worldz = -4000 + (1007.5);
					for (int canvasY = 0; canvasY < 128; ++canvasY)
					{
						
						
						int worldY = world.getHighestBlockAt((int)worldx, (int)worldz).getY();
						
						if (world.getHighestBlockAt((int)worldx, (int)worldz).getType()==Material.AIR) {
							worldY = worldY - 1;
							}
						
						Material type = world.getBlockAt((int)worldx, worldY, (int)worldz).getType();
						if (type == Material.WATER || type==Material.STATIONARY_WATER || type==Material.WATER_LILY) {buffer[canvasX][canvasY] = (byte)48;}
						else if (type == Material.DIRT) {buffer[canvasX][canvasY] = (byte)40;}
						else if (type == Material.GRASS || type==Material.LONG_GRASS) {buffer[canvasX][canvasY] = (byte)4;}
						else if (type==Material.LEAVES) {buffer[canvasX][canvasY] = (byte)28;}
						else if (type == Material.WOOD || type == Material.LOG) {buffer[canvasX][canvasY] = (byte)52;}
						else if (type == Material.SNOW || type==Material.SNOW_BALL || type==Material.SNOW_BLOCK) {buffer[canvasX][canvasY] = MapPalette.matchColor(255,255,255);}
						else if (type == Material.ICE) {buffer[canvasX][canvasY] = MapPalette.matchColor(230,230,250);}
						else if (type == Material.LAVA || type==Material.STATIONARY_LAVA) {buffer[canvasX][canvasY] = (byte)16;}
						else if (type == Material.STONE || type == Material.COBBLESTONE ||type == Material.GRAVEL) {buffer[canvasX][canvasY] = (byte)12;}
						else if (type == Material.SAND || type == Material.SANDSTONE) {buffer[canvasX][canvasY] = (byte)8;}
						else {buffer[canvasX][canvasY] = (byte)4;}
						
						worldz = worldz + 62.5;
						
					}
					worldx = worldx + 62.5;
				
				}
				
				// Writing the buffer out to the map
				
				for (int canvasX = 0; canvasX < 128; ++canvasX) {
					for (int canvasY = 0; canvasY < 128; ++canvasY) {
						canvas.setPixel(canvasX, canvasY, buffer[canvasX][canvasY]);
					}
				}
				
			setDirty(player.getName(), false);
			
			/*}		
			
						
				
			
			
				
			} 
			
			
			//catching the error if it doesn't work
			catch (IOException e) {
				e.printStackTrace();
			
					
			}*/
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
