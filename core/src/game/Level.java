package game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import decoration.Clouds;
import decoration.DesertBackground;
import decoration.Foreground;
import decoration.PyramidFar;
import decoration.PyramidNear;
import objects.AbstractGameObject;
import objects.Ground;
import objects.MelonMan;
import objects.Rain;
import objects.Star;

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
	public Array<Ground> groundBlocks;
	public Array<Foreground> foreground;
	public MelonMan melonMan;
	public Array<Rain> rainDrops;
	public Array<Star> stars;
	//Decoration
	public Clouds clouds;
	public PyramidNear pyramidNear;
	public PyramidFar pyramidFar;
	public DesertBackground background;
	public float sinVal;
	public int levelWidth;
	public int levelHeight;
	
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
		//Player character
		melonMan = null;
		
		//Objects
		groundBlocks = new Array<Ground>();
		foreground = new Array<Foreground>();
		rainDrops = new Array<Rain>();
		stars = new Array<Star>();
		
		//Load image file that represents level data
		Pixmap pixmap = new Pixmap(Gdx.files.internal(filename));
		levelWidth = pixmap.getWidth();
		levelHeight = pixmap.getHeight();
		
		//Scan pixels from top-left to bottom-right
		for(int pixelY = 0; pixelY < levelHeight; pixelY++)
		{
			for(int pixelX = 0; pixelX < levelWidth; pixelX++)
			{
				AbstractGameObject obj = null;
				float offsetHeight = 0;
				//Height grows from bottom to top
				float baseHeight = levelHeight-pixelY;
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
				
				//Ground
				else if(BLOCK_TYPE.GROUND.sameColor(currentPixel))
				{
					obj = new Ground();
					float heightIncreaseFactor = 0.3f;
					offsetHeight = -2.5f;
					obj.position.set(pixelX, baseHeight*obj.dimension.y*heightIncreaseFactor+offsetHeight);
					groundBlocks.add((Ground)obj);
				}
				
				//Player spawnpoint
				else if(BLOCK_TYPE.PLAYER_SPAWNPOINT.sameColor(currentPixel))
				{
					obj = new MelonMan();
					offsetHeight = -3.0f;
					obj.position.set(pixelX, baseHeight*obj.dimension.y+offsetHeight);
					melonMan = (MelonMan)obj;
				}
				
				//Star
				else if(BLOCK_TYPE.ITEM_STAR.sameColor(currentPixel))
				{
					obj = new Star();
					offsetHeight = -2.2f;
					obj.position.set(pixelX, baseHeight*obj.dimension.y+offsetHeight);
					stars.add((Star)obj);
				}
				
				//Rain drops
				else if(BLOCK_TYPE.ITEM_RAIN.sameColor(currentPixel))
				{
					Rain drop = new Rain();
					offsetHeight = 0;//-1.5f;
//					drop.position.set(pixelX, baseHeight*drop.dimension.y+offsetHeight);
					drop.position.set(MathUtils.random(0.0f, levelWidth), 1.5f + MathUtils.random(0.0f, 0.6f)+offsetHeight+drop.dimension.y);
					drop.setLevelDimensions(levelWidth, levelHeight);
					rainDrops.add(drop);
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
		clouds = new Clouds(levelWidth);
		pyramidNear = new PyramidNear(levelWidth);
		pyramidFar = new PyramidFar(levelWidth);
		pyramidNear.position.set(-1,-0.6f);
		pyramidFar.position.set(pyramidFar.dimension.x*2,-1.2f);
		background = new DesertBackground(levelWidth);
		background.position.set(-background.origin.x,-6.5f);
		
		
		//Free memory
		pixmap.dispose();
		Gdx.app.debug(TAG, "Level '"+filename+"' loaded.");
	}
	
	/**
	 * This method tells every object in the level to run their update() function
	 * 
	 * @param deltaTime
	 */
	public void update(float deltaTime)
	{
		melonMan.update(deltaTime);
		
		for(Ground ground: groundBlocks)
		{
			ground.update(deltaTime);
		}
		
		for(Rain rain: rainDrops)
		{
			rain.update(deltaTime);
		}
		
		//When a star has been collected, remove it from the ArrayList
		int index = 0;
		for(Star star: stars)
		{
			star.update(deltaTime);
			if(star.collected)
			{
				stars.removeIndex(index);
			}
			else
			{
				index++;
			}
		}
		
		clouds.update(deltaTime);
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
		
		//Draw Pyramids
		float pfTmpX = pyramidFar.position.x;
		float pfTmpY = pyramidFar.position.y;
		float pnTmpX = pyramidNear.position.x;
		float pnTmpY = pyramidFar.position.y;
		
		pyramidFar.render(batch);
		pyramidNear.render(batch);
		
		//Draw transparent pyramids moving back and forth
		batch.setColor(1,1,1,0.5f);
		pyramidFar.position.x += cos*4;
		pyramidFar.position.y += (MathUtils.randomBoolean() ? 0.02 : -0.02)+sin;
		pyramidFar.render(batch);
		pyramidFar.position.y = pfTmpY;
		pyramidFar.position.x = pfTmpX;
		
		pyramidNear.position.x += cos*4;//sin*6;
		pyramidNear.position.y += (MathUtils.randomBoolean() ? 0.02 : -0.02)+cos;
		pyramidNear.render(batch);
		pyramidNear.position.y = pnTmpY;
		pyramidNear.position.x = pnTmpX;
		batch.setColor(1,1,1,1);
		
		//Draw player
		melonMan.render(batch);
		
		//Draw Rocks
		for(Ground ground: groundBlocks)
		{
			ground.render(batch);		
		}
		
		//Draw Foreground
		for(Foreground fg: foreground)
		{
			fg.render(batch);
		}
		
		//Draw Stars
		for(Star star: stars)
		{
			star.render(batch);
		}
		
		//Draw rain drops
		for(Rain drop: rainDrops)
		{
			drop.render(batch);
		}
		
		//Draw second, semi-transparent, moving background
		batch.setColor(1,1,1,0.4f);
		background.position.x *= sin + 0.8f;
		background.position.y += 0.7f;
		background.render(batch);
		background.position.x = bgTmp;
		background.position.y -= 0.7f;
		batch.setColor(1,1,1,1);
		
		//Draw Clouds
		clouds.render(batch);
	}
}
