package objects;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import game.Assets;
import utilities.Constants;

/**
 * This class represents a Rain drop object. These will fall at random times and locations
 * and these are meant to be collected to increase the player's score
 * 
 * @author Raistlin Hess
 *
 */
public class Rain extends AbstractGameObject
{
	private TextureRegion regRain;
	public int levelWidth;
	public int levelHeight;
	public boolean collected;
	public float timer;
	
	public Rain()
	{
		init();
	}
	
	/**
	 * Sets up the dimesnions and bounding box of this object
	 */
	private void init()
	{
		dimension.set(0.5f, 0.5f);
		regRain = Assets.instance.rain.rain;
		timer = Constants.ITEM_RAIN_BASE_TIME + MathUtils.random(0.0f, 8.0f) * (MathUtils.randomBoolean() ? 1.5f : -1.5f);
		
		//Set bounding box for collision detection
		bounds.set(0,0, dimension.x, dimension.y);
		
		//Set physics values
		terminalVelocity.set(3.0f, 4.0f);
		friction.set(0.0f, 1.0f);
		acceleration.set(0.0f, -20.0f);
		
		collected = false;
	}
	
	/**
	 * This method is responsible for resetting the timer and placing this drop
	 * at a random location on the level
	 */
	@Override
	public void update(float deltaTime)
	{
		//Check if rain drop has fallen out of bounds or collected
		//so it can be queued to respawn
		if(collected || position.y < -11.0f)
		{
			Vector2 pos = new Vector2();
			pos.x += MathUtils.random(0.0f, levelWidth);
			pos.y += 1.5f;
			pos.y += MathUtils.random(0.0f, 0.6f);
			position.set(pos);
			timer -= deltaTime; //Constants.ITEM_RAIN_BASE_TIME - deltaTime;
			velocity.y = 0.0f;
		}
		
		if(timer <= 0)
		{
			timer = Constants.ITEM_RAIN_BASE_TIME + MathUtils.random(0.0f, 8.0f) * (MathUtils.randomBoolean() ? 1.5f : -1.5f);
			collected = false;
		}
		
		if(!collected)
		{
			super.update(deltaTime);
		}
	}
	
	/**
	 * This method stores the dimensions of the current level for
	 * random placement of this drop
	 */
	public void setLevelDimensions(int levelWidth, int levelHeight)
	{
		this.levelWidth = levelWidth;
		this.levelHeight = levelHeight;
	}
	
	/**
	 * This method returns a value to add to the player's score 
	 * 
	 * @return
	 */
	public int getScore()
	{
		return 5;
	}

	/**
	 * Draws the rain drop if it has not been collected and randomly mirrors the image 
	 */
	@Override
	public void render(SpriteBatch batch)
	{
		if(!collected)
		{
			TextureRegion reg = regRain;
			batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), MathUtils.randomBoolean(), false);
		}
	}
}
