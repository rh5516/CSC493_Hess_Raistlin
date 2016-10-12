package objects;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import game.Assets;
import utilities.CharacterSkin;
import utilities.Constants;
import utilities.GamePreferences;

/**
 * This represents the Bunny's Head which can be controlled by the player
 * 
 * @author Raistlin Hess
 *
 */
public class BunnyHead extends AbstractGameObject
{
	public static final String TAG = BunnyHead.class.getName();
	private final float JUMP_TIME_MAX = 0.3f;
	private final float JUMP_TIME_MIN = 0.1f;
	private final float JUMP_TIME_OFFSET_FLYING = JUMP_TIME_MAX -0.018f;
	private TextureRegion regHead;
	public VIEW_DIRECTION viewDirection;
	public float timeJumping;
	public JUMP_STATE jumpState;
	public boolean hasFeatherPowerup;
	public float timeLeftFeatherPowerup;
	public ParticleEffect dustParticles = new ParticleEffect();
	
	public enum VIEW_DIRECTION{LEFT, RIGHT}
	public enum JUMP_STATE{GROUNDED, FALLING, JUMP_RISING, JUMP_FALLING}
	
	public BunnyHead()
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
		regHead = Assets.instance.bunny.head;
		
		//Center image on game object
		origin.set(dimension.x/2, dimension.y/2);
		
		//Bounding box for collision detection
		bounds.set(0,0,dimension.x,dimension.y);
		
		//Set physics values
		terminalVelocity.set(3.0f, 4.0f);
		friction.set(12.0f, 0.0f);
		acceleration.set(0.0f, -25.0f);
		
		//View diection
		viewDirection = VIEW_DIRECTION.RIGHT;
		
		//Jump state
		jumpState = JUMP_STATE.FALLING;
		timeJumping = 0;
		
		//Power-ups
		hasFeatherPowerup = false;
		timeLeftFeatherPowerup = 0;
		
		//Particles
		dustParticles.load(Gdx.files.internal("particles/dust.pfx"),Gdx.files.internal("particles"));
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
				if(jumpKeyPressed && hasFeatherPowerup)
				{
					jumpState = JUMP_STATE.JUMP_RISING;
				}
				break;
		}
	}
	
	/**
	 * This applied the Feather's powerup effect to this object
	 * 
	 * @param pickedUp
	 */
	public void setFeatherPowerup(boolean pickedUp)
	{
		hasFeatherPowerup = pickedUp;
		if(pickedUp)
		{
			timeLeftFeatherPowerup = Constants.ITEM_FEATHER_POWERUP_DURATION;
		}
	}
	
	/**
	 * Returns true if hasFeatherPowerup is set and the timer is greater than 0  
	 */
	public boolean hasFeatherPowerup()
	{
		return hasFeatherPowerup && timeLeftFeatherPowerup > 0;
	}

	@Override
	public void update(float deltaTime)
	{
		super.update(deltaTime);
		//Updates the viewing direction of the BunnyHead based on it's x velocity
		if(velocity.x != 0)
		{
			viewDirection = velocity.x < 0 ? VIEW_DIRECTION.LEFT : VIEW_DIRECTION.RIGHT;
		}
		
		if(timeLeftFeatherPowerup > 0)
		{
			timeLeftFeatherPowerup -= deltaTime;
			//If the timer is less than 0, disable the power-up
			if(timeLeftFeatherPowerup < 0)
			{
				timeLeftFeatherPowerup = 0;
				setFeatherPowerup(false);
			}
		}
		dustParticles.update(deltaTime);
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
				if(velocity.x != 0)
				{
					dustParticles.setPosition(position.x+dimension.x/2,position.y);
					dustParticles.start();
				}
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
		
		//Update y velocity if bunny is not standing on the ground
		if(jumpState != JUMP_STATE.GROUNDED)
		{
			dustParticles.allowCompletion();
			super.updateMotionY(deltaTime);
		}
	}
	
	/**
	 * Renders the bunny head. If it has a feather power-up, draw it with a colored overlay
	 * 
	 * @param batch
	 */
	@Override
	public void render(SpriteBatch batch)
	{
		TextureRegion reg = null;
		
		//Apply skin color
		batch.setColor(CharacterSkin.values()[GamePreferences.instance.charSkin].getColor());
		
		//Set special color when game object has a feather power-up
		if(hasFeatherPowerup)
		{
			batch.setColor(1, 0.8f, 0, 1.0f);
		}
		
		//Draw Particles
		dustParticles.draw(batch);
		
		//Draw image. If looking left, mirror texture along the y plane
		reg = regHead;
		batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), viewDirection==VIEW_DIRECTION.LEFT, false);
		
		//Reset color to white
		batch.setColor(1,1,1,1);
	}

}
