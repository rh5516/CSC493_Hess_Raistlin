package objects;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.Assets;

/**
 * This represents a Feather object. Can be colected and allows the player to
 * jump higher and increases score
 * 
 * @author Raistlin Hess
 *
 */
public class Feather extends AbstractGameObject
{
	private TextureRegion regFeather;
	public boolean collected;
	
	public Feather()
	{
		init();
	}
	
	/**
	 * Initializes the feather to be not collected and sets a bounding box
	 */
	private void init()
	{
		dimension.set(0.5f, 0.5f);
		regFeather = Assets.instance.feather.feather;
		
		//Set bounding box for collision detection
		bounds.set(0, 0, dimension.x, dimension.y);
		collected = false;
	}
	
	/**
	 * Draw the feather if it has not been collected
	 */
	@Override
	public void render(SpriteBatch batch)
	{
		if(collected) return;
		TextureRegion reg = null;
		reg = regFeather;
		batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false, false);
	}
	
	/**
	 * Returns the score associated with this object upon collection
	 * 
	 * @return
	 */
	public int getScore()
	{
		return 250;
	}
}
