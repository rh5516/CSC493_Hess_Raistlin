package decoration;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import game.Assets;
import objects.AbstractGameObject;

/**
 * This class represents a Mountains game object
 * 
 * @author Raistlin Hess
 *
 */
public class Mountains extends AbstractGameObject
{
	private TextureRegion regMountainLeft;
	private TextureRegion regMountainRight;
	private int length;	//This represents the number of times Mountains will be drawn
	
	/**
	 * Sets the length of the Mountains to the parameter length and calls init()
	 * 
	 * @param length
	 */
	public Mountains(int length)
	{
		this.length = length;
		init();
	}
	
	/**
	 * Initializes the dimensions of the Mountains and its position
	 */
	private void init()
	{
		dimension.set(10,2);
		regMountainLeft = Assets.instance.levelDecoration.mountainLeft;
		regMountainRight = Assets.instance.levelDecoration.mountainRight;
		
		//Shift mountain and extend length
		origin.x = -dimension.x*2;
		length += dimension.x*2;
	}
	
	/**
	 * Draws the mountains so that they cover the entire background of the level
	 * The mountains also move slightly with the camera
	 */
	private void drawMountain(SpriteBatch batch, float offsetX, float offsetY, float tintColor, float parallaxSpeedX)
	{
		TextureRegion reg = null;
		batch.setColor(tintColor, tintColor, tintColor, 1);
		float xRel = dimension.x*offsetX;
		float yRel = dimension.y*offsetY;
		
		//Mountains span the whole level
		int mountainLength = 0;
		mountainLength += MathUtils.ceil(length/(2*dimension.x)*(1-parallaxSpeedX));
		mountainLength += MathUtils.ceil(0.5f+offsetX);
		
		for(int i = 0; i < mountainLength; i++)
		{
			//Left Mountain
			reg = regMountainLeft;
			batch.draw(reg.getTexture(), origin.x+xRel+position.x*parallaxSpeedX, origin.y+yRel+position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false, false);
			xRel += dimension.x;
			
			//Right mountain
			reg = regMountainRight;
			batch.draw(reg.getTexture(), origin.x+xRel+position.x*parallaxSpeedX, origin.y+yRel+position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false, false);
			xRel += dimension.x;
		}
		
		//Reset color to white
		batch.setColor(1,1,1,1);
	}
	
	/**
	 * Update the Mountain's position based on the camera's x position
	 * 
	 * @param camPosition
	 */
	public void updateScrollPosition(Vector2 camPosition)
	{
		position.set(camPosition.x, position.y);
	}
	
	/**
	 * Draw three layers of Mountains, each with a different color to simulate distance
	 */
	@Override
	public void render(SpriteBatch batch)
	{
		//Distant mountains (dark gray), scrolling at 80% of camera speed
		drawMountain(batch, 0.5f, 0.5f, 0.5f, 0.8f);
		
		//Distant mountains (gray), scrolling at 50% of camera speed
		drawMountain(batch, 0.25f, 0.25f, 0.7f, 0.5f);
		
		//Distant mountains (light gray), scrolling at 30% of camera speed
		drawMountain(batch, 0.0f, 0.0f, 0.9f, 0.3f);
	}

}
