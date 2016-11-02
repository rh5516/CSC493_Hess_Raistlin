package objects;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.Assets;

/**
 * This represents a Goal object. When the player collides with this,
 * Carrot objects will rain from the sky
 * 
 * @author Raistlin Hess
 *
 */
public class Goal extends AbstractGameObject
{
	private TextureRegion regGoal;
	
	public Goal()
	{
		init();
	}
	
	/**
	 * Create bounds for collision and dimensions
	 */
	private void init()
	{
		dimension.set(3.0f, 3.0f);
		regGoal = Assets.instance.levelDecoration.goal;
		
		//Set bounding box for collision detetion
		bounds.set(1, Float.MIN_VALUE, 10, Float.MAX_VALUE);
		origin.set(dimension.x/2.0f, 0.0f);
	}
	
	/**
	 * Draw the Goal object
	 */
	public void render(SpriteBatch batch)
	{
		TextureRegion reg = null;
		
		reg = regGoal;
		batch.draw(reg.getTexture(), position.x-origin.x, position.y-origin.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false, false);
	}
}
