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
	
	/**
	 * Sets the length of DesertBackground to the parameter length
	 * 
	 * @param length
	 */
	public DesertBackground(float length)
	{
		this.length = length;
		init();
	}
	
	/**
	 * Sets the dimensions of the DesertBackground and places it at the bottom portion of the screen
	 */
	private void init()
	{
		dimension.set(length+(length/1.7f), length/7.0f);
		background = Assets.instance.levelDecoration.desertBG;
		origin.x = -dimension.x/2;
	}
	
	/**
	 * Draw background at x position based on random value
	 */
	@Override
	public void render(SpriteBatch batch)
	{
		TextureRegion reg = null;
		reg = background;
		batch.draw(reg.getTexture(), position.x+origin.x, position.y+origin.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false, false);
	}
}
