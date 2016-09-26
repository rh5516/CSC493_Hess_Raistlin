package com.hess.assignment1;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

/**
 * This class is responsible for reading the level asset and creating and placing
 * game objects based on the pixels in it.
 * 
 * @author Raistlin Hess
 *
 */
public class Level
{
	public static final String TAG = Level.class.getName();
	//Objects
	public Array<Ground> ground;
	public Array<Foreground> foreground;
	//Decoration
	public Clouds clouds;
	public PyramidNear pyramidNear;
	public PyramidFar pyramidFar;
	public DesertBackground background;
	public float sinVal;
	
	/**
	 * This assigns different color values to unique game objects
	 */
	public enum BLOCK_TYPE
	{
		EMPTY(0,0,0),						//Black
		FOREGROUND(255,0,0),				//Red
		GROUND(0,255,0),					//Green
		PLAYER_SPAWNPOINT(255,255,255),		//White
		ITEM_STAR(255,0,255),				//Purple
		ITEM_RAIN(255,255,0);				//Yellow
		
		private int color;
		/**
		 * This strips the values of the parameters r, g, and b and places the important bits in color
		 * 
		 * @param r
		 * @param g
		 * @param b
		 */
		private BLOCK_TYPE(int r, int g, int b)
		{
			color = r<<24 | g<<16 | b<<8 | 0xff;
		}
		
		/**
		 * Returns true if parameter color matches the current color 
		 * 
		 * @param color
		 * @return
		 */
		public boolean sameColor(int color)
		{
			return this.color == color;
		}
		
		/**
		 * Returns the current color
		 * 
		 * @return
		 */
		public int getColor()
		{
			return color;
		}
	}

	/**
	 * Takes the parameter filename and loads the level corresponding to it
	 * 
	 * @param filename
	 */
	public Level(String filename)
	{
		init(filename);
		sinVal = 1.0f;
	}
	
	/**
	 * This method loads the asset matching the parameter filename and places objects 
	 * at positions based upon the colors in the level asset
	 * 
	 * @param filename
	 */
	private void init(String filename)
	{
		//Objects
		ground = new Array<Ground>();
		foreground = new Array<Foreground>();
		
		//Load image file that represents level data
		Pixmap pixmap = new Pixmap(Gdx.files.internal(filename));
		//Scan pixels from top-left to bottom-right
		for(int pixelY = 0; pixelY < pixmap.getHeight(); pixelY++)
		{
			for(int pixelX = 0; pixelX < pixmap.getWidth(); pixelX++)
			{
				AbstractGameObject obj = null;
				float offsetHeight = 0;
				//Height grows from bottom to top
				float baseHeight = pixmap.getHeight()-pixelY;
				//Get color of current pixel as 32-bit RGBA value
				int currentPixel = pixmap.getPixel(pixelX, pixelY);
				//Find matching color value to identify block type at (x,y)
				//Point and create the corresponding game object if there is a match
				
				//Empty space
				if(BLOCK_TYPE.EMPTY.sameColor(currentPixel));
				
				//Foreground
				else if(BLOCK_TYPE.FOREGROUND.sameColor(currentPixel))
				{
					obj = new Foreground();
					float heightIncreaseFactor = 0.25f;
					offsetHeight = -2.5f;
					obj.position.set(pixelX, baseHeight*obj.dimension.y*heightIncreaseFactor+offsetHeight);
					foreground.add((Foreground)obj);
				}
				
				//Rock
				else if(BLOCK_TYPE.GROUND.sameColor(currentPixel))
				{
					obj = new Ground();
					float heightIncreaseFactor = 0.3f;
					offsetHeight = -2.5f;
					obj.position.set(pixelX, baseHeight*obj.dimension.y*heightIncreaseFactor+offsetHeight);
					ground.add((Ground)obj);
				}
				
				//Player spawnpoint
				else if(BLOCK_TYPE.PLAYER_SPAWNPOINT.sameColor(currentPixel))
				{
					
				}
				
				//Feather
				else if(BLOCK_TYPE.ITEM_STAR.sameColor(currentPixel))
				{
					
				}
				
				//Gold Coin
				else if(BLOCK_TYPE.ITEM_RAIN.sameColor(currentPixel))
				{
					
				}
				
				//Unknown object/pixel color
				else
				{
					int r = 0xff & (currentPixel >>> 24);	//Red color channel
					int g = 0xff & (currentPixel >>> 16);	//Green color channel
					int b = 0xff & (currentPixel >>> 8);	//Blue color channel
					int a = 0xff & (currentPixel);			//Alpha channel
					Gdx.app.error(TAG, "Unknown object at x<"+pixelX+"> y<"+pixelY+">: r<"+r+"> g<"+g+"> b<"+b+"> a<"+a+">");
				}
			}
		}
		
		//Decoration
		clouds = new Clouds(pixmap.getWidth());
		clouds.position.set(0,2.5f);
		pyramidNear = new PyramidNear(pixmap.getWidth());
		pyramidFar = new PyramidFar(pixmap.getWidth());
		pyramidNear.position.set(-1,-0.6f);
		pyramidFar.position.set(pyramidFar.dimension.x*2,-1.2f);
		background = new DesertBackground(pixmap.getWidth());
		background.position.set(-background.origin.x,-6.5f);
		
		
		//Free memory
		pixmap.dispose();
		Gdx.app.debug(TAG, "Level '"+filename+"' loaded.");
	}
	
	/**
	 * Takes all sprites in the batch and renders them
	 * 
	 * @param batch
	 */
	public void render(SpriteBatch batch)
	{
		//These values are used to slightly move objects' x position in the background
		//Supposed to simulate a heat shimmer effect
		float sin = (float)(Math.sin(sinVal)%(sinVal))/10;//%sinVal;
		float cos = (float)(Math.cos(sinVal)%sinVal)/8;
		sinVal += 0.01f;
		
		//Draw Background, moving slightly
		float bgTmp = background.position.x;
		background.position.x *= cos + 0.8f;
		background.render(batch);
		background.position.x = bgTmp;
		
		//Draw Pyramids, slightly moving back and forth
		float pfTmp = pyramidFar.position.x;
		float pnTmp = pyramidNear.position.x;
		pyramidFar.position.x += cos*4;
		pyramidFar.render(batch);
		pyramidFar.position.x = pfTmp;
		
		pyramidNear.position.x += sin*6;
		pyramidNear.render(batch);
		pyramidNear.position.x = pnTmp;
		
		//Draw Rocks
		for(Ground ground: ground)
		{
			ground.render(batch);		
		}
		
		//Draw Clouds
		clouds.render(batch);
		
		//Draw Foreground
		for(Foreground fg: foreground)
		{
			fg.render(batch);
		}
		
		//Draw second, semi-transparent, moving background
		batch.setColor(1,1,1,0.3f);
		background.position.x *= sin + 0.8f;
		background.position.y += 0.7f;
		background.render(batch);
		background.position.x = bgTmp;
		background.position.y -= 0.7f;
		batch.setColor(1,1,1,1);
	}
}
