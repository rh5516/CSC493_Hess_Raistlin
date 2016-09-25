package com.hess.assignment1;

/**
 * This class is responsible for providing a single place to store all of the constants used in the game.
 * 
 * @author Raistlin Hess
 *
 */
public class Constants
{
	//Set visible game world to 5 meters wide and tall
	public static final float VIEWPORT_WIDTH = 5.0f;
	public static final float VIEWPORT_HEIGHT = 5.0f;
	
	//GUI Width
	public static final float VIEWPORT_GUI_WIDTH = 800.0f;
	
	//GUI Height
	public static final float VIEWPORT_GUI_HEIGHT = 480.0f;
	
	//Location of description file for texture atlas
	public static final String TEXTURE_ATLAS_OBJECTS = "images/canyonbunny.pack.atlas";
	
	//Location of image file fro Level 1
	public static final String LEVEL_01 = "levels/level-01.png";
	
	//Amount of extra lives at level start
	public static final int LIVES_START = 3;
}
