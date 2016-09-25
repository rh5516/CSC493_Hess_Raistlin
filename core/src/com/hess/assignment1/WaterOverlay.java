package com.hess.assignment1;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * This represents the game object WaterOverlay. Provides the illusion
 * of water spanning the whole level
 * 
 * @author Raistlin Hess
 *
 */
public class WaterOverlay extends AbstractGameObject
{
	private TextureRegion regWaterOverlay;
	private float length;
	
	/**
	 * Sets the length of WaterOverlay to the parameter length
	 * 
	 * @param length
	 */
	public WaterOverlay(float length)
	{
		this.length = length;
		init();
	}
	
	/**
	 * Sets the dimensions of the WaterOverlay and places it at the bottom portion of the screen
	 */
	private void init()
	{
		dimension.set(length*10, 3);
		regWaterOverlay = Assets.instance.levelDecoration.waterOverlay;
		origin.x = -dimension.x/2;
	}
	
	@Override
	public void render(SpriteBatch batch)
	{
		TextureRegion reg = null;
		reg = regWaterOverlay;
		batch.draw(reg.getTexture(), position.x+origin.x, position.y+origin.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false, false);
	}
}
