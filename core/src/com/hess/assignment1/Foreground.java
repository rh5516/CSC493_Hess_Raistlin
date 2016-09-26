package com.hess.assignment1;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * This class represents a Foreground game object
 * 
 * @author Raistlin Hess
 *
 */
public class Foreground extends AbstractGameObject
{
	private TextureRegion regEdge;
	private int length;
	
	public Foreground()
	{
		init();
	}
	
	/**
	 * This method sets the dimensions and assets for this Foreground object
	 */
	private void init()
	{
		dimension.set(1.01f,1);
		regEdge = Assets.instance.levelDecoration.fg_sand;
		setLength(1);
	}
	
	/**
	 * Sets the length of this Foreground to parameter length
	 */
	public void setLength(int length)
	{
		this.length = length;
	}
	
	/**
	 * This renders the foreground sand
	 */
	@Override
	public void render(SpriteBatch batch)
	{
		TextureRegion reg = null;
		reg = regEdge;
		batch.setColor(1,1,1,0.5f);
		batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false, false);
		batch.setColor(1,1,1,1);
	}
}
