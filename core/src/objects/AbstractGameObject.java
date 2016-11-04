package objects;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Provides the framework for what a game object is
 * 
 * @author Raistlin Hess
 *
 */
public abstract class AbstractGameObject
{
	public Vector2 position;
	public Vector2 dimension;
	public Vector2 origin;
	public Vector2 scale;
	public float friction;
	public Vector2 velocity;			//current speed in m/s
	public Vector2 terminalVelocity;	//positive and negative maximum speed in m/s
	public Vector2 acceleration;		//constant acceleration in m/s^2
	public Rectangle bounds;			//describes physical body for collision detection
	public float rotation;
	public int numContacting;
	public Body body;
	
	/**
	 * Initialize variables
	 */
	public AbstractGameObject()
	{
		position = new Vector2();
		dimension = new Vector2(1,1);
		origin = new Vector2();
		scale = new Vector2(1,1);
		friction = 0;
		rotation = 0;
		velocity = new Vector2();
		terminalVelocity = new Vector2(1,1);
		acceleration = new Vector2();
		bounds = new Rectangle();
		numContacting = 0;
	}
	
	/**
	 * This method is responsible for handling what variables update for the object
	 * 
	 * @param deltaTime
	 */
	public void update(float deltaTime)
	{
		if(body != null)
		{
			position.set(body.getPosition());
			rotation = body.getAngle()*MathUtils.radiansToDegrees;
		}
	}
	
	/**
	 * Increases the number of objects that this object is in contact with
	 */
	public void startContact()
	{
		numContacting++;
	}
	
	/**
	 * Decreases the number of objects that this object is in contact with
	 */
	public void endContact()
	{
		numContacting--;
	}
	
	/**
	 * All objects will need to implement their own rendering method
	 * 
	 * @param batch
	 */
	public abstract void render(SpriteBatch batch);
}
