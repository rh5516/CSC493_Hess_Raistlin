package objects;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.Assets;

/**
 * This represents a rat. It can push MelonMan and steal rain
 * drops
 * 
 * @author Raistlin Hess
 *
 */
public class Rat extends AbstractGameObject
{
	public static final String TAG = Rat.class.getName();
	private TextureRegion regHead;
	
	public Rat()
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
		regHead = Assets.instance.rat.head;
		
		//Center image on object
		origin.set((dimension.x-0.2f)/2, (dimension.y-0.4f)/2);
		
		//Bounding box for collision detection
		bounds.set(0,0, dimension.x-0.2f, dimension.y-0.4f);
		
		//Set physics values
		terminalVelocity.set(3.3f, 4.0f);
		acceleration.set(8.0f, 12.0f);
		friction = 1.0f;
	}
	
	/**
	 * Update the object's position
	 */
	@Override
	public void update(float deltaTime)
	{
		super.update(deltaTime);
		
		//Determine if object stopped moving. If so, make it jump
		if(body.getLinearVelocity().x >= 0.0f)
		{
			body.applyLinearImpulse(0.0f, acceleration.y, origin.x, origin.y, true);
		}
		
		//Limit velocity to terminal
		float maxX = Math.min(body.getLinearVelocity().x, -terminalVelocity.x);
		float maxY = Math.min(body.getLinearVelocity().y, terminalVelocity.y);
		body.setLinearVelocity(maxX, maxY);
	}
	
	/**
	 * Draw the image of the Rat
	 */
	@Override
	public void render(SpriteBatch batch)
	{
		TextureRegion reg = null;
		reg = regHead;
		batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false, false);
		
	}
}
