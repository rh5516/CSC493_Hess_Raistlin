package objects;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.Assets;

/**
 * This class represents a Ground game object
 * 
 * @author Raistlin Hess
 *
 */
public class Ground extends AbstractGameObject
{
	private TextureRegion regEdge;
	private int length;
	
	public Ground()
	{
		init();
	}
	
	/**
	 * This method initializes the dimensions, length of the rock, and assigns
	 * assets to the regEdge and regMiddle 
	 */
	private void init()
	{
		dimension.set(1.01f,1);
		regEdge = Assets.instance.ground.edge;
		
		//Start length of this rock
		setLength(1);
	}
	
	/**
	 * Sets the length of this Rock to parameter length
	 */
	public void setLength(int length)
	{
		this.length = length;
		
		//Update bounding box for collision detetion
		bounds.set(0,0, dimension.x*length, dimension.y-0.25f);
	}
	
	/**
	 * Changes the Rock's length relative to its current length
	 */
	public void increaseLength(int amount)
	{
		setLength(length+amount);
	}
	
	/**
	 * This renders the ground sand
	 */
	@Override
	public void render(SpriteBatch batch)
	{
		TextureRegion reg = null;
		reg = regEdge;
		batch.draw(reg.getTexture(), position.x, position.y, origin.x/*+dimension.x/8*/, origin.y, dimension.x/*/4*/, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false, false);
	}
}
