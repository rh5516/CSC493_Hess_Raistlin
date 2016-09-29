package com.hess.assignment1;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

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
	public Vector2 velocity;			//current speed in m/s
	public Vector2 terminalVelocity;	//positive and negative maximum speed in m/s
	public Vector2 friction;			//0 = no friction, meaning velocity doesn't decrease
	public Vector2 acceleration;		//constant acceleration in m/s^2
	public Rectangle bounds;			//describes physical body for collision detection
	public float rotation;
	
	/**
	 * Initialize variables
	 */
	public AbstractGameObject()
	{
		position = new Vector2();
		dimension = new Vector2(1,1);
		origin = new Vector2();
		scale = new Vector2(1,1);
		rotation = 0;
		velocity = new Vector2();
		terminalVelocity = new Vector2(1,1);
		friction = new Vector2();
		acceleration = new Vector2();
		bounds = new Rectangle();
	}
	
	/**
	 * This method is responsible for handling what variables update for the object
	 * 
	 * @param deltaTime
	 */
	public void update(float deltaTime)
	{
		updateMotionX(deltaTime);
		updateMotionY(deltaTime);
		//Move to new posiiton
		position.x += velocity.x*deltaTime;
		position.y += velocity.y*deltaTime;
	}
	
	/**
	 * This method is responsible for managing velocity and friction of the object
	 * along the X-plane
	 *  
	 * @param deltaTime
	 */
	protected void updateMotionX(float deltaTime)
	{
		//If this object is moving, handle modifying x velocity based on friction
		if(velocity.x != 0)
		{
			if(velocity.x > 0)
			{
				velocity.x = Math.max(velocity.x-friction.x*deltaTime, 0);
			}
			else
			{
				velocity.x = Math.min(velocity.x+friction.x*deltaTime, 0);
			}
		}
		
		//Apply acceleration
		velocity.x += acceleration.x*deltaTime;
		//Make sure the object's velocity does not exceed terminal velocity
		velocity.x = MathUtils.clamp(velocity.x,-terminalVelocity.x,terminalVelocity.x);
	}
	
	/**
	 * This method is responsible for managing velocity and friction of the object
	 * along the Y-plane
	 *  
	 * @param deltaTime
	 */
	protected void updateMotionY(float deltaTime)
	{
		//If this object is moving, handle modifying y velocity based on friction
		if(velocity.y != 0)
		{
			if(velocity.y > 0)
			{
				velocity.y = Math.max(velocity.y-friction.y*deltaTime, 0);
			}
			else
			{
				velocity.y = Math.min(velocity.y+friction.y*deltaTime, 0);
			}
		}
		
		//Apply acceleration
		velocity.y += acceleration.y*deltaTime;
		//Make sure the object's velocity does not exceed terminal velocity
		velocity.y = MathUtils.clamp(velocity.y,-terminalVelocity.y,terminalVelocity.y);
	}
	
	/**
	 * All objects will need to implement their own rendering method
	 * 
	 * @param batch
	 */
	public abstract void render(SpriteBatch batch);
}
