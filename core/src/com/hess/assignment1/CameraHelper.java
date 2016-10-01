package com.hess.assignment1;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Thsi class is responsible for manipulating the game camera
 * 
 * @author Raistlin Hess
 *
 */
public class CameraHelper
{
	private static final String TAG = CameraHelper.class.getName();
	private final float MAX_ZOOM_IN = 0.3f;
	private final float MAX_ZOOM_OUT = 11.0f;
	private Vector2 position;
	private float zoom;
	private AbstractGameObject target;
	
	/**
	 * Sets the initial position and zooming factor
	 */
	public CameraHelper()
	{
		position = new Vector2();
		zoom = 2.0f;
	}
	
	/**
	 * If the camera has a target, it will update its position to match that of its target
	 */
	public void update(float deltaTime)
	{
		if(!hasTarget()) return;
		
		position.x = target.position.x + target.origin.x;
		position.y = target.position.y + target.origin.y;
		
		//Prevent camera from moving down passed the bottom of level
		position.y = Math.max(-1.0f, position.y);
	}
	
	/**
	 * This changes the position of the camera
	 * 
	 * @param x
	 * @param y
	 */
	public void setPosition(float x, float y)
	{
		this.position.set(x, y);
	}
	
	/**
	 * Returns position of the camera
	 * 
	 * @return
	 */
	public Vector2 getPosition()
	{
		return position;
	}
	
	/**
	 * Causes the camera to zoom in or out based on amount
	 * 
	 * @param amount
	 */
	public void addZoom(float amount)
	{
		setZoom(zoom + amount);
	}
	
	/**
	 * Zooms the camera in at a specific amount, not relative to previous zoom level
	 * 
	 * @param zoom
	 */
	public void setZoom(float zoom)
	{
		this.zoom = MathUtils.clamp(zoom, MAX_ZOOM_IN, MAX_ZOOM_OUT);
	}
	
	/**
	 * Returns current zoom
	 * 
	 * @return
	 */
	public float getZoom()
	{
		return zoom;
	}
	
	/**
	 * Sets a target for the camera to follow
	 * 
	 * @param target
	 */
	public void setTarget(AbstractGameObject target)
	{
		this.target = target;
	}
	
	/**
	 * Returns the target that the camera is following
	 * 
	 * @return
	 */
	public AbstractGameObject getTarget()
	{
		return target;
	}
	
	/**
	 * Returns true if camera has a target, false if it doesn't
	 * 
	 * @return
	 */
	public boolean hasTarget()
	{
		return target != null;
	}
	
	/**
	 * Returns true if the camera's current target is not null and is the target passed in as target
	 * 
	 * @param target
	 * @return
	 */
	public boolean hasTarget(AbstractGameObject target)
	{
		return hasTarget() && this.target.equals(target);
	}
	
	/**
	 * This method takes the current state of CameraHelper and passes it to the camera
	 * in order to make any changes and update
	 * 
	 * @param camera
	 */
	public void applyTo(OrthographicCamera camera)
	{
		camera.position.x = position.x;
		camera.position.y = position.y;
		camera.zoom = zoom;
		camera.update();
	}
}
