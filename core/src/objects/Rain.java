package objects;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import game.Assets;

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
	public boolean collected;
	public boolean decaying;
	public float decay;
	
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
		rotation = 0;
		position.set(0.0f, 10.0f);	//Dummy Height
		decaying = false;
		decay = 1.0f;
		regRain = Assets.instance.rain.rain;
		
		//Set bounding box for collision detection
		bounds.set(dimension.x, dimension.y, dimension.x, dimension.y);
		this.
		//Set physics values
		terminalVelocity.set(0.0f, 10.0f);
		velocity.y = 0.5f;	//Prevents rain from being destroyed immediately
		friction = 0;
		acceleration.set(0.0f, 1.0f);
		
		collected = false;
	}
	
	/**
	 * This method is responsible for resetting the timer and placing this drop
	 * at a random location on the level
	 */
	@Override
	public void update(float deltaTime)
	{
		if(!collected)
		{
			if(decaying)
			{
				decay -= deltaTime;
				body.setLinearVelocity(0, body.getLinearVelocity().y);
			}
			else
			{
				body.setLinearVelocity(body.getLinearVelocity().x + MathUtils.random(-0.8f, 1.0f), body.getLinearVelocity().y);
			}
			super.update(deltaTime);
			position = body.getPosition();
			
		}
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
	 * Draws the rain drop if it has not been collected
	 */
	@Override
	public void render(SpriteBatch batch)
	{
		if(!collected)
		{
			TextureRegion reg = regRain;
			batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false, false);
		}
	}
}
