package com.hess.assignment1;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * This class represents a Rock game object
 * 
 * @author Raistlin Hess
 *
 */
public class Rock extends AbstractGameObject
{
	private TextureRegion regEdge;
	private TextureRegion regMiddle;
	private int length;
	
	public Rock()
	{
		init();
	}
	
	/**
	 * This method initializes the dimensions, length of the rock, and assigns
	 * assets to the regEdge and regMiddle 
	 */
	private void init()
	{
		dimension.set(1,1.5f);
		regEdge = Assets.instance.rock.edge;
		regMiddle = Assets.instance.rock.middle;
		
		//Start length of this rock
		setLength(1);
	}
	
	/**
	 * Sets the length of this Rock to parameter length
	 */
	public void setLength(int length)
	{
		this.length = length;
	}
	
	/**
	 * Changes the Rock's length relative to its current length
	 */
	public void increaseLength(int amount)
	{
		setLength(length+amount);
	}
	
	/**
	 * This draws the rock with edges. It stretches the center part for as long as it needs to and adds edges at the edges
	 */
	@Override
	public void render(SpriteBatch batch)
	{
		TextureRegion reg = null;
		float relX = 0;
		float relY = 0;
		
		//Draw left edge
		reg = regEdge;
		relX -= dimension.x/4;
		batch.draw(reg.getTexture(), position.x+relX, position.y+relY, origin.x, origin.y, dimension.x/4, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false, false);
		
		//Draw middle
		relX = 0;
		reg = regMiddle;
		for(int i = 0; i < length; i++)
		{
			batch.draw(reg.getTexture(), position.x+relX, position.y+relY, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false, false);
			relX += dimension.x;
		}
		
		//Draw right edge
		reg = regEdge;
		batch.draw(reg.getTexture(), position.x+relX, position.y+relY, origin.x+dimension.x/8, origin.y, dimension.x/4, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), true, false);
	}
	
}
