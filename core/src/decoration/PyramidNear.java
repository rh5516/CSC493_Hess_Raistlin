package decoration;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import game.Assets;
import objects.AbstractGameObject;

/**
 * This class represents a PyramidNear game object
 * 
 * @author Raistlin Hess
 *
 */
public class PyramidNear extends AbstractGameObject
{
	private TextureRegion regPyramidNear;
	private int length;	//This represents the number of times Pyramids will be drawn
	
	/**
	 * Sets the length of the Mountains to the parameter length and calls init()
	 * 
	 * @param length
	 */
	public PyramidNear(int length)
	{
		this.length = length;
		init();
	}
	
	/**
	 * Initializes the dimensions of the Mountains and its position
	 */
	private void init()
	{
		dimension.set(2.375f,1.125f);
		regPyramidNear = Assets.instance.levelDecoration.pyramidNear;
		
		//Shift pyramid and extend length
		origin.x = -dimension.x*2;
		length += dimension.x*2;
	}
	
	/**
	 * Draws the mountains so that they cover the entire background of the level
	 */
	private void drawPyramid(SpriteBatch batch, float offsetX, float offsetY)
	{
		TextureRegion reg = null;
		float parallaxSpeedX = 0.4f;
		float xRel = dimension.x*offsetX;
		float yRel = dimension.y*offsetY;
		
		//Pyramids span the whole level
		int pyramidLength = 0;
//		pyramidLength += MathUtils.ceil(length/ (2*dimension.x));
		pyramidLength += MathUtils.ceil(length/ (2*dimension.x)*(1-parallaxSpeedX));
//		pyramidLength += MathUtils.ceil(0.5f+offsetX);
		
		for(int i = 0; i < pyramidLength; i++)
		{
			reg = regPyramidNear;
			batch.draw(reg.getTexture(), origin.x+xRel*parallaxSpeedX, position.y+origin.y+yRel, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false, false);
			xRel += dimension.x*pyramidLength;//+(pyramidLength/dimension.x);
		}
	}
	
	/**
	 * Update the Pyramid's x position based on the camera's
	 * 
	 * @param camPosition
	 */
	public void updateScrollPosition(Vector2 camPosition)
	{
		position.set(camPosition.x, position.y);
	}
	
	/**
	 * Render the nearby pyramids with very slight color overlay
	 */
	@Override
	public void render(SpriteBatch batch)
	{
		drawPyramid(batch, position.x/6, 0.6f);
	}
}
