package game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import decoration.Clouds;
import decoration.Mountains;
import decoration.WaterOverlay;
import objects.AbstractGameObject;
import objects.BunnyHead;
import objects.Carrot;
import objects.Feather;
import objects.Goal;
import objects.GoldCoin;
import objects.Rock;

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
	public Goal goal;
	public BunnyHead bunnyHead;
	public Array<Rock> rocks;
	public Array<GoldCoin> goldCoins;
	public Array<Feather> feathers;
	public Array<Carrot> carrots;
	//Decoration
	public Clouds clouds;
	public Mountains mountains;
	public WaterOverlay waterOverlay;
	
	/**
	 * This assigns different color values to unique game objects
	 */
	public enum BLOCK_TYPE
	{
		EMPTY(0,0,0),						//Black
		ROCK(0,255,0),						//Green
		PLAYER_SPAWNPOINT(255,255,255),		//White
		ITEM_FEATHER(255,0,255),			//Purple
		ITEM_GOLD_COIN(255,255,0),			//Yellow
		GOAL(255,0,0);						//Red
		
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
		bunnyHead = null;
		
		//Objects
		rocks = new Array<Rock>();
		goldCoins = new Array<GoldCoin>();
		feathers = new Array<Feather>();
		carrots = new Array<Carrot>();
		
		//Load image file that represents level data
		Pixmap pixmap = new Pixmap(Gdx.files.internal(filename));
		//Scan pixels from top-left to bottom-right
		int lastPixel = -1;
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
				
				//Rock
				else if(BLOCK_TYPE.ROCK.sameColor(currentPixel))
				{
					if(lastPixel != currentPixel)
					{
						obj = new Rock();
						float heightIncreaseFactor = 0.25f;
						offsetHeight = -2.5f;
						obj.position.set(pixelX, baseHeight*obj.dimension.y*heightIncreaseFactor+offsetHeight);
						rocks.add((Rock)obj);
						
					}
					else
					{
						rocks.get(rocks.size-1).increaseLength(1);
					}
				}
				
				//Player spawnpoint
				else if(BLOCK_TYPE.PLAYER_SPAWNPOINT.sameColor(currentPixel))
				{
					obj = new BunnyHead();
					offsetHeight = -3.0f;
					obj.position.set(pixelX, baseHeight*obj.dimension.y+offsetHeight);
					bunnyHead = (BunnyHead)obj;
				}
				
				//Feather
				else if(BLOCK_TYPE.ITEM_FEATHER.sameColor(currentPixel))
				{
					obj = new Feather();
					offsetHeight = -1.5f;
					obj.position.set(pixelX, baseHeight*obj.dimension.y+offsetHeight);
					feathers.add((Feather)obj);
				}
				
				//Gold Coin
				else if(BLOCK_TYPE.ITEM_GOLD_COIN.sameColor(currentPixel))
				{
					obj = new GoldCoin();
					offsetHeight = -1.5f;
					obj.position.set(pixelX, baseHeight*obj.dimension.y+offsetHeight);
					goldCoins.add((GoldCoin) obj);
				}
				
				//Goal
				else if(BLOCK_TYPE.GOAL.sameColor(currentPixel))
				{
					obj = new Goal();
					offsetHeight = -7.0f;
					obj.position.set(pixelX, baseHeight+offsetHeight);
					goal = (Goal)obj;
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
				lastPixel = currentPixel;
			}
		}
		
		//Decoration
		clouds = new Clouds(pixmap.getWidth());
		clouds.position.set(0,2);
		mountains = new Mountains(pixmap.getWidth());
		mountains.position.set(-1,-1);
		waterOverlay = new WaterOverlay(pixmap.getWidth());
		waterOverlay.position.set(0, -3.75f);
		
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
		bunnyHead.update(deltaTime);
		
		for(Rock rock: rocks)
		{
			rock.update(deltaTime);
		}
		
		for(GoldCoin coin: goldCoins)
		{
			coin.update(deltaTime);
		}
		
		for(Feather feather: feathers)
		{
			feather.update(deltaTime);
		}
		
		for(Carrot carrot: carrots)
		{
			carrot.update(deltaTime);
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
		//Draw Mountains
		mountains.render(batch);
		
		//Draw Goal
		goal.render(batch);
		
		//Draw Rocks
		for(Rock rock: rocks)
		{
			rock.render(batch);			
		}
		
		//Draw coins
		for(GoldCoin coin: goldCoins)
		{
			coin.render(batch);
		}
		
		//Draw Feathers
		for(Feather feather: feathers)
		{
			feather.render(batch);
		}
		
		//Draw Carrots
		for(Carrot carrot: carrots)
		{
			carrot.render(batch);
		}
		
		//Draw Player
		bunnyHead.render(batch);
		
		//Draw Water Overlay
		waterOverlay.render(batch);
		
		//Draw Clouds
		clouds.render(batch);
	}
}
