package com.hess.assignment1;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
	}
	
	/**
	 * This method is responsible for handling what variables update for the object
	 * 
	 * @param deltaTime
	 */
	public void update(float deltaTime)
	{
		
	}
	
	/**
	 * All objects will need to implement their own rendering method
	 * 
	 * @param batch
	 */
	public abstract void render(SpriteBatch batch);
}
