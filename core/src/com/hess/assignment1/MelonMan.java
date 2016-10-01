package com.hess.assignment1;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

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
	private final float JUMP_TIME_MAX = 0.6f;
	private final float JUMP_TIME_MIN = 0.1f;
	private TextureRegion regHead;
	public VIEW_DIRECTION viewDirection;
	public float timeJumping;
	public JUMP_STATE jumpState;
	public boolean hasStar;
	public float timeLeftStar;
	public enum VIEW_DIRECTION{LEFT, RIGHT}
	public enum JUMP_STATE{GROUNDED, FALLING, JUMP_RISING, JUMP_FALLING}

	public MelonMan()
	{
		init();
	}
	
	/**
	 * Initializes the bounding box and physics associated with this object.
	 * Also initializes the state of the object and other miscellaneous variables
	 */
	public void init()
	{
		dimension.set(1,1);
		regHead = Assets.instance.melonMan.head;
		
		//Center image on game object
		origin.set(dimension.x/2, dimension.y/2);
		
		//Bounding box for collision detection
		bounds.set(0,0,dimension.x,dimension.y);
		
		//Set physics values
		terminalVelocity.set(4.0f, 4.0f);
		friction.set(12.0f, 0.0f);
		acceleration.set(0.0f, -40.0f);
		
		//View direction
		viewDirection = VIEW_DIRECTION.RIGHT;
		
		//Jump state
		jumpState = JUMP_STATE.FALLING;
		timeJumping = 0;
		
		//Power-ups
		hasStar = false;
		timeLeftStar = 0;
	}

	/**
	 * This handles state changes for this object whenever the jumpKey is pressed
	 * 
	 * @param jumpKeyPressed
	 */
	public void setJumping(boolean jumpKeyPressed)
	{
		switch(jumpState)
		{
			case GROUNDED:	//Character is standing on a platform
				if(jumpKeyPressed)
				{
					//Start counting jump time from the beginning
					timeJumping = 0;
					jumpState = JUMP_STATE.JUMP_RISING;
				}
				break;
				
			case JUMP_RISING:	//Rising in the air
				if(!jumpKeyPressed)
				{
					jumpState = JUMP_STATE.JUMP_FALLING;
				}
				break;
				
			case FALLING:		//Falling down
			case JUMP_FALLING:	//Falling down after a jump
				break;
		}
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
		super.update(deltaTime);
		//Updates the viewing direction of the MelonMan based on it's x velocity
		if(velocity.x != 0)
		{
			viewDirection = velocity.x < 0 ? VIEW_DIRECTION.LEFT : VIEW_DIRECTION.RIGHT;
			if(hasStar)
			{
				terminalVelocity.x = 8.0f;
				friction.set(24.0f, 0.0f);
			}
		}
		
		if(timeLeftStar > 0)
		{
			timeLeftStar -= deltaTime;
			//If the timer is less than 0, disable the power-up
			if(timeLeftStar < 0)
			{
				timeLeftStar = 0;
				friction.set(12.0f, 0.0f);
				terminalVelocity.set(4.0f, 4.0f);
				setStarPowerup(false);
			}
		}
	}
	
	/**
	 * Overrides updateMotionY from AbstractGameObject to track and handle
	 * jumpTime and jumping with a power-up
	 * 
	 * @param deltaTime
	 */
	@Override
	protected void updateMotionY(float deltaTime)
	{
		switch(jumpState)
		{
			case GROUNDED:
				jumpState = JUMP_STATE.FALLING;
				break;
				
			case JUMP_RISING:
				//Keep track of jump time
				timeJumping += deltaTime;
				
				//Jump time left?
				if(timeJumping <= JUMP_TIME_MAX)
				{
					//Still jumping
					velocity.y = terminalVelocity.y;
				}
				break;
				
			case FALLING:
				break;
				
			case JUMP_FALLING:
				//Add delta times to track jump time
				timeJumping += deltaTime;
				
				//Jump to minimal height if jump key was pressed too short
				if(timeJumping > 0 && timeJumping <= JUMP_TIME_MIN)
				{
					//Still jumping 
					velocity.y = terminalVelocity.y;
				}
				break;
		}
		
		//Update y velocity if MelonMan is not standing on the ground
		if(jumpState != JUMP_STATE.GROUNDED)
		{
			super.updateMotionY(deltaTime);
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
		batch.setColor(0.6f, 0.6f, 0.6f, 1.0f);
		
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
