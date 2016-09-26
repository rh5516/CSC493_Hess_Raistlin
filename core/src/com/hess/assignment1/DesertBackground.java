package com.hess.assignment1;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * This class draws sand dunes in the background
 * 
 * @author Raistlin Hess
 *
 */
public class DesertBackground extends AbstractGameObject
{
	private TextureRegion background;
	private float length;
	private float random;
	
	/**
	 * Sets the length of DesertBackground to the parameter length
	 * 
	 * @param length
	 */
	public DesertBackground(float length)
	{
		this.length = length;
		this.random = 1;
		init();
	}
	
	/**
	 * Sets the dimensions of the DesertBackground and places it at the bottom portion of the screen
	 */
	private void init()
	{
		dimension.set(length+(length/4), length/9.0f);
		background = Assets.instance.levelDecoration.desertBG;
		origin.x = -dimension.x/2;
	}
	
	/**
	 * This is a random value to shift the background. Intended to simulate heat shimmer
	 */
	public void setRandom(float random)
	{
		this.random = random;
	}
	
	/**
	 * Draw background at x position based on random value
	 */
	@Override
	public void render(SpriteBatch batch)
	{
		TextureRegion reg = null;
		reg = background;
		this.position.x *= random;
		batch.draw(reg.getTexture(), position.x+origin.x, position.y+origin.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false, false);
		this.position.x /= random;
	}
}
