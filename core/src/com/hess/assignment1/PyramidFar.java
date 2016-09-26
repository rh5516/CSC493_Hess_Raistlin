package com.hess.assignment1;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

/**
 * This class represents a PyramidFar game object
 * 
 * @author Raistlin Hess
 *
 */
public class PyramidFar extends AbstractGameObject
{
	private TextureRegion regPyramidFar;
	private int length;	//This is the length of the level. Determines number of pyramids drawn
	
	/**
	 * Sets the length of the PyramidFar to the parameter length and calls init()
	 * 
	 * @param length
	 */
	public PyramidFar(int length)
	{
		this.length = length;
		init();
	}
	
	/**
	 * Initializes the dimensions of the PyramidFar and its position
	 */
	private void init()
	{
		dimension.set(4,2);
		regPyramidFar = Assets.instance.levelDecoration.pyramidNear;
		
		//Shift pyramid and extend length
		origin.x = -dimension.x*2;
		length += dimension.x*2;
	}
	
	/**
	 * Draws the PyramidFars at regular x intervals
	 */
	private void drawPyramid(SpriteBatch batch, float offsetX, float offsetY, float tintColor)
	{
		TextureRegion reg = null;
		batch.setColor(tintColor, tintColor, tintColor, 1);
		float xRel = dimension.x*offsetX;
		float yRel = dimension.y*offsetY;
		
		//Pyramids span the whole level
		int pyramidLength = 0;
		pyramidLength += MathUtils.ceil(length/ (2*dimension.x));
		pyramidLength += MathUtils.ceil(0.5f+offsetX);
		
		for(int i = 0; i < pyramidLength; i++)
		{
			reg = regPyramidFar;
			batch.draw(reg.getTexture(), origin.x+xRel, position.y+origin.y+yRel, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false, false);
			xRel += dimension.x*3;
		}
		
		//Reset color to white
		batch.setColor(1,1,1,1);
	}
	
	/**
	 * Draws the PyramidFars with a slight dark overlay
	 */
	@Override
	public void render(SpriteBatch batch)
	{
		//Distant mountains (light gray)
		drawPyramid(batch, position.x/6, 1.0f, 0.7f);
	}
}
