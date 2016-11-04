package utilities;

/**
 * This class is responsible for providing a single place to store all of the constants used in the game.
 * 
 * @author Raistlin Hess
 *
 */
public class Constants
{
	
	//Atlas for UI
	public static final String TEXTURE_ATLAS_UI = "images/game-ui-pack.atlas";
	
	//Atlas for Menu UI
	public static final String TEXTURE_ATLAS_LIBGDX_UI = "images/uiskin.atlas";
	
	//Location of description file for skins
	public static final String SKIN_LIBGDX_UI = "images/uiskin.json";
	
	//Skin for the Bunny game over the UI
	public static final String SKIN_MELONMAN_UI = "images/game-ui.json";
	
	//File location for preferences
	public static final String PREFERENCES = "melonman.prefs";
	
	//Delay after game over
	public static final float TIME_DELAY_GAME_OVER = 3;
	
	//Set visible game world to 5 meters wide and tall
	public static final float VIEWPORT_WIDTH = 5.0f;
	public static final float VIEWPORT_HEIGHT = 5.0f;
	
	//GUI Width
	public static final float VIEWPORT_GUI_WIDTH = 800.0f;
	
	//GUI Height
	public static final float VIEWPORT_GUI_HEIGHT = 480.0f;
	
	//Location of description file for texture atlas
	public static final String TEXTURE_ATLAS_OBJECTS = "images/melonman.pack.atlas";
	
	//Directory to each level's asset
	public static final String LEVEL_01 = "levels/level_01.png";
	public static final String LEVEL_02 = "levels/level_02.png";
	
	//Amount of extra lives at level start
	public static final int LIVES_START = 3;
	
	//Duration of feather power-up in seconds
	public static final float ITEM_STAR_POWERUP_DURATION = 9;
	
	//Used for random distance from player that rain will spawn
	public static final float RAIN_SPAWN_RADIUS = 3.0f;
}
