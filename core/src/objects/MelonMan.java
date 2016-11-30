package objects;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.Assets;
import utilities.AudioManager;
import utilities.CharacterSkin;
import utilities.Constants;
import utilities.GamePreferences;

/**
 * This represents MelonMan, the playable character. Can move left and right as well as jump
 * and pick up collectable objects
 * 
 * @author Raistlin Hess
 *
 */
public class MelonMan extends AbstractGameObject
{
	public static final String TAG = MelonMan.class.getName();
	private TextureRegion regHead;
	public VIEW_DIRECTION viewDirection;
	public boolean hasStar;
	public boolean grounded;
	public float timeLeftStar;
	public enum VIEW_DIRECTION{LEFT, RIGHT}

	public MelonMan()
	{
		init();
	}
	
	/**
	 * Initializes the bounding box and physics associated with this object.
	 * Also initializes the state of the object and other miscellaneous variables
	 */
	private void init()
	{
		dimension.set(1,1);
		regHead = Assets.instance.melonMan.head;
		
		//Center image on game object
		origin.set(dimension.x/2, dimension.y/2);
		
		//Bounding box for collision detection
		bounds.set(0,0,dimension.x,dimension.y);
		
		//Set physics values
		terminalVelocity.set(5.0f, 5.0f);
		acceleration.set(14.0f, 12.0f);
		friction = 2.0f;
		
		//View direction
		viewDirection = VIEW_DIRECTION.RIGHT;
		grounded = false;
		
		//Power-ups
		hasStar = false;
		timeLeftStar = 0;
	}

	/**
	 * This applied the star's powerup effect to this object
	 * 
	 * @param pickedUp
	 */
	public void setStarPowerup(boolean pickedUp)
	{
		hasStar = pickedUp;
		if(pickedUp)
		{
			timeLeftStar = Constants.ITEM_STAR_POWERUP_DURATION;
			terminalVelocity.set(8.0f, 5.0f);
			acceleration.set(22.0f, 12.0f);
			friction = 5.0f;
		}
	}
	
	/**
	 * Returns true if hasStar is set and the timer is greater than 0  
	 */
	public boolean hasStar()
	{
		return hasStar && timeLeftStar > 0;
	}

	@Override
	public void update(float deltaTime)
	{
		grounded = false;
		super.update(deltaTime);
		
		//Limit velocity to terminal
		float maxX;
		if(viewDirection == VIEW_DIRECTION.LEFT)
		{
			maxX = Math.max(body.getLinearVelocity().x, -terminalVelocity.x);
		}
		else
		{
			maxX = Math.min(body.getLinearVelocity().x, terminalVelocity.x);
		}
		float maxY = Math.min(body.getLinearVelocity().y, terminalVelocity.y);
		body.setLinearVelocity(maxX, maxY);
		
		if(timeLeftStar > 0)
		{
			timeLeftStar -= deltaTime;
			//If the timer is less than 0, disable the power-up
			if(timeLeftStar < 0)
			{
				timeLeftStar = 0;
				terminalVelocity.set(5.0f, 5.0f);
				acceleration.set(14.0f, 12.0f);
				friction = 2.0f;
				setStarPowerup(false);
			}
		}
	}
	
	/**
	 * Renders MelonMan. If it has a star power-up, tint him with a blueish color
	 * 
	 * @param batch
	 */
	@Override
	public void render(SpriteBatch batch)
	{
		TextureRegion reg = null;
		
		//Apply skin color
		batch.setColor(CharacterSkin.values()[GamePreferences.instance.charSkin].getColor());
		
		//Set special color when game object has a star power-up
		if(hasStar)
		{
			batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		}
		
		//Draw image. If looking right, mirror texture along the y plane
		reg = regHead;
		batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), viewDirection==VIEW_DIRECTION.RIGHT, false);
		
		batch.setColor(1,1,1,1);
	}

}
